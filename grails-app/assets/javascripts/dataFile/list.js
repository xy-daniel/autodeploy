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
                    url: contextPath + 'dataFile/list/' + $("#tableId").val(),
                    type: "POST"
                },
                aoColumns: [//初始化要显示的列
                    {
                        "mDataProp": "tableName",//主机名称
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "address",//用户名
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "size",//密码
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "dateCreated",//表名
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "id",//主机主键
                        "sClass": "center",
                        "mRender": function (data, type, full) {
                            let html = '<div class="table-text">';
                            html += '<a href="javascript:void(0);" data-id="'+ data +'" class="btn btn-inverse btn-xs m-r-5 btn-enabled del">删除</a>';
                            html += '<a href="' + contextPath + 'dataFile/download?filePath=' + encodeURI(full.address) + '"  class="btn btn-inverse btn-xs m-r-5 btn-enabled">下载</a>';
                            html += '</div>';
                            return html
                        }
                    }
                ],
                fnDrawCallback: function () {
                    $(".del").bind('click', function () {
                        const id = $(this).attr("data-id");
                        $.get(
                            contextPath + "dataFile/del/" + id,
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
                                        type: 'error',
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
