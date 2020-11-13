<html>
  <head>
  <h1 style="text-align: center;"><span style="color: #008000;"><strong>Project Funder</strong></span></h1>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>DatenBank Lab Project</strong></span></h1>
    <title>New Project Fund</title>
    <link rel="stylesheet" type="text/css" href="/icons/style.css">
  </head>

  <body>
  <ul>
          <li style="float:right"><a href="/view_profile?u=${loggedInUser}">My Profile</a></li>
        <li><a class="active" href="/view_main">ViewMain</a></li>
        <li style="float:right"><a href="/search">Search</a></li>
        <li><a href="/new_project">Create Project</a></li>
         </ul>
    <form method="post">
      Donation for project ${projectTitle}<br>
      Amount: <input type="text" name="amount"> <br>
      <input type="radio" name="anonymous" value="true"> Anonymous donation <br>
      <input type="hidden" name="user" value=${loggedInUser}>
      <input type="hidden" name="id" value=${id}>
      <input type="submit" value="donate">
    </form>
  </body>
</html>
