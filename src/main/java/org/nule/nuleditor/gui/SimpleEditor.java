/*
 * SimpleEditor.java
 *
 * Created on March 20, 2007, 3:27 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.nule.nuleditor.gui;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;


/**
 *
 * @author mike
 */
public class SimpleEditor extends JEditorPane {
    
    private UndoManager undo = new UndoManager();
    
    /** Creates a new instance of SimpleEditor */
    public SimpleEditor() {
        setEditorKit(new SimpleKit());
        InputMap im = getInputMap();
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        im.put(ks, SimpleKit.tabAction);
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK);
        im.put(ks, SimpleKit.shiftTabAction);
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        im.put(ks, SimpleKit.enterAction);
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0);
        im.put(ks, TransferHandler.getCopyAction().getValue(Action.NAME));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_CUT, 0);
        im.put(ks, TransferHandler.getCutAction().getValue(Action.NAME));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_PASTE, 0);
        im.put(ks, TransferHandler.getPasteAction().getValue(Action.NAME));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        im.put(ks, new Integer(AppEvent.UNDO_ACTION));
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);
        im.put(ks, new Integer(AppEvent.REDO_ACTION));
        ActionMap am = getActionMap();
        am.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        am.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        am.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());
        am.put(new Integer(AppEvent.UNDO_ACTION), new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                if (undo.canUndo()) {
                    try {
                        undo.undo();
                    } catch (CannotUndoException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        am.put(new Integer(AppEvent.REDO_ACTION), new AbstractAction() {
            public void actionPerformed(ActionEvent ae) {
                if (undo.canRedo()) {
                    try {
                        undo.redo();
                    } catch (CannotRedoException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent uee) {
                undo.addEdit(uee.getEdit());
            }
        });
    }
    
    public StyledDocument getStyledDocument() {
        return (StyledDocument) getDocument();
    }
    
    public void clearAndSetText(String text) {
        Document d = getDocument();
        try {
            d.remove(d.getStartPosition().getOffset(), d.getEndPosition().getOffset() -
                    d.getStartPosition().getOffset() - 1);
            d.insertString(d.getStartPosition().getOffset(), text, null);
            setCaretPosition(d.getEndPosition().getOffset() - 1);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        undo.discardAllEdits();
    }
    
    public void undo() {
        if (undo.canUndo()) {
            try {
                undo.undo();
            } catch (CannotUndoException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void redo() {
        if (undo.canRedo()) {
            try {
                undo.redo();
            } catch (CannotRedoException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void clearTabs() {
        String text = getText();
        clearAndSetText(text.replaceAll("\t", "    "));
    }
    
    public void blockComment() {
        ((SimpleKit) getEditorKit()).blockCommentAction(this);
    }
    
    public void blockUncomment() {
        ((SimpleKit) getEditorKit()).blockUncommentAction(this);
    }
}
