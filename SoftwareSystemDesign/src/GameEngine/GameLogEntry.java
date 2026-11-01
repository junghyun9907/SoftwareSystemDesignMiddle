package GameEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 한 판의 확정 결과를 나타내는 불변 기록이다. 콜은 족보, 다른 행동은 받은 패를 표시한다.
 *
 * @param time 판 종료 시각. 분 단위로 보관
 * @param hand 콜의 확정 족보 또는 받은 패. 분배 전이면 null
 * @param action 콜, 다이, 밑장빼기냐 또는 중도 종료
 * @param profit 최초 베팅 전 대비 최종 순손익
 * @param balance 정산 후 최종 잔액
 * @param outcome 최종 결과 설명
 * @param people 사용자 포함 참가 인원수 2~5
 * @param paper 기본 패 수 2 또는 3
 * @param bet 최초 베팅액
 * @param cheated 재경기 포함 밑장빼기 발생 여부
 * @param replays 최종 종료 전 재경기 횟수
 */
public record GameLogEntry(LocalDateTime time, String hand, String action, long profit,
        long balance, String outcome, int people, int paper, long bet,
        boolean cheated, int replays) {
    private static final DateTimeFormatter TIME = DateTimeFormatter
            .ofPattern("uuuu:MM:dd:HH:mm", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);
    /**
     * 패 분배 전 종료한 판의 표시 문자열이다.
     */
    public static final String NO_CARDS = "분배 전";
    private static final Pattern LINE = Pattern.compile(
            "\\[(.{16})\\] - ([23])장 섯다( \\(밑장빼기\\))? - (.+?) - (콜|다이|밑장빼기냐|중도 종료)"
            + " - ([+-][0-9,]+)원 - 잔액 ([0-9,]+)원"
            + " \\| 결과=(.+?) \\| 인원=([2-5]) \\| 모드=([23]) \\| 베팅=([0-9]+)"
            + " \\| 밑장빼기=(true|false) \\| 재경기=([0-9]+)");

    /**
     * 불변 데이터의 값과 일관성을 검증한다.
     * @throws IllegalArgumentException 값 또는 잔액과 기록이 올바르지 않은 경우
     */
    public GameLogEntry {
        if (time == null || action == null || outcome == null || balance < 0 || bet < 0
                || people < 2 || people > 5 || paper < 2 || paper > 3 || replays < 0) {
            throw new IllegalArgumentException("잘못된 게임 기록입니다.");
        }
        time = time.truncatedTo(ChronoUnit.MINUTES);
        if (hand != null) validateText(hand);
        validateText(outcome);
        if (!action.matches("콜|다이|밑장빼기냐|중도 종료")) {
            throw new IllegalArgumentException("알 수 없는 행동입니다.");
        }
    }

    private static void validateText(String text) {
        if (text.isBlank() || text.contains("\n") || text.contains("\r") || text.contains("|")
                || text.contains(" - ")) throw new IllegalArgumentException("잘못된 기록 내용입니다.");
    }

    /**
     * 시각, 모드, 패, 행동, 부호 있는 순손익과 잔액을 화면 표시용 문자열로 반환한다.
     * @return 화면에 표시할 한 줄 로그
     */
    public String displayLine() {
        String mode = paper + "장 섯다" + (cheated ? " (밑장빼기)" : "");
        return "[" + TIME.format(time) + "] - " + mode + " - " + (hand == null ? NO_CARDS : hand)
                + " - " + action + " - " + signedMoney(profit) + "원 - 잔액 "
                + String.format(Locale.ROOT, "%,d", balance) + "원";
    }

    /**
     * 화면용 로그와 인원, 베팅, 결과 및 재경기 메타데이터를 저장용 한 줄로 직렬화한다.
     * @return 파일에 기록할 한 줄 문자열
     */
    public String toFileLine() {
        return displayLine() + " | 결과=" + outcome + " | 인원=" + people + " | 모드=" + paper
                + " | 베팅=" + bet + " | 밑장빼기=" + cheated + " | 재경기=" + replays;
    }

    /**
     * 금액을 부호와 세 자리 구분 기호를 포함한 문자열로 변환한다.
     *
     * @param value 원 단위의 부호 있는 금액
     * @return 예: +10,000 또는 -5,000 형식의 문자열
     */
    public static String signedMoney(long value) {
        return String.format(Locale.ROOT, "%+,d", value);
    }

    /**
     * 저장용 한 줄을 엄격하게 검증하고 게임 기록으로 변환한다.
     *
     * @param line toFileLine 형식의 저장 문자열
     * @return 검증된 불변 게임 기록
     * @throws IllegalArgumentException 저장 문자열이 올바르지 않은 경우
     */
    public static GameLogEntry parse(String line) {
        Matcher match = LINE.matcher(line);
        if (!match.matches()) throw new IllegalArgumentException("로그 형식이 올바르지 않습니다.");
        String hand = NO_CARDS.equals(match.group(4)) ? null : match.group(4);
        GameLogEntry entry = new GameLogEntry(LocalDateTime.parse(match.group(1), TIME), hand,
                match.group(5), Long.parseLong(match.group(6).replace(",", "")),
                Long.parseLong(match.group(7).replace(",", "")), match.group(8),
                Integer.parseInt(match.group(9)), Integer.parseInt(match.group(10)),
                Long.parseLong(match.group(11)), Boolean.parseBoolean(match.group(12)),
                Integer.parseInt(match.group(13)));
        if (!entry.toFileLine().equals(line)) throw new IllegalArgumentException("로그 형식이 변경되었습니다.");
        return entry;
    }
}
