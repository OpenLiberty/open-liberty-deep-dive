# Module 9: Support Licensing

Open Liberty is Open Source under the Eclipse Public License v1, as a result there is no fee to use in production.  Community support is available via StackOverflow, Gitter, or the mail list, and bugs can be raised in [github](https://github.com/openliberty/open-liberty). Commercial support from IBM is available for Open Liberty, you can find out more on the [IBM Marketplace](https://www.ibm.com/uk-en/marketplace/elite-support-for-open-liberty). The WebSphere Liberty product is built on Open Liberty, there is no migration required to use WebSphere Liberty, you simply point to WebSphere Liberty in your build.  Users of WebSphere Liberty get support for the packaged Open Liberty function.

WebSphere Liberty is also available in Maven Central[https://search.maven.org/search?q=g:com.ibm.websphere.appserver.runtime].

You can use WebSphere Liberty for development even if you haven't purchased it, but if you have production entitlement you can easily change to use it, as follows:

In the `open-liberty-masterclass/start/coffee-shop/pom.xml` change these two lines from:

```XML
    <groupId>io.openliberty</groupId>
    <artifactId>openliberty-kernel</artifactId>
```
To:
```XML
    <groupId>com.ibm.websphere.appserver.runtime</groupId>
    <artifactId>wlp-kernel</artifactId>
```
{: codeblock}

Rebuild and re-start the `coffee-shop` service:

```
mvn install liberty:run
```
{: codeblock}


Try the service out using the Open API Web page and you should see the behavior is identical.  Not surprising since the code is identical, from the same build, just built into WebSphere Liberty.

## Conclusion

Thanks for trying the Open Liberty Masterclass. If you're interested in finding out more, please visit the [Open Liberty website](http://openliberty.io), and for more hands-on experience, why not try the [Open Liberty Guides](http://openliberty.io/guides).