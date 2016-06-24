# geoportal-server-catalog
As part of the evolution of Geoportal Server, the catalog capability has been separated into its own module. This because there are use cases where the catalog can be used without the need for harvesting or even metadata editing. 

This repository thus contains the catalog capability of Geoportal Server, while it's sibling [geoportal-server-harvester](https://github.com/ArcGIS/geoportal-server-harvester) is the new harvester of Geoportal Server.

## Releases and Downloads
- 2.0.1 - released June 24, 2016, click [here](https://github.com/ArcGIS/geoportal-server-catalog/releases) for release notes and downloads.
- 2.0.0 - released May 5, 2016, initial release of geoportal-server-catalog. Click [here](https://github.com/ArcGIS/geoportal-server-catalog/releases) for release notes and downloads.


## Features
* Faceted Search - Configure different facets to allow your user to filter from the hay stack to the needle
* Scalability - Thank you elasticsearch for providing multi-node configuration support
* OGC CSW 3.0.0 - Standards compliant catalog service interface
* Many metadata formats - Extend the configuration with your favorite XML format
* Built-in Viewer - Use the app we include or build one using Web AppBuilder and hook it up!
* No more database - Yes that's a feature!

## Requirements

* Elasticsearch 2.3.3 or higher
* Tomcat 8

## Installation
- See the [installation instructions](https://github.com/Esri/geoportal-server-catalog/wiki/Installation) on the wiki.

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an [issue](https://github.com/ArcGIS/geoportal-server-catalog/issues).

## Documentation
- Please refer to the [Wiki](https://github.com/ArcGIS/geoportal-server-catalog/wiki).


## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).


## Licensing
Copyright 2016 Esri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

A copy of the license is available in the repository's [license](https://github.com/ArcGIS/geoportal-server-catalog/blob/master/LICENSE.txt) file.

[](Esri Tags: Geoportal Server)
[](Esri Language: CSW)
[](Esri Language: Metadata)

