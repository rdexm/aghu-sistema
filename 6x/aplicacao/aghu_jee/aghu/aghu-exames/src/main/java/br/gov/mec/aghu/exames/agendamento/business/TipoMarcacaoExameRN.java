package br.gov.mec.aghu.exames.agendamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioGradeExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoMarcacaoExameDAO;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioGradeExame;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de negócio migradas
 * de triggers e procedures da package AELK_AEL_RN
 * relacionadas à tipo de marcação de exame.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class TipoMarcacaoExameRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(TipoMarcacaoExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelHorarioGradeExameDAO aelHorarioGradeExameDAO;
	
	@Inject
	private AelTipoMarcacaoExameDAO aelTipoMarcacaoExameDAO;
	
	@Inject
	private AelHorarioExameDispDAO aelHorarioExameDispDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3494090155839477679L;
	
	public enum TipoMarcacaoExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00346, AEL_00369, AEL_00343, ERRO_TIPO_MARCACAO_EXAME_BUSCA_PARAM_DIAS_PERM_DEL_AEL, MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, AEL_00353;
	}
	
	public void persistirTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExameNew)
			throws ApplicationBusinessException {
		if (tipoMarcacaoExameNew.getSeq() == null) {
			inserirTipoMarcacaoExame(tipoMarcacaoExameNew);
		} else {
			atualizarTipoMarcacaoExame(tipoMarcacaoExameNew);
		}
	}
	
	public void inserirTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExameNew) 
			throws ApplicationBusinessException {
		executarBeforeInsertTipoMarcacaoExame(tipoMarcacaoExameNew);
		getAelTipoMarcacaoExameDAO().persistir(tipoMarcacaoExameNew);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_TME_BRI
	 * 
	 * @param tipoMarcacaoExameNew
	 * @throws ApplicationBusinessException 
	 */
	public void executarBeforeInsertTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExameNew)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Servidor que está criando um tipo de marcação.
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(TipoMarcacaoExameRNExceptionCode.AEL_00353);
		}
		
		tipoMarcacaoExameNew.setServidor(servidorLogado);
		tipoMarcacaoExameNew.setCriadoEm(new Date());
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_TME_BRU
	 * 
	 * @param tipoMarcacaoExameOld
	 * @param tipoMarcacaoExameNew  
	 */
	public void executarBeforeUpdateTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExameOld, 
			AelTipoMarcacaoExame tipoMarcacaoExameNew) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(tipoMarcacaoExameOld.getDescricao(), tipoMarcacaoExameNew.getDescricao())) {
			// A descrição não pode ser alterada
			throw new ApplicationBusinessException(TipoMarcacaoExameRNExceptionCode.AEL_00346); 
		}
		
		if (CoreUtil.modificados(tipoMarcacaoExameOld.getCriadoEm(), tipoMarcacaoExameNew.getCriadoEm()) 
				|| CoreUtil.modificados(tipoMarcacaoExameOld.getServidor(), tipoMarcacaoExameNew.getServidor())) {
			// Tentativa de alterar campos que não podem ser alterados
			throw new ApplicationBusinessException(TipoMarcacaoExameRNExceptionCode.AEL_00369);
		}
	}
	
	
	public void atualizarTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExameNew) throws ApplicationBusinessException {
		AelTipoMarcacaoExameDAO aelTipoMarcacaoExameDAO = getAelTipoMarcacaoExameDAO();		
		AelTipoMarcacaoExame tipoMarcacaoExameOld = aelTipoMarcacaoExameDAO.obterOriginal(tipoMarcacaoExameNew);
		tipoMarcacaoExameNew = aelTipoMarcacaoExameDAO.merge(tipoMarcacaoExameNew);
		executarBeforeUpdateTipoMarcacaoExame(tipoMarcacaoExameOld, tipoMarcacaoExameNew);		
		aelTipoMarcacaoExameDAO.atualizar(tipoMarcacaoExameNew);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_TME_BRD
	 * 
	 * @param tipoMarcacaoExameOld
	 */
	public void executarBeforeDeleteTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExameOld) 
			throws ApplicationBusinessException {
		verificarDelecao(tipoMarcacaoExameOld.getCriadoEm());
		
		verificarDependentesGradeExame(tipoMarcacaoExameOld);
		verificarDependentesExameDisp(tipoMarcacaoExameOld);
	}
	
	protected void verificarDependentesGradeExame(AelTipoMarcacaoExame tipoMarcacaoExameOld) throws ApplicationBusinessException {
		List<AelHorarioGradeExame> listAelHorarioGradeExame =  getAelHorarioGradeExameDAO().pesquisaPorTipoMarcacaoExame(tipoMarcacaoExameOld);
		if (listAelHorarioGradeExame != null && !listAelHorarioGradeExame.isEmpty()) {
			throw new ApplicationBusinessException(TipoMarcacaoExameRNExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, "AEL_HORARIO_GRADE_EXAMES");
		}
	}
	
	protected void verificarDependentesExameDisp(AelTipoMarcacaoExame tipoMarcacaoExameOld) throws ApplicationBusinessException {
		List<AelHorarioExameDisp> listAelHorarioExameDisp =  getAelHorarioExameDispDAO().pesquisaPorTipoMarcacaoExame(tipoMarcacaoExameOld);
		if (listAelHorarioExameDisp != null && !listAelHorarioExameDisp.isEmpty()) {
			throw new ApplicationBusinessException(TipoMarcacaoExameRNExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, "AEL_HORARIO_EXAME_DISPS");
		}
	}

	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}

	protected AelHorarioGradeExameDAO getAelHorarioGradeExameDAO() {
		return aelHorarioGradeExameDAO;
	}

	/**
	 * Procedure
	 * 
	 * ORADB: AELK_AEL_RN.RN_AELP_VER_DEL
	 * 
	 * @param dataCriadoEm
	 */
	public void verificarDelecao(Date dataCriadoEm) throws ApplicationBusinessException {
		AghParametros paramDiasPermDelAel = null;
		try {
			paramDiasPermDelAel = getParametroFacade()
					.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(
					TipoMarcacaoExameRNExceptionCode.ERRO_TIPO_MARCACAO_EXAME_BUSCA_PARAM_DIAS_PERM_DEL_AEL);
		}
		
		Integer numDias = DateUtil.calcularDiasEntreDatas(dataCriadoEm, DateUtil.truncaData(new Date()));
		
		if (numDias > paramDiasPermDelAel.getVlrNumerico().intValue()) {
			throw new ApplicationBusinessException(TipoMarcacaoExameRNExceptionCode.AEL_00343);
		}
	}
	
	public void excluirTipoMarcacaoExame(Short seq) throws ApplicationBusinessException {
		AelTipoMarcacaoExame tipoMarcacaoExameOld = getAelTipoMarcacaoExameDAO().obterPorChavePrimaria(seq);
		
		if (tipoMarcacaoExameOld == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		executarBeforeDeleteTipoMarcacaoExame(tipoMarcacaoExameOld);
		getAelTipoMarcacaoExameDAO().remover(tipoMarcacaoExameOld);
	}
	
	public void refreshTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExame) {
		this.refresh(tipoMarcacaoExame);
	}
	
	protected AelTipoMarcacaoExameDAO getAelTipoMarcacaoExameDAO() {
		return aelTipoMarcacaoExameDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
