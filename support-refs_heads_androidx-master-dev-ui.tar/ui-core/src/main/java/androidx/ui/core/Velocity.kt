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

package androidx.ui.core

/** A velocity in two dimensions. */
data class Velocity(
    /** The number of pixels per second of velocity in the x and y directions. */
    val pixelsPerSecond: PxPosition
) {
    /** Return the negation of a velocity. */
    operator fun unaryMinus() =
        Velocity(pixelsPerSecond = -pixelsPerSecond)

    companion object {
        /**
         * Velocity of 0 pixels per second in both x and y.
         */
        val Zero = Velocity(PxPosition(0.px, 0.px))
    }
}

/** Return the difference of two velocities. */
operator fun Velocity.minus(other: Velocity) =
    Velocity(pixelsPerSecond = pixelsPerSecond - other.pixelsPerSecond)

/** Return the sum of two velocities. */
operator fun Velocity.plus(other: Velocity) =
    Velocity(pixelsPerSecond = pixelsPerSecond + other.pixelsPerSecond)