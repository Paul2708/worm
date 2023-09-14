package de.paul2708.worm.attributes;

import de.paul2708.worm.attributes.generator.NoneGenerator;
import de.paul2708.worm.attributes.generator.ValueGenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Identifier {

    Class<? extends ValueGenerator<?>> generator() default NoneGenerator.class;
}
