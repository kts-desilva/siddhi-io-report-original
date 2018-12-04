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

import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.carbon.datasource.core.exception.DataSourceException;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is the implementation of the util class of the RDBMS datasources.
 */

public class RDBMSUtil {
    private static final Logger LOG = Logger.getLogger(RDBMSUtil.class);

    /**
     * Utility method to get the datasource service
     *
     * @param dataSourceName The datasource name
     * @return Hikari Data Source
     */
    public static HikariDataSource getDataSourceService(String dataSourceName) {

        BundleContext bundleContext = FrameworkUtil.getBundle(DataSourceService.class)
                .getBundleContext();
        ServiceReference serviceRef = bundleContext.getServiceReference(DataSourceService.class
                .getName());
        if (serviceRef == null) {
            throw new SiddhiAppRuntimeException("DatasourceService : '" +
                    DataSourceService.class.getName() + "' cannot be found.");
        } else {
            DataSourceService dataSourceService = (DataSourceService) bundleContext
                    .getService(serviceRef);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Lookup for datasource '" + dataSourceName + "' completed through " +
                        "DataSource Service lookup.");
            }
            try {
                return (HikariDataSource) dataSourceService.getDataSource(dataSourceName);
            } catch (DataSourceException e) {
                throw new SiddhiAppRuntimeException("Datasource '" + dataSourceName + "' cannot be " +
                        "connected.", e);
            }
        }

    }

    /**
     * Method which can be used to clear up and ephemeral SQL connectivity artifacts.
     *
     * @param rs   {@link ResultSet} instance (can be null)
     * @param stmt {@link Statement} instance (can be null)
     * @param conn {@link Connection} instance (can be null)
     */
    public static void cleanupConnection(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Closed ResultSet");
                }
            } catch (SQLException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error in closing ResultSet: ", e);
                }
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Closed PreparedStatement");
                }
            } catch (SQLException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error in closing PreparedStatement: ", e);
                }

            }
        }
        if (conn != null) {
            try {
                conn.close();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Closed Connection");
                }
            } catch (SQLException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error in closing Connection: ", e);
                }
            }
        }
    }
}
