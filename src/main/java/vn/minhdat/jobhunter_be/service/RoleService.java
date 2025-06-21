package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Permission;
import vn.minhdat.jobhunter_be.entity.Role;
import vn.minhdat.jobhunter_be.repository.PermissionRepository;
import vn.minhdat.jobhunter_be.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role handleCreateRole(Role role) {
        if(role.getPermissions() != null){
            List<Long> permissionIds = role.getPermissions().stream().map(Permission::getPermissionId).toList();
            List<Permission> permissions = this.permissionRepository.findByPermissionIdIn(permissionIds);
            role.setPermissions(permissions);
        }
        return this.roleRepository.save(role);
    }

    public Role handleUpdateRole(Role role) {
        Role resRole = this.handleGetRoleById(role.getRoleId());

        if(role.getPermissions() != null){
            List<Long> permissionIds = role.getPermissions().stream().map(Permission::getPermissionId).toList();
            List<Permission> permissions = this.permissionRepository.findByPermissionIdIn(permissionIds);
            role.setPermissions(permissions);
        }

        resRole.setDescription(role.getDescription());
        resRole.setActive(role.isActive());
        resRole.setName(role.getName());
        resRole.setPermissions(role.getPermissions());

        return this.roleRepository.save(resRole);
    }

    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }

    public Role handleGetRoleById(long id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    public Role handleGetRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    public ResultPaginationResponse handleGetAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(page.getTotalElements());
        meta.setPages(page.getTotalPages());

        return new ResultPaginationResponse(meta, page.getContent());
    }

    public boolean handleExistRole(Role role) {
        return this.roleRepository.existsByName(role.getName());
    }
}
