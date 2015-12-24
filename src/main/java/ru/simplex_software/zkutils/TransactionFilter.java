package ru.simplex_software.zkutils;

import org.springframework.transaction.annotation.Transactional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**.*/
public class TransactionFilter implements Filter {


    public void init(FilterConfig filterConfig) throws ServletException {}

    @Transactional
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(req, res);
    }

    public void destroy() { }
}
