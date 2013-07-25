HERE IS THE LIST OF ALL DEPENDENCIES TO THE PROJECT.

#### List of Required JAR Files and where they Can be found #####

// SLF4J http://www.slf4j.org/
slf4j-api-1.7.5.jar
slf4j-simple-1.7.5.jar

// Apache Commons Logging http://commons.apache.org/proper/commons-logging/download_logging.cgi
commons-logging.jar

// FREE TTS http://sourceforge.net/projects/freetts/files/
Get all Files 
// Stanford NLP http://nlp.stanford.edu/
stanford-corenlp-2011-09-14-models.jar
stanford-corenlp-2011-09-14.jar
stanford-tregex.jar
xom.jar
joda-time.jar


// Illinois WordNet Similarity http://cogcomp.cs.illinois.edu/page/software_view/Illinois%20WNSim%20(Java)
WNSim.jar
jwnl.jar

### OTHER DEPENDENCIES ###
//WORDNET
The wordNet similarity Module Requires WordNet download and Provide the PATH to in wordnet.xml



### HOW TO ####

1)You can Tweak Configuration Parameters in edu.ncsu.csc.ase.dristi.configuration.Configuration.java
The names of the parameters are self explanatory
2)The entry method is main method in edu.ncsu.csc.ase.dristi.Whyper.java
Before that do set the variable "Permission permission" to required permission enum.
Finally only the first Sentence is considered so provide input sentence by sentence.
