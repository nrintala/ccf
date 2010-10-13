/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ccf.server

import ccf.transport.{Codec, TransportResponse, TransportRequest}
import ccf.session.{ClientId, ChannelId, SessionRequest, SessionResponse}
import ccf.tree.operation.TreeOperation

class DefaultServerOperationInterceptor extends ServerOperationInterceptor {
  def currentStateFor(channelId: ChannelId) = ""
  def applyOperation(server: Server, clientId: ClientId, channelId: ChannelId, op: TreeOperation) {}
  def operationsForCreatingClient(clientId: ClientId, channelId: ChannelId, op: TreeOperation): List[TreeOperation] = List()
  def operationsForAllClients(clientId: ClientId, channelId: ChannelId, op: TreeOperation): List[TreeOperation] = List()
}

class ServerEngine(codec: Codec, interceptor: ServerOperationInterceptor = new DefaultServerOperationInterceptor) {
  val encodingMimeType = codec.mimeType

  def processRequest(request: String): String = {
    val transportRequest = codec.decodeRequest(request).getOrElse(error("Unable to decode request"))
    val transportResponse = processRequest(transportRequest)
    codec.encodeResponse(transportResponse)
  }

  protected def sessionRequest(transportRequest: TransportRequest) = SessionRequest(transportRequest)

  private def processRequest(transportRequest: TransportRequest): TransportResponse = {
    processRequest(sessionRequest(transportRequest)).transportResponse
  }

  private def processRequest(sessionRequest: SessionRequest): SessionResponse = {
    sessionRequest.successResponse(None)
  }
}
