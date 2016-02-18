/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.model.internal.manage.schema.extract;

import com.google.common.collect.Iterables;
import org.gradle.api.Nullable;

import java.lang.reflect.Method;
import java.util.Collections;

public class ModelPropertyExtractionContext {

    private final String propertyName;
    private PropertyAccessorExtractionContext getGetter;
    private PropertyAccessorExtractionContext isGetter;
    private PropertyAccessorExtractionContext setter;

    public ModelPropertyExtractionContext(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setGetGetter(PropertyAccessorExtractionContext getGetterContext) {
        this.getGetter = getGetterContext;
    }

    @Nullable
    public PropertyAccessorExtractionContext getGetGetter() {
        return getGetter;
    }

    public void setIsGetter(PropertyAccessorExtractionContext isGetterContext) {
        this.isGetter = isGetterContext;
    }

    @Nullable
    public PropertyAccessorExtractionContext getIsGetter() {
        return isGetter;
    }

    public void setSetter(PropertyAccessorExtractionContext setterGetterContext) {
        this.setter = setterGetterContext;
    }

    @Nullable
    public PropertyAccessorExtractionContext getSetter() {
        return setter;
    }

    @Nullable
    public PropertyAccessorExtractionContext mergeGetters() {
        if (getGetter == null && isGetter == null) {
            return null;
        }
        Iterable<Method> getMethods = getGetter != null ? getGetter.getDeclaringMethods() : Collections.<Method>emptyList();
        Iterable<Method> isMethods = isGetter != null ? isGetter.getDeclaringMethods() : Collections.<Method>emptyList();
        return new PropertyAccessorExtractionContext(Iterables.concat(getMethods, isMethods));
    }
}
