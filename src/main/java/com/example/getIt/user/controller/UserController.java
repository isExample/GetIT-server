package com.example.getIt.user.controller;


import com.example.getIt.jwt.DTO.TokenDTO;
import com.example.getIt.product.DTO.ProductDTO;
import com.example.getIt.user.DTO.UserDTO;
import com.example.getIt.user.service.UserService;
import com.example.getIt.util.BaseException;
import com.example.getIt.util.BaseResponse;
import com.example.getIt.util.BaseResponseStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

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
    @PostMapping("/login")
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
    public BaseResponse<UserDTO.UserLikeList> getUserLikeList(Principal principal, @RequestParam("type") String type){
        try {
            UserDTO.UserLikeList userInfo = userService.getUserLikeList(principal, type);
            return new BaseResponse<>(userInfo);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /*
     * 토큰 재발급
     * */
    @ResponseBody
    @PostMapping("/reissue")
    public BaseResponse<TokenDTO> reissue(@RequestBody TokenDTO tokenRequestDto, HttpServletRequest request) { //RequestBody로 Access Token + Refresh Token를 받는다.
        try {
            return new BaseResponse<TokenDTO>(userService.reissue(tokenRequestDto, request));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /*
    * 프로필 이미지 변경
    * */
    @ResponseBody
    @PatchMapping("/profileImg")
    public BaseResponse<String> patchProfile(Principal principal,
                                             @RequestPart(value = "profileImg", required = false) MultipartFile profileImg){
        try {
            this.userService.patchProfileImg(principal, profileImg);
            return new BaseResponse<>("프로필 이미지 정보를 변경했습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/profileNickName")
    public BaseResponse<String> patchProfileNickName(Principal principal,
                                                     @RequestBody UserDTO.UserProfile user){
        try {
            this.userService.patchProfileNickname(principal, user);
            return new BaseResponse<>("프로필 닉네임 정보를 변경했습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @DeleteMapping("/withdrawl")
    public BaseResponse<String> deleteUser(Principal principal, HttpServletRequest request){
        try{
            userService.deleteUserData(principal, request);
            return new BaseResponse<>("유저에 대한 정보를 모두 삭제했습니다.");
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/review")
    public BaseResponse<List<UserDTO.UserReview>> getReview(Principal principal){
        try{
            List<UserDTO.UserReview> getReviewList = userService.getUserReview(principal);
            return new BaseResponse<>(getReviewList);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    @ResponseBody
    @GetMapping("/logout")
    public BaseResponse<String> logout(Principal principal, HttpServletRequest request){
        try{
            userService.logout(principal, request);
            return new BaseResponse<>("로그아웃 되었습니다.");
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
