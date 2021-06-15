package main.janela;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;

import main.MindSort;
@SuppressWarnings("serial")
public class JanelaVidro extends JDialog{
//BORDA
	private Borda borda=new Borda(this);
		public Borda getBorda(){return borda;}
//BOTÃO X
	private Botao X=new Botao(borda){{
		setAction(new Runnable(){
			public void run(){
				dispose();
			}
		});
		final int size=Borda.TOP_WIDTH-Borda.SHADOW;
		setSize(size,size);
		setLegenda(MindSort.getLang().get("M_Tx_F","Close"));
		setTitulo("✕");
		setVisible(true);
	}};
//BOTÃO SIZE MÍNIMO
	private Botao SMin=new Botao(borda){{
		final int size=Borda.TOP_WIDTH-Borda.SHADOW;
		setSize(size*2,size);
		setLegenda(MindSort.getLang().get("M_Tx_Mi","Minimal"));
		setTitulo("□");
		getCores().setBackgroundOnHover(new Color(100,100,100));
		getCores().setBackgroundOnActive(new Color(118,118,117));
		setVisible(true);
	}};
//BOTÃO SIZE MÉDIO
	private Botao SMed=new Botao(borda){{
		final int size=Borda.TOP_WIDTH-Borda.SHADOW;
		setSize(size*2,size);
		setLegenda(MindSort.getLang().get("M_Tx_Me","Medium"));
		setTitulo("◧");
		getCores().setBackgroundOnHover(new Color(100,100,100));
		getCores().setBackgroundOnActive(new Color(118,118,117));
		setVisible(true);
	}};
//BOTÃO SIZE MÁXIMO
	private Botao SMax=new Botao(borda){{
		final int size=Borda.TOP_WIDTH-Borda.SHADOW;
		setSize(size*2,size);
		setLegenda(MindSort.getLang().get("M_Tx_Ma","Maximus"));
		setTitulo("■");
		getCores().setBackgroundOnHover(new Color(100,100,100));
		getCores().setBackgroundOnActive(new Color(118,118,117));
		setVisible(true);
	}};
//TRANSPARÊNCIA
	private int transparencia=60;
		public int getTransparencia(){return transparencia;}
		public void setTransparencia(int transparencia){
			this.transparencia=transparencia;
			setOpacity(((float)transparencia)/100);
		}
//MAIN
	public JanelaVidro(JFrame janela){
		super(janela);
		addWindowListener(new WindowListener(){
			public void windowOpened(WindowEvent w){}
			public void windowIconified(WindowEvent w){}
			public void windowDeiconified(WindowEvent w){}
			public void windowClosing(WindowEvent w){}
			public void windowClosed(WindowEvent w){}
			public void windowActivated(WindowEvent w){
				setOpacity(1f);
				repaint();
			}
			public void windowDeactivated(WindowEvent w){
				setOpacity(((float)getTransparencia())/100);
				repaint();
			}
		});
		final JanelaVidro janelaVidro=this;
		SMin.setAction(new Runnable(){
			public void run(){
				if(janela.getWidth()>janela.getHeight()){//HORIZONTAL
					janelaVidro.setSize(0,janelaVidro.getHeight());		//LIMITADO PELO MIN_SIZE
				}else{//VERTICAL
					janelaVidro.setSize(janelaVidro.getWidth(),0);		//LIMITADO PELO MIN_SIZE
				}
				janelaVidro.setBounds(getLimitedBounds(janelaVidro.getBounds()));
			}
		});
		SMed.setAction(new Runnable(){
			public void run(){
				if(janela.getWidth()>janela.getHeight()){//HORIZONTAL
					final int widthMed=(janela.getWidth()/2)+janelaVidro.getBorda().getInnerX();	//DESCONSIDERA SOMBRA WIDTH
					janelaVidro.setSize(widthMed,janelaVidro.getHeight());		//LIMITADO PELO MIN_SIZE
				}else{//VERTICAL
					final int heightMed=janela.getHeight()/2;
					janelaVidro.setSize(janelaVidro.getWidth(),heightMed);		//LIMITADO PELO MIN_SIZE
				}
				janelaVidro.setBounds(getLimitedBounds(janelaVidro.getBounds()));
			}
		});
		SMax.setAction(new Runnable(){
			public void run(){
				if(janela.getWidth()>janela.getHeight()){//HORIZONTAL
					final int widthMax=janela.getWidth()-(janela.getWidth()/5);
					janelaVidro.setSize(widthMax,janelaVidro.getHeight());		//LIMITADO PELO MIN_SIZE
				}else{//VERTICAL
					final int heightMax=janela.getHeight()-(janela.getHeight()/5);
					janelaVidro.setSize(janelaVidro.getWidth(),heightMax);		//LIMITADO PELO MIN_SIZE
				}
				janelaVidro.setBounds(getLimitedBounds(janelaVidro.getBounds()));
			}
		});
	}
//FUNCS
@Override
	public void setLocation(int x,int y){
		super.setLocation(x,y);
		setButtonLocations();
	}
@Override
	public void setSize(int width,int height){
		super.setSize(width,height);
		setButtonLocations();
	}
@Override
	public void setBounds(int x,int y,int width,int height){
		super.setBounds(x,y,width,height);
		setButtonLocations();
	}
	private void setButtonLocations(){
		final int borda=4;
		int x=getBorda().getInnerX()+getBorda().getInnerWidth()-borda;
		x-=X.getWidth();
		X.setLocation(x,borda);
		x-=SMin.getWidth();
		SMin.setLocation(x,borda);
		x-=SMed.getWidth();
		SMed.setLocation(x,borda);
		x-=SMax.getWidth();
		SMax.setLocation(x,borda);
	}
	private Rectangle getLimitedBounds(Rectangle newBounds){
		if(getOwner().getWidth()>getOwner().getHeight()){//HORIZONTAL
			newBounds.setSize(newBounds.width,getOwner().getHeight()-getOwner().getInsets().top);
			newBounds.setLocation(getOwner().getX()+getOwner().getWidth()-getOwner().getWidth(),getOwner().getY()+getOwner().getInsets().top);
		}else{//VERTICAL
			newBounds.setSize(getOwner().getWidth(),newBounds.height);
			newBounds.setLocation(getOwner().getX(),getOwner().getY()+getOwner().getHeight()-getOwner().getHeight());
		}
		return newBounds;
	}
	public void updateLang(){
		X.setLegenda(MindSort.getLang().get("M_Tx_F","Close"));
		SMin.setLegenda(MindSort.getLang().get("M_Tx_Mi","Minimal"));
		SMed.setLegenda(MindSort.getLang().get("M_Tx_Me","Medium"));
		SMax.setLegenda(MindSort.getLang().get("M_Tx_Ma","Maximus"));
	}
//DRAW
@Override
	public void paint(Graphics imagemEdit){
		final Graphics2D imagemEdit2D=(Graphics2D)imagemEdit;
	//BORDA
		getBorda().draw(imagemEdit2D);
	//FUNDO
		imagemEdit2D.setColor(Color.WHITE);
		final Rectangle innerArea=getBorda().getInnerBounds();
		imagemEdit2D.fillRect(innerArea.x,innerArea.y,innerArea.width,innerArea.height);
	//COMPONENTES
		if(!getBorda().isDragging()){
			super.paintComponents(imagemEdit2D);
		}
	//END
		imagemEdit2D.dispose();
	}
}