package com.kesei.rag.entity.dto.table;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author viamamo
 */
@Data
public class TableInfoPostRequest extends GenericPostRequest {
    private Long id;
    private String name;
    private String content;
}
