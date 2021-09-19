package main;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import element.tree.popup.color.CorPick;
import main.janela.Janela;
import element.tree.objeto.Objeto;
import element.tree.propriedades.Cor;
import element.tree.main.Tree;
import element.tree.main.TreeUI;
@SuppressWarnings("serial")
public class MindSort{
//MAIN(EXECUTE)
	public static void main(String[]args){new MindSort(args);}
//VAR GLOBAIS
	protected String titulo="MindSort";
	protected String ini="config.ini";
//LINK
	private File link=null;
		public File getFileLink(){return link;}
//TREE
	private Tree tree;
		public Tree getTree(){return tree;}
		public void setTree(Tree tree){this.tree=tree;}
//UI
	private final MindSortUI UI=new MindSortUI(this);
		public MindSortUI getUI(){return UI;}
//MAIN
	public MindSort(String[]args){
		getUI().build();
		if(args.length>0)abrir(new File(args[0]));
		tree.draw();
		tree.getUI().getPopup().show(new Point(0,0),null);	//APARENTEMENTE NECESSÁRIO PARA QUE O MENUBAR.MENU SUMA??? 
		tree.getUI().getPopup().close();
	}
//ICONES
	protected Image getImage(String nome){
		return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/"+nome+".png"));
	}
//FILE CHOOSER
	public File choose(String titulo,Image icone,boolean saveAsMind){
		final JFileChooser choose=new JFileChooser(){
			public void approveSelection(){
				if(getSelectedFile().exists()){
					switch(JOptionPane.showConfirmDialog(this,
							getSelectedFile().getName()+MindSortUI.getLang().get("M_Menu_F_SC_Tx"," already exists.\nReplace it?"),
							MindSortUI.getLang().get("M_Menu_F_SC_Ti","File already exists"),
							JOptionPane.YES_NO_OPTION)){
						case JOptionPane.YES_OPTION:	super.approveSelection();return;
						case JOptionPane.NO_OPTION:		return;
						case JOptionPane.CLOSED_OPTION:	return;
					}
				}
				super.approveSelection();
			}{
			if(saveAsMind) {
				setFileFilter(new FileNameExtensionFilter("Mind Map (*.mind)","mind"));
			}else setFileFilter(new FileNameExtensionFilter("Image (*.png)","png"));
			setAcceptAllFileFilterUsed(false);
		}};
		final Frame frameIcon=new Frame();
		frameIcon.setIconImage(icone);
		return (choose.showDialog(frameIcon,titulo)==JFileChooser.APPROVE_OPTION?choose.getSelectedFile():null);
	}
//NOVO
	public void novo(File mind){
		if(mind==null)return;
		if(!mind.toString().endsWith(".mind"))mind=new File(mind+".mind");
		try{
			final PrintWriter writer=new PrintWriter(this.link=mind,"UTF-8");
			writer.println("<mind fontName=\""+TreeUI.getFonte().getName()+"\" fontSize=\""+TreeUI.getFonte().getSize()+"\" fontStyle=\""+TreeUI.getFonte().getStyle()+"\">");
			writer.println("	<mod border=\"0\" color=\"(0,255,255)\" icons=\"\" title=\""+TreeUI.getLang().get("T_M","New Mind Map")+"\" x=\"0\" y=\"0\"><text/></mod>");
			writer.println("</mind>");
			writer.close();
		}catch(Exception erro){
			MindSortUI.mensagem(
					MindSortUI.getLang().get("M_Err3","Error: Couldn't create .mind file!")+"\n"+erro,
					MindSortUI.Options.ERRO);
		}
		getUI().getJanela().setTitle(mind.getName()+" - "+link);
	}
//ABRIR
	public void abrir(File mind){
		if(mind==null)return;
		tree.clear();
		Tree.getMestre().setTitle(MindSortUI.getLang().get("M_L","Loading..."));
		tree.getActions().setFocusOn(new Objeto[]{Tree.getMestre()});
		tree.setEnabled(false);
		tree.setVisible(false);
		new Thread(new Runnable(){ 
			public void run(){
				try{
					final Document tags=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(link=mind);
					final Element mindTag=tags.getDocumentElement();
					tree.addTree(mindTag,true);
				}catch(Exception erro){
					MindSortUI.mensagem(
							MindSortUI.getLang().get("M_Err4","Error: Couldn't open .mind file!")+"\n"+erro,
							MindSortUI.Options.ERRO);
				}
				getUI().getJanela().setTitle(mind.getName()+" - "+link);
				tree.getActions().setFocusOn(new Objeto[]{Tree.getMestre()});
				tree.setEnabled(true);
				tree.setVisible(true);
			}
		}).start();
	}
//SALVAR
	private boolean saving=false;
	public void salvar(File mind){
		saving=true;
		salvar(mind,5);	//TENTA SALVAR 5 VEZES
		saving=false;
	}
	private void salvar(File mind,int tentativas){
		if(mind==null)return;
		if(tentativas<=0){
			MindSortUI.mensagem(
					MindSortUI.getLang().get("M_Av5","Number of attempts exceeded: File unavailable!"),
					MindSortUI.Options.AVISO);
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
				MindSortUI.mensagem(
						MindSortUI.getLang().get("M_Err5","Error: Could not create .temp file!")+"\n"+erro,
						MindSortUI.Options.ERRO);
			}
			if(!tempMind.exists())salvar(mind,tentativas-1);	//RETRY
			writeFile(mind);			//SALVA ALTERAÇÕES EM MIND
			if(mind.exists()&&tempMind.exists()){
				tempMind.delete();		//APAGA TEMP_MIND
			}else salvar(mind,tentativas-1);					//RETRY
		}
		if(getUI().getJanela().getTitle().startsWith("*")) {
			getUI().getJanela().setTitle(getUI().getJanela().getTitle().substring(1));
		}
	}
	public boolean salvarAntes(){
		if(!getUI().getJanela().getTitle().startsWith("*"))return true;
		switch(JOptionPane.showConfirmDialog(null,
				MindSortUI.getLang().get("M_Menu_F_S_Tx","Save changes?"),
				MindSortUI.getLang().get("M_Menu_F_S_Ti","Save .mind"),
				JOptionPane.YES_NO_CANCEL_OPTION)){
			case JOptionPane.YES_OPTION:
				if(link==null)novo(choose(
						MindSortUI.getLang().get("M_Menu_F_S","Save"),
						getImage("Salvar"),true));
				salvar(link);
			break;
			case JOptionPane.NO_OPTION:
				getUI().getJanela().setTitle(getUI().getJanela().getTitle().substring(1));
			break;
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:	return false;
		}
		return true;
	}
	public void writeFile(File mind){
		try{
			final BufferedWriter mindFile=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mind),StandardCharsets.UTF_8));
			mindFile.write(tree.getText(tree.getObjetos()));
			mindFile.close();
		}catch(IOException erro){
			MindSortUI.mensagem(
					MindSortUI.getLang().get("M_Err6","Error: Unable to write to .mind file!")+"\n"+erro,
					MindSortUI.Options.ERRO);
		}
	}
//EXPORTAR
	public void exportar(File png){
		if(png==null)return;
		if(!png.toString().endsWith(".png"))png=new File(png+".png");
	    try{
			ImageIO.write((RenderedImage)tree.getImage(tree.getObjetos()),"png",png);
		}catch(IOException erro){
			MindSortUI.mensagem(
					MindSortUI.getLang().get("M_Err6","Error: Couldn't open .png file!")+"\n"+erro,
					MindSortUI.Options.ERRO);
		}
	}
//FECHAR
	public void fechar(){
		if(saving)return;	//IGNORA FECHAR
		System.exit(0);
	}
//.INI
	public boolean getIniConfig(){
		final File iniLink=new File(System.getProperty("user.dir")+"/"+ini);
		final JFrame janela=getUI().getJanela();
		final Janela janelaTexto=getUI().getJanelaTexto();
		if(iniLink.exists())try{
			for(String linha:Files.readAllLines(iniLink.toPath(),StandardCharsets.UTF_8)){
				final Matcher match=Pattern.compile("^\t{0,}([^=\n]+)=+").matcher(linha);
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
					case "fonte":						getUI().setTreeFont(getFont(linha));												break;
				//FUNDO
					case "showGrid":					getUI().getShowGridButton().doToggle(getBoolean(linha));					break;
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
					case "lineWrap":					getUI().getLineWrapButton().doToggle(getBoolean(linha));					break;
					case "showAllChars":				getUI().getShowAllCharsButton().doToggle(getBoolean(linha));				break;
					case "separarTextWindow":			getUI().getSeparateTextButton().doToggle(getBoolean(linha));				break;
					case "autoFocarTextWindow":			getUI().getAutoFocusTextButton().doToggle(getBoolean(linha));				break;
				//LIMITES
					case "objetosLimite":				tree.setObjetosLimite(getInteger(linha));									break;
					case "undoRedoLimite":				tree.getUndoRedoManager().setDoLimite(getInteger(linha));					break;
					case "transparenciaNivel":			janelaTexto.getTransparentInstance().setTransparencia(getInteger(linha));	break;
				}
			}
		}catch(IOException erro){
			MindSortUI.mensagem(
					MindSortUI.getLang().get("M_Err7","Error: Could not read .ini file!")+"\n"+erro,
					MindSortUI.Options.ERRO);
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
	public boolean setConfigIni(){
		final File link=new File(System.getProperty("user.dir")+"/"+ini);
		final JFrame janela=getUI().getJanela();
		final Janela janelaTexto=getUI().getJanelaTexto();
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
			writer.println("		font="+TreeUI.getFonte().getName()+","+TreeUI.getFonte().getStyle()+","+TreeUI.getFonte().getSize());
			writer.println("	[Fundo]");
			writer.println("		showGrid="+getUI().getShowGridButton().isPressed());
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
			writer.println("		lineWrap="+getUI().getLineWrapButton().isPressed());
			writer.println("		showAllChars="+getUI().getShowAllCharsButton().isPressed());
			writer.println("		separarTextWindow="+getUI().getSeparateTextButton().isPressed());
			writer.println("		autoFocarTextWindow="+getUI().getAutoFocusTextButton().isPressed());
			writer.println("	[Limites]");
			writer.println("		objetosLimite="+tree.getObjetosLimite());
			writer.println("		undoRedoLimite="+tree.getUndoRedoManager().getDoLimite());
			writer.println("		transparenciaNivel="+janelaTexto.getTransparentInstance().getTransparencia());
			writer.close();
			return true;
		}catch(Exception erro){
			MindSortUI.mensagem(
					MindSortUI.getLang().get("M_Err8","Error: Unable to write to .ini file!")+"\n"+erro,
					MindSortUI.Options.ERRO);
			return false;
		}
	}
//FOLDERS
	public boolean updateIconFolder(){
		final File icons=new File(System.getProperty("user.dir")+"/Icons");
		if(!icons.exists())return false;
		tree.getUI().getPopup().setIconePasta(icons);
		return true;
	}
	public boolean updateLanguageFolder(String idiomaFiltro){
		final File langs=new File(System.getProperty("user.dir")+"/Languages");
		if(!langs.exists())return false;
		MindSortUI.addLanguage(langs,idiomaFiltro,"M");
		TreeUI.addLanguage(langs,idiomaFiltro,"T");
		return true;
	}
}