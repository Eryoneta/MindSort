package main;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import element.Painel;
import element.tree.ObjetoFocusListener;
import element.tree.main.Tree;
import element.tree.main.TreeST;
import element.tree.main.TreeUI;
import element.tree.objeto.Objeto;
import element.tree.objeto.conexao.Conexao;
import element.tree.objeto.modulo.Modulo;
import element.tree.objeto.modulo.ModuloUI;
import element.tree.propriedades.Cor;
import element.tree.texto.Texto;
import element.tree.undoRedo.UndoRedoListener;
import main.janela.Janela;
import main.search.Searcher;
import menu.Botao;
import menu.Menu;
import menu.Toggle;
import utilitarios.ferramenta.language.LanguagePackage;
import java.util.Locale;
import javax.swing.JColorChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
@SuppressWarnings({"serial","unchecked"})
public class MindSortUI{
//LANG
	private static LanguagePackage LANG=new LanguagePackage();
		public static LanguagePackage getLang(){return LANG;}
		public static void addLanguage(File link,String idiomaFiltro,String prefixFiltro){
			LANG.add(link,idiomaFiltro,prefixFiltro);
		}
//FONTE
	public static final Font FONTE=new Font("Segoe UI Emoji",Font.PLAIN,12);
//AÇÕES DE BOTÕES
	private AbstractAction novoAction;
	private AbstractAction abrirAction;
	private AbstractAction salvarAction;
	private AbstractAction salvarComoAction;
	private AbstractAction sairAction;
//JANELA
	private JFrame janela;
		public JFrame getJanela(){return janela;}
		public void buildJanela(){
			janela=new JFrame(){{
				setTitle(mind.titulo);
				setSize(618,618);
				setMinimumSize(new Dimension(250,250));
				setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				setBackground(TreeUI.FUNDO);
				setLocationRelativeTo(null);
				setLayout(new BorderLayout());
				setIconImage(mind.getImage("Icone"));
				setFont(FONTE);
				add(new Painel(this){{
					setBackground(TreeUI.FUNDO);
					buildTree(this);
					add(mind.getTree());
				}});
				addWindowListener(new WindowListener(){
					public void windowOpened(WindowEvent w){}
					public void windowIconified(WindowEvent w){}
					public void windowDeiconified(WindowEvent w){	//CHAMA A JANELA_TEXTO AO DEICONIFICAR
						if(!separateText.isPressed()&&autoFocusText.isPressed()&&showTexto.isPressed()){
							focusJanelaTexto();
							focusJanela();
						}
					}
					public void windowClosing(WindowEvent w){
						if(mind.salvarAntes())mind.fechar();
					}
					public void windowClosed(WindowEvent w){}
					public void windowActivated(WindowEvent w){}
					public void windowDeactivated(WindowEvent w){}
					private void focusJanela(){
						if(janela.isFocused())return;
						if(janelaTexto.isDragging())return;
						if(mind.getTree().getUI().getTitulo().isVisible()){
							mind.getTree().getUI().getTitulo().requestFocus();
						}else janela.requestFocus();
					}
					private void focusJanelaTexto(){
						if(janelaTexto.isFocused())return;
						if(mind.getTree().getUI().getTexto().isEnabled()){
							mind.getTree().getUI().getTexto().requestFocus();
						}else janelaTexto.requestFocus();
					}
				});
				addComponentListener(new ComponentAdapter(){
					public void componentResized(ComponentEvent r){
						if(!janela.isUndecorated())window.setSize(janela.getSize());
						mind.getTree().setSize(janela.getWidth()-janela.getInsets().left-janela.getInsets().right,
								janela.getHeight()-janela.getInsets().top-menu.getHeight()-janela.getInsets().bottom);
						mind.getTree().draw();
					}
					public void componentMoved(ComponentEvent m){
						if(!janela.isUndecorated())window.setLocation(janela.getLocation());
					}
				});
				setDropTarget(new DropTarget(){
					public void drop(DropTargetDropEvent d){
						try{
							d.acceptDrop(DnDConstants.ACTION_COPY);
							Transferable drop=d.getTransferable();
							if(drop.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
								for(File file:(List<File>)drop.getTransferData(DataFlavor.javaFileListFlavor)){
									if(file.getName().endsWith(".mind")){
										if(mind.salvarAntes())mind.abrir(file);
									}else{
										MindSortUI.mensagem(
												MindSortUI.getLang().get("M_Av1","Unrecognized file format!"),
												MindSortUI.Options.AVISO);
									}
								}
							}
						}catch(Exception erro){
							MindSortUI.mensagem(
									MindSortUI.getLang().get("M_Err1","Error: Couldn't open .mind file!")+"\n"+erro,
									MindSortUI.Options.ERRO);
						}
					}
				});
				addMouseListener(new MouseAdapter(){
					public void mousePressed(MouseEvent m){
						mousePressed=true;
					}
					public void mouseReleased(MouseEvent m){
						mousePressed=false;
					}
				});
			}};
		}
			public void buildTree(Painel painel){
				mind.setTree(new Tree(painel){{
					getUI().setFonte(FONTE);
					addObjetoListener(new ObjetoFocusListener(){
						public void objetoFocused(Objeto obj){
							if(mind.getTree().getSelectedObjetos().getModulos().size()+mind.getTree().getSelectedObjetos().getConexoes().size()!=1){
								janelaTexto.setTitle(TreeUI.getLang().get("T_G","Text"));
								return;		//APENAS UM ÚNICO MOD OU COX PODE SER EDITADO POR VEZ
							}
							if(obj.getTipo().is(Objeto.Tipo.MODULO)){
								setModTitle((Modulo)obj);
							}else if(obj.getTipo().is(Objeto.Tipo.CONEXAO)){
								setCoxTitle((Conexao)obj);
							}
						}
						public void objetoUnFocused(Objeto obj){
							if(mind.getTree().getSelectedObjetos().getModulos().size()+mind.getTree().getSelectedObjetos().getConexoes().size()!=1){
								janelaTexto.setTitle(TreeUI.getLang().get("T_G","Text"));
								return;		//APENAS UM ÚNICO MOD OU COX PODE SER EDITADO POR VEZ
							}
							if(mind.getTree().getSelectedObjetos().getModulos().size()==1){
								setModTitle(mind.getTree().getSelectedObjetos().getModulos().get(0));
							}else if(mind.getTree().getSelectedObjetos().getConexoes().size()==1){
								setCoxTitle(mind.getTree().getSelectedObjetos().getConexoes().get(0));
							}
						}
						private void setModTitle(Modulo mod){
							janelaTexto.setTitle(getPaiTitle(0,mod));
						}
						private void setCoxTitle(Conexao cox){
							final String sonTitle=(cox).getSon().getTitle().replace("\n"," ");
							final String paiTitle=(cox).getPai().getTitle().replace("\n"," ");
							janelaTexto.setTitle("["+sonTitle+"] -> ["+paiTitle+"]");
						}
						private String getPaiTitle(int nivel,Modulo mod){
							if(nivel==5)return "..";
							for(Conexao cox:mod.getConexoes()){
								if(cox.getSon()==mod)return getPaiTitle(nivel+1,cox.getPai())+"/"+mod.getTitle().replace("\n"," ");
							}
							return mod.getTitle().replace("\n"," ");
						}
					});
					getUndoRedoManager().addUndoRedoListener(new UndoRedoListener(){
						public void actionUndone(){run();}
						public void actionRedone(){run();}
						public void actionSaved(){run();}
						private void run(){
							if(!((JFrame)painel.getJanela()).getTitle().startsWith("*")){
								((JFrame)painel.getJanela()).setTitle("*"+((JFrame)painel.getJanela()).getTitle());
							}
						}
					});
					setVisible(true);
				}});
			}
	private JMenuBar menu;
	private Toggle fullscreen;
		public Toggle getFullscreenButton(){return fullscreen;}
	private Toggle separateText;
		public Toggle getSeparateTextButton(){return separateText;}
	private Toggle autoFocusText;
		public Toggle getAutoFocusTextButton(){return autoFocusText;}
	private Toggle showGrid;
		public Toggle getShowGridButton(){return showGrid;}
	private Toggle lineWrap;
		public Toggle getLineWrapButton(){return lineWrap;}
	private Toggle showAllChars;
		public Toggle getShowAllCharsButton(){return showAllChars;}
	private Toggle showTexto;
		public Toggle getShowTextoButton(){return showTexto;}
	private Toggle showNotes;
		public Toggle getShowNotesButton(){return showNotes;}
		private void updateMenu(){
			menu=new JMenuBar(){{
				final JMenuBar menu=this;
				final Cor corBorda=Cor.getChanged(ModuloUI.Cores.FUNDO,0.7f);
				setBorder(BorderFactory.createMatteBorder(0,0,1,0,corBorda));
				setBackground(TreeUI.FUNDO);
				setForeground(TreeUI.Fonte.DARK);
				setOpaque(true);
			//ARQUIVO
				add(new Menu(menu,MindSortUI.getLang().get("M_Menu_F","File")){{
				//NOVO
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_N","New")){{
						setAction(novoAction=new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								if(mind.salvarAntes()){
									mind.novo(mind.choose(
											MindSortUI.getLang().get("M_Menu_F_N","New"),
											mind.getImage("Novo"),true));
									mind.abrir(mind.getFileLink());
								}
							}
						});
						setIcon(new ImageIcon(mind.getImage("Novo")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_N,true,true);
					}});
				//ABRIR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_A","Open...")){{
						setAction(abrirAction=new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								if(mind.salvarAntes())mind.abrir(mind.choose(
										MindSortUI.getLang().get("M_Menu_F_A","Open..."),
										mind.getImage("Abrir"),true));
							}
						});
						setIcon(new ImageIcon(mind.getImage("Abrir")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_O,true,true);
					}});
				//------
					add(new JSeparator());
				//SALVAR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_S","Save")){{
						setAction(salvarAction=new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getUI().getTexto().setEnabled(false);
								if(mind.getFileLink()==null)mind.novo(mind.choose(
										MindSortUI.getLang().get("M_Menu_F_S","Save"),
										mind.getImage("Salvar"),true));
								mind.salvar(mind.getFileLink());
								mind.getTree().getUI().getTexto().setEnabled(true);
							}
						});
						setIcon(new ImageIcon(mind.getImage("Salvar")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_S,true,true);
					}});
				//SALVAR COMO
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_SC","Save As...")){{
						setAction(salvarComoAction=new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.novo(mind.choose(
										MindSortUI.getLang().get("M_Menu_F_SC","Save As..."),
										mind.getImage("Salvar Como"),true));
							}
						});
						setIcon(new ImageIcon(mind.getImage("Salvar Como")));
						setAtalho(Event.CTRL_MASK+Event.SHIFT_MASK,KeyEvent.VK_S,true,true);
					}});
				//------
					add(new JSeparator());
				//EXPORTAR COMO
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_Ex","Export...")){{
						setAction(salvarComoAction=new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.exportar(mind.choose(
										MindSortUI.getLang().get("M_Menu_F_Ex","Export..."),
										mind.getImage("Exportar"),false));
							}
						});
						setIcon(new ImageIcon(mind.getImage("Exportar")));
					}});
				//------
					add(new JSeparator());
				//SAIR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_E","Exit")){{
						setAction(sairAction=new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								if(mind.salvarAntes())mind.fechar();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Sair")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_W,true,true);
					}});
				}});
			//EDITAR
				add(new Menu(menu,MindSortUI.getLang().get("M_Menu_E","Edit")){{
				//DESFAZER
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_U","Undo")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().undo();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Desfazer")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_Z,true,true);
					}});
				//REFAZER
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_R","Redo")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().redo();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Refazer")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_Y,true,true);
					}});
				//------
					add(new JSeparator());
				//MÓDULO
					add(new Menu(menu,MindSortUI.getLang().get("M_Menu_E_Mod","Module")){{
					//EDITAR TÍTULO
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Mod_E","Edit Title")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().editTitulo();
								}
							});
							setIcon(new ImageIcon(mind.getImage("Editar Título")));
							setAtalho(0,KeyEvent.VK_F2,true,true);
						}});
					//RELACIONAR
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Mod_R","Relate")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().setModo(TreeST.TO_CONNECT);
								}
							});
							setIcon(new ImageIcon(mind.getImage("Relacionar")));
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_R,true,true);
						}});
					//------
						add(new JSeparator());
					//CRIAR RELACIONADO AOS SELECIONADOS
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Mod_CR","Create Related to Selected")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().createModRelacionado();
							}});
							setAtalho(Event.CTRL_MASK+Event.ALT_MASK,KeyEvent.VK_G,true,true);
						}});
					//RELACIONAR SELECIONADOS
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Mod_RS","Relate Selected")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().startRelation();
							}});
							setAtalho(Event.CTRL_MASK+Event.ALT_MASK,KeyEvent.VK_R,true,true);
						}});
					//EXCLUIR SELECIONADOS
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Mod_ES","Exclude Selected")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().deleteMods();
							}});
							setAtalho(Event.CTRL_MASK+Event.ALT_MASK,KeyEvent.VK_D,true,true);
						}});
					//------
						add(new JSeparator());
					//SELECIONAR SEM ANTECEDENTES
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Mod_S","Select Parentless")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().selectModSemPai();
							}});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_P,true,true);
						}});
					}});
				//CONEXÃO
					add(new Menu(menu,MindSortUI.getLang().get("M_Menu_E_Cox","Connection")){{
					//INVERTER RELAÇÃO
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Cox_I","Invert Relation")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().invertCox();
								}
							});
						}});
					}});
				//CRIAR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_G","Create")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().setModo(TreeST.TO_CREATE);
							}
						});
						setIcon(new ImageIcon(mind.getImage("Criar")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_G,true,true);
					}});
				//DELETAR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_D","Delete")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().setModo(TreeST.TO_DELETE);
							}
						});
						setIcon(new ImageIcon(mind.getImage("Deletar")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_D,true,true);
					}});
				//MOVER
					add(new Menu(menu,MindSortUI.getLang().get("M_Menu_E_M","Move")){{
					//MOVER PARA CIMA
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_M_C","Move Up")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().moveUp(false);
								}
							});
							setAtalho(0,KeyEvent.VK_UP,true,true);
						}});
					//MOVER PARA DIREITA
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_M_D","Move Right")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().moveRight(false);
								}
							});
							setAtalho(0,KeyEvent.VK_RIGHT,true,true);
						}});
					//MOVER PARA ESQUERDA
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_M_E","Move Left")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().moveLeft(false);
								}
							});
							setAtalho(0,KeyEvent.VK_LEFT,true,true);
						}});
					//MOVER PARA BAIXO
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_M_B","Move Down")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().moveDown(false);
								}
							});
							setAtalho(0,KeyEvent.VK_DOWN,true,true);
						}});
					//------
						add(new JSeparator());
					//MOVER BASTANTE PARA CIMA
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_M_BC","Move Far Up")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().moveUp(true);
								}
							});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_UP,true,true);
						}});
					//MOVER BASTANTE PARA DIREITA
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_M_BD","Move Far Right")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().moveRight(true);
								}
							});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_RIGHT,true,true);
						}});
					//MOVER BASTANTE PARA ESQUERDA
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_M_BE","Move Far Left")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().moveLeft(true);
								}
							});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_LEFT,true,true);
						}});
					//MOVER BASTANTE PARA BAIXO
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_M_BB","Move Far Down")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().moveDown(true);
								}
							});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_DOWN,true,true);
						}});
					}});
				//------
					add(new JSeparator());
				//RECORTAR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Rec","Cut")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().cut();
						}});
						setIcon(new ImageIcon(mind.getImage("Recortar")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_X,true,true);
					}});
				//COPIAR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Cop","Copy")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().copy();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Copiar")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_C,true,true);
					}});
				//COPIAR COMO IMAGEM
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_CopImg","Copy As Image")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().copyAsImg();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Copiar Como Imagem")));
						setAtalho(Event.CTRL_MASK+Event.SHIFT_MASK,KeyEvent.VK_C,true,true);
					}});
				//COLAR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Col","Paste")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().paste();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Colar")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_V,true,true);
					}});
				//EXCLUIR
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_Exc","Exclude")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().delete();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Excluir")));
						setAtalho(0,KeyEvent.VK_DELETE,true,true);
					}});
				//------
					add(new JSeparator());
				//SELECIONAR TUDO
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_ST","Select All")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().selectAll();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Selecionar Tudo")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_A,true,true);
					}});
				//DESELECIONAR TUDO
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_DT","UnSelect All")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().unSelectAll();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Deselecionar Tudo")));
						setAtalho(0,KeyEvent.VK_ESCAPE,true,true);
					}});
				//SELECIONAR DESCENDENTES
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_SD","Select Descendants")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().selectSons();
						}});
						setAtalho(KeyEvent.SHIFT_DOWN_MASK,KeyEvent.VK_SHIFT,false,true);
					}});
				//INVERTER SELEÇÃO
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_E_IS","Invert Selection")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().invertSelection();
						}});
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_I,true,true);
					}});
				}});
			//EXIBIR
				add(new Menu(menu,MindSortUI.getLang().get("M_Menu_Ex","Show")){{
				//TELA CHEIA
					add(fullscreen=new Toggle(menu,MindSortUI.getLang().get("M_Menu_Ex_T","Fullscreen")){{
						setAction(new Runnable(){
							private boolean isToSeparateText=false;
							public void run(){
								fullscreen(fullscreen.isPressed());
								if(fullscreen.isPressed()){
									isToSeparateText=separateText.isPressed();
									separateText.doToggle(true);
								}else{
									separateText.doToggle(isToSeparateText);
								}
								separateText.setEnabled(!fullscreen.isPressed());
							}
						});
						setIcon(new ImageIcon(mind.getImage("Tela Cheia")));
						setAtalho(0,KeyEvent.VK_F11,true,true);
					}});
				//ZOOM
					add(new Menu(menu,MindSortUI.getLang().get("M_Menu_Ex_Z","Zoom")){{
					//AUMENTAR
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_Ex_Z_A","Increase")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().zoom(1);
								}
							});
							setIcon(new ImageIcon(mind.getImage("Aumentar")));
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_EQUALS,true,true);
						}});
					//DIMINUIR
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_Ex_Z_D","Decrease")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().zoom(-1);
								}
							});
							setIcon(new ImageIcon(mind.getImage("Diminuir")));
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_MINUS,true,true);
						}});
						add(new Menu(menu,MindSortUI.getLang().get("M_Menu_Ex_Z_O","Options")){{
							add(new Botao(menu,MindSortUI.getLang().get("M_Menu_Ex_Z_O_1","Zoom x1")){{
								setAction(new AbstractAction(){
									public void actionPerformed(ActionEvent a){
										mind.getTree().getActions().zoom(8-Tree.UNIT);
									}
								});
								setAtalho(Event.CTRL_MASK,KeyEvent.VK_1,true,true);
							}});
							add(new Botao(menu,MindSortUI.getLang().get("M_Menu_Ex_Z_O_2","Zoom x2")){{
								setAction(new AbstractAction(){
									public void actionPerformed(ActionEvent a){
										mind.getTree().getActions().zoom(16-Tree.UNIT);
									}
								});
								setAtalho(Event.CTRL_MASK,KeyEvent.VK_2,true,true);
							}});
							add(new Botao(menu,MindSortUI.getLang().get("M_Menu_Ex_Z_O_3","Zoom x3")){{
								setAction(new AbstractAction(){
									public void actionPerformed(ActionEvent a){
										mind.getTree().getActions().zoom(24-Tree.UNIT);
									}
								});
								setAtalho(Event.CTRL_MASK,KeyEvent.VK_3,true,true);
							}});
						}});
					//------
						add(new JSeparator());
					//RESTAURAR ZOOM
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_Ex_Z_R","Restore to Default")){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									mind.getTree().getActions().zoom(8-Tree.UNIT);
								}
							});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_0,true,true);
						}});
					}});
				//------
					add(new JSeparator());
				//CENTRALIZAR CÂMERA
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_Ex_C","Center Camera")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								mind.getTree().getActions().centralizar();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Centralizar Câmera")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_E,true,true);
					}});
				//------
					add(new JSeparator());
				//QUEBRA DE LINHA
					add(lineWrap=new Toggle(menu,MindSortUI.getLang().get("M_Menu_Ex_Q","Automatic Line Break")){{
						setAction(new Runnable(){
							public void run(){
								mind.getTree().getUI().getTexto().setLineWrap(lineWrap.isPressed());
								notesTexto.setLineWrap(lineWrap.isPressed());
							}
						});
						setIcon(new ImageIcon(mind.getImage("Quebra Automática de Linha")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_Q,true,true);
					}});
				//MOSTRAR CARACTERES
					add(showAllChars=new Toggle(menu,MindSortUI.getLang().get("M_Menu_Ex_M","Show Hidden Characters")){{
						setAction(new Runnable(){
							public void run(){
								mind.getTree().getUI().getTexto().setViewAllChars(showAllChars.isPressed());
								notesTexto.setViewAllChars(showAllChars.isPressed());
							}
						});
						setIcon(new ImageIcon(mind.getImage("Mostrar Caracteres Escondidos")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_M,true,true);
					}});
				//SEPARAR JANELA DE TEXTO
					add(separateText=new Toggle(menu,MindSortUI.getLang().get("M_Menu_Ex_S","Separate Text Window")){{
						setAction(new Runnable(){
							public void run(){
								janelaTexto.setLocked(!separateText.isPressed());
								janelaTexto.requestFocus();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Separar Janela de Texto")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_L,true,true);
					}});
				//AUTO-FOCAR JANELA DE TEXTO
					add(autoFocusText=new Toggle(menu,MindSortUI.getLang().get("M_Menu_Ex_A","Auto-Focus Text Window")){{
						setAction(new Runnable(){	//FAZ ATIVAR O TOGGLE
							public void run(){}
						});
						setIcon(new ImageIcon(mind.getImage("Auto-Focar Janela de Texto")));
					}});
				//------
					add(new JSeparator());
				//MOSTRAR GRADE
					add(showGrid=new Toggle(menu,MindSortUI.getLang().get("M_Menu_Ex_MG","Show Grid")){{
						setAction(new Runnable(){
							public void run(){
								mind.getTree().setShowGrid(showGrid.isPressed());
								mind.getTree().draw();
							}
						});
						setIcon(new ImageIcon(mind.getImage("Mostrar Grade")));
					}});
				}});
			//CONFIGURAR
				add(new Menu(menu,MindSortUI.getLang().get("M_Menu_C","Configuration")){{
				//FONTE
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_C_F","Font...")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								new FontChooser(){{
									setSelectedFont(TreeUI.getFonte());
									if(showDialog(janela)==FontChooser.Option.APPROVE_OPTION){
										setTreeFont(getSelectedFont());
									}
								}};
							}
						});
					}});
				//------
					add(new JSeparator());
				//TRANSPARÊNCIA
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_C_T","Transparency...")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								final Object[]transNvlOpcoes=new Object[]{
										MindSortUI.getLang().get("M_Menu_C_T_I","Invisible"),
										"10%","25%","40%","50%","60%","75%","90%",
										MindSortUI.getLang().get("M_Menu_C_T_O","Opaque")};
								final int index=getValorIndex();
								final int transparencia=getChoosenValor(transNvlOpcoes,index);
								janelaTexto.getTransparentInstance().setTransparencia(transparencia);
							}
							private int getValorIndex(){
								switch(janelaTexto.getTransparentInstance().getTransparencia()){
									case 0:default:	return 0;
									case 10:		return 1;
									case 25:		return 2;
									case 40:		return 3;
									case 50:		return 4;
									case 60:		return 5;
									case 75:		return 6;
									case 90:		return 7;
									case 100:		return 8;
								}
							}
							private int getChoosenValor(Object[]opcoes,int index){
								final Object opcao=JOptionPane.showInputDialog(null,
										MindSortUI.getLang().get("M_Menu_C_T_Ti","Text window transparency level"),
										MindSortUI.getLang().get("M_Menu_C_T_Tx","Transparency Level"),
										JOptionPane.QUESTION_MESSAGE,null,opcoes,opcoes[index]);
								if(opcao==null)return index;		//CANCELADO
								for(int i=0;i<opcoes.length;i++){
									if(((String)opcao).equals((String)opcoes[i])){
										index=i;
										break;
									}
								}
								switch(index){
									case 0:		return 0;
									case 1:		return 10;
									case 2:		return 25;
									case 3:		return 40;
									case 4:		return 50;
									case 5:		return 60;
									case 6:		return 75;
									case 7:		return 90;
									case 8:		return 100;
								}
								return index;
							}
						});
					}});
				//------
					add(new JSeparator());
				//LIMITE DE OBJETOS
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_C_LO","Object Limit...")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								final Object[]objsLimOpcoes=new Object[]{
										MindSortUI.getLang().get("M_Menu_C_LO_R","Restricted"),
										50,100,200,300,500,1000,
										MindSortUI.getLang().get("M_Menu_C_LO_S","No Restrictions")};
								final int index=getValorIndex(objsLimOpcoes);
								final int limite=getChoosenValor(objsLimOpcoes,index);
								mind.getTree().setObjetosLimite(limite);
							}
							private int getValorIndex(Object[]opcoes){
								switch(mind.getTree().getObjetosLimite()){
									case 0:		return 0;
									case -1:	return opcoes.length-1;
									default:
										for(int i=1;i<opcoes.length-1;i++){
											if((Integer)opcoes[i]==mind.getTree().getObjetosLimite())return i;
										}
										return opcoes.length-1;
								}
							}
							private int getChoosenValor(Object[]opcoes,int index){
								final Object opcao=JOptionPane.showInputDialog(null,
										MindSortUI.getLang().get("M_Menu_C_LO_Ti","Limit of objects on screen before decreasing graphic quality"), 
										MindSortUI.getLang().get("M_Menu_C_LO_Tx","Limit of objects"),
										JOptionPane.QUESTION_MESSAGE,null,opcoes,opcoes[index]);
								if(opcao instanceof Integer){
									return (Integer)opcao;
								}else if(opcao instanceof String){
									return (((String)opcao).equals((String)opcoes[0])?0:-1);
								}else return -1;
							}
						});
					}});
				//LIMITE DE DESFAZER/REFAZER
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_C_LDR","Undo/Redo Limit...")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								final Object[]doLimOpcoes=new Object[]{
										MindSortUI.getLang().get("M_Menu_C_LDR_D","Disabled"),
										50,100,200,300,500,1000,
										MindSortUI.getLang().get("M_Menu_C_LDR_S","No Restrictions")};
								final int index=getValorIndex(doLimOpcoes);
								final int limite=getChoosenValor(doLimOpcoes,index);
								mind.getTree().getUndoRedoManager().setDoLimite(limite);
							}
							private int getValorIndex(Object[]opcoes){
								switch(mind.getTree().getUndoRedoManager().getDoLimite()){
									case 0:		return 0;
									case -1:	return opcoes.length-1;
									default:
										for(int i=1;i<opcoes.length-1;i++){
											if((Integer)opcoes[i]==mind.getTree().getUndoRedoManager().getDoLimite())return i;
										}
									return opcoes.length-1;
								}
							}
							private int getChoosenValor(Object[]opcoes,int index){
								final Object opcao=JOptionPane.showInputDialog(null,
										MindSortUI.getLang().get("M_Menu_C_LDR_Ti","Stored undo and redo limit"), 
										MindSortUI.getLang().get("M_Menu_C_LDR_Tx","Undo/Redo Limit"),
										JOptionPane.QUESTION_MESSAGE,null,opcoes,opcoes[index]);
								if(opcao instanceof Integer){
									return (Integer)opcao;
								}else if(opcao instanceof String){
									return (((String)opcao).equals((String)opcoes[0])?0:-1);
								}else return -1;
							}
						});
					}});
				//------
					add(new JSeparator());
				//SALVAR CONFIGURAÇÕES
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_C_S","Save Configurations")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								if((JOptionPane.showConfirmDialog(null,
										new JLabel("<html>Deseja <font color='blue'>SALVAR</font> a configuração atual?<br>")
										,"Salvar .ini",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)){
									final boolean success=mind.setConfigIni();
									if(success){
										MindSortUI.mensagem(
												MindSortUI.getLang().get("M_Av2","Configuration saved!"),
												Options.AVISO);
									}
								}
							}
						});
					}});
				//RESTAURAR CONFIGURAÇÕES
					add(new Botao(menu,MindSortUI.getLang().get("M_Menu_C_R","Restore to Default")){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								if((JOptionPane.showConfirmDialog(null,
										new JLabel("<html>Deseja <font color='red'>DELETAR</font> a configuração atual?<br>")
										,"Deletar .ini",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)){
									final File link=new File(System.getProperty("user.dir")+"/"+mind.ini);
									if(link.exists()){
										link.delete();
										final boolean success=mind.getIniConfig();
										if(success){
											MindSortUI.mensagem(
													MindSortUI.getLang().get("M_Av3","Configuration saved!"),
													Options.AVISO);
										}
									}else{
										MindSortUI.mensagem(
												MindSortUI.getLang().get("M_Av4","The .ini file was not found!"),
												Options.AVISO);
									}
								}
							}
						});
					}});
				}});
			//PESQUISAR
				add(new Botao(menu,MindSortUI.getLang().get("M_Menu_P","Search")){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							searcher.getUI().updateInterface();
							searcher.chamar();
						}
					});
					setIcon(new ImageIcon(mind.getImage("Pesquisar")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_F,false,false);
					setMaximumSize(new Dimension(getPreferredSize().width,100));
				}});
			//TEXTO
				add(showTexto=new Toggle(menu,MindSortUI.getLang().get("M_Menu_T","Text")){{
					setAction(new Runnable(){
						public void run(){
							janelaTexto.show(showTexto.isPressed());
						}
					});
					setIcon(new ImageIcon(mind.getImage("Texto")));
					setMaximumSize(new Dimension(getPreferredSize().width,100));
				}});
			//ANOTAÇÕES
				add(showNotes=new Toggle(menu,MindSortUI.getLang().get("M_Menu_A","Notes")){{
					setAction(new Runnable(){
						public void run(){
							if(showNotes.isPressed()){
								notesTexto.setFont(TreeUI.getFonte());
								janelaNotes.setVisible(true);
							}else janelaNotes.dispose();
						}
					});
					setIcon(new ImageIcon(mind.getImage("Anotações")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_T,false,false);
					setMaximumSize(new Dimension(getPreferredSize().width,100));
				}});
			}};
			janela.setJMenuBar(menu);
		}
//JANELA DO TEXTO
	private boolean mousePressed=false;
	private Rectangle window=new Rectangle();
	private Janela janelaTexto;
		public Janela getJanelaTexto(){return janelaTexto;}
		public void buildJanelaTexto(){
			janelaTexto=new Janela(janela){{
				setMinimumSize(new Dimension(180,180));
				setBounds(janela.getX(),janela.getY()+janela.getHeight()-200,janela.getWidth(),200);
				setAlwaysOnTop(true);
				setIconImage(mind.getImage("Icone"));
				add(new JScrollPane(){{
					setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
					setBackground(Cor.WHITE);
					setViewportView(mind.getTree().getUI().getTexto());
					setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					removeMouseWheelListener(getMouseWheelListeners()[0]);
					addMouseWheelListener(new MouseWheelListener(){
						public void mouseWheelMoved(MouseWheelEvent w){
							final int modifier=w.getModifiersEx(),ctrl=MouseEvent.CTRL_DOWN_MASK,right=4352;
							if(modifier==ctrl||modifier==right){
								getHorizontalScrollBar().setValue(getHorizontalScrollBar().getValue()+w.getWheelRotation()*40);//DIREITA-ESQUERDA
							}else getVerticalScrollBar().setValue(getVerticalScrollBar().getValue()+w.getWheelRotation()*40);//CIMA-BAIXO
						}
					});
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}});
				addWindowListenerOnClosed(new Runnable(){
					public void run(){
						showTexto.setToggle(false);
					}
				});
				new Thread(){
					public void run(){
						Dimension size=janelaTexto.getBounds().getSize();
						while(true){
							try{
								Thread.sleep(100);
							}catch(InterruptedException erro){}
							if(mousePressed)continue;
							if(autoFocusText==null)continue;
							if(!autoFocusText.isPressed()||(autoFocusText.isPressed()&&!size.equals(janelaTexto.getBounds().getSize()))){
								size=janelaTexto.getBounds().getSize();
								continue;
							}
							if(!janela.isFocused()&&!janelaTexto.isFocused())continue;
							if(janelaTexto.isLocked()){
								if(isJanelaTextoHover()){
									focusJanelaTexto();
								}else if(isJanelaHover())focusJanela();
							}else{
								if(janela.isFocused()){
									if(isJanelaTextoHover()&&!isJanelaHover())focusJanelaTexto();
								}else if(janelaTexto.isFocused()){
									if(isJanelaHover()&&!isJanelaTextoHover())focusJanela();
								}
							}
						}
					}
					private boolean isJanelaHover(){return janela.getBounds().contains(MouseInfo.getPointerInfo().getLocation());}
					private void focusJanela(){
						if(janela.isFocused())return;
						if(janelaTexto.isDragging())return;
						if(mind.getTree().getUI().getTitulo().isVisible()){
							mind.getTree().getUI().getTitulo().requestFocus();
						}else janela.requestFocus();
					}
					private boolean isJanelaTextoHover(){return janelaTexto.getBounds().contains(MouseInfo.getPointerInfo().getLocation());}
					private void focusJanelaTexto(){
						if(janelaTexto.isFocused())return;
						if(mind.getTree().getUI().getTexto().isEnabled()){
							mind.getTree().getUI().getTexto().requestFocus();
						}else janelaTexto.requestFocus();
					}
				}.start();
			}};
		}
			private void updateJanelaTextoMenu(){
				janelaTexto.setMenu(new JMenuBar(){{
					final JMenuBar menu=this;
					final Cor corBorda=Cor.getChanged(ModuloUI.Cores.FUNDO,0.7f);
					setBorder(BorderFactory.createMatteBorder(0,0,1,0,corBorda));
					setBackground(Cor.WHITE);
					setForeground(TreeUI.Fonte.DARK);
				//ARQUIVO
					add(new Menu(menu,MindSortUI.getLang().get("M_Menu_F","File")){{
					//NOVO
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_N","New")){{
							setAction(novoAction);
							setIcon(new ImageIcon(mind.getImage("Novo")));
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_N,true,true);
						}});
					//ABRIR
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_A","Open...")){{
							setAction(abrirAction);
							setIcon(new ImageIcon(mind.getImage("Abrir")));
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_O,true,true);
						}});
						add(new JSeparator());
					//SALVAR
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_S","Save")){{
							setAction(salvarAction);
							setIcon(new ImageIcon(mind.getImage("Salvar")));
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_S,true,true);
						}});
					//SALVAR COMO
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_SC","Save As...")){{
							setAction(salvarComoAction);
							setIcon(new ImageIcon(mind.getImage("Salvar Como")));
							setAtalho(Event.CTRL_MASK+Event.SHIFT_MASK,KeyEvent.VK_S,true,true);
						}});
						add(new JSeparator());
					//SAIR
						add(new Botao(menu,MindSortUI.getLang().get("M_Menu_F_E","Exit")){{
							setAction(sairAction);
							setIcon(new ImageIcon(mind.getImage("Sair")));
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_W,true,true);
						}});
					}});
				}});
			}
//JANELA DAS ANOTAÇÕES
	private Texto notesTexto;
	private JFrame janelaNotes;
		private void buildJanelaNotes(){
			notesTexto=new Texto();
			notesTexto.setFont(TreeUI.getFonte());
			notesTexto.setForeground(TreeUI.Fonte.DARK);
			notesTexto.setLineWrappable(true);
			janelaNotes=new JFrame(){{
				setMinimumSize(new Dimension(180,180));
				setBackground(Cor.WHITE);
				setBounds(janela.getX(),janela.getY(),350,300);
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				setAlwaysOnTop(true);
				setIconImage(mind.getImage("Icone"));
				add(new JScrollPane(){{
					setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
					setBackground(Cor.WHITE);
					setViewportView(notesTexto);
					setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					removeMouseWheelListener(getMouseWheelListeners()[0]);
					addMouseWheelListener(new MouseWheelListener(){
						public void mouseWheelMoved(MouseWheelEvent w){
							final int modifier=w.getModifiersEx(),ctrl=MouseEvent.CTRL_DOWN_MASK,right=4352;
							if(modifier==ctrl||modifier==right){
								getHorizontalScrollBar().setValue(getHorizontalScrollBar().getValue()+w.getWheelRotation()*40);//DIREITA-ESQUERDA
							}else getVerticalScrollBar().setValue(getVerticalScrollBar().getValue()+w.getWheelRotation()*40);//CIMA-BAIXO
						}
					});
				}});
				addWindowListener(new WindowListener(){
					public void windowOpened(WindowEvent w){}
					public void windowIconified(WindowEvent w){}
					public void windowDeiconified(WindowEvent w){}
					public void windowClosing(WindowEvent w){}
					public void windowClosed(WindowEvent w){
						showNotes.setToggle(false);
					}
					public void windowDeactivated(WindowEvent w){
						notesTexto.setEnabled(false);
					}
					public void windowActivated(WindowEvent w){
						notesTexto.setEnabled(true);
					}
				});
			}};
		}
//SEARCHER
	private Searcher searcher;
		public void buildSeacher(){
			searcher=new Searcher(mind.getTree());
		}
//MINDSORT
	private MindSort mind;
//MAIN
	public MindSortUI(MindSort mind){this.mind=mind;}
	public void build(){
		buildJanela();
		buildJanelaTexto();
		buildJanelaNotes();
		buildSeacher();
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			final Font fonteMenu=new Font(FONTE.getName(),Font.PLAIN,FONTE.getSize());
			final Cor corBorda=Cor.getChanged(ModuloUI.Cores.FUNDO,0.7f);
			final Cor corSelec=new Cor(144,200,246);
			UIManager.put("Menu.font",fonteMenu);
			UIManager.put("MenuItem.font",fonteMenu);
			UIManager.put("Menu.selectionBackground",corSelec);
			UIManager.put("MenuItem.selectionBackground",corSelec);
			UIManager.put("PopupMenu.border",BorderFactory.createLineBorder(corBorda));
			UIManager.put("Separator.foreground",corBorda);
			UIManager.getLookAndFeelDefaults().put("MenuItem.acceleratorFont",FONTE);
			UIManager.getLookAndFeelDefaults().put("MenuItem.acceleratorForeground",corBorda);
		}catch(ClassNotFoundException|InstantiationException|IllegalAccessException|UnsupportedLookAndFeelException erro){
			MindSortUI.mensagem(
					MindSortUI.getLang().get("M_Err2","Error: Style couldn't be configured!"),
					MindSortUI.Options.AVISO);
		}
		final String lang=Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry();
//		final String lang="EN-US";
		mind.updateLanguageFolder(lang);	//CARREGA IDIOMA, SE EXISTE 
		MindSortUI.getLang().setLanguage(lang);
		TreeUI.getLang().setLanguage(lang);
		updateLang();
		mind.updateIconFolder();
		mind.getIniConfig();
		mind.getTree().clear();	//ATUALIZA O IDIOMA DOS MODS
		getFullscreenButton().doToggle(getFullscreenButton().isPressed());
		getSeparateTextButton().doToggle(getSeparateTextButton().isPressed());
		getAutoFocusTextButton().doToggle(getAutoFocusTextButton().isPressed());
		getShowGridButton().doToggle(getShowGridButton().isPressed());
		getLineWrapButton().doToggle(getLineWrapButton().isPressed());
		getShowAllCharsButton().doToggle(getShowAllCharsButton().isPressed());
		getShowTextoButton().doToggle(true);
		getShowNotesButton().doToggle(false);
		mind.getTree().getActions().setFocusOn(new Objeto[]{Tree.getMestre()});
		janela.requestFocus();
	}
//FUNCS
	public void setTreeFont(Font fonte){
		mind.getTree().getUI().setFonte(fonte);
		janelaTexto.setFont(fonte);
		notesTexto.setFont(fonte);
	}
	private void updateLang(){
		updateMenu();
		janelaTexto.setTitle(MindSortUI.getLang().get("M_Tx","Text"));
		updateJanelaTextoMenu();
		janelaTexto.updateLang();
		janelaNotes.setTitle(MindSortUI.getLang().get("M_AT","Temporary Notes"));
		JColorChooser.setDefaultLocale(Locale.getDefault());
	}
//FULLSCREEN
	public void fullscreen(boolean fullscreen){
		janela.dispose();
		janela.setAlwaysOnTop(fullscreen);
		if(fullscreen){
			janela.setUndecorated(true);			//DEVE OCORRER ANTES DE RETIRAR O FUNDO
			janela.setBackground(Cor.TRANSPARENTE);	//A JANELA DEVE SER DESBORDADA
			final Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
			janela.setBounds(0,0,screenSize.width,screenSize.height);
		}else{
			janela.setBackground(TreeUI.FUNDO);		//DEVE OCORRER ANTES DE BORDAR
			janela.setUndecorated(false);			//O FUNDO DEVE SER OPACO
			janela.setBounds(window);
		}
		final int width=janela.getWidth()-janela.getInsets().left-janela.getInsets().right;
		final int height=janela.getHeight()-janela.getInsets().top-janela.getInsets().bottom;
		mind.getTree().setBounds(0,janela.getInsets().top,width,height);
		mind.getTree().draw();
		janela.setVisible(true);
		janela.requestFocus();
	}
//MENSAGEM
	public enum Options{
		ERRO,
		AVISO;
	}
	public static void mensagem(String mensagem,Options tipo){
		Toolkit.getDefaultToolkit().beep();
		switch(tipo){
			case AVISO:	JOptionPane.showMessageDialog(null,mensagem,MindSortUI.getLang().get("M_Av","Warning!"),JOptionPane.WARNING_MESSAGE);break;
			case ERRO:	JOptionPane.showMessageDialog(null,mensagem,MindSortUI.getLang().get("M_Err","Error...!"),JOptionPane.ERROR_MESSAGE);break;
		}
	}
}