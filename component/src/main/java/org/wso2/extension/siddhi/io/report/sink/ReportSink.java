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

package org.wso2.extension.siddhi.io.report.sink;

import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.generators.DynamicReportGenerator;
import org.wso2.extension.siddhi.io.report.generators.QueryModeReportGenerator;
import org.wso2.extension.siddhi.io.report.generators.StaticReportGenerator;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.stream.output.sink.Sink;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.DynamicOptions;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains the implementation of siddhi-io-file sink which provides the functionality of publishing data
 * to reports as pdf files through siddhi.
 */

@Extension(
        name = "report",
        namespace = "sink",
        description = "Report sink can be used to publish (write) event data which is processed within siddhi" +
                "into reports.\nSiddhi-io-report provides support to generate reports in PDF format.\n",
        parameters = {
                @Parameter(name = "outputpath",
                        description = "This parameter is used to specify the report path for data to be written.",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "title",
                        description = "This parameter is used to specify the title of the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "description",
                        description = "This parameter is used to specify the description of the report.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "subtitle",
                        description = "This parameter is used to specify the subtitle of the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "template",
                        description = "This parameter is used to specify an external JRXML template path to generate " +
                                "the report. The given template will be filled and generate the report accordingly.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "dataset",
                        description = "This parameter is used to specify the dataset for the external template. This " +
                                "value can have a static stream attribute name or a dynamic value specified by '{}'" +
                                "eg:sink(type='report',dataset='{symbol}', @map(type='json'));" +
                                "define stream (symbol string, price float, volume long);",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "header",
                        description = "This parameter is used to specify the header image for the report.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "footer",
                        description = "This parameter is used to specify the footer image for the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "chart",
                        description = "Used to specify the chart type in the report. The value can be 'line', 'bar', " +
                                "'pie', 'table'. The chart is added into the report according to the parameter value.",
                        optional = true,
                        defaultValue = "table",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "chart.title",
                        description = "This parameter is used to specify the title of the chart. The title is added " +
                                "along with the chart.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "category",
                        description = "This parameter is used to specify the category variable for the chart defined." +
                                " The value of this parameter will be taken as the X axis of the chart.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "series",
                        description = "This parameter is used to specify the series variable for the chart. The value" +
                                " of this parameter will be taken as the Y axis of the chart and it is necessary to " +
                                "provide  numerical value for this parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "mode",
                        description = "This parameter is used to specify the series variable for the chart. The value" +
                                " of this parameter will be taken as the Y axis of the chart and it is necessary to " +
                                "provide  numerical value for this parameter.",
                        optional = true,
                        defaultValue = "stream",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "queries",
                        description = "This parameter is used to specify the series variable for the chart. The value" +
                                " of this parameter will be taken as the Y axis of the chart and it is necessary to " +
                                "provide  numerical value for this parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
        },
        examples = {
                @Example(
                        syntax = " " +
                                "@sink(type='report',outputpath='/abc/example.pdf',@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated. There will be a table in the report."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report',outputpath='/abc/{symbol}.pdf',@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated. The name of the report will be the first " +
                                "event value of the symbol parameter in the stream. There will be a table in the " +
                                "report."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report',outputpath='/abc/example.pdf',description='This is a sample " +
                                "report for the report sink.',title='Sample Report',subtitle='Report sink sample'," +
                                "@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated. There will be a table in the report." +
                                "The report title, description and subtitle will include the values specified as the " +
                                "parameters. The report will be generated in the given output path."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report',outputpath='/abc/example.pdf',chart='Line'," +
                                "chart.title='Line chart for the sample report.',category='symbol',series='price'," +
                                "@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated.The report report will include a line chart" +
                                " with the specified chart title. The chart will be generated with the specified " +
                                "category and series. The report will be generated in the given output path."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report', outputpath='/abc/example.pdf'," +
                                "mode='query',datasource.name='SAMPLE_DATASOURCE'," +
                                "queries=\"\"\"[{\"query\":\"SELECT * FROM SampleTable;\",\"chart\":\"table\"}," +
                                "@map(type='json'))",
                        description = " " +
                                "Under above configuration, for an event trigger," +
                                "a report of type PDF will be generated.The report report will include a table with " +
                                "the data from the RDBMS datasource specifies as 'datasource.name' and the data from " +
                                "the query as specified in 'queries'. The report will be saved in the given output " +
                                "path."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report', outputpath='/abc/example.pdf'," +
                                "mode='query',datasource.name='SAMPLE_DATASOURCE'," +
                                "queries=\"\"\"[{\"query\":\"SELECT * FROM SampleTable;\",\"chart\":\"table\"}," +
                                "{\"query\":\"SELECT Value, Age FROM SampleTable;\"," +
                                "\"chart\":\"line\",\"series\":\"Value\",\"category\":\"Age\",\"chart.title\":\"Test " +
                                "chart\"}]\"\"\",\n" +
                                "@map(type='json'))",
                        description = " " +
                                "Under above configuration, for an event trigger," +
                                "a report of type PDF will be generated. The will be two charts as per each RDBMS " +
                                "query. The datasource for both queries will be the value specified as 'datasource" +
                                ".name'. The first query will generate a table with the data from the query as " +
                                "specified in 'queries'. The second query will generate a line chart where the data " +
                                "will be taken from the second query as defined in the 'queries' parameter. The " +
                                "report will be saved in the given output path."
                )
        }
)

public class ReportSink extends Sink {
    private static final Logger log = Logger.getLogger(ReportSink.class);
    private OptionHolder optionHolder;
    private StreamDefinition streamDefinition;
    private SiddhiAppContext siddhiAppContext;
    private Map<String, String> reportProperties = new HashMap<>();

    /**
     * The initialization method for {@link Sink}, will be called before other methods. It used to validate
     * all configurations and to get initial values.
     *
     * @param streamDefinition containing stream definition bind to the {@link Sink}
     * @param optionHolder     Option holder containing static and dynamic configuration related
     *                         to the {@link Sink}
     * @param configReader     to read the sink related system configuration.
     * @param siddhiAppContext the context of the {@link org.wso2.siddhi.query.api.SiddhiApp} used to
     *                         get siddhi related utility functions.
     */
    @Override
    protected void init(StreamDefinition streamDefinition, OptionHolder optionHolder, ConfigReader configReader,
                        SiddhiAppContext siddhiAppContext) {
        this.optionHolder = optionHolder;
        this.streamDefinition = streamDefinition;
        this.siddhiAppContext = siddhiAppContext;
        validateAndGetParameters();
    }

    /**
     * Returns the list of classes which this sink can consume.
     * Based on the type of the sink, it may be limited to being able to publish specific type of classes.
     * For example, a sink of type file can only write objects of type String .
     *
     * @return array of supported classes , if extension can support of any types of classes
     * then return empty array .
     */
    @Override
    public Class[] getSupportedInputEventClasses() {
        return new Class[]{String.class, Event.class};
    }

    /**
     * Returns a list of supported dynamic options (that means for each event value of the option can change) by
     * the transport
     *
     * @return the list of supported dynamic option keys
     */
    @Override
    public String[] getSupportedDynamicOptions() {
        return new String[]{};
    }


    /**
     * This method will be called when events need to be published via this sink
     *
     * @param payload        payload of the event based on the supported event class exported by the extensions
     * @param dynamicOptions holds the dynamic options of this sink and Use this object to obtain dynamic options.
     * @throws ConnectionUnavailableException if end point is unavailable the ConnectionUnavailableException thrown
     *                                        such that the  system will take care retrying for connection
     */
    @Override
    public void publish(Object payload, DynamicOptions dynamicOptions) throws ConnectionUnavailableException {
        if (reportProperties.get(ReportConstants.MODE).equalsIgnoreCase(ReportConstants.DEFAULT_MODE)) {
            if (!reportProperties.get(ReportConstants.TEMPLATE).equals(ReportConstants.DEFAULT_TEMPLATE)) {
                ignoreOtherParameters(reportProperties);
                StaticReportGenerator staticReportGenerator = new StaticReportGenerator();
                staticReportGenerator.generateReport(payload, reportProperties);
            } else {
                DynamicReportGenerator dynamicReportGenerator = new DynamicReportGenerator();
                dynamicReportGenerator.generateReport(payload, reportProperties);
            }
        } else {
            QueryModeReportGenerator queryModeReportGenerator = new QueryModeReportGenerator();
            queryModeReportGenerator.generateReport(payload, reportProperties);
        }
    }

    private void ignoreOtherParameters(Map<String, String> reportProperties) {
        String[] ignoringParameters = {ReportConstants.HEADER, ReportConstants.FOOTER, ReportConstants.SERIES,
                ReportConstants.CATEGORY, ReportConstants.CHART, ReportConstants.DESCRIPTION, ReportConstants.SUBTITLE,
                ReportConstants.TITLE, ReportConstants.CHART_TITLE};
        Arrays.stream(ignoringParameters).forEach(parameter -> {
            if (reportProperties.containsKey(parameter)) {
                log.debug("In 'report' sink of siddhi app " + siddhiAppContext.getName() + " Ignoring " +
                        reportProperties.get(parameter) + " for " + parameter + " as JRXML is provided.");
            }
        });
    }

    private void validateAndGetParameters() {
        String template = optionHolder.validateAndGetStaticValue(ReportConstants.TEMPLATE, ReportConstants
                .DEFAULT_TEMPLATE);
        validatePath(template, ReportConstants.TEMPLATE);

        String header = optionHolder.validateAndGetStaticValue(ReportConstants.HEADER, ReportConstants.EMPTY_STRING);
        validatePath(header, ReportConstants.HEADER);

        String footer = optionHolder.validateAndGetStaticValue(ReportConstants.FOOTER, ReportConstants.EMPTY_STRING);
        validatePath(footer, ReportConstants.FOOTER);

        String chart = optionHolder.validateAndGetStaticValue(ReportConstants.CHART, ReportConstants.DEFAULT_CHART);
        validateChart(chart);

        String seriesVariable = optionHolder.validateAndGetStaticValue(ReportConstants.SERIES, ReportConstants
                .EMPTY_STRING);
        validateVariable(ReportConstants.SERIES, seriesVariable);

        String categoryVariable = optionHolder.validateAndGetStaticValue(ReportConstants.CATEGORY, ReportConstants
                .EMPTY_STRING);
        validateVariable(ReportConstants.CATEGORY, categoryVariable);

        String description = optionHolder.validateAndGetStaticValue(ReportConstants.DESCRIPTION, ReportConstants
                .EMPTY_STRING);
        validateStringParameters(ReportConstants.DESCRIPTION, description);

        String reportTitle = optionHolder.validateAndGetStaticValue(ReportConstants.TITLE, ReportConstants
                .DEFAULT_TITLE);
        validateStringParameters(ReportConstants.TITLE, reportTitle);

        String reportSubtitle = optionHolder.validateAndGetStaticValue(ReportConstants.SUBTITLE, ReportConstants
                .EMPTY_STRING);
        validateStringParameters(ReportConstants.SUBTITLE, reportSubtitle);

        String chartTitle = optionHolder.validateAndGetStaticValue(ReportConstants.CHART_TITLE, ReportConstants
                .EMPTY_STRING);
        validateStringParameters(ReportConstants.CHART_TITLE, chartTitle);

        String outputPath = optionHolder.validateAndGetStaticValue(ReportConstants.OUTPUT_PATH, ReportConstants
                .EMPTY_STRING);
        if (!outputPath.isEmpty() && outputPath.contains(File.separator)) {
            validatePath(outputPath.substring(0, outputPath.lastIndexOf(File.separator)), ReportConstants.OUTPUT_PATH);
        }
        validateStringParameters(ReportConstants.OUTPUT_PATH, outputPath);

        String datasetName = optionHolder.validateAndGetStaticValue(ReportConstants.DATASET, ReportConstants
                .EMPTY_STRING);
        validateStringParameters(ReportConstants.DATASET, datasetName);
        validateMapType();

        String queryMode = optionHolder.validateAndGetStaticValue(ReportConstants.MODE, ReportConstants.DEFAULT_MODE);
        validateMode(queryMode);

        String datasourceName = optionHolder.validateAndGetStaticValue(ReportConstants.DATASOURCE_NAME,
                ReportConstants.EMPTY_STRING);
        validateQueryParameter(queryMode, datasourceName, ReportConstants.DATASOURCE_NAME);

        String queries = optionHolder.validateAndGetStaticValue(ReportConstants.QUERIES,
                ReportConstants.EMPTY_STRING);
        validateQueryParameter(queryMode, queries, ReportConstants.QUERIES);
    }

    private void validateMode(String queryMode) {
        if (queryMode.equalsIgnoreCase(ReportConstants.DEFAULT_MODE) || queryMode.equalsIgnoreCase(ReportConstants
                .QUERY)) {
            reportProperties.put(ReportConstants.MODE, queryMode);
        } else {
            throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() +
                    " '" + queryMode + "' is invalid. Should be either query or stream.");
        }
    }

    private void validateQueryParameter(String queryMode, String queryValue, String parameterName) {
        if (Boolean.parseBoolean(queryMode)) {
            if (queryValue.isEmpty()) {
                throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() +
                        " '" + parameterName + "' Should be defined when 'mode' is query.");
            }
        }
        reportProperties.put(parameterName, queryValue);
    }

    private void validateMapType() {
        String mapType = streamDefinition.getAnnotations().get(0).getAnnotations().get(0)
                .getElements().get(0).getValue();
        if (!mapType.equals("json")) {
            throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() +
                    " Invalid map type " + mapType + " Only JSON map type is allowed.");
        }
    }

    private void validateStringParameters(String property, String value) {
        if (property.equals(ReportConstants.OUTPUT_PATH) || property.equals(ReportConstants.DATASET)) {
            String dynamicOptionPattern = "(\\{\\w*\\})";
            Pattern pattern = Pattern.compile(dynamicOptionPattern);
            Matcher matcher = pattern.matcher(value);

            if (matcher.find()) {
                String matchingPart = matcher.group().substring(1, matcher.group().length() - 1);
                Attribute matchingAttribute = streamDefinition.getAttributeList().stream()
                        .filter(attribute -> attribute.getName().equals(matchingPart))
                        .findAny()
                        .orElse(null);
                if (matchingAttribute != null) {
                    if (property.equals(ReportConstants.OUTPUT_PATH)) {
                        reportProperties.put(ReportConstants.REPORT_DYNAMIC_NAME_VALUE, matcher.group());
                    } else if (property.equals(ReportConstants.DATASET)) {
                        reportProperties.put(ReportConstants.REPORT_DYNAMIC_DATASET_VALUE, matcher.group());
                    }
                } else {
                    throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName()
                            + " Invalid Property '" + matchingPart + "'. No such parameter in the stream definition");
                }
            }
            if (property.equals(ReportConstants.OUTPUT_PATH)) {
                if (!value.endsWith(ReportConstants.PDF_EXTENSION)) {
                    value += ReportConstants.PDF_EXTENSION;
                }
            }
        }
        //doesn't check for empty strings as they are ignored in report generation in default.
        reportProperties.put(property, value);
    }

    private void validateVariable(String property, String chartVariable) {
        if (!chartVariable.isEmpty()) {
            Optional<Attribute> validAttribute = streamDefinition.getAttributeList().stream()
                    .filter(attribute -> attribute.getName().equals(chartVariable))
                    .findAny();
            if (validAttribute.isPresent()) {
                if (property.equals(ReportConstants.SERIES)) {
                    if (!isNumeric(validAttribute.get().getType())) {
                        throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext
                                .getName() + " " + chartVariable + "is invalid. Provide a numeric series column.");
                    }
                }
            } else {
                throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName()
                        + " Invalid property " + chartVariable + " for " + property);
            }
            reportProperties.put(property, chartVariable);
        }
    }

    private boolean isNumeric(Attribute.Type attributeType) {
        switch (attributeType) {
            case INT:
            case LONG:
            case DOUBLE:
            case FLOAT:
                return true;
            default:
                return false;
        }
    }

    private void validatePath(String path, String parameter) {
        Path file = new File(path).toPath();
        ClassLoader classLoader = ReportSink.class.getClassLoader();
        URL resourceFile = classLoader.getResource(path);
        if (resourceFile != null) {
            file = new File(resourceFile.getFile()).toPath();
        }
        FileSystem fileSystem = FileSystems.getDefault();
        if (!Files.exists(file)) {
            if (!path.equals(ReportConstants.DEFAULT_TEMPLATE)) {
                throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() +
                        " " + path + " does not exists. " + parameter + " should be a valid path");
            }
        }

        if (parameter.equals(ReportConstants.TEMPLATE)) {
            PathMatcher matcher = fileSystem.getPathMatcher("glob:**.jrxml");
            if (!path.isEmpty()) {
                if (!matcher.matches(file)) {
                    throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName()
                            + " " + path + " is invalid." + ReportConstants
                            .TEMPLATE + " should have a JRXML template");
                } else {
                    reportProperties.put(parameter, path);
                }
            }
        }

        if (parameter.equals(ReportConstants.HEADER) || parameter.equals(ReportConstants.FOOTER)) {
            PathMatcher matcher = fileSystem.getPathMatcher("glob:**.{png,jpeg,JPEG}");
            if (!path.isEmpty()) {
                if (!matcher.matches(file)) {
                    throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName()
                            + " Invalid path " + path + ". " + parameter + " should be an image");
                } else {
                    reportProperties.put(parameter, path);
                }
            }
        }
    }

    private void validateChart(String chart) {
        List<String> validChartTypes = Stream.of(ReportConstants.ChartTypes.values()).map(ReportConstants
                .ChartTypes::name).collect(Collectors.toList());
        if (!validChartTypes.contains(chart.toUpperCase(Locale.ENGLISH))) {
            throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() + " " +
                    chart + " is not a valid chart type. Only table,line,bar,pie charts are supported.");
        }
        if (!chart.equals(ReportConstants.DEFAULT_CHART)) {
            if (!reportProperties.containsKey(ReportConstants.SERIES)) {
                boolean numericAttributeFound = streamDefinition.getAttributeList().stream()
                        .anyMatch(attribute -> isNumeric(attribute.getType()));
                if (!numericAttributeFound) {
                    throw new SiddhiAppCreationException("In 'report' sink of siddhi app " +
                            siddhiAppContext.getName() + " " + chart + " chart definition is invalid. " +
                            "There is no numeric stream attribute for the series in. Provide a numeric series column.");
                }
            }
        } else {
            //warn for unnecessary parameter definition for table chart.
            if (reportProperties.containsKey(ReportConstants.SERIES) || reportProperties.containsKey(ReportConstants
                    .CATEGORY)) {
                log.warn("In 'report' sink of siddhi app " + siddhiAppContext.getName() + " Invalid " + chart + " " +
                        "definition. Series or category parameters is ignored for table chart.");
            }
        }
        reportProperties.put(ReportConstants.CHART, chart);
    }

    /**
     * This method will be called before the processing method.
     * Intention to establish connection to publish event.
     *
     * @throws ConnectionUnavailableException if end point is unavailable the ConnectionUnavailableException thrown
     *                                        such that the  system will take care retrying for connection
     */
    @Override
    public void connect() throws ConnectionUnavailableException {
        // do nothing
    }

    /**
     * Called after all publishing is done, or when {@link ConnectionUnavailableException} is thrown
     * Implementation of this method should contain the steps needed to disconnect from the sink.
     */
    @Override
    public void disconnect() {
        // do nothing
    }

    /**
     * The method can be called when removing an event receiver.
     * The cleanups that have to be done after removing the receiver could be done here.
     */
    @Override
    public void destroy() {
        // do nothing
    }

    /**
     * Used to collect the serializable state of the processing element, that need to be
     * persisted for reconstructing the element to the same state on a different point of time
     * This is also used to identify the internal states and debugging
     *
     * @return all internal states should be return as an map with meaning full keys
     */
    @Override
    public Map<String, Object> currentState() {
        return null;
    }

    /**
     * Used to restore serialized state of the processing element, for reconstructing
     * the element to the same state as if was on a previous point of time.
     *
     * @param map the stateful objects of the processing element as a map.
     *            This map will have the  same keys that is created upon calling currentState() method.
     */
    @Override
    public void restoreState(Map<String, Object> map) {
        // no state
    }
}
