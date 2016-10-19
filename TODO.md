# TODO
This file contains a rough list about stuff that should probably be done.

## HTML-View
Use [WebView](http://docs.oracle.com/javafx/2/api/javafx/scene/web/WebView.html) & [WebEngine](http://docs.oracle.com/javafx/2/api/javafx/scene/web/WebEngine.html) instead of FS.

### izpack
- [Homepage](http://izpack.org/)
- [gradle-izpack-plugin](https://github.com/bmuschko/gradle-izpack-plugin)

### launch4j 

## Deployment

- [Enjoy Bintray and use it as pain-free gateway to Maven Central](http://blog.bintray.com/2014/02/11/bintray-as-pain-free-gateway-to-maven-central/)


## Enhancements
- Ability to add/remove/configure event receivers
- Plugin interfaces (EventSender (e.g. Bonjour etc.), EventReceiver, EventHandler (?, stuff like the fart on error, RRD statistics, anything that's working directly on received events), GoToSource).
- Option to show full Logger name in table (Suggested by Lilianne)

### Network
- add SSL option to multiplexers
- add ip.ip.ip.ip:port (and something similar for IP6) to multiplexer receiver list.
- [Netty](http://netty.io/)
- [Disruptor](https://lmax-exchange.github.io/disruptor/)

### Misc
- Send message (not event) over Bonjour, i.e. a simple IM.
- Mac: Bouncing icon in case of error
- Preferences for Toolbar (text, icon size)
- SVG Icons
