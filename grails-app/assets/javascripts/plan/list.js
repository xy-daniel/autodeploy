/**
 * Created by sakuri on 2019/8/13.
 * 2021.05.26 >>> 并案排期 daniel
 * 2021.06.16 >>> 互联网开庭统计 daniel
 */
var client
;-function (window) {
    'use strict';
    var ready = {
        checked: "",
        hasHandleSelectAll: false
    };
    var init = function () {
        init_ready();
        init_event();
    };

    function init_ready() {
    }

    function init_event() {
        core.render_table();
        core.combinedPlan();
        core.delCheckedBtn();
        core.importPlanExcel();
        core.exportExcel();
        core.resetInput();
        core.queryCondition();
        core.datetime();
    }

    var core = {
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
                    url: contextPath + 'plan/list',
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
                        d.model = $("#model").val();
                    }
                },
                aoColumns: [//初始化要显示的列
                    {
                        "mDataProp": "id",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            if (full.status === '排期' || full.status === '排期(并案)') {
                                return '<td class="with-checkbox">' +
                                    '<div class="checkbox checkbox-css">' +
                                    '<input class="ck" type="checkbox" name="checkbox-select" value="' + data + '" id="table_checkbox_' + data + '" data-user="' + data + '" data-status="' + full.status + '" />' +
                                    '<label for="table_checkbox_' + data + '">&nbsp;</label>' +
                                    '</div>'
                            } else {
                                return ""
                            }

                        }
                    },
                    {
                        "mDataProp": "caseArchives",
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "caseName",
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "courtroom",
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "judge",
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "secretary",
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "startDate",
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "status",
                        "mRender": function (data) {
                            if (data.indexOf("并案") === -1) {
                                return data
                            }
                            return "<div class='cancelPadding'>" + data + "</div>"
                        }
                    },
                    {
                        "mDataProp": "model",
                        "mRender": function (data) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "id",//获取列数据，跟服务器返回字段一致
                        "sClass": "center",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            var html = '<div class="table-text">';
                            var pageType = $("#pageType")
                            if (pageType.val() === 'trialLive') {
                                //v2庭审直播
                                html += '<a href="' + contextPath + 'trialLive/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">详情</a>';
                            } else if (pageType.val() === 'trialVideo') {
                                //v2庭审点播
                                html += '<a href="' + contextPath + 'trialVideo/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">详情</a>';
                            } else {
                                //v2庭审预告 || v1庭审管理
                                html += '<a href="' + contextPath + 'plan/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">详情</a>';
                            }
                            if (full.status === '排期') {
                                html += '<a href="' + contextPath + 'plan/edit/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>';
                            }
                            if (full.status === '排期(并案)') {
                                html += '<a href="' + contextPath + 'plan/edit/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>';
                                html += '<button href="javascript:void(0)" class="btn btn-inverse btn-xs m-r-5 btn-enabled cancel" data-id="' + data + '">取消并案</button>';
                            }
                            //非排期、开庭状态排期添加按钮
                            if (full.status.indexOf('排期') === -1 && full.status.indexOf('开庭') === -1) {
                                html += '<button href="javascript:void(0)" class="btn btn-inverse btn-xs m-r-5' +
                                    ' btn-enabled model" data-id="' + data + '">';
                                if (full.model === "互联网开庭") {
                                    html += '设置为本地开庭';
                                } else {
                                    html += '设置为互联网开庭';
                                }
                                html += '</button>';
                            }
                            html += '</div>';
                            return html
                        }
                    }
                ],
                fnDrawCallback: function () {
                    if (!ready.hasHandleSelectAll) {
                        ready.hasHandleSelectAll = true
                        //全选按钮的点击事件
                        $('#table_checkbox_all').bind('click', function () {
                            var checkbox = $("input[type='checkbox']");
                            var checkboxes = $(".ck");
                            if ($(this).is(':checked')) {
                                checkbox.prop("checked", true);
                                for (var i = 0; i < checkboxes.length; i++) {
                                    if (checkboxes[i].getAttribute("data-status") === "排期") {
                                        ready.checked += checkboxes[i].value + ","
                                    }
                                }
                            } else {
                                checkbox.prop("checked", false);
                                for (var j = 0; j < checkboxes.length; j++) {
                                    ready.checked = ready.checked.split(checkboxes[j].value + ",").join("")
                                }
                            }
                            console.log("已选择需要并案排期的内容1:" + ready.checked)
                        });
                    }
                    //单选按钮的点击事件
                    $(".ck").bind('click', function () {
                        if (this.checked) {
                            if ($(this).attr("data-status") === "排期") {
                                ready.checked += $(this).val() + ","
                            }
                        } else {
                            ready.checked = ready.checked.split($(this).val() + ",").join("")
                        }
                        console.log("已选择需要并案排期的内容2:" + ready.checked)
                        //获取所有的小的checkbox
                        var all = $('#table_checkbox_all')
                        var checkboxes = $(".ck");
                        for (var i = 0; i < checkboxes.length; i++) {
                            if (!checkboxes[i].checked) {
                                all.prop("checked", false);
                                return;
                            }
                        }
                        all.prop("checked", true);
                    })
                    //处理并案排期的背景颜色
                    var cancelPadding = $(".cancelPadding");
                    for (var i = 0; i < cancelPadding.length; i++) {
                        cancelPadding[i].parentElement.setAttribute("style", "background: #348EE3;color: white")
                    }
                    //取消并案按钮的点击事件
                    $(".cancel").bind('click', function () {
                        var id = $(this).attr("data-id")
                        swal({
                            title: "确认取消?",
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
                            $.get(
                                contextPath + 'plan/cancelCombinedPlan',
                                {
                                    id: id
                                },
                                function (result) {
                                    if (result.code === 0) {
                                        swal({
                                            title: '取消成功!',
                                            type: 'success',
                                            confirmButtonText: '确 认'
                                        }, function () {
                                            window.location.reload()
                                        });
                                    } else {
                                        swal({
                                            title: '并案失败!',
                                            type: 'error',
                                            confirmButtonText: '确 认'
                                        });
                                    }
                                }, 'json'
                            )
                        })
                    })
                    //设置为互联网开庭模式
                    $(".model").bind("click", function() {
                        var thisEle = $(this)
                        var id = thisEle.attr("data-id")
                        var text = thisEle.text()
                        swal({
                            title: "确认"+ text +"模式?",
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
                            if (text === "设置为互联网开庭") {
                                $.get(
                                    contextPath + 'plan/internet',
                                    {
                                        id: id
                                    },
                                    function (result) {
                                        if (result.code === 0) {
                                            swal({
                                                title: '设置成功!',
                                                type: 'success',
                                                confirmButtonText: '确 认'
                                            }, function () {
                                                thisEle.text('设置为本地开庭');
                                                thisEle.parent().parent().prev().text('互联网开庭');
                                                thisEle.attr('class', 'btn btn-inverse btn-xs m-r-5 btn-enabled locale');
                                            });
                                        }
                                    }, 'json'
                                )
                            } else {
                                $.get(
                                    contextPath + 'plan/locale',
                                    {
                                        id: id
                                    },
                                    function (result) {
                                        if (result.code === 0) {
                                            swal({
                                                title: '设置成功!',
                                                type: 'success',
                                                confirmButtonText: '确 认'
                                            }, function () {
                                                thisEle.text("设置为互联网开庭");
                                                thisEle.parent().parent().prev().text('本地开庭');
                                                thisEle.attr('class', 'btn btn-inverse btn-xs m-r-5 btn-enabled internet');
                                            });
                                        }
                                    }, 'json'
                                )
                            }

                        })
                    })
                }
            });
        },
        combinedPlan: function () {
            $("#combinedPlan").bind('click', function () {
                swal({
                    title: "确认并案?",
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
                    $.get(
                        contextPath + 'plan/combinedPlan',
                        {
                            schIds: ready.checked
                        },
                        function (result) {
                            if (result.code === 0) {
                                swal({
                                    title: '并案成功!',
                                    type: 'success',
                                    confirmButtonText: '确 认'
                                }, function () {
                                    window.location.reload()
                                });
                            } else if (result.code === 110) {
                                swal({
                                    title: '请排除已并案排期!',
                                    type: 'error',
                                    confirmButtonText: '确 认'
                                })
                            } else if (result.code === 410) {
                                swal({
                                    title: '请至少选择两次排期并案!',
                                    type: 'warning',
                                    confirmButtonText: '确 认'
                                })
                            } else {
                                swal({
                                    title: '并案失败!',
                                    type: 'error',
                                    confirmButtonText: '确 认'
                                });
                            }
                        }, 'json'
                    )
                })
            })
        },
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
                    $.get(
                        contextPath + 'plan/del',
                        {
                            schIds: handleCheckedBox()
                        },
                        function (result) {
                            if (result.code === 0) {
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText: '确 认'
                                }, function () {
                                    window.location.reload()
                                });
                            } else if (result.code === 410) {
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText: '确 认'
                                })
                            } else {
                                swal({
                                    title: '删除失败！',
                                    type: 'error',
                                    confirmButtonText: '确 认'
                                }, function () {
                                    window.location.reload()
                                });
                            }
                        }, 'json'
                    )
                })
            })
        },
        importPlanExcel: function () {
            $("#morePlanAdd").bind('click', function () {
                swal({
                    title: "<small>请选择上传文件</small>!",
                    text: "<input id=\"selectFile\" name=\"file\" type=\"file\" class=\"form-control m-b-5\"/>",
                    showCancelButton: true,
                    closeOnConfirm: false,
                    cancelButtonText: '取 消',
                    confirmButtonText: '确 认',
                    html: true
                }, function () {
                    var selectFile = $("#selectFile");
                    var formData = new FormData();
                    var name = selectFile.val();
                    if (name === '' || name == null) {
                        swal({
                            title: '错误,没有选择文件!',
                            type: 'error'
                        });
                        return false
                    }
                    formData.append("file", selectFile[0].files[0]);
                    formData.append("name", name);//这个地方可以传递多个参数
                    core.loading();//显示进度条
                    $.ajax({
                        url: contextPath + "plan/importPlanExcel",
                        type: 'POST',
                        async: true,
                        data: formData,
                        processData: false,
                        contentType: false,
                        success: function (result) {
                            if (result.status === 0) {
                                swal({
                                    title: '成功',
                                    type: 'success',
                                    confirmButtonText: '确 认'
                                }, function () {
                                    window.location.reload();
                                });
                            } else {
                                swal({
                                    title: '错误!',
                                    text: result.msg,
                                    type: 'error'
                                });
                            }
                        },
                        error: function () {
                            swal({
                                title: '出现未知错误!',
                                type: 'error'
                            });
                        }
                    });
                });
            })
        },
        loading: function () {
            //显示进度条
            swal({
                title: "<h3>数据导入中……</h3>",
                text: "<p class='exup-progress-msg m-t-15'>文件上传中……</p>" +
                    "<div class=\"progress rounded-corner m-t-15\">\n" +
                    "  <div class=\"progress-bar\" style=\"width: 0\"></div>\n" +
                    "</div>",
                showConfirmButton: false,
                html: true
            });
            var loading = $(".progress-bar");
            var exupProgressMsg = $('.exup-progress-msg');
            //开始ws链接服务器
            client = function () {
                var socket = new SockJS(contextPath + 'stomp');
                var c = Stomp.over(socket);
                c.connect({}, function () {
                    c.subscribe('/topic/plan/data_import', function (message) {
                        var r = JSON.parse(message.body);
                        if (r.code === 1000) {//反馈信息
                            exupProgressMsg.html(r.data);
                        } else if (r.code === 1001) {//反馈进度
                            var i = r.data.i;
                            var total = r.data.total;
                            var progress = (i / total) * 100;
                            exupProgressMsg.html(progress.toFixed(3) + "%");
                            loading.attr("style", "width: " + progress.toFixed(0) + "%");
                        }
                    });
                });
                return c;
            }();
        },
        exportExcel: function () {
            //开始ws链接服务器
            ready.clientExport = function () {
                var socket = new SockJS(contextPath + 'stomp');
                var c = Stomp.over(socket);
                c.connect({}, function () {
                    c.subscribe('/topic/plan/data_export', function (message) {
                        var r = JSON.parse(message.body);
                        if (r.code === 1000) {//反馈信息
                            ready.exupProgressMsg.html(r.data);
                        } else if (r.code === 1001) {//反馈进度
                            var i = r.data.i;
                            var total = r.data.total;
                            var progress = (i / total) * 100;
                            ready.exupProgressMsg.html(progress.toFixed(3) + "%");
                            ready.loading.attr("style", "width: " + progress.toFixed(0) + "%");
                            if (progress === 100) {
                                swal({
                                    title: '成功',
                                    type: 'success',
                                    confirmButtonText: '确 认'
                                }, function () {
                                });
                            }
                        }
                    });
                });
                return c;
            }();
            $("#exportExcel").bind('click', function () {
                //显示进度条
                swal({
                    title: "<h3>数据导出中……</h3>",
                    text: "<p class='exup-progress-msg m-t-15'>Excel文件导出中……</p>" +
                        "<div class=\"progress rounded-corner m-t-15\">\n" +
                        "  <div class=\"progress-bar\" style=\"width: 0\"></div>\n" +
                        "</div>",
                    showConfirmButton: false,
                    html: true
                });
                ready.loading = $(".progress-bar");
                ready.exupProgressMsg = $('.exup-progress-msg');
                window.location.href = contextPath + "plan/exportPlanExcel/?archives=" + $("#archives").val() + "&name=" + $("#name").val() +
                    "&courtroom=" + $("#courtroom").find("option:selected").val() + "&judge=" + $("#judge").find("option:selected").val() +
                    "&secretary=" + $("#secretary").find("option:selected").val() + "&status=" + $("#status").find("option:selected").val() +
                    "&startDate=" + $("#startDate").val() + "&endDate=" + $("#endDate").val() + "&pageType=" + $("#pageType").val() +
                    "&allPlan=1&model=" + $("#model").val();
            })
        },
        resetInput: function () {
            $("#reset").bind('click', function () {
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
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;

    function handleCheckedBox() {
        var checkedBox = $(".ck");
        var schIds = "";
        for (var i = 0; i < checkedBox.length; i++) {
            if (checkedBox[i].checked) {
                if (schIds === "") {
                    schIds = checkedBox[i].value;
                } else {
                    schIds = schIds + "," + checkedBox[i].value;
                }
            }
        }
        return schIds
    }
}(window);
