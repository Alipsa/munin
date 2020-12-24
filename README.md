# Munin
This is a report server for reports created in R based on Renjin and Spring Boot.
The name comes from the one of Odin's ravens who he sent out every day to scout the world and bring him
back reports. 

# Basic idea
This is a reporting server that can run and display reports created in Renjin R on the web.

Currently, it supports R reports where the R program returns html. 
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
e.g. by using `exists()`. Let's say the parameter is the name of the dataset to use i.e.  
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
  dataSet <- "iris"
}
```
# Styling
Bootstrap is available, so you can use bootstrap classes to style the form.
If you are using the htmlcreator package, the html.add() functions takes a list of attributes as 
an optional parameter. This way you can add attributes such as id and class like this:
```r
html.add(mtcars, htmlattr=list(id = "mtcars-table", class="table table-striped"))
```

There is currently no way to add generic stylesheets that can be referenced in the reports but you can 
of course add stylesheets inline, e.g.
```r
# add a style class to adjust the font size of table text:
html.add("
<style>
  .table-font-size {
    font-size: 14px;
  }
</style>
")

# reference the class together with some bootstrap classes when rendering a table:
html.add(mtcars, htmlattr=list(class="table table-striped table-font-size"))
```
# Admin
The default admin user name / password is admin / adminpwd.
If you log in as admin you will see an "Administration" button on the main page (http://localhost:8088).
There are three predefined roles:
- Viewer: someone who can only view reports. There is one predefined: test / testpwd
- Analyst: someone who can view / add / edit and schedule reports. There is one predefined: analyst /analystpwd
- Admin: someone who can do user/role administration. There is one predefined: admin / adminpwd

# Installing
There are a few different ways to install Munin.

1. Simple:
    - Download the munin-[version].jar file from https://github.com/perNyfelt/munin/releases/latest
    - Copy the jar to a directory of your choice
    - create a application-prod.properties file and override whatever default config you need
    - run the application with `java -Dspring.profiles.active=prod -jar munin-[version].jar`
      or create a bash starter script and make it run as a [Linux service](https://linuxconfig.org/how-to-create-systemd-service-unit-in-linux)
      or [Windows service](https://github.com/winsw/winsw).
      See the [Spring documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment-service)
      for more info.
      
2. Customized:
   This is appropriate if you want to do more involved customization.
   - Create a new maven (of Gradle or whatever) project and set munin as the parent project:
   
   ```xml
   <parent>
       <artifactId>munin</artifactId>
       <groupId>se.alipsa</groupId>
       <version>1.0.0</version>
   </parent>
   ```
3. Customized alternative:
Fork the project on [github](https://github.com/perNyfelt/munin) and make any changes you want.
Create the executable jar with `mvn clean package` and copy it from your target dir.   


# Production config 
You can do any customization by adding an application-prod.properties file next to the jar.
Then start the server with `-Dspring.profiles.active=prod` set e.g.
`java -Dspring.profiles.active=prod -jar munin-1.0.0-SNAPSHOT.jar`
This will override any default config with your specific config.

## application-prod.properties variables
See application.properties for settings to override. Typically, you will change the following 

### Web port
Set the property `server.port` to something else e.g. `server.port=8080` to listen for
web requests on port 8080 instead of the default 8088.

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

### Monitoring
Perhaps not something you would typically override, but you likely want to set up some kind of integration
with whatever health monitoring tool you are using at your business.

Actuator is included with default settings which means that a network monitoring tool can
check for availability by querying `http://localhost:8088/actuator/health` which will return the
json string `{"status":"UP"}` if everything is normal.


The first thing you should probably do after setting up a database and providing the necessary config overrides 
is to change / remove the three predefined users using the admin interface mentioned above.

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

See pom.xml for more details...

