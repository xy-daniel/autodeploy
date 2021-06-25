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

    function init_event() {//初始化页面事件
        //前端校验
        core.parsley();
        //检查路径是否已经配置
        core.validateurlMount();
        //表单提交
        core.ajax_form();
    }

    //内部核心属性
    var core = {
        parsley : function(){
            $("#form-mountDiskadd").parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        },
        validateurlMount: function () {
            $("#urlMount").keyup(function () {
                window.Parsley.addAsyncValidator('checkurlMount', function (xhr) {
                    console.log(JSON.parse(xhr.responseText).data);
                    return JSON.parse(xhr.responseText).data == 0;
                }, contextPath +  'mountDisk/getMountByUrlMountname');
            })
        },
        ajax_form:function(){
            $("#form-mountDiskadd").ajaxForm({
                type: 'post',
                dataType: 'json',
                // 提交之前的验证---->通过parsely进行验证
                beforeSubmit: function(){
                },
                //回调成功
                success: function (data) {
                    if (data.code === 0) {
                        swal({
                            title: '操作成功!',
                            type: 'success',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "mountDisk/list";
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "mountDisk/add";
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
                        window.location.href= contextPath + "mountDisk/add";
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