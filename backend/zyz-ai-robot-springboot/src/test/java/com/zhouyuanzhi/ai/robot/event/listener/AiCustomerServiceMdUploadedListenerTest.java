package com.zhouyuanzhi.ai.robot.event.listener;

import com.zhouyuanzhi.ai.robot.domain.dos.AiCustomerServiceMdStorageDO;
import com.zhouyuanzhi.ai.robot.domain.mapper.AiCustomerServiceMdStorageMapper;
import com.zhouyuanzhi.ai.robot.enums.AiCustomerServiceMdStatusEnum;
import com.zhouyuanzhi.ai.robot.event.AiCustomerServiceMdUploadedEvent;
import com.zhouyuanzhi.ai.robot.reader.MarkdownReader;
import com.zhouyuanzhi.ai.robot.repository.VectorStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiCustomerServiceMdUploadedListenerTest {

    @Mock
    private MarkdownReader markdownReader;
    @Mock
    private VectorStore vectorStore;
    @Mock
    private VectorStoreRepository vectorStoreRepository;
    @Mock
    private AiCustomerServiceMdStorageMapper aiCustomerServiceMdStorageMapper;
    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private AiCustomerServiceMdUploadedListener listener;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<Boolean> callback = invocation.getArgument(0);
            return callback.doInTransaction(new SimpleTransactionStatus());
        });
    }

    @Test
    void shouldSkipBlankDocumentsBeforeVectorizing() throws IOException {
        Path markdownFile = Files.writeString(tempDir.resolve("knowledge.md"), "# Title\n\ncontent");
        when(markdownReader.loadMarkdown(any(), anyMap())).thenReturn(List.of(
                new Document("   "),
                new Document("valid content")
        ));

        listener.vectorizing(buildEvent(markdownFile));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> documentsCaptor = ArgumentCaptor.forClass(List.class);
        verify(vectorStoreRepository, times(1)).deleteByMdStorageId(1L);
        verify(vectorStore).add(documentsCaptor.capture());
        assertThat(documentsCaptor.getValue()).hasSize(1);
        assertThat(documentsCaptor.getValue().get(0).getText()).isEqualTo("valid content");

        ArgumentCaptor<AiCustomerServiceMdStorageDO> statusCaptor = ArgumentCaptor.forClass(AiCustomerServiceMdStorageDO.class);
        verify(aiCustomerServiceMdStorageMapper, times(2)).updateById(statusCaptor.capture());
        assertThat(statusCaptor.getAllValues())
                .extracting(AiCustomerServiceMdStorageDO::getStatus)
                .containsExactly(
                        AiCustomerServiceMdStatusEnum.VECTORIZING.getCode(),
                        AiCustomerServiceMdStatusEnum.COMPLETED.getCode()
                );
    }

    @Test
    void shouldMarkFailedWhenMarkdownHasNoVectorizableContent() throws IOException {
        Path markdownFile = Files.writeString(tempDir.resolve("empty.md"), "---");
        when(markdownReader.loadMarkdown(any(), anyMap())).thenReturn(List.of(new Document(" ")));

        listener.vectorizing(buildEvent(markdownFile));

        verify(vectorStore, never()).add(any());
        verify(vectorStoreRepository, times(1)).deleteByMdStorageId(1L);

        ArgumentCaptor<AiCustomerServiceMdStorageDO> statusCaptor = ArgumentCaptor.forClass(AiCustomerServiceMdStorageDO.class);
        verify(aiCustomerServiceMdStorageMapper, times(2)).updateById(statusCaptor.capture());
        assertThat(statusCaptor.getAllValues())
                .extracting(AiCustomerServiceMdStorageDO::getStatus)
                .containsExactly(
                        AiCustomerServiceMdStatusEnum.VECTORIZING.getCode(),
                        AiCustomerServiceMdStatusEnum.FAILED.getCode()
                );
    }

    private AiCustomerServiceMdUploadedEvent buildEvent(Path markdownFile) {
        return AiCustomerServiceMdUploadedEvent.builder()
                .id(1L)
                .filePath(markdownFile.toString())
                .metadatas(Map.of("mdStorageId", 1L))
                .build();
    }
}
