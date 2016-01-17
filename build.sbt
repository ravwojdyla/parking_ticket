name := """parking_tickets"""

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.typesafe.slick"  %%  "slick" % "2.0.2",
    "org.slf4j"           %   "slf4j-nop" % "1.6.4",
    "com.h2database"      %   "h2" % "1.3.170",
    "io.spray" %%  "spray-json" % "1.3.2"
  )
}
