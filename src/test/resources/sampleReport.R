library('se.alipsa:renjin-html')

html.clear()
html.add("<html><body>")
html.add("<h2>A Sample report with a table and an image<h2>")
html.add(
  barplot,
  table(mtcars$vs, mtcars$gear),
  main="Car Distribution by Gears and VS",
  col=c("darkblue","red")
)
html.add("<div class='table'>")
html.add(mtcars)
html.add("</div>")

html.add("</html></body>")
# If we are using Ride (or another IDE that defines an inout object), display the report in the IDE
if(exists("inout")) {
  inout$viewHtml(html.content(), "SimpleExample")
}
html.content()
