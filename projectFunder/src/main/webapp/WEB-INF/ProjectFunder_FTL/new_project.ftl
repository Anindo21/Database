<html>
	<head>
	<h1 style="text-align: center;"><span style="color: #008000;"><strong>Project Funder</strong></span></h1>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>DatenBank Lab Project</strong></span></h1>
	<title>Create New Project</title>
	</head>
	<body>
	<ul>
        <li><a class="active" href="/view_main">ViewMain</a></li>
        <li style="float:right"><a href="/search">Search</a></li>
        <li><a href="/new_project">Create Project</a></li>
         </ul>
	<center>
	
		<form>
			<br>
			<h1 font size=20>Create Project</h1>
			<p text-align="justify">Title:<input type="text" name="title"><br></p>
			<p>Limit:<input type="text" name="limit"> €<br></p>

		Category:<br>
			<input type="radio" name="category" value="1"> Health & Creative Works       <input type="radio" name="category" value="2"> Art & Creative Works<br>

			<input type="radio" name="category" value="3"> Education
			<input type="radio" name="category" value="4"> Tech & Innovation<br><br>

      Predecessor:<br>
           <input type="radio" name="vorganger" value="None" checked="checked"> None<br>
                 <#list projectList as p>
                   <input type="radio" name="vorganger" value=${p.id}> ${p.title}<br>
                 </#list>
           			Description:<br>
           			<input type="text" name="description"><br>
           			<input type="submit" value="Create"/>
           		</form>

		<#if error_detected>
              <p> ${error_message} </p>
            </#if>
            <#if success>
              <p> Created project successfully! </p>
            </#if>
		</center>
	</body>
</html>
