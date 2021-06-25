/**
 * 远程法院列表页面脚本 created by daniel in 2021.04.19
 * 2021.04.20 >>> 添加删除法院功能 daniel
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
        core.addDc();
    }

    //内部核心属性
    var core = {
        addDc: function () {
            $("#addDc").bind('click', function () {
                var hrefStr = window.location.href;
                var hrefNum = window.location.href.lastIndexOf("/");
                var id = hrefStr.substring(hrefNum + 1);
                if (id === "list") {
                    id = 0;
                }
                window.location.href = contextPath + "distanceCourt/add/" + id;
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
                    var ids = "";
                    for (var i = 0; i < checkedBox.length; i++) {
                        if (checkedBox[i].checked) {
                            if (ids === "") {
                                ids = checkedBox[i].value;
                            } else {
                                ids = ids + "," + checkedBox[i].value;
                            }
                        }
                    }
                    //逻辑删除
                    $.get(
                        contextPath + 'distanceCourt/del',
                        {
                            ids: ids
                        },
                        function (result) {
                            if (result.code === 0) {
                                var title = ""
                                if (result.data === 0) {
                                    title = '删除成功!'
                                } else {
                                    title = "删除成功!(已跳过含有下级法院的选项)"
                                }
                                swal({
                                    title: title,
                                    type: 'success',
                                    confirmButtonText: '确 认'
                                }, function () {
                                    window.location.href = contextPath + 'distanceCourt/list';
                                });
                            } else if (result.code === 410) {
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText: '确 认'
                                })
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
            let indexOf = window.location.href.lastIndexOf("/");
            let id = window.location.href.substring(indexOf + 1);
            if (id === "list") {
                id = 0;
            }
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
                    url: contextPath + 'distanceCourt/list/' + id,
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
                    //法院名称
                    {
                        "mDataProp": "name",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //短名称
                    {
                        "mDataProp": "shortName",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //分级码
                    {
                        "mDataProp": "code",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //服务地址
                    {
                        "mDataProp": "service",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //上级法院
                    {
                        "mDataProp": "parentName",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "id",//获取列数据，跟服务器返回字段一致
                        "sClass": "center",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            var html = '<div class="table-text">';
                            html += '<a href="' + contextPath + 'distanceCourt/list/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">下级法院</a>';
                            html += '<a href="' + contextPath + 'distanceCourt/edit/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>';
                            html += '</div>';
                            return html
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
