package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CidController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(CidController.class);

	private static final long serialVersionUID = 23310961025925452L;

	private static final String PAGE_CID_LIST = "cidList";

	@EJB
	private ICidFacade cidFacade;

	private AghCapitulosCid capituloCid = new AghCapitulosCid();

	private AghGrupoCids grupoCids = new AghGrupoCids();

	private Integer cidSeq;

	private String cidCodigo;

	/*
	 * Cid a ser Criado/editado
	 */
	private AghCid cidO = new AghCid();
	/*
	 * Variável que indica o estado de edição de um AghCids.
	 */
	private Boolean isUpdate;

	/**
	 * Flag novo, obtido via page parameter.
	 */
	private boolean novo;

	private enum Operacao {
		CRIACAO, EDICAO
	}

	private Operacao operacao;
	private boolean operacaoConcluida = false;

	/**
	 * LOV Cid Secundário Inicial
	 * 
	 * @return
	 */
	private AghCid cidInicial = new AghCid();
	private AghCid cidFinal = new AghCid();

	// ############## Métódos do Controller ###########################

	public String iniciarInclusao() {
		this.cidSeq = null;
		return "iniciarInclusao";
	}

	public void inicio() {
		try {
			if (isNovo()) {
				// Criando um novo CID
				operacao = Operacao.CRIACAO;
				this.cidO = this.cidFacade.nova();
				this.cidFinal = null;
				this.cidInicial = null;
			} else {
				operacao = Operacao.EDICAO;
				this.cidO = this.cidFacade.obterCid(cidCodigo);
				if (StringUtils.isNotBlank(this.cidO.getCidInicialSecundario())) {
					cidInicial = this.cidFacade.obterCid(this.cidO.getCidInicialSecundario());
				} else {
					cidInicial = null;
				}
				if (StringUtils.isNotBlank(this.cidO.getCidFinalSecundario())) {
					cidFinal = this.cidFacade.obterCid(this.cidO.getCidFinalSecundario());
				} else {
					cidFinal = null;
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String confirmar() {
		try {
			this.atribuirValoresCidSecundarios();

			switch (operacao) {
				case CRIACAO:
					this.cidFacade.incluirCid(cidO);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_CID", this.cidO.getDescricao());
					break;
				case EDICAO:
					this.cidFacade.atualizarCid(cidO);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CID", this.cidO.getCodigo());
					break;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return cancelar();
	}

	public void atribuirValoresCidSecundarios() {
		if (this.cidInicial != null) {
			this.cidO.setCidInicialSecundario(this.cidInicial.getCodigo() == null ? "" : this.cidInicial.getCodigo());
		} else {
			this.cidO.setCidInicialSecundario(null);
		}

		if (this.cidFinal != null) {
			this.cidO.setCidFinalSecundario(this.cidFinal.getCodigo() == null ? "" : this.cidFinal.getCodigo());
		} else {
			this.cidO.setCidFinalSecundario(null);
		}
	}

	public List<AghGrupoCids> pesquisaGrupoCids(String strPesquisa) {
		return cidFacade.listarPorSigla(strPesquisa);
	}

	public List<AghCid> pesquisaCategoriaCids(String param) {
		return cidFacade.pesquisarCidsSemSubCategoriaPorDescricaoOuId(param, Integer.valueOf(300));
	}

	public List<AghCid> pesquisaCategoriaCidSecundarioInicial(String param) {
		return cidFacade.pesquisarCidsComSubCategoriaPorDescricaoOuId(param, Integer.valueOf(300));
	}

	public List<AghCid> pesquisaCategoriaCidsSecundarioFinal(String param) {
		return cidFacade.pesquisarCidsComSubCategoriaPorDescricaoOuId(param, Integer.valueOf(300));
	}

	public List<AghCid> pesquisarCids(Object param) {
		return cidFacade.pesquisarCidsComSubCategoriaPorDescricaoOuId(param.toString(), Integer.valueOf(300));
	}

	public String cancelar() {
		LOG.info("Cancelado");
		this.cidSeq = null;
		operacao = null;
		this.cidO = null;
		this.cidFinal = null;
		this.cidInicial = null;
		return PAGE_CID_LIST;
	}

	public void limparGrupoCID() {
		this.cidO.setGrupoCids(null);
	}

	public boolean isMostrarLinkExcluirGrupo() {
		return this.cidO.getGrupoCids() != null;
	}

	// SET's e GET's

	public AghCapitulosCid getCapituloCid() {
		return capituloCid;
	}

	public void setCapituloCid(AghCapitulosCid capituloCid) {
		this.capituloCid = capituloCid;
	}

	public AghGrupoCids getGrupoCids() {
		return grupoCids;
	}

	public void setGrupoCids(AghGrupoCids grupoCids) {
		this.grupoCids = grupoCids;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public AghCid getCidO() {
		return cidO;
	}

	public void setCidO(AghCid cid) {
		this.cidO = cid;
	}

	public boolean isNovo() {
		return novo;
	}

	public void setNovo(boolean novo) {
		this.novo = novo;
	}

	public Operacao getOperacao() {
		return operacao;
	}

	public void setOperacao(Operacao operacao) {
		this.operacao = operacao;
	}

	public boolean isOperacaoConcluida() {
		return operacaoConcluida;
	}

	public void setOperacaoConcluida(boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public String getCidCodigo() {
		return cidCodigo;
	}

	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}

	public AghCid getCidInicial() {
		return cidInicial;
	}

	public void setCidInicial(AghCid cidInicial) {
		this.cidInicial = cidInicial;
	}

	public AghCid getCidFinal() {
		return cidFinal;
	}

	public void setCidFinal(AghCid cidFinal) {
		this.cidFinal = cidFinal;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

}