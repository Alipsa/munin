# To do (backlog)
* additional R report formats
    1. Support rmd (R markdown) scripts. This depends on fixing the markdown package so that knitr will work

* Later on maybe I'll attempt to support Shiny as well...
  At least 3 packages needs to fixed to be able to use the shiny package:
  HTTPUV 1.5.1, PROMISES 1.0.1, and LATER 0.8.0
  
* Add support for additional remote repos when using AetherPackageLoader
    
* Figure out a way/workflow to support adding of packages when using ClasspathPackageLoader
    - Add config for who can add packages (ADMIN, ANALYST)
    - Currently, when set to ClasspathPackageLoader, we use the spring boot classloader: this would need to change
         
* Customization 
    - Document how and what can be customized, at least
        - styles
        - database
        - logging
       
* Production readiness
    - Do some load testing to check performance, concurrency and resource consumption
    - Run some 3:rd party security audit to ensure Security Config is good enough
    - Document how to set up as an autostarting service
    