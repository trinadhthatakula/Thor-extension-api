# Thor Extension API

Contract interfaces for building extensions for the [Thor](https://github.com/trinadhthatakula/Thor) Android app manager.

> This SDK is published from its own repository at <https://github.com/trinadhthatakula/Thor-extension-api>, separate from the [Thor app](https://github.com/trinadhthatakula/Thor) itself.

## Add the dependency

```kotlin
dependencies {
    // Thor loads extensions into its own process and provides these classes at runtime,
    // so depend on the API as compileOnly — do NOT bundle it into your extension APK.
    compileOnly("com.trinadhthatakula:thor-extension-api:2.0.0")
}
```

## Declare your extension

An extension is a normal Android APK with **no launcher activity**. Its package name must start with
`com.valhalla.thor.ext.` and its manifest points Thor at your implementation class:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application android:hasCode="true">
        <meta-data
            android:name="thor.extension.class"
            android:value="com.valhalla.thor.ext.sample.SampleDebloatExtension" />
        <meta-data
            android:name="thor.extension.api.version"
            android:value="1" />
    </application>
</manifest>
```

## Minimal example

```kotlin
package com.valhalla.thor.ext.sample

import com.valhalla.thor.extension.api.DebloatExtension
import com.valhalla.thor.extension.api.ExtensionDebloatItem

class SampleDebloatExtension : DebloatExtension {
    override val name = "Sample Debloat List"
    override val description = "Example manufacturer debloat list"
    override val version = "1.0.0"
    override val author = "you"
    override val targetManufacturer = "Generic"

    override fun getDebloatItems(): List<ExtensionDebloatItem> = listOf(
        ExtensionDebloatItem(
            packageName = "com.example.bloat",
            recommendation = "recommended",
            description = "Removable sample bloatware"
        )
    )
}
```

## Current limitation: signature trust

On **release** builds, Thor currently loads only extensions signed with Thor's own signing key.
Third-party extensions signed with your own key load on **debug / self-built** Thor today; loading
third-party-signed extensions on the official release build is a planned future change.
