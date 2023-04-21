package com.kesei.rag.support;

/**
 * @author kesei
 */
public class Constants {
    
    // sort
    
    public static final String SORT_ORDER_ASC="ASC";
    public static final String SORT_ORDER_DESC = "DESC";
    public static final String DEFAULT_SORT_COLUMN="id";
    
    // pagination
    
    public static final Integer DEFAULT_PAGE_SIZE=10;
    public static final Integer DEFAULT_PAGE_NUM =1;
    
    // response
    
    public static final Integer SUCCESS_CODE=20000;
    public static final Integer PARAMS_ERROR_CODE=40000;
    public static final Integer UNAUTHORIZED_ERROR_CODE=40100;
    public static final Integer NOT_FOUND_ERROR_CODE=40400;
    public static final Integer SYSTEM_ERROR_CODE=50000;
    public static final Integer SQL_OPERATION_ERROR_CODE =50001;
    public static final String SUCCESS_MESSAGE="成功";
    public static final String PARAMS_ERROR_MESSAGE="请求错误";
    public static final String UNAUTHORIZED_ERROR_MESSAGE="未登录/权限不足";
    public static final String NOT_FOUND_ERROR_MESSAGE="请求数据不存在";
    public static final String SYSTEM_ERROR_MESSAGE="系统内部错误";
    public static final String SQL_OPERATION_ERROR_MESSAGE ="数据库操作失败";
    
    // mock
    
    public static final Integer SIMPLE_MIN_MOCK_NUM=1;
    public static final Integer SIMPLE_MAX_MOCK_NUM=1000;
    public static final Integer SIMPLE_DEFAULT_MOCK_NUM=20;
    
    // dialect
    
    public static final String MYSQL_ESCAPE_CHARACTER="`";
    
    // role
    
    public static final String ROLE_ADMIN="admin";
    public static final String ROLE_USER="user";
    
    // service
    
    public static final Integer MAX_NAME_LENGTH=30;
    public static final Integer MAX_CONTENT_LENGTH=10000;
    
    // user
    
    public static final String SALT="keseIA";
    public static final Integer MIN_USERNAME_LENGTH=1;
    public static final Integer MAX_USERNAME_LENGTH=16;
    public static final Integer MIN_ACCOUNT_LENGTH=4;
    public static final Integer MAX_ACCOUNT_LENGTH=32;
    public static final Integer MIN_PASSWORD_LENGTH=8;
    public static final Integer MAX_PASSWORD_LENGTH=32;
    public static final String USER_STATE="userState";
    
    // generator
    
    public static final String CURRENT_TIMESTAMP="CURRENT_TIMESTAMP";
    
    // metaTable build
    
    public static final Integer AUTO_MAX_WORD_SIZE=16;
    
    // format
    
    public static final String[] DATE_PATTERNS = {
            "yyyy-MM-dd",
            "yyyy年MM月dd日",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm", "yyyyMMdd"
    };
    
    // template
    
    public static final String FRONT_CODE_TEMPLATE="typescript_type.ftl";
    public static final String JAVA_ENTITY_TEMPLATE="java_entity.ftl";
    public static final String JAVA_OBJECT_TEMPLATE="java_object.ftl";
    
    // database
    
    public static final Integer MAX_ACTIVE_CONNECTION=20;
}
