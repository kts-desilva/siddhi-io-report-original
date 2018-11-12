/*
 *  Copyright (C) 2018 WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wso2.extension.siddhi.io.report.generators;

import java.util.HashMap;
import java.util.Map;

/**
 * Dummy data for the tests.
 */
public class DummyData {
    public static final String DUMMY_PAYLOAD = "[{\"event\":{\"symbol\":\"WSO2\",\"price\":55.6,\"volume\":100}}," +
            "{\"event\":{\"symbol\":\"IBM\",\"price\":57.678,\"volume\":100}},{\"event\":{\"symbol\":\"GOOGLE\"," +
            "\"price\":50.0,\"volume\":100}},{\"event\":{\"symbol\":\"WSO2\",\"price\":55.6,\"volume\":100}}]";

    public static final String STRING_DUMMY_DATA = "[{\"event\":{\"symbol\":\"WSO2\",\"price\":\"55.6f\"," +
            "\"volume\":\"100L\"}},{\"event\":{\"symbol\":\"IBM\",\"price\":\"57.678f\",\"volume\":\"100L\"}}," +
            "{\"event\":{\"symbol\":\"GOOGLE\",\"price\":\"50f\",\"volume\":\"100L\"}}," +
            "{\"event\":{\"symbol\":\"WSO2\",\"price\":\"55.6f\",\"volume\":\"100L\"}}]";

    public static Map<String, String> getInvalidTemplateReportProperties() {
        Map<String, String> reportProperties = new HashMap<>();
        String template = DummyData.class.getClassLoader().getResource("invalidTemplate.jrxml").getFile();
        reportProperties.put("template", template);
        reportProperties.put("outputpath", "testOutReport");
        return reportProperties;
    }

    public static Map<String, String> getIncorrectTemplateReportProperties() {
        Map<String, String> reportProperties = new HashMap<>();
        String template = DummyData.class.getClassLoader().getResource("incorrectTemplate.jrxml").getFile();
        reportProperties.put("template", template);
        reportProperties.put("outputpath", "testOutReport");
        return reportProperties;
    }

    public static Map<String, String> getTemplateWithoutDatasetsReportProperties() {
        Map<String, String> reportProperties = new HashMap<>();
        String template = DummyData.class.getClassLoader().getResource("invalidTemplateWitoutDatasets.jrxml").getFile();
        reportProperties.put("template", template);
        reportProperties.put("outputpath", "testOutReport");
        return reportProperties;
    }

    public static Map<String, String> getWithoutParametersReportProperties() {
        Map<String, String> reportProperties = new HashMap<>();
        String template = DummyData.class.getClassLoader().getResource("incorrectFromResultsetData.jrxml").getFile();
        reportProperties.put("template", template);
        reportProperties.put("outputpath", "testOutReport");
        return reportProperties;
    }

    public static Map<String, String> getDyanmicReportParameters() {
        Map<String, String> reportProperties = new HashMap<>();
        String template = DummyData.class.getClassLoader().getResource("dynamicTemplate.jrxml").getFile();
        reportProperties.put("template", template);
        reportProperties.put("outputpath", "testOutDynamicReport");
        reportProperties.put("chart", "line");
        return reportProperties;
    }
}
