package com.tastytown.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@SuppressWarnings("deprecation")
@Schema(description = "Payload for creating or fully updating a Food entity")
public record FoodRequestDTO(

        @Schema(description = "Name of the food item", example = "Margherita Pizza", required = true)
        @NotBlank(message = "Food name is required")
        @NotEmpty(message = "Food name is required")
        @NotNull(message = "Food name cannot be null")
        String foodName,

        @Schema(description = "Short description of the food (max 100 chars)", example = "Classic pizza with mozzarella and tomato sauce")
        @NotBlank(message = "Food description is required")
        @Size(max = 100, message = "Food description must be less than 100 characters")
        String foodDescription,

        @Schema(description = "Price of the food item", example = "9.99")
        @Digits(integer = 5, fraction = 2)
        @PositiveOrZero
        Double foodPrice,

        @Schema(description = "UUID of the category this food belongs to", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890", required = true)
        @NotBlank(message = "Category ID is required")
        @NotNull(message = "Category Id is required")
        String categoryId

) {}
