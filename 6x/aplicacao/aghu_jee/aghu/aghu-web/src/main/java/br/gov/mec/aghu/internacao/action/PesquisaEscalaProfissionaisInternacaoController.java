package br.gov.mec.aghu.internacao.action;

import java.util.Date;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaEscalaProfissionaisInternacaoController extends ActionController {

	private static final long serialVersionUID = 8269280969503282337L;
	private static final String REDIRECT_MANTER_ESCALA_PROFISSIONAL = "manterEscalaProfissional";
	private static final String REDIRECT_ESCALA_PROFISSIONAIS_INTERNACAO = "escalaProfissionaisInternacao";
	
	// filtros de pesquisa
	private Integer matriculaServidor;
	private Integer vinculoServidor;
	private Integer seqEspecialidade;
	private Integer codigoConvenio;
	private Date dataInicio;
	private Date dataFim;
	//descrições a serem mostradas
	private String siglaEspecialidade;
	private String descricaoConvenio;
	private String registroServidor;
	private String nomeServidor;
	
	private String cameFrom = "";
	
	@EJB
	IInternacaoFacade internacaoFacade;

	@Inject
	private ManterEscalaProfissionaisInternacaoController manterEscalaProfissionaisInternacaoController;
	
	@Inject 
	private EscalaProfissionaisInternacaoController escalaProfissionaisInternacaoController;
	
	@Inject
	private PesquisaEscalaProfissionaisInternacaoPaginatorController pesquisaEscalaProfissionaisInternacaoPaginatorController;
	
	private Date escalaDataInicio;
	
	private boolean incluirEscala = false;
	private boolean inicializado;
	
	public void iniciar(){
		inicializado = true;
		pesquisar();
	}

	public void pesquisar() {
		//valida parametros obrigatorios
		if(matriculaServidor==null||vinculoServidor==null||seqEspecialidade==null||codigoConvenio==null){
			this.apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_PARAMETROS_OBRIGATORIOS");
			return;
		}
		//validata datas quando informado
		if(dataFim!=null){
			if(dataInicio==null){
				this.apresentarMsgNegocio(Severity.ERROR,
					"MENSAGEM_DATA_FIM_OBRIGATORIO_INICIO");
				return;
			}
			if(dataFim.before(dataInicio)){
				this.apresentarMsgNegocio(Severity.ERROR,
				"MENSAGEM_DATA_FIM_MAIOR_QUE_INICIO");
				return;			
			}
		}

		pesquisaEscalaProfissionaisInternacaoPaginatorController.reiniciarPaginator();
		pesquisaEscalaProfissionaisInternacaoPaginatorController.setMaxResults(12);
		pesquisaEscalaProfissionaisInternacaoPaginatorController.setAtivo(true);

		// limpa filtros short
		pesquisaEscalaProfissionaisInternacaoPaginatorController.setVinculoServidor(null);
		pesquisaEscalaProfissionaisInternacaoPaginatorController.setCodigoConvenio(null);
		// seta os filtros
		pesquisaEscalaProfissionaisInternacaoPaginatorController.setMatriculaServidor(matriculaServidor);
		if (vinculoServidor != null) {
			pesquisaEscalaProfissionaisInternacaoPaginatorController.setVinculoServidor(vinculoServidor.shortValue());
		}
		pesquisaEscalaProfissionaisInternacaoPaginatorController.setSeqEspecialidade(seqEspecialidade);
		if (codigoConvenio != null) {
			pesquisaEscalaProfissionaisInternacaoPaginatorController.setCodigoConvenio(codigoConvenio.shortValue());
		}
		pesquisaEscalaProfissionaisInternacaoPaginatorController.setDataInicio(dataInicio);
		pesquisaEscalaProfissionaisInternacaoPaginatorController.setDataFim(dataFim);

		//habilita botão novo
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.dataInicio = null;
		this.dataFim = null;
		this.pesquisaEscalaProfissionaisInternacaoPaginatorController.setAtivo(false);
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela. 
	 */
	public String cancelar() {
		limparPesquisa();
//		if(cameFrom.equalsIgnoreCase("pesquisaEscalaProfissionaisInternacaoController")){
			escalaProfissionaisInternacaoController.setRetorno("true");
			return REDIRECT_ESCALA_PROFISSIONAIS_INTERNACAO;
//		}
//		return null;
	}
	

	public String alterarEscala(){
		this.incluirEscala = false;
		this.escalaDataInicio = pesquisaEscalaProfissionaisInternacaoPaginatorController.getObjetoSelecionado().getDataInicio();
		this.inicializado = false;
		return this.redirecionarParaManterEscalaProfissional();
	}
	
	/**
	 * Redireciona para inclusão de uma nova escala.
	 * @return
	 */
	public String incluirEscala(){
		this.incluirEscala = true;
		this.escalaDataInicio = null;
		return this.redirecionarParaManterEscalaProfissional();
	}
	
	private String redirecionarParaManterEscalaProfissional(){	
		this.manterEscalaProfissionaisInternacaoController.setCameFrom("escalaInternacao");
		this.manterEscalaProfissionaisInternacaoController.setPecPreSerMatricula(matriculaServidor);
		this.manterEscalaProfissionaisInternacaoController.setPecPreSerVinCodigo(vinculoServidor);
		this.manterEscalaProfissionaisInternacaoController.setPecCnvCodigo(codigoConvenio);
		this.manterEscalaProfissionaisInternacaoController.setPecPreEspSeq(seqEspecialidade);
		this.manterEscalaProfissionaisInternacaoController.setRegistroServidor(registroServidor);
		this.manterEscalaProfissionaisInternacaoController.setNomeServidor(nomeServidor);
		this.manterEscalaProfissionaisInternacaoController.setSiglaEspecialidade(siglaEspecialidade);
		this.manterEscalaProfissionaisInternacaoController.setDescricaoConvenio(descricaoConvenio);
		this.manterEscalaProfissionaisInternacaoController.setIncluirEscala(incluirEscala);
		this.manterEscalaProfissionaisInternacaoController.setDtInicio(escalaDataInicio);
		this.manterEscalaProfissionaisInternacaoController.inicio();
		return REDIRECT_MANTER_ESCALA_PROFISSIONAL;
	}

	// getters and setters
	public PesquisaEscalaProfissionaisInternacaoPaginatorController getPaginator() {
		return pesquisaEscalaProfissionaisInternacaoPaginatorController;
	}

	public void setPaginator(
			final PesquisaEscalaProfissionaisInternacaoPaginatorController paginator) {
		this.pesquisaEscalaProfissionaisInternacaoPaginatorController = paginator;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(final Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(final Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(final Integer codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}

	public void setMatriculaServidor(final Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}

	public Integer getVinculoServidor() {
		return vinculoServidor;
	}

	public void setVinculoServidor(final Integer vinculoServidor) {
		this.vinculoServidor = vinculoServidor;
	}

	public Integer getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(final Integer seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(final String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}

	public void setDescricaoConvenio(final String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}

	public String getRegistroServidor() {
		return registroServidor;
	}

	public void setRegistroServidor(final String registroServidor) {
		this.registroServidor = registroServidor;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(final String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(final String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Date getEscalaDataInicio() {
		return escalaDataInicio;
	}

	public void setEscalaDataInicio(final Date escalaDataInicio) {
		this.escalaDataInicio = escalaDataInicio;
	}

	public boolean isIncluirEscala() {
		return incluirEscala;
	}

	public void setIncluirEscala(final boolean incluirEscala) {
		this.incluirEscala = incluirEscala;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(final boolean inicializado) {
		this.inicializado = inicializado;
	}
	
}