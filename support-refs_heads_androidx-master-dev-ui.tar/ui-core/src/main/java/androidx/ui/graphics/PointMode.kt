/*
 * Copyright 2018 The Android Open Source Project
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

package androidx.ui.graphics

/**
 * Defines how a list of points is interpreted when drawing a set of points.
 *
 */
// ignore: deprecated_member_use
/** Used by [Canvas.drawPoints]. */
// These enum values must be kept in sync with SkCanvas::PointMode.
enum class PointMode {
    /**
     * Draw each point separately.
     *
     * If the [Paint.strokeCap] is [StrokeCap.round], then each point is drawn
     * as a circle with the diameter of the [Paint.strokeWidth], filled as
     * described by the [Paint] (ignoring [Paint.style]).
     *
     * Otherwise, each point is drawn as an axis-aligned square with sides of
     * length [Paint.strokeWidth], filled as described by the [Paint] (ignoring
     * [Paint.style]).
     */
    points,

    /**
     * Draw each sequence of two points as a line segment.
     *
     * If the number of points is odd, then the last point is ignored.
     *
     * The lines are stroked as described by the [Paint] (ignoring
     * [Paint.style]).
     */
    lines,

    /**
     * Draw the entire sequence of point as one line.
     *
     * The lines are stroked as described by the [Paint] (ignoring
     * [Paint.style]).
     */
    polygon
}