<html>
  <head>
  <h1 style="text-align: center;"><span style="color: #008000;"><strong>Project Funder</strong></span></h1>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>DatenBank Lab Project</strong></span></h1>
  <title>New Comment</title>
  </head>
  <body>
  <ul>
          <li style="float:right"><a href="/view_profile?u=${loggedInUser}">My Profile</a></li>
        <li><a class="active" href="/view_main">ViewMain</a></li>
        <li style="float:right"><a href="/search">Search</a></li>
        <li><a href="/new_project">Create Project</a></li>
         </ul>
    <p> Logged in user = ${loggedInUser} </p>

    <#if error_detected>
        <p> ${error_message} </p>
    <#else>
        <p> Project title =  ${projectTitle} </p>
    </#if>
    <form method="post">
      <textarea id="comment" cols="42" rows="20" name="comment"></textarea>
      <input type="checkbox" name="anonymous" value="true">Anonymous Comment
      <input type="submit" value="Add comment"/>
      
    </form>
  </body>
</html>
