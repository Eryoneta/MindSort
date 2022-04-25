package main.search;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import element.tree.main.Tree;
import element.tree.objeto.Objeto;
import element.tree.objeto.conexao.Conexao;
import element.tree.objeto.modulo.Modulo;
import utilitarios.HashList;
import utilitarios.ferramenta.regex.RegexBuilder;
public class MatchMade implements RegexBuilder{
//TREE
	private Tree tree;
//LISTA
	private HashList<Objeto,Match>matches=new HashList<>();
		public List<Objeto>getMatchedObjs(){return new ArrayList<>(matches.keySet());}
		public List<Match>getObjMatchList(Objeto obj){return matches.get(obj);}
		public Match getMatch(int index){return matches.getValue(index);}
		public int totalMatchedObjs(){return matches.size();}
		public int totalMatches(){return matches.totalSize();}
		public Match prevMatch(){
			if(isEmptyOfMatches())return null;
			currentIndex--;
			if(currentIndex<0)currentIndex=matches.totalSize()-1;
			return matches.getValue(currentIndex);
		}
		public Match nextMatch(){
			if(isEmptyOfMatches())return null;
			currentIndex++;
			if(currentIndex>=matches.totalSize())currentIndex=0;
			return matches.getValue(currentIndex);
		}
		public boolean isEmptyOfMatches(){return (matches.isEmpty());}
		public void clearMatches(){matches.clear();}
//VAR GLOBAIS
	private Pattern regex;
	private boolean diffMaiuscMinusc=false;
//CURRENT_INDEX
	private int currentIndex=-1;
//MAIN
	public MatchMade(Tree tree){this.tree=tree;}
//FUNCS
	public void search(String termo,boolean isRegex,boolean wholeWord,boolean diffMaiuscMinusc,boolean onlySelected){
		clearMatches();
		termo=(diffMaiuscMinusc?termo:termo.toLowerCase());
		final List<Objeto>objs=new ArrayList<Objeto>();
		if(onlySelected){
			objs.addAll(tree.getSelectedObjetos().getModulos());
			objs.addAll(tree.getSelectedObjetos().getConexoes());
		}else{
			objs.addAll(tree.getObjetos().getModulos());
			objs.addAll(tree.getObjetos().getConexoes());
		}
		final String termoRx=(isRegex?termo:Pattern.quote(termo));
		final Pattern regex=Pattern.compile(wholeWord?nonWord()+termoRx+nonWord():termoRx);
		for(Objeto obj:objs)searchInObj(obj,regex,diffMaiuscMinusc);
		this.regex=regex;
		this.diffMaiuscMinusc=diffMaiuscMinusc;
	}
		private void searchInObj(Objeto obj,Pattern regex,boolean diffMaiuscMinusc){
			switch(obj.getTipo()){
				case MODULO:default:
					final Modulo mod=(Modulo)obj;
					final String modTitulo=(diffMaiuscMinusc?mod.getTitle():mod.getTitle().toLowerCase());
					searchInObjTitulo(mod,regex,modTitulo);
					final String modTexto=(diffMaiuscMinusc?mod.getText():mod.getText().toLowerCase());
					searchInObjTexto(mod,regex,modTexto);
				break;
				case CONEXAO:
					final Conexao cox=(Conexao)obj;
					final String coxTexto=(diffMaiuscMinusc?cox.getText():cox.getText().toLowerCase());
					searchInObjTexto(cox,regex,coxTexto);
				break;
				case NODULO:break;
				case SEGMENTO:break;
			}
		}
			private void searchInObjTitulo(Objeto obj,Pattern regex,String texto){
				final Matcher match=regex.matcher(texto);
				while(match.find()){
					matches.add(obj,new Match(obj,false,match.start(),match.end()));
				}
			}
			private void searchInObjTexto(Objeto obj,Pattern regex,String texto){
				final Matcher match=regex.matcher(texto);
				while(match.find()){
					matches.add(obj,new Match(obj,true,match.start(),match.end()));
				}
			}
	public void researchObjText(Objeto obj){
		currentIndex=matches.indexOfKey(obj);
		matches.remove(obj);
		searchInObj(obj,regex,diffMaiuscMinusc);
	}
	
//LISTA
//@SuppressWarnings({"serial","unchecked"})
//	private class SortedList<T> extends ArrayList<T>{
//		//LISTA ORDENADA(POR VALOR) COM SUBLISTAS ORDENADAS(POR INSERÇÃO)
//		public boolean add(T valor){
//			super.add(getInsertIndex(valor),valor);
//			return true;
//		}
//		private int getInsertIndex(T valor){
//			int min=0;
//			int max=size()-1;
//			while(min<=max){
//				int mid=(min+max)>>>1;
//				final Comparable<? super T>midValor=(Comparable<T>)super.get(mid);
//				final int compara=midValor.compareTo(valor);
//				if(compara<0)min=mid+1;				//VALOR MENOR
//					else if(compara>0)max=mid-1;	//VALOR MAIOR
//						else{						//VALOR IGUAL
//							for(int index=mid+1;true;index++){
//								if(++index>=super.size())return index-1;	//VALOR > LISTA = FIM DA LISTA
//								final Comparable<? super T>indexValor=(Comparable<T>)super.get(index);
//								final int subCompara=indexValor.compareTo(valor);
//								if(subCompara!=0)return index;				//VALOR_INDEX > VALOR = FIM DA SUBLISTA
//							}
//						}
//			}
//			return min;		//VALOR NOVO
//		}
//	}
//	private SortedList<Match>matches=new SortedList<>();
//		public List<Match>getMatchs(){return matches;}
//		public Match get(int index){return matches.get(index);}
//	private HashMap<Integer,List<Match>>matchesByObjs=new HashMap<>();
//		public List<Match>getMatchsByObj(Objeto obj){return matchesByObjs.get(obj.getIndex());}
//	public void add(Match match){
//		final Objeto obj=match.getObjeto();
//		if(!matchesByObjs.containsKey(obj.getIndex())){
//			matchesByObjs.put(obj.getIndex(),new ArrayList<>());
//		}
//		matches.add(match);
//		matchesByObjs.get(obj.getIndex()).add(match);
//	}
//	public void del(Objeto obj){
//		if(matchesByObjs.get(obj.getIndex())!=null){
//			final List<Match>matchesRemoved=matchesByObjs.remove(obj.getIndex());
//			for(Match match:matchesRemoved)matches.remove(match);
//		}
//	}
//	public int totalObjs(){return matchesByObjs.size();}
//	public int totaMatches(){return matches.size();}
////SEARCH
//	private Pattern regex;
////	private boolean frente;
////	private boolean onlySelected;
////	private boolean wholeWord;
//	private boolean diffMaiuscMinusc;
//	public void search(String termo,boolean frente,boolean onlySelected,boolean wholeWord,boolean diffMaiuscMinusc){
////		this.frente=frente;
////		this.onlySelected=onlySelected;
////		this.wholeWord=wholeWord;
//		this.diffMaiuscMinusc=diffMaiuscMinusc;
//		termo=(diffMaiuscMinusc?termo:termo.toLowerCase());
//		final List<Objeto>objs=new ArrayList<Objeto>();
//		if(onlySelected){
//			objs.addAll(tree.getSelectedObjetos().getModulos());
//			objs.addAll(tree.getSelectedObjetos().getConexoes());
//		}else{
//			objs.addAll(tree.getObjetos().getModulos());
//			objs.addAll(tree.getObjetos().getConexoes());
//		}
//		final String termoRx=Pattern.quote(termo);
//		if(wholeWord){
//			regex=Pattern.compile(termoRx);
//		}else regex=Pattern.compile(nonWord()+termoRx+nonWord());
//		for(Objeto obj:objs){
//			searchInObj(obj);
//		}
//	}
//		private void searchInObj(Objeto obj){
//			switch(obj.getTipo()){
//				case MODULO:default:
//					final Modulo mod=(Modulo)obj;
//					final String modTitulo=(diffMaiuscMinusc?mod.getTitle():mod.getTitle().toLowerCase());
//					searchInObjTexto(mod,true,modTitulo);
//					final String modTexto=(diffMaiuscMinusc?mod.getText():mod.getText().toLowerCase());
//					searchInObjTexto(mod,false,modTexto);
//				break;
//				case CONEXAO:
//					final Conexao cox=(Conexao)obj;
//					final String coxTexto=(diffMaiuscMinusc?cox.getText():cox.getText().toLowerCase());
//					searchInObjTexto(cox,false,coxTexto);
//				break;
//				case NODULO:break;
//				case SEGMENTO:break;
//			}
//		}
//			private void searchInObjTexto(Objeto obj,boolean isTitulo,String texto){
//				final Matcher match=regex.matcher(texto);
//				while(match.find()){
//					add(new Match(obj,!isTitulo,match.start(),match.end()));
//				}
//			}
//				public void research(Objeto obj){searchInObj(obj);}	//APENAS PARA CONTEXTUALIZAR
//				public void researchText(Objeto obj,boolean isTitulo,String texto){searchInObjTexto(obj,isTitulo,texto);}	//APENAS PARA CONTEXTUALIZAR
////MAIN
//	public MatchMade(Tree tree){this.tree=tree;}
////FUNCS
//	public boolean isEmpty(){return (matchesByObjs.isEmpty());}
//	public void clear(){matchesByObjs.clear();}
}