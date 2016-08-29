package br.gov.mec.aghu.exames.pesquisa.action;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameReenvioGrupoVO;
import br.gov.mec.aghu.model.AelExameInternetStatus;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaLogExameInternetPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 4375862282112364375L;
	
	private static final Log LOG = LogFactory.getLog(PesquisaLogExameInternetPaginatorController.class);

	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	private DominioSituacaoExameInternet situacao;
	private DominioStatusExameInternet status;
	private Date dataHoraInicial;
	private Date dataHoraFinal;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private String localizador;	
	private RapConselhosProfissionais conselhoPesquisaGraduacao;	
	private String nroRegConselho;	
	private Long cnpjContratante;
	

	@Inject @Paginator
	private DynamicDataModel<AelExameInternetStatus> dataModel;
		
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation, true);
	}

	
	public void pesquisar() {
		if (this.dataHoraInicial.after(this.dataHoraFinal)) {
			this.apresentarMsgNegocio(Severity.ERROR, "DATA_INICIAL_MAIOR_DATA_FINAL");

		}else{
			dataModel.reiniciarPaginator();
		}
	}
	
	
	public void limpar() {
		dataModel.limparPesquisa();
		dataHoraInicial = null;
		dataHoraFinal = null;
		iseSoeSeq = null;
		iseSeqp = null;
		localizador = null;
		conselhoPesquisaGraduacao = null;	
		nroRegConselho = null;	
		cnpjContratante = null;		
	}
		
	@Override
	public Long recuperarCount() {		
		try {
			return this.examesFacade.listarExameInternetStatusCount(
					dataHoraInicial, dataHoraFinal, situacao,
					status, iseSoeSeq, iseSeqp, localizador,
					conselhoPesquisaGraduacao != null ? conselhoPesquisaGraduacao.getSigla() : null, 
					nroRegConselho, cnpjContratante);
			
		} catch (ParseException e) {
			return null;
		}
	}

	@Override
	public List<AelExameInternetStatus> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return this.examesFacade.listarExameInternetStatus( firstResult, maxResult, AelExameInternetStatus.Fields.SEQ.toString(), true,
																dataHoraInicial, dataHoraFinal, situacao, status, iseSoeSeq, iseSeqp, localizador,
																conselhoPesquisaGraduacao != null ? conselhoPesquisaGraduacao.getSigla() : null, 
																nroRegConselho, cnpjContratante);
		} catch (ParseException e) {
			return null;
		}
	}


	public String obterSolicitante(AelExameInternetStatus exameInternetStatus) {
		if(exameInternetStatus != null ) {
			return this.solicitacaoExameFacade.obterSolicitanteExame(exameInternetStatus);
		} else {
			return null;
		}
	}
	
	public void reenviarFila(AelExameInternetStatus exameInternetStatus){
		MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO = new MensagemSolicitacaoExameReenvioGrupoVO();
		mensagemSolicitacaoExameGrupoVO.setSeqSolicitacaoExame(exameInternetStatus.getSolicitacaoExames().getSeq());
		mensagemSolicitacaoExameGrupoVO.setSeqExameInternetGrupo(exameInternetStatus.getExameInternetGrupo().getSeq());
		
		try {
			solicitacaoExameFacade.reenviarExameParaPortal(mensagemSolicitacaoExameGrupoVO);
		} catch (Exception e) {
			LOG.error("ERRO_REENVIO_EXAME_FILA",e);
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_REENVIO_EXAME_FILA");			
		}
			this.apresentarMsgNegocio(Severity.INFO,"SUCESSO_REENVIO_EXAME_FILA");
	}

	
	public boolean habilitarReenvio(AelExameInternetStatus exameInternetStatus){
		if(DominioStatusExameInternet.NO.equals(exameInternetStatus.getStatus())){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Pesquisa conselhos para a suggestion box.
	 */
	public List<RapConselhosProfissionais> pesquisarConselhosPorDescricao(String descricao) {
		return cadastrosBasicosFacade.pesquisarConselhosPorDescricao((String) descricao);
	}
	
	public DominioSituacaoExameInternet getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoExameInternet situacao) {
		this.situacao = situacao;
	}

	public DominioStatusExameInternet getStatus() {
		return status;
	}

	public void setStatus(DominioStatusExameInternet status) {
		this.status = status;
	}

	public Date getDataHoraInicial() {
		return dataHoraInicial;
	}

	public void setDataHoraInicial(Date dataHoraInicial) {
		this.dataHoraInicial = dataHoraInicial;
	}

	public Date getDataHoraFinal() {
		return dataHoraFinal;
	}

	public void setDataHoraFinal(Date dataHoraFinal) {
		this.dataHoraFinal = dataHoraFinal;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public String getLocalizador() {
		return localizador;
	}

	public void setLocalizador(String localizador) {
		this.localizador = localizador;
	}


	public RapConselhosProfissionais getConselhoPesquisaGraduacao() {
		return conselhoPesquisaGraduacao;
	}


	public void setConselhoPesquisaGraduacao(
			RapConselhosProfissionais conselhoPesquisaGraduacao) {
		this.conselhoPesquisaGraduacao = conselhoPesquisaGraduacao;
	}


	public String getNroRegConselho() {
		return nroRegConselho;
	}


	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}


	public Long getCnpjContratante() {
		return cnpjContratante;
	}


	public void setCnpjContratante(Long cnpjContratante) {
		this.cnpjContratante = cnpjContratante;
	}
	
	 


	public DynamicDataModel<AelExameInternetStatus> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelExameInternetStatus> dataModel) {
	 this.dataModel = dataModel;
	}
}
