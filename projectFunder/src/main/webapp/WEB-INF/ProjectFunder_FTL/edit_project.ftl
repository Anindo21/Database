<html>
	<head>
	<h1 style="text-align: center;"><span style="color: #008000;"><strong>Project Funder</strong></span></h1>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>DatenBank Lab Project</strong></span></h1>
	  <title>Edit Project</title>

	</head>
	<body>
	<ul>
          <li style="float:right"><a href="/view_profile?u=${loggedInUser}">My Profile</a></li>
        <li><a class="active" href="/view_main">ViewMain</a></li>
        <li style="float:right"><a href="/search">Search</a></li>
        <li><a href="/new_project">Create Project</a></li>
         </ul>
		<form method="post">
			Title:<br>

      <input type="hidden" name="creator" value=${loggedInUser}>
			<input type="text" name="title" value=${title}><br>
			Limit:<br>
			<input type="text" name="limit" value=${limit}><br>

      Category:<br>
			<input type="radio" name="category" value="1"> Health & Creative Works<br>
			<input type="radio" name="category" value="2"> Art & Creative Works<br>
			<input type="radio" name="category" value="3"> Education<br>
			<input type="radio" name="category" value="4"> Tech & Innovation<br>

      Predecessor:<br>
			<input type="radio" name="vorganger" value="None" checked="checked"> None<br>
      <#list projectList as p>
        <input type="radio" name="vorganger" value=${p.id}> ${p.title}<br>
      </#list>
			Description:<br>
			<input type="text" name="description" value=${description}><br>
			<input type="submit" value="Update"/>
		</form>
<#if error_detected>
              <p> ${error_message} </p>
            </#if>
            <#if success>
              <p> Updated project successfully! </p>
            </#if>
	</body>
</html>
