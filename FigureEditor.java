import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FigureEditor extends JFrame {
   String selectedBtn = ""; //선택한 버튼의 이름을 알기 위해
   Vector<Shape> shapeArray = new Vector<>();
   // Vector를 사용하는 이유:  Vector는 동기화 되어있어, 한번에 하나의 스레드만 접근할 수 있기 때문에 스레드 안전 
    // 즉, 멀티스레드 환경에 적합하기 때문에 Vector를 사용

   PanelA Pa = new PanelA(); 
   
   //Figure Editor
   public FigureEditor() {
      setTitle("Figure Editor");
      setSize(600, 300);
      
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      Container c = getContentPane();
      c.setLayout(new BorderLayout());
      
      c.add(Pa, BorderLayout.CENTER);
      c.add(new PanelC(), BorderLayout.WEST);
      
      setVisible(true);
   }

   //모든 도형 선택 해제
   private void unselectAll() {
      for (Shape item : shapeArray) {
         if (item instanceof Rectangle || item instanceof Circle 
               || item instanceof Line) item.selected = false;
      }
   }
   
   //Panel A (MymouseListener, PaintComponent)
   private class PanelA extends JPanel {
      private Point start, end;
      private int offX = 0, offY = 0; // 도형 이동 시 마우스를 누른 점을 기준으로 하기 위한 변수

      public PanelA() {
         setLayout(new FlowLayout());
         setBackground(Color.YELLOW);

         addMouseListener(new MymouseListener());  //clicked, pressed, released 이벤트 리스너 등록
         addMouseMotionListener(new MymouseListener()); //dragged 이벤트 리스너 등록
      }

      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         if(start == null) {   //Released 이벤트 발생 시, 그려왔던 도형들을 유지하기 위해 그려준다.
                              //Released메서드에 start == null 이기 때문에
            g.setColor(Color.BLUE);
            
            for (Shape item : shapeArray) {  //shapeArray에서 꺼낼 Shape객체가 없을 때 까지 
                                            //차례대로 Shape객체를 꺼내 item에 대입
                                          
               if (item instanceof Rectangle) { //꺼낸 객체가 사각형일때 
                  ((Rectangle) item).draw(g);   
               } else if (item instanceof Circle) { //꺼낸 객체가 타원일때
                  ((Circle) item).draw(g);
               } else if (item instanceof Line) { //꺼낸 객체가 직선일때
                  ((Line) item).draw(g);
               }
            }
            
            return;  //모든 도형들을 그려주고 종료
         }
         
         g.setColor(Color.BLUE);
         int x = Math.min(start.x, end.x);  //새로운 도형을 그리기 위한 x,y,width,height 세팅
         int y = Math.min(start.y, end.y);
         int width = Math.abs(start.x - end.x);
         int height = Math.abs(start.y - end.y);

         // selectedBtn에 따라 다르게 그림
         switch(selectedBtn) {
            case "사각":
               g.drawRect(x, y, width, height);
               break;
            case "타원":
               g.drawOval(x, y, width, height);
               break;
            case "직선":
               g.drawLine(start.x, start.y, end.x, end.y);  //직선은 시작점과 끝점만 알면 되기에
               break;
         }

         // Released 이벤트 발생 외에도 그려왔던 도형들을 화면에 그림
         for (Shape shape : shapeArray) {
            if (shape instanceof Rectangle) {
               ((Rectangle) shape).draw(g);
            } else if (shape instanceof Circle) {
               ((Circle) shape).draw(g);
            } else if (shape instanceof Line) {
               ((Line) shape).draw(g);
            }
         }
      }
      private class MymouseListener extends MouseAdapter {
         @Override
         public void mouseClicked(MouseEvent e) {
            // 도형 선택 기능
            Point point = e.getPoint(); //클릭이벤트 발생 시 좌표를 알기 위해
            for (Shape item : shapeArray) {  //클릭해서 선택한 도형에 selected = true 하기 위해
               if (item instanceof Rectangle || item instanceof Circle) {
                  if ((point.x >= item.x && point.y >= item.y) && 
                     (point.x <= item.x + item.width &&
                      point.y <= item.y + item.height)) { //클릭 시, 선택한 도형이 있는 경우
                     unselectAll(); // 이미 선택된 도형이 있을 시 선택 해제
                     item.selected = true; // 새롭게 선택한 도형을 알기 위해 
                     break;
                  }
                  else item.selected = false; //클릭 시, 선택된 도형이 없으므로 false
               }
               else if (item instanceof Line) {  //직선은 방식이 조금 다르므로 따로 구현
                  if (point.x >= Math.min(item.x, item.width) && 
                     point.y >= Math.min(item.y, item.height) &&
                     point.x <= Math.max(item.x, item.width) &&
                     point.y <= Math.max(item.y, item.height)) {                     
                     //직선이므로 여기서 width는 end.x이고 height는 end.y임 
                     unselectAll();  // 이미 선택된 도형이 있을 시 선택 해제
                     item.selected = true;  // 새롭게 선택한 도형을 알기 위해 
                     break;
                  }
                  else item.selected = false;
               }
            }
         }
         @Override
         public void mousePressed(MouseEvent e) { 
            start = e.getPoint();  //마우스가 눌러졌을 때, 시작점을 알기 위해

            for (Shape item : shapeArray) {
               if (item instanceof Rectangle || item instanceof Circle || item instanceof Line) {
                  if (item.selected) { //도형이 선택되었을 때만
                     offX = e.getPoint().x - item.x; //누른 점의 x좌표와 도형의 x좌표의 차이
                     offY = e.getPoint().y - item.y; //누른 점의 y좌표와 도형의 y좌표의 차이
                     break;
                  }
               }
            }
         }
         
         @Override
         public void mouseDragged(MouseEvent e) {
            end = e.getPoint();
            for (Shape item : shapeArray) {
               if (item instanceof Rectangle || 
                     item instanceof Circle || item instanceof Line) {
                  if (item.selected) { //도형이 선택되었을 때만
                     int lastX = item.x, lastY = item.y; //크기조절 하기 전의 원래 도형의 x,y좌표
                     // 도형 크기 조절 기능
                     if ((item.x - 10 <= end.x && item.x + 10 >= end.x) //item 객체 x < 받은 x = 왼쪽, 반대는 오른쪽을 나타냄
                           && (item.y - 10 <= end.y && item.y + 10 >= end.y)) { //item y < 받은 y = 위쪽, 반대는 아래를 나타냄
                        if (item.width > 0 && item.height > 0) { //객체의 기존 가로보다 크거나 세로보다 클 때 
                           item.x = e.getPoint().x; //좌표 받아두기
                           item.y = e.getPoint().y;
                           if (!(item instanceof Line)) {  //직선은 조금 다르기 때문에
                              item.width = item.width - (item.x - lastX);
                              item.height = item.height - (item.y - lastY);
                           }
                        }
                        repaint();
                     }
                     // 왼 위, 오른쪽 아래로 크기 조절
                     else if (!(item instanceof Line) && (item.x + item.width - 10 <= end.x && item.x + item.width + 10 >= end.x) 
                           && (item.y + item.height - 10 <= end.y && item.y + item.height + 10 >= end.y)) {
                        // line이 아닌 객체 일 때, 객체의 width < 받은 x = 작아질때나  && 객체 width > 받은 x = 커질때 
                       // 객체의 height < 받은 y = 작아질때나 && 객체 height > 받은 y = 길어질 때
                       item.width = item.width + (e.getPoint().x - (item.x + item.width)); //width 업데이트 = 기존 + 받은 좌표와의 차이 (크면 더할 것이고 작으면 -로 줄여줌)
                        item.height = item.height + (e.getPoint().y - (item.y + item.height)); //height 업데이트 = 기존 + 받은 좌표와의 차이 (크면 더할 것이고 작으면 -로 줄여줌)
                     }
                     else if ((item instanceof Line) 
                           && (item.width - 10 <= end.x && item.width + 10 >= end.x) 
                           && (item.height - 10 <= end.y && item.height + 10 >= end.y)) {
                        item.width = e.getPoint().x; //line에서는 item.width = x2 (shape에서 정의됨)
                        item.height = e.getPoint().y; //line에서는 item.height = y2 (shape에서 정의됨)
                     }
                     // 도형 이동 기능
                     else if (!(item instanceof Line) && //선택한 도형이 사각, 타원인 경우 +
                           (e.getPoint().x >= item.x && e.getPoint().y >= item.y) &&
                           (e.getPoint().x <= item.x + item.width &&
                           e.getPoint().y <= item.y + item.height)) { 
                        //드래그한 좌표가 선택한 도형 경계선 및 내부인 경우
                        item.x = e.getPoint().x - offX;  //Pressed에서 설정한 offX를 빼줘야된다
                        item.y = e.getPoint().y - offY;  //Pressed에서 설정한 offY를 빼줘야된다
                     }
                     else if ((item instanceof Line) && //선택한 도형이 직선인 경우 +
                           (Math.min(item.x, item.width) <= e.getPoint().x && //직선일때, width는 끝점의 x좌표
                           Math.max(item.x, item.width) >= e.getPoint().x) && 
                           (Math.min(item.y, item.height) <= e.getPoint().y && //직선일때, height는 끝점의 y좌표
                           Math.max(item.y, item.height) >= e.getPoint().y)) {
                        //드래그한 좌표가 선택한 직선 내부인 경우
                        item.x = e.getPoint().x - offX; 
                        item.y = e.getPoint().y - offY;
                        item.width -= lastX - item.x; //lastX는 이동하기 전의 원래 도형의 X좌표
                        item.height -= lastY - item.y; //lastY는 이동하기 전의 원래 도형의 Y좌표
                     }
                     repaint();
                     break;
                  } //if(item.selected) 끝부분
               }
            }
            repaint();
         }
         @Override
         public void mouseReleased(MouseEvent e) {
            end = e.getPoint();

            // 도형 등록 기능
            if (selectedBtn.equals("사각") || selectedBtn.equals("타원") || selectedBtn.equals("직선")) {
               Shape shp = null;
               if (selectedBtn.equals("사각"))
                  shp = new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.abs(start.x - end.x), Math.abs(start.y - end.y));
               else if (selectedBtn.equals("타원"))
                  shp = new Circle(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.abs(start.x - end.x), Math.abs(start.y - end.y));
               else if (selectedBtn.equals("직선")) shp = new Line(start.x, start.y, end.x, end.y);
               shapeArray.add(shp);
            }

            // 데이터 초기화
            selectedBtn = "";
            start = null;
            end = null;
            repaint();
         }
      }
   }  //PanelA 끝부분
   
   //PanelB (MyActionlistner, Buttons)
   private class PanelB extends JPanel {
      public PanelB() {
         setLayout(new GridLayout(7, 3, 3, 3));
         setBackground(Color.BLUE);
         
         String menuItem[] = {"사각", "타원", "직선", "복사", "삭제", "저장", "불러오기"};
            for (String item : menuItem) { //menuitem 하나하나 item 돈다
               JButton btn = new JButton(item);
               btn.addActionListener(new MyActionlistner()); //btn 추가
               add(btn);
            }
      }
      
      class MyActionlistner implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) {
            selectedBtn = e.getActionCommand();

            // 도형 복사 기능
            if (selectedBtn.equals("복사")) {
               Shape shp = null;
               for (Shape item : shapeArray) {
                  if ((item instanceof Rectangle || item instanceof Circle 
                        || item instanceof Line) && item.selected) {
                     if (item instanceof Rectangle) shp = 
                           new Rectangle(item.x + 10, item.y + 10, item.width, item.height);
                     else if (item instanceof Circle) shp =
                           new Circle(item.x + 10, item.y + 10, item.width, item.height);
                     else if (item instanceof Line) shp =
                           new Line(item.x + 10, item.y, item.width+10, item.height);
                     break;
                  }
               }
               shapeArray.add(shp);
            }
            // 도형 삭제 기능
            else if (selectedBtn.equals("삭제")) {
               for (int i = 0; i < shapeArray.size(); i++) {
                  if ((shapeArray.get(i) instanceof Rectangle || 
                      shapeArray.get(i) instanceof Circle || 
                      shapeArray.get(i) instanceof Line) && shapeArray.get(i).selected) {
                     shapeArray.remove(i); //Vector의 remove() 활용
                     break;
                  }
               }
            }
            // 도형 저장 기능
            else if (selectedBtn.equals("저장")) {
               try {
                  saveObjectToFile("out.dat");  //저장할 파일을 'out.dat'로 설정
               } catch (IOException ex) {
                  System.out.println("IOException");
               }
            }
            // 도형 불러오기 기능
            else if (selectedBtn.equals("불러오기")) {
               try {
                  shapeArray = loadObjectFromFile("out.dat");
               } catch (IOException ex) {
                  System.out.println("IOException");
               } catch (ClassNotFoundException ex) {
                  System.out.println("ClassNotFoundException");
               }
            }

            // 모든 도형 선택 해제
            unselectAll();
            Pa.repaint(); //PaintCompenent한테 다시 그리기 요청
         } //actionPerformed(ActionEvent e) 끝부분
      }
      //저장, 불러오기 기능 구현하는 입출력스트림 
      public void saveObjectToFile(String fileName) throws IOException {
         // ObjectOutputStream을 이용하여 파일에 클래스 저장
         ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));

         for (Shape item : shapeArray) {
            out.writeObject(item); //파일에 Vector객체 저장
         }
         out.close(); //더이상 출력 스트림을 사용하지 않기 때문에 해제
      }
      public Vector<Shape> loadObjectFromFile(String fileName) throws IOException, ClassNotFoundException {
         // ObjectInputStream을 이용해 파일에서 클래스를 읽고 res에 저장
         Vector<Shape> res = new Vector<>();
         ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
         //파일에 저장된 객체를 불러오기 위해 입력스트림 생성
         Shape s;
         try {
            while ((s = (Shape) in.readObject()) != null) {  //파일에 저장된 도형 객체를 불러옴, 있으면 (!null)
               res.add(s); // 받아온 shape 객체 s 를 vector res 객체에 추가
            }
         } catch (IOException e) {
            in.close();
            return res;
         }
         in.close();
         return res;
      }
   }  //PanelB 끝부분
   
   //Panel C
   private class PanelC extends JPanel {
      public PanelC() {
         setLayout(new FlowLayout());
         setBackground(Color.LIGHT_GRAY);
         
         add(new PanelB());
      }
   } //PanelC 끝부분
   
   //main
   public static void main(String[] args) {
      new FigureEditor();
   }
} //FigureEditor클래스 끝 부분