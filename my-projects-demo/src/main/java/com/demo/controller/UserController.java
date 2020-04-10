package com.demo.controller;


import com.demo.dto.User;
import com.demo.exception.UserNotExistException;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    @JsonView(User.UserSimpleView.class)
    @ApiOperation(value = "用户查询服务")
    public List<User> query(@ApiParam("用户名") @RequestParam(value = "username",required = false,defaultValue = "tom") String username){
        System.out.println(username);
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        users.add(new User());
        return users;
    }

    /**
     * ://限制类型
     * @param id
     * @return
     */
    @GetMapping("/{id:\\d+}")
    @JsonView(User.UserDetailView.class)
    public User getInfo(@PathVariable String id){
        User user = new User();
        user.setUsername("mimi");
        return user;
    }

    @PostMapping
    @JsonView(User.UserSimpleView.class)
    public User create(@Valid  @RequestBody User user,BindingResult errors){

        //BindingResult 参数错误处理
        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach(error ->{
                System.out.println(error.getDefaultMessage());
            });
        }
        return user;
    }


    @PutMapping("/{id:\\d+}")
    public User update(@PathVariable String id,@RequestBody User user,BindingResult errors){

        if (errors.hasErrors()) {
            errors.getAllErrors().stream().forEach(error ->{
                FieldError fieldError = (FieldError) error;
                String msg = fieldError.getField() + ":" + fieldError.getDefaultMessage();
                System.out.println(msg);
            });
        }
        return user;
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable String id){

        throw new UserNotExistException(id);
    }

}
