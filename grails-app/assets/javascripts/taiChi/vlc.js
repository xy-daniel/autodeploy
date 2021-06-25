var play_count=0;
(function () {
    if (navigator.appName.indexOf("Microsoft Internet") == -1) {
        onVLCPluginReady()
    }
    else if (document.readyState == 'complete') {
        onVLCPluginReady();
    }
    else {
        /* Explorer loads plugins asynchronously */
        document.onreadystatechange = function () {
            if (document.readyState == 'complete') {
                onVLCPluginReady();
            }
        }
    }
})();

//第一种，使用while循环
function sleep(delay) {
    debugger
    var start = (new Date()).getTime();
    while((new Date()).getTime() - start < delay) {
        continue;
    }
}

function getVLC(name) {
    if (window.document[name]) {
        return window.document[name];
    }
    if (navigator.appName.indexOf("Microsoft Internet") == -1) {
        if (document.embeds && document.embeds[name])
            return document.embeds[name];
    }
    else {
        return document.getElementById(name);
    }
}

function registerVLCEvent(event, handler) {
    var vlc = getVLC("vlc");

    if (vlc) {
        if (vlc.attachEvent) {
            // Microsoft
            vlc.attachEvent(event, handler);
        }
        else if (vlc.addEventListener) {
            // Mozilla: DOM level 2
            vlc.addEventListener(event, handler, false);
        }
    }
}

function unregisterVLCEvent(event, handler) {
    var vlc = getVLC("vlc");

    if (vlc) {
        if (vlc.detachEvent) {
            // Microsoft
            vlc.detachEvent(event, handler);
        }
        else if (vlc.removeEventListener) {
            // Mozilla: DOM level 2
            vlc.removeEventListener(event, handler, false);
        }
    }
}

// JS VLC API callbacks
function handleMediaPlayerMediaChanged() {
    document.getElementById("info").innerHTML = "Media Changed";
    sleep(1000)
}

function handle_MediaPlayerNothingSpecial() {
    // document.getElementById("state").innerHTML = "Idle...";
}

function handle_MediaPlayerOpening() {
    onOpen();
}

function handle_MediaPlayerBuffering(val) {
    var vlc = getVLC("vlc");

    document.getElementById("info").innerHTML = val + "%";

    if (vlc && val == 100) {
        // display the current state after buffering
        if (vlc.playlist.isPlaying)
            onPlay();
        else
            onPause();
    }
}

function handle_MediaPlayerPlaying() {
    onPlay();
}

function handle_MediaPlayerPaused() {
    onPause();
}

function handle_MediaPlayerStopped() {
    onStop();
}

function handle_MediaPlayerForward() {
    document.getElementById("state").innerHTML = "Forward...";
}

function handle_MediaPlayerBackward() {
    document.getElementById("state").innerHTML = "Backward...";
}

function handle_MediaPlayerEndReached() {
    //alert(333);
    onEnd();
}

function handle_MediaPlayerEncounteredError() {
    onError();
}

function handle_MediaPlayerTimeChanged(time) {
    var vlc = getVLC("vlc");
    var info = document.getElementById("info");
    if (vlc) {
        var mediaLen = vlc.input.length;
        if (mediaLen > 0) {
            // seekable media
            info.innerHTML = formatTime(time) + "/" + formatTime(mediaLen);
        }
        else {
            // non-seekable "live" media
            liveFeedRoll = liveFeedRoll & 3;
            info.innerHTML = liveFeedText[liveFeedRoll++];
        }
    }
}

function handle_MediaPlayerPositionChanged(val) {
    if (canSeek) {
        var percent = Math.round(val * 10000) / 100;
        //document.getElementById("sliderProgress").style.width = percent + "%";
    }
}

function handle_MediaPlayerSeekableChanged(val) {
    setSeekable(val);
}

function handle_MediaPlayerPausableChanged(val) {
    setPauseable(val);
}

function handle_MediaPlayerTitleChanged(val) {
    //setTitle(val);
    document.getElementById("info").innerHTML = "Title: " + val;
}

function handle_MediaPlayerLengthChanged(val) {
    //setMediaLength(val);
}

// VLC Plugin
function onVLCPluginReady() {
    registerVLCEvent("MediaPlayerMediaChanged", handleMediaPlayerMediaChanged);
    registerVLCEvent("MediaPlayerNothingSpecial", handle_MediaPlayerNothingSpecial);
    registerVLCEvent("MediaPlayerOpening", handle_MediaPlayerOpening);
    registerVLCEvent("MediaPlayerBuffering", handle_MediaPlayerBuffering);
    registerVLCEvent("MediaPlayerPlaying", handle_MediaPlayerPlaying);
    registerVLCEvent("MediaPlayerPaused", handle_MediaPlayerPaused);
    registerVLCEvent("MediaPlayerStopped", handle_MediaPlayerStopped);
    registerVLCEvent("MediaPlayerForward", handle_MediaPlayerForward);
    registerVLCEvent("MediaPlayerBackward", handle_MediaPlayerBackward);
    registerVLCEvent("MediaPlayerEndReached", handle_MediaPlayerEndReached);
    registerVLCEvent("MediaPlayerEncounteredError", handle_MediaPlayerEncounteredError);
    registerVLCEvent("MediaPlayerTimeChanged", handle_MediaPlayerTimeChanged);
    registerVLCEvent("MediaPlayerPositionChanged", handle_MediaPlayerPositionChanged);
    registerVLCEvent("MediaPlayerSeekableChanged", handle_MediaPlayerSeekableChanged);
    registerVLCEvent("MediaPlayerPausableChanged", handle_MediaPlayerPausableChanged);
    registerVLCEvent("MediaPlayerTitleChanged", handle_MediaPlayerTitleChanged);
    registerVLCEvent("MediaPlayerLengthChanged", handle_MediaPlayerLengthChanged);
}

function close() {
    unregisterVLCEvent("MediaPlayerMediaChanged", handleMediaPlayerMediaChanged);
    unregisterVLCEvent("MediaPlayerNothingSpecial", handle_MediaPlayerNothingSpecial);
    unregisterVLCEvent("MediaPlayerOpening", handle_MediaPlayerOpening);
    unregisterVLCEvent("MediaPlayerBuffering", handle_MediaPlayerBuffering);
    unregisterVLCEvent("MediaPlayerPlaying", handle_MediaPlayerPlaying);
    unregisterVLCEvent("MediaPlayerPaused", handle_MediaPlayerPaused);
    unregisterVLCEvent("MediaPlayerStopped", handle_MediaPlayerStopped);
    unregisterVLCEvent("MediaPlayerForward", handle_MediaPlayerForward);
    unregisterVLCEvent("MediaPlayerBackward", handle_MediaPlayerBackward);
    unregisterVLCEvent("MediaPlayerEndReached", handle_MediaPlayerEndReached);
    unregisterVLCEvent("MediaPlayerEncounteredError", handle_MediaPlayerEncounteredError);
    unregisterVLCEvent("MediaPlayerTimeChanged", handle_MediaPlayerTimeChanged);
    unregisterVLCEvent("MediaPlayerPositionChanged", handle_MediaPlayerPositionChanged);
    unregisterVLCEvent("MediaPlayerSeekableChanged", handle_MediaPlayerSeekableChanged);
    unregisterVLCEvent("MediaPlayerPausableChanged", handle_MediaPlayerPausableChanged);
    unregisterVLCEvent("MediaPlayerTitleChanged", handle_MediaPlayerTitleChanged);
    unregisterVLCEvent("MediaPlayerLengthChanged", handle_MediaPlayerLengthChanged);
}
var canPause = false;
var canSeek = false;

function setPauseable(val) {
    canPause = val;
}

function setSeekable(val) {
    canSeek = val;
    if (liveFeedRoll != 0)
        liveFeedRoll = 0;
}
function formatTime(timeVal) {
    if (typeof timeVal != 'number')
        return "-:--:--";

    var timeHour = Math.round(timeVal / 1000);
    var timeSec = timeHour % 60;
    if (timeSec < 10)
        timeSec = '0' + timeSec;
    timeHour = (timeHour - timeSec) / 60;
    var timeMin = timeHour % 60;
    if (timeMin < 10)
        timeMin = '0' + timeMin;
    timeHour = (timeHour - timeMin) / 60;
    if (timeHour > 0)
        return timeHour + ":" + timeMin + ":" + timeSec;
    else
        return timeMin + ":" + timeSec;
}

/* events */

function onOpen() {
   //$("#state").html("Opening...");
   // document.getElementById("PlayOrPause").value = "Pause";
   // document.getElementById("sliderProgress").style.width = "0%";
    setSeekable(false);
}

function onBuffer() {
   // document.getElementById("state").innerHTML = "Buffering...";
    //document.getElementById("PlayOrPause").value = "Pause";
}

function onPlay() {
   // document.getElementById("state").innerHTML = "Playing...";
    //document.getElementById("PlayOrPause").value = "Pause";
    onPlaying();
}

function onEnd() {
    setSeekable(false);
    var vlc = getVLC("vlc");
    if(play_count<vlc.playlist.items.count-1){
        play_count++;
    }else{
        play_count=0;
    }
    lightPart(play_count+1);
}

var liveFeedText = ["Live", "((Live))", "(( Live ))", "((&nbsp; Live &nbsp;))"];
var liveFeedRoll = 0;

function onPlaying() {
    var vlc = getVLC("vlc");
    var info = document.getElementById("info");
    if (vlc) {
        var mediaLen = vlc.input.length;
        if (mediaLen > 0) {
            // seekable media
            info.innerHTML = formatTime(vlc.input.time) + "/" + formatTime(mediaLen);
        }
        else {
            // non-seekable "live" media
            liveFeedRoll = liveFeedRoll & 3;
            info.innerHTML = liveFeedText[liveFeedRoll++];
        }
    }
}

function onPause() {
  //  document.getElementById("state").innerHTML = "Paused...";
   // document.getElementById("PlayOrPause").value = "Play";
}

function onStop() {
    //document.getElementById("info").innerHTML = "-:--:--/-:--:--";
    //document.getElementById("state").innerHTML = "Stopped...";
   // document.getElementById("PlayOrPause").value = "Play";
   // document.getElementById("sliderProgress").style.width = "0%";
    setSeekable(false);
}

function onError() {
    //document.getElementById("state").innerHTML = "Error...";

}