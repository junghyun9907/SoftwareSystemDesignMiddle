package UI;

import javax.swing.JPanel;


import javax.swing.JToggleButton;
import GameEngine.*;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;

/**
	 * 2장 및 3장 섯다의 패 표시와 입력을 처리하고 GameRound에 진행과 정산을 위임한다.
 * @serial exclude
 */
public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	JButton callB;
	JButton challengeButton;
	JButton dieB;
	JButton backB;
	JButton restartB;
	boolean restart = false;
	boolean started = false;
	boolean finished = false;
	GameState state;
	JLabel moneyL;
	JLabel informationL;
	JToggleButton[] playerCards;
	private final GameGui gameGui;
	private final Engine engine;
	private final GameRound round;
	private boolean leftPanel;
	
	/**
	 * 현재 상태와 화면 컴포넌트를 초기화한다.
	 *
	 * @param gameGui 화면 전환과 저장을 담당하는 상위 창
	 * @param state 현재 게임의 공유 상태
	 */
	public GamePanel(GameGui gameGui, GameState state) {
		setBackground(new Color(0, 128, 64));
		setLayout(null);
		this.gameGui = gameGui;
		this.engine = new Engine(state);
		this.state = state;
		this.round = new GameRound(state, engine);
		moneyL = new JLabel("현금: " + String.format("%,d", state.getMoney()));
		JLabel winPrizeL = new JLabel("보상: " + String.format("%,d", round.getWinPrize()));
		if(state.getBetting() == 0) {
			winPrizeL.setText("연습게임 (0원)");
		}
		JLabel dieL = new JLabel("죽으면: " + String.format("%,d", round.getDieRefund()));
		informationL = new JLabel("패를 눌러 나눠주십시오.");
		JRadioButton bottomDealOption = new JRadioButton("밑장빼기");
		JButton[][] cards = new JButton[5][3];
		ImageIcon backIMG = loadCardImage("21");
		JButton card_1_01 = new JButton(backIMG);
		JButton card_1_02 = new JButton(backIMG);
		JButton card_3_01 = new JButton(backIMG);
		JButton card_3_02 = new JButton(backIMG);
		JButton card_2_01 = new JButton(backIMG);
		JButton card_2_02 = new JButton(backIMG);
		JButton card_4_01 = new JButton(backIMG);
		JButton card_4_02 = new JButton(backIMG);
		JToggleButton card1 = new JToggleButton(); 
		JToggleButton card2 = new JToggleButton();
		JToggleButton card3 = new JToggleButton();
		JToggleButton card4 = new JToggleButton();
		playerCards = new JToggleButton[] {card1, card2, card3, card4};
		JButton card_0_01 = new JButton(backIMG);
		JButton card_0_02 = new JButton(backIMG);
		JButton card_0_03 = new JButton(backIMG);
		JButton card_0_04 = new JButton(backIMG);
		JButton[] playerCardBacks = {card_0_01, card_0_02, card_0_03, card_0_04};
		restartB = new JButton("재경기");
		backB = new JButton("뒤로가기");
		callB = new JButton("콜");
		challengeButton = new JButton("밑장빼기냐");
		dieB = new JButton("다이");
		JButton startB = new JButton(backIMG);

		moneyL.setForeground(new Color(255, 255, 0));
		moneyL.setFont(new Font("굴림", Font.BOLD, 16));
		moneyL.setBounds(250, 98, 300, 24);
		winPrizeL.setForeground(Color.YELLOW);
		winPrizeL.setFont(new Font("굴림", Font.BOLD, 16));
		winPrizeL.setBounds(250, 128, 300, 24);
		dieL.setForeground(Color.YELLOW);
		dieL.setFont(new Font("굴림", Font.BOLD, 16));
		dieL.setBounds(250, 160, 300, 24);
		informationL.setFont(new Font("굴림", Font.BOLD, 14));
		informationL.setForeground(Color.WHITE);
		informationL.setBounds(250, 185, 280, 30);
		bottomDealOption.setBackground(new Color(0, 128, 64));
		bottomDealOption.setFont(new Font("굴림", Font.BOLD, 14));
		bottomDealOption.setBounds(345, 339, 85, 23);
		bottomDealOption.setVisible(state.getLastWinner() == 0);
		card_1_01.setBounds(61, 47, 77, 115);
		card_1_02.setBounds(150, 47, 77, 115);
		card_3_01.setBounds(61, 218, 77, 115);
		card_3_02.setBounds(150, 218, 77, 115);
		card_2_01.setBounds(559, 47, 77, 115);
		card_2_02.setBounds(648, 47, 77, 115);
		card_4_01.setBounds(559, 218, 77, 115);
		card_4_02.setBounds(648, 218, 77, 115);
		card1.setSelectedIcon(backIMG);
		card1.setVisible(false);
		card1.setBounds(293, 399, 77, 120);
		card1.setActionCommand("1");
		card2.setSelectedIcon(backIMG);
		card2.setBounds(382, 399, 77, 120);
		card2.setActionCommand("2");
		card2.setVisible(false);
		card3.setSelectedIcon(backIMG);
		card3.setBounds(471, 399, 77, 120);
		card3.setActionCommand("3");
		card3.setVisible(false);
		card4.setSelectedIcon(backIMG);
		card4.setBounds(560, 399, 77, 120);
		card4.setActionCommand("4");
		card4.setVisible(false);
		card_0_01.setBounds(293, 399, 77, 120);
		card_0_02.setBounds(382, 399, 77, 120);
		card_0_03.setBounds(471, 399, 77, 120);
		card_0_03.setVisible(false);
		card_0_04.setBounds(560, 399, 77, 120);
		card_0_04.setVisible(false);
		restartB.setBounds(77, 366, 150, 47);
		restartB.setVisible(false);
		backB.setVisible(false);
		callB.setBounds(77, 530, 150, 60);
		callB.setVisible(false);
		challengeButton.setBounds(325, 530, 150, 60);
		challengeButton.setVisible(false);
		dieB.setBounds(575, 530, 150, 60);
		dieB.setVisible(false);
		startB.setBounds(349, 218, 77, 115);
		backB.setBounds(77, 442, 150, 47);

		add(moneyL);
		add(winPrizeL);
		add(dieL);
		add(informationL);
		add(bottomDealOption);
		add(card_1_01);
		add(card_1_02);
		add(card_3_01);
		add(card_3_02);
		add(card_2_01);
		add(card_2_02);
		add(card_4_01);
		add(card_4_02);
		add(card1);
		add(card2);
		add(card3);
		add(card4);
		add(card_0_01);
		add(card_0_02);
		add(card_0_03);
		add(card_0_04);
		add(restartB);
		add(callB);
		add(challengeButton);
		add(dieB);
		add(startB);
		add(backB);

		cards[0][0] = card_0_01;
		cards[0][1] = card_0_02;
		cards[1][0] = card_1_01;
		cards[1][1] = card_1_02;
		cards[2][0] = card_2_01;
		cards[2][1] = card_2_02;
		cards[3][0] = card_3_01;
		cards[3][1] = card_3_02;
		cards[4][0] = card_4_01;
		cards[4][1] = card_4_02;
		if(state.getPaper() == 3) {
			moneyL.setBounds(250, 98, 280, 24);
			winPrizeL.setBounds(250, 128, 280, 24);
			dieL.setBounds(250, 160, 280, 24);
			for(int i = 1; i < 5; i++) {
				int x = i % 2 == 1 ? 10 : 535;
				int y = i <= 2 ? 47 : 218;
				cards[i][2] = new JButton(backIMG);
				cards[i][2].setVisible(false);
				add(cards[i][2]);
				for(int j = 0; j < 3; j++) {
					cards[i][j].setBounds(x + j * 80, y, 77, 115);
				}
			}
		}
		for(int i = 0; i < 5; i++) {
			for(int j = 0; j < 2; j++) {
				cards[i][j].setVisible(false);
			}
		}

		for(JToggleButton card : playerCards) {
			card.addActionListener(e -> {
				if(!started || finished) return;
				int discardCount = engine.getCardCount(0) - 2;
				if(card.isSelected() && discardCount == 1) {
					for(JToggleButton other : playerCards) {
						if(other != card) other.setSelected(false);
					}
				}
				if(selectedCardCount() > discardCount) {
					card.setSelected(false);
				}
			});
		}

		card_0_01.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!started) return;
				card1.setVisible(true);
				card_0_01.setVisible(false);
			}
		});

		card_0_02.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!started) return;
				card2.setVisible(true);
				card_0_02.setVisible(false);
			}
		});

		card_0_03.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!started || card3.getIcon() == null) return;
				card3.setVisible(true);
				card_0_03.setVisible(false);
			}
		});
		card_0_04.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!started || card4.getIcon() == null) return;
				card4.setVisible(true);
				card_0_04.setVisible(false);
			}
		});

		restartB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!finished || leftPanel) return;
				if(gameGui != null && !gameGui.saveProgress()) return;
				if(!restart) {
					if(state.getBetting() < 0 || state.getBetting() > state.getMaxBetting()) {
						started = false;
						leftPanel = true;
						gameGui.getContentPane().remove(gameGui.getInGamePanel());
						gameGui.showPanel("start");
						return;
					}
					GameRound.placeBet(state, state.getBetting());
				}
				started = false;
				leftPanel = true;
				gameGui.restartGame();
			}
		});

		backB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!finished || leftPanel) return;
				if(restart) abandonRound();
				if(gameGui != null) gameGui.saveProgress();
				started = false;
				leftPanel = true;
				gameGui.getContentPane().remove(gameGui.getInGamePanel());
				gameGui.showPanel("start");
			}
		});

		callB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!started || finished) return;
				int discardCount = engine.getCardCount(0) - 2;
				if(discardCount > 0) {
					if(selectedCardCount() != discardCount) {
						informationL.setText("버릴 카드 " + discardCount + "장을 선택하세요.");
						informationL.setVisible(true);
						return;
					}
				}
				int[] discards = new int[selectedCardCount()];
				int next = 0;
				for(int i = 0; i < engine.getCardCount(0); i++) {
					if(playerCards[i].isSelected()) discards[next++] = i;
				}
				GameRound.Result result = round.call(discards);
				for(int i = 0; i < playerCards.length; i++) {
					playerCardBacks[i].setVisible(false);
					playerCards[i].setVisible(playerCards[i].getIcon() != null);
				}
				openBotCards(engine, cards);
				showResult(result);

			}
		});

		challengeButton.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!started || finished || state.getLastWinner() == 0) return;
				GameRound.Result result = round.challenge();
				openBotCards(engine, cards);
				showResult(result);
			}
		});

		dieB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(!started || finished) return;
				showResult(round.fold());
			}
		});

		startB.addActionListener(new ActionListener() {
			/**
			 * actionPerformed 작업을 수행한다.
			 *
			 * @param e e 값
			 */
			public void actionPerformed(ActionEvent e) {
				if(started || finished) return;
				boolean useBottomDeal = bottomDealOption.isSelected();

				round.deal(useBottomDeal);
				started = true;
				for(int i = 0; i < engine.getCardCount(0); i++) {
					ImageIcon cardIMG = loadCardImage(engine.getIndex(0,i));
					playerCards[i].setIcon(cardIMG);
					playerCards[i].setDisabledIcon(cardIMG);
					playerCards[i].setSelectedIcon(backIMG);
					playerCards[i].setDisabledSelectedIcon(backIMG);
					playerCards[i].setEnabled(engine.getCardCount(0) > 2);
				}
				startB.setVisible(false);
				informationL.setVisible(false);
				bottomDealOption.setVisible(false);
				
				
				card_0_03.setVisible(engine.getCardCount(0) >= 3);
				card_0_04.setVisible(engine.getCardCount(0) == 4);
				callB.setVisible(true);
				challengeButton.setVisible(state.getLastWinner() != 0);
				dieB.setVisible(true);
				for(int i = 0; i < state.getPeople(); i++) {
					for(int j = 0; j < 2; j++) {
						cards[i][j].setVisible(true);
					}
					if(state.getPaper() == 3 && i > 0) {
						String openCard = engine.getIndex(i, 2);
						cards[i][2].setActionCommand(openCard);
						cards[i][2].setIcon(loadCardImage(openCard));
						cards[i][2].setVisible(true);
					}
				}
			}
		});

	}
	private void showResult(GameRound.Result result) {
		if(result == null) return;
		restart = result.replay();
		informationL.setText(result.message());
		macroVisible(restart);
		GameLogEntry entry = result.entry();
		if(entry != null) {
			informationL.setText("<html>" + result.message() + "<br>손익: " + GameLogEntry.signedMoney(entry.profit()) + "원</html>");
			if(gameGui != null) gameGui.saveProgress();
		}
	}

	/**
	 * 진행 중이거나 재경기 대기 중인 판을 베팅액 손실로 마무리한다. 이미 차감한 금액은 다시 차감하지 않는다.
	 */
	public void abandonRound() {
		GameRound.Result result = round.abandon();
		if(result == null) return;
		restart = false;
		macroVisible(false);
		informationL.setText(result.message() + " (베팅액 손실)");
	}

	private int selectedCardCount() {
		int selected = 0;
		for(JToggleButton card : playerCards) {
			if(card.isSelected()) selected++;
		}
		return selected;
	}

	private ImageIcon loadCardImage(String card) {
		java.net.URL resource = GamePanel.class.getResource("/images/" + card + ".png");
		if(resource == null) {
			throw new IllegalStateException("카드 이미지를 찾을 수 없습니다: " + card);
		}
		return new ImageIcon(resource);
	}

	private void openBotCards(Engine engine, JButton[][] cards) {
		for(int i = 1; i < state.getPeople(); i++) {
			String first = engine.getIndex(i, 0);
			String second = engine.getIndex(i, 1);
			if(state.getPaper() == 3) {
				String openCard = cards[i][2].getActionCommand();
				if(openCard.equals(first) || openCard.equals(second)) {
					first = openCard.equals(first) ? second : first;
					second = "21";
				}
				else {
					cards[i][2].setIcon(loadCardImage("21"));
				}
			}
			cards[i][0].setIcon(loadCardImage(first));
			cards[i][1].setIcon(loadCardImage(second));
		}
	}
	private void macroVisible(boolean restart) {
		finished = true;
		informationL.setBounds(250, 190, 280, 70);
		callB.setVisible(false);
		challengeButton.setVisible(false);
		dieB.setVisible(false);
		
		if(restart == false) {
			restartB.setText("다시하기");
		}

		moneyL.setText("현금: " + String.format("%,d", state.getMoney()));
		informationL.setVisible(true);
		for(int i = 0; i < playerCards.length; i++) {
			playerCards[i].setEnabled(false);
		}
		restartB.setVisible(restart || (state.getBetting() >= 0 && state.getBetting() <= state.getMaxBetting()));
		backB.setVisible(true);
	}
}
