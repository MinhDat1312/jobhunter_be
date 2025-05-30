package vn.minhdat.jobhunter_be.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.minhdat.jobhunter_be.dto.response.ResultPaginationResponse;
import vn.minhdat.jobhunter_be.entity.Permission;
import vn.minhdat.jobhunter_be.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission handleUpdatePermission(Permission permission) {
        Permission resPermission = this.handleGetPermissionById(permission.getPermissionId());

        resPermission.setApiPath(permission.getApiPath());
        resPermission.setMethod(permission.getMethod());
        resPermission.setModule(permission.getModule());
        resPermission.setName(permission.getName());

        return this.permissionRepository.save(resPermission);
    }

    public void handleDeletePermission(long id) {
        Permission permission = this.handleGetPermissionById(id);

        if(permission.getRoles() != null){
            permission.getRoles().forEach(role -> role.getPermissions().remove(permission));
        }

        this.permissionRepository.delete(permission);
    }

    public Permission handleGetPermissionById(long id){
        return this.permissionRepository.findById(id).orElse(null);
    }

    public ResultPaginationResponse handleGetAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = this.permissionRepository.findAll(spec, pageable);

        ResultPaginationResponse.Meta meta = new ResultPaginationResponse.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(page.getTotalElements());
        meta.setPages(page.getTotalPages());

        return new ResultPaginationResponse(meta, page.getContent());
    }

    public boolean handleExistPermission(Permission permission) {
        return this.permissionRepository.existsByApiPathAndModuleAndMethod(
                permission.getApiPath(), permission.getModule(), permission.getMethod()
        );
    }
}
