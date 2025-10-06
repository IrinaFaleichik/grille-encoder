package infrastructure.config

// TODO here make a connection to the DB according to ZIO config library
import zio.IO

import zio.config._
import zio.ConfigProvider
import zio.Config, Config._

case class ZIOConfig(ldap: String, port: Int, dburl: String)

// TODO where to put it?
val myConfig: Config[ZIOConfig] =
  (string("LDAP") zip int("PORT") zip string("DB_URL")).to[ZIOConfig]

