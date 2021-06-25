/**
 * Created by sakuri on 2019/8/13.
 */
;-function (window) {
    'use strict';
    var ready = {
        id: "menuTreeTable",
        table: null
    };
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件

    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        core.initMenuTreeTable();
        // core.initTable();
    }

    //内部核心属性
    var core = {
        initMenuTreeTable: function () {
            $('#menuTreeTable').bootstrapTreeTable({
                id: 'id', // 选取记录返回的值
                code: 'id', // 用于设置父子关系
                parentCode: 'parentId', // 用于设置父子关系
                rootCodeValue: null, //设置根节点code值----可指定根节点，默认为null,"",0,"0"
                data: [], // 构造table的数据集合，如果是ajax请求则不必填写
                type: "POST", // 请求数据的ajax类型
                url: contextPath + 'menu/list', // 请求数据的ajax的url
                ajaxParams: {
                    // deptName: $(".form").find("input[name='Name']").val().trim()
                }, // 请求数据的ajax的data属性
                expandColumn: 1, // 在哪一列上面显示展开按钮
                expandAll: true, // 是否全部展开
                striped: true, // 是否各行渐变色
                columns: [
                    /*{
                        field: 'selectItem',
                        // field: 'id',
                        checkbox: true,
                        width: '50px',
                        formatter: function(item, index){
                            if (row.check == true) {
                                //设置选中
                                return {  checked: true };
                            }
                            /!*return '<td class="with-checkbox">' +
                                '<div class="checkbox checkbox-css">' +
                                '<input class="ck" type="checkbox" name="checkbox-select" value="' + item + '" id="table_checkbox_' + item + '" data-user="' + item + '" />' +
                                '<label for="table_checkbox_' + item + '">&nbsp;</label>' +
                                '</div>'*!/
                        }
                    },*/
                    {
                        title: '编号',
                        field: 'id',
                        width: '50px'
                    },
                    {
                        title: '名称',
                        field: 'name'
                    },
                    {
                        title: '路径',
                        field: 'url'
                    },
                    {
                        title: '类型',
                        field: 'type'
                    },
                    {
                        title: '操作',
                        field: 'id',
                        formatter: function(item, index){
                            var html =  '<div class="table-text">';
                            html +=  '<a href="'+contextPath+'menu/edit/' + item.id + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>';
                            html +=  '<a href="'+contextPath+'menu/del/' + item.id + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">删除</a>';
                            html +=  '</div>';
                            return html
                        }
                    }
                ], // 设置列
                toolbar: null, //顶部工具条
                height: 560,
                expanderExpandedClass: 'fa fa-chevron-down', // 展开的按钮的图标
                expanderCollapsedClass: 'fa fa-chevron-up' // 缩起的按钮的图标
            });
        }


    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
