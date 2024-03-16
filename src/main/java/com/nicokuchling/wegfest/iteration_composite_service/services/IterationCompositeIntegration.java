package com.nicokuchling.wegfest.iteration_composite_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicokuchling.wegfest.api.core.iteration.Iteration;
import com.nicokuchling.wegfest.api.core.person.Person;
import com.nicokuchling.wegfest.api.core.scene.aggregates.SceneInteractionRecordAggregate;
import com.nicokuchling.wegfest.api.exceptions.InvalidInputException;
import com.nicokuchling.wegfest.api.exceptions.NotFoundException;
import com.nicokuchling.wegfest.shared.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Component
public class IterationCompositeIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(IterationCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String iterationServiceUrl;
    private final String personServiceUrl;
    private final String sceneServiceUrl;

    @Autowired
    public IterationCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.iteration-service.host}") String iterationServiceHost,
            @Value("${app.iteration-service.port}") int iterationServicePort,
            @Value("${app.person-service.host}") String personServiceHost,
            @Value("${app.person-service.port}") int personServicePort,
            @Value("${app.scene-service.host}") String sceneServiceHost,
            @Value("${app.scene-service.port}") int sceneServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        this.iterationServiceUrl = "http://" + iterationServiceHost + ":" + iterationServicePort + "/wegfest";
        this.personServiceUrl = "http://" + personServiceHost + ":" + personServicePort + "/wegfest";
        this.sceneServiceUrl = "http://" + sceneServiceHost + ":" + sceneServicePort + "/wegfest";
    }

    public List<Iteration> getAllIterations() {

        try {
            String url = iterationServiceUrl + "/iteration";
            LOG.debug("Will call iteration API on URL: {}", url);

            ResponseEntity<List<Iteration>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {});

            List<Iteration> iterations = response.getBody();
            LOG.debug("{} iteration objects found.", iterations.size());

            return iterations;
        } catch(HttpClientErrorException ex) {

            LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
            LOG.warn("Error body: {}", ex.getResponseBodyAsString());
            throw ex;
        }
    }

    public Person getPersonById(int personId) {

        try {
            String url = personServiceUrl + "/person/" + personId;
            LOG.debug("Will call person API on URL: {}", url);

            Person person = restTemplate.getForObject(url, Person.class);
            LOG.debug("Found a person with id: {}", person.getPersonId());

            return person;
        } catch(HttpClientErrorException ex) {

            switch(HttpStatus.resolve(ex.getStatusCode().value())) {
                case NOT_FOUND -> throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY -> throw new InvalidInputException(getErrorMessage(ex));
                default -> {
                    LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                    LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                    throw ex;
                }
            }
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioException) {
            return ioException.getMessage();
        }
    }

    public List<SceneInteractionRecordAggregate> getSceneInteractionRecordAggregatesByIds(
            List<Integer> sceneInteractionRecordIds) {

        try {
            String url = buildUrlStringForGetRequestWithQueryParametersFor(sceneInteractionRecordIds);
            LOG.debug("Call scene API on URL: {}", url);

            ResponseEntity<List<SceneInteractionRecordAggregate>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {});

            List<SceneInteractionRecordAggregate> sceneInteractionRecords = response.getBody();
            LOG.debug("{} scene interaction records found.", sceneInteractionRecords.size());

            return sceneInteractionRecords;
        } catch(HttpClientErrorException ex) {

            LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
            LOG.warn("Error body: {}", ex.getResponseBodyAsString());
            throw ex;
        }
    }

    private String buildUrlStringForGetRequestWithQueryParametersFor(List<Integer> sceneInteractionRecordIds) {

        StringBuilder sb = new StringBuilder(sceneServiceUrl + "/scene/interaction/record?");
        for(int i = 0; i < sceneInteractionRecordIds.size(); i++) {

            sb.append("sceneInteractionRecordId=");
            sb.append(sceneInteractionRecordIds.get(i));

            if(i != sceneInteractionRecordIds.size() - 1)
                sb.append("&");
        }

        return sb.toString();
    }
}
