package com.space.backend.infrastructure.persistence.booking;

import com.space.backend.application.admin.BookingSearchCondition;
import com.space.backend.domain.booking.Booking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminBookingQueryRepository {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final EntityManager em;

    public List<Booking> findByCondition(BookingSearchCondition cond) {
        WhereClause where = buildWhereClause(cond);
        String jpql = "SELECT b FROM Booking b JOIN FETCH b.user JOIN FETCH b.space"
                + where.clause
                + " ORDER BY b.createdAt DESC";

        TypedQuery<Booking> query = em.createQuery(jpql, Booking.class);
        bindParams(query, where.params);
        query.setFirstResult(cond.page() * cond.size());
        query.setMaxResults(cond.size());
        return query.getResultList();
    }

    public long countByCondition(BookingSearchCondition cond) {
        WhereClause where = buildWhereClause(cond);
        String jpql = "SELECT COUNT(b) FROM Booking b" + where.clause;

        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        bindParams(query, where.params);
        return query.getSingleResult();
    }

    private WhereClause buildWhereClause(BookingSearchCondition cond) {
        StringBuilder clause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        int paramIdx = 1;

        if (cond.status() != null) {
            clause.append(clause.isEmpty() ? " WHERE" : " AND")
                  .append(" b.status = ?").append(paramIdx++);
            params.add(cond.status());
        }
        if (cond.date() != null) {
            Instant dayStart = cond.date().atStartOfDay(SEOUL).toInstant();
            Instant dayEnd   = cond.date().plusDays(1).atStartOfDay(SEOUL).toInstant();
            clause.append(clause.isEmpty() ? " WHERE" : " AND")
                  .append(" b.startAt >= ?").append(paramIdx++)
                  .append(" AND b.startAt < ?").append(paramIdx++);
            params.add(dayStart);
            params.add(dayEnd);
        }
        return new WhereClause(clause.toString(), params);
    }

    private void bindParams(TypedQuery<?> query, List<Object> params) {
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }
    }

    private record WhereClause(String clause, List<Object> params) {}
}
