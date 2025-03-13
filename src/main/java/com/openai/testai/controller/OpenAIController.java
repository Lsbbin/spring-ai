package com.openai.testai.controller;

import com.openai.testai.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping(value = "/api")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    /**
     * 수도 (템플릿 O)
     * @param param
     * @return
     */
    @GetMapping(value = "/capital")
    public ResponseEntity<String> getCapital (@RequestParam(value = "param") String param) {
        return ResponseEntity.ok(openAIService.getCapital(param));
    }

    /**
     * 축구 클럽 (템플릿 O)
     * @param param
     * @return
     */
    @GetMapping(value = "/fc")
    public ResponseEntity<String> getFootballClub (@RequestParam(value = "param") String param) {
        return ResponseEntity.ok(openAIService.getFootballClub(param));
    }

    /**
     * 챗봇 (템플릿 X)
     * @param param
     * @return
     */
    @GetMapping(value = "/chat")
    public ResponseEntity<String> getChatResponse (@RequestParam(value = "param") String param) {
        return ResponseEntity.ok(openAIService.getChatResponse(param));
    }

    /**
     * AI 이미지 생성
     * @param param
     * @return
     */
    @GetMapping(value = "/image")
    public ResponseEntity<String> getImageUrl (@RequestParam(value = "param") String param) {
        return ResponseEntity.ok(openAIService.getImageUrl(param));
    }

    /**
     * AI 이미지 분석
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/analysis")
    public ResponseEntity<String> imageAnalysis (@RequestParam(value = "file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(openAIService.imageAnalysis(file));
    }

    /**
     * Chroma vector store 임베딩 데이터 추가
     */
    @PostMapping(value = "/document")
    public void createDocument (@RequestParam(value = "file") MultipartFile file) throws IOException {
        openAIService.createDocument(file);
    }

    /**
     * Chroma vector store 문서 탐색 결과
     * @param param
     * @return
     */
    @GetMapping("/document")
    public ResponseEntity<String> searchDocument (@RequestParam(value = "param") String param, @RequestParam(value = "id") String id) {
        return ResponseEntity.ok(openAIService.searchDocument(param, id));
    }

    /**
     * Chroma vector store Collection 삭제 후 생성
     */
    @DeleteMapping(value = "/collection")
    public void deleteCollection () {
        openAIService.deleteCollection();
    }


}
