package controller;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import model.Edge;
import model.Graph;
import model.Node;
import service.GraphGenerator;
import ui.NodeUI;
import utils.DialogUtils;
import utils.Strings;

import java.util.stream.Collectors;

public class GraphController {
    @FXML
    private Pane container;

    private Graph graph;

    // Events data
    private NodeUI movingNode;
    private double eventSceneX, eventSceneY, eventTranslateX, eventTranslateY, contextMenuX, contextMenuY;

    private ContextMenu contextMenu;

    @FXML
    private void initialize() {
        //createTestGraph();
        graph = GraphGenerator.generateGraph(3, 0, 20, true);

        initEventHandlers();
        initGraphUi();
    }

    private void initEventHandlers() {
        container.setOnContextMenuRequested(event -> {
            Parent parent = event.getPickResult().getIntersectedNode().getParent();

            while (parent != null) {
                if (parent instanceof NodeUI) {
                    NodeUI nodeUI = (NodeUI)parent;
                    showContextMenu(GraphController.this.createNodeContextMenu(nodeUI.getNode()), event);
                    return;
                }

                parent = parent.getParent();
            }

            showContextMenu(createMainContextMenu(), event);
        });

        container.setOnMousePressed(event -> {

            hideContextMenu();

            Parent parent = event.getPickResult().getIntersectedNode().getParent();

            while (parent != null) {
                if (parent instanceof NodeUI) {
                    eventSceneX = event.getSceneX();
                    eventSceneY = event.getSceneY();

                    movingNode = (NodeUI) parent;
                    Circle source = movingNode.getCircle();

                    eventTranslateX = source.getCenterX();
                    eventTranslateY = source.getCenterY();

                    parent.setCursor(Cursor.CLOSED_HAND);

                    return;
                }

                parent = parent.getParent();
            }
        });

        container.setOnMouseDragged(event -> {
            if (movingNode == null) {
                return;
            }

            Circle source = movingNode.getCircle();

            Double translateX = eventTranslateX + event.getSceneX() - eventSceneX;
            Double translateY = eventTranslateY + event.getSceneY() - eventSceneY;

            source.setCenterX(translateX);
            source.setCenterY(translateY);
        });

        container.setOnMouseReleased(event -> {
            if (movingNode == null) {
                return;
            }

            movingNode.setCursor(Cursor.HAND);
            movingNode = null;
        });
    }

    private ContextMenu createNodeContextMenu(Node node) {
        ContextMenu nodeContextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem(Strings.remove_node);
        item1.setOnAction(event -> removeNode(node));
        nodeContextMenu.getItems().add(item1);

        return nodeContextMenu;
    }

    private ContextMenu createMainContextMenu() {
        ContextMenu mainContextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem(Strings.create_node);
        item1.setOnAction(event -> addNode());
        mainContextMenu.getItems().add(item1);

        return mainContextMenu;
    }

    private void showContextMenu(ContextMenu contextMenu, ContextMenuEvent event) {
        contextMenuX = event.getSceneX();
        contextMenuY = event.getSceneY();

        this.contextMenu = contextMenu;
        this.contextMenu.show((Pane) event.getSource(), event.getScreenX(), event.getScreenY());
    }

    private void hideContextMenu() {
        if (contextMenu != null) {
            contextMenu.hide();
            contextMenu = null;
        }
    }

    // TODO Change for graph import/generation
    private void createTestGraph() {
        graph = new Graph();

        for (char c = 'A'; c <= 'F'; ++c) {
            graph.addNode(String.valueOf(c));
        }

        graph.addEdge("A", "D");
        graph.addEdge("A", "E");
        graph.addEdge("A", "F");
        graph.addEdge("B", "D");
        graph.addEdge("B", "E");
        graph.addEdge("B", "F");
        graph.addEdge("C", "D");
        graph.addEdge("C", "E");
        graph.addEdge("C", "F");

        System.out.println(graph.getAdjacencies());
    }

    // TODO Change for a real display algorithm
    private void initGraphUi() {
        int count = 0;
        int startX = 50;
        int startY = 50;

        for (Node n : graph.getNodes()) {
            NodeUI ui = n.getUi();
            ui.getCircle().setCenterX(startX + startX * 3 * (count % 3));
            ui.getCircle().setCenterY(startY + startY * 3 * (count / 3));

            count++;
        }

        updateGraphUI();
    }

    private void updateGraphUI() {
        container.getChildren().clear();
        container.getChildren().addAll(graph.getEdges().stream().map(Edge::getUi).collect(Collectors.toSet()));
        container.getChildren().addAll(graph.getNodes().stream().map(Node::getUi).collect(Collectors.toSet()));
    }

    private void addNode() {
        try {
            Node node = graph.addNode(DialogUtils.showTextInputDialog(
                    Strings.create_node_title, null, Strings.name, null));

            if (node == null) {
                DialogUtils.showErrorDialog(
                        Strings.error, Strings.create_node, Strings.error_create_node);
                return;
            }

            node.getUi().getCircle().setCenterX(contextMenuX);
            node.getUi().getCircle().setCenterY(contextMenuY);
            updateGraphUI();

        } catch (Exception e) {
            //
        }
    }

    private void removeNode(Node node) {
        if (graph.removeNode(node)) {
            updateGraphUI();
        }
    }
}
