package dev.tolana.projectcalculationtool.repository;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;

import java.util.List;

public interface IndexRepository {

    List<UserEntityRoleDto> getUserEntityRoleListOnUsername(String username);
}
