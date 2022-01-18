library('se.alipsa:htmlcreator')

html.clear()
html.add("<html><body>")
html.add("<h2>Iris report</h2>")

if(exists("inout")) {
  html.add(html.imgUrl(paste0(muninBaseUrl, "/common/resources/iris.jpg")))
} else {
  html.add(html.imgUrl("/common/resources/iris.jpg"))
}
species <- table(iris$Species)
html.add(
  pie,
  species,
  labels = paste(names(species), "\n", species, sep=""),
  main="Pie Chart of Species\n (with sample sizes)"
)

html.add("</html></body>")

html.content()