package im.tox.hlapi.group

sealed trait PrivacySetting
case object Public         extends PrivacySetting
case object Private        extends PrivacySetting

sealed trait Role
case object Founder        extends Role
case object Op             extends Role
case object User           extends Role
case object Observer       extends Role
case object Banned         extends Role
case object Invalid        extends Role

sealed trait JoinError
case object NickTaken      extends JoinError
case object GroupFull      extends JoinError
case object InviteDisabled extends JoinError
case object InviteFailed   extends JoinError

class GroupChat {

}
