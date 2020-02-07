# JSONSystem
Save your plugin's files as a JSON with ease!

# How to use
This is an example POM that uses JSON System.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.birthdates</groupId>
    <artifactId>JSONSystemTest</artifactId>
    <version>1.0-SNAPSHOT</version>

    <repositories>
        <repository>
            <id>jsonsystem-repo</id>
            <url>https://raw.githubusercontent.com/birthdates/JSONSystem/repository/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.birthdates</groupId>
            <artifactId>JSONSystem</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</project>
```
