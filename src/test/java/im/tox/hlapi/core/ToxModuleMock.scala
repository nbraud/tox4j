package im.tox.hlapi.core

object ToxModuleMock {
  def apply() = new ToxModuleMock()
}

final class ToxModuleMock extends ToxModule {
  type State = Unit
  def initial = ()

  type ImplType = Unit
  def impl(x: Any) = ()
}
