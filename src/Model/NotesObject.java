package Model;

import javafx.scene.control.Label;

public class NotesObject {

    public int notesNo;
    public Label notes;

    public int getNotesNo() {
        return notesNo;
    }

    public void setNotesNo(int notesNo) {
        this.notesNo = notesNo;
    }

    public Label getNotes() {
        return notes;
    }

    public void setNotes(Label notes) {
        this.notes = notes;
    }
}
