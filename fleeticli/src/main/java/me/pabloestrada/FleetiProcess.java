package me.pabloestrada;

import me.pabloestrada.commands.Command;
import sun.rmi.log.LogOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FleetiProcess
{
    private List<Command> commands;

    public FleetiProcess(final Command command) {
        // TODO
        // Add List.of
        final List<Command> commands = new ArrayList<>();
        commands.add(command);
        this.commands = commands;
    }

    public FleetiProcess(final List<Command> commands) {
        this.commands = commands;
    }

    public void execute(final Optional<String> concurrentCommand) {
        commands.forEach(command -> {
            final ProcessBuilder commandBuilder = new ProcessBuilder();
            commandBuilder.command(command.getRawCommand().split(" "));
            commandBuilder.directory(new File(command.getPath()));
            try {
                final Process commandProcess = commandBuilder.start();
                final StreamGobbler streamGobbler = new StreamGobbler(commandProcess.getInputStream(), System.out::println);
                Executors.newSingleThreadExecutor().submit(streamGobbler);
                int exitVal = commandProcess.waitFor();
                if (exitVal == 0) {
                    FleetiMessage.printMessage(
                            new FleetiMessage(FleetiMessageType.SUCCESS, "Successfully ran process for " + command.getRawCommand()));
                } else {
                    FleetiMessage.printMessage(
                            new FleetiMessage(FleetiMessageType.ERROR, "could not run process for command " + command.getRawCommand()));
                }
            } catch (final IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void execute(final String concurrentCommand) {

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
