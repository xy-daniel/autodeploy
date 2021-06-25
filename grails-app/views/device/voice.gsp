<%--
  Created by IntelliJ IDEA.
  User: midianjun
  Date: 2021/2/3
  Time: 16:42
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta charset="utf-8" />
    <title>法庭传唤</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />
    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <asset:stylesheet href="device/voice.css"/>
    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>

<body style="margin: 0" >
<div class="court">
    <div class="court-header">
        <h2 class="title">请选择法庭</h2>
        <a href="javascript:" target="_self" id="set" class="set"></a>
    </div>
    <div class="row">
        <div class="court-list">
            <div class="ul-list">
                <ul class="ul-message">
                </ul>
            </div>
        </div>
        <div class="court-content" style="padding: 10px;">
            <div class="court-content-message">
                <div class="court-people" id="court-people">
                </div>
            </div>
        </div>
    </div>
</div>
<div id="popup-window" class="back">
    <div id="div1" class="content">
        <div id="close">
            <span id="close-button">×</span>
            <h2 style="margin: 0px;">传唤人确认</h2>
        </div>
        <div id="div2">
            <p id="popup-content" style="margin: 0px;"></p>
        </div>
        <div id="buttom" style="line-height: 7vh">
            <button type="submit" class="popup-btn" id="close-btn" value="">取消</button>
            <button type="submit" class="popup-btn" id="sure-btn" value="">确认</button>
        </div>
    </div>
</div>
<div id="set-window" class="set-window back">
    <div id="div3" class="content">
        <div class="close">
            <span class="close-button">×</span>
            <h2 style="margin: 0px;">选择法庭</h2>
        </div>
        <div id="div4">
            <div class=" content-title">
                请选择法庭：
            </div>
            <div class=" content-select">
                <select class="select">
                    <option class="select-option">请选择</option>
                </select>
            </div>

        </div>
        <div id="set-buttom" style="line-height: 45px">
            <button type="submit" class="popup-btn" id="set-close-btn" value="">取消</button>
            <button type="submit" class="popup-btn" id="set-sure-btn" value="">确认</button>
        </div>
    </div>
</div>
<asset:javascript src="jquery/jquery-3.3.1.min.js"/>
<asset:javascript src="device/voice.js"/>
<script type="text/javascript">
    var contextPath = "${createLink(uri: '/')}";
    window.onload = function(){
        myFunction();
        subpoena();
        set_window();
        transmit();
    }
</script>
</body>
</html>