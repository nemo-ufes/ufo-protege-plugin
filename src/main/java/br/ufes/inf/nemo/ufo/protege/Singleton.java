/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luciano
 */
public class Singleton {

    protected static Logger log = LoggerFactory.getLogger(Singleton.class);

    public static <T extends Disposable> T get(
            ModelManager modelManager, Class<T> aClass) {

        T result = (T) modelManager.get(aClass);
        if (result == null) {
            try {
                Constructor<T> constructor = aClass.getConstructor();
                result = constructor.newInstance();
                if (result instanceof Initializable) {
                    ((Initializable) result).initialize(modelManager);
                }
                modelManager.put(aClass, result);
            } catch (
                    NoSuchMethodException |
                    SecurityException |
                    InstantiationException |
                    IllegalAccessException |
                    IllegalArgumentException
                    | InvocationTargetException ex) {
                log.error("Error on getting singleton object.", ex);
            }
        }
        return result;
    }

    public interface Initializable extends Disposable {

        public void initialize(ModelManager modelManager);

        @Override
        public default void dispose() throws Exception {

        }
    }
}
