var client
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
        core.parsley();
        core.render_table();
        core.datetime();
        core.resetInput();
        core.queryCondition();//多条件查询
        core.cdButton();//点击集中刻录功能
        core.ajax_form();
    }

    //内部核心属性
    var core = {
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
                    url: contextPath + 'trials/list',
                    type: "POST",
                    "data": function (d) {
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
                        "mDataProp": "id",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            return '<td class="with-checkbox">' +
                                '<div class="radio radio-css radio-inline">' +
                                '<input class="ck" type="radio" name="enabled" value="' + data + '" value="'+ data +'" id="table_checkbox_' + data + '" data-user="' + data + '" />' +
                                '<label for="table_checkbox_' + data + '">&nbsp;</label>' +
                                '</div>'
                        }
                    },
                    {
                        "mDataProp": "archives",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "name",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "courtroomName",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "judgeName",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "secretaryName",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "startDate",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "endDate",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "status",
                        "mRender": function (data, type, full) {
                            return "<div class='canclePadding'>" + data + "</div>"
                        }
                    }
                ],
                fnDrawCallback: function () {
                    $(':radio').click(function(){
                        ready.trialId = $(this).val();
                    });
                }
            });
        },
        cdButton: function (){
            //点击需要刻录的庭审
            $('.cdBurn').click(function (e) {
                var trialId = ready.trialId;
                $.get(
                    contextPath + 'trials/getChn',
                    {
                        trialId: trialId
                    },
                    function (result) {
                        if (result.code == 1) {
                            swal({
                                title: '请选择需要刻录的庭审!',
                                type: 'warning',
                                confirmButtonText: '确 认'
                            }, function () {
                                window.location.href = contextPath + 'trials/list';
                            });
                        } else {
                            $("#select_cdChn").empty();
                            $("#select_cdChn").append("<option value=\"\">请选择刻录通道</option>")
                            for (var i=0; i < result.data.videoInfo.length; i++){
                                $("#select_cdChn").append("<option value='"+result.data.videoInfo[i].name+"'>"+result.data.videoInfo[i].name+"</option>");
                            }
                            
                        }
                    }, 'json'
                )
            });
        },
        ajax_form : function(){
            $('form').ajaxForm({
                type: 'post',
                dataType: 'json',
                url: contextPath + 'trials/postConburn',
                // 提交之前的验证---->通过parsely进行验证
                beforeSubmit: function(){
                    if ($("#burnNum").val() === null || $("#burnNum").val()==="" || $("#chnName").val() === null || $("#chnName").val()===""){
                        console.log("不能为空")
                        return 
                    }
                },
                beforeSerialize: function(){
                    $("#trialId").val(ready.trialId);
                },
                //回调成功
                success: function (data) {
                    if (data.code === 0) {
                        swal({
                            title: '操作成功!',
                            type: 'success',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "trials/list";
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "trials/list";
                        });
                    }
                },
                //回调失败
                error: function () {
                    swal({
                        title: '数据出错!',
                        type: 'error',
                        confirmButtonText:'确 认'
                    },function () {
                        window.location.href= contextPath + "trials/list";
                    });
                }
            });
        },
        parsley : function(){
            $("#cdBurnForm").parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        },
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
