# Publishing Guide for CWAC SafeRoom

## Maven Central Publishing Configuration

The library has been configured for publication to Maven Central under the new coordinates.

### Published Artifacts

The library is published with the following Maven coordinates:
- **GroupId**: `io.github.softartdev`
- **ArtifactId**: `saferoom.x`
- **Version**: `1.3.1`

### Previous Coordinates (Deprecated)
- **GroupId**: `com.commonsware.cwac`
- **ArtifactId**: `saferoom.x`
- **Version**: `1.3.0`

### Published Files

When you run `./gradlew :saferoom:publishToMavenLocal`, the following artifacts are published to your local Maven repository (`~/.m2/repository/`):

1. **`saferoom.x-1.3.1.aar`** - The main Android library archive (~21KB)
2. **`saferoom.x-1.3.1.pom`** - The Maven POM file with dependencies (~2KB)
3. **`saferoom.x-1.3.1-sources.jar`** - Source code JAR (~15KB)
4. **`saferoom.x-1.3.1-javadoc.jar`** - Javadoc JAR (currently empty for local testing)

### Location

For local testing, the artifacts are published to:
```
~/.m2/repository/io/github/softartdev/saferoom.x/1.3.1/
```

For Maven Central, the artifacts will be available at:
```
https://repo1.maven.org/maven2/io/github/softartdev/saferoom.x/1.3.1/
```

### Dependencies

The POM file includes the following dependencies:
- **net.zetetic:sqlcipher-android:4.10.0** (compile scope)
- **androidx.sqlite:sqlite:2.6.1** (compile scope)

## How to Publish Locally

To publish the library to your local Maven repository:

```bash
./gradlew :saferoom:publishToMavenLocal
```

This will:
1. Build the release AAR
2. Generate source and javadoc JARs
3. Create the POM file with dependencies
4. Publish all artifacts to `~/.m2/repository/`

## Using the Locally Published Library

To use the locally published library in another project:

### 1. Add Maven Local to your repositories

In your project's `build.gradle` or `settings.gradle`:

```gradle
repositories {
    mavenLocal()  // Add this first to prioritize local artifacts
    mavenCentral()
    google()
}
```

### 2. Add the dependency

In your app's `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.softartdev:saferoom.x:1.3.1'
}
```

### 3. Verify it's using the local version

When you build your project, Gradle will use the locally published version from `~/.m2/repository/` instead of downloading from remote repositories.

## Testing in the Demo Module

The demo module currently uses `project(":saferoom")` dependency, which references the local project directly. To test with the Maven Local version:

1. Change `demo/build.gradle` line 32 from:
   ```gradle
   implementation project(":saferoom")
   ```
   to:
   ```gradle
   implementation 'io.github.softartdev:saferoom.x:1.3.1'
   ```

2. Build the demo:
   ```bash
   ./gradlew :demo:assembleDebug
   ```

## Publishing to Maven Central

The library is configured to publish to Maven Central using the Vanniktech Maven Publish Plugin.

### Prerequisites

1. **Sonatype Account**: You need an account on [Sonatype's Central Portal](https://central.sonatype.com/)
2. **GPG Key**: A GPG key pair for signing artifacts (already configured in `local.properties`)
3. **GitHub Secret**: Only ONE secret needs to be configured in your GitHub repository:
   - `LARGE_SECRET_PASSPHRASE` - For decrypting the encrypted `local.properties` and GPG key files
   
   All other credentials (Maven Central username/password and signing info) are automatically loaded from the decrypted `local.properties` file.

### Publishing Process

#### Option 1: Via GitHub Actions (Recommended)

1. Create a new tag:
   ```bash
   git tag 1.3.1
   git push origin 1.3.1
   ```

2. GitHub Actions will automatically:
   - Build the library
   - Run tests
   - Sign artifacts
   - Publish to Maven Central
   - Release to public repositories

#### Option 2: Manual Publishing

1. Ensure all secrets are configured in `local.properties`:
   ```properties
   signing.keyId=YOUR_KEY_ID
   signing.password=YOUR_KEY_PASSWORD
   signing.secretKeyRingFile=path/to/key.gpg
   mavenCentralUsername=YOUR_USERNAME
   mavenCentralPassword=YOUR_PASSWORD
   ```

2. Run the publish command:
   ```bash
   ./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
   ```

3. The artifacts will be automatically published and released to Maven Central

## Build Configuration

The publishing configuration is defined in:
- **`gradle.properties`** - Maven coordinates (groupId, artifactId, version)
- **`saferoom/build.gradle`** - Publishing tasks and POM configuration

The configuration uses the modern `maven-publish` plugin and is compatible with Android Gradle Plugin 8.x.

## Troubleshooting

### Clean Local Publication

If you need to clean the local Maven repository:

```bash
rm -rf ~/.m2/repository/io/github/softartdev/saferoom.x/1.3.1/
```

Then republish:

```bash
./gradlew :saferoom:publishToMavenLocal
```

### Verify Published Artifacts

Check what was published:

```bash
ls -lah ~/.m2/repository/io/github/softartdev/saferoom.x/1.3.1/
```

View the POM file:

```bash
cat ~/.m2/repository/io/github/softartdev/saferoom.x/1.3.1/saferoom.x-1.3.1.pom
```

Inspect the AAR contents:

```bash
unzip -l ~/.m2/repository/io/github/softartdev/saferoom.x/1.3.1/saferoom.x-1.3.1.aar
```

## Version Information

- **Current Version**: 1.3.1
- **Previous Version**: 1.3.0 (from original Maven coordinates)
- **Target Version for Maven Central**: 1.3.1 with `io.github.softartdev` groupId

## Repository Information

- **GitHub Repository**: https://github.com/softartdev/cwac-saferoom
- **Upstream**: https://github.com/commonsguy/cwac-saferoom
- **License**: Apache License 2.0

