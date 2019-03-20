package dasniko.serverless.jbake;

import com.amazonaws.jmespath.ObjectMapperSingleton;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;

import java.io.File;
import java.util.Map;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Slf4j
public class BakeHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    @SneakyThrows
    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    public Void handleRequest(SQSEvent event, Context context) {
        log.info("Received event: {}", event);

        String body = event.getRecords().get(0).getBody();
        log.info("Record body: {}", body);

        Map<String, Object> payload = ObjectMapperSingleton.getObjectMapper()
                .readValue(body, new TypeReference<Map<String, Object>>(){});

        Map<String, String> repository = (Map<String, String>) payload.get("repository");
        String name = repository.get("name");
        String cloneUrl = repository.get("clone_url");

        File dir = new File("/tmp/" + name);
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            FileUtils.delete(dir, FileUtils.RECURSIVE);
        }

        try {
            Git.cloneRepository().setURI(cloneUrl).setDirectory(dir).call();
        } catch (GitAPIException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

}
