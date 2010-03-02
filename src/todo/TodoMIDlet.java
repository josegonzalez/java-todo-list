package todo;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class TodoMIDlet extends MIDlet implements CommandListener {

    private Display display;
    private Form fmMain, fmAdd;
    private Command cmNew, cmExit, cmCancel, cmCancelList, cmAdd, cmDone, cmCompleted;
    private Vector vecTodo;
    private TextField tfTodo;
    private ChoiceGroup cgTodos;
    private List list;

    public TodoMIDlet() {
        display = Display.getDisplay(this);
        cmExit = new Command("Exit", Command.EXIT, 0);
        display = Display.getDisplay(this);

        // Create 'main' and 'add todo item' forms
        // Form for setting prefs is in commandAction()
        fmMain = new Form("Todo List");
        fmAdd = new Form("New Todo");

        // Todo list and vector
        cgTodos = new ChoiceGroup("", Choice.MULTIPLE);
        vecTodo = new Vector();

        // Commands
        cmNew = new Command("New Todo", Command.SCREEN, 2);
        cmExit = new Command("Exit", Command.EXIT, 1);
        cmCancel = new Command("Main", Command.CANCEL, 1);
        cmCancelList = new Command("Main", Command.CANCEL, 1);
        cmAdd = new Command("Add to List", Command.OK, 2);
        cmDone = new Command("Mark Set as Done", Command.OK, 2);
        cmCompleted = new Command("View Completed", Command.SCREEN, 2);

        // Text fields
        tfTodo = new TextField("Add:", "", 50, TextField.ANY);

        // Add all to form and listen for events
        fmMain.addCommand(cmExit);
        fmMain.addCommand(cmNew);
        fmMain.addCommand(cmDone);
        fmMain.addCommand(cmCompleted);
        fmMain.append(cgTodos);
        fmMain.setCommandListener(this);

        fmAdd.addCommand(cmCancel);
        fmAdd.addCommand(cmAdd);
        fmAdd.append(tfTodo);
        fmAdd.setCommandListener(this);

        list = new List("Completed Todos", Choice.IMPLICIT);
        list.addCommand(cmCancelList);
        list.setCommandListener(this);
    }

    public void startApp() {
        display.setCurrent(fmMain);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable s) {
        if (c == cmExit) {
            destroyApp(false);
            notifyDestroyed();
        }
        if (c == cmCancelList) {
            display.setCurrent(fmMain);
        }
        if (c == cmNew) {
            tfTodo.setString("");
            display.setCurrent(fmAdd);
        }
        if (c == cmCancel) {
            tfTodo.setString("");
            display.setCurrent(fmMain);
        }
        if (c == cmAdd) {
            addTodoItem(tfTodo.getString());
            rebuildTodoList();
            tfTodo.setString("");
        }
        if (c == cmDone) {
            setSelectedToComplete();
        }
        if (c == cmCompleted) {
            display.setCurrent(list);
        }
    }

    public boolean addTodoItem(String item) {
        int count = vecTodo.size();
        vecTodo.addElement(item);
        return vecTodo.size() > count;
    }

    public void rebuildTodoList() {
        for (int i = cgTodos.size(); i > 0; i--) {
            cgTodos.delete(i - 1);
        }

        for (int i = 0; i < vecTodo.size(); i++) {
            // Get a todo item from vector
            Object item = (String) vecTodo.elementAt(i);

            // Append to todo choicegroup
            cgTodos.append(item.toString(), null);
        }
    }

    public void setSelectedToComplete() {
        for (int i = cgTodos.size(); i > 0; i--) {
            if(cgTodos.isSelected(i - 1)) {
                list.append(cgTodos.getString(i - 1), null);
                cgTodos.delete(i - 1);
            }
        }
    }

}
