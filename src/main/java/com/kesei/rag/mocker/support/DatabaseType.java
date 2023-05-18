package com.kesei.rag.mocker.support;

/**
 * @author kesei
 */
public enum DatabaseType {
    /**
     * 数据库类型
     */
    MYSQL("MySQL","mysql://",3306),
    POSTGRESQL("PostgreSQL","postgresql://",5432),
    ORACLE("Oracle","oracle:thin:@",1521),
    SQLSERVER("SQLServer","sqlserver://",1433),
    SYBASE("Sybase","sybase:Tds://",2638);
    
    private final String name;
    
    private final String protocol;
    
    private final Integer defaultPort;
    
    DatabaseType(String name,String protocol,Integer defaultPort){
        this.name=name;
        this.protocol=protocol;
        this.defaultPort=defaultPort;
    }
    
    public String getName() {
        return name;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public Integer getDefaultPort() {
        return defaultPort;
    }
}
