package irka.grilleEncoder
package application.api.auth

import application.api.auth.identity.*
import application.api.auth.AuthService
import domain.model.UserId
import core.repository.AuthRepository
import application.api.auth.dto.{AuthUserDto, Role}

import zio.*
import zio.test.*
import zio.test.Assertion.*

object AuthServiceSpec extends ZIOSpecDefault:

  // Test data
  val testUserId = new UserId("test-id")
  val testUsername = "testUser"
  val testPassword = "testPassword"
  val testEmail = "test@example.com"

  val usernameIdentity = UsernameIdentity(testUsername, testPassword)
  val emailIdentity = EmailIdentity(testEmail, testPassword)

  val regularUserDto = AuthUserDto(
    id = testUserId,
    username = testUsername,
    email = Some(testEmail),
    role = Role.User
  )

  val adminUserDto = AuthUserDto(
    id = testUserId,
    username = testUsername,
    email = Some(testEmail),
    role = Role.Admin
  )

  // Mock implementation of AuthRepository for UsernameIdentity
  val successfulUsernameRepo: AuthRepository[UsernameIdentity] = new AuthRepository[UsernameIdentity]:
    def authenticate(identity: UsernameIdentity): ZIO[Any, Throwable, AuthUserDto] =
      if (identity.username == testUsername && identity.password == testPassword)
        ZIO.succeed(regularUserDto)
      else
        ZIO.fail(new Exception("Authentication failed: invalid credentials"))

    override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] = ???

    override def create(identity: UsernameIdentity): ZIO[Any, Throwable, AuthUserDto] = ???

  // Mock implementation for admin authentication
  val adminUsernameRepo: AuthRepository[UsernameIdentity] = new AuthRepository[UsernameIdentity]:
    override def authenticate(identity: UsernameIdentity): ZIO[Any, Throwable, AuthUserDto] =
      if (identity.username == testUsername && identity.password == testPassword)
        ZIO.succeed(adminUserDto)
      else
        ZIO.fail(new Exception("Authentication failed: invalid credentials"))

    override def create(identity: UsernameIdentity): ZIO[Any, Throwable, AuthUserDto] =
      ???

    override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] = ???


  // Mock implementation that fails authentication
  val failingUsernameRepo: AuthRepository[UsernameIdentity] = new AuthRepository[UsernameIdentity]:
    override def authenticate(identity: UsernameIdentity): ZIO[Any, Throwable, AuthUserDto] =
      ZIO.fail(new Exception("Authentication failed: invalid credentials"))

    override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] = ???

    override def create(identity: UsernameIdentity): ZIO[Any, Throwable, AuthUserDto] = ???

  // Mock implementation of AuthRepository for EmailIdentity
  val emailRepo: AuthRepository[EmailIdentity] = new AuthRepository[EmailIdentity]:
    def authenticate(identity: EmailIdentity): ZIO[Any, Throwable, AuthUserDto] =
      ZIO.fail(new Exception("Email authentication is not implemented yet"))

    override def findById(id: UserId): ZIO[Any, Throwable, Option[AuthUserDto]] = ???

    override def create(identity: EmailIdentity): ZIO[Any, Throwable, AuthUserDto] = ???

  // Test layers
  private val regularUserLayer = ZLayer.succeed(successfulUsernameRepo) ++ ZLayer.succeed(emailRepo) >>> AuthService.live
  private val adminUserLayer = ZLayer.succeed(adminUsernameRepo) ++ ZLayer.succeed(emailRepo) >>> AuthService.live
  private val failingAuthLayer = ZLayer.succeed(failingUsernameRepo) ++ ZLayer.succeed(emailRepo) >>> AuthService.live

  def spec: Spec[Any, Throwable] = suite("AuthService")(
    suite("authenticate")(
      test("should authenticate user with username"):
        for
          result <- AuthService.authenticate(usernameIdentity)
        yield assertTrue(result == regularUserDto)
      .provide(regularUserLayer)
      ,
      test("should fail for email authentication (not implemented yet)"):
        for
          exit <- AuthService.authenticate(emailIdentity).exit
        yield assertTrue(
          exit.isFailure &&
            exit.toString.contains("Email authentication is not implemented")
        )
      .provide(regularUserLayer),

      test("should propagate repository authentication errors"):
        for
          exit <- AuthService.authenticate(usernameIdentity).exit
        yield assertTrue(
          exit.isFailure &&
            exit.toString.contains("Authentication failed: invalid credentials")
        )
      .provide(failingAuthLayer)
    ),

    suite("authenticateAdmin")(
      test("should authenticate admin user successfully"):
        for
          result <- AuthService.authenticateAdmin(usernameIdentity)
        yield assertTrue(result == adminUserDto)
      .provide(adminUserLayer),

      test("should fail when user is not admin"):
        for
          exit <- AuthService.authenticateAdmin(usernameIdentity).exit
        yield assertTrue(
          exit.isFailure &&
            exit.toString.contains("Access restricted")
        )
      .provide(regularUserLayer)
    )
  )
