package projet_web.ServeurWeb;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Controller {

	@FXML
	private Button btLancement;

	private Main _main;

	public Controller()
	{

	}

	@FXML
	private void Initialize()
	{

	}

	private void LancerServ()
	{
		Platform.runLater(() ->{
			final Service<Void> LancerServ = new Service<Void>() {

	            @Override
	            protected Task<Void> createTask() {
	                return new Task<Void>() {

	                    @Override
	                    protected Void call() throws Exception {
	                        _main.LancerServ();
	                        return null;
	                    }
	                };
	            }
	        };
	        LancerServ.start();
		});
	}

	public void SetMain(Main main)
	{
		this._main = main;
		btLancement.setOnMouseClicked(mouseEvent -> LancerServ());
	}

}
