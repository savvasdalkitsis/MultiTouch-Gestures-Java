//
//  EventDispatch.cpp
//  OSXGestures4JavaJNI
//
//  Created by Martijn Courteaux on 30/06/15.
//  Copyright © 2015 Martijn Courteaux. All rights reserved.
//

#include <stdio.h>

#import <AppKit/AppKit.h>

#include "EventDispatch.h"

JNIEnv *env;
jclass jc_EventDispatch;
jmethodID jm_dispatchMagnifyGesture;
jmethodID jm_dispatchRotateGesture;
jmethodID jm_dispatchScrollWheelEvent;
jmethodID jm_dispatchSmartMagnifyEvent;

CFMachPortRef eventTap;
CFRunLoopRef runningLoop;
bool running = false;

int convertCocoaPhaseToJavaPhase(NSEventPhase phase)
{
    switch (phase)
    {
        case NSEventPhaseNone:
            return 0;
        case NSEventPhaseBegan:
            return 1;
        case NSEventPhaseChanged:
            return 2;
        case NSEventPhaseEnded:
            return 3;
        case NSEventPhaseCancelled:
            return 4;
    }
    return -1;
}


NSEventMask eventMask =
NSEventMaskGesture |
NSEventMaskMagnify|
NSEventMaskRotate |
NSScrollWheelMask |
NSEventMaskSmartMagnify;

CGEventRef eventTapCallback(CGEventTapProxy proxy, CGEventType type, CGEventRef eventRef, void *refcon) {

    if (!(type > 0 && type < kCGEventTapDisabledByTimeout))
    {
        return eventRef;
    }
    
    // convert the CGEventRef to an NSEvent
    NSEvent *event = [NSEvent eventWithCGEvent:eventRef];
    
    // filter out events which do not match the mask
    if (!(eventMask & NSEventMaskFromType([event type]))) { return [event CGEvent]; }

    NSPoint m = [NSEvent mouseLocation];

    int phase = convertCocoaPhaseToJavaPhase([event phase]);
    double x = (double) m.x;
    double y = (double) m.y;

    switch ([event type])
    {
        case NSEventTypeMagnify:
        {
            env->CallStaticVoidMethod(jc_EventDispatch, jm_dispatchMagnifyGesture, x, y, event.magnification, phase);
            break;
        }
        case NSEventTypeRotate:
        {
            env->CallStaticVoidMethod(jc_EventDispatch, jm_dispatchRotateGesture, x, y, event.rotation, phase);
            break;
        }
        case NSEventTypeScrollWheel:
        {
            bool fromMouse = event.subtype == NSEventSubtypeMouseEvent;
            env->CallStaticVoidMethod(jc_EventDispatch, jm_dispatchScrollWheelEvent, x, y, event.scrollingDeltaX, event.scrollingDeltaY, fromMouse, phase);
            break;
        }
        case NSEventTypeSmartMagnify:
        {
            env->CallStaticVoidMethod(jc_EventDispatch, jm_dispatchSmartMagnifyEvent, x, y, phase);
            break;
        }
        default:
            break;
    }
    
    return [event CGEvent];
}

void JNICALL Java_com_savvasdalkitsis_multitouch_EventDispatch_init(JNIEnv *env, jclass clazz)
{
    printf("[NATIVE] Prepare JNI Gesture Listener.\n");
    fflush(stdout);
    ::env = env;
    jc_EventDispatch = clazz;
    jm_dispatchMagnifyGesture = env->GetStaticMethodID(jc_EventDispatch, "dispatchMagnifyGesture", "(DDDI)V");
    jm_dispatchRotateGesture = env->GetStaticMethodID(jc_EventDispatch, "dispatchRotateGesture", "(DDDI)V");
    jm_dispatchScrollWheelEvent = env->GetStaticMethodID(jc_EventDispatch, "dispatchScrollWheelEvent", "(DDDDZI)V");
    jm_dispatchSmartMagnifyEvent = env->GetStaticMethodID(jc_EventDispatch, "dispatchSmartMagnifyEvent", "(DDI)V");
}

void JNICALL Java_com_savvasdalkitsis_multitouch_EventDispatch_start(JNIEnv *env, jclass)
{
    printf("[NATIVE] Starting JNI Gesture Listener Tap.\n");
    fflush(stdout);
    if (!running)
    {
        eventTap = CGEventTapCreate(kCGSessionEventTap, kCGHeadInsertEventTap, kCGEventTapOptionListenOnly, kCGEventMaskForAllEvents, eventTapCallback, nil);
        CFRunLoopAddSource(CFRunLoopGetCurrent(), CFMachPortCreateRunLoopSource(kCFAllocatorDefault, eventTap, 0), kCFRunLoopCommonModes);
        CGEventTapEnable(eventTap, true);
        runningLoop = CFRunLoopGetCurrent();
        CFRunLoopRun();
        running = false;
    }
}

void JNICALL Java_com_savvasdalkitsis_multitouch_EventDispatch_stop(JNIEnv *, jclass)
{
    printf("[NATIVE] Stopping JNI Gesture Listener Tap.\n");
    fflush(stdout);

    if (running)
    {
        running = false;
        CFRunLoopStop(runningLoop);
        CGEventTapEnable(eventTap, false);
    }
}
