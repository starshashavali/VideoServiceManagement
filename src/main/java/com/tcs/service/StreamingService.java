package com.tcs.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.domain.Video;
import com.tcs.repository.VideoRepository;

@Service
public class StreamingService {

    @Autowired
    private VideoRepository videoRepository;

    // Save video
    public void saveVideo(String title, byte[] data) {
        Video video = new Video();
        video.setTitle(title);
        video.setVideoData(data);
        video.setUploadDate(LocalDateTime.now());
        videoRepository.save(video);
    }

    // Fetch video by ID
    public byte[] getVideoDataById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Video not found with id: " + id));

        if (video.getVideoData() != null) {
            return video.getVideoData();
        } else {
            throw new IllegalArgumentException("Video data is null for video id: " + id);
        }
    }

    // Fetch today uploaded videos
    public List<byte[]> getVideoDataForVideosUploadedToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();
        List<Video> videos = videoRepository.findByUploadDateBetween(startOfDay, endOfDay);
        return videos.stream().map(Video::getVideoData).collect(Collectors.toList());
    }

    // Fetch videos uploaded on a specific date
    public List<byte[]> getVideoDataForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        List<Video> videos = videoRepository.findByUploadDateBetween(startOfDay, endOfDay);
        return videos.stream().map(Video::getVideoData).collect(Collectors.toList());
    }

    // Fetch videos uploaded yesterday
    public List<byte[]> getVideoDataForYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return getVideoDataForDate(yesterday);
    }
}
