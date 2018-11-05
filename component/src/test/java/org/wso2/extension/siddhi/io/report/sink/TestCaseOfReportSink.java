package org.wso2.extension.siddhi.io.report.sink;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.stream.input.InputHandler;

import java.io.File;

public class TestCaseOfReportSink {
    // If you will know about this related testcase,
    //refer https://github.com/wso2-extensions/siddhi-io-file/blob/master/component/src/test
    public static final Logger LOGGER = Logger.getLogger(TestCaseOfReportSink.class);

    @Test
    public void reportSinkTest1() throws InterruptedException {
        LOGGER.info("----------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 1 - Configure siddhi to generate reports only using mandatory params");
        LOGGER.info("----------------------------------------------------------------------------------------");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(ReportConstants.DEFAULT_REPORT_NAME + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest2() throws InterruptedException {
        LOGGER.info("----------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 2 - Configure siddhi to generate reports with report name given");
        LOGGER.info("----------------------------------------------------------------------------------------");

        String testReportName = "TestReport";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "{volume}',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest3() throws InterruptedException {
        LOGGER.info("---------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 3 - Configure siddhi to generate reports with report path and name given.");
        LOGGER.info("---------------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testReportURI = "TestReportURI/";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', report.uri='" + testReportURI + "',@map" +
                "(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest4() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 4 - Configure siddhi to generate reports with invalid report path given.");
        LOGGER.info("--------------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testReportURI = "InvalidTestReportURI/";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', report.uri='" + testReportURI + "',@map" +
                "(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), testReportURI + " does not exists. report.uri should be" +
                    " a " +
                    "valid path");
        }
    }

    @Test
    public void reportSinkTest5() throws InterruptedException {
        LOGGER.info("-------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 5 - Generate reports with template given.");
        LOGGER.info("-------------------------------------------------------------");

        String testReportName = "TestTemplateReport";
        String testReportURI = "TestReportURI/";
        String testTemplatePath = TestCaseOfReportSink.class.getClassLoader().getResource("fromResultsetData.jrxml")
                .getFile();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "{symbol}', report.uri='" + testReportURI +
                "'," +
                "template='" + testTemplatePath + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest6() throws InterruptedException {
        LOGGER.info("---------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 6 - Generate reports with invalid template given.");
        LOGGER.info("---------------------------------------------------------------------");

        String testReportName = "TestTemplateReport";
        String testReportURI = "TestReportURI/";
        String invalidTemplatePath = "invalidTemplate.jrxml";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', report.uri='" + testReportURI + "'," +
                "template='" + invalidTemplatePath + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), invalidTemplatePath + " does not exists. template " +
                    "should be a valid path");
        }
    }

    @Test
    public void reportSinkTest7() throws InterruptedException {
        LOGGER.info("------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 7 - Generate reports with invalid header image given.");
        LOGGER.info("------------------------------------------------------------------------------");

        String testReportName = "TestHeaderReport";
        String testReportURI = "TestReportURI/";
        String invalidHeaderPath = "invalidHeaderImage.png";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', report.uri='" + testReportURI + "'," +
                "header='" + invalidHeaderPath + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), invalidHeaderPath + " does not exists. header should be" +
                    " a valid path");
        }
    }

    @Test
    public void reportSinkTest8() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 8 - Generate reports with invalid chart type given.");
        LOGGER.info("-----------------------------------------------------------------------");

        String testReportName = "TestChartReport";
        String testReportURI = "TestReportURI/";
        String invalidChartType = "histogram";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', report.uri='" + testReportURI + "'," +
                "chart='" + invalidChartType + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), invalidChartType + " is not a valid chart type. " +
                    "Only table,line,bar,pie charts are supported.");
        }
    }

    @Test
    public void reportSinkTest9() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 9 - Generate reports with invalid series type given.");
        LOGGER.info("-----------------------------------------------------------------------");

        String testReportName = "TestChartReport";
        String testReportURI = "TestReportURI/";
        String testChartType = "line";
        String invalidSeries = "symbol";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', report.uri='" + testReportURI + "'," +
                "chart='" + testChartType + "',series='" + invalidSeries + "',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

            siddhiAppRuntime.start();

            Event testEvent1 = new Event();
            testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

            Event testEvent2 = new Event();
            testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

            Event testEvent3 = new Event();
            testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

            Event testEvent4 = new Event();
            testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

            File sink = new File(testReportURI + testReportName + ".pdf");
            AssertJUnit.assertTrue(sink.exists());
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), invalidSeries + "is invalid. Provide a numeric " +
                    "series column.");
        }
    }

    @Test
    public void reportSinkTest10() throws InterruptedException {
        LOGGER.info("-------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 10 - Generate reports with series and category types given.");
        LOGGER.info("-------------------------------------------------------------------------------");

        String testReportName = "TestChartSeriesCategoryReport";
        String testReportURI = "TestReportURI/";
        String testChartType = "line";
        String testCategory = "symbol";
        String testSeries = "price";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', report.uri='" + testReportURI + "'," +
                "chart='" + testChartType + "',series='" + testSeries + "',category='" + testCategory + "',@map" +
                "(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();

    }

    @Test
    public void reportSinkTest11() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 11 - Generate reports with a single event.");
        LOGGER.info("--------------------------------------------------------------");

        String testReportName = "TestSingleEventReport";
        String testReportURI = "TestReportURI/";
        String testChartType = "line";
        String testCategory = "symbol";
        String testSeries = "price";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', report.uri='" + testReportURI + "'," +
                "chart='" + testChartType + "',series='" + testSeries + "',category='" + testCategory + "',@map" +
                "(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 100L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 100L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 100L});

        stockStream.send(new Object[]{"WSO2", 55.6f, 100L});

        File sink = new File(testReportURI + testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest12() throws InterruptedException {
        LOGGER.info("-------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 12 - Generate reports with dynamic variable in report.name");
        LOGGER.info("-------------------------------------------------------------------------------");

        String testReportName = "TestReport";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "{price}',@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");

        siddhiAppRuntime.start();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});

        File sink = new File(testReportName + ".pdf");
        AssertJUnit.assertTrue(sink.exists());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void reportSinkTest13() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 13 - Generate reports with invalid dynamic variable in report.name");
        LOGGER.info("--------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testDynamicReportName = "invalidName";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "{" + testDynamicReportName + "}'," +
                "@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "Invalid Property '" + testDynamicReportName + "'. " +
                    "No such parameter in the stream definition");
        }
    }

    @Test
    public void reportSinkTest14() throws InterruptedException {
        LOGGER.info("-----------------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 14 - Generate reports with invalid syntax for dynamic values in report.name");
        LOGGER.info("-----------------------------------------------------------------------------------------------");

        String testReportName = "TestReport";
        String testDynamicReportName = "volume";

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "{" + testDynamicReportName + "'," +
                "@map(type='json')) " +
                "define stream BarStream (symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        try {
            SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
            InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
            siddhiAppRuntime.start();
            stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
            siddhiAppRuntime.shutdown();
        } catch (SiddhiAppCreationException e) {
            Assert.assertEquals(e.getMessageWithOutContext(), "Invalid Property '" + testDynamicReportName + "'. " +
                    "No such parameter in the stream definition");
        }
    }

    @Test
    public void reportSinkTest15() throws InterruptedException {
        LOGGER.info("--------------------------------------------------------------------------------------");
        LOGGER.info("ReportSink TestCase 15 - Generate reports with multiple datasets for a given template.");
        LOGGER.info("--------------------------------------------------------------------------------------");

        String testReportName = "TestReportWithMultipleDatasets";
        String datasourceName1 = "TableDataSource";
        String datasourceName2 = "OtherTableDataSource";
        String testTemplate = TestCaseOfReportSink.class.getClassLoader().getResource("fromResultsetDataMultiple" +
                ".jrxml").getFile();

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "define stream FooStream(symbol string, price float, volume long); " +
                "@sink(type='report',report.name='" + testReportName + "', template='" + testTemplate + "', " +
                "@map(type='json')) " +
                "define stream BarStream (datasource string,symbol string,price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select ifThenElse(volume>200,'" + datasourceName1 + "','" + datasourceName2 + "') as datasource, " +
                "symbol,price,volume " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();

        Event testEvent1 = new Event();
        testEvent1.setData(new Object[]{"WSO2", 55.6f, 100L});

        Event testEvent2 = new Event();
        testEvent2.setData(new Object[]{"IBM", 57.678f, 200L});

        Event testEvent3 = new Event();
        testEvent3.setData(new Object[]{"GOOGLE", 50f, 300L});

        Event testEvent4 = new Event();
        testEvent4.setData(new Object[]{"WSO2", 55.6f, 400L});

        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("FooStream");
        siddhiAppRuntime.start();
        stockStream.send(new Event[]{testEvent1, testEvent2, testEvent3, testEvent4});
        siddhiAppRuntime.shutdown();

    }
}

