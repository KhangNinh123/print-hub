package com.iuh.printshop.printshop_be.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CategoryRequest {
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 80, message = "Tên phải từ 2 đến 80 ký tự")
    private String name;

    private String description;
}
