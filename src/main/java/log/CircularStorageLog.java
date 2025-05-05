package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CircularStorageLog {
    private final LogEntry[] buffer;
    private final int capacity;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int nextLog = 0;
    private int oldLog = 0;
    private int size = 0;


    public int getSize() {
        return this.size;
    }

    public CircularStorageLog(int capacity) {
        this.capacity = capacity;
        this.buffer = new LogEntry[this.capacity];
    }

    public void add(LogEntry entry) {
        lock.writeLock().lock();
        try {
            buffer[nextLog] = entry;
            nextLog = (nextLog + 1) % this.capacity;
            if (this.size == this.capacity) {
                oldLog = (oldLog + 1) % this.capacity;
            } else {
                size++;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<LogEntry> getPartOfLogs(int start, int end) {
        lock.readLock().lock();
        try {
            if (start < 0 || end > size || start > end) {
                throw new IndexOutOfBoundsException();
            }
            List<LogEntry> result = new ArrayList<>(end - start);
            for (int i = start; i < end; i++) {
                int index = (this.oldLog + i) % this.capacity;
                result.add(this.buffer[index]);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Iterator<LogEntry> iterator() {
        lock.readLock().lock();
        try {
            List<LogEntry> part = getPartOfLogs(0, size);
            return Collections.unmodifiableList(part).iterator();
        } finally {
            lock.readLock().unlock();
        }
    }
}
