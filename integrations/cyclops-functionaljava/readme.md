# Functional Java Integration

v5.0.0 of cyclops-functionaljava and above is built using v4.4 of FunctionalJava.



Use FJ.anyM to create wrapped FunctionalJava Monads.

Pacakage com.aol.cyclops.functionaljava contains converters for types from various functional libraries for Java

* JDK
* Guava
* Javaslang
* jooλ
* simple-react

Supported Functional Java Monads include

* IO
* Either
* Option
* Stream
* List
* State
* Reader
* Writer
* Trampoline
* Validation

These are available in Cyclops Comprehensions, or via Cyclops AnyM.

## Example flatMap a Functional Java List, that returns JdK Optional

```java
	FJ.anyM(List.list("hello world"))
				.map(String::toUpperCase)
				.flatMapOptional(Optional::of)
				.toSequence()
				.toList()
 ```
			
## Get cyclops-functionaljava


* [![Maven Central : cyclops-functionaljava](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-functionaljava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-functionaljava)
* [Javadoc for Cyclops Javaslang](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-functionaljava/5.0.0)
