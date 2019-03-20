package dasniko.serverless.jbake;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@Slf4j
public class GitHookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        log.info("Received event: {}", request);

        String body = request.getBody();
        log.info("Request body: {}", body);

        String queueUrl = sqs.getQueueUrl(System.getenv("QUEUENAME")).getQueueUrl();
        sqs.sendMessage(queueUrl, body);

        return new APIGatewayProxyResponseEvent().withStatusCode(200);
    }
}
