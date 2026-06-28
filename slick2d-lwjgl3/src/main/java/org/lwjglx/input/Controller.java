package org.lwjglx.input;

public interface Controller {
    String getName();

    int getButtonCount();

    boolean isButtonPressed(int index);

    int getAxisCount();

    float getAxisValue(int index);

    String getAxisName(int index);

    int getXAxisIndex();

    int getYAxisIndex();

    float getXAxisValue();

    float getYAxisValue();

    int getPovX();

    int getPovY();
}
