package com.kesei.rag.entity.dto.job;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author viamamo
 */
@Data
public class AddJobRequest extends GenericPostRequest {
    private Long dbInfoId;
    private String content;
}
