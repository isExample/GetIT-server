package com.example.getIt.user.controller;


import com.example.getIt.jwt.DTO.TokenDTO;
import com.example.getIt.user.DTO.UserDTO;
import com.example.getIt.user.service.UserService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import com.example.getIt.util.BaseResponseStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    // 회원 조회
    // 후에 양방향 매핑할지, 코드를 유지할 지 정해야 할 것으로 보임!
    @ResponseBody
    @GetMapping("/mypage")
    public BaseResponse<UserDTO.UserProtected> getUser(Principal principal){
        try {
            UserDTO.UserProtected userInfo = userService.getUser(principal);
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
    /*
     *pwd 변경
     * */
    @ResponseBody
    @PatchMapping("/pwd")
    public BaseResponse<String> patchPwd(Principal principal, @RequestBody UserDTO.UserPwd user){
        try {
            this.userService.patchPwd(principal, user);
            return new BaseResponse<>("password 정보 수정을 완료했습니다");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /*
    * 좋아요 list
    * */
    @ResponseBody
    @GetMapping("/mylike")
    public BaseResponse<UserDTO.UserLikeList> getUserLikeList(Principal principal){
        try {
            UserDTO.UserLikeList userInfo = userService.getUserLikeList(principal);
            return new BaseResponse<>(userInfo);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /*
    * 토큰 재발급
    * */
    @PostMapping("/reissue")
    public BaseResponse<TokenDTO> reissue(@RequestBody TokenDTO tokenRequestDto) { //RequestBody로 Access Token + Refresh Token를 받는다.
        return new BaseResponse<TokenDTO>(userService.reissue(tokenRequestDto));
    }

    /*
    * 프로필 변경
    * */
    @ResponseBody
    @PatchMapping("/profile")
    public BaseResponse<String> patchProfile(Principal principal, @RequestPart(value = "user", required = false) UserDTO.UserProfile user,
                                             @RequestPart(value = "profileImg", required = false) MultipartFile profileImg){
        try {
            this.userService.patchProfile(principal, user, profileImg);
            return new BaseResponse<>("프로필 정보를 변경했습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        } catch (IOException e){
            return new BaseResponse<>("IO Exception Error");
        }
    }
}
