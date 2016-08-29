package br.gov.mec.aghu.compras.autfornecimento.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.vo.AlteracaoEntregaProgramadaVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ParcelasEntregaMatDiretoON extends BaseBusiness {
	
	private static final long serialVersionUID = 8662713532053239956L;
	
	private static final Log LOG = LogFactory.getLog(ParcelasEntregaMatDiretoON.class);
	
	@EJB
	private ParcelasEntregaMatDiretoRN parcelasEntregaMatDiretoRN;
	
	public AlteracaoEntregaProgramadaVO gerarParcelas(DominioSimNao entregaProgramada, Integer afNumero) throws ApplicationBusinessException {
		AlteracaoEntregaProgramadaVO vo = parcelasEntregaMatDiretoRN.gerarParcelas(entregaProgramada, afNumero); 
		return vo;
	}

	public AlteracaoEntregaProgramadaVO geraProgramacaoParcela(Integer afNumero, Boolean gerarProgramacao) {
		try {
			return parcelasEntregaMatDiretoRN.gerarProgEntregaMatDireto(afNumero, gerarProgramacao, true);
		} catch (ApplicationBusinessException e) {
			return null;
		}
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
