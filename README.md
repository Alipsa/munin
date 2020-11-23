# Munin
This is a reports server for reports created in R based on Renjin and Spring Boot.
The name comes from the one of Odins ravens who he sent out every day to scout the world and bring him
back reports. 

# Basic idea
This is a reporting server that can run and display reports created in Renjin R on the web.

Currently, it support R reports where the R program returns html. 
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
Note that, in order to be able to schedule a parameterized report, you must provide default parameters in the R code
e.g. by using `exists()`. Lets say the parameter is the name of the dataset to use i.e.  
```html
<div class="form-group">
<select name="dataSet">
   <option value="mtcars">Motor Trend Car Road Tests</option>
   <option value="iris">3 species of iris flowers</option>
</select>
</div>
```
Then you can provide a default value for it as follows:
```r
if (!exists("dataSet")) {
  dataSet <- "PlantGrowth"
}
```
# Styling
Bootstrap is available, so you can use bootstrap classes to style the form.

# Production config 
you can do any customization by adding an application-prod.properties file next to the jar.
The start the server with `-Dspring.profiles.active=prod` set e.g.
`java -Dspring.profiles.active=prod -jar munin-1.0.0-SNAPSHOT.jar`
This will override any default config with your specific config.

### Web port
Set the property `server.port` to something else e.g. `server.port=8080` to listen for
web requests on port 8080 instead of the default 8088.

### Monitoring
Actuator is included with default settings which means that a network monitoring tool can
check for availability by querying `http://localhost:8088/actuator/health` which will return the
json string `{"status":"UP"}` if everything is normal.

### Database
The database stores the reports and user config. 
Default config is a file based H2 database (`jdbc:h2:file:./munindb;DATABASE_TO_LOWER=TRUE`) 
To change the underlying database config, set the spring.datasource.xxx parameters 
as you see fit.

Not that if you want another database other than H2, you need to make sure spring boot can access
the jdbc driver jar. This can be done by setting the loader.path, e.g:

1. create a lib folder where your spring boot jar resides
2. copy the additional jar to the lib folder
3. add path to the folder when starting spring boot:
`java -Dloader.path=file:lib/ -Dspring.profiles.active=prod -jar munin-1.0.0-SNAPSHOT.jar`

### Mail
Mail is used to email passwords when users are created as well as mailing out scheduled reports.
Set spring.mail.xxx properties as suitable for your mail server
The "from" address is controlled by the property `munin.email.from`
 

#Todo: 

- additional R report formats
    1. Support rmd (R markdown) scripts. This depends on fixing the markdown package so that knitr will work


- Later on maybe I'll attempt to support Shiny as well...
At least 3 packages needs to fixed to be able to use the shiny package:
HTTPUV 1.5.1, PROMISES 1.0.1, and LATER 0.8.0

# 3:rd party dependencies

## Renjin
- R interpreter for the JVM; used to execute reports
- https://renjin.org/
- License: GNU General Public License v2.0

## Spring boot
- The application server framework providing most of the functionality
- https://spring.io/projects/spring-boot
- License: Apache License 2.0

## se.alipsa:renjin-spring-boot-starter
- Integration of Renjin with Spring boot
- https://github.com/perNyfelt/renjin-spring-boot-starter
- License: MIT

## se.alipsa:htmlcreator
- renjin extension (package) to create html from R objects (data.frames, images etc)

## commons-io
- Various IO stuff
- https://commons.apache.org/proper/commons-io/
- License: Apache License 2.0

## org.apache.commons:commons-collections4
- Used to handle various collection transformations
- https://commons.apache.org/proper/commons-collections/
- License: Apache License 2.0

## cron-utils
- Used to convert Unix cron expressions to Spring boot equivalent
- https://github.com/jmrozanec/cron-utils
- License: Apache License 2.0

## org.webjars:webjars-locator
- Simplifies web resource locations
- http://webjars.org
- License: MIT

## org.webjars:bootstrap
- Pretty web pages
- http://webjars.org
- License: Apache License 2.0

## org.webjars:jquery
- More convenient javascripts
- http://webjars.org
- License: MIT

## org.webjars:codemirror
- Syntax highlighting for R and html code
- http://webjars.org
- License: MIT

## Jquery-cron
- Used in the UI to schedule reports
- version: shawnchin-jquery-cron-v0.1.4.1-0-g89922ea
- https://shawnchin.github.io/jquery-cron/
- License: MIT

See pom.xml for more details...

