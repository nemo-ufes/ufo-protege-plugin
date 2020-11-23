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
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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


    /**
     * Read reource content and return it as a String.
     *
     * @param clazz The class whose ClassLoader is going to be used to load the
     * resource
     * @param resourceName The resource name
     */
    public static String readAsString(Class<?> clazz, String resourceName) {
        try (
            Scanner scanner = new Scanner(
                clazz.getResourceAsStream(resourceName), "UTF-8")
        ) {
            return scanner.useDelimiter("\\A").next();
        }
    }

    /**
     * Create a new string scanner for a regular expression.
     *
     * @param regexString The regular expression to be used by scanner
     * @return The scanner
     */
    public static StringScanner stringScanner(String regexString) {
        return new StringScanner(regexString);
    }

    /**
     * Utility class to scan a string, spliting it according to a regular
     * expression.
     * <br>
     */
    public static class StringScanner {

        private final Pattern pattern;

        StringScanner(String regexString) {
            pattern = Pattern.compile(regexString);
        }

        /**
         * Scans given string and reporting matched and unmatched blocks.
         * <br>
         * A matcher is created based on regular expression of this scanner for
         * the given string. The string is then scanned by occurrence of the
         * regular expression, and the consumers are invoked to accept
         * sequentially the substrings matching and not matching the regular
         * expression.
         *
         * @param str The string to be scanned
         * @param unmatchConsumer The consumer of unmatched blocks
         * @param matcherConsumer The consumer of matched blocks
         */
        public void scan(String str,
                Consumer<String> unmatchConsumer,
                Consumer<Matcher> matcherConsumer) {

            Matcher matcher = pattern.matcher(str);
            int startIndex = 0;
            while (matcher.find()) {
                int matcherStart = matcher.start();
                if (startIndex < matcherStart) {
                    unmatchConsumer.accept(
                            str.substring(startIndex, matcherStart));
                }
                matcherConsumer.accept(matcher);
                startIndex = matcher.end();
            }
            if (startIndex < str.length()) {
                unmatchConsumer.accept(str.substring(startIndex));
            }
        }

        /**
         * Replace expression matches by values returned by given function.
         * <br>
         * The string is scanned by occurrence of the regular expression and
         * the given function is applied for every match. The result string has
         * all matches replaced by the result of applying the function.
         *
         * @param str The string to be scanned
         * @param doReplace Function replacing the matches
         * @return Text with occurrences of regular expression replaced
         */
        public String replace(String str, Function<Matcher, String> doReplace) {
            StringBuilder result = new StringBuilder();
            scan(str, result::append, matcher -> {
                result.append(doReplace.apply(matcher));
            });
            return result.toString();
        }
    }
}
