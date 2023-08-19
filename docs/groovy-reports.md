# Groovy reports

Groovy reports are just Groovy code that generates html.
You are completely free to generate that html any way you prefer.

A convenient way it to use the Html class of the gmd library which has some 
nice ways of dealing with Matrix tables and charts.

If you want to add bootstrap and highlightjs to your html, you can use the
se.alipsa.groovy.gmd.HtmlDecorator.decorate() method to do that e.g:

```groovy
import se.alipsa.groovy.gmd.*

Html html = new Html()
html.add("<h1>Hello</h1>")
HtmlDecorator.decorate(html.toString())

```
