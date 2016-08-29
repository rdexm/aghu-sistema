package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;

@Stateless
public class ValorCadastralProcedimentoSusRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ValorCadastralProcedimentoSusRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	private static final long serialVersionUID = -3968475630909936701L;
	
	public BigDecimal getValorCadastralProcedimentoSus(
			DominioOrigemPacienteCirurgia origemPacienteCirurgia,
			final BigDecimal vlrServHospitalar,
			final BigDecimal vlrServProfissional, final BigDecimal vlrSadt,
			final BigDecimal vlrProcedimento, final BigDecimal vlrAnestesista) {
	
		BigDecimal valorTotal = BigDecimal.ZERO;
	
		if (DominioOrigemPacienteCirurgia.I.equals(origemPacienteCirurgia)) {
	
			BigDecimal valorServHospitalar = nvl(vlrServHospitalar);
			BigDecimal valorServProfissional = nvl(vlrServProfissional);
			BigDecimal valorSadt = nvl(vlrSadt); 
			BigDecimal valorAnestesista = nvl(vlrAnestesista); 
	
			valorTotal = valorTotal.add(valorServHospitalar).add(valorServProfissional).add(valorSadt).add(valorAnestesista);
	
		} else {
			valorTotal = nvl(vlrProcedimento);
		}
		
		return valorTotal;
	}
	
	private BigDecimal nvl(Object a) {
		return (BigDecimal) CoreUtil.nvl(a, BigDecimal.ZERO);
	}

}
