package tech.wetech.mybatis.generator.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.mybatis.generator.utils.AlertUtil;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author cjbi
 */
public abstract class BaseFXController implements Initializable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String FOLDER_NO_EXIST = "部分目录不存在，是否创建";

    @FXML
    protected TabPane tabPaneMain;

    private Stage primaryStage;

    protected Stage dialogStage;

    private static Map<FXMLConstant, SoftReference<? extends BaseFXController>> cacheNodeMap = new HashMap<>();

    protected <T extends BaseFXController> T loadDialog(String title, FXMLConstant fxmlConstant, boolean cache) {
        SoftReference<? extends BaseFXController> parentNodeRef = cacheNodeMap.get(fxmlConstant);
        if (cache && parentNodeRef != null) {
            return (T) parentNodeRef.get();
        }
        URL skeletonResource = Thread.currentThread().getContextClassLoader().getResource(fxmlConstant.getFxml());
        FXMLLoader loader = new FXMLLoader(skeletonResource);
        Parent loginNode;
        try {
            loginNode = loader.load();
            T controller = loader.getController();
            dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(getPrimaryStage());
            dialogStage.setScene(new Scene(loginNode));
            dialogStage.setMaximized(false);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);
            // put into cache map
            SoftReference<BaseFXController> softReference = new SoftReference<>(controller);
            cacheNodeMap.put(fxmlConstant, softReference);

            return controller;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            AlertUtil.showErrorAlert(e.getMessage());
        }
        return null;
    }

    protected <T extends BaseFXController> T addTab(String title, String url, String resourceBundleName, String iconPath) {
        try {
            URL skeletonResource = Thread.currentThread().getContextClassLoader().getResource(url);
            FXMLLoader loader = new FXMLLoader(skeletonResource);
            if (StringUtils.isNotEmpty(resourceBundleName)) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName, Locale.getDefault());
                loader.setResources(resourceBundle);
            }
            Tab tab = new Tab(title);
            if (StringUtils.isNotEmpty(iconPath)) {
                ImageView imageView = new ImageView(new Image(iconPath));
                imageView.setFitHeight(18);
                imageView.setFitWidth(18);
                tab.setGraphic(imageView);
            }

            tab.setContent(loader.load());
            tabPaneMain.getTabs().add(tab);
            tabPaneMain.getSelectionModel().select(tab);
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Title: addWebView
     * @Description: Append a webview.
     */
    protected void addWebView(String title, String url, String iconPath) {
        WebView browser = new WebView();
        WebEngine webEngine = browser.getEngine();
        if (url.startsWith("http")) {
            webEngine.load(url);
        } else {
            webEngine.load(getClass().getResource(url).toExternalForm());
        }
        Tab tab = new Tab(title);
        if (StringUtils.isNotEmpty(iconPath)) {
            ImageView imageView = new ImageView(new Image(iconPath));
            imageView.setFitHeight(18);
            imageView.setFitWidth(18);
            tab.setGraphic(imageView);
        }
        tab.setContent(browser);
        tabPaneMain.getTabs().add(tab);
        tabPaneMain.getSelectionModel().select(tab);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void showDialogStage() {
        if (dialogStage != null) {
            dialogStage.show();
        }
    }

    public TabPane getTabPaneMain() {
        return tabPaneMain;
    }

    public void setTabPaneMain(TabPane tabPaneMain) {
        this.tabPaneMain = tabPaneMain;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }
}
