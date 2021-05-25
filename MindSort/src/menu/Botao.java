package menu;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
@SuppressWarnings("serial")
public class Botao extends JMenuItem{
//AÇÃO
	public void setAction(AbstractAction acao){
		getActionMap().put("acao",acao);
		addActionListener(acao);
	}
//MAIN
	public Botao(JMenuBar menu,String nome){
		super(nome);
		if(menu==null)return;
		setBackground(menu.getBackground());
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