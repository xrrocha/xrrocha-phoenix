<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%
// Create and configure factory
DiskFileItemFactory factory = new DiskFileItemFactory();

int maxSize = 4 * 1024 * 1024;
factory.setSizeThreshold(maxSize);

File tmpDir = new File(System.getProperty("java.io.tmpdir"));
factory.setRepository(tmpDir);

// Create a new file upload handler
ServletFileUpload upload = new ServletFileUpload(factory);

// Set overall request size constraint
upload.setSizeMax(2 * maxSize);

// Parse the request
List<DiskFileItem> items = (List<DiskFileItem>) upload.parseRequest(request);
%>
<html>
    <head>
        <title>Upload Example</title>
    </head>
    <body>
        <h1 style="text-align: center; color: navy; background-color: white">
           Deploy Archive
        </h1>
        <p>
            Work in progress...
        </p>
        <table border="1" align="center">
            <tr>
                <th>Name</th>
                <th>File</th>
                <th>Exists?</th>
            </tr>
            <%
                for (DiskFileItem item: items) {
                    File file = File.createTempFile("rrc", ".tmp");
                    item.write(file);
                    %>
                        <td><%= item.getName() %></td>
                        <td><%= file.getAbsolutePath() %></td>
                        <td><%= file.exists() %></td>
                    <%
                }
            %>
        </table>
    </body>
</html>

