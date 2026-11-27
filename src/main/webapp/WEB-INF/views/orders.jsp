<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="model" class="viewbeans.Orders" scope="request"/>
<jsp:include page="header.jsp">
    <jsp:param name="title" value="HTML Generic HTTP WS Client - Order"/>
</jsp:include>

<h2>Your order</h2>

<p>
    Napkin color:
    <span style="background-color:<c:out value='${model.orders.color}'/>;
            color:contrast-color(<c:out value='${model.orders.color}'/> )">
		<c:out value="${model.orders.color}"/>
	</span>
</p>

<p>
    An order time of
    <c:choose>
        <c:when test="${model.orders.birthtime != null}">
            <c:out value="${model.orders.birthtime}"/>
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
    with toppings
    <c:choose>
        <c:when test="${model.orders.toppings.size() > 0}">
            <c:out value="${model.orders.toppings}"/>
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
        <c:when test="${model.orders.sendtime != null}">
            <c:out value="${model.orders.sendtime}"/>
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    Sides:
    <c:choose>
        <c:when test="${model.orders.sides != null && fn:length(model.orders.sides) > 0}">
            [<c:forEach var="side" items="${model.orders.sides}" varStatus="st"><c:if test="${!st.first}">, </c:if><c:out value="${side}"/></c:forEach>]
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    The kitchen is on fire: <c:out value="${model.orders.onFire}"/>
</p>

<p>
    Recipe:
    <c:choose>
        <c:when test="${model.orders.document != null}">
            <c:out value="${model.orders.document.name}"/> (size: <c:out value="${model.orders.document.length()}"/> bytes)
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    Pairing for Steak:
    <c:choose>
        <c:when test="${model.orders.pairing != null && model.orders.pairing['Steak'] != null}">
            <c:out value="${model.orders.pairing['Steak']}"/>
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<p>
    Pairing for Fish:
    <c:choose>
        <c:when test="${model.orders.pairing != null && model.orders.pairing['Fish'] != null}">
            <c:out value="${model.orders.pairing['Fish']}"/>
        </c:when>
        <c:otherwise>
            none
        </c:otherwise>
    </c:choose>
</p>

<%@ include file="footer.jsp" %>