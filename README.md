# ktlint-custom-rules
Additional rules for [KtLint](https://github.com/shyiko/ktlint) - Kotlin linter with build-in formatter

## Idea
Custom rules allow to try new rules, test them and collect feedback. Once rules are polished, they will be pushed to KtLint and, hopefully, included into library.

## Rules

### IndentationRule
Verifies indentatin size.
* more intelligent than same rule from stable version (KtLint v.0.15.1)
* supports autocorrection

### ClassAndFunctionHeaderFormatRule
Verifies class and function header formatting according to https://android.github.io/kotlin-guides/style.html#functions

Example of valid header
```kotlin
fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = ""
): String {
    // …
}
```

That format is required:
* header already takes more than one line
* header is longer than maximum allowed line length

## Usage

See KtLint main page for details [how to integrate KtLint](https://github.com/shyiko/ktlint#integration)

Once KtLint is configured, you have to add custom rules.

**Gradle via custom tasks**
>build.gradle

```gradle
repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
}

dependencies{
    ktlint 'com.github.MyDogTom:ktlint-custom-rules:v0.1.0-RC1'
}
```

**Complete (KtLint + custom rules) Gradle configuration**
>build.gradle

```gradle
apply plugin: "java"

repositories {
    jcenter()
    maven {
      url "https://plugins.gradle.org/m2/"
    }
}

configurations {
    ktlint
}

dependencies {
    ktlint "com.github.shyiko:ktlint:0.15.0"
    // additional 3rd party ruleset(s) can be specified here
    // just add them to the classpath (e.g. ktlint 'groupId:artifactId:version') and 
    // ktlint will pick them up
    ktlint 'com.github.MyDogTom:ktlint-custom-rules:v0.1.0-RC1'
}

task ktlint(type: JavaExec, group: "verification") {
    description = "Check Kotlin code style."
    classpath = configurations.ktlint
    main = "com.github.shyiko.ktlint.Main"
    args "src/**/*.kt"
    // to generate report in checkstyle format prepend following args:
    // "--reporter=plain", "--reporter=checkstyle,output=${buildDir}/ktlint.xml"
    // see https://github.com/shyiko/ktlint#usage for more
}
check.dependsOn ktlint

task ktlintFormat(type: JavaExec, group: "formatting") {
    description = "Fix Kotlin code style deviations."
    classpath = configurations.ktlint
    main = "com.github.shyiko.ktlint.Main"
    args "-F", "src/**/*.kt"
}
```

## Requirements
`ktlint-custom-rules:v0.1.0-RC1` requires `KtLint v0.15.1`

## Feedback

Your feedback is very important. The entire idea of that repository is to run these rules on as many code bases as possible. Find and fix bugs.
