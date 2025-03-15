import { MAX_STRING_LENGTH } from "@/config/constants";

export function validatePassword(password) {
    const minLength = 6;
    const hasLetter = /[a-zA-Z]/.test(password);
    const hasNumber = /\d/.test(password);
    return password.length >= minLength && hasLetter && hasNumber;
}

export function validateUsername(username) {
    const regex = /^\w+$/;
    return regex.test(username);
}

export function validateInput(type, value) {
    if (!value.trim()) {
        return `${type}不能为空！`;
    }
    if (value.length > MAX_STRING_LENGTH) {
        return `${type}过长！`;
    }
    if (type === "用户名" && !validateUsername(value)) {
        return "用户名只能包含字母、数字和下划线！";
    }
    if (type === "密码" && !validatePassword(value)) {
        return "密码至少6位，且需包含字母和数字！";
    }
    return ""; // 没有错误
}
