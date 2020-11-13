<html>
  <head>
  <h1 style="text-align: center;"><span style="color: #008000;"><strong>Project Funder</strong></span></h1>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>DatenBank Lab Project</strong></span></h1>
    <title>Project Details</title>
    
  </head>
  <body>
  <ul>
          <li style="float:right"><a href="/view_profile?u=${loggedInUser}">My Profile</a></li>
        <li><a class="active" href="/view_main">ViewMain</a></li>
        <li style="float:right"><a href="/search">Search</a></li>
        <li><a href="/new_project">Create Project</a></li>
         </ul>
<center>
    <h2>Information</h2>
    <img src=${icon} height=24 width=24 ><br>
    Title: ${title} <br>
    Created by: <a href="/view_profile?u=${creatorMail}">${creatorName} </a><br>
    ${description}<br>
    Limit: ${limit} <br>
    Current sum: ${sum} <br>
    Status: ${status}<br>
    <#if vorganger>
      Predecessor:: <a href="view_project?id=${vid}">${vtitle}</a> <br>
    </#if>
</center>
	<h2>Edit Project</h2>
    <a href="/edit_project?id=${id}"><input type = "submit" value="Edit"></a>
    
    <h2>Donate</h2>
    <a href="/new_project_fund?id=${id}"><input type = "submit" value="Donate"></a>
     <h2>Delete</h2>
    <form method="post">
      <input type="hidden" name="loggedInUser" value=${loggedInUser}>
      <input type="hidden" name="projectid" value=${id}>
      <input type = "submit" value="Delete">
    </form>

    <h2>Donors Lists</h2>
    <#list donations as d>
    <th>Donor Name: </th><th>Donation Amount</th><br>
      ${d.name} : ${d.amount} <br>
    </#list>

    <h2>Comments</h2>
    
    <#list comments as c>
      ${c.name} : ${c.text} <br>
    </#list> <br>
    <a href="/new_comment?id=${id}"> <input type = "submit" value="Add Comment"> </a> <br><br>
  </body>
</html>
