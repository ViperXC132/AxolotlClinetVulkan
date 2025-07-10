package io.github.axolotlclient.bridge.internal;

import io.github.axolotlclient.bridge.BridgeVersion;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresImpl {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Inherits {
    }

    BridgeVersion min() default BridgeVersion.V1_8;
    BridgeVersion max() default BridgeVersion.V1_21_7;
}
