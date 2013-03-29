## Description

This is an experiment in porting a straight-forward sinatra app to
pedestal-service. The sinatra app is
[previewradio](https://github.com/mnoble/previewradio).

previewradio is a music discovery service that works like this:

1. Search for an album you know.
2. Click on it.
3. Sit back and enjoy preview clips of albums related to that album via iTunes.

## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/)

## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its
[documentation](http://logback.qos.ch/documentation.html).

## Credits

* @mnoble for writing the original app! Perhaps he and others will
convert to the (dark side).
* @thinkrelevance for awesome fridays!
