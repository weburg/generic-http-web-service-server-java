package com.weburg.ghowst;

import org.junit.jupiter.api.Test;

class HttpWebServiceMapperTest {

    @Test
    void getResourceFromPath() {
        String resource = HttpWebServiceMapper.getResourceFromPath("/resource/subresource");
        assert resource.equals("resource");
    }

    @Test
    void getResourceFromMethod() {
        String resource = HttpWebServiceMapper.getResourceFromMethod("createResource");
        assert resource.equals("resource");
    }

    @Test
    void getCustomVerbFromPath() {
        String customVerb = HttpWebServiceMapper.getCustomVerbFromPath("/resource/subresource");
        assert customVerb.equals("subresource");
    }

    @Test
    void getCustomVerbFromMethod() {
        String customVerb = HttpWebServiceMapper.getCustomVerbFromMethod("restartResource");
        assert customVerb.equals("restart");
    }

    @Test
    void getHttpMethodFromServiceMethodName() {
        HttpWebServiceMapper.HttpMethod methodName = HttpWebServiceMapper.getHttpMethodFromServiceMethodName("createResource");
        assert methodName.equals(HttpWebServiceMapper.HttpMethod.POST);
    }
}