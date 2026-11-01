package UI;

import GameEngine.GameLogEntry;
import GameEngine.GameState;
import GameEngine.SaveRepository;
import GameEngine.SavedGame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * 저장 파일의 미리보기와 실제 복원을 분리한다. 이어하기를 선택할 때만 잔액과 기록을 복원한다.
 * @serial exclude
 */
public class LoadPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final GameState state;
    private final SaveRepository repository;
    private SavedGame preview;
    final JButton continueB = new JButton("이어하기");
    final JButton reloadB = new JButton("다시 조회");
    final JButton backB = new JButton("뒤로가기");
    final JLabel balanceL = new JLabel();
    final JLabel statusL = new JLabel();
    final JTextArea logsArea = new JTextArea();

    /**
     * 현재 상태와 화면 컴포넌트를 초기화한다.
     *
     * @param state 현재 게임의 공유 상태
     * @param repository 저장 파일 읽기를 담당하는 저장소
     * @param onRestored 복원 성공 후 실행할 화면 전환 작업
     * @param onBack 뒤로가기 시 실행할 작업
     */
    public LoadPanel(GameState state, SaveRepository repository, Runnable onRestored, Runnable onBack) {
        this.state = state;
        this.repository = repository;
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(230, 230, 230));
        setBorder(new EmptyBorder(22, 22, 22, 22));
        JPanel header = new JPanel(new GridLayout(4, 1, 0, 8));
        header.setOpaque(false);
        JLabel title = new JLabel("불러오기 · 최근 10판");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        balanceL.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        header.add(title);
        header.add(balanceL);
        header.add(new JLabel("이어하기를 누르면 저장된 잔액과 기록으로 게임을 이어갑니다."));
        header.add(statusL);
        add(header, BorderLayout.NORTH);
        logsArea.setEditable(false);
        logsArea.setLineWrap(true);
        logsArea.setWrapStyleWord(true);
        logsArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        logsArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
        add(new JScrollPane(logsArea), BorderLayout.CENTER);
        JPanel buttons = new JPanel(new GridLayout(1, 3, 12, 0));
        buttons.setOpaque(false);
        for (JButton button : new JButton[] {backB, reloadB, continueB}) {
            button.setFont(new Font("맑은 고딕", Font.BOLD, 17));
            button.setPreferredSize(new java.awt.Dimension(150, 52));
            buttons.add(button);
        }
        add(buttons, BorderLayout.SOUTH);
        continueB.setEnabled(false);
        backB.addActionListener(e -> onBack.run());
        reloadB.addActionListener(e -> refreshPreview());
        continueB.addActionListener(e -> {
            if (preview == null) return;
            try {
                state.getHistory().restore(state, preview);
                onRestored.run();
            } catch (IllegalStateException ex) {
                statusL.setText(ex.getMessage());
                statusL.setForeground(Color.RED.darker());
            }
        });
    }

    /**
     * 파일을 읽어 잔액과 최신 10판을 미리보기로 표시한다. 현재 게임 상태는 변경하지 않는다.
     */
    public void refreshPreview() {
        preview = null;
        continueB.setEnabled(false);
        logsArea.setText("");
        balanceL.setText("저장된 잔액: -");
        statusL.setForeground(Color.DARK_GRAY);
        try {
            preview = repository.load();
            if (preview == null) {
                statusL.setText("저장된 게임이 없습니다.");
                return;
            }
            balanceL.setText("저장된 잔액: " + String.format("%,d", preview.money()) + "원");
            StringBuilder text = new StringBuilder();
            for (GameLogEntry entry : preview.logs()) {
                text.append(entry.displayLine()).append('\n').append("    결과: ").append(entry.outcome());
                if (entry.replays() > 0) text.append(" / 재경기 ").append(entry.replays()).append("회 후 종료");
                text.append("\n\n");
            }
            logsArea.setText(text.isEmpty() ? "아직 완료된 판이 없습니다." : text.toString());
            logsArea.setCaretPosition(0);
            statusL.setText("최신 기록부터 " + preview.logs().size() + "판 · 현재 잔액은 아직 바뀌지 않았습니다.");
            continueB.setEnabled(true);
        } catch (IOException ex) {
            statusL.setText("불러오기 실패: 파일을 확인한 뒤 다시 조회하세요.");
            statusL.setForeground(Color.RED.darker());
            logsArea.setText(ex.getMessage());
        }
    }
}
