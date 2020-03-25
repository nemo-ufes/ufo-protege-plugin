/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufes.inf.nemo.ufo.protege;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * @author luciano
 */
public abstract class Util {

    /**
     * Read lines from resource, convert to a Stream of strings, and pass it to
     * a consumer.
     *
     * @param aClass Class whose class loader will be used to load the resource
     * @param resourceName The resource name
     * @param consumer Consumer which will get the Stream
     * @throws IOException If any error occour when accessing the resource
     */
    public static void readLines(
            Class<?> aClass,
            String resourceName,
            Consumer<Stream<String>> consumer) throws IOException {
        try (
                InputStream inputStream
                    = aClass.getResourceAsStream(resourceName);
                InputStreamReader unbuf
                    = new InputStreamReader(inputStream);
                BufferedReader reader = new LineNumberReader(unbuf);
        ) {
            consumer.accept(reader.lines());
        }
    }

    /**
     * Read lines from resource, convert to a Stream of strings, and pass it to
     * a consumer.
     * <p>
     * This method delegates to the
     * {@link #readLines(Class, String, Consumer) three parameter version} of
     * method with same name,
     * passing the class of <i>this</> object as first argument.
     *
     * @param resourceName The resource name
     * @param consumer Consumer which will get the Stream
     * @throws IOException If any error occour when accessing the resource
     */
    protected void readLines(
            String resourceName,
            Consumer<Stream<String>> consumer) throws Exception {
        readLines(getClass(), resourceName, consumer);
    }

    /**
     * Read lines from a resource and feed them to a Consumer.
     *
     * @param aClass Class whose class loader will be used to load the resource
     * @param resourceName The resource name
     * @param consumer Consumer which will get the lines
     * @throws IOException If any error occour when accessing the resource
     */
    public static void forEachLine(
            Class<?> aClass,
            String resourceName,
            Consumer<String> consumer) throws IOException {
        readLines(aClass, resourceName, lines -> { lines.forEach(consumer); });
    }

    /**
     * Read lines from a resource and feed them to a Consumer.
     * <p>
     * This method delegates to the
     * {@link #forEachLine(Class, String, Consumer) three parameter version} of
     * method with same name,
     * passing the class of <i>this</> object as first argument.
     *
     * @param resourceName The resource name
     * @param consumer Consumer which will get the lines
     * @throws IOException If any error occour when accessing the resource
     */
    public void forEachLine(
            String resourceName,
            Consumer<String> consumer) throws Exception {
        forEachLine(getClass(), resourceName, consumer);
    }
}
