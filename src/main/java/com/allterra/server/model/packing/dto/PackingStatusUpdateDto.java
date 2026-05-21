package com.allterra.server.model.packing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating the packed status of a gear item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackingStatusUpdateDto {
    private boolean packed;
}
