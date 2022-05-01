package kr.giljabi.jwt.filter;


import javax.servlet.*;
import java.io.IOException;

public class MyFilter2 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("MyFilter2");

        //chain을 사용하는 이유는 filter이후 계속 수행되어야 하기 때문...
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
