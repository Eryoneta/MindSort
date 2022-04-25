package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
@SuppressWarnings({"serial","rawtypes","unchecked"})
public class FontChooser extends JComponent{
//RESPOSTAS
	public enum Option{
		APPROVE_OPTION,
		CANCEL_OPTION,
		ERROR_OPTION;
	}
	protected Option resposta=Option.ERROR_OPTION;
//VAR GLOBAIS
	private static final Font FONTE=new Font("Dialog",Font.PLAIN,10);
	private static final Font DEFAULT_FONTE=new Font("Arial",Font.PLAIN,12);
//NOMES
	private static final String[]NOMES_OPTIONS=GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	//TEXTO DO NOME SELECIONADO
		private JTextField nomeTexto;
			public JTextField getNomeTexto(){
				if(nomeTexto==null){
					nomeTexto=new JTextField(){{
						addFocusListener(new TextoFocusAction(this));
						setFont(FONTE);
					}};
					nomeTexto.addKeyListener(new TextoKeyAction(getNomesLista()));	//FORA PARA EVITAR CAUSAR LOOP NA CRIAÇÃO
					nomeTexto.getDocument().addDocumentListener(new ListDocumentAction(getNomesLista()));
				}
				return nomeTexto;
			}
	//LISTA DE NOMES
		private JList nomesLista;
			public JList getNomesLista(){
				if(nomesLista==null){
					nomesLista=new JList(NOMES_OPTIONS){{
						setCellRenderer(new ListCellRenderer(){
							public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus){
								return new JLabel(){{
									setText(value.toString());
									setFont(new Font(value.toString(),Font.PLAIN,12));
									setOpaque(true);
									if(isSelected){
										setBackground(new Color(0,120,215));
										setForeground(Color.WHITE);
									}else{
										setBackground(Color.WHITE);
										setForeground(Color.BLACK);
									}
								}};
							}
						});
						setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						setFont(FONTE);
						setFocusable(false);
					}};
					nomesLista.addListSelectionListener(new ListSelectionAction(getNomeTexto()));
					nomesLista.setSelectedIndex(0);
				}
				return nomesLista;
			}
			public String getSelectedNome(){
				return (String)getNomesLista().getSelectedValue();
			}
			public void setSelectedNome(String name){
				final String[]nomes=NOMES_OPTIONS;
				for (int i=0;i<nomes.length;i++){
					if(nomes[i].toLowerCase().equals(name.toLowerCase())){
						getNomesLista().setSelectedIndex(i);
						break;
					}
				}
				updateExemplo();
			}
	//PAINEL DOS NOMES
		private JPanel nomePainel;
			protected JPanel getNomePainel(){
				if(nomePainel==null){
					nomePainel=new JPanel(){{
						setLayout(new BorderLayout());
						setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
						setPreferredSize(new Dimension(180,130));
						add(new JLabel(MindSortUI.getLang().get("M_Menu_C_F_N","Name")){{
							setHorizontalAlignment(JLabel.LEFT);
							setHorizontalTextPosition(JLabel.LEFT);
							setLabelFor(getNomeTexto());
							setDisplayedMnemonic('F');
						}},BorderLayout.NORTH);
						add(new JPanel(){{
							setLayout(new BorderLayout());
							add(getNomeTexto(),BorderLayout.NORTH);
							add(new JScrollPane(getNomesLista()){{
								getVerticalScrollBar().setFocusable(false);
								setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
							}},BorderLayout.CENTER);
						}},BorderLayout.CENTER);
					}};
				}
				return nomePainel;
			}
//ESTILOS
	private static final int[]ESTILOS={
		Font.PLAIN,Font.BOLD,Font.ITALIC,Font.BOLD|Font.ITALIC
	};
	private static final String[]ESTILOS_OPTIONS=new String[]{
		MindSortUI.getLang().get("M_Menu_C_F_S_R","Plain"),
		MindSortUI.getLang().get("M_Menu_C_F_S_B","Bold"),
		MindSortUI.getLang().get("M_Menu_C_F_S_I","Italic"),
		MindSortUI.getLang().get("M_Menu_C_F_S_BI","Bold and Italic")
	};
	//TEXTO DO ESTILO SELECIONADO
		private JTextField estiloTexto;
			public JTextField getEstiloTexto(){
				if(estiloTexto==null){
					estiloTexto=new JTextField(){{
						addFocusListener(new TextoFocusAction(this));
						setFont(FONTE);
					}};
					estiloTexto.addKeyListener(new TextoKeyAction(getEstilosLista()));
					estiloTexto.getDocument().addDocumentListener(new ListDocumentAction(getEstilosLista()));
				}
				return estiloTexto;
			}
	//LISTA DE ESTILOS
		private JList estilosLista;
			public JList getEstilosLista(){
				if(estilosLista==null){
					estilosLista=new JList(ESTILOS_OPTIONS){{
						setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						setFont(FONTE);
						setFocusable(false);
					}};
					estilosLista.addListSelectionListener(new ListSelectionAction(getEstiloTexto()));
					estilosLista.setSelectedIndex(0);
				}
				return estilosLista;
			}
			public int getSelectedEstilo(){
				return ESTILOS[getEstilosLista().getSelectedIndex()];
			}
			public void setSelectedEstilo(int estilo){
				for(int i=0;i<ESTILOS.length;i++){
					if(ESTILOS[i]==estilo){
						getEstilosLista().setSelectedIndex(i);
						break;
					}
				}
				updateExemplo();
			}
	//PAINEL DOS ESTILOS
		private JPanel estiloPainel;
			protected JPanel getEstiloPainel(){
				if(estiloPainel==null){
					estiloPainel=new JPanel(){{
						setLayout(new BorderLayout());
						setPreferredSize(new Dimension(140,130));
						setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
						add(new JLabel(MindSortUI.getLang().get("M_Menu_C_F_S","Style:")){{
							setHorizontalAlignment(JLabel.LEFT);
							setHorizontalTextPosition(JLabel.LEFT);
							setLabelFor(getEstiloTexto());
							setDisplayedMnemonic('Y');
						}},BorderLayout.NORTH);
						add(new JPanel(){{
							setLayout(new BorderLayout());
							add(getEstiloTexto(),BorderLayout.NORTH);
							add(new JScrollPane(getEstilosLista()){{
								getVerticalScrollBar().setFocusable(false);
								setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
							}},BorderLayout.CENTER);
						}},BorderLayout.CENTER);
					}};
				}
				return estiloPainel;
			}
//TAMANHOS
	private static final String[]TAMANHOS={
		"8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72"
	};
	private static final String[]TAMANHOS_OPTIONS=TAMANHOS;
	//TEXTO DO TAMANHO SELECIONADO
		private JTextField tamanhoTexto;
			public JTextField getTamanhoTexto(){
				if(tamanhoTexto==null){
					tamanhoTexto=new JTextField(){{
						addFocusListener(new TextoFocusAction(this));
						setFont(FONTE);
					}};
					tamanhoTexto.addKeyListener(new TextoKeyAction(getTamanhosLista()));
					tamanhoTexto.getDocument().addDocumentListener(new ListDocumentAction(getTamanhosLista()));
				}
				return tamanhoTexto;
			}
	//LISTA DE TAMANHOS
		private JList tamanhosLista;
			public JList getTamanhosLista(){
				if(tamanhosLista==null){
					tamanhosLista=new JList(TAMANHOS_OPTIONS){{
						setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						setFont(FONTE);
						setFocusable(false);
					}};
					tamanhosLista.addListSelectionListener(new ListSelectionAction(getTamanhoTexto()));
					tamanhosLista.setSelectedIndex(0);
				}
				return tamanhosLista;
			}
			public int getSelectedTamanho(){
				int fontSize=1;
				String fontSizeString=getTamanhoTexto().getText();
				while(true){	//TODO: PARA QUE SERVE???
					try{
						fontSize=Integer.parseInt(fontSizeString);
						break;
					}catch(NumberFormatException erro){
						fontSizeString=(String)getTamanhosLista().getSelectedValue();
						getTamanhoTexto().setText(fontSizeString);
					}
				}
				return fontSize;
			}
			public void setSelectedTamanho(int size){
				final String sizeString=String.valueOf(size);
				for (int i=0;i<TAMANHOS_OPTIONS.length;i++){
					if (TAMANHOS_OPTIONS[i].equals(sizeString)){
						getTamanhosLista().setSelectedIndex(i);
						break;
					}
				}
				getTamanhoTexto().setText(sizeString);
				updateExemplo();
			}
	//PAINEL DOS TAMANHOS
		private JPanel tamanhoPainel;
			protected JPanel getTamanhoPainel(){
				if(tamanhoPainel==null){
					tamanhoPainel=new JPanel(){{
						setLayout(new BorderLayout());
						setPreferredSize(new Dimension(70,130));
						setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
						add(new JLabel(MindSortUI.getLang().get("M_Menu_C_F_T","Size:")){{
							setHorizontalAlignment(JLabel.LEFT);
							setHorizontalTextPosition(JLabel.LEFT);
							setLabelFor(getTamanhoTexto());
							setDisplayedMnemonic('S');
						}},BorderLayout.NORTH);
						add(new JPanel(){{
							setLayout(new BorderLayout());
							add(getTamanhoTexto(),BorderLayout.NORTH);
							add(new JScrollPane(getTamanhosLista()){{
								getVerticalScrollBar().setFocusable(false);
								setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
							}},BorderLayout.CENTER);
						}},BorderLayout.CENTER);
					}};
				}
				return tamanhoPainel;
			}
//EXEMPLO
	//TEXTO DO EXEMPLO
		private JTextField exemploTexto;
			protected JTextField getSampleTextField(){
				if(exemploTexto==null){
					exemploTexto=new JTextField(("AaBbYyZz")){{
						final Border lowered=BorderFactory.createLoweredBevelBorder();
						setBorder(lowered);
						setPreferredSize(new Dimension(300,100));
					}};
				}
				return exemploTexto;
			}
	//PAINEL DO EXEMPLO
		private JPanel exemploPainel;
			protected JPanel getExemploPainel(){
				if(exemploPainel==null){
					exemploPainel=new JPanel(){{
						setLayout(new BorderLayout());
						final Border titledBorder=BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
								MindSortUI.getLang().get("M_Menu_C_F_E","Example"));
						final Border empty=BorderFactory.createEmptyBorder(5,10,10,10);
						final Border border=BorderFactory.createCompoundBorder(titledBorder,empty);
						setBorder(border);
						add(getSampleTextField(),BorderLayout.CENTER);
					}};
				}
				return exemploPainel;
			}
	protected void updateExemplo(){
		getSampleTextField().setFont(getSelectedFont());
	}
//LISTENER: DISPARA COM A SELEÇÃO DE ITEM
	protected class ListSelectionAction implements ListSelectionListener{
	//TEXTO
		private JTextComponent texto;
	//MAIN
		public ListSelectionAction(JTextComponent texto){this.texto=texto;}
	//FUNCS
		public void valueChanged(ListSelectionEvent l){
			if(l.getValueIsAdjusting()==false){
				final JList lista=(JList)l.getSource();
				final String selecTexto=(String)lista.getSelectedValue();
				final String selecOldTexto=texto.getText();
				texto.setText(selecTexto);
				if(!selecOldTexto.equalsIgnoreCase(selecTexto)){
					texto.selectAll();
					texto.requestFocus();
				}
				updateExemplo();
			}
		}
	}
//LISTENER: DISPARA COM O FOCO DE TEXTO
	protected class TextoFocusAction extends FocusAdapter{
	//TEXTO
		private JTextComponent texto;
	//MAIN
		public TextoFocusAction(JTextComponent texto){this.texto=texto;}
	//FUNCS
		public void focusGained(FocusEvent f){
			texto.selectAll();
		}
		public void focusLost(FocusEvent f){
			texto.select(0,0);
			updateExemplo();
		}
	}
//LISTENER: DISPARA COM O INPUT DE TECLAS
	protected class TextoKeyAction extends KeyAdapter{
	//LISTA
		private JList lista;
	//MAIN
		public TextoKeyAction(JList lista){this.lista=lista;}
	//FUNCS
		public void keyPressed(KeyEvent k){
			int i=lista.getSelectedIndex();
			switch(k.getKeyCode()){
				case KeyEvent.VK_UP:
					i=lista.getSelectedIndex()-1;
					if(i<0)i=0;
					lista.setSelectedIndex(i);
				break;
				case KeyEvent.VK_DOWN:
					int listaSize=lista.getModel().getSize();
					i=lista.getSelectedIndex()+1;
					if(i>=listaSize)i=listaSize-1;
					lista.setSelectedIndex(i);
				break;
			}
		}
	}
//LISTENER: DISPARA COM A EDIÇÃO DE TEXTO
	protected class ListDocumentAction implements DocumentListener{
	//LISTA
		private JList lista;
	//MAIN
		public ListDocumentAction(JList lista){this.lista=lista;}
	//FUNCS
		public void insertUpdate(DocumentEvent d){update(d);}
		public void removeUpdate(DocumentEvent d){update(d);}
		public void changedUpdate(DocumentEvent d){update(d);}
		private void update(DocumentEvent d){
			String texto="";
			try{
				final Document doc=d.getDocument();
				texto=doc.getText(0,doc.getLength());
			}catch(BadLocationException erro){
				MindSortUI.mensagem(
						MindSortUI.getLang().get("M_Err9","Error: Cannot accept value!")+"\n"+erro,
						MindSortUI.Options.ERRO);
			}
			if(texto.length()>0){
				int index=lista.getNextMatch(texto,0,Position.Bias.Forward);
				if(index<0)index=0;
				lista.ensureIndexIsVisible(index);
				final String matchedName=lista.getModel().getElementAt(index).toString();
				if(texto.equalsIgnoreCase(matchedName)){
					if(index!=lista.getSelectedIndex()){
						SwingUtilities.invokeLater(new ListSelector(index));
					}
				}
			}
		}
		public class ListSelector implements Runnable{
		//INDEX
			private int index;
		//MAIN
			public ListSelector(int index){this.index=index;}
		//FUNCS
			public void run(){lista.setSelectedIndex(this.index);}
		}
	}
//MAIN
	public FontChooser(){
		setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		add(new JPanel(){{			//PAINEL DO FUNDO
			setLayout(new GridLayout(2,1));
			add(new JPanel(){{			//PAINEL DAS SELEÇÕES
				setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
				add(getNomePainel());		//NOMES
				add(getEstiloPainel());		//ESTILOS
				add(getTamanhoPainel());	//TAMANHOS
			}},BorderLayout.NORTH);
			add(getExemploPainel(),BorderLayout.CENTER);	//EXEMPLO
		}});
		setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		setSelectedFont(DEFAULT_FONTE);
	}
//FUNCS
	public Font getSelectedFont(){
		return new Font(getSelectedNome(),getSelectedEstilo(),getSelectedTamanho());
	}
	public void setSelectedFont(Font fonte){
		setSelectedNome(fonte.getFamily());
		setSelectedEstilo(fonte.getStyle());
		setSelectedTamanho(fonte.getSize());
	}
	public Option showDialog(Component janela){
		resposta=Option.ERROR_OPTION;
		JDialog dialog=createDialog(janela);
		dialog.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent w){
				resposta=Option.CANCEL_OPTION;
			}
		});
		dialog.setVisible(true);
		dialog.dispose();
		dialog=null;
		return resposta;
	}
		protected JDialog createDialog(Component janela){
			final Frame frame=(janela instanceof Frame?(Frame)janela:(Frame)SwingUtilities.getAncestorOfClass(Frame.class,janela));
			final JDialog dialog=new JDialog(frame,MindSortUI.getLang().get("M_Menu_C_F_SF","Select Font"),true);
			dialog.getContentPane().add(this,BorderLayout.CENTER);
			dialog.getContentPane().add(new JPanel(){{
				setLayout(new BorderLayout());
				add(new JPanel(){{
					setLayout(new GridLayout(2,1));
					final ActionMap actionMap=getActionMap();
					final InputMap inputMap=getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
					final Action okAction=new DialogOKAction(dialog);			//ATALHO DE OK
					actionMap.put(okAction.getValue(Action.DEFAULT),okAction);
					inputMap.put(KeyStroke.getKeyStroke("ENTER"),okAction.getValue(Action.DEFAULT));
					add(new JButton(okAction){{				//OK
						setFont(FONTE);
					}});
					final Action cancelAction=new DialogCancelAction(dialog);	//ATALHO DE CANCEL
					actionMap.put(cancelAction.getValue(Action.DEFAULT),cancelAction);
					inputMap.put(KeyStroke.getKeyStroke("ESCAPE"),cancelAction.getValue(Action.DEFAULT));
					add(new JButton(cancelAction){{			//CANCEL
						setFont(FONTE);
					}});
					setBorder(BorderFactory.createEmptyBorder(25,0,10,10));
				}},BorderLayout.NORTH);
			}},BorderLayout.EAST);
			dialog.pack();
			dialog.setLocationRelativeTo(frame);
			dialog.setMinimumSize(dialog.getSize());
			return dialog;
		}
//BOTÃO OK
	protected class DialogOKAction extends AbstractAction{
	//VAR GLOBAIS
		protected final String ACTION_NAME=MindSortUI.getLang().get("M_Menu_C_F_Ok","OK");
		private JDialog dialog;
	//MAIN
		protected DialogOKAction(JDialog dialog){
			this.dialog=dialog;
			putValue(Action.DEFAULT,ACTION_NAME);
			putValue(Action.ACTION_COMMAND_KEY,ACTION_NAME);
			putValue(Action.NAME,(ACTION_NAME));
		}
	//FUNCS
		public void actionPerformed(ActionEvent a){
			resposta=Option.APPROVE_OPTION;
			dialog.dispose();
		}
	}
//BOTÃO CANCELAR
	protected class DialogCancelAction extends AbstractAction{
	//VAR GLOBAIS
		protected final String ACTION_NAME=MindSortUI.getLang().get("M_Menu_C_F_Cl","Cancel");
		private JDialog dialog;
	//MAIN
		protected DialogCancelAction(JDialog dialog){
			this.dialog=dialog;
			putValue(Action.DEFAULT,ACTION_NAME);
			putValue(Action.ACTION_COMMAND_KEY,ACTION_NAME);
			putValue(Action.NAME,(ACTION_NAME));
		}
	//FUNCS
		public void actionPerformed(ActionEvent a){
			resposta=Option.CANCEL_OPTION;
			dialog.dispose();
		}
	}
}