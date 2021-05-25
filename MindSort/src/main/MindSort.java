package main;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import element.tree.popup.color.CorPick;
import element.tree.texto.Texto;
import element.tree.undoRedo.UndoRedoListener;
import main.janela.Janela;
import main.search.Searcher;
import menu.Botao;
import menu.Menu;
import menu.Toggle;
import element.Painel;
import element.tree.objeto.Objeto;
import element.tree.objeto.conexao.Conexao;
import element.tree.objeto.modulo.Modulo;
import element.tree.Actions;
import element.tree.Cor;
import element.tree.ObjetoFocusListener;
import element.tree.Tree;
@SuppressWarnings({"serial","unchecked"})
public class MindSort{
//MAIN(EXECUTE)
	public static void main(String[]args){new MindSort(args);}
//LINK
	private File link=null;
	private String ini="configMindSort.ini";
//VAR GLOBAIS
	private String titulo="MindSort";
	private JMenuBar menu;
	private Tree tree;
	private Toggle fullscreen;
	private Toggle separateText;
	private Toggle autoFocusText;
	private Toggle showGrid;
	private Toggle lineWrap;
	private Toggle showAllChars;
	public static Cor MENU=new Cor(220,220,220);
//FONTE
	private static final Font FONTE=new Font("Segoe UI Emoji",Font.PLAIN,12);
//LISTAS
	private Object[]objsLimOpcoes={"Restrito",50,100,200,300,500,1000,"Sem restrição"};
	private Object[]doLimOpcoes={"Desativado",50,100,200,300,500,1000,"Sem restrição"};
	private Object[]transNvlOpcoes={"Invisível","10%","25%","40%","50%","60%","75%","90%","Opaco"};
//BOTÕES
	private AbstractAction novoAction;
	private AbstractAction abrirAction;
	private AbstractAction salvarAction;
	private AbstractAction salvarComoAction;
	private AbstractAction sairAction;
//JANELA
	private JFrame janela=new JFrame(){{
		setTitle(titulo);
		setSize(618,618);
		setMinimumSize(new Dimension(250,250));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBackground(Tree.FUNDO);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setIconImage(getImage("Icone"));
		setFont(FONTE);
		setJMenuBar(menu=new JMenuBar(){{
			JMenuBar menu=this;
			final Cor corBorda=Cor.getChanged(Modulo.Cores.FUNDO,0.7f);
			setBorder(BorderFactory.createMatteBorder(0,0,1,0,corBorda));
			setPreferredSize(new Dimension(getWidth(),20));
			setBackground(MindSort.MENU);
			setForeground(Tree.Fonte.DARK);
			final Font fonteMenu=new Font(FONTE.getName(),Font.BOLD,FONTE.getSize());
			UIManager.put("Menu.font",fonteMenu);
			UIManager.put("MenuItem.font",fonteMenu);
			UIManager.put("Menu.selectionBackground",Cor.getChanged(MindSort.MENU,1.1f));
			UIManager.put("MenuItem.selectionBackground",Cor.getChanged(MindSort.MENU,1.1f));
			UIManager.put("PopupMenu.border",BorderFactory.createLineBorder(corBorda));
			UIManager.put("Separator.foreground",corBorda);
			UIManager.getLookAndFeelDefaults().put("MenuItem.acceleratorForeground",corBorda);
			UIManager.getLookAndFeelDefaults().put("MenuItem.acceleratorFont",FONTE);
		//ARQUIVO
			add(new Menu(menu,"Arquivo"){{
			//NOVO
				add(new Botao(menu,"Novo"){{
					setAction(novoAction=new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							if(salvarAntes()){
								novo(choose("Novo"));
								abrir(link);
							}
						}
					});
					setIcon(new ImageIcon(getImage("Novo")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_N,true,true);
				}});
			//ABRIR
				add(new Botao(menu,"Abrir..."){{
					setAction(abrirAction=new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							if(salvarAntes())abrir(choose("Abrir"));
						}
					});
					setIcon(new ImageIcon(getImage("Abrir")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_O,true,true);
				}});
				add(new JSeparator());
			//SALVAR
				add(new Botao(menu,"Salvar"){{
					setAction(salvarAction=new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getTexto().setEnabled(false);
							if(link==null)novo(choose("Salvar"));
							salvar(link);
							tree.getTexto().setEnabled(true);
						}
					});
					setIcon(new ImageIcon(getImage("Salvar")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_S,true,true);
				}});
			//SALVAR COMO
				add(new Botao(menu,"Salvar Como..."){{
					setAction(salvarComoAction=new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							novo(choose("Salvar Como"));
						}
					});
					setIcon(new ImageIcon(getImage("Salvar Como")));
					setAtalho(Event.CTRL_MASK+Event.SHIFT_MASK,KeyEvent.VK_S,true,true);
				}});
				add(new JSeparator());
			//SAIR
				add(new Botao(menu,"Sair"){{
					setAction(sairAction=new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							if(salvarAntes())fechar();
						}
					});
					setIcon(new ImageIcon(getImage("Sair")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_W,true,true);
				}});
			}});
		//EDITAR
			add(new Menu(menu,"Editar"){{
			//DESFAZER
				add(new Botao(menu,"Desfazer"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().undo();
						}
					});
					setIcon(new ImageIcon(getImage("Desfazer")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_Z,true,true);
				}});
			//REFAZER
				add(new Botao(menu,"Refazer"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().redo();
						}
					});
					setIcon(new ImageIcon(getImage("Refazer")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_Y,true,true);
				}});
				add(new JSeparator());
			//MÓDULO
				add(new Menu(menu,"Módulo"){{
				//EDITAR TÍTULO
					add(new Botao(menu,"Editar Título"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().editTitulo();
							}
						});
						setIcon(new ImageIcon(getImage("Editar Título")));
						setAtalho(0,KeyEvent.VK_F2,true,true);
					}});
				//RELACIONAR
					add(new Botao(menu,"Relacionar"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().setModo(Actions.TO_CONNECT);
							}
						});
						setIcon(new ImageIcon(getImage("Relacionar")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_R,true,true);
					}});
					add(new JSeparator());
				//CRIAR RELACIONADO AOS SELECIONADOS
					add(new Botao(menu,"Criar Relacionado aos Selecionados"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().createModRelacionado();
						}});
						setAtalho(Event.CTRL_MASK+Event.ALT_MASK,KeyEvent.VK_G,true,true);
					}});
				//RELACIONAR SELECIONADOS
					add(new Botao(menu,"Relacionar Selecionados"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().startRelation();
						}});
						setAtalho(Event.CTRL_MASK+Event.ALT_MASK,KeyEvent.VK_R,true,true);
					}});
				//EXCLUIR SELECIONADOS
					add(new Botao(menu,"Excluir Selecionados"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().deleteMods();
						}});
						setAtalho(Event.CTRL_MASK+Event.ALT_MASK,KeyEvent.VK_D,true,true);
					}});
					add(new JSeparator());
				//SELECIONAR SEM ANTECEDENTES
					add(new Botao(menu,"Selecionar Sem Antecedentes"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().selectModSemPai();
						}});
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_P,true,true);
					}});
				}});
			//CONEXÃO
				add(new Menu(menu,"Conexão"){{
				//EDITAR TÍTULO
					add(new Botao(menu,"Inverter Relação"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().invertCox();
							}
						});
					}});
				}});
			//CRIAR
				add(new Botao(menu,"Criar"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().setModo(Actions.TO_CREATE);
						}
					});
					setIcon(new ImageIcon(getImage("Criar")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_G,true,true);
				}});
			//DELETAR
				add(new Botao(menu,"Deletar"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().setModo(Actions.TO_DELETE);
						}
					});
					setIcon(new ImageIcon(getImage("Deletar")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_D,true,true);
				}});
				add(new JSeparator());
			//RECORTAR
				add(new Botao(menu,"Recortar"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().cut();
					}});
					setIcon(new ImageIcon(getImage("Recortar")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_X,true,true);
				}});
			//COPIAR
				add(new Botao(menu,"Copiar"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().copy();
						}
					});
					setIcon(new ImageIcon(getImage("Copiar")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_C,true,true);
				}});
			//COLAR
				add(new Botao(menu,"Colar"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().paste();
						}
					});
					setIcon(new ImageIcon(getImage("Colar")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_V,true,true);
				}});
			//EXCLUIR
				add(new Botao(menu,"Excluir"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().delete();
						}
					});
					setIcon(new ImageIcon(getImage("Excluir")));
					setAtalho(0,KeyEvent.VK_DELETE,true,true);
				}});
				add(new JSeparator());
			//SELECIONAR TUDO
				add(new Botao(menu,"Selecionar Tudo"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().selectAll();
						}
					});
					setIcon(new ImageIcon(getImage("Selecionar Tudo")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_A,true,true);
				}});
			//DESELECIONAR TUDO
				add(new Botao(menu,"Deselecionar Tudo"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().unSelectAll();
						}
					});
					setIcon(new ImageIcon(getImage("Deselecionar Tudo")));
					setAtalho(0,KeyEvent.VK_ESCAPE,true,true);
				}});
			//SELECIONAR DESCENDENTES
				add(new Botao(menu,"Selecionar Descendentes"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().selectSons();
					}});
					setAtalho(KeyEvent.SHIFT_DOWN_MASK,KeyEvent.VK_SHIFT,false,true);
				}});
			//INVERTER SELEÇÃO
				add(new Botao(menu,"Inverter Seleção"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().invertSelection();
					}});
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_I,true,true);
				}});
			}});
		//EXIBIR
			add(new Menu(menu,"Exibir"){{
			//TELA CHEIA
				add(fullscreen=new Toggle(menu,"Tela Cheia"){{
					setAction(new Runnable(){
						private boolean isToSeparateText=false;
						public void run(){
							fullscreen(fullscreen.isPressed());
							if(fullscreen.isPressed()){
								isToSeparateText=separateText.isPressed();
								separateText.setToggle(true);
							}else{
								separateText.setToggle(isToSeparateText);
							}
							separateText.setEnabled(!fullscreen.isPressed());
						}
					});
					setIcon(new ImageIcon(getImage("Tela Cheia")));
					setAtalho(0,KeyEvent.VK_F11,true,true);
				}});
			//ZOOM
				add(new Menu(menu,"Zoom"){{
				//AUMENTAR
					add(new Botao(menu,"Aumentar"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().zoom(1);
							}
						});
						setIcon(new ImageIcon(getImage("Aumentar")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_EQUALS,true,true);
					}});
				//DIMINUIR
					add(new Botao(menu,"Diminuir"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().zoom(-1);
							}
						});
						setIcon(new ImageIcon(getImage("Diminuir")));
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_MINUS,true,true);
					}});
					add(new Menu(menu,"Opções"){{
						add(new Botao(menu,"Zoom x1"){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									tree.getActions().zoom(8-Tree.UNIT);
								}
							});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_1,true,true);
						}});
						add(new Botao(menu,"Zoom x2"){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									tree.getActions().zoom(16-Tree.UNIT);
								}
							});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_2,true,true);
						}});
						add(new Botao(menu,"Zoom x3"){{
							setAction(new AbstractAction(){
								public void actionPerformed(ActionEvent a){
									tree.getActions().zoom(24-Tree.UNIT);
								}
							});
							setAtalho(Event.CTRL_MASK,KeyEvent.VK_3,true,true);
						}});
					}});
					add(new JSeparator());
				//RESTAURAR ZOOM
					add(new Botao(menu,"Restaurar para Padrão"){{
						setAction(new AbstractAction(){
							public void actionPerformed(ActionEvent a){
								tree.getActions().zoom(8-Tree.UNIT);
							}
						});
						setAtalho(Event.CTRL_MASK,KeyEvent.VK_0,true,true);
					}});
				}});
				add(new JSeparator());
			//CENTRALIZAR CÂMERA
				add(new Botao(menu,"Centralizar Câmera"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							tree.getActions().centralizar();
						}
					});
					setIcon(new ImageIcon(getImage("Centralizar Câmera")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_E,true,true);
				}});
				add(new JSeparator());
			//QUEBRA DE LINHA
				add(lineWrap=new Toggle(menu,"Quebra Automática de Linha"){{
					setAction(new Runnable(){
						public void run(){
							tree.getTexto().setLineWrap(lineWrap.isPressed());
							notesTexto.setLineWrap(lineWrap.isPressed());
						}
					});
					setIcon(new ImageIcon(getImage("Quebra Automática de Linha")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_Q,true,true);
				}});
			//MOSTRAR CARACTERES
				add(showAllChars=new Toggle(menu,"Mostrar Caracteres Escondidos"){{
					setAction(new Runnable(){
						public void run(){
							tree.getTexto().setViewAllChars(showAllChars.isPressed());
							notesTexto.setViewAllChars(showAllChars.isPressed());
						}
					});
					setIcon(new ImageIcon(getImage("Mostrar Caracteres Escondidos")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_M,true,true);
				}});
			//SEPARAR JANELA DE TEXTO
				add(separateText=new Toggle(menu,"Separar Janela de Texto"){{
					setAction(new Runnable(){
						public void run(){
							janelaTexto.setLocked(!separateText.isPressed());
							janelaTexto.requestFocus();
						}
					});
					setIcon(new ImageIcon(getImage("Separar Janela de Texto")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_L,true,true);
				}});
			//AUTO-FOCAR JANELA DE TEXTO
				add(autoFocusText=new Toggle(menu,"Auto-Focar Janela de Texto"){{
					setAction(new Runnable(){	//FAZ ATIVAR O TOGGLE
						public void run(){}
					});
					setIcon(new ImageIcon(getImage("Auto-Focar Janela de Texto")));
				}});
				add(new JSeparator());
			//MOSTRAR GRADE
				add(showGrid=new Toggle(menu,"Mostrar Grade"){{
					setAction(new Runnable(){
						public void run(){
							tree.setShowGrid(showGrid.isPressed());
							tree.draw();
						}
					});
					setIcon(new ImageIcon(getImage("Mostrar Grade")));
				}});
			}});
		//CONFIGURAR
			add(new Menu(menu,"Configurar"){{
			//FONTE
				add(new Botao(menu,"Fonte..."){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							new FontChooser(){{
								setSelectedFont(Tree.Fonte.FONTE);
								if(showDialog(janela)==FontChooser.Option.APPROVE_OPTION){
									setTreeFont(getSelectedFont());
								}
							}};
						}
					});
				}});
				add(new JSeparator());
			//TRANSPARÊNCIA
				add(new Botao(menu,"Transparência..."){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							int index=0;
							switch(janelaTexto.getTransparentInstance().getTransparencia()){
								case 0:		index=0;	break;
								case 10:	index=1;	break;
								case 25:	index=2;	break;
								case 40:	index=3;	break;
								case 50:	index=4;	break;
								case 60:	index=5;	break;
								case 75:	index=6;	break;
								case 90:	index=7;	break;
								case 100:	index=8;	break;
							}
							final Object opcao=JOptionPane.showInputDialog(null,
									"Nível de transparência da janela de texto", 
									"Nível de Transparência",JOptionPane.QUESTION_MESSAGE,null,transNvlOpcoes,transNvlOpcoes[index]);
							int transparencia=60;
							switch((String)opcao){
								case "Invisível":	transparencia=0;	break;
								case "10%":			transparencia=10;	break;
								case "25%":			transparencia=25;	break;
								case "40%":			transparencia=40;	break;
								case "50%":			transparencia=50;	break;
								case "60%":			transparencia=60;	break;
								case "75%":			transparencia=75;	break;
								case "90%":			transparencia=90;	break;
								case "Opaco":		transparencia=100;	break;
							}
							janelaTexto.getTransparentInstance().setTransparencia(transparencia);
						}
					});
				}});
				add(new JSeparator());
			//LIMITE DE OBJETOS
				add(new Botao(menu,"Limite de Objetos..."){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							int index=0;
							switch(tree.getObjetosLimite()){
								case 0:		index=0;						break;
								case -1:	index=objsLimOpcoes.length-1;	break;
								default:
									for(int i=1;i<objsLimOpcoes.length-1;i++){
										if((Integer)objsLimOpcoes[i]==tree.getObjetosLimite())index=i;
									}
								break;
							}
							final Object opcao=JOptionPane.showInputDialog(null,
									"Limite de objetos na tela antes diminuir qualidade do gráfico", 
									"Limite de Objetos",JOptionPane.QUESTION_MESSAGE,null,objsLimOpcoes,objsLimOpcoes[index]);
							if(opcao instanceof Integer){
								tree.setObjetosLimite((Integer)opcao);
							}else if(opcao instanceof String){
								tree.setObjetosLimite(((String)opcao).equals((String)objsLimOpcoes[0])?0:-1);
							}
						}
					});
				}});
			//LIMITE DE DESFAZER/REFAZER
				add(new Botao(menu,"Limite de Desfazer/Refazer..."){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							int index=0;
							switch(tree.getUndoRedoManager().getDoLimite()){
								case 0:		index=0;						break;
								case -1:	index=doLimOpcoes.length-1;		break;
								default:
									for(int i=1;i<doLimOpcoes.length-1;i++){
										if((Integer)doLimOpcoes[i]==tree.getUndoRedoManager().getDoLimite())index=i;
									}
								break;
							}
							final Object opcao=JOptionPane.showInputDialog(null,
									"Limite de desfazer e refazer guardados", 
									"Limite de Desfazer/Refazer",JOptionPane.QUESTION_MESSAGE,null,doLimOpcoes,doLimOpcoes[index]);
							if(opcao instanceof Integer){
								tree.getUndoRedoManager().setDoLimite((Integer)opcao);
							}else if(opcao instanceof String){
								tree.getUndoRedoManager().setDoLimite(((String)opcao).equals((String)doLimOpcoes[0])?0:-1);
							}
						}
					});
				}});
				add(new JSeparator());
			//SALVAR CONFIGURAÇÕES
				add(new Botao(menu,"Salvar Configurações"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							if((JOptionPane.showConfirmDialog(null,
									new JLabel("<html>Deseja <font color='blue'>SALVAR</font> a configuração atual?<br>")
									,"Salvar .ini",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)){
								final boolean success=setConfigIni();
								if(success)Tree.mensagem("Configuração salva!",Tree.Options.AVISO);
							}
						}
					});
				}});
			//RESTAURAR CONFIGURAÇÕES
				add(new Botao(menu,"Restaurar Para Padrão"){{
					setAction(new AbstractAction(){
						public void actionPerformed(ActionEvent a){
							if((JOptionPane.showConfirmDialog(null,
									new JLabel("<html>Deseja <font color='red'>DELETAR</font> a configuração atual?<br>")
									,"Deletar .ini",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)){
								final File link=new File(System.getProperty("user.dir")+"/"+ini);
								if(link.exists()){
									link.delete();
									final boolean success=getConfigIni();
									if(success)Tree.mensagem("Configuração restaurada!",Tree.Options.AVISO);
								}else Tree.mensagem("Configuração .ini não encontrada!",Tree.Options.ERRO);
							}
						}
					});
				}});
			}});
		//PESQUISAR
			add(new Botao(menu,"Pesquisar"){{
				setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent a){
						new Searcher(tree).chamar();
					}
				});
				setIcon(new ImageIcon(getImage("Pesquisar")));
				setAtalho(Event.CTRL_MASK,KeyEvent.VK_F,false,false);
				setMaximumSize(new Dimension(100,100));
			}});
		//ANOTAÇÕES
			add(new Botao(menu,"Anotações"){{
				setAction(new AbstractAction(){
					public void actionPerformed(ActionEvent a){
						janelaNotes.setVisible(true);
					}
				});
				setIcon(new ImageIcon(getImage("Anotações")));
				setAtalho(Event.CTRL_MASK,KeyEvent.VK_T,false,false);
				setMaximumSize(new Dimension(100,100));
			}});
		}});
	//PAINEL
		add(new Painel(this){{
			setBackground(Tree.FUNDO);
			add(tree=new Tree(this){{
				setFonte(FONTE);
				addObjetoListener(new ObjetoFocusListener(){
					public void objetoFocused(Objeto obj){
						if(tree.getSelectedObjetos().getModulos().size()+tree.getSelectedObjetos().getConexoes().size()!=1){
							janelaTexto.setTitle("Texto");
							return;		//APENAS UM ÚNICO MOD OU COX PODE SER EDITADO POR VEZ
						}
						if(obj.getTipo().is(Objeto.Tipo.MODULO)){
							setModTitle((Modulo)obj);
						}else if(obj.getTipo().is(Objeto.Tipo.CONEXAO)){
							setCoxTitle((Conexao)obj);
						}
					}
					public void objetoUnFocused(Objeto obj){
						if(tree.getSelectedObjetos().getModulos().size()+tree.getSelectedObjetos().getConexoes().size()!=1){
							janelaTexto.setTitle("Texto");
							return;		//APENAS UM ÚNICO MOD OU COX PODE SER EDITADO POR VEZ
						}
						if(tree.getSelectedObjetos().getModulos().size()==1){
							setModTitle(tree.getSelectedObjetos().getModulos().get(0));
						}else if(tree.getSelectedObjetos().getConexoes().size()==1){
							setCoxTitle(tree.getSelectedObjetos().getConexoes().get(0));
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
		}});
		addWindowListener(new WindowListener(){
			public void windowOpened(WindowEvent w){}
			public void windowIconified(WindowEvent w){}
			public void windowDeiconified(WindowEvent w){}
			public void windowClosing(WindowEvent w){
				if(salvarAntes())fechar();
			}
			public void windowClosed(WindowEvent w){}
			public void windowActivated(WindowEvent w){}
			public void windowDeactivated(WindowEvent w){}
		});
		addComponentListener(new ComponentAdapter(){
			public void componentResized(ComponentEvent r){
				if(!janela.isUndecorated())window.setSize(janela.getSize());
				tree.setSize(janela.getWidth()-janela.getInsets().left-janela.getInsets().right,
						janela.getHeight()-janela.getInsets().top-menu.getHeight()-janela.getInsets().bottom);
				tree.draw();
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
					if(drop.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
						for(File file:(List<File>)drop.getTransferData(DataFlavor.javaFileListFlavor))
							if(file.getName().endsWith("mind"))
								if(salvarAntes())abrir(file);
				}catch(Exception erro){
					Tree.mensagem("Erro ao carregar mind!\n"+erro,Tree.Options.ERRO);
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
//JANELA DO TEXTO
	private Rectangle window=new Rectangle(janela.getBounds());
	private boolean mousePressed=false;
	private Janela janelaTexto=new Janela(janela){{
		setTitle("Texto");
		setMinimumSize(new Dimension(180,180));
		setBounds(janela.getX(),janela.getY()+janela.getHeight()-200,janela.getWidth(),200);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setIconImage(getImage("Icone"));
		add(new JScrollPane(){{
			setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
			setBackground(Cor.WHITE);
			setViewportView(tree.getTexto());
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
		setMenu(new JMenuBar(){{
			final JMenuBar menu=this;
			final Cor corBorda=Cor.getChanged(Modulo.Cores.FUNDO,0.7f);
			setBorder(BorderFactory.createMatteBorder(0,0,1,0,corBorda));
			setPreferredSize(new Dimension(getWidth(),20));
			setBackground(MindSort.MENU);
			setForeground(Tree.Fonte.DARK);
			final Font fonteMenu=new Font(FONTE.getName(),Font.BOLD,FONTE.getSize());
			UIManager.put("Menu.font",fonteMenu);
			UIManager.put("MenuItem.font",fonteMenu);
			UIManager.put("Menu.selectionBackground",Cor.getChanged(MindSort.MENU,1.1f));
			UIManager.put("MenuItem.selectionBackground",Cor.getChanged(MindSort.MENU,1.1f));
			UIManager.put("PopupMenu.border",BorderFactory.createLineBorder(corBorda));
			UIManager.put("Separator.foreground",corBorda);
			UIManager.getLookAndFeelDefaults().put("MenuItem.acceleratorForeground",corBorda);
			UIManager.getLookAndFeelDefaults().put("MenuItem.acceleratorFont",FONTE);
		//ARQUIVO
			add(new Menu(menu,"Arquivo"){{
			//NOVO
				add(new Botao(menu,"Novo"){{
					setAction(novoAction);
					setIcon(new ImageIcon(getImage("Novo")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_N,true,true);
				}});
			//ABRIR
				add(new Botao(menu,"Abrir..."){{
					setAction(abrirAction);
					setIcon(new ImageIcon(getImage("Abrir")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_O,true,true);
				}});
				add(new JSeparator());
			//SALVAR
				add(new Botao(menu,"Salvar"){{
					setAction(salvarAction);
					setIcon(new ImageIcon(getImage("Salvar")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_S,true,true);
				}});
			//SALVAR COMO
				add(new Botao(menu,"Salvar Como..."){{
					setAction(salvarComoAction);
					setIcon(new ImageIcon(getImage("Salvar Como")));
					setAtalho(Event.CTRL_MASK+Event.SHIFT_MASK,KeyEvent.VK_S,true,true);
				}});
				add(new JSeparator());
			//SAIR
				add(new Botao(menu,"Sair"){{
					setAction(sairAction);
					setIcon(new ImageIcon(getImage("Sair")));
					setAtalho(Event.CTRL_MASK,KeyEvent.VK_W,true,true);
				}});
			}});
		}});
		new Thread(){
			public void run(){
				Dimension size=janelaTexto.getBounds().getSize();
				while(true){
					try{
						Thread.sleep(100);
					}catch(InterruptedException erro){}
					if(mousePressed)continue;
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
				if(tree.getTitulo().isVisible()){
					tree.getTitulo().requestFocus();
				}else janela.requestFocus();
			}
			private boolean isJanelaTextoHover(){return janelaTexto.getBounds().contains(MouseInfo.getPointerInfo().getLocation());}
			private void focusJanelaTexto(){
				if(janelaTexto.isFocused())return;
				if(tree.getTexto().isEnabled()){
					tree.getTexto().requestFocus();
				}else janelaTexto.requestFocus();
			}
		}.start();
	}};
//JANELA DAS ANOTAÇÕES
	private Texto notesTexto=new Texto(){{
		setFont(Tree.Fonte.FONTE);
		setForeground(Tree.Fonte.DARK);
		setLineWrappable(true);
	}};
	private JFrame janelaNotes=new JFrame(){{
		setTitle("Anotações Temporárias");
		setMinimumSize(new Dimension(180,180));
		setBackground(Cor.WHITE);
		setBounds(janela.getX(),janela.getY(),350,300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setIconImage(getImage("Icone"));
		add(new JScrollPane(){{
			setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
			setBackground(Cor.WHITE);
			setViewportView(notesTexto);
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
	}};
//MAIN
	public MindSort(String[]args){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(ClassNotFoundException|InstantiationException|IllegalAccessException|UnsupportedLookAndFeelException erro){
			Tree.mensagem("Erro ao configurar janela!\n"+erro,Tree.Options.ERRO);
		}
		fullscreen.setToggle(fullscreen.isPressed());
		separateText.setToggle(separateText.isPressed());
		autoFocusText.setToggle(autoFocusText.isPressed());
		showGrid.setToggle(showGrid.isPressed());
		lineWrap.setToggle(lineWrap.isPressed());
		showAllChars.setToggle(showAllChars.isPressed());
		getConfigIni();
		tree.setFocusOn(new Objeto[]{Tree.getMestre()});
		janela.requestFocus();
		if(args.length>0)abrir(new File(args[0]));
		tree.draw();
		tree.getPopup().show(new Point(0,0),null);	//APARENTEMENTE NECESSÁRIO PARA QUE O MENUBAR.MENU SUMA??? 
		tree.getPopup().close();
	}
//FUNCS
	private void setTreeFont(Font fonte){
		tree.setFonte(fonte);
		notesTexto.setFont(fonte);
	}
//FULLSCREEN
	private void fullscreen(boolean fullscreen){
		janela.dispose();
		janela.setAlwaysOnTop(fullscreen);
		if(fullscreen){
			janela.setUndecorated(true);			//DEVE OCORRER ANTES DE RETIRAR O FUNDO
			janela.setBackground(Cor.TRANSPARENTE);	//A JANELA DEVE SER DESBORDADA
			final Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
			janela.setBounds(0,0,screenSize.width,screenSize.height);
		}else{
			janela.setBackground(Tree.FUNDO);		//DEVE OCORRER ANTES DE BORDAR
			janela.setUndecorated(false);			//O FUNDO DEVE SER OPACO
			janela.setBounds(window);
		}
		final int width=janela.getWidth()-janela.getInsets().left-janela.getInsets().right;
		final int height=janela.getHeight()-janela.getInsets().top-janela.getInsets().bottom;
		tree.setBounds(0,janela.getInsets().top,width,height);
		tree.draw();
		janela.setVisible(true);
		janela.requestFocus();
	}
//ICONES
	private Image getImage(String nome){
		return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/"+nome+".png"));
	}
//FILE CHOOSER
	private File choose(String nome){
		final JFileChooser choose=new JFileChooser(){
			public void approveSelection(){
				if(getSelectedFile().exists()){
					switch(JOptionPane.showConfirmDialog(this,getSelectedFile().getName()+" já existe.\nDeseja substituí-lo?","Arquivo já existe",JOptionPane.YES_NO_OPTION)){
						case JOptionPane.YES_OPTION:super.approveSelection();return;
						case JOptionPane.NO_OPTION:return;
						case JOptionPane.CLOSED_OPTION:return;
					}
				}
				super.approveSelection();
			}{
			setFileFilter(new FileNameExtensionFilter("Mind Map","mind"));
			setAcceptAllFileFilterUsed(false);
		}};
		final Frame icone=new Frame();
		icone.setIconImage(getImage(nome));
		return (choose.showDialog(icone,nome)==JFileChooser.APPROVE_OPTION?choose.getSelectedFile():null);
	}
//NOVO
	private void novo(File mind){
		if(mind==null)return;
		if(!mind.toString().endsWith(".mind"))mind=new File(mind+".mind");
		try{
			final PrintWriter writer=new PrintWriter(this.link=mind,"UTF-8");
			writer.println("<mind fontName=\""+Tree.Fonte.FONTE.getName()+"\" fontSize=\""+Tree.Fonte.FONTE.getSize()+"\" fontStyle=\""+Tree.Fonte.FONTE.getStyle()+"\">");
			writer.println("	<mod border=\"0\" color=\"(0,255,255)\" icons=\"\" title=\"Novo Mind\" x=\"0\" y=\"0\"><text/></mod>");
			writer.println("</mind>");
			writer.close();
		}catch(Exception erro){
			Tree.mensagem("Erro ao criar .mind!\n"+erro,Tree.Options.ERRO);
		}
		janela.setTitle(titulo+" - "+link);
	}
//ABRIR
	private void abrir(File mind){
		if(mind==null)return;
		tree.setEnabled(false);
		tree.setVisible(false);
		tree.clear();
		tree.setFocusOn(new Objeto[]{Tree.getMestre()});
		new Thread(new Runnable(){
			public void run(){
				try{
					final Document tags=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(link=mind);
					final Element mindTag=tags.getDocumentElement();
					tree.addTree(mindTag,true);
				}catch(Exception erro){
					Tree.mensagem("Erro ao abrir .mind!\n"+erro,Tree.Options.ERRO);
				}
				janela.setTitle(mind.getName()+" - "+link);
				tree.setFocusOn(new Objeto[]{Tree.getMestre()});
				tree.setEnabled(true);
				tree.setVisible(true);
			}
		}).start();
	}
//SALVAR
	private boolean saving=false;
	private void salvar(File mind){
		saving=true;
		salvar(mind,5);	//TENTA SALVAR 5 VEZES
		saving=false;
	}
	private void salvar(File mind,int tentativas){
		if(mind==null)return;
		if(tentativas<=0){
			Tree.mensagem("Não foi possível salvar .mind!",Tree.Options.AVISO);
			return;
		}
		if(!mind.exists()){		//CRIAR NOVO ARQUIVO
			writeFile(mind);			//CRIA NOVO MIND
			if(!mind.exists())salvar(mind,tentativas-1);		//RETRY
		}else{					//SALVAR ALTERAÇÕES
			final File tempMind=new File(mind.toString()+".temp");
			try{						//COPIA MIND PARA TEMP_MIND
				Files.copy(mind.toPath(),tempMind.toPath(),StandardCopyOption.REPLACE_EXISTING);
			}catch(IOException erro){
				Tree.mensagem("Erro ao criar .temp!\n"+erro,Tree.Options.ERRO);
			}
			if(!tempMind.exists())salvar(mind,tentativas-1);	//RETRY
			writeFile(mind);			//SALVA ALTERAÇÕES EM MIND
			if(mind.exists()&&tempMind.exists()){
				tempMind.delete();		//APAGA TEMP_MIND
			}else salvar(mind,tentativas-1);					//RETRY
		}
		if(janela.getTitle().startsWith("*"))janela.setTitle(janela.getTitle().substring(1));
	}
	private boolean salvarAntes(){
		if(!janela.getTitle().startsWith("*"))return true;
		switch(JOptionPane.showConfirmDialog(null,new JLabel("<html>Deseja <font color='blue'>SALVAR</font>?<br>"),"Salvar .mind",JOptionPane.YES_NO_CANCEL_OPTION)){
			case JOptionPane.YES_OPTION:
				if(link==null)novo(choose("Salvar"));
				salvar(link);
			break;
			case JOptionPane.NO_OPTION:
				janela.setTitle(janela.getTitle().substring(1));
			break;
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:	return false;
		}
		return true;
	}
	private void writeFile(File mind){
		try{
			final BufferedWriter mindFile=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mind),StandardCharsets.UTF_8));
			mindFile.write(tree.getText(tree.getObjetos()));
			mindFile.close();
		}catch(IOException erro){
			Tree.mensagem("Erro ao salvar .mind!\n"+erro,Tree.Options.ERRO);
		}
	}
//FECHAR
	private void fechar(){
		if(saving)return;	//IGNORA FECHAR
		System.exit(0);
	}
//.INI
	private boolean getConfigIni(){
		final File link=new File(System.getProperty("user.dir")+"/"+ini);
		if(link.exists())try{
			for(String linha:Files.readAllLines(link.toPath(),StandardCharsets.UTF_8)){
				final Matcher match=Pattern.compile("^\t?\t?([^=\n]+)=+").matcher(linha);
				if(match.find())switch(match.group(1)){
				//ÁREA DA JANELA
					case "x_Window":					janela.setLocation(getInteger(linha),janela.getY());						break;
					case "y_Window":					janela.setLocation(janela.getX(),getInteger(linha));						break;
					case "width_Window":				janela.setSize(getInteger(linha),janela.getHeight());						break;
					case "height_Window":				janela.setSize(janela.getWidth(),getInteger(linha));						break;
				//ÁREA DA JANELA DO TEXTO
					case "x_TextWindow":				janelaTexto.setLocation(getInteger(linha),janelaTexto.getY());				break;
					case "y_TextWindow":				janelaTexto.setLocation(janelaTexto.getX(),getInteger(linha));				break;
					case "width_TextWindow":			janelaTexto.setSize(getInteger(linha),janelaTexto.getHeight());				break;
					case "height_TextWindow":			janelaTexto.setSize(janelaTexto.getWidth(),getInteger(linha));				break;
				//ÁREA DO DIALOG DO TEXTO
					case "defaultWidth_TextWindow":		janelaTexto.setDialogDefaultWidth(getInteger(linha));						break;
					case "defaultHeight_TextWindow":	janelaTexto.setDialogDefaultHeight(getInteger(linha));						break;
				//FONTE
					case "fonte":						setTreeFont(getFont(linha));												break;
				//FUNDO
					case "showGrid":					showGrid.setToggle(getBoolean(linha));										break;
				//PALETA
					case "cor_0_0":						CorPick.PALETA_DEFAULT[0][0]=getCor(linha);									break;
					case "cor_0_1":						CorPick.PALETA_DEFAULT[0][1]=getCor(linha);									break;
					case "cor_0_2":						CorPick.PALETA_DEFAULT[0][2]=getCor(linha);									break;
					case "cor_0_3":						CorPick.PALETA_DEFAULT[0][3]=getCor(linha);									break;
					case "cor_0_4":						CorPick.PALETA_DEFAULT[0][4]=getCor(linha);									break;
					case "cor_0_5":						CorPick.PALETA_DEFAULT[0][5]=getCor(linha);									break;
					case "cor_1_0":						CorPick.PALETA_DEFAULT[1][0]=getCor(linha);									break;
					case "cor_1_1":						CorPick.PALETA_DEFAULT[1][1]=getCor(linha);									break;
					case "cor_1_2":						CorPick.PALETA_DEFAULT[1][2]=getCor(linha);									break;
					case "cor_1_3":						CorPick.PALETA_DEFAULT[1][3]=getCor(linha);									break;
					case "cor_1_4":						CorPick.PALETA_DEFAULT[1][4]=getCor(linha);									break;
					case "cor_1_5":						CorPick.PALETA_DEFAULT[1][5]=getCor(linha);									break;
					case "cor_2_2":						CorPick.PALETA_DEFAULT[2][2]=getCor(linha);									break;
					case "cor_2_3":						CorPick.PALETA_DEFAULT[2][3]=getCor(linha);									break;
					case "cor_2_4":						CorPick.PALETA_DEFAULT[2][4]=getCor(linha);									break;
					case "cor_2_5":						CorPick.PALETA_DEFAULT[2][5]=getCor(linha);									break;
					case "cor_3_2":						CorPick.PALETA_DEFAULT[3][2]=getCor(linha);									break;
					case "cor_3_3":						CorPick.PALETA_DEFAULT[3][3]=getCor(linha);									break;
					case "cor_3_4":						CorPick.PALETA_DEFAULT[3][4]=getCor(linha);									break;
					case "cor_3_5":						CorPick.PALETA_DEFAULT[3][5]=getCor(linha);									break;
				//TEXTO
					case "lineWrap":					lineWrap.setToggle(getBoolean(linha));										break;
					case "showAllChars":				showAllChars.setToggle(getBoolean(linha));									break;
					case "separarTextWindow":			separateText.setToggle(getBoolean(linha));									break;
					case "autoFocarTextWindow":			autoFocusText.setToggle(getBoolean(linha));									break;
				//LIMITES
					case "objetosLimite":				tree.setObjetosLimite(getInteger(linha));									break;
					case "undoRedoLimite":				tree.getUndoRedoManager().setDoLimite(getInteger(linha));					break;
					case "transparenciaNivel":			janelaTexto.getTransparentInstance().setTransparencia(getInteger(linha));	break;
				//ICONE
					case "iconesPasta":					tree.getPopup().setIconePasta(new File(getString(linha)));					break;
				}
			}
		}catch(IOException erro){
			Tree.mensagem("Erro ao configurar sistema!\n"+erro,Tree.Options.ERRO);
			return false;
		}
		return true;
	}
		private String getString(String linha){return linha.substring(linha.indexOf("=")+1,linha.length());}
		private int getInteger(String linha){return Integer.parseInt(getString(linha));}
		private Font getFont(String linha){
			String[]fonte=getString(linha).split(",");
			return new Font(fonte[0],Integer.parseInt(fonte[1]),Integer.parseInt(fonte[2]));
		}
		private Cor getCor(String linha){
			final String corHex=getString(linha);
			return new Cor(Integer.valueOf(corHex.substring(1,3),16),Integer.valueOf(corHex.substring(3,5),16),Integer.valueOf(corHex.substring(5,7),16));
		}
		private boolean getBoolean(String linha){
			final String bool=getString(linha);
			return(bool.equals("true")?true:bool.equals("false")?false:null);
		}
	private boolean setConfigIni(){
		final File link=new File(System.getProperty("user.dir")+"/"+ini);
		try{
			final PrintWriter writer=new PrintWriter(link,"UTF-8");
			writer.println("[Settings]");
			writer.println("	[Dimensions]");
			writer.println("		x_Window="+janela.getX());
			writer.println("		y_Window="+janela.getY());
			writer.println("		width_Window="+janela.getWidth());
			writer.println("		height_Window="+janela.getHeight());
			writer.println("		x_TextWindow="+janelaTexto.getX());
			writer.println("		y_TextWindow="+janelaTexto.getY());
			writer.println("		width_TextWindow="+janelaTexto.getWidth());
			writer.println("		height_TextWindow="+janelaTexto.getHeight());
			writer.println("		defaultWidth_TextWindow="+janelaTexto.getDialogDefaultWidth());
			writer.println("		defaultHeight_TextWindow="+janelaTexto.getDialogDefaultHeight());
			writer.println("	[Font]");
			writer.println("		font="+Tree.Fonte.FONTE.getName()+","+Tree.Fonte.FONTE.getStyle()+","+Tree.Fonte.FONTE.getSize());
			writer.println("	[Fundo]");
			writer.println("		showGrid="+showGrid.isPressed());
			writer.println("	[Paleta]");
			for(int r=0;r<CorPick.PALETA_DEFAULT.length;r++){
				final Cor[]linha=CorPick.PALETA_DEFAULT[r];
				for(int c=0;c<linha.length;c++){
					final Cor cor=linha[c];
					if(cor==null)continue;
					writer.println("		cor_"+r+"_"+c+"="+("#"+Integer.toHexString(cor.getRGB()).substring(2).toUpperCase()));
				}
			}
			writer.println("	[Texto]");
			writer.println("		lineWrap="+lineWrap.isPressed());
			writer.println("		showAllChars="+showAllChars.isPressed());
			writer.println("		separarTextWindow="+separateText.isPressed());
			writer.println("		autoFocarTextWindow="+autoFocusText.isPressed());
			writer.println("	[Limites]");
			writer.println("		objetosLimite="+tree.getObjetosLimite());
			writer.println("		undoRedoLimite="+tree.getUndoRedoManager().getDoLimite());
			writer.println("		transparenciaNivel="+janelaTexto.getTransparentInstance().getTransparencia());
			writer.println("	[Icones]");
			writer.println("		iconesPasta="+tree.getPopup().getIconePasta());
			writer.close();
			return true;
		}catch(Exception erro){
			Tree.mensagem("Erro ao salvar .ini!\n"+erro,Tree.Options.ERRO);
			return false;
		}
	}
}