;-function (window) {
    //内部核心属性
    const core = {
        /**
         * 删除所选择的日志列表
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
                    const checkedBox = document.getElementsByName("checkbox-select");
                    let roleIds = "";
                    for (let i = 0; i < checkedBox.length; i++) {
                        if (checkedBox[i].checked) {
                            if (roleIds === "") {
                                roleIds = checkedBox[i].value;
                            } else {
                                roleIds = roleIds + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'role/del',
                        {
                            roleIds: roleIds
                        },
                        function (result) {
                            if (result.code === 0) {
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText: '确 认'
                                }, function () {
                                    window.location.href = contextPath + 'role/list';
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
                                    window.location.href = contextPath + 'role/list';
                                });
                            }
                        }, 'json'
                    )
                })
            })
        },
        handleSelectAll: function () {
            $('#table_checkbox_all').bind('click', function (e) {
                const checkbox = $("input[type='checkbox']");
                if ($(this).is(':checked')) {
                    checkbox.prop("checked", true);
                } else {
                    checkbox.prop("checked", false);
                }
            });
            //每一次的小checkbox点击事件
            $(".ck").bind('click', function () {
                //获取所有的小的checkbox
                const table_checkbox_all = $("#table_checkbox_all");
                const checkBoxes = $(".ck");
                for (let i = 0; i < checkBoxes.length; i++) {
                    if (!checkBoxes[i].checked) {
                        table_checkbox_all.prop("checked", false);
                        return;
                    }
                }
                table_checkbox_all.prop("checked", true);
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
                    url: contextPath + 'role/list',
                    type: "POST"
                },
                aoColumns: [//初始化要显示的列
                    //id
                    {
                        "mDataProp": "id",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            return '<td class="with-checkbox">' +
                                '<div class="checkbox checkbox-css">' +
                                '<input class="ck" type="checkbox" name="checkbox-select" value="' + data + '" id="table_checkbox_' + data + '" data-role="' + data + '" />' +
                                '<label for="table_checkbox_' + data + '">&nbsp;</label>' +
                                '</div>'
                        }
                    },
                    //排序
                    {
                        "mDataProp": "sequence",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //角色名称
                    {
                        "mDataProp": "authority",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //描述
                    {
                        "mDataProp": "remark",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    //对该用户的操作
                    {
                        "mDataProp": "id",//获取列数据，跟服务器返回字段一致
                        "sClass": "center",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            let html = '<div class="table-text">';
                            html += '<a href="' + contextPath + 'role/edit/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>';
                            html += '</div>';
                            return html
                        }
                    }
                ],
                fnDrawCallback: function () {
                    core.handleSelectAll();
                }
            });
        }
    };
    'use strict';
    const ready = {};
    const option = {};
    const init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        core.render_table();
        core.delCheckedBtn();
    }

    //对外公开的方法
    const page = {};
    init();
    window.p = page;
}(window);
