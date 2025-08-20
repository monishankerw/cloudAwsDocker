// Registration functionality with detailed logging
import { logAuth, showAuthToast, USERS_API_BASE_URL } from './auth-common.js';

// Registration state
const registrationState = {
    currentStep: 1,
    totalSteps: 3,
    formData: {}
};

/**
 * Initialize registration functionality
 */
function initRegistration() {
    logAuth('Initializing registration functionality');
    
    // Registration form submission
    $('#registrationForm').on('submit', function(e) {
        e.preventDefault();
        logAuth('Registration form submitted');
        handleRegistration();
    });
    
    // Step navigation
    $('.next-step').on('click', function() {
        goToStep(registrationState.currentStep + 1);
    });
    
    $('.prev-step').on('click', function() {
        goToStep(registrationState.currentStep - 1);
    });
    
    // OTP verification
    $('#verifyOtpBtn').on('click', handleOtpVerification);
    
    // Resend OTP
    $('#resendOtpBtn').on('click', handleResendOtp);
    
    logAuth('Registration functionality initialized');
}

/**
 * Handle user registration
 */
async function handleRegistration() {
    logAuth('Starting registration process');
    
    // Collect form data
    const formData = {
        username: $('#regUsername').val(),
        email: $('#regEmail').val(),
        mobile: $('#regMobile').val(),
        password: $('#regPassword').val(),
        confirmPassword: $('#regConfirmPassword').val()
    };
    
    logAuth('Registration form data collected', 'debug', { 
        email: formData.email,
        username: formData.username,
        hasMobile: !!formData.mobile
    });
    
    // Basic validation
    if (!formData.username || !formData.email || !formData.password || !formData.confirmPassword) {
        logAuth('Registration validation failed - missing required fields', 'warn');
        showAuthToast('Error', 'Please fill in all required fields', 'error');
        return;
    }
    
    if (formData.password !== formData.confirmPassword) {
        logAuth('Registration validation failed - passwords do not match', 'warn');
        showAuthToast('Error', 'Passwords do not match', 'error');
        return;
    }
    
    try {
        logAuth('Sending registration request to server');
        showLoading(true);
        
        // Remove confirmPassword before sending
        const { confirmPassword, ...registrationData } = formData;
        
        const response = await $.ajax({
            url: `${USERS_API_BASE_URL}/register`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(registrationData)
        });
        
        logAuth('Registration successful, OTP sent', 'info', { 
            email: formData.email,
            hasOtp: !!response.otp
        });
        
        // Save form data for OTP verification
        registrationState.formData = formData;
        
        // Show OTP verification step
        goToStep(2);
        
        // Pre-fill email in OTP form
        $('#otpEmail').val(formData.email);
        
        // Set focus to OTP input
        $('#otpInput').focus();
        
    } catch (error) {
        logAuth('Registration failed', 'error', { 
            email: formData.email,
            status: error.status,
            response: error.responseJSON 
        });
        
        let errorMessage = 'Registration failed. Please try again.';
        if (error.responseJSON && error.responseJSON.message) {
            errorMessage = error.responseJSON.message;
        }
        
        showAuthToast('Registration Failed', errorMessage, 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Handle OTP verification
 */
async function handleOtpVerification() {
    const otp = $('#otpInput').val();
    const email = $('#otpEmail').val();
    
    logAuth('OTP verification attempt', 'info', { email: email });
    
    if (!otp) {
        logAuth('OTP verification failed - no OTP provided', 'warn');
        showAuthToast('Error', 'Please enter the OTP', 'error');
        return;
    }
    
    try {
        logAuth('Verifying OTP with server');
        showLoading(true);
        
        const response = await $.ajax({
            url: `${USERS_API_BASE_URL}/verify/email`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ 
                emailOrMobile: email,
                otp: otp
            })
        });
        
        logAuth('OTP verification successful', 'info', { email: email });
        
        // Show success step
        goToStep(3);
        
        // Auto-redirect to login after delay
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 3000);
        
    } catch (error) {
        logAuth('OTP verification failed', 'error', { 
            email: email,
            status: error.status,
            response: error.responseJSON 
        });
        
        let errorMessage = 'OTP verification failed. Please try again.';
        if (error.status === 400) {
            errorMessage = 'Invalid or expired OTP';
        } else if (error.responseJSON && error.responseJSON.message) {
            errorMessage = error.responseJSON.message;
        }
        
        showAuthToast('Verification Failed', errorMessage, 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Handle resend OTP
 */
async function handleResendOtp() {
    const email = $('#otpEmail').val();
    
    logAuth('Resend OTP requested', 'info', { email: email });
    
    try {
        showLoading(true);
        
        const response = await $.ajax({
            url: `${USERS_API_BASE_URL}/resend/email`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: email })
        });
        
        logAuth('OTP resent successfully', 'info', { email: email });
        showAuthToast('Success', 'New OTP has been sent to your email', 'success');
        
    } catch (error) {
        logAuth('Resend OTP failed', 'error', { 
            email: email,
            status: error.status,
            response: error.responseJSON 
        });
        
        let errorMessage = 'Failed to resend OTP. Please try again.';
        if (error.responseJSON && error.responseJSON.message) {
            errorMessage = error.responseJSON.message;
        }
        
        showAuthToast('Error', errorMessage, 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Navigate to a specific step in the registration process
 * @param {number} step - The step number to navigate to
 */
function goToStep(step) {
    if (step < 1 || step > registrationState.totalSteps) return;
    
    logAuth(`Navigating to registration step ${step}`, 'debug');
    
    // Hide all steps
    $('.registration-step').removeClass('active');
    
    // Show the selected step
    $(`#step${step}`).addClass('active');
    
    // Update navigation buttons
    $('.prev-step').toggle(step > 1);
    $('.next-step').toggle(step < registrationState.totalSteps);
    
    // Update progress
    const progress = ((step - 1) / (registrationState.totalSteps - 1)) * 100;
    $('.progress-bar').css('width', `${progress}%`);
    
    registrationState.currentStep = step;
}

// Initialize when document is ready
$(document).ready(function() {
    logAuth('Registration module loaded');
    initRegistration();
    
    // Initialize step navigation
    goToStep(1);
});

// Export functions for testing
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initRegistration,
        handleRegistration,
        handleOtpVerification,
        handleResendOtp,
        goToStep,
        registrationState
    };
}
