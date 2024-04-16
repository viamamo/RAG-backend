package com.kesei.rag.entity.dto.db;

import com.kesei.rag.entity.dto.GenericGetRequest;
import lombok.Data;

/**
 * @author viamamo
 */
@Data
public class DbInfoGetRequest extends GenericGetRequest {
    private Long id;
    private String name;
    private Long userId;
}
