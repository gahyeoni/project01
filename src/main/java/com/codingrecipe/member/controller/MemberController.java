package com.codingrecipe.member.controller;

import com.codingrecipe.member.dto.MemberDTO;
import com.codingrecipe.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor //lombok에서 제공//생성자주입 어노테이션
public class MemberController {
    //생성자 주입(자동적으로 MemberController class가 MemberService의 필드,자원을 사용할 수 있음
    private final MemberService memberService; //이 필드를 매개변수로 하는 생성자를 만듦

    //회원가입 페이지 출력 요청
    @GetMapping("/member/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/member/save")
    //RequestParam(html에 지정해준 name)값을 -> String memberEmail에 옮겨담는다.
//    public String save(@RequestParam("memberEmail") String memberEmail,
//                       @RequestParam("memberPassword") String memberPassword,
//                       @RequestParam("memberName") String memberName) {}

    //View와 Controller 사이에 데이터를 주고받기 위해 DTO사용
    public String save(@ModelAttribute MemberDTO memberDTO) {

        System.out.println("MemberController.save"); //soutp (매개변수값 알아서 자동완성)
        System.out.println("memberDTO = " + memberDTO);
        memberService.save(memberDTO);
        return "login";
    }
    //로그인하기
    @GetMapping("/member/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/member/login")                         //로그인 된 상태로 사이트 이용가능하게 만들기
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if (loginResult != null) {
            // login 성공
            session.setAttribute("loginEmail", loginResult.getMemberEmail());
            return "main";
        } else {
            // login 실패
            return "login";
        }
    }

    //회원목록 출력하기
    @GetMapping("/member/")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        //어떠한 html로 가져갈 데이터가 있다면 Model사용
        model.addAttribute("memberList", memberDTOList);
        return "list";
    }

    //회원정보 상세조회
    @GetMapping("/member/{id}") //list.html 내 th${id}
                        //{id}경로상의 값을 받아주는 어노테이션
    public String findById(@PathVariable Long id, Model model){
        //한개의 ID에 대한 정보므로 list가 아닌 객체타입으로 받아줌
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "detail";
    }

    //회원정보 수정하기
    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model){
        //session에 있는 로그인 이메일 값->전체정보를 DB->
        //->Model담기->update.html로 가져가기
        //session.getAttribute("logionEmail")Object-> String 강제형변환
        String myEmail = (String)session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.updateForm(myEmail);
        model.addAttribute("updateMember", memberDTO);
        return "update";
    }

    //수정한 값 객체에 저장
    @PostMapping("/member/update")
    public String update(@ModelAttribute MemberDTO memberDTO){
        memberService.update(memberDTO);
        return "redirect:/member/" + memberDTO.getId();
    }

    //회원 삭제하기
    @GetMapping("/member/delete/{id}")
    public String deleteById(@PathVariable Long id){
        memberService.deleteById(id);
        return "redirect:/member/";
    }

    //로그아웃하기
    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        //session을 무효화하기
        session.invalidate();
        //HomeController-@GetMapping("/") 코드과정이 없으므로 그냥 html바로 return
        return "index";
    }

    //이메일 중복체크/입력한 이메일 서버로 보내기(save.html)
    @PostMapping("/member/email-check")
    //ajax사용할 때 ResponseBody 무조건 사용
    public @ResponseBody String emailCheck(@RequestParam("memberEmail") String memberEmail){
        System.out.println("memberEmail = " + memberEmail);
        String checkResult = memberService.emailCheck(memberEmail);
        return checkResult;
//        if(checkResult != null){
//            return "ok";
//        }else {
//            return "no";
//        }

    }

}


