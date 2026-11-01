package GameEngine;

import java.util.List;

/**
 * 잔액과 최신순 최대 10개 기록을 함께 전달하는 불변 저장 데이터이다.
 *
 * @param money 원 단위의 보유 금액
 * @param logs 최신순 최대 10판의 기록
 */
public record SavedGame(long money, List<GameLogEntry> logs) {
    /**
     * 불변 데이터의 값과 일관성을 검증한다.
     * @throws IllegalArgumentException 값 또는 잔액과 기록이 올바르지 않은 경우
     */
    public SavedGame {
        logs = List.copyOf(logs);
        if (money < 0 || logs.size() > GameHistory.LIMIT
                || (!logs.isEmpty() && logs.get(0).balance() != money)) {
            throw new IllegalArgumentException("잔액과 게임 기록이 일치하지 않습니다.");
        }
    }
}
