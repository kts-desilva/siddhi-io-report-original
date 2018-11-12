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

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.domain.DynamicReport;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.wso2.extension.siddhi.io.report.util.DynamicLayoutManager;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.util.Map;

/**
 * This abstract class provides implementation of methods for the report generation.
 */
public abstract class ReportGenerator {

    public void exportAsPdf(JasperPrint jasperPrint, String outputPath) {
        try {
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the PDF. ", e);
        }
    }

    public JasperPrint generateReportPrint(DynamicReport report, DynamicLayoutManager reportLayout,
                                           JRBeanCollectionDataSource dataSource, Map<String, Object> parameters)
            throws SiddhiAppRuntimeException {
        JasperPrint jasperPrint;
        try {
            jasperPrint = DynamicJasperHelper.generateJasperPrint(report, reportLayout, dataSource,
                    parameters);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the JasperPrint ", e);
        } catch (ClassCastException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the report. Provide a numeric series column. ", e);
        }
        return jasperPrint;
    }

    public JasperPrint fillReportData(JasperReport jasperReport, Map<String, Object> parameters, JRDataSource
            dataSource) {
        JasperPrint jasperPrint;
        try {
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to fill data into report template. ", e);
        }
        return jasperPrint;
    }

    public JasperDesign loadTemplate(String template) {
        JasperDesign jasperDesign;
        try {
            jasperDesign = JRXmlLoader.load(template);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to load the report template. ", e);
        }
        return jasperDesign;
    }

    public JasperReport compileTemplate(JasperDesign jasperDesign) {
        JasperReport jasperReport;
        try {
            jasperReport = JasperCompileManager.compileReport(jasperDesign);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to compile the report. ", e);
        }
        return jasperReport;
    }

    public abstract void generateReport(Object payload, Map<String, String> reportProperties);
}
