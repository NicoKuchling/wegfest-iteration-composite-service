package com.nicokuchling.wegfest.iteration_composite_service.services;

import com.nicokuchling.wegfest.api.composite.ServiceAddresses;
import com.nicokuchling.wegfest.api.composite.iteration.IterationAggregate;
import com.nicokuchling.wegfest.api.core.iteration.Iteration;
import com.nicokuchling.wegfest.api.core.person.Person;
import com.nicokuchling.wegfest.api.core.scene.aggregates.SceneInteractionRecordAggregate;

import java.util.List;

public class IterationAggregateFactory {

    public static IterationAggregate from(
            Iteration iteration,
            Person person,
            List<SceneInteractionRecordAggregate> sceneInteractionRecordAggregates,
            String serviceAddress) {

        // Create info regarding the involved microservice addresses
        String iterationServiceAddress = iteration.getServiceAddress();
        String personServiceAddress = person.getServiceAddress();
        String sceneServiceAddress = sceneInteractionRecordAggregates.get(0).getServiceAddresses().toString();

        ServiceAddresses serviceAddresses = new ServiceAddresses(
                serviceAddress,
                iterationServiceAddress,
                personServiceAddress,
                sceneServiceAddress);

        IterationAggregate iterationAggregate = new IterationAggregate(
                iteration.getIterationId(),
                person,
                iteration.isCompleted(),
                iteration.isAborted(),
                iteration.getCompletedAt(),
                sceneInteractionRecordAggregates,
                serviceAddresses);

        return iterationAggregate;
    }
}
