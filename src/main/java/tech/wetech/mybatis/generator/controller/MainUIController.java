package tech.wetech.mybatis.generator.controller;

import com.alibaba.fastjson.JSON;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import tech.wetech.mybatis.generator.bridge.MybatisGeneratorBridge;
import tech.wetech.mybatis.generator.model.*;
import tech.wetech.mybatis.generator.utils.*;

import java.io.File;
import java.net.URL;
import java.sql.SQLRecoverableException;
import java.util.*;

/**
 * 主界面
 *
 * @author cjbi
 */
public class MainUIController extends BaseFXController {

    @FXML
    private Label connectionLabel;
    @FXML
    private Label configsLabel;
    @FXML
    private TreeView<String> leftDBTree;

    private final Map<String, TableController> tableControllerCache = new HashMap<>(16);

    private final MybatisGeneratorBridge mybatisGeneratorBridge = new MybatisGeneratorBridge();

    public MainUIController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView dbImage = new ImageView("icons/computer.png");
        dbImage.setFitHeight(40);
        dbImage.setFitWidth(40);
        connectionLabel.setGraphic(dbImage);
        connectionLabel.setOnMouseClicked(event -> {
            JdbcConnectionController controller = loadDialog("新建数据库连接", FXMLConstant.JDBC_CONNECTION, false);
            controller.getDialogStage().showAndWait();
            loadLeftDBTree();
        });
        ImageView configImage = new ImageView("icons/config-list.png");
        configImage.setFitHeight(40);
        configImage.setFitWidth(40);
        configsLabel.setGraphic(configImage);
        configsLabel.setOnMouseClicked(event -> {
            ConfigsController controller = loadDialog("配置", FXMLConstant.CONFIGS, false);
            controller.showDialogStage();
        });

        ImageView tplSettingsImage = new ImageView("icons/tpl_settings.png");
        tplSettingsImage.setFitHeight(40);
        tplSettingsImage.setFitWidth(40);
        ImageView variableMapConfigImage = new ImageView("icons/variable_settings.png");
        variableMapConfigImage.setFitHeight(40);
        variableMapConfigImage.setFitWidth(40);

        leftDBTree.setShowRoot(false);
        leftDBTree.setRoot(new TreeItem<>());
        Callback<TreeView<String>, TreeCell<String>> defaultCellFactory = TextFieldTreeCell.forTreeView();
        leftDBTree.setCellFactory((TreeView<String> tv) -> {
            TreeCell<String> cell = defaultCellFactory.call(tv);
            cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                int level = leftDBTree.getTreeItemLevel(cell.getTreeItem());
                TreeCell<String> treeCell = (TreeCell<String>) event.getSource();
                TreeItem<String> treeItem = treeCell.getTreeItem();
                if (level == 1) {
                    final ContextMenu contextMenu = new ContextMenu();
                    MenuItem item1 = new MenuItem("关闭连接");
                    item1.setOnAction(event1 -> treeItem.getChildren().clear());
                    MenuItem item2 = new MenuItem("编辑连接");
                    item2.setOnAction(event1 -> {
                        JdbcConnection selectedConfig = (JdbcConnection) treeItem.getGraphic().getUserData();
                        JdbcConnectionController controller = loadDialog("编辑数据库连接", FXMLConstant.JDBC_CONNECTION, false);
                        controller.setConfig(selectedConfig);
                        controller.getDialogStage().showAndWait();
                        loadLeftDBTree();
                    });
                    MenuItem item3 = new MenuItem("删除连接");
                    item3.setOnAction(event1 -> {
                        JdbcConnection selectedConfig = (JdbcConnection) treeItem.getGraphic().getUserData();
                        try {
                            ConfigHelper.deleteDatabaseConfig(selectedConfig);
                            this.loadLeftDBTree();
                        } catch (Exception e) {
                            AlertUtil.showErrorAlert("Delete connection failed! Reason: " + e.getMessage());
                        }
                    });
                    contextMenu.getItems().addAll(item1, item2, item3);
                    cell.setContextMenu(contextMenu);
                }
                if (event.getClickCount() == 2) {
                    if (treeItem == null) {
                        return;
                    }
                    treeItem.setExpanded(true);
                    if (level == 1) {
                        JdbcConnection selectedConfig = (JdbcConnection) treeItem.getGraphic().getUserData();
                        try {
                            List<String> tables = DbUtil.getTableNames(selectedConfig);
                            if (tables != null && tables.size() > 0) {
                                ObservableList<TreeItem<String>> children = cell.getTreeItem().getChildren();
                                children.clear();
                                for (String tableName : tables) {
                                    TreeItem<String> newTreeItem = new TreeItem<>();
                                    ImageView imageView = new ImageView("icons/table.png");
                                    imageView.setFitHeight(16);
                                    imageView.setFitWidth(16);
                                    newTreeItem.setGraphic(imageView);
                                    newTreeItem.setValue(tableName);
                                    children.add(newTreeItem);
                                }
                            }
                        } catch (SQLRecoverableException e) {
                            logger.error(e.getMessage(), e);
                            AlertUtil.showErrorAlert("连接超时");
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            AlertUtil.showErrorAlert(e.getMessage());
                        }
                    } else if (level == 2) {
                        // left DB tree level3
                        String tableName = treeCell.getTreeItem().getValue();
                        JdbcConnection selectedConfig = (JdbcConnection) treeItem.getParent().getGraphic().getUserData();
                        if (tabPaneMain.getTabs().stream().allMatch(tab -> !tab.getText().equals(selectedConfig.getSchema() + "." + tableName))) {

                            String domainObjectName = MyStringUtils.dbStringToCamelStyle(tableName);
                            JdbcConnection jdbcConnection = (JdbcConnection) treeItem.getParent().getGraphic().getUserData();


                            TableVO table = null;
                            try {
                                table = ConfigHelper.loadConfig(ConfigConstant.DEFAULT_TABLE_CONFIG, TableVO.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            table.setDomainObjectName(domainObjectName);
                            table.setTableName(tableName);
                            table.setMapperName(domainObjectName.concat("Mapper"));
                            table.setJdbcConnection(jdbcConnection);

                            TableController tableController = addMainTab(jdbcConnection.getSchema() + "." + tableName, FXMLConstant.TABLE.getFxml(), null, null);
                            tableController.setTableVO(table);

                            tableControllerCache.put(jdbcConnection.getSchema() + "." + tableName, tableController);
                        } else {
                            Tab selectTab = tabPaneMain.getTabs().stream().filter(tab -> tab.getText().equals(selectedConfig.getSchema() + "." + tableName)).findFirst().get();
                            tabPaneMain.getSelectionModel().select(selectTab);
                        }

                    }
                }
            });
            return cell;
        });

        loadLeftDBTree();
        //默认选中第一个，否则如果忘记选择，没有对应错误提示
    }

    private boolean isTableVOValid(TableVO table) {
        String errorMessage = "";
        if (StringUtils.isBlank(table.getTableName())) {
            errorMessage += "表名不能为空!\n";
        }
        if (StringUtils.isBlank(table.getDomainObjectName())) {
            errorMessage += "Java实体类名不能为空!\n";
        }
        if (StringUtils.isBlank(table.getModelPackage())) {
            errorMessage += "实体类包名不能为空!\n";
        }
        if (StringUtils.isBlank(table.getModelPackageTargetFolder())) {
            errorMessage += "实体类存放目录不能为空!\n";
        }
        if (StringUtils.isBlank(table.getMappingXMLTargetFolder())) {
            errorMessage += "Mapper接口存放目录不能为空!\n";
        }
        if (StringUtils.isBlank(table.getDaoPackage())) {
            errorMessage += "Mapper接口包名不能为空!\n";
        }
        if (StringUtils.isBlank(table.getMappingXMLPackage())) {
            errorMessage += "映射XML文件包名不能为空!\n";
        }
        if (StringUtils.isBlank(table.getMappingXMLTargetFolder())) {
            errorMessage += "映射XML文件存放目录不能为空!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            AlertUtil.showErrorAlert(errorMessage);
            return false;
        }
    }

    @FXML
    public void generateCode() throws Exception {
        logger.info("generating code......");

        GeneratorContext context = new GeneratorContext();

        List<TableVO> tableList = new ArrayList<>();

        context.setTables(tableList);

        if (tabPaneMain.getTabs().isEmpty()) {
            AlertUtil.showWarnAlert("请选择一张表再进行下一步操作！");
            return;
        }

        GeneratorConfig config = ConfigHelper.loadConfig(ConfigConstant.GENERATOR_CONFIG, GeneratorConfig.class);

        if (config == null) {
            ConfigsController controller = loadDialog("配置", FXMLConstant.CONFIGS, false);
            AlertUtil.showWarnAlert("第一次使用请设置配置信息！");
            controller.getDialogStage().showAndWait();
        }

        context.setConfiguration(config);

        for (Tab tab : getTabPaneMain().getTabs()) {
            TableController tableController = tableControllerCache.get(tab.getText());
            TableVO table = tableController.getTableVO();
            if (isTableVOValid(table)) {
                tableList.add(table);
            } else {
                getTabPaneMain().getSelectionModel().select(tab);
                return;
            }
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFolder = directoryChooser.showDialog(getPrimaryStage());
        if (selectedFolder == null) {
            return;
        }
        context.setProjectFolder(selectedFolder.getAbsolutePath());
        logger.info(JSON.toJSONString(context));

        if (!checkDirs(context)) {
            return;
        }

        //生成代码
        UIProgressCallback alert = new UIProgressCallback(Alert.AlertType.INFORMATION);
        mybatisGeneratorBridge.setProgressCallback(alert);
        alert.show();
        try {

            TemplateConfig templateConfig = ConfigHelper.loadConfig(ConfigConstant.TEMPLATE_CONFIG, TemplateConfig.class);
            PropertiesConfig propertiesConfig = ConfigHelper.loadConfig(ConfigConstant.PROPERTIES_CONFIG, PropertiesConfig.class);

            context.setTemplateConfig(templateConfig);
            context.setPropertiesConfig(propertiesConfig);

            mybatisGeneratorBridge.generate(context);

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    /**
     * 检查并创建不存在的文件夹
     *
     * @return
     */
    private boolean checkDirs(GeneratorContext generatorData) {
        List<String> dirs = new ArrayList<>();
        dirs.add(generatorData.getProjectFolder());

        generatorData.getTables().forEach(table -> {
            dirs.add(FilenameUtils.normalize(generatorData.getProjectFolder().concat("/").concat(table.getModelPackageTargetFolder())));
            dirs.add(FilenameUtils.normalize(generatorData.getProjectFolder().concat("/").concat(table.getDaoTargetFolder())));
            dirs.add(FilenameUtils.normalize(generatorData.getProjectFolder().concat("/").concat(table.getMappingXMLTargetFolder())));
        });

        boolean haveNotExistFolder = false;
        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists()) {
                haveNotExistFolder = true;
            }
        }
        if (haveNotExistFolder) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText(FOLDER_NO_EXIST);
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.isPresent()) {
                if (ButtonType.OK == optional.get()) {
                    try {
                        for (String dir : dirs) {
                            FileUtils.forceMkdir(new File(dir));
                        }
                        return true;
                    } catch (Exception e) {
                        AlertUtil.showErrorAlert("创建目录失败，请检查目录是否是文件而非目录");
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    void loadLeftDBTree() {
        TreeItem rootTreeItem = leftDBTree.getRoot();
        rootTreeItem.getChildren().clear();
        try {
            List<JdbcConnection> dbConfigs = ConfigHelper.loadDatabaseConfig();
            for (JdbcConnection dbConfig : dbConfigs) {
                TreeItem<String> treeItem = new TreeItem<>();
                treeItem.setValue(dbConfig.getName());
                ImageView dbImage = new ImageView("icons/database.png");
                dbImage.setFitHeight(16);
                dbImage.setFitWidth(16);
                dbImage.setUserData(dbConfig);
                treeItem.setGraphic(dbImage);
                rootTreeItem.getChildren().add(treeItem);
            }
        } catch (Exception e) {
            logger.error("connect db failed, reason: {}", e);
            AlertUtil.showErrorAlert(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
        }
    }

}
