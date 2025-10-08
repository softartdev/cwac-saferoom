# CWAC-SafeRoom: Room ↔ SQLCipher for Android Bridge

[![Maven Central](https://img.shields.io/maven-central/v/io.github.softartdev/saferoom.x.svg)](https://central.sonatype.com/artifact/io.github.softartdev/saferoom.x)
[![Build CI](https://github.com/softartdev/cwac-saferoom/actions/workflows/build_only.yml/badge.svg)](https://github.com/softartdev/cwac-saferoom/actions/workflows/build_only.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

> **📢 Note**: This is a maintained fork of the original [CWAC-SafeRoom](https://github.com/commonsguy/cwac-saferoom) library, which is no longer actively maintained. This fork has been updated to work with the new [SQLCipher for Android](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/) API (`net.zetetic:sqlcipher-android`) that replaced the older `android-database-sqlcipher` library.

This project implements the `SupportSQLite...` series of classes and interfaces
that [Room](https://developer.android.com/topic/libraries/architecture/room.html)
can use for working with a particular edition of SQLite. Specifically, this
project's classes connect Room
with [SQLCipher for Android](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/),
a version of SQLite that offers transparent encryption of its contents.

## 📦 Installation

### Maven Central

Add the dependency to your app's `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.softartdev:saferoom.x:1.3.1'
}
```

### Maven Local (for testing)

To use a locally published version:

```bash
./gradlew :saferoom:publishToMavenLocal
```

Then add `mavenLocal()` to your repositories.

## 🚀 Quick Start

See [the original README](doc/README-original.markdown) for usage instructions and examples.

## 📚 Documentation

> **📖 [View Full Documentation Index](./doc/INDEX.md)**

### For Users

- **[Usage Guide](doc/README-original.markdown)** - How to use SafeRoom with Room and SQLCipher
- **[Version History](./doc/README-1.0.0.md)** - Release notes for version 1.0.0

### For Contributors & Publishers

- **[Summary](./doc/SUMMARY.md)** ⭐ - Complete overview of the Maven Central publishing setup
- **[Publishing Guide](./doc/PUBLISHING.md)** - How to publish to Maven Central
- **[Maven Central Setup](./doc/MAVEN_CENTRAL_SETUP.md)** - Detailed Maven Central configuration
- **[GitHub Actions Setup](./doc/GITHUB_ACTIONS_SETUP.md)** - CI/CD configuration and workflows
- **[Contributing Guidelines](./doc/CONTRIBUTING.md)** - How to contribute to this project
- **[Support](./doc/SUPPORT.md)** - How to get help

## 🔄 SQLCipher Migration

This library has been updated to use the new **SQLCipher for Android** (`net.zetetic:sqlcipher-android:4.10.0`) API, which replaced the older `android-database-sqlcipher` library in 2022.

### Why This Fork?

- **Original library no longer maintained**: The [original CWAC-SafeRoom](https://github.com/commonsguy/cwac-saferoom) is retired
- **SQLCipher API migration**: SQLCipher for Android migrated from `android-database-sqlcipher` to `sqlcipher-android` with significant API changes
- **Continued support needed**: Existing projects using Room + SQLCipher still need this integration layer

### About SQLCipher for Android

SQLCipher for Android now has its own implementation of the `SupportSQLite...` classes and interfaces. However, SafeRoom continues to provide value for:

- Projects that need a stable, tested integration layer
- Migration from older SQLCipher implementations
- Compatibility with existing codebases

### Resources

- **[SQLCipher for Android Migration Guide](https://www.zetetic.net/sqlcipher/sqlcipher-for-android-migration/)** - Official migration guide from `android-database-sqlcipher` to `sqlcipher-android`
- **[SQLCipher for Android Documentation](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/)** - Complete integration guide
- **[SQLCipher GitHub](https://github.com/sqlcipher/android-database-sqlcipher)** - Source code and examples
- **[SQLCipher Support Forum](https://discuss.zetetic.net/c/sqlcipher)** - Community support

## 🔗 Links

- **Maven Central**: https://central.sonatype.com/artifact/io.github.softartdev/saferoom.x
- **GitHub Repository**: https://github.com/softartdev/cwac-saferoom
- **Original Repository**: https://github.com/commonsguy/cwac-saferoom
- **Issues**: https://github.com/softartdev/cwac-saferoom/issues
