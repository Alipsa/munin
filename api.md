# Munin Rest API

Munin provides a rest api for integration with other applications (e.g. [Ride](https://github.com/perNyfelt/ride)).
Per default, the analyst group have access to the REST api.

## getReportGroups
- Description: get a list of the report groups defined
- Path: /api/getReportGroups
- Method: GET
- Returns: A JSON array of strings of the report group names defined

- Example:

```shell
curl -u analyst:analystpwd localhost:8088/api/getReportGroups
```
Returns:
```json
["Examples","MDR","Parameterized reports"]
```

## getReports
- Description: get a list of reports for a report group
- Path: /api/getReports
- Method: GET
- Parameters: String groupName
- Returns: A JSON array of _Report_ objects of the reports for the group name specified
A _Report_ object contains the following fields:
    - **reportName**: This is the unique identifier for a report
    - **description:** A free text short description of the report. 
        - Type: String
        - Max length = 500
    - **definition**: This is the R or mdr code (logic for the report). 
        - Type: String
        - Max length = 15000
    - **inputContent**: The parameter(s) prompt html code
        - Type: String
        - Max length = 9000
    - **reportType**;
        - Type: ReportType, an ENUM string with the possible values UNMANAGED, MDR
    - **reportGroup**: The name of the report group this report belongs to
        - Type: String
        - Max length = 50
    
- Example:
```shell
curl -u analyst:analystpwd localhost:8088/api/getReports?groupName=Examples
```
Returns:
```json
[
  {
    "reportName":"Iris",
    "description":"Pie chart with external image",
    "definition":"library('se.alipsa:htmlcreator')\r\n\r\nhtml.clear()\r\nhtml.add(\"<html><body>\")\r\nhtml.add(\"<h2>Iris report</h2>\")\r\n\r\nif(exists(\"inout\")) {\r\n  html.add(html.imgUrl(paste0(\"file://\", getwd(),\"/resources/iris.jpg\")))\r\n} else {\r\n  html.add(html.imgUrl(\"/common/iris.jpg\"))\r\n}\r\nspecies <- table(iris$Species)\r\nhtml.add(\r\n  pie, \r\n  species, \r\n  labels = paste(names(species), \"\\n\", species, sep=\"\"), \r\n  main=\"Pie Chart of Species\\n (with sample sizes)\"\r\n)\r\n\r\nhtml.add(\"</html></body>\")\r\n\r\nif(exists(\"inout\")) {\r\n  inout$viewHtml(html.content(), \"Pie chart\")\r\n}\r\nhtml.content()",
    "inputContent":"",
    "reportType":"UNMANAGED",
    "reportGroup":"Examples"
  },
  {
    "reportName":"Sample",
    "description":"Sample report",
    "definition":"library('se.alipsa:htmlcreator')\r\n\r\nhtml.clear()\r\nhtml.add(\"<html><body>\")\r\nhtml.add(\"\r\n<style>\r\n  .table-font-size {\r\n    font-size: 14px;\r\n  }\r\n</style>\r\n\")\r\n\r\nhtml.add(\"<h2>A Sample report with a table and an image<h2>\")\r\nhtml.add(\r\n  barplot,\r\n  table(mtcars$vs, mtcars$gear),\r\n  main=\"Car Distribution by Gears and VS\",\r\n  col=c(\"darkblue\",\"red\"),\r\n  htmlattr = list(alt=\"an mtcars plot\")\r\n)\r\nhtml.add(mtcars, htmlattr=list(class=\"table table-striped table-font-size\"))\r\n\r\nhtml.add(\"</html></body>\")\r\n# If we are using Ride (or another IDE that defines an inout object), display the report in the IDE\r\nif(exists(\"inout\")) {\r\n  inout$viewHtml(html.content(), \"SimpleExample\")\r\n}\r\nhtml.content()\r\n",
    "inputContent":"",
    "reportType":"UNMANAGED",
    "reportGroup":"Examples"
  }
]
```

## addReport
- Description: add a new report
- Path: /api/addReport
- Method: POST
- Parameters: None
- Payload: A Report JSON object as described above  
- Returns: 200 OK if successful otherwise an HTTP error e.g. BAD REQUEST or INTERNAL SERVER ERROR

- Example:
```shell
curl -u analyst:analystpwd --header "Content-Type: application/json" --request POST --data '{ "reportName":"Hello Example","description":"Hello World","definition":"library(\"se.alipsa:htmlcreator\")\nhtml.clear()\nhtml.add(\"<h1>Hello World</h1>\")\nhtml.content()","inputContent":"","reportType":"UNMANAGED","reportGroup":"Examples"}' localhost:8088/api/addReport
```

## updateReport
- Description: update an existing report
- Path: /api/updateReport
- Method: PUT
- Parameters: None
- Payload: A Report JSON object as described above
- Returns: 200 OK if successful otherwise an HTTP error e.g. BAD REQUEST or INTERNAL SERVER ERROR

- Example:
```shell
curl -u analyst:analystpwd --header "Content-Type: application/json" --request PUT --data '{ "reportName":"Hello Example","description":"Hello World","definition":"library(\"se.alipsa:htmlcreator\")\nhtml.new(\"<h1>Hello World updated</h1>\")\nhtml.content()","inputContent":"","reportType":"UNMANAGED","reportGroup":"Examples"}' localhost:8088/api/updateReport
```