/**
 * Created by sakuri on 2019/8/13.
 */
var client;
;-function (window) {
    'use strict';
    var ready = {};
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        core.render_table();
        client = core.connect();
    }

    //内部核心属性
    var core = {
        connect: function () {
            var socket = new SockJS(contextPath + 'stomp');
            var c = Stomp.over(socket);
            c.connect({}, function () {
                c.subscribe('/topic/stopC', function (message) {
                    window.location.reload();
                });
                c.subscribe('/topic/connect', function (message) {
                    window.location.reload();
                });
                //用户进入时刷新，用户退出时刷新，用户刷新时刷新，一段时间后系统强制刷新时刷新
                c.subscribe('/queue/editVideoStatus', function (message) {
                    setTimeout(function () {
                        window.location.reload()
                    }, 100)
                });
            });
            return c
        },
        render_table: function () {
            $('#data-table').DataTable({
                language: {
                    'sProcessing': '处理中...',
                    'sLengthMenu': '显示 _MENU_ 项结果',
                    'sZeroRecords': '没有匹配结果',
                    'sInfo': '显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项',
                    'sInfoEmpty': '显示第 0 至 0 项结果，共 0 项',
                    'sInfoFiltered': '(由 _MAX_ 项结果过滤)',
                    'sInfoPostFix': '',
                    'sSearch': '按用户名/连接状态搜索：',
                    'sUrl': '',
                    'sEmptyTable': '表中数据为空',
                    'sLoadingRecords': '载入中...',
                    'sInfoThousands': ',',
                    'oPaginate': {
                        'sFirst': '首页',
                        'sPrevious': '上页',
                        'sNext': '下页',
                        'sLast': '末页'
                    },
                    'oAria': {
                        'sSortAscending': ': 以升序排列此列',
                        'sSortDescending': ': 以降序排列此列'
                    }
                },
                serverSide: true,
                ordering: false,
                bProcessing: true,
                ajax: {
                    url: contextPath + 'videoRecord/list',
                    type: "POST"
                },
                aoColumns: [//初始化要显示的列
                    //用户姓名
                    {
                        "mDataProp": "userName",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //案件编号
                    {
                        "mDataProp": "archives",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //案件名称
                    {
                        "mDataProp": "caseName",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //ip
                    {
                        "mDataProp": "ip",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //最近观看时间
                    {
                        "mDataProp": "time",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //状态
                    {
                        "mDataProp": "playStatus",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //操作
                    {
                        "mDataProp": "id_status",//获取列数据，跟服务器返回字段一致
                        "sClass": "center",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            var id_statusArr = data.toString().split("_");
                            var html = '<div class="sr table-text">';
                            html += '<input type="hidden" value="' + id_statusArr[0] + '">';
                            //跳转到对应的排期详情页面
                            html += '<a href="'+contextPath+'plan/planDetail/' + id_statusArr[0] + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled detail">排期信息</a>';
                            if (id_statusArr[1] === '0') {
                                html += '<button class="btn btn-inverse btn-xs m-r-5 btn-enabled" onclick="stopC(this)">禁止观看</button>';
                            }
                            if (id_statusArr[1] === '2') {
                                html += '<button class="btn btn-inverse btn-xs m-r-5 btn-enabled" onclick="restart(this)">恢复观看</button>';
                            }
                            html += '</div>';
                            return html
                        }
                    }
                ]
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);

/**
 * 原生js操作恢复连接
 * @param elem  被点击的button按钮
 */
function restart(elem) {
    var vrId = elem.parentNode.firstChild.value;
    var xmlHttp;
    if (window.XMLHttpRequest) {
        xmlHttp = new XMLHttpRequest();
    } else {
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    var url = contextPath + 'videoRecord/restart?vrId=' + vrId;
    xmlHttp.open("GET", url, true);
    xmlHttp.send();
    //接收服务器返回的数据
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
            var data = JSON.parse(xmlHttp.responseText);
            if (data.code === 0) {
                window.location.reload();
            }
        }
    };
}

/**
 * 原生js操作停止用户的观看连接
 * @param elem  被点击的button按钮
 */
function stopC(elem) {
    client.send('/app/stopC', {}, elem.parentNode.firstChild.value);

}
