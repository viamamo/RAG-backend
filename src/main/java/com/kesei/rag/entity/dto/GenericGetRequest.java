package com.kesei.rag.entity.dto;

import com.kesei.rag.support.Constants;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用get封装
 *
 * @author viamamo
 */
@Data
public class GenericGetRequest implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private String sortOrder = Constants.SORT_ORDER_ASC;
    private String sortColumn = Constants.DEFAULT_SORT_COLUMN;
    
    private Integer paginationNum =Constants.DEFAULT_PAGE_NUM;
    private Integer paginationSize=Constants.DEFAULT_PAGE_SIZE;
    
    private String searchParam="";
}
