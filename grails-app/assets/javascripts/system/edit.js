/**
 * 系统设置脚本 created by daniel in 2021.04.26
 * 2021.06.16 >>> 互联网开庭统计 daniel
 */
;-function (window) {
    'use strict';
    var ready = {};
    var option = {};
    var init = function () {
        init_ready();
        init_event();
    };
    function init_ready() {}
    function init_event() {
        core.selectpicker();
        core.ajax_form();
    }
    var core = {
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        ajax_form:function(){
            $("form").ajaxForm({
                url: contextPath + "system/editSave",
                type: 'post',
                dataType: 'json',
                success: function (data) {
                    if (data.code === 0) {
                        swal({
                            title: '操作成功!',
                            type: 'success',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "index";
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "system/edit";
                        });
                    }
                },
                error: function(){
                    swal({
                        title: '数据出错!',
                        type: 'error',
                        confirmButtonText:'确 认'
                    },function () {
                        window.location.href= contextPath + "system/edit";
                    });
                }
            });
        }
    };
    var page = {};
    init();
    window.p = page;
}(window);
