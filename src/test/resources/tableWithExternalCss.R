library('se.alipsa:htmlcreator')

html.clear()
html.add("<html><head>")
if(exists("inout")) {
  # To be able to preview it in Ride:
  html.add("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css'>")
  html.add("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js'></script>")
  html.add("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js'></script>")
  html.add(paste0("<link rel='stylesheet' href='file://", getwd(), "/resources/mystyle.css'>"))
} else {
  html.add("<link rel='stylesheet' href='/common/mystyle.css'>")
}

html.add("</head><body>")
html.add("<h2>Orange trees summary</h2>")
orangeDfSum <- as.data.frame(sapply(Orange[, c("age", "circumference")], summary))
orangeDfSum <- cbind(statistic = c("Min", "1st Q", "Median", "Mean", "3rd Q", "Max"), orangeDfSum)

html.add(orangeDfSum, htmlattr=list("id"="strongTable", "class"="table table-striped"))
html.add("</html></body>")

html.content()