package tech.wetech.mybatis.generator.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.wetech.mybatis.generator.exception.DbDriverLoadingException;
import tech.wetech.mybatis.generator.model.JdbcConnection;
import tech.wetech.mybatis.generator.utils.AlertUtil;
import tech.wetech.mybatis.generator.utils.ConfigHelper;
import tech.wetech.mybatis.generator.utils.DbUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author cjbi
 */
public class JdbcConnectionController extends BaseFXController {

    private static final Logger _LOG = LoggerFactory.getLogger(JdbcConnectionController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField schemaField;
    @FXML
    private ChoiceBox<String> encodingChoice;
    @FXML
    private ChoiceBox<String> dbTypeChoice;

    private boolean isUpdate = false;
    private Integer primayKey;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void saveConnection() {
        JdbcConnection config = extractConfigForUI();
        if (config == null) {
            return;
        }
        try {
            ConfigHelper.saveDatabaseConfig(isUpdate, primayKey, config);
            getDialogStage().close();
        } catch (Exception e) {
            _LOG.error(e.getMessage(), e);
            AlertUtil.showErrorAlert(e.getMessage());
        }

    }

    private JdbcConnection extractConfigForUI() {
        if (isInputValid()) {
            JdbcConnection jdbcConnection = new JdbcConnection();
            jdbcConnection.setName(nameField.getText());
            jdbcConnection.setDbType(dbTypeChoice.getValue());
            jdbcConnection.setHost(hostField.getText());
            jdbcConnection.setPort(portField.getText());
            jdbcConnection.setUsername(userNameField.getText());
            jdbcConnection.setPassword(passwordField.getText());
            jdbcConnection.setSchema(schemaField.getText());
            jdbcConnection.setEncoding(encodingChoice.getValue());
            return jdbcConnection;
        }
        return null;
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (StringUtils.isBlank(nameField.getText())) {
            errorMessage += "名称不能为空!\n";
        }
        if (StringUtils.isBlank(dbTypeChoice.getValue())) {
            errorMessage += "数据库类型不能为空!\n";
        }
        if (StringUtils.isBlank(hostField.getText())) {
            errorMessage += "主机或IP地址不能为空!\n";
        }
        if (StringUtils.isBlank(portField.getText())) {
            errorMessage += "端口号不能为空!\n";
        }
        if (StringUtils.isBlank(userNameField.getText())) {
            errorMessage += "用户名不能为空!\n";
        }
        if (StringUtils.isBlank(passwordField.getText())) {
            errorMessage += "密码不能为空!\n";
        }
        if (StringUtils.isBlank(schemaField.getText())) {
            errorMessage += "Schema/数据库不能为空!\n";
        }
        if (StringUtils.isBlank(encodingChoice.getValue())) {
            errorMessage += "编码不能为空!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            AlertUtil.showWarnAlert(errorMessage);
            return false;
        }
    }

    @FXML
    void testConnection() {
        JdbcConnection config = extractConfigForUI();
        if (config == null) {
            return;
        }
        try {
            DbUtil.getConnection(config);
            AlertUtil.showInfoAlert("连接成功");
        } catch (DbDriverLoadingException e) {
            _LOG.error("{}", e);
            AlertUtil.showWarnAlert("连接失败, " + e.getMessage());
        } catch (Exception e) {
            _LOG.error(e.getMessage(), e);
            AlertUtil.showWarnAlert("连接失败");
        }

    }

    @FXML
    void cancel() {
        getDialogStage().close();
    }

    public void setConfig(JdbcConnection config) {
        isUpdate = true;
        primayKey = config.getId(); // save id for update config
        nameField.setText(config.getName());
        hostField.setText(config.getHost());
        portField.setText(config.getPort());
        userNameField.setText(config.getUsername());
        passwordField.setText(config.getPassword());
        encodingChoice.setValue(config.getEncoding());
        dbTypeChoice.setValue(config.getDbType());
        schemaField.setText(config.getSchema());
    }

}
