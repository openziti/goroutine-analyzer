# goroutine-analyzer

This is GUI tool for helping analyze golang goroutine dumps. It lets you organize dumps by grouping sets of similar goroutines (inspired by: https://github.com/linuxerwang/goroutine-inspect). It also lets you group goroutines by using regular expressions. Muliple dumps can be viewed at once, though there aren't any tools yet to diff dumps.

The tool is written in Java for expediency on the UI front and was also inspired by TDA (thread dump analyzer). 

To run, download and execute `./gradlew run`

## Thank you
This tool was inspired by 

* TDA (thread dump analyzer). I tried to find a link to this, but was unable to find the original. Ping me if you've got a link
* [goroutine-inspect](https://github.com/linuxerwang/goroutine-inspect)

The build was helped along by [this very helpful example repo from github user sualeh](https://github.com/sualeh/build-jpackage)
