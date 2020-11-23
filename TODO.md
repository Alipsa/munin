# To do (backlog)

## Features    
* Add support for additional remote repos when using AetherPackageLoader
    
* Figure out a way/workflow to support adding of packages when using ClasspathPackageLoader
    - Add config for who can add packages (ADMIN, ANALYST)
    - Currently, when set to ClasspathPackageLoader, we use the spring boot classloader: this would need to change
         
* Customization 
    - Document how and what can be customized, at least
        - styles
        - database
        - logging
    - Customize enough so that you don't have to fork the project and build your own (should be simple with an external application.properties in the same dir as the jar) 
        
* Production readiness
    - Do some load testing to check performance, concurrency and resource consumption
    - Run some security audit to ensure Security Config is good enough
    - Document how to set up as an autostarting service     
    
## Libraries used    
### cron ui input
- https://github.com/invigoro/Cron-Scheduler-Input is a fork of the original jquery-cron-quartz with support
for i18n and initial cron expressions. Consider testing it and maybe change. The original has not been updated for
several years...
    