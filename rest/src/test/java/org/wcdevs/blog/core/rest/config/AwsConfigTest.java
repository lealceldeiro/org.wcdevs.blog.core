package org.wcdevs.blog.core.rest.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderAsync;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderAsyncClientBuilder;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AwsConfigTest {
  @Test
  void awsIdProvider() {
    var region = UUID.randomUUID().toString();
    var awsCredentialsProvider = mock(AWSCredentialsProvider.class);

    try (var mockedAWSCognitoIdentityProviderAsyncClientBuilder
             = Mockito.mockStatic(AWSCognitoIdentityProviderAsyncClientBuilder.class)) {
      var identityProvider = mock(AWSCognitoIdentityProviderAsync.class);

      var clientBuilder = mock(AWSCognitoIdentityProviderAsyncClientBuilder.class);
      when(clientBuilder.withCredentials(awsCredentialsProvider)).thenReturn(clientBuilder);
      when(clientBuilder.withRegion(region)).thenReturn(clientBuilder);
      when(clientBuilder.build()).thenReturn(identityProvider);

      mockedAWSCognitoIdentityProviderAsyncClientBuilder
          .when(AWSCognitoIdentityProviderAsyncClientBuilder::standard).thenReturn(clientBuilder);

      var awsConfig = new AwsConfig();
      var cognitoIdentityProvider = awsConfig.awsIdProvider(region, awsCredentialsProvider);

      assertEquals(identityProvider, cognitoIdentityProvider);
    }
  }
}
