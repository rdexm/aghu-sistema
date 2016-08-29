package br.gov.mec.aghu.prescricaomedica.business;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.annotation.TransactionTimeout;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.model.RapServidores;


@Modulo(ModuloEnum.PRESCRICAO_MEDICA)
@Stateless
public class PrescricaoMedicaBeanFacade extends BaseFacade implements IPrescricaoMedicaBeanFacade {
	
	private static final long serialVersionUID = -7427980165160378868L;

	private static final Log LOG = LogFactory.getLog(PrescricaoMedicaBeanFacade.class);
	
	@EJB
	private ManterSumarioRN manterSumarioRN;
	
	
	
	public enum PrescricaoMedicaBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_GERAR_DADOS_SUMARIO_PRESCRICAO_MEDICA
		;
	}
	
	@Resource
	protected SessionContext ctx;

	
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@TransactionTimeout(value=3, unit=TimeUnit.HOURS)
	public void geraDadosSumarioPrescricao(final Integer seqAtendimento,
			final DominioTipoEmissaoSumario tipoEmissao, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		try {
			this.getManterSumarioRN().geraDadosSumarioPrescricao(seqAtendimento, tipoEmissao);
		} catch (ApplicationBusinessException e) {
			ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			ctx.setRollbackOnly();
			ApplicationBusinessException ex = new ApplicationBusinessException(PrescricaoMedicaBeanExceptionCode.ERRO_AO_GERAR_DADOS_SUMARIO_PRESCRICAO_MEDICA);
			ex.initCause(e);
			throw ex;
		}
	}
	
	
	
	private ManterSumarioRN getManterSumarioRN() {
		return manterSumarioRN;
	}

}
