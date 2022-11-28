# cqdump performance project

This project contains code I use in my blog https://cqdump.joerghoh.de; it should be possible for you to run the code on your own to validate my recommendations.

The focus is clearly on the illustration part, there is no focus on proper test coverage.

Related blog articles:
 * Sling Model performance: https://cqdump.joerghoh.de/2022/11/28/sling-model-performance/



## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

To build all the modules and deploy the `all` package to a local instance of AEM, run in the project root directory the following command:

    mvn clean install -PautoInstallSinglePackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallSinglePackagePublish

Or alternatively

    mvn clean install -PautoInstallSinglePackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

Or to deploy only a single content package, run in the sub-module directory (i.e `ui.apps`)

    mvn clean install -PautoInstallPackage


