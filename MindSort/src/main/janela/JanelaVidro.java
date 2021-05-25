package main.janela;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
@SuppressWarnings("serial")
public class JanelaVidro extends JDialog{
//BORDA
	private Borda borda=new Borda(this);
		public Borda getBorda(){return borda;}
//BOTÃO X
	private Botao X=new Botao(borda){{
		setAction(new Runnable(){
			public void run(){
				//NADA
			}
		});
		final int size=Borda.TOP_WIDTH-Borda.WIDTH;
		setSize(size,size);
		setLegenda("Fechar");
		setTitulo("✕");
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
	}
//FUNCS
@Override
	public void setLocation(int x,int y){
		super.setLocation(x,y);
		X.setLocation(getBorda().getInnerX()+getBorda().getInnerWidth()-X.getWidth()-4,4);
	}
@Override
	public void setSize(int width,int height){
		super.setSize(width,height);
		X.setLocation(getBorda().getInnerX()+getBorda().getInnerWidth()-X.getWidth()-4,4);
	}
@Override
	public void setBounds(int x,int y,int width,int height){
		super.setBounds(x,y,width,height);
		X.setLocation(getBorda().getInnerX()+getBorda().getInnerWidth()-X.getWidth()-4,4);
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