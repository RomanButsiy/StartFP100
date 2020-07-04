package base.view.Settings;

import base.Editor;
import base.view.BaseView.BaseView;

import javax.swing.*;

public class Settings  extends BaseView {

    private JPanel rootPanel;
    
    public Settings(Editor editor) {
        super(editor, "Налаштування", true, false);
        initButtons("Додатково", "Гаразд", "Скасувати", "Застосувати");
        setViewPanel(rootPanel);
    }


    @Override
    public void leftButtonAction() {

    }

    @Override
    public void applyAction() {

    }

    @Override
    public void closeAction() {

    }
}
