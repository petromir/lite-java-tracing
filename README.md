# Strings

There are case when a very simple "tracing" is needed, i.e. having just a `Correlation-ID` in the log messages, 
by the support of Mapped Diagnostic Context (MDC)
This library covers exactly this need

> [!WARNING]
> Under active development 🚧

## Features


## Requirements

Java 25

## Usage

### Add to your project
```xml
<dependencies>
    <dependency>
        <groupId>com.petromirdzhunev.libs</groupId>
        <artifactId>simple-tracing</artifactId>
    </dependency>
</dependencies>
```

### Code
```java
```

## Development

### Prerequisites
1. [Install SDKMAN](https://sdkman.io/install)
2. Initialize SDKMAN environment
```shell
sdk env install
```
Check [.sdkmanrc](.sdkmanrc) for all the tools installed with this command.

### Build and publish `SNAPSHOT` versions to local repository
Change the version by adding a `-SNAPSHOT` suffix in the [pom.xml](pom.xml) file and then execute:
```shell
 mvnd -B clean install
```

### Pointing to a `SNAPSHOT` version
```xml
<dependencies>
    <dependency>
        <groupId>com.petromirdzhunev.lib</groupId>
        <artifactId>simple-tracing</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Support my work

<a href="https://ko-fi.com/petromirdzhunev" target="_blank"><img src="https://raw.githubusercontent.com/petromir/petromir/refs/heads/master/assets/kofi-button.svg" alt="Buy Me A Ko-fi" style="height: 45px !important;width: 163px !important;" ></a>
<a href="https://www.buymeacoffee.com/petromirdzhunev" target="_blank"><img src="https://raw.githubusercontent.com/petromir/petromir/refs/heads/master/assets/bmc-button.svg" alt="Buy Me A Coffee" style="height: 45px !important;width: 163px !important;" ></a>
<a href="https://github.com/sponsors/petromir" target="_blank"><img src="https://raw.githubusercontent.com/petromir/petromir/refs/heads/master/assets/github-sponsor-button.svg" alt="GitHub Sponsor" style="height: 45px !important;width: 163px !important;" ></a>