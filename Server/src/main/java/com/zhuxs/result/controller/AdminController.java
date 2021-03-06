package com.zhuxs.result.controller;

import com.zhuxs.result.domain.entity.Permission;
import com.zhuxs.result.domain.entity.Role;
import com.zhuxs.result.domain.entity.User;
import com.zhuxs.result.dto.PermissionDto;
import com.zhuxs.result.dto.RoleDto;
import com.zhuxs.result.dto.UserDto;
import com.zhuxs.result.service.PermissionService;
import com.zhuxs.result.service.RoleService;
import com.zhuxs.result.service.UserService;
import com.zhuxs.result.utils.ApplicationUtil;
import org.hibernate.annotations.Parameter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shusesshou on 2017/9/25.
 */
@RequestMapping(value = AdminController.PATH,produces = MediaType.APPLICATION_JSON_VALUE)
@Controller
public class AdminController {
    public static final String PATH = "admin";

    public static final String SUBPATH_ROLE = "/roles";
    public static final String SUBPATH_USER = "/users";
    public static final String SUBPATH_PERMISSION = "/permissions";

    public static final String PATHVARIABLE_ID = "/{id}";

    public static final String USER_ID = "userId";

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    @PostMapping(value = SUBPATH_PERMISSION)
    public ResponseEntity<Permission> addPermission(@RequestBody PermissionDto permissionDto,
                                        UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_PERMISSION);
        permissionDto.setResource("");
        String name = permissionDto.getResourceType() + ":" + permissionDto.getAction();
        permissionDto.setName(name);

        Permission permission = convertToEntity(permissionDto);
        permissionService.addPermission(permission);
        return new ResponseEntity<Permission>(headers, HttpStatus.OK);
    }

    @GetMapping(value = SUBPATH_PERMISSION)
    public ResponseEntity<List<PermissionDto>> listPermissions(UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_PERMISSION);
        List<Permission> permissions = permissionService.listPermissions();
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(permission -> {
                    return convertToDto(permission);
                })
                .collect(Collectors.toList());
        return new ResponseEntity<List<PermissionDto>>(permissionDtos,headers,HttpStatus.OK);
    }

    @GetMapping(value = SUBPATH_PERMISSION,
            params = USER_ID)
    public ResponseEntity<List<PermissionDto>> getRolesByPermissionId(UriComponentsBuilder uriComponentsBuilder,
                                                          @RequestParam long userId){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_PERMISSION);
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        List<PermissionDto> permissionDtos = permissions.stream()
                .map(permission -> {
                    return convertToDto(permission);
                })
                .collect(Collectors.toList());

        return new ResponseEntity<List<PermissionDto>>(permissionDtos,headers,HttpStatus.OK);
    }


    @DeleteMapping(value = SUBPATH_PERMISSION + PATHVARIABLE_ID)
    public ResponseEntity<Long> deletePermissionById(UriComponentsBuilder uriComponentsBuilder,
                                                              @PathVariable long id){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_PERMISSION + "/" + id);
        permissionService.delPermissionById(id);
        return new ResponseEntity<Long>(id,headers,HttpStatus.OK);
    }

    @PostMapping(value = SUBPATH_ROLE)
    public ResponseEntity<RoleDto> addRole(@RequestBody RoleDto roleDto,
                                           UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_ROLE);
        Role role = convertToEntity(roleDto);
        roleService.addRole(role);
        return new ResponseEntity<RoleDto>(headers,HttpStatus.OK);
    }

    @GetMapping(value = SUBPATH_ROLE)
    public ResponseEntity<List<RoleDto>> listRoles(UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_ROLE);
        List<Role> roles = roleService.listRoles();
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> {
                    return convertToDto(role);
                })
                .collect(Collectors.toList());
        return new ResponseEntity<List<RoleDto>>(roleDtos,headers,HttpStatus.OK);
    }

    @GetMapping(value = SUBPATH_ROLE,
            params = USER_ID)
    public ResponseEntity<List<RoleDto>> getRolesByUserId(UriComponentsBuilder uriComponentsBuilder,
                                                          @RequestParam long userId){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_ROLE);
        List<Role> roles = roleService.getRolesByUserId(userId);
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> {
                    return convertToDto(role);
                })
                .collect(Collectors.toList());
        return new ResponseEntity<List<RoleDto>>(roleDtos,headers,HttpStatus.OK);
    }

    @PutMapping(value = SUBPATH_ROLE + PATHVARIABLE_ID + SUBPATH_PERMISSION)
    public ResponseEntity<RoleDto> updatePermissionsById(@PathVariable long id,@RequestBody List<PermissionDto> permissionDtos,
                                                      UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_ROLE + PATHVARIABLE_ID + SUBPATH_PERMISSION);
        List<Permission> permissions = permissionDtos.stream()
                .map(permissionDto -> convertToEntity(permissionDto))
                .collect(Collectors.toList());
        Role role = roleService.updatePermissionsById(id,permissions);
        RoleDto roleDto = convertToDto(role);
        return new ResponseEntity<RoleDto>(roleDto,headers,HttpStatus.OK);
    }

    @DeleteMapping(value = SUBPATH_ROLE + PATHVARIABLE_ID)
    public ResponseEntity<Long> deleteRoleById(UriComponentsBuilder uriComponentsBuilder,
                                                     @PathVariable long id){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_ROLE + "/" + id);
        roleService.delRoleById(id);
        return new ResponseEntity<Long>(id,headers,HttpStatus.OK);
    }


    @PostMapping(value = SUBPATH_USER)
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto,
                                           UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_USER);
        User user = convertToEntity(userDto);
        userService.addUser(user);
        return new ResponseEntity<UserDto>(headers,HttpStatus.OK);
    }

    @GetMapping(value = SUBPATH_USER)
    public  ResponseEntity<List<UserDto>> listUsers(UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_USER);
        List<User> users = userService.listUsers();
        List<UserDto> userDtos = users.stream()
                .map(user -> {
                    return convertToDto(user);
                })
                .collect(Collectors.toList());
        return new ResponseEntity<List<UserDto>>(userDtos,headers,HttpStatus.OK);
    }

    @PutMapping(value = SUBPATH_USER + PATHVARIABLE_ID + SUBPATH_ROLE)
    public ResponseEntity<UserDto> updateRolesById(@PathVariable Long id, @RequestBody List<RoleDto> roleDtos,
                                                UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,SUBPATH_USER + "/" + id.toString() + SUBPATH_ROLE);
        List<Role> roles = roleDtos.stream()
                .map(roleDto -> convertToEntity(roleDto))
                .collect(Collectors.toList());
        User user = userService.updateRolesById(id,roles);
        UserDto userDto = convertToDto(user);
        return new ResponseEntity<UserDto>(userDto,headers,HttpStatus.OK);
    }

    @PutMapping(value = SUBPATH_USER + PATHVARIABLE_ID + SUBPATH_PERMISSION)
    public ResponseEntity<UserDto> updatePermissionsById(@PathVariable Long id, @RequestBody List<PermissionDto> permissionDtos,
                                                   UriComponentsBuilder uriComponentsBuilder){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,SUBPATH_USER + "/" + id.toString() + SUBPATH_PERMISSION);
        List<Permission> permissions = permissionDtos.stream()
                .map(permissionDto -> convertToEntity(permissionDto))
                .collect(Collectors.toList());
        User user = userService.updatePermissionsById(id,permissions);
        UserDto userDto = convertToDto(user);
        return new ResponseEntity<UserDto>(userDto,headers,HttpStatus.OK);
    }


    @DeleteMapping(value = SUBPATH_USER + PATHVARIABLE_ID)
    public ResponseEntity<Long> deleteUserById(UriComponentsBuilder uriComponentsBuilder,
                                                     @PathVariable long id){
        HttpHeaders headers = ApplicationUtil.getHttpHeaders(uriComponentsBuilder,PATH + SUBPATH_USER + "/" + id);
        userService.delUserById(id);
        return new ResponseEntity<Long>(id,headers,HttpStatus.OK);
    }


    private Permission convertToEntity(PermissionDto permissionDto){
        return modelMapper.map(permissionDto,Permission.class);
    }
    private PermissionDto convertToDto(Permission permission){
        return modelMapper.map(permission,PermissionDto.class);
    }

    private Role convertToEntity(RoleDto roleDto){
        return modelMapper.map(roleDto,Role.class);
    }
    private RoleDto convertToDto(Role role){
        return modelMapper.map(role,RoleDto.class);
    }

    private User convertToEntity(UserDto userDto){
        return modelMapper.map(userDto,User.class);
    }

    private UserDto convertToDto(User user){
        return modelMapper.map(user,UserDto.class);
    }
}
