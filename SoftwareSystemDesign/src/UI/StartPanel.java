package UI;

import javax.swing.JPanel;
import GameEngine.GameState;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

/**
 * 보유 금액과 설정을 표시하고 게임 시작, 설정 및 종료로 연결하는 시작 화면이다.
 * @serial exclude
 */
public class StartPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	GameState state;
	JLabel moneyL;
	JLabel peopleL;
	JLabel paperL;
	/**
	 * 현재 상태와 화면 컴포넌트를 초기화한다.
	 *
	 * @param gameGui 화면 전환과 저장을 담당하는 상위 창
	 * @param state 현재 게임의 공유 상태
	 */
	public StartPanel(GameGui gameGui, GameState state) {
		setBackground(new Color(128, 0, 0));
		setLayout(null);
		this.state = state;
		JLabel lblNewLabel = new JLabel("KOREANPOKER WITH 타짜");
		JButton startB = new JButton("START");
		JButton settingB = new JButton("설정");
		JButton exitB = new JButton("EXIT");
		moneyL = new JLabel("현금: " + String.format("%,d", state.getMoney()));
		peopleL = new JLabel("인원: " + Integer.toString(state.getPeople()));
		paperL = new JLabel("장 수: " + Integer.toString(state.getPaper()));

		lblNewLabel.setBounds(72, 150, 652, 58);
		lblNewLabel.setForeground(Color.ORANGE);
		lblNewLabel.setFont(new Font("굴림", Font.BOLD, 50));
		startB.setFont(new Font("굴림", Font.BOLD, 30));
		startB.setBounds(150, 250, 500, 150);
		startB.setPreferredSize(new Dimension(500, 100));
		settingB.setFont(new Font("굴림", Font.BOLD, 15));
		settingB.setBounds(150, 435, 230, 120);
		exitB.setFont(new Font("굴림", Font.BOLD, 15));
		exitB.setBounds(420, 435, 230, 120);
		moneyL.setForeground(Color.ORANGE);
		moneyL.setFont(new Font("굴림", Font.BOLD, 15));
		moneyL.setBounds(600, 10, 170, 30);
		peopleL.setForeground(Color.ORANGE);
		peopleL.setFont(new Font("굴림", Font.BOLD, 14));
		peopleL.setBounds(600, 40, 170, 30);
		paperL.setForeground(Color.ORANGE);
		paperL.setFont(new Font("굴림", Font.BOLD, 15));
		paperL.setBounds(600, 70, 170, 30);

		add(lblNewLabel);
		add(startB);
		add(settingB);
		add(exitB);
		add(moneyL);
		add(peopleL);
		add(paperL);	
		JLabel loadHintL = new JLabel("이전 게임: 설정 → 불러오기 → 이어하기");
		loadHintL.setForeground(Color.ORANGE);
		loadHintL.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		loadHintL.setBounds(150, 575, 500, 30);
		add(loadHintL);

		startB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				gameGui.showPanel("betting");
			}
		});

		settingB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				gameGui.showPanel("setting");
			}
		});

		exitB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				gameGui.exitGame();
			}
		});
		refreshValue();

	}
	/**
	 * 현재 상태를 읽어 금액과 화면 정보를 갱신한다.
	 */
	public void refreshValue() {
		moneyL.setText("현금: " + String.format("%,d",state.getMoney()));
		peopleL.setText("인원: " + Integer.toString(state.getPeople()));
		paperL.setText("장 수: " + Integer.toString(state.getPaper()));
		int width = Math.max(170, moneyL.getPreferredSize().width);
		int x = 770 - width;
		moneyL.setBounds(x, 10, width, 30);
		peopleL.setBounds(x, 40, width, 30);
		paperL.setBounds(x, 70, width, 30);
		
		revalidate();
		repaint();
	}
}
