;-function (window) {
    //内部核心属性
    const core = {
        render_table: function () {
            $('#data-table').DataTable({
                language: {
                    'sProcessing': '数据查询中,请稍候...',
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
                    url: contextPath + 'task/list',
                    type: "POST"
                },
                aoColumns: [//初始化要显示的列
                    {
                        "mDataProp": "name",//任务名称
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "content",//任务名称
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "devices",//已绑定主机
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "executed",//已绑定主机
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "radio",//比例
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "id",//任务主键
                        "sClass": "center",
                        "mRender": function (data) {
                            let html = '<div class="table-text">';
                            html += '<a href="' + contextPath + 'task/edit/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>';
                            html += '<a href="javascript:void(0);" data-id="'+ data +'" class="btn btn-inverse btn-xs m-r-5 btn-enabled taskDel">删除</a>';
                            html += '<a href="javascript:void(0);" data-id="'+ data +'" class="btn btn-inverse btn-xs m-r-5 btn-enabled taskExec">执行</a>';
                            html += '</div>';
                            return html
                        }
                    }
                ],
                fnDrawCallback: function () {
                    $(".taskDel").bind('click', function () {
                        const id = $(this).attr("data-id");
                        $.get(
                            contextPath + "task/del/" + id,
                            {},
                            function (data) {
                                if (data.code === 0) {
                                    swal({
                                        title: '删除成功!',
                                        type: 'success',
                                        confirmButtonText:'确 认'
                                    },function () {
                                        window.location.reload()
                                    });
                                }else{
                                    swal({
                                        title: '删除失败!',
                                        type: 'warning',
                                        confirmButtonText:'确 认'
                                    },function () {
                                        window.location.reload()
                                    });
                                }
                            },'json'
                        )
                    })
                    $(".taskExec").bind('click', function () {
                        const id = $(this).attr("data-id");
                        $.get(
                            contextPath + "task/exec/" + id,
                            {},
                            function (data) {
                                if (data.code === 0) {
                                    console.log(data.data)
                                    swal({
                                        title: '执行结束!',
                                        text: '执行结果请自行使用`F12`查看~',
                                        confirmButtonText:'确 认'
                                    },function () {
                                        window.location.reload()
                                    });
                                }
                            },'json'
                        )
                    })
                }
            });
        }
    };
    'use strict';
    const init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        core.render_table();
    }

    //对外公开的方法
    const page = {};
    init();
    window.p = page;
}(window);
