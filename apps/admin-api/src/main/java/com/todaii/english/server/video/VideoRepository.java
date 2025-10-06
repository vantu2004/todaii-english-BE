package com.todaii.english.server.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long>{

}
