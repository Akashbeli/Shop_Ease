(function () {
  'use strict';

  var toast = document.getElementById('toast');
  var timer;

  function showToast(message, type) {
    if (!toast || !message) { return; }
    toast.textContent = message;
    toast.className = 'toast show ' + (type || 'success');
    clearTimeout(timer);
    timer = setTimeout(function () { toast.className = 'toast'; }, 3400);
  }

  // Message left by the server on the previous request (flash message)
  if (toast && toast.getAttribute('data-msg')) {
    showToast(toast.getAttribute('data-msg'), toast.getAttribute('data-type'));
  }

  function setCartCount(count) {
    var badge = document.getElementById('cartCount');
    if (!badge) { return; }
    badge.textContent = count;
    badge.hidden = !(count > 0);
    badge.classList.remove('bump');
    void badge.offsetWidth;
    badge.classList.add('bump');
  }

  // "Add" buttons: send the form with fetch() so the page does not reload.
  // Without JavaScript the same form still works as a normal POST.
  document.querySelectorAll('form.js-add').forEach(function (form) {
    form.addEventListener('submit', function (event) {
      if (!window.fetch || !window.URLSearchParams) { return; }
      event.preventDefault();
      var button = form.querySelector('button[type="submit"]');
      if (button) { button.disabled = true; }
      fetch(form.action, {
        method: 'POST',
        credentials: 'same-origin',
        headers: { 'X-Requested-With': 'fetch', 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(new FormData(form)).toString()
      })
        .then(function (response) { return response.json(); })
        .then(function (data) {
          showToast(data.message, data.ok ? 'success' : 'error');
          setCartCount(data.count);
        })
        .catch(function () { form.submit(); })
        .then(function () { if (button) { button.disabled = false; } });
    });
  });

  // <select data-autosubmit> sends its form as soon as the choice changes
  document.querySelectorAll('select[data-autosubmit]').forEach(function (select) {
    select.addEventListener('change', function () { select.form.submit(); });
  });

  // <form data-confirm="Are you sure?"> asks before submitting
  document.querySelectorAll('form[data-confirm]').forEach(function (form) {
    form.addEventListener('submit', function (event) {
      if (!window.confirm(form.getAttribute('data-confirm'))) { event.preventDefault(); }
    });
  });
})();
