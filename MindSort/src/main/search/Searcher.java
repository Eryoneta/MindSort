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
	private final String procurarTxt=MindSort.getLang().get("M_Menu_P_P","Find");
	private final String listarTxt=MindSort.getLang().get("M_Menu_P_L","List");
	private final String proximoTxt=MindSort.getLang().get("M_Menu_P_L_P","Next");
	private final String anteriorTxt=MindSort.getLang().get("M_Menu_P_L_A","Previous");
//MAIN
	public Searcher(Tree tree){
		this.tree=tree;
		matches=new MatchMade(tree);
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
					if(tree.getTexto().getObjeto()==null)return;
					if(!tree.getTexto().getObjeto().getTipo().is(Objeto.Tipo.MODULO))return;
					if(matches.isEmpty()||matches.get(index)==null)return;
					final Objeto objAtual=matches.get(index).getObjeto();
					matches.del(objAtual);
					try{
						final String texto=d.getDocument().getText(0,d.getDocument().getLength());
						matches.researchText(objAtual,true,texto);
					}catch(BadLocationException erro){}
				}
			});
			tree.getTexto().addEditorListener(new DocumentListener(){
				public void removeUpdate(DocumentEvent d){run(d);}
				public void insertUpdate(DocumentEvent d){run(d);}
				public void changedUpdate(DocumentEvent d){run(d);}
				private void run(DocumentEvent d){
					if(tree.getTexto().getObjeto()==null)return;
					if(!tree.getTexto().getObjeto().getTipo().is(Objeto.Tipo.MODULO))return;
					if(matches.isEmpty()||matches.get(index)==null)return;
					final Objeto objAtual=matches.get(index).getObjeto();
					matches.del(objAtual);
					try{
						final String texto=d.getDocument().getText(0,d.getDocument().getLength());
						matches.researchText(objAtual,false,texto);
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
//PROCURAR
	private MatchMade matches;
	private int index=0;
	private void listar(boolean frente){
		if(!procurar.getText().equals(listarTxt)){
			index+=(frente?+1:-1);
		}else procurar.setText(frente?proximoTxt:anteriorTxt);
		if(index<0)index=matches.size()-1;		//RESETA PARA O FIM
		if(index>=matches.size())index=0;		//RESETA PARA O COMEÇO
		tree.getActions().unSelectAll();
		final Match match=matches.get(index);
		if(!tree.getObjetos().contains(match.getObjeto()))return;	//CASO TENHA SIDO DEL
		if(match.isOnText()){
			tree.select(match.getObjeto());
			tree.draw();
			tree.getTexto().requestFocus();
			tree.getTexto().select(match.getSelectionStart(),match.getSelectionEnd());
		}else{
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
		matches.search(termo,frente,onlySelected,wholeWord,diffMaiuscMinusc);
		final int size=matches.size();
		if(size==0){
			resultado.setText(MindSort.getLang().get("M_Menu_P_P_NE","No encounter!"));
			Toolkit.getDefaultToolkit().beep();
		}else if(size==1){
			resultado.setText(size+MindSort.getLang().get("M_Menu_P_P_I"," instance found!"));
			listar(frente);
		}else{
			resultado.setText(size+MindSort.getLang().get("M_Menu_P_P_Is"," instances found!"));
		}
		procurar.setText(listarTxt);
		tree.getPainel().getJanela().requestFocus();
		tree.draw();
	}
//DESTACAR
	private void destacar(String termo,boolean onlySelected,boolean wholeWord,boolean diffMaiuscMinusc){
		if(termo.isEmpty()){
			Toolkit.getDefaultToolkit().beep();
			return;
		}
		matches.search(termo,true,onlySelected,wholeWord,diffMaiuscMinusc);
		final int size=matches.size();
		if(size==0){
			resultado.setText(MindSort.getLang().get("M_Menu_P_D_NR","No results!"));
			Toolkit.getDefaultToolkit().beep();
		}else if(size==1){
			resultado.setText(size+MindSort.getLang().get("M_Menu_P_D_O"," selected object!"));
		}else{
			resultado.setText(size+MindSort.getLang().get("M_Menu_P_D_Os"," selected objects!"));
		}
		tree.getPainel().getJanela().requestFocus();
		tree.draw();
	}
}