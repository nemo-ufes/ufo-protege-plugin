/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.ModelManager;
import org.protege.editor.owl.model.OWLModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author luciano
 */
public class Singleton {

    protected static Logger log = LoggerFactory.getLogger(Singleton.class);

    public static <T extends Disposable> T get(
            OWLModelManager modelManager, Class<T> aClass) {

        T result = (T) modelManager.get(aClass);
        if (result == null) {
            Optional<T> optional =

            Stream.<ResultConstructor<T>>of(
                () -> aClass.getConstructor(OWLModelManager.class)
                        .newInstance(modelManager),
                () -> aClass.getConstructor()
                        .newInstance()
            )
            .map(constructor -> {
                try {
                    return constructor.construct();
                } catch (NoSuchMethodException
                        | SecurityException
                        | InstantiationException
                        | IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException ex) {
                    log.debug("Could not instatiate object.", ex);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst()
            ;

            if (optional.isPresent()) {
                result = optional.get();
                modelManager.put(aClass, result);
                if (result instanceof Initializable) {
                    ((Initializable) result).initialize(modelManager);
                }
            } else {
                log.error("Could not instantiate singleton for class "
                        + aClass.getName());
            }
        }
        return result;
    }

    public interface ResultConstructor<T> {
        public T construct() throws
                NoSuchMethodException,
                SecurityException,
                InstantiationException,
                IllegalAccessException,
                IllegalArgumentException,
                InvocationTargetException;
    }

    public interface Initializable extends Disposable {

        public void initialize(ModelManager modelManager);

        @Override
        public default void dispose() throws Exception {

        }
    }
}
