package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.vo.RnCpecAtuEncCompVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatCompetenciaId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * <p>
 * TODO Implementar metodos <br/>
 * Linhas: 204 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 3 <br/>
 * Consultas: 1 tabelas <br/>
 * Alteracoes: 10 tabelas <br/>
 * Metodos: 5 <br/>
 * Metodos externos: 0 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_CPE_RN</code>
 * </p>
 * 
 * @author gandriotti
 * 
 */
@Stateless
public class FaturamentoFatkCpeRN extends BaseBusiness implements Serializable {


@EJB
private FatCompetenciaON fatCompetenciaON;

@EJB
private FatCompetenciaRN fatCompetenciaRN;

private static final Log LOG = LogFactory.getLog(FaturamentoFatkCpeRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatCompetenciaDAO fatCompetenciaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1297399594595231519L;

	/**
	 * <p>
	 * TODO <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>NOK</b><br/>
	 * Linhas: 68 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 1 <br/>
	 * Consultas: 0 tabelas <br/>
	 * Alteracoes: 4 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_CPEC_ATU_ENC_COMP</code>
	 * </p>
	 * <p>
	 * <b>INSERT:</b> {@link FatCompetencia} <br/>
	 * </p>
	 * <p>
	 * <b>UPDATE:</b> {@link FatCompetencia} <br/>
	 * </p>
	 * 
	 * @param pModulo
	 * @param pDthrInicio
	 * @param pMes
	 * @param pAno
	 * @param pDthrFim
	 * @param pNewMes
	 * @param pNewAno
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FatCompetencia
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public RnCpecAtuEncCompVO rnCpecAtuEncComp(String pModulo, Date pDthrInicio, Integer pMes, Integer pAno, Date pDthrFim, Integer pNewMes,
			Integer pNewAno) throws ApplicationBusinessException {
		RnCpecAtuEncCompVO retorno = new RnCpecAtuEncCompVO(pDthrFim, pDthrInicio, pNewMes, pNewAno, Boolean.FALSE);

		final FatCompetencia fatCompetenciaUpdate = getFatCompetenciaDAO().obterCompetenciaModuloMesAno(DominioModuloCompetencia.valueOf(pModulo),
				pMes, pAno);
		final FatCompetencia fatCompetenciaInsert = new FatCompetencia();
		fatCompetenciaInsert.setId(new FatCompetenciaId());

		try {
			FatCompetenciaRN competenciaRN = getFatCompetenciaRN();
			retorno.setResult(Boolean.TRUE);
			final Date vDthrFim = DateUtil.hojeSeNull(pDthrFim);

			// calcula proximo mes/ano
			if (CoreUtil.igual(pMes, 12)) {
				fatCompetenciaInsert.getId().setMes(1);
				fatCompetenciaInsert.getId().setAno(pAno + 1);
			} else {
				fatCompetenciaInsert.getId().setMes(pMes + 1);
				fatCompetenciaInsert.getId().setAno(pAno);
			}

			// Fecha competencia
			fatCompetenciaUpdate.setIndSituacao(DominioSituacaoCompetencia.M);
			fatCompetenciaUpdate.setDtHrFim(vDthrFim);
			competenciaRN.atualizarFatCompetencia(fatCompetenciaUpdate);
			retorno.setpDthrFim(vDthrFim);

			// Abre nova competencia
			fatCompetenciaInsert.getId().setModulo(DominioModuloCompetencia.valueOf(pModulo));
			fatCompetenciaInsert.getId().setDtHrInicio(vDthrFim);
			fatCompetenciaInsert.setIndFaturado(Boolean.FALSE);
			fatCompetenciaInsert.setIndSituacao(DominioSituacaoCompetencia.A);
			competenciaRN.inserirFatCompetencia(fatCompetenciaInsert, true);

			retorno.setpDthrInicio(vDthrFim);
			retorno.setpNewMes(fatCompetenciaInsert.getId().getMes());
			retorno.setpNewAno(fatCompetenciaInsert.getId().getAno());
			retorno.setResult(Boolean.TRUE);

			// Erro ao encerrar competência e criar nova competência aberta.
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			retorno.setResult(Boolean.FALSE);
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00495);
		}

		return retorno;
	}

	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}

	protected FatCompetenciaRN getFatCompetenciaRN() {
		return fatCompetenciaRN;
	}

	/**
	 * <p>
	 * TODO <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>NOK</b><br/>
	 * Linhas: 14 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 0 tabelas <br/>
	 * Alteracoes: 0 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_CPEP_VER_DATAS</code>
	 * </p>
	 * 
	 * @param pDtHrInicio
	 * @param pDtHrFim
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FaturamentoExceptionCode#FAT_00403
	 */
	public Boolean rnCpepVerDatas(final Date pDtHrInicio, final Date pDtHrFim) throws ApplicationBusinessException {
		if (pDtHrFim != null && DateValidator.validaDataMenor(pDtHrFim, pDtHrInicio)) {
			throw new ApplicationBusinessException(FaturamentoExceptionCode.FAT_00403);
		}

		return Boolean.FALSE;
	}

	/**
	 * <p>
	 * <br/>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 27 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 1 <br/>
	 * Consultas: 0 tabelas <br/>
	 * Alteracoes: 2 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_CPEC_ATU_FATURADO</code>
	 * </p>
	 * <p>
	 * <b>UPDATE:</b> {@link FatCompetencia} <br/>
	 * </p>
	 * 
	 * @param competencia
	 * @return
	 * @see FatCompetencia
	 */
	public boolean rnCpecAtuFaturado(FatCompetencia competencia) {
		try {
			FatCompetenciaDAO fatCompetenciaDAO = getFatCompetenciaDAO();
			FatCompetenciaON fatCompetenciaON = getFatCompetenciaON();
			List<FatCompetencia> competencias = fatCompetenciaDAO.obterCompetenciaModuloMesAnoDtHoraInicioSemHoraEmManutencao(competencia.getId()
					.getModulo(), competencia.getId().getMes(), competencia.getId().getAno(), competencia.getId().getDtHrInicio());

			if (competencias.isEmpty()) {
				return false;
			}
			
			for (FatCompetencia fatCompetencia : competencias) {
				fatCompetencia.setIndFaturado(true);
				fatCompetencia.setIndSituacao(DominioSituacaoCompetencia.P);
				fatCompetenciaON.atualizarFatCompetencia(fatCompetencia);
			}
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			return false;
		}
		return true;
	}

	private FatCompetenciaON getFatCompetenciaON() {
		return fatCompetenciaON;
	}

	/**
	 * ORADB Function <code>RN_CPEC_VER_SITUACAO</code>
	 * 
	 * @param pModulo
	 * @param pDthrInicio
	 * @param pMes
	 * @param pAno
	 * @return
	 */
	public DominioSituacaoCompetencia rnCpecVerSituacao(DominioModuloCompetencia pModulo, Date pDthrInicio, Integer pMes, Integer pAno) {

		FatCompetencia competencia = getFatCompetenciaDAO().obterPorChavePrimaria(new FatCompetenciaId(pModulo, pMes, pAno, pDthrInicio));

		if (competencia == null) {
			return null;
		} else {
			return competencia.getIndSituacao();
		}
	}

}
