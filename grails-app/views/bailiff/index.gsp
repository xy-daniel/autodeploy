%{--
    2021.06.03 >>> 点击回车键确认收到法庭报警 daniel
--}%
<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8" />
    <title>警务室</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />
    <asset:stylesheet href="bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<style>
    /*全局控制*/
    * {
        margin: 0;
        padding: 0;
        border: 0;
    }

    body {
        margin: 0 4px;
        background-color: #CCCCCC;
    }

    .text-center {
        text-align: center;
    }

    .white {
        color: white;
    }

    .c33 {
        color: #CC3333 !important;
    }

    .space-between {
        display: flex;
        justify-content: space-between;
    }

    .space-left {
        display: flex;
        justify-content: left;
    }

    .p-t-p-10{
        padding-top: 10%
    }

    .b-r-8 {
        border-radius: 8px;
    }

    .bg-393 {
        background-color: #339933;
    }

    .bg-333 {
        background-color: #333333;
    }

    .bg-transparent {
        background-color: transparent;
    }

    .level-2 {
        z-index: 2;
    }

    .level-3 {
        z-index: 3;
    }

    .courtroom {
        margin: 0 4px 4px;
        padding: 1px 0 0 10px;
    }

    .modal {
        position: fixed;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        display: none;
        overflow: hidden;
        outline: 0;
    }
</style>
<body>
    <audio id="audio" src="${audio}" hidden loop="loop" preload="auto"></audio>
    <div id="container">
        <g:each in="${courtroomList}" var="courtroom" status="i">
            <g:if test="${courtroomList.size()-i >= 5 && (i+1) % 5 == 1}">
                <div class="space-between">
            </g:if>
            <g:if test="${courtroomList.size()-i < 5 && (i+1) % 5 == 1}">
                <div class="space-left">
            </g:if>
            <div id="courtroom${courtroom.id}" class="courtroom bg-393 white b-r-8">
                <div>
                    <p class="name">${courtroom.name}</p>
                </div>
                <div>
                    <p class="con">案号：${courtroom.archives}</p>
                    <p class="con">法官：${courtroom.judge}</p>
                    <p class="con">书记员：${courtroom.clerk}</p>
                    <p class="con">当事人：${courtroom.litigant}</p>
                </div>
            </div>
            <g:if test="${(i+1) % 5 == 0}">
                </div>
            </g:if>
        </g:each>
    </div>
    <div id="modal" class="modal white bg-333 level-2">
        <div id="video"></div>
    </div>
    <div id="textModal" class="modal bg-transparent text-center level-3 p-t-p-10">
        <div class="c33">
            <div id="courtName"></div>出现警情,请即刻到庭处理！
        </div>
        <div id="notice" class="white">注：请点击`Enter(回车键)`关闭此次报警.</div>
    </div>
</body>
<asset:javascript src="jquery/jquery-3.3.1.min.js"/>
<asset:javascript src="sockjs/sockjs.min.js"/>
<asset:javascript src="stomp/stomp.min.js"/>
<asset:javascript src="flvjs/flv.min.js"/>
<asset:javascript src="DPlayer/DPlayer.min.js"/>
<asset:javascript src="hls/hls.min.js"/>
<asset:javascript src="bailiff/index.js"/>
</html>
