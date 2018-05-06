package workshops.introToScala.part1.exercises

import workshops.introToScala.part1.exercises.BasicsSpec.BetterLdap


/* ======================================
        >>>Spoilers Below <<<<
    >>>Look at your own risk <<<
====================================== */


























































object ExercizeSpoiler {
  def getUsersResetPasswordToken(ldap: BetterLdap, email: String) : Option[String] ={
    ldap.users.find(_.email == email).flatMap(_.resetPasswordToken)
  }

  def getUsersPassword(ldap: BetterLdap, email: String): String = {
    ldap.users.find(_.email == email).map(_.password).get
  }
}
