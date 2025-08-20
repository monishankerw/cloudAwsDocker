// Login functionality with detailed logging
import { logAuth, showAuthToast, AUTH_API_BASE_URL } from './auth-common.js';

/**
 * Initialize login functionality
 */
function initLogin() {
    logAuth('Initializing login functionality');
    
    // Login form submission
    $('#loginForm').on('submit', function(e) {
        e.preventDefault();
        logAuth('Login form submitted');
        handleLogin();
    });
    
    // Forgot password link
    $('#forgotPasswordLink').on('click', function(e) {
        e.preventDefault();
        logAuth('Forgot password link clicked');
        // TODO: Implement forgot password flow
        showAuthToast('Coming Soon', 'Forgot password functionality will be available soon.', 'info');
    });
    
    logAuth('Login functionality initialized');
}

/**
 * Handle user login
 */
async function handleLogin() {
    const email = $('#loginEmail').val();
    const password = $('#loginPassword').val();
    
    logAuth('Login attempt', 'info', { email: email });
    
    // Basic validation
    if (!email || !password) {
        logAuth('Login validation failed - missing fields', 'warn');
        showAuthToast('Error', 'Please enter both email and password', 'error');
        return;
    }
    
    try {
        logAuth('Sending login request to server');
        showLoading(true);
        
        const response = await $.ajax({
            url: `${AUTH_API_BASE_URL}/login`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email, password })
        });
        
        logAuth('Login successful', 'info', { email: email });
        
        // Save token to localStorage
        if (response.token) {
            localStorage.setItem('authToken', response.token);
            logAuth('Auth token saved to localStorage');
            
            // Update UI
            updateUIForAuth(true);
            
            // Show success message
            showAuthToast('Success', 'Login successful!', 'success');
            
            // Redirect to dashboard after a short delay
            setTimeout(() => {
                window.location.href = '/dashboard.html';
            }, 1000);
        }
    } catch (error) {
        logAuth('Login failed', 'error', { 
            email: email, 
            status: error.status,
            response: error.responseJSON 
        });
        
        let errorMessage = 'Login failed. Please try again.';
        if (error.status === 401) {
            errorMessage = 'Invalid email or password';
        } else if (error.responseJSON && error.responseJSON.message) {
            errorMessage = error.responseJSON.message;
        }
        
        showAuthToast('Login Failed', errorMessage, 'error');
    } finally {
        showLoading(false);
    }
}

// Initialize when document is ready
$(document).ready(function() {
    logAuth('Login module loaded');
    initLogin();
    
    // Check if user is already logged in
    if (localStorage.getItem('authToken')) {
        logAuth('User already has auth token, redirecting to dashboard');
        window.location.href = '/dashboard.html';
    }
});

// Export functions for testing
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initLogin,
        handleLogin
    };
}
