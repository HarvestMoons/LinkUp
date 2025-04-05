import  * as Constants from "@/config/constants";
import {t} from "@/i18n";

export function validateInput(type, value) {
    let fieldName = "";

    switch (type) {
        case Constants.USER_NAME_VALIDATION:
            fieldName = t('common.username')
            if (!/^[a-zA-Z0-9_]+$/.test(value)) {
                return t('error.validation.username_invalid')
            }
            break;
        case Constants.PW_VALIDATION:
            fieldName = t('common.password')
            if (!/(?=.*[A-Za-z])(?=.*\d).{6,}/.test(value)){
                return t('error.validation.password_invalid')
            }
            break;
        case Constants.GROUP_NAME_VALIDATION:
            fieldName=t('groups.name')
            break;
        default:
            return t('error.unknown_parameter',{param:type})
    }
    if (value==null||!value.trim()) {
        return t('error.validation.required', { field: fieldName })
    }
    if (value.length > Constants.MAX_STRING_LENGTH) {
        return t('error.validation.too_long', { field: fieldName })
    }
    return "" // 验证通过
}