package Service;

import javafx.scene.input.MouseEvent;

;


/**
 * Created by User on 17/02/2020.
 */
public interface IMeasurement {

    void handleClick(MouseEvent event);

    public abstract void handleMouseMove(MouseEvent event);

    public abstract void handleFinish();

    public abstract void handleShowContextMenu(MouseEvent event);


}
