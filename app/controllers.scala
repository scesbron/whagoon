package controllers

import play._
import data.validation._
import play.mvc._
import templates.Template
import management.ManagementFactory
import java.util.Date

object Application extends Controller {
    
  def index = Template
  def jquery = Template

  def sayHello(@Required myName:String) = {
    if(validation.hasErrors) {
        flash += "error" -> "Oops, please enter your name!"
        Action(index)
    } else {
      Template('myName -> myName)
    }
  }
    
}
object Memory extends Controller {
  val memoryMXBean = ManagementFactory.getMemoryMXBean
  def used = Json(new Value(new Date().getTime, memoryMXBean.getHeapMemoryUsage.getUsed / 1024))
}

class Value(val time: Long, val value: Long)


