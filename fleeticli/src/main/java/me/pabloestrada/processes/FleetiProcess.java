package me.pabloestrada.processes;

import me.pabloestrada.FleetiMessage;
import me.pabloestrada.FleetiMessageType;
import me.pabloestrada.commands.Command;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FleetiProcess
{
    private List<Command> commands;
    private List<Process> allProcesses;

    public FleetiProcess(final List<Command> commands) {
        this.commands = commands;
        this.allProcesses = new ArrayList<>();
    }

    public void execute() {
       runProcess();
    }

    public void executeAsync(final int delayInSeconds) {
        Executors.newSingleThreadScheduledExecutor().schedule(this::runProcess, delayInSeconds, TimeUnit.SECONDS);
    }

    public void execute(final int delayInSeconds) {
        Executors.newSingleThreadScheduledExecutor().schedule(this::stopFleetiProcess, delayInSeconds, TimeUnit.SECONDS);
        runProcess();
    }

    public void stopFleetiProcess() {
        allProcesses.forEach(Process::destroyForcibly);
    }

    private void runProcess() {
        commands.forEach(command -> {
            final ProcessBuilder commandBuilder = new ProcessBuilder();
            commandBuilder.command(command.getRawCommand().split(" "));
            commandBuilder.directory(new File(command.getPath()));
            try {
                final Process commandProcess = commandBuilder.start();
                final StreamGobbler streamGobbler = new StreamGobbler(commandProcess.getInputStream(), System.out::println);
                Executors.newSingleThreadExecutor().submit(streamGobbler);
                allProcesses.add(commandProcess);

                int exitVal = commandProcess.waitFor();
                if (exitVal == 0) {
                    FleetiMessage.printMessage(
                            new FleetiMessage(FleetiMessageType.SUCCESS, "Successfully ran process for " + command.getRawCommand()));
                } else {
                    FleetiMessage.printMessage(
                            new FleetiMessage(FleetiMessageType.ERROR, "Could not run process for command " + command.getRawCommand()));
                }
            } catch (final IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
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
