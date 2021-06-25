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
        //记住密码功能
        core.remember();

    }
    //内部核心属性
    var core = {
        /**
         * 记住密码
         */
        remember : function(){
            $("#remember_checkbox").click(function () {
                //记住密码复选框的点击事件
                var remFlag = $("input[type='checkbox']").is(':checked');
                if(remFlag==true){ //如果选中设置remFlag为true
                    var username = $('#username').val();
                    Cookies.set('username', btoa(username), { expires: 365 })
                }else{ //如果没选中设置remFlag为false并将存储网站的cookie清除
                    $("#remFlag").val(false);
                    Cookies.remove('username')
                }
            })
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);