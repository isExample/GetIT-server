package com.example.getIt.controller;


import com.example.getIt.DTO.UserDTO;
import com.example.getIt.entity.UserEntity;
import com.example.getIt.service.UserService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import com.example.getIt.util.BaseResponseStatus;
import org.springframework.web.bind.annotation.*;
import static com.example.getIt.util.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @ResponseBody
    @GetMapping("/sign-in")
    public BaseResponse<UserDTO.PostUserRes> signIn(@RequestBody UserDTO.User user){
        try {
            if(user.getEmail() == null || user.getName() == null
                    || user.getNickName() == null || user.getPassword() == null){
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY);
            }

            if(!isRegexEmail(user.getEmail())){
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
            }

            if(userService.isHaveEmail(user.getEmail()) != null){
                return new BaseResponse<>(BaseResponseStatus.DUPLICATE_EMAIL);
            }

            if(userService.isHaveNickName(user.getNickName())!= null){
                return new BaseResponse<>(BaseResponseStatus.DUPLICATE_NICKNAME);
            }

            UserDTO.PostUserRes postUserRes = this.userService.signIn(user);
            return new BaseResponse<>(postUserRes);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    //회원 조회
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<UserDTO.GetUserRes> getUser(@PathVariable("userIdx")Long userIdx){
        try {
            UserDTO.GetUserRes userInfo = userService.getUser(userIdx);
            return new BaseResponse<>(userInfo);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

}
