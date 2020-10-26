# renjin-web-reports
This is a reports server for reports created in R based on Renjin and Spring Boot

# Basic idea
This will be a reporting server that can run and display reports created in Renjin R on the web.

The initial target will be:
 1. To create some custom formatter package based on the xmlr package to generate xhtml
 1. To support rmd (R markdown) scripts. This depends on fixing the markdown package so that knitr will work


Later on maybe I'll attempt to support Shiny as well...
At least 3 packages needs to fixed to be able to use the shiny package:
HTTPUV 1.5.1, PROMISES 1.0.1, and LATER 0.8.0