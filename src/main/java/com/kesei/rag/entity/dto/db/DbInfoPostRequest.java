package com.kesei.rag.entity.dto.db;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author viamamo
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
    private ArrayList<Map<String,String>> property;
    private String host;
    private Integer port;
}
