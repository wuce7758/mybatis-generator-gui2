package tech.wetech.mybatis.generator.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import tech.wetech.mybatis.generator.model.TableVO;
import tech.wetech.mybatis.generator.model.UITableColumnVO;
import tech.wetech.mybatis.generator.utils.AlertUtil;
import tech.wetech.mybatis.generator.utils.DbUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 表格
 *
 * @author cjbi
 */
public class TableController extends BaseFXController {

    @FXML
    private TextField modelTargetPackageTextField;
    @FXML
    private TextField mapperTargetPackageTextField;
    @FXML
    private TextField daoTargetPackageTextField;
    @FXML
    private TextField tableNameFieldTextField;
    @FXML
    private TextField domainObjectNameTextField;
    @FXML
    private TextField generateKeysFieldTextField;    //主键ID
    @FXML
    private TextField modelTargetProjectTextField;
    @FXML
    private TextField mappingTargetProjectTextField;
    @FXML
    private TextField daoTargetProjectTextField;
    @FXML
    private TextField mapperNameTextField;

    private TableVO tableVO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView dbImage = new ImageView("icons/computer.png");
        dbImage.setFitHeight(40);
        dbImage.setFitWidth(40);
        ImageView configImage = new ImageView("icons/config-list.png");
        configImage.setFitHeight(40);
        configImage.setFitWidth(40);
    }

    public TableVO getTableVO() {
        tableVO.setModelPackage(modelTargetPackageTextField.getText());
        tableVO.setMappingXMLPackage(mapperTargetPackageTextField.getText());
        tableVO.setDaoPackage(daoTargetPackageTextField.getText());
        tableVO.setTableName(tableNameFieldTextField.getText());
        tableVO.setDomainObjectName(domainObjectNameTextField.getText());
        tableVO.setGenerateKeys(generateKeysFieldTextField.getText());
        tableVO.setModelPackageTargetFolder(modelTargetProjectTextField.getText());
        tableVO.setMappingXMLTargetFolder(mappingTargetProjectTextField.getText());
        tableVO.setDaoTargetFolder(daoTargetProjectTextField.getText());
        tableVO.setMapperName(mapperNameTextField.getText());
        return tableVO;
    }

    public void setTableVO(TableVO tableVO) {
        this.tableVO = tableVO;

        modelTargetPackageTextField.setText(tableVO.getModelPackage());
        mapperTargetPackageTextField.setText(tableVO.getMappingXMLPackage());
        daoTargetPackageTextField.setText(tableVO.getDaoPackage());
        tableNameFieldTextField.setText(tableVO.getTableName());
        domainObjectNameTextField.setText(tableVO.getDomainObjectName());
        generateKeysFieldTextField.setText(tableVO.getGenerateKeys());
        modelTargetProjectTextField.setText(tableVO.getModelPackageTargetFolder());
        mappingTargetProjectTextField.setText(tableVO.getMappingXMLTargetFolder());
        daoTargetProjectTextField.setText(tableVO.getDaoTargetFolder());
        mapperNameTextField.setText(tableVO.getMapperName());
    }


    @FXML
    public void openTableColumnCustomizationPage() {
        String tableName = tableVO.getTableName();
        if (tableVO.getTableName() == null) {
            AlertUtil.showWarnAlert("请先在左侧选择数据库表");
            return;
        }
        SelectTableColumnController controller = loadDialog("定制列", FXMLConstant.SELECT_TABLE_COLUMN, true);
        controller.setTableController(this);
        try {
            // If select same schema and another tableVO, update tableVO data
            if (!tableName.equals(controller.getTableName())) {
                List<UITableColumnVO> tableColumns = DbUtil.getTableColumns(tableVO.getJdbcConnection(), tableName);
                controller.setColumnList(FXCollections.observableList(tableColumns));
                controller.setTableName(tableName);
            }
            controller.showDialogStage();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            AlertUtil.showErrorAlert(e.getMessage());
        }

    }
}
