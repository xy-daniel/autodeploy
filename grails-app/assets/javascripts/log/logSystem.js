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
        core.render_table();
        core.delCheckedBtn();
    }

    //内部核心属性
    var core = {
        /**
         * 删除所选择的日志列表
         */
        delCheckedBtn: function () {
            $("#checkedBtn").click(function () {
                var checkedBox = document.getElementsByName("checkbox-select");
                var logSystemIds = "";
                for (var i = 0; i < checkedBox.length; i++) {
                    if (checkedBox[i].checked) {
                        if (logSystemIds == "") {
                            logSystemIds = checkedBox[i].value;
                        } else {
                            logSystemIds = logSystemIds + "," + checkedBox[i].value;
                        }
                    }
                }
                $.get(
                    contextPath + 'log/system/delLogs',
                    {ids: logSystemIds},
                    function (result) {
                        if (result.data == 1) {
                            swal({
                                title: '删除成功!',
                                type: 'success',
                                confirmButtonText: '确 认'
                            }, function () {
                                window.location.reload();
                            });
                        } else if (result.code === 410) {
                            swal({
                                title: '请选择数据',
                                type: 'error',
                                confirmButtonText: '确 认'
                            })
                        } else {
                            swal({
                                title: '数据删除失败，请重新操作...',
                                type: 'error',
                                confirmButtonText: '确 认'
                            }, function () {
                                window.location.reload();
                            });
                        }
                    }, 'json'
                )
            })
        },
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
                    url: contextPath + 'log/system/list',
                    type: "POST"
                },
                aoColumns: [//初始化要显示的列
                    {
                        "mDataProp": "id",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            return '<td class="with-checkbox">' +
                                '<div class="checkbox checkbox-css">' +
                                '<input class="ck" type="checkbox" name="checkbox-select" value="' + data + '" id="table_checkbox_' + data + '" data-user="' + data + '" />' +
                                '<label for="table_checkbox_' + data + '">&nbsp;</label>' +
                                '</div>'
                        }
                    },
                    {
                        "mDataProp": "level",
                        "mRender": function (data, type, full) {
                            if (data == 1) {
                                data = "ERROR";
                            }
                            if (data == 2) {
                                data = "WARN";
                            }
                            if (data == 3) {
                                data = "INFO";
                            }
                            if (data == 4) {
                                data = "DEBUG";
                            }
                            return data
                        }
                    },
                    {
                        "mDataProp": "message",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "dateCreated",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    }
                ],
                fnDrawCallback: function () {
                    core.handleSelectAll();
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
