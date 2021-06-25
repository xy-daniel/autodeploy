"use strict";
var initSample = function () {
    console.log(plan)
    var panel = $('.panel-body')
    panel.append('<div class="btn-group">');
    $(plan).each(function (i, n) {
        panel.append(' <button class="btn btn-primary active" onclick="letGo(\'' + n.uid + '\')">' + n.startTime + '</button> ')
    });
    panel.append('</div>');
};

var letGo = function (uid) {
    $('#letGo').attr('action', key);
    $('#planUid').val(uid);
    $('#letGo').submit();
};

var Plan_index = function () {
    "use strict";
    return {
        //main function
        init: function () {
            initSample();
        }
    };
}();