library('se.alipsa:renjin-html')

# set defaults
if (!exists("dataSet")) {
  dataSet <- "PlantGrowth"
}
if (!exists("firstName")) {
  firstName <- "Per"
}

html.clear()
html.add("<html><body>")
html.add(paste("Hello", firstName))
if (dataSet == "PlantGrowth") {
  html.add("<h2>PlantGrowth weight</h2>")
  html.add(
    hist,
    PlantGrowth$weight
  )
  html.add(format(summary(PlantGrowth)))
} else if (dataSet == "mtcars") {
  html.add("<h2>mtcars qsec</h2>")
  html.add(
    hist,
    mtcars$qsec
  )
  html.add(format(summary(mtcars)))
} else {
  html.add(paste("Unknown option", dataSet))
}
html.add("</body></html>")
if(exists("inout")) {
  inout$viewHtml(html.content(), "ParamReport")
}
html.content()