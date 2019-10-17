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

package androidx.ui.res

import androidx.compose.unaryPlus
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.ui.core.ContextAmbient
import androidx.ui.framework.test.R
import androidx.ui.test.createComposeRule
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Locale

@RunWith(JUnit4::class)
@SmallTest
class ResourcesTest {

    // Constants defined in strings.xml
    private val NotLocalizedText = "NotLocalizedText"
    private val DefaultLocalizedText = "DefaultLocaleText"
    private val SpanishLocalizedText = "SpanishText"

    // Constants defined in strings.xml with formatting with integer 100.
    private val NotLocalizedFormatText = "NotLocalizedFormatText:100"
    private val DefaultLocalizedFormatText = "DefaultLocaleFormatText:100"
    private val SpanishLocalizedFormatText = "SpanishFormatText:100"

    // Constant used for formatting string in test.
    private val FormatValue = 100

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun stringResource_not_localized() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            ContextAmbient.Provider(value = context) {
                assertThat(+stringResource(R.string.not_localized)).isEqualTo(NotLocalizedText)
            }
        }

        val spanishContext = context.createConfigurationContext(
            context.resources.configuration.apply {
                setLocale(Locale.forLanguageTag("es-ES"))
            }
        )

        composeTestRule.setContent {
            ContextAmbient.Provider(value = spanishContext) {
                assertThat(+stringResource(R.string.not_localized)).isEqualTo(NotLocalizedText)
            }
        }
    }

    @Test
    fun stringResource_localized() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            ContextAmbient.Provider(value = context) {
                assertThat(+stringResource(R.string.localized))
                    .isEqualTo(DefaultLocalizedText)
            }
        }

        val spanishContext = context.createConfigurationContext(
            context.resources.configuration.apply {
                setLocale(Locale.forLanguageTag("es-ES"))
            }
        )

        composeTestRule.setContent {
            ContextAmbient.Provider(value = spanishContext) {
                assertThat(+stringResource(R.string.localized))
                    .isEqualTo(SpanishLocalizedText)
            }
        }
    }

    @Test
    fun stringResource_not_localized_format() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            ContextAmbient.Provider(value = context) {
                assertThat(+stringResource(R.string.not_localized_format, FormatValue))
                    .isEqualTo(NotLocalizedFormatText)
            }
        }

        val spanishContext = context.createConfigurationContext(
            context.resources.configuration.apply {
                setLocale(Locale.forLanguageTag("es-ES"))
            }
        )

        composeTestRule.setContent {
            ContextAmbient.Provider(value = spanishContext) {
                assertThat(+stringResource(R.string.not_localized_format, FormatValue))
                    .isEqualTo(NotLocalizedFormatText)
            }
        }
    }

    @Test
    fun stringResource_localized_format() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            ContextAmbient.Provider(value = context) {
                assertThat(+stringResource(R.string.localized_format, FormatValue))
                    .isEqualTo(DefaultLocalizedFormatText)
            }
        }

        val spanishContext = context.createConfigurationContext(
            context.resources.configuration.apply {
                setLocale(Locale.forLanguageTag("es-ES"))
            }
        )

        composeTestRule.setContent {
            ContextAmbient.Provider(value = spanishContext) {
                assertThat(+stringResource(R.string.localized_format, FormatValue))
                    .isEqualTo(SpanishLocalizedFormatText)
            }
        }
    }
}