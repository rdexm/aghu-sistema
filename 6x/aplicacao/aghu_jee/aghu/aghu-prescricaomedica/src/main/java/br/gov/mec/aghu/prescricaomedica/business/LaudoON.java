package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class LaudoON extends BaseBusiness {


@EJB
private LaudoRN laudoRN;

private static final Log LOG = LogFactory.getLog(LaudoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5878157573598954094L;

	private LaudoRN getLaudoRN() {
		return laudoRN;
	}
	
	/**
	 * Metodo que atualiza um laudo por completo (executa verificacoes pre update, atualiza, e insere journal caso necessario - nesta sequencia)
	 * @param laudoNew
	 * @param laudoOld
	 */
	public void atualizarLaudo(MpmLaudo laudoNew, MpmLaudo laudoOld) throws BaseException {
			
			this.getLaudoRN().atualizarLaudo(laudoNew, laudoOld);
	}			

	public void inserirLaudo(MpmLaudo laudoNew) throws BaseException {
		this.getLaudoRN().inserirLaudo(laudoNew);
	}
	
	public void geraLaudoInternacao(final AghAtendimentos atendimento,
			final Date dthrInicio, final AghuParametrosEnum tipoLaudo,
			final FatConvenioSaudePlano convenioSaudePlano)
			throws BaseException {
		getLaudoRN().geraLaudoInternacao(atendimento, dthrInicio, tipoLaudo, convenioSaudePlano);
	}

}
