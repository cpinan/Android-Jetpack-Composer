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

package androidx.ui.graphics.colorspace

import androidx.annotation.IntRange
import androidx.annotation.Size

/**
 * Implementation of the CIE XYZ color space. Assumes the white point is D50.
 */
internal class Xyz(
    name: String,
    @IntRange(from = MinId.toLong(), to = MaxId.toLong()) id: Int
) : ColorSpace(name,
    ColorModel.Xyz, id) {

    override val isWideGamut: Boolean
        get() = true

    override fun getMinValue(@IntRange(from = 0, to = 3) component: Int): Float {
        return -2.0f
    }

    override fun getMaxValue(@IntRange(from = 0, to = 3) component: Int): Float {
        return 2.0f
    }

    override fun toXyz(@Size(min = 3) v: FloatArray): FloatArray {
        v[0] = clamp(v[0])
        v[1] = clamp(v[1])
        v[2] = clamp(v[2])
        return v
    }

    override fun fromXyz(@Size(min = 3) v: FloatArray): FloatArray {
        v[0] = clamp(v[0])
        v[1] = clamp(v[1])
        v[2] = clamp(v[2])
        return v
    }

    private fun clamp(x: Float): Float {
        return x.coerceIn(-2f, 2f)
    }
}