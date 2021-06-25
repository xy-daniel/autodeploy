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
        //选择需要上报的法庭
        core.choice();
        //上报
        core.reporeRemote();
    }

    function init_event() {
    }
    //内部核心属性
    var core = {
        reporeRemote: function(){
          $("#report-remote-court").click(function () {
              var checker = $(".fa-check").prev();
              swal({
                  title:"已选择"+ checker.length +"个法庭，确认上传?",
                  type:"warning",
                  showCancelButton:true,
                  cancelButtonText:'取 消',
                  cancelButtonColor:'#b9b9b9',
                  showConfirmButton:true,
                  confirmButtonText:'确 认',
                  confirmButtonColor:"#dd6b55",
                  closeOnConfirm:false,
                  closeOnCancel:true
              },function(){
                  swal({
                      title: '上传中请等待!',
                      type: 'success',
                      confirmButtonText:'确 认'
                  });
                  var data = "";
                  $.each(checker, function (i) {
                      if (data!==""){
                          data+=","
                      }
                      data+=checker[i].value
                  });
                  $.get(
                      contextPath + 'remote/synrooms',
                      {
                          data:data
                      },
                      function (result) {
                          if (result.code===0){
                              swal({
                                  title: '上报成功!',
                                  type: 'success',
                                  confirmButtonText:'确 认'
                              },function () {
                                  alert("上报成功");
                              });
                          }else{
                              var msg = result.data;
                              if (msg === ""){
                                  msg = "异常"
                              }
                              swal({
                                  title: msg,
                                  type: 'error',
                                  confirmButtonText:'确 认'
                              });
                          }
                      },'json'
                  )
              });
          })
        },
        choice: function(){
            $(".ch").click(function () {
                var th = $(this);
                var statusBtn = $(this).children("div.stats-link").children("a").children("span");
                var status = statusBtn.text();
                var flag = $(this).children("div.stats-icon").children("i");
                //未上报的切换为待上报状态
                if (status==="未上报"){
                    th.attr("class", "ch widget widget-stats bg-orange-lighter");
                    flag.attr("class", "fas fa-check");
                    statusBtn.text("待上报")
                }
                //待上报的切换为未上报状态
                if (status==="待上报"){
                    th.attr("class", "ch widget widget-stats bg-grey-darker");
                    flag.attr("class", "fas fa-plus");
                    statusBtn.text("未上报")
                }
                //已上报的切换为未上报状态
                if (status==="已上报"){
                    th.attr("class", "ch widget widget-stats bg-grey-darker");
                    flag.attr("class", "fas fa-plus");
                    statusBtn.text("取消上报")
                }
                //取消上报的切换为已上报状态
                if (status==="取消上报"){
                    th.attr("class", "ch widget widget-stats bg-green-lighter");
                    flag.attr("class", "fas fa-check");
                    statusBtn.text("已上报")
                }
            })
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
