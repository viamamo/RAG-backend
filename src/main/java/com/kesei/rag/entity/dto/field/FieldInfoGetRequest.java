package com.kesei.rag.entity.dto.field;

import com.kesei.rag.entity.dto.GenericGetRequest;
import lombok.Data;

/**
 * @author kesei
 */
@Data
public class FieldInfoGetRequest extends GenericGetRequest {
    private Long id;
    private String name;
    private String fieldName;
    private String searchName;
    private Long userId;
}
