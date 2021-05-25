package main.search;
import element.tree.objeto.Objeto;
public class Match{
//OBJETO
	private Objeto obj;
		public Objeto getObjeto(){return obj;}
//TEXTO OU TÍTULO
	private boolean isOnText=false;
		public boolean isOnText(){return isOnText;}
//INDEX
	private int selecIni=-1;
		public int getSelectionStart(){return selecIni;}
	private int selecFim=-1;
		public int getSelectionEnd(){return selecFim;}
//MAIN
	public Match(Objeto obj,boolean isOnText,int selecIni,int selecFim){
		this.obj=obj;
		this.isOnText=isOnText;
		this.selecIni=selecIni;
		this.selecFim=selecFim;
	}
}