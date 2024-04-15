package com.tcs.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.tcs.service.StreamingService;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin("*")
public class VideoController {

	@Autowired
	private StreamingService streamingService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("title") String title,
                                         @RequestParam("file") MultipartFile file) {
        try {
            byte[] data = file.getBytes();
            streamingService.saveVideo(title, data);
            return ResponseEntity.ok("Video uploaded successfully");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload video", e);
        }
    }



    //localhost:9001/api/videos/3
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadVideo(@PathVariable Long id) {
        try {
            byte[] videoData = streamingService.getVideoDataById(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("video/mp4"));
            headers.setContentDispositionFormData("attachment", "filename=\"video.mp4\"");

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(videoData.length)
                .body(videoData);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    //localhost:9001/api/videos/dataUploadedToday

    @GetMapping("/dataUploadedToday")
    public ResponseEntity<List<String>> getVideoDataForTodayUploadedVideos() {
        try {
            List<byte[]> videoDataList = streamingService.getVideoDataForVideosUploadedToday();
            List<String> base64EncodedVideos = videoDataList.stream()
                                                            .map(Base64Utils::encodeToString)
                                                            .collect(Collectors.toList());
            return ResponseEntity.ok(base64EncodedVideos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
   //  localhost:9001/api/videos//videosOnDate/2024-04-15
    @GetMapping("/videosOnDate/{date}")
    public ResponseEntity<List<String>> getVideosOnDate(@PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            List<byte[]> videoDataList = streamingService.getVideoDataForDate(localDate);
            List<String> base64EncodedVideos = videoDataList.stream()
                .map(Base64Utils::encodeToString)
                .collect(Collectors.toList());
            return ResponseEntity.ok(base64EncodedVideos);
        } catch (Exception e) {
            e.printStackTrace(); // Properly log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //localhost:9001/api/videos/videosYesterday

    @GetMapping("/videosYesterday")
    public ResponseEntity<List<String>> getVideosYesterday() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            List<byte[]> videoDataList = streamingService.getVideoDataForDate(yesterday);
            List<String> base64EncodedVideos = videoDataList.stream()
                                                            .map(Base64Utils::encodeToString)
                                                            .collect(Collectors.toList());
            return ResponseEntity.ok(base64EncodedVideos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}