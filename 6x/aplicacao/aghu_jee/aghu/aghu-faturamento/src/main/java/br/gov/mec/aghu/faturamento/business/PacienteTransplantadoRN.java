package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatPacienteTransplantesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ORADB PACKAGE FATK_PTR_RN
 * 
 * @author aghu
 * 
 */
@Stateless
public class PacienteTransplantadoRN extends BaseBusiness implements Serializable {

private static final Log LOG = LogFactory.getLog(PacienteTransplantadoRN.class);

@Override
@Deprecated
public Log getLogger() {
return LOG;
}


@Inject
private FatPacienteTransplantesDAO fatPacienteTransplantesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5660987113829567287L;

	/**
	 * ORADB PROCEDURE RN_PTRP_VER_DATAS
	 * 
	 * @param dataInscricao
	 * @param dataTransplante
	 * @throws BaseException
	 */
	public void verificarDatasPacienteTransplante(final Date dataInscricao, final Date dataTransplante) throws BaseException {
		if (DateUtil.validaDataMaior(dataInscricao, dataTransplante)) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00745);
		}
	}

	/**
	 * ORADB FUNCTION RN_PTRC_DATA_ULT_TTR
	 * 
	 * @param pacCodigo
	 * @throws BaseException
	 */
	public Date obterDataUltimoTransplante(final Integer pacCodigo) {
		return this.getFatPacienteTransplantesDAO().obterDataUltimoTransplante(pacCodigo); // CURSOR C_DATA_ULTIMO_TRANSP
	}

	/**
	 * ORADB FUNCTION RN_PTRC_TIPO_ULT_TTR
	 * 
	 * @param pacCodigo
	 * @throws BaseException
	 */
	public String obterTipoUltimoTransplante(final Integer pacCodigo) {
		String tipoTransplanteCodigo = null;
		List<String> listaTiposUltimosTransplantes = this.getFatPacienteTransplantesDAO().pesquisarTiposUltimosTransplantes(pacCodigo); // CURSOR C_TIPO_ULTIMO_TRANSP
		if (!listaTiposUltimosTransplantes.isEmpty()) {
			tipoTransplanteCodigo = listaTiposUltimosTransplantes.get(0);
		}
		return tipoTransplanteCodigo;
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	private FatPacienteTransplantesDAO getFatPacienteTransplantesDAO() {
		return fatPacienteTransplantesDAO;
	}

}
