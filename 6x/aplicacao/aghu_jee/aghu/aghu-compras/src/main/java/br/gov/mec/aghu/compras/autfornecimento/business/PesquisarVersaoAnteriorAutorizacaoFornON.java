package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.suprimentos.vo.CalculoValorTotalAFVO;
import br.gov.mec.aghu.suprimentos.vo.ParamCalculoValorTotalAFVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class PesquisarVersaoAnteriorAutorizacaoFornON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisarVersaoAnteriorAutorizacaoFornON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	private static final long serialVersionUID = -1694509997151414114L;
	
	/**
	 * Estória do Usuário #5531 (SQL4)
	 * 
	 * @param afnNumero
	 * @return
	 */
	public CalculoValorTotalAFVO getCalculoValorTotalAF(Integer afnNumero, Integer seqAlteracao) {		
		List<Object[]> lista = getScoFaseSolicitacaoDAO().createCalculoValorTotalAFHql(afnNumero, seqAlteracao);
		
		CalculoValorTotalAFVO calculoVo = new CalculoValorTotalAFVO();
		
		calculoVo.setValorBruto(0.0);
		calculoVo.setValorIPI(0.0);
		calculoVo.setValorAcresc(0.0);
		calculoVo.setValorDesc(0.0);
		calculoVo.setValorLiq(0.0);
		calculoVo.setValorEfetivo(0.0);
		calculoVo.setValorTotalAF(0.0);
		
		for (Object[] object : lista) {
			ParamCalculoValorTotalAFVO vo = new ParamCalculoValorTotalAFVO();
			setInitialParam(vo, object);
			setFinalParam(vo, object);
			
			Double vlrDescCond;
			Double vlrDescItem;
			Double vlrDesc;
			Double vlrBruto;
			Double vlrItem;
			Double vlrAcrescItem;
			Double vlrAcrescCond;
			Double vlrAcresc;
			Double vlrIPI;
			
			Double vlrTotalLiq = 0.0;
			
			if (DominioTipoFaseSolicitacao.C.equals(vo.getTipo())) {
				vlrBruto = ((vo.getQtdadeSolicitada() - vo.getQtdeRecebida()) * vo
						.getVlrUnitario());
				vlrItem = vlrBruto + vo.getValorEfetivado();
			} else {
				vlrBruto = vo.getVlrUnitario();
				vlrItem = vo.getVlrUnitario();
			}
			
			if(vo.getPercDescItem() != null){
				if(vo.getPercDescItem() > 0){
					vlrDescItem = (vlrBruto * (vo.getPercDescItem() / 100));
				} else {
					vlrDescItem = 0.0;
				}
			} else {
				vlrDescItem = 0.0;
			}
			
			
			if(vo.getPercDesc() != null){
				if(vo.getPercDesc() > 0){
					vlrDescCond = (vlrBruto * (vo.getPercDesc() / 100));
				} else {
					vlrDescCond = 0.0;
				}
			} else {
				vlrDescCond = 0.0;
			}
			
			vlrDesc = vlrDescItem + vlrDescCond;	//valor Desconto
			
			if(vo.getPercAcrescItem() != null){
				if(vo.getPercAcrescItem() > 0){
					vlrAcrescItem = (vlrBruto * (vo.getPercAcrescItem() / 100));
				} else {
					vlrAcrescItem = 0.0;
				}
			} else {
				vlrAcrescItem = 0.0;
			}
			
			if(vo.getPercAcresc() != null){
				if(vo.getPercAcresc() > 0){
					vlrAcrescCond = (vlrBruto * (vo.getPercAcresc() / 100));
				} else {
					vlrAcrescCond = 0.0;
				}
			} else {
				vlrAcrescCond = 0.0;
			}
			
			vlrAcresc = vlrAcrescItem + vlrAcrescCond; //valor Acrescimo
			
			if(vo.getPercIpi() != null){
				if(vo.getPercIpi() > 0){
					vlrIPI = (vlrBruto - vlrDesc + vlrAcresc) * (vo.getPercIpi() / 100);
				} else {
					vlrIPI = 0.0; //valor IPI
				}
			}	else {
				vlrIPI = 0.0; //valor IPI
			}
			
			vlrTotalLiq = vlrItem - vo.getValorEfetivado();
			
			calculoVo.setValorBruto(calculoVo.getValorBruto() + vlrBruto);
			calculoVo.setValorIPI(calculoVo.getValorIPI() + vlrIPI);
			calculoVo.setValorAcresc(calculoVo.getValorAcresc() + vlrAcresc);
			calculoVo.setValorDesc(calculoVo.getValorDesc() + vlrDesc);
			calculoVo.setValorLiq(calculoVo.getValorLiq() + vlrTotalLiq);
			calculoVo.setValorEfetivo(calculoVo.getValorEfetivo() + vo.getValorEfetivado());
			calculoVo.setValorTotalAF(calculoVo.getValorTotalAF() + vlrItem);
		}
		
		return calculoVo;
	}
	
	private void setInitialParam(ParamCalculoValorTotalAFVO vo, Object[] object) {
		if (object[0] != null){
			vo.setQtdadeSolicitada((Integer)object[0]);
		} else {
			vo.setQtdadeSolicitada(0);
		}
		
		if (object[1] != null){
			vo.setVlrUnitario((Double)object[1]);
		}
		
		if (object[2] != null){
			vo.setPercIpi((Double)object[2]);
		}
		
		if (object[3] != null){
			vo.setPercAcrescItem((Double)object[3]);
		}
		
		if (object[4] != null){
			vo.setPercDesc((Double)object[4]);
		}
	}
	
	private void setFinalParam(ParamCalculoValorTotalAFVO vo, Object[] object) {		
		if (object[5] != null){
			vo.setPercAcresc((Double)object[5]);
		}
		
		if (object[6] != null){
			vo.setPercDescItem((Double)object[6]);
		}
		
		if (object[7] != null){
			vo.setValorEfetivado((Double)object[7]);
		}
		
		if (object[8] != null){
			vo.setTipo((DominioTipoFaseSolicitacao)object[8]);
		}
		
		if (object[9] != null) {
			vo.setQtdeRecebida((Integer) object[9]);
		} else {
			vo.setQtdeRecebida(0);
		}
	}

	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
}
