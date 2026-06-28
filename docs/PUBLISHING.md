# Publishing `thor-extension-api` to Maven Central

## One-time setup

### 1. Central Portal account + namespace
1. Create an account at https://central.sonatype.com.
2. Register the namespace `io.github.trinadhthatakula`.
3. Verify it: the portal gives you a verification token. Create a **public** GitHub repository named
   exactly that token under the `trinadhthatakula` GitHub account, click verify, then delete the repo.

### 2. User token
In the portal, generate a user token. It yields a username + password pair used as
`mavenCentralUsername` / `mavenCentralPassword`.

### 3. GPG signing key
```bash
gpg --full-generate-key                        # choose RSA, 4096 bits (plain --gen-key defaults to ECC)
gpg --list-secret-keys --keyid-format=long     # note the KEY_ID
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>   # publish the public key
gpg --armor --export-secret-keys <KEY_ID>      # copy the ascii-armored private key
```

### 4. Local credentials (never commit these)
Add to `~/.gradle/gradle.properties`:
```properties
mavenCentralUsername=<token username>
mavenCentralPassword=<token password>
signingInMemoryKey=<ascii-armored private key, newlines as \n>
signingInMemoryKeyPassword=<key passphrase>
```

## Per-release

> **Build on JDK 21.** This project mandates JDK 21 (Zulu/Corretto). If your machine's default
> `java` is newer (e.g. JDK 26), the javadoc/Dokka step fails with an `IllegalArgumentException`.
> Either set `JAVA_HOME` to a JDK 21, or pass `-Dorg.gradle.java.home=/path/to/jdk-21` to the
> Gradle commands below. AGP is pinned to stable **9.2.0** (compileSdk 37) for reproducible builds.

1. Bump `VERSION_NAME` in `gradle.properties` (SemVer). Bump the runtime contract integer
   `thor.extension.api.version` only on a breaking contract change (and the library MAJOR with it).
- Optional local dry-run without a GPG key (SNAPSHOT versions are exempt from signing):
  `./gradlew publishToMavenLocal -PVERSION_NAME=<VERSION_NAME>-SNAPSHOT`
2. Publish:
   ```bash
   ./gradlew publishToMavenCentral --no-configuration-cache
   ```
3. With `automaticRelease = true` the deployment promotes itself; otherwise confirm it in the portal.
4. Verify the artifact appears at
   https://repo1.maven.org/maven2/io/github/trinadhthatakula/thor-extension-api/ (allow time for sync).

## CI / automated publishing (GitHub Actions)

`.github/workflows/publish.yml` publishes automatically on every push to the **`production`** branch.

**Release flow:**
1. Bump `VERSION_NAME` in `gradle.properties` on `main` and commit.
2. Merge / push that commit to the `production` branch.
3. The workflow runs `./gradlew publishToMavenCentral` on JDK 21 and auto-promotes the release.

Pushing the same `VERSION_NAME` twice fails (Central rejects duplicate coordinates) — that is the
intended guard against accidental double-publishes. Always bump `VERSION_NAME` before releasing.

**Required repository secrets** (Settings → Secrets and variables → Actions → New repository secret).
The workflow maps each to the matching `ORG_GRADLE_PROJECT_*` Gradle property:

| Secret name | Value |
|---|---|
| `MAVEN_CENTRAL_USERNAME` | Central Portal user-token username |
| `MAVEN_CENTRAL_PASSWORD` | Central Portal user-token password |
| `SIGNING_IN_MEMORY_KEY` | full armored private key — `gpg --armor --export-secret-keys <KEY_ID>` (paste the whole block, real newlines; GitHub secrets preserve them) |
| `SIGNING_IN_MEMORY_KEY_PASSWORD` | the GPG key passphrase |

To switch the trigger to a different branch (e.g. `main`) or to tag-based releases, edit the `on:`
block in `.github/workflows/publish.yml`.
