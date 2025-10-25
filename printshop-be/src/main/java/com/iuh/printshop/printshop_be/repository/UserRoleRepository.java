package com.iuh.printshop.printshop_be.repository;

import com.iuh.printshop.printshop_be.entity.UserRole;
import com.iuh.printshop.printshop_be.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
