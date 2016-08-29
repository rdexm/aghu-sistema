package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.action.AtenderSolicitacaoInternacaoController;
import br.gov.mec.aghu.internacao.action.CadastroInternacaoController;
import br.gov.mec.aghu.internacao.action.RelatorioSolicitacaoInternacaoController;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaSolicitacaoInternacaoPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -261631397244157362L;
	private static final Log LOG = LogFactory.getLog(PesquisaSolicitacaoInternacaoPaginatorController.class);
	private static final String REDIRECT_RELATORIO_SOLICITACAO_INTERNACAO = "internacao-relatorioSolicitacaoInternacao";
	private static final String REDIRECT_PESQUISA_PACIENTE = "paciente-pesquisaPaciente";
	private static final String REDIRECT_ATENDER_SOLICITACAO_INTERNACAO = "internacao-atenderSolicitacaoInternacao";
	private static final String REDIRECT_CADASTRO_INTERNACAO = "internacao-cadastroInternacao";
	private static final String REDIRECT_DETALHAR_SOLICITACAO_INTERNACAO = "detalhaSolicitacaoInternacao";
	private static final String PAGE_PESQUISAR_SOLICITACAO_INTERNACAO = "pesquisarSolicitacaoInternacao";
	
	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private RelatorioSolicitacaoInternacaoController  relatorioSolicitacaoInternacaoController;
	
	@Inject
	private  PesquisaPacienteController pesquisaPacienteController;
	
	@Inject
	private DetalhaSolicitacaoInternacaoController detalhaSolicitacaoInternacaoController;
	
	@Inject 
	private AtenderSolicitacaoInternacaoController atenderSolicitacaoInternacaoController ;

	@Inject
	private CadastroInternacaoController cadastroInternacaoController;
	
	private AghClinicas clinicaPesquisa;

	private ServidorConselhoVO servidorConselhoVOPesquisa;
	
	private ConvenioPlanoVO convenioPlanoVOPesquisa;

	private AghEspecialidades especialidadePesquisa;
	
	private AipPacientes pacientePesquisa;

	private DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao;
	
	private Date dataSolicitacao;
	
	private Date dataPrevisao;
	
	private Integer prontuario;

	private Integer codigoPaciente;
	
	private String nomePaciente;
	
	private AinSolicitacoesInternacao solicitacaoInternacaoAlteracao;
	
	@Inject @Paginator
	private DynamicDataModel<AipOcupacoes> dataModel;

	@PostConstruct
	public void init(){
		this.begin(this.conversation);
		this.carregarPaciente();
	}
	
	public void carregarPaciente() {
		if(this.codigoPaciente != null) {
			this.pacientePesquisa = this.pacienteFacade.obterPacientePorCodigo(this.codigoPaciente);
			this.prontuario = this.pacientePesquisa.getProntuario();
			this.nomePaciente = this.pacientePesquisa.getNome();
		}
	}
	
	
	public void pesquisar() {
		if (this.pacientePesquisa == null && this.prontuario != null){
			try {
				this.pacientePesquisa = this.pacienteFacade.pesquisarPacientePorProntuario(this.prontuario);
			} catch (Exception e) {
				LOG.error("Exceção capturada: ", e);
				this.pacientePesquisa = null;
			}
		}
		this.reiniciarPaginator();
	}
	
	/*
	 * Método utilizado para redirecionar para a impressão do relatório.
	 * Necessário, devido ao problema encontrado na atualização dos atributos dataSolicitacao e indSolicitacaoInternacao
	 */
	public String visualizarRelatorio(){	
		this.relatorioSolicitacaoInternacaoController.setClinicaPesquisa(clinicaPesquisa);
		this.relatorioSolicitacaoInternacaoController.setServidorConselhoVOPesquisa(servidorConselhoVOPesquisa);
		this.relatorioSolicitacaoInternacaoController.setConvenioPlanoVOPesquisa(convenioPlanoVOPesquisa);
		this.relatorioSolicitacaoInternacaoController.setEspecialidadePesquisa(especialidadePesquisa);
		this.relatorioSolicitacaoInternacaoController.setPacientePesquisa(pacientePesquisa);
		this.relatorioSolicitacaoInternacaoController.setIndSolicitacaoInternacao(indSolicitacaoInternacao);
		this.relatorioSolicitacaoInternacaoController.setDataSolicitacao(dataSolicitacao);
		this.relatorioSolicitacaoInternacaoController.setDataPrevisao(dataPrevisao);
		return REDIRECT_RELATORIO_SOLICITACAO_INTERNACAO;
	}
	
	public String pesquisarPaciente(){
		this.pesquisaPacienteController.limparCampos();
		this.pesquisaPacienteController.setCameFrom("solicitacaoInternacao");
		return REDIRECT_PESQUISA_PACIENTE;
	}
	
	public String atenderSolicitacaoInternacao(Integer seqSolicitacaoInternacao){
		this.atenderSolicitacaoInternacaoController.setSeqSolicitacaoInternacao(seqSolicitacaoInternacao);
		this.atenderSolicitacaoInternacaoController.inicio();
		return REDIRECT_ATENDER_SOLICITACAO_INTERNACAO;
	}
	
	public String internarPaciente(Integer codigoPaciente, Integer seqSolicitacaoInternacao){
		this.cadastroInternacaoController.setAipPacCodigo(codigoPaciente);
		this.cadastroInternacaoController.setCameFrom(PAGE_PESQUISAR_SOLICITACAO_INTERNACAO);
		this.cadastroInternacaoController.setAinSolicitacaoInternacaoSeq(seqSolicitacaoInternacao);
		return REDIRECT_CADASTRO_INTERNACAO;
	}
	
	public String detalharSolicitacaoInternacao(Integer seqSolicitacao){		
		this.detalhaSolicitacaoInternacaoController.setSeqSolicitacao(seqSolicitacao);
		this.detalhaSolicitacaoInternacaoController.inicio();
		return REDIRECT_DETALHAR_SOLICITACAO_INTERNACAO;
	}
	
	// ### Paginação ###

	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		Long count = 0L;
		try {
			count = this.solicitacaoInternacaoFacade.pesquisarSolicitacaoInternacaoCount(this.indSolicitacaoInternacao, this.clinicaPesquisa, this.dataSolicitacao, this.dataPrevisao, this.pacientePesquisa, this.servidorConselhoVOPesquisa, this.especialidadePesquisa, this.convenioPlanoVOPesquisa);
		} catch (Exception e) {
			LOG.error("Erro ao recuperar count da lista paginada.", e);
		}
		return count;		
	}

	@Override
	public List<AinSolicitacoesInternacao> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<AinSolicitacoesInternacao> result = new ArrayList<AinSolicitacoesInternacao>();
		
		try {
			
		result = this.solicitacaoInternacaoFacade
				.pesquisarSolicitacaoInternacao(firstResult, maxResults,
						orderProperty, asc, this.indSolicitacaoInternacao,
						this.clinicaPesquisa, this.dataSolicitacao,
						this.dataPrevisao, this.pacientePesquisa,
						this.servidorConselhoVOPesquisa,
						this.especialidadePesquisa,
						this.convenioPlanoVOPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
		
		return result;
	}
	
	public String formatarTelefonePaciente(AipPacientes paciente){
		String retorno = "";
		if (paciente.getDddFoneResidencial() != null && paciente.getFoneResidencial() != null){
			retorno = paciente.getDddFoneResidencial() + "-" + paciente.getFoneResidencial();
		}
		return retorno;
	}
	
	public String formatarProcedimento(FatItensProcedHospitalar procedimento){
		String retorno = "";
		if (procedimento != null){
			retorno = procedimento.getCodTabela() + " - " + procedimento.getDescricao();
		}
		return retorno;
	}

	public void limpar() {
		reiniciarPaginator();
		this.clinicaPesquisa = null;
		this.servidorConselhoVOPesquisa = null;
		this.convenioPlanoVOPesquisa = null;
		this.especialidadePesquisa = null;
		this.pacientePesquisa = null;
		this.prontuario = null;
		this.nomePaciente = null;
		this.codigoPaciente = null;
		this.indSolicitacaoInternacao = null;
		this.dataSolicitacao = null;
		this.dataPrevisao = null;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	

	public Boolean exibirBotaoLiberar(
			AinSolicitacoesInternacao solicitacaoInternacao) {
		return solicitacaoInternacao != null
				&& DominioSituacaoSolicitacaoInternacao.P
						.equals(solicitacaoInternacao
								.getIndSitSolicInternacao());
	}

	public Boolean exibirBotaoCancelar(
			AinSolicitacoesInternacao solicitacaoInternacao) {
		return solicitacaoInternacao != null
				&& (DominioSituacaoSolicitacaoInternacao.P
						.equals(solicitacaoInternacao
								.getIndSitSolicInternacao()) || DominioSituacaoSolicitacaoInternacao.L
						.equals(solicitacaoInternacao
								.getIndSitSolicInternacao()));
	}

	public Boolean exibirBotaoAtender(
			AinSolicitacoesInternacao solicitacaoInternacao) {
		return solicitacaoInternacao != null
				&& DominioSituacaoSolicitacaoInternacao.L
						.equals(solicitacaoInternacao
								.getIndSitSolicInternacao());
	}

	public Boolean exibirBotaoInternar(
			AinSolicitacoesInternacao solicitacaoInternacao) {
		return solicitacaoInternacao != null
				&& DominioSituacaoSolicitacaoInternacao.A
						.equals(solicitacaoInternacao
								.getIndSitSolicInternacao());
	}
	
	public void obterSolicitacaoParaAlteracao(
			AinSolicitacoesInternacao solicitacaoInternacao){
		this.solicitacaoInternacaoAlteracao = solicitacaoInternacao;
		
	}
	
	public void obterSolicitacaoParaCancelamento(
			AinSolicitacoesInternacao solicitacaoInternacao){
		this.solicitacaoInternacaoAlteracao = solicitacaoInternacao;
		
	}
	
	public void cancelarSolicitacaoInternacao() {
		try {
			reiniciarPaginator();

			this.solicitacaoInternacaoAlteracao
					.setIndSitSolicInternacao(DominioSituacaoSolicitacaoInternacao.C);
			
			if(this.solicitacaoInternacaoAlteracao.getObservacao() != null){
				this.solicitacaoInternacaoAlteracao.setObservacao(
						this.solicitacaoInternacaoAlteracao.getObservacao().toUpperCase());
			}
			
			AinSolicitacoesInternacao solicitacaoInternacaoOriginal = solicitacaoInternacaoFacade
					.obterSolicitacaoInternacaoDetached(this.solicitacaoInternacaoAlteracao);
			solicitacaoInternacaoFacade.persistirSolicitacaoInternacao(
					this.solicitacaoInternacaoAlteracao, solicitacaoInternacaoOriginal, false);
			
			limparModalObs();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limparModalObs(){
		this.solicitacaoInternacaoAlteracao = null;
	}
	
	public void liberarSolicitacaoInternacao(AinSolicitacoesInternacao solicitacaoInternacao) {
		try {
			reiniciarPaginator();
			solicitacaoInternacao.setIndSitSolicInternacao(DominioSituacaoSolicitacaoInternacao.L);
			AinSolicitacoesInternacao solicitacaoInternacaoOriginal = solicitacaoInternacaoFacade.obterSolicitacaoInternacaoDetached(solicitacaoInternacao);
			solicitacaoInternacaoFacade.persistirSolicitacaoInternacao(	solicitacaoInternacao, solicitacaoInternacaoOriginal, false);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void processarBuscaPacientePorProntuario(){
		if(this.prontuario != null){
			tratarResultadoBuscaPaciente(pacienteFacade.obterPacientePorProntuario(this.prontuario));
		}
	}
	
	public void processarBuscaPacientePorCodigo(){
		if(this.codigoPaciente != null){
			tratarResultadoBuscaPaciente(pacienteFacade.buscaPaciente(codigoPaciente));
		}
	}	

	private void tratarResultadoBuscaPaciente(AipPacientes paciente) {		
		if(paciente != null){
			this.prontuario = (paciente.getProntuario());
			this.codigoPaciente = paciente.getCodigo();
			this.nomePaciente = paciente.getNome();
			this.pacientePesquisa = paciente;
		}else{
			this.prontuario = null;
			this.codigoPaciente = null;
			this.nomePaciente = null;
			this.pacientePesquisa = null;
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_PACIENTE_ENCONTRADO");		
		}
	}
	
	public List<AghClinicas> pesquisarClinicas(String paramPesquisa) {		
		return this.aghuFacade.pesquisarClinicasSolInternacao(paramPesquisa == null ? null
				: paramPesquisa);
	}

	public List<ServidorConselhoVO> pesquisarCRM(String paramPesquisa) {
		return this.solicitacaoInternacaoFacade.pesquisarServidorConselhoVOPorNomeeCRM(paramPesquisa == null ? null
				: paramPesquisa);
	}

	public List<ConvenioPlanoVO> pesquisarConvenio(String paramPesquisa) {
		return this.solicitacaoInternacaoFacade.pesquisarConvenioPlanoVOPorCodigoDescricao(paramPesquisa == null ? null
				: paramPesquisa);
	}

	public List<AghEspecialidades> pesquisarEspecialidades(String paramPesquisa) {
		return this.aghuFacade.listarEspecialidadePorSiglaENome(paramPesquisa == null ? null
				: paramPesquisa);
	}

	

	public AghClinicas getClinicaPesquisa() {
		return this.clinicaPesquisa;
	}

	public void setClinicaPesquisa(AghClinicas clinicaPesquisa) {
		this.clinicaPesquisa = clinicaPesquisa;
	}

	public DominioSituacaoSolicitacaoInternacao getIndSolicitacaoInternacao() {
		return this.indSolicitacaoInternacao;
	}

	public void setIndSolicitacaoInternacao(
			DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao) {
		this.indSolicitacaoInternacao = indSolicitacaoInternacao;
	}

	public Date getDataSolicitacao() {
		return this.dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Date getDataPrevisao() {
		return this.dataPrevisao;
	}

	public void setDataPrevisao(Date dataPrevisao) {
		this.dataPrevisao = dataPrevisao;
	}

	public ServidorConselhoVO getServidorConselhoVOPesquisa() {
		return this.servidorConselhoVOPesquisa;
	}

	public void setServidorConselhoVOPesquisa(
			ServidorConselhoVO servidorConselhoVOPesquisa) {
		this.servidorConselhoVOPesquisa = servidorConselhoVOPesquisa;
	}

	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AghEspecialidades getEspecialidadePesquisa() {
		return this.especialidadePesquisa;
	}

	public void setEspecialidadePesquisa(AghEspecialidades especialidadePesquisa) {
		this.especialidadePesquisa = especialidadePesquisa;
	}

	public ConvenioPlanoVO getConvenioPlanoVOPesquisa() {
		return this.convenioPlanoVOPesquisa;
	}

	public void setConvenioPlanoVOPesquisa(ConvenioPlanoVO convenioPlanoVOPesquisa) {
		this.convenioPlanoVOPesquisa = convenioPlanoVOPesquisa;
	}

	public Integer getCodigoPaciente() {
		return this.codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public AipPacientes getPacientePesquisa() {
		return this.pacientePesquisa;
	}

	public void setPacientePesquisa(AipPacientes pacientePesquisa) {
		this.pacientePesquisa = pacientePesquisa;
		this.codigoPaciente = null;
	}

	public AinSolicitacoesInternacao getSolicitacaoInternacaoAlteracao() {
		return solicitacaoInternacaoAlteracao;
	}

	public void setSolicitacaoInternacaoAlteracao(
			AinSolicitacoesInternacao solicitacaoInternacaoAlteracao) {
		this.solicitacaoInternacaoAlteracao = solicitacaoInternacaoAlteracao;
	}
	
	public DynamicDataModel<AipOcupacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipOcupacoes> dataModel) {
		this.dataModel = dataModel;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
}
