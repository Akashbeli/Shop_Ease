<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/fragments/header.jsp" />

<c:choose>
  <c:when test="${cart.itemCount eq 0}">
    <div class="empty">
      <div class="empty-art"><span class="ico ico-cart" aria-hidden="true"></span></div>
      <h1>Your cart is empty</h1>
      <p>Browse the shelves and add something you like. Delivery is free over &#8377;999.</p>
      <a class="btn btn-primary btn-lg" href="${ctx}/shop">Start shopping</a>
    </div>
  </c:when>

  <c:otherwise>
    <h1 class="page-title">Your cart <small>${cart.itemCount} ${cart.itemCount eq 1 ? 'item' : 'items'}</small></h1>

    <div class="cart-layout">
      <section class="cart-list" aria-label="Items in your cart">
        <c:forEach var="item" items="${cart.items}">
          <c:set var="p" value="${item.product}" />
          <article class="cart-row">
            <a class="thumb ${p.categoryClass} tint-${p.tint}" href="${ctx}/product?id=${p.id}" aria-hidden="true" tabindex="-1">
              <span class="ico ico-<c:out value='${p.icon}'/>"></span>
            </a>
            <div class="cart-info">
              <a class="cart-name" href="${ctx}/product?id=${p.id}"><c:out value="${p.name}"/></a>
              <span class="muted">${p.priceDisplay} each</span>
              <form method="post" action="${ctx}/cart">
                <input type="hidden" name="action" value="remove">
                <input type="hidden" name="productId" value="${p.id}">
                <button class="link-btn danger" type="submit">Remove</button>
              </form>
            </div>
            <form class="stepper" method="post" action="${ctx}/cart">
              <input type="hidden" name="action" value="update">
              <input type="hidden" name="productId" value="${p.id}">
              <button type="submit" name="quantity" value="${item.quantity - 1}" aria-label="Decrease quantity">&minus;</button>
              <span class="qty" aria-live="polite">${item.quantity}</span>
              <button type="submit" name="quantity" value="${item.quantity + 1}" aria-label="Increase quantity">+</button>
            </form>
            <span class="line-total">${item.subtotalDisplay}</span>
          </article>
        </c:forEach>

        <div class="cart-foot">
          <a class="btn btn-ghost" href="${ctx}/shop">Continue shopping</a>
          <form method="post" action="${ctx}/cart" data-confirm="Remove everything from your cart?">
            <input type="hidden" name="action" value="clear">
            <button class="link-btn danger" type="submit">Clear cart</button>
          </form>
        </div>
      </section>

      <aside class="summary" aria-label="Order summary">
        <h2>Order summary</h2>
        <div class="meter">
          <c:choose>
            <c:when test="${cart.freeShipping}"><p><strong>You&rsquo;ve unlocked free delivery.</strong></p></c:when>
            <c:otherwise><p>Add <strong>${cart.amountToFreeShippingDisplay}</strong> more for free delivery.</p></c:otherwise>
          </c:choose>
          <div class="meter-bar"><span style="width: ${cart.freeShippingPercent}%"></span></div>
        </div>
        <dl class="totals">
          <div><dt>Subtotal</dt><dd>${cart.subtotalDisplay}</dd></div>
          <div><dt>Delivery</dt><dd>${cart.shippingDisplay}</dd></div>
          <div class="grand"><dt>Total</dt><dd>${cart.totalDisplay}</dd></div>
        </dl>
        <a class="btn btn-primary btn-lg btn-block" href="${ctx}/checkout">Go to checkout</a>
        <p class="summary-note">You can review everything again before paying.</p>
      </aside>
    </div>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
