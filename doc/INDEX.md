# Documentation Index

## 📚 Overview

This directory contains all documentation for the CWAC SafeRoom library, including setup guides, publishing instructions, and contribution guidelines.

---

## 📖 Documentation Files

### Getting Started

- **[Main README](../README.md)** - Project overview and installation instructions
- **[Original Usage Guide](README-original.markdown)** - Detailed usage instructions and examples
- **[Version 1.4.0 Release Notes](RELEASE-1.4.0.md)** - Latest release notes
- **[Version 1.0.0 Release Notes](README-1.0.0.md)** - Historical release notes

### For Publishers & Maintainers

- **[SUMMARY](SUMMARY.md)** ⭐ **START HERE** - Complete overview of the Maven Central publishing setup
- **[Publication Fix](PUBLICATION_FIX.md)** 🔧 **LATEST** - CI/CD signing issue fix and verification
- **[CI/CD Fix Details](CI_CD_FIX.md)** - Detailed analysis of the signing configuration fix
- **[Publishing Guide](PUBLISHING.md)** - Step-by-step publishing instructions
- **[Maven Central Setup](MAVEN_CENTRAL_SETUP.md)** - Detailed Maven Central configuration and verification
- **[GitHub Actions Setup](GITHUB_ACTIONS_SETUP.md)** - CI/CD configuration and workflow details

### For Contributors

- **[Contributing Guidelines](CONTRIBUTING.md)** - How to contribute to this project
- **[Support](SUPPORT.md)** - How to get help and report issues

---

## 🚀 Quick Links

### Publishing to Maven Central

1. Read: [SUMMARY.md](SUMMARY.md) - Overview
2. Read: [GITHUB_ACTIONS_SETUP.md](GITHUB_ACTIONS_SETUP.md) - GitHub Actions configuration
3. Configure: Only `LARGE_SECRET_PASSPHRASE` GitHub secret
4. Publish: Push a tag to trigger automatic publishing

### Local Testing

```bash
# Build the library
./gradlew clean build

# Publish to Maven Local
./gradlew :saferoom:publishToMavenLocal

# Use in your project
implementation 'io.github.softartdev:saferoom.x:1.3.1'
```

### Contributing

1. Read: [CONTRIBUTING.md](CONTRIBUTING.md)
2. Fork the repository
3. Create a feature branch
4. Submit a pull request

---

## 🔗 External Links

- **Maven Central**: https://central.sonatype.com/artifact/io.github.softartdev/saferoom.x
- **GitHub Repository**: https://github.com/softartdev/cwac-saferoom
- **Original Repository**: https://github.com/commonsguy/cwac-saferoom
- **Issues**: https://github.com/softartdev/cwac-saferoom/issues

---

## 📊 Documentation Structure

```
cwac-saferoom/
├── README.md                       # Main project README
└── doc/
    ├── INDEX.md                    # This file
    ├── SUMMARY.md                  # ⭐ Complete setup overview
    ├── PUBLISHING.md               # Publishing instructions
    ├── MAVEN_CENTRAL_SETUP.md      # Maven Central configuration
    ├── GITHUB_ACTIONS_SETUP.md     # CI/CD configuration
    ├── CONTRIBUTING.md             # Contribution guidelines
    ├── SUPPORT.md                  # Support information
    ├── README-1.0.0.md             # Version 1.0.0 release notes
    └── README-original.markdown    # Original usage guide
```

---

**Last Updated**: October 8, 2025  
**Current Version**: 1.3.1

