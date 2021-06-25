/**
 * Created by sakuri on 2019/8/13.
 */

;-function (window) {
    'use strict';
    var ready = {
        planId: $('.plaan-info').attr('planId'),
        trialId: $('.plaan-info').attr('trialId'),
        allowPlay: $('.plaan-info').attr('allowPlay'),
        showNumber: $('.plan-tab-chn.active').attr('data'),
        chnname: $('.plan-tab-chn.active').text(),
        data: null,
        client: null
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
            if (ready.data.status === 0) {//排期状态
                console.log("trial处于排期状态");
                return
            }
            if (ready.flvPlayer != null) {//销毁之前播放器
                console.log("销毁了播放器");
                if(ready.player != null){
                    ready.player.destroy();
                }
                ready.flvPlayer.pause();
                ready.flvPlayer.unload();
                ready.flvPlayer.detachMediaElement();
                ready.flvPlayer.destroy();
                ready.flvPlayer = null;
            }
            console.log("当前通道号：" + ready.showNumber);
            console.log("当前通道名称：" + ready.chnname);

            if (ready.data.status === 1) { //开庭状态 直播模式
                console.log("trial处于开庭状态，进入直播模式");
                if (ready.allowPlay === "1") {
                    console.log("视频无直播权限");
                    $('.plan-show-video-notfound span').html('视频无直播权限');
                    return
                }
                core.liveVideo();
            } else {//视频处于休庭或者闭庭 点播模式
                console.log("trial处于闭庭状态，进入点播模式");
                var video = ready.data.video;
                var totalLength = 0;
                for (var i = 0; i < video.length; i++){
                    totalLength += video[i].totalLength;
                    if (totalLength > 0){//存在存储视频情况下
                        //构建视频界面
                        core.playVideo();
                        //构建多视频下载
                        /*core.handleVideoDom();
                        setInterval(function handleVideoDom() {
                            core.handleVideoDom();
                        },3000); //指定3秒刷新一次*/
                        break;
                    }
                }
            }
        },
        liveVideo: function () {//直播视频
            var url = null;
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.showNumber && video.name === ready.chnname) {
                    url = video.url;
                }
            });
            console.log("当前直播地址：" + url);
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
            console.log("视频开始播放");
        },
        playVideo: function () {//点播视频
            var pvList = [];
            var pv = 0;
            var pCalTime = 0;
            var url = null;
            var totalLength = 0;
            //遍历视频数据获取视频信息列表
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.showNumber) {
                    pvList = video.videoUrl;
                    totalLength = video.totalLength;
                    url = pvList[0].url;
                }
            });
            console.log("当前点播地址：" + url + " totalLength: " + totalLength);
            //初始化视频控件
            ready.player = new DPlayer({
                container: document.getElementById('video'),
                autoplay: true,
                video: {
                    url: url
                }
            });
            //获取原进度条
            var container = $(ready.player.container);
            var oldController = container.find(".dplayer-controller");
            //删除原有进度条
            oldController.remove();
            //创建新的进度条
            var controller = $('<div class="dplayer-controller"></div>');
            controller.append(oldController.html());
            //隐藏设置按钮
            controller.find('.dplayer-setting').hide();
            var dtime = Math.floor(totalLength / 60) + ":" + (Array(2).join(0) + Math.floor(totalLength % 60)).slice(-2);//计算dtime
            //写入总时长
            controller.find('.dplayer-dtime').html(dtime);
            container.append(controller);

            //获取进度条宽度
            var barWidth = controller.find('.dplayer-bar-wrap').css('width');
            barWidth = barWidth.replace("px", "");

            //获取进度条dom
            var timeBar = controller.find('.dplayer-bar-time');
            timeBar.removeClass('hidden');//取消跟随鼠标显示时间隐藏class
            timeBar.hide();//jquery 重新隐藏显示时间
            controller.find('.dplayer-thumb').hide();//隐藏容易出bug的小圆点

            $('.dplayer-notice').hide();//隐藏快进提示

            var fullScreenFlag = 0;
            var fullScreenWebFlag = 0;
            //全屏按钮事件
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
            //页面全屏按钮事件
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
            //播放暂停按钮事件
            controller.find('.dplayer-play-icon').click(function () {
                ready.player.toggle();
            });

            //鼠标点击跳转进度事件
            controller.find('.dplayer-bar-wrap').click(function (e) {
                var barOffsetX = e.offsetX;
                var barTime = Math.floor(barOffsetX / barWidth * totalLength);
                //计算跳转的视频属于第几段
                var calTime = 0;
                // debugger
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
                if(barTime > 0){
                    var serialCalTime = barTime - pCalTime;
                    url = pvList[i - 1].url;
                    console.log("跳转到第" + pv + "段视频，第" + serialCalTime + "秒" + url);
                    ready.player.switchVideo({//跳转视频
                        url: url
                    });
                    ready.player.play();
                    ready.player.seek(serialCalTime); //跳转播放进度
                }
            });
            //鼠标跟随显示时间事件
            controller.find('.dplayer-bar-wrap').mousemove(function (e) {
                timeBar.show();
                var barOffsetX = e.offsetX;
                if (barOffsetX > barWidth - 40) {
                    timeBar.css('left', barOffsetX - 40 + "px");
                } else {
                    timeBar.css('left', barOffsetX + "px");
                }
                var barTime = Math.floor(barOffsetX / barWidth * totalLength);
                var barText = Math.floor(barTime / 60) + ":" + (Array(2).join(0) + Math.floor(barTime % 60)).slice(-2);
                timeBar.html(barText);
            });
            //鼠标移出隐藏时间事件
            controller.find('.dplayer-bar-wrap').mouseout(function () {
                timeBar.hide();
            });
            //音量条宽度
            var soundWidth = 45;
            //鼠标点击选择音量事件
            controller.find('.dplayer-volume-bar-wrap').click(function (e) {
                var barOffsetX = e.offsetX;
                var sound = barOffsetX / soundWidth;
                controller.find('.dplayer-volume-bar-inner').css('width', (sound * 100) + '%');
                ready.player.volume(sound, true, false);
                console.log("设置音量："+ (sound * 100) + "%")
            });

            //播放器播放事件
            ready.player.on('play', function () {
                controller.find('.dplayer-play-icon').html('<svg xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="0 0 17 32"><path d="M14.080 4.8q2.88 0 2.88 2.048v18.24q0 2.112-2.88 2.112t-2.88-2.112v-18.24q0-2.048 2.88-2.048zM2.88 4.8q2.88 0 2.88 2.048v18.24q0 2.112-2.88 2.112t-2.88-2.112v-18.24q0-2.048 2.88-2.048z"></path></svg>');//写入总时长
            });
            //播放器暂停事件
            ready.player.on('pause', function () {
                controller.find('.dplayer-play-icon').html('<svg xmlns="http://www.w3.org/2000/svg" version="1.1" viewBox="0 0 16 32"><path d="M15.552 15.168q0.448 0.32 0.448 0.832 0 0.448-0.448 0.768l-13.696 8.512q-0.768 0.512-1.312 0.192t-0.544-1.28v-16.448q0-0.96 0.544-1.28t1.312 0.192z"></path></svg>');//写入总时长
            });
            //播放器播放结束事件
            ready.player.on('ended', function () {
                //上一个视频结束后，开始播放下一个视频
                pv++;
                if (pv < pvList.length) {
                    url = pvList[pv].url;
                    console.log("切换下一个视频：" + url);
                    ready.player.switchVideo({
                        url: url
                    });
                    pCalTime = pvList[pv - 1].length
                    ready.player.play();
                }
            });

            //定时器刷新进度条进度
            var ptimeInterval = setInterval(function () {
                //1秒获取1次时间
                var currentTime = pCalTime + ready.player.video.currentTime;
                var ptime = Math.floor(currentTime / 60) + ":" + (Array(2).join(0) + Math.floor(currentTime % 60)).slice(-2);
                controller.find('.dplayer-ptime').html(ptime);//写入当前播放时间
                var schedule = (currentTime / totalLength) * 100;
                controller.find('.dplayer-played').css('width', schedule + "%");//设置进度百分比
            }, 500);

            //2秒后 固定视频容器大小
            setTimeout(function() {
                var v =  $('#video');
                v.css('height',v.css('height'));
            }, 2000);

            console.log("视频开始播放");
        },
        //构建多视频界面下载
        /*handleVideoDom: function () {
            $('.plan-show-video-tab-content').empty();
            var videoData = null;
            var pvList = [];
            var totalLength = 0;
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.showNumber) {
                    videoData = video;
                    pvList = video.videoUrl;
                    totalLength = video.totalLength;
                }
            });
            var navTabDiv = $('<div class="tab-pane fade active show" id="nav-tab-0"></div>');
            var videoItemUl = $('<ul class="attached-document plan-show-attached-document clearfix"></ul>');
            navTabDiv.append(videoItemUl);

            for (var j = 0; j < pvList.length; j++) {
                var url = pvList[j].url;
                var videoItem = $('<li class="fa-video">' +
                    '                  <div class="document-file">' +
                    '                      <a title="点击下载" href="'+ contextPath + "plan/download/?url=" + url +'" download="'+ pvList[j].startRecTime +  '.mp4">' +
                    '                          <img title="点击下载" src="'+ contextPath + 'plan/getComment?trialId=' + ready.trialId + '&picName=' + ready.showNumber + pvList[j].startRecTime.toString().replace(":","") + '.jpg" alt=""/>' +
                    '                      </a>' +
                    '                  </div>' +
                    // '                  <div class="document-name"><a title="点击下载" href="'+ contextPath + "plan/download/?url=" + url +'" download="'+ pvList[j].startRecTime +  '.mp4">' + pvList[j].startRecTime + '</a></div>' +
                    '                  <div class="document-name"><a title="点击下载" href="'+ contextPath + "plan/download/?url=" + url +'" download="'+ pvList[j].startRecTime +  '.mp4"> 点击下载 </a></div>' +
                    '              </li>');
                videoItemUl.append(videoItem);
            }

            $('.plan-show-video-tab-content').append(navTabDiv);
        },*/
        initVideoData: function () { //加载视频数据
            function dataErr() {
                swal({
                    title: '数据出错!',
                    type: 'error',
                    confirmButtonText: '确 认'
                }, function () {
                    // window.location.href = contextPath + 'plan/show/' + ready.planId;
                });
            }
            $.ajax({
                type: 'post',
                url: contextPath + 'api/taiChi/showVideo/' + ready.trialId,
                dataType: 'json',
                success: function (result) {
                    console.log("服务器返回数据集：" + JSON.stringify(result));
                    if (result.code === 0) {
                        ready.data = result.data;
                        core.handleVideo();//初始化视频
                    }
                },
                error: function () {
                    dataErr();
                }
            });

        },
        changeChn: function () { //切换通道号
            $('.plan-show-chn li').click(function () { //点击切换通道事件
                var number = $(this).find('a').attr('data');
                var name = $(this).find('a').text();
                console.log("切换通道：" + number);
                console.log("切换通道名称：" + name);

                ready.showNumber = number;
                ready.chnname = name;
                core.handleVideo();
            });
        }
    };

    function getNow(s) {
        return s < 10 ? '0' + s : s;
    }

    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
