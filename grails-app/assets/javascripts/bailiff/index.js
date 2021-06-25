/**
 * 2021.03.16 >>> 创建 daniel
 * 2021.06.03 >>> 点击回车键确认收到法庭报警 daniel
 */
var client;
;-function (window) {
    'use strict';
    var ready = {
        "courtroomId": ""
    };
    var option = {};
    var init = function () {
        var height = document.documentElement.clientHeight - 20
        var width = document.documentElement.clientWidth - 40
        var courtrooms = document.getElementsByClassName("courtroom")
        for (var i = 0; i < courtrooms.length; i++) {
            courtrooms.item(i).style.height= height / 6 + "px";
            courtrooms.item(i).style.width= width / 5 + "px";
        }
        var names = document.getElementsByClassName("name")
        for (var j = 0; j < names.length; j++) {
            names.item(j).style.fontSize= height / 30 + "px";
            names.item(j).style.margin = "0";
        }
        var cons = document.getElementsByClassName("con")
        for (var k = 0; k < cons.length; k++) {
            cons.item(k).style.fontSize= height / 60 + "px";
            cons.item(k).style.margin= "0";
        }
        document.getElementById("textModal").style.fontSize = height / 12 + "px";
        document.getElementById("notice").style.fontSize = height / 24 + "px";
        init_ready();
        init_event();
    };

    function init_ready() {
    }

    function init_event() {
        client = core.connect();
        core.flush()
    }

    var core = {
        connect: function () {
            var path=window.document.location.pathname;
            var contextPath=path.substring(0,path.substr(1).indexOf('/')+2);
            console.log(contextPath)
            var socket = new SockJS( contextPath + 'stomp');
            var c = Stomp.over(socket);
            c.connect({}, function () {
                c.subscribe('/topic/warn', function (message) {
                    console.log("id:" + ready.courtroomId)
                    if (ready.courtroomId.toString() !== "") {
                        return false;
                    }
                    var data = JSON.parse(message.body);
                    ready.courtroomId = data.id
                    var courtroom = document.getElementById("courtroom"+data.id)
                    if (courtroom == null) {
                        return false;
                    }
                    courtroom.style.backgroundColor = "#CC3333";
                    document.getElementById('audio').play();
                    $("#courtName").text(data.name)
                    document.getElementById("modal").style.display = "block";
                    document.getElementById("textModal").style.display = "block";
                    var live = $('#video');
                    //视频直播
                    live.empty();
                    var v = $('<video width="1000px" autoplay loop></video>');
                    live.append(v);
                    var dPlayer = new DPlayer({
                        container: document.getElementById('video'),
                        live: true,
                        autoplay: true,
                        volume: 0, //设置静音播放,坑(浏览器有缓存，用户修改音量后就不默认音量为0了，这儿没事，上面还有一层模态无法修改音量)
                        video: {
                            url: data.url,
                            type: 'customFlv',
                            customType: {
                                customFlv: function (video, player) {
                                    ready.flvPlayer = flvjs.createPlayer({
                                        type: 'flv',
                                        url: data.url
                                    });
                                    ready.flvPlayer.attachMediaElement(video);
                                    ready.flvPlayer.load();
                                }
                            }
                        }
                    });
                    dPlayer.fullScreen.request('web');
                    setTimeout(function(){
                        window.location.reload();
                    },1000 * 15);
                });
                c.subscribe('/topic/quit', function (message) {
                    console.log("id:" + ready.courtroomId)
                    if (ready.courtroomId.toString() !== "" && ready.courtroomId.toString() === message.body.toString()) {
                        window.location.reload();
                    }
                });
                c.subscribe('/topic/flush', function (message) {
                    console.log("id:" + ready.courtroomId)
                    if (ready.courtroomId.toString() !== "") {
                        return false;
                    }
                    window.location.reload();
                });
            });
            return c
        },
        flush: function () {
            document.onkeydown = function () {
                if (event.code === "Enter") {
                    if (ready.courtroomId.toString() === "") {
                        return
                    }
                    console.log("法警确认.")
                    $.get(
                        window.location.href.split("bailiff")[0] + "bailiff/confirm",
                        {
                            id: ready.courtroomId
                        },
                        function (result) {
                            console.log(result)
                            if (result.code === 0) {
                                window.location.reload();
                            }
                        }, "json"
                    )
                }
            }
        }
    };
    var page = {};
    init();
    window.p = page;
}(window);
