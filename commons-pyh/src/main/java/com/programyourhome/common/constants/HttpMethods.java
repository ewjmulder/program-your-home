package com.programyourhome.common.constants;

public class HttpMethods {

    private HttpMethods() {
    }

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";

    public static final boolean isGet(final String method) {
        return GET.equalsIgnoreCase(method);
    }

    public static final boolean isPost(final String method) {
        return POST.equalsIgnoreCase(method);
    }

    public static final boolean isDelete(final String method) {
        return DELETE.equalsIgnoreCase(method);
    }

    public static final boolean isOptions(final String method) {
        return OPTIONS.equalsIgnoreCase(method);
    }

}
