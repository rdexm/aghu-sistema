package br.gov.mec.aghu.internacao.leitos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoTransferencia;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.transferir.action.TransferirPacienteController;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.internacao.vo.SolicitacaoTransferenciaPacienteVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class AtenderTransferenciaPacientePaginatorController extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8499405699265538844L;

	private final String PAGE_VERIFICAR_CARACTERISTICAS_PEDIDO_INTERNACAO = "internacao-verificarCaracteristicasPedidoInternacao";
	private final String PAGE_ATENDER_TRANSFERENCIA_PACIENTE_CRUD = "internacao-atenderTransferenciaPacienteCRUD";
	private final String PAGE_TRANSFERIR_PACIENTE_CRUD = "internacao-transferirPacienteCRUD";
	private final String ATENDER_TRANSFERENCIA_PACIENTE_LIST = "atenderTransferenciaPacienteList";

	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;
	
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private TransferirPacienteController transferirPacienteController;
	
	private Integer prontuario;
	
	private DominioSituacaoSolicitacaoTransferencia situacao = DominioSituacaoSolicitacaoTransferencia.P;
	
	private Date dataSolicitacao;
	
	private ServidoresCRMVO servidorCRMVO;
	
	private AghEspecialidades especialidade;
	
	private AghUnidadesFuncionais unidadeFuncional;
	
	private AinSolicTransfPacientes solicitacao;
	
	private Integer solicitacaoSeq;
	
	@Inject @Paginator
	private DynamicDataModel<SolicitacaoTransferenciaPacienteVO> dataModel;	
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		this.setProntuario(null);
		this.dataSolicitacao = null;
		this.situacao = DominioSituacaoSolicitacaoTransferencia.P;
		this.servidorCRMVO = null;
		this.especialidade = null;
		this.unidadeFuncional = null;
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		Long count = this.leitosInternacaoFacade.
			pesquisarSolicitacaoTransferenciaPacienteCount(this.situacao, this.dataSolicitacao, this.prontuario, this.unidadeFuncional , this.servidorCRMVO, this.especialidade);
		return count;
	}

	@Override
	public List<SolicitacaoTransferenciaPacienteVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		List<AinSolicTransfPacientes> resultList = this.leitosInternacaoFacade
				.pesquisarSolicitacaoTransferenciaPaciente(firstResult, maxResult, orderProperty,
						asc, this.situacao, this.dataSolicitacao, this.prontuario, this.unidadeFuncional , this.servidorCRMVO, this.especialidade);

		List<SolicitacaoTransferenciaPacienteVO> result = new ArrayList<SolicitacaoTransferenciaPacienteVO>();
		
		for (AinSolicTransfPacientes solicTransfPac : resultList) {
			SolicitacaoTransferenciaPacienteVO solicitacao = new SolicitacaoTransferenciaPacienteVO();
			solicitacao.setSolicitacao(solicTransfPac);

			if (solicTransfPac.getUnfSolicitante() != null) {
				solicitacao.setDescricaoUnidadeFuncional(solicTransfPac
						.getUnfSolicitante().getLPADAndarAlaDescricao());
			} else {
				solicitacao
						.setDescricaoUnidadeFuncional(this.leitosInternacaoFacade
								.recuperaUnidadeFuncionalInternacao(solicTransfPac
										.getInternacao().getSeq()));
			}

			Boolean aplicaEstilo= this.leitosInternacaoFacade
										.verificaSolicitacoesComAlerta(solicTransfPac);
			if(aplicaEstilo){
				solicitacao.setEstilo("color:red;");
			}
			result.add(solicitacao);			
		}

		return result;
	}
	
	public void cancelarSolicitacao(){
		dataModel.reiniciarPaginator(); 
		
		this.solicitacao = this.leitosInternacaoFacade.obterSolicitacaoPorSeq(this.solicitacaoSeq);
		
		try {		
			if (this.solicitacao != null && this.solicitacao.getIndSitSolicLeito() == DominioSituacaoSolicitacaoInternacao.P){
				this.leitosInternacaoFacade.cancelarSolicitacao(this.solicitacao);
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CANCELAR_SOLICITACAO",
						this.solicitacao.getSeq());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_CANCELAR_SOLICITACAO");
			} 
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public List<ServidoresCRMVO> pesquisarCRM(String paramPesquisa) {
		return this.solicitacaoInternacaoFacade.pesquisarServidorCRMVOPorNomeeCRM(paramPesquisa == null ? null
				: paramPesquisa);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidades(String paramPesquisa) {
		return this.aghuFacade.listarEspecialidadesPorSiglaOuDescricao(paramPesquisa == null ? null
				: paramPesquisa, false);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			String paramPesquisa) {
		return this.cadastrosBasicosInternacaoFacade
				.pesquisarUnidadeFuncionalPorCodigoPorAndarAlaDescricaoAtivasInativas(paramPesquisa == null ? null
						: paramPesquisa);
	}
	
	public String getProntuarioFormatado(AipPacientes paciente){
		String prontuario = paciente.getProntuario().toString();
		prontuario = prontuario.substring(0, prontuario.length() - 1) + "/"
				+ prontuario.charAt(prontuario.length() - 1);
		return StringUtils.leftPad(prontuario, 9, '0');
	}
	
	public String detalhar(){
		return PAGE_VERIFICAR_CARACTERISTICAS_PEDIDO_INTERNACAO;
	}	
	
	public String atenderVisualizar(){
		return PAGE_ATENDER_TRANSFERENCIA_PACIENTE_CRUD;
	}
	
	public String transferir(Integer internacaoSeq, String leitoID) {
		transferirPacienteController.setInternacaoSeq(internacaoSeq);
		transferirPacienteController.setLeitoConcedido(leitoID);
		transferirPacienteController.setCameFrom(ATENDER_TRANSFERENCIA_PACIENTE_LIST);
		transferirPacienteController.setValidar(true);
		transferirPacienteController.inicio();
		return PAGE_TRANSFERIR_PACIENTE_CRUD;
	}	
	
	//GETTERS E SETTERS
	public Date getDataSolicitacao() {
		return this.dataSolicitacao;
	}

	public DominioSituacaoSolicitacaoTransferencia getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoSolicitacaoTransferencia situacao) {
		this.situacao = situacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public ServidoresCRMVO getServidorCRMVO() {
		return this.servidorCRMVO;
	}

	public void setServidorCRMVO(ServidoresCRMVO servidorCRMVO) {
		this.servidorCRMVO = servidorCRMVO;
	}

	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setSolicitacao(AinSolicTransfPacientes solicitacao) {
		this.solicitacao = solicitacao;
	}

	public AinSolicTransfPacientes getSolicitacao() {
		return this.solicitacao;
	}

	public void setSolicitacaoSeq(Integer solicitacaoSeq) {
		this.solicitacaoSeq = solicitacaoSeq;
	}

	public Integer getSolicitacaoSeq() {
		return this.solicitacaoSeq;
	}

	public DynamicDataModel<SolicitacaoTransferenciaPacienteVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<SolicitacaoTransferenciaPacienteVO> dataModel) {
		this.dataModel = dataModel;
	}

	public TransferirPacienteController getTransferirPacienteController() {
		return transferirPacienteController;
	}

	public void setTransferirPacienteController(TransferirPacienteController transferirPacienteController) {
		this.transferirPacienteController = transferirPacienteController;
	}
	
}
