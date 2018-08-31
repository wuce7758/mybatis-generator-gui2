/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package tech.wetech.mybatis.generator.utils;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;


/**
 * @author cjbi
 */
public class Reflections {
    private static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");
    private static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");

    private Reflections() {
    }

    public static String fnToFieldName(Fn fn) {
        try {
            SerializedLambda serializedLambda = getSerializedLambda(fn);
            String getter = serializedLambda.getImplMethodName();
            if (GET_PATTERN.matcher(getter).matches()) {
                getter = getter.substring(3);
            } else if (IS_PATTERN.matcher(getter).matches()) {
                getter = getter.substring(2);
            }
            return Introspector.decapitalize(getter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SerializedLambda getSerializedLambda(Fn fn) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = fn.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(Boolean.TRUE);
        return (SerializedLambda) method.invoke(fn);
    }

    public static String fnToFieldValue(Fn fn, Object object) {
        try {
            SerializedLambda serializedLambda = getSerializedLambda(fn);
            String getter = serializedLambda.getImplMethodName();
            Method method = object.getClass().getMethod(getter);
            return (String) method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class fnToClass(Fn fn) {
        try {
            SerializedLambda serializedLambda = getSerializedLambda(fn);
            String implClass = serializedLambda.getImplClass();
            return Class.forName(implClass.replace("/","."));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
