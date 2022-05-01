package com.voyager.community.config;

import com.voyager.community.service.UserService;
import com.voyager.community.util.CommunityConstant;
import com.voyager.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Voyager1
 * @create 2022-03-30 10:03
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Autowired
    private UserService userService;
    /**
     * 忽略静态资源的访问
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    // 认证环节我们使用自己的代码 LoginController，绕过 Spring Security 的

    //AuthenticationManager:认证的核心接口
    //AuthenticationManagerBuilder:用于构建 AuthenticationManager 对象的工具
    //ProviderManager:AuthenticationManager 接口默认实现类
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception{
//        //内置认证规则
//        //auth.userDetailsService(userService).passwordEncoder(new Pbkdf2PasswordEncoder("1234"))//加盐
//
//        //自定义认证规则
//        //AuthenticationProvider：ProviderManager持有一组 AuthenticationProvider 每个AuthenticationProvider负责一种认证
//        //采用委托模式：ProviderManager 将认证委托给  AuthenticationProvider
//
//        auth.authenticationProvider(new AuthenticationProvider() {
//           //Authentication: 用于封装认证信息的接口，不同的实现类代表不同类型的认证信息
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                String username = authentication.getName();
//                String password = (String) authentication.getCredentials();
//
//                User user = userService.findUserByName(username);
//                if(user == null){
//                    throw new UsernameNotFoundException("账号不存在");
//                }
//                password = CommunityUtil.md5(password + user.getSalt());
//                if(!user.getPassword().equals(password)){
//                    throw new BadCredentialsException("密码不正确");
//                }
//                return null;
//
//                //return new UsernamePasswordAuthenticationToken(user,user.getPassword(),user.getAuthorities());
//            }
//
//            //当前 AuthenticationProvider 支持哪种类型的认证
//            @Override
//            public boolean supports(Class<?> aClass) {
//                //UsernamePasswordAuthenticationToken: AuthenticationProvider 常用的实现类
//                return UsernamePasswordAuthenticationToken.class.equals(aClass);
//            }
//        });
//    }

    /**
     * 授权
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权配置
        http.authorizeRequests()
                .antMatchers(//添加访问路径
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/discuss/publish",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(//这些用户拥有访问以上路径的权力
                        AUTHORITY_USER,//用户
                        AUTHORITY_ADMIN,//管理员
                        AUTHORITY_MODERATOR//版主
                )

                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )

                .antMatchers(
                        "/discuss/delete",
                        "/discuss/delete/",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )

                .anyRequest().permitAll()

                .and().csrf().disable();

        // 权限不够时的处理
        http.exceptionHandling()
                // 1. 未登录时的处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");//判段是否是异步请求：请求消息头的key为x-requested-with的值
                        if ("XMLHttpRequest".equals(xRequestedWith)) {//如果是XMLHttpRequest则为异步请求
                            // 异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你还没有登录"));
                        }
                        else {
                            // 普通请求
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                // 2. 权限不够时的处理
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {
                            // 异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你没有访问该功能的权限"));
                        }
                        else {
                            // 普通请求
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

        // Security 底层会默认拦截 /logout 请求，进行退出处理
        // 此处赋予它一个根本不存在的退出路径，覆盖它默认的逻辑，使得程序能够执行到我们自己编写的退出代码
        http.logout().logoutUrl("/securitylogout");

        http.headers().frameOptions().sameOrigin();
    }
}
