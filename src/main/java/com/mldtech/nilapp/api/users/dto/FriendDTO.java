package com.mldtech.nilapp.api.users.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendDTO {
    private Long yourId;
    private Long friendId;
//    private Long friendUserId;
    private String friendUsername;
    private Integer statusId;
    private String status;
    private String statusDescription;
}
