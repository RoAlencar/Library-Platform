package br.com.fiap.shared.application.dto;

import br.com.fiap.shared.common.DateUtils;
import br.com.fiap.shared.domain.LibraryTime;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public record ReportResponse<T>(String title, OffsetDateTime generatedAt, String timeZone, int total, List<T> items) {

    public static <T> ReportResponse<T> of(String title, LocalDateTime generatedAt, List<T> items) {
        return new ReportResponse<>(title, DateUtils.toOffsetDateTime(generatedAt), LibraryTime.ZONE_ID.getId(),
                items.size(), List.copyOf(items));
    }
}
