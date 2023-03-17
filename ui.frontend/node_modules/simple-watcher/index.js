'use strict'

const fs = require('fs')
const path = require('path')

const TOLERANCE = 200 // For ReadDirectoryChangesW() double reporting.
const PLATFORMS = ['win32', 'darwin']

// OS directory watcher.
function watchDir (dirToWatch, options, callback) {
  const last = { filePath: null, timestamp: 0 }
  const w = fs.watch(dirToWatch, { persistent: true, recursive: !options.shallow }, (event, fileName) => {
    // On Windows fileName may actually be empty.
    // In such case assume this is the working dir change.
    const filePath = fileName ? path.join(dirToWatch, fileName) : dirToWatch

    fs.stat(filePath, (err, stat) => {
      // If error, the file was likely deleted.
      const timestamp = err ? 0 : (new Date(stat.mtime)).getTime()
      const ready = err || timestamp - last.timestamp >= options.tolerance
      const fileMatches = filePath === last.filePath
      last.filePath = filePath
      last.timestamp = timestamp

      if (fileMatches && !ready) {
        return
      }

      callback(filePath)
    })
  })

  w.on('error', (e) => {
    w.close()
  })
}

// Fallback deep watcher.
function watchDirFallback (dirToWatch, options, callback) {
  const dirs = [dirToWatch]
  options.ledger = options.ledger || new Set()

  for (let ii = 0; ii < dirs.length; ++ii) {
    const dir = dirs[ii]
    // Append dirs with descendants.
    if (!options.shallow) {
      for (const entityName of fs.readdirSync(dir)) {
        const entityPath = path.resolve(dir, entityName)
        fs.statSync(entityPath).isDirectory() && dirs.push(entityPath)
      }
    }

    options.ledger.add(dir)
    watchDir(dir, { shallow: true, tolerance: options.tolerance }, (entityPath) => {
      fs.stat(entityPath, (err, stat) => {
        if (err) { // Entity was deleted.
          options.ledger.delete(entityPath)
        } else if (stat.isDirectory() && !options.ledger.has(entityPath) && !options.shallow) { // New directory added.
          watchDirFallback(entityPath, options, callback)
        }

        callback(entityPath)
      })
    })
  }
}

function watchFile (filePath, options, callback) {
  options = options.interval ? { interval: options.interval } : {}
  fs.watchFile(filePath, options, (curr, prev) => {
    curr.mtime === 0 && fs.unwatchFile(filePath) // Unwatch if deleted.
    callback(filePath)
  })
}

function watch (entitiesToWatch, arg1, arg2) {
  const callback = arg2 || arg1
  const options = arg2 ? arg1 : { tolerance: TOLERANCE }
  options.tolerance = process.platform === 'win32' ? (options.tolerance || TOLERANCE) : 0 // Disable tolerance if not on Windows.
  options.fallback = options.fallback || !PLATFORMS.includes(process.platform)

  entitiesToWatch = entitiesToWatch.constructor === Array ? entitiesToWatch : [entitiesToWatch] // Normalize to array.
  entitiesToWatch = entitiesToWatch.map(entityToWatch => path.resolve(entityToWatch)) // Resolve directory paths.

  for (const entityToWatch of entitiesToWatch) {
    if (!fs.statSync(entityToWatch).isDirectory()) {
      watchFile(entityToWatch, options, callback)
    } else {
      options.fallback ? watchDirFallback(entityToWatch, options, callback) : watchDir(entityToWatch, options, callback)
    }
  }
}

watch.main = function () {
  const args = process.argv.slice(2)
  const entitiesToWatch = args.filter(a => !a.startsWith('--'))
  const options = { shallow: args.includes('--shallow'), fallback: args.includes('--fallback') }

  watch(entitiesToWatch, options, fileName => {
    console.log(`${fileName}`)
  })
}

if (require.main === module) {
  watch.main()
}

module.exports = watch
