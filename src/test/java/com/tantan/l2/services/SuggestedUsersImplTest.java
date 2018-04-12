package com.tantan.l2.services;

import com.tantan.avro.*;
import com.tantan.l2.builders.UserInfoResponseBuilder;
import com.tantan.l2.builders.UserRespBuilder;
import com.tantan.l2.clients.MergerClient;
import com.tantan.l2.models.*;
import com.tantan.l2.relevance.SuggestedUserRanker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
public class SuggestedUsersImplTest {
  @TestConfiguration
  static class SuggestedUsersImplTestContextConfiguration {

    @Bean
    public SuggestedUsers suggestedUsers() {
      return new SuggestedUsersImpl();
    }
  }

  @Autowired
  private SuggestedUsers suggestedUsers;

  @MockBean
  public KafkaTemplate<Integer, KafkaTest> _kafkaTemplate;

  @MockBean
  public MergerClient _mergerClient;

  @MockBean
  public UserInfoService _userInfoService;

  @MockBean
  private SuggestedUserRanker _suggestedUserRanker;

  @Before
  public void setUp() {
    Resp userResp = new UserRespBuilder().buildUserResp();
    Mockito.when(_mergerClient.getUsers(any(), any(), any(), any(), any()))
        .thenReturn(CompletableFuture.completedFuture(userResp));

    Mockito.when(_suggestedUserRanker.getSuggestedUsers(anyLong(), any(), anyList(), anyInt()))
        .thenReturn(userResp.getData().getUsers().subList(0,1));

    Mockito.when(_kafkaTemplate.send(any(), any()))
        .thenReturn(null);

    Mockito.when(_userInfoService.getUserInfoResponse(anyLong(), anyString()))
        .thenReturn(new UserInfoResponseBuilder().build());
  }


  @Test
  public void testGetSuggestedUsers() throws ExecutionException, InterruptedException {
    User returnedUser = suggestedUsers.getSuggestedUsers(anyLong(), anyInt(), anyString(), anyString(), anyString())
                            .get()
                            .getData()
                            .getUsers()
                            .get(0);
    User expectedUser = new UserRespBuilder().buildRankedUserResp().getData().getUsers().get(0);
    assertThat(returnedUser.getId()).isEqualTo(expectedUser.getId());
  }
}
