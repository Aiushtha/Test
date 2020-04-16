package org.lxz.utils.base;

/**
 * Created by Lin on 2017/4/20.
 */


import android.support.annotation.LayoutRes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind a field to the view for the specified ID. The view will automatically be cast to the field
 * type.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TitleName {
    /** View ID to which the field will be bound. */
   String value();
}
