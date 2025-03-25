package com.example.web_nhom_5.conventer;

import com.example.web_nhom_5.dto.request.UserCreationRequest;
import com.example.web_nhom_5.dto.request.UserProfileUpdateRequest;
import com.example.web_nhom_5.dto.request.UserUpdateRequest;
import com.example.web_nhom_5.dto.response.RoleResponse;
import com.example.web_nhom_5.dto.response.UserResponse;
import com.example.web_nhom_5.entity.Role;
import com.example.web_nhom_5.entity.UserEntity;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Microsoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toUser(UserCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.userName( request.getUserName() );
        userEntity.email( request.getEmail() );
        userEntity.password( request.getPassword() );
        userEntity.fullName( request.getFullName() );

        return userEntity.build();
    }

    @Override
    public UserResponse toUserResponse(UserEntity users) {
        if ( users == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId( String.valueOf( users.getId() ) );
        userResponse.setUserName( users.getUserName() );
        userResponse.setEmail( users.getEmail() );
        userResponse.setFullName( users.getFullName() );
        userResponse.setRoles( roleSetToRoleResponseSet( users.getRoles() ) );

        return userResponse;
    }

    @Override
    public void updateUser(UserEntity users, UserUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getEmail() != null ) {
            users.setEmail( request.getEmail() );
        }
        if ( request.getFullName() != null ) {
            users.setFullName( request.getFullName() );
        }
    }

    @Override
    public void updateProfile(UserEntity users, UserProfileUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getEmail() != null ) {
            users.setEmail( request.getEmail() );
        }
        if ( request.getFullName() != null ) {
            users.setFullName( request.getFullName() );
        }
    }

    protected RoleResponse roleToRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponse roleResponse = new RoleResponse();

        roleResponse.setName( role.getName() );
        roleResponse.setDescription( role.getDescription() );

        return roleResponse;
    }

    protected Set<RoleResponse> roleSetToRoleResponseSet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleResponse> set1 = new LinkedHashSet<RoleResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleToRoleResponse( role ) );
        }

        return set1;
    }
}
