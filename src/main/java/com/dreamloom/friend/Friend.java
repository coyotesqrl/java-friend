package com.dreamloom.friend;

import java.lang.annotation.*;

/**
 * Provides pseduo-friend functionality for Java.
 * <p/>
 * There are several implementation routes that may be taken, the simplest being
 * applying this Annotation to those public methods that are to be friend-protected and
 * then calling the {@link FriendChecker#check()} method. This will provide run-time checking
 * of access to the method.
 * <p/>
 * Another implementation route requires that those methods to be friend-accessible are
 * marked as protected/package/private (depending on other access requirements of the API)
 * and then wrapped with an Adapter. The Adapter should be a public inner class of the container in question
 * that wraps each of the friend-accessible methods. Each of the wrapper methods would then have this
 * Annotation applied to them and would invoke the {@link FriendChecker#check()} method. Callers would
 * defer to the Adapter instead of attempting to invoke the private method of the container. In this way, compile-time
 * access to the private methods is restricted and the public interface of the container is not polluted with
 * methods that should not be available to invalid callers.
 * <p/>
 * A third implementation pattern is to use a static inner class as
 * the Adapter with a private constructor and a static factory method that takes the containing class as its sole
 * parameter. The factory method is annotated and locked down to friends and all other methods in the Adapter are
 * publicly accessible. This provides class-level, rather than function-level access checking.
 * <p/>
 * Copyright 2011 R.A. Porter
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author R.A. Porter.
 * @version 1.0 12 July 2011
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Friend {
    /**
     * The set of Classes allowed to access the annotated method.
     *
     * @return the set of allowed classes
     */
    Class[] value() default {};

    /**
     * An alternate key to use to annotate the method.
     * While access checking is normally determined based on method names, in the case where there are overloaded
     * methods in the callee class, the altKey should be set and the {@link FriendChecker#check(String)} called.
     *
     * @return the alternate key to use for method discrimination
     */
    String altKey() default "";
}
