# RSF (Raw Sakai Framework)

[![Maven Build](https://github.com/ern/rsf/actions/workflows/maven.yml/badge.svg)](https://github.com/ern/rsf/actions/workflows/maven.yml)

RSF abstracts over the Sakai dispatch cycle and tool state holders. It is a
component-oriented web application framework originally developed for the
[Sakai](https://www.sakailms.org/) project by CARET, University of Cambridge.

## Requirements

* JDK 17
* Maven 3

## Building

This project combines all the rsf projects into one multimodule Maven build:

```
rsf-base
| + rsf-core-base
| | + rsf-core-ponderutilcore
| | + rsf-core-servletutil
| | + rsf-core
| + rsf-web-base
| | + rsf-web-evolvers
| | + rsf-web-templates
| | + rsf-web-test
| | | + rsf-web-test-webapp
| | | + rsf-web-test-base-webapp
```

Build and test everything with:

```
mvn verify
```

## License

RSF is distributed under the [BSD License](LICENSE.TXT).
