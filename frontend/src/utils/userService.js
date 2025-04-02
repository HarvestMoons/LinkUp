import  * as Constants from "@/config/constants";

export function validateInput(type, value,t) {

    // 获取字段显示名称（如果未配置则使用原始type）
    let fieldName = "";

    switch (type) {
        case Constants.NAME_VALIDATION:
            fieldName = t('common.username')
            if (!/^[a-zA-Z0-9_]+$/.test(value)) {
                return t('validation.username_invalid')
            }
            break;
        case Constants.PW_VALIDATION:
            fieldName = t('common.password')
            if (!/(?=.*[A-Za-z])(?=.*\d).{6,}/.test(value)){
                return t('validation.password_invalid')
            }
            break;
        default:
            return t('error.unknown_parameter',{param:type})
    }
    if (!value.trim()) {
        return t('validation.required', { field: fieldName })
    }
    if (value.length > Constants.MAX_STRING_LENGTH) {
        return t('validation.too_long', { field: fieldName })
    }
    return "" // 验证通过
}