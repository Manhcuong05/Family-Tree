package com.giapha.api.scheduler;

import com.giapha.api.entity.Member;
import com.giapha.api.repository.MemberRepository;
import com.giapha.api.util.LunisolarConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemorialScheduler {

    private final MemberRepository memberRepository;

    // Run every day at 00:00
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkUpcomingMemorials() {
        log.info("Starting Daily Memorial Check Job...");
        
        LocalDate today = LocalDate.now();
        LunisolarConverter.LunarDate todayLunar = LunisolarConverter.convertSolarToLunar(today);
        
        // Target is 3 days from now
        LocalDate targetSolar = today.plusDays(3);
        LunisolarConverter.LunarDate targetLunar = LunisolarConverter.convertSolarToLunar(targetSolar);

        // Fetch all deceased members
        // In a real optimized system, you'd want to query specifically for the lunar month/day
        // But since Postgres doesn't natively know Lunar dates, we usually fetch all deceased 
        // or store the lunar death date directly in the DB.
        
        // For demonstration, assuming we fetch deceased members and check:
        // List<Member> deceasedMembers = memberRepository.findByIsAliveFalse();
        // for (Member m : deceasedMembers) {
        //     if (m.getNgayMat() != null) {
        //         LunisolarConverter.LunarDate deathLunar = LunisolarConverter.convertSolarToLunar(m.getNgayMat());
        //         if (deathLunar.day == targetLunar.day && deathLunar.month == targetLunar.month) {
        //             log.info("Upcoming Memorial for: {} on lunar date: {}/{}", m.getHoTen(), deathLunar.day, deathLunar.month);
        //             // TODO: Send Email or Notification
        //         }
        //     }
        // }
        
        log.info("Finished Daily Memorial Check Job.");
    }
}
