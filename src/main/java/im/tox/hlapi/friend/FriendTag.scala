package im.tox.hlapi.friend

final case class FriendTag(
  val name: String,
  private[hlapi] val id: Int
)
