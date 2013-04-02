## Description

This is an experiment to see how
[pedestal-service](https://github.com/pedestal/pedestal/tree/master/service)
compares to [sinatra](http://www.sinatrarb.com/). The sinatra app
converted is [previewradio](https://github.com/mnoble/previewradio).

To try this app, see [the demo](http://previewradio-pedestal.herokuapp.com/).

## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/)

## Usage
previewradio is a music discovery service that works like this:

1. Search for an album you know.
2. Click on it.
3. Sit back and enjoy preview clips of related albums via iTunes. When an
album finishes, it should automatically transition to the next one.

## Comparison to Sinatra

A [diff is a thousand words](https://github.com/cldwalker/previewradio-pedestal/commit/229f1904b69bf428668532663c9b23aa13ade119) or less. The original app had 150 lines, 4 endpoints, 3 views and 4 javascript files. The pedestal service ended up:

* functionally equivalent to the sinatra one
* with about the same line count
* changing a few of the templates to accomodate a different erb parser
* not changing any of the javascript or css

This app doesn't play to pedestal-service's strengths but it does show
that it can be an easy to use sinatra equivalent for some use cases.

## Configuration
To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its
[documentation](http://logback.qos.ch/documentation.html).

## Credits

* @mnoble for writing the original app! Perhaps he and others will
convert to the (dark side).
* @thinkrelevance for awesome fridays!
