package road_friend.road_friend_server.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import road_friend.road_friend_server.Dto.MemberDto;
import road_friend.road_friend_server.JWT.JwtUtil;
import road_friend.road_friend_server.Repository.MemberRepository;
import road_friend.road_friend_server.domain.Member;
import road_friend.road_friend_server.domain.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/auth/signup")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody MemberDto memberdto) {

        Map<String, String> result = new HashMap<>();

        if(memberRepository.findByEmail(memberdto.getEmail())!=null){
             result.put("message","이미 존재하는 이메일입니다.");
             return ResponseEntity.status(404).body(result);


        }

        Member member = new Member();
        member.setEmail(memberdto.getEmail());
        member.setNickname(memberdto.getNickname());
        member.setPassword(passwordEncoder.encode(memberdto.getPassword()));
        member.setRole(Role.USER);
        result.put("message", "회원가입 성공");


        memberRepository.saveMember(member);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>>  login(@RequestBody MemberDto memberDto) {
        Member member = memberRepository.findByEmail(memberDto.getEmail());
        Map<String, String> result = new HashMap<>();

        if(member == null){
            result.put("message","존재하지 않는 이메일 입니다.");
            return ResponseEntity.status(404).body(result);        }




        if(!passwordEncoder.matches(memberDto.getPassword(), member.getPassword())){
            result.put("message","비밀번호가 틀렸습니다.");
            return ResponseEntity.status(404).body(result);        }

        String token = jwtUtil.generateToken(member.getEmail(), member.getRole().name());
        result.put("message", token);
        return ResponseEntity.ok(result); // ✅ JSON 반환
    }

    @GetMapping("/members")
    public List<Member> getUsers() {
        return memberRepository.findAll();
    }

    // jwt로 본인 확인 가능하나 확인용 api
    @GetMapping("/me")
    public MemberDto getMyInfo(Authentication authentication){
        Member member = (Member) authentication.getPrincipal();
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail(member.getEmail());
        memberDto.setNickname(member.getNickname());
        memberDto.setId(member.getId());

        return memberDto;
    }






}
