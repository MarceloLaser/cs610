# cs610

Jars with dependencies included for ease of use.

Example use command for CFG is:

java -jar cs610-CFG.jar csci610.cfg.samples.Subject1 VERYOBVIOUSFILE.dotty ./src/test/resources

Example use command for RDDU is:

java -jar cs610-RDDU.jar csci610.cfg.samples.Subject1 DUFile.txt RDFile.dotty ./src/test/resources

The last parameter should be the path to the first directory in your package structure. .class file must be inside directory structure equal to package name, otherwise soot will not find it. For example, in the example commands above, directory structure is:

src/test/resources/csci610/cfg/samples/Subject1.class

Systems are set to analyze main method only.
