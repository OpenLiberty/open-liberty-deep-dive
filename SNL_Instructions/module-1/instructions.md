# Open Liberty Masterclass

These instructions contain the hands-on lab modules for the Open Liberty Masterclass.  It is intended to be used in conjunction with taught materials, however, feel free to give it a try, even if you're not in a Masterclass.

We strongly recommend using Google Chrome for the purpose of these labs.

## The Application

To open a new command-line session, select **Terminal** > **New Terminal** from the menu of the IDE.

Clone the following repo:
```
git clone https://github.com/OpenLiberty/open-liberty-masterclass.git
```
{: codeblock}


Now navigate into the project folder:

```
cd open-liberty-masterclass
```
{: codeblock}

The application consists of two Microservices; **coffee-shop** and **barista**.  The **coffee-shop** service allows you to place an order and the **barista** service services the making of the coffee.

```
    ^|
    || orderCoffee()
    || 
    || 
┌───|v────────┐   startCoffeeBrew()   ┌─────────────┐
│ coffee-shop │---------------------->│   barista   │
└─────────────┘<----------------------└─────────────┘
```
The completed code for the Masterclass is provided in the `open-liberty-masterclass/finish` directory.  To work through the Masterclass, you will develop in the `open-liberty-masterclass/start` directory.

## Module 1: Build

Liberty has support for building and deploying applications using Maven and Gradle.  The source and documentation for these plugins can be found here:
* https://github.com/OpenLiberty/ci.maven
* https://github.com/OpenLiberty/ci.gradle

The Masterclass will make use of the `liberty-maven-plugin`.

Take a look at the Maven build file for the coffee-shop project: `open-liberty-masterclass/start/barista/pom.xml`

Go to the barista project:

```
cd start/barista
```
{: codeblock}

Build and run the barista service:

```
mvn liberty:dev
```
{: codeblock}

# Next Steps

Congratulations on completing your first exercise. Don't stop now. Move on to the next module in the master class by simply closing this tab and clicking on the next module in the Open Liberty Masterclass landing page.
