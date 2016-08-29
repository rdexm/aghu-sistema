package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.AelAmostraItemExamesVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CargaExamesInterfaceamentoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private static final Log LOG = LogFactory.getLog(CargaExamesInterfaceamentoPaginatorController.class);

	@Inject @Paginator
	private DynamicDataModel<AelAmostraItemExamesVO> dataModel;

	private static final long serialVersionUID = 8342672938576375975L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController;

	// campos de pesquisa
	private Integer solicitacao;// solicitacao
	private Integer amostra;// amostra
	private AelEquipamentos equipamento;// equipamentos
	private String exameSigla;// exame
	private DominioSimNao enviado; // enviado

	private List<AelAmostraItemExamesVO> listAmostraItemExamesVO = new LinkedList<AelAmostraItemExamesVO>();
	private Set<AelAmostraItemExamesVO> listAmostraItemExamesSelecionadosVO = new LinkedHashSet<AelAmostraItemExamesVO>();

	private AelAmostraItemExames amostraItemExames = new AelAmostraItemExames();
	private Boolean exibirModalConfirmaProgramacao = Boolean.FALSE;
	private Boolean exibirLabelConfirmaProgramacaoAmostra = Boolean.FALSE;
	private Boolean selecionarTodos = Boolean.FALSE;
	private Boolean allChecked = Boolean.FALSE;

	private String voltarPara;
	private Short unfSeq;

	/**
	 * Metodo que realiza a acao de <br>
	 * pesquisar na tela.
	 */
	public void pesquisar() {
		if (this.unfSeq != null) {
			// Quando a unidade é informada via parâmetro
			this.unidadeExecutoraController.setUnidadeExecutora(this.aghuFacade.obterUnidadeFuncional(this.unfSeq));
		}
		this.desfazerSelecao();
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Chamado no inicio da conversação
	 */
	public void iniciar() {
	 

		if (this.solicitacao != null) {
			this.pesquisar();
		}
	
	}

	/**
	 * Metodo que limpa os campos <br>
	 * de pesquisa na tela.
	 */
	public void limparPesquisa() {
		this.solicitacao = null;
		this.amostra = null;
		this.equipamento = null;
		this.exameSigla = null;
		this.enviado = null;
		this.unidadeExecutoraController.setUnidadeExecutora(null);
		this.amostraItemExames = new AelAmostraItemExames();
		this.exibirModalConfirmaProgramacao = Boolean.FALSE;
		this.selecionarTodos = Boolean.FALSE;
		this.listAmostraItemExamesSelecionadosVO = new LinkedHashSet<AelAmostraItemExamesVO>();
		this.dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return this.examesFacade
				.pesquisarAmostraItemExamesCount(this.unidadeExecutoraController.getUnidadeExecutora(), this.solicitacao, this.amostra, this.equipamento, this.exameSigla, this.enviado);
	}

	@Override
	public List<AelAmostraItemExamesVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		this.listAmostraItemExamesVO = this.examesFacade.pesquisarAmostraItemExames(this.unidadeExecutoraController.getUnidadeExecutora(), this.solicitacao, this.amostra, this.equipamento,
				this.exameSigla, this.enviado, firstResult, maxResult, orderProperty, asc);

		if (this.selecionarTodos) {
			this.allChecked = Boolean.TRUE;
			for (AelAmostraItemExamesVO vo : this.listAmostraItemExamesVO) {
				vo.setSelecionado(Boolean.TRUE);
			}
		} else if (!this.listAmostraItemExamesSelecionadosVO.isEmpty()) {
			for (AelAmostraItemExamesVO voSelecionado : this.listAmostraItemExamesSelecionadosVO) {
				for (AelAmostraItemExamesVO vo : this.listAmostraItemExamesVO) {
					if (vo.equals(voSelecionado)) {
						vo.setSelecionado(voSelecionado.getSelecionado());
						break;
					}
				}
			}
		}

		return this.listAmostraItemExamesVO;
	}

	public List<AelEquipamentos> pesquisarSBEquipamento(String valor) throws ApplicationBusinessException {
		try {
			return this.cadastrosApoioExamesFacade.pesquisarEquipamentosSeqDescricao((String) valor);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void selecionarItem(AelAmostraItemExamesVO item) {
		for (AelAmostraItemExamesVO vo : this.listAmostraItemExamesVO) {
			if (vo.equals(item)) {
				vo.setSelecionado(item.getSelecionado());
				if (item.getSelecionado()) {
					this.listAmostraItemExamesSelecionadosVO.add(item);
				} else {
					this.listAmostraItemExamesSelecionadosVO.remove(item);
				}
				break;
			}
		}
	}

	public void selecionarTodos() {
		if (this.allChecked) {
			this.selecionarTodos = Boolean.TRUE;
			for (AelAmostraItemExamesVO vo : this.listAmostraItemExamesVO) {
				vo.setSelecionado(Boolean.TRUE);
			}
		} else {
			this.desfazerSelecao();
		}
	}

	public void desfazerSelecao() {
		this.allChecked = Boolean.FALSE;
		this.selecionarTodos = Boolean.FALSE;
		this.listAmostraItemExamesSelecionadosVO = new LinkedHashSet<AelAmostraItemExamesVO>();
		for (AelAmostraItemExamesVO vo : this.listAmostraItemExamesVO) {
			vo.setSelecionado(Boolean.FALSE);
		}
	}

	public void realizarCargaInterfaceamento() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {

			Boolean retorno = this.examesFacade.realizarCargaInterfaceamento(this.listAmostraItemExamesSelecionadosVO, nomeMicrocomputador);
			if (retorno) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONFIRMA_PROGRAMACAO_AMOSTRA_EQUIPAMENTOS");
				this.desfazerSelecao();
				this.desmarcarOpcaoExibirModal();
				this.dataModel.reiniciarPaginator();
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NAO_CONFIRMA_PROGRAMACAO_AMOSTRA_EQUIPAMENTOS");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}

	public void verificarCondicaoModal() {
		this.exibirModalConfirmaProgramacao = Boolean.FALSE;
		this.exibirLabelConfirmaProgramacaoAmostra = Boolean.FALSE;

		if (this.selecionarTodos) {
			List<AelAmostraItemExamesVO> aelAmostraList = this.examesFacade.listarAmostraItemExamesTodos(this.unidadeExecutoraController.getUnidadeExecutora(), this.solicitacao, this.amostra,
					this.equipamento, this.exameSigla, this.enviado);
			this.listAmostraItemExamesSelecionadosVO.addAll(aelAmostraList);
		}

		for (AelAmostraItemExamesVO vo : this.listAmostraItemExamesSelecionadosVO) {
			if (vo.getSelecionado() && DominioSimNao.S == vo.getIndEnviado()) {
				this.exibirLabelConfirmaProgramacaoAmostra = Boolean.TRUE;
				break;
			}
		}

		if (!this.listAmostraItemExamesSelecionadosVO.isEmpty()) {
			this.exibirModalConfirmaProgramacao = Boolean.TRUE;
		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "AEL_01245");
		}
		
		if(this.exibirModalConfirmaProgramacao && !this.exibirLabelConfirmaProgramacaoAmostra){
			openDialog("modalConfirmaProgramacaoAmostraWG");
		} else {
			openDialog("modalConfirmaProgramacaoAmostraEquipamentoWG");
		}
	}

	public void desmarcarOpcaoExibirModal() {
		this.exibirModalConfirmaProgramacao = Boolean.FALSE;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		String retorno = this.voltarPara;

		this.solicitacao = null;
		this.amostra = null;
		this.equipamento = null;
		this.exameSigla = null;
		this.enviado = null;

		this.listAmostraItemExamesVO = new LinkedList<AelAmostraItemExamesVO>();
		this.listAmostraItemExamesSelecionadosVO = new LinkedHashSet<AelAmostraItemExamesVO>();

		this.amostraItemExames = new AelAmostraItemExames();
		this.exibirModalConfirmaProgramacao = Boolean.FALSE;
		this.exibirLabelConfirmaProgramacaoAmostra = Boolean.FALSE;
		this.selecionarTodos = Boolean.FALSE;
		this.allChecked = Boolean.FALSE;

		this.voltarPara = null;
		this.unfSeq = null;

		return retorno;
	}

	/** GET/SET **/
	public AelAmostraItemExames getAmostraItemExames() {
		return amostraItemExames;
	}

	public void setAmostraItemExames(AelAmostraItemExames amostraItemExames) {
		this.amostraItemExames = amostraItemExames;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public Integer getAmostra() {
		return amostra;
	}

	public AelEquipamentos getEquipamento() {
		return equipamento;
	}

	public String getExameSigla() {
		return exameSigla;
	}

	public DominioSimNao getEnviado() {
		return enviado;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public void setAmostra(Integer amostra) {
		this.amostra = amostra;
	}

	public void setEquipamento(AelEquipamentos equipamento) {
		this.equipamento = equipamento;
	}

	public void setExameSigla(String exameSigla) {
		this.exameSigla = exameSigla;
	}

	public void setEnviado(DominioSimNao enviado) {
		this.enviado = enviado;
	}

	public List<AelAmostraItemExamesVO> getListAmostraItemExamesVO() {
		return listAmostraItemExamesVO;
	}

	public void setListAmostraItemExamesVO(List<AelAmostraItemExamesVO> listAmostraItemExamesVO) {
		this.listAmostraItemExamesVO = listAmostraItemExamesVO;
	}

	public Boolean getExibirModalConfirmaProgramacao() {
		return exibirModalConfirmaProgramacao;
	}

	public void setExibirModalConfirmaProgramacao(Boolean exibirModalConfirmaProgramacao) {
		this.exibirModalConfirmaProgramacao = exibirModalConfirmaProgramacao;
	}

	public Boolean getExibirLabelConfirmaProgramacaoAmostra() {
		return exibirLabelConfirmaProgramacaoAmostra;
	}

	public void setExibirLabelConfirmaProgramacaoAmostra(Boolean exibirLabelConfirmaProgramacaoAmostra) {
		this.exibirLabelConfirmaProgramacaoAmostra = exibirLabelConfirmaProgramacaoAmostra;
	}

	public Boolean getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(Boolean allChecked) {
		this.allChecked = allChecked;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public DynamicDataModel<AelAmostraItemExamesVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelAmostraItemExamesVO> dataModel) {
		this.dataModel = dataModel;
	}
}
