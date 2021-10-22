package com.example.petto.controller;

import com.example.petto.controller.request.MemberRequest;
import com.example.petto.entity.Member;
import com.example.petto.service.MemberService;
import com.example.petto.session.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/petto/member")
@CrossOrigin(origins = "http://localhost:8080", allowedHeaders = "*")
public class MemberController {

    private UserInfo info;

    private HttpSession session;

    @Autowired
    MemberService memberService;

    @PostMapping("/idDupliChk/{id}")
    public ResponseEntity<Boolean> idDupliChk(@PathVariable("id") String id) {
        log.info("idDupliChk(): " + id);

        boolean NoDupliId =  memberService.idDupliChk(id);

        return new ResponseEntity<Boolean>(NoDupliId, HttpStatus.OK);
    }

    @PostMapping("/nicknameDupliChk/{nickname}")
    public ResponseEntity<Boolean> nicknameDupliChk(@PathVariable("nickname") String nickname) {
        log.info("nicknameDupliChk(): " + nickname);

        boolean NoDupliNickname =  memberService.nicknameDupliChk(nickname);

        return new ResponseEntity<Boolean>(NoDupliNickname, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Validated @RequestBody MemberRequest memberRequest) {
        log.info("signup(): " + memberRequest);

        memberService.signup(memberRequest);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/checkValidEmail")
    //@ResponseBody
    public ResponseEntity<String> checkValidEmail(@RequestParam("email") String email, @RequestParam("id") String id,
                                                @RequestParam("birthday") String birthday) {

        if(!id.equals("")) {
            log.info("checkValidEmail(): " + email + ", " + id);

            String confidentialCode = memberService.checkValidEmailForPw(email, id);

            return new ResponseEntity<String>(confidentialCode, HttpStatus.OK);

        } else {

            log.info("checkValidEmail(): " + email, ", " + birthday);

            boolean isEmailExists = memberService.checkValidEmail(email, birthday);

            String EmailExists = "NotEmailExists";
            if(isEmailExists) EmailExists = "EmailExists";

            return new ResponseEntity<String>(EmailExists, HttpStatus.OK);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<Void> changePassword(@Validated @RequestBody MemberRequest memberRequest) {
        log.info("changePassword(): " + memberRequest);

        memberService.changePassword(memberRequest);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("/getUserInfo/{userNo}")
    public ResponseEntity<Member> getUserInfo(@PathVariable("userNo") Integer userNo) {
        log.info("getUserInfo(): " + userNo);

        Member memberInfo = memberService.getUserInfo(userNo);

        return new ResponseEntity<Member>(memberInfo, HttpStatus.OK);
    }

    @PutMapping("/modifyUserInfo")
    public ResponseEntity<Void> modifyUserInfo(@Validated @RequestBody Member member) {
        log.info("modifyUserInfo(): " + member);

        memberService.modifyUserInfo(member);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfo> jpaLogin(
            @RequestBody MemberRequest memberRequest,
            HttpServletRequest request
    ) throws Exception {

        log.info("login() - id: " + memberRequest.getId() + ", password: " + memberRequest.getPassword());

        Boolean isSuccess = memberService.login(memberRequest);

        if (isSuccess) {
            log.info("Login Success");
            info = new UserInfo();
            info.setId(memberRequest.getId());
            log.info("Session Info: " + info);

            session = request.getSession();
            session.setAttribute("member", info);
        } else {
            log.info("Login Failure");
            info = null;
        }

        return new ResponseEntity<UserInfo>(info, HttpStatus.OK);
    }

    @PostMapping("/needSession")
    public ResponseEntity<Boolean> postNeedSession(HttpServletRequest request) throws Exception {

        //UserInfo info = (UserInfo) session.getAttribute("member");
        Object obj = session.getAttribute("member");

        Boolean isLogin = false;

        if (obj != null) {
            log.info("Session Info: " + info);

            isLogin = memberService.checkIdValidation(info.getId());

            return new ResponseEntity<Boolean>(isLogin, HttpStatus.OK);
        }

        return new ResponseEntity<Boolean>(isLogin, HttpStatus.OK);
    }


    @PostMapping("/removeSession")
    public ResponseEntity<Boolean> removeSession(HttpServletRequest request) throws Exception {
        Boolean mustFalse = false;

        session.invalidate();

        return new ResponseEntity<Boolean>(mustFalse, HttpStatus.OK);
    }

}
