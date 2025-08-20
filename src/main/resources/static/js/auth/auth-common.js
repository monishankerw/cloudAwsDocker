// Common authentication utilities and configurations
const AUTH_API_BASE_URL = '/api/v1/auth';
const USERS_API_BASE_URL = '/api/v1/users';

/**
 * Logs messages to console with timestamp and type
 * @param {string} message - The message to log
 * @param {string} type - The type of log (info, error, warn, debug)
 * @param {Object} [data] - Additional data to log
 */
function logAuth(message, type = 'info', data = null) {
    const timestamp = new Date().toISOString();
    const logMessage = `[${timestamp}] [AUTH:${type.toUpperCase()}] ${message}`;
    
    // Log to console
    switch(type.toLowerCase()) {
        case 'error':
            console.error(logMessage, data || '');
            break;
        case 'warn':
            console.warn(logMessage, data || '');
            break;
        case 'debug':
            console.debug(logMessage, data || '');
            break;
        default:
            console.log(logMessage, data || '');
    }
    
    // Optionally send logs to server in production
    if (process.env.NODE_ENV === 'production') {
        // TODO: Implement server-side logging
    }
}

/**
 * Shows a toast notification
 * @param {string} title - The title of the toast
 * @param {string} message - The message to display
 * @param {string} type - The type of toast (success, error, warning, info)
 */
function showAuthToast(title, message, type = 'info') {
    logAuth(`Toast: ${title} - ${message}`, type);
    window.showToast?.(title, message, type);
}

// Export utilities
window.AuthUtils = {
    logAuth,
    showAuthToast,
    AUTH_API_BASE_URL,
    USERS_API_BASE_URL
};
