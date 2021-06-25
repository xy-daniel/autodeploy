/**
 * 2021.05.08 >>> 东软点播页面脚本 daniel
 */
;-function (window) {
    'use strict';
    var ready = {
        chnNumber: $('.plan-tab-chn.active').attr('data'),
        chnName: $('.plan-tab-chn.active').text(),
        data: null
    };
    var option = {};
    var init = function () {
        init_ready();
        init_event();
    };
    function init_ready() {
    }
    function init_event() {
        core.initVideoData();
        core.changeChn();
    }
    var core = {
        initVideoData: function () {
            $.ajax({
                type: 'post',
                url: contextPath + 'api/showVideo?id=' + $('.plan-info').attr('trialId'),
                dataType: 'json',
                success: function (result) {
                    console.log("视频数据:\n" + JSON.stringify(result));
                    if (result.code === 0) {
                        ready.data = result.data;
                        core.handleVideo();
                    }
                },
                error: function () {
                    swal({
                        title: '数据出错!',
                        type: 'error',
                        confirmButtonText: '确 认'
                    }, function () {
                        window.location.reload()
                    });
                }
            });
        },
        handleVideo: function () {
            if (ready.data.status === 0) {
                console.log("trial处于排期状态");
                return
            }
            if (ready.flvPlayer != null) {
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
            console.log("当前通道号:" + ready.chnNumber + "/当前通道名称:" + ready.chnName);
            if (ready.data.status === 1) {
                console.log("正在开庭状态,进入直播模式.");
                if ($('.plan-info').attr('allowPlay') === "1") {
                    $('.plan-show-video-notfound span').html('不允许直播');
                    return
                }
                core.liveVideo();
            } else {
                console.log("闭庭状态,进入点播模式.");
                var video = ready.data.video;
                var totalLength = 0;
                for (var i = 0; i < video.length; i++) {
                    totalLength += video[i].totalLength;
                    if (totalLength > 0) {
                        core.playVideo();
                        break;
                    }
                }
            }
        },
        liveVideo: function () {
            var url = null;
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.chnNumber && video.name === ready.chnName) {
                    url = video.url;
                    return false
                }
            });
            console.log("当前直播地址:" + url);
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
            console.log("视频开始播放.");
        },
        playVideo: function () {
            var pvList = [];
            var pv = 0;
            var pCalTime = 0;
            var url = null;
            var totalLength = 0;
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.chnNumber && video.name === ready.chnName) {
                    pvList = video.videoUrl;
                    totalLength = video.totalLength;
                    url = pvList[0].url;
                }
            });
            console.log("当前点播地址:" + url + ",总时长:" + totalLength);
            ready.player = new DPlayer({
                container: document.getElementById('video'),
                autoplay: true,
                video: {
                    url: url
                }
            });
            var container = $(ready.player.container);
            var oldController = container.find(".dplayer-controller");
            oldController.remove();
            var controller = $('<div class="dplayer-controller"></div>');
            controller.append(oldController.html());
            controller.find('.dplayer-setting').hide();
            var dtime = Math.floor(totalLength / 60) + ":" + (Array(2).join('0') + Math.floor(totalLength % 60)).slice(-2);
            controller.find('.dplayer-dtime').html(dtime);
            container.append(controller);
            var barWidth = controller.find('.dplayer-bar-wrap').css('width');
            barWidth = barWidth.replace("px", "");
            var timeBar = controller.find('.dplayer-bar-time');
            timeBar.removeClass('hidden');
            timeBar.hide();
            controller.find('.dplayer-thumb').hide();
            $('.dplayer-notice').hide();
            var fullScreenFlag = 0;
            var fullScreenWebFlag = 0;
            controller.find('.dplayer-full-icon').click(function () {
                if (fullScreenFlag === 1) {
                    fullScreenFlag = 0;
                    fullScreenWebFlag = 0;
                    ready.player.fullScreen.cancel();
                } else {
                    fullScreenFlag = 1;
                    fullScreenWebFlag = 0;
                    ready.player.fullScreen.request();
                }
            });
            controller.find('.dplayer-full-in-icon').click(function () {
                if (fullScreenWebFlag === 1) {
                    fullScreenFlag = 0;
                    fullScreenWebFlag = 0;
                    ready.player.fullScreen.cancel('web');
                } else {
                    fullScreenWebFlag = 1;
                    fullScreenFlag = 0;
                    ready.player.fullScreen.request('web');
                }
            });
            controller.find('.dplayer-play-icon').click(function () {
                ready.player.toggle();
            });
            controller.find('.dplayer-bar-wrap').click(function (e) {
                var barOffsetX = e.offsetX;
                var barTime = Math.floor(barOffsetX / barWidth * totalLength);
                var calTime = 0;
                for (var i = 0; i < pvList.length + 1; i++) {
                    if (calTime > barTime) {
                        calTime -= pvList[i - 1].length;
                        pCalTime = calTime;
                        pv = pvList[i - 1].serial;
                        break;
                    } else {
                        calTime += pvList[i].length;
                    }
                }
                if (barTime > 0) {
                    var serialCalTime = barTime - pCalTime;
                    url = pvList[i - 1].url;
                    console.log("跳转到第" + pv + "段视频,第" + serialCalTime + "秒" + url);
                    ready.player.switchVideo({
                        url: url
                    });
                    ready.player.play();
                    ready.player.seek(serialCalTime);
                }
            });
            controller.find('.dplayer-bar-wrap').mousemove(function (e) {
                timeBar.show();
                var barOffsetX = e.offsetX;
                if (barOffsetX > barWidth - 40) {
                    timeBar.css('left', barOffsetX - 40 + "px");
                } else {
                    timeBar.css('left', barOffsetX + "px");
                }
                var barTime = Math.floor(barOffsetX / barWidth * totalLength);
                var barText = Math.floor(barTime / 60) + ":" + (Array(2).join('0') + Math.floor(barTime % 60)).slice(-2);
                timeBar.html(barText);
            });
            controller.find('.dplayer-bar-wrap').mouseout(function () {
                timeBar.hide();
            });
            var soundWidth = 45;
            controller.find('.dplayer-volume-bar-wrap').click(function (e) {
                var barOffsetX = e.offsetX;
                var sound = barOffsetX / soundWidth;
                controller.find('.dplayer-volume-bar-inner').css('width', (sound * 100) + '%');
                ready.player.volume(sound, true, false);
                console.log("设置音量:" + (sound * 100) + "%")
            });
            ready.player.on('play', function () {
                controller.find('.dplayer-play-icon').html('<svg xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="0 0 17 32"><path d="M14.080 4.8q2.88 0 2.88 2.048v18.24q0 2.112-2.88 2.112t-2.88-2.112v-18.24q0-2.048 2.88-2.048zM2.88 4.8q2.88 0 2.88 2.048v18.24q0 2.112-2.88 2.112t-2.88-2.112v-18.24q0-2.048 2.88-2.048z"></path></svg>');
            });
            ready.player.on('pause', function () {
                controller.find('.dplayer-play-icon').html('<svg xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="0 0 16 32"><path d="M15.552 15.168q0.448 0.32 0.448 0.832 0 0.448-0.448 0.768l-13.696 8.512q-0.768 0.512-1.312 0.192t-0.544-1.28v-16.448q0-0.96 0.544-1.28t1.312 0.192z"></path></svg>');
            });
            ready.player.on('ended', function () {
                pv++;
                if (pv < pvList.length) {
                    url = pvList[pv].url;
                    console.log("切换下一个视频:" + url);
                    ready.player.switchVideo({
                        url: url
                    });
                    pCalTime = pvList[pv - 1].length
                    ready.player.play();
                }
            });
            var ptimeInterval = setInterval(function () {
                var currentTime = pCalTime + ready.player.video.currentTime;
                var ptime = Math.floor(currentTime / 60) + ":" + (Array(2).join('0') + Math.floor(currentTime % 60)).slice(-2);
                controller.find('.dplayer-ptime').html(ptime);
                var schedule = (currentTime / totalLength) * 100;
                controller.find('.dplayer-played').css('width', schedule + "%");
            }, 500);
            setTimeout(function () {
                var v = $('#video');
                v.css('height', v.css('height'));
            }, 2000);
            console.log("视频开始播放.");
        },
        changeChn: function () {
            $('.plan-show-chn li').bind('click', function () {
                var number = $(this).find('a').attr('data');
                var name = $(this).find('a').text();
                console.log("切换通道号:" + number + "/切换通道名称:" + name);
                ready.chnNumber = number;
                ready.chnName = name;
                core.handleVideo();
            });
        }
    };
    var page = {};
    init();
    window.p = page;
}(window);
