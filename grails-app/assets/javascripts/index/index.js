/**
 * Created by sakuri on 2019/8/13.
 */
;-function (window) {
    'use strict';
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };
    var ready = {
        now: new Date(),
        courtroom: null,
        courtroomName: null,
        sels: null,
        defaultData: null,
        planId: null
    };

    function init_ready() {//初始化预设值
        $('.index-show-video-msg').slimScroll({
            height: '100%'
        });
        $('.index-show-video-bigmsg').slimScroll({
            height: '100%'
        });
    }

    function init_event() {//初始化页面事件
        core.drawTreeview();
        core.queryCondition();

        core.handlePlanList();
        core.handlePlanShow();
    }

    //内部核心属性
    var core = {

        drawTreeview: function () {//根据指定日期显示排期列表
            $(document).ready(function () {
                $('#treeview2').slimScroll({
                    height: '756px'
                });
                // renderTreeview('','start');
                core.renderTreeview('');
            });
        },

        queryCondition: function () {
            $('#input-search').on('keyup', function (e) {
                var str = $('#input-search').val();
                if ($.trim(str).length > 0) {
                    core.renderTreeview(str);
                } else {
                    core.renderTreeview('');
                }
            });
        },

        renderTreeview: function (str) {
            $.get(
                contextPath + "index/getCourtroom?courtroom= " + str,
                function (data) {
                    ready.defaultData = [
                        {
                            text: "本地法庭", //节点显示的文本值  string
                            selectable: false, //标记节点是否可以选择。false表示节点应该作为扩展标题，不会触发选择事件。  string
                            state: { //描述节点的初始状态    Object
                                checked: true, //是否选中节点
                                /*disabled: true,*/ //是否禁用节点
                                expanded: true, //是否展开节点
                                selected: false //是否选中节点
                            },
                            showTags: true,
                            // selectable:false,
                            tags: [data.data.courtroomLocalNum], //向节点的右侧添加附加信息（类似与boostrap的徽章）    Array of Strings
                            nodes: data.data.courtroomLocal
                        }
                        /*,
                        {
                            text: '远程法庭',
                            selectable: false, //标记节点是否可以选择。false表示节点应该作为扩展标题，不会触发选择事件。  string
                            state: { //描述节点的初始状态    Object
                                checked: true, //是否选中节点
                                /!*disabled: true,*!/ //是否禁用节点
                                expanded: false, //是否展开节点
                                selected: false //是否选中节点
                            },
                            showTags: true,
                            // selectable:false,
                            tags: [data.data.courtroomDistanceNum], //向节点的右侧添加附加信息（类似与boostrap的徽章）    Array of Strings
                            nodes: data.data.courtroomDistance
                        }*/
                    ];
                    var treeview2 = $('#treeview2');
                    treeview2.treeview({
                        // levels: 1,
                        showTags: true,
                        data: ready.defaultData,
                        selectedBackColor: '#242a30',
                        onNodeSelected: function (event, data) {
                            ready.sels = treeview2.treeview('getSelected');
                            for (var i = 0; i < ready.sels.length; i++) {
                                if (ready.sels[i].nodeId == data.nodeId) {
                                    continue;
                                }
                                $('#treeview2').treeview('unselectNode', [ready.sels[i].nodeId, {silent: true}]);
                            }
                            treeview2.treeview('selectNode', [data.nodeId, {silent: true}]);
                            ready.courtroom = data.id;
                            ready.courtroomName = data.text;
                            core.handlePlanDayCount(ready.now.getFullYear(), ready.now.getMonth() + 1, ready.now.getDate());
                            core.courtroomData(ready.courtroom);
                            core.handlePlanDayItem(null, ready.courtroom);
                        },
                        onNodeUnselected: function (event, data) {
                            if (ready.sels != null) {
                                for (var i = 0; i < ready.sels.length; i++) {
                                    if (ready.sels[i].nodeId == data.nodeId) {
                                        treeview2.treeview('selectNode', [data.nodeId, {silent: true}]);
                                        continue;
                                    }
                                    treeview2.treeview('unselectNode', [ready.sels[i].nodeId, {silent: true}]);
                                }
                            }
                        }
                    });
                    //默认选择第一项
                    if (data.data !== undefined && data.data !== null && (data.data.courtroomLocalNum + data.data.courtroomDistanceNum) !== 0) {
                        //默认选择第一个节点中的第一个节点
                        treeview2.treeview('selectNode', [1, {silent: true}]);
                        ready.sels = treeview2.treeview('getSelected');
                        ready.courtroom = ready.defaultData[0].nodes[0].id;
                        ready.courtroomName = ready.defaultData[0].nodes[0].text;
                        core.handlePlanDayCount(ready.now.getFullYear(), ready.now.getMonth() + 1, ready.now.getDate());
                        core.courtroomData(ready.courtroom);
                        core.handlePlanDayItem(null, ready.courtroom);
                    }
                    /* if (str == null || str == '' || str == ' '){
                         if (start == 'start'){
                             $('#treeview2').treeview('collapseAll', {
                                 silent : false//设置初始化节点关闭
                             });
                         }
                     }else{
                         $('#treeview2').treeview('expandAll', {
                             silent : false//设置初始化节点关闭
                         })
                     }*/

                }, 'json'
            )
        },
        handlePlanList: function () {//根据指定日期显示排期列表
            $(".btn").bind('click', function () {
                var datetime = $('#datetime').val();
                if (datetime == null || datetime == '') {
                    datetime = $('#schedule-calendar').val();
                }
                window.location.href = contextPath + 'plan/list?date=' + datetime;
            })
        },
        handlePlanShow: function () {//根据指定日期显示排期列表
            $(".plan-show").bind('click', function () {
                window.location.href = contextPath + 'plan/show/' + ready.planId;
            })
        },
        courtroomData: function (courtroom) {
            ready.live = $('#video');
            ready.live.empty();
            ready.live.append('<div class="index-show-video-null">\n' +
                '            <span>视频正在请求加载中.....</span>\n' +
                '        </div>');
            $.ajax({
                type: "get",
                url: contextPath + "index/courtroomData",
                data: {
                    courtroom: courtroom
                },
                dataType: "json",
                success: function (result) {
                    if (result.code === 0) {
                        core.drawSelectCourtroom(result.data)
                        $.ajax({
                            type: "get",
                            url: contextPath + "index/courtroomIsOnline",
                            data: {
                                courtroom: courtroom
                            },
                            dataType: "json",
                            success: function (result) {
                                if (result.code === 0) {
                                    core.drawSelectCourtroomVideo(result.data)
                                }
                            }
                        });
                    }
                }
            });
        },
        drawSelectCourtroomVideo: function (data) {
            // ready.planId = null; //清空planid
            //销毁之前播放器
            if (ready.flvPlayer != null) {
                if (ready.player != null) {
                    ready.player.destroy();
                    console.log("销毁了之前的播放器");
                }
                ready.flvPlayer.pause();
                ready.flvPlayer.unload();
                ready.flvPlayer.detachMediaElement();
                ready.flvPlayer.destroy();
                ready.flvPlayer = null;
            }
            if (data) {
                //添加首页右侧信息
                if (ready.allowPlay == 1) {
                    ready.live.empty();
                    ready.live.append('<div class="index-show-video-null">\n' +
                        '            <span>视频无直播权限</span>\n' +
                        '        </div>');
                } else {
                    //视频直播
                    ready.live.empty();
                    var v = $('<video width="100%" height="100%" autoplay loop></video>');
                    ready.live.append(v);
                    console.log("直播地址：" + ready.liveAddress)
                    console.log("法庭状态：" + data.isConnect)
                    if(data.isConnect === '在线状态'){//法庭在线情况下展示直播 否则显示法庭状态
                        ready.player = new DPlayer({
                            container: document.getElementById('video'),
                            live: true,
                            autoplay: true,
                            volume: 0, //设置静音播放,坑(浏览器有缓存，用户修改音量后就不默认音量为0了)
                            video: {
                                url: ready.liveAddress,
                                type: 'customFlv',
                                customType: {
                                    customFlv: function (video, player) {
                                        ready.flvPlayer = flvjs.createPlayer({
                                            type: 'flv',
                                            url: ready.liveAddress
                                        });
                                        ready.flvPlayer.attachMediaElement(video);
                                        ready.flvPlayer.load();
                                    }
                                }
                            }
                        });
                    }else{
                        ready.live.empty();
                        ready.live.append('<div class="index-show-video-null">\n' +
                            '            <span>'+ data.isConnect +'</span>\n' +
                            '        </div>');
                    }
                }
            } else {
                $(".plan-show")[0].style.display = "none";
            }
        },
        drawSelectCourtroom: function (data) {
            //添加表头
            var courtplan = $('.court-plan');
            courtplan.empty();
            courtplan.append(ready.courtroomName + "-" + "当前法庭排期信息");
            
            var msg = $('.index-show-video-msg');
            var bigmsg = $('.index-show-video-bigmsg');
            msg.empty();
            bigmsg.empty();
            msg.append('<div class="index-show-msg-null">\n' +
                '            <span></span>\n' +
                '        </div>');
            bigmsg.append('<div class="index-show-msg-null">\n' +
                '            <span>今日期无排期数据</span>\n' +
                '        </div>');
            if (data) {
                //获取返回的planid和显示按钮
                $(".plan-show")[0].style.display = "block";
                ready.planId = data.planInfo;
                if (ready.planId == null || ready.planId == ""){
                    $(".plan-show")[0].style.display = "none";
                }
                ready.allowPlay = data.allowPlay;
                ready.liveAddress = data.live
                if (data.planInfo != null) {
                    msg.empty();
                    bigmsg.empty();
                    var msgHtml =
                        '<h3>' + data.archives + '</h3>' +
                        '<h4>' + data.name + '</h4>' +
                        '<dl class="dl-horizontal">' +
                        '    <dt class="text-inverse">庭审状态</dt>';
                    if (data.status == "排期") {
                        msgHtml += '<dd><button class="btn btn-secondary btn-xs">排期</button></dd>';
                    }
                    if (data.status == "开庭") {
                        msgHtml += '<dd><button class="btn btn-success btn-xs">开庭</button></dd>';
                    }
                    msgHtml +=
                        '    <dt class="text-inverse">立案日期</dt>' +
                        '    <dd>' + (data.filingDate == null ? "无数据" : data.filingDate) + '</dd>' +
                        '    <dt class="text-inverse">所在法庭</dt>' +
                        '    <dd>' + data.courtroom + ' - ' + data.caseType + '</dd>' +
                        '    <dt class="text-inverse">庭审时间</dt>' +
                        '    <dd>' + data.startDate + ' - ' + data.endDate + '</dd>' +
                        '</dl>';
                    msg.html(msgHtml);
                    bigmsg.html(
                        '       <dl class="dl-horizontal">\n' +
                        '           <dt class="text-inverse">法官</dt>\n' +
                        '           <dd>' + data.judge + '</dd>\n' +
                        '           <dt class="text-inverse">原告</dt>\n' +
                        '           <dd>' + data.accuser + '</dd>\n' +
                        '           <dt class="text-inverse">被告</dt>\n' +
                        '           <dd>' + data.accused + '</dd>\n' +
                        '           <dt class="text-inverse">合议庭成员</dt>\n' +
                        '           <dd>' + data.collegial + '</dd>\n' +
                        '           <dt class="text-inverse">书记员</dt>\n' +
                        '           <dd>' + data.secretary + '</dd>\n' +
                        '           <dt class="text-inverse">概要:</dt>\n' +
                        '           <dd>' + data.summary + '</dd>\n' +
                        '       </dl>')
                }
            } else {
                $(".plan-show")[0].style.display = "none";
            }
        },
        handlePlanDayCount: function (year, month, day) {//获取所选月份排期数量
            $.ajax({
                type: "get",
                url: contextPath + "index/planDayCount",
                data: {
                    year: year,
                    month: month,
                    day: day,
                    courtroom: ready.courtroom
                },
                dataType: "json",
                success: function (result) {
                    if (result.code === 0) {
                        core.drawScheduleCalendar(result.data, year, month, day)
                    } else {
                    }
                }
            });
        },
        drawScheduleCalendar: function (events, year, month, day) {//根据指定数据显示日历
            var calendarTarget = $('#schedule-calendar');
            calendarTarget.empty();
            if (!day) {
                day = 1
            }
            calendarTarget.val(day + '/' + month + '/' + year);
            $(calendarTarget).calendar({
                events: events,
                popover_options: {
                    placement: 'top',
                    html: true
                }
            });
            $(calendarTarget).find('td.event').each(function () {
                var backgroundColor = $(this).css('background-color');
                $(this).removeAttr('style');
                $(this).find('a').css('background-color', backgroundColor);
            });

            $(calendarTarget).find('.icon-arrow-left, .icon-arrow-right').parent().on('click', function () {//上一页 下一页
                var year = $(calendarTarget).find('.visualmonthyear').attr("data-year");
                var month = $(calendarTarget).find('.visualmonthyear').attr("data-month");
                core.handlePlanDayCount(year, month);
            });

            $(calendarTarget).find('a[data-day]').on('click', function () {
                $(calendarTarget).find('td.event a').each(function () {
                    $(this).css('background-color', 'rgb(45, 53, 60)');
                });
                $(this).css('background-color', 'rgb(0, 172, 172)');
                $('#datetime').val($(this).attr("data-day"));
                core.handlePlanDayItem($(this).attr("data-day"), ready.courtroom);
                // courtroomData($(this).attr("data-day"),ready.courtroom);
            });
        },
        handlePlanDayItem: function (date, courtroom) {//获取指定日期排期列表数据
            $.ajax({
                type: "get",
                url: contextPath + "index/planDayItem",
                data: {
                    date: date,
                    courtroom: courtroom,
                    length: 20
                },
                dataType: "json",
                success: function (result) {
                    if (result.code === 0) {
                        core.drawPlanDayItem(result.data)
                    } else {
                    }
                }
            });
        },
        drawPlanDayItem: function (dataList) {//根据指定数据显示排期列表
            var itemPanel = $('.index-plan-list');
            itemPanel.empty();
            if (dataList.length === 0) {
                itemPanel.append(' <div class="index-plan-list-null">\n' +
                    '                  <span>今日期无排期数据</span>\n' +
                    '              </div>')
            }
            $.each(dataList, function (index, data) {
                var item
                var pageVersion = $("#pageVersion").val()
                if (pageVersion === 'v1') {
                    item = $('<a href="' + contextPath + 'plan/show/' + data.id + '" title="点击查看详情" class="list-group-item justify-content-between align-items-center text-ellipsis"></a>');
                } else if (pageVersion === 'v2') {
                    if (data.status === '开庭') {
                        item = $('<a href="' + contextPath + 'trialLive/show/' + data.id + '" title="点击查看详情" class="list-group-item justify-content-between align-items-center text-ellipsis"></a>');
                    } else if (data.status === '闭庭') {
                        item = $('<a href="' + contextPath + 'trialVideo/show/' + data.id + '" title="点击查看详情" class="list-group-item justify-content-between align-items-center text-ellipsis"></a>');
                    } else if (data.status === '排期') {
                        item = $('<a href="' + contextPath + 'plan/show/' + data.id + '" title="点击查看详情" class="list-group-item justify-content-between align-items-center text-ellipsis"></a>');
                    } else {
                        item = $('<a href="' + contextPath + 'trialVideo/show/' + data.id + '" title="点击查看详情" class="list-group-item justify-content-between align-items-center text-ellipsis"></a>');
                    }
                }
                item.append('<span class="badge f-w-500 bg-gradient-blue f-s-10 m-r-5 index-plan-list-label-levitation">' + data.status + ' ' + data.startDate + '</span>');
                item.append('<span>' + data.text + '</span>');
                itemPanel.append(item)
            });
        }

    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;

}(window);
