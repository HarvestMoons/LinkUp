let shownMessages = new Set();

export const showToast = (toast, message, type = 'success', timeout = 3000) => {
    // Check if the message has already been shown
    if (shownMessages.has(message)) return;

    // Mark the message as shown and set a timeout to remove it from the set
    shownMessages.add(message);
    setTimeout(() => shownMessages.delete(message), timeout);

    // Map of icons for each toast type
    const icons = {
        success: "✅",
        error: "❌",
        info: "ℹ️",
        warning: "⚠️"
    };

    // Display the toast based on the type and default icon
    toast[type] ? toast[type](message, {
        position: "top-right",
        timeout,
        theme: "colored",
        closeButton: true,
        icon: icons[type] || icons.success  // Fallback to success icon if type is invalid
    }) : toast(message, { timeout });
};
