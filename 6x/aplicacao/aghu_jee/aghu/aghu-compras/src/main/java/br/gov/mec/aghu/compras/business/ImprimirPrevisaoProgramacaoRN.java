package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.vo.EntregaPorItemVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFVO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ImprimirPrevisaoProgramacaoRN extends BaseBusiness {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8819518903346077958L;
	private static final Log LOG = LogFactory.getLog(ImprimirPrevisaoProgramacaoRN.class);
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	public enum AcessoFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CODIGO_CADASTRADO, MENSAGEM_NENHUM_CONTATO_ENCONTRADO;
	}
	
	/**
	 * Monta as parcelas para visualizar no relatorio
	 * Aqui as parcelas ja estão corretas com os tipos de fases e 
	 * Com as entregas de cada item
	 * 
	 * Baseado no VO ParcelasAFVO apenas é necessario exibir as informacoes no relatorio
	 * @param numeroAutorizacao
	 * @return
	 */
	public List<ParcelasAFVO> recuperaAFPacelas(Integer numeroAutorizacao) {
		DominioTipoFaseSolicitacao tipoFaseSolicitacao = getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultaAFMaterialServico(numeroAutorizacao);
		
		List<ParcelasAFVO> parcelas = null;
		if (DominioTipoFaseSolicitacao.C.equals(tipoFaseSolicitacao)) {
			parcelas = getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultaParcelasAFMaterial(numeroAutorizacao);
			calculaQuantidadeParcelaMaterial(parcelas);
		} else {
			parcelas = getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultaParcelasAFServico(numeroAutorizacao);
			setQuantidadeParcelaServico(parcelas);
		}
		//cria as entregas por itens
		for (ParcelasAFVO parcelasAFVO : parcelas) {
			List<EntregaPorItemVO> listaEntregaPorItemVOs = getScoProgEntregaItemAutorizacaoFornecimentoDAO().consultaEntregaPorItem(parcelasAFVO);
			parcelasAFVO.setEntregasPorItem(listaEntregaPorItemVOs);
			
			if (listaEntregaPorItemVOs == null || listaEntregaPorItemVOs.isEmpty()) {
				parcelasAFVO.setPossuiEntregaPorItem(Boolean.FALSE);
			} else {
				parcelasAFVO.setPossuiEntregaPorItem(Boolean.TRUE);
			}
			
			Double valorTot = parcelasAFVO.getPeaValorTotal() == null ? 0 : parcelasAFVO.getPeaValorTotal();
			Integer qtd = parcelasAFVO.getPeaQtde() == null ? 1 : parcelasAFVO.getPeaQtde();  
			Double vlrUnit = valorTot / qtd;
			parcelasAFVO.setPeaValorUnit(vlrUnit);
			
			parcelasAFVO.setTipoParcela(tipoFaseSolicitacao);
		}
		
		return parcelas;
	}


	private void setQuantidadeParcelaServico(List<ParcelasAFVO> parcelas) {
		for (ParcelasAFVO parcelasAFVO : parcelas) {
			parcelasAFVO.setPeaQtde(1);
		}
	}


	private void calculaQuantidadeParcelaMaterial(List<ParcelasAFVO> parcelas) {
		for (ParcelasAFVO parcelasAFVO : parcelas) {
			Double v = parcelasAFVO.getPeaValorTotal()/parcelasAFVO.getPeaQtde();
			parcelasAFVO.setPeaValorUnit(v);
		}
	}
	
	
	

	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}


	protected void setScoProgEntregaItemAutorizacaoFornecimentoDAO(ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO) {
		this.scoProgEntregaItemAutorizacaoFornecimentoDAO = scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}


	@Override
	protected Log getLogger() {
		return LOG;
	}


}
