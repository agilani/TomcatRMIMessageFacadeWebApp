<html>
	<head>
		<title>Distributed Systems Assignment 2013 - Async Crypto Service</title>
	</head>
	<body>
		
		<h1>Distributed System Assignment - Asynchronous Messaging - 2013</h1>
		<h3>By: Adeel Gilani - G00279198</h3>
		<%
			//check if app is ready to run
			//if not then display an error detailing what to do
			if((Integer)getServletContext().getAttribute("webapp_ready_for_use")==0)
			{
		%>
		<h3>Couldn't locate the RMI binding. Please first run the ie.gmit.AsyncService from crypto.ds and then restart this app</h3>
		<%
			}
		else
		{
		%>
		<form name="frmCryptoService" method="POST" ENCTYPE="multipart/form-data" action="handler">
			<p><b>Operation: &nbsp;</b>
			<select name="cmbOperation">
				<option selected value="Encrypt">Encrypt</option>
				<option value="Decrypt">Decrypt</option>
				<option value="Compress">Compress</option>
				<option value="Decompress">Decompress</option>
				<option value="EncryptAndCompress">Encrypt and Compress</option>
				<option value="DecompressAndDecrypt">Decompress And Decrypt</option>
			</select></p>
			<p><b>Encryption Key:</b>
            <input name="pwd" type="text" width="35" maxlength="15"> <i>if no key is entered then the key will be "G00279198"</i></p>
            <p><b>File:</b>
            <input type="file" name="filePath"></p>
			<h2> OR </h2>
            <p>
			<b>Enter Text</b><br/>
			<textarea name="txtMessage" rows="12" cols="100" wrap="hard"></textarea>
			<p/>
			<p><em>if file is choosen then the operation will be on file and the textarea will not be accounted</em>
			  <input type="Submit" name="btnSubmit" value="Go Get 'Em!">
		  </p>
		</form>
        <h3>Already submitted a Job...?</h3>
        <form method="POST" action="response">
        <p>Enter Job ID: &nbsp;&nbsp;&nbsp;<input type="text" name="msgID" /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="submit" value="Go Get 'Em!" />
        </form>
        <%}%>
	</body>
</html>	
		