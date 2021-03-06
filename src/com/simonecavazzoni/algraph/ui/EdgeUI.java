package com.simonecavazzoni.algraph.ui;

import com.simonecavazzoni.algraph.res.Colors;
import javafx.beans.InvalidationListener;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import com.simonecavazzoni.algraph.model.Edge;


/**
 * UI component for Edge model class
 * @see Edge
 */
public class EdgeUI extends Group {

    private static final double DEFAULT_ARROW_LENGTH = 10;
    private static final double DEFAULT_ARROW_WIDTH = 7;
    private static final double DEFAULT_LABEL_DISTANCE = 10;
    private static final double DEFAULT_LINE_DISTANCE = 5;
    private static final double DEFAULT_FONT_SIZE = 20;

    private Edge edge;

    private Line line;
    private Line arrowLeft;
    private Line arrowRight;
    private Label label;
    private boolean disabled = false;

    private Color lastColor = Colors.DEFAULT_COLOR;

    /**
     * Instantiates the UI component of an edge, initializes views and updater.
     * @param edge Linked edge model
     */
    public EdgeUI(Edge edge) {
        super();

        this.edge = edge;

        if (edge.isDirected() || edge.getN1().getLabel().compareTo(edge.getN2().getLabel()) > 0) {
            line = new Line();
            line.setStroke(Colors.DEFAULT_COLOR);

            if (edge.isDirected()) {
                arrowLeft = new Line();
                arrowRight = new Line();
            }

            label = new Label(String.valueOf(edge.getWeight()));
            label.setTextFill(Colors.DEFAULT_COLOR);
            label.setAlignment(Pos.CENTER);
            label.setLabelFor(this);
            label.setFont(new Font(DEFAULT_FONT_SIZE));

            edge.getN1().getUi().getCircle().centerXProperty().addListener(updater);
            edge.getN1().getUi().getCircle().centerYProperty().addListener(updater);
            edge.getN2().getUi().getCircle().centerXProperty().addListener(updater);
            edge.getN2().getUi().getCircle().centerYProperty().addListener(updater);

            label.widthProperty().addListener(updater);
            label.heightProperty().addListener(updater);

            updater.invalidated(null);

            this.getChildren().addAll(line, label);
            if (edge.isDirected()) {
                this.getChildren().addAll(arrowLeft, arrowRight);
            }
        } else {
            disabled = true;
        }
    }

    /**
     * Linked edge model.
     * @return Edge model
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Weight label.
     * @return Weight label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Updater callback, called when ui needs a refresh.
     */
    private InvalidationListener updater = o -> {
        double margin = getEdge().getN2().getUi().getCircle().getRadius();

        double
                sx = edge.getN1().getUi().getCircle().getCenterX(),
                sy = edge.getN1().getUi().getCircle().getCenterY(),
                ex = edge.getN2().getUi().getCircle().getCenterX(),
                ey = edge.getN2().getUi().getCircle().getCenterY(),
                dx = sx - ex,
                dy = sy - ey;

        double dist = Math.hypot(dx, dy);

        dx /= dist;
        dy /= dist;

        if (edge.isDirected()) {
            sx = sx + DEFAULT_LINE_DISTANCE * dy;
            sy = sy - DEFAULT_LINE_DISTANCE * dx;
            ex = ex + DEFAULT_LINE_DISTANCE * dy;
            ey = ey - DEFAULT_LINE_DISTANCE * dx;

        }

        line.setStartX(sx);
        line.setStartY(sy);
        line.setEndX(ex);
        line.setEndY(ey);

        double middleX = (sx + ex) / 2;
        double middleY = (sy + ey) / 2;

        double labelX = middleX + DEFAULT_LABEL_DISTANCE * dy - label.getWidth() / 2;
        double labelY = middleY - DEFAULT_LABEL_DISTANCE * dx - label.getHeight() / 2;
        label.setTranslateX(labelX);
        label.setTranslateY(labelY);

        if (edge.isDirected()) {
            double distance = Math.hypot(ex - sx, ey - sy);
            double t = (distance - margin + 1) / distance;

            double arrowsEx = (1 - t) * sx + t * ex;
            double arrowsEy = (1 - t) * sy + t * ey;

            arrowLeft.setEndX(arrowsEx);
            arrowLeft.setEndY(arrowsEy);
            arrowRight.setEndX(arrowsEx);
            arrowRight.setEndY(arrowsEy);

            double parallelComponent = DEFAULT_ARROW_LENGTH / (distance - margin);
            double orthogonalComponent = DEFAULT_ARROW_WIDTH / (distance - margin);

            // part in direction of main line
            dx = (sx - arrowsEx) * parallelComponent;
            dy = (sy - arrowsEy) * parallelComponent;

            // part orthogonal to main line
            double ox = (sx - arrowsEx) * orthogonalComponent;
            double oy = (sy - arrowsEy) * orthogonalComponent;

            arrowLeft.setStartX(arrowsEx + dx - oy);
            arrowLeft.setStartY(arrowsEy + dy + ox);
            arrowRight.setStartX(arrowsEx + dx + oy);
            arrowRight.setStartY(arrowsEy + dy - ox);
        }
    };

    /**
     * Sets current weight UI.
     * @param weight Current weight
     */
    public void setWeight(int weight) {
        if (disabled) {
            return;
        }

        label.setText(String.valueOf(weight));
    }

    /**
     * Resets highlight state, sets color to default
     */
    public void resetHighlight() {
        lastColor = Colors.DEFAULT_COLOR;
        highlight(false, Colors.PRIMARY_COLOR);
    }

    /**
     * Sets highlight state and changes colors accordingly.
     * @param highlight Current highlight state
     * @param color Color to apply
     */
    public void highlight(boolean highlight, Color color) {
        if (line == null) {
            return;
        }
        Color currentColor = (Color)line.getStroke();

        if (highlight) {
            if (currentColor.equals(color)) {
                return;
            }
            lastColor = currentColor;
        }

        line.setStroke(highlight ? color : lastColor);
        label.setTextFill(highlight ? color : lastColor);

        if (edge.isDirected()) {
            arrowLeft.setStroke(highlight ? color : lastColor);
            arrowRight.setStroke(highlight ? color : lastColor);
        }
    }
}
