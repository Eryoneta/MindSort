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
		getUI().resultado.setText("");
		getUI().procurar.setText(getUI().procurarTxt);
		matches.clear();
		index=0;
		getUI().procurar.setEnabled(!getUI().termo.getText().isEmpty());
		getUI().destacar.setEnabled(!getUI().termo.getText().isEmpty());
	}
	protected void researchMatch(String newTexto){
		if(matches.isEmpty()||matches.get(index)==null)return;
		final Objeto objAtual=matches.get(index).getObjeto();
		matches.del(objAtual);
		matches.researchText(objAtual,false,newTexto);
	}
//PROCURAR
	private int index=0;
	protected void listar(boolean frente){
		if(!getUI().procurar.getText().equals(getUI().listarTxt)){
			index+=(frente?+1:-1);
		}else getUI().procurar.setText(frente?getUI().proximoTxt:getUI().anteriorTxt);	//PROCURAR -> PROX/ANTE
		if(index<0)index=matches.totaMatches()-1;				//RESETA PARA O FIM
		if(index>=matches.totaMatches())index=0;				//RESETA PARA O COMEÇO
		tree.getActions().unSelectAll();						//DESELECIONA TUDO
		final Match match=matches.get(index);
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
			tree.getUI().getTitulo().requestFocus();				//FOCA O TÍTULO
			tree.getUI().getTitulo().select(match.getSelectionStart(),match.getSelectionEnd());
		}
	}
	protected void procurar(String termo,boolean frente,boolean onlySelected,boolean wholeWord,boolean diffMaiuscMinusc){
		if(termo.isEmpty()){
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		matches.search(termo,frente,onlySelected,wholeWord,diffMaiuscMinusc);	//PROCURA
		tree.getActions().unSelectAll();										//DESELECIONA TUDO
		for(Match match:matches.getMatchs())tree.select(match.getObjeto());		//SELECIONA OS ACHADOS
		final int size=matches.totaMatches();		//TOTAL DE INSTÂNCIAS ACHADAS
		if(size==0){
			getUI().resultado.setText(MindSortUI.getLang().get("M_Menu_P_P_NE","No encounter!"));
			Toolkit.getDefaultToolkit().beep();
		}else if(size==1){
			getUI().resultado.setText(size+MindSortUI.getLang().get("M_Menu_P_P_I"," instance found!"));
			listar(frente);				//IMEDIATAMENTE O SELECIONA, SENDO APENAS UM
		}else{
			getUI().resultado.setText(size+MindSortUI.getLang().get("M_Menu_P_P_Is"," instances found!"));
		}
		getUI().procurar.setText(getUI().listarTxt);	//PROCURAR -> LISTAR
		tree.getPainel().getJanela().requestFocus();
		tree.draw();
	}
//DESTACAR
	protected void destacar(String termo,boolean onlySelected,boolean wholeWord,boolean diffMaiuscMinusc){
		if(termo.isEmpty()){
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		matches.search(termo,true,onlySelected,wholeWord,diffMaiuscMinusc);		//PROCURA
		tree.getActions().unSelectAll();										//DESELECIONA TUDO
		for(Match match:matches.getMatchs())tree.select(match.getObjeto());		//SELECIONA OS ACHADOS
		final int size=matches.totalObjs();			//TOTAL DE OBJS ACHADOS
		if(size==0){
			getUI().resultado.setText(MindSortUI.getLang().get("M_Menu_P_D_NR","No results!"));
			Toolkit.getDefaultToolkit().beep();
		}else if(size==1){
			getUI().resultado.setText(size+MindSortUI.getLang().get("M_Menu_P_D_O"," selected object!"));
		}else{
			getUI().resultado.setText(size+MindSortUI.getLang().get("M_Menu_P_D_Os"," selected objects!"));
		}
		getUI().procurar.setText(getUI().listarTxt);	//PROCURAR -> LISTAR
		tree.getPainel().getJanela().requestFocus();
		tree.draw();
	}
}