//当前播放列表
var play_list = [];
//当前播放通道号
var now_chn;
//var options = [":rtsp-tcp"];
var options=[":vout={directdraw}"]
var initSample = (function () {
    //构建顶部通道
    for (var re in record) {
        if (record.hasOwnProperty(re)) {
            var html_temp = $(' <div class="col-lg-2 col-md-4">\n' +
                '                <div class="widget widget-stats bg-grey-darker" id="' + re + '">\n' +
                '                    <div class="stats-icon"><i class="fa fa-desktop"></i></div>\n' +
                '                    <div class="stats-info">\n' +
                '                        <!--<h4>TOTAL VISITORS</h4>-->\n' +
                '                        <p></p>\n' +
                '                    </div>\n' +
                '                    <div class="stats-link">\n' +
                '                        <a href="javascript:;" onclick="changeVideo(' + re + ')">' +
                '                           选择画面 <i class="fa fa-arrow-alt-circle-right"></i></a>\n' +
                '                    </div>\n' +
                '                </div>\n' +
                '            </div>');
            $(record[re]).each(function (i, chnInfo) {
                html_temp.find('p').html(chnInfo.name);
            });
        }
        //为div赋予点击事件
        $('#chn_row').append(html_temp);
    }
    //获取当前通道,第一次启动默认使用第一个通道
    now_chn = $('#chn_row').find('.col-lg-2').eq(0).find('div').attr('id');
    //构建第一次播放列表
    changePlayList(now_chn);

    //高亮当前播放通道
    $('#' + now_chn).attr('class', 'widget widget-stats bg-black-lighter');
   // player = new ckplayer(videoObject);
    /**
     * 修改日志2020-07-09  换成vlc播放器
     * gaoziwei
     * */
    changePlayPart();
    //默认播放合成通道的第一段视频

});

var changeVideo = function (chn) {
   // console.log('change chn:' + chn);
    //高亮当前的播放通道
    $('.widget').attr('class', 'widget widget-stats bg-grey-darker');
    $('#' + chn).attr('class', 'widget widget-stats bg-black-lighter');
    //循环record来重新初始化播放列表
    changePlayList(chn);
    changePlayPart();
};

//初始化播放列表
var changePlayList = function (chn) {
    play_list = [];
    Skyvis_ActiveX_Player = getVLC("vlc");
    var count=Skyvis_ActiveX_Player.playlist.items.count;
    if(count>0){
        Skyvis_ActiveX_Player.playlist.items.clear();
    }
    for (var re in record) {
            if (re == chn) {
                $(record[re]).each(function (i, chnInfo) {
                    Skyvis_ActiveX_Player.playlist.add(chnInfo.playUrl,"",options);
                    var video = {};
                    video.file = chnInfo.playUrl;
                    video.type = 'video/mp4';
                    play_list.push(video);
                });
            }

    }
    play_count=0;
    //先停止播放
   // Skyvis_ActiveX_Player.playlist.stop();
    //直接播放
    Skyvis_ActiveX_Player.playlist.play();
};

//构建右边每个通道的视频分段,可以高亮指定视频分段
var changePlayPart = function (index) {
    index = index || 0;
    var table = $('.table').find('thead').find('td');
    table.empty();
    table.append('<div class="btn-group">');
    $(play_list).each(function (i, n) {
        if (i == index) {
            table.append(' <button class="btn btn-danger" onclick="partClick(' + (i + 1) + ')">第' + (i + 1) + '段</button> ')
        } else {
            table.append(' <button class="btn btn-primary" onclick="partClick(' + (i + 1) + ')">第' + (i + 1) + '段</button> ')
        }
    });
    table.append('</div>');
};
//点击通道分段视频,取消高亮当前的div,高亮选中的
//改变播放的视频
var partClick = function (i) {
    lightPart(i);
    changePlay(i);
};
var lightPart = function (i) {
    if (i) {
        var table = $('.table').find('thead').find('td');
        table.find('.btn').attr('class', 'btn btn btn-primary');
        table.find('.btn').eq(i-1).attr('class', 'btn btn-danger');

    }
};
var changePlay = function (playNum) {
    var vlc= getVLC("vlc");
    vlc.playlist.stop();
     play_count=playNum-1;
      vlc.playlist.playItem(play_count);

};

var caseDetail = function () {
    //转换json
    var trial = $.parseJSON(trialDetail);
    $('#video_now_title').html('正在播放' + trial.caseName)
    var table = $('.table');
    table.append('<tbody>');
    table.append('<tr><td>案件名称</td><td>' + trial.caseName + '</td></tr>')
    table.append('<tr><td>案号</td><td>' + trial.caseNo + '</td></tr>')
    table.append('<tr><td>原告</td><td>' + trial.accuse + '</td></tr>')
    table.append('<tr><td>被告</td><td>' + trial.accused + '</td></tr>')
    table.append('<tr><td>法官名称</td><td>' + trial.judgeName + '</td></tr>')
    table.append('<tr><td>书记员名称</td><td>' + trial.secretaryName + '</td></tr>')
    table.append('<tr><td>审判法庭</td><td>' + trial.courtroomName + '</td></tr>')
    table.append('<tr><td>计划开庭</td><td>' + trial.planStart + '</td></tr>')
    table.append('<tr><td>开庭时间</td><td>' + trial.startDate + '</td></tr>')
    table.append('<tr><td>闭庭时间</td><td>' + trial.endDate + '</td></tr>')
    table.append('<tr><td>案件状态</td><td>' + trial.status + '</td></tr>')
    table.append('</tbody>')
};
var Tcs_index = function () {
    return {
        //main function
        init: function () {
            initSample();
            caseDetail();
        }
    };
}();
