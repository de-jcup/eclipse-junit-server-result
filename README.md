### About
This is an additional eclipse JUnit plugin. Project is hosted at https://github.com/de-jcup/eclipse-junit-server-result.

#### Why an additional plugin ?

In one of my projects I had some flaky integration tests and was not able to reproduce the behaviour at my local machine, so I was/am very dependant on server outputs

Eclipse JDT standard JUnit view is very good wheny you are starting your tests inside eclipse. And you are able to import existing junit xml files.
But the imported tests do not contain test console output (system out/err) because normally eclipse does run the tests and shows output inside console view.

**Main reasons for this plugin:**
- Show console output from server tests
- Show corelated server log file parts  
When having failing integration tests, server logs are often also necessary to inspect - in same timeperiod where test happend. When server logs are bigger it is very cumbersome to find the relevant parts manually. So inisde the server result view you can define a logfile location and link it to the results. A doubleclick at the testsuite will calculate the relevant part inside the log file corresponding to the test timestamps and shows up the truncated log parts inside a special editor (read-only).

**Other interesting parts:**
- You can filter testsuites by name  
Asterisk wildcards are also supported.
- It delegates to standard view: a doubleclick shows only one imported testsuite and not all, so standard junit view is not overloaded
- You can delete testsuites inside the "JUnit server result" view. This can be convenient when fixing many tests and deleting fixed ones in server result view - its independant from standard junit view, so you can execute tests local and still have overview about your server problems
- Of course you can use the plugin also for local builds (e.g. maven, gradle, ... whatever - but it must create junit xml output files)
  

### Installation
The plugin will be found at eclipse marketplace (in next future).


