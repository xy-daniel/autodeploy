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
        core.parsley();
        core.selectpicker();
        //前端校验账号是否已经被注册
        core.validateUserName();
        //表单提交
        core.ajax_form();
    }

    //内部核心属性
    var core = {
        parsley : function(){
            $('form').parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        },
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        validateUserName: function(){
            $("#username").keyup(function () {
                window.Parsley.addAsyncValidator('checkname', function (xhr) {
                    console.log(JSON.parse(xhr.responseText).data);
                    var usernamePo = $("#usernamePo").val();
                    var username = $("#username").val();
                    if (username!==usernamePo){
                        return JSON.parse(xhr.responseText).data === 0;
                    }
                    return true;
                }, contextPath + 'user/getUserByUsername');
            });
            window.Parsley.addAsyncValidator('checkname', function (xhr) {
                console.log(JSON.parse(xhr.responseText).data);
                var usernamePo = $("#usernamePo").val();
                var username = $("#username").val();
                if (username!==usernamePo){
                    return JSON.parse(xhr.responseText).data === 0;
                }
                return true;
            }, contextPath + 'user/getUserByUsername');
        },
        ajax_form:function(){
            $("form").ajaxForm({
                url: contextPath + "user/editSave",
                type: 'post',
                dataType: 'json',
                beforeSubmit: function(){
                },
                success: function (data) {
                    if (data.code === 0) {
                        swal({
                            title: '操作成功!',
                            type: 'success',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "user/list";
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "user/edit/"+$("[name='userId']").val();
                        });
                    }
                },
                //回调失败
                error: function(){
                    swal({
                        title: '数据出错!',
                        type: 'error',
                        confirmButtonText:'确 认'
                    },function () {
                        window.location.href= contextPath + "user/edit/"+$("[name='userId']").val();
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
