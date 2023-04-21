package com.kesei.rag.entity.dto.job;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author kesei
 */
@Data
public class SimpleExecutionRequest extends GenericPostRequest {
    private Long dbInfoId;
    private String sql;
    private Boolean withMarkColumn;
}
