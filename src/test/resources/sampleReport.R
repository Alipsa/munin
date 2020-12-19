library('se.alipsa:htmlcreator')

html.clear()
html.add("<html><body>")
html.add("
<style>
  .table-font-size {
    font-size: 14px;
  }
</style>
")

html.add("<h2>A Sample report with a table and an image<h2>")
html.add(
  barplot,
  table(mtcars$vs, mtcars$gear),
  main="Car Distribution by Gears and VS",
  col=c("darkblue","red"),
  htmlattr = list(alt="an mtcars plot")
)
html.add(mtcars, htmlattr=list(class="table table-striped table-font-size"))

html.add("</html></body>")
# If we are using Ride (or another IDE that defines an inout object), display the report in the IDE
if(exists("inout")) {
  inout$viewHtml(html.content(), "SimpleExample")
}
html.content()
