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

        // 1. DB에서 사용자 정보 조회 및 입대일 가져오기
        LocalDate enlistmentDate = getEnlistmentDateFromDb(userId);

        LocalDate today = LocalDate.now();
        int totalServiceMonths = branch.getTotalMonths();

        // 2. 전역일 및 총/남은 복무 일수 계산
        LocalDate dischargeDate = enlistmentDate.plusMonths(totalServiceMonths);
        long totalDays = ChronoUnit.DAYS.between(enlistmentDate, dischargeDate);

        // 오늘 날짜가 입대일보다 미래인 경우에만 계산 (전역일이 오늘보다 과거일 수도 있음)
        long daysServed = ChronoUnit.DAYS.between(enlistmentDate, today);
        long daysToDischarge = ChronoUnit.DAYS.between(today, dischargeDate);
        long totalMonthsServed = ChronoUnit.MONTHS.between(enlistmentDate, today);

        // 3. 현재 계급, 다음 진급일, 다음 계급 이름 결정 (로직 유지)
        String nowRank = "전역";
        String nextRank = "해당 없음";
        LocalDate nextRankDate = dischargeDate;
        LocalDate currentRankStartDate = enlistmentDate;

        List<Map.Entry<String, Integer>> promotionEntries = new ArrayList<>(branch.getPromotionMonths().entrySet());

        if (totalMonthsServed >= totalServiceMonths) {
            // 전역
            daysToDischarge = 0;
            daysServed = totalDays;
        } else if (totalMonthsServed < 0) {
            nowRank = "입대 예정";
            nextRank = promotionEntries.get(0).getKey();
            nextRankDate = enlistmentDate;
            daysServed = 0;
            daysToDischarge = totalDays; // 입대까지 남은 일수
        } else {
            // 순서대로 순회하며 현재 계급 찾기
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
                        // 마지막 계급인 경우 다음은 전역
                        nextRank = "전역";
                        nextRankDate = dischargeDate;
                    }
                    break;
                }
            }
        }

        // --- 4. 퍼센트 계산 및 DTO 설정 로직 추가 ---

        MilitaryDto result = new MilitaryDto();

        // 4-1. 전역까지의 퍼센트
        double dischargeProgress = (totalDays > 0 && daysServed > 0) ? (double) daysServed * 100 / totalDays : 0.0;

        // 4-2. 다음 진급까지의 퍼센트 및 남은 일수
        long daysToNextRank = 0;
        double nextRankProgress = 0.0;

        if (!nextRank.equals("전역") && !nextRank.equals("입대 예정")) {
            long totalDaysInCurrentRank = ChronoUnit.DAYS.between(currentRankStartDate, nextRankDate);
            long daysInCurrentRank = ChronoUnit.DAYS.between(currentRankStartDate, today);
            daysToNextRank = ChronoUnit.DAYS.between(today, nextRankDate);

            if (totalDaysInCurrentRank > 0) {
                nextRankProgress = (double) daysInCurrentRank * 100 / totalDaysInCurrentRank;
                // 100% 초과 방지
                nextRankProgress = Math.min(100.0, nextRankProgress);
            }
        }

        // 5. DTO에 최종 값 설정
        result.setNowRank(nowRank);
        result.setNextRank(nextRank);

        // 소수점 2째 자리까지만 표시
        result.setDischargeProgress(Math.round(dischargeProgress * 100.0) / 100.0);
        result.setNextRankProgress(Math.round(nextRankProgress * 100.0) / 100.0);

        // 남은 일수는 음수가 되지 않도록 조정
        result.setDaysToDischarge(Math.max(0, daysToDischarge));
        result.setDaysToNextRank(Math.max(0, daysToNextRank));

        return result;
    }

    // DB 조회 메서드 (실제 구현 필요)
    private LocalDate getEnlistmentDateFromDb(String userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // UserEntity의 필드명에 따라 getEnlistDate() 또는 getEnlistmentDate()를 사용합니다.
        return user.getEnlistDate();
    }
}
