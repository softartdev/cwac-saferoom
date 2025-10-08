# CWAC-SafeRoom: A Room<->SQLCipher for Android Bridge

This project implements the `SupportSQLite...` series of classes and interfaces
that [Room](https://developer.android.com/topic/libraries/architecture/room.html)
can use for working with a particular edition of SQLite. Specficially, this
project's classes connect Room
with [SQLCipher for Android](https://www.zetetic.net/sqlcipher/sqlcipher-for-android/),
a version of SQLite that offers transparent encryption of its contents.

**NOTE**: SQLCipher for Android has its own implementation of the `SupportSQLite...` classes and
interfaces.
Please consider using SQLCipher for Android directly, rather than using SafeRoom.

## Notable Forks

- https://github.com/hannesa2/cwac-saferoom

## Installation

There are two versions of this library, for AndroidX and for the older Android Support Library.

If you cannot use SSL, use `http://repo.commonsware.com` for the repository URL.

### AndroidX

```groovy
repositories {
    maven {
        url "https://s3.amazonaws.com/repo.commonsware.com"
    }
}

dependencies {
    implementation "com.commonsware.cwac:saferoom.x:1.3.0"
}
```

### Android Support Library

```groovy
repositories {
    maven {
        url "https://s3.amazonaws.com/repo.commonsware.com"
    }
}

dependencies {
    implementation "com.commonsware.cwac:saferoom:1.2.1"
}
```

## Usage

When you use Room, you use `Room.databaseBuilder()` or `Room.inMemoryDatabaseBuilder()`
to get a `RoomDatabase.Builder`. After configuring that object, you call
`build()` to get an instance of your custom subclass of `RoomDatabase`, whichever
one that you supplied as a Java class object to the
`Room.databaseBuilder()` or `Room.inMemoryDatabaseBuilder()` method.

To use SafeRoom, on the `RoomDatabase.Builder`, before calling `build()`:

- Create an instance of `com.commonsware.cwac.saferoom.SafeHelperFactory`,
  passing in the passphrase to use

- Pass that `SafeHelperFactory` to the `RoomDatabase.Builder` via the
  `openHelperFactory()` method

```java
// EditText passphraseField;
SafeHelperFactory factory=SafeHelperFactory.fromUser(passphraseField.getText());

StuffDatabase db=Room.databaseBuilder(ctxt, StuffDatabase.class, DB_NAME)
  .openHelperFactory(factory)
  .build();
```

### Supplying a Passphrase

A cardinal rule of passphrases in Java is: do not hold them in `String`
objects. You have no means of clearing those from memory, as a `String`
is an immutable value.

The `SafeHelperFactory` constructor takes a either a `byte[]` or a `char[]` for the passphrase. If
you are getting the passphrase from the user via an `EditText` widget,
use the `fromUser()` factory method instead, supplying the `Editable`
that you get from `getText()` on the `EditText`.

SafeRoom will zero out the `byte[]` or `char[]` once the database is opened. If you use
`fromUser()`, SafeRoom will also clear the contents of the `Editable`.

### Encrypting Existing Databases

If you have an existing SQLite database &mdash; created with Room or
otherwise &mdash; the `SQLCipherUtils` class has `getDatabaseState()`
and `encrypt()` methods for you.

`getDatabaseState()` returns a `State` object indicating whether a database
is `ENCRYPTED`, `UNENCRYPTED`, or `DOES_NOT_EXIST`. The determination
of whether the database is unencrypted is based on whether we can open it
without a passphrase. There are two versions of `getDatabaseState()`:

- `getDatabaseState(Context, String)` for a `Context` and database name

- `getDatabaseState(File)`, where the `File` points to the database

`encrypt()` will take an unencrypted database as input and encrypt it
using the supplied passphrase. Technically, it will encrypt a copy
of the database, then delete the unencrypted one and rename the copy to
the original name. There are five versions of `encrypt()`:

- `encrypt(Context, String, Editable)` where the `String` is the database
  name and the `Editable` is the passphrase (e.g., from `getText()` on
  an `EditText`)

- `encrypt(Context, String, char[])` where the `String` is the database
  name and the `char[]` is the passphrase

- `encrypt(Context, File, char[])` where the `File` points to the database
  and the `char[]` is the passphrase

- `encrypt(Context, String, byte[])` where the `String` is the database
  name and the `byte[]` is the passphrase

- `encrypt(Context, File, byte[])` where the `File` points to the database
  and the `byte[]` is the passphrase

The passphrase is left untouched by `encrypt()`, so you can turn around and
use it with `SafeHelperFactory`. If you are not planning on opening the database,
please clear out the passphrase after `encrypt()` returns.

Only call `encrypt()` when the database is closed. Ideally, call `encrypt()`
before opening the database in Room. At minimum, call `close()` on your
`RoomDatabase` before calling `encrypt()`.

### Changing the Passphrase

If you want to change the passphrase for an existing database:

- Open it in writeable mode

- Call `SafeHelperFactory.rekey()`, supplying that database plus either a
  `char[]` or an `Editable` reflecting the new passphrase to use

Note that this does *not* encrypt an unencrypted database. Use the `encrypt()`
option listed above for that.

The `Editable` will be cleared as part of this work, but the `char[]` will
not be zero'd out. Please clear that array as soon as you are done with it.

### Decrypting Existing Databases

You can call `decrypt()` on `SQLCipherUtils` to decrypt an existing
SQLCipher-encrypted database. Supply the `Context`, the `File` pointing
to the database, and a `char[]` or `byte[]` with the passphrase. `decrypt()` will
replace the encrypted database with a decrypted one, so that database can
be opened using ordinary SQLite.

### Opening Unencrypted Databases

If you need to open a regular unencrypted SQLite database, use
`new SafeHelperFactory("".toCharArray())` to create the `SafeHelperFactory`.

## Upgrading to 1.0.0 and Higher

SafeRoom 1.x uses SQLCipher for Android 4.x. SafeRoom 0.x used SQLCipher for Android 3.x.

The problem is that Zetetic changed the SQLCipher for Android file format
between 3.x and 4.x.

If you have existing SQLCipher for Android 3.x files, you will need to do a bit of extra
work for existing databases in the older format.

### Keeping the Old Format

Perhaps you have a strong need to keep the database in the older format, for
whatever reason. In that case, pass `SafeHelperFactory.POST_KEY_SQL_V3` as the
second parameter to either the `SafeHelperFactory` constructor or `fromUser()`
static method:

```java
SafeHelperFactory factory=
  SafeHelperFactory.fromUser(new SpannableStringBuilder(passphraseField.getText()),
    SafeHelperFactory.POST_KEY_SQL_V3);
```

This will open the database using `PRAGMA cipher_compatibility = 3;`, which was introduced
in SQLCipher for Android 4.0.1.

### Migrating to the New Format

If you wish to convert to the newer, more secure settings, the *first time* that
you open the existing database, pass `SafeHelperFactory.POST_KEY_SQL_MIGRATE`
as the second parameter to the `SafeHelperFactory` constructor or `fromUser()`
static method:

```java
SafeHelperFactory factory=
  SafeHelperFactory.fromUser(new SpannableStringBuilder(passphraseField.getText()),
    SafeHelperFactory.POST_KEY_SQL_MIGRATE);
```

This will convert the existing database in place. The second and subsequent times
that you work with the database, you can (and should) skip this parameter, as the
database will have already been migrated.

## Support for Pre-Key and Post-Key SQL

SQLCipher for Android supports [a number of custom
`PRAGMA`s](https://www.zetetic.net/sqlcipher/sqlcipher-api/)
for configuring the encryption, such as the number of PBKDF2 iterations to use for
key stretching. Some of that SQL needs to be performed at specific times with respect
to opening the database.

For SQL that needs to be executed after the database key is set but before you get
to start using the database, you can pass the SQL in as a parameter to the
`SafeHelperFactory` constructor or `fromUser()` methods &mdash; that is what the
upgrade options in the preceding sections are doing.

More formally, you can pass a `SafeHelperFactory.Options` object as the second
parameter, supplying both pre-key and post-key SQL statements to be executed.
You can create an `Options` object by calling the `builder()` `static` method
on `Options`, calling setters for the SQL, then `build()` to get the options:

```java
SafeHelperFactory.Options options = SafeHelperFactory.Options.builder().setPreKeySql(PREKEY_SQL).build();
```

## Closing the Database

Frequently, apps do not close their databases. Given asynchronous work, it is not
always clear when it is safe to close the database. However, there may be times
when you specifically do want to close the database. For example, closing the database
is a good idea before you try working with the database files directly, such as for
backup or restore operations.

However, by default, the `SafeHelperFactory` object is a single-use object. Use it
to open a database, but if you close that database, create a fresh `SafeHelperFactory`
object to open the database again.

The reason for this is that `SafeHelperFactory` needs to cache the passphrase
between the time you create the factory and when you try opening the database (directly
or indirectly). By default, `SafeHelperFactory` clears out that cached passphrase,
so the plaintext passphrase is not held in memory any longer than it has to. However,
this means that attempting to reuse the `SafeHelperFactory`, and open the database
again after closing it, will fail.

However, it is possible that you are using code that opens and closes the database
on its own, and you do not control the timing of that work. If so, you can opt
out of the automatic passphrase clearing feature. To do this, use the `SafeHelperFactory.Options`
and its `setClearPassphrase()` option, passing in `false`:

```java
// EditText passphraseField;
SafeHelperFactory.Options options = SafeHelperFactory.Options.builder().setClearPassphrase(false).build();
SafeHelperFactory factory=SafeHelperFactory.fromUser(passphraseField.getText(), options);

StuffDatabase db=Room.databaseBuilder(ctxt, StuffDatabase.class, DB_NAME)
  .openHelperFactory(factory)
  .build();
```

However, bear in mind that this weakens the security of your app a bit, as that
passphrase will remain in memory indefinitely.

## Hey, I Got This Really Long `IllegalStateException` Message!

If you are here because your logs show:

```
The passphrase appears to be cleared. This happens by
default the first time you use the factory to open a database, so we can remove the
cleartext passphrase from memory. If you close the database yourself, please use a
fresh SafeHelperFactory to reopen it. If something else (e.g., Room) closed the
database, and you cannot control that, use SafeHelperFactory.Options to opt out of
the automatic password clearing step. See the project README for more information.
```

...then read the preceding section ("Closing the Database"). This particular
`IllegalStateException` message appears if you attempt to open a database,
and the passphrase is not `null` but has `0` for all its bytes. It is likely that
this means that the passphrase was cleared automatically, and the preceding
section explains what is going on.

## Dependencies

As one might expect, this project depends on SQLCipher for Android. The AndroidX edition
of SafeRoom supports SQLCipher for Android 4.3.0, while the Android Support Library
edition supports SQLCipher for Android 4.2.0.

This project also depends upon `android.arch.persistence:db` (Android Support Library edition)
or `androidx.sqlite:sqlite-framework` (AndroidX edition), which is
the support database API that Room uses.

The Android Support Library edition of CWAC-SafeRoom is frozen at supporting `1.1.1`
of `android.arch.persistence:db`.

The AndroidX edition of CWAC-SafeRoom supports `2.0.1` of `androidx.sqlite:sqlite-framework`
and should be updated to support newer versions of AndroidX over time.

In terms of Android versions:

- Version 1.3.0 of the AndroidX SafeRoom requires API Level 16 or higher

- Other versions of SafeRoom require API Level 15 or higher

## Tests

This project has two sources of tests. Some are local to the project. The
rest come from the [`support-db-tests`](https://gitlab.com/commonsguy/support-db-tests)
project. That project contains tests that exercise any support database API
implementation.

TL;DR: to run the full set of CWAC-SafeRoom tests, use `SafeRoomSuite`.
Either run that directly from your IDE, or set up a run configuration pointing
to it, etc.

## ProGuard

SafeRoom itself should require no special ProGuard setup. You may need to add
ProGuard rules for SQLCipher for Android, such as:

```
-keep class net.sqlcipher.** { ; }
-keep class net.sqlcipher.database. { *; }
```

## Additional Documentation

[JavaDocs are available](http://javadocs.commonsware.com/cwac/saferoom/index.html),
though most of the library is not `public`, as it does not need to be.

[Android's Architecture Components](https://commonsware.com/AndroidArch)
contains a chapter dedicated to SafeRoom.

## License

The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file. The copyrights are owned by CommonsWare for things unique to this
library and a combination of CommonsWare and the Android Open Source
Project for code modified from the Architecture Components' `Framework*`
set of classes.

## Questions

If you have questions regarding the use of this code, please post a question
on [Stack Overflow](http://stackoverflow.com/questions/ask) tagged with
`commonsware-cwac` and `android`
after [searching to see if there already is an answer](https://stackoverflow.com/search?q=[commonsware-cwac]+saferoom).
Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code
and stack traces if you are encountering crashes.

If you have encountered what is clearly a bug, or if you have a feature request,
please post an [issue](https://github.com/commonsguy/cwac-saferoom/issues).
Be certain to include complete steps for reproducing the issue.
The [contribution guidelines](CONTRIBUTING.md)
provide some suggestions for how to create a bug report that will get
the problem fixed the fastest.

You are also welcome to join
[the CommonsWare Community](https://community.commonsware.com/)
and post questions
and ideas to [the CWAC category](https://community.commonsware.com/c/cwac).

Do not ask for help via social media.

Also, if you plan on hacking
on the code with an eye for contributing something back,
please open an issue that we can use for discussing
implementation details. Just lobbing a pull request over
the fence may work, but it may not.
Again, the [contribution guidelines](CONTRIBUTING.md) provide a bit
of guidance here.

## Release Notes

### Android X

- v1.3.0: upgraded to SQLCipher for Android 4.3.0
- v1.2.1: fixed bug where `char[]` passphrases were no longer being cleared after use
- v1.2.0: added support for opting out of passphrase clearing
- v1.1.3: fixed bug preventing `SafeHelperFactory` from opening unencrypted databases
- v1.1.2: closed `SQLStatement` used in `encrypt()`, `decrypt()`
- v1.1.1: fixed a bug in `BindingsRecorder`
- v1.1.0: added `SafeHelperFactory.Options` and support for pre-key SQL
- v1.0.5: upgraded to SQLCipher for Android 4.2.0
- v1.0.4: added support for `byte[]` passphrases to `SQLCipherUtils`
- v1.0.3: added support for `byte[]` passphrases
- v1.0.2: upgraded to SQLCipher for Android 4.1.3
- v1.0.1: changed `SQLCipherUtils`
  per [issue #45](https://github.com/commonsguy/cwac-saferoom/issues/45)
- v1.0.0:
    - Upgraded to SQLCipher for Android 4.0.1
    - `SQLCipherUtils.encrypt()` and `SQLCipherUtils.decrypt()` will throw `FileNotFoundException`
      if the database to encrypt/decrypt is not found
- v0.5.1: added more synchronization
- v0.5.0: released AndroidX edition

### Android Support Library

- v1.2.1: fixed bug where `char[]` passphrases were no longer being cleared after use
- v1.2.0: added support for opting out of passphrase clearing
- v1.1.3: fixed bug preventing `SafeHelperFactory` from opening unencrypted databases
- v1.1.2: closed `SQLStatement` used in `encrypt()`, `decrypt()`
- v1.1.1: fixed a bug in `BindingsRecorder`
- v1.1.0: added `SafeHelperFactory.Options` and support for pre-key SQL
- v1.0.5: upgraded to SQLCipher for Android 4.2.0
- v1.0.4: added support for `byte[]` passphrases to `SQLCipherUtils`
- v1.0.3: added support for `byte[]` passphrases
- v1.0.2: upgraded to SQLCipher for Android 4.1.3
- v1.0.1: changed `SQLCipherUtils`
  per [issue #45](https://github.com/commonsguy/cwac-saferoom/issues/45)
- v1.0.0:
    - Upgraded to SQLCipher for Android 4.0.1
    - `SQLCipherUtils.encrypt()` and `SQLCipherUtils.decrypt()` will throw `FileNotFoundException`
      if the database to encrypt/decrypt is not found
- v0.4.5: added more synchronization
- v0.4.4: addressed [thread-safety issue](https://github.com/commonsguy/cwac-saferoom/issues/27)
- v0.4.3: bumped `android.arch.persistence:db` dependency to `1.1.1`
- v0.4.2: fixed [edge case WAL issue](https://github.com/commonsguy/cwac-saferoom/issues/23)
- v0.4.1: added Room-specific tests,
  fixed [WAL issue](https://github.com/commonsguy/cwac-saferoom/issues/17)
- v0.4.0: updated to `1.1.0` of the support database API
- v0.3.4: changed non-WAL journal mode to TRUNCATE
- v0.3.3: added WAL support, with an assist
  from [plackemacher](https://github.com/commonsguy/cwac-saferoom/pull/20)
- v0.3.2: added `decrypt()` utility method
- v0.3.1: changed `rekey()` to use the existing `changePassword()`
- v0.3.0: added `rekey()`, upgraded to SQLCipher for Android 3.5.9, replaced tests
- v0.2.1: added temporary implementation of `getDatabaseName()` to `Helper`
- v0.2.0: added `SQLCipherUtils`
  to [help encrypt existing databases](https://github.com/commonsguy/cwac-saferoom/issues/6)
- v0.1.3: upgraded to Android Gradle Plugin 3.0.0, set transitive dependencies to `api`
- v0.1.2: fixed [issue #3](https://github.com/commonsguy/cwac-saferoom/issues/3), related to closing
  statements
- v0.1.1: updated support database dependency to `1.0.0`
- v0.1.0: eliminated Room dependency
- v0.0.4: raised Room dependencies to `1.0.0-beta1` and SQLCipher for Android to `3.5.7`
- v0.0.3: raised Room dependencies to `1.0.0-alpha8`
- v0.0.2: raised Room dependencies to `1.0.0-alpha5`
- v0.0.1: initial release

## Who Made This?

<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>
