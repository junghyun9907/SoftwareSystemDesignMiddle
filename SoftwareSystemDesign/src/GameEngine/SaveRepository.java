package GameEngine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * 잔액과 최근 기록을 UTF-8 파일로 읽고 원자적으로 교체한다. 손상된 기존 파일은 덮어쓰지 않는다.
 */
public final class SaveRepository {
    private static final String HEADER = "# KOREAN_POKER_SAVE_V1";
    private final Path file;

    /**
     * 지정한 디렉터리의 game-log.txt를 사용하는 저장소를 생성한다.
     *
     * @param logDirectory game-log.txt를 보관할 디렉터리
     */
    public SaveRepository(Path logDirectory) {
        file = logDirectory.toAbsolutePath().normalize().resolve("game-log.txt");
    }

    /**
     * game.dataDir, game.projectDir, 실행 클래스 위치 순서로 저장 위치를 정한다. JAR 단독 실행에는 JAR 옆의 data를 사용한다.
     * @return 실행 환경의 저장 디렉터리를 사용하는 저장소
     */
    public static SaveRepository forProject() {
        String dataDirectory = System.getProperty("game.dataDir");
        if (dataDirectory != null && !dataDirectory.isBlank()) {
            return new SaveRepository(Path.of(dataDirectory));
        }
        String configured = System.getProperty("game.projectDir");
        if (configured != null && !configured.isBlank()) {
            return new SaveRepository(Path.of(configured).resolve("data"));
        }
        try {
            Path location = Path.of(SaveRepository.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path base = Files.isRegularFile(location) ? location.getParent() : location;
            if (base.getFileName() != null && base.getFileName().toString().equals("bin")) {
                base = base.getParent();
            }
            return new SaveRepository(base.resolve("data"));
        } catch (URISyntaxException e) {
            throw new IllegalStateException("실행 파일 위치를 확인할 수 없습니다.", e);
        }
    }

    /**
     * 저장 파일을 검증하고 읽는다. 파일이 없으면 null이며 오류가 있으면 예외로 알리고 상태를 변경하지 않는다.
     * @return 검증된 저장 데이터. 파일이 없으면 null
     * @throws java.io.IOException 파일 형식, 읽기, 쓰기 또는 원자적 교체 오류가 발생한 경우
     */
    public SavedGame load() throws IOException {
        if (Files.notExists(file)) return null;
        if (Files.size(file) > 65536) throw new IOException("저장 파일의 크기가 허용 범위를 넘었습니다.");
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        try {
            if (lines.size() < 2 || lines.size() > GameHistory.LIMIT + 2
                    || !HEADER.equals(lines.get(0)) || !lines.get(1).matches("money=[0-9]+")) {
                throw new IllegalArgumentException("저장 파일 형식이 올바르지 않습니다.");
            }
            long money = Long.parseLong(lines.get(1).substring("money=".length()));
            List<GameLogEntry> logs = new ArrayList<>();
            for (int i = 2; i < lines.size(); i++) logs.add(GameLogEntry.parse(lines.get(i)));
            return new SavedGame(money, logs);
        } catch (RuntimeException e) {
            throw new IOException("저장 파일을 읽을 수 없습니다. 기존 파일은 유지됩니다. " + e.getMessage(), e);
        }
    }

    /**
     * 기존 파일을 검증한 뒤 임시 파일을 작성하고 원자적으로 교체한다. 쓰기 실패 시 기존 파일을 보존한다.
     *
     * @param saved 검증된 잔액과 최신순 기록
     * @throws java.io.IOException 파일 형식, 읽기, 쓰기 또는 원자적 교체 오류가 발생한 경우
     */
    public void save(SavedGame saved) throws IOException {
        load();
        Files.createDirectories(file.getParent());
        StringBuilder text = new StringBuilder(HEADER).append('\n').append("money=").append(saved.money()).append('\n');
        for (GameLogEntry entry : saved.logs()) text.append(entry.toFileLine()).append('\n');
        Path temporary = Files.createTempFile(file.getParent(), "game-log-", ".tmp");
        try {
            try (FileChannel channel = FileChannel.open(temporary, StandardOpenOption.WRITE)) {
                ByteBuffer bytes = StandardCharsets.UTF_8.encode(text.toString());
                while (bytes.hasRemaining()) channel.write(bytes);
                channel.force(true);
            }
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
