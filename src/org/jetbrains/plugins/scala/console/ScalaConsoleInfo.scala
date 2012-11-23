package org.jetbrains.plugins.scala.console

import com.intellij.execution.process.{ConsoleHistoryModel, ProcessHandler}
import com.intellij.util.containers.WeakHashMap
import com.intellij.openapi.project.Project
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

/**
 * @author Ksenia.Sautina
 * @since 7/27/12
 */

object ScalaConsoleInfo {
  private val allConsoles =
    new WeakHashMap[Project, List[(ScalaLanguageConsole, ConsoleHistoryModel, ProcessHandler)]]()

  def getConsole(file: PsiFile): ScalaLanguageConsole = get(file)._1
  def getConsole(project: Project): ScalaLanguageConsole = get(project)._1
  def getModel(project: Project): ConsoleHistoryModel = get(project)._2
  def getProcessHandler(project: Project): ProcessHandler = get(project)._3
  def getConsole(editor: Editor): ScalaLanguageConsole = get(editor)._1
  def getModel(editor: Editor): ConsoleHistoryModel = get(editor)._2
  def getProcessHandler(editor: Editor): ProcessHandler = get(editor)._3

  def addConsole(console: ScalaLanguageConsole, model: ConsoleHistoryModel, processHandler: ProcessHandler) {
    val project = console.getProject
    synchronized {
      allConsoles.get(project) match {
        case null =>
          allConsoles.put(project, (console, model, processHandler) :: Nil)
        case list: List[(ScalaLanguageConsole, ConsoleHistoryModel, ProcessHandler)] =>
          allConsoles.put(project, (console, model, processHandler) :: list)
      }
    }
  }

  def disposeConsole(console: ScalaLanguageConsole) {
    val project = console.getProject
    synchronized {
      allConsoles.get(project) match {
        case null =>
        case list: List[(ScalaLanguageConsole, ConsoleHistoryModel, ProcessHandler)] =>
          allConsoles.put(project, list.filter {
            case (sConsole, _, _) => sConsole != console
          })
      }
    }
  }

  private def get(project: Project): (ScalaLanguageConsole, ConsoleHistoryModel, ProcessHandler) = {
    synchronized {
      allConsoles.get(project) match {
        case null => null
        case list => list.headOption.getOrElse(null, null, null)
      }
    }
  }

  private def get(editor: Editor) = {
    synchronized {
      allConsoles.get(editor.getProject) match {
        case null => null
        case list =>
          list.find {
            case (console: ScalaLanguageConsole, model: ConsoleHistoryModel, handler: ProcessHandler) =>
              console.getConsoleEditor == editor
          } match {
            case Some(res) => res
            case _ => (null, null, null)
          }
      }
    }
  }

  private def get(file: PsiFile) = {
    synchronized {
      allConsoles.get(file.getProject) match {
        case null => null
        case list =>
          list.find {
            case (console: ScalaLanguageConsole, model: ConsoleHistoryModel, handler: ProcessHandler) =>
              console.getFile == file
          } match {
            case Some(res) => res
            case _ => (null, null, null)
          }
      }
    }
  }
}