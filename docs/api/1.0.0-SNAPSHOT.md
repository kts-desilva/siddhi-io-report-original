# API Docs - v1.0.0-SNAPSHOT

## Sink

### report *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#sink">(Sink)</a>*

<p style="word-wrap: break-word"> </p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@sink(type="report", chart="<STRING>", template="<STRING>", header="<STRING>", footer="<STRING>", category="<STRING>", series="<STRING>", description="<STRING>", title="<STRING>", subtitle="<STRING>", chart.title="<STRING>", outputpath="<STRING>", dataset.name="<STRING>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">chart</td>
        <td style="vertical-align: top; word-wrap: break-word">Chart to be added into the report</td>
        <td style="vertical-align: top">table</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">template</td>
        <td style="vertical-align: top; word-wrap: break-word">JRXML template path</td>
        <td style="vertical-align: top">/home/senuri/Projects/Jasper/template/dynamicTemplate.jrxml</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">header</td>
        <td style="vertical-align: top; word-wrap: break-word">Header image for the report</td>
        <td style="vertical-align: top">/home/senuri/Projects/Jasper/template/stream-processor.png</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">footer</td>
        <td style="vertical-align: top; word-wrap: break-word">Footer image for the report</td>
        <td style="vertical-align: top">/home/senuri/Projects/Jasper/template/stream-processor.png</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">category</td>
        <td style="vertical-align: top; word-wrap: break-word">Category variable for the chart</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">series</td>
        <td style="vertical-align: top; word-wrap: break-word">Series variable for the chart</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">description</td>
        <td style="vertical-align: top; word-wrap: break-word">Description for the report</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">title</td>
        <td style="vertical-align: top; word-wrap: break-word">Title of the report</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">subtitle</td>
        <td style="vertical-align: top; word-wrap: break-word">Subtitle of the report</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">chart.title</td>
        <td style="vertical-align: top; word-wrap: break-word">Title of the chart</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">outputpath</td>
        <td style="vertical-align: top; word-wrap: break-word">The folder where report is saved</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">dataset.name</td>
        <td style="vertical-align: top; word-wrap: break-word">The name of the parameter of the dataset</td>
        <td style="vertical-align: top">none</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
 @sink(type='report',@map(type='json'))define stream BarStream(symbol string, price float, volume long);
```
<p style="word-wrap: break-word"> Under above configuration, for an event chunck,a report of type PDF will be generated. There will be a table in the report.</p>

## Source

### report *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#source">(Source)</a>*

<p style="word-wrap: break-word"> </p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@source(type="report", @map(...)))
```

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
 
```
<p style="word-wrap: break-word"> </p>

