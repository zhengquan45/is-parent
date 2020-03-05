package org.zhq.security;

import lombok.Data;

@Data
public class TokenInfo {

    private String access_token;
    private String token_type;
    private Long expire_in;
    private String scope;
}
