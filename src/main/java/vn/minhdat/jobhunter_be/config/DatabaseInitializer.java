package vn.minhdat.jobhunter_be.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.common.Gender;
import vn.minhdat.jobhunter_be.entity.*;
import vn.minhdat.jobhunter_be.entity.embeddable.Contact;
import vn.minhdat.jobhunter_be.repository.PermissionRepository;
import vn.minhdat.jobhunter_be.repository.RoleRepository;
import vn.minhdat.jobhunter_be.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository,
                               UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Database initialization is starting...");

        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if(countPermissions == 0){
            ArrayList<Permission> permissions = new ArrayList<>();

            permissions.add(new Permission("Create recruiter", "/api/v1/recruiters", "POST", "RECRUITERS"));
            permissions.add(new Permission("Update recruiter", "/api/v1/recruiters", "PUT", "RECRUITERS"));
            permissions.add(new Permission("Delete recruiter", "/api/v1/recruiters/{id}", "DELETE", "RECRUITERS"));
            permissions.add(new Permission("Get recruiter", "/api/v1/recruiters/{id}", "GET", "RECRUITERS"));
            permissions.add(new Permission("Get all recruiters", "/api/v1/recruiters", "GET", "RECRUITERS"));

            permissions.add(new Permission("Create applicant", "/api/v1/applicants", "POST", "APPLICANTS"));
            permissions.add(new Permission("Update applicant", "/api/v1/applicants", "PUT", "APPLICANTS"));
            permissions.add(new Permission("Delete applicant", "/api/v1/applicants/{id}", "DELETE", "APPLICANTS"));
            permissions.add(new Permission("Get applicant", "/api/v1/applicants/{id}", "GET", "APPLICANTS"));
            permissions.add(new Permission("Get all applicants", "/api/v1/applicants", "GET", "APPLICANTS"));

            permissions.add(new Permission("Get all users", "/api/v1/users", "GET", "USERS"));
            permissions.add(new Permission("Update password", "/api/v1/users/update-password", "PUT", "USERS"));


            permissions.add(new Permission("Create career", "/api/v1/careers", "POST", "CAREERS"));
            permissions.add(new Permission("Update career", "/api/v1/careers", "PUT", "CAREERS"));
            permissions.add(new Permission("Delete career", "/api/v1/careers/{id}", "DELETE", "CAREERS"));
            permissions.add(new Permission("Get career", "/api/v1/careers/{id}", "GET", "CAREERS"));
            permissions.add(new Permission("Get all careers", "/api/v1/careers", "GET", "CAREERS"));

            permissions.add(new Permission("Create job", "/api/v1/jobs", "POST", "JOBS"));
            permissions.add(new Permission("Update job", "/api/v1/jobs", "PUT", "JOBS"));
            permissions.add(new Permission("Delete job", "/api/v1/jobs/{id}", "DELETE", "JOBS"));
            permissions.add(new Permission("Get job", "/api/v1/jobs/{id}", "GET", "JOBS"));
            permissions.add(new Permission("Get all jobs", "/api/v1/jobs", "GET", "JOBS"));

            permissions.add(new Permission("Create skill", "/api/v1/skills", "POST", "SKILLS"));
            permissions.add(new Permission("Update skill", "/api/v1/skills", "PUT", "SKILLS"));
            permissions.add(new Permission("Delete skill", "/api/v1/skills/{id}", "DELETE", "SKILLS"));
            permissions.add(new Permission("Get skill", "/api/v1/skills/{id}", "GET", "SKILLS"));
            permissions.add(new Permission("Get all skills", "/api/v1/skills", "GET", "SKILLS"));

            permissions.add(new Permission("Upload file", "/api/v1/files", "POST", "FILES"));
            permissions.add(new Permission("Download file", "/api/v1/files", "GET", "FILES"));

            permissions.add(new Permission("Create application", "/api/v1/applications", "POST", "APPLICATIONS"));
            permissions.add(new Permission("Update application", "/api/v1/applications", "PUT", "APPLICATIONS"));
            permissions.add(new Permission("Delete application", "/api/v1/applications/{id}", "DELETE", "APPLICATIONS"));
            permissions.add(new Permission("Get application", "/api/v1/applications/{id}", "GET", "APPLICATIONS"));
            permissions.add(new Permission("Get all applications by recruiter", "/api/v1/applications", "GET", "APPLICATIONS"));
            permissions.add(new Permission("Get all applications by applicant", "/api/v1/applications/by-applicant", "GET", "APPLICATIONS"));
            permissions.add(new Permission("Get all applications", "/api/v1/all-applications", "GET", "APPLICATIONS"));

            permissions.add(new Permission("Create permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            permissions.add(new Permission("Update permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            permissions.add(new Permission("Delete permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            permissions.add(new Permission("Get permission", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            permissions.add(new Permission("Get all permissions", "/api/v1/permissions", "GET", "PERMISSIONS"));

            permissions.add(new Permission("Create role", "/api/v1/roles", "POST", "ROLES"));
            permissions.add(new Permission("Update role", "/api/v1/roles", "PUT", "ROLES"));
            permissions.add(new Permission("Delete role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            permissions.add(new Permission("Get role", "/api/v1/roles/{id}", "GET", "ROLES"));
            permissions.add(new Permission("Get all roles", "/api/v1/roles", "GET", "ROLES"));

            permissions.add(new Permission("Create a subscriber", "/api/v1/subscribers", "POST", "SUBSCRIBERS"));
            permissions.add(new Permission("Update a subscriber", "/api/v1/subscribers", "PUT", "SUBSCRIBERS"));
            permissions.add(new Permission("Delete a subscriber", "/api/v1/subscribers/{id}", "DELETE", "SUBSCRIBERS"));
            permissions.add(new Permission("Get a subscriber by id", "/api/v1/subscribers/{id}", "GET", "SUBSCRIBERS"));
            permissions.add(new Permission("Get subscribers with pagination", "/api/v1/subscribers", "GET", "SUBSCRIBERS"));

            this.permissionRepository.saveAll(permissions);
        }

        if(countRoles == 0){
            List<Permission> permissions = this.permissionRepository.findAll();

            Role role = new Role();
            role.setName("SUPER_ADMIN");
            role.setDescription("Admin will gain full permissions");
            role.setActive(true);
            role.setPermissions(permissions);

            this.roleRepository.save(role);
        }

        if(countUsers == 0){
            Role role = this.roleRepository.findByName("SUPER_ADMIN");

            User user = new Recruiter();
            user.setAddress("137/20 Phước Long, Nha Trang, Khánh Hòa");
            user.setContact(new Contact("admin@gmail.com", "0987654321"));
            user.setDob(LocalDate.of(2003, 12, 13));
            user.setFullName("Admin");
            user.setGender(Gender.MALE);
            user.setUsername("admin");
            user.setPassword(this.passwordEncoder.encode("12345678"));
            user.setRole(role);

            this.userRepository.save(user);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println("Skip init database ~ Already have data...");
        } else {
            System.out.println("Database initialization ended...");
        }
    }
}
