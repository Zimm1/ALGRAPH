package com.simonecavazzoni.algraph.ui;

import com.simonecavazzoni.algraph.res.Colors;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import com.simonecavazzoni.algraph.model.Node;


public class NodeUI extends Group {

    private static final double DEFAULT_CENTER_X = 0;
    private static final double DEFAULT_CENTER_Y = 0;
    public static final double DEFAULT_RADIUS = 20;

    private final Node node;

    private Circle circle;
    private Label label;
    private Label distanceLabel;

    public NodeUI(Node node) {
        this(node, DEFAULT_CENTER_X, DEFAULT_CENTER_Y, DEFAULT_RADIUS);
    }

    public NodeUI(Node node, double centerX, double centerY, double radius) {
        super();

        this.node = node;

        circle = new Circle(centerX, centerY, radius, Colors.DEFAULT_COLOR);

        label = new Label(node.getLabel());
        label.setTextFill(Colors.LIGHT_TEXT_COLOR);
        label.setAlignment(Pos.CENTER);
        label.translateXProperty().bind(circle.centerXProperty().subtract(circle.radiusProperty()));
        label.translateYProperty().bind(circle.centerYProperty().subtract(circle.radiusProperty()));
        label.prefWidthProperty().bind(circle.radiusProperty().multiply(2));
        label.prefHeightProperty().bind(circle.radiusProperty());
        label.setLabelFor(this);

        distanceLabel = new Label();
        distanceLabel.setTextFill(Colors.LIGHT_TEXT_COLOR);
        distanceLabel.setAlignment(Pos.CENTER);
        distanceLabel.translateXProperty().bind(circle.centerXProperty().subtract(circle.radiusProperty()));
        distanceLabel.translateYProperty().bind(circle.centerYProperty());
        distanceLabel.prefWidthProperty().bind(circle.radiusProperty().multiply(2));
        distanceLabel.prefHeightProperty().bind(circle.radiusProperty());
        distanceLabel.setLabelFor(this);

        this.getChildren().addAll(circle, label, distanceLabel);

        this.setCursor(Cursor.HAND);
    }

    public Node getNode() {
        return node;
    }

    public Circle getCircle() {
        return circle;
    }

    public Label getLabel() {
        return label;
    }

    public void highlight(boolean highlight) {
        circle.setFill(highlight ? Colors.PRIMARY_COLOR : Colors.DEFAULT_COLOR);
    }

    public Label getDistanceLabel() {
        return distanceLabel;
    }
}
