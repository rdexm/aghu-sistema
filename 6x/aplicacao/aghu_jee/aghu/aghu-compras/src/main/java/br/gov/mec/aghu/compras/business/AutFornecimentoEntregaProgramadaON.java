/**
 * 
 */
package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.vo.AutFornEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 *
 */
@Stateless
public class AutFornecimentoEntregaProgramadaON extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2261955322391131845L;
	private static final Log LOG = LogFactory.getLog(AutFornecimentoEntregaProgramadaON.class);
	
	
	
	public ProgramacaoEntregaGlobalTotalizadorVO totalizaValores(List<AutFornEntregaProgramadaVO> listagem) {
		ProgramacaoEntregaGlobalTotalizadorVO programacaoEntregaGlobalTotalizadorVO = new ProgramacaoEntregaGlobalTotalizadorVO();
		for (AutFornEntregaProgramadaVO vo : listagem) {
			programacaoEntregaGlobalTotalizadorVO.setTotalSaldoProgramado(programacaoEntregaGlobalTotalizadorVO.getTotalSaldoProgramado().add(vo.getSaldoProgramado() == null ? BigDecimal.ZERO : vo.getSaldoProgramado()));
			programacaoEntregaGlobalTotalizadorVO.setTotalValorALiberar(programacaoEntregaGlobalTotalizadorVO.getTotalValorALiberar().add(vo.getValorALiberar() == null ? BigDecimal.ZERO : vo.getValorALiberar()));
			programacaoEntregaGlobalTotalizadorVO.setTotalValorLiberado(programacaoEntregaGlobalTotalizadorVO.getTotalValorLiberado().add(vo.getValorLiberado() == null ? BigDecimal.ZERO : vo.getValorLiberado()));
			programacaoEntregaGlobalTotalizadorVO.setTotalValorEmAtraso(programacaoEntregaGlobalTotalizadorVO.getTotalValorEmAtraso().add(vo.getValorEmAtraso() == null ? BigDecimal.ZERO : vo.getValorEmAtraso()));
		}
		
		return programacaoEntregaGlobalTotalizadorVO;
	}



	@Override
	protected Log getLogger() {
		return LOG;
	}
}	
