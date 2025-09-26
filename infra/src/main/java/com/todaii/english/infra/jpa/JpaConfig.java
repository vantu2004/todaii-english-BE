package com.todaii.english.infra.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// mặc định Spring ko quét Entity và Repository
/*
 * nếu đặt trong application thì khi test api, mặc định nó sẽ load luôn cả tầng
 * repo => lỗi vì @WebMvcTest ko cho phép load tầng này => tách file cấu hình
 */
@Configuration
@EntityScan("com.todaii.english.core")
@EnableJpaRepositories("com.todaii.english.core")
public class JpaConfig {

}
