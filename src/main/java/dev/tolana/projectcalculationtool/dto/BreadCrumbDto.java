package dev.tolana.projectcalculationtool.dto;

import java.util.List;

public record BreadCrumbDto(boolean show, List<BreadCrumbItmDto> breadCrumbs) {
}
