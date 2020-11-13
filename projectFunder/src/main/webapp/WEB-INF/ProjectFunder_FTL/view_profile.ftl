<html>
<head>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>Project Funder</strong></span></h1>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>DatenBank Lab Project</strong></span></h1>
<title>Profile Details</title>
<body>
<ul>
          
        <li><a class="active" href="/view_main">ViewMain</a></li>
        <li style="float:right"><a href="/search">Search</a></li>
        <li><a href="/new_project">Create Project</a></li>
         </ul>
         <center>
<#if error_detected>
<p>${error_message}</p>
<#elseif userEmail == "">
<p> USER NOT FOUND </p>
<#else>
    <p>
        Profile Holder Email: ${userEmail}<br>
        Profile Holder Name: ${name}<br>
        Number of Created Projects: ${created}<br>
	Number of Supported Projects: ${funding}
    </p>


    <h2> Created Projects </h2>
        <table class="datatable">
            <tr>
                <th>Icon</th>  <th>Title</th> <th>Status</th> <th>Donated Amount</th>
            </tr>
            <#list userCreatedProjects as p_created>
                <tr>
                    <td>${p_created.icon}</td>
                    <td><a href="../view_project?id=${p_created.id}">${p_created.title}</a></td>
                    <td>${p_created.status}</td>
                    <td>${p_created.sum}</td>
                </tr>
            </#list>
        </table>


    <h2> Supported Projects </h2>
        <table class="datatable">
            <tr>
                <th>Icon</th>  <th>Title</th> <th>Status</th> <th>Limit</th> <th>Donated Amount</th>
            </tr>
            <#list supportedProjects as p_supported>
                <tr>
                    <td>${p_supported.icon}</td>
                    <td><a href="../view_project?id=${p_supported.id}">${p_supported.title}</a></td>
                    <td>${p_supported.status}</td>
                    <td>${p_supported.limit}</td>
                    <td>${p_supported.amount}</td>
                </tr>
            </#list>
        </table>
        </#if>
</body
</html>
