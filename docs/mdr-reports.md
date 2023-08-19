# Mdr reports

Mdr files is another supported report format. Support for mdr files is provided by the
[mdr](https://github.com/perNyfelt/mdr) package. Mdr files are similar to rmd files but
gives you more control of the output of the r code parts. As a consequence it also pushes rendering
responsibility over to you i.e. you need to make sure that your R code returns valid markdown syntax.
Fortunately, the [r2md](https://github.com/perNyfelt/r2md) package does just that. It is "built in" to
the mdr package so no need to explicitly load it with "library(...)".

Set the "ReportType" to MDR to have Munin call mdr to render the report (mdr -> md -> html).
If report type is set to UNMANAGED Munin assumes it is working R code that returns html
(like in all the example above which is using the htmlcreator package) and will execute the code and render its result.

Here is a mdr example:

`````markdown
# MDR report example

This is a straight forward example of a mdr report. 
This way of creating reports are useful when there are more text to write than actual R code to run. 
I.e. you want the ease of markdown for your text, but the power of R for dynamic content. 

There are two ways to add R content.

1. Inline, eg 2 + 5 * pi = `r 2 + 5 * pi` or `r x <- 2 + 5 * pi; x` which just evaluates and returns the result as markdown text
2. Code blocks, i.e. longer pieces of R code that returns Markdown text e.g.
```{r}
employee <- c('John Doe','Peter Smith','Jane Doe')
salary <- c(21000, 23400, 26800)
startdate <- as.Date(c('2013-11-1','2018-3-25','2017-3-14'))
endDate <- as.POSIXct(c('2020-01-10 00:00:00', '2020-04-12 12:10:13', '2020-10-06 10:00:05'), tz='UTC' )
df <- data.frame(employee, salary, startdate, endDate)
md.add(df, attr=list(class="table"))
```
We can also reference previous code in the document e.g:
```{r}
md.new(paste("As stated before, 2 + 5 * pi =", x))
```
Plots are supported, here is an example of a barplot:
```{r}
md.add("# Barplot")
md.add(
  barplot,
  table(mtcars$gear),
  main="Car Distribution",
  horiz=TRUE,
  names.arg=c("3 Gears", "4 Gears", "5 Gears"),
  col=c("darkblue","red", "green")
)
```
See the r2md [README](https://github.com/perNyfelt/r2md/blob/main/README.md) for more information.

`````

Which will result in the following report output:
![mdrExample](docs/mdrExample.png)

## What about RMD?
Rmd requires knitr which depends on the Markdown package. The Markdown packages has some C code that
the Renjin GCC bridge cannot make sense of. Hence, knitr and thus the rmd file format does not (currently)
work in Renjin. As soon as that is fixed, I plan to support rmd files in Munin as well. 
