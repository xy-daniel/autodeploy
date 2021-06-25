/**
 * Created by arctic on 2020/12/09.
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
        client = core.connect2();
    }

    function init_event() {//初始化页面事件
        core.import_form();
        core.import_submit();
        core.import_form2();
        core.import_submit2();

    }

    //内部核心属性
    var core = {
        connect: function () {
            var socket = new SockJS(contextPath + 'stomp');
            var c = Stomp.over(socket);
            c.connect({}, function () {
                c.subscribe('/topic/revideo', function (message) {
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
                client.send('/app/revideo', {}, JSON.stringify($('#import-form').serializeJson()));
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
        },

        connect2: function () {
            var socket = new SockJS(contextPath + 'stomp');
            var c = Stomp.over(socket);
            c.connect({}, function () {
                c.subscribe('/topic/indexVideo', function (message) {
                    var console_div = $('.court_content');
                    if (console_div_num > 200) {
                        console_div.find('p:eq(0)').remove();
                    }
                    var msg_body = message.body;
                    if (msg_body === 'Finished.') {
                        $('#import-form2 button').show();
                    }
                    console_div.append('<p class="text-success">_> ' + msg_body + '</p>');
                    console_div[0].scrollTop = console_div[0].scrollHeight;
                    console_div_num++;
                });
            });
            return c;
        },
        import_submit2: function(){
            $('.import-submit-btn2').on("click",function () {
                client.send('/app/indexVideo', {}, JSON.stringify($('#import-form2').serializeJson()));
                $('#import-beforeSubmit2').modal('hide');
            });
        },
        import_form2: function(){
            $('#import-form2').ajaxForm({
                type: 'post',
                dataType: 'json',
                clearForm: true,
                beforeSubmit: function(){
                    $('#import-beforeSubmit2').modal('show');
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
