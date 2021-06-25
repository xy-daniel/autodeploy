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
        core.uploadUpdatePackage()
    }

    //内部核心属性
    var core = {
        uploadUpdatePackage: function () {
            $("#uploadUpdatePackage").bind('click', function () {
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
                    $.ajax({
                        url: contextPath + "clerkClient/uploadUpdatePackage",
                        type: 'POST',
                        async: true,
                        data: formData,
                        processData: false,
                        contentType: false,
                        success: function (result) {
                            if (JSON.parse(result).code.toString() === '0') {
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
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
