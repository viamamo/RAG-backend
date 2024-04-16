package com.kesei.rag.entity.dto.sql;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author viamamo
 */
@Data
public class GenerateFromSqlRequest extends GenericPostRequest {
    private String dbType;
}
