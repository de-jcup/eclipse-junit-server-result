/*
 * Copyright 2020 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.junit.serverresult;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.junit.serverresult.JunitModel.JUnitModelStatistics;
import de.jcup.junit.serverresult.JunitServerResultInfoAreaCalculator.JunitServerResultInfoAreaResult;

public class JunitServerResultInfoAreaCalculatorTest {

    private JunitServerResultInfoAreaCalculator calculatorToTest;

    @Before
    public void before() {
        calculatorToTest = new JunitServerResultInfoAreaCalculator();
    }

    @Test
    public void even_with_all_to_0_no_div_by_zero() {
        calculatorToTest.calculate(new Area(), new JUnitModelStatistics());
    }
    
    /**
     * RED, BLUE, WHITE , GREEN
     */
    @Test
    public void given_area_100_with_and_5_errors_5_failurs_10_skipped_success_80__all_100_testcases() {
        /* prepare */
        Area clientArea = new Area();
        clientArea.x = 0;
        clientArea.y = 0;
        clientArea.width = 100;
        clientArea.height = 20;

        JUnitModelStatistics statistics = new JUnitModelStatistics();
        statistics.testcases = 100;

        statistics.success = 80;
        statistics.failures = 5;
        statistics.errors = 5;
        statistics.skipped = 10;

        /* execute */
        JunitServerResultInfoAreaResult result = calculatorToTest.calculate(clientArea, statistics);

        assertEquals(0, result.red_error.x);
        assertEquals(0, result.red_error.y);
        assertEquals(5, result.red_error.width);
        assertEquals(20, result.red_error.height);

        assertEquals(5, result.blue_failed.x);
        assertEquals(0, result.blue_failed.y);
        assertEquals(5, result.blue_failed.width);
        assertEquals(20, result.blue_failed.height);

        assertEquals(10, result.white_ignored.x);
        assertEquals(0, result.white_ignored.y);
        assertEquals(10, result.white_ignored.width);
        assertEquals(20, result.white_ignored.height);

        assertEquals(20, result.green_ok.x);
        assertEquals(0, result.green_ok.y);
        assertEquals(80, result.green_ok.width);
        assertEquals(20, result.green_ok.height);

    }
    
    @Test
    public void given_area_200_with_and_5_errors_5_failurs_10_skipped_success_80__all_100_testcases() {
        /* prepare */
        Area clientArea = new Area();
        clientArea.x = 100;
        clientArea.y = 100;
        clientArea.width = 300;
        clientArea.height = 20;

        JUnitModelStatistics statistics = new JUnitModelStatistics();
        statistics.testcases = 100;

        statistics.success = 80;
        statistics.failures = 5;
        statistics.errors = 5;
        statistics.skipped = 10;

        /* execute */
        JunitServerResultInfoAreaResult result = calculatorToTest.calculate(clientArea, statistics);

        assertEquals(100+0, result.red_error.x);
        assertEquals(100, result.red_error.y);
        assertEquals(10, result.red_error.width);
        assertEquals(20, result.red_error.height);

        assertEquals(100+10, result.blue_failed.x);
        assertEquals(100, result.blue_failed.y);
        assertEquals(10, result.blue_failed.width);
        assertEquals(20, result.blue_failed.height);

        assertEquals(100+20, result.white_ignored.x);
        assertEquals(100, result.white_ignored.y);
        assertEquals(20, result.white_ignored.width);
        assertEquals(20, result.white_ignored.height);

        assertEquals(100+40, result.green_ok.x);
        assertEquals(100, result.green_ok.y);
        assertEquals(160, result.green_ok.width);
        assertEquals(20, result.green_ok.height);

    }
    
    @Test
    public void real_world_example_1034_testcases_948_success_86_ignored() {
        /* prepare */
        Area clientArea = new Area();
        clientArea.x = 100;
        clientArea.y = 100;
        clientArea.width = 300;
        clientArea.height = 20;

        JUnitModelStatistics statistics = new JUnitModelStatistics();
        statistics.testcases = 1034;

        statistics.success = 948;
        statistics.failures = 0;
        statistics.errors = 0;
        statistics.skipped = 86;

        /* execute */
        JunitServerResultInfoAreaResult result = calculatorToTest.calculate(clientArea, statistics);

        assertEquals(100+0, result.red_error.x);
        assertEquals(100, result.red_error.y);
        assertEquals(0, result.red_error.width);
        assertEquals(20, result.red_error.height);

        assertEquals(100+0, result.blue_failed.x);
        assertEquals(100, result.blue_failed.y);
        assertEquals(0, result.blue_failed.width);
        assertEquals(20, result.blue_failed.height);

        assertEquals(100+0, result.white_ignored.x);
        assertEquals(100, result.white_ignored.y);
        assertEquals(16, result.white_ignored.width);
        assertEquals(20, result.white_ignored.height);

        assertEquals(100+16, result.green_ok.x);
        assertEquals(100, result.green_ok.y);
        assertEquals(184, result.green_ok.width);
        assertEquals(20, result.green_ok.height);

    }
    
    @Test
    public void real_world_example_96_testcases_10_success_86_ignored() {
        /* prepare */
        Area clientArea = new Area();
        clientArea.x = 100;
        clientArea.y = 100;
        clientArea.width = 300;
        clientArea.height = 20;

        JUnitModelStatistics statistics = new JUnitModelStatistics();
        statistics.testcases = 96;

        statistics.success = 10;
        statistics.failures = 0;
        statistics.errors = 0;
        statistics.skipped = 86;

        /* execute */
        JunitServerResultInfoAreaResult result = calculatorToTest.calculate(clientArea, statistics);

        assertEquals(100+0, result.red_error.x);
        assertEquals(100, result.red_error.y);
        assertEquals(0, result.red_error.width);
        assertEquals(20, result.red_error.height);

        assertEquals(100+0, result.blue_failed.x);
        assertEquals(100, result.blue_failed.y);
        assertEquals(0, result.blue_failed.width);
        assertEquals(20, result.blue_failed.height);

        assertEquals(100+0, result.white_ignored.x);
        assertEquals(100, result.white_ignored.y);
        assertEquals(172, result.white_ignored.width);
        assertEquals(20, result.white_ignored.height);

        assertEquals(100+172, result.green_ok.x);
        assertEquals(100, result.green_ok.y);
        assertEquals(28, result.green_ok.width);
        assertEquals(20, result.green_ok.height);

    }
}
