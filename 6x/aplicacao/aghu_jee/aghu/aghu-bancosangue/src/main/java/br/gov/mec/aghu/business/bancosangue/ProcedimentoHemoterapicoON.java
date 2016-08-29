package br.gov.mec.aghu.business.bancosangue;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsProcedHemoterapicoDAO;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;

@Stateless
public class ProcedimentoHemoterapicoON extends BaseBusiness {


	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final Log LOG = LogFactory.getLog(ProcedimentoHemoterapicoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AbsProcedHemoterapicoDAO absProcedHemoterapicoDAO;

	@EJB	
	private ProcedimentoHemoterapicoRN procedimentoHemoterapicoRN; 
	

	private static final long serialVersionUID = 2505990045650532185L;

	private AbsProcedHemoterapicoDAO getAbsProcedHemoterapicoDAO() {
		return absProcedHemoterapicoDAO;
	}
	
	private ProcedimentoHemoterapicoRN getProcedimentoHemoterapicoRN() {
		return procedimentoHemoterapicoRN;
	}

	/**
	 * Persitir ou atualizar o Procedimento Hemoterapico na base de dados.
	 */
	public void persistirProcedimentoHemoterapico(AbsProcedHemoterapico procedimento) throws BaseException {
		try {
			if (procedimento.getCriadoEm() == null) {
				getProcedimentoHemoterapicoRN().verificarExisteProcedimentoHemoterapico(procedimento);
				getProcedimentoHemoterapicoRN().preInsertProcedimentoHemoterapico(procedimento);
				getAbsProcedHemoterapicoDAO().persistir(procedimento);
				getProcedimentoHemoterapicoRN().posInsertProcedimentoHemoterapico(procedimento);
			} else {
				AbsProcedHemoterapico original = getAbsProcedHemoterapicoDAO().obterOriginal(procedimento);
				getProcedimentoHemoterapicoRN().preUpdateProcedimentoHemoterapico(procedimento);
				getAbsProcedHemoterapicoDAO().merge(procedimento);
				getProcedimentoHemoterapicoRN().posUpdateProcedimentoHemoterapico(procedimento, original);
			}
		} catch (IllegalAccessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		} catch (InvocationTargetException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		} catch (NoSuchMethodException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
	}

	/**
	 * Excluir o Procedimento Hemoterapico na base de dados.
	 */
	public void excluirProcedimentoHemoterapico(String codigo) throws ApplicationBusinessException {
		try {
			AbsProcedHemoterapico procedimento = absProcedHemoterapicoDAO.obterPorChavePrimaria(codigo);
			
			if (procedimento == null) {
				throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
			}
			
			getProcedimentoHemoterapicoRN().preDeleteProcedimentoHemoterapico(procedimento);
			absProcedHemoterapicoDAO.remover(procedimento);
			getProcedimentoHemoterapicoRN().posDeleteProcedimentoHemoterapico(procedimento);
		} catch (IllegalAccessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		} catch (InvocationTargetException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		} catch (NoSuchMethodException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
	}
}
