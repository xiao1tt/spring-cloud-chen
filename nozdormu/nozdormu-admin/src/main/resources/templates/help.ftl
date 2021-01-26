<!DOCTYPE html>
<html>
<head>
    <#import "./common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <title>${I18n.admin_name}</title>
</head>
<body
    class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["job_adminlte_settings"]?exists && "off" == cookieMap["job_adminlte_settings"].value >sidebar-collapse</#if> ">
<div class="wrapper">
</div>
<@netCommon.commonScript />
</body>
</html>
