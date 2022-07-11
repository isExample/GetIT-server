package com.example.getIt.user.controller;


import com.example.getIt.user.jwt.DTO.TokenDTO;
import com.example.getIt.user.DTO.UserDTO;
import com.example.getIt.user.service.UserService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import com.example.getIt.util.BaseResponseStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping("/sign-in")
    public BaseResponse<TokenDTO> signIn(@RequestBody UserDTO.User user){
        try {
            TokenDTO token = this.userService.signIn(user);
            return new BaseResponse<>(token);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    //회원 조회
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<UserDTO.User> getUser(@PathVariable("userIdx")Long userIdx){
        try {
            UserDTO.User userInfo = userService.getUser(userIdx);
            return new BaseResponse<>(userInfo);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @ResponseBody
    @GetMapping("/login")
    public BaseResponse<TokenDTO> logIn(@RequestBody UserDTO.User user){
        try {
            if(user.getEmail()==null){
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_NICKNAME);
            }

            if(user.getPassword()==null){
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
            }
            TokenDTO postUserRes = userService.logIn(user);

            return new BaseResponse<>(postUserRes);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

}
