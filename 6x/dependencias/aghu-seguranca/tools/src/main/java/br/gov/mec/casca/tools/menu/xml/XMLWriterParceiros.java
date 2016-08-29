package br.gov.mec.casca.tools.menu.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.casca.model.Menu;
import br.gov.mec.casca.tools.updater.HibernateHelper;

import static org.apache.commons.lang3.StringEscapeUtils.escapeXml11;

public class XMLWriterParceiros {
	
	public final static String SEPARADOR= "-";
	
	public final static String EXTENSAO = ".xml";
	
	public final static String IDENT = "	";
	
	public final static String NOME_ARQUIVO = "menu";
	
	private boolean simulacao = false;
	
	private String local;
	
	private String ambiente;

	private String diretorio;

	public void setSimulacao(boolean simulacao) {
		this.simulacao = simulacao;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public void setDiretorio(String diretorio) {
		this.diretorio = diretorio;
	}

	public static void main(String[] args) throws Exception {
		XMLWriterParceiros xmlWriter = new XMLWriterParceiros();
		if(args != null && args.length > 0) {
			if(args[0] != null) {
				xmlWriter.setDiretorio(args[0]);
			}
			if(args.length >= 2 && args[1] != null) {
				boolean simulacao = Boolean.parseBoolean(args[1]);
				xmlWriter.setSimulacao(simulacao);
			}
			if(args.length >= 3 && !args[2].isEmpty()) {
				xmlWriter.setLocal(args[2]);
			}
			if(args.length >= 4 && !args[3].isEmpty()) {
				xmlWriter.setAmbiente(args[3]);
			}			
			
			xmlWriter.execute();
		} else {
			System.err.println("Forma de utilização: XMLWriter <diretorio> [simular] [local]");
		}
	}
	
	private void execute() throws FileNotFoundException {
		HibernateHelper.loadInstance(null);
		Session session = HibernateHelper.getSingleton().getSession();

		System.out.printf("Usando dir %s%n", this.diretorio);
		System.out.printf("Modo de simulacao: %s%n", this.simulacao ? "ligado" : "desligado");
		System.out.printf("Local usado: %s%n", this.local == null ? "nenhum" : this.local);
		
		PrintWriter writer = createWriter(NOME_ARQUIVO);
		
		List<Menu> menus = recuperarMenus(session);
		
		gerarMenus(menus, writer);
		
		session.close();

	}
	
	protected PrintWriter createWriter(String nomeArquivo) throws FileNotFoundException {
		
		OutputStream out = System.out;

		if(!this.simulacao) {
			if(this.local != null && !this.local.isEmpty()) {
				nomeArquivo = nomeArquivo + SEPARADOR + this.local;
			}
			if(this.ambiente != null && !this.ambiente.isEmpty()) {
				nomeArquivo = nomeArquivo + SEPARADOR + this.ambiente;
			}			
			if(this.diretorio != null && !this.diretorio.isEmpty()) {
				nomeArquivo = this.diretorio + File.separator + nomeArquivo;
			}
			out = new java.io.FileOutputStream(nomeArquivo + EXTENSAO);
		}

		PrintWriter writer = new PrintWriter(out);
		return writer;
	}

	private List<Menu> recuperarMenus(Session session) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		
		criteria.add(Restrictions.isNull(Menu.Fields.MENU_PAI.toString()));
//		criteria.add(Restrictions.eq(Menu.Fields.ATIVO.toString(),DominioSimNao.S));
		criteria.add(Restrictions.eq(Menu.Fields.APLICACAO_ID.toString(), 1));
			
		criteria.addOrder(Order.asc(Menu.Fields.ORDEM.toString()));
		
		List<Menu> list = criteria.getExecutableCriteria(session).list();
		return list;
	}
	
	private void gerarMenus(List<Menu> menus, PrintWriter writer) {
		writer.println("<menus>");
		
		gerarMenus(menus, writer, IDENT);
		
		writer.println("</menus>");
		
		writer.flush();
		writer.close();
	}
	
	private void gerarMenus(List<Menu> menus, PrintWriter writer, String ident) {
		for (Menu menu : menus) {

			if (menu.getAplicacao().getNome().equals("AGHU")
				|| menu.getAplicacao().getNome().equals("AGHU5")
				|| menu.getAplicacao().getNome().equals("CASCA")){
			
			writer.printf("%s<menu nome=\"%s\"", ident, escapeXml11(menu.getNome()));
			
			if (menu.getUrl() != null) {
				writer.printf(" url=\"%s\"", escapeXml11(menu.getUrl()));
			}
			
			writer.printf(" ordem=\"%s\"", menu.getOrdem());	
			
			if ((menu.getNome().equals("Administrar Servidores")||menu.getNome().equals("Cadastros")) && menu.getAtivo()==DominioSimNao.N){
				writer.printf(" ativo=\"%s\"", "true");
			}
			else{
			writer.printf(" ativo=\"%s\"", menu.getAtivo().equals(DominioSimNao.S));
			}
			
			if (menu.getClasseIcone() != null) {
				writer.printf(" classe-icone=\"%s\"", menu.getClasseIcone());
			}
			
			writer.printf(" aplicacao=\"%s\"", "AGHU");
			
			if (menu.getMenus() != null && !menu.getMenus().isEmpty()) {
				writer.println(">");
				gerarMenus(menu.getMenus(), writer, ident + IDENT);
				writer.printf("%s</menu>%n", ident);
			} else {
				writer.println("/>");
			}
		}
		}
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}
}
