/**
 * Created by liyechao on 2021/03/22.
 */
;-function (window) {
    'use strict';
    var client, console_div_num = 0;
    var ready = {};
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
        client = core.connect();
    }

    function init_event() {//初始化页面事件
        core.import_submit();

    }

    //内部核心属性
    var core = {
        connect: function () {
            var socket = new SockJS(contextPath + 'stomp');
            var c = Stomp.over(socket);
            c.connect({}, function () {
                c.subscribe('/topic/showVideoPlatform', function (message) {
                    var console_div = $('.court_content');
                    if (console_div_num > 200) {
                        console_div.find('p:eq(0)').remove();
                    }
                    var msg_body = message.body;
                    if (msg_body === 'Finished.') {
                        $('#import-form button').show();
                    }
                    console_div.append('<p class="text-success">_> ' + msg_body + '</p>');
                    console_div[0].scrollTop = console_div[0].scrollHeight;
                    console_div_num++;
                });
            });
            return c;
        },
        import_submit: function(){
            $('.import-submit-btn').on("click",function () {
                $.ajax({
                    type: 'post',
                    url: contextPath + 'toolBox/showVideoPlatform/',
                    dataType: 'json',
                    success: function (result) {
                        console.log("推送数据完成！");
                    },
                    error: function () {
                        console.log("推送数据出错！");
                    }
                });
            });
        },
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
