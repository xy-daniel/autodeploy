/**
 * Created by sakuri on 2019/8/13.
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

    function init_event() {
        //初始化页面事件
        core.parsley();
        core.ajax_form();
    }
    //内部核心属性
    var core = {
        ajax_form:function(){
            $('form').ajaxForm({
                type: 'post',
                dataType: 'json',
                beforeSubmit: function(){
                },
                success: function (data) {
                    if (data.code === 0) {
                        swal({
                            title: '操作成功,请手动重启服务!',
                            type: 'success',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath;
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "court/index/";
                        });
                    }
                },
                error: function () {
                }
            });
        },
        parsley : function(){
            $('form').parsley({
                errorsContainer: function(pEle) {
                    var $err = pEle.$element.siblings('.errorBlock');
                    return $err;
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
