;-function (window) {
    //内部核心属性
    const core = {
        parsley : function(){
            $("form").parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        },
        ajax_form:function(){
            $('form').ajaxForm({
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
                            window.location.href= contextPath + "task/list";
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.reload()
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
                        window.location.reload()
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
        core.parsley();
        core.ajax_form();
        $(".version").bind('click', function () {
            $(".version").css("backgroundColor", "#348FE2")
            const val = $(this).next().val();
            const input = document.createElement('input');
            input.value = val;
            document.body.appendChild(input);
            input.select();
            document.execCommand("Copy");
            input.className = 'oInput';
            input.style.display = 'none';
            // $(this).style.backgroundColor = "red";
            $(this).css("backgroundColor", "#FF5555")
        })
        $("#commandAdd").bind('click', function () {
            $("#commit").before("<div class='form-group row m-b-10'>" +
                "                                <label class='col-md-2 text-md-right col-form-label'></label>" +
                "                                <div class='col-md-10'>" +
                "                                    <input type='text' class='form-control m-b-5' name='content'/>" +
                "                                </div>" +
                "                            </div>")
        })
    }

    function init_event() {
    }
    //对外公开的方法
    const page = {};
    init();
    window.p = page;
}(window);
