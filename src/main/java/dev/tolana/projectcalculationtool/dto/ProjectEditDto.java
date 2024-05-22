package dev.tolana.projectcalculationtool.dto;

import dev.tolana.projectcalculationtool.enums.Status;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ProjectEditDto(
long id,
String projectName,
String description,

@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
LocalDateTime deadline,
int allottedHours,
Status status
) {
}
