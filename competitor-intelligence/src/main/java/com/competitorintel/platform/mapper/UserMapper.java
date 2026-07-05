package com.competitorintel.platform.mapper;

import com.competitorintel.platform.domain.entity.User;
import com.competitorintel.platform.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "active",        source = "active")
    @Mapping(target = "emailVerified", source = "emailVerified")
    @Mapping(target = "fullName",      expression = "java(user.getFullName())")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
