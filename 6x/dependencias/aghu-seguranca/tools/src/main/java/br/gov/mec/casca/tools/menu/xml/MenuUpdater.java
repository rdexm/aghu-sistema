package br.gov.mec.casca.tools.menu.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Favorito;
import br.gov.mec.casca.model.Menu;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.tools.updater.HibernateHelper;

public class MenuUpdater {
	
	/**
	 * Identifica o nome da Aplicação AGHU externa da versão 5 ou 6 para uso 
	 * concomitante em ambiente de produção junto com uma versão superior de AGHU 
	 * (vide tarefa #45444)
	 * OBS1: A constante abaixo representa o atributo Aplicacao.nome
	 * OBS2: Após a migração total para a versão AGHUse (7.0) as constantes
	 * abaixo podem ser apagadas deste script bem como o código relacionado 
	 * à elas.
	 */
	private static final String NOME_APLICACAO_AGHU_VERSAO_5 = "AGHU5";
	private static final String NOME_APLICACAO_AGHU_VERSAO_6 = "AGHU6";

	private boolean simulacao;
	
	private boolean priorizarXML = false;
	
	private String aplicacao;
	
	private String versao;

	private String diretorio;
	
	private Stack<Menu> menuStack;
	
	private MenuCleaner menuCleaner;
	
	private Set<String> conjuntoURLunicas = new HashSet<String>();
	
	private Map<String, Aplicacao> cacheAplicacao = new HashMap<String, Aplicacao>(3);
	
	private Map<String, Menu> cacheMenu = new HashMap<String, Menu>();
	
	private Set<String> cacheMenuAGHU5 = new HashSet<String>();
	private Set<String> cacheMenuAGHU6 = new HashSet<String>();
	
	private static Logger log = Logger.getLogger(MenuUpdater.class);

	private MenuUpdater() {
		menuCleaner = new MenuCleaner();
	}
	
	private void processarArquivo() throws FileNotFoundException, XMLStreamException {
		log.info(String.format("Usando dir %s", this.diretorio));
		log.info(String.format("Modo de simulação: %s", this.simulacao ? "ligado" : "desligado"));
		log.info(String.format("Versão do arquivo de menu: %s", this.versao));

		String nomeArquivo = this.diretorio + File.separator
				+ XMLWriter.NOME_ARQUIVO;
		
		if (this.versao != null && !this.versao.isEmpty()) {
			nomeArquivo = nomeArquivo + XMLWriter.SEPARADOR + this.versao;
		} else {
			log.error("Versão do arquivo de menu não informado via parâmetro 'versao'.");
			System.exit(1);
		}

		nomeArquivo = nomeArquivo + XMLWriter.EXTENSAO;
		File arquivo = new File(nomeArquivo);
		if (arquivo.exists()) {
			InputStream in = new java.io.FileInputStream(arquivo);
			log.info("Processando arquivo " + arquivo.getName());
			this.execute(in);
		} else {
			log.error("Arquivo de menu "+ nomeArquivo + " não encontrado ou está com erro.");
		}
	}
	
	private void execute(InputStream in) throws XMLStreamException {
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlReader = xmlFactory.createXMLStreamReader(in);
		
		HibernateHelper.getSingleton().beginTransaction();
		
		menuStack = new Stack<Menu>();
		
		montarCacheMenusAGHU5e6();
		
		try {
			
			while (xmlReader.hasNext()) {
				xmlReader.next();
				
				if (xmlReader.getEventType() == START_ELEMENT) {
					String name = xmlReader.getName().toString();
		
					if (name.equals("menu")) {
						handleTagMenu(xmlReader);
					}
				}
				
				if (xmlReader.getEventType() == END_ELEMENT) {
					if (!menuStack.isEmpty()) {
						menuStack.pop();
					}
				}
			}
			
			evitarExclusaoAplicacoesAGHUExternas(); // (vide tarefa #45444)

			menuCleaner.cleanup();
			
			restaurarFavoritos();
			
			if (!simulacao) {
				HibernateHelper.getSingleton().commit();
				HibernateHelper.getSingleton().getSession().flush();
				HibernateHelper.getSingleton().getSession().close();
			} else {
				HibernateHelper.getSingleton().rollback();
				log.info("Fim da simulação");
			}			
		} catch (Exception e) {
			HibernateHelper.getSingleton().rollback();
			e.printStackTrace();
		}		
	}
	
	private Aplicacao obterAplicacao(String nome) {
		Aplicacao aplicacao = null;
		if (cacheAplicacao.containsKey(nome)) {
			aplicacao = cacheAplicacao.get(nome);
		}
		
		if (aplicacao == null) {
			LockMode lockMode = isSimulacao() ? LockMode.NONE : LockMode.UPGRADE;
			
			DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);
			criteria.setLockMode(lockMode);
			criteria.add(Restrictions.eq(Aplicacao.Fields.NOME.toString(), nome));
			aplicacao = (Aplicacao)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
			cacheAplicacao.put(nome, aplicacao);
		}
		
		return aplicacao;
	}
	
	private void restaurarFavoritos() throws Exception {
		Set<String> conjuntoURLs = new HashSet<String>();
		for (String usuarioFavorito : menuCleaner.getCacheFavoritosPorUsuario().keySet()) {			
			Usuario usuario = obterUsuarioAtivo(usuarioFavorito);
			if (usuario == null) {
				continue;
			}
			
			conjuntoURLs = menuCleaner.getCacheFavoritosPorUsuario().get(usuarioFavorito);
			int contadorOrdem = 0;
			for (String url : conjuntoURLs) {
				Menu menu = null;
				if (!cacheMenu.containsKey(url.trim())) {
					menu = obterMenu(url);
					cacheMenu.put(url.trim(), menu);			
				}
				menu = cacheMenu.get(url.trim());
				
				if (menu == null || menu.getAtivo().equals(DominioSimNao.N)) {
					continue;
				}

				Favorito favorito = new Favorito();
				favorito.setUsuario(usuario);
				favorito.setDataCriacao(new Date());
				favorito.setOrdem(contadorOrdem++); 
				favorito.setMenu(menu);
				
				log.info("Restaurando o menu favorito ["+menu.getUrl()+"] para o usuário ["+usuarioFavorito+"]");

				HibernateHelper.getSingleton().salvar(favorito);				
			}
		}
	}

	/** 
	 * Evita que menus de aplicações externas do AGHU versões 5 ou 6
	 * sejam apagados (tarefa #45444)
	 * Após a migração total para a versão AGHUse (7.0) o método abaixo
	 * pode ser apagado deste script.
	 */
	@SuppressWarnings("unchecked")
	private void evitarExclusaoAplicacoesAGHUExternas() {
		for (Aplicacao aplicacao : listarAplicacoes()) {
			if (aplicacao.getExterno() 
					&& (aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_5)
							|| aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_6))) {
				for (Menu menu : aplicacao.getMenus()) {
					menuCleaner.addMenu(menu.getId());
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<Aplicacao> listarAplicacoes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);
		return criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
	}
	
	private void montarCacheMenusAGHU5e6() {
		for (Aplicacao aplicacao : listarAplicacoes()) {
			if (aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_5)
					|| aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_6)) {
				for (Menu menu : aplicacao.getMenus()) {
					if (aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_5)) {
						cacheMenuAGHU5.add(menu.getUrl());
						continue;
					}
					if (aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_6)) {
						cacheMenuAGHU6.add(menu.getUrl());
					}
				}
			}
		}		
	}
	
	private Usuario obterUsuarioAtivo(String login) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Usuario.class);
		criteria.add(Restrictions.eq(Usuario.Fields.LOGIN.toString(), login))
				.add(Restrictions.eq(Usuario.Fields.ATIVO.toString(), true));
		
		return (Usuario) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
	}	
	
	private Menu obterMenu(String aplicacao, Menu menuPai, Integer ordem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
				
		if (menuPai == null) {
			criteria.add(Restrictions.isNull(Menu.Fields.MENU_PAI.toString()));
		} else {
			criteria.add(Restrictions.eq(Menu.Fields.MENU_PAI.toString(), menuPai));
		}
		criteria.add(Restrictions.eq(Menu.Fields.ORDEM.toString(), ordem));		
		criteria.createCriteria(Menu.Fields.APLICACAO.toString())
			.add(Restrictions.eq(Aplicacao.Fields.NOME.toString(), aplicacao));
		
		return (Menu)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
	}
	
	private Menu obterMenu(final String url) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		criteria.add(Restrictions.eq(Menu.Fields.URL.toString(), url.trim()));
		
		Menu m = null;
		try {
			m = (Menu)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
		} catch (HibernateException e) {
			throw new Exception("Mais de um registro encontrado no banco para o Menu: " + url, e);
		}
		
		return m;
	}

	private Menu obterMenu(String url, String aplicacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
				
		if (url == null) {			
			criteria.add(Restrictions.isNull(Menu.Fields.URL.toString()));
		} else {
			criteria.add(Restrictions.eq(Menu.Fields.URL.toString(), url));
		}
				
		criteria.createCriteria(Menu.Fields.APLICACAO.toString()).add(Restrictions.eq(Aplicacao.Fields.NOME.toString(), aplicacao));
		
		return (Menu)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
	}
	
	private Menu obterMenu(String nome, String url, String aplicacao, Menu menuPai) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		
		if (nome != null) {
			criteria.add(Restrictions.eq(Menu.Fields.NOME.toString(), nome));
		}
		
		if (url == null) {			
			criteria.add(Restrictions.isNull(Menu.Fields.URL.toString()));
		} else {
			criteria.add(Restrictions.eq(Menu.Fields.URL.toString(), url));
		}
		
		if (menuPai == null) {
			criteria.add(Restrictions.isNull(Menu.Fields.MENU_PAI.toString()));
		} else {
			criteria.add(Restrictions.eq(Menu.Fields.MENU_PAI.toString(), menuPai));
		}
		
		criteria.createCriteria(Menu.Fields.APLICACAO.toString()).add(Restrictions.eq(Aplicacao.Fields.NOME.toString(), aplicacao));
		
		return (Menu)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
	}
	
	private Menu obterMenu(String nome, String aplicacao, Menu menuPai) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		
		criteria.add(Restrictions.eq(Menu.Fields.NOME.toString(), nome));
				
		if (menuPai == null) {
			criteria.add(Restrictions.isNull(Menu.Fields.MENU_PAI.toString()));
		} else {
			criteria.add(Restrictions.eq(Menu.Fields.MENU_PAI.toString(), menuPai));
		}
		
		criteria.createCriteria(Menu.Fields.APLICACAO.toString()).add(Restrictions.eq(Aplicacao.Fields.NOME.toString(), aplicacao));
		
		return (Menu)criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
	}
	
	private Integer obterMaiorOrdemNaMesmaHierarquia(String aplicacao, Menu menuPai) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		if (menuPai == null) {
			criteria.add(Restrictions.isNull(Menu.Fields.MENU_PAI.toString()));
		} else {
			criteria.add(Restrictions.eq(Menu.Fields.MENU_PAI.toString(), menuPai));
		}
		criteria.createCriteria(Menu.Fields.APLICACAO.toString())
			.add(Restrictions.eq(Aplicacao.Fields.NOME.toString(), aplicacao));
		
		criteria.setProjection(Projections.max(Menu.Fields.ORDEM.toString()));

		return (Integer) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
	}

	private void handleTagMenu(XMLStreamReader xmlReader) throws Exception {
		String nome = xmlReader.getAttributeValue(null, "nome");
		String url = xmlReader.getAttributeValue(null, "url");
		String aplicacao = xmlReader.getAttributeValue(null, "aplicacao");
		String ordemStr = xmlReader.getAttributeValue(null, "ordem");
		Integer ordem = Integer.valueOf(ordemStr);
		boolean excluiuMenuAntigo = false;

		Menu menuPai = menuStack.isEmpty() ? null : menuStack.peek();
		
		// Caso uma das caches de aplicações do AGHU v5 ou v6 contenha a URL que está 
		// sendo lido neste momento do XML entõa não faz nada, fica valendo o item de
		// menu que já estava no banco
		if (url != null && (cacheMenuAGHU5.contains(url) || cacheMenuAGHU6.contains(url))) {
			log.warn(String.format("A URL [%s] já ESTÁ ASSOCIADA a uma outra versão mais antiga do AGHU na tabela CSC_APLICACAO e por isso NÃO será associada a aplicação [%s].", url, aplicacao));
			// Injeta um item fake de menu que será desempilhado no fluxo 
			// principal agim de evitar uma referência de menu pai errada.
			menuStack.push(new Menu(null, "Item temporário", null, 0));
			return;
		}

		try {
			// Verifica se já não há um item na mesma posição do menu que será
			// alterado ou incluído, e retira-o, para respeitar a chave
			// única ID_APLICACAO, PARENT_ID, ORDEM existente na model Menu			
			Menu menuAntigo = obterMenu(aplicacao, menuPai, ordem);
			
			// Se encontrou um item de menu cujo o pai é o mesmo, mesma ordem
			// mas com nomes diferentes, infere então que são itens diferentes
			// e 'move' o menu antigo para a maior ordem dentro da mesma 
			// hierarquia para dar lugar ao item que está no XML.
			if (menuAntigo != null && !menuAntigo.getNome().equals(nome)) {
				Integer novaOrdemAntigo = obterMaiorOrdemNaMesmaHierarquia(aplicacao, menuPai) + 1;
				log.warn("Item de "+ menuAntigo + " da base de dados será movido para a posição "
						+ novaOrdemAntigo
						+ " dentro da mesma hierarquia para não colidir com o item de menu do XML cujo nome é ["
						+ nome + "]");
				menuAntigo.setOrdem(novaOrdemAntigo);
				menuAntigo = (Menu)HibernateHelper.getSingleton().salvar(menuAntigo);
				menuAntigo = null; // Anula para forçar uma nova consulta ao item correto
			}
			
			// Se não conseguir, tenta obter menu primeiramente pela chave
			// única que contempla ID_APLICACAO, PARENT_ID e NOME 			
			if (menuAntigo == null) { // CSC_MENU_UK1
				menuAntigo = obterMenu(nome, aplicacao, menuPai);
			}
			
			// Se ainda não conseguir, tenta obter pela combinação de um ou 
			// mais dos atribudos: nome, url, aplicação ou menu pai
			if (menuAntigo == null) { 
				menuAntigo = obterMenu(nome, url, aplicacao, menuPai);
				
				// Se ainda não obter nenhum item tenta apenas pela URL
				if (menuAntigo == null && url != null) {
					Menu itemMenuComMesmaURL = obterMenu(url, aplicacao);
					if (itemMenuComMesmaURL != null && itemMenuComMesmaURL.getAplicacao().getNome().equalsIgnoreCase(aplicacao)) {
						if (isPriorizarXML()) {
							excluir(itemMenuComMesmaURL);
							menuCleaner.addMenu(itemMenuComMesmaURL.getId());
							conjuntoURLunicas.remove(itemMenuComMesmaURL.getAplicacao().getNome()+itemMenuComMesmaURL.getUrl());
						} else {
							throw new RuntimeException(String.format(
									"Encontrado menu '%s' com mesma URL '%s'",
									itemMenuComMesmaURL.getNome(),
									itemMenuComMesmaURL.getUrl()));
						}
					}
				}							
			}	
			
			if (menuAntigo != null 
					&& ((menuAntigo.getUrl() != null && url != null && !menuAntigo.getUrl().equals(url))
						|| (menuAntigo.getUrl() == null && url != null)) ) {
				if (!isPriorizarXML()) {
					throw new RuntimeException(String.format("O item de '%s' já " +
							"existe na base na mesma hierarquia e conflita com o " +
							"novo item cuja URL é '%s'. Verifique o arquivo XML " +
							"e altere a posição na hierarquia ou a ordem do item.", menuAntigo, url));
				}
				
				excluir(menuAntigo);
				excluiuMenuAntigo = true;
			}

			Menu menu = menuAntigo;
			if (menuAntigo == null || excluiuMenuAntigo) {
				log.info(String.format("Criando menu '%s' com URL '%s' para a aplicação '%s'...", nome, url, aplicacao));
				
				menu = new Menu();
				menu.setDataCriacao(new Date());
			}
			
			menu.setNome(nome);
			menu.setUrl(url);
			menu.setAplicacao(obterAplicacao(aplicacao));
			menu.setMenuPai(menuPai);
			
			menu.setOrdem(Integer.valueOf(ordem));
			
			if (isPriorizarXML() || menu.getAtivo() == null) {
				String ativo = xmlReader.getAttributeValue(null, "ativo");
				menu.setAtivo(Boolean.parseBoolean(ativo) ? DominioSimNao.S : DominioSimNao.N);
			}
			
			menu.setClasseIcone(xmlReader.getAttributeValue(null, "classe-icone"));

			if (menu.getUrl() != null) {			
				Menu menuComMesmaURL = obterMenu(menu.getUrl(), menu.getAplicacao().getNome());
				if ((menuComMesmaURL != null && menuAntigo != null
						&& !menuComMesmaURL.equals(menu) 
						&& !menu.equals(menuAntigo))
						|| !conjuntoURLunicas.add(menu.getAplicacao().getNome()+url.trim())) {
					if (isPriorizarXML()) {
						excluir(menuComMesmaURL);
					} else {
						throw new RuntimeException(String.format("Encontrado menu '%s' com mesma URL '%s'", menuComMesmaURL.getNome(), menuComMesmaURL.getUrl()));
					}
				}
			}
			
			menu = (Menu)HibernateHelper.getSingleton().salvar(menu);
			
			if (menuAntigo != null && !menuAntigo.equals(menu)) {
				menuCleaner.addMenu(menuAntigo.getId());
				conjuntoURLunicas.remove(menuAntigo.getAplicacao().getNome()+menuAntigo.getUrl());
			}
			
			menuCleaner.addMenu(menu.getId());
			
			menuStack.push(menu);
	
		} catch (NonUniqueResultException nure) {
			throw new RuntimeException(
					String.format(
							"Inconsistencia no banco de dados. Registros duplicados: Menu[nome='%s', url='%s', aplicacao='%s', parent_id='%s']",
							nome, url, aplicacao, menuPai == null ? null : menuPai.getId()), nure);
		} catch (ConstraintViolationException cve) {
			throw new RuntimeException(
					String.format(
							"Inconsistencia no banco de dados. Restrição violada: Menu[nome='%s', url='%s', aplicacao='%s', parent_id='%s']",
							nome, url, aplicacao, menuPai == null ? null : menuPai.getId()), cve);
		} catch (Exception e) {
			throw new RuntimeException(
					String.format(
							"Problema no tratamento da tag <menu>: Menu[nome='%s', aplicacao='%s', parent_id='%s']",
							nome, aplicacao, menuPai == null ? null : menuPai.getId()), e);
		}
	}
	
	private void excluir(Menu menuParaExcluir) {
		HibernateHelper.getSingleton().delete(menuParaExcluir);
		HibernateHelper.getSingleton().flush();
		log.warn(String.format("Excluiu menu '%s' com URL '%s' para a aplicação '%s'...", menuParaExcluir.getNome(), menuParaExcluir.getUrl(), menuParaExcluir.getAplicacao()));
	}

	public void setSimulacao(boolean simulacao) {
		this.simulacao = simulacao;
	}
	
	public boolean isSimulacao() {
		return simulacao;
	}	

	public void setDiretorio(String diretorio) {
		this.diretorio = diretorio;
	}
	
	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	public String getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
	}

	public boolean isPriorizarXML() {
		return priorizarXML;
	}
	
	public void setPriorizarXML(boolean priorizarXML) {
		this.priorizarXML = priorizarXML;
	}	
	
	public static void main(String[] args) throws Exception {
		
		MenuUpdater updater = new MenuUpdater();

		if (args.length > 0) {
			
			boolean paramSimular = true;
			// Caso o parâmetro de simulação seja diferente de 'true' ou
			// 'false', assume true pois é opcional
			if (args[1] != null && (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false"))) {
				System.out.println("Parâmetro [simular] não informado, assumindo 'true'");
			} else {
				paramSimular = Boolean.parseBoolean(args[1]);
			}
			updater.setSimulacao(paramSimular);
			
			if (args.length >= 3 && args[2] != null && !args[2].equals("${aplicacao}")) {
				updater.setAplicacao(args[2].toUpperCase());
			} else {
				System.err.println("Informe o nome da aplicação através do parâmetro [aplicacao]");
				System.exit(2);
			}
			
			if (args.length >= 4 && args[3] != null && !args[3].equals("${versao}")) {
				updater.setVersao(args[3]);
			} else {
				System.err.println("Informe a versão da aplicação através do parâmetro [versao]");
				System.exit(3);
			}

			// Caso o parâmetro de priorizarXML seja diferente de 'true' ou
			// 'false', assume false para manter a compatibilidade do script
			// que é a de manter a tabela de menus mais íntegra possível.
			updater.setPriorizarXML(false);
			if (args.length >= 5 && args[4] != null && !args[4].equals("${priorizarXML}")
					&& (!args[4].equalsIgnoreCase("true") && !args[4].equalsIgnoreCase("false"))) {
				System.out.println("Parâmetro [priorizarXML] não informado, assumindo 'false'");
			} else {
				updater.setPriorizarXML(Boolean.parseBoolean(args[4]));
			}
			
			updater.setDiretorio(args[0] + File.separator + updater.getAplicacao() + File.separator + updater.getVersao());
			
			String fileArg = null;
			if (args.length >= 6) {
				fileArg = args[5];
			}
			HibernateHelper.loadInstance(fileArg);
			
			updater.processarArquivo();
		} else {
			System.err.println("Forma de utilização: MenuUpdater <diretorio> [simular] [local]");
		}
	}

}