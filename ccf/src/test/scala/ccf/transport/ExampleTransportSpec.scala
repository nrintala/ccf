package ccf.transport

import ccf.operation.Operation
import ccf.messaging.Message
import ccf.server.Server
import org.specs.Specification
import org.specs.mock.Mockito

object ExampleTransportSpec extends Specification with Mockito {
  val listener = mock[ExampleTransportListener]
  val transport = mock[TransportActor]
  val t = new ExampleTransport(listener)

  "ExampleTransport" should {
    val clientId = ClientId.randomId
    val channelId = ChannelId.randomId

    "initialize" in {
      val server = mock[Server[Operation]]
      t.initialize(server)
      listener.onInitialize(server) was called
    }

    "join" in {
      listener.onJoin(clientId, channelId) returns 123
      t !? Event.Join(clientId, channelId) must equalTo(Event.State(clientId, channelId, 123))
    }

    "quit" in {
      t !? Event.Quit(clientId, channelId) must equalTo(Event.Ok())
      listener.onQuit(clientId, channelId) was called
    }

    "request sync" in {
      t !? Event.Sync(clientId, channelId) must equalTo(Event.Ok())
      listener.onSync(clientId, channelId) was called
    }

    "pass a message" in {
      val msg = mock[Message[Operation]]
      t !? Event.Msg(clientId, channelId, msg) must equalTo(Event.Ok())
      listener.onMsg(clientId, channelId, msg) was called
    }

    "return Event.Error() on unknown message" in {
      case class UnknownMsg()
      t !? UnknownMsg() must haveClass[Event.Error]
    }
  }
}