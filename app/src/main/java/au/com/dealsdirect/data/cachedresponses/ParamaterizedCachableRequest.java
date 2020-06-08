package au.com.dealsdirect.data.cachedresponses;

public class ParamaterizedCachableRequest implements CachableRequest {

    private String uniqueKey;
    private String parameters = "";

    public ParamaterizedCachableRequest(String uniqueKey, Object... parameters) {
        this.uniqueKey = uniqueKey;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter instanceof String) {
                stringBuilder.append((String) parameter);
            } else if (parameter != null) {
                stringBuilder.append(parameter.toString());
            } else {
                stringBuilder.append("null");
            }
            if (i + 1 < parameters.length) {
                stringBuilder.append(",");
            }
        }

        this.parameters = stringBuilder.toString();
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getCacheKey() {
        return uniqueKey + "," + parameters;
    }
}
