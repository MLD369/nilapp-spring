package com.mldtech.nilapp.api.users.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateUserEntitiesRequest {
    private List<Long> entityIds;
}

