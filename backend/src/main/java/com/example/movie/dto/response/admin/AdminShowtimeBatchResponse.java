package com.example.movie.dto.response.admin;

import lombok.Builder;
import java.util.List;

@Builder
public record AdminShowtimeBatchResponse(
        List<Long> createdIds,
        List<String> skippedTimes
) {}