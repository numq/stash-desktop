package extension

import file.File

fun File.fullTitle() = "${name}.${extension}"

fun File.kindTitle() =
    if (name.length > 10) "${name.take(5)}...${name.takeLast(5)}.${extension}" else "${name}.${extension}"