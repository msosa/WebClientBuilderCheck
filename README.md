# WebClientBuilderCheck
This runs on the default port(8080). 

Start it up and call `http://localhost:8080/check` which will throw an error
```
org.springframework.core.codec.CodecException: Type definition error: [simple type, class java.time.OffsetDateTime]; nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.OffsetDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling
```
When it should not because the `ObjectMapper` it is using from `WebClientBuilderCheckApplication` has added the correct module. Adding this module to the `@Primary` bean will allow `WebClient` to properly deserialize the json
