# GitHub Actions Setup for Maven Central Publishing

## 🔐 Simplified Secret Management

This project uses an **encrypted secrets approach** where only ONE GitHub secret is required. All sensitive credentials are stored in encrypted files in the repository and decrypted during the GitHub Actions workflow.

## How It Works

### 1. Encrypted Files in Repository

The following files are encrypted and committed to the repository:
- `.github/secrets/8257B447.gpg.gpg` - Encrypted GPG signing key
- `.github/secrets/local.properties.gpg` - Encrypted properties file with credentials

### 2. Decryption Script

The `.github/scripts/decrypt_secret.sh` script decrypts these files using the `LARGE_SECRET_PASSPHRASE`:

```bash
#!/bin/sh
# Decrypt the file
gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./8257B447.gpg ./.github/secrets/8257B447.gpg.gpg

gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ./local.properties ./.github/secrets/local.properties.gpg
```

### 3. Decrypted local.properties Contains

Once decrypted, `local.properties` provides all necessary credentials:

```properties
signing.keyId=8257B447
signing.password=softartdev_sonatype_2021
signing.secretKeyRingFile=8257B447.gpg
mavenCentralUsername=W3Yi6N
mavenCentralPassword=nyECjYbRutbBGodN3KeSVWuaaIVBipZBX
```

### 4. Gradle Automatically Loads local.properties

Gradle automatically loads `local.properties` into project properties, so the vanniktech maven publish plugin can access:
- `mavenCentralUsername` and `mavenCentralPassword` for Maven Central authentication
- `signing.keyId`, `signing.password`, and `signing.secretKeyRingFile` for artifact signing

## 🚀 GitHub Actions Workflow

### Build Workflow (.github/workflows/build_only.yml)

Triggers on every push (except tags) and pull requests:

```yaml
on:
  push:
    branches: ['**']
    tags-ignore: ['**']
  pull_request:
    branches: [master, main, local]
```

**Steps:**
1. Checkout code
2. Set up JDK 17
3. Decrypt secrets using `LARGE_SECRET_PASSPHRASE`
4. Build and run tests

### Publish Workflow (.github/workflows/build_publish.yml)

Triggers only when a tag is pushed:

```yaml
on:
  push:
    tags: ['*']
```

**Steps:**
1. Checkout code
2. Set up JDK 17
3. Decrypt secrets using `LARGE_SECRET_PASSPHRASE`
4. Build and test
5. Publish to Maven Central (credentials from decrypted `local.properties`)

## 🔧 Required GitHub Secret

Configure only **ONE** secret in your GitHub repository settings:

### Secret Configuration

Go to: `Settings` → `Secrets and variables` → `Actions` → `New repository secret`

| Name | Value | Description |
|------|-------|-------------|
| `LARGE_SECRET_PASSPHRASE` | `your-encryption-password` | The passphrase used to encrypt/decrypt the secrets |

⚠️ **Important**: This passphrase must match the one used to encrypt the files with `.github/scripts/encrypt_secret.sh`

## 📝 How to Update Encrypted Secrets

If you need to update credentials (e.g., change Maven Central password):

### 1. Update local.properties

Edit your local `local.properties` file with new credentials.

### 2. Re-encrypt the file

```bash
export LARGE_SECRET_PASSPHRASE="your-encryption-password"
./.github/scripts/encrypt_secret.sh
```

This will re-encrypt:
- `8257B447.gpg` → `.github/secrets/8257B447.gpg.gpg`
- `local.properties` → `.github/secrets/local.properties.gpg`

### 3. Commit and push

```bash
git add .github/secrets/
git commit -m "chore: update encrypted secrets"
git push
```

## 🧪 Testing the Workflow Locally

You can test the decryption locally:

```bash
export LARGE_SECRET_PASSPHRASE="your-encryption-password"
./.github/scripts/decrypt_secret.sh
```

This should create:
- `8257B447.gpg` (GPG key)
- `local.properties` (credentials)

Then test publishing:

```bash
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
```

## 📦 Publishing Process

### Automatic Publishing via GitHub Actions

1. **Prepare a release:**
   ```bash
   git tag 1.3.1
   git push origin 1.3.1
   ```

2. **GitHub Actions automatically:**
   - Decrypts secrets
   - Builds the library
   - Runs tests
   - Signs artifacts with GPG key
   - Publishes to Maven Central staging repository
   - Releases to public Maven Central

3. **Monitor the workflow:**
   - Go to `Actions` tab in your GitHub repository
   - Watch the "Build & Publish CI/CD" workflow

### Manual Publishing (if needed)

If GitHub Actions fails or you need to publish manually:

1. Ensure `local.properties` and `8257B447.gpg` are in the project root
2. Run: `./gradlew publishAndReleaseToMavenCentral --no-configuration-cache`

## 🔍 Troubleshooting

### "Cannot perform signing task because it has no configured signatory"

**Cause**: The signing key or credentials are not properly loaded.

**Solution**: 
- Verify `local.properties` exists and contains all required properties
- Verify `8257B447.gpg` file exists in project root
- Check that the decryption step ran successfully in GitHub Actions logs

### "401 Unauthorized" when publishing to Maven Central

**Cause**: Invalid Maven Central credentials.

**Solution**:
- Verify `mavenCentralUsername` and `mavenCentralPassword` in `local.properties`
- Ensure credentials are correct for [Sonatype Central Portal](https://central.sonatype.com/)
- Re-encrypt `local.properties` if you updated credentials

### GitHub Actions fails at decrypt step

**Cause**: Incorrect `LARGE_SECRET_PASSPHRASE` secret.

**Solution**:
- Verify the GitHub secret matches the passphrase used to encrypt the files
- Check GitHub Actions logs for GPG decryption errors

## ✅ Advantages of This Approach

1. **Fewer GitHub Secrets**: Only 1 secret instead of 6+
2. **Easier Maintenance**: Update one encrypted file instead of multiple secrets
3. **Consistent Credentials**: Same credentials work locally and in CI
4. **Secure**: Encrypted files are safe to commit to repository
5. **Auditable**: Changes to credentials are tracked in git history (as encrypted files)

## 📚 Related Files

- `.github/workflows/build_only.yml` - Build and test workflow
- `.github/workflows/build_publish.yml` - Build, test, and publish workflow
- `.github/scripts/decrypt_secret.sh` - Decryption script (used by CI)
- `.github/scripts/encrypt_secret.sh` - Encryption script (used locally)
- `.github/secrets/8257B447.gpg.gpg` - Encrypted GPG key
- `.github/secrets/local.properties.gpg` - Encrypted credentials
- `local.properties` - Credentials file (gitignored, decrypted from encrypted version)
- `8257B447.gpg` - GPG key file (gitignored, decrypted from encrypted version)

---

**Status**: ✅ Configured and ready
**Required Secrets**: 1 (`LARGE_SECRET_PASSPHRASE`)
**Encrypted Files**: 2 (GPG key + local.properties)

