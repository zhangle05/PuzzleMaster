/**
 * 
 */
package master.sudoku.event;

/**
 * @author dannyzha
 *
 */
public class EventArgs {
	
	public static final int INPUT_PANEL_SELECT = 0;
	
	private int mEventType;
	
	private Object mEventData;
	
	/**
	 * Constructor
	 * @param eventType
	 * @param eventData
	 */
	public EventArgs(int eventType, Object eventData) {
		this.mEventType = eventType;
		this.mEventData = eventData;
	}
	
	public int getEventType() {
		return mEventType;
	}
	
	public Object getEventData() {
		return mEventData;
	}
}
