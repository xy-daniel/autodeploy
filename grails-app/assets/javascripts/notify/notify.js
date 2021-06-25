window.onload = function() {
    $.ajax({
        type: 'get',
        url: contextPath + 'notify/listUnread',
        dataType: 'json',
        success: function (result) {
            $(".label").html(result.data.recordsTotal);
            $(".dropdown-header").html('系统通知：('+ result.data.recordsTotal +')');
            for(var i=0;i<result.data.data.length;i++){ //遍历data数组
                var ls = result.data.data[i];
                $(".media-list").append( '<li class="media"> <a href="'+contextPath+'notify/show/' + ls.id + '"> <div class="media-left">  </div>' +
                    ' <div class="media-body"> <h6 class="media-heading"></h6> <p id="datadetail">'
                    + ls.remark +'</p> <div class="text-muted f-s-11">'+ ls.createTime +'</div> </div> </a> </li>');
            }
            $(".media-list").append('<li class="dropdown-footer text-center"> <a href="'+contextPath+'notify/list">查看更多</a> </li>');
        }
    });
};
