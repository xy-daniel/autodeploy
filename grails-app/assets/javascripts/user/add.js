;-function (window) {

    //内部核心属性
    const core = {
        parsley : function(){
            $("#form-useradd").parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        },
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        validateUserName: function () {
            $("#username").keyup(function () {
                window.Parsley.addAsyncValidator('checkusername', function (xhr) {
                    console.log(JSON.parse(xhr.responseText).data);
                    return JSON.parse(xhr.responseText).data == 0;
                }, contextPath +  'user/getUserByUsername');
            })
        },
        ajax_form:function(){
            $("#form-useradd").ajaxForm({
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
                            if (data.data != 0 ){
                                window.location.href= contextPath + "employee/list";
                            }else{
                                window.location.href= contextPath + "user/list";
                            }
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            if (data.data != 0 ){
                                window.location.href= contextPath + "user/add/" + data.data ;
                            }else{
                                window.location.href= contextPath + "user/add";
                            }
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
                        if (data.data != 0 ){
                            window.location.href= contextPath + "user/add/" + data.data ;
                        }else{
                            window.location.href= contextPath + "user/add";
                        }
                    });
                }
            });
        }
    };
    'use strict';
    const ready = {};
    const option = {};
    const init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        //前端校验
        core.parsley();
        //权限选择格式化
        core.selectpicker();
        //validate username
        core.validateUserName();
        //表单提交
        core.ajax_form();
    }
    //对外公开的方法
    const page = {};
    init();
    window.p = page;

}(window);