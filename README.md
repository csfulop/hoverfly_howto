# Hoverfly HOWTO

## Fake JSON API for testing

Based on: https://medium.com/@dotronglong/set-up-fake-api-in-minutes-with-docker-dfffebe264b0

Run this docker to create a fake JSON REST API:
```
docker run --rm -p 3030:3030 -v $PWD/src/componentTest/resources/mocks:/app/mocks dotronglong/faker:stable
curl -s http://localhost:3030/v1/users | jq .
```

Tests use org.testcontainers to automatically start the faker container.

Our app will query this fake JSON REST API.

The response will be catched by Hoverfly.

Then Hoverfly can replay the response and the test will work even after docker is stopped.
