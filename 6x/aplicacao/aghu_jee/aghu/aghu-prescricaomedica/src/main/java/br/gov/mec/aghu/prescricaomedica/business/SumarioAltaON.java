package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSumarioAltaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class SumarioAltaON extends BaseBusiness {


@EJB
private SumarioAltaRN sumarioAltaRN;

private static final Log LOG = LogFactory.getLog(SumarioAltaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmSumarioAltaDAO mpmSumarioAltaDAO;

	private static final long serialVersionUID = -5555241664096850104L;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void atualizarSumarioAlta(final MpmSumarioAlta sumarioAlta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException, Exception {
		if (sumarioAlta != null) {
			// Chamada de trigger "before each row"
			this.getSumarioAltaRN().preAtualizar(sumarioAlta, nomeMicrocomputador, dataFimVinculoServidor);

			if (!getMpmSumarioAltaDAO().contains(sumarioAlta)) {
				getMpmSumarioAltaDAO().merge(sumarioAlta);
			}

			getMpmSumarioAltaDAO().flush();

			// Chamada de trigger "after statement" (enforce)
			this.getSumarioAltaRN().executarAposAtualizarSumarioAlta(sumarioAlta);
		}
	}
	 
	/**
	 * #39012 - Serviço para atualizar Sumario
	 * @param atdSeq
	 * @param nomeMicromputador
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarSumarioAltaApagarDadosAlta(final Integer atdSeq, final String nomeMicromputador) throws ApplicationBusinessException{
		MpmSumarioAlta sumarioAlta = this.getMpmSumarioAltaDAO().obterPorChavePrimaria(atdSeq);
		if (sumarioAlta != null) {
				sumarioAlta.setMotivoAltaMedica(null);
				sumarioAlta.setDthrAlta(null);
				sumarioAlta.setIndNecropsia(null);
				try {
					atualizarSumarioAlta(sumarioAlta, nomeMicromputador, new Date());
				} catch (Exception e) {
					throw new ApplicationBusinessException("Erro ao executar atualizar dados alta", Severity.ERROR);
				}		
		}	
	}
	protected SumarioAltaRN getSumarioAltaRN() {
		return sumarioAltaRN;
	}

	protected MpmSumarioAltaDAO getMpmSumarioAltaDAO(){
		return mpmSumarioAltaDAO;
	}
}
