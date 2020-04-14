mac-gestures
============================

**mac-gestures** is a library for accessing multitouch gesture events on macOS for the JVM written in Kotlin

This project is a fork of https://github.com/mcourteaux/MultiTouch-Gestures-Java, rewritten in Kotlin with the addition 
of the **smart magnify** event and published in **JCenter** for easy consumption.

It aims to provide an easy way to enable MultiTouch touchpad gestures for JVM projects in Swing and AWT. This project
was originally started as an alternative for the AppleJavaExtensions `com.apple.eawt` package.

Read this is you came here because `com.apple.eawt` is not working
------------------------------------------------------------------

You landed on this page, probably because you are unable to make `com.apple.eawt` compile
on newer versions of Java (JDK 7 and higher). However after creating this project, I found that there is
a workaround. You should have a look at this question on StackOverflow:
[Using internal sun classes with javac](https://stackoverflow.com/questions/4065401/using-internal-sun-classes-with-javac).
All the classes in the `com.apple.eawt` package are not included in the `$JAVA_HOME/lib/ct.sym` file, which makes
the compilation fail with an error like: 

    com.apple.eawt can not find package
    package com.apple.eawt does not exist

Adding `-XDignore.symbol.file` to your compiler flags solves it.

However, this project still has a purpose, I believe, since you can have smooth two finger scrolling.
The Apple gesture features don't report scroll events, which are way smoother than the ones you get using
a classic `MouseWheelListener`.


Supported Platforms
-------------------
Since my personal need for now is only OS X, this is supported. However, if anyone needs support
for other platforms as well, feel free to fork and create a corresponding native source file for
your platform.

Download
-----
The library is available on **JCenter**.

To use it in your project, add the following to your project

- Gradle:
```groovy
implementation 'com.savvasdalkitsis:mac-gestures:0.0.9'
```
- Maven:
```xml
<dependency>
  <groupId>com.savvasdalkitsis</groupId>
  <artifactId>mac-gestures</artifactId>
  <version>0.0.9</version>
</dependency>
```

Usage
-----

The library will automatically load the first time you reference the class `MacOsGestures`.

A property will help you determine if the library can be used or not (which can either mean
you are not on a macOS system, or the native library failed to load):

```kotlin
if (MacOsGestures.isSupported) {}
```

or in Java:

```java
if (MacOsGestures.isSupported()) {}
```

Once you determine that the library can be used, you can add listeners on an awt `Component` instance
(thus allowing you to add them to a `JFrame` instance too) like so:

```kotlin
import com.savvasdalkitsis.mac.gestures.MacOsGesturesUtilities.addGestureListener

component.addGestureListener(object : GestureAdapter() {

     override fun magnify(e: GestureEvent<Magnification>) {}

     override fun rotate(e: GestureEvent<Rotation>) {}

     override fun scroll(e: GestureEvent<Scroll>) {}

     override fun smartMagnify(e: GestureEvent<SmartMagnify>) {}
})
```

or in Java:

```java
MacOsGesturesUtilities.addGestureListener(component, new GestureAdapter() {

    @Override
    public void magnify(@NotNull GestureEvent<Magnification> e) {}

    @Override
    public void rotate(@NotNull GestureEvent<Rotation> e) {}

    @Override
    public void scroll(@NotNull GestureEvent<Scroll> e) {}

    @Override
    public void smartMagnify(@NotNull GestureEvent<SmartMagnify> e) {}
});
```

The `addGestureListener` also takes an optional parameter `receiveEvenIfNotOnTop` which, when set to `true`
will deliver events to the specified component even if there is another component on top of it and under the mouse 
(this can be useful in cases where you have a different window surface, like OpenGL, 'inside' your JFrame and normal
mouse listeners would not work).

**When you dispose/remove a JFrame or component with a listener, you *MUST*
remove the `GestureListener` using one of following techniques:**

```kotlin
component.removeGestureListener(listener)
```

or

```kotlin
component.removeAllGestureListeners()
```

The `GestureListener` interface has these methods:

```kotlin
    fun magnify(e: GestureEvent<Magnification>)
    fun rotate(e: GestureEvent<Rotation>)
    fun scroll(e: GestureEvent<Scroll>)
    fun smartMagnify(e: GestureEvent<SmartMagnify>)
```

Where `GestureEvent` has these parameters:

 - `source`: the component that received the event.
 - `mouseX`: x-coordinate of the mouse in the component space.
 - `mouseX`: y-coordinate of the mouse in the component space.
 - `absMouseX`: x-coordinate of the mouse on the screen
 - `absMouseY`: y-coordinate of the mouse on the screen
 - `phase`: a `Phase` enum indicating what phase the gesture is in.
 - `data`: the data of the specific event generated.

A `Phase` is an enum containing these values:

 - `MOMENTUM`: Indicates this event is caused by momentum (like OS X).
 - `BEGIN`: The gesture just began.
 - `CHANGED`: The gesture updated (e.g.: rotating, zooming)
 - `END`: The gesture ended.
 - `CANCELLED`: The gesture was cancelled due to some popup or other thing.
 - `OTHER`: Any other reason for an event. Most of the time this will be errornous.

**Remark**: On OS X, the `MOMENTUM` events come after the `END` event.

Remark for OS X
---------------
The built-in Java scrolling listeners are not as smooth as the one provided in this project.
This gives you the native OS X scrolling experience.


