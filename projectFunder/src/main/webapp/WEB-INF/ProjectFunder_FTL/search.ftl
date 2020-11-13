<html>
  <head>
  <h1 style="text-align: center;"><span style="color: #008000;"><strong>Project Funder</strong></span></h1>
<h1 style="text-align: center;"><span style="color: #008000;"><strong>DatenBank Lab Project</strong></span></h1>
  <title>Search Projects</title>
  </head>
  <body>
  <ul>
        <li><a class="active" href="/view_main">ViewMain</a></li>
        <li style="float:right"><a href="/search">Search</a></li>
        <li><a href="/new_project">Create Project</a></li>
         </ul>
  <center>
    <form>
      Title:<br>
      <input type="text" name="title"/> <br>
      <input type="submit" value="search"/>
    </form>
    <#if error_detected>
      <p>${error_message}</p>
      <#elseif resultList?size == 0>
        <p>Nothing there</p>
        <#else>
          <table class ="datatable">
            <tr>
              <th></th>  <th>Title</th> <th>Creator</th> <th>Status</th> <th>Total</th>
            </tr>
            <#list resultList as r>
              <tr>
                <td>${r.icon}</td>
                <td><a href="../view_project?id=${r.id}">${r.title}</a></td>
                <td><a href="../view_profile?u=${r.email}">${r.name}</a></td>
                <td><a href="../view_profile?u=${r.email}">${r.status}</a></td>
                <td>${r.sum}</td>
              </tr>
            </#list>
          </table>
        </#if>
  </body>
</html>
