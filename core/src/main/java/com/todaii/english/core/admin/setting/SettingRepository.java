package com.todaii.english.core.admin.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

}
