package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.AmostraVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.vo.AelAmostraRecebidaVO;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AgruparExamesController extends ActionController {

	private static final String LISTAR_AMOSTRAS_SOLICITACAO_RECEBIMENTO = "listarAmostrasSolicitacaoRecebimento";

	private static final String EXCECAO_CAPTURADA = "Exceção capturada:";

	private static final Log LOG = LogFactory.getLog(AgruparExamesController.class);

	private static final long serialVersionUID = 8984763533625435561L;
	
	private Integer solicitacaoNumero;
	private Short seqp;
	private Integer solicitacaoProntuario;
	private String solicitacaoPaciente;
	private Integer solicitacaoPacCodigo;
	private Short unidadeSeq;
	
	private AelPatologista patologistaResponsavel;
	
	private List<ExameAndamentoVO> listaExamesAndamento;
	private Set<ExameAndamentoVO> listaExamesSelecionados;
	private List<AmostraVO> listaAmostras;
	private List<AmostraVO> listaAmostraSelecionadas;
	private List<AelPatologista> listaPatologistasResponsaveis;
	private Boolean allChecked = Boolean.FALSE;
	private Boolean selecionarTodos = Boolean.FALSE;
	private Boolean exibirPanelPatologistasResponsaveis;
	private List<AelAmostrasVO> listaAmostrasRecebidas;
	private AelAmostrasVO amostraRecebida;
	private Map<AghuParametrosEnum, String> situacoes = new HashMap<AghuParametrosEnum, String>();

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
		@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade; 
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject
	private ListarAmostrasSolicitacaoRecebimentoController listarAmostrasSolicitacaoRecebimentoController;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	/**
	 * Carrega exames em andamento e amostras dado uma solicitacao, numero de prontuario e codigo de amostras
	 * 
	 * @throws BaseException
	 */
	public void inicio() {
	 

		situacoes.put(AghuParametrosEnum.P_SITUACAO_A_COLETAR, parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR).getVlrTexto());
		situacoes.put(AghuParametrosEnum.P_SITUACAO_AGENDADO, parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO).getVlrTexto());
		situacoes.put(AghuParametrosEnum.P_SITUACAO_COLETADO, parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO).getVlrTexto());
		situacoes.put(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC, parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC).getVlrTexto());
		situacoes.put(AghuParametrosEnum.P_SITUACAO_EM_COLETA, parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_EM_COLETA).getVlrTexto());
		
		this.setSelecionarTodos(Boolean.FALSE);
		this.allChecked = Boolean.FALSE;
		exibirPanelPatologistasResponsaveis = Boolean.FALSE;
		listaPatologistasResponsaveis = new ArrayList<AelPatologista>();
		listaExamesSelecionados = new LinkedHashSet<ExameAndamentoVO>();
		listaAmostraSelecionadas = new ArrayList<AmostraVO>();
		try {
			listaExamesAndamento = examesFacade.obterExamesAndamento(solicitacaoPacCodigo, unidadeSeq);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		listaAmostras = examesFacade.obterAmostrasSolicitacao(solicitacaoNumero, this.getSeqpAmostrasRecebidas(), situacoes);

	}
	
	private List<Integer> getSeqpAmostrasRecebidas() {
		return examesFacade.getSeqpAmostrasRecebidas(listaAmostrasRecebidas);
	}
	
	public void adicionarPatologista() {
		try {
			examesFacade.adicionarPatologistaResponsavel(listaPatologistasResponsaveis, patologistaResponsavel);
			examesFacade.confirmarPatologistasResponsaveis(listaAmostraSelecionadas, patologistaResponsavel);
			if (!listaPatologistasResponsaveis.contains(patologistaResponsavel)) {
				listaPatologistasResponsaveis.add(patologistaResponsavel);
			}
			setPatologistaResponsavel(null);
			this.apresentarMsgNegocio(Severity.INFO, "PATOLOGISTA_ASSOCIADO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirPatologista(AelPatologista patologistaResponsavelSelecionado) {
		examesFacade.excluirPatologista(listaAmostraSelecionadas, patologistaResponsavelSelecionado);
		final List<AelPatologista> listaPatologistasResponsaveis = new ArrayList<AelPatologista>();
		for (final AelPatologista patologista : this.listaPatologistasResponsaveis) {
			if (!patologista.getSeq().equals(patologistaResponsavelSelecionado.getSeq())) {
				listaPatologistasResponsaveis.add(patologista);
			}
		}
		this.listaPatologistasResponsaveis = listaPatologistasResponsaveis;
	}
	
	public void agruparAmostras() {
		try {
			examesFacade.agruparAmostras(listaAmostraSelecionadas, listaExamesSelecionados, listaAmostras, listaPatologistasResponsaveis);
			this.apresentarMsgNegocio(Severity.INFO, "AMOSTRAS_AGRUPADAS_SUCESSO");
			this.selecionarTodos = this.allChecked = listaAmostraSelecionadas.size() == listaAmostras.size();
			this.mostrarPainelResp();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	private void mostrarPainelResp() {
		this.exibirPanelPatologistasResponsaveis = Boolean.FALSE;
		for (final AmostraVO amostra : listaAmostras) {
			if (amostra.isSelecionado()) {
				this.exibirPanelPatologistasResponsaveis = Boolean.TRUE;
				break;
			}
		}
	}

	public void desagruparAmostras(AmostraVO amostraVO) {
		examesFacade.desagruparAmostras(listaAmostraSelecionadas, listaAmostras, amostraVO);
		this.apresentarMsgNegocio(Severity.INFO, "AMOSTRAS_DESAGRUPADAS_SUCESSO");
		this.selecionarTodos = this.allChecked = listaAmostraSelecionadas.size() == listaAmostras.size();
		this.mostrarPainelResp();
	}
	
	//QUALIDADE
	/**
	 * Processo para agrupar amostras entre si ou amostras com exames
	 */
	public String gravarAmostrasExame() {
		try {
			final Map<AghuParametrosEnum, String> situacoes = new HashMap<AghuParametrosEnum, String>();
			situacoes.put(AghuParametrosEnum.P_SITUACAO_CANCELADO, parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO).getVlrTexto());
			situacoes.put(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA, parametroFacade.getAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA).getVlrTexto());
			
//			Long numeroExameOrigem = null;
//			if (this.listarAmostrasSolicitacaoRecebimentoController.getExameOrigemSelecionado() != null) {
//				numeroExameOrigem = this.listarAmostrasSolicitacaoRecebimentoController.getExameOrigemSelecionado().getNumeroExame();			
//			}
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			final List<AelAmostraRecebidaVO> amostrasRecebida = examesFacade.gravarAmostras(this.listaAmostras, this.solicitacaoNumero, servidorLogado,
					this.listarAmostrasSolicitacaoRecebimentoController.getUnidadeExecutora(),
					//this.listarAmostrasSolicitacaoRecebimentoController.getNumeroApOrigem(), this.getEnderecoRedeHostRemoto(), situacoes);
					listaExamesAndamento, this.getEnderecoRedeHostRemoto(), situacoes);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AMOSTRAS_RECEBIDAS_COM_SUCESSO",
					this.listarAmostrasSolicitacaoRecebimentoController.getUnidadeExecutora().getSeq());
			
			int etiquetasImpressas = 0;
			Long nroApImpresso = null;
			String siglaAnterior = null;
			
			for (AelAmostraRecebidaVO amostraRecebida : amostrasRecebida) {
				if (amostraRecebida != null && amostraRecebida.getEtiquetas() != null && !amostraRecebida.getEtiquetas().isEmpty()) {
					for (ImprimeEtiquetaVO etiquetaVo : amostraRecebida.getEtiquetas()) {
						try {
							this.aelpImprimeEtiqAp(etiquetaVo, this.listarAmostrasSolicitacaoRecebimentoController.getUnidadeExecutora());
							//examesFacade.aelpImprimeEtiqAp(etiquetaVo, super.getEnderecoIPv4HostRemoto());
							etiquetasImpressas++;
							if (!etiquetaVo.getNumeroAp().equals(nroApImpresso) && (siglaAnterior == null || etiquetaVo.getSigla().equals(siglaAnterior) )) {
								listarAmostrasSolicitacaoRecebimentoController.imprimirFichaTrabalho(etiquetaVo);
								nroApImpresso = etiquetaVo.getNumeroAp();
								siglaAnterior = etiquetaVo.getSigla();
							}
						} catch (SistemaImpressaoException e) {
							this.apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_IMPRIMIR_NRO_AP");
						} catch (BaseException e) {
							this.apresentarExcecaoNegocio(e);
						}
					}
				}
			}
			if (etiquetasImpressas > 0) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA_AMOSTRAS", etiquetasImpressas);
			}
			this.listarAmostrasSolicitacaoRecebimentoController.setPesquisar(true);
			return LISTAR_AMOSTRAS_SOLICITACAO_RECEBIMENTO;
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
		return null;
	}
	
	/**
	 * Monta etiquetas e envia para impressão.
	 * 
	 * @param etiquetaVo
	 * @param unidadeExecutora
	 * @throws SistemaImpressaoException
	 */
	private void aelpImprimeEtiqAp(final ImprimeEtiquetaVO etiquetaVo,
			AghUnidadesFuncionais unidadeExecutora)
			throws SistemaImpressaoException, BaseException {

		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		
		String nomeImpressora = solicitacaoExameFacade.obterNomeImpressoraEtiquetas(nomeMicrocomputador);
		
		String etiquetas = this.solicitacaoExameFacade
				.gerarZPLEtiquetaNumeroExame(etiquetaVo);
		this.sistemaImpressao.imprimir(etiquetas, nomeImpressora);
	}

	/**
	 * Seleciona um exame na grid como selecionado
	 * 
	 * @param exameAndamento exame selecionado na grid
	 */
	public void selecionarExameAndamento(ExameAndamentoVO exameAndamento) {
		examesFacade.selecionarExameAndamento(exameAndamento, listaExamesAndamento, listaExamesSelecionados);
	}

	/**
	 * Seleciona uma amostra na grid de amostras
	 * 
	 * @param amostraVO amostra selecionada
	 */
	public void selecionarAmostra(AmostraVO amostraVO) {
		examesFacade.selecionarAmostraExibirPatologistaResponsavel(amostraVO, listaAmostras, listaAmostraSelecionadas);

		if (!amostraVO.isSelecionado() && listaAmostraSelecionadas.size() == 1) {
			final AmostraVO amostraSelecionada = listaAmostraSelecionadas.get(0);
			if (amostraSelecionada.getPatologistasResponsaveis() == null) {
				listaPatologistasResponsaveis = new ArrayList<AelPatologista>();
			} else {
				listaPatologistasResponsaveis = new ArrayList<AelPatologista>(amostraSelecionada.getPatologistasResponsaveis());
			}
		} else {
			if (amostraVO.getPatologistasResponsaveis() == null) {
				listaPatologistasResponsaveis = new ArrayList<AelPatologista>();
			} else {
				listaPatologistasResponsaveis = new ArrayList<AelPatologista>(amostraVO.getPatologistasResponsaveis());
			}
		}
		this.setExibirPanelPatologistasResponsaveis(listaAmostraSelecionadas.size() >= 1);
	}
	
	private void desfazerSelecaoTodasAmostras() {
		this.allChecked = Boolean.FALSE;
		this.selecionarTodos = Boolean.FALSE;
		this.setExibirPanelPatologistasResponsaveis(Boolean.FALSE);
		examesFacade.desfazerSelecaoTodasAmostras(listaAmostras, listaAmostraSelecionadas);
	}
	
	
	/**
	 * Seleciona todas amostras da grid de amostras marcando elemento da lista como selecionado
	 */
	public void selecionarTodasAmostras() {
		if(this.allChecked) {
			this.selecionarTodos = Boolean.TRUE;
			examesFacade.selecionarTodasAmostras(allChecked, listaAmostras, listaAmostraSelecionadas);
			this.setExibirPanelPatologistasResponsaveis(listaAmostraSelecionadas.size() >= 1);
		} else {
			this.desfazerSelecaoTodasAmostras();
		}
	}
	
	/**
	 * Retorna a lista de patologista para popular a suggestionbox da tela
	 * de agrupar exames
	 * 
	 * @param param texto informado para utilizar na consulta de patologistas ativos
	 * @return lista de patologistas ativos
	 */
	public List<AelPatologista> pesquisarPatologistas(String param) {
		return this.returnSGWithCount(examesFacade.pesquisarPatologistasResponsaveis(param),pesquisarPatologistasCount(param));
	}

	/**
	 * Retorna o total patologistas para popular a suggestionbox da tela
	 * de agrupar exames
	 * 
	 * @param param texto informado para utilizar na consulta de patologistas ativos
	 * @return total de patologistas ativos
	 */
	public Long pesquisarPatologistasCount(String param) {
		return examesFacade.pesquisarPatologistasResponsaveisCount(param);
	}
	
	/**
	 * Retorna para tela anterior ao clicar no botao voltar da 
	 * tela de agrupar exames
	 * @return mapeamento voltar no agruparExames.page.xml
	 */
	public String voltar() {
		return LISTAR_AMOSTRAS_SOLICITACAO_RECEBIMENTO;
	}
	
	public List<ExameAndamentoVO> getListaExamesAndamento() {
		return listaExamesAndamento;
	}

	public void setListaExamesAndamento(List<ExameAndamentoVO> listaExamesAndamento) {
		this.listaExamesAndamento = listaExamesAndamento;
	}

	public List<AmostraVO> getListaAmostras() {
		return listaAmostras;
	}

	public void setListaAmostras(List<AmostraVO> listaAmostras) {
		this.listaAmostras = listaAmostras;
	}

	public Boolean getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(Boolean allChecked) {
		this.allChecked = allChecked;
	}

	public Boolean getSelecionarTodos() {
		return selecionarTodos;
	}

	public void setSelecionarTodos(Boolean selecionarTodos) {
		this.selecionarTodos = selecionarTodos;
	}

	public List<AelPatologista> getListaPatologistasResponsaveis() {
		return listaPatologistasResponsaveis;
	}

	public void setListaPatologistasResponsaveis(List<AelPatologista> listaPatologistasResponsaveis) {
		this.listaPatologistasResponsaveis = listaPatologistasResponsaveis;
	}

	public void setExibirPanelPatologistasResponsaveis(
			Boolean exibirPanelPatologistasResponsaveis) {
		this.exibirPanelPatologistasResponsaveis = exibirPanelPatologistasResponsaveis;
	}

	public Boolean getExibirPanelPatologistasResponsaveis() {
		return exibirPanelPatologistasResponsaveis;
	}


	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}


	public Integer getSolicitacaoNumero() {
		return solicitacaoNumero;
	}

	public void setSolicitacaoNumero(Integer solicitacaoNumero) {
		this.solicitacaoNumero = solicitacaoNumero;
	}

	public Integer getSolicitacaoProntuario() {
		return solicitacaoProntuario;
	}

	public void setSolicitacaoProntuario(Integer solicitacaoProntuario) {
		this.solicitacaoProntuario = solicitacaoProntuario;
	}

	public AelPatologista getPatologistaResponsavel() {
		return patologistaResponsavel;
	}

	public void setPatologistaResponsavel(AelPatologista patologistaResponsavel) {
		this.patologistaResponsavel = patologistaResponsavel;
	}

	public Set<ExameAndamentoVO> getListaExamesSelecionados() {
		return listaExamesSelecionados;
	}

	public void setListaExamesSelecionados(
			Set<ExameAndamentoVO> listaExamesSelecionados) {
		this.listaExamesSelecionados = listaExamesSelecionados;
	}

	public void setListaAmostrasRecebidas(List<AelAmostrasVO> listaAmostrasRecebidas) {
		this.listaAmostrasRecebidas = listaAmostrasRecebidas;
	}

	public List<AelAmostrasVO> getListaAmostrasRecebidas() {
		return listaAmostrasRecebidas;
	}

	public void setAmostraRecebida(AelAmostrasVO amostraRecebida) {
		this.amostraRecebida = amostraRecebida;
	}

	public AelAmostrasVO getAmostraRecebida() {
		return amostraRecebida;
	}

	public Short getUnidadeSeq() {
		return unidadeSeq;
	}

	public void setUnidadeSeq(Short unidadeSeq) {
		this.unidadeSeq = unidadeSeq;
	}
	
	public Integer getSolicitacaoPacCodigo() {
		return solicitacaoPacCodigo;
	}
	
	public void setSolicitacaoPacCodigo(Integer solicitacaoPacCodigo) {
		this.solicitacaoPacCodigo = solicitacaoPacCodigo;
	}
	
	public String getSolicitacaoPaciente() {
		return solicitacaoPaciente;
	}
	
	public void setSolicitacaoPaciente(String solicitacaoPaciente) {
		this.solicitacaoPaciente = solicitacaoPaciente;
	}

	
}
