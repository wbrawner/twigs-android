package com.wbrawner.twigs.shared.recurringtransaction

import com.wbrawner.twigs.shared.startOfMonth
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class RecurringTransaction(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val frequency: Frequency,
    val start: Instant,
    val finish: Instant? = null,
    val amount: Long,
    val categoryId: String? = null,
    val budgetId: String,
    val expense: Boolean,
    val createdBy: String
)

@Serializable(with = FrequencySerializer::class)
sealed class Frequency {
    abstract val count: Int
    abstract val time: Time
    abstract val name: String
    abstract val description: String

    data class Daily(override val count: Int, override val time: Time) : Frequency() {
        override fun toString(): String = "D;$count;$time"
        override val name: String = "Daily"
        override val description: String
            get() = if (count == 1) "Every day" else "Every $count days"

        companion object {
            fun parse(s: String): Daily {
                require(s[0] == 'D') { "Invalid format for Daily: $s" }
                return with(s.split(';')) {
                    Daily(
                        get(1).toInt(),
                        Time.parse(get(2))
                    )
                }
            }
        }
    }

    data class Weekly(
        override val count: Int,
        val daysOfWeek: Set<DayOfWeek>,
        override val time: Time
    ) : Frequency() {
        override fun toString(): String = "W;$count;${daysOfWeek.joinToString(",")};$time"
        override val name: String = "Weekly"
        override val description: String
            get() = if (count == 1) "Every week on ${daysOfWeek.joinToString(", ") { it.capitalizedName }}"
            else "Every $count weeks on ${daysOfWeek.joinToString(", ") { it.capitalizedName }}"

        companion object {
            fun parse(s: String): Weekly {
                require(s[0] == 'W') { "Invalid format for Weekly: $s" }
                return with(s.split(';')) {
                    Weekly(
                        get(1).toInt(),
                        get(2).split(',').map { DayOfWeek.valueOf(it) }.toSet(),
                        Time.parse(get(3))
                    )
                }
            }
        }
    }

    data class Monthly(
        override val count: Int,
        val dayOfMonth: DayOfMonth,
        override val time: Time
    ) : Frequency() {
        override fun toString(): String = "M;$count;$dayOfMonth;$time"
        override val name: String = "Monthly"
        override val description: String
            get() = if (count == 1) "Every month on the ${dayOfMonth.description}"
            else "Every $count months on ${dayOfMonth.description}"

        companion object {
            fun parse(s: String): Monthly {
                require(s[0] == 'M') { "Invalid format for Monthly: $s" }
                return with(s.split(';')) {
                    Monthly(
                        get(1).toInt(),
                        DayOfMonth.parse(get(2)),
                        Time.parse(get(3))
                    )
                }
            }
        }
    }

    data class Yearly(override val count: Int, val dayOfYear: DayOfYear, override val time: Time) :
        Frequency() {
        override fun toString(): String =
            "Y;$count;${dayOfYear.month.padStart(2, '0')}-${dayOfYear.day.padStart(2, '0')};$time"

        override val name: String = "Yearly"
        override val description: String
            get() = if (count == 1) "Every year on ${dayOfYear.description}"
            else "Every $count years on ${dayOfYear.description}"

        companion object {
            fun parse(s: String): Yearly {
                require(s[0] == 'Y') { "Invalid format for Yearly: $s" }
                return with(s.split(';')) {
                    Yearly(
                        get(1).toInt(),
                        DayOfYear.parse(get(2)),
                        Time.parse(get(3))
                    )
                }
            }
        }
    }

    fun instant(now: Instant): Instant =
        Instant.parse(now.toString().split("T")[0] + "T" + time.toString() + "Z")

    fun update(count: Int = this.count, time: Time = this.time): Frequency = when (this) {
        is Daily -> copy(count = count, time = time)
        is Weekly -> copy(count = count, time = time)
        is Monthly -> copy(count = count, time = time)
        is Yearly -> copy(count = count, time = time)
    }

    companion object {
        fun parse(s: String): Frequency = when (s[0]) {
            'D' -> Daily.parse(s)
            'W' -> Weekly.parse(s)
            'M' -> Monthly.parse(s)
            'Y' -> Yearly.parse(s)
            else -> throw IllegalArgumentException("Invalid frequency format: $s")
        }
    }
}


sealed class DayOfMonth {
    abstract val description: String

    data class OrdinalDayOfMonth(val ordinal: Ordinal, val dayOfWeek: DayOfWeek) : DayOfMonth() {
        override val description: String
            get() = "${ordinal.capitalizedName} ${dayOfWeek.capitalizedName}"

        override fun toString(): String = "${ordinal.name}-${dayOfWeek.name}"
    }

    data class FixedDayOfMonth(val day: Int) : DayOfMonth() {
        override val description: String = day.ordinalString

        override fun toString(): String = "DAY-$day"
    }

    companion object {
        fun parse(s: String): DayOfMonth = with(s.split("-")) {
            when (size) {
                2 -> when (first()) {
                    "DAY" -> FixedDayOfMonth(get(1).toInt())
                    else -> OrdinalDayOfMonth(
                        Ordinal.valueOf(first()),
                        DayOfWeek.valueOf(get(1))
                    )
                }

                else -> throw IllegalArgumentException("Failed to parse DayOfMonth: $s")
            }
        }
    }
}

enum class Ordinal {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    LAST
}

val Enum<*>.capitalizedName: String
    get() = name.lowercase().replaceFirstChar { it.uppercaseChar() }

class DayOfYear private constructor(val month: Int, val day: Int) {

    val description: String
        get() = "${month.toMonth().capitalizedName} ${day.ordinalString}"

    override fun toString(): String {
        return "${month.padStart(2, '0')}-${day.padStart(2, '0')}"
    }

    companion object {
        private fun maxDays(month: Int): Int = when (month) {
            2 -> 29
            4, 6, 9, 11 -> 30
            else -> 31
        }

        fun of(month: Int, day: Int): DayOfYear {
            require(month in 1..12) { "Invalid value for month: $month" }
            require(day in 1..maxDays(month)) { "Invalid value for day: $day" }
            return DayOfYear(month, day)
        }

        fun parse(s: String): DayOfYear {
            val (month, day) = s.split("-").map { it.toInt() }
            return of(month, day)
        }
    }
}

fun Int.toMonth(): Month = Month(this)

val Int.ordinalString: String
    get() = when {
        mod(1) == 0 -> "${this}st"
        mod(2) == 0 -> "${this}nd"
        mod(3) == 0 -> "${this}rd"
        else -> "${this}th"
    }

data class Time(val hours: Int, val minutes: Int, val seconds: Int) {
    override fun toString(): String {
        val s = StringBuilder()
        if (hours < 10) {
            s.append("0")
        }
        s.append(hours)
        s.append(":")
        if (minutes < 10) {
            s.append("0")
        }
        s.append(minutes)
        s.append(":")
        if (seconds < 10) {
            s.append("0")
        }
        s.append(seconds)
        return s.toString()
    }

    companion object {
        fun parse(s: String): Time {
            require(s.length < 9) { "Invalid time format: $s. Time should be formatted as HH:mm:ss" }
            require(s[2] == ':') { "Invalid time format: $s. Time should be formatted as HH:mm:ss" }
            require(s[5] == ':') { "Invalid time format: $s. Time should be formatted as HH:mm:ss" }
            return Time(
                s.substring(0, 2).toInt(),
                s.substring(3, 5).toInt(),
                s.substring(7).toInt(),
            )
        }
    }
}

fun Instant.time(): Time = with(toLocalDateTime(TimeZone.UTC).time) {
    Time(hour, minute, second)
}

fun Int.padStart(length: Int, char: Char): String {
    var stringValue = toString()
    while (stringValue.length < length) stringValue = char + stringValue
    return stringValue
}

object FrequencySerializer : KSerializer<Frequency> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Frequency", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Frequency) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): Frequency = Frequency.parse(decoder.decodeString())
}

// TODO: This needs to take into account the last run date, which isn't currently returned by the
//  server
val RecurringTransaction.isThisMonth: Boolean
    get() = !isExpired && when (frequency) {
        is Frequency.Daily -> true
        is Frequency.Weekly -> true
        is Frequency.Monthly -> true
        is Frequency.Yearly -> Clock.System.now()
            .toLocalDateTime(TimeZone.UTC).monthNumber == frequency.dayOfYear.month
    }

val RecurringTransaction.isExpired: Boolean
    get() = finish != null && startOfMonth() > finish