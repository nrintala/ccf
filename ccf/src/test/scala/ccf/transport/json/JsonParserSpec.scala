package ccf.transport.json

import org.specs.Specification

object JsonParserSpec extends Specification {
  "Empty string" should {
    "parse to None" in {
      JsonParser.parseResponse("") must equalTo(None)
    }
  }
  "Invalid JSON message" should {
    val invalidMessage = """{"a":1,"b":"c","d":3"""
    "cause MalformedDataException" in {
      JsonParser.parseResponse(invalidMessage) must throwA[MalformedDataException]
    }
  }
  "A JSON response containing list as top-level element" should {
    "cause MalformedDataException" in {
      JsonParser.parseResponse("[]") must throwA[MalformedDataException]
    }
  }
  "A JSON response containing improper header type" should {
    "cause MalformedDataException" in {
      JsonParser.parseResponse("""{"headers":["foo","bar"]}""") must throwA[MalformedDataException]
    }
  }
  "A JSON response without header" should {
    "cause MalformedDataException" in {
      JsonParser.parseResponse("""{"content":["foo","bar"]}""") must throwA[MalformedDataException]
    }
  }
  "A JSON response with headers but without content" should {
    val jsonResponse = """{"headers":{"aa":"bb","cc":"dd"}}"""
    "parse to equivalent Response" in {
      val expected = Response(Map[String, String]("aa" -> "bb", "cc" -> "dd"), None)
      val parsed = JsonParser.parseResponse(jsonResponse).get
      parsed must equalTo(expected)
    }
  }
  "A JSON response with headers and content elements" should {
    val jsonResponse = """{"headers":{"aa":"bb","cc":"dd"},"content":{"b":2}}"""
    "parse to equivalent Response" in {
      val expected = Response(Map("aa" -> "bb", "cc" -> "dd"), Some(Map("b" -> 2)))
      val parsed = JsonParser.parseResponse(jsonResponse).get
      parsed must equalTo(expected)
    }
  }
  "Response with content generated by JsonFormatter" should {
    val expected = Response(Map("key" -> "value"), Some(Map("a" -> 2)))
    val jsonResponse = JsonFormatter.formatResponse(expected)
    "be properly parsed by JsonParser" in {
      val parsed = JsonParser.parseResponse(jsonResponse).get
      parsed must equalTo(expected)
    }
  }
  "Response without content generated by JsonFormatter" should {
    val expected = Response(Map("key" -> "value"), None)
    val jsonResponse = JsonFormatter.formatResponse(expected)
    "be properly parsed by JsonParser" in {
      val parsed = JsonParser.parseResponse(jsonResponse).get
      parsed must equalTo(expected)
    }
  }
  "Request with content generated by JsonFormatter" should {
    val request = Request(Map("key" -> "value"), Some(Map("a" -> 1)))
    val jsonRequest = JsonFormatter.formatRequest(request)
    "must be properly parsed to Response by JsonParser" in {
      val expected = Request(Map("key" -> "value"), Some(Map("a" -> 1)))
      val parsed = JsonParser.parseRequest(jsonRequest).get
      parsed must equalTo(expected)
    }
  }
  "Request without content generated by JsonFormatter" should {
    val request = Request(Map("key" -> "value"), None)
    val jsonRequest = JsonFormatter.formatRequest(request)
    "must be properly parsed to Response by JsonParser" in {
      val expected = Request(Map("key" -> "value"), None)
      val parsed = JsonParser.parseRequest(jsonRequest).get
      parsed must equalTo(expected)
    }
  }
}
