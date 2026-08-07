# Lite Java Tracing

There are cases when a very lite "tracing" is needed, i.e. having just a `Correlation-ID` in the log messages, 
by the support of Mapped Diagnostic Context (MDC)
This library covers exactly this need.

> [!WARNING]
> Under active development 🚧

## Features

- Per-thread `Correlation-ID` context (`LiteTracingContext`)
- `LiteTracedRunnable` propagating the context to another thread
- `LiteTracedThreadFactory` wrapping tasks in `LiteTracedRunnable`, with support for platform and virtual threads
- `new LiteTracedExecutorService(...)` for per-task propagation on pooled executors

### Choosing the right mechanism

| Scenario                                    | Use                                       |
|---------------------------------------------|-------------------------------------------|
| Virtual threads / thread-per-task           | `LiteTracedThreadFactory.ofVirtual()`     |
| Pooled executor, varying submitter contexts | `new LiteTracedExecutorService(executor)` |
| Single manual thread                        | `LiteTracedRunnable`                      |

## Requirements

Java 25

## Usage

### Add to your project
```xml
<dependencies>
    <dependency>
        <groupId>com.petromirdzhunev.libs</groupId>
        <artifactId>lite-tracing</artifactId>
    </dependency>
</dependencies>
```

### Code
```java
LiteTracingContext.set("correlation-id-123");

// Virtual threads
ExecutorService executor = Executors.newThreadPerTaskExecutor(LiteTracedThreadFactory.ofVirtual());

// Platform threads
ExecutorService pool = Executors.newCachedThreadPool(LiteTracedThreadFactory.ofPlatform());

// Pooled executor with per-task propagation (id captured at submission time)
ExecutorService traced = new LiteTracedExecutorService(Executors.newCachedThreadPool());
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
        <artifactId>lite-tracing</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Support my work

<a href="https://ko-fi.com/petromirdzhunev" target="_blank"><img src="https://raw.githubusercontent.com/petromir/petromir/refs/heads/master/assets/kofi-button.svg" alt="Buy Me A Ko-fi" style="height: 45px !important;width: 163px !important;" ></a>
<a href="https://www.buymeacoffee.com/petromirdzhunev" target="_blank"><img src="https://raw.githubusercontent.com/petromir/petromir/refs/heads/master/assets/bmc-button.svg" alt="Buy Me A Coffee" style="height: 45px !important;width: 163px !important;" ></a>
<a href="https://github.com/sponsors/petromir" target="_blank"><img src="https://raw.githubusercontent.com/petromir/petromir/refs/heads/master/assets/github-sponsor-button.svg" alt="GitHub Sponsor" style="height: 45px !important;width: 163px !important;" ></a>