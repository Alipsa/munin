import se.alipsa.groovy.gmd.*
import se.alipsa.matrix.datasets.*
import se.alipsa.matrix.core.*

html = new Html()
html.add("<html><body>")
html.add('''
<style>
  .table-font-size {
    font-size: 14px;
  }
</style>
''')

html.add("<h2>A Sample report with a table and an image<h2>")
mtcars = Dataset.mtcars()
// vs is V-shape (vs=0) or Straight Line (vs=1)
gearsAndVs = Stat.frequency(mtcars, 'gear', 'vs', false)
//io.view(gearsAndVs)
tgv = gearsAndVs.transpose(['gear', 'vs0', 'vs1'], [String, int, int], true)
//io.view(tgv)

// TODO: fix syntax
html.add(
    barplot,
    table(mtcars$vs, mtcars$gear),
    main="Car Distribution by Gears and VS",
    col=c("darkblue","red"),
    htmlattr = list(alt="an mtcars plot")
)
html.add(mtcars, [class:"table table-striped table-font-size"])

html.add("</html></body>")
html.content()