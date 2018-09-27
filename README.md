# Trellis Linked Data Server

A scalable platform for building [linked data](https://www.w3.org/TR/ldp/) applications.

[![Build Status](https://travis-ci.org/trellis-ldp/trellis.png?branch=jpms)](https://travis-ci.org/trellis-ldp/trellis)
[![Build status](https://ci.appveyor.com/api/projects/status/nvdwx442663ib39d/branch/jpms?svg=true)](https://ci.appveyor.com/project/acoburn/trellis/branch/jpms)
[![Coverage Status](https://coveralls.io/repos/github/trellis-ldp/trellis/badge.svg?branch=master)](https://coveralls.io/github/trellis-ldp/trellis?branch=jpms)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/09f8d4ae61764bd9a1fead16514b6db2)](https://www.codacy.com/app/acoburn/trellis?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=trellis-ldp/trellis&amp;utm_campaign=Badge_Grade)
[![Javadoc](https://javadoc-badge.appspot.com/org.trellisldp/trellis-api.svg?label=javadoc)](https://trellis-ldp.github.io/trellis/apidocs/)
[![Maven Central](https://img.shields.io/maven-central/v/org.trellisldp/trellis-api.svg)](https://mvnrepository.com/artifact/org.trellisldp/trellis-api/0.6.0)
[![DOI](https://zenodo.org/badge/77492072.svg)](https://zenodo.org/badge/latestdoi/77492072)

Trellis is a rock-solid, enterprise-ready linked data server.
The quickest way to get started with Trellis is to download the
[latest release](https://www.trellisldp.org/download.html)
and follow the installation instructions.

Trellis is built on existing [Web standards](https://github.com/trellis-ldp/trellis/wiki/Web-Standards).
It is modular, extensible and fast.

  * [Wiki](https://github.com/trellis-ldp/trellis/wiki)
  * [Configuration guide](https://github.com/trellis-ldp/trellis/wiki/Configuration-Guide)
  * [Mailing List](https://groups.google.com/group/trellis-ldp)
  * [API Documentation](https://trellis-ldp.github.io/trellis/apidocs) (JavaDocs)
  * [Website](https://www.trellisldp.org)

All source code is open source and licensed as Apache 2. Contributions are always welcome.

## Building Trellis

In most cases, you won't need to compile Trellis. Released components are available on Maven Central,
and the deployable applicaton can be [downloaded](https://www.trellisldp.org/download.html) directly
from the Trellis website. However, if you want to build the latest snapshot, you will need, at the very least,
to have Java 8+ available. The software can be built with [Gradle](https://gradle.org) using this command:

```
$ ./gradlew install
```

## Related projects

  * [py-ldnlib](https://github.com/trellis-ldp/py-ldnlib) A Python3 library for linked data notifications
  * [static-ldp](https://github.com/trellis-ldp/static-ldp) A PHP application that serves static files as LDP resources
  * [trellis-ui](https://github.com/trellis-ldp/trellis-ui) A JavaScript single page app for managing Trellis
  * [trellis-ext-db](https://github.com/trellis-ldp/trellis-ext-db) A Trellis application using a relational database for a persistence layer
  * [camel-ldp-recipes](https://github.com/trellis-ldp/camel-ldp-recipes) Integration workflows built with [Apache Camel](https://camel.apache.org)

