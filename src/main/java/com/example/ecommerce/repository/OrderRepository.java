package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"items", "buyer", "items.product", "items.product.seller"})
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findDetailedById(Long id);

    @Query("select o from Order o join fetch o.buyer where o.buyer.id = :buyerId")
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);

    // Demo limitation: seller view lists orders containing at least one of seller's products.
    @Query("select distinct o from Order o join o.items i join i.product p join fetch o.buyer where p.seller.id = :sellerId")
    Page<Order> findSellerOrders(Long sellerId, Pageable pageable);
}
