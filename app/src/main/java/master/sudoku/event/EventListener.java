/**
 * 
 */
package master.sudoku.event;

/**
 * @author dannyzha
 *
 */
public interface EventListener {
	/**
	 * handle event generated from an event source
	 * @param args the event arguments
	 * @return whether the event is handled properly
	 */
	boolean handleEvent(EventArgs args);
}
