# Module 9: Support Licensing

Open Liberty is Open Source under the Eclipse Public License v1, as a result there is no fee to use in production.  Community support is available via StackOverflow, Gitter, or the mail list, and bugs can be raised in [GitHub](https://github.com/openliberty/open-liberty). Commercial support from IBM is available for Open Liberty, you can find out more on the [IBM Marketplace](https://www.ibm.com/uk-en/marketplace/elite-support-for-open-liberty). The WebSphere Liberty product is built on Open Liberty, there is no migration required to use WebSphere Liberty, you simply point to WebSphere Liberty in your build.  Users of WebSphere Liberty get support for the packaged Open Liberty function.

WebSphere Liberty is also available in Maven Central[https://search.maven.org/search?q=g:com.ibm.websphere.appserver.runtime].

You can use WebSphere Liberty for development even if you haven't purchased it, but if you have production entitlement you can easily change to use it, as follows:

In the `open-liberty-masterclass/start/barista/pom.xml` and `open-liberty-masterclass/start/coffee-shop/pom.xml`, add the `<configuration>...</configuration>` as the following:

```XML
            <plugin>
                <groupId>io.openliberty.tools</groupId>
                <artifactId>liberty-maven-plugin</artifactId>
                <version>3.3.4</version>
                <configuration>
                  <runtimeArtifact>
                      <groupId>com.ibm.websphere.appserver.runtime</groupId>
                      <artifactId>wlp-kernel</artifactId>
                      <version>[21.0.0.4,)</version>
                      <type>zip</type>
                  </runtimeArtifact>
                </configuration>
            </plugin>
```
{: codeblock}

Rebuild and re-start the **barista** service:

```
cd /home/project/open-liberty-masterclass/start/barista
export DEFAULT_HTTP_PORT=9082
mvn clean
mvn liberty:dev
```
{: codeblock}

and the **coffee-shop** service:
```
cd /home/project/open-liberty-masterclass/start/coffee-shop
export DEFAULT_HTTP_PORT=
mvn clean
mvn liberty:dev
```
{: codeblock}

The **barista** service should be started at the port `9082` and the **coffee-shop** service at the port `9080`. 
Then, try the service out using the Open API Web page and you should see the behavior is identical.  Not surprising since the code is identical, from the same build, just built into WebSphere Liberty.

Or, you can run the following curl commands to try out the services running in containers:
```
curl http://localhost:9080/health
curl -X POST "http://localhost:9080/coffee-shop/resources/orders" \
     -H  "accept: */*" -H  "Content-Type: application/json" \
     -d "{\"status\":\"FINISHED\",\"type\":\"ESPRESSO\"}"
curl http://localhost:9080/coffee-shop/resources/orders

```
{: codeblock}

## Conclusion

Thanks for trying the Open Liberty Masterclass. If you're interested in finding out more, please visit the [Open Liberty website](http://openliberty.io), and for more hands-on experience, why not try the [Open Liberty Guides](http://openliberty.io/guides).
