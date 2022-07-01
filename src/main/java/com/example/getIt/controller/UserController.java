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
    public BaseResponse<String> signIn(@RequestBody UserDTO.User user){
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


            UserEntity userEntity = this.userService.signIn(user);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

}
