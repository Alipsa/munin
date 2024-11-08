import se.alipsa.groovy.gmd.Html
import se.alipsa.groovy.charts.*
import se.alipsa.groovy.datasets.*
import se.alipsa.groovy.matrix.*

/*
This is a barchart and a styled table
 */
def html = new Html()

html.add('<html><body>')
html.add('''
    <style>
    .table-font-size {
  font-size: 14px;
}
    </style>
''')

html.add('<h2>A Sample report with a table and an image<h2>')
def mtcars = Dataset.mtcars()
def carDist = Stat.frequency(mtcars, 'gear', 'vs')
html.add(BarChart.create("Car Distribution by Gears and VS"))
html.add(
  barplot,
  table(mtcars$vs, mtcars$gear),
  main="Car Distribution by Gears and VS",
  col=c("darkblue","red"),
  htmlattr = list(alt="an mtcars plot")
)
html.add(mtcars, htmlattr=list(class="table table-striped table-font-size"))

html.add("</html></body>")
html.content()