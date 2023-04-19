package com.kesei.rag.entity.dto.user;

import com.kesei.rag.entity.dto.GenericPostRequest;
import lombok.Data;

/**
 * @author kesei
 */
@Data
public class UserInfoPostRequest extends GenericPostRequest {
    private Long id;
    private String userName;
    private String userAccount;
    private String userRole;
    private String userPassword;
    private String checkPassword;
}
