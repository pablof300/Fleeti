package me.pabloestrada;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ProcessOutput
{
    public ProcessOutput(final Process process, final String appName) {
        try {
        final StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);

        int exitVal = process.waitFor();
        if (exitVal == 0) {
            FleetiMessage.printMessage(new FleetiMessage(FleetiMessageType.SUCCESS, "Successfully generate Fleeti app " + appName));
            System.exit(0);
        } else {
            FleetiMessage.printMessage(new FleetiMessage(FleetiMessageType.ERROR, "could not generate Fleeti app " + appName));
        }
        } catch(final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}
