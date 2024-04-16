package com.kesei.rag.entity.vo;

import com.kesei.rag.mocker.entity.MetaTable;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 生成结果
 * @author viamamo
 */
@Data
public class GenerationVO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    private MetaTable metaTable;
    
    private String createSql;
    
    private List<Map<String, Object>> dataList;
    
    private String insertSql;
    
    private String dataJson;
    
    private String javaEntityCode;
    
    private String javaObjectCode;
    
    private String typescriptTypeCode;
    
}
