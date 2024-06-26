package dev.tolana.projectcalculationtool.dto;

public record UserEntityRoleDto(String username,
                                long roleId,
                                long taskId,
                                long projectId,
                                long teamId,
                                long departmentId,
                                long organizationId)
implements Comparable<UserEntityRoleDto>
{
    @Override
    public int compareTo(UserEntityRoleDto userEntityRoleDto){
        return CharSequence.compare(this.username,userEntityRoleDto.username);
    }

    @Override
    public boolean equals(Object obj) {
        UserEntityRoleDto userEntityRoleDto = (UserEntityRoleDto) obj;
        return username.equals(userEntityRoleDto.username);
    }
}


