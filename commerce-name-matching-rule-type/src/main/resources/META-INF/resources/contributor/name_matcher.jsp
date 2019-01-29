<%@ include file="/init.jsp" %>

<%
Object cpCatalogRuleDisplayContext = request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

Class<?> displayContextClass = cpCatalogRuleDisplayContext.getClass();

Method getCPRuleTypeSettingsPropertyMethod = displayContextClass.getMethod("getCPRuleTypeSettingsProperty", String.class);

String nameSubstring = (String)getCPRuleTypeSettingsPropertyMethod.invoke(cpCatalogRuleDisplayContext, "nameSubstring");
%>

<aui:input label="name" name="nameSubstring" type="text" value="<%= nameSubstring %>" />