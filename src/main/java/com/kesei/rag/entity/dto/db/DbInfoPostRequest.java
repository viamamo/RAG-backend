package com.kesei.rag.entity.dto.db;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author kesei
 */
@Data
public class DbInfoPostRequest extends GenericPostRequest {
    private Long id;
    private String name;
    private String dbName;
    private String dbType;
    private String driver;
    private String url;
    private String username;
    private String password;
    private String property;
    private String host;
    private Integer port;
}
