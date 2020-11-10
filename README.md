# renjin-web-reports
This is a reports server for reports created in R based on Renjin and Spring Boot

# Basic idea
This is a reporting server that can run and display reports created in Renjin R on the web.

Currrently, it support R reports where the R program returns html. 
This can be done by using the htmlcreator package for Renjin, e.g:
```r
library('se.alipsa:htmlcreator')

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

# Return the html
html.content()
```

## Parameterized reports
When publishing a report you can optionally add report parameters in the form of
html form content, e.g:
```html
<div class="form-group">
<label for="firstName">First name</label>
<input id="firstName" name="firstName">
</div>

<div class="form-group">
<select name="dataSet">
   <option value="mtcars">Motor Trend Car Road Tests</option>
   <option value="iris">3 species of iris flowers</option>
</select>
</div>
```
Bootstrap is available, so you can use bootstrap classes to style the form.



#Todo: 

- additional R report formats
    1. Support rmd (R markdown) scripts. This depends on fixing the markdown package so that knitr will work


- Later on maybe I'll attempt to support Shiny as well...
At least 3 packages needs to fixed to be able to use the shiny package:
HTTPUV 1.5.1, PROMISES 1.0.1, and LATER 0.8.0