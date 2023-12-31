/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.cloudevents;

import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.drools.cloudevents.JsonUtil.createEvent;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class CloudEventIT {

/*
curl -v http://localhost:8080/drools/evaluate \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: User" \
  -H "Ce-Source: io.cloudevents.examples/user" \
  -H "Ce-Id: 536808d3-88be-4077-9d7a-a3f162705f78" \
  -H "Content-Type: application/json" \
  -H "Ce-Subject: SUBJ-0001" \
  -d '{"id":"color","val":"red"}'
*/

    @Test
    public void test() {
        Measurement mRed = new Measurement("color", "red");
        CloudEvent cloudEvent = createEvent(mRed);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .headers(getHeaders(cloudEvent))
                .accept(ContentType.JSON)
                .body(cloudEvent.getData().toBytes())
                .when()
                .post("/drools/evaluate")
                .then()
                .statusCode(200)
                .body(equalTo("{\"color\":\"red\"}"));
    }

    private Map<String, Object> getHeaders(CloudEvent cloudEvent) {
        Map<String, Object> headers = new HashMap<>();
        for (String attributeName : cloudEvent.getAttributeNames()) {
            headers.put("ce-" + attributeName, cloudEvent.getAttribute(attributeName).toString());
        }
        return headers;
    }
}
