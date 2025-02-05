export const showToast = (toast, message, type = 'success', timeout = 3000) => {
    switch (type) {
        case 'success':
            toast.success(message, {
                position: "top-right",
                timeout: timeout,
                theme: "colored",
                closeButton: true,
                icon: "✅"
            })
            break
        case 'error':
            toast.error(message, {
                position: "top-right",
                timeout: timeout,
                theme: "colored",
                closeButton: true,
                icon: "❌"
            })
            break
        case 'info':
            toast.info(message, {
                position: "top-right",
                timeout: timeout,
                theme: "colored",
                closeButton: true,
                icon: "ℹ️"
            })
            break
        case 'warning':
            toast.warning(message, {
                position: "top-right",
                timeout: timeout,
                theme: "colored",
                closeButton: true,
                icon: "⚠️"
            })
            break
        default:
            toast(message, { timeout })
            break
    }
}