
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

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.util.DynamicDataProvider;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides implementation for the report generation for an external JRXML template file.
 */
public class StaticReportGenerator extends ReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(StaticReportGenerator.class);

    @Override
    public void generateReport(Object payload, Map<String, String> reportProperties) {
        LOGGER.info(reportProperties);
        DynamicDataProvider dataProvider = new DynamicDataProvider(reportProperties);
        Map<String, Object> parameters = new HashMap<>();
        List<Map<String, Object>> data;
        JasperDesign jasperDesign = loadTemplate(reportProperties.get(ReportConstants.TEMPLATE));
        JasperReport jasperReport = compileTemplate(jasperDesign);
        JRParameter[] reportParameters = jasperReport.getParameters();
        Object[] datasourceParameters = Arrays.stream(reportParameters).filter(parameter -> (parameter
                .getValueClass().equals(JRDataSource.class)) && (!parameter.getName().equals
                ("REPORT_DATA_SOURCE"))).toArray();
        if (datasourceParameters.length > 1) {
            LOGGER.warn("Too many parameters for datasource.");
            Map<String, List<Map<String, Object>>> dataWithMultipleDatasources = dataProvider
                    .getDataWithMultipleDatasets(payload);
            for (Map.Entry<String, List<Map<String, Object>>> entry : dataWithMultipleDatasources.entrySet()) {
                data = entry.getValue();
                JRMapArrayDataSource mapArrayDataSource = new JRMapArrayDataSource(data.toArray());
                parameters.put(entry.getKey(), mapArrayDataSource);
            }
        } else {
            data = getDataFromPayload(dataProvider, payload);
            JRMapArrayDataSource mapArrayDataSource = new JRMapArrayDataSource(data.toArray());
            try {
                parameters.put(((JRParameter) datasourceParameters[0]).getName(), mapArrayDataSource);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new SiddhiAppRuntimeException("Datasets are missing in the template provided " +
                        reportProperties.get(ReportConstants.TEMPLATE), e);
            }
        }
        JasperPrint jasperPrint = fillReportData(jasperReport, parameters, new JREmptyDataSource());
        exportAsPdf(jasperPrint, reportProperties.get(ReportConstants.OUTPUT_PATH));
        LOGGER.info("output path : " + reportProperties.get(ReportConstants.OUTPUT_PATH));
        File f = new File(reportProperties.get(ReportConstants.OUTPUT_PATH));
        LOGGER.info("File exists : " + f.exists());
        LOGGER.info("Generated report " + reportProperties.get(ReportConstants.OUTPUT_PATH));
    }

    public List<Map<String, Object>> getDataFromPayload(DynamicDataProvider dataProvider, Object payload) {
        return dataProvider.getData(payload);
    }
}
