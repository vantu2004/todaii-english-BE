package com.todaii.english.core.admin.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRoleRepository extends JpaRepository<AdminRole, String> {

}
