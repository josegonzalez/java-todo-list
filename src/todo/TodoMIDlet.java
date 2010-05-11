package todo;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import me.regexp.*;

public class TodoMIDlet extends MIDlet implements CommandListener {

    private Display display;
    private Form fmMain, fmAdd, fmGist;
    private Command cmNew, cmExit, cmCancel, cmCancelList, cmAdd, cmDone, cmCompleted, cmSend;
    private Vector vecTodo, vecCompleted;
    private TextField tfTodo;
    private ChoiceGroup cgTodos;
    private List list;

    public TodoMIDlet() {
        display = Display.getDisplay(this);
        cmExit = new Command("Exit", Command.EXIT, 0);

        // Create 'main' and 'add todo item' forms
        // Form for setting prefs is in commandAction()
        fmMain = new Form("Todo List");
        fmAdd = new Form("New Todo");
        fmGist = new Form("Send to Gist");

        // Todo list and vector
        cgTodos = new ChoiceGroup("", Choice.MULTIPLE);
        vecTodo = new Vector();
        vecCompleted = new Vector();

        // Commands
        cmNew = new Command("New Todo", Command.SCREEN, 2);
        cmExit = new Command("Exit", Command.EXIT, 1);
        cmCancel = new Command("Main", Command.CANCEL, 1);
        cmCancelList = new Command("Main", Command.CANCEL, 1);
        cmAdd = new Command("Add to List", Command.OK, 1);
        cmDone = new Command("Mark Set as Done", Command.OK, 2);
        cmCompleted = new Command("View Completed", Command.SCREEN, 3);
        cmSend = new Command("Send to Gist", Command.SCREEN, 4);

        // Text fields
        tfTodo = new TextField("Add:", "", 50, TextField.ANY);

        // Add all to form and listen for events
        fmMain.addCommand(cmExit);
        fmMain.addCommand(cmNew);
        fmMain.addCommand(cmDone);
        fmMain.addCommand(cmCompleted);
        fmMain.addCommand(cmSend);
        fmMain.append(cgTodos);
        fmMain.setCommandListener(this);

        fmAdd.addCommand(cmCancel);
        fmAdd.addCommand(cmAdd);
        fmAdd.append(tfTodo);
        fmAdd.setCommandListener(this);

        list = new List("Completed Todos", Choice.IMPLICIT);
        list.addCommand(cmCancelList);
        list.setCommandListener(this);

        fmGist.addCommand(cmCancel);
        fmGist.setCommandListener(this);
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
        if (c == cmSend) {
            sendToGist();
            display.setCurrent(fmGist);
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
                vecCompleted.addElement(cgTodos.getString(i -1));
                vecTodo.removeElement(cgTodos.getString(i - 1));
                cgTodos.delete(i - 1);
            }
        }
    }

    public void sendToGist() {
        String note = "*Todo*\n";
        for (int i = 0; i < vecTodo.size(); i++) {
            Object item = (String) vecTodo.elementAt(i);
            note += item.toString() + "\n";
        }

        note += "\n*Completed*\n";
        for (int i = 0; i < vecCompleted.size(); i++) {
            Object item = (String) vecCompleted.elementAt(i);
            note += "-" + item.toString() + "-";
            if (i != vecCompleted.size()) {
                note += "\n";
            }
        }
        String url = "http://gist.github.com/api/v1/xml/new";
        HttpRequest request = new HttpRequest();
        String response = request.post(url, "files[file.textile]=" + note);
        if (response.equals("")) {
            StringItem text = new StringItem(null, "Your todo list could not be saved");
            fmGist.deleteAll();
            fmGist.append(text);
            fmGist.setTitle("Error");
        } else {
            // Pa
            RE r = new RE("<repo>");
            String[] arr = r.split(response);
            r = new RE("</repo>");
            arr = r.split(arr[1]);
            StringItem gist = new StringItem(null, "http://gist.github.com/" + arr[0]);
            StringItem text = new StringItem(null, "Your todo list has been saved to: ");

            fmGist.deleteAll();
            fmGist.append(text);
            fmGist.append(gist);
            fmGist.setTitle("Gist Saved!");
        }
    }

}
