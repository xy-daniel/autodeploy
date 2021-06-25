/**
 * Created by sakuri on 2019/8/13.
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
        core.import_form();
        core.import_submit();

    }

    //内部核心属性
    var core = {
        connect: function () {
            var socket = new SockJS(contextPath + 'stomp');
            var c = Stomp.over(socket);
            c.connect({}, function () {
                c.subscribe('/topic/data_video_duration', function (message) {
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
                client.send('/app/data_video_duration', {}, JSON.stringify($('#import-form').serializeJson()));
                $('#import-beforeSubmit').modal('hide');
            });
        },
        import_form: function(){
            $('#import-form').ajaxForm({
                type: 'post',
                dataType: 'json',
                clearForm: true,
                beforeSubmit: function(){
                    $('#import-beforeSubmit').modal('show');
                    return false;
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
