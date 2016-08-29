package br.gov.mec.aghu.exames.solicitacao.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.HistoricoNumeroUnicoVO;
import br.gov.mec.aghu.exames.vo.AelAmostraExamesVO;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.exames.vo.AelExtratoAmostrasVO;
import br.gov.mec.aghu.model.AelAmostraItemExamesId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class PesquisaAmostrasExamesController extends ActionController{

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(PesquisaAmostrasExamesController.class);

	private static final long serialVersionUID = -2545180079114305732L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IColetaExamesFacade coletaExamesFacade;

	
	private List<AelAmostrasVO> listaAmostrasExames;
	private AelAmostrasVO selecionado;
	
	private Integer soeSeq;//solicitação de exames
	private Short iseSeqp;
	
	private List<AelAmostraExamesVO> listaItensAmostra;
	private Map<AelAmostrasVO, List<AelAmostraExamesVO>> itensAmostra;
	
	private List<AelExtratoAmostrasVO> listaExtratoAmostras;
	private Map<AelAmostrasVO, List<AelExtratoAmostrasVO>> itensExtratoAmostra;
	
	private List<HistoricoNumeroUnicoVO> listaHistoricoNumeroUnicoVO;
	private Map<AelAmostrasVO, List<HistoricoNumeroUnicoVO>> itensHistoricoNumeroUnico;
	
	private String voltarPara;
	
	//Agendamento
	private Date hedDthrAgenda;
	private Short hedGaeUnfSeq;
	private Integer hedGaeSeqp;
	private Boolean desabilitaBotaoCancelarColeta;
	private Boolean desabilitaBotaoColeta;

	private Boolean isHist = Boolean.FALSE;
	private Boolean origemPOL = Boolean.FALSE;
	private Boolean origemSolicDetalhamentoAmostras = Boolean.FALSE;
	
	public void inicio() {
		limpar();
		if(this.soeSeq != null || hedGaeUnfSeq != null){
			this.pesquisarAmostrasExames();
		}
	}
	
	private void limpar() {
		this.selecionado = null;
		this.listaHistoricoNumeroUnicoVO = null;
		this.listaItensAmostra = new ArrayList<AelAmostraExamesVO>();
		this.listaExtratoAmostras = null;
		this.listaAmostrasExames = null;
		this.desabilitaBotaoCancelarColeta = true;
		this.desabilitaBotaoColeta = true;
	}
	
	public String voltar() {
		return voltarPara;
	}

	public void pesquisarAmostrasExames() {

		try {
			if(soeSeq != null) {
				this.listaAmostrasExames = this.examesFacade.listarAmostrasExamesVO(soeSeq, isHist);
			} else {
			//	Date hedDthrAgenda = new Date(this.hedDthrAgendaLongTime);
				this.listaAmostrasExames = this.examesFacade.listarAmostrasExamesVOPorAtendimento(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda, isHist);
			}
			
			itensAmostra 	    	  = new HashMap<AelAmostrasVO, List<AelAmostraExamesVO>>(listaAmostrasExames.size());
			itensExtratoAmostra 	  = new HashMap<AelAmostrasVO, List<AelExtratoAmostrasVO>>(listaAmostrasExames.size());
			itensHistoricoNumeroUnico = new HashMap<AelAmostrasVO, List<HistoricoNumeroUnicoVO>>(listaAmostrasExames.size());
			
		} catch (BaseException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		}
	}

	public void selecionarAmostra() {
		try {
			carregarItensAmostras();
			desabilitaBotaoCancelarColeta = false;
			desabilitaBotaoColeta = false;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void carregarItensAmostras() throws BaseException {
		
		if(itensAmostra.containsKey(selecionado)){
			listaItensAmostra = itensAmostra.get(selecionado);
			
		} else {
			List<AelAmostraExamesVO> lista = examesFacade.listarItensAmostra(selecionado.getSoeSeq(),selecionado.getSeqp(), selecionado.getSeqp().intValue(), isHist);
			itensAmostra.put(selecionado, lista);
			listaItensAmostra = lista;
		}
	}

	
	public void pesquisarExtratoAmostra(AelAmostrasVO amostraVO)  {
		if(itensExtratoAmostra.containsKey(amostraVO)){
			listaExtratoAmostras = itensExtratoAmostra.get(amostraVO);
			
		} else {
			listaExtratoAmostras = this.examesFacade.pesquisarAelExtratosAmostrasVOPorAmostra(amostraVO.getSoeSeq() , amostraVO.getSeqp(), isHist);
			itensExtratoAmostra.put(amostraVO, listaExtratoAmostras);
		}
	}

	public void pesquisarHistoricoNroUnico(AelAmostrasVO pAmostraVO) {
		if(itensHistoricoNumeroUnico.containsKey(pAmostraVO)){
			listaHistoricoNumeroUnicoVO = itensHistoricoNumeroUnico.get(pAmostraVO);
			
		} else {
			listaHistoricoNumeroUnicoVO = solicitacaoExameFacade.listarHistoricoNroUnico(pAmostraVO.getSoeSeq(), pAmostraVO.getSeqp(), isHist);
			itensHistoricoNumeroUnico.put(pAmostraVO, listaHistoricoNumeroUnicoVO);
		}
		openDialog("modalHistoricoNroUnicoWG");
	}
	
	public void confirmarColeta() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			
			Boolean coletou = false;
			this.coletaExamesFacade.validarColetaExame(listaItensAmostra);
			for(AelAmostraExamesVO amostraExames : this.listaItensAmostra) {
				if(amostraExames.getSelecionado()) {
					AelAmostraItemExamesId amostraItemExamesId = new AelAmostraItemExamesId(amostraExames.getIseSoeSeq(), 
							amostraExames.getIseSeqp(), amostraExames.getAmoSoeSeq(), amostraExames.getAmoSeqp());
					this.coletaExamesFacade.coletarExame(amostraItemExamesId, nomeMicrocomputador);
					coletou = true;
				}
			}
			// Atualização das Informações dos Exames das Amostras
			if(coletou) {
				this.pesquisarAmostrasExames();
				this.carregarItensAmostras();
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ITEM_COLETADO_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,"ERROR_SELECIONE_EXAME_PARA_COLETA");
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}	
	}
	
	public void voltarColeta() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			
			Boolean coletou = false;
			this.coletaExamesFacade.validarVoltaExame(listaItensAmostra);
			for(AelAmostraExamesVO amostraExames : this.listaItensAmostra) {
				if(amostraExames.getSelecionado()) {
					AelAmostraItemExamesId amostraItemExamesId = new AelAmostraItemExamesId(amostraExames.getIseSoeSeq(), 
							amostraExames.getIseSeqp(), amostraExames.getAmoSoeSeq(), amostraExames.getAmoSeqp());
					this.coletaExamesFacade.voltarExame(amostraItemExamesId, nomeMicrocomputador);
					coletou = true;
				}
			}
			if(coletou) {
				this.pesquisarAmostrasExames();
				this.carregarItensAmostras();
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ITEM_VOLTADO_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,"ERROR_SELECIONE_EXAME_PARA_VOLTAR_COLETA");
			}
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean desabilitarBotaoColeta(){
		
		return this.desabilitaBotaoColeta 
				|| this.origemPOL
				|| this.origemSolicDetalhamentoAmostras;
	}
	
	public Boolean desabilitarBotaoCancelarColeta(){
		
		return this.desabilitaBotaoCancelarColeta 
				|| this.origemPOL
				|| this.origemSolicDetalhamentoAmostras
				|| verificarSitucaoAmostraSelecionada();
	}
	

	private boolean verificarSitucaoAmostraSelecionada() {
		// Se situação da amostra for igual a CANCELADA, desabilita botão de "Voltar Exames"  
		return this.selecionado.getSituacao().equals(DominioSituacaoAmostra.A);
	}

	public List<AelExtratoAmostrasVO> getListaExtratoAmostras() {
		return listaExtratoAmostras;
	}

	public void setListaExtratoAmostras(List<AelExtratoAmostrasVO> listaExtratoAmostras) {
		this.listaExtratoAmostras = listaExtratoAmostras;
	}

	public List<AelAmostrasVO> getListaAmostrasExames() {
		return listaAmostrasExames;
	}

	public void setListaAmostrasExames(List<AelAmostrasVO> listaAmostrasExames) {
		this.listaAmostrasExames = listaAmostrasExames;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public List<AelAmostraExamesVO> getListaItensAmostra() {
		return listaItensAmostra;
	}

	public void setListaItensAmostra(List<AelAmostraExamesVO> listaItensAmostra) {
		this.listaItensAmostra = listaItensAmostra;
	}

	public List<HistoricoNumeroUnicoVO> getListaHistoricoNumeroUnicoVO() {
		return listaHistoricoNumeroUnicoVO;
	}

	public void setListaHistoricoNumeroUnicoVO(
			List<HistoricoNumeroUnicoVO> listaHistoricoNumeroUnicoVO) {
		this.listaHistoricoNumeroUnicoVO = listaHistoricoNumeroUnicoVO;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getHedGaeUnfSeq() {
		return hedGaeUnfSeq;
	}

	public void setHedGaeUnfSeq(Short hedGaeUnfSeq) {
		this.hedGaeUnfSeq = hedGaeUnfSeq;
	}

	public Integer getHedGaeSeqp() {
		return hedGaeSeqp;
	}

	public void setHedGaeSeqp(Integer hedGaeSeqp) {
		this.hedGaeSeqp = hedGaeSeqp;
	}

	public Boolean getDesabilitaBotaoCancelarColeta() {
		return desabilitaBotaoCancelarColeta;
	}

	public void setDesabilitaBotaoCancelarColeta(
			Boolean desabilitaBotaoCancelarColeta) {
		this.desabilitaBotaoCancelarColeta = desabilitaBotaoCancelarColeta;
	}

	public Boolean getDesabilitaBotaoColeta() {
		return desabilitaBotaoColeta;
	}

	public void setDesabilitaBotaoColeta(Boolean desabilitaBotaoColeta) {
		this.desabilitaBotaoColeta = desabilitaBotaoColeta;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}

	public Boolean getOrigemPOL() {
		return origemPOL;
	}

	public void setOrigemPOL(Boolean origemPOL) {
		this.origemPOL = origemPOL;
	}

	public Date getHedDthrAgenda() {
		return hedDthrAgenda;
	}

	public void setHedDthrAgenda(Date hedDthrAgenda) {
		this.hedDthrAgenda = hedDthrAgenda;
	}

	public AelAmostrasVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelAmostrasVO selecionado) {
		this.selecionado = selecionado;
	}

	public Map<AelAmostrasVO, List<AelAmostraExamesVO>> getItensAmostra() {
		return itensAmostra;
	}

	public void setItensAmostra(
			Map<AelAmostrasVO, List<AelAmostraExamesVO>> itensAmostra) {
		this.itensAmostra = itensAmostra;
	}

	public Map<AelAmostrasVO, List<AelExtratoAmostrasVO>> getItensExtratoAmostra() {
		return itensExtratoAmostra;
	}

	public void setItensExtratoAmostra(
			Map<AelAmostrasVO, List<AelExtratoAmostrasVO>> itensExtratoAmostra) {
		this.itensExtratoAmostra = itensExtratoAmostra;
	}

	public Map<AelAmostrasVO, List<HistoricoNumeroUnicoVO>> getItensHistoricoNumeroUnico() {
		return itensHistoricoNumeroUnico;
	}

	public void setItensHistoricoNumeroUnico(
			Map<AelAmostrasVO, List<HistoricoNumeroUnicoVO>> itensHistoricoNumeroUnico) {
		this.itensHistoricoNumeroUnico = itensHistoricoNumeroUnico;
	}

	public Boolean getOrigemSolicDetalhamentoAmostras() {
		return origemSolicDetalhamentoAmostras;
	}

	public void setOrigemSolicDetalhamentoAmostras(
			Boolean origemSolicDetalhamentoAmostras) {
		this.origemSolicDetalhamentoAmostras = origemSolicDetalhamentoAmostras;
	}
}