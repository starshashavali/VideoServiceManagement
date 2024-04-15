package com.tcs.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.domain.Video;


public interface VideoRepository extends JpaRepository<Video, Long> {
	
	List<Video> findByUploadDate(LocalDate uploadDate);

	
    List<Video> findByUploadDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);




}
