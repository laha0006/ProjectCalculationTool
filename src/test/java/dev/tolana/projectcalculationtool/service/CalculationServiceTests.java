package dev.tolana.projectcalculationtool.service;

import dev.tolana.projectcalculationtool.dto.ProjectStatsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CalculationServiceTests {

    @Autowired
    private CalculationService calculationService;

    @Test
    public void testCalculate() {
        ProjectStatsDto stats = calculationService.getProjectStats(1);
        System.out.println(stats);
    }
}
