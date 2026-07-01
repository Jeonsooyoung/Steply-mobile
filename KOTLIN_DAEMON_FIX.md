# Kotlin daemon startup fix

This project now avoids the separate Kotlin compile daemon by using:

```properties
kotlin.compiler.execution.strategy=in-process
kotlin.incremental=false
ksp.incremental=false
```

This fixes the Windows/Android Studio error:

```text
The daemon has terminated unexpectedly on startup attempt #1 with error code: 0
1. Kotlin compile daemon is ready
```

## Recommended clean run on Windows

From the project root:

```powershell
.\gradlew --stop
Remove-Item -Recurse -Force .gradle, app\build, build -ErrorAction SilentlyContinue
.\gradlew :app:assembleDebug --no-daemon --stacktrace
```

Or double-click / run:

```text
scripts\fix-kotlin-daemon-windows.bat
```

If Android Studio still shows the old error, use **File > Invalidate Caches**, then reopen the project.
