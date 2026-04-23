package com.laoji.novelai.config;

import com.laoji.novelai.entity.auth.Permission;
import com.laoji.novelai.entity.auth.Role;
import com.laoji.novelai.entity.auth.User;
import com.laoji.novelai.repository.auth.PermissionRepository;
import com.laoji.novelai.repository.auth.RoleRepository;
import com.laoji.novelai.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 初始化权限
        if (permissionRepository.count() == 0) {
            Permission readPermission = new Permission();
            readPermission.setName("read");
            readPermission.setResourceType("novel");
            readPermission.setAction("read");
            readPermission.setDescription("读取小说权限");
            permissionRepository.save(readPermission);

            Permission writePermission = new Permission();
            writePermission.setName("write");
            writePermission.setResourceType("novel");
            writePermission.setAction("write");
            writePermission.setDescription("写入小说权限");
            permissionRepository.save(writePermission);
        }

        // 初始化角色
        Role adminRole;
        if (roleRepository.count() == 0) {
            adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("管理员角色");
            adminRole.setPermissions(Set.copyOf(permissionRepository.findAll()));
            roleRepository.save(adminRole);
        } else {
            adminRole = roleRepository.findByName("ADMIN").orElse(null);
        }

        // 初始化用户：先删除已有的用户，然后创建新用户
        userRepository.findByUsername("admin").ifPresent(userRepository::delete);
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setNickname("管理员");
        adminUser.setEmail("admin@example.com");
        adminUser.setEnabled(true);
        adminUser.setAccountNonExpired(true);
        adminUser.setAccountNonLocked(true);
        adminUser.setCredentialsNonExpired(true);
        if (adminRole != null) {
            adminUser.setRoles(Set.of(adminRole));
        }
        userRepository.save(adminUser);
    }
}
