export const API_BASE_URL = '/api';
export const PUBLIC_AUTH_API = '/public';
export const MAX_STRING_LENGTH = 25;

export const USER_NAME_VALIDATION=0;
export const PW_VALIDATION=1;
export const GROUP_NAME_VALIDATION=2;

export const Role = {
    Admin: 'ADMIN',
    Member: 'MEMBER',
    Owner: 'OWNER'
};
export const TaskOrder = {
    Priority: 0,
    Status: 1
};
export const TaskStatus = {
    Todo: 'TODO',
    InProgress: 'IN_PROGRESS',
    Completed: 'COMPLETED',
    Archived: 'ARCHIVED'
};
export const TaskPriority = {
    High: 'HIGH',
    Medium: 'MEDIUM',
    Low: 'LOW'
};