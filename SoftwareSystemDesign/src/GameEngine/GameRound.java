package GameEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Swing과 독립적으로 한 번의 분배, 패 선택, 정산 및 기록 생성을 처리한다.
 * 재경기는 새 GameRound를 생성하되 GameHistory의 최초 베팅 정보를 유지한다.
 */
public final class GameRound {
    private final GameState state;
    private final Engine engine;
    private final long bet;
    private final long winPrize;
    private boolean dealt;
    private boolean finished;
    private boolean replay;
    private String dealtCards;

    /**
     * 한 번의 행동 결과를 화면에 전달한다. 재경기는 완료 기록을 생성하지 않는다.
     * @param replay 재경기 대기 여부
     * @param message 화면에 표시할 결과 설명
     * @param entry 완료 기록. 재경기이면 null
     */
    public record Result(boolean replay, String message, GameLogEntry entry) { }

    /**
     * 최초 베팅이 이미 차감된 상태로 분배를 준비한다.
     * @param state 공유 게임 상태
     * @param engine 같은 상태를 사용하는 패 계산 엔진
     */
    public GameRound(GameState state, Engine engine) {
        this.state = state;
        this.engine = engine;
        bet = state.getBetting();
        winPrize = Math.multiplyExact(bet, state.getPeople());
        state.getHistory().beginRound(state);
    }

    /**
     * 새 판의 베팅을 검증하고 한 번 차감한다. 재경기에서는 호출하지 않는다.
     * @param state 공유 게임 상태
     * @param bet 원 단위 베팅액
     * @throws IllegalArgumentException 베팅액이 잔액 또는 정산 한도를 벗어난 경우
     * @throws IllegalStateException 진행 중이거나 미저장인 판이 있는 경우
     */
    public static void placeBet(GameState state, long bet) {
        if (state.getHistory().isActive() || state.getHistory().isDirty()) {
            throw new IllegalStateException("기존 판을 마무리하고 저장해야 합니다.");
        }
        if (bet < 0 || bet > state.getMaxBetting()) {
            throw new IllegalArgumentException("베팅액이 허용 범위를 벗어났습니다.");
        }
        state.setBetting(bet);
        state.setMoney(state.getMoney() - bet);
    }

    /**
     * 베팅 원금을 포함한 승리 지급액을 반환한다.
     * @return 전체 참가자의 베팅액 합계
     */
    public long getWinPrize() { return winPrize; }

    /**
     * 다이 시 반환할 금액을 반환한다.
     * @return 최초 베팅액의 절반. 소수점 이하는 버림
     */
    public long getDieRefund() { return bet / 2; }

    /**
     * 덱을 섞고 분배하며 실제 밑장빼기 여부와 받은 패를 보관한다.
     * @param useBottomDeal 사용자 담당 판의 밑장빼기 선택
     * @throws IllegalStateException 이미 분배하거나 종료한 경우
     */
    public void deal(boolean useBottomDeal) {
        if (dealt || finished) throw new IllegalStateException("이미 분배하거나 종료한 판입니다.");
        engine.makeDeck();
        engine.distribute(useBottomDeal);
        dealt = true;
        state.getHistory().noteDeal(engine.isBottomDealUsed());
        List<String> names = new ArrayList<>();
        for (int i = 0; i < engine.getCardCount(0); i++) {
            int card = Integer.parseInt(engine.getIndex(0, i));
            String name = ((card - 1) % 10 + 1) + "월";
            if (card == 11 || card == 13 || card == 18) name += "(광)";
            names.add(name);
        }
        dealtCards = String.join(", ", names);
    }

    /**
     * 버릴 패를 검증한 뒤 승자를 판정하고 정산한다. 중복 호출은 무시한다.
     * @param discards 버릴 패의 0부터 시작하는 위치
     * @return 행동 결과. 분배 전이거나 이미 종료했으면 null
     * @throws IllegalArgumentException 버릴 패의 개수, 위치 또는 중복이 잘못된 경우
     */
    public Result call(int... discards) {
        if (!canAct()) return null;
        int count = engine.getCardCount(0);
        int[] sorted = discards.clone();
        Arrays.sort(sorted);
        if (sorted.length != count - 2) throw new IllegalArgumentException("두 장이 남도록 선택해야 합니다.");
        for (int i = 0; i < sorted.length; i++) {
            if (sorted[i] < 0 || sorted[i] >= count || (i > 0 && sorted[i] == sorted[i - 1])) {
                throw new IllegalArgumentException("버릴 패의 위치가 잘못되었습니다.");
            }
        }
        for (int i = sorted.length - 1; i >= 0; i--) engine.selectingCard(0, sorted[i]);
        engine.convertIndex(0, 0, 0);
        selectBotCards();
        engine.whoIsWinner();
        replay = state.getWinner() == 6;
        if (replay) {
            finished = true;
            state.getHistory().noteReplay();
            return new Result(true, "재경기", null);
        }
        if (state.getWinner() == 0) payWin();
        String outcome = state.getWinner() == 0 ? "플레이어가 승리" : state.getWinner() + "번 봇이 승리";
        String hand = engine.handName(Integer.parseInt(engine.getIndex(0, 0)),
                Integer.parseInt(engine.getIndex(0, 1)));
        return finish("콜", outcome, hand);
    }

    /**
     * 담당 봇의 밑장빼기 여부로 승패를 정하고 정산한다.
     * @return 행동 결과. 행동할 수 없거나 사용자 담당 판이면 null
     */
    public Result challenge() {
        if (!canAct() || state.getLastWinner() == 0) return null;
        int dealer = state.getLastWinner();
        String outcome;
        if (engine.isBottomDealUsed()) {
            state.setWinner(0);
            payWin();
            outcome = dealer + "번 봇 밑장빼기 적발! 승리";
        } else {
            state.setWinner(dealer);
            outcome = "적발 실패! " + dealer + "번 봇 승리";
        }
        selectBotCards();
        return finish("밑장빼기냐", outcome, dealtCards);
    }

    /**
     * 베팅액의 절반을 반환하고 다음 담당자를 1번 봇으로 정한다.
     * @return 행동 결과. 분배 전이거나 이미 종료했으면 null
     */
    public Result fold() {
        if (!canAct()) return null;
        state.setMoney(Math.addExact(state.getMoney(), getDieRefund()));
        state.setWinner(1);
        Result result = finish("다이", "다이", dealtCards);
        return new Result(false, "다이! 다음 담당은 1번 봇", result.entry());
    }

    /**
     * 진행 중인 판 또는 재경기를 포기한다. 이미 차감한 베팅액을 다시 차감하지 않는다.
     * @return 행동 결과. 완료된 판이면 null
     */
    public Result abandon() {
        if ((finished && !replay) || !state.getHistory().isActive()) return null;
        String outcome = replay ? "재경기 포기" : "진행 중 종료";
        replay = false;
        return finish("중도 종료", outcome, dealtCards);
    }

    private boolean canAct() { return dealt && !finished; }

    private void selectBotCards() {
        for (int i = 1; i < state.getPeople(); i++) engine.selectBotCards(i);
    }

    private void payWin() { state.setMoney(Math.addExact(state.getMoney(), winPrize)); }

    private Result finish(String action, String outcome, String hand) {
        finished = true;
        return new Result(false, outcome, state.getHistory().finish(state, hand, action, outcome));
    }
}
