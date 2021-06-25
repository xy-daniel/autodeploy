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
        core.ajax_form();
    }

    //内部核心属性
    var core = {
        parsley: function () {
            $('form').parsley({
                errorsContainer: function (pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        },
        ajax_form: function () {
            $("form").ajaxForm({
                url: contextPath + "osInfo/editSave",
                type: 'post',
                dataType: 'json',
                beforeSubmit: function () {
                },
                success: function (data) {
                    if (data.code === 0) {
                        swal({
                            title: '操作成功!',
                            type: 'success',
                            confirmButtonText: '确 认'
                        }, function () {
                            window.location.href = contextPath + "osInfo/list";
                        });
                    } else {
                        swal({
                            title: data.msg,
                            type: 'error',
                            confirmButtonText: '确 认'
                        }, function () {
                            window.location.href = contextPath + "osInfo/edit";
                        });
                    }
                },
                //回调失败
                error: function () {
                    swal({
                        title: '数据出错!',
                        type: 'error',
                        confirmButtonText: '确 认'
                    }, function () {
                        window.location.href = contextPath + "osInfo/edit";
                    });
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
