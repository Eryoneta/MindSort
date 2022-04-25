package main.search;
import java.awt.Toolkit;
import main.MindSortUI;
import element.tree.objeto.Objeto;
import element.tree.main.Tree;
public class Searcher{
//VAR GLOBAIS
	protected Tree tree;
	protected MatchMade matches;
//UI
	private final SearcherUI UI=new SearcherUI(this);
		public SearcherUI getUI(){return UI;}
//MAIN
	public Searcher(Tree tree){
		this.tree=tree;
		matches=new MatchMade(tree);
		getUI().updateInterface();
	}
//FUNCS
	public void chamar(){getUI().getWindow().setVisible(true);}
	public void dispensar(){
		reset();
		getUI().getWindow().dispose();
	}
	public void reset(){
		matches.clearMatches();
		getUI().resultado.setText("");
		getUI().procurar.setText(getUI().procurarTxt);
		getUI().procurar.setEnabled(!getUI().termo.getText().isEmpty());
		getUI().destacar.setEnabled(!getUI().termo.getText().isEmpty());
	}
//PROCURAR
	protected void procurar(String termo,boolean isRegex,boolean wholeWord,boolean diffMaiuscMinusc,boolean onlySelected,boolean frente){
		reset();
		if(termo.isEmpty()){
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		matches.search(termo,isRegex,wholeWord,diffMaiuscMinusc,onlySelected);
		tree.getActions().unSelectAll();
		for(Objeto obj:matches.getMatchedObjs())tree.select(obj);
		final int matchesQtd=matches.totalMatches();
		if(matchesQtd==0){
			getUI().resultado.setText(MindSortUI.getLang().get("M_Menu_P_P_NE","No encounter!"));
			Toolkit.getDefaultToolkit().beep();
		}else if(matchesQtd==1){
			getUI().resultado.setText(matchesQtd+MindSortUI.getLang().get("M_Menu_P_P_I"," instance found!"));
			listar(frente);		//HAVENDO APENAS 1 = IMEDIATAMENTE O SELECIONA
		}else{
			getUI().resultado.setText(matchesQtd+MindSortUI.getLang().get("M_Menu_P_P_Is"," instances found!"));
		}
		getUI().procurar.setText(getUI().listarTxt);	//PROCURAR -> LISTAR
		tree.getPainel().getJanela().requestFocus();
		tree.draw();
	}
	protected void reProcurar(Objeto obj){
		if(matches.isEmptyOfMatches())return;
		matches.researchObjText(obj);
		matches.nextMatch();
	}
	protected void listar(boolean frente){
		getUI().procurar.setText(frente?getUI().proximoTxt:getUI().anteriorTxt);	//PROCURAR -> PROX/ANTE
		final Match match=(frente?matches.nextMatch():matches.prevMatch());
		tree.getActions().unSelectAll();
		if(match==null)return;
		if(!tree.getObjetos().contains(match.getObjeto()))return;	//CASO OBJ TENHA SIDO DEL
		if(match.isOnText()){	//DESTACA TEXTO
			tree.select(match.getObjeto());
			tree.draw();
			tree.getUI().getTexto().requestFocus();
			tree.getUI().getTexto().select(match.getSelectionStart(),match.getSelectionEnd());
		}else{					//DESTACA TÍTULO
			tree.select(match.getObjeto());
			tree.getPainel().getJanela().requestFocus();	//FOCA A JANELA
			tree.getActions().editTitulo();
			tree.draw();
			tree.getUI().getTitulo().requestFocus();		//FOCA O TÍTULO
			tree.getUI().getTitulo().select(match.getSelectionStart(),match.getSelectionEnd());
		}
	}
//DESTACAR
	protected void destacar(String termo,boolean isRegex,boolean wholeWord,boolean diffMaiuscMinusc,boolean onlySelected){
		reset();
		if(termo.isEmpty()){
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		matches.search(termo,isRegex,wholeWord,diffMaiuscMinusc,onlySelected);
		tree.getActions().unSelectAll();
		for(Objeto objs:matches.getMatchedObjs())tree.select(objs);
		final int matchesQtd=matches.totalMatchedObjs();
		if(matchesQtd==0){
			getUI().resultado.setText(MindSortUI.getLang().get("M_Menu_P_D_NR","No results!"));
			Toolkit.getDefaultToolkit().beep();
		}else if(matchesQtd==1){
			getUI().resultado.setText(matchesQtd+MindSortUI.getLang().get("M_Menu_P_D_O"," selected object!"));
		}else{
			getUI().resultado.setText(matchesQtd+MindSortUI.getLang().get("M_Menu_P_D_Os"," selected objects!"));
		}
		getUI().procurar.setText(getUI().listarTxt);	//PROCURAR -> LISTAR
		tree.getPainel().getJanela().requestFocus();
		tree.draw();
	}
}