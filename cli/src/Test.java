import utils.FileAnalyzer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 8/29/2015.
 */
public class Test {


    public static void main(String[] args) {

        Shape shape = ShapeFactory.getShape(Shape.CIRCLE);
        _test(shape);

    }

    public static void _test(Shape shape){
        System.out.println(shape instanceof Circle);
        Circle c = (Circle) shape;
        c.draw();
        c.specialMethod();
    }

    interface Shape {
        int
                CIRCLE = 1,
                TRIANGLE = 2,
                RECTANGLE = 3;

        void draw();
    }

    static class Circle implements Shape {

        public void specialMethod(){
            System.out.println("Circle.specialMethod");
        }

        @Override
        public void draw() {
            System.out.println("Circle.draw");
        }
    }

    static class Triangle implements Shape {

        @Override
        public void draw() {
            System.out.println("Triangle.draw");
        }
    }

    static class Rectangle implements Shape {

        @Override
        public void draw() {
            System.out.println("Rectangle.draw");
        }
    }

    static class ShapeFactory {
        public static Shape getShape(int shapeCode) {
          switch (shapeCode){
              case Shape.CIRCLE:
                  return new Circle();
              case Shape.TRIANGLE:
                  return new Triangle();
              case Shape.RECTANGLE:
                  return new Rectangle();
              default:
                  throw new IllegalArgumentException("Invalid shape code "+shapeCode);
          }
        }
    }

}
