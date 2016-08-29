package br.gov.mec.aghu.exames.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @HIST RelatorioMascaraExamesHistController
 * 
 *       Teve que ser desextendida a classe ActionController pois foi não estava
 *       injetando na controller aonde Scope(ScopeType.SESSION)
 * @deprecated esta classe faz consulta de dados para o antigo laudo de exames<br />
 */

@SuppressWarnings({"PMD.HierarquiaControllerIncorreta"})
public class RelatorioMascaraExamesController extends ActionController {

	private static final long serialVersionUID = -7062309275503143576L;
	private static final Log LOG = LogFactory.getLog(RelatorioMascaraExamesController.class);

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	/**
	 * Parâmetro de entrada na tela. Representa o sequencial da solicitação de
	 * exames.
	 */
	private Integer solicitacaoExameSeq;

	/**
	 * Parâmetro de entrada na tela. Representa o sequencial do item da
	 * solicitação de exame.
	 */
	private Short itemSolicitacaoExameSeq;

	private List<DesenhoMascaraExameVO> desenhosMascarasExamesVO;

	private Boolean isHist;

	private Boolean isUltimoItem;

	private List<Short> seqps;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Método executado na carga da página. Carrega os dados da máscara a ser
	 * exibida e instancia os componentes de acordo.
	 * 
	 * @throws BaseException
	 */
	public void inicializar(DominioSubTipoImpressaoLaudo subTipoImpressaoLaudo) throws BaseException {
		LOG.info(String.format("Pesquisando máscaras. SOE_SEQ: %s, SEQP: %s.", this.solicitacaoExameSeq, this.itemSolicitacaoExameSeq));
		inicializar(Boolean.FALSE, subTipoImpressaoLaudo);
	}

	public void inicializar(final Boolean isPdf, DominioSubTipoImpressaoLaudo subTipoImpressaoLaudo) throws BaseException {
		if (isPdf) {
			LOG.info(String.format("Pesquisando máscaras -> PDF. SOE_SEQ: %s, SEQP: %s.", this.solicitacaoExameSeq,
					this.itemSolicitacaoExameSeq));
		}
//		this.desenhosMascarasExamesVO = this.mascaraExamesComponentes.buscaDesenhosRelatorioMascarasExamesVO(this.solicitacaoExameSeq,
//				this.itemSolicitacaoExameSeq, subTipoImpressaoLaudo, isPdf, null, isHist == null ? Boolean.FALSE : isHist, isUltimoItem,
//				seqps);
	}

	public List<Short> ordenarExames(Integer solicitacao, List<Short> seqps) throws ApplicationBusinessException {
		return pesquisaExamesFacade.buscaExamesSolicitadosOrdenados(solicitacao, seqps, isHist);
	}

	// GETERS E SETERS

	public Integer getSolicitacaoExameSeq() {
		return solicitacaoExameSeq;
	}

	public void setSolicitacaoExameSeq(Integer solicitacaoExameSeq) {
		this.solicitacaoExameSeq = solicitacaoExameSeq;
	}

	public Short getItemSolicitacaoExameSeq() {
		return itemSolicitacaoExameSeq;
	}

	public void setItemSolicitacaoExameSeq(Short itemSolicitacaoExameSeq) {
		this.itemSolicitacaoExameSeq = itemSolicitacaoExameSeq;
	}

	public List<DesenhoMascaraExameVO> getDesenhosMascarasExamesVO() {
		return desenhosMascarasExamesVO;
	}

	public void setDesenhosMascarasExamesVO(List<DesenhoMascaraExameVO> desenhosMascarasExamesVO) {
		this.desenhosMascarasExamesVO = desenhosMascarasExamesVO;
	}

	public Boolean getIsHist() {
		return isHist;
	}

	public void setIsHist(Boolean isHist) {
		this.isHist = isHist;
	}

	public Boolean getIsUltimoItem() {
		return isUltimoItem;
	}

	public void setIsUltimoItem(Boolean isUltimoItem) {
		this.isUltimoItem = isUltimoItem;
	}

	public List<Short> getSeqps() {
		return seqps;
	}

	public void setSeqps(List<Short> seqps) {
		this.seqps = seqps;
	}

}
