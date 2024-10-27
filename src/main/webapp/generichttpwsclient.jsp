<jsp:include page="/WEB-INF/views/header.jsp">
    <jsp:param name="title" value="Generic HTTP Web Service Client in HTML (GHoWSt)"/>
</jsp:include>

<h2>Sounds</h2>

<form action="generichttpws/sounds/play" method="post">
    <fieldset>
        Sound: <label><input type="radio" name="name" value="arrow_x.wav" size="10" checked>Arrow X</label> <label><input type="radio" name="name" value="arrow2.wav" size="10">Arrow 2</label>
        <input type="submit" name="submit" value="Play on server">
    </fieldset>
</form>

<form action="generichttpws/sounds/play" method="post">
    <fieldset>
        Sound: <input type="text" name="name" value="" size="10">
        <input type="submit" name="submit" value="Play on server">
    </fieldset>
</form>

<form action="generichttpws/sounds" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>New sound (wav)</legend>
        Sound: <input type="file" name="soundFile" value="" size="10" accept=".wav"><br>
        <br>
        <input type="submit" name="submit" value="Create">
    </fieldset>
</form>

<form action="generichttpws/sounds" method="get">
    <fieldset>
        <legend>Get sound</legend>
        Sound: <input type="text" name="name" value="" size="10">
        <input type="submit" name="submit" value="Get">
    </fieldset>
</form>

<blockquote><a href="generichttpws/sounds">Get all sounds</a></blockquote>

<h2>Lights</h2>

<form action="generichttpws/keyboards/light" method="post">
    <fieldset>
        Keyboard and mouse: <input type="submit" name="submit" value="Light on server">
    </fieldset>
</form>

<h2>Engines</h2>

<form action="generichttpws/engines" method="post">
    <fieldset>
        <legend>New engine</legend>
        Name: <input type="text" name="name" value="" size="10"><br>
        Cylinders: <input type="text" name="cylinders" value="" size="10"><br>
        Throttle: <input type="text" name="throttleSetting" value="" size="10"><br>
        <br>
        <input type="submit" name="submit" value="Create">
    </fieldset>
</form>

<form action="generichttpws/engines/restart" method="post">
    <fieldset>
        <legend>Restart engine</legend>
        Id: <input type="text" name="id" value="" size="10">
        <input type="submit" name="submit" value="Restart">
    </fieldset>
</form>

<form action="generichttpws/engines/stop" method="post">
    <fieldset>
        <legend>Stop engine</legend>
        Id: <input type="text" name="id" value="" size="10">
        <input type="submit" name="submit" value="Stop">
    </fieldset>
</form>

<form action="generichttpws/engines" method="get">
    <fieldset>
        <legend>Get engine</legend>
        Id: <input type="text" name="id" value="" size="10">
        <input type="submit" name="submit" value="Get">
    </fieldset>
</form>

<blockquote><a href="generichttpws/engines">Get all engines</a></blockquote>

<h2>Photos</h2>

<form action="generichttpws/photos" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>New photo (gif, jpg, jpeg, png)</legend>
        Caption: <input type="text" name="caption" value="" size="10"><br>
        Photo: <input type="file" name="photoFile" value="" size="10" accept=".gif,.jpg,.jpeg,.png"><br>
        <br>
        <input type="submit" name="submit" value="Create">
    </fieldset>
</form>

<form action="generichttpws/photos" method="get">
    <fieldset>
        <legend>Get photo</legend>
        Photo: <input type="text" name="name" value="" size="10">
        <input type="submit" name="submit" value="Get">
    </fieldset>
</form>

<blockquote><a href="generichttpws/photos">Get all photos</a></blockquote>

<%@ include file="WEB-INF/views/footer.jsp" %>