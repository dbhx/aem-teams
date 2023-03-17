# Simple Watcher

A simple recursive directory watcher.

## But why?

I know there's plenty of them out there, but most don't seem to care about the `recursive` option of Node's [`fs.watch()`](https://nodejs.org/docs/latest/api/fs.html#fs_fs_watch_filename_options_listener), which **significantly** improves performance on the supported platforms, especially for large directories.

Features:
* Dead simple and dead lightweight.
* No dependencies.
* Leverages the `recursive` options on OS X and Windows; uses a fallback for other platforms.
* Takes care of WinAPI's `ReadDirectoryChangesW` [double reporting](http://stackoverflow.com/questions/14036449/c-winapi-readdirectorychangesw-receiving-double-notifications).

## Usage

### Command line

```
Usage:
  simple-watcher path1 [path2 path3 ...] [--shallow]
```

### JavaScript

```JavaScript
const watch = require('simple-watcher')

// Watch over file or directory:
watch('/path/to/foo', filePath => {
  console.log(`Changed: ${filePath}`)
})

// Watch over multiple paths:
watch(['/path/to/foo', '/path/to/bar'], filePath => {
  console.log(`Changed: ${filePath}`)
})

// Shallow watch:
watch(['/path/to/foo', '/path/to/bar'], { shallow: true }, filePath => {
  console.log(`Changed: ${filePath}`)
})
```

## Caveats

When watching over files rather than directories, the [`fs.watchFile()`](https://nodejs.org/docs/latest/api/fs.html#fs_fs_watchfile_filename_options_listener) is used. This is to provide a polling fallback in cases where directory watching is problematic (e.g. Docker).
