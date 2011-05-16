# semanticExperiments

This repository contains some code of my experiments with semantic web technologies.

## Software & Tools

The following software and tools are used:

- [Protégé](http://protege.stanford.edu/) - OWL-Editor
- [W3C RDF Validator](http://www.w3.org/RDF/Validator/)
- [rdf:about RDF Validator](http://www.rdfabout.com/demo/validator/)
- [Arduino IDE](http://arduino.cc/en/Main/Software) - IDE for programming arduino µControllers
- [Sesame](http://www.openrdf.org/) - Semanitc Web Framwork for Java
- [Jena](http://jena.sourceforge.net/) - Semanitc Web Framwork for Java
- [rdfQuery](https://github.com/alohaeditor/rdfQuery) - Javascript library for RDF-related processing
- [AgentOWL](http://agentowl.sourceforge.net/) - library for RDF/OWL support in [JADE](http://jade.tilab.com/)
- [Scala](http://www.scala-lang.org/) - Programming language for the JVM
- [sbt](https://github.com/harrah/xsbt) - a build tool for Scala

## Interesting JavaScript libraries

- [js3](https://github.com/webr3/js3) - generates RDF out of JavaScript values and objects
- [Jstle](https://github.com/dnewcome/jstle) - RDF serialization language
- [jOWL](https://code.google.com/p/jowl-plugin/) - a jQuery plugin for processing OWL, MIT, 2009
- [RDFAuthor](https://code.google.com/p/rdfauthor/), GPLv3, 2011
- [rdf-parser](http://www.jibbering.com/rdf-parser/) - a simple RDF parser, 2006
- [hercules](http://hercules.arielworks.net/) - framework for semantic web applications
- [sparql.js](http://www.thefigtrees.net/lee/sw/sparql.js) - JS library for processing SPARQL queries
- [vie.js ](http://bergie.github.com/VIE/) - Library for making RDFa -annotated content on a web pages editable
- [jquery-sparql](https://github.com/jgeldart/jquery-sparql) - a SPARQL jQuery plugin

### Graph visualization

- [moowheel](http://labs.unwieldy.net/moowheel/)
- [Dracula](http://www.graphdracula.net/)
- [InfoVis Toolkit](http://thejit.org/)
- [Protovis](http://vis.stanford.edu/protovis/)
- [arbor.js](http://arborjs.org/)

## Build

For compiling JAR files [sbt](https://github.com/harrah/xsbt) is used.

To install sbt globally on a Linux machine, copy the file `sbt-launch-0.7.4.jar` into
the directory `/usr/local/lib/` and create the file `sbt` in the directory `/usr/bin/`
with the following content:

    java -Xmx1024M -XX:MaxPermSize=512M -jar /usr/local/lib/sbt-launch-0.7.4.jar "$@"

Now make the script executable:

    sudo chmod a+x sbt

### API documentation

**sbt** supports **scaladoc**, so just type `doc` to create the documentation.

## Weblinks

- [RDF-JSON](http://docs.api.talis.com/platform-api/output-types/rdf-json)
- [JQbus](http://svn.foaf-project.org/foaftown/jqbus/intro.html) - XMPP query services
- [SPARQL By Example - A Tutorial](http://www.cambridgesemantics.com/2008/09/sparql-by-example/)
- [Semantic web basics](http://www.linkeddatatools.com/semantic-web-basics)
- [SPARQL implementations](http://www.w3.org/wiki/SparqlImplementations)
- [Jena JavaDoc](http://jena.sourceforge.net/javadoc/index.html)
- [Jena ARQ JavaDoc](http://jena.sourceforge.net/ARQ/javadoc/)
- [Joseki](http://www.joseki.org/) - SPARQL Endpoint for Jena
- [Scala Style Guide](https://github.com/davetron5000/scala-style/)

## Licence

The source code is licenced under the GPLv3.
