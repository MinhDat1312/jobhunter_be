package vn.minhdat.jobhunter_be.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.minhdat.jobhunter_be.entity.Permission;
import vn.minhdat.jobhunter_be.entity.Role;
import vn.minhdat.jobhunter_be.entity.User;
import vn.minhdat.jobhunter_be.exception.PermissionException;
import vn.minhdat.jobhunter_be.service.UserService;
import vn.minhdat.jobhunter_be.util.SecurityUtil;

import java.util.List;

@Transactional
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String method = request.getMethod();

        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if(!email.isEmpty()) {
            User user = this.userService.handleGetUserByEmail(email);
            if(user != null) {
                Role role = user.getRole();
                if(role != null) {
                    List<Permission> permissions = user.getRole().getPermissions();
                    boolean hasPermission = permissions.stream().anyMatch(
                            p -> p.getApiPath().equals(path) && p.getMethod().equals(method)
                    );
                    if(!hasPermission) {
                        throw new PermissionException("You don't have permission to access");
                    }
                } else {
                    throw new PermissionException("You don't have permission to access");
                }
            }
        }

        return true;
    }
}
