package com.dreamloom.friend;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for checking friend access to methods.
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
public final class FriendChecker
{
    /**
     * Using class introspection, this method checks the callstack to determine if the caller of a method
     * has been authorized to do so using the {@link Friend} annotation. If not, a runtime exception is thrown.
     *
     * @throws FriendException thrown if the calling class is not an authorized friend of the invoked method.
     */
    public static void check()
            throws FriendException
    {
        internalCheck(null);
    }

    /**
     * Using class introspection, this method checks the callstack to determine if the caller of a method
     * has been authorized to do so using the {@link Friend} annotation. If not, a runtime exception is thrown.
     *
     * @param altKey alternate identifier for the method to use instead of its name. Must match the
     *               {@link com.dreamloom.friend.Friend#altKey()} annotation.
     * @throws FriendException thrown if the calling class is not an authorized friend of the invoked method.
     */
    public static void check(String altKey)
            throws FriendException
    {
        internalCheck(altKey);
    }

    private static void internalCheck(String altKey)
            throws FriendException
    {
        try
        {
            StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
            String methodName = (altKey != null) ? altKey : stackTrace[2].getMethodName();
            Class callee = FriendChecker.class.getClassLoader().loadClass(stackTrace[2].getClassName());
            Class caller = FriendChecker.class.getClassLoader().loadClass(stackTrace[3].getClassName());

            for (Method method : callee.getDeclaredMethods())
            {
                Friend friend = method.getAnnotation(Friend.class);
                if (friend != null)
                {
                    // If the altKey param was provided, check against the altKey annotation field;
                    // else, use the methodName determined through reflection to compare.
                    if (altKey != null && methodName.equals(friend.altKey())
                            || (altKey == null && methodName.equals(method.getName())))
                    {
                        List<Class> classes = Arrays.asList(friend.value());
                        if (!classes.contains(caller))
                        {
                            String msg = String.format("%s is not allowed to invoke %s.%s",
                                                       caller.getName(),
                                                       callee.getName(),
                                                       methodName);
                            throw new FriendException(msg);
                        }
                    }
                }
            }
        }
        catch (ClassNotFoundException cnfe)
        {
            throw new FriendException("Error determining friend access", cnfe);
        }
    }
}
