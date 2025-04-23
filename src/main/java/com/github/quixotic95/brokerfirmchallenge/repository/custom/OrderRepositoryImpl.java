package com.github.quixotic95.brokerfirmchallenge.repository.custom;

import com.github.quixotic95.brokerfirmchallenge.enums.OrderStatus;
import com.github.quixotic95.brokerfirmchallenge.model.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Order> findAllByDynamicFilters(Long customerId, String assetName, OrderStatus status, Instant startDate, Instant endDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();

        if (customerId != null) {
            predicates.add(cb.equal(root.get("customerId"), customerId));
        }
        if (assetName != null && !assetName.isBlank()) {
            predicates.add(cb.equal(root.get("assetName"), assetName));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createDate"), endDate));
        }

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query)
                .getResultList();
    }
}