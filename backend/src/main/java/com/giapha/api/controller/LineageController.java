package com.giapha.api.controller;

import com.giapha.api.entity.LineageInfo;
import com.giapha.api.repository.LineageInfoRepository;
import com.giapha.api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lineage")
@RequiredArgsConstructor
public class LineageController {

    private final LineageInfoRepository lineageInfoRepository;
    private final StatisticsService statisticsService;

    @GetMapping("/{branchId}/info")
    public ResponseEntity<LineageInfo> getInfo(@PathVariable UUID branchId) {
        return ResponseEntity.ok(lineageInfoRepository.findByBranchId(branchId)
                .orElse(new LineageInfo()));
    }

    @PostMapping("/info")
    public ResponseEntity<LineageInfo> updateInfo(@RequestBody LineageInfo info) {
        return ResponseEntity.ok(lineageInfoRepository.save(info));
    }

    @GetMapping("/{branchId}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable UUID branchId) {
        return ResponseEntity.ok(statisticsService.getLineageStats(branchId));
    }
}
