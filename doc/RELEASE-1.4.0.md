# Release Notes - Version 1.4.0

**Release Date**: October 8, 2025  
**Maven Coordinates**: `io.github.softartdev:saferoom.x:1.4.0`  
**Repository**: https://repo1.maven.org/maven2/io/github/softartdev/saferoom.x/1.4.0/

---

## 🎉 What's New in 1.4.0

### 🔄 Migration to New Maven Coordinates

This is the first official release under the new Maven Central coordinates after forking from the retired original CWAC-SafeRoom library.

**Previous (Deprecated):**
```gradle
implementation 'com.commonsware.cwac:saferoom.x:1.3.0'
```

**New:**
```gradle
implementation 'io.github.softartdev:saferoom.x:1.4.0'
```

### 🔧 SQLCipher Migration

- **Updated to SQLCipher for Android 4.10.0** - Migrated from the deprecated `android-database-sqlcipher` to the new `net.zetetic:sqlcipher-android` API
- **Package changes**: Updated to use `net.zetetic.database.sqlcipher` packages
- Full compatibility maintained with existing Room implementations

### 📦 Publishing Infrastructure

- **New publisher**: Published by [softartdev](https://github.com/softartdev)
- **Automated CI/CD**: GitHub Actions workflow for automatic publishing on tag push

---

## ✅ Code Quality Improvements

### 🔍 Static Analysis Fixes

All inspection issues from the codebase have been resolved:

- **Resource management**: Converted to try-with-resources patterns
- **Nullability checks**: Added proper null checks for exception messages
- **Code duplication**: Refactored repeated pragma checks into helper methods
- **Immutability**: Made fields final where appropriate
- **Warnings suppressed**: Added appropriate suppressions for false positives and intentional patterns

### 📝 Documentation

- **Updated Javadoc**: Added missing parameter descriptions
- **Inline comments**: Improved code documentation
- **Lint suppressions**: Properly documented with `//noinspection` comments

---

## 🏗️ Build Configuration

### Maven Central Publishing

- **Plugin**: Vanniktech Maven Publish Plugin 0.30.0
- **Target**: Maven Central Portal (central.sonatype.com)
- **Signing**: Automatic GPG signing with in-memory keys
- **Workarounds**: Implemented fix for plugin build service cleanup issue ([#1116](https://github.com/vanniktech/gradle-maven-publish-plugin/issues/1116))

### Build Environment

- **Min SDK**: 23 (Android 6.0)
- **Target SDK**: 36 (Android 14+)
- **Compile SDK**: 36
- **Java Version**: 17
- **Kotlin Version**: 2.2.20
- **Android Gradle Plugin**: 8.13.0

---

## 📦 Dependencies

### Runtime Dependencies

- **SQLCipher for Android**: `net.zetetic:sqlcipher-android:4.10.0`
- **AndroidX SQLite**: `androidx.sqlite:sqlite:2.6.1`

### Test Dependencies

- **Room**: 2.8.1
- **AndroidX Test**: Latest stable versions
- **JUnit**: 4.13.2

---

## 📋 Migration Guide

### From 1.3.0 (com.commonsware.cwac) to 1.4.0 (io.github.softartdev)

**1. Update Maven coordinates:**

```diff
dependencies {
-   implementation 'com.commonsware.cwac:saferoom.x:1.3.0'
+   implementation 'io.github.softartdev:saferoom.x:1.4.0'
}
```

**2. No code changes required!**

The API remains 100% compatible. All classes and methods work exactly the same.

**3. Sync your project:**

```bash
./gradlew clean build
```

That's it! Your project now uses the maintained fork with the latest SQLCipher API.

---

## 🐛 Bug Fixes

- Fixed SQL syntax inspection false positives in `SQLCipherUtils.java`
- Fixed resource leak warnings with proper try-with-resources usage
- Fixed nullability warnings in error handling
- Fixed deprecation warnings in demo app (Kotlin)
- Fixed file operation handling with proper return value checks

---

## 🎯 What's Different from 1.3.0

### Breaking Changes
- **None!** The API is fully backward compatible

### New Features
- ✅ Published to Maven Central under new coordinates
- ✅ Updated to SQLCipher for Android 4.10.0
- ✅ Modern Gradle publishing configuration
- ✅ Automated CI/CD with GitHub Actions
- ✅ GPG-signed artifacts for security

### Improvements
- ✅ All code quality issues resolved
- ✅ Improved documentation
- ✅ Better build configuration
- ✅ Simplified secret management for CI/CD

---

## 📚 Documentation

Complete documentation is available in the `doc/` directory:

- **[Publication Fix](doc/PUBLICATION_FIX.md)** - CI/CD signing configuration details
- **[Publishing Guide](doc/PUBLISHING.md)** - How to publish updates
- **[Maven Central Setup](doc/MAVEN_CENTRAL_SETUP.md)** - Complete publishing setup
- **[GitHub Actions Setup](doc/GITHUB_ACTIONS_SETUP.md)** - CI/CD workflow configuration
- **[Contributing](doc/CONTRIBUTING.md)** - How to contribute

---

## 🔗 Links

- **Maven Central**: https://central.sonatype.com/artifact/io.github.softartdev/saferoom.x/1.4.0
- **Maven Repository**: https://repo1.maven.org/maven2/io/github/softartdev/saferoom.x/1.4.0/
- **GitHub Release**: https://github.com/softartdev/cwac-saferoom/releases/tag/1.4.0
- **Changelog**: https://github.com/softartdev/cwac-saferoom/compare/1.3.0...1.4.0

---

## 👥 Credits

### Original Author
- **Mark Murphy** (commonsguy) - Original CWAC-SafeRoom implementation

### Maintainer
- **Artur Babichev** (softartdev) - Fork maintenance, SQLCipher migration, Maven Central publication

---

## 🙏 Acknowledgments

- Thanks to the original CWAC-SafeRoom project for the foundation
- Thanks to [Zetetic LLC](https://www.zetetic.net/) for SQLCipher for Android
- Thanks to the Room and AndroidX teams at Google

---

## 📄 License

Apache License 2.0

Copyright 2017-2025 CommonsWare, LLC  
Copyright 2025 Artur Babichev

See [LICENSE](../LICENSE) file for details.

---

## 🚀 Next Steps

After updating to 1.4.0:

1. **Test your app** thoroughly with the new version
2. **Report issues** at https://github.com/softartdev/cwac-saferoom/issues
3. **Star the repo** if you find it useful!
4. **Check for updates** using the Maven Central badge in the README

---

**Status**: ✅ Released  
**Quality**: Production-ready  
**Tested**: Full test suite passing  
**Signed**: All artifacts GPG-signed

