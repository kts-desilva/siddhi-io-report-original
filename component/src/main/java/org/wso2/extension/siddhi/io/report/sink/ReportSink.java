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
import org.wso2.extension.siddhi.io.report.report.DynamicReportGenerator;
import org.wso2.extension.siddhi.io.report.report.StaticReportGenerator;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import org.wso2.extension.siddhi.io.report.report.DynamicReportGenerator;

/**
 * This class contains the implementation of siddhi-io-report sink which provides the functionality of publishing
 * data to reports as PDF files through siddhi.
 */

/**
 * Annotation of Siddhi Extension.
 * <pre><code>
 * eg:-
 * {@literal @}Extension(
 * name = "The name of the extension",
 * namespace = "The namespace of the extension",
 * description = "The description of the extension (optional).",
 * //Sink configurations
 * parameters = {
 * {@literal @}Parameter(name = "The name of the first parameter", type = "Supprted parameter types.
 *                              eg:{DataType.STRING,DataType.INT, DataType.LONG etc},dynamic=false ,optinal=true/false ,
 *                              if optional =true then assign default value according the type")
 *   System parameter is used to define common extension wide
 *              },
 * examples = {
 * {@literal @}Example({"Example of the first CustomExtension contain syntax and description.Here,
 *                      Syntax describe default mapping for SourceMapper and description describes
 *                      the output of according this syntax},
 *                      }
 * </code></pre>
 */

@Extension(
        name = "report",
        namespace = "sink",
        description = " ",
        parameters = {
                @Parameter(name = "chart",
                        description = "Chart to be added into the report",
                        optional = true,
                        defaultValue = "table",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "template",
                        description = "JRXML template path",
                        optional = true,
                        defaultValue = "/home/senuri/Projects/Jasper/template/dynamicTemplate.jrxml",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "header",
                        description = "Header image for the report",
                        optional = true,
                        defaultValue = "/home/senuri/Projects/Jasper/template/stream-processor.png",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "footer",
                        description = "Footer image for the report",
                        optional = true,
                        defaultValue = "/home/senuri/Projects/Jasper/template/stream-processor.png",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "category",
                        description = "Category variable for the chart",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "series",
                        description = "Series variable for the chart",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "description",
                        description = "Description for the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "title",
                        description = "Title of the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "subtitle",
                        description = "Subtitle of the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "chart.title",
                        description = "Title of the chart",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "report.name",
                        description = "Name of the report generated",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "report.uri",
                        description = "The folder where report is saved",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),

                /*@Parameter(name = " ",
                        description = " " ,
                        dynamic = false/true,
                        optional = true/false, defaultValue = " ",
                        type = {DataType.INT, DataType.BOOL, DataType.STRING, DataType.DOUBLE,etc }),
                        type = {DataType.INT, DataType.BOOL, DataType.STRING, DataType.DOUBLE, }),*/
        },
        examples = {
                @Example(
                        syntax = " " +
                                "@sink(type='report',@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated. There will be a table in the report."
                )
        }
)

// for more information refer https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sinks

public class ReportSink extends Sink {
    private static final Logger log = Logger.getLogger(ReportSink.class);
    private OptionHolder optionHolder;
    private StreamDefinition streamDefinition;
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
        return new String[]{ReportConstants.REPORT_NAME};
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
        log.info("payload : " + payload);
        if (!reportProperties.get(ReportConstants.TEMPLATE).equals(ReportConstants.DEFAULT_TEMPLATE)) {
            StaticReportGenerator staticReportGenerator = new StaticReportGenerator();
            staticReportGenerator.setReportProperties(reportProperties);
            staticReportGenerator.generateReport(payload);
        } else {
            DynamicReportGenerator dynamicReportGenerator = new DynamicReportGenerator();
            dynamicReportGenerator.setReportProperties(reportProperties);
            dynamicReportGenerator.generateReportFromData(payload);
        }
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

        String outPath = optionHolder.validateAndGetStaticValue(ReportConstants.URI, ReportConstants
                .EMPTY_STRING);
        validatePath(outPath, ReportConstants.URI);

        String reportName = optionHolder.validateAndGetStaticValue(ReportConstants.REPORT_NAME, ReportConstants
                .DEFAULT_REPORT_NAME);
        validateStringParameters(ReportConstants.REPORT_NAME, reportName);
        validateMapType();
    }

    private void validateMapType() {
        String mapType = streamDefinition.getAnnotations().get(0).getAnnotations().get(0).getElements().get(0)
                .getValue();
        if (!mapType.equals("json")) {
            throw new SiddhiAppCreationException("Invalid map type " + mapType + "Only JSON map type is allowed.");
        }
    }

    private void validateStringParameters(String property, String value) {
        if (property.equals(ReportConstants.REPORT_NAME)) {
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
                    reportProperties.put(ReportConstants.REPORT_DYNAMIC_VALUE, matcher.group());
                } else {
                    throw new SiddhiAppCreationException("Invalid Property '" + matchingPart + "'. No such " +
                            "parameter in the stream definition");
                }
            }
        }
        this.reportProperties.put(property, value);
    }

    private void validateVariable(String property, String chartVariable) {
        if (!chartVariable.isEmpty()) {
            List<Attribute> attributeList = streamDefinition.getAttributeList();
            boolean validAttributeFound = false;
            for (Attribute attribute : attributeList) {
                if (attribute.getName().equals(chartVariable)) {
                    if (property.equals(ReportConstants.SERIES)) {
                        Attribute.Type attributeType = attribute.getType();
                        if (!isNumeric(attributeType)) {
                            throw new SiddhiAppCreationException(chartVariable + "is invalid. Provide a numeric " +
                                    "series column.");
                        }
                    }
                    validAttributeFound = true;
                }
            }
            if (validAttributeFound) {
                reportProperties.put(property, chartVariable);
            } else {
                throw new SiddhiAppCreationException("Invalid property " + chartVariable + " for " + property);
            }
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
                throw new SiddhiAppCreationException(path + " does not exists. " + parameter + " should be a valid " +
                        "path");
            }
        }

        if (parameter.equals(ReportConstants.TEMPLATE)) {
            PathMatcher matcher = fileSystem.getPathMatcher("glob:**.jrxml");
            if (!path.isEmpty()) {
                if (!matcher.matches(file)) {
                    throw new SiddhiAppCreationException(parameter + " is invalid." + ReportConstants.TEMPLATE + " " +
                            "should have a JRXML template");
                } else {
                    reportProperties.put(parameter, path);
                }
            }
        }

        if (parameter.equals(ReportConstants.HEADER) || parameter.equals(ReportConstants.FOOTER)) {
            PathMatcher matcher = fileSystem.getPathMatcher("glob:**.{png,jpeg,JPEG}");
            if (!path.isEmpty()) {
                if (!matcher.matches(file)) {
                    throw new SiddhiAppCreationException("Invalid path " + path + ". " + parameter + " should be an " +
                            "image");
                } else {
                    reportProperties.put(parameter, path);
                }
            }
        }

        if (parameter.equals(ReportConstants.URI)) {
            reportProperties.put(parameter, path);
        }
    }

    private void validateChart(String chart) {
        List<String> validChartTypes = Stream.of(ReportConstants.ChartTypes.values()).map(ReportConstants
                .ChartTypes::name).collect(Collectors.toList());
        if (!validChartTypes.contains(chart.toUpperCase(Locale.ENGLISH))) {
            throw new SiddhiAppCreationException(chart + " is not a valid chart type. " +
                    "Only table,line,bar,pie charts are supported.");
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
