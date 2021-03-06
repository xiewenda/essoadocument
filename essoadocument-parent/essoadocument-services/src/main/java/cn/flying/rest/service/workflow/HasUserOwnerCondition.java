package cn.flying.rest.service.workflow;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;

import java.util.*;

/**
 * 判断工作流处理人是否存在
 */
public class HasUserOwnerCondition implements Condition {
  /**
   * 确定条件 信息通过或失败
   * 
   * @param transientVars
   *          Map：是客户端代码调用Workflow.doAction()时传进来的。 这个参数可以基于用户的不同的输入使函数有不同的行为。
   *          这个参数也包含了一些特别变量，这些变量对于访问workflow的信息是很有帮助的。
   *          它也包含了所有的在Registers中配置的变量和下面两种特别的变量： entry
   *          (com.opensymphony.workflow.spi.WorkflowEntry) and context
   *          (com.opensymphony.workflow.WorkflowContext)。
   * 
   * @param args
   *          Map是一个包含在所有的<function/>标签中的<arg/>标签的Map。 这些参数都是String类型的。这意味着<arg
   *          name="foo">this is ${someVar}</arg> 标签中定义的参数将在arg
   *          Map中通过"foo"，可以映射到"this is [contents of someVar]"字串。
   * 
   * @param propertySet
   *          包含所有的在workflow实例中持久化的变量。
   * @return true表示通过 false表示失败
   */
  @SuppressWarnings("rawtypes")
  public boolean passesCondition(Map transientVars, Map args, PropertySet ps)
      throws StoreException {
    int stepId = 0;
    String stepIdVal = (String) args.get("stepId");

    if (stepIdVal != null) {
      try {
        stepId = Integer.parseInt(stepIdVal);
      } catch (Exception ex) {
      }
    }

    WorkflowContext context = (WorkflowContext) transientVars.get("context");
    WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
    WorkflowStore store = (WorkflowStore) transientVars.get("store");
    List currentSteps = store.findCurrentSteps(entry.getId());

    if (stepId == 0) {
      for (Iterator iterator = currentSteps.iterator(); iterator.hasNext();) {
        Step step = (Step) iterator.next();

        if ((step.getOwner() != null)
            && step.getOwner().indexOf(context.getCaller()) != -1) {
          return true;
        }
      }
    } else {
      for (Iterator iterator = currentSteps.iterator(); iterator.hasNext();) {
        Step step = (Step) iterator.next();

        if (stepId == step.getStepId()) {
          if ((step.getOwner() != null)
              && step.getOwner().indexOf(context.getCaller()) != -1) {
            return true;
          }
        }
      }
    }

    return false;
  }
}
