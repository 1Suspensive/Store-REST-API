package com.suspensive.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import com.suspensive.store.models.entities.PermissionEntity;
import com.suspensive.store.models.entities.RoleEntity;
import com.suspensive.store.models.entities.RolesEnum;
import com.suspensive.store.models.entities.UserEntity;
import com.suspensive.store.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;



@SpringBootApplication
public class StoreApplication{

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}
	/*Code to create admin, roles and permissions by default */
	@Bean
	CommandLineRunner init(UserRepository userRepository,PasswordEncoder passwordEncoder){
		return args ->{
			PermissionEntity permission1 = PermissionEntity.builder().name("READ").build();
			PermissionEntity permission2 = PermissionEntity.builder().name("DEFAULT_PURCHASE").build();
			PermissionEntity permission3 = PermissionEntity.builder().name("PREMIUM_PURCHASE").build();
			PermissionEntity permission4 = PermissionEntity.builder().name("SELL").build();
	
			RoleEntity role1 = RoleEntity.builder().rolesEnum(RolesEnum.DEFAULT_USER).permissions(Set.of(permission1,permission2)).build();
			RoleEntity role2 = RoleEntity.builder().rolesEnum(RolesEnum.PREMIUM_USER).permissions(Set.of(permission1,permission2,permission3)).build();
			RoleEntity role3 = RoleEntity.builder().rolesEnum(RolesEnum.ADMIN).permissions(Set.of(permission1,permission2,permission3,permission4)).build();
		
			UserEntity admin = UserEntity.builder()
			.username("Jeferson")
			.password(passwordEncoder.encode("1234"))
			.email("jefersonospinag@gmail.com")
			.phoneNumber("3008317482")
			.wallet(1000000)
			.roles(Set.of(role1,role2,role3))
			.isEnabled(true)
			.accountNoExpired(true)
			.accountNoLocked(true)
			.credentialNoExpired(true).build();

			userRepository.save(admin);
		};
	}

	
}
