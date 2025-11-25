package road_friend.road_friend_server.JWT;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import road_friend.road_friend_server.Repository.MemberRepository;
import road_friend.road_friend_server.domain.Member;
import road_friend.road_friend_server.domain.Role;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    @Value("${app.oauth2.authorizedRedirectUri}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Member member;

        //DB에 없으면 저장
        if(memberRepository.findByEmail(email)==null){
             member = new Member();
            member.setRole(Role.USER);
            member.setNickname(name);
            member.setEmail(email);
            memberRepository.saveMember(member);

        }
        else{

            member = memberRepository.findByEmail(email);


        }


        // JWT 발급
        String token = jwtUtil.generateToken(member.getEmail(),member.getRole().name());


        // 환경변수 기반 redirect
        String finalUrl = redirectUrl + "?token=" + token;



        getRedirectStrategy().sendRedirect(request, response, finalUrl );
    }
}