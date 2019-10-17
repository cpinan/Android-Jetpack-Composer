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

package androidx.ui.core.gesture.util

import kotlin.math.sqrt

/**
 *  TODO (shepshapard): If we want to support varying weights for each position, we could accept a
 *  3rd FloatArray of weights for each point and use them instead of the [DefaultWeight].
 */
private const val DefaultWeight = 1f

/**
 * Fits a polynomial of the given degree to the data points.
 *
 * If the [degree] is larger than or equal to the number of points, a polynomial will be returned
 * with coefficients of the value 0 for all degrees larger than or equal to the number of points.
 * For example, if 2 data points are provided and a quadratic polynomial (degree of 2) is requested,
 * the resulting polynomial ax^2 + bx + c is guaranteed to have a = 0;
 *
 * Throws an IllegalArgumentException if:
 * <ul>
 *   <li>[degree] is not a positive integer.
 *   <li>[x] and [y] are not the same size.
 *   <li>[x] or [y] are empty.
 *   <li>(some other reason that
 * </ul>
 *
 */
internal fun polyFitLeastSquares(
    /** The x-coordinates of each data point. */
    x: List<Float>,
    /** The y-coordinates of each data point. */
    y: List<Float>,
    degree: Int
): PolynomialFit {
    if (degree < 1) {
        throw IllegalArgumentException("The degree must be at positive integer")
    }
    if (x.size != y.size) {
        throw IllegalArgumentException("x and y must be the same length")
    }
    if (x.isEmpty()) {
        throw IllegalArgumentException("At least one point must be provided")
    }

    val truncatedDegree =
        if (degree >= x.size) {
            x.size - 1
        } else {
            degree
        }

    val coefficients = MutableList(degree + 1) { 0.0f }

    // Shorthands for the purpose of notation equivalence to original C++ code.
    val m: Int = x.size
    val n: Int = truncatedDegree + 1

    // Expand the X vector to a matrix A, pre-multiplied by the weights.
    val a = Matrix(n, m)
    for (h in 0 until m) {
        a.set(0, h, DefaultWeight)
        for (i in 1 until n) {
            a.set(i, h, a.get(i - 1, h) * x[h])
        }
    }

    // Apply the Gram-Schmidt process to A to obtain its QR decomposition.

    // Orthonormal basis, column-major ordVectorer.
    val q = Matrix(n, m)
    // Upper triangular matrix, row-major order.
    val r = Matrix(n, n)
    for (j in 0 until n) {
        for (h in 0 until m) {
            q.set(j, h, a.get(j, h))
        }
        for (i in 0 until j) {
            val dot: Float = q.getRow(j) * q.getRow(i)
            for (h in 0 until m) {
                q.set(j, h, q.get(j, h) - dot * q.get(i, h))
            }
        }

        val norm: Float = q.getRow(j).norm()
        if (norm < 0.000001) {
            // TODO(b/129494471): Determine what this actually means and see if there are
            // alternatives to throwing an Exception here.

            // Vectors are linearly dependent or zero so no solution.
            throw IllegalArgumentException("Vectors are linearly dependent or zero so no " +
                    "solution. TODO(shepshapard), actually determine what this means")
        }

        val inverseNorm: Float = 1.0f / norm
        for (h in 0 until m) {
            q.set(j, h, q.get(j, h) * inverseNorm)
        }
        for (i in 0 until n) {
            r.set(j, i, if (i < j) 0.0f else q.getRow(j) * a.getRow(i))
        }
    }

    // Solve R B = Qt W Y to find B. This is easy because R is upper triangular.
    // We just work from bottom-right to top-left calculating B's coefficients.
    val wy = Vector(m)
    for (h in 0 until m) {
        wy[h] = y[h] * DefaultWeight
    }
    for (i in n - 1 downTo 0) {
        coefficients[i] = q.getRow(i) * wy
        for (j in n - 1 downTo i + 1) {
            coefficients[i] -= r.get(i, j) * coefficients[j]
        }
        coefficients[i] /= r.get(i, i)
    }

    // Calculate the coefficient of determination (confidence) as:
    //   1 - (sumSquaredError / sumSquaredTotal)
    // ...where sumSquaredError is the residual sum of squares (variance of the
    // error), and sumSquaredTotal is the total sum of squares (variance of the
    // data) where each has been weighted.
    var yMean = 0.0f
    for (h in 0 until m) {
        yMean += y[h]
    }
    yMean /= m

    var sumSquaredError = 0.0f
    var sumSquaredTotal = 0.0f
    for (h in 0 until m) {
        var term = 1.0f
        var err: Float = y[h] - coefficients[0]
        for (i in 1 until n) {
            term *= x[h]
            err -= term * coefficients[i]
        }
        sumSquaredError += DefaultWeight * DefaultWeight * err * err
        val v = y[h] - yMean
        sumSquaredTotal += DefaultWeight * DefaultWeight * v * v
    }

    val confidence =
        if (sumSquaredTotal <= 0.000001f) 1f else 1f - (sumSquaredError / sumSquaredTotal)

    return PolynomialFit(coefficients, confidence)
}

internal data class PolynomialFit(
    val coefficients: List<Float>,
    val confidence: Float
)

private class Vector(
    val length: Int
) {
    val elements: Array<Float> = Array(length) { 0.0f }

    operator fun get(i: Int) = elements[i]

    operator fun set(i: Int, value: Float) {
        elements[i] = value
    }

    operator fun times(a: Vector): Float {
        var result = 0.0f
        for (i in 0 until length) {
            result += this[i] * a[i]
        }
        return result
    }

    fun norm(): Float = sqrt(this * this)
}

private class Matrix(rows: Int, cols: Int) {
    private val elements: Array<Vector> = Array(rows) { Vector(cols) }

    fun get(row: Int, col: Int): Float {
        return elements[row][col]
    }

    fun set(row: Int, col: Int, value: Float) {
        elements[row][col] = value
    }

    fun getRow(row: Int): Vector {
        return elements[row]
    }
}