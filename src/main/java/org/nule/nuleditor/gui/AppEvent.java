/*
 * AppEvent.java
 *
 * Created on March 20, 2007, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.nule.nuleditor.gui;

/**
 *
 * @author mike
 */
public class AppEvent {
    
    // put an empty snippit in the code window
    public static final int LOAD_NEW_SNIPPIT_1 = 1;
    public static final int LOAD_NEW_SNIPPIT_2 = 2;
    
    // undo/redo/edit options
    public static final int UNDO_ACTION = 11;
    public static final int REDO_ACTION = 12;
    public static final int CLEAR_TABS_ACTION = 13;
    public static final int BLOCK_COMMENT = 14;
    public static final int BLOCK_UNCOMMENT = 15;
    
    // file open save options
    public static final int LOAD_INPUT = 21;
    public static final int SAVE_OUTPUT = 22;
    public static final int LOAD_SNIPPIT = 23;
    public static final int SAVE_SNIPPIT = 24;
    
    // validate/run options
    public static final int VALIDATE_ACTION = 31;
    public static final int RUN_ACTION_1 = 32; // Load input data from InPanel
    public static final int RUN_ACTION_2 = 33; // CodePanel runs code and data
    public static final int RUN_ACTION_3 = 34; // CodePanel sends data to OutPanel
    
    /** Creates a new instance of AppEvent */
    public AppEvent() {
    }
    
    public AppEvent(int eventCode) {
        this.eventCode = eventCode;
    }
    
    public AppEvent(int eventCode, String eventData) {
        this.eventCode = eventCode;
        this.eventData = eventData;
    }
    
    private int eventCode;
    
    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }
    
    public int getEventCode() {
        return eventCode;
    }
    
    private String eventData = null;
    
    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
    
    public String getEventData() {
        return eventData;
    }
    
}
