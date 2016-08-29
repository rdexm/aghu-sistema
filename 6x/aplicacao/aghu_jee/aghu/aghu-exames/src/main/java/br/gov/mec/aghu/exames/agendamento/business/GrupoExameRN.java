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
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExamesDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GrupoExameRN extends BaseBusiness {


@Inject
private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;

private static final Log LOG = LogFactory.getLog(GrupoExameRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelGrupoExamesDAO aelGrupoExamesDAO;

@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4049281627531218275L;
	
	public enum GrupoExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00343, AEL_00344, AEL_00470, ERRO_AEL_00506_DATA_MOTIVO, ERRO_AEL_00506_MOTIVO, ERRO_AEL_00506_DATA, ERRO_AEL_00506,
		AEL_00831, AEL_00503, AEL_00504, AEL_00505, AEL_00353, AEL_00370, AEL_00371, AEL_00677, AEL_00346, AEL_00369, AEL_00516,
		AEL_01036;

	}

	/**
	 * Trigger
	 * 
	 * ORADB: AELT_GEX_BRD
	 * 
	 * @param criadoEm
	 */
	public void executarBeforeDeleteGrupoExame(Date criadoEm)
			throws ApplicationBusinessException {
		
		AghParametros paramDiasPermDelAel;
		try {
			paramDiasPermDelAel = getParametroFacade()
					.obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00344);
		}
		
		Integer numDias = DateUtil.calcularDiasEntreDatas(criadoEm, DateUtil.truncaData(new Date()));
		
		if (numDias > paramDiasPermDelAel.getVlrNumerico().intValue()) {
			throw new ApplicationBusinessException(GrupoExameRNExceptionCode.AEL_00343);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_GEU_BRI
	 * 
	 * @param aelGrupoExameUnidExameId
	 */
	public void executarBeforeInsertGrupoExameUnidExame(AelGrupoExameUnidExame grupoExameUnidExame)
			throws ApplicationBusinessException {
		
		verificarUnidadeFuncionalExecutaExamesAtivo(grupoExameUnidExame.getUnfExecutaExame());
		verificarGrupoExameAtivo(grupoExameUnidExame.getId());
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: RN_GEUP_UF_EXEC_EXME
	 * 
	 * @param aelGrupoExameUnidExameId
	 */
	public void verificarUnidadeFuncionalExecutaExamesAtivo(AelUnfExecutaExames unfExecutaExame) throws ApplicationBusinessException {
		if(unfExecutaExame == null) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00470);
		} else {
			if(!DominioSituacao.A.equals(unfExecutaExame.getIndSituacao())) {
				if(unfExecutaExame.getDthrReativaTemp() != null && unfExecutaExame.getMotivoDesativacao() != null) {
					throw new ApplicationBusinessException(
							GrupoExameRNExceptionCode.ERRO_AEL_00506_DATA_MOTIVO,
							unfExecutaExame.getNomeUsualMaterialUnidade(),
							DateUtil.obterDataFormatada(
									unfExecutaExame.getDthrReativaTemp(),
									"dd/MM/yyyy"), unfExecutaExame
									.getMotivoDesativacao());
				} else if (unfExecutaExame.getDthrReativaTemp() == null
						&& unfExecutaExame.getMotivoDesativacao() != null) {
					throw new ApplicationBusinessException(
							GrupoExameRNExceptionCode.ERRO_AEL_00506_MOTIVO,
							unfExecutaExame.getNomeUsualMaterialUnidade(),
							unfExecutaExame.getMotivoDesativacao());
				} else if (unfExecutaExame.getDthrReativaTemp() != null
						&& unfExecutaExame.getMotivoDesativacao() == null) {
					throw new ApplicationBusinessException(
							GrupoExameRNExceptionCode.ERRO_AEL_00506_DATA,
							unfExecutaExame.getNomeUsualMaterialUnidade(),
							DateUtil.obterDataFormatada(
									unfExecutaExame.getDthrReativaTemp(),
									"dd/MM/yyyy"));
				} else {
					throw new ApplicationBusinessException(
							GrupoExameRNExceptionCode.ERRO_AEL_00506,
							unfExecutaExame.getNomeUsualMaterialUnidade());
				}

			}
			if(!(DominioSimNaoRestritoAreaExecutora.S.equals(unfExecutaExame.getIndAgendamPrevioInt())
						|| DominioSimNaoRestritoAreaExecutora.R.equals(unfExecutaExame.getIndAgendamPrevioInt()))
					&& !(DominioSimNaoRestritoAreaExecutora.S.equals(unfExecutaExame.getIndAgendamPrevioNaoInt())
						|| DominioSimNaoRestritoAreaExecutora.R.equals(unfExecutaExame.getIndAgendamPrevioNaoInt()))) {
				throw new ApplicationBusinessException(
						GrupoExameRNExceptionCode.AEL_00831);
			}
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: RN_GEUP_GRUPO_EXAME
	 * 
	 * @param aelGrupoExameUnidExameId
	 */
	public void verificarGrupoExameAtivo(AelGrupoExameUnidExameId aelGrupoExameUnidExameId) throws ApplicationBusinessException {
		AelGrupoExames grupoExame = getAelGrupoExamesDAO().obterPorChavePrimaria(aelGrupoExameUnidExameId.getGexSeq());
		if(grupoExame == null) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00503);
		}
		if(!DominioSituacao.A.equals(grupoExame.getSituacao())) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00504);
		}
		
		Boolean unfGrupoExameUnidPossuiCaracteristica = getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(
				aelGrupoExameUnidExameId.getUfeUnfSeq(), ConstanteAghCaractUnidFuncionais.AREA_FECHADA);
		
		Boolean gexUnidadeFuncionalPossuiCaracteristica = getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(
				grupoExame.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.AREA_FECHADA); 
		
		if((unfGrupoExameUnidPossuiCaracteristica || gexUnidadeFuncionalPossuiCaracteristica)
				&& (aelGrupoExameUnidExameId.getUfeUnfSeq().compareTo(grupoExame.getUnidadeFuncional().getSeq()) != 0)) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00505);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_GEU_BRU
	 * 
	 * @param aelGrupoExameUnidExameNew, descricaoExame
	 */
	public void executarBeforeUpdateGrupoExameUnidExame(AelGrupoExameUnidExame aelGrupoExameUnidExameNew)
			throws ApplicationBusinessException {
		if(DominioSituacao.A.equals(aelGrupoExameUnidExameNew.getSituacao())) {
			//Foi feita chamada a procedure AELT_GEU_BRI pois ela é idêntica à RN_GEUP_VER_ATIVO
			verificarUnidadeFuncionalExecutaExamesAtivo(aelGrupoExameUnidExameNew.getUnfExecutaExame());
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_GEX_BRI
	 * 
	 * @param aelGrupoExamesNew
	 */
	public void executarBeforeInsertGrupoExame(AelGrupoExames aelGrupoExamesNew)
			throws ApplicationBusinessException {
		
		if (aelGrupoExamesNew.getServidor() == null) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00353);
		}
		
		AghUnidadesFuncionais unidadeFuncional = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(
				aelGrupoExamesNew.getUnidadeFuncional().getSeq());
		
		if(unidadeFuncional == null) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00370);
		} else {
			verificarUnidFuncAtivaEUnidExecExamesOuColeta(unidadeFuncional);
			aelGrupoExamesNew.setCriadoEm(DateUtil.truncaData(new Date()));
		}
	}
	
	/**
	 * Procedure
	 * 
	 * RN_GEX003 - Verifica se a unidade funcional está ativa
	 * e se é unidade executora de exames ou se é unidade de coleta
	 * 
	 * ORADB: RN_GEXP_VER_UNID_FUN
	 * 
	 * @param unidadeFuncional
	 */
	public void verificarUnidFuncAtivaEUnidExecExamesOuColeta(AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		if(!DominioSituacao.A.equals(unidadeFuncional.getIndSitUnidFunc())) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00371);
		}
		
		Boolean possuiCaracteristica = false;
		for(AghCaractUnidFuncionais caract : unidadeFuncional.getCaracteristicas()) {
			if(ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES.equals(caract.getId().getCaracteristica())
					|| ConstanteAghCaractUnidFuncionais.UNID_COLETA.equals(caract.getId().getCaracteristica())) {
				possuiCaracteristica = true;
				break;
			}
		}
		if(!possuiCaracteristica) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00677);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_GEX_BRU
	 * 
	 * @param aelGrupoExameNew, aelGrupoExameOld
	 */
	public void executarBeforeUpdateGrupoExame(AelGrupoExames aelGrupoExameNew, AelGrupoExames aelGrupoExameOld)
			throws ApplicationBusinessException {
		if(!aelGrupoExameOld.getDescricao().equals(aelGrupoExameNew.getDescricao())) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00346);
		}
		if(!aelGrupoExameOld.getCriadoEm().equals(aelGrupoExameNew.getCriadoEm())
				|| !aelGrupoExameOld.getServidor().equals(aelGrupoExameNew.getServidor())) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00369);
		}
		if(!aelGrupoExameOld.getUnidadeFuncional().equals(aelGrupoExameNew.getUnidadeFuncional())) {
			verificarUnidFuncAtivaEUnidExecExamesOuColeta(aelGrupoExameNew.getUnidadeFuncional());
		}
		if(aelGrupoExameOld.getSituacao().equals(aelGrupoExameNew.getSituacao())
				&& DominioSituacao.I.equals(aelGrupoExameNew.getSituacao())) {
			verificarGradesAtivasGrupoExame(aelGrupoExameNew);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * RN_GEX004 - Ao inativar um grupo de exames, não deve haver grades ativas
	 * que referenciem este grupo de exames.
	 * 
	 * ORADB: RN_GEXP_VER_GRADE_AT
	 * 
	 * @param aelGrupoExame
	 */
	public void verificarGradesAtivasGrupoExame(AelGrupoExames grupoExame) throws ApplicationBusinessException {
		List<AelGradeAgendaExame> listaGrade = getAelGradeAgendaExameDAO().pesquisarGradeExamePorGrupoExame(grupoExame);
		if(listaGrade == null) {
			throw new ApplicationBusinessException(
					GrupoExameRNExceptionCode.AEL_00516);
		} else {
			for(AelGradeAgendaExame grade : listaGrade) {
				if(DominioSituacao.A.equals(grade.getSituacao())) {
					throw new ApplicationBusinessException(
							GrupoExameRNExceptionCode.AEL_01036);
				}
			}
		}
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO(){
		return aelGradeAgendaExameDAO;
	}
	
	protected AelGrupoExamesDAO getAelGrupoExamesDAO(){
		return aelGrupoExamesDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
}
