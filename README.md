# Android Jetpack Composer @Composable

## References:

1. https://developer.android.com/jetpack/compose

2. https://proandroiddev.com/playing-with-jetpack-compose-dev-1-be992c6f8915 <--- Special thanks to this link. It was really useful

3. https://engineering.q42.nl/android-jetpack-compose/

4. https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/ui/

More about:

Jetpack Compose is a toolkit to simplify ui design. It's still in dev testing.

### $version = 0.1.0-dev02

## Jetpack Composer

  - androidx.compose:compose-compiler:$version
    #### compose-compiler is the processor for annotations that you need to add with kapt to your Gradle module.

  - androidx.compose:compose-runtime:$version
    #### compose-runtime contains APIs that used to generate code for Compose UI. @Composable should be used to declare UI.
    
## UI Components 

  - androidx.ui:ui-core:$version
    #### Base classes used across the system covering primitives, graphics, and painting: Dp, Autofill, Offset, Rect, Paint, vector math, etc.
    
  - androidx.ui:ui-layout:$version
    #### Basic layout components: Column, Row, Table, Padding, AspectRatio, etc.
    
  - androidx.ui:ui-framework:$version
    #### Base components exposed by the system as building blocks. This includes Draw, Layout, Text, etc.
    
  - androidx.ui:ui-animation:$version
    #### Animation components
    
  - androidx.ui:ui-animation-core:$version
    #### Internal declarations for the animations system
    
  - androidx.ui:ui-android-text:$version
    #### Base components for a text from the system as build blocks.
    
  - androidx.ui:ui-material:$version
    #### Set of UI components built according to the Material spec: MaterialTheme, BottomAppBar, Buttons, FAB, TopAppBar, etc.
    
  - androidx.ui:ui-tooling:$version
    #### UI Tooling helper
    
  - androidx.ui:ui-platform:$version
    #### Internal implementation that allows separation of the Android implementation from host-side tests
    
  - androidx.ui:ui-vector:$version
    #### Vector graphics
    
  - androidx.ui:ui-foundation:$version
    #### Base components exposed by the system as building blocks: PopupLayout, Scroller, Dialog, Gestures, Selection, Shapes.
    
  - androidx.ui:ui-text:$version
    #### Text engine
    
  - androidx.ui:ui-test:$version
    #### Testing framework

## General

Compose is a reactive UI toolkit entirely developed in Kotlin. It's similar to Flutter, Swift UI, Litho and React.

## Extra content in case

https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet

## Slides

  - In Spanish: https://docs.google.com/presentation/d/1O8cM0gZ19IJb5CsP8DjF_sR72775BRVXR6EEBWRQWos/edit#slide=id.g35f391192_00
  - In English: In Progress
