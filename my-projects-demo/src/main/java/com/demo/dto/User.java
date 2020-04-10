package com.demo.dto;

import com.demo.validate.MyConstraint;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Past;
import java.util.Date;

/**
 * jsonview 指定不同的视图返回
 */
public class User {

    public interface  UserSimpleView{};
    public interface  UserDetailView extends UserSimpleView{};

    private String id;
    @MyConstraint(message = "这是一个测试")
    private String username;

    @NotBlank(message = "生日必须是过去的时间")
    private String password;
    //前端传时间戳 由前端控制格式
    @Past
    @ApiModelProperty(value = "生日")
    private Date birthday;


    @JsonView(UserSimpleView.class)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonView(UserDetailView.class)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonView(UserSimpleView.class)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonView(UserSimpleView.class)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
