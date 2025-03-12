package com.sprint.mission.discodeit.service;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HibernateStatisticsService {
    private final EntityManagerFactory entityManagerFactory;

    public void printStatistics() {
        Statistics statistics = entityManagerFactory.unwrap(org.hibernate.SessionFactory.class).getStatistics();

        System.out.println("========== Hibernate Statistics ==========");
        System.out.println("Executed Query Count: " + statistics.getQueryExecutionCount()); // 실행된 쿼리 개수
        System.out.println("Fetched Collections Count: " + statistics.getCollectionFetchCount()); // 컬렉션 페치 횟수
        System.out.println("Fetched Entities Count: " + statistics.getEntityFetchCount()); // 엔티티 페치 횟수
        System.out.println("Optimized Queries: " + statistics.getOptimisticFailureCount()); // 낙관적 잠금 실패 횟수
        System.out.println("========== End ==========");
    }
}
