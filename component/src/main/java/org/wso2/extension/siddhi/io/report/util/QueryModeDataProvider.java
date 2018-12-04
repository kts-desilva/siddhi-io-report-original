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
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.generators.RangeConditionStyleExpressionGenerator;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is the implementation of the query mode data provider.
 */
public class QueryModeDataProvider implements DataProvider {
    private static Logger logger = Logger.getLogger(QueryModeDataProvider.class);
    private HikariDataSource dataSource;
    private String dataSourceName;
    private List<AbstractColumn> abstractColumns;
    private Map<String, AbstractColumn> abstractColumnMap;
    private DynamicReportBuilder reportBuilder;

    public QueryModeDataProvider(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        this.dataSource = RDBMSUtil.getDataSourceService(this.dataSourceName);
        this.abstractColumns = new ArrayList<>();
        this.abstractColumnMap = new HashMap<>();
    }

    public void setReportBuilder(DynamicReportBuilder reportBuilder) {
        this.reportBuilder = reportBuilder;
    }

    private Connection getConnection() {
        Connection conn;
        try {
            conn = this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new SiddhiAppRuntimeException("Cannot initialize datasource '" + this.dataSourceName +
                    "'connection: ", e);
        }
        return conn;
    }

    public List<Map<String, Object>> processData(String query) {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        ResultSetMetaData metaData = null;
        List<Map<String, Object>> data = new ArrayList<>();

        try {
            stmt = conn.prepareStatement(query);
            resultSet = stmt.executeQuery();
            metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> dataMap = new HashMap<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    dataMap.put(metaData.getColumnName(i + 1), resultSet.getObject(i + 1));
                }
                data.add(dataMap);
            }
            addAbstractColumns(metaData);
            return data;
        } catch (SQLException e) {
            throw new SiddhiAppRuntimeException("Cannot retrieve records from  datasource '" + this.dataSourceName, e);
        } finally {
            RDBMSUtil.cleanupConnection(resultSet, stmt, conn);
        }
    }

    public void addAbstractColumns(ResultSetMetaData metaData) {
        int columnSize = 0;
        try {
            columnSize = ReportConstants.COLUMN_WIDTH / metaData.getColumnCount();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                ColumnBuilder columnBuilder = ColumnBuilder.getNew();
                String columnClassName = metaData.getColumnClassName(i + 1);
                String columnName = metaData.getColumnName(i + 1);
                if (columnClassName.equals(Integer.class.getName()) || columnClassName.equals(Float.class.getName()
                ) || columnClassName.equals(Double.class.getName())) {
                    columnBuilder.addConditionalStyle(getNumericalConditionalStyle());
                } else if (columnClassName.equals(String.class.getName())) {
                    columnBuilder.addConditionalStyle(getStringConditionalStyle());
                }

                AbstractColumn abColumn = columnBuilder.setColumnProperty(columnName, columnClassName)
                        .setTitle(StringUtils.capitalize(columnName)).setWidth(columnSize)
                        .setHeaderStyle(DynamicStyleProvider.getColumnHeaderStyle(columnClassName))
                        .build();
                abstractColumns.add(abColumn);
                abstractColumnMap.put(columnName.toLowerCase(Locale.ENGLISH), abColumn);
                reportBuilder.addField(columnName, columnClassName);
            }
        } catch (SQLException e) {
            throw new SiddhiAppRuntimeException("Could not load metadata from '" + this.dataSourceName, e);
        }
    }

    public ConditionalStyle getNumericalConditionalStyle() {
        Style numericalStyle = new Style();
        numericalStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
        StatusLightCondition numericalLightCondition = new StatusLightCondition((double) 0, (double) Integer
                .MAX_VALUE);
        return new ConditionalStyle(numericalLightCondition, numericalStyle);
    }

    public ConditionalStyle getStringConditionalStyle() {
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
        return abstractColumnMap.get(columnName.toLowerCase(Locale.ENGLISH));
    }

    public AbstractColumn getSeriesColumn(String columnName) {
        return abstractColumnMap.get(columnName.toLowerCase(Locale.ENGLISH));
    }

    public AbstractColumn getCategoryColumn() {
        return abstractColumns.get(0);
    }

    public AbstractColumn getSeriesColumn() {
        return abstractColumns.get(1);
    }
}
