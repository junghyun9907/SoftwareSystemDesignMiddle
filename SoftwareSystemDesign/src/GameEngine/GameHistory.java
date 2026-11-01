package GameEngine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 한 판의 시작 금액과 재경기를 추적하고 종료 시 순손익을 계산하여 최신 10판을 유지한다.
 */
public final class GameHistory {
    /** 빈 기록 관리자를 생성한다. */
    public GameHistory() { }

    /**
     * 유지하는 최근 완료 기록의 최대 개수이다.
     */
    public static final int LIMIT = 10;
    private final List<GameLogEntry> logs = new ArrayList<>();
    private boolean active;
    private boolean dirty;
    private long startingMoney;
    private long bet;
    private int people;
    private int paper;
    private int replays;
    private boolean cheated;

    /**
     * 최초 베팅 전 금액과 설정을 보관한다. 활성 판이 있으면 재경기의 기준 금액을 유지한다.
     *
     * @param state 현재 게임의 공유 상태
     */
    public void beginRound(GameState state) {
        if (active) return;
        startingMoney = Math.addExact(state.getMoney(), state.getBetting());
        bet = state.getBetting();
        people = state.getPeople();
        paper = state.getPaper();
        replays = 0;
        cheated = false;
        active = true;
    }

    /**
     * 이번 분배의 밑장빼기 여부를 판 전체의 사용 여부에 누적한다.
     *
     * @param usedCheat 이번 분배의 실제 밑장빼기 사용 여부
     */
    public void noteDeal(boolean usedCheat) { cheated |= usedCheat; }
    /**
     * 진행 중인 판의 재경기 횟수를 한 번 증가시킨다.
     */
    public void noteReplay() { if (active) replays++; }
    /**
     * 아직 최종 결과가 기록되지 않은 판이 있는지 반환한다.
     * @return 조건을 만족하면 true
     */
    public boolean isActive() { return active; }
    /**
     * 파일에 저장하지 않은 최종 결과가 있는지 반환한다.
     * @return 조건을 만족하면 true
     */
    public boolean isDirty() { return dirty; }
    /**
     * 진행 중인 판의 순손익을 계산하고 기록을 한 번 추가한다. 활성 판이 없으면 null을 반환한다.
     *
     * @param state 현재 게임의 공유 상태
     * @param hand 콜의 확정 족보 또는 받은 패. 분배 전이면 null
     * @param action 콜, 다이, 밑장빼기냐 또는 중도 종료
     * @param outcome 최종 결과 설명
     * @return 새로 생성한 기록. 활성 판이 없으면 null
     */
    public GameLogEntry finish(GameState state, String hand, String action, String outcome) {
        if (!active) return null;
        GameLogEntry entry = new GameLogEntry(LocalDateTime.now(), hand, action,
                state.getMoney() - startingMoney, state.getMoney(), outcome,
                people, paper, bet, cheated, replays);
        logs.add(0, entry);
        if (logs.size() > LIMIT) logs.remove(logs.size() - 1);
        active = false;
        dirty = true;
        return entry;
    }

    /**
     * 완료된 잔액과 기록의 불변 저장 데이터를 생성한다.
     *
     * @param state 현재 게임의 공유 상태
     * @return 현재 잔액과 기록의 불변 복사본
     * @throws IllegalStateException 아직 진행 중인 판이 있는 경우
     */
    public SavedGame snapshot(GameState state) {
        if (active) throw new IllegalStateException("진행 중인 판은 최종 결과로 저장할 수 없습니다.");
        return new SavedGame(state.getMoney(), logs);
    }

    /**
     * 미저장 표시를 해제한다. 실제 파일 저장이 성공한 뒤 호출한다.
     */
    public void markSaved() { dirty = false; }

    /**
     * 진행 중이거나 미저장인 판이 없을 때 잔액과 기록을 복원하고 베팅액을 0으로 초기화한다.
     *
     * @param state 현재 게임의 공유 상태
     * @param saved 검증된 잔액과 최신순 기록
     * @throws IllegalStateException 진행 중이거나 미저장인 판이 있는 경우
     */
    public void restore(GameState state, SavedGame saved) {
        if (active || dirty) throw new IllegalStateException("진행 중이거나 미저장인 게임이 있습니다.");
        state.setMoney(saved.money());
        state.setBetting(0);
        logs.clear();
        logs.addAll(saved.logs());
    }
}
