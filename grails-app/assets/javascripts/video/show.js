/**
 * Created by sakuri on 2019/8/13.
 */

;-function (window) {
    'use strict';
    var ready = {
        chnNumber: $('.plan-tab-chn.active').attr('data-number'),
        chnName: $('.plan-tab-chn.active').text(),
        data: null
    };
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
        core.initVideoData(); //加载视频数据
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        core.changeChn();//切换通道
    }

    //内部核心属性
    var core = {

        handleVideo: function () { //处理视频方法
            if (ready.flvPlayer != null) {//销毁之前播放器
                console.log("销毁了播放器");
                if (ready.player != null) {
                    ready.player.destroy();
                }
                ready.flvPlayer.pause();
                ready.flvPlayer.unload();
                ready.flvPlayer.detachMediaElement();
                ready.flvPlayer.destroy();
                ready.flvPlayer = null;
            }
            console.log("当前通道号：" + ready.chnNumber + "/" + "当前通道名称：" + ready.chnName);
            core.liveVideo();
        },
        liveVideo: function () {//直播视频
            var url = null;
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.chnNumber && video.name === ready.chnName) {
                    url = video.url;
                    return false
                }
            });
            console.log("当前直播地址：" + url);
            console.log("法庭状态：" + ready.isConnect)
            if (ready.isConnect === '在线状态') {//法庭在线情况下展示直播 否则显示法庭状态
                ready.player = new DPlayer({
                    container: document.getElementById('video'),
                    live: true,
                    autoplay: true,
                    video: {
                        url: url,
                        type: 'customFlv',
                        customType: {
                            customFlv: function (video, player) {
                                ready.flvPlayer = flvjs.createPlayer({
                                    type: 'flv',
                                    url: video.src
                                });
                                ready.flvPlayer.attachMediaElement(video);
                                ready.flvPlayer.load();
                            }
                        }
                    }
                });
            } else {
                ready.live.empty();
                ready.live.append('<div class="plan-show-video-notfound">\n' +
                    '            <span>' + ready.isConnect + '</span>\n' +
                    '        </div>');
            }
            console.log("视频开始播放");
        },

        initVideoData: function () { //加载视频数据
            ready.live = $('#video');
            ready.live.empty();
            ready.live.append('<div class="plan-show-video-notfound">\n' +
                '            <span>视频正在请求加载中.....</span>\n' +
                '        </div>');
            var roomId = $("#roomId").val();

            function dataErr() {
                swal({
                    title: '数据出错!',
                    type: 'error',
                    confirmButtonText: '确 认'
                }, function () {
                    window.location.href = contextPath + 'video/show/' + roomId;
                });
            }

            $.ajax({
                type: 'post',
                url: contextPath + 'video/showVideo/' + roomId,
                dataType: 'json',
                success: function (result) {
                    if (result.code === 0) {
                        ready.data = result.data;
                        $.ajax({
                            type: "get",
                            url: contextPath + "index/courtroomIsOnline",
                            data: {
                                courtroom: roomId
                            },
                            dataType: "json",
                            success: function (result) {
                                if (result.code === 0) {
                                    ready.isConnect = result.data.isConnect
                                    core.handleVideo();//初始化视频
                                }
                            }
                        });
                    }
                },
                error: function () {
                    dataErr();
                }
            });

        },
        changeChn: function () { //切换通道号
            $('.plan-show-chn li').bind('click', function () { //点击切换通道事件
                var number = $(this).find('a').attr('data-number');
                var name = $(this).find('a').text();
                console.log("切换通道号：" + number + "/" + "切换通道名称：" + name);
                ready.chnNumber = number;
                ready.chnName = name;
                core.handleVideo();
            });
        }
    };

    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
