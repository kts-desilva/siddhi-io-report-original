/*
 * Copyright (C) 2018 WSO2 Inc. (http://wso2.com) All Rights Reserved.

 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wso2.extension.siddhi.io.report.report.dynamic;

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
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.report.DynamicStyleProvider;
import org.wso2.extension.siddhi.io.report.report.RangeStatusLightCondition;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

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
    private Map<String, List<Map<String, Object>>> multipleDatasourcedata;
    private int columnCount;

    public void setReportProperties(Map<String, String> reportProperties) {
        this.reportProperties = reportProperties;
    }

    private Map<String, String> reportProperties;

    public DynamicDataProvider() {
        abstractColumns = new ArrayList<>();
        abstractColumnMap = new HashMap<>();
        data = new ArrayList<>();
        multipleDatasourcedata = new HashMap<>();
        columnCount = 0;
    }

    public List<Map<String, Object>> getData(Object payload, DynamicReportBuilder reportBuilder) {
        try {
            JsonParser payloadParser = new JsonParser();
            JsonElement payloadJson = payloadParser.parse(payload.toString());
            if (!payloadJson.isJsonArray()) {
                payloadJson = payloadParser.parse("[" + payload.toString() + "]");
            }
            JsonArray events = payloadJson.getAsJsonArray();
            Map<String, String> columnMetadata = getColumnMetaData(events.get(0).getAsJsonObject());
            for (JsonElement eventElement : events) {
                JsonObject event = eventElement.getAsJsonObject();
                Map<String, Object> eventMap = getMapFromJsonObject(event);
                this.data.add(eventMap);
            }
            addAbstractColumns(columnCount, columnMetadata, reportBuilder);
            setDynamicReportName(events.get(0).getAsJsonObject());
            return this.data;
        } catch (JsonSyntaxException e) {
            throw new SiddhiAppCreationException("Invalid mapper type. Set the map to json");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapFromJsonObject(JsonObject jsonObject) {
        return new Gson().fromJson(jsonObject.get("event").toString(), LinkedHashMap.class);
    }


    private Map<String, String> getColumnMetaData(JsonObject jsonObject) {
        //used linked hashmap inorder to keep the insertion order of the json elements.
        //Map<String, String> columnMetadata = new LinkedHashMap<>();
        Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
        Map columnMetadata = eventMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getClass().getName(),
                        (oldValue, newValue) -> newValue, LinkedHashMap::new));
        columnCount = eventMap.size();

        logger.info("new : " + columnMetadata);
        return columnMetadata;
    }

    private void setDynamicReportName(JsonObject jsonObject) {
        if (reportProperties.containsKey(ReportConstants.REPORT_DYNAMIC_VALUE)) {
            String dynamicReportNameParameter = reportProperties.get(ReportConstants.REPORT_DYNAMIC_VALUE);
            JsonElement dynamicReportElement = jsonObject.get("event").getAsJsonObject().get
                    (dynamicReportNameParameter.substring(1, dynamicReportNameParameter.length() - 1));
            if (dynamicReportElement != null) {
                String dynamicReportNameValue = dynamicReportElement.getAsString();
                String dynamicOptionPattern = "(\\{\\w*\\})";
                Pattern pattern = Pattern.compile(dynamicOptionPattern);
                Matcher matcher = pattern.matcher(reportProperties.get(ReportConstants.REPORT_NAME));
                String newReportName = matcher.replaceAll(dynamicReportNameValue);
                reportProperties.put(ReportConstants.REPORT_NAME, newReportName);
            }
        }
    }

    public List<Map<String, Object>> getData() {
        return this.data;
    }

    private void addAbstractColumns(int columnCount, Map<String, String> metaData, DynamicReportBuilder reportBuilder) {
        int columnSize = ReportConstants.COLUMN_WIDTH / columnCount;

        for (Map.Entry<String, String> entry : metaData.entrySet()) {
            ColumnBuilder columnBuilder = ColumnBuilder.getNew();
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
        RangeStatusLightCondition rangeStatusLightCondition = new RangeStatusLightCondition();
        return new ConditionalStyle(rangeStatusLightCondition, rangeStyle);
    }

    public List<AbstractColumn> getColumns() {
        return abstractColumns;
    }

    public AbstractColumn getCategoryColumn(String columnName) {
        AbstractColumn categoryColumn = abstractColumnMap.get(columnName);
        if (categoryColumn != null) {
            return categoryColumn;
        } else {
            throw new SiddhiAppRuntimeException("Invalid category column");
        }
    }

    public AbstractColumn getSeriesColumn(String columnName) {
        AbstractColumn seriesColumn = abstractColumnMap.get(columnName);
        if (seriesColumn != null) {
            return seriesColumn;
        } else {
            throw new SiddhiAppRuntimeException("Invalid series column");
        }
    }

    public AbstractColumn getCategoryColumn() {
        return abstractColumns.get(0);
    }

    public AbstractColumn getSeriesColumn() {
        return abstractColumns.get(1);
    }

    public List<Map<String, Object>> getData(Object payload) {
        try {
            JsonParser payloadParser = new JsonParser();
            JsonElement payloadJson = payloadParser.parse(payload.toString());
            if (!payloadJson.isJsonArray()) {
                payloadJson = payloadParser.parse("[" + payload.toString() + "]");
            }
            JsonArray events = payloadJson.getAsJsonArray();
            for (JsonElement eventElement : events) {
                JsonObject jsonObject = eventElement.getAsJsonObject();
                Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
                this.data.add(eventMap);
            }
            setDynamicReportName(events.get(0).getAsJsonObject());
            return this.data;
        } catch (JsonSyntaxException e) {
            throw new SiddhiAppCreationException("Invalid mapper type. Set the map to json");
        }
    }

    public Map<String, List<Map<String, Object>>> getDataWithMultipleDatasources(Object payload) {
        try {
            JsonParser payloadParser = new JsonParser();
            JsonElement payloadJson = payloadParser.parse(payload.toString());
            if (!payloadJson.isJsonArray()) {
                payloadJson = payloadParser.parse("[" + payload.toString() + "]");
            }
            JsonArray events = payloadJson.getAsJsonArray();
            for (JsonElement eventElement : events) {
                JsonObject jsonObject = eventElement.getAsJsonObject();
                Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
                String datasourceName = eventMap.get("datasource").toString();
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
            setDynamicReportName(events.get(0).getAsJsonObject());
            return this.multipleDatasourcedata;
        } catch (JsonSyntaxException e) {
            throw new SiddhiAppCreationException("Invalid mapper type. Set the map to json");
        }
    }
}
