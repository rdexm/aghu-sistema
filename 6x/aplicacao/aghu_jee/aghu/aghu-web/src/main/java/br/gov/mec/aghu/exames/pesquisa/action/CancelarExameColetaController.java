package br.gov.mec.aghu.exames.pesquisa.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class CancelarExameColetaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(CancelarExameColetaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3729547145830110603L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private IExamesBeanFacade examesBeanFacade;


	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	private Integer soeSeq;
	private Short amoSeqp;
	private Short iseSeq;
	private boolean showSuggestion = false;
	
	private PesquisaExamesPacientesResultsVO solicPaciente = new PesquisaExamesPacientesResultsVO();
	private AelMotivoCancelaExames motivoCancelar = null;
	private AelItemSolicitacaoExames itemsolcitacao = null;
	
	/*	fitro da tela de pesquisa	*/
	private PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();
	private List<PesquisaExamesPacientesResultsVO> itensSolicitacao = new ArrayList<PesquisaExamesPacientesResultsVO>();
	private boolean habilitaBotao = false;
	private boolean habilitaCCMotivo = false;
	private String motivoComplementoMotivo;

	private PesquisaExamesPacientesResultsVO pesquisaExameSelecionado;
	private AelAmostraItemExames itemAmostraSelecionado;
	
	public void inicio() {
	 

		try {
			//Busca dados da solicitação
			setSolicPaciente(pesquisaExamesFacade.buscaDadosSolicitacaoPorSoeSeq(this.soeSeq));
			//Busca dados dos itens da solicitação
			setItensSolicitacao(pesquisaExamesFacade.buscaDadosItensSolicitacaoCancelarColetaPorSoeSeq(this.soeSeq, null));
			//Pega o primeiro registro
			if(getItensSolicitacao()!=null && getItensSolicitacao().size()>0){
				setIseSeq(getItensSolicitacao().get(0).getIseSeq());
			}else{
				setIseSeq(null);
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}

	// Metódo para Suggestion Box Unidade executora
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(Object param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(param);
	}
	public void renderSuggestion(Short iseSoe){
		setItemsolcitacao(examesFacade.buscaItemSolicitacaoExamePorId(this.soeSeq, iseSoe));
		setIseSeq(iseSoe);
		setShowSuggestion(true);
	}
	
	public void estornarItemExame(Short iseSoe){
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try{
			setItemsolcitacao(examesFacade.buscaItemSolicitacaoExamePorId(this.soeSeq, iseSoe));
			examesBeanFacade.estornarItemSolicitacaoExame(itemsolcitacao, nomeMicrocomputador);
			setHabilitaBotao(false);
			
			setItensSolicitacao(pesquisaExamesFacade.buscaDadosItensSolicitacaoCancelarColetaPorSoeSeq(this.soeSeq, null));

			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ITEM_ESTORNADO");
			
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
				
	}

	public String gravar() {
		
		try {
			AelItemSolicitacaoExames itemSolicitacaoCancelar = this.examesFacade.buscaItemSolicitacaoExamePorId(itemsolcitacao.getId().getSoeSeq(), itemsolcitacao.getId().getSeqp());
			
			itemSolicitacaoCancelar.setAelMotivoCancelaExames(motivoCancelar);
			if(motivoCancelar!=null && motivoCancelar.getIndPermiteComplemento().equals(DominioSimNao.S)){
				itemSolicitacaoCancelar.setComplementoMotCanc(this.motivoComplementoMotivo);
			}else{
				itemSolicitacaoCancelar.setComplementoMotCanc(null);
			}
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			examesBeanFacade.cancelarExames(itemSolicitacaoCancelar, motivoCancelar, nomeMicrocomputador);

			setItensSolicitacao(pesquisaExamesFacade.buscaDadosItensSolicitacaoCancelarColetaPorSoeSeq(this.soeSeq, null));

			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ITEM_CANCELADO", itemSolicitacaoCancelar.getId().getSeqp());

			setMotivoCancelar(null);
			setMotivoComplementoMotivo(null);
			setShowSuggestion(false);
			setHabilitaBotao(false);
		
		} catch (BaseException e) {
	
			apresentarExcecaoNegocio(e);
		
		}
		
		return null;
	}
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		setIseSeq(null);
		desabilitaBotaoGravar();
		setShowSuggestion(false);
		return "exames-pesquisaCancelarExamesColeta";
	}
	
	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExames(String param) {
		return pesquisaExamesFacade.pesquisarMotivoCancelaExamesColeta(param);
	}

	public  List<AelAmostraItemExames> getAmostrasItemExame(){
		List<AelAmostraItemExames> listaItens = null;
		try {
			listaItens = this.solicitacaoExameFacade.buscarAmostrasItemExame(getSoeSeq(), getIseSeq());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return listaItens;
	}
	
	public void habilitaBotaoGravar(){
		setHabilitaBotao(true);

		if(motivoCancelar!=null && motivoCancelar.getIndPermiteComplemento().equals(DominioSimNao.S)){
			setHabilitaCCMotivo(true);
		}
	}

	public void desabilitaBotaoGravar(){
		setHabilitaBotao(false);
		setHabilitaCCMotivo(false);
		setMotivoComplementoMotivo(null);
	}
	
	public void selecionarItem(Short iseSeq) {
		this.iseSeq = iseSeq;
	}
	
	public boolean isItemSelecionado(Short iseSeq){
		return (this.iseSeq != null && this.iseSeq.equals(iseSeq));
	}

	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public PesquisaExamesPacientesResultsVO getSolicPaciente() {
		return solicPaciente;
	}

	public void setSolicPaciente(PesquisaExamesPacientesResultsVO solicPaciente) {
		this.solicPaciente = solicPaciente;
	}

	public List<PesquisaExamesPacientesResultsVO> getItensSolicitacao() {
		return itensSolicitacao;
	}

	public void setItensSolicitacao(
			List<PesquisaExamesPacientesResultsVO> itensSolicitacao) {
		this.itensSolicitacao = itensSolicitacao;
	}

	public Short getIseSeq() {
		return iseSeq;
	}

	public void setIseSeq(Short iseSeq) {
		this.iseSeq = iseSeq;
	}

	public AelMotivoCancelaExames getMotivoCancelar() {
		return motivoCancelar;
	}

	public void setMotivoCancelar(AelMotivoCancelaExames motivoCancelar) {
		this.motivoCancelar = motivoCancelar;
	}

	public boolean isShowSuggestion() {
		return showSuggestion;
	}

	public void setShowSuggestion(boolean showSuggestion) {
		this.showSuggestion = showSuggestion;
	}

	public AelItemSolicitacaoExames getItemsolcitacao() {
		return itemsolcitacao;
	}

	public void setItemsolcitacao(AelItemSolicitacaoExames itemsolcitacao) {
		this.itemsolcitacao = itemsolcitacao;
	}

	public boolean isHabilitaBotao() {
		return habilitaBotao;
	}

	public void setHabilitaBotao(boolean habilitaBotao) {
		this.habilitaBotao = habilitaBotao;
	}
	
	public Short getAmoSeqp() {
		return amoSeqp;
	}
	
	public void setAmoSeqp(Short amoSeqp) {
		this.amoSeqp = amoSeqp;
	}

	public boolean isHabilitaCCMotivo() {
		return habilitaCCMotivo;
	}

	public void setHabilitaCCMotivo(boolean habilitaCCMotivo) {
		this.habilitaCCMotivo = habilitaCCMotivo;
	}

	public String getMotivoComplementoMotivo() {
		return motivoComplementoMotivo;
	}

	public void setMotivoComplementoMotivo(String motivoComplementoMotivo) {
		this.motivoComplementoMotivo = motivoComplementoMotivo;
	}

	public PesquisaExamesPacientesResultsVO getPesquisaExameSelecionado() {
		return pesquisaExameSelecionado;
	}

	public void setPesquisaExameSelecionado(
			PesquisaExamesPacientesResultsVO pesquisaExameSelecionado) {
		this.pesquisaExameSelecionado = pesquisaExameSelecionado;
	}

	public AelAmostraItemExames getItemAmostraSelecionado() {
		return itemAmostraSelecionado;
	}

	public void setItemAmostraSelecionado(
			AelAmostraItemExames itemAmostraSelecionado) {
		this.itemAmostraSelecionado = itemAmostraSelecionado;
	}
}