/*
 * Copyright 2022 HM Revenue & Customs
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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class JsonMapperSpec extends AnyWordSpec with Matchers {

  trait Setup {
    val underTest: JsonMapper = new JsonMapper{}
  }

  "toJson" should {
    "not include null properties in the serialisation" in new Setup {
      val result: String = underTest.toJson(Foobar("value", null))

      result shouldEqual """{"foo":"value"}"""
    }
  }
}

case class Foobar(foo: String, bar: String)
