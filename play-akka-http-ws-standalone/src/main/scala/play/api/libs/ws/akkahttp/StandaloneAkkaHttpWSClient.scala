/*
 * Copyright (C) 2009-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package play.api.libs.ws.akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.IllegalUriException
import akka.stream.Materializer
import play.api.libs.ws.{ StandaloneWSClient, StandaloneWSRequest }

import scala.util.control.NonFatal

object StandaloneAkkaHttpWSClient {
  def apply()(implicit sys: ActorSystem, mat: Materializer): StandaloneAkkaHttpWSClient = new StandaloneAkkaHttpWSClient()
}

final class StandaloneAkkaHttpWSClient private ()(implicit val sys: ActorSystem, val mat: Materializer) extends StandaloneWSClient {
  /**
   * The underlying implementation of the client, if any.  You must cast explicitly to the type you want.
   *
   * @tparam T the type you are expecting (i.e. isInstanceOf)
   * @return the backing class.
   */
  override def underlying[T]: T = Http().asInstanceOf[T]

  /**
   * Generates a request.  Throws IllegalArgumentException if the URL is invalid.
   *
   * @param url The base URL to make HTTP requests to.
   * @return a request
   */
  override def url(url: String): StandaloneWSRequest = try {
    StandaloneAkkaHttpWSRequest(url)
  } catch {
    case ex: IllegalUriException => throw new IllegalArgumentException(ex.getMessage, ex)
    case NonFatal(ex) => throw ex
  }

  /**
   * Closes this client, and releases underlying resources.
   */
  override def close(): Unit =
    Http().shutdownAllConnectionPools()
}