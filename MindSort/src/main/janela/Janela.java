package main.janela;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
@SuppressWarnings("serial")
public class Janela{
//VAR GLOBAIS
	private JFrame janelaPai;
	private JFrame janela;
		public JFrame getSolidInstance(){return janela;}
	private JanelaVidro janelaVidro;
		public JanelaVidro getTransparentInstance(){return janelaVidro;}
//PAINEL
	private JRootPane painel=new JRootPane();
		public JRootPane getPainel(){return painel;}
//DEFAULT FORM
	private int dialogDefaultWidth;
		public int getDialogDefaultWidth(){return dialogDefaultWidth;}
		public void setDialogDefaultWidth(int width){
			dialogDefaultWidth=width;
			janelaVidro.setSize(width,janelaVidro.getHeight());
		}
	private int dialogDefaultHeight;
		public int getDialogDefaultHeight(){return dialogDefaultHeight;}
		public void setDialogDefaultHeight(int height){
			dialogDefaultHeight=height;
			janelaVidro.setSize(janelaVidro.getWidth(),height);
		}
//LOCK
	public void setLocked(boolean locked){
		if(locked){
			janela.remove(getPainel());
			janelaVidro.add(getPainel());
			getPainel().setBounds(janelaVidro.getBorda().getInnerBounds());
		}else{
			janelaVidro.remove(getPainel());
			janela.add(getPainel());
		}
		janela.setVisible(!locked);
		janelaVidro.setVisible(locked);
	}
	public boolean isLocked(){return (!janela.isVisible()&&janelaVidro.isVisible());}
//MAIN
	public Janela(JFrame janelaPai){
		this.janelaPai=janelaPai;
		janela=new JFrame(){{
			setFont(janelaPai.getFont());
		}};
		janelaVidro=new JanelaVidro(janelaPai){{
			setFont(janelaPai.getFont());
			setLayout(null);
		}
		@Override
			public void setLocation(int x,int y){
				final Point local=getLimitedLocation(x,y);
				super.setLocation(local.x,local.y);
			}
		@Override
			public void setSize(int width,int height){
				final Dimension size=getLimitedSize(width,height);
				super.setSize(size.width,size.height);
			}
		@Override
			public void setBounds(int x,int y,int width,int height){
				final Rectangle bounds=getLimitedBounds(x,y,width,height);
				super.setBounds(bounds.x,bounds.y,bounds.width,bounds.height);
			}
		};
		janelaPai.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent r){
				if(isLocked())limitBounds();
			}
			public void componentMoved(ComponentEvent m){
				if(isLocked())limitBounds();
			}
		});
		getPainel().setBounds(janelaVidro.getBorda().getInnerBounds());
		janelaVidro.addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent r){
				getPainel().setBounds(janelaVidro.getBorda().getInnerBounds());
			}
		});
	}
//FUNCS
//ELEMENTOS
	public void add(Component elemento){
		getPainel().getContentPane().add(elemento);
	}
	public void remove(Component elemento){
		getPainel().getContentPane().remove(elemento);
	}
//LOCAL
	public int getX(){return janela.getX();}
	public int getY(){return janela.getY();}
	public void setLocation(int x,int y){
		janela.setLocation(x,y);
		janelaVidro.setLocation(x,y);
	}
//FORM
	public int getWidth(){return janela.getWidth();}
	public int getHeight(){return janela.getHeight();}
	public void setSize(int width,int height){
		janela.setSize(width,height);
		janelaVidro.setSize(width,height);
	}
	public Rectangle getBounds(){return (isLocked()?janelaVidro.getBounds():janela.getBounds());}
	public void setBounds(int x,int y,int width,int height){
		janela.setBounds(x,y,width,height);
		janelaVidro.setBounds(x,y,width,height);
	}
	public void setBounds(Rectangle area){
		janela.setBounds(area);
		janelaVidro.setBounds(area);
	}
	private boolean horizontal=false;
	private Point getLimitedLocation(int x,int y){return getLimitedBounds(new Rectangle(x,y,0,0)).getLocation();}
	private Dimension getLimitedSize(int width,int height){return getLimitedBounds(new Rectangle(0,0,width,height)).getSize();}
	private Rectangle getLimitedBounds(int x,int y,int width,int height){return getLimitedBounds(new Rectangle(x,y,width,height));}
	private Rectangle getLimitedBounds(Rectangle newBounds){
		if(janelaPai.getWidth()>janelaPai.getHeight()){//HORIZONTAL
			final int widthMax=janelaPai.getWidth()-(janelaPai.getWidth()/5);
			final int width=(horizontal==false?dialogDefaultWidth:Math.min(widthMax,newBounds.width));
			newBounds.setSize(width,janelaPai.getHeight()-janelaPai.getInsets().top);
			dialogDefaultWidth=width;
			horizontal=true;
			newBounds.setLocation(janelaPai.getX()+janelaPai.getWidth()-janelaVidro.getWidth(),janelaPai.getY()+janelaPai.getInsets().top);
		}else{//VERTICAL
			final int heightMax=janelaPai.getHeight()-(janelaPai.getHeight()/5);
			final int height=(horizontal==true?dialogDefaultHeight:Math.min(heightMax,newBounds.height));
			newBounds.setSize(janelaPai.getWidth(),height);
			dialogDefaultWidth=height;
			horizontal=false;
			newBounds.setLocation(janelaPai.getX(),janelaPai.getY()+janelaPai.getHeight()-janelaVidro.getHeight());
		}
		return newBounds;
	}
	private Dimension windowSize=new Dimension();
	private void limitBounds(){
		janelaVidro.setBounds(janelaVidro.getBounds());
		getPainel().setBounds(janelaVidro.getBorda().getInnerBounds());
		if(!windowSize.equals(janelaVidro.getSize())){	//ATUALIZA APENAS SE MUDADO O TAMANHO(EVITA DA SOMBRA ESCURECER)
			janelaVidro.repaint();
			windowSize.setSize(janelaVidro.getSize());
		}
	}
//GERAIS
	public void setTitle(String titulo){
		janela.setTitle(titulo);
		janelaVidro.setTitle(titulo);
		janelaVidro.repaint();
	}
	public void setMinimumSize(Dimension size){
		janela.setMinimumSize(size);
		janelaVidro.setMinimumSize(size);
		janelaVidro.repaint();
	}
	public void setBackground(Color cor){
		janela.setBackground(cor);
		janelaVidro.setBackground(cor);
		janelaVidro.repaint();
	}
	public void setDefaultCloseOperation(int operation){
		janela.setDefaultCloseOperation(operation);
		janelaVidro.setDefaultCloseOperation(operation);
	}
	public void setAlwaysOnTop(boolean topo){
		janelaVidro.setAlwaysOnTop(topo);
	}
	public void setIconImage(Image imagem){
		janela.setIconImage(imagem);
		janelaVidro.setIconImage(imagem);
		janelaVidro.repaint();
	}
	public void requestFocus(){
		janela.requestFocus();
		janelaVidro.requestFocus();
	}
	public boolean isFocused(){return (janela.isFocused()||janelaVidro.isFocused());}
	public void addKeyListener(KeyAdapter k){
		janela.addKeyListener(k);
		janelaVidro.addKeyListener(k);
	}
	public void setMenu(JMenuBar menu){
		getPainel().setJMenuBar(menu);
	}
	public boolean isDragging(){
		return janelaVidro.getBorda().isDragging();
	}
}