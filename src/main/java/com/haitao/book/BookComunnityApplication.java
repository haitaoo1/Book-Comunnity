package com.haitao.book;

import com.haitao.book.entities.Role;
import com.haitao.book.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class BookComunnityApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookComunnityApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository){
		return args -> {
			if (roleRepository.findByname("USER").isEmpty()){
				roleRepository.save(
						Role.builder().name("USER").build());
			}
		};
	}

}
