package com.kesei.rag.entity.dto.table;

import com.kesei.rag.entity.dto.GenericGetRequest;
import lombok.Data;

/**
 * @author viamamo
 */
@Data
public class TableInfoGetRequest extends GenericGetRequest {
    private Long id;
    private String name;
    private Long userId;
}
