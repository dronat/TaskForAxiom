import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DeathLockTest {

    private Process mainProcess;
    private Process jstackProcess;


    @CsvSource({"true", "false"})
    @ParameterizedTest(name = "DeadLock test with param isUseDeadLock={0}")
    public void testDeadLock(String isUseDeadLock) throws IOException, InterruptedException {
        String path = Paths.get("").toAbsolutePath() + "\\target\\classes";
        mainProcess = new ProcessBuilder("java", "-cp", path, "ru.axiomjdk.Main", isUseDeadLock).start();

        ProcessBuilder jstackPB = new ProcessBuilder("jstack", "-l", String.valueOf(mainProcess.pid()))
                .redirectErrorStream(true);
        while (!mainProcess.waitFor(500, TimeUnit.MILLISECONDS)) {
            jstackProcess = jstackPB.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(jstackProcess.getInputStream()));
            String stackTrace = br.lines().collect(Collectors.joining("\n"));
            br.close();
            Matcher matcher = Pattern.compile("Found .+ Java-level deadlock(.*\n)+Found .+ deadlock")
                    .matcher(stackTrace);

            if (matcher.find()) {
                Assertions.fail("Fail info:\n" + matcher.group());
            } else {
                Thread.sleep(500);
            }
        }
    }

    @AfterEach
    public void afterEach() {
        mainProcess.destroy();
        jstackProcess.destroy();
    }
}
