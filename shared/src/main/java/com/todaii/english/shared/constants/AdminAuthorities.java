package com.todaii.english.shared.constants;

public class AdminAuthorities {
    public static final String SUPER = "SUPER_ADMIN";
    public static final String USER_MGR = "USER_MANAGER";
    public static final String CONTENT_MGR = "CONTENT_MANAGER";

    public static final String[] ALL_ADMINS = {SUPER, USER_MGR, CONTENT_MGR};
    public static final String[] SUPER_AND_USER = {SUPER, USER_MGR};
    public static final String[] SUPER_AND_CONTENT = {SUPER, CONTENT_MGR};
}
