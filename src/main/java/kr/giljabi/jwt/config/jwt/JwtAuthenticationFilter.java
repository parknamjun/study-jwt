package kr.giljabi.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.giljabi.jwt.config.auth.PrincipalDetails;
import kr.giljabi.jwt.config.auth.PrincipalDetailsService;
import kr.giljabi.jwt.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

/**
 * Spring 시큐리티의 UsernamePasswordAuthenticationFilter..
 * login 요청에서 username, password를 post로 전송하면
 * UsernamePasswordAuthenticationFilter 작동
 *
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // /login 요청을 하면 로그인 시도를 위해 실행된다.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter login");
        //1. usernmae, password 입력
        try {
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println("user = " + user);
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            //PrincipalDetailService의 loadUserByUsername 호출되고 정상이면 authentication이 리턴
            //DB에 있는 username, password 일치
            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);
            PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();

            //값이 있으면 정상적인 로그인이 됨
            System.out.println("Login 완료 = " + principalDetails.getUser().getUsername());

            //authentication을 session에 저장
            //리턴은 권한관리를 security가 대신하므로 관리가 편리함
            //JWT 토큰을 사용하면서 session을 만들 필요는 없음, 권한처리 때문에 session에 저장함
            return authentication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //attemptAuthentication 이후 호출됨
    //JWT token을 만들어 등답한다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        System.out.println("successfulAuthentication = " + request);
        System.out.println("인증완료");
        PrincipalDetails principalDetails = (PrincipalDetails)authResult.getPrincipal();

        //Hash 암호화 방식
        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))   //10qns
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("password key"));
        response.addHeader("Authorization", "Bearer " + jwtToken);
        //super.successfulAuthentication(request, response, chain, authResult);
    }
}
