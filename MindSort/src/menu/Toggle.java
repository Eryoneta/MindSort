package menu;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import element.tree.Cor;
@SuppressWarnings("serial")
public class Toggle extends JMenuItem{
//COR
	private Color corPadrao=new Color(255,255,255);
//PRESSIONADO
	private boolean pressed=false;
		public void setToggle(boolean pressed){
			this.pressed=pressed;
			setBackground(pressed?Cor.getChanged(corPadrao,1.2f):corPadrao);
			if(acao!=null)acao.run();
		}
		public boolean isPressed(){return pressed;}
//AÇÃO
	private Runnable acao;
		public void setAction(Runnable acao){
			this.acao=acao;
			final AbstractAction run=new AbstractAction(){
				public void actionPerformed(ActionEvent a){
					setToggle(!pressed);
				}
			};
			getActionMap().put("acao",run);
			addActionListener(run);
		}
//MAIN
	public Toggle(JMenuBar menu,String nome){
		super(nome);
		if(menu==null)return;
		setBackground(corPadrao=menu.getBackground());
		setForeground(menu.getForeground());
	}
//FUNCS
	public void setAtalho(int mask,int key,boolean showMask,boolean showKey){
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key,mask),"acao");
		if(!showMask)mask=0;
		if(!showKey)key=0;
		if(showMask||showKey)setAccelerator(KeyStroke.getKeyStroke(key,mask));
	}
}