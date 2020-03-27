package app.datamodel.mongo;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({TYPE, FIELD})
public @interface Embedded
{
	Class<? extends Pojo> value() default EmbeddedPojo.class;
	String nestedName() default "";
	boolean list() default false;
}
