package main.janela;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
public class Borda{
//STATE
	private enum Side{
		NONE,
		ALL,
		TOP,
		TOP_RIGHT,
		RIGHT,
		BOTTOM_RIGHT,
		BOTTOM,
		BOTTOM_LEFT,
		LEFT,
		TOP_LEFT;
		private boolean is(Borda.Side... sides){
			for(Borda.Side side:sides)if(this.equals(side))return true;
			return false;
		}
	}
	private Side side=Side.NONE;
		private Side getSide(Point ponto){
			if(getInnerBounds().contains(ponto))return Side.NONE;
			if(getTopBorder().contains(ponto)){
				boolean isInsideButton=false;
				for(Botao botao:botoes)if(botao.getBounds().contains(ponto)){
					isInsideButton=true;
					break;
				}
				if(!isInsideButton){
					if(getRightBorder().contains(ponto))return Side.TOP_RIGHT;
					if(getLeftBorder().contains(ponto))return Side.TOP_LEFT;
					return Side.TOP;
				}
			}
			if(getRightBorder().contains(ponto)){
				if(getBottomBorder().contains(ponto))return Side.BOTTOM_RIGHT;
				return Side.RIGHT;
			}
			if(getBottomBorder().contains(ponto)){
				if(getLeftBorder().contains(ponto))return Side.BOTTOM_LEFT;
				return Side.BOTTOM;
			}
			if(getLeftBorder().contains(ponto)){
				return Side.LEFT;
			}
			if(getTitleBar().contains(ponto)){
				boolean isInsideButton=false;
				for(Botao botao:botoes)if(botao.getBounds().contains(ponto)){
					isInsideButton=true;
					break;
				}
				if(!isInsideButton)return Side.ALL;	
			}
			return Side.NONE;
		}
//JANELA(PAI)
	private Window janela;
		public Window getJanela(){return janela;}
//BORDAS
	public static int TOP_WIDTH=30;
	public static int WIDTH=1;
	public static int SHADOW=8;
		public int getInnerX(){return SHADOW+WIDTH;}
		public int getInnerY(){return TOP_WIDTH;}
		public int getInnerWidth(){return janela.getWidth()-SHADOW-WIDTH-WIDTH-SHADOW;}
		public int getInnerHeight(){return janela.getHeight()-TOP_WIDTH-SHADOW-WIDTH;}
	public Rectangle getInnerBounds(){return new Rectangle(getInnerX(),getInnerY(),getInnerWidth(),getInnerHeight());}
//ÁREAS DAS BORDAS
	private Rectangle getTitleBar(){		return new Rectangle(getInnerX(),0,getInnerWidth(),getInnerY());}
	private Rectangle getTopBorder(){		return new Rectangle(0,0,janela.getWidth(),Borda.SHADOW);}
	private Rectangle getBottomBorder(){	return new Rectangle(0,janela.getHeight()-Borda.SHADOW,janela.getWidth(),Borda.SHADOW);}
	private Rectangle getLeftBorder(){		return new Rectangle(0,0,Borda.SHADOW,janela.getHeight());}
	private Rectangle getRightBorder(){		return new Rectangle(janela.getWidth()-Borda.SHADOW,0,Borda.SHADOW,janela.getHeight());}
//VAR GLOBAIS
	private static Color WINDOW_COLOR=Color.DARK_GRAY;
		public static Color getBordaCor(){return WINDOW_COLOR;}
		public static void setBordaCor(Color bordaCor){WINDOW_COLOR=bordaCor;}
	private static Color WINDOW_DISABLED_COLOR=Color.WHITE;
		public static Color getBordaDisabledCor(){return WINDOW_DISABLED_COLOR;}
		public static void setBordaDisabledCor(Color bordaCor){WINDOW_DISABLED_COLOR=bordaCor;}
	private Rectangle windowSize;
	private Point mousePressed=new Point(0,0);
	private boolean isDragging=false;
		public boolean isDragging(){return isDragging;}
//BOTÕES
	private List<Botao>botoes=new ArrayList<>();
		public void add(Botao button){
			botoes.add(button);
			button.getCores().setBackground(WINDOW_COLOR);
			button.getCores().setBackgroundOnUnfocused(WINDOW_DISABLED_COLOR);
			button.getCores().setBackgroundOnHover(new Color(232,17,35));
			button.getCores().setBackgroundOnActive(new Color(172,43,53));
			button.getCores().setForeground(Color.WHITE);
			button.getCores().setForegroundOnUnfocused(new Color(155,155,155));
//			int buttonsWidth=TOP_WIDTH*3;		//ALTURA DA BORDA X3 
//			for(Botao botao:botoes)buttonsWidth+=botao.getWidth();
//			janela.setMinimumSize(new Dimension(WIDTH+buttonsWidth+WIDTH,TOP_WIDTH+WIDTH));
		}
//FONTE
	private Font fonte=new Font("Segoe UI",Font.PLAIN,12);
		public Font getFonte(){return fonte;}
//MAIN
	public Borda(JDialog janela){
		this.janela=janela;
		windowSize=janela.getBounds();
		janela.setUndecorated(true);
		janela.setBackground(new Color(0,0,0,1));	//QUASE INVISÍVEL, PARA NÃO PERDER O CURSOR AO DRAG
		setJanela(janela);
	}
	public Borda(JFrame janela){
		this.janela=janela;
		windowSize=janela.getBounds();
		setJanela(janela);
	}
	private void setJanela(Window janela){
		janela.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent m){
				mousePressed.setLocation(m.getXOnScreen(),m.getYOnScreen());
				windowSize=janela.getBounds();
				side=getSide(m.getPoint());	//CASO MOUSE_RELEASE E MOUSE_PRESS SEGUIDOS
			}
			public void mouseReleased(MouseEvent m){
				side=Side.NONE;
				if(isDragging()){
					getJanela().repaint();
					isDragging=false;
				}
			}
		});
		janela.addMouseMotionListener(new MouseAdapter(){
			public void mouseMoved(MouseEvent m){
				side=getSide(m.getPoint());
				setCursor();
			}
			public void mouseDragged(MouseEvent m){
				if(side.is(Side.NONE))return;
				isDragging=true;
				if(element.tree.Cursor.match(m,element.tree.Cursor.LEFT)){
					resize(m.getLocationOnScreen());
				}
			}
			private void setCursor(){
				int cursor=0;
				switch(side){
					case NONE:case ALL:default:	cursor=Cursor.DEFAULT_CURSOR;break;
					case TOP:					cursor=Cursor.N_RESIZE_CURSOR;break;
					case TOP_RIGHT:				cursor=Cursor.NE_RESIZE_CURSOR;break;
					case RIGHT:					cursor=Cursor.E_RESIZE_CURSOR;break;
					case BOTTOM_RIGHT:			cursor=Cursor.SE_RESIZE_CURSOR;break;
					case BOTTOM:				cursor=Cursor.S_RESIZE_CURSOR;break;
					case BOTTOM_LEFT:			cursor=Cursor.SW_RESIZE_CURSOR;break;
					case LEFT:					cursor=Cursor.W_RESIZE_CURSOR;break;
					case TOP_LEFT:				cursor=Cursor.NW_RESIZE_CURSOR;break;
				}
				janela.setCursor(Cursor.getPredefinedCursor(cursor));
			}
			private void resize(Point mouseAtual){
				final Dimension diff=new Dimension(mousePressed.x-mouseAtual.x,mousePressed.y-mouseAtual.y);
				final Point newLocal=new Point(windowSize.x-diff.width,windowSize.y-diff.height);
				final Dimension newAreaDimm=new Dimension(windowSize.width-diff.width,windowSize.height-diff.height);
				final Dimension newAreaGrow=new Dimension(windowSize.width+diff.width,windowSize.height+diff.height);
				final Rectangle newWindow=new Rectangle();
				switch(side){
					case ALL:default:	newWindow.setBounds(	newLocal.x,		newLocal.y,		windowSize.width,	windowSize.height	);break;
					case TOP:			newWindow.setBounds(	windowSize.x,	newLocal.y,		windowSize.width,	newAreaGrow.height	);break;
					case TOP_RIGHT:		newWindow.setBounds(	windowSize.x,	newLocal.y,		newAreaDimm.width,	newAreaGrow.height	);break;
					case RIGHT:			newWindow.setBounds(	windowSize.x,	windowSize.y,	newAreaDimm.width,	windowSize.height	);break;
					case BOTTOM_RIGHT:	newWindow.setBounds(	windowSize.x,	windowSize.y,	newAreaDimm.width,	newAreaDimm.height	);break;
					case BOTTOM:		newWindow.setBounds(	windowSize.x,	windowSize.y,	windowSize.width,	newAreaDimm.height	);break;
					case BOTTOM_LEFT:	newWindow.setBounds(	newLocal.x,		windowSize.y,	newAreaGrow.width,	newAreaDimm.height	);break;
					case LEFT:			newWindow.setBounds(	newLocal.x,		windowSize.y,	newAreaGrow.width,	windowSize.height	);break;
					case TOP_LEFT:		newWindow.setBounds(	newLocal.x,		newLocal.y,		newAreaGrow.width,	newAreaGrow.height	);break;
				}
				final Dimension minSize=janela.getMinimumSize();
				if(side.is(Side.TOP_LEFT,Side.LEFT,Side.BOTTOM_LEFT)&&newWindow.width<minSize.width){		//IMPEDE X DE CONTINUAR
					newWindow.x-=minSize.width-newWindow.width;
					newWindow.width=minSize.width;
				}
				if(side.is(Side.TOP_LEFT,Side.TOP,Side.TOP_RIGHT)&&newWindow.height<minSize.height){	//IMPEDE Y DE CONTINUAR
					newWindow.y-=minSize.height-newWindow.height;
					newWindow.height=minSize.height;
				}
				janela.setBounds(newWindow);
			}
		});
	}
//DRAW
	public void draw(Graphics imagemEdit){
		final Rectangle area=imagemEdit.getClipBounds();
		((Graphics2D)imagemEdit).setComposite(AlphaComposite.Clear);	//LIMPA A SOMBRA
		((Graphics2D)imagemEdit).fillRect(area.x,area.y,area.width,area.height);
		((Graphics2D)imagemEdit).setComposite(AlphaComposite.SrcOver);
		drawShadow(imagemEdit);
		drawBorda(imagemEdit);
	}
		private void drawShadow(Graphics imagemEdit){
			final Rectangle area=imagemEdit.getClipBounds();
			final BufferedImage imagem=new BufferedImage(area.width,area.height,BufferedImage.TYPE_INT_ARGB);
			final Graphics2D imagemEdit2D=(Graphics2D)imagem.getGraphics();
			drawShadowBlock(imagemEdit2D,	area.width-SHADOW,		SHADOW,					area.width,			area.height-SHADOW,	Side.RIGHT);
			drawShadowBlock(imagemEdit2D,	SHADOW,					SHADOW,					0,					area.height-SHADOW,	Side.LEFT);
			drawShadowBlock(imagemEdit2D,	SHADOW,					area.height-SHADOW,		area.width-SHADOW,	area.height,		Side.BOTTOM);
			drawShadowBlock(imagemEdit2D,	area.width-SHADOW*2,	area.height-SHADOW*2,	area.width,			area.height,		Side.BOTTOM_RIGHT);
			drawShadowBlock(imagemEdit2D,	SHADOW*2,				area.height-SHADOW*2,	0,					area.height,		Side.BOTTOM_LEFT);
			drawShadowBlock(imagemEdit2D,	SHADOW*2,				SHADOW*2,				0,					0,					Side.TOP_LEFT);
			drawShadowBlock(imagemEdit2D,	area.width-SHADOW*2,	SHADOW*2,				area.width,			0,					Side.TOP_RIGHT);
			imagemEdit.drawImage(imagem,0,0,null);
		}
			private void drawShadowBlock(Graphics2D imagemEdit2D,int x1,int y1,int x2,int y2,Side side){
				final Color sombra=new Color(0,0,0,0.2f);
				final Color transparente=new Color(0,0,0,0);
				int x=Math.min(x1,x2);
				int y=Math.min(y1,y2);
				int width=Math.max(x1,x2)-x;
				int height=Math.max(y1,y2)-y;
				switch(side){
					case RIGHT:case LEFT:	y2=y1;	break;
					case BOTTOM:			x2=x1;	break;
					case BOTTOM_RIGHT:
						width=height/=2;
						x+=width;
						y+=height;
					break;
					case BOTTOM_LEFT:
						width=height/=2;
						y+=height;
					break;
					case TOP_RIGHT:
						width=height/=2;
						x+=width;
					break;
					case TOP_LEFT:
						width=height/=2;
					break;
					default:	break;
				}
				imagemEdit2D.setPaint(new GradientPaint(x1,y1,sombra,x2,y2,transparente));
				imagemEdit2D.fillRect(x,y,width,height);
			}
		private void drawBorda(Graphics imagemEdit){
		//CONFIG TEXTO
			final BufferedImage buffer=new BufferedImage(
					WIDTH+getInnerWidth()+WIDTH,
					TOP_WIDTH+getInnerHeight()+WIDTH,BufferedImage.TYPE_INT_RGB);	//A ÚNICA FORMA DE LCD FUNCIONAR
			final Graphics2D bufferEdit=(Graphics2D)buffer.getGraphics();
			bufferEdit.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);	//ADICIONA BORDA, CONTRASTE
			bufferEdit.translate(-getInnerX(),0);	//ALINHA O QUE SEGUIR COM A JANELA
		//BORDA
			bufferEdit.setColor(janela.isFocused()?getBordaCor():getBordaDisabledCor());
			bufferEdit.fillRect(getInnerX(),0,buffer.getWidth(),buffer.getHeight());	//BORDA
		//ICONE
			final boolean isIconified=!getJanela().getIconImages().isEmpty();
			if(isIconified){
				final Image icone=getJanela().getIconImages().get(0);
				bufferEdit.drawImage(icone,SHADOW+5,SHADOW,16,16,null);		//ICONE
			}
		//TITULO
			bufferEdit.setColor(janela.isFocused()?Color.WHITE:new Color(160,143,176));
			bufferEdit.setFont(getFonte());
			String titulo=((Dialog)janela).getTitle();
			final int x=SHADOW+5+(isIconified?16+5:0);
			final int y=3+bufferEdit.getFontMetrics().getHeight();
			int buttonsWidth=0; 
			for(Botao botao:botoes)buttonsWidth+=botao.getWidth();
			final int widthLimite=getInnerWidth()-x-(buttonsWidth+SHADOW)-bufferEdit.getFontMetrics().stringWidth("...");
			boolean didCut=false;
			while(bufferEdit.getFontMetrics().stringWidth(titulo)>widthLimite){
				if(titulo.length()-2<0){
					titulo="";
					break;
				}
				titulo=titulo.substring(0,titulo.length()-2);
				didCut=true;
			}
			if(didCut)titulo+="...";
			bufferEdit.drawString(titulo,x,y);	//TITULO
		//BOTÕES
			for(Botao botao:botoes)botao.draw(bufferEdit);
			imagemEdit.drawImage(buffer,getInnerX()-WIDTH,0,null);	//PASSA UM RGB PARA O RGBA
		}
}