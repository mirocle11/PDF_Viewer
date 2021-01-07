package Model;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class NotesObject {

    public int notesNo;
    public Label notes;
    private Color color;

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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
