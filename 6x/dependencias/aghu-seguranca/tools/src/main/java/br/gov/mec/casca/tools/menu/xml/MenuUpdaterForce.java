package br.gov.mec.casca.tools.menu.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.NonUniqueResultException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.casca.menu.vo.FavoritoVO;
import br.gov.mec.casca.menu.vo.PalavraChaveVO;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Dashboard;
import br.gov.mec.casca.model.Favorito;
import br.gov.mec.casca.model.Menu;
import br.gov.mec.casca.model.PalavraChaveMenu;
import br.gov.mec.casca.tools.updater.HibernateHelper;

public class MenuUpdaterForce {
	private final String nomeMenuVirtual = "Menu virtual - ID original";
	private final Integer ultimoNumeroOrdem = 99999999;
	
	// Existe o menu do SA que tem favorito para TODOS usuarios
	// Para ele, não será feito processamento (possui ~20.000 favoritos)
	private final String urlMenuExcecao = "/apps/qms/CustomOccurrenceTypeSelectorApplication.jsp";
	Menu menuExcecaoAntigo = new Menu();
	Menu menuExcecaoNovo = null;
	
	
	private Map<String, Aplicacao> cacheAplicacao = new HashMap<String, Aplicacao>(3);
	private static Logger log = Logger.getLogger(MenuUpdaterForce.class);
	
	private boolean simulacao;
	private String aplicacao;
	private String versao;
	private String diretorio;

	private Stack<Menu> menuStack;
	
	private Integer menuQtd = 0;
	private Integer menuMenorId = 0;
	private Integer menuMaiorId = 0;
	
	private Set<FavoritoVO> favoritoVOList = new HashSet<FavoritoVO>();
	private Map<Integer, Menu> menuVirtualMap = new HashMap<Integer, Menu>();
	
	
	private List<PalavraChaveVO> palavras = new ArrayList<PalavraChaveVO>();
	
	

	private MenuUpdaterForce() {
		menuExcecaoAntigo.setUrl(urlMenuExcecao);
	}
	
	private void processarArquivo() throws FileNotFoundException, XMLStreamException, Exception {
		log.info(String.format("Usando dir %s", this.diretorio));
		log.info(String.format("Modo de simulação: %s", this.simulacao ? "ligado" : "desligado"));
		log.info(String.format("Versão do arquivo de menu: %s", this.versao));

		StringBuilder nomeArquivo = new StringBuilder(500);
		nomeArquivo.append(this.diretorio).append(File.separator).append(XMLWriter.NOME_ARQUIVO);
		
		if (this.versao != null && !this.versao.isEmpty()) {
			nomeArquivo.append(XMLWriter.SEPARADOR).append(this.versao);
		} else {
			log.error("Versão do arquivo de menu não informado via parâmetro 'versao'.");
			System.exit(1);
		}

		nomeArquivo.append(XMLWriter.EXTENSAO);
		File arquivo = new File(nomeArquivo.toString());
		if (arquivo.exists()) {
			InputStream in = new java.io.FileInputStream(arquivo);
			log.info("Processando arquivo " + arquivo.getName());
			this.execute(in);
		} else {
			log.error("Arquivo de menu "+ nomeArquivo + " não encontrado ou está com erro.");
		}
	}
	
	private void execute(InputStream in) throws XMLStreamException, Exception {
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlReader = xmlFactory.createXMLStreamReader(in);
		
		HibernateHelper.getSingleton().beginTransaction();
		
		// Lista para controlar o tempo de execução de cada etapa
		List<String> tempoIteracoes = new ArrayList<String>();
		long ultimaIteracao = new Date().getTime();
		tempoIteracoes.add("Inicio do processo: 0 segundos");
		
		
		// Inserido LOCK na tabela CSC_FAVORITO para que nenhum usuario adicione menus aos seus favoritos durante a execucao da rotina
		HibernateHelper.getSingleton().getSession().createSQLQuery("LOCK TABLE CASCA." + Favorito.Fields.NOME_TABELA.toString().toUpperCase() + " IN EXCLUSIVE MODE NOWAIT").executeUpdate();
		menuStack = new Stack<Menu>();
		
		try {
			// Armazena as faixas de valores para posteriormente fazer o algoritmo para guardar e atualziar menus com IDs virtuais
			this.verificarDadosMenu();
			tempoIteracoes.add("verificarDadosMenu(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			log.info("Maior ID de menu no BD:" + this.menuMaiorId);
			log.info("Menor ID de menu no BD:" + this.menuMenorId);
			log.info("Quantidade de menus no BD:" + this.menuQtd);
			
			//Carrega Palavra chave
			this.carregarPalavrasChave();
			tempoIteracoes.add("carregarPalavrasChave(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();

			// Atualiza a ordem de todos menus incrementalmente para evitar problemas com constraints do BD (UKs, FKs) 
			this.atualizarOrdemMenu();
			tempoIteracoes.add("atualizarOrdemMenu(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			// Armazena os favoritos em memória (VO - Value Object)
			this.guardarFavoritos();
			tempoIteracoes.add("guardarFavoritos(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			// Insere os menus virtuais no BD para serem relacionados aos favoritos (Menu.nome = 'Menu virtual - ID original 000000')
			this.inserirMenusVirtuais();
			tempoIteracoes.add("inserirMenusVirtuais(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
	
			// Atualiza os favoritos existentes com os IDs virutais dos menus criados
			this.atualizarFavoritos();
			tempoIteracoes.add("atualizarFavoritos(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
	
			// Remove todos registros da tabela de menus, exceto menus virtuais
			this.removerMenusAtuais();
			tempoIteracoes.add("removerMenusAtuais(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			// Cria os novos registros de menu a partir do arquivo XML
			int count = 0;
			while (xmlReader.hasNext()) {
				xmlReader.next();
				
				if (xmlReader.getEventType() == START_ELEMENT) {
					String name = xmlReader.getName().toString();
		
					if (name.equals("menu")) {
						handleTagMenu(xmlReader);
						count++;
					}
				}
				
				if (xmlReader.getEventType() == END_ELEMENT) {
					if (!menuStack.isEmpty()) {
						menuStack.pop();
					}
				}
			}
			
			log.info("------------------------------------------------------");
			log.info("Adicionados " + count + " itens de menu");
			log.info("------------------------------------------------------");
			
			tempoIteracoes.add("inserirMenusXML(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			// Atualiza os favoritos que estavam com os IDs de menus virtuais para IDs dos novos menus criados correspondentes
			this.restaurarFavoritos();
			tempoIteracoes.add("restaurarFavoritos(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			// Ajusta o menu de excecao
			this.atualizarMenuExcecao();
			tempoIteracoes.add("atualizarMenuExcecao(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			// Remove os menus virtuais criados
			this.removerMenusVirtuais();
			tempoIteracoes.add("removerMenusVirtuais(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			log.info("Reinserindo palavras chave");

			this.reinserirPalavrasChave();
			tempoIteracoes.add("reinserirPalavrasChave(): " + (new Date().getTime() - ultimaIteracao) + " segundos");
			ultimaIteracao = new Date().getTime();
			
			log.info("Reinserindo palavras chave - concluido");
	
			if (!simulacao) {
				HibernateHelper.getSingleton().commit();
				HibernateHelper.getSingleton().getSession().flush();
				log.info("Menu atualizado com sucesso.");
			} else {
				HibernateHelper.getSingleton().rollback();
				log.info("Fim da simulação");
			}
			
			// Impressão de resumo de tempos na execução do programa.
			log.info("=====================================");
			log.info(" >>> Tempo das iterações <<<");
			for (String iteracao : tempoIteracoes) {
				log.info(iteracao);
			}
			log.info("=====================================");
			
			HibernateHelper.getSingleton().getSession().close();
			
		} catch (Exception e) {
			HibernateHelper.getSingleton().rollback();
			HibernateHelper.getSingleton().getSession().close();
			throw new Exception(e);
		}		
	}
	
	/**
	 * Método para veriicar as quantidades de registros existentes na tabela de menus.
	 * Armazena os seguintes dados:
	 *     maior ID
	 *     menor ID
	 *     quantidade total de registros
	 */
	@SuppressWarnings("rawtypes")
	private void verificarDadosMenu() {
		Criteria criteria = HibernateHelper.getSingleton().getSession()
				.createCriteria(Menu.class)
				.setProjection(
						Projections.projectionList().add(Projections.min("id"))
								.add(Projections.max("id"))
								.add(Projections.count("id")));
		
		List list = criteria.list();
		Object[] valores = (Object[]) list.get(0);
		
		this.menuMenorId = (Integer) valores[0];
		this.menuMaiorId = (Integer) valores[1];
		this.menuQtd = (Integer) valores[2];
	}
	
	/**
	 * Método para atualizar o campo "ordem" de todos menus existentes no BD.
	 * O campo "ordem" é incrementado para todos itens para evitar que as operações posteriores resultem em problemas de violação de constraints. 
	 */
	@SuppressWarnings("unchecked")
	private void atualizarOrdemMenu() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		List<Menu> menuList = (List<Menu>) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
		
		log.info("Atualizando dados dos menus para remoção ...");
		
		Integer ordem = 1000;
		for (Menu menu : menuList) {
			menu.setOrdem(ordem);
			menu.setNome(menu.getNome() + " - " + ordem);
			menu.setMenuPai(null);
			
			if (menuExcecaoAntigo.getUrl().equals(menu.getUrl())) {
				menuExcecaoAntigo = menu;
			} 
			
			HibernateHelper.getSingleton().getSession().saveOrUpdate(menu);
			ordem++;
		}
		HibernateHelper.getSingleton().getSession().flush();
		
	}
	
	/**
	 * Método para armazenar os favoritos em memória (lista de VOs).
	 * Esta lista é armazenada para guardar os menus que eestão nos favoritos e depois atualizá-los com menus virtuais. Dessa maneira não é necessário deletar os favoritos existentes. 
	 */
	private void guardarFavoritos() {
		Criteria criteria = HibernateHelper.getSingleton().getSession()
				.createCriteria(Favorito.class);
		criteria.createAlias("menu", "menu");
		criteria.createAlias("menu.aplicacao", "aplicacao");
		criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("menu.id")).add(Projections.groupProperty("menu.url")).add(Projections.groupProperty("aplicacao.id")));
		
		// Restricao para nao considerar os favoritos do menu Excecao
		criteria.add(Restrictions.ne("menu." + Menu.Fields.URL.toString(), urlMenuExcecao));
		
		// Lista com itens de menu que estão nos favoritos
		this.popularFavoritoVO(criteria.list());
	}
	
	/**
	 * Método para popular a lista de VOs com favoritos em memória.
	 * @param favoritoList
	 */
	@SuppressWarnings("rawtypes")
	private void popularFavoritoVO(List favoritoList) {
		log.info("Tamanho da lista de favoritos com menus a serem virtualizados: " + favoritoList.size());
		FavoritoVO favoritoVO;
		for (Object favorito : favoritoList) {
			favoritoVO = new FavoritoVO();
			
			Object[] fav = (Object[]) favorito;
			favoritoVO.setMenuId((Integer) fav[0]);
			favoritoVO.setMenuUrl((String) fav[1]);
			favoritoVO.setMenuIdAplicacao((Integer) fav[2]);
			
			favoritoVOList.add(favoritoVO);
			log.info("Virtualizado favorito para o menu com URL: " + favoritoVO.getMenuUrl());
		}
	}
	
	/**
	 * Método para inserir os menus virtuais no BD (todos tem Menu.nome = 'Menu virtual - ID original 000000')
	 */
	private void inserirMenusVirtuais() {
		Aplicacao app = this.obterAplicacao("AGHU");
		
		Integer ordem = this.ultimoNumeroOrdem - this.menuQtd;

		for (FavoritoVO favoritoVO : this.favoritoVOList) {
			Menu menu = new Menu();
			
			menu.setNome(this.nomeMenuVirtual + " " + favoritoVO.getMenuId());
			menu.setUrl(favoritoVO.getMenuUrl());
			menu.setOrdem(ordem);
			menu.setAplicacao(app);
			menu.setAtivo(DominioSimNao.N);
			menu.setDataCriacao(new Date());
			
			HibernateHelper.getSingleton().getSession().persist(menu);
			HibernateHelper.getSingleton().getSession().flush();
			
			log.info("Inserido menu virtual com ID: " + menu.getId() +  " para a URL: " + menu.getUrl() + ". (menu original - ID: " + favoritoVO.getMenuId() + ")");

			favoritoVO.setMenuIdVirtual(menu.getId());
			menuVirtualMap.put(menu.getId(), menu);
			ordem++;
		}
	}
	
	/**
	 * Método para atualizar a os favoritos existentes com o ID dos menus virtuais criados
	 */
	private void atualizarFavoritos() throws Exception {
		for (FavoritoVO favoritoVO : favoritoVOList) {
			for (Favorito favorito : this.pesquisarFavoritos(favoritoVO.getMenuId())) {
				
				Menu menu = null;
				menu = menuVirtualMap.get(favoritoVO.getMenuIdVirtual());
				favorito.setMenu(menu);
				log.info("Atualizado favorito vinculado ao menu ID: " + favoritoVO.getMenuId() + "  para o menu virtual ID: " + favoritoVO.getMenuIdVirtual());
				HibernateHelper.getSingleton().getSession().persist(favorito);
			}
			HibernateHelper.getSingleton().getSession().flush();
		}
	}
	
	/**
	 * Método para remover todos itens de menu existentes, exceto os menus virtuais
	 */
	private void removerMenusAtuais() {
		int count = 1;
		for(Menu menu : this.pesquisarMenus(false)) {
			
			if (!urlMenuExcecao.equals(menu.getUrl())) {
			
				log.info("Removendo menu ID: " + menu.getId() + " com URL: " + menu.getUrl() + " com ordem: " + menu.getOrdem());
				
				List<Favorito> favoritoList = this.pesquisarFavoritos(menu.getId());
				
				if (favoritoList.size() > 0) {
					log.warn("Ainda existe favorito relacionado ao item de menu com ID: " + menu.getId() + " com URL: " + menu.getUrl() + " com ordem: " + menu.getOrdem());
				} else {
					HibernateHelper.getSingleton().getSession().delete(menu);
					count++;
					
					// Faz flush a cada 100 registros
					if (count % 100 == 0) {
						HibernateHelper.getSingleton().getSession().flush();
					}
				}
			}
		}
		
		log.info("------------------------------------------------------");
		log.info("Removidos " + count + " itens de menu");
		log.info("------------------------------------------------------");
	}
	
	/**
	 * Método para processar XML.
	 * 
	 * @param xmlReader
	 * @throws Exception
	 */
	private void handleTagMenu(XMLStreamReader xmlReader) throws Exception {
		String nome = xmlReader.getAttributeValue(null, "nome");
		String url = xmlReader.getAttributeValue(null, "url");
		String aplicacao = xmlReader.getAttributeValue(null, "aplicacao");
		String ordemStr = xmlReader.getAttributeValue(null, "ordem");
		Integer ordem = Integer.valueOf(ordemStr);

		Menu menuPai = menuStack.isEmpty() ? null : menuStack.peek();
		
		try {
			Menu menu = new Menu();
			menu.setDataCriacao(new Date());
			menu.setNome(nome);
			menu.setUrl(url);
			menu.setAplicacao(obterAplicacao(aplicacao));
			menu.setMenuPai(menuPai);
			
			menu.setOrdem(Integer.valueOf(ordem));
			
			if (menu.getAtivo() == null) {
				String ativo = xmlReader.getAttributeValue(null, "ativo");
				menu.setAtivo(Boolean.parseBoolean(ativo) ? DominioSimNao.S : DominioSimNao.N);
			}
			
			menu.setClasseIcone(xmlReader.getAttributeValue(null, "classe-icone"));
			
			if (urlMenuExcecao.equals(menu.getUrl())) {
				menuExcecaoNovo = menu;
				log.info("Armazenado novo menu exceção ID: " + menu.getId() + "; URL: " + menu.getUrl());
			} else {
				HibernateHelper.getSingleton().getSession().persist(menu);
				HibernateHelper.getSingleton().getSession().flush();
				
				log.info("Adicionado menu ID: " + menu.getId() + "; URL: " + menu.getUrl());
			}
			
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
	
	/**
	 * Método para atualizar os favoritos que estão com os menus virtuais para os novos menus (criados a partir do XML).
	 *  
	 * @throws Exception
	 */
	private void restaurarFavoritos() throws Exception {
		for(FavoritoVO favoritoVO : favoritoVOList) {
			Menu menu = this.obterMenu(favoritoVO.getMenuUrl(), favoritoVO.getMenuIdAplicacao());
			
			// Se menu não for encontrado, possivelmente o mesmo foi removido, porém ainda existe um favorito apontando para o mesmo
			// Caso isto ocorra, o favorito será removido
			if (menu == null) {
				this.removerFavoritos(favoritoVO.getMenuIdVirtual());
			} else {
				favoritoVO.setMenuIdNovo(menu.getId());
				
				for (Favorito favorito : this.pesquisarFavoritos(favoritoVO.getMenuIdVirtual())) {
					favorito.setMenu(menu);
					HibernateHelper.getSingleton().getSession().saveOrUpdate(favorito);
					
					log.info("Atualizado favorito ID: " + favorito.getId() + " do menu ID: " + favoritoVO.getMenuIdVirtual() + " para o novo menu ID: " + menu.getId());
				}
				HibernateHelper.getSingleton().getSession().flush();
			}
		}
	}
	
	/**
	 * Método para remover favoritos de um item de menu.
	 * 
	 * @param idMenu
	 */
	@SuppressWarnings("unchecked")
	private void removerFavoritos(Integer idMenu) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Favorito.class);
		criteria.add(Restrictions.eq("menu." + Menu.Fields.ID.toString(), idMenu));
		
		List<Favorito> favoritoList = (List<Favorito>) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
		
		for (Favorito favorito : favoritoList) {
			HibernateHelper.getSingleton().getSession().delete(favorito);
		}
		HibernateHelper.getSingleton().getSession().flush();
	}
	
	/**
	 * Método para manter o menu excecao na mesma posicao original (antiga) para que o novo menu seja removido.
	 * Isto elimina o problema da atualização de ~20.000 itens de favoritos.  
	 */
	private void atualizarMenuExcecao() {
		if (menuExcecaoNovo != null) {
			String nomeAntigo = menuExcecaoAntigo.getNome(); 
			
			menuExcecaoAntigo.setAplicacao(menuExcecaoNovo.getAplicacao());
			menuExcecaoAntigo.setAtivo(menuExcecaoNovo.getAtivo());
			menuExcecaoAntigo.setMenuPai(menuExcecaoNovo.getMenuPai());
			menuExcecaoAntigo.setNome(menuExcecaoNovo.getNome());
			menuExcecaoAntigo.setDataCriacao(new Date());
			menuExcecaoAntigo.setOrdem(menuExcecaoNovo.getOrdem());
			
			menuExcecaoNovo.setNome(nomeAntigo);
			
			HibernateHelper.getSingleton().getSession().persist(menuExcecaoAntigo);
			HibernateHelper.getSingleton().getSession().flush();
		}
	}
	
	/**
	 * Remove todos itens de menu virtual
	 */
	private void removerMenusVirtuais() {
		for (Menu menu : this.pesquisarMenus(true)) {
			log.info("Removendo item de menu: ID: " + menu.getId() + " - URL: " + menu.getUrl() + " + Nome: " + menu.getNome() + " - Ordem: " + menu.getOrdem());
			HibernateHelper.getSingleton().getSession().delete(menu);
		}
		HibernateHelper.getSingleton().getSession().flush();
	}
	
	/**
	 * Método para listar favorito que apontam para um determinado item de menu
	 * @param idMenu
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Favorito> pesquisarFavoritos(Integer idMenu) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(Favorito.class);
		criteria.createAlias("menu", "menu");
		criteria.add(Restrictions.eq("menu." + Menu.Fields.ID.toString(), idMenu));
		
		return (List<Favorito>) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
	}
	
	/**
	 * Método para pesquisar menus.
	 *  
	 * @param listarMenusVirtuais
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Menu> pesquisarMenus(boolean listarMenusVirtuais) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		
		if (listarMenusVirtuais) {
			criteria.add(Restrictions.ilike(Menu.Fields.NOME.toString(), this.nomeMenuVirtual, MatchMode.ANYWHERE));
		} else {
			criteria.add(Restrictions.not(Restrictions.like(Menu.Fields.NOME.toString(), this.nomeMenuVirtual, MatchMode.ANYWHERE)));
		}
		
		return (List<Menu>) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
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
	
	@SuppressWarnings("unchecked")
	private Menu obterMenu(final String url, Integer idAplicacao) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		criteria.createAlias("aplicacao", "aplicacao");
		
		criteria.add(Restrictions.eq(Menu.Fields.URL.toString(), url.trim()));
		criteria.add(Restrictions.eq("aplicacao." + Aplicacao.Fields.ID.toString(), idAplicacao));
		criteria.add(Restrictions.not(Restrictions.like(Menu.Fields.NOME.toString(), this.nomeMenuVirtual, MatchMode.ANYWHERE)));
		criteria.addOrder(Order.desc(Menu.Fields.ID.toString())); 
		
		Menu m = null;
		try {
			m = (Menu) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
		} catch (HibernateException e) {
			List<Menu> menus = (List<Menu>) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
			
			for (Menu i : menus) {
				log.warn("Menu duplicado: " + i.toString());
			}
			
			m = menus.get(0);
			log.warn("Utilizado menu " + m.toString());
		}
		
		return m;
	}
	
	private Menu obterMenu(final String nome, String nomeMenuPai, Integer idAplicacao) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		criteria.createAlias("aplicacao", "aplicacao");
		criteria.createAlias(Menu.Fields.MENU_PAI.toString(), "menuPai", Criteria.LEFT_JOIN);

		
		criteria.add(Restrictions.like(Menu.Fields.MENU_PAI.toString() + "." + Menu.Fields.NOME.toString(), nomeMenuPai.trim(), MatchMode.ANYWHERE));
		criteria.add(Restrictions.like(Menu.Fields.NOME.toString(), nome.trim(), MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("aplicacao." + Aplicacao.Fields.ID.toString(), idAplicacao));
		criteria.addOrder(Order.desc(Menu.Fields.ID.toString())); 
		
		Menu m = null;
		try {
			m = (Menu) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
		} catch (HibernateException e) {
			List<Menu> menus = (List<Menu>) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
			
			for (Menu i : menus) {
				log.warn("Menu duplicado: " + i.toString());
			}
			
			m = menus.get(0);
			log.warn("Utilizado menu " + m.toString());
		}
		
		return m;
	}

	@SuppressWarnings("unchecked")
	private void carregarPalavrasChave() throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(PalavraChaveMenu.class);
		criteria.createAlias("menu", "menu", Criteria.INNER_JOIN);
		criteria.createAlias("menu.menuPai", "menuPai", Criteria.LEFT_JOIN);
		List<PalavraChaveMenu> listaPalavras = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();
		
		if(listaPalavras != null && !listaPalavras.isEmpty()) {
			for(PalavraChaveMenu p : listaPalavras) {
				palavras.add(new PalavraChaveVO(p.getPalavra(), p.getMenu().getUrl(), p.getMenu().getNome(), p.getMenu().getMenuPai().getNome(), p.getMenu().getId(), p.getMenu().getMenuPai().getId(), p.getAtivo()));
			}
		}		
	}

	private void reinserirPalavrasChave() throws Exception {
		Aplicacao app = this.obterAplicacao("AGHU");
		if(palavras != null && !palavras.isEmpty()) {
			for(PalavraChaveVO palavra : palavras) {
				log.info("URL '" + palavra.getUrl());
				Menu menu = obterMenu(palavra.getUrl(), app.getId());
				if(menu == null) {
					menu = obterMenu(palavra.getNomeMenu(), palavra.getNomeMenuPai(), app.getId());
				} 

				if(menu != null) {
					log.info("Inserindo palavra chave '" + palavra.getPalavra() + "' para menu : " + menu.getId());
					PalavraChaveMenu novaPalavra = new PalavraChaveMenu();
					novaPalavra.setAtivo(palavra.getAtivo());
					novaPalavra.setMenu(menu);
					novaPalavra.setPalavra(palavra.getPalavra());
					HibernateHelper.getSingleton().getSession().persist(novaPalavra);
			}
		}		
		HibernateHelper.getSingleton().getSession().flush();
		}
	}
	
	private Menu obterMenu(final Integer id) throws Exception {
		DetachedCriteria criteria = DetachedCriteria.forClass(Menu.class);
		criteria.add(Restrictions.eq(Menu.Fields.ID.toString(), id));
		
		Menu m = null;
		try {
			m = (Menu) criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).uniqueResult();
		} catch (HibernateException e) {
			throw new Exception("Mais de um registro encontrado no banco para o Menu: " + id, e);
		}
		
		return m;
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

	public static void main(String[] args) throws Exception {
		
		MenuUpdaterForce updater = new MenuUpdaterForce();

		if (args.length > 0) {
			
			boolean paramSimular = true;
			// Caso o parâmetro de simulação seja diferente de 'true' ou
			// 'false', assume true pois é opcional
			if (args[1] != null && (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false"))) {
				log.info("Parâmetro [simular] não informado, assumindo 'true'");
			} else {
				paramSimular = Boolean.parseBoolean(args[1]);
			}
			updater.setSimulacao(paramSimular);
			
			if (args.length >= 3 && args[2] != null && !args[2].equals("${aplicacao}")) {
				updater.setAplicacao(args[2].toUpperCase());
			} else {
				log.error("Informe o nome da aplicação através do parâmetro [aplicacao]");
				System.exit(2);
			}
			
			if (args.length >= 4 && args[3] != null && !args[3].equals("${versao}")) {
				updater.setVersao(args[3]);
			} else {
				log.error("Informe a versão da aplicação através do parâmetro [versao]");
				System.exit(3);
			}

			updater.setDiretorio(args[0] + File.separator + updater.getAplicacao() + File.separator + updater.getVersao());
			
			String fileArg = null;
			if (args.length >= 6) {
				fileArg = args[5];
			}
			HibernateHelper.loadInstance(fileArg);
			
			try {
				updater.processarArquivo();
			} catch(Exception e) {
				e.printStackTrace();
				log.error("Erro ao executar o script de menu!");
				throw new Exception(e);
			}
		} else {
			log.error("Forma de utilização: MenuUpdaterForce <diretorio> [simular] [local]");
			throw new IllegalArgumentException();
		}
	}

}