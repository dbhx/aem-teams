'use strict'

const watch = require('../index')
const path = require('path')

const toWatch = path.resolve(__dirname, 'root', 'level1-file')
const options = {
  shallow: false,
  fallback: true,
  interval: 100
}

watch(toWatch, options, fileName => {
  console.log(`${fileName}`, options.ledger)
})
