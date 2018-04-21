# Notes

## How to use generate REST document code (HTML) in MacOS Sierra 10.12.6

- Install analyze-jaxrs ( pom.xml ) 

```
<plugin>
    <groupId>com.sebastian-daschner</groupId>
    <artifactId>jaxrs-analyzer-maven-plugin</artifactId>
    <version>0.9</version>
    <executions>
        <execution>
            <goals>
                <goal>analyze-jaxrs</goal>
            </goals>
            <configuration>
                <!-- Available backends are plaintext (default), swagger and asciidoc -->
                <backend>plaintext</backend>
            </configuration>
        </execution>
    </executions>
</plugin>
```

- Install Swagger ( terminal )

```
brew install swagger-codegen
```

- Generate target/jaxrs-analyser/swagger.json ( terminal or Eclispe)

```
mvn install
```

- Generate the doc code:

```
mkdir WebContent/doc;
cd WebContent/doc;
swagger-codegen generate -i /Users/iuri/git/StarWarsDataService/target/jaxrs-analyzer/swagger.json -l html
```

- Access the doc:

[http://localhost:8080/StarWarsDataService/doc](http://localhost:8080/StarWarsDataService/doc)




