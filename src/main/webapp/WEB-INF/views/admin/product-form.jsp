<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<a class="back" href="${ctx}/admin/products">Back to products</a>
<h1 class="page-title">${product.id eq 0 ? 'Add product' : 'Edit product'}</h1>

<c:if test="${not empty errors}">
  <div class="alert alert-error" role="alert">
    <ul><c:forEach var="e" items="${errors}"><li><c:out value="${e}"/></li></c:forEach></ul>
  </div>
</c:if>

<form class="panel" method="post" action="${ctx}/admin/products">
  <input type="hidden" name="action" value="save">
  <input type="hidden" name="id" value="${product.id}">

  <div class="form-grid">
    <div class="field span-2">
      <label for="name">Name</label>
      <input id="name" name="name" required maxlength="120" value="<c:out value='${product.name}'/>">
    </div>
    <div class="field">
      <label for="category">Category</label>
      <input id="category" name="category" list="category-list" required maxlength="50" value="<c:out value='${product.category}'/>">
      <datalist id="category-list">
        <c:forEach var="c" items="${categories}"><option value="<c:out value='${c.name}'/>"></option></c:forEach>
      </datalist>
      <span class="hint">Pick one or type a new category</span>
    </div>
    <div class="field">
      <label for="price">Price (&#8377;)</label>
      <input id="price" name="price" type="number" min="1" step="0.01" required value="${product.price eq 0 ? '' : product.price}">
    </div>
    <div class="field">
      <label for="stock">Stock</label>
      <input id="stock" name="stock" type="number" min="0" step="1" required value="${product.id eq 0 and product.stock eq 0 ? 10 : product.stock}">
    </div>
    <div class="field">
      <label for="rating">Rating (0 to 5)</label>
      <input id="rating" name="rating" type="number" min="0" max="5" step="0.1" required value="${product.rating}">
    </div>
    <div class="field span-2">
      <label for="description">Description</label>
      <textarea id="description" name="description"><c:out value="${product.description}"/></textarea>
    </div>
    <div class="field span-2">
      <label for="imageUrl">Image link (optional)</label>
      <input id="imageUrl" name="imageUrl" maxlength="255" placeholder="https://..." value="<c:out value='${product.imageUrl}'/>">
      <span class="hint">Leave empty to show the icon below instead of a photo</span>
    </div>
    <div class="field span-2">
      <span class="field-label" id="icon-label"><strong>Icon</strong></span>
      <div class="icon-picker" role="radiogroup" aria-labelledby="icon-label">
        <c:forEach var="i" items="${icons}">
          <label class="icon-opt" title="<c:out value='${i}'/>">
            <input type="radio" name="icon" value="<c:out value='${i}'/>" ${i eq product.icon ? 'checked' : ''}>
            <span class="ico ico-<c:out value='${i}'/>" aria-hidden="true"></span>
            <span class="sr-only"><c:out value="${i}"/></span>
          </label>
        </c:forEach>
      </div>
    </div>
  </div>

  <div class="actions" style="justify-content:flex-start">
    <button class="btn btn-primary btn-lg" type="submit">Save product</button>
    <a class="btn btn-ghost btn-lg" href="${ctx}/admin/products">Cancel</a>
  </div>
</form>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
