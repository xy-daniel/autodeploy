package com.hxht.autodeploy.sync.util


class PushHandler {
    public static final String pushCase = "/pipe/pushCase"

    static void pushCase(final Object obj) {
        PushUtil.submit(pushCase, obj)
    }

    public static final String pushPlan = "/pipe/pushPlan"

    static void pushPlan(final Object obj) {
        PushUtil.submit(pushPlan, obj)
    }

    public static final String pushTrial = "/pipe/pushTrial"

    static void pushTrial(final Object obj) {
        PushUtil.submit(pushTrial, obj)
    }

    public static final String pushVideo = "/pipe/pushVideo"

    static void pushVideo(final Object obj) {
        PushUtil.submit(pushVideo, obj)
    }

    public static final String pushUser = "/pipe/pushUser"

    static void pushUser(final Object obj) {
        PushUtil.submit(pushUser, obj)
    }

    public static final String pushCourtroom = "/pipe/pushCourtroom"

    static void pushCourtroom(final Object obj) {
        PushUtil.submit(pushCourtroom, obj)
    }

    public static final String pushDistanceArraigned = "/pipe/distanceArraigned"

    static void pushDistanceArraigned(final Object obj) {
        PushUtil.submit(pushDistanceArraigned, obj)
    }
}
