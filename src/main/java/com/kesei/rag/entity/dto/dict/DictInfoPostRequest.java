package com.kesei.rag.entity.dto.dict;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author kesei
 */
@Data
public class DictInfoPostRequest extends GenericPostRequest {
    private Long id;
    private String name;
    private String content;
}
