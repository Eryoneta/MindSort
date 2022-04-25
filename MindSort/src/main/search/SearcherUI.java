package main.search;
import java.awt.BorderLayout;
import java.awt.GridLayout;
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
import element.tree.objeto.Objeto;
import main.MindSortUI;
@SuppressWarnings("serial")
public class SearcherUI{
//MINDSORT
	private Searcher searcher;
//VAR GLOBAIS
	private JDialog dialog;
		public JDialog getWindow(){return dialog;}
	protected JTextField termo;
	protected JLabel resultado;
	protected JButton procurar;
	protected JButton destacar;
	protected String procurarTxt;
	protected String listarTxt;
	protected String proximoTxt;
	protected String anteriorTxt;
//MAIN
	public SearcherUI(Searcher searcher){this.searcher=searcher;}
//FUNCS
	public void updateInterface(){
		if(dialog!=null)dialog.dispose();
		procurarTxt=MindSortUI.getLang().get("M_Menu_P_P","Find");
		listarTxt=MindSortUI.getLang().get("M_Menu_P_L","List");
		proximoTxt=MindSortUI.getLang().get("M_Menu_P_L_P","Next");
		anteriorTxt=MindSortUI.getLang().get("M_Menu_P_L_A","Previous");
	//JANELA
		dialog=new JDialog(searcher.tree.getPainel().getJanela()){{
			setTitle(MindSortUI.getLang().get("M_Menu_P_Ti","Find"));
			setSize(300,400);
			setMinimumSize(getSize());
			setLocationRelativeTo(searcher.tree.getPainel().getJanela());
		//TEXTO
			termo=new JTextField(){{
				addKeyListener(new KeyListener(){
					public void keyTyped(KeyEvent k){}
					public void keyReleased(KeyEvent k){searcher.reset();}
					public void keyPressed(KeyEvent k){}
				});
			}};
		//GRUPO_DIREÇÃO
			final ButtonGroup grupoDirecao=new ButtonGroup();
			//FRENTE
				final JRadioButton frente=new JRadioButton(MindSortUI.getLang().get("M_Menu_P_Dir_F","Forward")){{
					grupoDirecao.add(this);
					setSelected(true);
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){
							if(!searcher.matches.isEmptyOfMatches())procurar.setText(proximoTxt);
						}
					});
				}};
			//ATRÁS
				final JRadioButton atras=new JRadioButton(MindSortUI.getLang().get("M_Menu_P_Dir_T","Backward")){{
					grupoDirecao.add(this);
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){
							if(!searcher.matches.isEmptyOfMatches())procurar.setText(anteriorTxt);
						}
					});
				}};
		//GRUPO_ESCOPO
			final ButtonGroup grupoEscopo=new ButtonGroup();
			//TUDO
				final JRadioButton tudo=new JRadioButton(MindSortUI.getLang().get("M_Menu_P_Es_T","All")){{
					grupoEscopo.add(this);
					setSelected(true);
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){searcher.reset();}
					});
				}};
			//APENAS SELECIONADOS
				final JRadioButton onlySelected=new JRadioButton(MindSortUI.getLang().get("M_Menu_P_Es_S","Selected")){{
					grupoEscopo.add(this);
					addItemListener(new ItemListener(){
						public void itemStateChanged(ItemEvent i){searcher.reset();}
					});
				}};
		//PALAVRAS INTEIRAS
			final JCheckBox wholeWord=new JCheckBox(MindSortUI.getLang().get("M_Menu_P_Op_P","Whole word")){{
				addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent i){searcher.reset();}
				});
			}};
		//DIFERENCIAR CAPITAL
			final JCheckBox diffMaiuscMinusc=new JCheckBox(MindSortUI.getLang().get("M_Menu_P_Op_D","Case sensitive")){{
				addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent i){searcher.reset();}
				});
			}};
		//EXPRESSÃO REGULAR
			final JCheckBox regEx=new JCheckBox(MindSortUI.getLang().get("M_Menu_P_Op_R","Regular Expression")){{
				addItemListener(new ItemListener(){
					public void itemStateChanged(ItemEvent i){searcher.reset();}
				});
			}};
		//PAINEL SUPERIOR
			getContentPane().add(new JPanel(){{
				setLayout(new GridLayout(1,2));
			//PROCURAR
				add(new JLabel(MindSortUI.getLang().get("M_Menu_P_Tx","Find:")){{
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
										MindSortUI.getLang().get("M_Menu_P_Dir","Direction")),
								BorderFactory.createEmptyBorder(5,10,10,10)
						));
					}});
					add(new JPanel(){{								//PAINEL ESCOPO
						setLayout(new GridLayout(2,1));
						add(tudo);										//TUDO
						add(onlySelected);								//APENAS SELECIONADOS
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
										MindSortUI.getLang().get("M_Menu_P_Es","Scope")),
								BorderFactory.createEmptyBorder(5,10,10,10)
						));
					}});
				}});
				add(new JPanel(){{									//PAINEL OPÇÕES INFERIOR
					setLayout(new GridLayout(3,1));
					add(wholeWord);										//PALAVRAS INTEIRAS
					add(diffMaiuscMinusc);								//DIFERENCIAR CAPITAL
					add(regEx);											//EXPRESSÃO REGULAR 
					setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
									MindSortUI.getLang().get("M_Menu_P_Op","Options")),
							BorderFactory.createEmptyBorder(5,10,10,10)
					));
				}});
			}},BorderLayout.CENTER);
			getContentPane().add(new JPanel(){{					//PAINEL INFERIOR
				setLayout(new GridLayout(2,2));
				add(destacar=new JButton(MindSortUI.getLang().get("M_Menu_P_D","Highlight")){{						//DESTACAR
					addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent a){
							searcher.destacar(termo.getText(),regEx.isSelected(),wholeWord.isSelected(),diffMaiuscMinusc.isSelected(),onlySelected.isSelected());
						}
					});
				}});
				add(procurar=new JButton(procurarTxt){{						//PROCURAR
					addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent a){
							if(!searcher.matches.isEmptyOfMatches()){
								searcher.listar(frente.isSelected());
							}else{
								searcher.procurar(termo.getText(),regEx.isSelected(),wholeWord.isSelected(),diffMaiuscMinusc.isSelected(),onlySelected.isSelected(),frente.isSelected());
							}
						}
					});
				}});
				add(resultado=new JLabel(""){{						//RESULTADOS
					setHorizontalAlignment(JLabel.LEFT);
					setHorizontalTextPosition(JLabel.LEFT);
				}});
				add(new JButton(MindSortUI.getLang().get("M_Menu_P_F","Close")){{	//FECHAR
					addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent a){
							searcher.dispensar();
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
				public void windowClosing(WindowEvent w){searcher.dispensar();}
				public void windowClosed(WindowEvent w){}
				public void windowActivated(WindowEvent w){}
			});
			searcher.tree.getUI().getTitulo().getDocument().addDocumentListener(new DocumentListener(){
				public void removeUpdate(DocumentEvent d){run(d);}
				public void insertUpdate(DocumentEvent d){run(d);}
				public void changedUpdate(DocumentEvent d){}
				private void run(DocumentEvent d){
					if(searcher.tree.getUI().getTitulo().getObjeto()==null)return;
					if(!searcher.tree.getUI().getTitulo().getObjeto().getTipo().is(Objeto.Tipo.MODULO))return;
					searcher.reProcurar(searcher.tree.getUI().getTitulo().getObjeto());
				}
			});
			searcher.tree.getUI().getTexto().getDocument().addDocumentListener(new DocumentListener(){
				public void removeUpdate(DocumentEvent d){run(d);}
				public void insertUpdate(DocumentEvent d){run(d);}
				public void changedUpdate(DocumentEvent d){}
				private void run(DocumentEvent d){
					if(searcher.tree.getUI().getTexto().getObjeto()==null)return;
					if(!searcher.tree.getUI().getTexto().getObjeto().getTipo().is(Objeto.Tipo.MODULO,Objeto.Tipo.CONEXAO))return;
					searcher.reProcurar(searcher.tree.getUI().getTexto().getObjeto());
				}
			});
		}};
	}
}