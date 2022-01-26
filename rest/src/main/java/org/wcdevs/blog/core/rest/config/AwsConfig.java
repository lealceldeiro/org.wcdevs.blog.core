package org.wcdevs.blog.core.rest.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration to be applied only in an AWS environment.
 */
@Configuration
@Profile("aws")
public class AwsConfig {
  /**
   * Provides and AWS identity provider.
   *
   * @param reg                    AWS region.
   * @param awsCredentialsProvider {@link AWSCredentialsProvider} instance.
   *
   * @return An {@link AWSCognitoIdentityProvider} bean.
   */
  @Bean
  public AWSCognitoIdentityProvider awsIdProvider(@Value("${cloud.aws.region.static}") String reg,
                                                  AWSCredentialsProvider awsCredentialsProvider) {
    return AWSCognitoIdentityProviderAsyncClientBuilder.standard()
                                                       .withCredentials(awsCredentialsProvider)
                                                       .withRegion(reg)
                                                       .build();
  }
}
