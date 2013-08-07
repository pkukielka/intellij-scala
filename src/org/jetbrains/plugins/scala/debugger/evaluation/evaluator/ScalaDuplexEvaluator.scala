package org.jetbrains.plugins.scala
package debugger.evaluation.evaluator

import com.intellij.debugger.engine.evaluation.expression.{Modifier, Evaluator}
import com.intellij.debugger.engine.evaluation.{EvaluateExceptionUtil, EvaluationContextImpl}

/**
 * Nikolay.Tropin
 * 7/31/13
 */

/**
 * Tries to use first evaluator first. If gets exception or null, uses second one.
 */
class ScalaDuplexEvaluator(first: Evaluator, second: Evaluator) extends Evaluator {
  private var myModifier: Modifier = null

  def evaluate(context: EvaluationContextImpl): AnyRef = {
    var result: AnyRef = null
    try {
      result = first.evaluate(context)
      myModifier = first.getModifier
    }
    catch {
      case e: Exception => result = null; myModifier = null
    }
    if (result == null) {
      try {
        result = second.evaluate(context)
        myModifier = second.getModifier
      }
      catch {
        case e: Exception => throw EvaluateExceptionUtil.createEvaluateException(e)
      }
    }
    result
  }

  def getModifier: Modifier = myModifier
}