/**
 * Created by sakuri on 2019/8/13.
 */

;-function (window) {
    'use strict';
    var ready = {
        planId: $('.plaan-info').attr('planId'),
        trialId: $('.plaan-info').attr('trialId'),
        allowPlay: $('.plaan-info').attr('allowPlay'),
        chnNumber: $('.plan-tab-chn.active').attr('data'),
        chnName: $('.plan-tab-chn.active').text(),
        data: null,
        client: null
    };
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件

    };

    function init_ready() {//初始化预设值
    }

    function init_event() {
        ready.client = core.connect();//创建websocket连接并初始化聊天记录
        core.chatScroll();//初始化聊天框的滚动条界面
        core.sendMsg();//发送通讯消息事件
        core.deleteMsg();//删除通讯消息事件
        core.initVideoData(); //初始化视频数据事件
        core.videoPlay();//视频播放事件
        core.caseChange();//并案案件详情查询事件
        core.selectpicker();//初始化select界面
        core.onbeforeunload();//离开页面事件
        core.consoleBtn();//控制台按钮事件
        core.changeChn();//切换通道事件
        core.importWord();//上传笔录事件
        core.handleNote();//获取笔录事件
        core.handleNoteFresh();//定时刷新笔录事件
    }

    //内部核心属性
    var core = {
        //创建连接
        connect: function () {
            var socket = new SockJS(contextPath + 'stomp');
            var c = Stomp.over(socket);
            c.connect({}, function () {
                //连接成功了发送用户上线信息
                c.send('/app/connect', {}, "有用户上线");
                //聊天订阅功能
                c.subscribe('/topic/chat', function (message) {
                    var jsonData = JSON.parse(message.body);
                    var otherUserName = jsonData.userName;
                    var otherChatContext = jsonData.chatContext;
                    var otherPlanId = jsonData.planId.toString();
                    var name = otherUserName + "(" + jsonData.time + ")";
                    var uuid = jsonData.uuid;
                    //获取当前用户名和当前法庭主键
                    var userName = $("#userName").val();
                    var planId = $("#planId").val();
                    if (planId !== otherPlanId) {
                        return
                    }
                    var chatDiv = $(".chat");
                    if (userName === otherUserName) {
                        //添加在右侧
                        chatDiv.append("<div class=\"media media-sm message\">" +
                            "                                                <input type=\"hidden\" value=\"" + uuid + "\">" +
                            "                                                <div class=\"media-body\">" +
                            "                                                    <div class=\"row\">" +
                            "                                                        <div class=\"col-3\">" +
                            "                                                                <div class=\"checkbox checkbox-css\">" +
                            "                                                                    <input class=\"ck\" type=\"checkbox\" name=\"checkbox-select\" value=\"" + uuid + "\" id=\"table_checkbox_" + uuid + "\"\" />" +
                            "                                                                    <label for=\"table_checkbox_" + uuid + "\"></label>" +
                            "                                                                </div>" +
                            "                                                        </div>" +
                            "                                                        <div class=\"col-9\">" +
                            "                                                            <h4 class=\"media-heading\" style=\"text-align: right\">" + name + "</h4>" +
                            "                                                        </div>" +
                            "                                                    </div>" +
                            "                                                    <div class=\"row\">" +
                            "                                                        <div class=\"col-3\"></div>" +
                            "                                                        <div class=\"col-9\"><p style=\"text-align: left;float: right;word-break: break-all\">" + otherChatContext + "</p></div>" +
                            "                                                    </div>" +
                            "                                                </div>" +
                            "                                            </div>")
                    } else {
                        //添加在左侧
                        chatDiv.append("<div class=\"media media-sm message\">" +
                            "                                                <input type=\"hidden\" value=\"" + uuid + "\">" +
                            "                                                <div class=\"media-body\">" +
                            "                                                    <div class=\"row\">" +
                            "                                                        <div class=\"col-1\" style=\"margin-right: -10px\">" +
                            "                                                                <div class=\"checkbox checkbox-css\">" +
                            "                                                                    <input class=\"ck\" type=\"checkbox\" name=\"checkbox-select\" value=\"" + uuid + "\" id=\"table_checkbox_" + uuid + "\"/>" +
                            "                                                                    <label for=\"table_checkbox_" + uuid + "\"></label>" +
                            "                                                                </div>" +
                            "                                                        </div>" +
                            "                                                        <div class=\"col-11\" style=\"padding-left: 0;padding-top: 3px\">" +
                            "                                                            <h4 class=\"media-heading\">" + name + "</h4>" +
                            "                                                        </div>" +
                            "                                                    </div>" +
                            "                                                    <p style=\"width: 75%;word-break:break-word;padding-left: 30px\">" + otherChatContext + "</p>" +
                            "                                                </div>" +
                            "                                            </div>")
                    }
                    chatDiv[0].scrollTop = chatDiv[0].scrollHeight;
                });
                //这儿接收到消息用于将本用户本排期的相关观看记录修改为正在连接的状态
                c.subscribe('/queue/editVideoStatus', function (message) {
                    if (message.body !== '0') {
                        return
                    }
                    $.get(
                        contextPath + 'plan/editVideoStatus',
                        {
                            planId: $("#planId").val()
                        },
                        function (result) {
                            //如果修改失败刷新页面
                            if (result.code === 1) {
                                window.location.reload()
                            }
                        }, 'json'
                    )
                });
                //被管理员禁止后触发
                c.subscribe('/topic/stopC', function (message) {
                    var jsonData = JSON.parse(message.body);
                    var planId = jsonData.planId.toString();
                    var userId = jsonData.userId.toString();
                    var flag = jsonData.flag.toString();
                    if (flag === "0") {
                        return
                    }
                    if (userId === $("#userId").val() && planId === $("#planId").val()) {
                        //退出程序
                        swal({
                            title: '未授权，无法观看!',
                            type: 'warning',
                            //禁止弹窗外点击
                            allowOutsideClick: false,
                            //禁止esc键弹窗
                            allowEscapeKey: false,
                            confirmButtonText: '确 认'
                        }, function () {
                            window.location.href = contextPath + "plan/list";
                        });
                        ready.player.pause()
                    }
                })
            });
            return c;
        },
        //初始化聊天框的滚动条界面
        chatScroll: function () {
            var chatdiv = document.getElementById("chat");
            chatdiv.scrollTop = chatdiv.scrollHeight;
            var penhoder = document.getElementById("penhoder");
            penhoder.scrollTop = penhoder.scrollHeight;
        },
        //发送消息事件
        sendMsg: function () {
            $('.fa-song').on("click", function () {
                //获取当前输入的文字
                var chatContextHtml = $("#chatContext");
                var chatContext = chatContextHtml.val();
                if (chatContext === "") {
                    return false;
                }
                var myDate = new Date();
                var year = myDate.getFullYear();        //获取当前年
                var month = myDate.getMonth() + 1;   //获取当前月
                var date = myDate.getDate();            //获取当前日
                var h = myDate.getHours();              //获取当前小时数(0-23)
                var m = myDate.getMinutes();          //获取当前分钟数(0-59)
                var s = myDate.getSeconds();
                var now = year + '-' + getNow(month) + "-" + getNow(date) + " " + getNow(h) + ':' + getNow(m) + ":" + getNow(s);
                //拼接用于后端处理的JSON
                var sendData = JSON.stringify({
                    userName: $("#userName").val(),
                    chatContext: chatContext,
                    planId: $("#planId").val(),
                    time: now
                });
                sendData = btoa(encodeURIComponent(sendData));
                //发送聊天信息
                ready.client.send('/app/chat', {}, sendData);
                //清空聊天框数据
                chatContextHtml.val("");
            });
        },
        //选中聊天记录的复选框删除聊天记录（只允许管理员进行操作）
        deleteMsg: function () {
            $("#delete").click(function () {
                swal({
                    title: "确认删除?",
                    type: "warning",
                    showCancelButton: true,
                    cancelButtonText: '取 消',
                    cancelButtonColor: '#b9b9b9',
                    showConfirmButton: true,
                    confirmButtonText: '确 认',
                    confirmButtonColor: "#dd6b55",
                    //禁止弹窗外点击
                    allowOutsideClick: false,
                    //禁止esc键弹窗
                    allowEscapeKey: false,
                    closeOnConfirm: false,
                    closeOnCancel: true
                }, function () {
                    var checkedBox = document.getElementsByClassName("ck");
                    var uuids = "";
                    for (var i = 0; i < checkedBox.length; i++) {
                        if (checkedBox[i].checked) {
                            if (uuids === "") {
                                uuids = checkedBox[i].value;
                            } else {
                                uuids = uuids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'plan/messageDel',
                        {
                            planId: $("#planId").val(),
                            uuids: uuids
                        },
                        function (result) {
                            if (result.code === 0) {
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText: '确 认',
                                    //禁止弹窗外点击
                                    allowOutsideClick: false,
                                    //禁止esc键弹窗
                                    allowEscapeKey: false
                                }, function () {
                                    for (var i = checkedBox.length - 1; i >= 0; i--) {
                                        if (checkedBox[i].checked) {
                                            var smallNode = checkedBox[i].parentNode.parentNode.parentNode.parentNode.parentNode;
                                            smallNode.parentNode.removeChild(smallNode);
                                        }
                                    }
                                });
                            }
                        }, 'json'
                    )
                })
            });
        },
        //加载视频数据
        initVideoData: function () {
            function dataErr() {
                swal({
                    title: '数据出错!',
                    type: 'error',
                    confirmButtonText: '确 认'
                }, function () {
                    window.location.href = contextPath + 'plan/show/' + ready.planId;
                });
            }
            $.ajax({
                type: 'post',
                url: contextPath + 'plan/showVideo/' + ready.trialId,
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
        //构建视频直播或点播界面
        handleVideo: function () { //处理视频方法
            if (ready.data.status === 0) {//排期状态
                console.log("trial处于排期状态");
                return
            }
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
            console.log("当前通道号：" + ready.chnNumber + " / 当前通道名称：" + ready.chnName);
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
                for (var i = 0; i < video.length; i++) {
                    totalLength += video[i].totalLength;
                    if (totalLength > 0) {//存在存储视频情况下
                        //构建视频界面
                        core.playVideo();
                        //构建多视频下载
                        core.handleVideoDom();
                        setInterval(function handleVideoDom() {
                            core.handleVideoDom();
                        }, 3000); //指定3秒刷新一次
                        break;
                    }
                }
            }
        },
        //视频直播事件
        liveVideo: function () {//直播视频
            var url = null;
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.chnNumber && video.name === ready.chnName) {
                    url = video.url;
                    return false
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
        //视频点播事件
        playVideo: function () {//点播视频
            var pvList = [];
            var pv = 0;
            var pCalTime = 0;
            var url = null;
            var totalLength = 0;
            //遍历视频数据获取视频信息列表
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.chnNumber && video.name === ready.chnName) {
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
                if (barTime > 0) {
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
                console.log("设置音量：" + (sound * 100) + "%")
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
            setTimeout(function () {
                var v = $('#video');
                v.css('height', v.css('height'));
            }, 2000);
            console.log("视频开始播放");
        },
        //构建多视频界面下载界面
        handleVideoDom: function () {
            $('.plan-show-video-tab-content').empty();
            var videoData = null;
            var pvList = [];
            var totalLength = 0;
            $(ready.data.video).each(function (i, video) {
                if (video.number === ready.chnNumber && video.name === ready.chnName) {
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
                    '                      <a title="点击下载" href="' + contextPath + "plan/download/?url=" + url + '" download="' + pvList[j].startRecTime + '.mp4">' +
                    '                          <img title="点击下载" src="' + contextPath + 'plan/getComment?trialId=' + ready.trialId + '&picName=' + ready.chnNumber + ready.chnName + pvList[j].startRecTime.toString().replace(":", "") + '.jpg" alt=""/>' +
                    '                      </a>' +
                    '                  </div>' +
                    // '                  <div class="document-name"><a title="点击下载" href="'+ contextPath + "plan/download/?url=" + url +'" download="'+ pvList[j].startRecTime +  '.mp4">' + pvList[j].startRecTime + '</a></div>' +
                    '                  <div class="document-name"><a title="点击下载" href="' + contextPath + "plan/download/?url=" + url + '" download="' + pvList[j].startRecTime + '.mp4"> 点击下载 </a></div>' +
                    '              </li>');
                videoItemUl.append(videoItem);
            }
            $('.plan-show-video-tab-content').append(navTabDiv);
        },
        videoPlay: function () {
            $.get(
                contextPath + 'plan/videoStatus',
                {
                    planId: $("#planId").val()
                },
                function (result) {
                    if (result.code === 1) {
                        //点击确认直接退出直播间
                        swal({
                            title: '未授权，无法观看!',
                            type: 'warning',
                            allowOutsideClick: false,
                            allowEscapeKey: false,
                            confirmButtonText: '确 认'
                        }, function () {
                            window.location.href = contextPath + "plan/list";
                        });
                    }
                }, 'json'
            )
        },
        //通道切换事件
        changeChn: function () { //切换通道号
            $('.plan-show-chn li').click(function () { //点击切换通道事件
                var number = $(this).find('a').attr('data');
                var name = $(this).find('a').text();
                console.log("切换通道号：" + number + " / 切换通道名称：" + name);
                ready.chnNumber = number;
                ready.chnName = name;
                core.handleVideo();
            });
        },
        //控制台按钮事件
        consoleBtn: function () {
            //开庭
            $("#begincourt").bind('click', function () {
                $.get(
                    contextPath + 'plan/editConsoleOpen',
                    {
                        planId: ready.planId,
                        trialId: ready.trialId
                    },
                    function (result) {
                        if (result.code === 0) {
                            swal({
                                title: '开庭成功!',
                                type: 'success',
                                confirmButtonText: '确 认'
                            }, function () {
                                window.location.href = contextPath + 'plan/show/' + ready.planId;
                            });
                        } else if (result.code === 1) {
                            swal({
                                title: '当前已是开庭状态！',
                                type: 'warning',
                                confirmButtonText: '确 认'
                            }, function () {
                            });
                        } else {
                            swal({
                                title: '开庭失败!',
                                type: 'error',
                                confirmButtonText: '确 认'
                            }, function () {
                            });
                        }
                    }, 'json'
                )
            });
            //休庭
            $("#adjournment").bind('click', function () {
                $.get(
                    contextPath + 'plan/editConsoleAdjourn',
                    {
                        planId: ready.planId,
                        trialId: ready.trialId
                    },
                    function (result) {
                        if (result.code === 0) {
                            swal({
                                title: '休庭成功！',
                                type: 'success',
                                confirmButtonText: '确 认'
                            }, function () {
                                window.location.href = contextPath + 'plan/show/' + ready.planId;
                            });
                        } else if (result.code === 1) {
                            swal({
                                title: '当前已是休庭状态！',
                                type: 'warning',
                                confirmButtonText: '确 认'
                            }, function () {
                            });
                        } else {
                            if (ready.trialId === null || ready.trialId === '') {
                                swal({
                                    title: '休庭无对应庭审！请先开庭！',
                                    type: 'warning',
                                    confirmButtonText: '确 认'
                                }, function () {
                                });
                            } else {
                                swal({
                                    title: '休庭失败！',
                                    type: 'error',
                                    confirmButtonText: '确 认'
                                }, function () {
                                });
                            }
                        }
                    }, 'json'
                )
            });
            //闭庭
            $("#adjourned").bind('click', function () {
                $.get(
                    contextPath + 'plan/editConsoleClose',
                    {
                        planId: ready.planId,
                        trialId: ready.trialId
                    },
                    function (result) {
                        if (result.code === 0) {
                            swal({
                                title: '闭庭成功!',
                                type: 'success',
                                confirmButtonText: '确 认'
                            }, function () {
                                window.location.href = contextPath + 'plan/show/' + ready.planId;
                            });
                        } else if (result.code === 1) {
                            swal({
                                title: '当前已是闭庭状态！',
                                type: 'warning',
                                confirmButtonText: '确 认'
                            }, function () {
                            });
                        } else {
                            if (ready.trialId === null || ready.trialId === '') {
                                swal({
                                    title: '闭庭无对应庭审！请先开庭！',
                                    type: 'warning',
                                    confirmButtonText: '确 认'
                                }, function () {
                                });
                            } else {
                                swal({
                                    title: '闭庭失败!',
                                    type: 'error',
                                    confirmButtonText: '确 认'
                                }, function () {
                                });
                            }
                        }
                    }, 'json'
                )
            });
        },
        //上传笔录事件
        importWord: function () {
            var saveScan = true
            //准备附件上传的样式
            $("#file-0a").fileinput({
                language: 'zh',
                showUpload: true, //是否显示上传按钮
                uploadUrl: contextPath + "plan/uploadConsoleNode/" + ready.trialId,
                dropZoneTitle: '拖拽文件到这里 &hellip;',
                maxFileSize: 102400,//文件大小限制
                maxFileCount: 1,//最大上传文件数限制
                msgFilesTooMany: "只允许上传一个文件！",
                allowedFileExtensions: ['doc', 'docx', 'pdf'] //允许上传文件的后缀
            }).on('filepreupload', function (event, data, previewId, index) {
                /*var form = data.form, files = data.files, extra = data.extra, response = data.response,
                    reader = data.reader;*/
                saveScan = false;
            }).on("fileuploaded", function (event, data) {//上传成功之后的一些处理
                if (data.response.code === 0) {
                    saveScan = true;
                    if (saveScan) {
                        swal({
                            title: '上传成功!',
                            type: 'success',
                            confirmButtonText: '确 认'
                        }, function () {
                            window.location.href = contextPath + 'plan/show/' + ready.planId;
                        });
                    } else {
                        swal({
                            title: '上传失败!',
                            type: 'error',
                            confirmButtonText: '确 认'
                        }, function () {
                        });
                    }
                } else {
                    if (ready.trialId === null || ready.trialId === '') {
                        swal({
                            title: '上传文件无对应庭审！请先开庭或有对应庭审！',
                            type: 'warning',
                            confirmButtonText: '确 认'
                        }, function () {
                        });
                    } else {
                        swal({
                            title: '上传失败!',
                            type: 'error',
                            confirmButtonText: '确 认'
                        }, function () {
                        });
                    }
                }
            });
        },
        //尝试获取笔录文件事件
        handleNote: function () {
            var penDiv = $(".penhoder");
            penDiv.html('<a href="' + contextPath + 'plan/downTrialNote/' + ready.trialId + '" class="btn btn-info">下载笔录</a>');
            $.ajax({
                type: "post",
                url: contextPath + "plan/note/" + ready.trialId,
                dataType: "json",
                success: function (result) {
                    if (result.code === 0) {
                        penDiv.append('<pre>' + result.data + '</pre>');
                    } else {
                    }
                }
            });
            penDiv[0].scrollTop = penDiv[0].scrollHeight;
        },
        //定时刷新笔录
        handleNoteFresh: function () {
            setInterval(function handleNote() {
                core.handleNote();
            }, 30000); //指定30秒刷新一次获取笔录文件
        },
        //并案案件详情查询事件
        caseChange: function () {
            $("#caselist").bind('change', function () {
                $.get(
                    contextPath + 'plan/getCaseInfo',
                    {
                        caseId: $(this).val(),
                        planId: $("#planId").val(),
                        trialId: $("#trialId").val()
                    }, function (result) {
                        var data = result.data;
                        var mainCaseDiv = $("#mainCaseDiv");
                        var collegial = "";
                        if (data.collegial.length === 0) {
                            collegial = "无数据"
                        } else {
                            for (var i = 0; i < data.collegial.length; i++) {
                                if (i === 0) {
                                    collegial = data.collegial[i].name
                                } else {
                                    collegial += "," + data.collegial[i].name
                                }
                            }
                        }
                        mainCaseDiv.empty();
                        mainCaseDiv.append("<h4>" + data.caseName + "</h4>" +
                            "                                    <dl class=\"dl-horizontal\">" +
                            "                                        <dt class=\"text-inverse\">立案日期</dt>" +
                            "                                        <dd>" + data.filingDate + "</dd>" +
                            "                                        <dt class=\"text-inverse\">所在法庭</dt>" +
                            "                                        <dd>" + data.courtroom + " - " + data.caseType + "</dd>" +
                            "                                        <dt class=\"text-inverse\">法官</dt>" +
                            "                                        <dd>" + data.judge + "</dd>" +
                            "                                        <dt class=\"text-inverse\">原告</dt>" +
                            "                                        <dd>" + data.accuser + "</dd>" +
                            "                                        <dt class=\"text-inverse\">被告</dt>" +
                            "                                        <dd>" + data.accused + "</dd>" +
                            "                                        <dt class=\"text-inverse\">庭审时间</dt>" +
                            "                                        <dd>" + data.startDate + " - " + data.endDate + "</dd>" +
                            "                                        <dt class=\"text-inverse\">合议庭成员</dt>" +
                            "                                        <dd>" + collegial + "</dd>" +
                            "                                        <dt class=\"text-inverse\">书记员</dt>" +
                            "                                        <dd>" + data.secretary + "</dd>" +
                            "                                        <dt class=\"text-inverse\">概要</dt>" +
                            "                                        <dd>" + data.gy + "</dd>" +
                            "                                        <dt class=\"text-inverse\">详情</dt>" +
                            "                                        <dd>" + data.detail + "</dd>" +
                            "                                    </dl>");
                    }, 'json');
            })
        },
        //select初始化事件
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        //离开页面事件
        onbeforeunload: function () {
            window.onbeforeunload = function (e) {
                console.log("即将离开或刷新此页面");
                $.get(
                    contextPath + 'plan/stopVideo',
                    {
                        planId: $("#planId").val()
                    },
                    function (result) {
                        //如果修改失败刷新页面
                        if (result.code === 0) {
                            console.log("本次观看结束成功");
                        }
                    }, 'json'
                )
            };
        },
    };
    function getNow(s) {
        return s < 10 ? '0' + s : s;
    }
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
