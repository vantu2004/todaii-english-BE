package com.todaii.english.server.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.AdminRole;


@Repository
public interface AdminRoleRepository extends JpaRepository<AdminRole, String> {

}
