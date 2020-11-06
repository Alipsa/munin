# renjin-web-reports
This is a reports server for reports created in R based on Renjin and Spring Boot

# Basic idea
This is a reporting server that can run and display reports created in Renjin R on the web.

Currrently, it support R reports where the R program returns html. 
This can be done by using the renjin-html package



Todo: additional R report formats
 1. Support rmd (R markdown) scripts. This depends on fixing the markdown package so that knitr will work


Later on maybe I'll attempt to support Shiny as well...
At least 3 packages needs to fixed to be able to use the shiny package:
HTTPUV 1.5.1, PROMISES 1.0.1, and LATER 0.8.0