package com.kesei.rag.entity.dto.job;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author kesei
 */
@Data
public class JobInfoPostRequest extends GenericPostRequest {
    String operation;
}
