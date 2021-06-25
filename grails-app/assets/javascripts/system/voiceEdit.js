/**
 * 语音传唤脚本 created by daniel in 2021.04.26
 */
;-function (window) {
    'use strict';
    var ready = {};
    var option = {};
    var init = function () {
        init_ready();
        init_event();
    };

    function init_ready() {
    }

    function init_event() {
        core.ajax_form();
    }

    var core = {
        ajax_form: function () {
            $("form").ajaxForm({
                url: contextPath + "system/voiceEditSave",
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
                            window.location.href = contextPath + "detentionDevice/list";
                        });
                    } else {
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText: '确 认'
                        }, function () {
                            window.location.href = contextPath + "dent/voiceEdit";
                        });
                    }
                },
                error: function () {
                    swal({
                        title: '数据出错!',
                        type: 'error',
                        confirmButtonText: '确 认'
                    }, function () {
                        window.location.href = contextPath + "system/voiceEdit";
                    });
                }
            });
        }
    };
    var page = {};
    init();
    window.p = page;
}(window);
