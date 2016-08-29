package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoHistoricoAdvertForn;
import br.gov.mec.aghu.model.ScoHistoricoAdvertFornId;
import br.gov.mec.aghu.model.ScoHistoricoMultaForn;
import br.gov.mec.aghu.model.ScoHistoricoMultaFornId;
import br.gov.mec.aghu.model.ScoHistoricoOcorrForn;
import br.gov.mec.aghu.model.ScoHistoricoOcorrFornId;
import br.gov.mec.aghu.model.ScoHistoricoSuspensForn;
import br.gov.mec.aghu.model.ScoHistoricoSuspensFornId;
import br.gov.mec.aghu.model.ScoTipoOcorrForn;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;


public class ConsultarPenalidadesFornecedorController extends ActionController {

	private static final String CADASTRAR_PENALIDADE_FORNECEDOR = "cadastrarPenalidadeFornecedor";

	@PostConstruct
	protected void inicializar(){
	 LOG.debug("Iniciando conversation");
	 this.begin(conversation);
	}
	
	private static final Log LOG = LogFactory.getLog(ConsultarPenalidadesFornecedorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2701061706535656107L;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private RelatorioPendenciasFornecedorController relatorioPendenciasFornecedorController;
	
	private VScoFornecedor fornecedor;
	private String tipoPenalidade; 
	private Date periodoInicialPenalidade;
	private Date periodoFinalPenalidade;

	private List<ScoHistoricoAdvertForn> listaAdvertencias;
	private List<ScoHistoricoMultaForn> listaMultas;
	private List<ScoHistoricoSuspensForn> listaSuspensoes;
	private List<ScoHistoricoOcorrForn> listaOcorrencias;
	
	private BigDecimal valorTotal = BigDecimal.ZERO;
	private Integer tamanhoLista = 240;
	
	private final String ADVERTENCIA = "Advertência";
	private final String MULTA = "Multa";
	private final String OCORRENCIA = "Ocorrência";
	private final String SUSPENSAO = "Suspensão";
	
	private Boolean edicao = false;
	private Boolean pesquisaRealizada = false;
	private Boolean listasVazias = true;
	
	private String tipoPenalidadeCadastro;
	private ScoTipoOcorrForn tipoOcorrencia;
	private ScoHistoricoOcorrForn ocorrencia;
	private ScoHistoricoSuspensForn suspensao;
	private ScoHistoricoAdvertForn advertencia;
	private ScoHistoricoMultaForn multa;
	
	
	private Integer numeroFrn;
	
	private String voltarParaUrl;
	
	private Boolean edicaoNovo = false;
	
	public void inicio() {
	 

		if(numeroFrn != null) {
			List<VScoFornecedor> listaFornecedores = listarFornecedores(numeroFrn.toString());
			if(listaFornecedores != null && !listaFornecedores.isEmpty()) {
				fornecedor = listaFornecedores.get(0);
				this.pesquisar();
			}
		}
	
	}
	
	public void pesquisar() {
		try {
			
			if(periodoInicialPenalidade != null && periodoFinalPenalidade == null) {
				periodoFinalPenalidade = new Date();
			}
			
			getListasLimpas();

			periodoFinalPenalidade = (periodoInicialPenalidade != null && periodoFinalPenalidade == null) ? new Date() : periodoFinalPenalidade;
			
			getTipoPesquisa();

			getLimiteQuantidadeResultados();
			
			pesquisaRealizada = true;
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		verificarListasVazias();
	}

	private void verificarListasVazias() {
		if((listaAdvertencias == null || listaAdvertencias.isEmpty()) && (listaMultas == null || listaMultas.isEmpty()) &&
				(listaSuspensoes == null || listaSuspensoes.isEmpty()) && (listaOcorrencias == null || listaOcorrencias.isEmpty())) {
			listasVazias = true;
		}
		else {
			listasVazias = false;
		}
	}
	
	private void getTipoPesquisa() throws BaseException{
		Integer numeroFornecedor = fornecedor != null ? fornecedor.getNumeroFornecedor() : null;
		
		if(StringUtils.isBlank(tipoPenalidade) || ADVERTENCIA.equalsIgnoreCase(tipoPenalidade)) {
			listaAdvertencias = comprasFacade.pesquisarAdvertenciasFornecedor(numeroFornecedor, periodoInicialPenalidade, periodoFinalPenalidade);
		}
		if(StringUtils.isBlank(tipoPenalidade) || MULTA.equalsIgnoreCase(tipoPenalidade)) {
			listaMultas = comprasFacade.pesquisarMultasFornecedor(numeroFornecedor, periodoInicialPenalidade, periodoFinalPenalidade);
		}			
		if(StringUtils.isBlank(tipoPenalidade) || SUSPENSAO.equalsIgnoreCase(tipoPenalidade)) {			
			listaSuspensoes = comprasFacade.pesquisarSuspensoesFornecedor(numeroFornecedor, periodoInicialPenalidade, periodoFinalPenalidade);
		}
		if(StringUtils.isBlank(tipoPenalidade) || OCORRENCIA.equalsIgnoreCase(tipoPenalidade)) {						
			listaOcorrencias = comprasFacade.pesquisarOcorrenciasFornecedor(numeroFornecedor, periodoInicialPenalidade, periodoFinalPenalidade);
		}
	}
	
	public void limpar() {
		fornecedor = null;
		tipoPenalidade = null;
		periodoInicialPenalidade = null;
		periodoFinalPenalidade = null;

		getListasLimpas();
		
		valorTotal = BigDecimal.ZERO;
		tamanhoLista = 240;
		pesquisaRealizada = false;
	}
	
	private void getListasLimpas() {
		listaAdvertencias = null;
		listaMultas = null;
		listaSuspensoes = null;
		listaOcorrencias = null;
	}
	
	public String novo() {

		if(fornecedor == null) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					"MSG_PREENCHIMENTO_FORNECEDOR_OBRIGATORIO_CADASTRO");
			return "";
		}
		
		edicao = false;
		edicaoNovo = true;
		tipoPenalidadeCadastro = null;
		tipoOcorrencia = null;
		advertencia = new ScoHistoricoAdvertForn();
		advertencia.setDtEmissao(new Date());
		multa = new ScoHistoricoMultaForn();
		multa.setDtEmissao(new Date());
		ocorrencia = new ScoHistoricoOcorrForn();
		ocorrencia.setDtEmissao(new Date());
		suspensao = new ScoHistoricoSuspensForn();
		suspensao.setDtInicio(new Date());
		
		return CADASTRAR_PENALIDADE_FORNECEDOR;
	}
	
	private void getLimiteQuantidadeResultados() {
		valorTotal = BigDecimal.ZERO;
		if(listaMultas != null && !listaMultas.isEmpty()) {
			for(ScoHistoricoMultaForn multa : listaMultas) {
				valorTotal = valorTotal.add(multa.getValor());
			}
		}
		
		int numeroListasExibidas = 0;
		if(listaAdvertencias != null && !listaAdvertencias.isEmpty()) {
			numeroListasExibidas++;
		}
		if(listaMultas != null && !listaMultas.isEmpty()) {
			numeroListasExibidas++;
		}
		if(listaSuspensoes != null && !listaSuspensoes.isEmpty()) {
			numeroListasExibidas++;
		}
		if(listaOcorrencias != null && !listaOcorrencias.isEmpty()) {
			numeroListasExibidas++;
		}

		if(numeroListasExibidas > 1) {
			tamanhoLista = 90;
		}
	}
	
	public String truncarTexto(String texto) {
		return StringUtil.trunc(texto, true, 65L);
	}
	
	public String cancelar() {
		if (StringUtils.isNotBlank(voltarParaUrl) && !this.edicaoNovo){
			return voltarParaUrl;
		}
		edicaoNovo = false;
		return "consultarPenalidadeFornecedor";
	}

	public String gravar() {
		try {
			if(ADVERTENCIA.equalsIgnoreCase(tipoPenalidadeCadastro)) {
				if(advertencia.getId() == null) {
					advertencia.setId(new ScoHistoricoAdvertFornId(fornecedor.getNumeroFornecedor(), null));
				}
				comprasFacade.persistirAdvertencia(advertencia);
			}
			else if(MULTA.equalsIgnoreCase(tipoPenalidadeCadastro)) {
				if(multa.getId() == null) {
					multa.setId(new ScoHistoricoMultaFornId(fornecedor.getNumeroFornecedor(), null));
				}
				comprasFacade.persistirMulta(multa);
			}
			else if(OCORRENCIA.equalsIgnoreCase(tipoPenalidadeCadastro)) {
				ocorrencia.setScoTipoOcorrForn(tipoOcorrencia);
				if(ocorrencia.getId() == null) {
					ocorrencia.setId(new ScoHistoricoOcorrFornId(fornecedor.getNumeroFornecedor(), null));
				}
				comprasFacade.persistirOcorrencia(ocorrencia);
			}
			else if(SUSPENSAO.equalsIgnoreCase(tipoPenalidadeCadastro)) {
				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
				if(suspensao.getId() == null) {
					suspensao.setId(new ScoHistoricoSuspensFornId(fornecedor.getNumeroFornecedor(), null));					
				}
				comprasFacade.persistirSuspensao(suspensao, servidorLogado);
			}
			
			if(edicao) {
				this.apresentarMsgNegocio(Severity.INFO, 
						"MSG_PENALIDADE_ALTERADA_SUCESSO");
			}
			else {
				this.apresentarMsgNegocio(Severity.INFO, 
						"MSG_PENALIDADE_INCLUIDA_SUCESSO");
			}
			edicaoNovo = false;
			this.pesquisar();
			
			return "consultarPenalidadeFornecedor";
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void gerarPdf() {
		try {
			if(fornecedor == null) {
				this.apresentarMsgNegocio(Severity.ERROR, 
						"MSG_PREENCHIMENTO_FORNECEDOR_OBRIGATORIO_CADASTRO");
				return;
			}

			this.pesquisar();
			
			relatorioPendenciasFornecedorController.setInicioPeriodo(periodoInicialPenalidade);
			relatorioPendenciasFornecedorController.setFimPeriodo(periodoFinalPenalidade);
			relatorioPendenciasFornecedorController.setFornecedor(fornecedor);
			relatorioPendenciasFornecedorController.setListaAdvertencias(listaAdvertencias);
			relatorioPendenciasFornecedorController.setListaMultas(listaMultas);
			relatorioPendenciasFornecedorController.setListaOcorrencias(listaOcorrencias);
			relatorioPendenciasFornecedorController.setListaSuspensoes(listaSuspensoes);
			relatorioPendenciasFornecedorController.carregarLogo();
			relatorioPendenciasFornecedorController.downloadPdf();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch(Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					"MSG_ERRO_IMPRIMIR_PDF");
		}
	}

	public void gerarCsv() {
		try {
			if(fornecedor == null && (periodoInicialPenalidade == null)) {
				this.apresentarMsgNegocio(Severity.ERROR, 
						"FORNECEDOR_OU_PERIODO_OBRIGATORIOS");
				return;
			}
			
			this.pesquisar();
			
			relatorioPendenciasFornecedorController.setInicioPeriodo(periodoInicialPenalidade);
			relatorioPendenciasFornecedorController.setFimPeriodo(periodoFinalPenalidade);
			relatorioPendenciasFornecedorController.setFornecedor(fornecedor);
			relatorioPendenciasFornecedorController.setListaAdvertencias(listaAdvertencias);
			relatorioPendenciasFornecedorController.setListaMultas(listaMultas);
			relatorioPendenciasFornecedorController.setListaOcorrencias(listaOcorrencias);
			relatorioPendenciasFornecedorController.setListaSuspensoes(listaSuspensoes);
			relatorioPendenciasFornecedorController.downloadCsv();
		} catch(Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					"MSG_ERRO_GERAR_CSV");
		}
	}

	public String editar(ScoHistoricoAdvertFornId id) {
		tipoPenalidadeCadastro = ADVERTENCIA;
		advertencia = comprasFacade.obterAdvertenciaPorId(id);
		edicao = true;
		edicaoNovo = true;
		return CADASTRAR_PENALIDADE_FORNECEDOR;
	}

	public String editar(ScoHistoricoMultaFornId id) {
		tipoPenalidadeCadastro = MULTA;
		multa = comprasFacade.obterMultaPorId(id);
		edicao = true;
		edicaoNovo = true;
		return CADASTRAR_PENALIDADE_FORNECEDOR;
	}

	public String editar(ScoHistoricoOcorrFornId id) {
		tipoPenalidadeCadastro = OCORRENCIA;
		ocorrencia = comprasFacade.obterOcorrenciaPorId(id);
		tipoOcorrencia = ocorrencia.getScoTipoOcorrForn();
		edicao = true;
		edicaoNovo = true;
		return CADASTRAR_PENALIDADE_FORNECEDOR;
	}

	public String editar(ScoHistoricoSuspensFornId id) {
		tipoPenalidadeCadastro = SUSPENSAO;
		suspensao = comprasFacade.obterSuspensaoPorId(id);
		edicao = true;
		edicaoNovo = true;
		return CADASTRAR_PENALIDADE_FORNECEDOR;
	}
	
	public void excluir(ScoHistoricoAdvertFornId id) {
		advertencia = comprasFacade.obterAdvertenciaPorId(id);
		multa = null;
		ocorrencia = null;
		suspensao = null;
	}

	public void excluir(ScoHistoricoMultaFornId id) {
		multa = comprasFacade.obterMultaPorId(id);
		advertencia = null;
		ocorrencia = null;
		suspensao = null;
	}

	public void excluir(ScoHistoricoOcorrFornId id) {
		ocorrencia = comprasFacade.obterOcorrenciaPorId(id);
		advertencia = null;
		multa = null;
		suspensao = null;
	}

	public void excluir(ScoHistoricoSuspensFornId id) {
		suspensao = comprasFacade.obterSuspensaoPorId(id);
		advertencia = null;
		multa = null;
		ocorrencia = null;
	}

	public void remover() {
		if(advertencia != null) {
			comprasFacade.removerAdvertencia(advertencia.getId());
		} else if(multa != null) {
			comprasFacade.removerMulta(multa.getId());
		} else if(ocorrencia != null) {
			comprasFacade.removerOcorrencia(ocorrencia.getId());
		} else if(suspensao != null) {
			comprasFacade.removerSuspensao(suspensao.getId());
		}
		
		this.pesquisar();
		
		this.apresentarMsgNegocio(Severity.INFO, 
				"MSG_PENALIDADE_EXCLUIDA_SUCESSO");
	}
	
	public List<VScoFornecedor> listarFornecedores(String parametro){		
		return this.comprasFacade.pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(parametro);
	}

	public Long listarFornecedoresCount(String parametro){		
		return this.comprasFacade.pesquisarVFornecedorPorNumeroCgcCpfRazaoSocialCount(parametro);
	}

	public List<ScoTipoOcorrForn> listarTiposOcorrencia(String parametro){		
		return this.returnSGWithCount(this.comprasFacade.pesquisarTipoOcorrenciaPorCodigoOuDescricao(parametro),listarTiposOcorrenciaCount(parametro));
	}

	public Long listarTiposOcorrenciaCount(String parametro){		
		return this.comprasFacade.pesquisarTipoOcorrenciaPorCodigoOuDescricaoCount(parametro);
	}

	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getTipoPenalidade() {
		return tipoPenalidade;
	}

	public void setTipoPenalidade(String tipoPenalidade) {
		this.tipoPenalidade = tipoPenalidade;
	}

	public Date getPeriodoInicialPenalidade() {
		return periodoInicialPenalidade;
	}

	public void setPeriodoInicialPenalidade(Date periodoInicialPenalidade) {
		this.periodoInicialPenalidade = periodoInicialPenalidade;
	}

	public Date getPeriodoFinalPenalidade() {
		return periodoFinalPenalidade;
	}

	public void setPeriodoFinalPenalidade(Date periodoFinalPenalidade) {
		this.periodoFinalPenalidade = periodoFinalPenalidade;
	}

	public List<ScoHistoricoAdvertForn> getListaAdvertencias() {
		return listaAdvertencias;
	}

	public void setListaAdvertencias(List<ScoHistoricoAdvertForn> listaAdvertencias) {
		this.listaAdvertencias = listaAdvertencias;
	}

	public List<ScoHistoricoMultaForn> getListaMultas() {
		return listaMultas;
	}

	public void setListaMultas(List<ScoHistoricoMultaForn> listaMultas) {
		this.listaMultas = listaMultas;
	}

	public List<ScoHistoricoSuspensForn> getListaSuspensoes() {
		return listaSuspensoes;
	}

	public void setListaSuspensoes(List<ScoHistoricoSuspensForn> listaSuspensoes) {
		this.listaSuspensoes = listaSuspensoes;
	}

	public List<ScoHistoricoOcorrForn> getListaOcorrencias() {
		return listaOcorrencias;
	}

	public void setListaOcorrencias(List<ScoHistoricoOcorrForn> listaOcorrencias) {
		this.listaOcorrencias = listaOcorrencias;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Integer getTamanhoLista() {
		return tamanhoLista;
	}

	public void setTamanhoLista(Integer tamanhoLista) {
		this.tamanhoLista = tamanhoLista;
	}

	public ScoTipoOcorrForn getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	public void setTipoOcorrencia(ScoTipoOcorrForn tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}

	public ScoHistoricoOcorrForn getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(ScoHistoricoOcorrForn ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public String getTipoPenalidadeCadastro() {
		return tipoPenalidadeCadastro;
	}

	public void setTipoPenalidadeCadastro(String tipoPenalidadeCadastro) {
		this.tipoPenalidadeCadastro = tipoPenalidadeCadastro;
	}

	public ScoHistoricoSuspensForn getSuspensao() {
		return suspensao;
	}

	public void setSuspensao(ScoHistoricoSuspensForn suspensao) {
		this.suspensao = suspensao;
	}

	public ScoHistoricoAdvertForn getAdvertencia() {
		return advertencia;
	}

	public void setAdvertencia(ScoHistoricoAdvertForn advertencia) {
		this.advertencia = advertencia;
	}

	public ScoHistoricoMultaForn getMulta() {
		return multa;
	}

	public void setMulta(ScoHistoricoMultaForn multa) {
		this.multa = multa;
	}

	public Boolean getPesquisaRealizada() {
		return pesquisaRealizada;
	}

	public void setPesquisaRealizada(Boolean pesquisaRealizada) {
		this.pesquisaRealizada = pesquisaRealizada;
	}

	public Boolean getListasVazias() {
		return listasVazias;
	}

	public void setListasVazias(Boolean listasVazias) {
		this.listasVazias = listasVazias;
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}

	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
}
