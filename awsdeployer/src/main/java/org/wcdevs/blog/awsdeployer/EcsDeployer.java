package org.wcdevs.blog.awsdeployer;

import static java.util.Map.entry;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.wcdevs.blog.cdk.ApplicationEnvironment;
import org.wcdevs.blog.cdk.CognitoStack;
import org.wcdevs.blog.cdk.Database;
import org.wcdevs.blog.cdk.ElasticContainerService;
import org.wcdevs.blog.cdk.Network;
import org.wcdevs.blog.cdk.Util;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.constructs.Construct;

/**
 * Deploys the core app to an Ecs instance.
 */
public class EcsDeployer {
  /**
   * Name of the stack created for the AWS ECS.
   */
  private static final String SERVICE_STACK_NAME = "be-service-stack";

  /**
   * Entry point for execution.
   *
   * @param args CI arguments for AWS deployment.
   */
  public static void main(String[] args) {
    var app = new App();

    String accountId = Util.getValueInApp("accountId", app);
    String region = Util.getValueInApp("region", app);
    String applicationName = Util.getValueOrDefault("applicationName", app, "core");

    String environmentName = Util.getValueInApp("environmentName", app);
    var springProfile = Util.getValueOrDefault("springProfile", app, "aws");

    String dockerRepositoryName = Util.getValueInApp("dockerRepositoryName", app, false);
    String dockerImageTag = Util.getValueInApp("dockerImageTag", app, false);
    String dockerImageUrl = Util.getValueInApp("dockerImageUrl", app, false);

    var appListenPort = Util.getValueOrDefault("appPort", app, "8080");
    var appHealthCheckPath = Util.getValueOrDefault("healthCheckPath", app, "/");
    var appHealthCheckPort = Util.getValueOrDefault("healthCheckPort", app, "8080");
    // origins can be comma separated. i.e.: https://api.wcdevs.org,https://wcdevs.org
    var commaSeparatedAllowedOrigins = Util.getValueOrDefault("allowedOrigins", app, "/**");
    var cognitoScopes = Util.getValueOrDefault("cognitoScopes", app, "openid, profile, email");

    var awsEnvironment = Util.environmentFrom(accountId, region);
    var appEnv = new ApplicationEnvironment(applicationName, environmentName);

    var serviceStack = serviceStack(app, appEnv, awsEnvironment);
    var parametersStack = parametersStack(app, appEnv, awsEnvironment);

    var dbOutputParams = Database.outputParametersFrom(parametersStack, appEnv);
    var cognitoParams = CognitoStack.getOutputParameters(parametersStack,
                                                         appEnv.getEnvironmentName());

    var commonEnvVar = commonEnvVars(region, environmentName, springProfile, appListenPort,
                                     appHealthCheckPort, commaSeparatedAllowedOrigins);
    var dbEnvVar = dbEnvVars(serviceStack, dbOutputParams);
    var cognitoEnvVar = cognitoEnvVars(serviceStack, appEnv, cognitoParams, cognitoScopes);

    var environmentVariables = environmentVariables(commonEnvVar, dbEnvVar, cognitoEnvVar);
    var secGroupIdsToGrantIngressFromEcs = secGroupIdAccessFromEcs(dbOutputParams);
    var dockerImage = dockerImage(dockerRepositoryName, dockerImageTag, dockerImageUrl);
    var inputParameters = inputParameters(dockerImage, environmentVariables, appListenPort,
                                          appHealthCheckPath, appHealthCheckPort,
                                          secGroupIdsToGrantIngressFromEcs);

    var networkOutputParameters = Network.outputParametersFrom(serviceStack,
                                                               appEnv);

    ElasticContainerService.newInstance(serviceStack, "BEECServiceApp", awsEnvironment,
                                        appEnv, inputParameters,
                                        networkOutputParameters);
    app.synth();
  }

  private static Stack serviceStack(App app, ApplicationEnvironment applicationEnvironment,
                                    Environment awsEnvironment) {
    var serviceStackName = applicationEnvironment.prefixed(SERVICE_STACK_NAME);
    return new Stack(app, "BEServiceStack", StackProps.builder()
                                                      .stackName(serviceStackName)
                                                      .env(awsEnvironment)
                                                      .build());
  }

  private static Stack parametersStack(App app, ApplicationEnvironment applicationEnvironment,
                                       Environment awsEnvironment) {
    var timeId = System.currentTimeMillis();
    var paramsStackName = Util.joinedString(Util.DASH_JOINER, "parameters",
                                            SERVICE_STACK_NAME, timeId);
    var prefixedParamsStackName = applicationEnvironment.prefixed(paramsStackName);

    var stackProps = StackProps.builder()
                               .stackName(prefixedParamsStackName)
                               .env(awsEnvironment)
                               .build();
    return new Stack(app, prefixedParamsStackName, stackProps);
  }

  static Map<String, String> cognitoEnvVars(Stack scope, ApplicationEnvironment appEnv,
                                            CognitoStack.OutputParameters cognitoParams,
                                            String scopes) {
    var cognitoClientSecret = CognitoStack.getUserPoolClientSecret(scope, appEnv);
    var cognitoClientSecretValue
        = cognitoClientSecret.secretValueFromJson(CognitoStack.USER_POOL_CLIENT_SECRET_HOLDER)
                             .toString();
    var cognitoClientId
        = cognitoClientSecret.secretValueFromJson(CognitoStack.USER_POOL_CLIENT_ID_HOLDER)
                             .toString();
    var cognitoClientName
        = cognitoClientSecret.secretValueFromJson(CognitoStack.USER_POOL_CLIENT_NAME_HOLDER)
                             .toString();

    return Map.ofEntries(entry("COGNITO_PROVIDER_URL", cognitoParams.getProviderUrl()),
                         entry("COGNITO_CLIENT_ID", cognitoClientId),
                         entry("COGNITO_CLIENT_NAME", cognitoClientName),
                         entry("COGNITO_CLIENT_SECRET", cognitoClientSecretValue),
                         entry("COGNITO_SCOPES", scopes));
  }

  static Map<String, String> environmentVariables(Map<String, String> commonEnvVar,
                                                  Map<String, String> dbEnvVar,
                                                  Map<String, String> cognitoEnvVar) {
    return Stream.concat(commonEnvVar.entrySet().stream(),
                         Stream.concat(dbEnvVar.entrySet().stream(),
                                       cognitoEnvVar.entrySet().stream()))
                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private static List<String> secGroupIdAccessFromEcs(Database.OutputParameters dbOutput) {
    return List.of(dbOutput.getDbSecurityGroupId());
  }

  private static Map<String, String> commonEnvVars(String awsRegion, String environmentName,
                                                   String springProfile, String listenPort,
                                                   String healthCheckPort, String allowedOrigins) {
    return Map.ofEntries(entry("SERVER_PORT", listenPort),
                         entry("MANAGEMENT_SERVER_PORT", healthCheckPort),
                         entry("ENVIRONMENT_NAME", environmentName),
                         entry("SPRING_PROFILES_ACTIVE", springProfile),
                         entry("AWS_REGION", awsRegion),
                         entry("ALLOWED_ORIGINS", allowedOrigins));
  }

  private static Map<String, String> dbEnvVars(Construct scope,
                                               Database.OutputParameters dbOutput) {
    var dbEndpointAddress = dbOutput.getEndpointAddress();
    var dbEndpointPort = dbOutput.getEndpointPort();
    var dbName = dbOutput.getDbName();
    var springDataSourceUrl = String.format("jdbc:postgresql://%s:%s/%s",
                                            dbEndpointAddress, dbEndpointPort, dbName);

    var dbSecret = Database.getDataBaseSecret(scope, dbOutput);
    var dbUsername = dbSecret.secretValueFromJson(Database.USERNAME_SECRET_HOLDER).toString();
    var dbPassword = dbSecret.secretValueFromJson(Database.PASSWORD_SECRET_HOLDER).toString();

    return Map.ofEntries(entry("SPRING_DATASOURCE_DRIVERCLASSNAME", "org.postgresql.Driver"),
                         entry("SPRING_DATASOURCE_URL", springDataSourceUrl),
                         entry("SPRING_DATASOURCE_USERNAME", dbUsername),
                         entry("SPRING_DATASOURCE_PASSWORD", dbPassword));
  }

  private static ElasticContainerService.DockerImage dockerImage(String dockerRepositoryName,
                                                                 String dockerImageTag,
                                                                 String dockerImageUrl) {
    return ElasticContainerService.DockerImage.builder()
                                              .dockerRepositoryName(dockerRepositoryName)
                                              .dockerImageTag(dockerImageTag)
                                              .dockerImageUrl(dockerImageUrl)
                                              .build();
  }

  private static ElasticContainerService.InputParameters inputParameters(
      ElasticContainerService.DockerImage dockerImage, Map<String, String> envVariables,
      String appPort, String healthCheckPath, String healthCheckPort,
      List<String> securityGroupIdsGrantIngressFromEcs) {

    var defaultPort = 8080;
    return ElasticContainerService.InputParameters
        .builder()
        .dockerImage(dockerImage)
        .environmentVariables(envVariables)
        .securityGroupIdsToGrantIngressFromEcs(securityGroupIdsGrantIngressFromEcs)
        .taskRolePolicyStatements(taskRolePolicyStatements())
        .applicationPort(intValueFrom(appPort, defaultPort))
        .healthCheckPort(intValueFrom(healthCheckPort, defaultPort))
        .healthCheckPath(healthCheckPath)
        .healthCheckIntervalSeconds(60)
        .desiredInstancesCount(1)
        .build();
  }

  private static int intValueFrom(String rawValue, int defaultIfError) {
    try {
      return Integer.parseInt(rawValue);
    } catch (Exception ignored) {
      // let's use the default one
    }
    return defaultIfError;
  }

  private static List<PolicyStatement> taskRolePolicyStatements() {
    var sqsActions = List.of("sqs:DeleteMessage", "sqs:GetQueueUrl",
                             "sqs:ListDeadLetterSourceQueues", "sqs:ListQueues",
                             "sqs:ListQueueTags", "sqs:ReceiveMessage", "sqs:SendMessage",
                             "sqs:ChangeMessageVisibility", "sqs:GetQueueAttributes");
    return List.of(policyStatement(sqsActions),
                   policyStatement("cognito-idp:*"),
                   policyStatement("ses:*"),
                   policyStatement("dynamodb:*"),
                   policyStatement("cloudwatch:PutMetricData"));
  }

  private static PolicyStatement policyStatement(String action) {
    return policyStatement(List.of(action));
  }

  private static PolicyStatement policyStatement(List<String> actions) {
    return PolicyStatement.Builder.create()
                                  .effect(Effect.ALLOW)
                                  .resources(List.of("*"))
                                  .actions(actions)
                                  .build();
  }
}
