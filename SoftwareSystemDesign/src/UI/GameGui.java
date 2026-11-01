package UI;

import GameEngine.*;


import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JOptionPane;


/**
 * 애플리케이션의 진입점이다. 화면 전환과 게임 시작, 결과 저장 및 정상 종료를 조정한다.
 * @serial exclude
 */
public class GameGui extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private CardLayout cardLayout;
	private StartPanel startPanel;
	private SettingPanel settingPanel;
	private BettingPanel bettingPanel;
	private GamePanel inGamePanel;
	private GameState state;
	private final SaveRepository repository = SaveRepository.forProject();
	private LoadPanel loadPanel;

	/**
	 * Swing 이벤트 디스패치 스레드에서 게임 창을 생성하고 표시한다.
	 *
	 * @param args 명령행 인수. 현재 사용하지 않는다.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			/**
			 * run 작업을 수행한다.
			 */
			public void run() {
				try {
					GameGui frame = new GameGui();
					frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "게임을 시작하지 못했습니다.\n" + e.getMessage(),
							"실행 오류", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * 현재 상태와 화면 컴포넌트를 초기화한다.
	 */
	public GameGui() {
		super("KoreanPoker with 타짜");
		this.state = new GameState();
		initialize(state);
	}
	void initialize(GameState state) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) { exitGame(); }
		});
		setLocation(100, 100);
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(800, 630));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		cardLayout = new CardLayout(0,0);
		contentPane.setLayout(cardLayout);

		startPanel = new StartPanel(this ,state);
		settingPanel = new SettingPanel(this, state);
		bettingPanel = new BettingPanel(this, state);
		loadPanel = new LoadPanel(state, repository, () -> showPanel("start"), () -> showPanel("setting"));
		
		contentPane.add(startPanel , "start");
		contentPane.add(settingPanel, "setting");
		contentPane.add(bettingPanel , "betting");
		contentPane.add(loadPanel, "load");
		pack();
		
		
	}
	
	/**
	 * 요청한 화면을 갱신하고 전환한다. 불러오기 진입 전에는 미저장 결과를 먼저 저장한다.
	 *
	 * @param Name 화면 이름 start, setting, betting, load 또는 ingame
	 */
	public void showPanel(String Name) {
		if(Name.equals("start")) {
			startPanel.refreshValue();
		}
		else if(Name.equals("betting")) {
			bettingPanel.refreshValue();
		}
		else if(Name.equals("setting")) {
			settingPanel.refreshValues();
		}
		else if(Name.equals("load")) {
			if(!saveProgress()) return;
			loadPanel.refreshPreview();
		}
		cardLayout.show(contentPane, Name);
		contentPane.revalidate();
		contentPane.repaint();
	}
	
	/**
	 * 기존 게임 패널을 교체하고 현재 설정으로 새 게임 화면을 생성한다.
	 */
	public void startGame() {
		if(inGamePanel != null) contentPane.remove(inGamePanel);
		inGamePanel = new GamePanel(this, state);
		contentPane.add(inGamePanel, "ingame");

	}
	/**
	 * 게임 화면을 새로 생성하여 표시한다. 재경기일 때 GameHistory의 기존 판 정보를 유지한다.
	 */
	public void restartGame() {
			contentPane.remove(inGamePanel);
			startGame();
			this.showPanel("ingame");


	}
	
	/**
	 * 현재 게임 패널을 반환한다. 게임 시작 전이면 null이다.
	 * @return 현재 게임 화면 또는 null
	 */
	public GamePanel getInGamePanel() {
		return inGamePanel;
	}

	/**
	 * 미저장 결과의 잔액과 기록을 저장한다. 실패 시 재시도 또는 나중에를 안내하며 중복 정산하지 않는다.
	 * @return 저장할 결과가 없거나 저장에 성공하면 true, 실패 또는 진행 중 미저장 상태이면 false
	 */
	public boolean saveProgress() {
		if(!state.getHistory().isDirty()) return true;
		if(state.getHistory().isActive()) return false;
		while(true) {
			try {
				repository.save(state.getHistory().snapshot(state));
				state.getHistory().markSaved();
				return true;
			} catch(IOException ex) {
				int choice = JOptionPane.showOptionDialog(this,
						"저장하지 못했습니다. 현재 잔액과 기록은 유지됩니다.\n" + ex.getMessage(),
						"저장 실패", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
						null, new String[] {"재시도", "나중에"}, "재시도");
				if(choice != 0) return false;
			}
		}
	}

	/**
	 * 진행 중인 판을 손실로 마무리하고 저장에 성공하면 창을 닫는다.
	 */
	public void exitGame() {
		if(inGamePanel != null && state.getHistory().isActive()) {
			inGamePanel.abandonRound();
		}
		if(saveProgress()) dispose();
	}

}
