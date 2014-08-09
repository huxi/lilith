# TODO
This file contains a rough list about stuff that needs to be done.

## Bugs
- Conditions-Focus/Exclude menu of detached windows not updated on saved condition change.

## Bundling
It's critical that Lilith switches from the old Java6-Mac bundling to the proper new one with embedded VM. This prevents a switch to Java8 and fancy stuff like JavaFX (especially [WebView](http://docs.oracle.com/javafx/2/api/javafx/scene/web/WebView.html) & [WebEngine](http://docs.oracle.com/javafx/2/api/javafx/scene/web/WebEngine.html))

### macappbundle
- [Gradle Plugin Homepage](https://code.google.com/p/gradle-macappbundle/)
- [MacAppBundlePluginExtension](https://code.google.com/p/gradle-macappbundle/source/browse/src/main/groovy/edu/sc/seis/gradle/macAppBundle/MacAppBundlePluginExtension.groovy)
- Artifact: `'edu.sc.seis.gradle:macappbundle:2.0.0'`

### izpack
- [Homepage](http://izpack.org/)
- [gradle-izpack-plugin](https://github.com/bmuschko/gradle-izpack-plugin)

### launch4j 

## Deployment

- [Enjoy Bintray and use it as pain-free gateway to Maven Central](http://blog.bintray.com/2014/02/11/bintray-as-pain-free-gateway-to-maven-central/)


## Enhancements
- Replace SimpleDateFormat with Joda.
- Use Woodstox for all things StaX.
- Cleanup of duplicated code in actions, e.g. the new layout actions.
- Ability to add/remove/configure event receivers
- Plugin interfaces (EventSender (e.g. Bonjour etc.), EventReceiver, EventHandler (?, stuff like the fart on error, RRD statistics, anything that's working directly on received events), GoToSource).
- Option to left-align Thread name in table (Suggested by Lilianne)
- Option to left-align Logger name in table (Suggested by Lilianne)
- Option to show full Logger name in table (Suggested by Lilianne)
- add SSL option to multiplexers
- Send message (not event) over Bonjour, i.e. a simple IM.
- Mac: Bouncing icon in case of error
- Preferences for Toolbar (text, icon size)
- SVG Icons
- Create logger name index file from existing lilith-file for *sigh* tree-view *yawn*
- add ip.ip.ip.ip:port (and something similar for IP6) to multiplexer receiver list.
