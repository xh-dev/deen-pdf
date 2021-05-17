import java.io.File

import App.Commands.Mode.{Decrypt, Encrypt, Mode, Test}
import App.Commands.builder.{cmd, head, opt, programName}
import App.Commands.{CmdArgs, parser1}
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.encryption.{AccessPermission, InvalidPasswordException, StandardProtectionPolicy}
import scopt.{OParser, OParserBuilder}

object App {

  object Commands {

    object Mode extends Enumeration {
      type Mode = Value
      val Test, Decrypt, Encrypt = Value
    }

    case class CmdArgs(
                        inFile: Option[String] = None,
                        mode: Mode = Test,
                        pwd: Option[String] = None

                      )

    import scopt.OParser

    val builder: OParserBuilder[CmdArgs] = OParser.builder[CmdArgs]
    val parser1 = {
      OParser.sequence(
        programName("PDF Tools"),
        head("PDF Tools", "1.0.0"),
        opt[String]("input-file")
          .abbr("in")
          .required()
          .text("input pdf file path")
          .action((p, c) => c.copy(inFile = Some(p))),
        cmd("test")
          .text("Test if a pdf file is encrypted")
          .action((_, c) => c.copy(mode = Mode.Test))
          .children(
            opt[String]("password")
              .abbr("pwd")
              .action((p, c) => c.copy(pwd = Some(p)))
              .text("password to validate")
          ),
        cmd("decrypt")
          .text("Decrypt pdf with password")
          .action((_, c) => c.copy(mode = Mode.Decrypt))
          .children(
            opt[String]("password")
              .abbr("pwd")
              .action((p, c) => c.copy(pwd = Some(p)))
              .text("decrypt password")
          ),
        cmd("encrypt")
          .text("encrypt pdf with password")
          .action((_, c) => c.copy(mode = Mode.Encrypt))
          .children(
            opt[String]("password")
              .abbr("pwd")
              .action((p, c) => c.copy(pwd = Some(p)))
              .text("encrypt password")
          ),
      )
    }

  }

  def loadFile(file: File, withPwd: Option[String] = None): Option[PDDocument] = {
    if (withPwd.nonEmpty) {
      Some(PDDocument.load(file, withPwd.get))
    }
    else {
      Some(PDDocument.load(file))
    }
  }

  def isEncrypted(file: File): Boolean = {
    try {
      PDDocument.load(file).isEncrypted
    }
    catch {
      case e: InvalidPasswordException =>
        true
    }

  }

  def main(args: Array[String]): Unit = {
    if (args.length == 0)
      println(OParser.usage(parser1))

    val cmd = OParser.parse(Commands.parser1, args, CmdArgs())
    val pdfFile = cmd.get.inFile
      .map { it =>
        println(s"Process file: ${it}")
        it
      }
      .map {
        new File(_)
      }
      .filter(_.exists())
      .filter(_.isFile)


    if (cmd.nonEmpty && pdfFile.nonEmpty) {
      if (cmd.get.mode == Test) {
        if (isEncrypted(pdfFile.get)) {
          println(s"File[${pdfFile.get.toString}] is not encrypted")
        }
        else {
          if (cmd.get.pwd.isEmpty) {
            println(s"File[${pdfFile.get.toString}] is not encrypted but no validation to password")
          }
          else {
            try {
              loadFile(pdfFile.get, cmd.get.pwd)
              println(s"File[${pdfFile.get.toString}] is encrypted and password is correct")
            }
            catch {
              case e: Throwable =>
                e.printStackTrace()
                println(s"File[${pdfFile.get.toString}] is encrypted but password is not correct")
            }
          }
        }
      }
      else if (cmd.get.mode == Decrypt) {
        if (isEncrypted(pdfFile.get)) {
          try {
            loadFile(pdfFile.get, cmd.get.pwd) match {
              case Some(doc) =>
                doc.setAllSecurityToBeRemoved(true)
                val decryptedFile = new File(pdfFile.get.getParentFile, pdfFile.map { it =>
                  val fName = if (it.getName.endsWith(".pdf")) it.getName.substring(0, it.getName.length - 4) else it.getName
                  val extName = "_decrypted"
                  val endsWith = ".pdf"
                  fName + extName + endsWith
                }.get)
                doc.save(decryptedFile)
                doc.close()

                println(s"Decrypted file to ${decryptedFile.toString}")
              case None =>
                println("PDDocument not loaded!")
            }
          }
          catch {
            case e: InvalidPasswordException =>
              println("Password maybe not correct")
          }

        }
        else {
          println("File is not encrypted")
        }
      }
      else if (cmd.get.mode == Encrypt) {
        if (!isEncrypted(pdfFile.get)) {
          try {
            loadFile(pdfFile.get) match {
              case Some(doc) =>
                val ap = new AccessPermission()
                val spp = new StandardProtectionPolicy(cmd.get.pwd.get, cmd.get.pwd.get, ap)
                spp.setEncryptionKeyLength(256)
                doc.protect(spp)
                val encryptedFile = new File(pdfFile.get.getParentFile, pdfFile.map { it =>
                  val fName = if (it.getName.endsWith(".pdf")) it.getName.substring(0, it.getName.length - 4) else it.getName
                  val extName = "_encrypted"
                  val endsWith = ".pdf"
                  fName + extName + endsWith
                }.get)
                doc.save(encryptedFile)
                doc.close()

                println(s"Encrypted file to ${encryptedFile.toString}")
              case None =>
                println("PDDocument not loaded!")
            }
          }
          catch {
            case e: InvalidPasswordException =>
              println("Password maybe not correct")
          }

        }
        else {
          println("File is already encrypted")
        }
      }
    }


  }

}
