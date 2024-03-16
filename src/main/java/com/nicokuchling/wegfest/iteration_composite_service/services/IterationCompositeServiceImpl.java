package com.nicokuchling.wegfest.iteration_composite_service.services;

import com.nicokuchling.wegfest.api.composite.ServiceAddresses;
import com.nicokuchling.wegfest.api.composite.iteration.IterationAggregate;
import com.nicokuchling.wegfest.api.composite.iteration.IterationCompositeService;
import com.nicokuchling.wegfest.api.core.iteration.Iteration;
import com.nicokuchling.wegfest.api.core.person.Person;
import com.nicokuchling.wegfest.api.core.scene.aggregates.SceneInteractionRecordAggregate;
import com.nicokuchling.wegfest.shared.http.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class IterationCompositeServiceImpl implements IterationCompositeService {

    private final ServiceUtil serviceUtil;
    private IterationCompositeIntegration integration;

    @Autowired
    public IterationCompositeServiceImpl(
            ServiceUtil serviceUtil,
            IterationCompositeIntegration integration) {

        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }


    @Override
    public List<IterationAggregate> getAllIterations() {

        List<Iteration> iterations = integration.getAllIterations();

        List<IterationAggregate> iterationAggregates = new ArrayList<>();
        for (Iteration iteration : iterations) {

            Person person = integration.getPersonById(iteration.getPersonId());

            List<Integer> sceneInteractionRecordIds = iteration.getSceneInteractionRecordIds();
            List<SceneInteractionRecordAggregate> sceneInteractionRecordAggregates =
                    integration.getSceneInteractionRecordAggregatesByIds(sceneInteractionRecordIds);

            IterationAggregate iterationAggregate = IterationAggregateFactory
                    .from(iteration, person, sceneInteractionRecordAggregates, serviceUtil.getServiceAddress());

            iterationAggregates.add(iterationAggregate);
        }

        return iterationAggregates;
    }
}
