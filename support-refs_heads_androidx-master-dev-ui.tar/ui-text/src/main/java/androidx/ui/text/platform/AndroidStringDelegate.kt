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

package androidx.ui.text.platform

import androidx.ui.text.PlatformStringDelegate

/**
 * An Android implementation of StringDelegate
 */
internal class AndroidStringDelegate : PlatformStringDelegate {
    override fun toUpperCase(string: String, locale: PlatformLocale): String =
        string.toUpperCase((locale as AndroidLocale).javaLocale)

    override fun toLowerCase(string: String, locale: PlatformLocale): String =
        string.toLowerCase((locale as AndroidLocale).javaLocale)

    override fun capitalize(string: String, locale: PlatformLocale): String =
        // TODO(nona): pass locale when capitalize with locale is out of experiment.
        string.capitalize()

    override fun decapitalize(string: String, locale: PlatformLocale): String =
        // TODO(nona): pass locale when decapitalize with locale is out of experiment.
        string.decapitalize()
}