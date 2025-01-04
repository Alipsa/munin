import se.alipsa.groovy.gmd.Html
import se.alipsa.groovy.charts.*
import se.alipsa.groovy.datasets.*
import se.alipsa.groovy.matrix.*

html = new Html()

html.add('<html><body>')
html.add('''
    <style>
    .table-font-size {
  font-size: 14px;
}
    </style>
''')

html.add('<h2>A Sample report with a table and an image<h2>')
mtcars = Dataset.mtcars()
carDist = Stat.frequency(mtcars, 'gear', 'vs')
println carDist.content()
println carDist.transpose('vs', true).content()