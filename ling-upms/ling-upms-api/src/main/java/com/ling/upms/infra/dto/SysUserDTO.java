package com.ling.upms.infra.dto;

import lombok.Data;

@Data
public class SysUserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private String status;
    private String createTime;
    private String updateTime;
    private String deleteTime;
}
