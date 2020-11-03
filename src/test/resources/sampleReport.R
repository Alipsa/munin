library('se.alipsa:renjin-htmlcontent')

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
html$getContent()
