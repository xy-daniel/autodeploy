//仅适用于IE浏览器是，并且安装有vlc插件，则返回true；
function isInsalledIEVLC() {
    var vlcObj = null;
    var vlcInstalled = false;
    try {
        vlcObj = new ActiveXObject("VideoLAN.Vlcplugin.1");
        if (vlcObj != null) {
            vlcInstalled = true
        }
    } catch (e) {
        vlcInstalled = false;
    }
    return vlcInstalled;
}

//仅适用于firefox浏览器是，并且安装有vlc插件，则返回true；
function isInsalledFFVLC() {
    var numPlugins = navigator.plugins.length;
    for (i = 0; i < numPlugins; i++) {
        plugin = navigator.plugins[i];
        if (plugin.name.indexOf("VideoLAN") > -1 || plugin.name.indexOf("VLC") > -1) {
            return true;
        }
    }
    return false;
}

/* 浏览器检测 */
function checkBrowser() {
    return isInsalledFFVLC() || isInsalledIEVLC();
}