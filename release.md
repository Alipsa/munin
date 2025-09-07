# Release history

### 2.0.0, in progress
- Add support for Groovy and GMD reports.
- Upgrade spring boot (2.7.2 -> 3.5.5)
- Upgrade thymeleaf (5 to 6)
- Upgrade boostrap (5.2.0 -> 5.3.8)
- Upgrade jQuery (3.6.0 -> 3.7.1)
- Upgrade liquibase (4.15.0 -> 4.33.0)
- Upgrade webjars (0.45 -> 0.52)
- Upgrade h2 (2.1.214 -> 2.3.232)
- Upgrade cron-utils (9.2.1 -> 9.2.1)
- upgrade javafx (21.0.5 -> 23.0.2)
- upgrade commons-io (2.19.0 -> 2.20.0)
- upgrade journo (0.7.1 -> 0.7.)
- Require Java 21
- Add parameters to the Preprocessor so that e.g. `= ` expressions works
- Add support for Journo (Freemarker) reports
- Remove support for R and mdr reports

### 1.2.1 (2022-Aug-16)
- Upgrade dependencies for bootstrap (5.1.3 -> 5.2.0), cron-utils (9.1.6 -> 9.2.0),
  liquibase (4.9.1 -> 4.15.0), h2 (2.1.210 -> 2.1.214), spring-boot (2.6.6 -> 2.7.2)

### 1.2.0 (2022-Jan-22)
- Upgrade the h2 version from 1.4.200 to 2.1.210 due to security vulnerability issues with the previous versions. Migration is needed.
- Added a migrateDb.sh script to facilitate h2 migration
- Upgrade liquibase core from 4.7.0 to 4.7.1

### 1.1.7 (2022-Jan-21)
- upgrade spring boot, mdr, plugins etc.
- Add some example to docs

### 1.1.6 (2021-Dec-08)
- Add breadcrumbs for improved navigation
- Cleanup alignments and fix some bootstrap 4 to 5 changes
- Add info dialog for report types
- upgrade spring-boot, liquibase, junit, htmlcreator, mdr2html

### 1.1.5 (2021-Oct-17)
- Bump versions for jquery, spring boot, cronutils, liquibase, spotbugs-annotations, junit, commons-collections
  bootstrap, webjars-locator
- Load javascripts at the end of the body instead of in the header - should give a slight perceived speed increase
- Add preview to common files section

### 1.1.4 (2021-Mar-16)
Fix regression bug introduced in 1.1.3 where jquery update was not handled properly in
the header.

### 1.1.3 (2021-Mar-13)
- Allow public access to shared resources (/common)
- Add muninBaseUrl variable to the global R env so it can be referred to in reports.
- Upgrade jquery

### 1.1.2 (2021-Feb-17)
- add docs on how to add external css (two ways, in body or in head using js)
- cleanup some link tags in the headers (use xhtml style)
- Add mdr output to the mdr example in the README.md
- Add restapi for integration with other tools (mainly to be able to add built in
  support for creating/editing Munin reports in Ride).
- convert unmanaged R reports to character vector if it returned something else.
- upgrade dependencies for junit, liquibase, mdr2html, and spotbugs,  htmlcreator, spring-boot, bootstrap

### 1.1.1 (2021-Jan-13)
- Add syntax highlighting for code blocks in mdr reports.
- Fix bug that prevented "normal" syntax highlighted blocks from working properly
- Go to group page instead of back to index after edit report

### 1.1.0 (2021-Jan-08)
- Change to deploy the original jar to central instead of the repackaged one.
- Improve documentation
- Add support for the mdr (markdown with r) format
- Add report groups (folders that go one level deep only)

### 1.0.1 (2020-Dec-26)
Add support for adding/removing external content (e.g. images and css) which can be referenced from
the reports.

### 1.0.0
Basic functionality for admin and view/add/edit/delete/schedule reports.