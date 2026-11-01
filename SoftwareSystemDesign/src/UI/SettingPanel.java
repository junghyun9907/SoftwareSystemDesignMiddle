package UI;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import GameEngine.GameState;

/**
 * 참가 인원과 기본 패 수를 설정하고 불러오기 및 저장 재시도로 연결한다.
 * @serial exclude
 */
public class SettingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	GameState state;
	JLabel peopleCounterL;
	JLabel paperCounterL;
	private final JButton retrySaveB = new JButton("저장 재시도");

	/**
	 * 현재 상태와 화면 컴포넌트를 초기화한다.
	 *
	 * @param gameGui 화면 전환과 저장을 담당하는 상위 창
	 * @param state 현재 게임의 공유 상태
	 */
	public SettingPanel(GameGui gameGui,GameState state) {
		this.state = state;
		setBackground(new Color(192, 192, 192));
		setLayout(null);
		JButton peopleMinusB = new JButton("-");
		JButton peoplePlusB = new JButton("+");
		JButton paperMinusB = new JButton("-");
		JButton paperPlusB = new JButton("+");
		JLabel peopleL = new JLabel("PEOPLE");
		JLabel paperL = new JLabel("PAPER");
		peopleCounterL = new JLabel(Integer.toString(state.getPeople()));
		paperCounterL = new JLabel(Integer.toString(state.getPaper()));
		JButton backB = new JButton("뒤로가기");
		JButton loadB = new JButton("불러오기");

		peopleMinusB.setFont(new Font("굴림", Font.BOLD, 15));
		peopleMinusB.setBounds(200, 150, 80, 80);
		peoplePlusB.setFont(new Font("굴림", Font.BOLD, 15));
		peoplePlusB.setBounds(520, 150, 80, 80);
		paperMinusB.setFont(new Font("굴림", Font.BOLD, 15));
		paperMinusB.setBounds(200, 350, 80, 80);
		paperPlusB.setFont(new Font("굴림", Font.BOLD, 15));
		paperPlusB.setBounds(520, 350, 80, 80);
		peopleL.setFont(new Font("굴림", Font.BOLD, 30));
		peopleL.setBounds(331, 54, 289, 88);
		paperL.setFont(new Font("굴림", Font.BOLD, 30));
		paperL.setBounds(340, 263, 289, 88);
		peopleCounterL.setFont(new Font("굴림", Font.BOLD, 30));
		peopleCounterL.setBounds(385, 180, 80, 35);
		paperCounterL.setFont(new Font("굴림", Font.BOLD, 30));
		paperCounterL.setBounds(385, 382, 68, 35);
		backB.setFont(new Font("굴림", Font.BOLD, 20));
		loadB.setFont(new Font("굴림", Font.BOLD, 20));
		loadB.setBounds(200, 455, 400, 60);
		backB.setBounds(200, 530, 400, 60);
		retrySaveB.setBounds(620, 530, 150, 60);
		retrySaveB.setVisible(false);

		add(peopleMinusB);
		add(peoplePlusB);
		add(paperMinusB);
		add(paperPlusB);
		add(peopleL);
		add(paperL);
		add(peopleCounterL);
		add(paperCounterL);
		add(backB);
		add(loadB);
		add(retrySaveB);
		loadB.addActionListener(e -> gameGui.showPanel("load"));
		retrySaveB.addActionListener(e -> { gameGui.saveProgress(); refreshValues(); });

		peopleMinusB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				state.setPeople(state.getPeople() - 1);
				refreshValues();
			}
		});

		peoplePlusB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				state.setPeople(state.getPeople() + 1);
				refreshValues();
			}
		});

		paperMinusB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				state.setPaper(state.getPaper() - 1);
				refreshValues();
			}
		});

		paperPlusB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				state.setPaper(state.getPaper() + 1);
				refreshValues();
			}
		});

		backB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				gameGui.showPanel("start");
			}
		});

	}
	/**
	 * 현재 인원, 모드 및 미저장 여부를 설정 화면에 반영한다.
	 */
	public void refreshValues() {
		peopleCounterL.setText(Integer.toString(state.getPeople()));
		paperCounterL.setText(Integer.toString(state.getPaper()));
		retrySaveB.setVisible(state.getHistory().isDirty());
		revalidate();
		repaint();
	}

}
