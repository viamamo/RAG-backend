package com.kesei.rag.entity.dto.dict;

import com.kesei.rag.entity.dto.GenericGetRequest;
import lombok.Data;

/**
 * @author viamamo
 */
@Data
public class DictInfoGetRequest extends GenericGetRequest {
    private Long id;
    private String name;
    private Long userId;
}
