package br.gov.mec.casca.tools.security.xml;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.Modulo;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisPermissoes;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.PermissaoModulo;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.casca.model.Protocolo;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.tools.updater.HibernateHelper;

public class DatabaseUpdater {
	
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
	
	private DatabaseDAO databaseDAO;

	private Permissao permissao;
	
	private Componente componente;
	
	private Perfil perfil;
	
	private Usuario usuario;
	
	private DatabaseCleaner databaseCleaner;
	
	private boolean simulacao; 
	
	private String local;

	private String diretorio;
	
	private String aplicacao;
	
	private String versao;
	
	private boolean priorizarXML = false;

	private static String USER_HOME;
	
	private static String USER_NAME;
	
	private static String HOST_NAME;
	
	private static String HOST_ADDRESS;
	
	private static Logger getLogger() {
		return Logger.getLogger(DatabaseUpdater.class);
	}
	
	static {
		USER_HOME = System.getProperty("user.home");
		USER_NAME = System.getProperty("user.name");
		try {
			HOST_NAME = InetAddress.getLocalHost().getHostName();
			HOST_ADDRESS = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private DatabaseUpdater() {
		databaseCleaner = new DatabaseCleaner();
		databaseDAO = new DatabaseDAO();
	}

	public static void main(String[] args) throws Exception {

		DatabaseUpdater dbUpdater = new DatabaseUpdater();

		if (args.length > 0) {

			boolean simular = true;
			// Caso o parâmetro de simulação seja diferente de 'true' ou
			// 'false', assume true pois é opcional
			if (args[1] != null && (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false"))) {
				getLogger().info("Parâmetro [simular] não informado, assumindo 'true'");
			} else {
				simular = Boolean.parseBoolean(args[1]);
			}
			dbUpdater.setSimulacao(simular);

			if (args.length >= 3 && args[2] != null && !args[2].equals("${local}")) {
				dbUpdater.setLocal(args[2]);
			} else {
				getLogger().error("Informe o local do banco de dados [local]");
				System.exit(1);
			}
			
			if (args.length >= 4 && args[3] != null && !args[3].equals("${aplicacao}")) {
				dbUpdater.setAplicacao(args[3].toUpperCase());
			} else {
				getLogger().error("Informe o nome da aplicação através do parâmetro [aplicacao]");
				System.exit(1);
			}
			
			if (args.length >= 5 && args[4] != null && !args[4].equals("${versao}")) {
				dbUpdater.setVersao(args[4]);
			} else {
				getLogger().error("Informe a versão da aplicação através do parâmetro [versao]");
				System.exit(1);
			}
			
			// Caso o parâmetro de priorizarXML seja diferente de 'true' ou
			// 'false', assume false para manter a compatibilidade do script
			// que é a de manter as tabelas do CASCA relacionadas a permissões
			// o mais íntegras possível.
			dbUpdater.setPriorizarXML(false);
			if (args.length >= 6 && args[5] != null && !args[5].equals("${priorizarXML}")
					&& (!args[5].equalsIgnoreCase("true") && !args[5].equalsIgnoreCase("false"))) {
				getLogger().warn("Parâmetro [priorizarXML] não informado, assumindo 'false'");
			} else {
				dbUpdater.setPriorizarXML(Boolean.parseBoolean(args[5]));
			}
			
			dbUpdater.setDiretorio(args[0] + File.separator + dbUpdater.getAplicacao() + File.separator + dbUpdater.getVersao());

			String fileArg = null;
			if (args.length >= 7) {
				fileArg = args[6];
			}
			HibernateHelper.loadInstance(fileArg);
			
			try {
				dbUpdater.processar();
			} catch(Exception e) {
				e.printStackTrace();
				getLogger().error("Erro ao executar o script de seguranca!");
				throw new Exception(e);
			}
		} else {
			getLogger().error("Forma de utilização: DatabaseUpdate <diretorio> [simular] [local]");
			throw new IllegalArgumentException();
		}
	}
	
	private void processar() throws Exception {
		getLogger().info(String.format("Usando dir %s", this.diretorio));
		getLogger().info(String.format("Modo de simulação: %s", this.simulacao ? "ligado" : "desligado"));
		getLogger().info(String.format("Local usado: %s", this.local));
		
		HibernateHelper.getSingleton().beginTransaction();
		
		if (isPriorizarXML()) {
			databaseCleaner.cleanBeforeExecute();
		} else {
			databaseDAO.carregarComponenteComMetodos();
		}
		
		String nomeArquivo = this.diretorio + File.separator + XMLWriter.ARQUIVO_PRINCIPAL;
		processaArquivo(nomeArquivo);
		
		databaseDAO.carregarPermissoesComponentes();
		databaseDAO.carregarPermissaoModulo();

		nomeArquivo = XMLWriter.ARQUIVO_PERMISSOES + XMLWriter.SEPARADOR;
		processaDiretorio(nomeArquivo);
		getLogger().info("===============================================================================");
		
		databaseDAO.carregarPerfilComPermissoes();
		
		nomeArquivo = diretorio + File.separator + XMLWriter.ARQUIVO_PERFIS;
		processaArquivo(nomeArquivo);
		getLogger().info("===============================================================================");
		
		evitarExclusaoAplicacoesAGHUExternas(); // (vide tarefa #45444)
		
		databaseCleaner.cleanup();
		
		if (!simulacao) {
			HibernateHelper.getSingleton().commit();
			HibernateHelper.getSingleton().getSession().close();
		}
		
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("\n############ Totais de hits nas caches #############");			
			getLogger().debug("# Cache Objetos: "+databaseDAO.getContadorHitsCacheObjetos());
			getLogger().debug("# Cache PermissaoModulo: "+databaseDAO.getContadorHitsCachePermissaoModulo());
			getLogger().debug("# Cache PermissoesComponentes: "+databaseDAO.getContadorHitsCachePermissoesComponentes());
			getLogger().debug("# Cache Metodo: "+databaseDAO.getContadorHitsCacheMetodo());
			getLogger().debug("# Cache PerfisUsuarios: "+databaseDAO.getContadorHitsCachePerfisUsuarios());
			getLogger().debug("# Cache PerfisPermissoes: "+databaseDAO.getContadorHitsCachePerfisPermissoes());
			getLogger().debug("\n############ Fim totais de hits nas caches #########");
		}
	}

	private void processaArquivo(String nomeArquivo) throws Exception {
		if(local != null && !this.local.isEmpty()) {
			nomeArquivo = nomeArquivo + XMLWriter.SEPARADOR + local;
		}
		nomeArquivo =  nomeArquivo + XMLWriter.EXTENSAO;
		File arquivo = new File(nomeArquivo);
		if(arquivo.exists()) {
			InputStream in = new java.io.FileInputStream(arquivo);			
			getLogger().info("Processando arquivo " + arquivo.getName());
			this.execute(in);
		} else {
			throw new FileNotFoundException("Não foi encontrado o arquivo " + nomeArquivo);
		}
	}
	
	/**
	 * @param nomeArquivos
	 * @throws FileNotFoundException
	 * @throws XMLStreamException
	 */
	private void processaDiretorio(String nomeArquivos) throws Exception {

		File dir = new File(this.diretorio);

		if(dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			String finalNome = (this.local != null ? XMLWriter.SEPARADOR + this.local : "") + XMLWriter.EXTENSAO; 
			for(File file : files) {
				if(file.getName().startsWith(nomeArquivos) && file.getName().endsWith(finalNome)) {
					InputStream in = new java.io.FileInputStream(file);
					getLogger().info("===============================================================================");
					getLogger().info("Processando arquivo " + file.getName());
					this.execute(in);
					HibernateHelper.getSingleton().getSession().flush();
				}
			}
		}
	}

	private void execute(InputStream in) throws Exception {
		
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlReader = xmlFactory.createXMLStreamReader(in);
		
		try {
			
			while (xmlReader.hasNext()) {
				xmlReader.next();
	
				if (xmlReader.getEventType() == START_ELEMENT) {
					String name = xmlReader.getName().toString();
	
					if (name.equals("aplicacao")) {
						handleTagAplicacao(xmlReader);
					}
	
					else if (name.equals("modulo")) {
						handleTagModulo(xmlReader);
					}

					else if (name.equals("target")) {
						handleTagTarget(xmlReader);
					}

					else if (name.equals("action")) {
						handleTagAction(xmlReader);
					}
					
					else if (name.equals("permissao")) {
						handleTagPermissao(xmlReader);
					}
					
					else if (name.equals("permissao_target")) {
						handleTagPermissaoTarget(xmlReader);
					}
					
					else if (name.equals("perfil")) {
						handleTagPerfil(xmlReader);
					}
					
					else if (name.equals("perfil_permissao")) {
						handleTagPerfilPermissao(xmlReader);
					}
					
					else if (name.equals("usuario")) {
						handleTagUsuario(xmlReader);
					}
					
					else if (name.equals("usuario_perfil")) {
						handleTagUsuarioPerfil(xmlReader);
					}
					
				}
			}
		} catch (Exception e) {
			HibernateHelper.getSingleton().rollback();
			throw e;
		}
	}
	
	private void handleTagAplicacao(XMLStreamReader xmlReader) throws Exception {
		String nomeAplicacao = xmlReader.getAttributeValue(null, "nome");
		
		Aplicacao aplicacao = databaseDAO.obter(Aplicacao.class, nomeAplicacao, !isSimulacao());
		
		if (aplicacao == null) {
			aplicacao = new Aplicacao();
			getLogger().info(String.format("Criando aplicação '%s'", nomeAplicacao));
		}
		
		// Gera um comentário no campo descricao da entidade Aplicacao para
		// ajudar a identificar a execução simultânea do script, principalmente
		// em ambientes de desenvolvimento
		String atributoDescricaoTagAplicacao = xmlReader.getAttributeValue(null, "descricao");
		if (aplicacao.getId() != null && (atributoDescricaoTagAplicacao == null || atributoDescricaoTagAplicacao.isEmpty())) {
			String dataFormatoCompletoStr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
			atributoDescricaoTagAplicacao = "Script de segurança executado por [Usuário:"
				+ USER_NAME + "] [Iniciado em:" + dataFormatoCompletoStr 
				+ " ] [Diretório:" + USER_HOME 
				+ "] [Máquina:" + HOST_NAME 
				+ "] [IP:" + HOST_ADDRESS 
				+ "]. Com parâmetros: [aplicacao="+getAplicacao()
				+"] [versao="+getVersao()+"] [priorizarXML="+isPriorizarXML()+"] "
				+"OBSERVAÇÃO: Esta descrição é apenas para fins de teste, pode ser apagada em ambientes de produção.";
		}
		
		if (aplicacao.getDescricao() != null && aplicacao.getDescricao().startsWith("Script")) {
			getLogger().info(String.format(">>>>> Informações sobre a última execução deste script <<<<< \n '%s' \n", aplicacao.getDescricao()));
		}
		aplicacao.setDescricao(atributoDescricaoTagAplicacao);
		getLogger().warn(String.format("Alterou o campo 'descricao' da aplicação '%s' \n", aplicacao));
		
		// Caso seja uma nova Aplicação ou deva-se priorizar o conteúdo do XML
		if (isPriorizarXML() || aplicacao.getId() == null) {
			aplicacao.setContexto(xmlReader.getAttributeValue(null, "contexto"));
			
			String externo = xmlReader.getAttributeValue(null, "externo");
			aplicacao.setExterno(Boolean.valueOf(externo));
			aplicacao.setNome(xmlReader.getAttributeValue(null, "nome"));
	
			String porta = xmlReader.getAttributeValue(null, "porta");
			try {
				if (porta != null && !porta.trim().isEmpty()) {
					aplicacao.setPorta(Integer.valueOf(porta));
				}
			} catch (NumberFormatException e) {
				throw new RuntimeException(String.format("Aplicação '%s' possui um valor inválido para a porta (%s)", aplicacao.getNome(), porta));
			}
			
			String protocolo = xmlReader.getAttributeValue(null, "protocolo");
			if (protocolo != null ) {
				for (Protocolo protocoloPermitido : Protocolo.values()) {
					if (protocoloPermitido.name().equalsIgnoreCase(protocolo)) {
						aplicacao.setProtocolo(protocoloPermitido);
						break;
					}
				}
				if (aplicacao.getProtocolo() == null) {
					throw new RuntimeException(String.format("Protocolo '%s' definido para a aplicação '%s' não é suportado pelo AGHU.", protocolo, aplicacao.getNome()));
				}
			}
	
			aplicacao.setServidor(xmlReader.getAttributeValue(null, "servidor"));
		}
		
		aplicacao = (Aplicacao) HibernateHelper.getSingleton().salvar(aplicacao);
		
		databaseCleaner.addAplicacao(aplicacao.getId());
	}
	
	private void handleTagTarget(XMLStreamReader xmlReader) throws Exception {
		String nomeTarget = xmlReader.getAttributeValue(null, "nome");
		String nomeAplicacao = xmlReader.getAttributeValue(null, "aplicacao");
		Aplicacao aplicacao = databaseDAO.obter(Aplicacao.class, nomeAplicacao);
		if (aplicacao == null) {
			throw new RuntimeException(String.format("Aplicacao '%s' não mapeada", nomeAplicacao));
		}
		
		componente = databaseDAO.obter(Componente.class, nomeTarget);
		
		if (componente != null && !isPriorizarXML() && nomeTarget.equals(componente.getNome()) 
				&& componente.getAplicacao().getId().equals(aplicacao.getId())) {
			databaseCleaner.addComponente(componente.getId());
			return;
		}
		
		if (componente == null) {
			componente = new Componente();
			getLogger().info(String.format("Criando target '%s'", nomeTarget));
		} else if (getLogger().isDebugEnabled()) {
			getLogger().debug(String.format("Alterando target '%s'", nomeTarget));
		}
		
		componente.setAplicacao(aplicacao);
		componente.setNome(nomeTarget);
		componente.setMetodos(new HashSet<Metodo>());
		
		componente = (Componente) HibernateHelper.getSingleton().salvar(componente);
		
		databaseCleaner.addComponente(componente.getId());
	}
	
	
	private void handleTagAction(XMLStreamReader xmlReader) throws Exception {
		String nomeMetodo = xmlReader.getAttributeValue(null, "nome");
		if (nomeMetodo == null || nomeMetodo.isEmpty()) {
			throw new Exception("Nome da action não informado.");
		}
		
		String descricao = xmlReader.getAttributeValue(null, "descricao");
		if (descricao == null || descricao.isEmpty()) {
			throw new Exception("Descrição não informada para action " + nomeMetodo);
		}		

		Metodo metodo = databaseDAO.obterMetodo(nomeMetodo, componente.getNome());

		if (metodo != null && !isPriorizarXML() && nomeMetodo.equals(metodo.getNome()) && descricao.equals(metodo.getDescriao())) {
			databaseCleaner.addMetodo(metodo.getId());
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Encontrado método: " + metodo);
			}
			return;
		}
		
		if (metodo == null) {
			metodo = new Metodo();
			getLogger().info(String.format("Criando action '%s' para o target '%s'", nomeMetodo, componente.getNome()));
		} else if (getLogger().isDebugEnabled()) {
			getLogger().debug(String.format("Action action '%s' do target '%s'", nomeMetodo, componente.getNome()));
		}
		
		metodo.setNome(nomeMetodo);		
		metodo.setDescriao(descricao);
		metodo.setComponente(componente);
		
		// colocar no XML?
		metodo.setAtivo(DominioSimNao.S);
		
		componente.getMetodos().add(metodo);
		
		metodo = (Metodo) HibernateHelper.getSingleton().salvar(metodo);
		
		databaseCleaner.addMetodo(metodo.getId());		
	}

	private void handleTagModulo(XMLStreamReader xmlStreamReader) throws Exception { 
		String nomeModulo = xmlStreamReader.getAttributeValue(null, "nome");
		
		Modulo modulo = databaseDAO.obter(Modulo.class, nomeModulo);
		if(modulo == null) {
			modulo = new Modulo();
			modulo.setAtivo(false);
			getLogger().info(String.format("Criando módulo (inativo) '%s'", nomeModulo));
		} else if (getLogger().isDebugEnabled()) {
			getLogger().debug(String.format("Alterando módulo '%s'", nomeModulo));
		}

		modulo.setNome(nomeModulo);
		modulo.setDescricao(xmlStreamReader.getAttributeValue(null, "descricao"));
		
		String nomeApp = xmlStreamReader.getAttributeValue(null, "aplicacao");
		Aplicacao aplicacao = databaseDAO.obter(Aplicacao.class, nomeApp);
		
		if(aplicacao == null) {
			throw new RuntimeException("Aplicacao " + nomeApp + " nao encontrada para modulo " + nomeModulo);
		}
		modulo.setAplicacao(aplicacao);
		
		modulo = (Modulo) HibernateHelper.getSingleton().salvar(modulo);
		
		databaseCleaner.addModulo(modulo.getId());
	}
	
	private void handleTagPermissao(XMLStreamReader xmlReader) throws Exception {
		
		String nomePermissao = xmlReader.getAttributeValue(null, "nome");
		
		permissao = databaseDAO.obter(Permissao.class, nomePermissao);
		
		if (permissao == null) {
			permissao = new Permissao();
			permissao.setDataCriacao(new Date());
			getLogger().info(String.format("Criando permissão '%s'", nomePermissao));
		} else if (getLogger().isDebugEnabled()) {
			getLogger().debug(String.format("Alterando permissão '%s'", nomePermissao));
		}

		permissao.setNome(xmlReader.getAttributeValue(null, "nome"));
		permissao.setDescricao(xmlReader.getAttributeValue(null, "descricao"));
		
		// TODO Colocar no XML?
		permissao.setAtivo(DominioSituacao.A);
		
		permissao = (Permissao) HibernateHelper.getSingleton().salvar(permissao);
		
		databaseCleaner.addPermissao(permissao.getId());
		
		String nomeModulo = xmlReader.getAttributeValue(null, "modulo");
		Modulo modulo = databaseDAO.obter(Modulo.class, nomeModulo);
		if(modulo == null) {
			throw new RuntimeException("Modulo " + nomeModulo + " inexistente.");
		}
		
		PermissaoModulo permissaoModulo = databaseDAO.obterPermissaoModulo(nomePermissao, nomeModulo);
		if(permissaoModulo == null) {
			permissaoModulo = new PermissaoModulo();
			permissaoModulo.setModulo(modulo);
			permissaoModulo.setPermissao(permissao);
			
			permissaoModulo = (PermissaoModulo) HibernateHelper.getSingleton().salvar(permissaoModulo);			
		}

		databaseCleaner.addPermissaoModulo(permissaoModulo.getId());
	}

	private void handleTagPermissaoTarget(XMLStreamReader xmlReader) throws Exception {
		String targetName = xmlReader.getAttributeValue(null, "target");
		if (targetName == null || targetName.isEmpty()) {
			throw new RuntimeException("A tag permissao_target não tem o atributo [target] declarado");
		}

		Componente componente = databaseDAO.obter(Componente.class, targetName);

		if (componente == null) {
			throw new RuntimeException(String.format("Target '%s' não mapeado", targetName));
		}
		
		String actionName = xmlReader.getAttributeValue(null, "action");
		Metodo metodo = databaseDAO.obterMetodo(actionName, componente.getNome());
		
		if (metodo == null) {
			throw new RuntimeException(String.format("Target|Action => '%s | %s' não está mapeada na base de dados", targetName, actionName));
		}
		
		PermissoesComponentes permComp = databaseDAO.obterPermissaoComponente(permissao, componente, metodo);

		if (permComp == null) {
			permComp = new PermissoesComponentes();
			permComp.setComponente(componente);
			permComp.setMetodo(metodo);
			permComp.setPermissao(permissao);
			
			permComp = (PermissoesComponentes) HibernateHelper.getSingleton().salvar(permComp);			
			
			getLogger().info(String.format("Associando target '%s' e action '%s' à permissão '%s'", targetName, actionName, permissao.getNome()));
		}

		databaseCleaner.addPermissaoComponente(permComp.getId());
	}
	
	private void handleTagPerfil(XMLStreamReader xmlReader) throws Exception {
		String nomePerfil = xmlReader.getAttributeValue(null, "nome");
		
		if (nomePerfil == null || nomePerfil.isEmpty()) {
			throw new Exception("Nome não informado para o perfil, revise o aquivo de permissões de perfis.");
		}
		
		perfil = databaseDAO.obter(Perfil.class, nomePerfil);
		
		String descricao = xmlReader.getAttributeValue(null, "descricao");
		String descricaoResumida = xmlReader.getAttributeValue(null, "descricao-resumida");				
		
		if (perfil != null && !isPriorizarXML() && perfil.getNome().equals(nomePerfil) 
				&& perfil.getDescricao().equals(descricao) 
				&& descricaoResumida != null && descricaoResumida.equals(perfil.getDescricaoResumida())) {
			databaseCleaner.addPerfil(perfil.getId());
			return;
		}
		
		if (perfil == null) {
			perfil = new Perfil();
			perfil.setDataCriacao(new Date());
			perfil.setDelegavel(Boolean.FALSE);
			String delegavel = xmlReader.getAttributeValue(null, "delegavel");
			if (delegavel != null && delegavel.trim().length() > 0) {
				perfil.setDelegavel(Boolean.valueOf(delegavel));
			}
			
			getLogger().info(String.format("Criando perfil '%s'", nomePerfil));
		} else if (getLogger().isDebugEnabled()) {
			getLogger().debug(String.format("Alterando perfil '%s'", nomePerfil));
		}
		
		perfil.setNome(nomePerfil);

		if (descricao == null || descricao.isEmpty()) {
			throw new Exception("Descrição não informada para o perfil "+nomePerfil);
		}
		perfil.setDescricao(descricao);

		perfil.setDescricaoResumida(descricaoResumida == null ? descricao : descricaoResumida);		
		perfil.setSituacao(DominioSituacao.A);
		
		perfil = (Perfil) HibernateHelper.getSingleton().salvar(perfil);
		
		databaseCleaner.addPerfil(perfil.getId());
	}
	
	private void handleTagPerfilPermissao(XMLStreamReader xmlReader) throws Exception {
		String nomePermissao = xmlReader.getAttributeValue(null, "permissao");
		Permissao permissao = databaseDAO.obter(Permissao.class, nomePermissao);
		
		if (permissao == null) {
			throw new RuntimeException(String.format("Permissão '%s' não mapeada", nomePermissao));
		}
		
		PerfisPermissoes perfilPermissao = databaseDAO.obterPerfilPermissao(perfil, permissao);
		
		if (perfilPermissao != null && !isPriorizarXML() && perfilPermissao.getPerfil() == perfil && perfilPermissao.getPermissao() == permissao) {
			databaseCleaner.addPerfilPermissao(perfilPermissao.getId());
			return;
		}
		
		if (perfilPermissao == null) {
			perfilPermissao = new PerfisPermissoes();
			getLogger().info(String.format("Associando permissão '%s' ao perfil '%s'", permissao.getNome(), perfil.getNome()));
		}
		
		perfilPermissao.setPerfil(perfil);
		perfilPermissao.setPermissao(permissao);

		perfilPermissao = (PerfisPermissoes) HibernateHelper.getSingleton().salvar(perfilPermissao);

		databaseCleaner.addPerfilPermissao(perfilPermissao.getId());
	}
	
	private void handleTagUsuario(XMLStreamReader xmlReader) throws Exception {
		String nomeUsuario = xmlReader.getAttributeValue(null, "nome");
		usuario = databaseDAO.obterUsuario(nomeUsuario);
		
		if (usuario == null) {
			usuario = new Usuario();
			usuario.setDataCriacao(new Date());
			usuario.setTempoSessaoMinutos(30);
			usuario.setAtivo(Boolean.FALSE);
			usuario.setDelegarPerfil(Boolean.FALSE);
			String delegarPerfil = xmlReader.getAttributeValue(null, "delegar-perfil");
			if (delegarPerfil != null) {
				usuario.setDelegarPerfil(Boolean.valueOf(delegarPerfil));
			}
			
			getLogger().info(String.format("Criando usuário '%s'", nomeUsuario));
		} else if (getLogger().isDebugEnabled()) {
			getLogger().debug(String.format("Alterando usuário '%s'", nomeUsuario));
		}
		
		String tempoSessaoMinutos = xmlReader.getAttributeValue(null, "tempo-sessao-minutos");
		if (tempoSessaoMinutos != null) {
			usuario.setTempoSessaoMinutos(Integer.valueOf(tempoSessaoMinutos));
		}		
		
		usuario.setNome(nomeUsuario);
		usuario.setLogin(nomeUsuario);

		usuario = (Usuario) HibernateHelper.getSingleton().salvar(usuario);
		
		databaseCleaner.addUsuario(usuario.getId());
	}
	
	private void handleTagUsuarioPerfil(XMLStreamReader xmlReader) throws Exception {
		String nomePerfil = xmlReader.getAttributeValue(null, "perfil");
		Perfil perfil = databaseDAO.obter(Perfil.class, nomePerfil);
		
		if (perfil == null) {
			throw new RuntimeException(String.format("Perfil '%s' não mapeado", nomePerfil));
		}
		
		PerfisUsuarios perfilUsuario = databaseDAO.obterPerfilUsuario(perfil, usuario);
		
		if (perfilUsuario == null) {
			perfilUsuario = new PerfisUsuarios();
			perfilUsuario.setDataCriacao(new Date());
			getLogger().info(String.format("Associando perfil '%s' ao usuário '%s'", perfil.getNome(), usuario.getLogin()));
		}
		
		perfilUsuario.setPerfil(perfil);
		perfilUsuario.setUsuario(usuario);
		
		perfilUsuario = (PerfisUsuarios) HibernateHelper.getSingleton().salvar(perfilUsuario);
		
		databaseCleaner.addUsuarioPerfil(perfilUsuario.getId());
	}
	
	/** 
	 * Evita que menus de aplicações externas do AGHU versões 5 ou 6
	 * sejam apagados (tarefa #45444)
	 * Após a migração total para a versão AGHUse (7.0) o método abaixo
	 * pode ser apagado deste script.
	 */
	@SuppressWarnings("unchecked")
	private void evitarExclusaoAplicacoesAGHUExternas() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);
		List<Aplicacao> listaAplicacoes = criteria.getExecutableCriteria(HibernateHelper.getSingleton().getSession()).list();

		for (Aplicacao aplicacao : listaAplicacoes) {
			if (aplicacao.getExterno() 
					&& (aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_5)
							|| aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_6))) {
				databaseCleaner.addAplicacao(aplicacao.getId());
			}
		}
		
	}

	private void setSimulacao(boolean simulacao) {
		this.simulacao = simulacao;		
	}
	
	private boolean isSimulacao() {
		return simulacao;		
	}	

	private void setLocal(String local) {
		// Desabilitando por enquanto
		//this.local = local;
	}
	
	private void setDiretorio(String diretorio) {
		this.diretorio = diretorio;
	}

	public String getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}
	
	private boolean isPriorizarXML() {
		return priorizarXML;
	}

	private void setPriorizarXML(boolean priorizarXML) {
		this.priorizarXML = priorizarXML;
	}
}