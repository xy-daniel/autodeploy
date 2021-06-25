/**
 * Created by sakuri on 2019/8/13.
 */
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
        core.queryCondition();
        core.resetInput();
        core.render_table();
        core.datetime();
    }

    //内部核心属性
    var core = {
        datetime: function () {
            $('#startDate').datetimepicker({
                format: 'YYYY/MM/DD',
                collapse: false,
                widgetPositioning: {
                    horizontal: 'auto',
                    vertical: 'bottom'
                }
            });
            $('#endDate').datetimepicker({
                format: 'YYYY/MM/DD',
                collapse: false,
                widgetPositioning: {
                    horizontal: 'auto',
                    vertical: 'bottom'
                }
            });
        },
        resetInput:function(){
            $("#reset").click(function () {
                $('#archives').val('');
                $('#name').val('');
                $('.selectpicker option').prop('selected', function () {
                    return this.defaultSelected;
                });
                $('.selectpicker').selectpicker('render');
                $('#startDate').val('');
                $('#endDate').val('');
            });
        },
        queryCondition:function(){
            $("#query").bind("click", function () { //点击查询按钮 触发table重新请求服务器
                $("#data-table").dataTable().fnDraw(true);
            });
        },

        render_table: function () {
            $('#data-table').DataTable({
                "dom": 'rtlpi',
                "searching": false,
                "bFilter": true,
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
                    url: contextPath + 'leader/list',
                    type: "POST",
                    "data": function ( d ) {
                        //添加额外的参数传给服务器
                        d.archives = $("#archives").val();
                        d.name = $("#name").val();
                        d.courtroom = $("#courtroom").find("option:selected").val();
                        d.judge = $("#judge").find("option:selected").val();
                        d.secretary = $("#secretary").find("option:selected").val();
                        d.status = $("#status").find("option:selected").val();
                        d.startDate = $("#startDate").val();
                        d.endDate = $("#endDate").val();
                        d.pageType = $("#pageType").val();

                    }
                },
                aoColumns: [//初始化要显示的列
                    {
                        "sWidth":"2%",
                        "mDataProp": "num",//获取列数据，跟服务器返回字段一致
                        "sClass": "table-select",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            return data
                        }
                    },
                    {
                        "sWidth":"11%",
                        "mDataProp": "archives",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"11%",
                        "mDataProp": "caseName",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"11%",
                        "mDataProp": "caseType",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"11%",
                        "mDataProp": "startDate",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"11%",
                        "mDataProp": "courtroom",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"11%",
                        "mDataProp": "judge",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"11%",
                        "mDataProp": "secretary",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "sWidth":"11%",
                        "mDataProp": "id",//获取列数据，跟服务器返回字段一致
                        "sClass": "table-select",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            var html =  '<div class="table-text">';
                            html += '<a href="'+contextPath+'leader/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled" style="padding-top:5px ">详情</a> </sec:ifAnyGranted>';
                            html +=  '</div>';
                            return html
                        }
                    }
                ],
                fnDrawCallback: function () {

                }
            });
        },

    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
