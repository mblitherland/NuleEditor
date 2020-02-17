/*
 * AppController.java
 *
 * Created on March 20, 2007, 3:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.nule.nuleditor.gui;

import java.util.*;

/**
 *
 * @author mike
 */
public class AppController {
    
    private List listeners = new ArrayList();
    
    /** Creates a new instance of AppController */
    public AppController() {
    }
    
    public void addListener(AppListener listener) {
        synchronized(listeners) {
            listeners.add(listener);
        }
    }
    
    public void performEvent(AppEvent ae) {
        synchronized(listeners) {
            Iterator it = listeners.iterator();
            while (it.hasNext()) {
                AppListener listener = (AppListener) it.next();
                listener.appEventPerformed(ae);
            }
        }
    }
    
}
