<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="Generic HTTP Web Service Client in HTML (GHoWSt)"/>
</jsp:include>

<h2>Sounds</h2>

<c:set var="uri" value="/generichttpws/sounds/play"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <label><span>Sound: </span><input type="radio" name="name" value="arrow_x.wav" size="10"<c:if test="${form[uri].name[0] == 'arrow_x.wav' || (form[uri].name[0] != 'arrow_x.wav' && form[uri].name[0] != 'arrow2.wav')}"> checked</c:if>>Arrow X</label> <label><input type="radio" name="name" value="arrow2.wav" size="10"<c:if test="${form[uri].name[0] == 'arrow2.wav'}"> checked</c:if>>Arrow 2</label>
        <button type="submit">Play on server</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <label><span>Sound: </span><input type="text" name="name" value="<c:out value="${form[uri].name[0]}"/>" size="10"></label>
        <button type="submit">Play on server</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/sounds"/>

<form action="<c:out value="${uri}"/>" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>New sound (wav)</legend>
        <label><span>Sound: </span><input type="file" name="sound.soundFile" value="" size="10" accept=".wav"></label><br>
        <br>
        <button type="submit">Create</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <legend>Get sound</legend>
        <label><span>Sound: </span><input type="text" name="name" value="<c:out value="${form[uri].name[0]}"/>" size="10"></label>
        <button type="submit">Get</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <button type="submit">Get all sounds</button>
    </fieldset>
</form>

<h2>Lights</h2>

<c:set var="uri" value="/generichttpws/keyboards/light"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <label><span>Color: </span><input type="color" name="color" value="<c:out value="${form[uri]['color'][0]}"/>"></label><br>
        <br>
        <button type="submit">Apply to server keyboard</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/keyboards/restore"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <button type="submit">Restore server keyboard</button>
    </fieldset>
</form>

<h2>Engines</h2>

<c:set var="uri" value="/generichttpws/engines"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>New engine</legend>
        <label><span>Name: </span><input type="text" name="engine.name" value="<c:out value="${form[uri]['engine.name'][0]}"/>" size="10"></label><br>
        <label><span>Cylinders: </span><input type="text" name="engine.cylinders" value="<c:out value="${form[uri]['engine.cylinders'][0]}"/>" size="10"></label><br>
        <label><span>Throttle: </span><input type="text" name="engine.throttleSetting" value="<c:out value="${form[uri]['engine.throttleSetting'][0]}"/>" size="10"></label><br>
        <br>
        <button type="submit">Create</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/engines/restart"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>Restart engine</legend>
        <label><span>Id: </span><input type="text" name="id" value="<c:out value="${form[uri].id[0]}"/>" size="10"></label>
        <button type="submit">Restart</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/engines/stop"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>Stop engine</legend>
        <label><span>Id: </span><input type="text" name="id" value="<c:out value="${form[uri].id[0]}"/>" size="10"></label>
        <button type="submit">Stop</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/engines"/>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <legend>Get engine</legend>
        <label><span>Id: </span><input type="text" name="id" value="<c:out value="${form[uri].id[0]}"/>" size="10"></label>
        <button type="submit">Get</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <button type="submit">Get all engines</button>
    </fieldset>
</form>

<h2>Photos</h2>

<c:set var="uri" value="/generichttpws/photos"/>

<form action="<c:out value="${uri}"/>" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>New photo (gif, jpg, jpeg, png)</legend>
        <label><span>Caption: </span><input type="text" name="photo.caption" value="<c:out value="${form[uri]['photo.caption'][0]}"/>" size="10"></label><br>
        <label><span>Photo: </span><input type="file" name="photo.photoFile" value="" size="10" accept=".gif,.jpg,.jpeg,.png"></label><br>
        <br>
        <button type="submit">Create</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <legend>Get photo</legend>
        <label><span>Photo: </span><input type="text" name="name" value="<c:out value="${form[uri].name[0]}"/>" size="10"></label>
        <button type="submit">Get</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <button type="submit">Get all photos</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/photos/display"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <label><span>Photo: </span><input type="text" name="name" value="<c:out value="${form[uri].name[0]}"/>" size="10"></label>
        <button type="submit">Display on server</button>
    </fieldset>
</form>

<h2>Trucks</h2>

<c:set var="uri" value="/generichttpws/trucks/race"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>Race trucks</legend>
        <label><span>T1 name: </span><input type="text" name="truck1.name" value="<c:out value="${form[uri]['truck1.name'][0]}"/>" size="10"></label><br>
        <label><span>T1 engine id: </span><input type="text" name="truck1.engineId" value="<c:out value="${form[uri]['truck1.engineId'][0]}"/>" size="10"></label><br>
        <label><span>T2 name: </span><input type="text" name="truck2.name" value="<c:out value="${form[uri]['truck2.name'][0]}"/>" size="10"></label><br>
        <label><span>T2 engine id: </span><input type="text" name="truck2.engineId" value="<c:out value="${form[uri]['truck2.engineId'][0]}"/>" size="10"></label><br>
        <br>
        <button type="submit">Race</button>
    </fieldset>
</form>

<%@ include file="footer.jsp" %>