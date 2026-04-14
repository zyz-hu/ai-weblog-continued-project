package com.zhouyuanzhi.ai.robot.reader;

import cn.hutool.core.collection.CollUtil;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Markdown 文件读取器
 */
@Component
public class MarkdownReader {

    /**
     * 读取 Markdown 文件为 Document 集合
     */
    public List<Document> loadMarkdown(Resource resource, Map<String, Object> metadatas) {
        MarkdownDocumentReaderConfig.Builder configBuilder = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(false);

        if (CollUtil.isNotEmpty(metadatas)) {
            configBuilder.withAdditionalMetadata(metadatas);
        }

        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, configBuilder.build());
        return reader.get();
    }
}
