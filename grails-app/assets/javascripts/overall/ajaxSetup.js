/**
 * 2021.06.22 >>> ajax全局拦截器 daniel
 */
;-function (window) {
    'use strict';
    var init = function () {
        $.ajaxSetup({
            contentType: "application/x-www-form-urlencoded;charset=utf-8",
            complete: function (XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    var result = JSON.parse(XMLHttpRequest.responseText)
                    console.log("服务器返回值:" + JSON.stringify(result))
                    if (result.code !== 0) {
                        var data = result.data.toString()
                        if (data.startsWith("您缺少")) {
                            alert(data)
                        }
                    }
                }
            }
        });
    };
    init();
}(window);
