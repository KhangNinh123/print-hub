package com.iuh.printshop.printshop_be.controller;

import com.iuh.printshop.printshop_be.dto.user.UserResponse;
import com.iuh.printshop.printshop_be.entity.User;
import com.iuh.printshop.printshop_be.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user by email (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        try {
            UserResponse user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user with specified roles (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody User user, @RequestParam Set<String> roles) {
        try {
            UserResponse response = userService.createUser(user, roles);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @Valid @RequestBody User userUpdate) {
        try {
            UserResponse response = userService.updateUser(id, userUpdate);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== USER PROFILE ENDPOINTS (5 PROFESSIONAL APIs) ====================

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Get current user's profile information")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMyProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            UserResponse user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile", description = "Update current user's profile (fullName, phone, address)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateMyProfile(@Valid @RequestBody Map<String, Object> updates) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            UserResponse user = userService.getUserByEmail(email);
            
            // Update only allowed fields
            User userUpdate = new User();
            if (updates.containsKey("fullName")) {
                userUpdate.setFullName((String) updates.get("fullName"));
            }
            if (updates.containsKey("phone")) {
                userUpdate.setPhone((String) updates.get("phone"));
            }
            if (updates.containsKey("defaultAddress")) {
                userUpdate.setDefaultAddress((String) updates.get("defaultAddress"));
            }
            
            UserResponse updatedUser = userService.updateUser(user.getId(), userUpdate);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/me/roles")
    @Operation(summary = "Get my roles", description = "Get current user's roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyRoles() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            UserResponse user = userService.getUserByEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("roles", user.getRoles());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me/info")
    @Operation(summary = "Get my basic info", description = "Get current user's basic information (without roles)")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyBasicInfo() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            UserResponse user = userService.getUserByEmail(email);
            
            Map<String, Object> basicInfo = new HashMap<>();
            basicInfo.put("id", user.getId());
            basicInfo.put("email", user.getEmail());
            basicInfo.put("fullName", user.getFullName());
            basicInfo.put("phone", user.getPhone() != null ? user.getPhone() : "");
            basicInfo.put("defaultAddress", user.getDefaultAddress() != null ? user.getDefaultAddress() : "");
            basicInfo.put("isActive", user.getIsActive());
            basicInfo.put("createdAt", user.getCreatedAt());
            return ResponseEntity.ok(basicInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    
}
