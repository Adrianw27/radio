import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class Radio implements Runnable {

    enum State { OFF, TOP, SCANNING, LOCKED, BOTTOM }

    private volatile State state = State.OFF;
    private volatile boolean scanning = false;
    private double freq = 108.0;

    private static final double TOP = 108.0, BOT = 88.0, STEP = 0.1;
    private static final double[] STATIONS = {107.5, 104.3, 101.1, 99.9, 95.1, 92.5, 89.7};

    private final JFrame frame = new JFrame("Radio");
    private final JLabel stateLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel freqLabel  = new JLabel("", SwingConstants.CENTER);
    private final JButton onButton   = new JButton("on");
    private final JButton offButton  = new JButton("off");
    private final JButton scanButton = new JButton("scan");
    private final JButton resetButton= new JButton("reset");

    public Radio() {
        stateLabel.setForeground(Color.BLACK);
        freqLabel.setForeground(Color.BLACK);

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.add(stateLabel);
        center.add(freqLabel);

        JPanel bar = new JPanel(new GridLayout(1, 4, 6, 6));
        bar.add(onButton);
        bar.add(offButton);
        bar.add(scanButton);
        bar.add(resetButton);

        frame.setLayout(new BorderLayout());
        frame.add(center, BorderLayout.CENTER);
        frame.add(bar, BorderLayout.SOUTH);
        frame.setSize(420, 160);

        onButton.addActionListener(e -> on());
        offButton.addActionListener(e -> off());
        scanButton.addActionListener(e -> scan());
        resetButton.addActionListener(e -> reset());

        setState(State.OFF);
        frame.setVisible(true);

        new Thread(this, "Radio-Scanner").start();
    }

    private void on() {
        if (state == State.OFF) setTop();
    }

    private void off() {
        scanning = false;
        setState(State.OFF);
    }

    private void reset() {
        setTop();
    }

    private void scan() {
        if (state == State.TOP || state == State.LOCKED) {
            setState(State.SCANNING);
            scanning = true;
        }
    }

    private void lock(double s) {
        scanning = false;
        freq = round(s);
        setState(State.LOCKED);
    }
    private void end() {
        scanning = false;
        freq = BOT;
        setState(State.BOTTOM);
    }
    private void setTop() {
        scanning = false;
        freq = TOP;
        setState(State.TOP);
    }

    private void setState(State s) {
        state = s;
        refresh();
    }

    private void refresh() {
        SwingUtilities.invokeLater(() -> {
            stateLabel.setText("State: " + state);
            freqLabel.setText("Frequency: " + freq + " MHz");

            onButton.setEnabled(state == State.OFF);
            offButton.setEnabled(state != State.OFF);
            resetButton.setEnabled(state != State.OFF);
            scanButton.setEnabled(state == State.TOP || state == State.LOCKED);
        });
    }

    @Override public void run() {
        while (true) {
            if (state == State.SCANNING && scanning) {
                sleep(100);
                freq = round(freq-STEP);
                if (freq <= BOT) {
                    end();
                    continue;
                }
                for (double s : STATIONS) {
                    if (freq - s == 0.0) {
                        lock(s);
                        break;
                    }
                }
                refresh();
            } else {
                sleep(50);
            }
        }
    }

    private static double round(double x) {
        return Math.round(x * 10.0) / 10.0;
    }

    private static void sleep(long ms) {
        try { 
            Thread.sleep(ms); 
        } catch (InterruptedException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Radio::new);
    }
}
