version := "0.1.0"

scalaVersion := "3.5.1"

name := "hnscrape3"

lazy val http4sVersion = "1.0.0-M42"
lazy val circeVersion  = "0.14.10"
lazy val fs2Version    = "3.2.2"

libraryDependencies ++= Seq(
  "org.typelevel"   %% "cats-effect"         % "3.5.4",
  "ch.qos.logback"   % "logback-classic"     % "1.5.8",
  "org.typelevel"   %% "log4cats-slf4j"      % "2.7.0",
  "io.circe"        %% "circe-core"          % circeVersion,
  "io.circe"        %% "circe-generic"       % circeVersion,
  "io.circe"        %% "circe-parser"        % circeVersion,
  "org.http4s"      %% "http4s-dsl"          % http4sVersion,
  "org.http4s"      %% "http4s-ember-client" % http4sVersion,
  "org.http4s"      %% "http4s-circe"        % http4sVersion,
  "com.github.luben" % "zstd-jni"            % "1.5.6-6",
  "org.scalatest"   %% "scalatest"           % "3.2.19" % "test",
  "org.rogach"      %% "scallop"             % "5.1.0"
)

ThisBuild / assemblyMergeStrategy := {
  case PathList("module-info.class")                                         => MergeStrategy.discard
  case "META-INF/io.netty.versions.properties"                               => MergeStrategy.first
  case "META-INF/MANIFEST.MF"                                                => MergeStrategy.discard
  case x if x.startsWith("META-INF/versions/")                               => MergeStrategy.first
  case x if x.startsWith("META-INF/services/")                               => MergeStrategy.concat
  case "META-INF/native-image/reflect-config.json"                           => MergeStrategy.concat
  case "META-INF/native-image/io.netty/netty-common/native-image.properties" => MergeStrategy.first
  case "META-INF/okio.kotlin_module"                                         => MergeStrategy.first
  case "findbugsExclude.xml"                                                 => MergeStrategy.discard
  case "log4j2-test.properties"                                              => MergeStrategy.discard
  case x if x.endsWith("/module-info.class")                                 => MergeStrategy.discard
  case x if x.startsWith("/META-INF/versions/9/org/yaml/snakeyaml/internal/") =>
    MergeStrategy.discard // pulsar client bundling snakeyaml
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
