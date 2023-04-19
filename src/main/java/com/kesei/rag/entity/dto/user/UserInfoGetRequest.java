package com.kesei.rag.entity.dto.user;

import com.kesei.rag.entity.dto.GenericGetRequest;
import lombok.Data;

/**
 * @author kesei
 */
@Data
public class UserInfoGetRequest extends GenericGetRequest {
    private Long id;
    private String userName;
    private String userAccount;
    private String userRole;
}
