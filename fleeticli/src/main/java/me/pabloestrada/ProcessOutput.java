package me.pabloestrada;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ProcessOutput
{
    private List<Process> processes;
    private String appName;

    public ProcessOutput(final List<Process> processes, final String appName) {
        this.processes = processes;
        this.appName = appName;
    }

    public void run() {
        processes.forEach(this::print);
    }

    private void print(final Process process) {
        try {
            final StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                FleetiMessage.printMessage(new FleetiMessage(FleetiMessageType.SUCCESS, "Successfully ran process for " + appName));
            } else {
                FleetiMessage.printMessage(new FleetiMessage(FleetiMessageType.ERROR, "could not run process for " + appName));
            }
        } catch(final InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
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
