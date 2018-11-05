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

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.chart.DJChart;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.report.dynamic.DynamicDataProvider;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is the implementation of the report generation logic.
 */
public class DynamicReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(DynamicReportGenerator.class);
    private DynamicDataProvider dataProvider;
    private ChartGenerator chartGenerator;
    private String categoryName;
    private String seriesName;
    private Map<String, Object> parameters;
    private DynamicReportBuilder reportBuilder;
    private Map<String, String> reportProperties;

    public DynamicReportGenerator() {
        dataProvider = new DynamicDataProvider();
        chartGenerator = new ChartGenerator();
        categoryName = "";
        seriesName = "";
        parameters = new HashMap<>();
        reportProperties = new HashMap<>();
        reportBuilder = new DynamicReportBuilder();
    }

    public void generateReportFromData(Object payload) {
        List<Map<String, Object>> data = dataProvider.getData(payload, reportBuilder);
//        reportBuilder.setTemplateFile("//home/senuri/Projects/Jasper/JasperTestPOC/siddhi-io-report/component/src" +
//                "/main/java/org/wso2/extension/siddhi/io/report/report/template/dynamicTemplate.jrxml");
        reportBuilder.setTemplateFile(reportProperties.get(ReportConstants.TEMPLATE));

        if (!reportProperties.get(ReportConstants.CHART).isEmpty()) {
            addChart(reportProperties, reportBuilder);
        }

        DynamicReport report = reportBuilder.build();
        parameters = setParameters(reportProperties, parameters);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        JasperPrint jasperPrint;
        try {
            if (reportProperties.get(ReportConstants.FOOTER) != null) {
                jasperPrint = DynamicJasperHelper.generateJasperPrint(report, new CustomLayoutManager
                        (reportProperties.get(ReportConstants.FOOTER)), dataSource, parameters);
            } else {
                jasperPrint = DynamicJasperHelper.generateJasperPrint(report, new CustomLayoutManager(), dataSource,
                        parameters);
            }
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the JasperPrint ", e);
        } catch (ClassCastException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the report. Provide a numeric series column. ", e);
        }

        try {
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportProperties.get(ReportConstants.URI) +
                    reportProperties.get(ReportConstants.REPORT_NAME) + ".pdf");
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the PDF ", e);
        }

        //LOGGER.info("replacing string : " + matcher.replaceAll(dataProvider.getDynamicReportNameValue()));
        File f = new File(reportProperties.get(ReportConstants.URI) +
                reportProperties.get(ReportConstants.REPORT_NAME) + ".pdf");
        LOGGER.info("File exists : " + f.exists());
        LOGGER.info("Generated report " + reportProperties.get(ReportConstants.REPORT_NAME));
        LOGGER.info("File exists : " + f.exists());
    }

    private void addChart(Map<String, String> reportProperties, DynamicReportBuilder reportBuilder) {
        String chartTitle = reportProperties.get(ReportConstants.CHART_TITLE);
        if (reportProperties.get(ReportConstants.CATEGORY) != null) {
            categoryName = reportProperties.get(ReportConstants.CATEGORY);
        }
        if (reportProperties.get(ReportConstants.SERIES) != null) {
            seriesName = reportProperties.get(ReportConstants.SERIES);
        }
        switch (reportProperties.get(ReportConstants.CHART).toLowerCase(Locale.ENGLISH)) {
            case "pie":
                DJChart pieChart;
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    pieChart = chartGenerator.createPieChart(dataProvider, chartTitle, categoryName, seriesName);
                } else {
                    pieChart = chartGenerator.createPieChart(dataProvider, chartTitle);
                }
                reportBuilder.addChart(pieChart);
                addTable(reportBuilder, dataProvider, chartGenerator, parameters);
                break;
            case "bar":
                DJChart barChart;
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    barChart = chartGenerator.createBarChart(dataProvider, chartTitle, categoryName,
                            seriesName);
                } else {
                    barChart = chartGenerator.createBarChart(dataProvider, chartTitle);
                }
                reportBuilder.addChart(barChart);
                addTable(reportBuilder, dataProvider, chartGenerator, parameters);
                break;
            case "line":
                DJChart lineChart;
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    lineChart = chartGenerator.createLineChart(dataProvider, chartTitle, categoryName,
                            seriesName);
                } else {
                    lineChart = chartGenerator.createLineChart(dataProvider, chartTitle);
                }
                reportBuilder.addChart(lineChart);
                addTable(reportBuilder, dataProvider, chartGenerator, parameters);
                break;
            default:
                chartGenerator.createTable(dataProvider, reportBuilder);
                break;
        }
    }

    public void setReportProperties(Map<String, String> reportProperties) {
        this.reportProperties = reportProperties;
        this.dataProvider.setReportProperties(reportProperties);
    }

    private Map<String, Object> setParameters(Map<String, String> reportProperties, Map<String, Object> parameters) {
        parameters.put(ReportConstants.TITLE, reportProperties.get(ReportConstants.TITLE));
        parameters.put(ReportConstants.SUBTITLE, reportProperties.get(ReportConstants.SUBTITLE));
        parameters.put(ReportConstants.DESCRIPTION, reportProperties.get(ReportConstants.DESCRIPTION));
        if ((reportProperties.get(ReportConstants.HEADER) != null)) {
            if (!reportProperties.get(ReportConstants.HEADER).isEmpty()) {
                parameters.put("headerImage", reportProperties.get(ReportConstants.HEADER));
            }
        }
        return parameters;
    }

    private void addTable(DynamicReportBuilder reportBuilder, DynamicDataProvider dataProvider, ChartGenerator
            chartGenerator, Map<String, Object> parameters) {
        DynamicReportBuilder nextReportBuilder = new DynamicReportBuilder();
        nextReportBuilder = chartGenerator.createTable(dataProvider, nextReportBuilder);

        DynamicReport nextReport = nextReportBuilder.build();
        reportBuilder.addConcatenatedReport(nextReport, new ClassicLayoutManager(), ReportConstants
                .DEFAULT_DATASOURCE, DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION);
        parameters.put(ReportConstants.DEFAULT_DATASOURCE, dataProvider.getData());
    }
}
