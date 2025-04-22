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
    private int tail = 0;  //
    private int head = 0;  //
    private int size = 0;  // Сколько ячеек заполнено


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
            buffer[tail] = entry;
            tail = (tail + 1) % this.capacity;
            if (this.size == this.capacity) {
                head = (head + 1) % this.capacity;
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
                int index = (this.head + i) % this.capacity;
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
