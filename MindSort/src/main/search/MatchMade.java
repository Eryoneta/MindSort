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
@SuppressWarnings("serial")
	private class SortedList<T> extends ArrayList<T>{
		//LISTA ORDENADA(POR VALOR) COM SUBLISTAS ORDENADAS(POR INSERÇÃO)
		public boolean add(T valor){
			super.add(getInsertIndex(valor),valor);
			return true;
		}
	@SuppressWarnings("unchecked")
		private int getInsertIndex(T valor){
			int min=0;
			int max=size()-1;
			while(min<=max){
				int mid=(min+max)>>>1;
				final Comparable<? super T>midValor=(Comparable<T>)super.get(mid);
				final int compara=midValor.compareTo(valor);
				if(compara<0)min=mid+1;				//VALOR MENOR
					else if(compara>0)max=mid-1;	//VALOR MAIOR
						else{						//VALOR IGUAL
							for(int index=mid+1;true;index++){
								if(++index>=super.size())return index-1;	//VALOR > LISTA = FIM DA LISTA
								final Comparable<? super T>indexValor=(Comparable<T>)super.get(index);
								final int subCompara=indexValor.compareTo(valor);
								if(subCompara!=0)return index;				//VALOR_INDEX > VALOR = FIM DA SUBLISTA
							}
						}
			}
			return min;		//VALOR NOVO
		}
	}
	private SortedList<Match>matches=new SortedList<>();
		public List<Match>getMatchs(){return matches;}
		public Match get(int index){return matches.get(index);}
	private HashMap<Integer,List<Match>>matchesByObjs=new HashMap<>();
		public List<Match>getMatchsByObj(Objeto obj){return matchesByObjs.get(obj.getIndex());}
	public void add(Match match){
		final Objeto obj=match.getObjeto();
		if(!matchesByObjs.containsKey(obj.getIndex())){
			matchesByObjs.put(obj.getIndex(),new ArrayList<>());
		}
		matches.add(match);
		matchesByObjs.get(obj.getIndex()).add(match);
	}
	public void del(Objeto obj){
		if(matchesByObjs.get(obj.getIndex())!=null){
			final List<Match>matchesRemoved=matchesByObjs.remove(obj.getIndex());
			for(Match match:matchesRemoved)matches.remove(match);
		}
	}
	public int totalObjs(){return matchesByObjs.size();}
	public int totaMatches(){return matches.size();}
//SEARCH
	private Pattern regex;
//	private boolean frente;
//	private boolean onlySelected;
//	private boolean wholeWord;
	private boolean diffMaiuscMinusc;
	public void search(String termo,boolean frente,boolean onlySelected,boolean wholeWord,boolean diffMaiuscMinusc){
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
				if(match.find()){
					do{
						add(new Match(obj,!isTitulo,match.start(),match.end()));
					}while(match.find());
				}
			}
				public void research(Objeto obj){searchInObj(obj);}	//APENAS PARA CONTEXTUALIZAR
				public void researchText(Objeto obj,boolean isTitulo,String texto){searchInObjTexto(obj,isTitulo,texto);}	//APENAS PARA CONTEXTUALIZAR
//MAIN
	public MatchMade(Tree tree){this.tree=tree;}
//FUNCS
	public boolean isEmpty(){return (matchesByObjs.isEmpty());}
	public void clear(){matchesByObjs.clear();}
}