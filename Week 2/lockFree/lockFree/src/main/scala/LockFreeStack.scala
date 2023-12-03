package lockFree
import instrumentation.monitors.LockFreeMonitor
import atomicReference.SimpleAtomicReference

/*
* One of the main uses of atomic types is to implement synchronization 
* primitives such as semaphores and mutexes. It is also useful to implement
* lock-free algorithms.
*  
* Task: In this exercise we implement a lock-free stack using an atomic reference.
* Assume that you have a class implementing an atomic reference called 
* SimpleAtomicReference with an interface as follows:
*
* class SimpleAtomicReference[V](initValue: V) {
*  protected var value:V
*
*  def compareAndSet(expect: V, update: V): Boolean
*  def get: V
*  def set(newValue: V): Unit 
*}
*
* Hint: refer and use compareAndSet(expect: V, update: V): Boolean method from
* past exercises. 
*/

/* Implement a lock free stack. */
class LockFreeStack[E](capacity: Int) extends LockFreeMonitor {
  class Node[E](val value: E) {
    var next: Node[E] = null
  }
  val top = new SimpleAtomicReference[Node[E]](null)
  // Do not add other variables

  def push(e: E): Unit = {
		//Create a new item
		var newHead: Node[E] = new Node[E](e);
		var headNode: Node[E] = null;
			
		while(!top.compareAndSet(headNode, newHead)) {
      		headNode = top.get;
			newHead.next = headNode;
    };
	
  }

  def pop(): E = {
    var headNode: Node[E] = top.get;
		headNode = top.get;
		while(!top.compareAndSet(headNode, headNode.next)){
      		headNode = top.get;
    };
		return headNode.value;
  }
}

/*
   
    class SimpleAtomicReference[V](initValue: V) {
        protected var value:V
        def compareAndSet(expect: V, update: V): Boolean
        def get: V
        def set(newValue: V): Unit 
    }
    
*/

/*
	private AtomicReference<StackNode> head;
	/**
	 * Creates a unbounded concurrent queue
	 */
	public ConcurrentStack() {
		head = new AtomicReference();
	}

	/**
	 * This method will try to push item into stack until it succeeds
	 */
	@Override
	public void push(E e) {
		//Create a new item
		StackNode<E> newHead = new StackNode<E>(e);
		StackNode<E> headNode = null;
		do
		{
			headNode = head.get();
			newHead.next = headNode;
		}while(!head.compareAndSet(headNode, newHead));
	}
*/