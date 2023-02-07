import java.awt.Graphics;
import java.io.Serializable;
//Shape 
public class Shape implements Serializable {
	int x, y;
	int width, height;
	boolean selected;  //선택된 도형이 있음을 알기 위한 핸들러

	public Shape(int x, int y) {  //직선 전용 생성자
		this.x = x;
		this.y = y;
		this.selected = false;
	}
	
	public Shape(int x, int y, int width, int height) { //사각, 타원 전용 생성자
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.selected = false; 
	}
}
// Shape 부모로 받는 Rectangle 
class Rectangle extends Shape {
	public Rectangle(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	public void draw(Graphics g) {
		g.drawRect(x, y, width, height);
		//checkbox
		if (selected) { //도형이 선택되었을 때만
			g.drawRect(x - 2, y - 2, 4, 4);  //체크박스의 width,height는 항상 4로 고정
			g.drawRect(x + width - 2, y + height - 2, 4, 4);
		}
	}
}
// Shape 부모로 받는 Circle
class Circle extends Shape {
	public Circle(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	public void draw(Graphics g) {
		g.drawOval(x, y, width, height);
		//checkbox
		if (selected) {
			g.drawRect(x - 2, y - 2, 4, 4);  //체크박스의 width,height는 항상 4로 고정
			g.drawRect(x + width - 2, y + height - 2, 4, 4);
		}
	}
}
// Shape 부모로 받는 Line
class Line extends Shape {
	public Line(int x1, int y1, int x2, int y2) {
		super(x1,y1);
		this.width = x2;
		this.height = y2;
	}
	public void draw(Graphics g) {
		g.drawLine(x, y, width, height);
		//checkbox
		if (selected) {
			g.drawRect(x - 2, y - 2, 4, 4);   //체크박스의 width,height는 항상 4로 고정
			g.drawRect(width - 2,height - 2, 4, 4);
		}
	}
}