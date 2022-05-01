package kr.giljabi.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kr.giljabi.jwt.config.auth.PrincipalDetails;
import kr.giljabi.jwt.entity.User;
import kr.giljabi.jwt.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Security가 가지고 있는 필터중에서 BasicAuthenticationFilter가 있음
//권한이나 인증이 필요한 특정주소를 요청하면 위 필터를 사용하고
//권한이 필요없는 주소는 이 필터를 사용하지 않음
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    //인증, 권한이 필요한 주소요청이 있으면 해당 필터를 사용한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //super.doFilterInternal(request, response, chain);
        System.out.println("인증이나 권한이 필요한 주소 요청입니다....");

        String authorization = request.getHeader("Authorization");
        System.out.println("jwtHeader = " + authorization);

        //JWT 토큰이 정상인지 확인
        if(authorization == null || !authorization.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];
        System.out.println("token = " + token);
        String username = JWT.require(Algorithm.HMAC512("password key"))
                .build().verify(token)
                .getClaim("username")
                .asString();

        //서명이 정상임
        if(username != null) {
            User userEntity = userRepository.findByusername(username);
            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            //서명이 정상이면 임의의 Authentication을 생성
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            //강제로 Security 세션에 Authentication을 저장한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
