package extension

val List<Any>.countSuffix: String
    get() = if (count() == 1) "" else "s"