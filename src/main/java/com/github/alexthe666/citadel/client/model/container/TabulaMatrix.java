package com.github.alexthe666.citadel.client.model.container;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Stack;

@OnlyIn(Dist.CLIENT)
public class TabulaMatrix {
    public Stack<Matrix4f> matrixStack;

    public TabulaMatrix() {
        this.matrixStack = new Stack<>();
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        this.matrixStack.push(matrix);
    }

    public void push() {
        this.matrixStack.push(new Matrix4f(this.matrixStack.peek()));
    }

    public void pop() {
        if (this.matrixStack.size() < 2) {
            try {
                throw new Exception("Stack Underflow for tabula matrix!!!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.matrixStack.pop();
    }

    public void translate(float x, float y, float z) {
        Matrix4f matrix = this.matrixStack.peek();
        Matrix4f translation = new Matrix4f();
        translation.setIdentity();
        translation.setTranslation(x, y, z);
        matrix.multiply(translation);
    }

    public void translate(double x, double y, double z) {
        translate((float)x, (float)y, (float)z);
    }

    public void rotate(double angle, double x, double y, double z) {
        Matrix4f matrix = this.matrixStack.peek();
        Matrix4f rotation = new Matrix4f();
        rotation.setIdentity();
        rotation.load(new Matrix4f());
        matrix.multiply(rotation);
    }

    public void rotate(float angle, float x, float y, float z) {
        rotate((float)angle, (float)x, (float)y, (float)z);
    }

    public void rotate(Matrix4f qaut) {
        Matrix4f matrix = this.matrixStack.peek();
        Matrix4f rotation = new Matrix4f();
        rotation.load(qaut);
        matrix.multiply(rotation);
    }

    public void scale(float x, float y, float z) {
        Matrix4f matrix = this.matrixStack.peek();
        Matrix4f scale = new Matrix4f();
        matrix.multiply(scale.createScaleMatrix(x, y, z));
    }

    public void scale(double x, double y, double z) {
        scale((float)x, (float)y, (float)z);
    }

    public void transform(Vector3f point) {
        Matrix4f matrix = this.matrixStack.peek();
        matrix.translate(point);
    }

    public Vector3f getTranslation() {
        Matrix4f matrix = this.matrixStack.peek();
        Vector3f translation = new Vector3f();
        matrix.translate(translation);
        return translation;
    }

    public Matrix4f getRotation() {
        Matrix4f matrix = this.matrixStack.peek();
        return matrix.copy();
    }

    public Vector3f getScale() {
        Matrix4f matrix = this.matrixStack.peek();
        /*float x = (float) Math.sqrt(matrix.m00 * matrix.m00 + matrix.m10 * matrix.m10 + matrix.m20 * matrix.m20);
        float y = (float) Math.sqrt(matrix.m01 * matrix.m01 + matrix.m11 * matrix.m11 + matrix.m21 * matrix.m21);
        float z = (float) Math.sqrt(matrix.m02 * matrix.m02 + matrix.m12 * matrix.m12 + matrix.m22 * matrix.m22);
        return new Vector3f(x, y, z);
         */
        return new Vector3f(1.0F, 1.0F, 1.0F);
    }

    public void multiply(TabulaMatrix matrix) {
        this.matrixStack.peek().multiply(matrix.matrixStack.peek());
    }

    public void multiply(Matrix4f matrix) {
        this.matrixStack.peek().multiply(matrix);
    }

    public void add(TabulaMatrix matrix) {
        this.matrixStack.peek().add(matrix.matrixStack.peek());
    }

    public void add(Matrix4f matrix) {
        this.matrixStack.peek().add(new Matrix4f(matrix));
    }

    public void invert() {
        this.matrixStack.peek().invert();
    }
}