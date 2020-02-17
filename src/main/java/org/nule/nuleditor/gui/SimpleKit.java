/*
 * SimpleKit.java
 *
 * Created on March 20, 2007, 3:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.nule.nuleditor.gui;

import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @author mike
 */
public class SimpleKit extends StyledEditorKit {
    
    public static String enterAction = "ENTER_ACTION";
    public static String tabAction = "TAB_ACTION";
    public static String shiftTabAction = "SHIFT_TAB_ACTION";
    
    private Action[] actions;
    
    /** Creates a new instance of SimpleKit */
    public SimpleKit() {
        super();
        Action[] parentActions = super.getActions();
        actions = new Action[parentActions.length + 3];
        int i;
        for (i = 0; i < parentActions.length; i++) {
            actions[i] = parentActions[i];
        }
        actions[i++] = new EnterAction(enterAction);
        actions[i++] = new TabAction(tabAction);
        actions[i++] = new ShiftTabAction(shiftTabAction);
    }
    
    public Action[] getActions() {
        return actions;
    }
    
    public ViewFactory getViewFactory() {
        return new SimpleViewFactory();
    }
    
    /**
     * Block comment the selected lines
     */
    public void blockCommentAction(SimpleEditor editor) {
        StyledDocument sd = editor.getStyledDocument();
        try {
            int start = editor.getSelectionStart();
            int end = editor.getSelectionEnd();
            int previousNewline = indexOfPreviousNewline(editor, sd);
            if (end > start) {
                String text = editor.getText(previousNewline, end - previousNewline);
                sd.remove(previousNewline, end - previousNewline);
                String newText = text.replaceAll("\n", "\n// ");
                sd.insertString(editor.getCaretPosition(), newText, null);
                editor.setSelectionStart(start + 2);
                editor.setSelectionEnd(previousNewline + newText.length());
            } else {
                sd.insertString(previousNewline + 1, "// ", null);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Block uncomment the selected lines
     */
    public void blockUncommentAction(SimpleEditor editor) {
        StyledDocument sd = editor.getStyledDocument();
        try {
            int start = editor.getSelectionStart();
            int end = editor.getSelectionEnd();
            int previousNewline = indexOfPreviousNewline(editor, sd);
            String text = editor.getText(previousNewline, end - previousNewline);
            int leadingRemoved = 0;
            if (text.charAt(1) == '/' && text.charAt(2) == '/' && text.charAt(3) == ' ') {
                leadingRemoved = 3;
            }
            if (end > start) {
                sd.remove(previousNewline, end - previousNewline);
                String newText = text.replaceAll("\n// ", "\n");
                sd.insertString(previousNewline, newText, null);
                if (start - leadingRemoved < previousNewline) {
                    editor.setSelectionStart(previousNewline);
                } else {
                    editor.setSelectionStart(start - leadingRemoved);
                }
                editor.setSelectionEnd(previousNewline + newText.length());
            } else {
                sd.remove(previousNewline + 1, leadingRemoved);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * an action the deletes 4 spaces when a shift tab is detected
     */
    public static class ShiftTabAction extends StyledTextAction {
        
        public ShiftTabAction(String nm) {
            super(nm);
        }
        
        public void actionPerformed(ActionEvent ae) {
            JEditorPane editor = getEditor(ae);
            StyledDocument sd = getStyledDocument(editor);
            try {
                int start = editor.getSelectionStart();
                int end = editor.getSelectionEnd();
                if (end > start) {
                    int previousNewline = indexOfPreviousNewline(editor, sd);
                    String text = editor.getText(previousNewline, end - previousNewline);
                    int leadingRemoved = 0;
                    for (leadingRemoved = 0; leadingRemoved < 4; leadingRemoved++) {
                        if (text.charAt(leadingRemoved + 1) != ' ') {
                            break;
                        }
                    }
                    sd.remove(previousNewline, end - previousNewline);
                    String newText = text.replaceAll("\n ", "\n").replaceAll("\n ", "\n").
                            replaceAll("\n ", "\n").replaceAll("\n ", "\n");
                    sd.insertString(previousNewline, newText, null);
                    if (start - leadingRemoved < previousNewline) {
                        editor.setSelectionStart(previousNewline);
                    } else {
                        editor.setSelectionStart(start - leadingRemoved);
                    }
                    editor.setSelectionEnd(previousNewline + newText.length());
                } else {
                    int current = editor.getCaretPosition() - 4;
                    if (current > 0) {
                        String text = editor.getText(current, 4);
                        if (text.equals("    ")) {
                            sd.remove(current, 4);
                        }
                    }
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * an action that inserts 4 spaces when a tab is detected
     */
    public static class TabAction extends StyledTextAction {
        
        public TabAction(String nm) {
            super(nm);
        }
        
        public void actionPerformed(ActionEvent ae) {
            JEditorPane editor = getEditor(ae);
            StyledDocument sd = getStyledDocument(editor);
            try {
                int start = editor.getSelectionStart();
                int end = editor.getSelectionEnd();
                if (end > start) {
                    int previousNewline = indexOfPreviousNewline(editor, sd);
                    System.out.println(">>>"+previousNewline+","+(end - previousNewline));
                    String text = editor.getText(previousNewline, end - previousNewline);
                    sd.remove(previousNewline, end - previousNewline);
                    String newText = text.replaceAll("\n", "\n    ");
                    sd.insertString(editor.getCaretPosition(), newText, null);
                    editor.setSelectionStart(start + 4);
                    editor.setSelectionEnd(previousNewline + newText.length());
                } else {
                    sd.insertString(editor.getCaretPosition(), "    ", null);
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * an action that inserts a newline and clone's the previous
     * line's tabulation if an enter is detected
     */
    public static class EnterAction extends StyledTextAction {
        
        public EnterAction(String nm) {
            super(nm);
        }
        
        public void actionPerformed(ActionEvent ae) {
            JEditorPane editor = getEditor(ae);
            StyledDocument sd = getStyledDocument(editor);
            try {
                int start = editor.getSelectionStart();
                int end = editor.getSelectionEnd();
                if (end > start) {
                    sd.remove(start, end - start);
                }
                int current = editor.getCaretPosition();
                // have to loop backwards through the document looking for previous
                // newlines.  it'd almost have to be possible to do this in a more
                // elegant manner
                int spaceAccum = 0;
                int count = current - 1;
                String append = "";
                while (count >= -1) {
                    String txt;
                    if (count == -1) {
                        txt = "\n";
                    } else {
                        txt = sd.getText(count, 1);
                    }
                    if (txt.equals(" ")) {
                        spaceAccum++;
                    } else if (txt.equals("\n")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < spaceAccum; i++) {
                            sb.append(" ");
                        }
                        append = sb.toString();
                        break;
                    } else {
                        spaceAccum = 0;
                    }
                    count--;
                }
                sd.insertString(current, "\n"+append, null);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    private static int indexOfPreviousNewline(JEditorPane editor, StyledDocument sd) {
        // have to loop backwards through the document looking for previous
        // newlines.  it'd almost have to be possible to do this in a more
        // elegant manner
        int count = editor.getSelectionStart() - 1;
        while (count >= 0) {
            try {
                String txt = sd.getText(count, 1);
                if (txt.equals("\n")) {
                    return count;
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
                return 0;
            }
            count--;
        }
        return 0;
    }
    
    public static class SimpleViewFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new LabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new NoWrapBoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }
            
            return new LabelView(elem);
        }
    }
    
    public static class NoWrapBoxView extends BoxView {
        public NoWrapBoxView(Element elem, int axis) {
            super(elem, axis);
        }
        
        public void layout(int width, int height) {
            super.layout(32768, height);
        }
        public float getMinimumSpan(int axis) {
            return super.getPreferredSpan(axis);
        }
    }
}
