package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.CsePerfisVO;
import br.gov.mec.aghu.model.AelAutorizacaoAlteracaoSituacao;
import br.gov.mec.aghu.model.AelAutorizacaoAlteracaoSituacaoId;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterSituacaoExameController extends ActionController {
	
	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	private static final String PAGE_PESQUISA = "manterSituacaoExamePesquisa";

	private static final Log LOG = LogFactory.getLog(ManterSituacaoExameController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3128771016016973821L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	//@EJB
	//private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private ICascaFacade cascaService;
	
	//Variáveis para controle de edição
	private String codigo;
	private AelSitItemSolicitacoes situacao;
	private Boolean matrizEmEdicao = Boolean.TRUE;
	private AelMatrizSituacao matrizSituacaoSelecionada;
	
	private AelMatrizSituacao matriz = new AelMatrizSituacao();
	private Short matrizEmEdicaoId;
	private Short idTemporarioMatriz = 0;

	private List<Perfil> todosPerfis;
	private List<CsePerfisVO> perfisSelecionados;
	private List<CsePerfisVO> perfisNaoSelecionados;
	
	private boolean exibirModalAlteracoesPendentes;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Método chamado ao carregar a tela
	 */
	public void iniciar() {
	 

		
		carregarTela();
		//this.//setIgnoreInitPageConfig(true);
		
	
	}
	
	private void carregarTela() {
		if (codigo != null) {
			situacao = examesFacade.obterSituacaoItemSolicitacaoParaSituacaoExame(codigo);
			resetarMatriz();
		} else {
			resetarSituacao();
		}		
	}

	private void zerarController() {
		codigo = null;
		situacao =null;
		
		matriz = new AelMatrizSituacao();
		matrizEmEdicaoId = null;
		idTemporarioMatriz = 0;

		todosPerfis = null;
		perfisSelecionados = null;
		perfisNaoSelecionados = null;
		
		exibirModalAlteracoesPendentes = false;
	}
	
	private void resetarSituacao() {
		situacao = new AelSitItemSolicitacoes();
		situacao.setIndSituacao(DominioSituacao.A);
		situacao.setIndPermiteManterResultado(false);
		situacao.setIndMostraSolicitarExames(true);
		situacao.setIndAlertaExameJaSolic(true);
		resetarMatriz();
	}
	
	private boolean validarCamposObrigatoriosSituacao() {
		boolean camposPreenchidos = true;
		
		//Validação de campos obrigatórios
		if(situacao.getCodigo() == null) {
			apresentarMsgNegocio("codigo", Severity.ERROR, CAMPO_OBRIGATORIO, "Código");
			camposPreenchidos = false;
		}
		if(situacao.getDescricao() == null) {
			apresentarMsgNegocio("descricao", Severity.ERROR, CAMPO_OBRIGATORIO, "Descrição");
			camposPreenchidos = false;
		}
		if(situacao.getIndSituacao() == null) {
			apresentarMsgNegocio("situacao", Severity.ERROR, CAMPO_OBRIGATORIO, "Situação");
			camposPreenchidos = false;
		}
		if(situacao.getIndPermiteManterResultado() == null) {
			apresentarMsgNegocio("permiteResultado", Severity.ERROR, CAMPO_OBRIGATORIO, "Permite Resultado");
			camposPreenchidos = false;
		}
		if(situacao.getIndMostraSolicitarExames() == null) {
			apresentarMsgNegocio("mostraSolicitarExame", Severity.ERROR, CAMPO_OBRIGATORIO, "Mostra Solicitar Exames");
			camposPreenchidos = false;
		}
		if(situacao.getIndAlertaExameJaSolic() == null) {
			apresentarMsgNegocio("alertaExame", Severity.ERROR, CAMPO_OBRIGATORIO, "Alerta Exame");
			camposPreenchidos = false;
		}
		
		return camposPreenchidos;
	}
	
	/**
	 * Método que realiza a ação do botão gravar
	 */
	public String gravar() {
		//Reinicia o paginator
		//reiniciarPaginator(ManterSituacaoExamePaginatorController.class);
		
		//Verifica se a ação é de criação ou edição
		boolean criando = situacao.getCriadoEm() == null;
		
		try {
			
			//Verifica preenchimento dos campos
			if (validarCamposObrigatoriosSituacao()) {
				
				//Converte codigo para mauiúsculo
				situacao.setCodigo(situacao.getCodigo().trim().toUpperCase());
				
				//Remove caracteres inválidos
				situacao.setCodigo(situacao.getCodigo().replaceAll("[^A-Z]", ""));
				
				//Converte descricao para mauiúsculo
				situacao.setDescricao(situacao.getDescricao().trim().toUpperCase());
				
				//Limpa ids temporários de matrizes
				limparIdsTemporariosMatrizes();

				//RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				//situacao.setServidor(servidorLogado);
				
				//Submete o procedimento para ser persistido
				cadastrosApoioExamesFacade.persistirSituacaoExame(situacao);
				
				//Realiza o flush
				//cadastrosApoioExamesFacade.flush();
				
				//Apresenta as mensagens de acordo
				if (criando) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_SITUACAO_EXAME", situacao.getDescricao());
				} else {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_SITUACAO_EXAME", situacao.getDescricao());
				}
			} else {
				return null;
			}
			
		} catch(BaseException e) {
			LOG.error(e.getMessage(), e);
			if(criando) {
				situacao.setCriadoEm(null);
			}
			apresentarExcecaoNegocio(e);
			return null;
		} catch(PersistenceException e) {
			LOG.error(e.getMessage(), e);
			if(criando) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_SITUACAO_EXAME", situacao.getDescricao());
				situacao.setCriadoEm(null);
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_SITUACAO_EXAME", situacao.getDescricao());
			}
			return null;
		}
		
		return PAGE_PESQUISA;
	}
	
	private void limparIdsTemporariosMatrizes() {
		if(situacao.getMatrizesSituacao() != null) {
			for(AelMatrizSituacao matriz : situacao.getMatrizesSituacao()) {
				if(matriz != null && matriz.getSeq() != null && matriz.getSeq() < 0) {
					matriz.setSeq(null);
				}
			}	
		}
	}
	
	/**
	 * Método que realiza a ação do botão limpar na tela
	 */
	public void limpar() {
		resetarSituacao();
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela
	 */
	public String cancelar() {
		zerarController();
		
		//this.
		return PAGE_PESQUISA;
	}
	
	/**
	 * Metódo para SuggestionBox 
	 */
	public List<AelSitItemSolicitacoes> obterSituacoesSuggestionBox(String texto) {
		return cadastrosApoioExamesFacade.listarSituacoesExame((String)texto);
	}
	
	public boolean exibirModalAlteracoesPendentes() {
		return exibirModalAlteracoesPendentes;
	}
	
	public String confirmarGravar() {
		if(matriz.getSituacaoItemSolicitacao() != null || matrizEmEdicaoId !=null) {
			exibirModalAlteracoesPendentes = true;
			return null;
		} else {
			return gravar();
		}
	}

	public String confirmarModal() {
		exibirModalAlteracoesPendentes = false;
		return gravar();
	}

	public void cancelarModal() {
		exibirModalAlteracoesPendentes = false;
	}
	
	//------------------------------------------------------
	//Métodos relacionados a operações sobre Matrizes de Situação
	
	private boolean validarCamposObrigatoriosMatriz() {
		boolean camposPreenchidos = true;
		
		//Validação de campos obrigatórios
//		if(matriz.getSituacaoItemSolicitacao() == null) {
//			apresentarMsgNegocio("situacaoOrigem", Severity.ERROR, "CAMPO_OBRIGATORIO", "Situação de Origem");
//			camposPreenchidos = false;
//		}
		
		return camposPreenchidos;
	}
	
	public void adicionarMatriz() {
		if(validarCamposObrigatoriosMatriz()) {
			matriz.setSeq(--idTemporarioMatriz);
			matriz.setSituacaoItemSolicitacaoPara(situacao);
			situacao.getMatrizesSituacao().add(matriz);
			atualizarPerfisEmMatriz(matriz);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MATRIZ_SITUACAO", matriz.getSituacaoItemSolicitacao() != null ? matriz.getSituacaoItemSolicitacao().getCodigo() : "''");
			resetarMatriz();
		}
	}
	
	private void atualizarPerfisEmMatriz(AelMatrizSituacao matrizVar) {
		for(CsePerfisVO perfilVO : perfisNaoSelecionados) {
			for(AelAutorizacaoAlteracaoSituacao perfilMapeado : matrizVar.getAutorizacaoAlteracaoSituacoes()) {
				if(perfilMapeado.getPerfil().equals(perfilVO.getNome())) {
					matrizVar.getAutorizacaoAlteracaoSituacoes().remove(perfilMapeado);
					break;
				}
			}
		}
		
		for(CsePerfisVO perfilVO : perfisSelecionados) {
			boolean novo = true;
			for(AelAutorizacaoAlteracaoSituacao perfilMapeado : matrizVar.getAutorizacaoAlteracaoSituacoes()) {
				if(perfilMapeado.getPerfil().equals(perfilVO.getNome())) {
					novo = false;
					break;
				}
			}
			if(novo) {
				AelAutorizacaoAlteracaoSituacaoId id = new AelAutorizacaoAlteracaoSituacaoId(matrizVar.getSeq(), perfilVO.getPerfil().getNome());
				AelAutorizacaoAlteracaoSituacao novoPerfilMapeado = new AelAutorizacaoAlteracaoSituacao(id, matrizVar);
				novoPerfilMapeado.setPerfil(perfilVO.getPerfil().getNome());
				matrizVar.getAutorizacaoAlteracaoSituacoes().add(novoPerfilMapeado);
			}
		}
	}
	
	public void atualizarMatriz() {
		if(validarCamposObrigatoriosMatriz()) {
			for(AelMatrizSituacao matrizVar : situacao.getMatrizesSituacao()) {
				if(matrizVar.getSeq() == matrizEmEdicaoId) {
					matrizVar.setSituacaoItemSolicitacao(matriz.getSituacaoItemSolicitacao());
					matrizVar.setIndExigeMotivoCanc(matriz.getIndExigeMotivoCanc());
					
					atualizarPerfisEmMatriz(matrizVar);
				}
			}
			String situacaoOrigem = matriz.getSituacaoItemSolicitacao() != null ? matriz.getSituacaoItemSolicitacao().getCodigo() : "''";
			String situacaoDestino = matriz.getSituacaoItemSolicitacaoPara() != null ? matriz.getSituacaoItemSolicitacaoPara().getCodigo() : "''";
			
			this.matrizEmEdicao = Boolean.TRUE;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MATRIZ_SITUACAO", situacaoOrigem, situacaoDestino);
			resetarMatriz();
		}
	}
	
	public void iniciarEdicaoMatriz(AelMatrizSituacao matriz) {
		this.matrizEmEdicao = Boolean.FALSE;
		resetarMatriz();
		matrizEmEdicaoId = matriz.getSeq();
		this.matriz = new AelMatrizSituacao(matriz.getSeq(), matriz.getSituacaoItemSolicitacaoPara(), matriz.getIndExigeMotivoCanc(), matriz.getSituacaoItemSolicitacao(), matriz.getAutorizacaoAlteracaoSituacoes());
	}
	
	public void excluirMatriz(AelMatrizSituacao matriz) {
		if(matrizEmEdicaoId != null && matrizEmEdicaoId == matriz.getSeq().shortValue()) {
			resetarMatriz();
		}
		situacao.getMatrizesSituacao().remove(matriz);
		
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MATRIZ_SITUACAO", matriz.getSituacaoItemSolicitacao() != null ? matriz.getSituacaoItemSolicitacao().getCodigo() : "''");
	}
	
	public void cancelarEdicaoMatriz() {
		this.matrizEmEdicao = Boolean.TRUE;
		resetarMatriz();
	}
	
	private void resetarMatriz() {
		matriz = new AelMatrizSituacao();
		matriz.setSituacaoItemSolicitacaoPara(situacao);
		matrizEmEdicaoId = null;
		perfisNaoSelecionados = null;
		perfisSelecionados = null;
	}
	
	public List<AelMatrizSituacao> getMatrizesSituacaoAtivas() {
		List<AelMatrizSituacao> matrizesSituacaoAtivas = new ArrayList<AelMatrizSituacao>();
		
		for(AelMatrizSituacao matriz : situacao.getMatrizesSituacao()) {
			//if(matriz.getSituacaoItemSolicitacao() != null) {
				if (matriz.getSituacaoItemSolicitacao() == null || matriz.getSituacaoItemSolicitacao().getIndSituacao() == DominioSituacao.A) {
					matrizesSituacaoAtivas.add(matriz);
				}
			//}
		}
		
		return matrizesSituacaoAtivas;
	}
	
	public AelSitItemSolicitacoes getSituacaoItemSolicitacaoMatriz() {
		return matriz.getSituacaoItemSolicitacao();
	}
	
	public void setSituacaoItemSolicitacaoMatriz(AelSitItemSolicitacoes situacaoItemSolicitacaoMatriz) {
		matriz.setSituacaoItemSolicitacao(situacaoItemSolicitacaoMatriz);
	}
	
	public AelSitItemSolicitacoes getSituacaoItemSolicitacaoParaMatriz() {
		return matriz.getSituacaoItemSolicitacaoPara();
	}
	
	public void setSituacaoItemSolicitacaoParaMatriz(AelSitItemSolicitacoes situacaoItemSolicitacaoParaMatriz) {
		matriz.setSituacaoItemSolicitacaoPara(situacaoItemSolicitacaoParaMatriz);
	}
	
	//------------------------------------------------------
	//Métodos relacionados a operações sobre Perfis
	
	private List<Perfil> getPerfisMapeadosMatrizEmEdicao() throws ApplicationBusinessException {
		List<Perfil> selecionados = new ArrayList<Perfil>();
		
		for(AelAutorizacaoAlteracaoSituacao perfilMapeado : matriz.getAutorizacaoAlteracaoSituacoes()) {
			Perfil perfil = getPerflPorNome(perfilMapeado.getPerfil());
			if(perfil != null) {
				selecionados.add(perfil);
			}
		}
		
		return selecionados;
	}
	
	public List<CsePerfisVO> listarPerfisNaoSelecionados() throws ApplicationBusinessException {
		if(perfisNaoSelecionados == null) {
			List<Perfil> perfisDisponiveis = new ArrayList<Perfil>();
			perfisDisponiveis.addAll(getTodosPerfis());
			
			perfisDisponiveis.removeAll(getPerfisMapeadosMatrizEmEdicao());
			
			perfisNaoSelecionados = coverterCsePerfisParaVO(perfisDisponiveis);
		}
		ordenarPerfis(perfisNaoSelecionados);
		return perfisNaoSelecionados;
	}
	
	public List<CsePerfisVO> listarPerfisSelecionados() throws ApplicationBusinessException {
		if(perfisSelecionados == null) {
			perfisSelecionados = coverterCsePerfisParaVO(getPerfisMapeadosMatrizEmEdicao());
			
		}
		ordenarPerfis(perfisSelecionados);
		return perfisSelecionados;
	}
	
	private void ordenarPerfis(List<CsePerfisVO> perfis) {
		Collections.sort(perfis, new Comparator<CsePerfisVO>() {
			@Override
			public int compare(CsePerfisVO perfil1, CsePerfisVO perfil2) {
				return perfil1.getNome().compareTo(perfil2.getNome());
			}
		});
	}
	
	private List<CsePerfisVO> coverterCsePerfisParaVO(List<Perfil> perfis) {
		List<CsePerfisVO> perfisVOs = new ArrayList<CsePerfisVO>();
		for(Perfil perfil : perfis) {
			perfisVOs.add(new CsePerfisVO(perfil, false));
		}
		return perfisVOs;
	}
	
	public void incluirPerfis() {
		List<CsePerfisVO> adicionados = new ArrayList<CsePerfisVO>();
		for(CsePerfisVO perfilVO : perfisNaoSelecionados) {
			if(perfilVO.isMarcado()) {
				adicionados.add(perfilVO);
				perfisSelecionados.add(perfilVO);
				perfilVO.setMarcado(false);
			}
		}
		
		perfisNaoSelecionados.removeAll(adicionados);
	}
	
	public void excluirPerfis() {
		List<CsePerfisVO> removidos = new ArrayList<CsePerfisVO>();
		for(CsePerfisVO perfilVO : perfisSelecionados) {
			if(perfilVO.isMarcado()) {
				removidos.add(perfilVO);
				perfisNaoSelecionados.add(perfilVO);
				perfilVO.setMarcado(false);
			}
		}
		perfisSelecionados.removeAll(removidos);
	}
	
	private List<Perfil> getTodosPerfis() throws ApplicationBusinessException {
		if (todosPerfis == null) {
			//try {
				todosPerfis = cascaService.pesquisarPerfis("");
			//} catch (ApplicationBusinessException e) {
			//	throw new ApplicationBusinessException(e.getCode());
			//}
		}
		
		return todosPerfis;
	}
	
	private Perfil getPerflPorNome(String nome) throws ApplicationBusinessException {
		// FIXME Vicente: 01/09/2011
		// Refatorar para não fazer esse loop que é muito custoso
		for(Perfil perfil : getTodosPerfis()) {
			if(perfil.getNome().equalsIgnoreCase(nome)) {
				return perfil;
			}
		}
		
		return null;
	}
	
	//------------------------------------------------------
	//Getters e setters

	public String getCodigo() {
		return codigo;
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}
	
	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}
	
	public AelMatrizSituacao getMatriz() {
		return matriz;
	}
	
	public void setMatriz(AelMatrizSituacao matriz) {
		this.matriz = matriz;
	}

	public Boolean getMatrizEmEdicao() {
		return matrizEmEdicao;
	}

	public void setMatrizEmEdicao(Boolean matrizEmEdicao) {
		this.matrizEmEdicao = matrizEmEdicao;
	}

	public AelMatrizSituacao getMatrizSituacaoSelecionada() {
		return matrizSituacaoSelecionada;
	}

	public void setMatrizSituacaoSelecionada(AelMatrizSituacao matrizSituacaoSelecionada) {
		this.matrizSituacaoSelecionada = matrizSituacaoSelecionada;
	}
	
}
