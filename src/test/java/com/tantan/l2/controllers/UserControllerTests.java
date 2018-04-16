/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tantan.l2.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tantan.l2.builders.UserRespBuilder;
import com.tantan.l2.models.Resp;
import com.tantan.l2.models.User;
import com.tantan.l2.relevance.SuggestedUserRanker;
import com.tantan.l2.services.SuggestedUsers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SuggestedUsers _suggestedUsers;

  Resp _userResp;

  @Before
  public void setUp() {
    _userResp = new UserRespBuilder().buildUserResp();
    Mockito.when(_suggestedUsers.getSuggestedUsers(any(), any(), any(), any(), any()))
        .thenReturn(CompletableFuture.completedFuture(_userResp));
  }

  @Test
  public void testReturnSuggestedUser() throws Exception {
    User user = new User().setId(1L).setDistance(1).setLastactivity("none").setPopularity(22).setScore(3).setType("type");
    List<User> userList = new ArrayList<User>();
    userList.add(user);
    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<String, String>();
    paramMap.add("user_id", "1");
    paramMap.add("limit", "10");
    paramMap.add("search", "search_val");
    paramMap.add("filter", "filter_val");
    paramMap.add("with", "with_val");

    MvcResult result = mockMvc.perform(get("/users").params(paramMap)).andReturn();
    this.mockMvc.perform(asyncDispatch(result))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.users[0].popularity")
                       .value(_userResp.getData().getUsers().get(0).getPopularity()));
  }

}
