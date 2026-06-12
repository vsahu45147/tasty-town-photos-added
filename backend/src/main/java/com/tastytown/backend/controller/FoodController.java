package com.tastytown.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastytown.backend.dto.FoodRequestDTO;
import com.tastytown.backend.dto.FoodResponseDTO;
import com.tastytown.backend.service.IFoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
@Tag(name = "Food API", description = "A controller manages the CRUD operations for Food entities.")
public class FoodController {

    private final ObjectMapper objectMapper;
    private final IFoodService foodService;

    // -----------------------------------------------------------------------
    // CREATE FOOD  (with optional image — single multipart request)
    // -----------------------------------------------------------------------
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Creates a new Food entity",
        description = "Creates a new food item. Send `foodData` as a JSON string part and an optional `foodImage` file part.",
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(implementation = FoodController.CreateFoodForm.class)
            )
        )
    )
    @ApiResponse(description = "Food created successfully",  responseCode = "201")
    @ApiResponse(description = "Food validation failed",     responseCode = "422")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FoodResponseDTO> createFood(
            @Parameter(
                description = "Food details as a JSON string. Example: {\"foodName\":\"Pizza\",\"foodDescription\":\"Cheesy pizza\",\"foodPrice\":9.99,\"categoryId\":\"cat-uuid\"}",
                required = true,
                schema = @Schema(type = "string",
                    example = "{\"foodName\":\"Pizza\",\"foodDescription\":\"Cheesy pizza\",\"foodPrice\":9.99,\"categoryId\":\"cat-uuid\"}")
            )
            @RequestPart("foodData") String foodData,

            @Parameter(description = "Optional food image file (jpg, jpeg, png)")
            @RequestPart(value = "foodImage", required = false) MultipartFile foodImage
    ) throws IOException {

        FoodRequestDTO dto = objectMapper.readValue(foodData, FoodRequestDTO.class);
        FoodResponseDTO responseDTO = foodService.createFood(dto);

        if (foodImage != null && !foodImage.isEmpty()) {
            foodService.createFoodImage(responseDTO.foodId(), foodImage);
            responseDTO = foodService.getSingleFoodById(responseDTO.foodId());
        }

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /** Schema helper used only by Swagger UI to render the multipart form. */
    @Schema(name = "CreateFoodForm", description = "Multipart form for creating a food item")
    public static class CreateFoodForm {
        @SuppressWarnings("deprecation")
        @Schema(
            description = "Food details as a JSON string",
            example = "{\"foodName\":\"Pizza\",\"foodDescription\":\"Cheesy pizza\",\"foodPrice\":9.99,\"categoryId\":\"cat-uuid\"}",
            required = true
        )
        public String foodData;

        @Schema(description = "Optional food image file", type = "string", format = "binary")
        public MultipartFile foodImage;
    }

    // -----------------------------------------------------------------------
    // UPLOAD IMAGE  (kept for backward-compat)
    // -----------------------------------------------------------------------
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Uploads an image for an existing Food entity",
        description = "Uploads a new image for the food item identified by foodId."
    )
    @ApiResponse(description = "Food image uploaded successfully", responseCode = "204")
    @ApiResponse(description = "Food not found",                   responseCode = "404")
    @PostMapping(value = "/image/{foodId}/food", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createFoodImage(
            @PathVariable String foodId,
            @RequestPart MultipartFile foodImage
    ) throws IOException {
        foodService.createFoodImage(foodId, foodImage);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // -----------------------------------------------------------------------
    // GET  — paginated
    // -----------------------------------------------------------------------
    @Operation(
        summary = "Retrieves all foods with pagination, filtering, and searching",
        description = "Returns a page of Food entities based on optional category, search term, page number, and page size."
    )
    @ApiResponse(description = "Successfully retrieved paginated foods", responseCode = "200")
    @GetMapping("/paginated-foods")
    public ResponseEntity<Page<FoodResponseDTO>> getPaginatedFoods(
            @RequestParam(required = false, defaultValue = "all", name = "catId") String categoryId,
            @RequestParam(required = false)                                         String search,
            @RequestParam(required = false, defaultValue = "0",  name = "page")    int pageNumber,
            @RequestParam(required = false, defaultValue = "12", name = "size")    int pageSize
    ) {
        return ResponseEntity.ok(foodService.getPaginatedFoods(categoryId, search, pageNumber, pageSize));
    }

    // -----------------------------------------------------------------------
    // UPDATE  — full (PUT)
    // -----------------------------------------------------------------------
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Performs a full update of a Food entity",
        description = "Replaces the entire Food entity with the new data provided in the request body."
    )
    @ApiResponse(description = "Food updated successfully", responseCode = "200")
    @ApiResponse(description = "Food not found",            responseCode = "404")
    @PutMapping("/{foodId}")
    public ResponseEntity<FoodResponseDTO> updateFoodFull(
            @PathVariable String foodId,
            @org.springframework.web.bind.annotation.RequestBody FoodRequestDTO dto
    ) {
        return ResponseEntity.ok(foodService.updateFoodFull(foodId, dto));
    }

    // -----------------------------------------------------------------------
    // UPDATE  — partial (PATCH)
    // -----------------------------------------------------------------------
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Performs a partial update of a Food entity",
        description = "Updates only the fields provided in the request body for the specified foodId."
    )
    @ApiResponse(description = "Food partially updated successfully", responseCode = "200")
    @ApiResponse(description = "Food not found",                      responseCode = "404")
    @PatchMapping("/{foodId}")
    public ResponseEntity<FoodResponseDTO> updateFoodPartial(
            @PathVariable String foodId,
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> updates
    ) throws IOException {
        return ResponseEntity.ok(foodService.updateFoodPartial(foodId, updates));
    }

    // -----------------------------------------------------------------------
    // GET  — all
    // -----------------------------------------------------------------------
    @Operation(summary = "Retrieves all available foods", description = "Returns a list of all Food entities.")
    @ApiResponse(description = "Successfully retrieved all foods", responseCode = "200")
    @GetMapping
    public ResponseEntity<List<FoodResponseDTO>> getAllFoods() {
        return ResponseEntity.ok(foodService.getAllFoods());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Deletes a food by ID", description = "Removes the specified food item from the database.")
    @ApiResponse(description = "Food deleted successfully", responseCode = "204")
    @ApiResponse(description = "Food not found", responseCode = "404")
    @DeleteMapping("/{foodId}")
    public ResponseEntity<Void> deleteFood(@PathVariable String foodId) {
        foodService.deleteFood(foodId);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------------------------
    // GET  — single by id
    // -----------------------------------------------------------------------
    @Operation(summary = "Retrieves a single food by ID", description = "Returns a specific Food entity identified by its ID.")
    @ApiResponse(description = "Food found successfully", responseCode = "200")
    @ApiResponse(description = "Food not found",          responseCode = "404")
    @GetMapping("/{foodId}")
    public ResponseEntity<FoodResponseDTO> getSingleFoodById(@PathVariable String foodId) {
        return ResponseEntity.ok(foodService.getSingleFoodById(foodId));
    }

    // -----------------------------------------------------------------------
    // GET  — image
    // -----------------------------------------------------------------------
    @Operation(summary = "Retrieves a food image by its name", description = "Returns the image file as a byte array for the given image name.")
    @ApiResponse(description = "Image retrieved successfully", responseCode = "200")
    @ApiResponse(description = "Image not found",              responseCode = "404")
    @GetMapping("/{imageName}/image")
    public ResponseEntity<byte[]> getFoodImageByName(@PathVariable String imageName) throws IOException {
        byte[] image = foodService.getFoodImageByImageName(imageName);

        String contentType;
        if (imageName.contains(".jpg") || imageName.contains(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        } else if (imageName.contains(".png")) {
            contentType = MediaType.IMAGE_PNG_VALUE;
        } else {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }
}
