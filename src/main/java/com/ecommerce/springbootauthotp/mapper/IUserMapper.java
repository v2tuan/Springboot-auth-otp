package com.ecommerce.springbootauthotp.mapper;

import com.ecommerce.springbootauthotp.dtos.request.UserDTO;
import com.ecommerce.springbootauthotp.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {
    User toUser(UserDTO userDTO);
}
