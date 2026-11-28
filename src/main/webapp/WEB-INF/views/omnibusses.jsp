<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="model" class="viewbeans.Omnibusses" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Omnibus"/>
</jsp:include>

<h2>Let's see all the fun bus stuff we can look at!</h2>

<p>
    Bus color:
    <span style="background-color:<c:out value='${model.omnibusses.color}'/>;
            color:contrast-color(<c:out value='${model.omnibusses.color}'/> )">
		<c:out value="${model.omnibusses.color}"/>
	</span>
</p>

<p>
    A build time of
    <c:choose>
        <c:when test="${model.omnibusses.birthtime != null}">
            <c:out value="${model.omnibusses.birthtime}"/>
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
    with meal toppings
    <c:choose>
        <c:when test="${model.omnibusses.toppings.size() > 0}">
            <c:out value="${model.omnibusses.toppings}"/>
        </c:when>
        <c:otherwise>
            missing
        </c:otherwise>
    </c:choose>
    is a party!
</p>

<p>
    A send time of
    <c:choose>
        <c:when test="${model.omnibusses.sendtime != null}">
            <c:out value="${model.omnibusses.sendtime}"/>
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    Meal sides:
    <c:choose>
        <c:when test="${model.omnibusses.sides != null && fn:length(model.omnibusses.sides) > 0}">
            [<c:forEach var="side" items="${model.omnibusses.sides}" varStatus="st"><c:if test="${!st.first}">, </c:if><c:out value="${side}"/></c:forEach>]
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    The bus is on fire: <c:out value="${model.omnibusses.onFire}"/>
</p>

<p>
    Manual:
    <c:choose>
        <c:when test="${model.omnibusses.document != null}">
            <c:out value="${model.omnibusses.document.name}"/> (size: <c:out value="${model.omnibusses.document.length()}"/> bytes)
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    Meal pairing for Steak:
    <c:choose>
        <c:when test="${model.omnibusses.pairing != null && model.omnibusses.pairing['Steak'] != null}">
            <c:out value="${model.omnibusses.pairing['Steak']}"/>
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    Meal pairing for Fish:
    <c:choose>
        <c:when test="${model.omnibusses.pairing != null && model.omnibusses.pairing['Fish'] != null}">
            <c:out value="${model.omnibusses.pairing['Fish']}"/>
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    Engine name: <c:out value="${model.omnibusses.engine.name}"/><br>
    Engine cylinders: <c:out value="${model.omnibusses.engine.cylinders}"/><br>
    Engine throttle: <c:out value="${model.omnibusses.engine.throttleSetting}"/>
</p>

<%@ include file="footer.jsp" %>