<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="model" scope="request" type="beans.HtmlClientBean"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="Generic HTTP Web Service Client in HTML (GHoWSt)"/>
</jsp:include>

<h2>Sounds</h2>

<c:set var="uri" value="/generichttpws/sounds"/>

<form action="<c:out value="${uri}"/>" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>New sound</legend>
        <label><span>Caption: </span><input type="text" name="sound.caption" value="<c:out value="${model.formData[uri]['sound.caption'][0]}"/>" size="10"> (optional)</label><br>
        <label><span>Sound: </span><input type="file" name="sound.soundFile" size="10" accept="${model.audioTypesList}"></label><br>
        <br>
        <button type="submit">Create</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <legend>Get sound</legend>
        <label><span>Sound: </span><input type="text" name="name" value="<c:out value="${model.formData[uri].name[0]}"/>" size="10"></label>
        <button type="submit">Get</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <button type="submit">Get all sounds</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/sounds/play"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>Play sound</legend>
        <label><span>Name: </span></label><label><input type="radio" name="name" value="arrow_x.wav"<c:if test="${model.formData[uri].name[0] == 'arrow_x.wav' || (model.formData[uri].name[0] != 'arrow_x.wav' && model.formData[uri].name[0] != 'arrow2.wav')}"> checked</c:if>>Arrow X</label> <label><input type="radio" name="name" value="arrow2.wav"<c:if test="${model.formData[uri].name[0] == 'arrow2.wav'}"> checked</c:if>>Arrow 2</label>
        <button type="submit">Play on server</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>Play sound</legend>
        <label><span>Name: </span><select name="name"><c:forEach var="sound" items="${model.sounds}"><option value="<c:out value="${sound.name}"/>"<c:if test="${sound.name == model.formData[uri].name[0]}"> selected</c:if>><c:out value="${not empty sound.caption ? sound.caption : sound.name}"/></option></c:forEach></select></label>
        <button type="submit">Play on server</button>
    </fieldset>
</form>

<h2>Images</h2>

<c:set var="uri" value="/generichttpws/images"/>

<form action="<c:out value="${uri}"/>" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>New image</legend>
        <label><span>Caption: </span><input type="text" name="image.caption" value="<c:out value="${model.formData[uri]['image.caption'][0]}"/>" size="10"> (optional)</label><br>
        <label><span>Image: </span><input type="file" name="image.imageFile" size="10" accept="${model.imageTypesList}"></label><br>
        <br>
        <button type="submit">Create</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <legend>Get image</legend>
        <label><span>Name: </span><input type="text" name="name" value="<c:out value="${model.formData[uri].name[0]}"/>" size="10"></label>
        <button type="submit">Get</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <button type="submit">Get all images</button>
    </fieldset>
</form>

<h2>Videos</h2>

<c:set var="uri" value="/generichttpws/videos"/>

<form action="<c:out value="${uri}"/>" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>New video</legend>
        <label><span>Caption: </span><input type="text" name="video.caption" value="<c:out value="${model.formData[uri]['video.caption'][0]}"/>" size="10"> (optional)</label><br>
        <label><span>Video: </span><input type="file" name="video.videoFile" size="10" accept="${model.videoTypesList}"></label><br>
        <br>
        <button type="submit">Create</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <legend>Get video</legend>
        <label><span>Name: </span><input type="text" name="name" value="<c:out value="${model.formData[uri].name[0]}"/>" size="10"></label>
        <button type="submit">Get</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <button type="submit">Get all videos</button>
    </fieldset>
</form>

<h2>Engines</h2>

<c:set var="uri" value="/generichttpws/engines"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>New engine</legend>
        <label><span>Name: </span><input type="text" name="engine.name" value="<c:out value="${model.formData[uri]['engine.name'][0]}"/>" size="10"></label><br>
        <label><span>Cylinders: </span><input type="text" name="engine.cylinders" value="<c:out value="${model.formData[uri]['engine.cylinders'][0]}"/>" size="10"></label><br>
        <label><span>Throttle: </span><input type="text" name="engine.throttleSetting" value="<c:out value="${model.formData[uri]['engine.throttleSetting'][0]}"/>" size="10"></label><br>
        <br>
        <button type="submit">Create</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/engines"/>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <legend>Get engine</legend>
        <label><span>Id: </span><input type="text" name="id" value="<c:out value="${model.formData[uri].id[0]}"/>" size="10"></label>
        <button type="submit">Get</button>
    </fieldset>
</form>

<form action="<c:out value="${uri}"/>" method="get">
    <fieldset>
        <button type="submit">Get all engines</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/engines/restart"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>Restart engine</legend>
        <label><span>Id: </span><input type="text" name="id" value="<c:out value="${model.formData[uri].id[0]}"/>" size="10"></label>
        <button type="submit">Restart</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/engines/stop"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>Stop engine</legend>
        <label><span>Id: </span><input type="text" name="id" value="<c:out value="${model.formData[uri].id[0]}"/>" size="10"></label>
        <button type="submit">Stop</button>
    </fieldset>
</form>

<h2>Trucks</h2>

<c:set var="uri" value="/generichttpws/trucks/race"/>

<form action="<c:out value="${uri}"/>" method="post">
    <fieldset>
        <legend>Race trucks</legend>
        <label><span>T1 name: </span><input type="text" name="truck1.name" value="<c:out value="${model.formData[uri]['truck1.name'][0]}"/>" size="10"></label><br>
        <label><span>T1 engine id: </span><input type="text" name="truck1.engineId" value="<c:out value="${model.formData[uri]['truck1.engineId'][0]}"/>" size="10"></label><br>
        <label><span>T2 name: </span><input type="text" name="truck2.name" value="<c:out value="${model.formData[uri]['truck2.name'][0]}"/>" size="10"></label><br>
        <label><span>T2 engine id: </span><input type="text" name="truck2.engineId" value="<c:out value="${model.formData[uri]['truck2.engineId'][0]}"/>" size="10"></label><br>
        <br>
        <button type="submit">Race</button>
    </fieldset>
</form>

<h2>Omnibus</h2>

<c:set var="uri" value="/generichttpws/omnibus/test"/>
<c:set var="selectedToppings" value="|${fn:join(model.formData[uri]['omnibus.toppings'], '|')}|"/>
<c:set var="selectedSides" value="|${fn:join(model.formData[uri]['omnibus.toppings'], '|')}|"/>
<c:set var="omnibusSendtime" value="${model.formData[uri]['omnibus.sendtime'][0] == null ? '2026-05-07T14:30:00.000-08:00' : model.formData[uri]['omnibus.sendtime'][0]}"/>

<form action="<c:out value="${uri}"/>" method="post" enctype="multipart/form-data">
    <fieldset disabled>
        <legend>Various other types within custom object</legend>
        <label><span>Birth time: </span><input type="datetime-local" name="omnibus.birthtime" value="<c:out value="${model.formData[uri]['omnibus.birthtime'][0]}"/>"></label><br>
        <label><span>Send time: </span><input type="text" name="omnibus.sendtime" value="<c:out value="${omnibusSendtime}"/>" size="30"></label><br>
        <label><span>Toppings: </span></label><label><input type="checkbox" name="omnibus.toppings" value="Cheese"<c:if test="${fn:contains(selectedToppings, '|Cheese|')}"> checked</c:if>>Cheese</label> <label><input type="checkbox" name="omnibus.toppings" value="Pepperoni"<c:if test="${fn:contains(selectedToppings, '|Pepperoni|')}"> checked</c:if>>Pepperoni</label> <label><input type="checkbox" name="omnibus.toppings" value="Mushrooms"<c:if test="${fn:contains(selectedToppings, '|Mushrooms|')}"> checked</c:if>>Mushrooms</label><br>
        <label><span>Sides: </span></label><label><input type="checkbox" name="omnibus.sides" value="Salad"<c:if test="${fn:contains(selectedSides, '|Salad|')}"> checked</c:if>>Salad</label> <label><input type="checkbox" name="omnibus.sides" value="Beans"<c:if test="${fn:contains(selectedSides, '|Beans|')}"> checked</c:if>>Beans</label> <label><input type="checkbox" name="omnibus.sides" value="Fries"<c:if test="${fn:contains(selectedSides, '|Fries|')}"> checked</c:if>>Fries</label><br>
        <label><span>On fire: </span><input type="checkbox" name="omnibus.onFire" value="true"<c:if test="${model.formData[uri]['omnibus.onFire'][0]}"> checked</c:if>></label><br>
        <label><span>Document: </span><input type="file" name="omnibus.document" size="10" accept="application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/pdf"></label><br>
        <br>
        <button type="submit">Test</button>
    </fieldset>
</form>

<c:set var="uri" value="/generichttpws/omnibusr/test"/>
<c:set var="selectedToppings" value="|${fn:join(model.formData[uri]['toppings'], '|')}|"/>
<c:set var="selectedSides" value="|${fn:join(model.formData[uri]['sides'], '|')}|"/>
<c:set var="sendtime" value="${model.formData[uri]['sendtime'][0] == null ? '2026-05-07T14:30:00.000-08:00' : model.formData[uri]['sendtime'][0]}"/>

<form action="<c:out value="${uri}"/>" method="post" enctype="multipart/form-data">
    <fieldset>
        <legend>Various other types at root level</legend>
        <label><span>Birth time: </span><input type="datetime-local" name="birthtime" value="<c:out value="${model.formData[uri]['birthtime'][0]}"/>"></label><br>
        <label><span>Send time: </span><input type="text" name="sendtime" value="<c:out value="${sendtime}"/>" size="30"></label><br>
        <label><span>Toppings: </span></label><label><input type="checkbox" name="toppings" value="Cheese"<c:if test="${fn:contains(selectedToppings, '|Cheese|')}"> checked</c:if>>Cheese</label> <label><input type="checkbox" name="toppings" value="Pepperoni"<c:if test="${fn:contains(selectedToppings, '|Pepperoni|')}"> checked</c:if>>Pepperoni</label> <label><input type="checkbox" name="toppings" value="Mushrooms"<c:if test="${fn:contains(selectedToppings, '|Mushrooms|')}"> checked</c:if>>Mushrooms</label><br>
        <label><span>Sides: </span></label><label><input type="checkbox" name="sides" value="Salad"<c:if test="${fn:contains(selectedSides, '|Salad|')}"> checked</c:if>>Salad</label> <label><input type="checkbox" name="sides" value="Beans"<c:if test="${fn:contains(selectedSides, '|Beans|')}"> checked</c:if>>Beans</label> <label><input type="checkbox" name="sides" value="Fries"<c:if test="${fn:contains(selectedSides, '|Fries|')}"> checked</c:if>>Fries</label><br>
        <label><span>On fire: </span><input type="checkbox" name="onFire" value="true"<c:if test="${model.formData[uri]['onFire'][0]}"> checked</c:if>></label><br>
        <label><span>Document: </span><input type="file" name="document" size="10" accept="application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/pdf"></label><br>
        <br>
        <button type="submit">Test</button>
    </fieldset>
</form>

<%@ include file="footer.jsp" %>