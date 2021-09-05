package main.search;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import main.MindSort;
import element.tree.objeto.Objeto;
import element.tree.Tree;
@SuppressWarnings("serial")
public class Searcher{
//VAR GLOBAIS
	private JDialog dialog;
	private Tree tree;
	private JTextField termo;
	private JLabel resultado;
	private JButton procurar;
	private JButton destacar;
	private String procurarTxt;
	private String listarTxt;
	private String proximoTxt;
	private String anteriorTxt;
	private MatchMade matches;
//MAIN
	public Searcher(Tree tree){
		this.tree=tree;
		matches=new MatchMade(tree);
		updateInterface();
	}
		public void updateInterface(){
			if(dialog!=null)dialog.dispose();
			procurarTxt=MindSort.getLang().get("M_Menu_P_P","Find");
			listarTxt=MindSort.getLang().get("M_Menu_P_L","List");
			proximoTxt=MindSort.getLang().get("M_Menu_P_L_P","Next");
			anteriorTxt=MindSort.getLang().get("M_Menu_P_L_A","Previous");
			dialog=new JDialog(tree.getPainel().getJanela()){{
				setTitle(MindSort.getLang().get("M_Menu_P_Ti","Find"));
				setSize(300,350);
				setMinimumSize(getSize());
				setLocationRelativeTo(tree.getPainel().getJanela());
				termo=new JTextField(){{				//TEXTO
					addKeyListener(new KeyListener(){
						public void keyTyped(KeyEvent k){reset();}
						public void keyReleased(KeyEvent k){reset();}
						public void keyPressed(KeyEvent k){reset();}
					});
				}};
				final ButtonGroup grupoDirecao=new ButtonGroup();
				final JRadioButton frente=new JRadioButton(MindSort.getLang().get("M_Menu_P_Dir_F","Forward")){{	//FRENTE
					grupoDirecao.add(this);
					setSelected(true);
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){
							if(!matches.isEmpty())procurar.setText(proximoTxt);
						}
					});
				}};
				final JRadioButton atras=new JRadioButton(MindSort.getLang().get("M_Menu_P_Dir_T","Backward")){{	//ATRÁS
					grupoDirecao.add(this);
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){
							if(!matches.isEmpty())procurar.setText(anteriorTxt);
						}
					});
				}};
				final ButtonGroup grupoEscopo=new ButtonGroup();
				final JRadioButton tudo=new JRadioButton(MindSort.getLang().get("M_Menu_P_Es_T","All")){{		//TUDO
					grupoEscopo.add(this);
					setSelected(true);
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){reset();}
					});
				}};
				final JRadioButton onlySelected=new JRadioButton(MindSort.getLang().get("M_Menu_P_Es_S","Selected")){{	//APENAS SELECIONADOS
					grupoEscopo.add(this);
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){reset();}
					});
				}};
				final JCheckBox wholeWord=new JCheckBox(MindSort.getLang().get("M_Menu_P_Op_P","Whole word")){{			//PALAVRAS INTEIRAS
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){reset();}
					});
				}};
				final JCheckBox diffMaiuscMinusc=new JCheckBox(MindSort.getLang().get("M_Menu_P_Op_D","Case sensitive")){{	//DIFERENCIAR CAPITAL
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){reset();}
					});
				}};
				getContentPane().add(new JPanel(){{				//PAINEL SUPERIOR
					setLayout(new GridLayout(1,2));
					add(new JLabel(MindSort.getLang().get("M_Menu_P_Tx","Find:")){{					//PROCURAR
						setHorizontalAlignment(JLabel.LEFT);
						setHorizontalTextPosition(JLabel.LEFT);
						setLabelFor(termo);
					}});
					add(termo);										//PALAVRA
					setBorder(BorderFactory.createEmptyBorder(25,10,20,10));
				}},BorderLayout.NORTH);
				getContentPane().add(new JPanel(){{				//PAINEL CENTRAL
					setLayout(new GridLayout(2,1));
					add(new JPanel(){{								//PAINEL OPÇÕES SUPERIOR
						setLayout(new GridLayout(1,2));
						add(new JPanel(){{								//PAINEL DIREÇÃO
							setLayout(new GridLayout(2,1));
							add(frente);									//FRENTE
							add(atras);										//ATRÁS
							setBorder(BorderFactory.createCompoundBorder(
									BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
											MindSort.getLang().get("M_Menu_P_Dir","Direction")),
									BorderFactory.createEmptyBorder(5,10,10,10)
							));
						}});
						add(new JPanel(){{								//PAINEL ESCOPO
							setLayout(new GridLayout(2,1));
							add(tudo);										//TUDO
							add(onlySelected);								//APENAS SELECIONADOS
							setBorder(BorderFactory.createCompoundBorder(
									BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
											MindSort.getLang().get("M_Menu_P_Es","Scope")),
									BorderFactory.createEmptyBorder(5,10,10,10)
							));
						}});
					}});
					add(new JPanel(){{									//PAINEL OPÇÕES INFERIOR
						setLayout(new GridLayout(2,1));
						add(wholeWord);										//PALAVRAS INTEIRAS
						add(diffMaiuscMinusc);								//DIFERENCIAR CAPITAL
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
										MindSort.getLang().get("M_Menu_P_Op","Options")),
								BorderFactory.createEmptyBorder(5,10,10,10)
						));
					}});
				}},BorderLayout.CENTER);
				getContentPane().add(new JPanel(){{					//PAINEL INFERIOR
					setLayout(new GridLayout(2,2));
					add(destacar=new JButton(MindSort.getLang().get("M_Menu_P_D","Highlight")){{						//DESTACAR
						addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent a){
								reset();
								destacar(termo.getText(),onlySelected.isSelected(),wholeWord.isSelected(),diffMaiuscMinusc.isSelected());
							}
						});
					}});
					add(procurar=new JButton(procurarTxt){{						//PROCURAR
						addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent a){
								if(!matches.isEmpty()){
									listar(frente.isSelected());
								}else{
									procurar(termo.getText(),frente.isSelected(),onlySelected.isSelected(),wholeWord.isSelected(),diffMaiuscMinusc.isSelected());
								}
							}
						});
					}});
					add(resultado=new JLabel(""){{						//RESULTADOS
						setHorizontalAlignment(JLabel.LEFT);
						setHorizontalTextPosition(JLabel.LEFT);
					}});
					add(new JButton(MindSort.getLang().get("M_Menu_P_F","Close")){{							//FECHAR
						addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent a){
								dispensar();
							}
						});
					}});
					setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
				}},BorderLayout.SOUTH);
				addWindowListener(new WindowListener(){
					public void windowOpened(WindowEvent w){}
					public void windowIconified(WindowEvent w){}
					public void windowDeiconified(WindowEvent w){}
					public void windowDeactivated(WindowEvent w){}
					public void windowClosing(WindowEvent w){dispensar();}
					public void windowClosed(WindowEvent w){}
					public void windowActivated(WindowEvent w){}
				});
				tree.getTitulo().addEditorListener(new DocumentListener(){
					public void removeUpdate(DocumentEvent d){run(d);}
					public void insertUpdate(DocumentEvent d){run(d);}
					public void changedUpdate(DocumentEvent d){run(d);}
					private void run(DocumentEvent d){
						if(tree.getTitulo().getObjeto()==null)return;
						if(!tree.getTitulo().getObjeto().getTipo().is(Objeto.Tipo.MODULO))return;
						try{
							researchMatch(d.getDocument().getText(0,d.getDocument().getLength()));
						}catch(BadLocationException erro){}
					}
				});
				tree.getTexto().addEditorListener(new DocumentListener(){
					public void removeUpdate(DocumentEvent d){run(d);}
					public void insertUpdate(DocumentEvent d){run(d);}
					public void changedUpdate(DocumentEvent d){run(d);}
					private void run(DocumentEvent d){
						if(tree.getTexto().getObjeto()==null)return;
						if(!tree.getTexto().getObjeto().getTipo().is(Objeto.Tipo.MODULO,Objeto.Tipo.CONEXAO))return;
						try{
							researchMatch(d.getDocument().getText(0,d.getDocument().getLength()));
						}catch(BadLocationException erro){}
					}
				});
			}};
		}
//FUNCS
	public void chamar(){dialog.setVisible(true);}
	public void dispensar(){
		reset();
		dialog.dispose();
	}
	public void reset(){
		resultado.setText("");
		procurar.setText(procurarTxt);
		matches.clear();
		index=0;
		procurar.setEnabled(!termo.getText().isEmpty());
		destacar.setEnabled(!termo.getText().isEmpty());
	}
	private void researchMatch(String newTexto){
		if(matches.isEmpty()||matches.get(index)==null)return;
		final Objeto objAtual=matches.get(index).getObjeto();
		matches.del(objAtual);
		matches.researchText(objAtual,false,newTexto);
	}
//PROCURAR
	private int index=0;
	private void listar(boolean frente){
		if(!procurar.getText().equals(listarTxt)){
			index+=(frente?+1:-1);
		}else procurar.setText(frente?proximoTxt:anteriorTxt);	//PROCURAR -> PROX/ANTE
		if(index<0)index=matches.totaMatches()-1;				//RESETA PARA O FIM
		if(index>=matches.totaMatches())index=0;				//RESETA PARA O COMEÇO
		tree.getActions().unSelectAll();						//DESELECIONA TUDO
		final Match match=matches.get(index);
		if(!tree.getObjetos().contains(match.getObjeto()))return;	//CASO OBJ TENHA SIDO DEL
		if(match.isOnText()){	//DESTACA TEXTO
			tree.select(match.getObjeto());
			tree.draw();
			tree.getTexto().requestFocus();
			tree.getTexto().select(match.getSelectionStart(),match.getSelectionEnd());
		}else{					//DESTACA TÍTULO
			tree.select(match.getObjeto());
			tree.getPainel().getJanela().requestFocus();	//FOCA A JANELA
			tree.getActions().editTitulo();
			tree.draw();
			tree.getTitulo().requestFocus();				//FOCA O TÍTULO
			tree.getTitulo().select(match.getSelectionStart(),match.getSelectionEnd());
		}
	}
	private void procurar(String termo,boolean frente,boolean onlySelected,boolean wholeWord,boolean diffMaiuscMinusc){
		if(termo.isEmpty()){
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		matches.search(termo,frente,onlySelected,wholeWord,diffMaiuscMinusc);	//PROCURA
		tree.getActions().unSelectAll();										//DESELECIONA TUDO
		for(Match match:matches.getMatchs())tree.select(match.getObjeto());		//SELECIONA OS ACHADOS
		final int size=matches.totaMatches();		//TOTAL DE INSTÂNCIAS ACHADAS
		if(size==0){
			resultado.setText(MindSort.getLang().get("M_Menu_P_P_NE","No encounter!"));
			Toolkit.getDefaultToolkit().beep();
		}else if(size==1){
			resultado.setText(size+MindSort.getLang().get("M_Menu_P_P_I"," instance found!"));
			listar(frente);				//IMEDIATAMENTE O SELECIONA, SENDO APENAS UM
		}else{
			resultado.setText(size+MindSort.getLang().get("M_Menu_P_P_Is"," instances found!"));
		}
		procurar.setText(listarTxt);	//PROCURAR -> LISTAR
		tree.getPainel().getJanela().requestFocus();
		tree.draw();
	}
//DESTACAR
	private void destacar(String termo,boolean onlySelected,boolean wholeWord,boolean diffMaiuscMinusc){
		if(termo.isEmpty()){
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		matches.search(termo,true,onlySelected,wholeWord,diffMaiuscMinusc);		//PROCURA
		tree.getActions().unSelectAll();										//DESELECIONA TUDO
		for(Match match:matches.getMatchs())tree.select(match.getObjeto());		//SELECIONA OS ACHADOS
		final int size=matches.totalObjs();			//TOTAL DE OBJS ACHADOS
		if(size==0){
			resultado.setText(MindSort.getLang().get("M_Menu_P_D_NR","No results!"));
			Toolkit.getDefaultToolkit().beep();
		}else if(size==1){
			resultado.setText(size+MindSort.getLang().get("M_Menu_P_D_O"," selected object!"));
		}else{
			resultado.setText(size+MindSort.getLang().get("M_Menu_P_D_Os"," selected objects!"));
		}
		procurar.setText(listarTxt);	//PROCURAR -> LISTAR
		tree.getPainel().getJanela().requestFocus();
		tree.draw();
	}
}