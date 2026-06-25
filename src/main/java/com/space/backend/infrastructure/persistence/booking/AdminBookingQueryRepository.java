package com.space.backend.infrastructure.persistence.booking;

import com.space.backend.application.admin.BookingSearchCondition;
import com.space.backend.domain.booking.Booking;
import com.space.backend.domain.booking.BookingStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminBookingQueryRepository {

    private final EntityManager em;

    public List<Booking> findByCondition(BookingSearchCondition cond) {
        StringBuilder jpql = new StringBuilder(
                "SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.space WHERE 1=1");
        List<Object> params = new ArrayList<>();
        int paramIdx = 1;

        if (cond.status() != null) {
            jpql.append(" AND b.status = ?").append(paramIdx++);
            params.add(cond.status());
        }
        if (cond.date() != null) {
            ZoneId zone = ZoneId.of("Asia/Seoul");
            var dayStart = cond.date().atStartOfDay(zone).toInstant();
            var dayEnd   = cond.date().plusDays(1).atStartOfDay(zone).toInstant();
            jpql.append(" AND b.startAt >= ?").append(paramIdx++)
                .append(" AND b.startAt < ?").append(paramIdx++);
            params.add(dayStart);
            params.add(dayEnd);
        }
        jpql.append(" ORDER BY b.createdAt DESC");

        TypedQuery<Booking> query = em.createQuery(jpql.toString(), Booking.class);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        int offset = cond.page() * cond.size();
        query.setFirstResult(offset);
        query.setMaxResults(cond.size());
        return query.getResultList();
    }

    public long countByCondition(BookingSearchCondition cond) {
        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(b) FROM Booking b WHERE 1=1");
        List<Object> params = new ArrayList<>();
        int paramIdx = 1;

        if (cond.status() != null) {
            jpql.append(" AND b.status = ?").append(paramIdx++);
            params.add(cond.status());
        }
        if (cond.date() != null) {
            ZoneId zone = ZoneId.of("Asia/Seoul");
            var dayStart = cond.date().atStartOfDay(zone).toInstant();
            var dayEnd   = cond.date().plusDays(1).atStartOfDay(zone).toInstant();
            jpql.append(" AND b.startAt >= ?").append(paramIdx++)
                .append(" AND b.startAt < ?").append(paramIdx++);
            params.add(dayStart);
            params.add(dayEnd);
        }
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
        return query.getSingleResult();
    }
}
