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

package androidx.ui.semantics

import androidx.ui.text.style.TextDirection

object SemanticsProperties {
    val AccessibilityLabel = object : SemanticsPropertyKey<String>("AccessibilityLabel") {
        override fun merge(existingValue: String, newValue: String): String {
            // TODO(b/138173613): Needs TextDirection, probably needs to pass both nodes
            //  to retrieve it
            return existingValue + "\n" + newValue
        }
    }

    val AccessibilityValue = SemanticsPropertyKey<String>("AccessibilityValue")

    val Enabled = SemanticsPropertyKey<Boolean>("Enabled")

    val Hidden = SemanticsPropertyKey<Boolean>("Hidden")

    val TextDirection = SemanticsPropertyKey<TextDirection>("TextDirection")

    // TODO(b/138172781): Move to FoundationSemanticsProperties
    val TestTag = SemanticsPropertyKey<String>("TestTag")
}

class SemanticsActions {
    companion object {
        val OnClick = SemanticsPropertyKey<AccessibilityAction<() -> Unit>>("OnClick")

        val CustomActions =
            SemanticsPropertyKey<List<AccessibilityAction<() -> Unit>>>("CustomActions")
    }
}

var SemanticsPropertyReceiver.accessibilityLabel by SemanticsProperties.AccessibilityLabel

var SemanticsPropertyReceiver.accessibilityValue by SemanticsProperties.AccessibilityValue

var SemanticsPropertyReceiver.enabled by SemanticsProperties.Enabled

var SemanticsPropertyReceiver.hidden by SemanticsProperties.Hidden

var SemanticsPropertyReceiver.textDirection by SemanticsProperties.TextDirection

var SemanticsPropertyReceiver.onClick by SemanticsActions.OnClick

fun SemanticsPropertyReceiver.onClick(label: String? = null, action: () -> Unit) {
    this[SemanticsActions.OnClick] = AccessibilityAction(label, action)
}

var SemanticsPropertyReceiver.customActions by SemanticsActions.CustomActions

// TODO(b/138172781): Move to FoundationSemanticsProperties.kt
var SemanticsPropertyReceiver.testTag by SemanticsProperties.TestTag

// TODO(b/138173613): Use this for merging labels
/*
private fun concatStrings(
    thisString: String?,
    otherString: String?,
    thisTextDirection: TextDirection?,
    otherTextDirection: TextDirection?
): String? {
    if (otherString.isNullOrEmpty())
        return thisString
    var nestedLabel = otherString
    if (thisTextDirection != otherTextDirection && otherTextDirection != null) {
        nestedLabel = when (otherTextDirection) {
            TextDirection.Rtl -> "${Unicode.RLE}$nestedLabel${Unicode.PDF}"
            TextDirection.Ltr -> "${Unicode.LRE}$nestedLabel${Unicode.PDF}"
        }
    }
    if (thisString.isNullOrEmpty())
        return nestedLabel
    return "$thisString\n$nestedLabel"
}
*/
