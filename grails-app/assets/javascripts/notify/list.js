/**
 * Created by sakuri on 2019/8/13.
 */
;-function (window) {
    'use strict';
    var init = function () {
        init_event();//初始化页面事件
    };
    function init_event() {//初始化页面事件
        core.render_table();
    }

    //内部核心属性
    var core = {
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
                    'sSearch': '搜索：',
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
                    url: contextPath + 'notify/list',
                    type: 'POST'
                },
                aoColumns: [//初始化要显示的列
                    {
                        'mDataProp': 'operator',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'isread',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'createTime',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'remark',
                        'mRender': function (data, type, full) {
                            return data
                        }
                    },
                    {
                        'mDataProp': 'id',//获取列数据，跟服务器返回字段一致
                        'sClass': 'center',//显示样式
                        'mRender': function (data, type, full) {//返回自定义的样式
                            var html =  '<div class="table-text">';
                            html += '<a href="'+contextPath+'notify/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">详情</a>';
                            html +=  '</div>';
                            return html
                        }
                    }
                ],
            });

        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
