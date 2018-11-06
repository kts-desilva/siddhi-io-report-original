
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

package org.wso2.extension.siddhi.io.report.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.report.dynamic.DynamicDataProvider;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides implementation for the report generation for a given JRXML template file.
 */

public class StaticReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(StaticReportGenerator.class);

    public StaticReportGenerator() {
    }

    public void generateReport(Object payload, Map<String, String> reportProperties) {
        DynamicDataProvider dataProvider = new DynamicDataProvider(reportProperties);
        Map<String, Object> parameters = new HashMap<>();
        List<Map<String, Object>> data;
        JasperDesign jasperDesign;
        try {
            jasperDesign = JRXmlLoader.load(reportProperties.get(ReportConstants.TEMPLATE));
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to load the report template. ", e);
        }

        JasperReport jasperReport;
        try {
            jasperReport = JasperCompileManager.compileReport(jasperDesign);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to compile the report. ", e);
        }

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
            data = dataProvider.getData(payload);
            JRMapArrayDataSource mapArrayDataSource = new JRMapArrayDataSource(data.toArray());
            parameters.put(((JRParameter) datasourceParameters[0]).getName(), mapArrayDataSource);
        }

        JasperPrint jasperPrint;
        try {
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to fill data into report template. ", e);
        }
        try {
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportProperties.get(ReportConstants.OUTPUT_PATH));
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the PDF. ", e);
        }
        LOGGER.info("output path : " + reportProperties.get(ReportConstants.OUTPUT_PATH));
        File f = new File(reportProperties.get(ReportConstants.OUTPUT_PATH));
        LOGGER.info("File exists : " + f.exists());
        LOGGER.info("Generated report " + reportProperties.get(ReportConstants.OUTPUT_PATH));
    }
}
