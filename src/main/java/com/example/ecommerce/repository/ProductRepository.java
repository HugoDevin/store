package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.ProductStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p join fetch p.seller where p.status = :status and (coalesce(:keyword, '') = '' or lower(p.name) like lower(concat('%', :keyword, '%')) or lower(p.description) like lower(concat('%', :keyword, '%')))")
    Page<Product> searchPublicProducts(ProductStatus status, String keyword, Pageable pageable);

    @Query("select p from Product p join fetch p.seller where p.id = :id")
    Optional<Product> findDetailById(Long id);

    @Query("select p from Product p join fetch p.seller where p.seller.id = :sellerId order by p.id desc")
    List<Product> findBySellerIdWithSeller(Long sellerId);
}
