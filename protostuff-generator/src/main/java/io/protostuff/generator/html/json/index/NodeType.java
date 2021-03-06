package io.protostuff.generator.html.json.index;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public enum NodeType {

    @JsonProperty("proto")
    PROTO,

    @JsonProperty("message")
    MESSAGE,

    @JsonProperty("enum")
    ENUM,

    @JsonProperty("service")
    SERVICE
}
