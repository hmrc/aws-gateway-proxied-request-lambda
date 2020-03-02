/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.aws_gateway_proxied_request_lambda

import java.net.HttpURLConnection._

import com.amazonaws.services.lambda.runtime.events.{APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent}
import com.amazonaws.services.lambda.runtime.{Context, LambdaLogger}
import org.mockito.Mockito.when
import org.scalatest._
import org.scalatest.mockito.MockitoSugar

class ProxiedRequestHandlerSpec extends WordSpecLike with Matchers with MockitoSugar with JsonMapper {

  trait Setup {
    val mockContext: Context = mock[Context]
    when(mockContext.getLogger).thenReturn(mock[LambdaLogger])

    val expectedStatusCode: Int = HTTP_OK
    val expectedResponseBody = """{"foo": "bar"}"""
    val proxiedRequestHandler: ProxiedRequestHandler = new ProxiedRequestHandler {
      override protected def handleInput(input: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent = {
        new APIGatewayProxyResponseEvent().withStatusCode(expectedStatusCode).withBody(expectedResponseBody)
      }
    }

    val expectedErrorMessage = "something went wrong"
    val failingProxiedRequestHandler: ProxiedRequestHandler = new ProxiedRequestHandler {
      override protected def handleInput(input: APIGatewayProxyRequestEvent): APIGatewayProxyResponseEvent = {
        throw new RuntimeException(expectedErrorMessage)
      }
    }

    val validInput: String = raw"""{
                                |    "httpMethod": "GET",
                                |    "body": "{\"host\": \"localhost\"}"
                                |}""".stripMargin

    val invalidInput: String = ""
  }

  "Proxied request handler" should {
    "return response from the handleInput method" in new Setup {
      val result: Either[Nothing, String] = proxiedRequestHandler.handle(validInput, mockContext)

      result.isRight shouldEqual true
      val Right(responseEvent) = result
      val response: APIGatewayProxyResponseEvent = fromJson[APIGatewayProxyResponseEvent](responseEvent)
      response.getStatusCode shouldEqual expectedStatusCode
      response.getBody shouldEqual expectedResponseBody
    }

    "return bad request from the handleInput method if input is invalid" in new Setup {
      val result: Either[Nothing, String] = proxiedRequestHandler.handle(invalidInput, mockContext)

      result.isRight shouldEqual true
      val Right(responseEvent) = result
      val response: APIGatewayProxyResponseEvent = fromJson[APIGatewayProxyResponseEvent](responseEvent)
      response.getStatusCode shouldEqual HTTP_BAD_REQUEST
      response.getBody should include ("No content to map due to end-of-input")
    }

    "return error response from the handleInput method if something goes wrong" in new Setup {
      val result: Either[Nothing, String] = failingProxiedRequestHandler.handle(validInput, mockContext)

      result.isRight shouldEqual true
      val Right(responseEvent) = result
      val response: APIGatewayProxyResponseEvent = fromJson[APIGatewayProxyResponseEvent](responseEvent)
      response.getStatusCode shouldEqual HTTP_INTERNAL_ERROR
      response.getBody shouldEqual expectedErrorMessage
    }
  }
}
