package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MpmPrescricaoMdtoRN  extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(MpmPrescricaoMdtoRN.class);

	public enum MpmPrescricaoMdtoRNExceptionCode implements BusinessExceptionCode {
		PARAMETROS_INCONSISTENTES, DATA_FIM_INVALIDA, FREQUENCIA_INVALIDA, GOTEJO_INVALIDO, QUANTIDADE_HORAS_CORRER_INVALIDA, DURACAO_TRATAMENTO_SOLICITADO_INVALIDO;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	@EJB
	private PrescricaoMedicamentoRN prescricaoMedicamentoRN;

	private static final long serialVersionUID = -2776477397713835620L;

	public void atualizarMpmPrescricaoMdto(Integer pmdAtdSeq, Long pmdSeq, Short duracaoTratSolicitado) throws ApplicationBusinessException{
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId(pmdAtdSeq, pmdSeq);
		MpmPrescricaoMdto prescricaoMdto = this.getMpmPrescricaoMdtoDAO().obterPorChavePrimaria(id);
		
		prescricaoMdto.setDuracaoTratSolicitado(duracaoTratSolicitado);
		
		//#52026 - Se informado duracao trat solicitado, atualizar dthr inicio tratamento com dthr_inicio
		if(prescricaoMdto.getDuracaoTratSolicitado() != null) {
			if(prescricaoMdto.getDthrInicioTratamento() == null) {
				prescricaoMdto.setDthrInicioTratamento(prescricaoMedicamentoRN.atualizaInicioTratamento(prescricaoMdto.getDthrInicio(), prescricaoMdto.getHoraInicioAdministracao()));	
			}
			else {
				prescricaoMdto.setDthrInicioTratamento(prescricaoMedicamentoRN.atualizaInicioTratamento(prescricaoMdto.getDthrInicio(), null));
			}
		}

		prePersistir(prescricaoMdto);
		prePersistir2(prescricaoMdto);

		this.getMpmPrescricaoMdtoDAO().merge(prescricaoMdto);
	}

	private void prePersistir(MpmPrescricaoMdto prescricaoMdto) throws ApplicationBusinessException {
		if (!((prescricaoMdto.getIndItemRecomendadoAlta() && prescricaoMdto.getIndPendente().equals(
				DominioIndPendenteItemPrescricao.N)) || (!prescricaoMdto.getIndItemRecomendadoAlta()))) {
			throw new ApplicationBusinessException(
					MpmPrescricaoMdtoRNExceptionCode.PARAMETROS_INCONSISTENTES);
		}

		if (!(prescricaoMdto.getDthrFim() == null || (prescricaoMdto.getDthrFim() != null && DateUtil
				.validaDataMaiorIgual(prescricaoMdto.getDthrFim(), prescricaoMdto.getDthrInicio())))) {
			throw new ApplicationBusinessException(
					MpmPrescricaoMdtoRNExceptionCode.DATA_FIM_INVALIDA);
		}

		if (!(prescricaoMdto.getFrequencia() == null || (prescricaoMdto.getFrequencia() != null && prescricaoMdto.getFrequencia() > 0))) {
			throw new ApplicationBusinessException(
					MpmPrescricaoMdtoRNExceptionCode.FREQUENCIA_INVALIDA);
		}
	}

	private void prePersistir2(MpmPrescricaoMdto prescricaoMdto) throws ApplicationBusinessException {
		if (!(prescricaoMdto.getGotejo() == null || (prescricaoMdto.getGotejo() != null && prescricaoMdto.getGotejo()
				.doubleValue() > 0))) {
			throw new ApplicationBusinessException(
					MpmPrescricaoMdtoRNExceptionCode.GOTEJO_INVALIDO);
		}

		if (!(prescricaoMdto.getQtdeHorasCorrer() == null || (prescricaoMdto.getQtdeHorasCorrer() != null && prescricaoMdto.getQtdeHorasCorrer() > 0))) {
			throw new ApplicationBusinessException(
					MpmPrescricaoMdtoRNExceptionCode.QUANTIDADE_HORAS_CORRER_INVALIDA);
		}

		if (!(prescricaoMdto.getDuracaoTratSolicitado() == null || (prescricaoMdto.getDuracaoTratSolicitado() != null && prescricaoMdto.getDuracaoTratSolicitado() > 0))) {
			throw new ApplicationBusinessException(
					MpmPrescricaoMdtoRNExceptionCode.DURACAO_TRATAMENTO_SOLICITADO_INVALIDO);
		}
	}

	public MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO() {
		return mpmPrescricaoMdtoDAO;
	}
	
}
