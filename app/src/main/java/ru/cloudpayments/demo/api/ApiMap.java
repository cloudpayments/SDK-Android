package ru.cloudpayments.demo.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public final class ApiMap extends HashMap<String, Object> {

    private static final String TOKEN = "token";

    private ApiMap() { }

    public static Builder builder() {
        return new Builder();
    }

    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static String urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }

    public static class Builder {

        private ApiMap apiMap = new ApiMap();

        private Builder() { }

        public Builder withSession() {
            /*User user = RepositoryProvider.provideUserRepository().getCurrentUser();
            if (user != null) {
                String token = user.getToken();
                if (token != null) {
                    apiMap.put(TOKEN, token);
                }
            }*/

            return this;
        }

        public ApiMap build() {
            return apiMap;
        }

        public String buildIntoQuery() {
            return urlEncodeUTF8(apiMap);
        }
    }
}
