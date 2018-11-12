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

package org.wso2.extension.siddhi.io.report.util;

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.StatusLightCondition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.generators.RangeConditionStyleExpressionGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class provides the implementation of the data provider for the dynamic reports.
 */
public class DynamicDataProvider {
    private static final Logger logger = Logger.getLogger(DynamicDataProvider.class);
    private List<AbstractColumn> abstractColumns;
    private Map<String, AbstractColumn> abstractColumnMap;
    private List<Map<String, Object>> data;
    private Map<String, String> reportProperties;
    private JsonParser payloadParser;
    private Map<String, String> columnMetadata;

    public DynamicDataProvider(Map<String, String> reportProperties) {
        abstractColumns = new ArrayList<>();
        abstractColumnMap = new HashMap<>();
        data = new ArrayList<>();
        this.reportProperties = reportProperties;
        payloadParser = new JsonParser();
        columnMetadata = new HashMap<>();
    }

    public List<Map<String, Object>> getData(Object payload, DynamicReportBuilder reportBuilder) {
        addDataTo(payload.toString(), data, true);
        addAbstractColumns(columnMetadata, reportBuilder);
        return this.data;
    }

    public List<Map<String, Object>> getData(Object payload) {
        addDataTo(payload.toString(), this.data, false);
        return this.data;
    }

    private void addDataTo(String payloadStirng, List<Map<String, Object>> dataList, boolean dynamic) {
        JsonElement payloadJson = parsePayload(payloadStirng);
        JsonArray events = getEvents(payloadJson);
        for (JsonElement eventElement : events) {
            JsonObject jsonObject = eventElement.getAsJsonObject();
            Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
            dataList.add(eventMap);
        }
        setDynamicReportValue(events.get(0).getAsJsonObject(), ReportConstants.REPORT_DYNAMIC_NAME_VALUE,
                ReportConstants.OUTPUT_PATH);
        if (dynamic) {
            generateMetaData(events.get(0));
        }
    }

    private void generateMetaData(JsonElement element) {
        columnMetadata = getColumnMetaData(element.getAsJsonObject());
    }

    private JsonElement parsePayload(String payload) {
        JsonElement payloadJson = payloadParser.parse(payload);
        if (!payloadJson.isJsonArray()) {
            payloadJson = payloadParser.parse("[" + payload + "]");
        }
        return payloadJson;
    }

    private JsonArray getEvents(JsonElement parsedPayload) {
        return parsedPayload.getAsJsonArray();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapFromJsonObject(JsonObject jsonObject) {
        return new Gson().fromJson(jsonObject.get("event").toString(), LinkedHashMap.class);
    }

    private Map getColumnMetaData(JsonObject jsonObject) {
        //used linked hashmap inorder to keep the insertion order of the json elements.
        //Map<String, String> columnMetadata = new LinkedHashMap<>();
        Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
        Map columnMetadata = eventMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getClass().getName(),
                        (oldValue, newValue) -> newValue, LinkedHashMap::new));
        logger.info("new : " + columnMetadata);
        return columnMetadata;
    }

    private void setDynamicReportValue(JsonObject jsonObject, String dynamicValueName, String propertyValueName) {
        if (reportProperties.containsKey(dynamicValueName)) {
            String dynamicReportNameParameter = reportProperties.get(dynamicValueName);
            JsonElement dynamicReportElement = jsonObject.get("event").getAsJsonObject().get
                    (dynamicReportNameParameter.substring(1, dynamicReportNameParameter.length() - 1));
            if (dynamicReportElement != null) {
                String dynamicReportNameValue = dynamicReportElement.getAsString();
                logger.info("dynamic report name value : " + dynamicReportNameValue);
                String dynamicOptionPattern = "(\\{\\w*\\})";
                Pattern pattern = Pattern.compile(dynamicOptionPattern);
                Matcher matcher = pattern.matcher(reportProperties.get(propertyValueName));
                logger.info("matching value : " + reportProperties.get(propertyValueName));
                String newReportName = matcher.replaceAll(dynamicReportNameValue);
                reportProperties.put(propertyValueName, newReportName);
            }
        }
    }

    public List<Map<String, Object>> getData() {
        return this.data;
    }

    private void addAbstractColumns(Map<String, String> metaData, DynamicReportBuilder reportBuilder) {
        int columnSize = ReportConstants.COLUMN_WIDTH / metaData.size();
        for (Map.Entry<String, String> entry : metaData.entrySet()) {
            ColumnBuilder columnBuilder = ColumnBuilder.getNew();
            logger.info("map value : " + entry.getValue());
            if (entry.getValue().equals(Integer.class.getName()) || entry.getValue().equals(Float.class.getName()
            ) || entry.getValue().equals(Double.class.getName())) {
                columnBuilder.addConditionalStyle(getNumericalConditionalStyle());
            } else if (entry.getValue().equals(String.class.getName())) {
                columnBuilder.addConditionalStyle(getStringConditionalStyle());
            }

            AbstractColumn abColumn = columnBuilder.setColumnProperty(entry.getKey(), entry.getValue())
                    .setTitle(StringUtils.capitalize(entry.getKey())).setWidth(columnSize)
                    .setHeaderStyle(DynamicStyleProvider.getColumnHeaderStyle(entry.getValue()))
                    .build();
            abstractColumns.add(abColumn);
            abstractColumnMap.put(entry.getKey(), abColumn);
            reportBuilder.addField(entry.getKey(), entry.getValue());
        }
    }

    private ConditionalStyle getNumericalConditionalStyle() {
        Style numericalStyle = new Style();
        numericalStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
        StatusLightCondition numericalLightCondition = new StatusLightCondition((double) 0, (double) Integer
                .MAX_VALUE);
        return new ConditionalStyle(numericalLightCondition, numericalStyle);
    }

    private ConditionalStyle getStringConditionalStyle() {
        Style rangeStyle = new Style();
        rangeStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        RangeConditionStyleExpressionGenerator rangeConditionStyleExpressionGenerator = new
                RangeConditionStyleExpressionGenerator();
        return new ConditionalStyle(rangeConditionStyleExpressionGenerator, rangeStyle);
    }

    public List<AbstractColumn> getColumns() {
        return abstractColumns;
    }

    public AbstractColumn getCategoryColumn(String columnName) {
        return abstractColumnMap.get(columnName);
    }

    public AbstractColumn getSeriesColumn(String columnName) {
        return abstractColumnMap.get(columnName);
    }

    public Map<String, List<Map<String, Object>>> getDataWithMultipleDatasets(Object payload) {
        Map<String, List<Map<String, Object>>> multipleDatasourcedata = new HashMap<>();
        JsonElement payloadJson = parsePayload(payload.toString());
        JsonArray events = getEvents(payloadJson);
        setDynamicReportValue(events.get(0).getAsJsonObject(), ReportConstants.REPORT_DYNAMIC_DATASET_VALUE,
                ReportConstants.DATASET);
        for (JsonElement eventElement : events) {
            JsonObject jsonObject = eventElement.getAsJsonObject();
            Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
            String datasetAttribute = "";
            if (reportProperties.containsKey(ReportConstants.REPORT_DYNAMIC_DATASET_VALUE)) {
                String datasetAttributeTemp = reportProperties.get(ReportConstants.REPORT_DYNAMIC_DATASET_VALUE);
                datasetAttribute = datasetAttributeTemp.substring(1, datasetAttributeTemp.length() - 1);
            } else if (reportProperties.containsKey(ReportConstants.DATASET)) {
                //same as {} definition for parameter value
                datasetAttribute = reportProperties.get(ReportConstants.DATASET);
            }
            if (datasetAttribute.isEmpty()) {
                datasetAttribute = eventMap.entrySet().iterator().next().getKey();
            }
            logger.info("dataset attribure : " + datasetAttribute);
            String datasourceName = eventMap.get(datasetAttribute).toString();
            if (multipleDatasourcedata.containsKey(datasourceName)) {
                List<Map<String, Object>> datasource = multipleDatasourcedata.get(datasourceName);
                eventMap.remove(datasourceName);
                datasource.add(eventMap);
                multipleDatasourcedata.put(datasourceName, datasource);
            } else {
                eventMap.remove(datasourceName);
                List<Map<String, Object>> datasource = new ArrayList<>();
                datasource.add(eventMap);
                multipleDatasourcedata.put(datasourceName, datasource);
            }
        }
        setDynamicReportValue(events.get(0).getAsJsonObject(), ReportConstants.REPORT_DYNAMIC_NAME_VALUE,
                ReportConstants.OUTPUT_PATH);
        return multipleDatasourcedata;
    }

    public AbstractColumn getCategoryColumn() {
        return abstractColumns.get(0);
    }

    public AbstractColumn getSeriesColumn() {
        return abstractColumns.get(1);
    }
}
