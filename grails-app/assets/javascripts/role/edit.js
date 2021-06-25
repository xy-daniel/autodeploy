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
        //前端校验是否已经被注册
        core.validateAuthority();
        core.validateRemark();
        //表单提交
        core.ajax_form();
        //初始化菜单树
        core.getMenuTree();
    }

    //内部核心属性
    var core = {
        parsley : function(){
            $("#form-roleEdit").parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        },
        ajax_form: function(){
            $("#form-roleEdit").ajaxForm({
                url: contextPath + 'role/editSave',
                type: 'post',
                dataType: 'json',
                beforeSerialize: function(){
                    ready.nodes = ready.menu_ztree.getCheckedNodes(true); // 获取所有选中节点
                    ready.ids = "";
                    for (var i = 0; i < ready.nodes.length; i++) {
                        ready.ids += ready.nodes[i].id;
                        ready.ids += ",";
                    }
                    $("#ids").val(ready.ids);
                },
                success: function (result) {
                    console.log(result)
                    if (result.code === 0) {
                        swal({
                            title: '操作成功!',
                            type: 'success',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "role/list";
                        });
                    }else{
                        swal({
                            title: '操作失败!',
                            type: 'error',
                            confirmButtonText:'确 认'
                        },function () {
                            window.location.href= contextPath + "role/edit/"+$("[name='roleId']").val();
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
                        window.location.href= contextPath + "role/edit/"+$("[name='roleId']").val();
                    });
                }
            });
        },
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        validateAuthority: function () {
            $("#authority").keyup(function () {
                window.Parsley.addAsyncValidator('checkauthority', function (xhr) {
                    console.log(JSON.parse(xhr.responseText).data);
                    var authorityPo = $("#authorityPo").val();
                    var authority = $("#authority").val();
                    if (authority!==authorityPo){
                        return JSON.parse(xhr.responseText).data === 0;
                    }
                    return true;
                }, contextPath + 'role/getRoleByAuthority');
            })
            window.Parsley.addAsyncValidator('checkauthority', function (xhr) {
                console.log(JSON.parse(xhr.responseText).data);
                var authorityPo = $("#authorityPo").val();
                var authority = $("#authority").val();
                if (authority!==authorityPo){
                    return JSON.parse(xhr.responseText).data === 0;
                }
                return true;
            }, contextPath + 'role/getRoleByAuthority');
        },
        validateRemark: function () {
            $("#remark").keyup(function () {
                window.Parsley.addAsyncValidator('checkremark', function (xhr) {
                    console.log(JSON.parse(xhr.responseText).data);
                    var remarkPo = $("#remarkPo").val();
                    var remark = $("#remark").val();
                    if (remark!==remarkPo){
                        return JSON.parse(xhr.responseText).data === 0;
                    }
                    return true;
                }, contextPath + 'role/getRoleByRemark');
            })
            window.Parsley.addAsyncValidator('checkremark', function (xhr) {
                console.log(JSON.parse(xhr.responseText).data);
                var remarkPo = $("#remarkPo").val();
                var remark = $("#remark").val();
                if (remark!==remarkPo){
                    return JSON.parse(xhr.responseText).data === 0;
                }
                return true;
            }, contextPath + 'role/getRoleByRemark');
        },
        getMenuTree: function() {
            var menu_setting = {
                data: {
                    simpleData: {
                        enable: true,
                        idKey: "id",
                        pIdKey: "parentId",
                        rootPId: -1
                    },
                    key: {
                        url:"nourl"
                    }
                },
                check:{
                    enable:true,
                    nocheckInherit:true
                }
                /*,
                callback:{    //第一步
                    onClick: core.getDepthTreeNode()
                }*/
            };
            //加载菜单树
            $.ajax({
                url: contextPath + 'menu/list',
                type: 'POST',
                dataType: "json",
                success: (data) => {
                    console.log(data);
                    ready.menu_ztree = $.fn.zTree.init($("#menuTree"), menu_setting, data);//初始化树节点时，添加同步获取的数据
                    core.checkNodes();
                },
                error: (data) => {
                    console.log(data.message);
                }
            });
            /*$.get(contextPath + "menu/list", function(r){
                menu_ztree = $.fn.zTree.init($("#menuTree"), menu_setting, r);
                //展开所有节点
                menu_ztree.expandAll(true);

                if(roleId != null){
                    vm.getRole(roleId);
                }
            });*/
        },
        //处理默认选中的方法
        checkNodes: function (){
            $.ajax({
                url: contextPath + 'menu/info/' + $("[name='roleId']").val(),
                type: 'GET',
                dataType: "json",
                success: (data) => {
                    var zTree = $.fn.zTree.getZTreeObj("menuTree"); //获取zTree对象
                    data.forEach(row => {
                        zTree.selectNode(zTree.getNodeByParam("id", row.id), true, false);
                        zTree.checkNode(zTree.getNodeByParam("id", row.id), true, false);
                    });
                },
                error: (data) => {
                    console.log(data.message);
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
