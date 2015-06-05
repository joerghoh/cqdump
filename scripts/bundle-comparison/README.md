
##Bundle comparison

These scripts are intended to generate a chart which list bundle versions in different AEM instances (versions don't matter). They are written in perl (which is the scripting language I am most familiar with); the only dependency is the JSON package to extract the bundle list from a running AEM instance.

First you need to extract the information from a running instance and create a file with that information.

```
curl -u admin:admin http://localhost:4502/system/console/bundles.json | perl json_extractor.pl > AEM_561
curl -u admin:admin http://localhost:4503/system/console/bundles.json | perl json_extractor.pl > AEM_60
curl -u admin:admin http://localhost:4504/system/console/bundles.json | perl json_extractor.pl > AEM_61
```


then you can generate the chart, which displays all bundle version information niceley next to each other:

```
perl generate_chart.pl AEM_561 AEM_60 AEM_61
```

it writes a raw HTML table directly to standard out.


