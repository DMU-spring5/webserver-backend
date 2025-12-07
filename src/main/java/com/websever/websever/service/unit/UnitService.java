package com.websever.websever.service.unit;

import com.websever.websever.dto.request.UnitRequest;
import com.websever.websever.dto.response.UnitResponse;
import com.websever.websever.entity.auth.UserEntity;
import com.websever.websever.entity.unit.UnitEntity;
import com.websever.websever.repository.auth.UserRepository;
import com.websever.websever.repository.unit.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UserRepository userRepository;
    private final UnitRepository unitRepository;

    // 🔥 1. 생성
    @Transactional
    public UnitResponse create(String currentUserId, UnitRequest request) {

        UserEntity userEntity = userRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("작성 사용자 정보를 찾을 수 없습니다. (ID: " + currentUserId + ")"));

        UnitEntity newUnit = new UnitEntity();
        newUnit.setStar(request.getStar());
        newUnit.setTitle(request.getTitle());
        newUnit.setHard(request.getHard());
        newUnit.setWorking(request.getWorking());
        newUnit.setGood(request.getGood());
        newUnit.setBad(request.getBad());
        newUnit.setHope(request.getHope());
        newUnit.setUserId(userEntity);

        UnitEntity savedUnit = unitRepository.save(newUnit);

        return UnitResponse.create(savedUnit);
    }

    // 🔥 2. 전체 목록 조회
    @Transactional(readOnly = true)
    public List<UnitResponse> getAllUnits() {
        return unitRepository.findAll().stream()
                .map(UnitResponse::create)
                .collect(Collectors.toList());
    }

    // 🔥 3. 단일 조회
    @Transactional(readOnly = true)
    public UnitResponse getUnitById(Integer unitId) {
        UnitEntity unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유닛을 찾을 수 없습니다. ID: " + unitId));

        return UnitResponse.create(unit);
    }

    // 🔥 4. 수정
    @Transactional
    public UnitResponse updateUnit(Integer unitId, String currentUserId, UnitRequest request) {

        UnitEntity unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유닛을 찾을 수 없습니다. ID: " + unitId));

        if (!unit.getUserId().getUserId().equals(currentUserId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        unit.setStar(request.getStar());
        unit.setTitle(request.getTitle());
        unit.setHard(request.getHard());
        unit.setWorking(request.getWorking());
        unit.setGood(request.getGood());
        unit.setBad(request.getBad());
        unit.setHope(request.getHope());

        return UnitResponse.create(unit);
    }

    // 🔥 5. 삭제
    @Transactional
    public void deleteUnit(Integer unitId, String currentUserId) {

        UnitEntity unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유닛을 찾을 수 없습니다. ID: " + unitId));

        if (!unit.getUserId().getUserId().equals(currentUserId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        unitRepository.delete(unit);
    }

    @Transactional(readOnly = true)
    public List<UnitResponse> searchByTitle(String keyword) {
        List<UnitEntity> list = unitRepository.findByTitleContainingIgnoreCase(keyword);

        return list.stream()
                .map(UnitResponse::create)
                .collect(Collectors.toList());
    }
}
