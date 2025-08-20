// Global variables
let currentUser = null;
let cart = [];
let wishlist = [];
const API_BASE_URL = '/api/v1';
let toastContainer = null;

// Initialize toast container
function initToastContainer() {
    if (!document.getElementById('toast-container')) {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
        toastContainer = container;
    } else {
        toastContainer = document.getElementById('toast-container');
    }
}

// Show toast notification
function showToast(title, message, type = 'info', duration = 3000) {
    if (!toastContainer) initToastContainer();
    
    const icons = {
        success: 'check-circle',
        error: 'exclamation-circle',
        warning: 'exclamation-triangle',
        info: 'info-circle'
    };
    
    const icon = icons[type] || 'info-circle';
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <i class="fas fa-${icon}"></i>
        <div class="toast-content">
            <div class="toast-title">${title}</div>
            <div class="toast-message">${message}</div>
        </div>
        <button class="toast-close" aria-label="Close">
            <i class="fas fa-times"></i>
        </button>
        <div class="toast-progress"></div>
    `;
    
    const closeButton = toast.querySelector('.toast-close');
    closeButton.addEventListener('click', () => {
        hideToast(toast);
    });
    
    toastContainer.appendChild(toast);
    
    // Force reflow to enable the transition
    void toast.offsetWidth;
    
    // Show the toast with animation
    setTimeout(() => {
        toast.classList.add('show');
    }, 10);
    
    // Auto-hide after duration
    const autoHide = setTimeout(() => {
        hideToast(toast);
    }, duration);
    
    // Pause auto-hide on hover
    toast.addEventListener('mouseenter', () => {
        clearTimeout(autoHide);
        const progress = toast.querySelector('.toast-progress');
        if (progress) {
            progress.style.animationPlayState = 'paused';
        }
    });
    
    toast.addEventListener('mouseleave', () => {
        const remainingTime = (1 - (toast.querySelector('.toast-progress').style.transform === 'scaleX(0)' ? 1 : 
            parseFloat(toast.querySelector('.toast-progress').style.transform.replace('scaleX(', '').replace(')', '')))) * duration;
            
        if (remainingTime > 0) {
            const progress = toast.querySelector('.toast-progress');
            if (progress) {
                progress.style.animationPlayState = 'running';
            }
            
            setTimeout(() => {
                hideToast(toast);
            }, remainingTime);
        }
    });
    
    return toast;
}

// Hide toast with animation
function hideToast(toast) {
    if (!toast) return;
    
    toast.style.transition = 'transform 0.3s ease-in-out, opacity 0.3s';
    toast.style.transform = 'translateX(120%)';
    toast.style.opacity = '0';
    
    // Remove from DOM after animation
    setTimeout(() => {
        if (toast.parentNode === toastContainer) {
            toastContainer.removeChild(toast);
        }
    }, 300);
}

// Initialize toast container when DOM is ready
$(document).ready(function() {
    initToastContainer();
});

// Sample product data
const products = [
    {
        id: 1,
        name: 'Wireless Headphones',
        price: 99.99,
        originalPrice: 149.99,
        image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',
        category: 'Electronics',
        rating: 4.5,
        reviews: 128,
        isNew: true,
        isOnSale: true,
        stock: 15
    },
    {
        id: 2,
        name: 'Smart Watch Pro',
        price: 199.99,
        originalPrice: 249.99,
        image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',
        category: 'Electronics',
        rating: 4.8,
        reviews: 256,
        isNew: true,
        isOnSale: false,
        stock: 8
    },
    {
        id: 3,
        name: 'Running Shoes',
        price: 89.99,
        originalPrice: 129.99,
        image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',
        category: 'Fashion',
        rating: 4.7,
        reviews: 342,
        isNew: false,
        isOnSale: true,
        stock: 0
    },
    {
        id: 4,
        name: 'Bluetooth Speaker',
        price: 79.99,
        originalPrice: 99.99,
        image: 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',
        category: 'Electronics',
        rating: 4.3,
        reviews: 87,
        isNew: true,
        isOnSale: false,
        stock: 12
    },
    {
        id: 5,
        name: 'Designer Handbag',
        price: 149.99,
        originalPrice: 199.99,
        image: 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',
        category: 'Fashion',
        rating: 4.6,
        reviews: 214,
        isNew: false,
        isOnSale: true,
        stock: 5
    },
    {
        id: 6,
        name: 'Smartphone X',
        price: 699.99,
        originalPrice: 799.99,
        image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80',
        category: 'Electronics',
        rating: 4.9,
        reviews: 512,
        isNew: true,
        isOnSale: false,
        stock: 20
    }
];

// Function to render products
function renderProducts(products) {
    const productsContainer = document.getElementById('featuredProducts');
    if (!productsContainer) return;
    
    productsContainer.innerHTML = products.map(product => `
        <div class="col-md-4 col-lg-3 mb-4">
            <div class="card product-card h-100">
                <div class="position-relative">
                    <img src="${product.image}" class="card-img-top" alt="${product.name}">
                    ${product.isNew ? '<span class="badge bg-primary position-absolute top-0 start-0 m-2">New</span>' : ''}
                    ${product.isOnSale ? '<span class="badge bg-danger position-absolute top-0 end-0 m-2">Sale</span>' : ''}
                    ${product.stock === 0 ? '<div class="sold-out-overlay"><span>Sold Out</span></div>' : ''}
                    <div class="product-actions">
                        <button class="btn btn-sm btn-light rounded-circle me-2" title="Add to Wishlist">
                            <i class="far fa-heart"></i>
                        </button>
                        <button class="btn btn-sm btn-primary rounded-circle me-2" title="Quick View">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-sm btn-success rounded-circle" title="Add to Cart" ${product.stock === 0 ? 'disabled' : ''}>
                            <i class="fas fa-shopping-cart"></i>
                        </button>
                    </div>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <span class="text-muted small">${product.category}</span>
                        <div class="rating">
                            ${Array(5).fill('').map((_, i) => 
                                `<i class="fas fa-star${i < Math.floor(product.rating) ? ' text-warning' : '-o'}"></i>`
                            ).join('')}
                            <small class="ms-1">(${product.reviews})</small>
                        </div>
                    </div>
                    <h5 class="card-title mb-1">${product.name}</h5>
                    <div class="d-flex align-items-center">
                        <span class="h5 mb-0 text-primary">$${product.price.toFixed(2)}</span>
                        ${product.originalPrice > product.price ? 
                            `<small class="text-muted text-decoration-line-through ms-2">$${product.originalPrice.toFixed(2)}</small>` : ''}
                    </div>
                    <div class="mt-2">
                        ${product.stock > 0 ? 
                            `<span class="text-success small"><i class="fas fa-check-circle"></i> In Stock (${product.stock})</span>` :
                            '<span class="text-danger small"><i class="fas fa-times-circle"></i> Out of Stock</span>'
                        }
                    </div>
                </div>
                <div class="card-footer bg-transparent border-top-0">
                    <button class="btn btn-outline-primary w-100" ${product.stock === 0 ? 'disabled' : ''}>
                        <i class="fas fa-shopping-cart me-2"></i>Add to Cart
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// Load cart and wishlist from localStorage
function loadData() {
    const savedCart = localStorage.getItem('cart');
    const savedWishlist = localStorage.getItem('wishlist');
    
    if (savedCart) cart = JSON.parse(savedCart);
    if (savedWishlist) wishlist = JSON.parse(savedWishlist);
    
    updateCartCount();
    updateWishlistCount();
}

// Save cart to localStorage
function saveCart() {
    localStorage.setItem('cart', JSON.stringify(cart));
    updateCartCount();
}

// Update cart count in the UI
function updateCartCount() {
    const count = cart.reduce((total, item) => total + (item.quantity || 1), 0);
    $('.cart-count').text(count).toggle(count > 0);
}

// Update wishlist count in the UI
function updateWishlistCount() {
    $('.wishlist-count').text(wishlist.length).toggle(wishlist.length > 0);
}

// Render product card
function renderProductCard(product) {
    const isInWishlist = wishlist.some(item => item.id === product.id);
    const discount = product.oldPrice ? 
        Math.round(((product.oldPrice - product.price) / product.oldPrice) * 100) : 0;
    
    return `
        <div class="product-card" data-id="${product.id}">
            <div class="product-image position-relative">
                <img src="${product.image}" alt="${product.name}" class="img-fluid">
                
                ${!product.inStock ? 
                    '<span class="product-badge badge-out-of-stock">Out of Stock</span>' : 
                    (product.isNew ? '<span class="product-badge badge-new">New</span>' : '')}
                
                ${discount > 0 ? `<span class="product-badge badge-sale">-${discount}%</span>` : ''}
                
                <div class="product-actions">
                    <button class="action-btn quick-view" data-id="${product.id}" title="Quick View">
                        <i class="far fa-eye"></i>
                    </button>
                    <button class="action-btn add-to-cart" data-id="${product.id}" title="Add to Cart" ${!product.inStock ? 'disabled' : ''}>
                        <i class="fas fa-shopping-cart"></i>
                    </button>
                    <button class="action-btn wishlist ${isInWishlist ? 'active' : ''}" data-id="${product.id}" title="${isInWishlist ? 'Remove from Wishlist' : 'Add to Wishlist'}">
                        <i class="${isInWishlist ? 'fas' : 'far'} fa-heart"></i>
                    </button>
                </div>
                
                <button class="quick-view-btn">
                    <i class="far fa-eye"></i> Quick View
                </button>
            </div>
            
            <div class="product-info">
                <div class="product-category">${product.category}</div>
                <h3 class="product-title">${product.name}</h3>
                
                <div class="product-rating">
                    <span class="rating-stars">
                        ${'<i class="fas fa-star"></i>'.repeat(Math.floor(product.rating))}
                        ${product.rating % 1 >= 0.5 ? '<i class="fas fa-star-half-alt"></i>' : ''}
                        ${'<i class="far fa-star"></i>'.repeat(5 - Math.ceil(product.rating))}
                    </span>
                    <span class="rating-count">(${product.reviews})</span>
                </div>
                
                <div class="product-price mt-auto">
                    <div>
                        <span class="price">$${product.price.toFixed(2)}</span>
                        ${product.oldPrice ? `<span class="old-price">$${product.oldPrice.toFixed(2)}</span>` : ''}
                    </div>
                    <button class="add-to-cart-btn" data-id="${product.id}" ${!product.inStock ? 'disabled' : ''}>
                        <i class="fas fa-plus"></i>
                    </button>
                </div>
            </div>
        </div>
    `;
}

// Add to cart function
function addToCart(productId, quantity = 1) {
    const product = products.find(p => p.id === productId);
    if (!product || !product.stock) return;
    
    const existingItem = cart.find(item => item.id === productId);
    
    if (existingItem) {
        existingItem.quantity += quantity;
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            image: product.image,
            quantity: quantity
        });
    }
    
    saveCart();
    showToast('Success', `${product.name} added to cart!`, 'success');
}

// Toggle wishlist
function toggleWishlist(productId) {
    const product = products.find(p => p.id === productId);
    if (!product) return;
    
    const existingIndex = wishlist.findIndex(item => item.id === productId);
    
    if (existingIndex > -1) {
        wishlist.splice(existingIndex, 1);
        showToast('Removed', `${product.name} removed from wishlist`, 'info');
    } else {
        wishlist.push({
            id: product.id,
            name: product.name,
            price: product.price,
            image: product.image
        });
        showToast('Added', `${product.name} added to wishlist`, 'success');
    }
    
    localStorage.setItem('wishlist', JSON.stringify(wishlist));
    updateWishlistCount();
}

// Initialize the application
function initializeApp() {
    loadData();
    renderHomePage();
}

// Render home page
function renderHomePage() {
    const $featuredProducts = $('#featuredProducts');
    if ($featuredProducts.length) {
        const featuredHtml = products.map(renderProductCard).join('');
        $featuredProducts.html(featuredHtml);
    }
}

// Set up event listeners
function setupEventListeners() {
    // Add to cart button
    $(document).on('click', '.add-to-cart, .add-to-cart-btn', function(e) {
        e.preventDefault();
        const productId = parseInt($(this).data('id'));
        addToCart(productId);
    });
    
    // Wishlist button
    $(document).on('click', '.wishlist', function(e) {
        e.preventDefault();
        const productId = parseInt($(this).data('id'));
        toggleWishlist(productId);
        $(this).toggleClass('active')
               .find('i')
               .toggleClass('far fas')
               .attr('title', $(this).hasClass('active') ? 'Remove from Wishlist' : 'Add to Wishlist');
    });
    
    // Quick view button
    $(document).on('click', '.quick-view, .quick-view-btn', function(e) {
        e.preventDefault();
        const productId = parseInt($(this).data('id'));
        // Implement quick view modal
        console.log('Quick view:', productId);
    });
}

// Document ready
$(document).ready(function() {
    renderProducts(products);
    initializeApp();
    setupEventListeners();
    checkAuthStatus();
});

// Initialize application
function init() {
    // Render featured products
    renderProducts(products);
    
    // Add event listeners
    document.addEventListener('click', function(e) {
        // Add to cart button click
        if (e.target.closest('.btn-success')) {
            const productCard = e.target.closest('.product-card');
            const productId = productCard ? parseInt(productCard.dataset.id) : null;
            if (productId) {
                addToCart(productId);
            }
        }
        
        // Wishlist button click
        if (e.target.closest('.btn-light')) {
            const productCard = e.target.closest('.product-card');
            const productId = productCard ? parseInt(productCard.dataset.id) : null;
            if (productId) {
                toggleWishlist(productId);
            }
        }
    });
    
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    });
}

// Setup event listeners
function setupEventListeners() {
    // Login form submission
    $('#loginForm').on('submit', function(e) {
        e.preventDefault();
        handleLogin();
    });

    // Register form submission
    $('#registerForm').on('submit', function(e) {
        e.preventDefault();
        handleRegistration();
    });

    // Logout button
    $('#logoutLink').on('click', function(e) {
        e.preventDefault();
        handleLogout();
    });

    // Navigation links
    $('a[data-page]').on('click', function(e) {
        e.preventDefault();
        const page = $(this).data('page');
        loadPage(page);
    });

    // Close modals on hidden
    $('.modal').on('hidden.bs.modal', function() {
        $(this).find('form')[0].reset();
        $(this).find('.alert').addClass('d-none').text('');
    });
}

// Check authentication status
function checkAuthStatus() {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        // Validate token on server
        $.ajax({
            url: `${API_BASE_URL}/auth/me`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(user) {
                currentUser = user;
                updateUIForAuth(true);
                loadPage('dashboard');
            },
            error: function() {
                localStorage.removeItem('jwtToken');
                updateUIForAuth(false);
            }
        });
    } else {
        updateUIForAuth(false);
    }
}

// Handle user login
function handleLogin() {
    const email = $('#loginEmail').val();
    const password = $('#loginPassword').val();
    const $alert = $('#loginAlert');

    showLoading(true);
    
    $.ajax({
        url: `${API_BASE_URL}/auth/login`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ email, password }),
        success: function(response) {
            localStorage.setItem('jwtToken', response.token);
            currentUser = response.user;
            updateUIForAuth(true);
            $('#loginModal').modal('hide');
            showToast('Success', 'Logged in successfully!', 'success');
            loadPage('dashboard');
function handleRegistration() {
    const $form = $('#registerForm');
    const $alert = $('#registerAlert');
    let registrationData = null;
    
    // Function to show OTP verification modal
    function showOtpVerificationModal(email, mobile) {
        const modalHtml = `
            <div class="modal fade" id="otpVerificationModal" tabindex="-1">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Verify Your Account</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div id="otpAlert" class="alert d-none"></div>
                            <p>We've sent verification codes to your email and mobile number.</p>
                            
                            <div class="mb-3">
                                <label class="form-label">Email Verification Code</label>
                                <input type="text" class="form-control" id="emailOtp" placeholder="Enter 6-digit code" maxlength="6" pattern="\d{6}" required>
                                <div class="form-text">Check your email for the verification code</div>
                                <button type="button" class="btn btn-link btn-sm p-0 mt-1" id="resendEmailOtp">Resend Email OTP</button>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Mobile Verification Code</label>
                                <input type="text" class="form-control" id="mobileOtp" placeholder="Enter 6-digit code" maxlength="6" pattern="\d{6}" required>
                                <div class="form-text">Check your mobile for the verification code</div>
                                <button type="button" class="btn btn-link btn-sm p-0 mt-1" id="resendMobileOtp">Resend SMS OTP</button>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary" id="verifyOtpBtn">Verify & Complete Registration</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        // Remove existing modal if any
        $('#otpVerificationModal').remove();
        $('body').append(modalHtml);
        
        const $otpModal = $('#otpVerificationModal');
        const $verifyBtn = $('#verifyOtpBtn');
        const $otpAlert = $('#otpAlert');
        
        // Handle OTP verification
        $verifyBtn.off('click').on('click', function() {
            const emailOtp = $('#emailOtp').val();
            const mobileOtp = $('#mobileOtp').val();
            
            if (!emailOtp || !mobileOtp) {
                showOtpAlert('Please enter both verification codes', 'danger');
                return;
            }
            
            // Verify email first
            verifyOtp(registrationData.email, emailOtp, true)
                .then(() => {
                    // Then verify mobile
                    return verifyOtp(registrationData.mobile, mobileOtp, false);
                })
                .then(() => {
                    showOtpAlert('Verification successful! Your account is now active.', 'success');
                    setTimeout(() => {
                        $otpModal.modal('hide');
                        showToast('Success', 'Registration completed successfully! You can now log in.', 'success');
                    }, 1500);
                })
                .catch(error => {
                    showOtpAlert(error, 'danger');
                });
        });
        
        // Handle resend email OTP
        $('#resendEmailOtp').off('click').on('click', function() {
            resendOtp(registrationData.email, true);
        });
        
        // Handle resend mobile OTP
        $('#resendMobileOtp').off('click').on('click', function() {
            resendOtp(registrationData.mobile, false);
        });
        
        // Show the modal
        $otpModal.modal('show');
        
        // Function to show alert in OTP modal
        function showOtpAlert(message, type) {
            $otpAlert.removeClass('d-none alert-danger alert-success')
                     .addClass(`alert-${type}`)
                     .text(message);
        }
    }
    
    // Function to verify OTP
    function verifyOtp(identifier, otp, isEmail) {
        return new Promise((resolve, reject) => {
            $.ajax({
                url: `${API_BASE_URL}/users/verify/${isEmail ? 'email' : 'mobile'}`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    emailOrMobile: identifier,
                    otp: otp
                }),
                success: function(response) {
                    resolve(response);
                },
                error: function(xhr) {
                    const error = xhr.responseJSON?.message || 'Verification failed. Please try again.';
                    reject(error);
                }
            });
        });
    }
    
    // Function to resend OTP
    function resendOtp(identifier, isEmail) {
        $.ajax({
            url: `${API_BASE_URL}/users/resend/${isEmail ? 'email' : 'mobile'}-verification/${encodeURIComponent(identifier)}`,
            type: 'POST',
            success: function(response) {
                showToast('Success', `Verification code has been resent to your ${isEmail ? 'email' : 'mobile'}`, 'success');
            },
            error: function(xhr) {
                const error = xhr.responseJSON?.message || 'Failed to resend verification code. Please try again.';
                showOtpAlert(error, 'danger');
            }
        });
    }
    
    // Handle registration form submission
    $form.on('submit', function(e) {
        e.preventDefault();
        
        registrationData = {
            username: $('#username').val(),
            email: $('#registerEmail').val(),
            mobile: $('#registerMobile').val(),
            password: $('#registerPassword').val(),
            confirmPassword: $('#confirmPassword').val()
        };
        
        // Basic validation
        if (registrationData.password !== registrationData.confirmPassword) {
            showAlert($alert, 'Passwords do not match');
            return;
        }
        
        if (!/^\+?[1-9]\d{9,14}$/.test(registrationData.mobile)) {
            showAlert($alert, 'Please enter a valid mobile number with country code');
            return;
        }
        
        // Show loading state
        const $submitBtn = $form.find('button[type="submit"]');
        const originalBtnText = $submitBtn.html();
        $submitBtn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Registering...');
        
        // Call registration API
        $.ajax({
            url: `${API_BASE_URL}/users/register`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                username: registrationData.username,
                email: registrationData.email,
                mobile: registrationData.mobile,
                password: registrationData.password
            }),
            success: function(response) {
                // Hide the registration modal
                $form[0].reset();
                $('#registerModal').modal('hide');
                
                // Show OTP verification modal
                showOtpVerificationModal(registrationData.email, registrationData.mobile);
                
                showToast('Success', 'Registration successful! Please verify your email and mobile number.', 'success');
            },
            error: function(xhr) {
                const error = xhr.responseJSON?.message || 'Registration failed. Please try again.';
                showAlert($alert, error);
            },
            complete: function() {
                $submitBtn.prop('disabled', false).html(originalBtnText);
            }
        });
    });
}

// Handle user logout
function handleLogout() {
    localStorage.removeItem('jwtToken');
    currentUser = null;
    updateUIForAuth(false);
    showToast('Success', 'Logged out successfully!', 'info');
    loadPage('home');
}

// Load page content
function loadPage(page) {
    showLoading(true);
    
    // If not authenticated and trying to access protected pages, redirect to home
    if (!currentUser && page !== 'home') {
        page = 'home';
    }

    let url = `/pages/${page}.html`;
    const $mainContent = $('#mainContent');

    // For demo purposes, we'll use client-side templates
    // In a real app, you would load HTML templates from the server
    switch(page) {
        case 'dashboard':
            renderDashboard($mainContent);
            break;
        case 'users':
            renderUsersPage($mainContent);
            break;
        case 'profile':
            renderProfilePage($mainContent);
            break;
        default:
            renderHomePage($mainContent);
    }

    // Update active nav item
    $('.nav-link').removeClass('active');
    $(`a[data-page="${page}"]`).addClass('active');
    
    showLoading(false);
}

// Render dashboard page
function renderDashboard($container) {
    const html = `
        <div class="row mb-4">
            <div class="col-12">
                <h2><i class="fas fa-tachometer-alt me-2"></i>Dashboard</h2>
                <p class="text-muted">Welcome back, ${currentUser.firstName}!</p>
            </div>
        </div>
        <div class="row">
            <div class="col-md-4">
                <div class="stat-card bg-primary">
                    <i class="fas fa-users"></i>
                    <span class="stat-value" id="totalUsers">0</span>
                    <span class="stat-label">Total Users</span>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card bg-success">
                    <i class="fas fa-chart-line"></i>
                    <span class="stat-value" id="activeSessions">0</span>
                    <span class="stat-label">Active Sessions</span>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card bg-info">
                    <i class="fas fa-tasks"></i>
                    <span class="stat-value" id="completedTasks">0</span>
                    <span class="stat-label">Completed Tasks</span>
                </div>
            </div>
        </div>
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">Recent Activity</h5>
                    </div>
                    <div class="card-body">
                        <p>No recent activity to display.</p>
                    </div>
                </div>
            </div>
        </div>
    `;
    $container.html(html);
    
    // Load dashboard data
    loadDashboardData();
}

// Load dashboard data via AJAX
function loadDashboardData() {
    // Simulate API call
    setTimeout(() => {
        $('#totalUsers').text('42');
        $('#activeSessions').text('12');
        $('#completedTasks').text('128');
    }, 500);
}

// Render users page
function renderUsersPage($container) {
    const html = `
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2><i class="fas fa-users me-2"></i>Users</h2>
            <button class="btn btn-primary" id="addUserBtn">
                <i class="fas fa-plus me-1"></i> Add User
            </button>
        </div>
        <div class="card">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="usersTableBody">
                            <tr>
                                <td colspan="6" class="text-center">Loading users...</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `;
    $container.html(html);
    
    // Load users data
    loadUsersData();
    
    // Add click handler for add user button
    $('#addUserBtn').on('click', function() {
        // Open add user modal or redirect to add user page
        showToast('Info', 'Add user functionality coming soon!', 'info');
    });
}

// Load users data via AJAX
function loadUsersData() {
    // In a real app, this would be an actual API call
    setTimeout(() => {
        const users = [
            { id: 1, firstName: 'John', lastName: 'Doe', email: 'john@example.com', role: 'Admin', active: true },
            { id: 2, firstName: 'Jane', lastName: 'Smith', email: 'jane@example.com', role: 'User', active: true },
            { id: 3, firstName: 'Bob', lastName: 'Johnson', email: 'bob@example.com', role: 'User', active: false }
        ];
        
        const $tableBody = $('#usersTableBody');
        if (users.length === 0) {
            $tableBody.html('<tr><td colspan="6" class="text-center">No users found.</td></tr>');
            return;
        }
        
        const rows = users.map(user => `
            <tr>
                <td>${user.id}</td>
                <td>${user.firstName} ${user.lastName}</td>
                <td>${user.email}</td>
                <td><span class="badge bg-info">${user.role}</span></td>
                <td>
                    <span class="badge ${user.active ? 'bg-success' : 'bg-secondary'}">
                        ${user.active ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-outline-primary me-1 edit-user" data-id="${user.id}">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger delete-user" data-id="${user.id}">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
        
        $tableBody.html(rows);
        
        // Add click handlers for action buttons
        $('.edit-user').on('click', function() {
            const userId = $(this).data('id');
            // Handle edit user
            showToast('Info', `Edit user ${userId} - Coming soon!`, 'info');
        });
        
        $('.delete-user').on('click', function() {
            const userId = $(this).data('id');
            if (confirm('Are you sure you want to delete this user?')) {
                // Handle delete user
                showToast('Success', `User ${userId} deleted!`, 'success');
                // In a real app, you would make an API call to delete the user
                // and then refresh the users list
                loadUsersData();
            }
        });
    }, 500);
}

// Render profile page
function renderProfilePage($container) {
    if (!currentUser) {
        renderHomePage($container);
        return;
    }
    
    const html = `
        <div class="row">
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body text-center">
                        <div class="mb-3">
                            <div class="avatar-circle" style="width: 120px; height: 120px; margin: 0 auto 20px; border-radius: 50%; background-color: #0d6efd; display: flex; align-items: center; justify-content: center; color: white; font-size: 48px; font-weight: bold;">
                                ${currentUser.firstName.charAt(0)}${currentUser.lastName ? currentUser.lastName.charAt(0) : ''}
                            </div>
                            <h4>${currentUser.firstName} ${currentUser.lastName || ''}</h4>
                            <p class="text-muted">${currentUser.email}</p>
                        </div>
                        <button class="btn btn-outline-primary btn-sm w-100 mb-2">
                            <i class="fas fa-edit me-1"></i> Edit Profile
                        </button>
                        <button class="btn btn-outline-secondary btn-sm w-100">
                            <i class="fas fa-lock me-1"></i> Change Password
                        </button>
                    </div>
                </div>
            </div>
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">Account Information</h5>
                    </div>
                    <div class="card-body">
                        <form id="profileForm">
                            <div class="row mb-3">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">First Name</label>
                                    <input type="text" class="form-control" value="${currentUser.firstName}" disabled>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Last Name</label>
                                    <input type="text" class="form-control" value="${currentUser.lastName || ''}" disabled>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input type="email" class="form-control" value="${currentUser.email}" disabled>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Member Since</label>
                                <input type="text" class="form-control" value="${new Date().toLocaleDateString()}" disabled>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    `;
    $container.html(html);
}

// Render home page
function renderHomePage($container) {
    const html = `
        <div class="text-center py-5">
            <h1>Welcome to Cloud AWS Docker</h1>
            <p class="lead">A modern web application built with Spring Boot and jQuery</p>
            <div class="mt-4">
                ${!currentUser ? `
                    <button class="btn btn-primary btn-lg me-3" data-bs-toggle="modal" data-bs-target="#loginModal">
                        <i class="fas fa-sign-in-alt me-2"></i>Login
                    </button>
                    <button class="btn btn-outline-primary btn-lg" data-bs-toggle="modal" data-bs-target="#registerModal">
                        <i class="fas fa-user-plus me-2"></i>Register
                    </button>
                ` : `
                    <a href="#" class="btn btn-primary btn-lg" data-page="dashboard">
                        <i class="fas fa-tachometer-alt me-2"></i>Go to Dashboard
                    </a>
                `}
            </div>
        </div>
        <div class="row mt-5">
            <div class="col-md-4 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-bolt text-primary mb-3" style="font-size: 2.5rem;"></i>
                        <h4>Fast & Efficient</h4>
                        <p class="text-muted">Built with performance in mind to handle your workload efficiently.</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-shield-alt text-success mb-3" style="font-size: 2.5rem;"></i>
                        <h4>Secure</h4>
                        <p class="text-muted">Enterprise-grade security to keep your data safe and protected.</p>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mb-4">
                <div class="card h-100">
                    <div class="card-body text-center">
                        <i class="fas fa-mobile-alt text-info mb-3" style="font-size: 2.5rem;"></i>
                        <h4>Responsive</h4>
                        <p class="text-muted">Works seamlessly on all devices, from desktop to mobile.</p>
                    </div>
                </div>
            </div>
        </div>
    `;
    $container.html(html);
}

// Update UI based on authentication status
function updateUIForAuth(isAuthenticated) {
    if (isAuthenticated) {
        $('#userSection').hide();
        $('#userProfile').show();
        $('#usernameDisplay').text(currentUser.firstName);
        
        // Update profile link
        $('#profileLink').on('click', function(e) {
            e.preventDefault();
            loadPage('profile');
        });
    } else {
        $('#userSection').show();
        $('#userProfile').hide();
        
        // If not on home page, redirect to home
        if (window.location.pathname !== '/') {
            loadPage('home');
        }
    }
}

// Show loading spinner
function showLoading(show) {
    if (show) {
        $('#loadingSpinner').show();
    } else {
        $('#loadingSpinner').hide();
    }
}

// Show alert in form
function showAlert($alert, message) {
    $alert.removeClass('d-none').text(message);
    $alert[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// Show toast notification
function showToast(title, message, type = 'info') {
    const $toast = $('#toast');
    const $toastTitle = $('#toastTitle');
    const $toastMessage = $('#toastMessage');
    
    // Set toast type class
    $toast.removeClass('bg-primary bg-success bg-danger bg-warning bg-info')
           .addClass(`bg-${type}`);
    
    $toastTitle.text(title);
    $toastMessage.html(message);
    
    // Show the toast
    const toast = new bootstrap.Toast($toast[0], { autohide: true, delay: 5000 });
    toast.show();
}

// Helper function to format date
function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString(undefined, options);
}

// Helper function to handle API errors
function handleApiError(xhr) {
    let errorMessage = 'An error occurred. Please try again.';
    
    if (xhr.status === 401) {
        errorMessage = 'Your session has expired. Please login again.';
        handleLogout();
    } else if (xhr.responseJSON && xhr.responseJSON.message) {
        errorMessage = xhr.responseJSON.message;
    }
    
    showToast('Error', errorMessage, 'danger');
    return errorMessage;
}
