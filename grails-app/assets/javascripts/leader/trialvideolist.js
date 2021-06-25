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
        // App.init();
        // $('.input-daterange').datepicker({
        //     todayHighlight: true
        // });
    }

    function init_event() {//初始化页面事件
        core.render_table();
        core.datetime();
        core.resetInput();
        core.queryCondition();//多条件查询
    }

    //内部核心属性
    var core = {
        handleSelectAll: function () {
            $('#table_checkbox_all').click(function (e) {
                var checkbox = $("input[type='checkbox']");
                if ($(this).is(':checked')) {
                    checkbox.prop("checked", true);
                } else {
                    checkbox.prop("checked", false);
                }
            });
            //每一次的小checkbox点击事件
            $(".ck").click(function () {
                //获取所有的小的checkbox
                var checkboxs = $(".ck");
                for (var i = 0; i < checkboxs.length; i++) {
                    if (!checkboxs[i].checked) {
                        $('#table_checkbox_all').prop("checked", false);
                        return;
                    }
                }
                $('#table_checkbox_all').prop("checked", true);
            })
        },
        resetInput: function () {
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
        queryCondition: function () {
            $("#query").bind("click", function () { //点击查询按钮 触发table重新请求服务器
                $("#data-table").dataTable().fnDraw(true);
            });
        },
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
                    url: contextPath + 'leader/trialVideoList',
                    type: "POST",
                    "data": function (d) {
                        //添加额外的参数传给服务器
                        d.archives = $("#archives").val();
                        d.name = $("#name").val();
                        d.courtroom = $("#courtroom").find("option:selected").val();
                        d.judge = $("#judge").find("option:selected").val();
                        d.secretary = $("#secretary").find("option:selected").val();
                        d.startDate = $("#startDate").val();
                        d.endDate = $("#endDate").val();
                        d.pageType = $("#pageType").val();
                    }
                },
                aoColumns: [//初始化要显示的列
                    {
                        "sWidth":"1%",
                        "mDataProp": "num",//获取列数据，跟服务器返回字段一致
                        "sClass": "table-select",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            return data
                        }
                    },
                    {
                        "sWidth":"9%",
                        "mDataProp": "archives",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"10%",
                        "mDataProp": "caseName",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"10%",
                        "mDataProp": "caseType",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"10%",
                        "mDataProp": "startDate",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"10%",
                        "mDataProp": "courtroom",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"10%",
                        "mDataProp": "judge",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            console.log(data)
                            return data

                        }
                    },
                    {
                        "sWidth":"10%",
                        "mDataProp": "secretary",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "sWidth":"10%",
                        "mDataProp": "status",
                        "mRender": function (data, type, full) {
                            // if (data.indexOf("并案") === -1) {
                            //     return data
                            // }
                            // return "<div class='canclePadding'>" + data + "</div>"
                            return data
                        }
                    },
                    {
                        "sWidth":"10%",
                        "mDataProp": "id",//获取列数据，跟服务器返回字段一致
                        "sClass": "center",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            var html = '<div class="table-text">';

                            if ($("#pageType").val() === 'trialLive') {
                                //v2庭审直播
                                html += '<a href="' + contextPath + 'leader/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">详情</a>';
                            } else if ($("#pageType").val() === 'trialVideo') {
                                //v2庭审点播
                                html += '<a href="' + contextPath + 'leader/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">详情</a>';
                            } else {
                                //v2庭审预告 || v1庭审管理
                                html += '<a href="' + contextPath + 'leader/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">详情</a>';
                            }
                            html += '</div>';

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
