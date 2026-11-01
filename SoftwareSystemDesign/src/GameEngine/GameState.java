package GameEngine;

/**
 * 잔액, 인원, 게임 모드, 베팅액, 승자 및 최근 기록 관리자를 보유하는 게임에서 공유되는 상태이다.
 */
public class GameState {
	private long money; 
	private int people; 
	private int paper;
	private int winner;
	private int lastWinner = 0;
	private long betting;
	private final GameHistory history = new GameHistory();

	/**
	 * 해당 실행에서 이용되는 게임 기록 객체를 반환한다.
	 * @return 게임 기록 관리자
	 */
	public GameHistory getHistory() { return history; }
	
	/**
	 * 보유 금액, 참가 인원 및 모드를 초기화한다. 기본 생성자는 1,000,000원, 2명, 2장으로 시작한다.
	 */
	public GameState(){
		this(1000000, 2, 2);
	}
	/**
	 * 보유 금액, 참가 인원 및 모드를 초기화한다. 기본 생성자는 1,000,000원, 2명, 2장으로 시작한다.
	 *
	 * @param money 원 단위의 보유 금액. 음수는 0원으로 보정
	 * @param people 사용자 포함 참가 인원수 2~5
	 * @param paper 기본 패 수 2 또는 3
	 * @throws IllegalArgumentException 인원 또는 기본 패 수가 허용 범위를 벗어난 경우
	 */
	public GameState(long money, int people, int paper){
		if(people < 2 || people > 5 || paper < 2 || paper > 3) {
			throw new IllegalArgumentException("인원은 2~5명, 기본 패 수는 2~3장이어야 합니다.");
		}
		setMoney(money);
		this.people = people;
		this.paper = paper;
		this.winner = 0;
	}
	
	/**
	 * 현재 보유 금액을 반환한다.
	 * @return 현재 상태의 값
	 */
	public long getMoney() {
		return money;
	}
	/**
	 * 사용자를 포함한 참가 인원수를 반환한다.
	 * @return 현재 상태의 값
	 */
	public int getPeople() {
		return people;
	}
	/**
	 * 참가자 기본 패 수인 2 또는 3을 반환한다.
	 * @return 현재 상태의 값
	 */
	public int getPaper() {
		return paper;
	}
	/**
	 * 최종 승자 번호 또는 재경기 값 6을 반환한다.
	 * @return 현재 상태의 값
	 */
	public int getWinner() {
		return winner;
	}
	/**
	 * 다음 패 분배 담당자 번호를 반환한다. 재경기에는 기존 담당자를 유지한다.
	 * @return 현재 상태의 값
	 */
	public int getLastWinner() {
		return lastWinner;
	}
	/**
	 * 현재 판의 최초 베팅액을 반환한다.
	 * @return 현재 상태의 값
	 */
	public long getBetting() {
		return betting;
	}
	/**
	 * 현재 잔액과 승리 후 long 범위 초과를 고려한 최대 베팅액을 반환한다.
	 * @return 승리 정산 시 long 범위를 넘지 않는 최대 베팅액
	 */
	public long getMaxBetting() {
		return Math.min(money, (Long.MAX_VALUE - money) / (people - 1));
	}
	
	/**
	 * 보유 금액을 변경한다. 음수 입력은 0원으로 보정한다.
	 *
	 * @param money 원 단위의 보유 금액
	 */
	public void setMoney(long money) {
		this.money = money;
		if(this.money < 0) {
			this.money = 0;
		}
	}
	/**
	 * 2~5명 범위의 인원을 적용한다. 담당자가 범위를 벗어나면 사용자로 초기화한다.
	 *
	 * @param people 사용자 포함 참가 인원수 2~5
	 */
	public void setPeople(int people) {
		if(2 <= people && people <= 5) {
			this.people = people;
			if(lastWinner >= people) {
				lastWinner = 0;
			}
		}
	}
	/**
	 * 2 또는 3인 기본 패 수만 적용한다.
	 *
	 * @param paper 기본 패 수 2 또는 3
	 */
	public void setPaper(int paper) {
		if(2 <= paper && paper <= 3) {
			this.paper = paper;
		}
	}
	/**
	 * 판정 결과를 설정한다. 유효한 참가자 번호이면 다음 분배 담당자도 갱신한다.
	 *
	 * @param winner 승자 번호 0~인원수-1 또는 재경기 값 6
	 */
	public void setWinner(int winner) {
		this.winner = winner;
		if(0 <= winner && winner < people) {
			lastWinner = winner;
		}
	}
	/**
	 * 현재 판의 베팅액을 설정한다. 호출할 때 입력과 정산 한도를 먼저 검증해야 한다.
	 *
	 * @param betting 검증된 원 단위 베팅액
	 */
	public void setBetting(long betting) {
		this.betting = betting;
	}
	
	
	
	
}
