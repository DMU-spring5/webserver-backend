package com.websever.websever.service.mainpage;

import com.websever.websever.dto.MilitaryDto;
import com.websever.websever.entity.auth.ServiceType;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.repository.auth.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MilitaryService {
    private final UserRepository userRepository;

    public MilitaryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MilitaryDto calculateProgress(String userId, ServiceType branch) {

        LocalDate enlistmentDate = getEnlistmentDateFromDb(userId);
        LocalDate today = LocalDate.now();
        
        int totalServiceMonths = branch.getTotalMonths();
        LocalDate dischargeDate = enlistmentDate.plusMonths(totalServiceMonths);
        long totalDays = ChronoUnit.DAYS.between(enlistmentDate, dischargeDate);
        
        long daysServed = ChronoUnit.DAYS.between(enlistmentDate, today);
        long daysToDischarge = ChronoUnit.DAYS.between(today, dischargeDate);
        long totalMonthsServed = ChronoUnit.MONTHS.between(enlistmentDate, today);

        String nowRank = "전역";
        String nextRank = "해당 없음";
        LocalDate nextRankDate = dischargeDate;
        LocalDate currentRankStartDate = enlistmentDate;

        List<Map.Entry<String, Integer>> promotionEntries = new ArrayList<>(branch.getPromotionMonths().entrySet());

        if (totalMonthsServed >= totalServiceMonths) {
            daysToDischarge = 0;
            daysServed = totalDays;
        } else if (totalMonthsServed < 0) {
            nowRank = "입대 예정";
            nextRank = promotionEntries.get(0).getKey();
            nextRankDate = enlistmentDate;
            daysServed = 0;
            daysToDischarge = totalDays;
        } else {
            for (int i = promotionEntries.size() - 1; i >= 0; i--) {
                Map.Entry<String, Integer> entry = promotionEntries.get(i);
                int startMonth = entry.getValue();

                if (totalMonthsServed >= startMonth) {
                    nowRank = entry.getKey();
                    currentRankStartDate = enlistmentDate.plusMonths(startMonth);

                    if (i < promotionEntries.size() - 1) {
                        Map.Entry<String, Integer> nextEntry = promotionEntries.get(i + 1);
                        nextRank = nextEntry.getKey();
                        nextRankDate = enlistmentDate.plusMonths(nextEntry.getValue());
                    } else {
                        nextRank = "전역";
                        nextRankDate = dischargeDate;
                    }
                    break;
                }
            }
        }

        MilitaryDto result = new MilitaryDto();
        
        double dischargeProgress = (totalDays > 0 && daysServed > 0) ? (double) daysServed * 100 / totalDays : 0.0;

        long daysToNextRank = 0;
        double nextRankProgress = 0.0;

        if (!nextRank.equals("전역") && !nextRank.equals("입대 예정")) {
            long totalDaysInCurrentRank = ChronoUnit.DAYS.between(currentRankStartDate, nextRankDate);
            long daysInCurrentRank = ChronoUnit.DAYS.between(currentRankStartDate, today);
            daysToNextRank = ChronoUnit.DAYS.between(today, nextRankDate);

            if (totalDaysInCurrentRank > 0) {
                nextRankProgress = (double) daysInCurrentRank * 100 / totalDaysInCurrentRank;
                nextRankProgress = Math.min(100.0, nextRankProgress);
            }
        }

        result.setNowRank(nowRank);
        result.setNextRank(nextRank);

        result.setDischargeProgress(Math.round(dischargeProgress * 100.0) / 100.0);
        result.setNextRankProgress(Math.round(nextRankProgress * 100.0) / 100.0);

        result.setDaysToDischarge(Math.max(0, daysToDischarge));
        result.setDaysToNextRank(Math.max(0, daysToNextRank));

        return result;
    }

    private LocalDate getEnlistmentDateFromDb(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return user.getEnlistDate();
    }
}
