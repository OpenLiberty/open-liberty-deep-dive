# Open Liberty Masterclass

These instructions contains the hands-on lab modules for the Open Liberty Masterclass.  It is intended to be used in conjunction with taught materials, however, feel free to give it a try, even if you're not in a Masterclass.

## The Application

Clone the following repo:

```
git clone https://github.com/OpenLiberty/open-liberty-masterclass.git
```
{: codeblock}


Now nvaigate into the project folder:

```
cd open-liberty-masterclass
```
{: codeblock}

The application consists of two Microservices; `coffee-shop` and `barista`.  The `coffee-shop` service allows you to place an order and the `barista` service services the making of the coffee.

```
    ^|
    || orderCoffee()
    || 
    || 
┌───|v────────┐   startCoffeeBrew()   ┌─────────────┐
│ coffee-shop │---------------------->│   barista   │
└─────────────┘<----------------------└─────────────┘
```
The completed code for the Masterclass is provided in the `open-liberty-masterclass/finish` directory.  To work through the Masterclass you will develop in the `open-liberty-masterclass/start` directory.

## Module 1: Build

Liberty has support for building and deploying applications using Maven and Gradle.  The source and documentation for these plugins can be found here:
* https://github.com/wasdev/ci.maven
* https://github.com/wasdev/ci.gradle

The Masterclass will make use of the `liberty-maven-plugin`.

Take a look at the maven build file for the coffee-shop project: `open-liberty-masterclass/start/barista/pom.xml`

Go to the barista project:

```
cd open-liberty-masterclass/start/barista
```
{: codeblock}

Build and run the barista service:

```
mvn install liberty:run
```
{: codeblock}

Visit: http://localhost:9081/openapi/ui by clicking the `launch application` tab above the IDE and type in the port 9081 to open a proxy to your web browser.

This page is an OpenAPI UI that lets you try out the barista service.  

Click on `POST` and then `Try it out`

Under `Example Value` specify:

```JSON
{
  "type": "ESPRESSO"
}
```
{: codeblock}

Click on `Execute`

Scroll down and you should see the server response code of `201`.  This says that the barista request to make an `ESPRESSO` was successfully `Created`.

# Next Steps

Congratulations on completing your first excercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.