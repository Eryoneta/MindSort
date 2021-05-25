package main.search;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import element.tree.Tree;
import element.tree.objeto.Objeto;
import element.tree.objeto.conexao.Conexao;
import element.tree.objeto.modulo.Modulo;
public class MatchMade{
//TREE
	private Tree tree;
//LISTA
	final HashMap<Integer,List<Match>>matchesByObjs=new HashMap<>();
		public List<Match>getByObj(Objeto obj){return matchesByObjs.get(obj.getIndex());}
		public Match get(int index){
			int indexTotal=0;
			for(List<Match>matches:matchesByObjs.values()){
				final int size=matches.size();
				if(index>=indexTotal+size){
					indexTotal+=size;
					continue;	//PRÓXIMO OBJ
				}
				return matches.get(index-indexTotal);
			}
			return null;
		}
		public void add(Match match){
			final Objeto obj=match.getObjeto();
			if(!matchesByObjs.containsKey(obj.getIndex())){
				matchesByObjs.put(obj.getIndex(),new ArrayList<>());
			}
			final boolean added=matchesByObjs.get(obj.getIndex()).add(match);
			if(added)size++;
		}
		public void del(Objeto obj){
			if(matchesByObjs.get(obj.getIndex())!=null){
				size-=matchesByObjs.get(obj.getIndex()).size();
				matchesByObjs.remove(obj.getIndex());
			}
		}
	private int size;
		public int size(){return size;}
//SEARCH
	private Pattern regex;
//	private boolean frente;
//	private boolean onlySelected;
//	private boolean wholeWord;
	private boolean diffMaiuscMinusc;
	public void search(String termo,boolean frente,boolean onlySelected,boolean wholeWord,boolean diffMaiuscMinusc){
		tree.getActions().unSelectAll();
//		this.frente=frente;
//		this.onlySelected=onlySelected;
//		this.wholeWord=wholeWord;
		this.diffMaiuscMinusc=diffMaiuscMinusc;
		termo=(diffMaiuscMinusc?termo:termo.toLowerCase());
		final List<Objeto>objs=new ArrayList<Objeto>();
		if(onlySelected){
			objs.addAll(tree.getSelectedObjetos().getModulos());
			objs.addAll(tree.getSelectedObjetos().getConexoes());
		}else{
			objs.addAll(tree.getObjetos().getModulos());
			objs.addAll(tree.getObjetos().getConexoes());
		}
		final String termoRx=termo.replaceAll("(\\(|\\)|\\[|\\]|\\{|\\}|\\^|\\$|\\*|\\+|\\?|\\.|\\|\\|)","\\\\$1");	//VÁRIOS DE (, ), [, ], {, }, ^, $, *, +, ?, ., \, OU | )
		regex=Pattern.compile(wholeWord?
				"(^|[^a-zA-ZÀ-ÿ])+("+termoRx+"){1}($|[^a-zA-ZÀ-ÿ])+":		//(1 QUE É COMEÇO OU NÃO É LETRA)(1 TERMO)(1 QUE É FIM OU NÃO É LETRA)
				"("+termoRx+"){1}"											//(1 TERMO)
		);
		for(Objeto obj:objs){
			searchInObj(obj);
		}
	}
	private void searchInObj(Objeto obj){
		switch(obj.getTipo()){
			case MODULO:default:
				final Modulo mod=(Modulo)obj;
				final String modTitulo=(diffMaiuscMinusc?mod.getTitle():mod.getTitle().toLowerCase());
				searchInObjTexto(mod,true,modTitulo);
				final String modTexto=(diffMaiuscMinusc?mod.getText():mod.getText().toLowerCase());
				searchInObjTexto(mod,false,modTexto);
			break;
			case CONEXAO:
				final Conexao cox=(Conexao)obj;
				final String coxTexto=(diffMaiuscMinusc?cox.getText():cox.getText().toLowerCase());
				searchInObjTexto(cox,false,coxTexto);
			break;
			case NODULO:break;
			case SEGMENTO:break;
		}
	}
	private void searchInObjTexto(Objeto obj,boolean isTitulo,String texto){
		final Matcher match=regex.matcher(texto);
		if(match.find()){			//VERIFICA TÍTULO
			tree.select(obj);
			do{
				add(new Match(obj,!isTitulo,match.start(),match.end()));
			}while(match.find());
		}
	}
	public void research(Objeto obj){searchInObj(obj);}	//APENAS PARA RENOMEAR
	public void researchText(Objeto obj,boolean isTitulo,String texto){searchInObjTexto(obj,isTitulo,texto);}	//APENAS PARA RENOMEAR
//MAIN
	public MatchMade(Tree tree){this.tree=tree;}
//FUNCS
	public boolean isEmpty(){return (matchesByObjs.isEmpty());}
	public void clear(){matchesByObjs.clear();}
}