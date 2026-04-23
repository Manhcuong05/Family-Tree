package com.giapha.api.controller;

import com.giapha.api.entity.Article;
import com.giapha.api.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ChroniclesController {

    private final ArticleRepository articleRepository;

    @GetMapping("/{branchId}")
    public ResponseEntity<List<Article>> getArticles(@PathVariable UUID branchId, @RequestParam(defaultValue = "PHA_KY") String category) {
        return ResponseEntity.ok(articleRepository.findByBranchIdAndCategory(branchId, category));
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(articleRepository.save(article));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id) {
        articleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
