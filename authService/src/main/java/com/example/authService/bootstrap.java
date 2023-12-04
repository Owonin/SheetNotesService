package com.example.authService;


import com.example.authService.entity.ERoles;
import com.example.authService.entity.RoleEntity;
import com.example.authService.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class bootstrap implements ApplicationListener<ContextRefreshedEvent> {

private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        RoleEntity role = new RoleEntity();
        RoleEntity role2 = new RoleEntity();
        role.setName(ERoles.ROLE_USER);
        role2.setName(ERoles.ROLE_ADMIN);
        role.setId(1l);
        role2.setId(2l);
        roleRepository.save(role);
        roleRepository.save(role2);

    }
}
