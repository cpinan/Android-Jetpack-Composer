/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("NOTHING_TO_INLINE")

package androidx.ui.core

import androidx.compose.Immutable
import androidx.ui.core.IntPx.Companion.Infinity
import androidx.ui.lerp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Dimension value represented in whole pixels (px). Layout and constraints operate on Int
 * pixels. Operations with an [Infinity] IntPx result in [Infinity].
 */
@Suppress("EXPERIMENTAL_FEATURE_WARNING")
@Immutable
data /*inline*/ class IntPx(val value: Int) {
    /**
     * Add two [IntPx]s together. Any operation on an
     * [IntPx.Infinity] results in [IntPx.Infinity]
     */
    operator fun plus(other: IntPx) =
        keepInfinity(other, IntPx(value = this.value + other.value))

    /**
     * Subtract a IntPx from another one. Any operation on an
     * [IntPx.Infinity] results in [IntPx.Infinity]
     */
    operator fun minus(other: IntPx) =
        keepInfinity(other, IntPx(value = this.value - other.value))

    /**
     * This is the same as multiplying the IntPx by -1. Any operation on an
     * [IntPx.Infinity] results in [IntPx.Infinity]
     */
    operator fun unaryMinus() = keepInfinity(IntPx(-value))

    /**
     * Divide a IntPx by a scalar and return the rounded result as an IntPx. Any operation on an
     * [IntPx.Infinity] results in [IntPx.Infinity]
     */
    operator fun div(other: Float): IntPx =
        keepInfinity(IntPx(value = (value.toFloat() / other).roundToInt()))

    /**
     * Divide a IntPx by a scalar and return the rounded result as an IntPx. Any operation on an
     * [IntPx.Infinity] results in [IntPx.Infinity]
     */
    operator fun div(other: Double): IntPx =
        keepInfinity(IntPx(value = (value.toDouble() / other).roundToInt()))

    /**
     * Divide a IntPx by a scalar and return the rounded result as an IntPx. Any operation on an
     * [IntPx.Infinity] results in [IntPx.Infinity]
     */
    operator fun div(other: Int): IntPx =
        keepInfinity(IntPx(value = (value.toFloat() / other).roundToInt()))

    /**
     * Multiply a IntPx by a scalar and round the result to an IntPx. Any operation on an
     * [IntPx.Infinity] results in [IntPx.Infinity]
     */
    operator fun times(other: Float): IntPx =
        keepInfinity(IntPx(value = (value.toFloat() * other).roundToInt()))

    /**
     * Multiply a IntPx by a scalar and round the result to an IntPx
     */
    operator fun times(other: Double): IntPx =
        keepInfinity(IntPx(value = (value.toDouble() * other).roundToInt()))

    /**
     * Multiply a IntPx by a scalar and result in an IntPx
     */
    operator fun times(other: Int): IntPx =
        keepInfinity(IntPx(value = value * other))

    /**
     * Returns the remainder of the IntPx when dividing by an integer.
     */
    inline operator fun rem(other: Int): IntPx =
        IntPx(value = value % other)

    /**
     * Support comparing Dimensions with comparison operators.
     */
    inline operator fun compareTo(other: IntPx) = value.compareTo(other.value)

    /**
     * Compares this [IntPx] to another [Px]
     */
    inline operator fun compareTo(other: Px) = value.compareTo(other.value)

    override fun toString() = "$value.ipx"

    companion object {
        /**
         * An IntPx that indicates that there is no bound in the dimension. This is
         * commonly used in [Constraints.maxHeight] and [Constraints.maxWidth] to indicate
         * that the particular dimension is not regulated and measurement should choose
         * the best option without any constraint.
         */
        val Infinity = IntPx(value = Int.MAX_VALUE)

        /**
         * Zero IntPx dimension. Same as `0.ipx`.
         */
        val Zero = IntPx(value = 0)
    }
}

/**
 * Return whether `true` when it is finite or `false` when it is [IntPx.Infinity]
 */
inline fun IntPx.isFinite(): Boolean = value != Int.MAX_VALUE

@PublishedApi
internal inline fun IntPx.keepInfinity(other: IntPx, noInfinityValue: IntPx): IntPx {
    return if (!isFinite() || !other.isFinite()) Infinity else noInfinityValue
}

@PublishedApi
internal inline fun IntPx.keepInfinity(noInfinityValue: IntPx): IntPx {
    return if (!isFinite()) this else noInfinityValue
}

/**
 * Create a [IntPx] using an [Int]:
 *     val left = 10
 *     val x = left.ipx
 *     // -- or --
 *     val y = 10.ipx
 */
inline val Int.ipx: IntPx get() = IntPx(value = this)

/**
 * Multiply an IntPx by a Float and round the result to an IntPx. Any operation on an
 * [IntPx.Infinity] results in [IntPx.Infinity]
 */
inline operator fun Float.times(other: IntPx): IntPx =
    other.keepInfinity(IntPx(value = (other.value.toFloat() * this).roundToInt()))

/**
 * Multiply an IntPx by a Double and round the result to an IntPx. Any operation on an
 * [IntPx.Infinity] results in [IntPx.Infinity]
 */
inline operator fun Double.times(other: IntPx): IntPx =
    other.keepInfinity(IntPx(value = (other.value.toDouble() * this).roundToInt()))

/**
 * Multiply an IntPx by a Double to result in an IntPx. Any operation on an
 * [IntPx.Infinity] results in [IntPx.Infinity]
 */
inline operator fun Int.times(other: IntPx): IntPx =
    other.keepInfinity(IntPx(value = other.value * this))

/**
 * Return the minimum of two [IntPx]s. Any value is considered less than [IntPx.Infinity].
 */
inline fun min(a: IntPx, b: IntPx): IntPx =
    IntPx(value = min(a.value, b.value))

/**
 * Return the maximum of two [IntPx]s. An [IntPx.Infinity] is considered the maximum value.
 */
inline fun max(a: IntPx, b: IntPx): IntPx =
    IntPx(value = max(a.value, b.value))

/**
 * Ensures that this value lies in the specified range [minimumValue]..[maximumValue].
 *
 * @return this value if it's in the range, or [minimumValue] if this value is less than
 * [minimumValue], or [maximumValue] if this value is greater than [maximumValue].
 */
inline fun IntPx.coerceIn(minimumValue: IntPx, maximumValue: IntPx): IntPx =
    IntPx(value = value.coerceIn(minimumValue.value, maximumValue.value))

/**
 * Ensures that this value is not less than the specified [minimumValue].
 *
 * @return this value if it's greater than or equal to the [minimumValue] or the
 * [minimumValue] otherwise.
 */
inline fun IntPx.coerceAtLeast(minimumValue: IntPx): IntPx =
    IntPx(value = value.coerceAtLeast(minimumValue.value))

/**
 * Ensures that this value is not greater than the specified [maximumValue].
 *
 * @return this value if it's less than or equal to the [maximumValue] or the
 * [maximumValue] otherwise. Passing [IntPx.Infinity] as [maximumValue] will
 * always return this.
 */
inline fun IntPx.coerceAtMost(maximumValue: IntPx): IntPx =
    IntPx(value = value.coerceAtMost(maximumValue.value))

/**
 * Linearly interpolate between two [IntPx]s.
 *
 * The [fraction] argument represents position on the timeline, with 0.0 meaning
 * that the interpolation has not started, returning [start] (or something
 * equivalent to [start]), 1.0 meaning that the interpolation has finished,
 * returning [stop] (or something equivalent to [stop]), and values in between
 * meaning that the interpolation is at the relevant point on the timeline
 * between [start] and [stop]. The interpolation can be extrapolated beyond 0.0 and
 * 1.0, so negative values and values greater than 1.0 are valid.
 *
 * If [start] or [stop] is [IntPx.Infinity], then [IntPx.Infinity] is returned.
 */
fun lerp(start: IntPx, stop: IntPx, fraction: Float): IntPx {
    return start.keepInfinity(stop, IntPx(lerp(start.value, stop.value, fraction)))
}

/**
 * Rounds a [Px] size to the nearest Int pixel value.
 */
inline fun Px.round(): IntPx =
    if (value.isInfinite()) Infinity else IntPx(value.roundToInt())

inline fun IntPx.toPx(): Px = value.px

// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
// Structures using IntPx
// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

/**
 * A two dimensional size using [IntPx] for units
 */
@UseExperimental(ExperimentalUnsignedTypes::class)
@Immutable
data class IntPxSize @PublishedApi internal constructor(@PublishedApi internal val value: Long) {
    /**
     * The horizontal aspect of the size in [IntPx].
     */
    inline val width: IntPx
        get() = unpackInt1(value).ipx

    /**
     * The vertical aspect of the size in [IntPx].
     */
    inline val height: IntPx
        get() = unpackInt2(value).ipx

    /**
     * Returns an IntPxSize scaled by multiplying [width] and [height] by [other]
     */
    inline operator fun times(other: Int): IntPxSize =
        IntPxSize(width = width * other, height = height * other)

    /**
     * Returns an IntPxSize scaled by dividing [width] and [height] by [other]
     */
    inline operator fun div(other: Int): IntPxSize =
        IntPxSize(width = width / other, height = height / other)
}

/**
 * Constructs an [IntPxSize] from width and height [IntPx] values.
 */
@UseExperimental(ExperimentalUnsignedTypes::class)
inline fun IntPxSize(width: IntPx, height: IntPx): IntPxSize =
    IntPxSize(packInts(width.value, height.value))

/**
 * Returns an [IntPxSize] with [size]'s [IntPxSize.width] and [IntPxSize.height]
 * multiplied by [this]
 */
inline operator fun Int.times(size: IntPxSize) = size * this

/**
 * Returns the [IntPxPosition] of the center of the rect from the point of [0, 0]
 * with this [IntPxSize].
 */
fun IntPxSize.center(): IntPxPosition {
    return IntPxPosition(width / 2f, height / 2f)
}

/**
 * A two-dimensional position using [IntPx] for units
 */
@UseExperimental(ExperimentalUnsignedTypes::class)
@Immutable
data class IntPxPosition @PublishedApi internal constructor(
    @PublishedApi internal val value: Long
) {
    /**
     * The horizontal aspect of the position in [IntPx]
     */
    inline val x: IntPx
        get() = unpackInt1(value).ipx

    /**
     * The vertical aspect of the position in [IntPx]
     */
    inline val y: IntPx
        get() = unpackInt2(value).ipx

    /**
     * Subtract a [IntPxPosition] from another one.
     */
    inline operator fun minus(other: IntPxPosition) =
        IntPxPosition(x - other.x, y - other.y)

    /**
     * Add a [IntPxPosition] to another one.
     */
    inline operator fun plus(other: IntPxPosition) =
        IntPxPosition(x + other.x, y + other.y)

    /**
     * Returns a new PxPosition representing the negation of this point.
     */
    inline operator fun unaryMinus() = IntPxPosition(-x, -y)

    companion object {
        val Origin = IntPxPosition(0.ipx, 0.ipx)
    }
}

/**
 * Constructs a [IntPxPosition] from [x] and [y] position [IntPx] values.
 */
@UseExperimental(ExperimentalUnsignedTypes::class)
inline fun IntPxPosition(x: IntPx, y: IntPx): IntPxPosition =
    IntPxPosition(packInts(x.value, y.value))

/**
 * Linearly interpolate between two [IntPxPosition]s.
 *
 * The [fraction] argument represents position on the timeline, with 0.0 meaning
 * that the interpolation has not started, returning [start] (or something
 * equivalent to [start]), 1.0 meaning that the interpolation has finished,
 * returning [stop] (or something equivalent to [stop]), and values in between
 * meaning that the interpolation is at the relevant point on the timeline
 * between [start] and [stop]. The interpolation can be extrapolated beyond 0.0 and
 * 1.0, so negative values and values greater than 1.0 are valid.
 */
fun lerp(start: IntPxPosition, stop: IntPxPosition, fraction: Float): IntPxPosition =
    IntPxPosition(lerp(start.x, stop.x, fraction), lerp(start.y, stop.y, fraction))

/**
 * A four dimensional bounds using [IntPx] for units
 */
data class IntPxBounds(
    val left: IntPx,
    val top: IntPx,
    val right: IntPx,
    val bottom: IntPx
)

/**
 * A width of this IntPxBounds in [IntPx].
 */
inline val IntPxBounds.width: IntPx get() = right - left

/**
 * A height of this IntPxBounds in [IntPx].
 */
inline val IntPxBounds.height: IntPx get() = bottom - top

/**
 * Convert a [IntPxBounds] to a [IntPxSize].
 */
inline fun IntPxBounds.toSize(): IntPxSize {
    return IntPxSize(width, height)
}

/**
 * Create a [PxSize] from [IntPx] values.
 */
inline fun PxSize(width: IntPx, height: IntPx): PxSize =
    PxSize(width = width.toPx(), height = height.toPx())

/**
 * Create a [PxPosition] from [IntPx] values.
 */
inline fun PxPosition(x: IntPx, y: IntPx): PxPosition = PxPosition(x = x.toPx(), y = y.toPx())

/**
 * Convert a [IntPxPosition] to a [PxPosition]
 */
inline fun IntPxPosition.toPxPosition(): PxPosition = PxPosition(this.x, this.y)

/**
 * Convert a [IntPxSize] to a [PxSize]
 */
inline fun IntPxSize.toPxSize(): PxSize = PxSize(this.width, this.height)