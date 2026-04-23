package com.giapha.api.service;

import com.giapha.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final MemberRepository memberRepository;

    public Map<String, Object> getLineageStats(UUID branchId) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalMembers = memberRepository.count(); // Should filter by branchId in future
        long males = memberRepository.findAll().stream().filter(m -> "NAM".equals(m.getGioiTinh().name())).count();
        long females = totalMembers - males;
        
        int maxGeneration = memberRepository.findAll().stream()
                .mapToInt(m -> m.getSoDoi() != null ? m.getSoDoi() : 0)
                .max()
                .orElse(0);

        stats.put("totalMembers", totalMembers);
        stats.put("males", males);
        stats.put("females", females);
        stats.put("maxGeneration", maxGeneration);
        
        return stats;
    }
}
