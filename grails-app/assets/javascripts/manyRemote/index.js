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
        core.higherCourt();
        core.seniorCourt();
    }

    //内部核心属性
    var core = {
        //改变初级人民法院
        seniorCourt: function(){
            $("#seniorCourt").change(function () {
                //获取改变后的值
                var getCourt = $(this).children('option:selected').val();
                $.get(
                    contextPath + "manyRemote/showCourt",
                    {
                        getCourt:getCourt
                    },
                    function (result) {
                        var selectElement = $("#court");
                        selectElement.empty();
                        //添加select的第一个option
                        selectElement.append("<option value=\"\">----初级人民法院----</option>");
                        $.each(result.data,function (i) {
                            selectElement.append("<option value=\""+ result.data[i] +"\">"+ result.data[i] +"</option>")
                        })
                    },'json'
                )
            })
        },
        //改变高级人民法院
        higherCourt: function(){
            $("#higherCourt").change(function(){
                //获取改变后的值
                var higherCourt = $(this).children('option:selected').val();
                $.get(
                    contextPath + "manyRemote/showSeniorCourt",
                    {
                        higherCourt:higherCourt
                    },
                    function (result) {
                        var selectElement = $("#seniorCourt");
                        selectElement.empty();
                        //添加select的第一个option
                        selectElement.append("<option value=\"\">----中级人民法院----</option>");
                        $.each(result.data,function (i) {
                            selectElement.append("<option value=\""+ result.data[i] +"\">"+ result.data[i] +"</option>")
                        })
                    },'json'
                )
            });
        },
        parsley : function(){
            $('form').parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;

}(window);