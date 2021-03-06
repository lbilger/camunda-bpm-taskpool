package io.holunda.camunda.taskpool.view.query

import io.holunda.camunda.taskpool.view.Task
import io.holunda.camunda.taskpool.view.auth.User
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway

data class TasksForUserQuery(
  val user: User
) : FilterQuery<Task> {

  fun subscribeTo(queryGateway: QueryGateway) = with(queryGateway.subscriptionQuery(
    this,
    ResponseTypes.instanceOf(TaskList::class.java),
    ResponseTypes.multipleInstancesOf(TaskChangeEvent::class.java))) {
    // executed after subscription is created
    initialResult()
  }

  override fun applyFilter(element: Task) =
// assignee
    element.assignee == this.user.username
      // candidate user
      || (element.candidateUsers.contains(this.user.username))
      // candidate groups:
      || (element.candidateGroups.any { candidateGroup -> this.user.groups.contains(candidateGroup) })


}

data class TaskList(val tasks: List<Task>)

sealed class TaskChangeEvent(open val task: Task)
data class TaskCreate(override val task: Task) : TaskChangeEvent(task)
data class TaskDelete(override val task: Task) : TaskChangeEvent(task)
data class TaskChange(override val task: Task) : TaskChangeEvent(task)




