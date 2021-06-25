/**
 * Created by sakuri on 2019/8/13.
 * 2021.04.26 >>> 添加语音传唤设置按钮脚本 daniel
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
        core.voiceSet()
    }

    //内部核心属性
    var core = {
        voiceSet: function () {
            $("#voiceSet").bind('click', function () {
                window.location.href = contextPath + 'system/voiceEdit';
            })
        },
        /**
         * 删除所选择的法庭
         */
        delCheckedBtn: function () {
            $("#checkedBtn").bind('click', function () {
                swal({
                    title: "确认删除?",
                    type: "warning",
                    showCancelButton: true,
                    cancelButtonText: '取 消',
                    cancelButtonColor: '#b9b9b9',
                    showConfirmButton: true,
                    confirmButtonText: '确 认',
                    confirmButtonColor: "#dd6b55",
                    closeOnConfirm: false,
                    closeOnCancel: true
                }, function () {
                    var checkedBox = document.getElementsByName("checkbox-select");
                    var deviceIds = "";
                    for (var i = 0; i < checkedBox.length; i++) {
                        if (checkedBox[i].checked) {
                            if (deviceIds == "") {
                                deviceIds = checkedBox[i].value;
                            } else {
                                deviceIds = deviceIds + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'detentionDevice/del',
                        {
                            deviceIds: deviceIds
                        },
                        function (result) {
                            if (result.code == 0) {
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText: '确 认'
                                }, function () {
                                    window.location.href = contextPath + 'detentionDevice/list';
                                });
                            } else {
                                swal({
                                    title: '删除失败',
                                    type: 'error',
                                    confirmButtonText: '确 认'
                                })
                            }
                        }, 'json'
                    )
                })
            })
        },
        handleSelect: function () {
            $('#table_checkbox_all').bind('click', function (e) {
                var checkbox = $("input[type='checkbox']");
                if ($(this).is(':checked')) {
                    checkbox.prop("checked", true);
                } else {
                    checkbox.prop("checked", false);
                }
            });
            //每一次的小checkbox点击事件
            $(".ck").bind('click', function () {
                //获取所有的小的checkbox
                var checkBoxes = $(".ck");
                for (var i = 0; i < checkBoxes.length; i++) {
                    if (!checkBoxes[i].checked) {
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
                    'sProcessing': '查询设备在线状态中,请稍候...',
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
                    url: contextPath + 'detentionDevice/list',
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
                        "mDataProp": "deviceName",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "ip",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "port",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "courtroom",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "isConnect",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "ver",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "status",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "id",//获取列数据，跟服务器返回字段一致
                        "sClass": "center",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            return '<div class="table-text">' +
                                        '<a href="' + contextPath + 'detentionDevice/edit/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>' +
                                   '</div>'
                        }
                    }
                ],
                fnDrawCallback: function () {
                    core.handleSelect();
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
