package rip.warzone.anticheat.log;

import lombok.Getter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogManager {

    @Getter
    private final Queue<Log> logQueue=new ConcurrentLinkedQueue<>();

    public void exportAllLogs() {
        new LogExportRunnable(null).run();
    }

}