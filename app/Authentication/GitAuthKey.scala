package Authentication

object GitAuthKey {
val token = sys.env.getOrElse("GITHUBAUTH", "Authentication could not be complete")
}
