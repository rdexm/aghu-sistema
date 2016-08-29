package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.exames.dao.AelSalasExecutorasExamesDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExamesId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ManterSalaExecutoraExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterSalaExecutoraExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;
	
	@Inject
	private AelSalasExecutorasExamesDAO aelSalasExecutorasExamesDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = -2945520744875808742L;

	public enum ManterSalaExecutoraExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00369,AEL_00370,AEL_00371, AEL_00675, AEL_00343, AEL_00515, ERRO_EXISTEM_DEPENDENCIAS_TABELA;
		
		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	public void atualizar (AelSalasExecutorasExames sala) throws ApplicationBusinessException{
		final AelSalasExecutorasExames salaOriginal = this.getAelSalasExecutorasExamesDAO().obterOriginal(sala);

		preAtualizar(sala, salaOriginal);
		aelSalasExecutorasExamesDAO.merge(sala);
		posPersistir(sala);
	}

	/**
	 * @ORADB AELT_SEE_BRU
	 */
	private void preAtualizar(AelSalasExecutorasExames sala,AelSalasExecutorasExames salaOriginal) throws ApplicationBusinessException{
		if (CoreUtil.modificados(sala.getId().getUnfSeq(), salaOriginal.getId().getUnfSeq())){
			this.verificarUnidadeFuncional(sala.getUnidadeFuncional());
		}		
		if (CoreUtil.modificados(sala.getCriadoEm(), salaOriginal.getCriadoEm())
				|| CoreUtil.modificados(sala.getServidor(), salaOriginal.getServidor())){
			ManterSalaExecutoraExameRNExceptionCode.AEL_00369.throwException();
		}		
		sala.setAlteradoEm(new Date());
	}
	
	/**
	 * @ORADB AELK_SEE_RN.RN_SEEP_VER_UNID_FUN
	 */
	private void verificarUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException{
		
		if (unidadeFuncional==null){
			ManterSalaExecutoraExameRNExceptionCode.AEL_00370.throwException();//Unidade Funcional não encontrada
		}else if(!unidadeFuncional.getIndSitUnidFunc().equals(DominioSituacao.A)){
			ManterSalaExecutoraExameRNExceptionCode.AEL_00371.throwException();//Unidade Funcional está inativa
		}
		
		if (!getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES) 
				&& !getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA)){
			ManterSalaExecutoraExameRNExceptionCode.AEL_00675.throwException();//Unidade funcional não é unidade executora ou de coleta
		}
	}
	
	public void inserir (AelSalasExecutorasExames sala) throws ApplicationBusinessException{
		this.posPersistir(sala); // Validação alterada para o início, evita persistir antes de verificar se o objeto já existe.
		this.preInserir(sala);
		this.getAelSalasExecutorasExamesDAO().persistir(sala);
		this.getAelSalasExecutorasExamesDAO().flush();
	}
	
	/**
	 * @ORADB aelt_see_bri
	 */
	private void preInserir(AelSalasExecutorasExames sala) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		sala.setServidor(servidorLogado);
		sala.setCriadoEm(new Date());
		sala.setAlteradoEm(new Date());
		this.verificarUnidadeFuncional(sala.getUnidadeFuncional());
	}
	
	public void excluir(AelSalasExecutorasExamesId id) throws ApplicationBusinessException{
		AelSalasExecutorasExames sala = aelSalasExecutorasExamesDAO.obterPorChavePrimaria(id);
		if (sala == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		preExcluir(sala);
		aelSalasExecutorasExamesDAO.remover(sala);
	}
	
	/**
	 * @ORADB aelt_see_brd e aelk_see_rn.RN_SEEP_VER_DELECAO e aelk_ael_rn.rn_aelp_ver_del
	*/
	private void preExcluir(AelSalasExecutorasExames salaOriginal) throws ApplicationBusinessException{
		AghParametros parametro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AEL);
		Float qtdDias = CoreUtil.diferencaEntreDatasEmDias(new Date(), salaOriginal.getCriadoEm());
		if (qtdDias > parametro.getVlrNumerico().floatValue()){
			ManterSalaExecutoraExameRNExceptionCode.AEL_00343.throwException();
		}
		if(this.getAelGradeAgendaExameDAO().contarAelGradeAgendaExamePorAelSalasExecutorasExames(salaOriginal) > 0) {
			ManterSalaExecutoraExameRNExceptionCode.ERRO_EXISTEM_DEPENDENCIAS_TABELA.throwException("AEL_GRADE_AGENDA_EXAMES");
		}
	}
	
	/**
	 * @ORADB aelk_see_rn.rn_seep_ver_numero 
	 * RN_SEE001 - Não deve permitir o mesmo número de sala para a mesma unidade funcional 
	 */	
	private void posPersistir(AelSalasExecutorasExames sala) throws ApplicationBusinessException{
		if (getAelSalasExecutorasExamesDAO().obterPorUnidadeFuncionalSeqpNumeroSala(sala.getId(), sala.getNumero())!=null){
			ManterSalaExecutoraExameRNExceptionCode.AEL_00515.throwException(sala.getNumero()); //Sala #1 já está cadastrado para esta unidade funcional
		}
	}
	
	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected AelSalasExecutorasExamesDAO getAelSalasExecutorasExamesDAO(){
		return aelSalasExecutorasExamesDAO;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO(){
		return aelGradeAgendaExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
