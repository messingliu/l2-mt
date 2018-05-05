package com.tantan.l2.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tantan.l2.constants.LogConstants;
import com.tantan.l2.models.User;
import com.tantan.l2.models.UserFeatures;
import com.tantan.l2.utils.JacksonConverter;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;

import org.lognet.springboot.grpc.GRpcService;
import com.tantan.ranker.suggestedusersfeatures.SuggestedUserFeaturesGrpc;
import com.tantan.ranker.suggestedusersfeatures.*;
import com.tantan.ranker.suggestedusersfeatures.SuggestedUserFeaturesGrpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

public class RankerRpcClient {
    private final Logger LOGGER = LoggerFactory.getLogger(RankerRpcClient.class.getName());

    private final ManagedChannel channel;
    private final SuggestedUserFeaturesBlockingStub blockingStub;
    private final SuggestedUserFeaturesStub asyncStub;

    public RankerRpcClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public RankerRpcClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = SuggestedUserFeaturesGrpc.newBlockingStub(channel);
        asyncStub = SuggestedUserFeaturesGrpc.newStub(channel);
    }


    public List<UserFeatures> getRankerList(Long id, List<Long> candidateIds, String linearModelParameter, int rankerId) {
        UserRequest request = UserRequest.newBuilder()
                .setId(id)
                .addAllCandidateIds(() -> candidateIds.iterator())
                .setModelId(0)
                .setLinearModelParameter(linearModelParameter)
                .setTopK(100)
                .build();
        UserFeaturesResponse feature;
        try {
            feature = blockingStub.getSuggestedUserFeatures(request);
        } catch (Exception e) {
            LOGGER.warn("RPC failed: {}", e);
            return null;
        }
        List<UserFeatures> userFeaturesList = new ArrayList<>();
        for (int i = 0; i < feature.getFeatureInfoCount(); i ++) {
            UserFeatures oneUser = new UserFeatures();
            oneUser.setId(feature.getFeatureInfo(i).getId());
            oneUser.setFeatures(feature.getFeatureInfo(i).getFeatureDataList());
        }
        return userFeaturesList;
    }
}
