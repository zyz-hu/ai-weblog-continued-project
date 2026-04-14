package com.zhouyuanzhi.ai.robot.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * PGVector 向量表简单操作
 */
@Repository
public class VectorStoreRepository {

    private static final String DELETE_BY_MD_ID_SQL = "DELETE FROM t_vector_store WHERE metadata ->> 'mdStorageId' IN (:ids)";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public VectorStoreRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public int deleteByMdStorageId(Long id) {
        if (id == null) {
            return 0;
        }
        return deleteByMdStorageIds(Collections.singletonList(id));
    }

    public int deleteByMdStorageIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<String> idStrings = ids.stream().map(String::valueOf).toList();
        MapSqlParameterSource params = new MapSqlParameterSource("ids", idStrings);
        return namedParameterJdbcTemplate.update(DELETE_BY_MD_ID_SQL, params);
    }
}
