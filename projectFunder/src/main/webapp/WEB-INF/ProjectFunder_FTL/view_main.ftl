<html>
  <head>
    <h1 style="text-align: center;"><span style="color: #008000;"><strong>Project Funder</strong></span></h1>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>DatenBank Lab Project</strong></span></h1>

  </head>
  <body>
  <#if error_detected>
          <p>${error_message}</p>
          <#else>
    <div class="nav">
      <ul>
          <li style="float:right"><a href="/view_profile?u=${loggedInUser}">My Profile</a></li>
        <li><a class="active" href="/view_main">ViewMain</a></li>
        <li style="float:right"><a href="/search">Search</a></li>
        <li><a href="/new_project">Create Project</a></li>
         </ul>
    </div>
    <h2 style="text-align: center;"> <span style="color: #3366ff;"> Open projects </h2>

    <table class="projects">
      <tr>
        <th></th>
        <th>Title</th>
        <th>Email</th>
        <th>Total</th>
      </tr>
      <#list openProject as openP>
        <tr>
          <td><img src=${openP.icon} height=24 width=24 class="icon"></td>
          <td><a href="../view_project?id=${openP.id}" class="projectid">${openP.title}</a></td>
          <td><a href="../view_profile?u=${openP.email}" class="email">${openP.name}</a></td>
          <td>${openP.sum}</td>
        </tr>
      </#list>
    </table>

    <h2> <h2 style="text-align: center;"><span style="color: #ff0000;">Closed projects </h2>


    <table class="projects">
      <tr>
        <th></th>
        <th>Title</th>
        <th>Email</th>
        <th>Total</th>
      </tr>
      <#list closedProject as closedP>
        <tr>
          <td><img src=${closedP.icon} height=24 width=24 class="icon"></td>
          <td><a href="../view_project?id=${closedP.id}" class="projectid">${closedP.title}</a></td>
          <td><a href="../view_profile?u=${closedP.email}" class="email">${closedP.name}</a></td>
          <td>${closedP.sum}</td>
        </tr>
      </#list>
    </table>
    </#if>
  </body>
</html>
