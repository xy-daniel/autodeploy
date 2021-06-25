var courtroomId = null
var name = null
var uid = null
var detentionName = null
/**
 * 获取法庭列表
 */
$("#set").bind('click', function() {
    $.get(
        contextPath + 'api/device/allCourtroom',
        function(result) {
            if (result.code == 0) {
                // $(".select").empty();    //清空
                console.log(result)
                for(var i=0;i<result.data.length;i++) {
                    $('select').append(
                        '<option class="select-option" '+'value="'+result.data[i].id+'">'+result.data[i].name+'</option>'
                    )
                }
            } else {
                console.log(result)
            }
        },'json')
})

//获取用户选择法庭传到前端页面
function transmit(){
    $('#set-sure-btn').bind('click', function () {
        var data = $(".select").find("option:selected").text();
        courtroomId = $(".select").find("option:selected").val();
        if (data !== '请选择'){
            $('.title').html(data);
            $('.title').attr('id',courtroomId);
            $('#set-window').hide();
            obtain_device();
        }else {
            alert("请选择法庭！")
        }
    })
}

//获取羁押室列表
function obtain_device(){
    $.get(
        contextPath + 'api/device/allDevice',
        function(result) {
            if (result.code == 0) {
                $('.ul-message').empty();
                var data_list = result.data
                console.log(data_list)
                for (var i = 0; i < data_list.length; i++) {
                    $('ul').append('<li class="li-text"><button class="li-btn" value="'+data_list[i].deviceUid+'">' + data_list[i].deviceName + '</button></li>');
                    if(data_list[i].courtroomId === courtroomId){
                        console.log("对比成功")
                        //默认选择按钮为与法庭id相同的羁押室
                        $('.li-btn').eq(i).addClass('active')
                        //uid赋值
                        uid = data_list[i].deviceUid
                        detentionName = data_list[i].deviceName
                        console.log(uid)
                    }else {
                        console.log("对比失败")
                        //默认选择按钮为第一个羁押室
                        uid = data_list[0].deviceUid
                        detentionName = data_list[0].deviceName
                        console.log(detentionName)
                        $('.li-btn').eq(0).addClass('active')

                    }
                }

            } else {
                alert("访问错误，请重新访问")
            }
        },'json');
}

//获取当事人列表
function obtain_litigant(courtroomId){
    if (courtroomId == null) {
        console.log("尚未选择法庭，请选择法庭.");
        return false;
    }
    var plan_id = null
    $.get(
        contextPath + 'api/device/getLitigant/' + courtroomId ,
        function(result) {
            if (result.code === 0) {
                var div = document.getElementById('popup-window');
                var people_list = result.data.empData;
                var new_plan_id = result.data.id;
                if (new_plan_id){
                    plan_id = new_plan_id;
                    $('.court-people').empty();
                    for (var j = 0; j < people_list.length; j++){
                        if(people_list[j]){
                            var btn = $('<button type="button" name="btn" class="btn" value="">' + people_list[j] + '</button>');
                            btn.bind('click', function() {
                                div.style.display = "block";
                                name = $(this).html();
                                var popupContent = document.getElementById('popup-content')
                                popupContent.innerHTML = '是否传唤' + detentionName +'中待传唤人' + name
                                $(this).css("background", "#0099FF");
                                $(this).siblings("button").css("background", "");
                            })
                            $('.court-people').append(btn);
                        }
                    }
                }else{
                    $('.court-people').empty();
                    plan_id = new_plan_id;
                }
            } else if (result.code === 410) {
                console.log("尚未开庭，当事人列表不存在.")
            } else {
                console.log("获取当事人列表失败，请重新尝试.")
            }
        },'json');
}

//轮询
function myFunction() {
    setInterval('obtain_litigant(courtroomId)',5000);
}

//点击传唤弹窗
function subpoena(){

    // var div = document.getElementById('popup-window');
    $('#popup-window').bind('click', function() {
        // div.style.display = "none";
        $('#popup-window').hide();
    })
    $('#close-btn').bind('click', function() {
        // div.style.display = "none";
        $('#popup-window').hide();
    })
}

//点击法庭列表弹窗
function set_window(){
    $('.set').bind('click', function() {
        $('#set-window').show();
    })
    $('.close-button').bind('click', function() {
        // div.style.display = "none";
        $('#set-window').hide();
    })
    $('#set-close-btn').bind('click', function() {
        // div.style.display = "none";
        $('#set-window').hide();
    })
}

//获取羁押室Uid
$('.ul-message').on('click','.li-btn',function(){

    uid = $(this).val();
    detentionName = $(this).text();
    $('.li-btn').removeClass('active');
    $(this).addClass('active');
    console.log(detentionName);
})

//传唤当事人
$("#sure-btn").bind('click', function() {
    // var div = document.getElementById('popup-window');
    $.get(
        contextPath + 'api/device/call/' + uid + '?name=' + name + "&id=" + courtroomId ,
        function(result) {
            if (result.code == 0) {
                console.log('传唤成功！')
                $('#popup-window').hide()
                // div.style.display = "none";
            } else if(result.code == 414){
                $('#popup-window').hide()
                // div.style.display = "none";
            }
            else if(result.code == 121){
                console.log('请选择正确的羁押室与传唤人姓名!')
                $('#popup-window').hide()
                // div.style.display = "none";
            }
        },'json');
})
