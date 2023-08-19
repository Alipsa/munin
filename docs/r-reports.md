# R reports

Creating html from R code can be done by using the htmlcreator package for Renjin, e.g:
```r
library('se.alipsa:htmlcreator')

html.clear()
html.add("<h2>A Sample report with a table and an image<h2>")
html.addPlot(
  {
    barplot(
      table(mtcars$vs, mtcars$gear),
      main="Car Distribution by Gears and VS",
      col=c("darkblue","red")
    )
  }
)
html.add("<div class='table'>")
html.add(mtcars)
html.add("</div>")

# Return the html
html.content()
```

Any R code that returns html can be used however - you are not bound to use htmlcreator.
This is why these type of reports are classified as "R" (as opposed to MDR) i.e. there is no magic to it (other than the magic of R).


[Ride](https://github.com/Alipsa/ride) supports the Munin R and mdr report formats natively,
so you can use Ride to create and edit Munin reports.

If you use some other IDE to create reports then you can do a simple
trick to detect the environment and display the report in the IDE or in Munin, i.e.
just before the very end when you return the html content, you check if you are running in the IDE and
display the report in Viewer tab, e.g:
```r
# If we are using some IDE that defines an inout object), display the report in the IDE
if(exists("inout")) {
  inout$viewHtml(html.content(), "SimpleExample")
}
html.content()
```
Note that from ver 1.2.3 of Ride, there is built-in support for Munin reports, and you have a View button
instead of the "normal" Run button than does this magic for you (and adds bootstrap etc. making the View
look the same as when viewed from a browser when run from within Munin).