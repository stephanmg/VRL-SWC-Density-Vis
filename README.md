VRL-SWC-Density-Vis
===================
A [VRL Studio](https://github.com/VRL-Studio/VRL-Studio) plugin for the density visualization and subsequent analysis of stacks of neuronal morphologies specified in the common SWC file format (cf. [*neuromorpho database*](http://neuromorpho.org)) compatible with e. g. Neurolucida, NEURON or GENESIS as well as XML-derived and ASC-derived files from Neurolucida. In addition one can merge different densities of various cell types as well as visualize the underlying *consensus* geometry. By extending the existing [VRL-Density-Vis](https://github.com/NeuroBox3D/VRL-Density-Vis) plugin for the matter in hand, the [VRL-SWC-Density-Vis](https://github.com/stephanmg/VRL-SWC-Density-Vis) plugin integrates flawless within the novel [NeuroBox](http://neurobox.eu/) *3D* toolbox for detailed simulations and analysis in *Computational Neuroscience* (compare the organization [NeuroBox3D](https://github.com/NeuroBox3D)). For a specification of the SWC file format cf. [NeuroLand](http://www.neuronland.org/NLMorphologyConverter/MorphologyFormats/SWC/Spec.html). The extension QuickHull3D was kindly provided by [John Llyod](https://www.cs.ubc.ca/~lloyd/java/quickhull3d.html). Visualization is based on the Java3D library.


## Development

### CI
* [![CI Build Status](https://travis-ci.org/stephanmg/VRL-SWC-Density-Vis.svg?branch=master)](https://travis-ci.org/stephanmg/VRL-SWC-Density-Vis)
[![CI Build Status](https://travis-ci.org/stephanmg/VRL-SWC-Density-Vis.svg?branch=devel)](https://travis-ci.org/stephanmg/VRL-SWC-Density-Vis)
[![CI Coverage Status](https://coveralls.io/repos/stephanmg/VRL-SWC-Density-Vis/badge.png)](https://coveralls.io/r/stephanmg/VRL-SWC-Density-Vis)

### Code metrics
* [![Codacy Badge](https://api.codacy.com/project/badge/grade/1363909ef6d445e7aa758f3b56fa6da5)](https://www.codacy.com/app/stephan_5/VRL-SWC-Density-Vis)

### License
* [![LGPLv3](https://img.shields.io/badge/license-LGPLv3-blue.svg)](./README.md)


## Installation

### Prerequisites
- the build automation system [Gradle](http://www.gradle.org/) 
- a working internet connection

### Build
Open project in your favorite IDE with Gradle support, or use the Gradle command line tools and build.

## Example
![](/resources/img/sample.png)
