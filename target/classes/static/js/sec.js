          document.querySelector('.reminder-details summary').addEventListener('click', function () {
            const arrow = this.querySelector('.summary-arrow');
            if (this.parentElement.open) {
                arrow.classList.add('open');
            } else {
                arrow.classList.remove('open');
            }
        });

        document.addEventListener('DOMContentLoaded', function() {
            // 為每個下拉菜單添加事件監聽
            document.querySelectorAll('.dropdown-toggle').forEach(function(dropdown) {
                dropdown.addEventListener('click', function(e) {
                    e.preventDefault();
                    var menu = this.nextElementSibling;
                    menu.classList.toggle('show');
                });
            });

            // 為下拉菜單中的每個連結添加事件監聽
            document.querySelectorAll('.dropdown-menu a').forEach(function(link) {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    var targetId = this.getAttribute('href').substring(1);
                    var targetElement = document.getElementById(targetId);
                    targetElement.scrollIntoView({ behavior: 'smooth' });
                });
            });
        });
    
    // script.js
document.addEventListener('DOMContentLoaded', () => {
    const links = document.querySelectorAll('.menu-nav a');

    links.forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();
            const targetId = link.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);

            if (targetElement) {
                targetElement.scrollIntoView({ behavior: 'smooth' });

                // 移除所有鏈接的active類
                links.forEach(link => link.classList.remove('active'));

                // 添加點擊鏈接的active類
                link.classList.add('active');
            }
        });
    });
});

    // script.js
function increaseQuantity(item) {
    const quantityInput = document.getElementById('quantity-' + item);
    let quantity = parseInt(quantityInput.value);
    quantityInput.value = quantity + 1;
}

function decreaseQuantity(item) {
    const quantityInput = document.getElementById('quantity-' + item);
    let quantity = parseInt(quantityInput.value);
    if (quantity > 1) {
        quantityInput.value = quantity - 1;
    }
}

function addToCart(item) {
    const quantityInput = document.getElementById('quantity-' + item);
    const quantity = parseInt(quantityInput.value);
    alert('已加入購物車: ' + item + ' 數量: ' + quantity);
}

function switchCategory(category) {
    const url = new URL(window.location.href);
    url.searchParams.set('category', category);
    window.history.pushState({}, '', url);
    showCategory(category);
}

function showCategory(category) {
    const tabs = document.querySelectorAll('.nav-link');
    tabs.forEach(tab => {
        tab.classList.remove('active');
    });

    const panes = document.querySelectorAll('.tab-pane');
    panes.forEach(pane => {
        pane.classList.remove('show', 'active');
    });

    const activeTab = document.querySelector(`[data-bs-target="#${category}"]`);
    if (activeTab) {
        activeTab.classList.add('active');
    }

    const activePane = document.querySelector(`#${category}`);
    if (activePane) {
        activePane.classList.add('show', 'active');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const category = urlParams.get('category');
    if (category) {
        showCategory(category);
    }  
});

 // 加減按鈕的JavaScript邏輯
    document.addEventListener('DOMContentLoaded', (event) => {
        document.querySelectorAll('.quantity-minus').forEach(button => {
            button.addEventListener('click', function() {
                const input = this.nextElementSibling;
                let value = parseInt(input.value);
                if (value > 0) value--;
                input.value = value;
            });
        });

        document.querySelectorAll('.quantity-plus').forEach(button => {
            button.addEventListener('click', function() {
                const input = this.previousElementSibling;
                let value = parseInt(input.value);
                value++;
                input.value = value;
            });
        });
    });

   
    



/* 假設 isLoggedIn 用於檢查用戶是否已登入
  // 這裡為示例，實際應根據具體情況設置該變量
  var isLoggedIn = false; // 假設未登入，測試時可以改為 true 看效果

  document.addEventListener('DOMContentLoaded', function() {
    if (!isLoggedIn) {
      // 選擇所有 quantity-controls 和 add-to-cart-btn
      var quantityControls = document.querySelectorAll('.quantity-controls');
      var addToCartButtons = document.querySelectorAll('.add-to-cart-btn');

      // 隱藏 quantity-controls
      quantityControls.forEach(function(control) {
        control.style.display = 'none';
      });

      // 隱藏 add-to-cart-btn
      addToCartButtons.forEach(function(button) {
        button.style.display = 'none';
      });
    }
  });

  // 減少數量函數
  function decreaseQuantity(id) {
    var quantityInput = document.getElementById('quantity-' + id);
    var currentValue = parseInt(quantityInput.value);
    if (currentValue > 1) {
      quantityInput.value = currentValue - 1;
    }
  }

  // 增加數量函數
  function increaseQuantity(id) {
    var quantityInput = document.getElementById('quantity-' + id);
    quantityInput.value = parseInt(quantityInput.value) + 1;
  }

  // 加入購物車函數
  function addToCart(productName) {
    // 添加商品到購物車的邏輯
    alert(productName + ' 已加入購物車');
  }}*/