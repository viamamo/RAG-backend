package com.kesei.rag.entity.dto.field;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author kesei
 */
@Data
public class FieldInfoPostRequest extends GenericPostRequest {
    private Long id;
    private String name;
    private String fieldName;
    private String content;
}
