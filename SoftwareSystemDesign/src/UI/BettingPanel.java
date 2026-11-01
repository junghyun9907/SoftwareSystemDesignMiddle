package UI;

import GameEngine.GameState;
import GameEngine.GameRound;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import GameEngine.DigitsOnlyFilter;

/**
 * 베팅액의 형식, 잔액 및 정산 한도를 검증한 뒤 최초 베팅을 한 번 차감한다.
 * @serial exclude
 */
public class BettingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField bettingField;
	private JLabel moneyL;
	private JLabel errorL;
	private GameState state;

	/**
	 * 현재 상태와 화면 컴포넌트를 초기화한다.
	 *
	 * @param gameGui 화면 전환과 저장을 담당하는 상위 창
	 * @param state 현재 게임의 공유 상태
	 */
	public BettingPanel(GameGui gameGui, GameState state) {
		setBackground(new Color(0, 128, 64));
		setLayout(null);
		this.state = state;
		bettingField = new JTextField();
		String placeHolder = "배팅 금액 (0원은 연습게임)";
		AbstractDocument document = (AbstractDocument) bettingField.getDocument();
		JButton backB = new JButton("뒤로 가기");
		moneyL = new JLabel("현금: " + String.format("%,d",state.getMoney()));
		errorL = new JLabel("");

		bettingField.setForeground(Color.GRAY);
		bettingField.setToolTipText("0원: 연습게임 / 숫자 입력 한도: " + String.format("%,d", Long.MAX_VALUE));
		bettingField.setFont(new Font("굴림", Font.BOLD, 15));
		bettingField.setBounds(250, 245, 302, 52);
		bettingField.setColumns(15);
		backB.setFont(new Font("굴림", Font.BOLD, 20));
		backB.setBounds(250, 390, 302, 98);
		moneyL.setHorizontalAlignment(SwingConstants.CENTER);
		moneyL.setVerticalAlignment(SwingConstants.CENTER);
		moneyL.setForeground(new Color(255, 255, 0));
		moneyL.setFont(new Font("굴림", Font.BOLD, 20));
		moneyL.setBounds(50, 132, 700, 52);
		errorL.setForeground(Color.RED);
		errorL.setFont(new Font("굴림", Font.BOLD, 14));
		errorL.setHorizontalAlignment(SwingConstants.CENTER);
		errorL.setBounds(50, 305, 700, 65);

		add(bettingField);
		add(backB);
		add(moneyL);
		add(errorL);

		mySetText(document, placeHolder);

		backB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				mySetText(document, placeHolder);
				errorL.setText("");
				bettingField.setForeground(Color.GRAY);
				gameGui.showPanel("start");
			}
		});

		bettingField.addFocusListener(new FocusAdapter() {
			/**
			 * focusGained 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			@Override
			public void focusGained(FocusEvent e) {
				if(!bettingField.getText().matches("\\d*")) {
					mySetText(document, "");
				}
				bettingField.setForeground(Color.BLACK);
			}
			/**
			 * focusLost 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			@Override
			public void focusLost(FocusEvent e) {
				if(bettingField.getText().isEmpty()) {
					mySetText(document, placeHolder);
					bettingField.setForeground(Color.GRAY);
				}
			}
		});

		bettingField.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				errorL.setText("");
				long bet;
				try {
					bet = Long.parseLong(bettingField.getText());
				}
				catch(NumberFormatException ex) {
					errorL.setText("0 ~ " + String.format("%,d", Long.MAX_VALUE) + " 사이의 정수를 입력하세요.");
					return;
				}
				if(bet < 0) {
					errorL.setText("베팅 금액은 0원 이상이어야 합니다. 0원은 연습게임입니다.");
					return;
				}
				if(bet > state.getMoney()) {
					errorL.setText("현금보다 더 베팅할 수 없습니다.");
					return;
				}
				if(bet > state.getMaxBetting()) {
					errorL.setText("승리 후 보유 한도를 넘습니다. 최대 베팅: " + String.format("%,d", state.getMaxBetting()) + "원");
					return;
				}
				if(!gameGui.saveProgress()) return;
				GameRound.placeBet(state, bet);
				mySetText(document, placeHolder);
				bettingField.setForeground(Color.GRAY);
				gameGui.startGame();
				gameGui.showPanel("ingame");
				
			}
		});

	}
	private void mySetText(AbstractDocument document, String msg) {
		document.setDocumentFilter(null);
		bettingField.setText(msg);
		document.setDocumentFilter(new DigitsOnlyFilter());
	}
	/**
	 * 현재 상태를 읽어 금액과 화면 정보를 갱신한다.
	 */
	public void refreshValue() {
		moneyL.setText("현금: " + String.format("%,d",state.getMoney()));
		errorL.setText("");
		revalidate();
		repaint();
	}
}
