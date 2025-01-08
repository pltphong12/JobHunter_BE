package org.example.jobhunter.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.example.jobhunter.domain.Permission;
import org.example.jobhunter.domain.Role;
import org.example.jobhunter.domain.User;
import org.example.jobhunter.service.ResumeService;
import org.example.jobhunter.service.UserService;
import org.example.jobhunter.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Autowired
    ResumeService resumeService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : null;
        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleFetchUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream().anyMatch(items -> items.getApiPath().equals(path) && items.getMethod().equals(httpMethod));
//                    long resumeId = Integer.parseInt(requestURI.substring(16));
//                    Resume resume = this.resumeService.fetchResumeById(resumeId);
//                    String a = resume.getJob().getCompany().getName();
//                    String b = user.getCompany().getName();
//                    if (!isAllow) {
//                        throw new BadRequestException("User don't permit access to is allow " + path);
//                    }
//                    else {
//                        return true;
//                    }
                }
                else {
                    throw new BadRequestException("User don't permit access to " + path);
                }
            }
        }
        return true;
    }
}

