/**
 * 表单验证器 created by daniel in 2021.04.26
 */
;-function (window) {
    'use strict';
    var ready = {};
    var option = {};
    var init = function () {
        init_ready();
        init_event();
    };

    function init_ready() {
    }

    function init_event() {
        core.parsley();
    }
    var core = {
        parsley : function(){
            $("form").parsley({
                errorsContainer: function(pEle) {
                    return pEle.$element.siblings('.errorBlock');
                }
            });
        }
    };
    var page = {};
    init();
    window.p = page;
}(window);
