# Maven Central Publishing Setup Complete

## ✅ Configuration Summary

The CWAC SafeRoom library has been successfully configured for publication to Maven Central with the new coordinates:

**New Maven Coordinates:**
```gradle
implementation 'io.github.softartdev:saferoom.x:1.3.1'
```

**Previous Coordinates (Deprecated):**
```gradle
implementation 'com.commonsware.cwac:saferoom.x:1.3.0'
```

---

## 📦 What Was Changed

### 1. **gradle.properties**
Updated Maven coordinates and added POM metadata:
- `PUBLISH_GROUP_ID=io.github.softartdev`
- `PUBLISH_ARTIFACT_ID=saferoom.x`
- `PUBLISH_VERSION=1.3.1`
- Added POM description, URLs, license info, developer info, and SCM details

### 2. **Root build.gradle**
- Added `com.vanniktech:gradle-maven-publish-plugin:0.30.0` to buildscript dependencies

### 3. **saferoom/build.gradle**
- Converted to `plugins` DSL
- Applied `com.vanniktech.maven.publish` plugin
- Configured `mavenPublishing` block with:
  - Maven Central portal as target
  - Conditional signing (enabled for Maven Central, disabled for local testing)
  - Android Single Variant Library configuration (release variant)
  - Complete POM configuration with licenses, developers, and SCM info
- Removed old custom publishing configuration

### 4. **GitHub Actions Workflows**

#### `.github/workflows/build_only.yml`
- Updated job name to "Build & Test"
- Changed JDK distribution to 'temurin'
- Added explicit chmod +x for gradlew
- Added pull request branches: master, main, local
- Improved step naming

#### `.github/workflows/build_publish.yml`
- Renamed job to "Build -> Test -> Publish to Maven Central"
- Changed JDK distribution to 'temurin'
- Added explicit chmod +x for gradlew
- Updated publish command to `publishAndReleaseToMavenCentral`
- Added environment variables for Maven Central credentials:
  - `ORG_GRADLE_PROJECT_mavenCentralUsername`
  - `ORG_GRADLE_PROJECT_mavenCentralPassword`
  - `ORG_GRADLE_PROJECT_signingInMemoryKey`
  - `ORG_GRADLE_PROJECT_signingInMemoryKeyId`
  - `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword`

### 5. **PUBLISHING.md**
- Updated all references to new Maven coordinates
- Added Maven Central publishing instructions
- Updated troubleshooting section
- Added GitHub Actions workflow documentation

### 6. **Signing Configuration**
- GPG key (`8257B447.gpg`) is already configured
- `local.properties` contains signing credentials
- Signing is automatically handled by the Vanniktech plugin
- Signing is disabled for `publishToMavenLocal` to allow local testing

---

## 🚀 Publishing Commands

### Local Testing (No Signing Required)
```bash
./gradlew :saferoom:publishToMavenLocal
```

This publishes to: `~/.m2/repository/io/github/softartdev/saferoom.x/1.3.1/`

### Publish to Maven Central (Manual)
```bash
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
```

### Publish via GitHub Actions (Recommended)
```bash
git tag 1.3.1
git push origin 1.3.1
```

The GitHub Actions workflow will automatically:
1. Build the library
2. Run all tests
3. Sign artifacts with GPG
4. Publish to Maven Central
5. Release to public repositories

---

## 📋 Published Artifacts

Each publication includes:
- **saferoom.x-1.3.1.aar** (~20KB) - The main Android library
- **saferoom.x-1.3.1.pom** (~2.4KB) - Maven POM with dependencies
- **saferoom.x-1.3.1-sources.jar** (~14KB) - Source code
- **saferoom.x-1.3.1-javadoc.jar** (~285KB) - JavaDoc documentation (generated with Dokka)
- **saferoom.x-1.3.1.module** (~4KB) - Gradle module metadata

### Dependencies (Automatically Included)
- `net.zetetic:sqlcipher-android:4.10.0`
- `androidx.sqlite:sqlite:2.6.1`

---

## 🔐 GitHub Secrets Required

For GitHub Actions to publish to Maven Central, you only need to configure **ONE** repository secret:

| Secret Name | Description |
|------------|-------------|
| `LARGE_SECRET_PASSPHRASE` | Passphrase to decrypt the encrypted `local.properties` and GPG key files |

All other credentials are automatically loaded from the decrypted `local.properties` file:
- `signing.keyId=8257B447`
- `signing.password=softartdev_sonatype_2021`
- `signing.secretKeyRingFile=8257B447.gpg`
- `mavenCentralUsername=W3Yi6N`
- `mavenCentralPassword=nyECjYbRutbBGodN3KeSVWuaaIVBipZBX`

---

## ✅ Verification

### Local Build Test ✅
```bash
./gradlew clean build
# BUILD SUCCESSFUL in 40s
# 191 actionable tasks: 191 executed
```

### Local Publishing Test ✅
```bash
./gradlew :saferoom:publishToMavenLocal
# BUILD SUCCESSFUL in 628ms
# Published to ~/.m2/repository/io/github/softartdev/saferoom.x/1.3.1/
```

### POM Verification ✅
- Correct groupId: `io.github.softartdev`
- Correct artifactId: `saferoom.x`
- Correct version: `1.3.1`
- All dependencies included
- License and developer info present
- SCM URLs correct

---

## 📝 Migration Guide for Users

### For Projects Using the Old Version

**Before:**
```gradle
dependencies {
    implementation 'com.commonsware.cwac:saferoom.x:1.3.0'
}
```

**After:**
```gradle
dependencies {
    implementation 'io.github.softartdev:saferoom.x:1.3.1'
}
```

No code changes required - the API remains the same!

---

## 🔗 Links

- **Maven Central Repository**: https://repo1.maven.org/maven2/io/github/softartdev/saferoom.x/
- **GitHub Repository**: https://github.com/softartdev/cwac-saferoom
- **Upstream Repository**: https://github.com/commonsguy/cwac-saferoom
- **Sonatype Central Portal**: https://central.sonatype.com/

---

## 📚 Additional Documentation

- See [PUBLISHING.md](PUBLISHING.md) for detailed publishing instructions
- See [README.md](../README.md) for library usage documentation
- See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines

---

**Status**: ✅ Ready for Maven Central publication
**Date**: October 8, 2025
**Version**: 1.3.1

