package com.tastytown.backend.dto;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public record CartResponseDTO(
        List<CartItemResponseDTO> items
) {
}
