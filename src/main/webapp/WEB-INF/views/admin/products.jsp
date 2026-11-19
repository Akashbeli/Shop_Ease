<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<div class="admin-bar">
  <div class="tabs">
    <a class="chip is-active" href="${ctx}/admin/products">Products</a>
    <a class="chip" href="${ctx}/admin/orders">Orders</a>
  </div>
  <a class="btn btn-primary" href="${ctx}/admin/products?action=new">Add product</a>
</div>

<h1 class="page-title">Products</h1>

<div class="table-wrap">
  <table class="table">
    <thead>
      <tr><th>Product</th><th>Category</th><th class="num">Price</th><th class="num">Stock</th><th><span class="sr-only">Actions</span></th></tr>
    </thead>
    <tbody>
      <c:forEach var="p" items="${products}">
        <tr>
          <td>
            <div class="cell-product">
              <span class="mini-thumb ${p.categoryClass} tint-${p.tint}"><span class="ico ico-<c:out value='${p.icon}'/>" aria-hidden="true"></span></span>
              <c:out value="${p.name}"/>
            </div>
          </td>
          <td><c:out value="${p.category}"/></td>
          <td class="num">${p.priceDisplay}</td>
          <td class="num">${p.stock}</td>
          <td>
            <div class="actions">
              <a class="btn btn-sm" href="${ctx}/admin/products?action=edit&amp;id=${p.id}">Edit</a>
              <form class="inline-form" method="post" action="${ctx}/admin/products" data-confirm="Delete this product? Past orders keep their details.">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="${p.id}">
                <button class="btn btn-sm" type="submit">Delete</button>
              </form>
            </div>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
