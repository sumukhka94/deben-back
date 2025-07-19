package com.sumukh.debenback.repositories;

import com.sumukh.debenback.dtos.SettlementDto;
import com.sumukh.debenback.entities.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByGroupIdOrderBySettledAtDesc(Long groupId);
}
