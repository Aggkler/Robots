package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;


import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;


public class LogWindow extends JInternalFrame implements LogChangeListener, SaveAble {

    private LogWindowSource m_logSource;
    private JTextArea m_logContent;
    private static final int MAX_LOGS = 200;

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new JTextArea();
        m_logContent.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(m_logContent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        int total = m_logSource.size();
        int start = Math.max(0, total - MAX_LOGS);

        for (LogEntry entry : m_logSource.range(start, MAX_LOGS)) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void dispose() {
        m_logSource.unregisterListener(this);
        super.dispose();
    }

    @Override
    public String getId() {
        return "LogWindow";
    }
}
