package br.gov.mec.aghu.exames.pesquisa.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

public class CancelarExameController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 671028798783833493L;
	
	private static final Log LOG = LogFactory.getLog(CancelarExameController.class);

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
	private Short ufeUnfSeq;
	private String voltarPara;
	private Short iseSeq;
	private boolean showSuggestion = false;

	private PesquisaExamesPacientesResultsVO solicPaciente = new PesquisaExamesPacientesResultsVO();
	private AelMotivoCancelaExames motivoCancelar = null;
	private AelItemSolicitacaoExames itemsolcitacao = null;
	
	private PesquisaExamesPacientesResultsVO pesquisaExameSelecionado;
	private AelAmostraItemExames itemAmostraSelecionado;
	

	/* fitro da tela de pesquisa */
	private PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();
	private List<PesquisaExamesPacientesResultsVO> itensSolicitacao = new ArrayList<PesquisaExamesPacientesResultsVO>();
	private boolean habilitaBotao = false;

	public void iniciar() {
	 

		try {
			// Busca dados da solicitação
			setSolicPaciente(pesquisaExamesFacade.buscaDadosSolicitacaoPorSoeSeq(this.soeSeq));
			// Busca dados dos itens da solicitação
			setItensSolicitacao(pesquisaExamesFacade.buscaDadosItensSolicitacaoPorSoeSeq(this.soeSeq, this.ufeUnfSeq));
			// Pega o primeiro registro
			if (getItensSolicitacao() != null && getItensSolicitacao().size() > 0) {
				setIseSeq(getItensSolicitacao().get(0).getIseSeq());
			} else {
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

	public void renderSuggestion(Short iseSoe) {
		setItemsolcitacao(examesFacade.buscaItemSolicitacaoExamePorId(this.soeSeq, iseSoe));
		setIseSeq(iseSoe);
		setShowSuggestion(true);
	}

	public void estornarItemExame(Short iseSoe) {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			setItemsolcitacao(examesFacade.buscaItemSolicitacaoExamePorId(this.soeSeq, iseSoe));
			examesBeanFacade.estornarItemSolicitacaoExame(itemsolcitacao, nomeMicrocomputador);
			setHabilitaBotao(false);

			setItensSolicitacao(pesquisaExamesFacade.buscaDadosItensSolicitacaoPorSoeSeq(this.soeSeq, this.ufeUnfSeq));

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ITEM_ESTORNADO");

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public String gravar() {

		try {
			AelItemSolicitacaoExames itemSolicitacaoCancelar = this.examesFacade.buscaItemSolicitacaoExamePorId(itemsolcitacao.getId().getSoeSeq(), itemsolcitacao.getId().getSeqp());

			itemSolicitacaoCancelar.setAelMotivoCancelaExames(motivoCancelar);

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			this.examesBeanFacade.cancelarExames(itemSolicitacaoCancelar, this.motivoCancelar, nomeMicrocomputador);

			setItensSolicitacao(pesquisaExamesFacade.buscaDadosItensSolicitacaoPorSoeSeq(this.soeSeq, this.ufeUnfSeq));

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ITEM_CANCELADO", itemSolicitacaoCancelar.getId().getSeqp());

			setMotivoCancelar(null);
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
		String retorno = this.voltarPara;

		this.soeSeq = null;
		this.amoSeqp = null;
		this.ufeUnfSeq = null;
		this.voltarPara = null;
		this.iseSeq = null;
		this.showSuggestion = false;

		this.solicPaciente = new PesquisaExamesPacientesResultsVO();
		this.motivoCancelar = null;
		this.itemsolcitacao = null;

		this.filtro = new PesquisaExamesFiltroVO();
		this.itensSolicitacao = new ArrayList<PesquisaExamesPacientesResultsVO>();
		this.habilitaBotao = false;

		return retorno;
	}

	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExames(String param) {
		return this.returnSGWithCount(pesquisaExamesFacade.pesquisarMotivoCancelaExames(param), pesquisarMotivoCancelaExamesCount(param));
	}

	public Long pesquisarMotivoCancelaExamesCount(String param) {
		return pesquisaExamesFacade.pesquisarMotivoCancelaExamesCount(param);
	}

	public List<AelAmostraItemExames> getAmostrasItemExame() {
		List<AelAmostraItemExames> listaItens = null;
		try {
			listaItens = this.solicitacaoExameFacade.buscarAmostrasItemExame(getSoeSeq(), getIseSeq());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return listaItens;
	}

	public void habilitaBotaoGravar() {
		setHabilitaBotao(true);
	}

	public void desabilitaBotaoGravar() {
		setHabilitaBotao(false);
	}

	public void selecionarItem() {
		if(pesquisaExameSelecionado != null){
			this.iseSeq = pesquisaExameSelecionado.getIseSeq();
		}
	}

	public boolean isItemSelecionado(Short iseSeq) {
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

	public void setItensSolicitacao(List<PesquisaExamesPacientesResultsVO> itensSolicitacao) {
		this.itensSolicitacao = itensSolicitacao;
	}

	public Short getIseSeq() {
		return iseSeq;
	}

	public void setIseSeq(Short iseSeq) {
		this.iseSeq = iseSeq;
	}

	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
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

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getAmoSeqp() {
		return amoSeqp;
	}

	public void setAmoSeqp(Short amoSeqp) {
		this.amoSeqp = amoSeqp;
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