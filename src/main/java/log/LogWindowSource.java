package log;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений
 * ограниченного размера)
 */
public class LogWindowSource {
    private CircularStorageLog m_messages;
    private final CopyOnWriteArrayList<LogChangeListener> m_listeners =
            new CopyOnWriteArrayList<LogChangeListener>();
    private volatile LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength) {
        m_messages = new CircularStorageLog(iQueueLength);
    }

    public void registerListener(LogChangeListener listener) {
        m_listeners.addIfAbsent(listener);
    }

    public void unregisterListener(LogChangeListener listener) {
        m_listeners.remove(listener);
    }

    public void append(LogLevel logLevel, String strMessage) {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        m_messages.add(entry);
        for (LogChangeListener listener : m_listeners) {
            listener.onLogChanged();
        }
    }

    public int size() {
        return m_messages.getSize();
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        if (startFrom < 0 || startFrom >= m_messages.getSize()) {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, m_messages.getSize());
        return m_messages.getPartOfLogs(startFrom, indexTo);
    }

    public Iterable<LogEntry> all() {
        return m_messages.getPartOfLogs(0, size());
    }
}
