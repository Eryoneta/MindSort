package main.janela;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import element.tree.Cursor;
@SuppressWarnings("serial")
public class Botao{
//STATES
	private enum State{
		INACTIVE,
		HOVERED,
		ACTIVE,
		DRAGGED;
//		public boolean is(Botao.State...states){
//			for(Botao.State state:states)if(this.equals(state))return true;
//			return false;
//		}
	}
	private State state=State.INACTIVE;
		private void setState(State state){
			this.state=state;
			draw();
		}
		private State getState(){return state;}
//BORDA
	private Borda borda;
		public Borda getBorda(){return borda;}
//CORES
	public class Cor{
		private Color background=Color.ORANGE;
			public Color getBackground(){return background;}
			public void setBackground(Color cor){this.background=cor;}
		private Color unFocusedBackground=Color.ORANGE;
			public Color getBackgroundOnUnfocused(){return unFocusedBackground;}
			public void setBackgroundOnUnfocused(Color cor){this.unFocusedBackground=cor;}
		private Color hoverBackground=Color.RED;
			public Color getBackgroundOnHover(){return hoverBackground;}
			public void setBackgroundOnHover(Color cor){this.hoverBackground=cor;}
		private Color activeBackground=Color.GREEN;
			public Color getBackgroundOnActive(){return activeBackground;}
			public void setBackgroundOnActive(Color cor){this.activeBackground=cor;}
		private Color foreground=Color.WHITE;
			public Color getForeground(){return foreground;}
			public void setForeground(Color cor){this.foreground=cor;}
		private Color unFocusedforeground=Color.GRAY;
			public Color getForegroundOnUnfocused(){return unFocusedforeground;}
			public void setForegroundOnUnfocused(Color cor){this.unFocusedforeground=cor;}
	}
	private Cor cor=new Cor();
		public Cor getCores(){return cor;}
//LEGENDA
	private JPopupMenu popup=null;
		public void setLegenda(String legenda){
			if(legenda.equals("")){
				popup=null;
				return;
			}
			popup=new JPopupMenu(){{
				try{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}catch(ClassNotFoundException|InstantiationException|IllegalAccessException|UnsupportedLookAndFeelException erro){}
				add(new JLabel(legenda));
			}};
		}
//ACTION
	private Runnable acao;
		public void setAction(Runnable acao){this.acao=acao;}
		public void click(){
			if(acao!=null)acao.run();
		}
//LOCAL
	protected int x=0;
		public int getX(){return x;}
		public void setX(int x){this.x=x;}
	protected int y=0;
		public int getY(){return y;}
		public void setY(int y){this.y=y;}
	public Point getLocation(){return new Point(getX(),getY());}
	public void setLocation(int x,int y){setX(x);setY(y);}
//FORM
	protected int width=0;
		public int getWidth(){return width;}
		public void setWidth(int width){this.width=width;update=true;}
	protected int height=0;
		public int getHeight(){return height;}
		public void setHeight(int height){this.height=height;update=true;}
	public Dimension getSize(){return new Dimension(getWidth(),getHeight());}
	public void setSize(int width,int height){setWidth(width);setHeight(height);}
	public Rectangle getBounds(){return new Rectangle(getX(),getY(),getWidth(),getHeight());}
	public Rectangle getRelativeBounds(){return new Rectangle(0,0,getWidth(),getHeight());}
	public void setBounds(int x,int y,int width,int height){setX(x);setY(y);setWidth(width);setHeight(height);}
	public void setBounds(Rectangle r){setBounds(r.x,r.y,r.width,r.height);}
//IMAGEM
	private Image print=new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		private Image getPrint(){return print;}
	private Image buffer=new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	private boolean update=false;
		private void update(){
			print.flush();
			buffer=new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
			print=new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_ARGB);
			update=false;
		}
//VISIBILIDADE
	private boolean visible=false;
		public boolean isVisible(){return visible;}
		public void setVisible(boolean visible){this.visible=visible;}
//TITULO
	private String titulo="";
		public String getTitulo(){return titulo;}
		public void setTitulo(String titulo){this.titulo=titulo;}
//MAIN
	public Botao(Borda borda){
		super();
		this.borda=borda;
		borda.add(this);
		final Window janela=borda.getJanela();
		janela.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent m){
				if(Cursor.match(m,Cursor.LEFT))switch(getState()){
					case INACTIVE:default:break;
					case HOVERED:
						setState(State.ACTIVE);
					break;
					case ACTIVE:break;
					case DRAGGED:break;
				}
			}
			public void mouseReleased(MouseEvent m){
				if(Cursor.match(m,Cursor.LEFT))switch(getState()){
					case INACTIVE:default:break;
					case HOVERED:break;
					case ACTIVE:
						click();
						setState(State.INACTIVE);
					break;
					case DRAGGED:
						setState(State.INACTIVE);
					break;
				}
			}
		});
		janela.addMouseMotionListener(new MouseAdapter(){
			public void mouseMoved(MouseEvent m){
				switch(getState()){
					case INACTIVE:default:
						if(getBounds().contains(m.getPoint())){
							setState(State.HOVERED);
							showPopup(true);
						}
					break;
					case HOVERED:
						if(!getBounds().contains(m.getPoint())){
							setState(State.INACTIVE);
							showPopup(false);
						}
					break;
					case ACTIVE:break;
					case DRAGGED:break;
				}
			}
			public void mouseDragged(MouseEvent m){
				if(Cursor.match(m,Cursor.LEFT))switch(getState()){
					case INACTIVE:default:break;
					case HOVERED:break;
					case ACTIVE:
						if(!getBounds().contains(m.getPoint())){
							setState(State.DRAGGED);
							showPopup(false);
						}
					break;
					case DRAGGED:
						if(getBounds().contains(m.getPoint())){
							setState(State.ACTIVE);
							showPopup(true);
						}
					break;
				}
			}
		});
	}
//FUNCS
	private void showPopup(boolean show){
		if(popup==null)return;
		if(show){
			final Point mouseScreen=MouseInfo.getPointerInfo().getLocation();
			popup.show(borda.getJanela(),mouseScreen.x-borda.getJanela().getX(),mouseScreen.y-borda.getJanela().getY()+getHeight());
		}else popup.setVisible(false);
	}
//DRAW
	public synchronized void draw(){
		if(!isVisible())return;
		if(update)update();		//RECRIA TELA
		draw((Graphics2D)buffer.getGraphics());											//DESENHA COMPONENTES EM BUFFER
		getPrint().getGraphics().drawImage(buffer,0,0,getWidth(),getHeight(),null);		//DESENHA TUDO EM IMAGEM
		getBorda().getJanela().repaint();
	}
	public void draw(Graphics imagemEdit){
		if(!isVisible())return;
		switch(state){
			case INACTIVE:default:		imagemEdit.setColor(getCores().getBackground());			break;
			case HOVERED:case DRAGGED:	imagemEdit.setColor(getCores().getBackgroundOnHover());		break;
			case ACTIVE:				imagemEdit.setColor(getCores().getBackgroundOnActive());	break;
		}
		if(!getBorda().getJanela().isFocused())imagemEdit.setColor(getCores().getBackgroundOnUnfocused());
		imagemEdit.fillRect(getX(),getY(),getWidth(),getHeight());
		if(getBorda().getJanela().isFocused()){
			imagemEdit.setColor(getCores().getForeground());
		}else imagemEdit.setColor(getCores().getForegroundOnUnfocused());
		final Font fonte=imagemEdit.getFont();
		imagemEdit.setFont(new Font("Segoe UI Emoji",Font.PLAIN,14));
		final int width=imagemEdit.getFontMetrics().stringWidth(getTitulo());
		final int height=imagemEdit.getFontMetrics().getHeight();
		final int heightOffset=-3;
		imagemEdit.drawString(getTitulo(),getX()+((getWidth()-width)/2),getY()+((getHeight()+height)/2)+heightOffset);
		imagemEdit.setFont(fonte);
	}
}