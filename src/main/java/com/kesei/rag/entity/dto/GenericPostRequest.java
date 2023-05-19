package com.kesei.rag.entity.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用post封装
 *
 * @author kesei
 */
@Data
public class GenericPostRequest implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String content;
}
