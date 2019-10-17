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

package androidx.ui.test

import androidx.ui.core.semantics.SemanticsConfiguration
import androidx.ui.core.semantics.getOrNull
import androidx.ui.foundation.semantics.FoundationSemanticsProperties
import androidx.ui.semantics.SemanticsActions

/**
 * Verifies that a component is checkable.
 */
val SemanticsConfiguration.isToggleable: Boolean
    get() = contains(FoundationSemanticsProperties.ToggleableState)

val SemanticsConfiguration.hasClickAction: Boolean
    get() = SemanticsActions.OnClick in this

// TODO(ryanmentley/pavlis): Do we want these convenience functions?
/**
 * Verifies that a component is in a mutually exclusive group - that is,
 * that [FoundationSemanticsProperties.InMutuallyExclusiveGroup] is set to true
 *
 */
val SemanticsConfiguration.isInMutuallyExclusiveGroup: Boolean
    get() = getOrNull(FoundationSemanticsProperties.InMutuallyExclusiveGroup) == true