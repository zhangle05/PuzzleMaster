/**
 * 
 */
package master.sudoku.event;

import java.util.Vector;

/**
 * @author dannyzha
 *
 */
public abstract class EventSource {
	
	protected Vector<EventListener> mListeners = new Vector<EventListener>();
	
	public void addEventListener(EventListener listener) {
		if(!mListeners.contains(listener)) {
			mListeners.addElement(listener);
		}
	}
	
	public void removeEventListener(EventListener listener) {
		if(mListeners.contains(listener)) {
			mListeners.removeElement(listener);
		}
	}
	
	protected void triggerEvent(int eventType, Object eventData) {
		triggerEvent(new EventArgs(eventType, eventData));
	}
	
	protected void triggerEvent(EventArgs args) {
		for(int i=0; i<mListeners.size(); i++) {
			EventListener listener = (EventListener)mListeners.elementAt(i);
			listener.handleEvent(args);
		}
	}
}
