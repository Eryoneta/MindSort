package menu;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
@SuppressWarnings("serial")
public class Menu extends JMenu{
	public Menu(JMenuBar menu,String nome){
		super(nome);
		setBackground(menu.getBackground());
		setForeground(menu.getForeground());
	}
}