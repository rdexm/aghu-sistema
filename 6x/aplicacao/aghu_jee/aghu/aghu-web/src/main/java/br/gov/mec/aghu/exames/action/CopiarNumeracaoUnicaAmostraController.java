package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExamesVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CopiarNumeracaoUnicaAmostraController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 3689987064310591625L;
	
	private static final Log LOG = LogFactory.getLog(CopiarNumeracaoUnicaAmostraController.class);

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IColetaExamesFacade coletaExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private AelAmostraItemExames origem;
	private AelAmostraItemExames destino;
	private Integer soeSeqOrig;
	private Short seqpOrig;
	private Integer soeSeqDest;
	private Short seqpDest;

	private AelAmostrasVO aelAmostraOrigemVO;
	private AelAmostrasVO aelAmostraDestinoVO;
	private List<AelItemSolicitacaoExamesVO> itemSolicitacaoExamesOrigemList;
	private List<AelItemSolicitacaoExamesVO> itemSolicitacaoExamesDestinoList;
	private Boolean isCopiarNumeracaoUnica = Boolean.FALSE;
	private String voltarPara;

	private Boolean hasCopiarNumeracaoUnica = Boolean.FALSE;

	public void iniciar() {
	 

		
		CoreUtil.validaParametrosObrigatorios(this.origem, this.destino);
		
		this.soeSeqOrig = this.origem.getId().getIseSoeSeq();
		this.soeSeqDest = this.destino.getId().getIseSoeSeq();
		this.seqpOrig = this.origem.getId().getAmoSeqp().shortValue();
		this.seqpDest = this.destino.getId().getAmoSeqp().shortValue();
		
		this.aelAmostraOrigemVO = this.examesFacade.obterAelAmostraVO(this.soeSeqOrig, this.seqpOrig);
		this.itemSolicitacaoExamesOrigemList = this.examesFacade.listarItemSolicitacaoExamesVO(this.soeSeqOrig, this.seqpOrig);

		this.aelAmostraDestinoVO = this.examesFacade.obterAelAmostraVO(this.soeSeqDest, this.seqpDest);
		this.itemSolicitacaoExamesDestinoList = this.examesFacade.listarItemSolicitacaoExamesVO(this.soeSeqDest, this.seqpDest);

	
	}

	public void copiarNumeracaoUnica() {
		this.isCopiarNumeracaoUnica = Boolean.TRUE;
		this.aelAmostraDestinoVO.setNroUnico(this.aelAmostraOrigemVO.getNroUnico());
		this.aelAmostraDestinoVO.setDtNumeroUnico(this.aelAmostraOrigemVO.getDtNumeroUnico());
	}

	public void gravar() {
		try {
			AelAmostras aelAmostraOrigem = this.examesFacade.buscarAmostrasPorId(this.soeSeqOrig, this.seqpOrig);
			AelAmostras aelAmostraDestino = this.examesFacade.buscarAmostrasPorId(this.soeSeqDest, this.seqpDest);

			if (this.isCopiarNumeracaoUnica) {
				// setar as colunas nro_unico e dt_numero_unico
				// da amostra de destino com as informações da amostra de origem.
				aelAmostraDestino.setNroUnico(this.aelAmostraOrigemVO.getNroUnico());
				aelAmostraDestino.setDtNumeroUnico(this.aelAmostraOrigemVO.getDtNumeroUnico());
			}

			this.examesFacade.ajustarNumeroUnicoAelAmostra(aelAmostraOrigem, aelAmostraDestino);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CONFIRMA_COPIAR_NUMERACAO_UNICA");
			this.hasCopiarNumeracaoUnica = Boolean.TRUE;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			this.iniciar();
		}
	}

	public String voltar() {
		String retorno = this.voltarPara;
		this.limparParametros();
		return retorno;
	}

	private void limparParametros() {
		this.origem = null;
		this.destino = null;
		this.soeSeqOrig = null;
		this.seqpOrig = null;
		this.soeSeqDest = null;
		this.seqpDest = null;
		this.aelAmostraOrigemVO = null;
		this.aelAmostraDestinoVO = null;
		this.itemSolicitacaoExamesOrigemList = null;
		this.itemSolicitacaoExamesDestinoList = null;
		this.isCopiarNumeracaoUnica = Boolean.FALSE;
		this.voltarPara = null;
		this.hasCopiarNumeracaoUnica = Boolean.FALSE;
	}

	public void imprimirEtiqueta() {
		try {

			final AelAmostras amostraImpressaoEtiquetas = this.coletaExamesFacade.obterAmostra(this.aelAmostraDestinoVO.getSoeSeq(), this.aelAmostraDestinoVO.getSeqp());
			final AghUnidadesFuncionais unidadeExecutora = this.aghuFacade.obterUnidadeFuncional(this.aelAmostraDestinoVO.getUnfSeq());
			String nomeMicrocomputador = getEnderecoRedeHostRemoto();

			this.examesFacade.imprimirEtiquetaAmostra(amostraImpressaoEtiquetas, unidadeExecutora, nomeMicrocomputador);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA", amostraImpressaoEtiquetas.getId().getSeqp());

		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public Integer getSoeSeqOrig() {
		return soeSeqOrig;
	}

	public void setSoeSeqOrig(Integer soeSeqOrig) {
		this.soeSeqOrig = soeSeqOrig;
	}

	public Short getSeqpOrig() {
		return seqpOrig;
	}

	public void setSeqpOrig(Short seqpOrig) {
		this.seqpOrig = seqpOrig;
	}

	public Integer getSoeSeqDest() {
		return soeSeqDest;
	}

	public void setSoeSeqDest(Integer soeSeqDest) {
		this.soeSeqDest = soeSeqDest;
	}

	public Short getSeqpDest() {
		return seqpDest;
	}

	public void setSeqpDest(Short seqpDest) {
		this.seqpDest = seqpDest;
	}

	public AelAmostrasVO getAelAmostraOrigemVO() {
		return aelAmostraOrigemVO;
	}

	public void setAelAmostraOrigemVO(AelAmostrasVO aelAmostraOrigemVO) {
		this.aelAmostraOrigemVO = aelAmostraOrigemVO;
	}

	public AelAmostrasVO getAelAmostraDestinoVO() {
		return aelAmostraDestinoVO;
	}

	public void setAelAmostraDestinoVO(AelAmostrasVO aelAmostraDestinoVO) {
		this.aelAmostraDestinoVO = aelAmostraDestinoVO;
	}

	public List<AelItemSolicitacaoExamesVO> getItemSolicitacaoExamesOrigemList() {
		return itemSolicitacaoExamesOrigemList;
	}

	public void setItemSolicitacaoExamesOrigemList(List<AelItemSolicitacaoExamesVO> itemSolicitacaoExamesOrigemList) {
		this.itemSolicitacaoExamesOrigemList = itemSolicitacaoExamesOrigemList;
	}

	public List<AelItemSolicitacaoExamesVO> getItemSolicitacaoExamesDestinoList() {
		return itemSolicitacaoExamesDestinoList;
	}

	public void setItemSolicitacaoExamesDestinoList(List<AelItemSolicitacaoExamesVO> itemSolicitacaoExamesDestinoList) {
		this.itemSolicitacaoExamesDestinoList = itemSolicitacaoExamesDestinoList;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getIsCopiarNumeracaoUnica() {
		return isCopiarNumeracaoUnica;
	}

	public void setIsCopiarNumeracaoUnica(Boolean isCopiarNumeracaoUnica) {
		this.isCopiarNumeracaoUnica = isCopiarNumeracaoUnica;
	}

	public Boolean getHasCopiarNumeracaoUnica() {
		return hasCopiarNumeracaoUnica;
	}

	public void setHasCopiarNumeracaoUnica(Boolean hasCopiarNumeracaoUnica) {
		this.hasCopiarNumeracaoUnica = hasCopiarNumeracaoUnica;
	}
	
	public AelAmostraItemExames getOrigem() {
		return origem;
	}
	
	public void setOrigem(AelAmostraItemExames origem) {
		this.origem = origem;
	}
	
	public AelAmostraItemExames getDestino() {
		return destino;
	}
	
	public void setDestino(AelAmostraItemExames destino) {
		this.destino = destino;
	}

}