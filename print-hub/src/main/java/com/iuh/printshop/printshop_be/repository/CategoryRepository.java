package com.iuh.printshop.printshop_be.repository;

import com.iuh.printshop.printshop_be.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // tìm Category theo tên
    Optional<Category> findByName(String name);

    // kiểm tra xem có Category nào trùng tên không
    boolean existsByNameAndIdNot(String name, Integer id);
}
