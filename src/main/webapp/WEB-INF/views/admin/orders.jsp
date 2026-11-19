<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<div class="admin-bar">
  <div class="tabs">
    <a class="chip" href="${ctx}/admin/products">Products</a>
    <a class="chip is-active" href="${ctx}/admin/orders">Orders</a>
  </div>
</div>

<h1 class="page-title">Orders</h1>

<c:choose>
  <c:when test="${empty orders}">
    <div class="empty">
      <div class="empty-art"><span class="ico ico-box" aria-hidden="true"></span></div>
      <h2>No orders yet</h2>
      <p>Orders from shoppers will appear here.</p>
    </div>
  </c:when>
  <c:otherwise>
    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr><th>Order</th><th>Customer</th><th>Placed on</th><th class="num">Items</th><th class="num">Total</th><th>Payment</th><th>Status</th></tr>
        </thead>
        <tbody>
          <c:forEach var="o" items="${orders}">
            <tr>
              <td><strong>#${o.id}</strong></td>
              <td><c:out value="${o.customerName}"/></td>
              <td>${o.createdDisplay}</td>
              <td class="num">${o.itemCount}</td>
              <td class="num">${o.totalDisplay}</td>
              <td><c:out value="${o.paymentLabel}"/></td>
              <td>
                <form class="status-form" method="post" action="${ctx}/admin/orders">
                  <input type="hidden" name="id" value="${o.id}">
                  <select name="status" aria-label="Status of order ${o.id}">
                    <c:forEach var="s" items="${statuses}">
                      <option value="${s}" ${o.status eq s ? 'selected' : ''}>${s.label}</option>
                    </c:forEach>
                  </select>
                  <button class="btn btn-sm" type="submit">Update</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
