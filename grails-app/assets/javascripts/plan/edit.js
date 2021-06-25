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
        core.wizard();
        core.planWizardBtnAdd();
        core.switcherCheckbox();
        core.changeCheckbox();
        core.selectpicker();
        core.primary();
        core.datetime();
        core.collegial();
        core.validatePlanArchives()
    }

    //内部核心属性
    var core = {
        validatePlanArchives: function () {
            $("#archives").keyup(function () {
                window.Parsley.addAsyncValidator('checkarchives', function (xhr) {
                    var namePo = $("#archivesPo").val();
                    var name = $("#archives").val();
                    if (name !== namePo){
                        return JSON.parse(xhr.responseText).data === 0;
                    }
                    return true;
                }, contextPath + 'plan/getPlanByArchives');
            })
            window.Parsley.addAsyncValidator('checkarchives', function (xhr) {
                var namePo = $("#archivesPo").val();
                var name = $("#archives").val();
                if (name !== namePo){
                    return JSON.parse(xhr.responseText).data === 0;
                }
                return true;
            }, contextPath + 'plan/getPlanByArchives');
        },
        wizard: function(){
            var wizard = $('#wizard');
            // Toolbar extra buttons
            var btnFinish = $('<button type="submit"></button>').text('完成')
                .addClass('btn btn-primary plan-wizard-btn-finish')
                .on('click', function(){
                    // alert('Finish Clicked');
                });
            wizard.smartWizard({
                lang:{
                    next: '下一步',
                    previous: '上一步'
                },
                selected: 0,
                theme: 'default',
                transitionEffect:'',
                transitionSpeed: 0,
                useURLhash: false,
                showStepURLhash: false,
                toolbarSettings: {
                    toolbarPosition: 'bottom',
                    toolbarExtraButtons: [btnFinish]
                }
            });
            wizard.on('leaveStep', function(e, anchorObject, stepNumber, stepDirection) {
                var res = $('form[name="form-wizard"]').parsley({
                    errorsContainer: function(pEle) {
                        return pEle.$element.siblings('.errorBlock');
                    }
                }).validate('step-' + (stepNumber + 1));
                if (stepNumber == 0) {
                    var tagIt_accuser = document.getElementById("tagIt_accuser");
                    var tagIt_accused = document.getElementById("tagIt_accused");
                    if ($("[name=accuser]").length == 0 && $("[name=accused]").length == 0) {
                        tagIt_accuser.style.borderColor = "#ff5b57";
                        tagIt_accused.style.borderColor = "#ff5b57";
                    }
                    //校验原告
                    if ($("[name=accuser]").length == 0) {
                        tagIt_accuser.style.borderColor = "#ff5b57";
                        return false;
                    }
                    //校验被告
                    if ($("[name=accused]").length == 0) {
                        tagIt_accused.style.borderColor = "#ff5b57";
                        return false;
                    }
                    var archives = $("#archives").val();
                    if (archives != "") {
                        var archivesPo = $("#archivesPo").val();
                        if (archives != archivesPo) {
                            var responseText = $.ajax({
                                url: contextPath + 'plan/getPlanByArchives?archives='+archives,
                                async: false
                            }).responseText;
                            if (JSON.parse(responseText).data != '0') {
                                return false
                            }
                        }
                    }
                }
                return res;
            });
            // Step show event
            wizard.on('showStep', function(e, anchorObject, stepNumber, stepDirection, stepPosition) {
                //alert("You are on step "+stepNumber+" now");
                if(stepPosition === 'final'){
                    $('.sw-btn-prev').hide();
                    $('.sw-btn-next').hide();
                    $('.plan-wizard-btn-finish').show();
                }else{
                    $('.sw-btn-prev').show();
                    $('.sw-btn-next').show();
                }
            });
            $('.plan-wizard-btn-finish').hide();
        },
        planWizardBtnAdd:function(){
            $('.plan-wizard-btn-add').click(function(){
                $('#wizard').smartWizard('next');
            });
        },
        switcherCheckbox: function(){
            if ( $('#switcher_checkbox').val() == 1){
                $('.distance-signature').show();
                $('#switcher_checkbox').attr("checked",true);
            }else{
                $('.distance-signature').hide();
            }
        },
        changeCheckbox: function(){
            var changeCheckbox = document.querySelector('.js-check-change')
            changeCheckbox.onchange = function() {
                if (changeCheckbox.checked){
                    $("input[name='distanceArraigned']").attr("value",1);
                    $('.distance-signature').show();
                }else{
                    $("input[name='distanceArraigned']").attr("value",0);
                    $('.distance-signature').hide();
                }
            };
        },
        selectpicker: function () {
            $('.selectpicker').selectpicker('render');
        },
        primary: function () {
            $('#tagIt_collegial').tagit({
                fieldName: "collegialName"
            });
            $('#tagIt-distance_signature').tagit({
                fieldName: "distanceSignature"
            });
            $('#tagIt_accuser').tagit({
                fieldName: 'accuser'
            });
            $('#tagIt_accused').tagit({
                fieldName: 'accused'
            });
        },
        datetime: function () {
            $('#datetimepicker1').datetimepicker({
                format: 'YYYY/MM/DD HH:mm',
                collapse: false,
                widgetPositioning: {
                    horizontal: 'auto',
                    vertical: 'bottom'
                }
            });
            $('#datetimepicker2').datetimepicker({
                format: 'YYYY/MM/DD HH:mm',
                collapse: false,
                widgetPositioning: {
                    horizontal: 'auto',
                    vertical: 'bottom'
                }
            });
            $('#datetimepicker3').datetimepicker({
                format: 'YYYY/MM/DD HH:mm',
                collapse: false,
                widgetPositioning: {
                    horizontal: 'auto',
                    vertical: 'bottom'
                }
            });
        },

        collegial: function () {
            $(".modal_collegial-submit").click(function () {
                var select_collegial = $('#select_collegial');//成员select
                var select_collegial_type = $('#select_collegial_type');//成员类型select
                var select_collegial_selected = select_collegial.find("option:selected").text();//成员select text
                var select_collegial_selected_idx = select_collegial_selected.indexOf('(');//成员text （ 位置
                var collegial_name = select_collegial_selected.substring(0, select_collegial_selected_idx > 0 ? select_collegial_selected_idx : select_collegial_selected.length);//只取成员名字
                var select_collegial_type_text = select_collegial_type.find("option:selected").text();
                $('#tagIt_collegial').tagit('createTag', collegial_name + '(' + select_collegial_type_text + ')');
                $(select_collegial).selectpicker('val', '');
                $(select_collegial_type).selectpicker('val', '');
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
