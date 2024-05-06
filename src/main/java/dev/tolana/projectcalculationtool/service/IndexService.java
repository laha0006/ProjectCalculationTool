package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.UserEntityRoleDto;
import dev.tolana.projectcalculationtool.repository.IndexRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexService {

    private IndexRepository indexRepository;
    public IndexService(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }
}
