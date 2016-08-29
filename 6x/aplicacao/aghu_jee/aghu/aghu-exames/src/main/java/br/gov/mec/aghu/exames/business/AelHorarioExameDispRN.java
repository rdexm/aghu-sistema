package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelHorarioExameDispRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelHorarioExameDispRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	
	@Inject
	private AelHorarioExameDispDAO aelHorarioExameDispDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3779402943242593367L;

	public enum AelHorarioExameDispRNExceptionCode implements BusinessExceptionCode {

		AEL_00369,
		AEL_00517,
		AEL_00522,
		AEL_00523,
		AEL_00521,
		AEL_00857,
		AEL_00862,
		AEL_00863,
		AEL_00864;

	}

	/**
	 * Insere objeto AelHorarioExameDisp SEM FLUSH
	 * 
	 * @param {AelHorarioExameDisp} amostra
	 * @return {AelHorarioExameDisp}
	 * @throws BaseException
	 */
	public AelHorarioExameDisp inserirSemFlush(AelHorarioExameDisp horarioExameDisp) throws BaseException {

		this.beforeInsertAelHorarioExameDisp(horarioExameDisp);
		this.getAelHorarioExameDispDAO().persistir(horarioExameDisp);

		return horarioExameDisp;

	}
	
	/**
	 * @ORADB AELT_HED_BRI
	 * 
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * @throws ApplicationBusinessException 
	 * @throws BaseException 
	 */
	private void beforeInsertAelHorarioExameDisp(AelHorarioExameDisp horarioExameDisp) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		horarioExameDisp.setServidor(servidorLogado);
		horarioExameDisp.setServidorAlterado(servidorLogado);
		
		this.verificarGrade(horarioExameDisp);
		this.verificarTipoMarcacao(horarioExameDisp);//Verifica se o tipo marcação de exames está ativo.
		this.verificaSituacaoHorarioExameDisponivel(horarioExameDisp);//Não permitir situação <> G se horário extra = S.
		this.verificarSituacao(horarioExameDisp);//A data do horário não pode ser posterior à data da última geração da disponibilidade da grade. Caso esta não exista, não permitir.
		this.verificaUltimaDataGeracaoGrade(horarioExameDisp);//se a coluna ind_exclusivo_executor for nula então atribuir o valor N.

		if (horarioExameDisp.getExclusivoExecutor() == null){
			horarioExameDisp.setExclusivoExecutor(Boolean.FALSE);
		}
		Date dataCorrente = new Date();
		
		horarioExameDisp.setCriadoEm(dataCorrente);
		horarioExameDisp.setAlteradoEm(dataCorrente);
	}
	
	/**
	 * ORADB aelk_hed_rn.RN_HEDP_VER_SITUACAO
	 * 
	 * Descrição: Somente quando for horário extra, permite criar horário em situação Liberada ou Gerada. 
	 * 
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * @throws ApplicationBusinessException 
	 */
	private void verificaSituacaoHorarioExameDisponivel (AelHorarioExameDisp horarioExameDisp) throws ApplicationBusinessException{
		
		if (Boolean.TRUE.equals(horarioExameDisp.getIndHorarioExtra())) {
			if (!(horarioExameDisp.getSituacaoHorario().equals(DominioSituacaoHorario.G)
					|| horarioExameDisp.getSituacaoHorario().equals(DominioSituacaoHorario.L))){
				throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00521);
				// Horário extra deve iniciar na situação G - gerado ou L - liberado
			}
		} else if (!horarioExameDisp.getSituacaoHorario().equals(DominioSituacaoHorario.G)){
			throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00857);
			//Horário não extra deve iniciar na situação G - gerado
		}
	}
	
	/**
	 * ORADB aelk_hed_rn.RN_HEDP_VER_ULT_DT
	 * 
	 * Não permite que a data de geração do horário extra seja superior à data da última geração da disponibilidade da grade. 
	 * 
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * @throws ApplicationBusinessException 
	 */
	public void verificaUltimaDataGeracaoGrade(AelHorarioExameDisp horarioExameDisp) throws ApplicationBusinessException{
		AelGradeAgendaExame grade = horarioExameDisp.getGradeAgendaExame();
		//#53158
		if (Boolean.TRUE.equals(horarioExameDisp.getIndHorarioExtra())) {
			if (grade == null){
				throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00862);//Grade não encontrada para geração de horário extra
			}else if (grade.getDataUltimaGeracao() == null){
				throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00863);//Grade não possui disponibilidade gerada
			}else if (horarioExameDisp.getId().getDthrAgenda().after(grade.getDataUltimaGeracao())){
				throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00864);//Não deve haver horário extra em data posterior à data da última geração da disponibilidade
			}
		}
	}
	
	/**
	 * Atualiza objeto AelHorarioExameDisp SEM FLUSH
	 * 
	 * @param {AelHorarioExameDisp} amostra
	 * @return {AelHorarioExameDisp}
	 * @throws BaseException
	 */
	public AelHorarioExameDisp atualizarSemFlush(AelHorarioExameDisp horarioExameDisp) throws BaseException {

		this.beforeUpdateAelHorarioExameDisp(horarioExameDisp);

		horarioExameDisp = this.getAelHorarioExameDispDAO().merge(horarioExameDisp);

		return horarioExameDisp;

	}

	/**
	 * Remover objeto AelHorarioExameDisp SEM FLUSH
	 * 
	 * @param {AelHorarioExameDisp} amostra
	 * @return {AelHorarioExameDisp}
	 * @throws BaseException
	 */
	public void removerSemFlush(AelHorarioExameDisp horarioExameDisp) {

		this.getAelHorarioExameDispDAO().remover(horarioExameDisp);

	}

	/**
	 * ORADB AELT_HED_BRU
	 * 
	 * Função deve ser executada antes de ser feito uma 
	 * atualização do Objeto AelHorarioExameDisp.
	 * 
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * @throws BaseException 
	 */
	protected void beforeUpdateAelHorarioExameDisp(
			AelHorarioExameDisp horarioExameDisp) throws BaseException {

		AelHorarioExameDisp horarioExameDispOriginal = this.getAelHorarioExameDispDAO().obterOriginal(horarioExameDisp.getId());
		
		this.verificarAlteracaoCamposCriadoEmOuServidor(horarioExameDisp, horarioExameDispOriginal);

		if (CoreUtil.modificados(horarioExameDisp.getGradeAgendaExame(), horarioExameDispOriginal.getGradeAgendaExame())) {

			this.verificarGrade(horarioExameDisp);

		}

		if (CoreUtil.modificados(horarioExameDisp.getTipoMarcacaoExame(), horarioExameDispOriginal.getTipoMarcacaoExame())) {

			this.verificarTipoMarcacao(horarioExameDisp);

		}

		if (CoreUtil.modificados(horarioExameDisp.getSituacaoHorario(), horarioExameDispOriginal.getSituacaoHorario())) {

			this.verificarSituacao(horarioExameDisp);

		}
		
		this.atualizarServidorEAlteradoEm(horarioExameDisp);
	}
	
	/**
	 * Atualiza o servidor que está alterando um horário/exame/disponível.
	 * 
	 * Atualiza data ALTERADO_EM.
	 * 
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * 
	 * @throws ApplicationBusinessException 
	 */
	protected void atualizarServidorEAlteradoEm(
			AelHorarioExameDisp horarioExameDisp) throws ApplicationBusinessException {

		horarioExameDisp.setAlteradoEm(new Date());
	}

	/**
	 * ORADB aelk_hed_rn.rn_hedp_ver_cons_sit
	 * 
	 * Se situação for G,L ou B não deve haver item solic.
	 * agendado para este horário. Se situação for M, deve 
	 * haver item solic. agendado neste horário. 
	 * 
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * 
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarSituacao(
			AelHorarioExameDisp horarioExameDisp) throws ApplicationBusinessException {

		Boolean inicializouListaItemHorarioAgendado = Hibernate.isInitialized(horarioExameDisp.getItemHorarioAgendados());
		Set<AelItemHorarioAgendado> setItemHorarioAgendado = horarioExameDisp.getItemHorarioAgendados();
		DominioSituacaoHorario situacaoHorario = horarioExameDisp.getSituacaoHorario();
		List<AelItemHorarioAgendado> listaItemHorarioAgendado = null;
		
		if (!inicializouListaItemHorarioAgendado) {
			listaItemHorarioAgendado = getAelItemHorarioAgendadoDAO()
					.pesquisarItemHorarioAgendadoPorHorarioExameDisp(horarioExameDisp.getId());
		} else {
			listaItemHorarioAgendado = new ArrayList<AelItemHorarioAgendado>(setItemHorarioAgendado);
		}
		
		if (listaItemHorarioAgendado.isEmpty()) {
			if (DominioSituacaoHorario.M.equals(situacaoHorario) || DominioSituacaoHorario.E.equals(situacaoHorario)) {
				throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00522);
			}
		} else if (DominioSituacaoHorario.G.equals(situacaoHorario) 
				|| DominioSituacaoHorario.L.equals(situacaoHorario) 
				|| DominioSituacaoHorario.B.equals(situacaoHorario)) {

			AelItemHorarioAgendado itemHorarioAgendado = new ArrayList<AelItemHorarioAgendado>(setItemHorarioAgendado).get(0);
			throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00523, itemHorarioAgendado.getId().getIseSoeSeq());
		}
	}

	/**
	 * ORADB aelk_hed_rn.rn_hedp_ver_tp_marca
	 * 
	 * Verifica se a tipo marcação exame esta ativo.
	 * 
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * 
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarTipoMarcacao(
			AelHorarioExameDisp horarioExameDisp) throws ApplicationBusinessException {

		DominioSituacao situacao = horarioExameDisp.getTipoMarcacaoExame().getIndSituacao();
		if (!DominioSituacao.A.equals(situacao)) {

			throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00517);

		}

	}

	/**
	 * ORADB aelk_hed_rn.rn_hedp_ver_grade
	 * 
	 * Verifica se a grade agenda exames esta ativa.
	 * 
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * 
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarGrade(
			AelHorarioExameDisp horarioExameDisp) throws ApplicationBusinessException {

		DominioSituacao situacao = horarioExameDisp.getGradeAgendaExame().getSituacao();
		if (!DominioSituacao.A.equals(situacao)) {

			throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00517);

		}

	}

	/**
	 * Verifica se os campos CRIADO_EM Ou SERVIDOR foram alterados
	 * se sim deve subir uma exceção.
	 * @param {AelHorarioExameDisp} horarioExameDisp
	 * @param {AelHorarioExameDisp} horarioExameDispOriginal
	 * @return {boolean}
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarAlteracaoCamposCriadoEmOuServidor(
			AelHorarioExameDisp horarioExameDisp,
			AelHorarioExameDisp horarioExameDispOriginal) throws ApplicationBusinessException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String criacaoHorarioExameAtual = sdf.format(horarioExameDisp.getCriadoEm());
        String criacaoHorarioExameOriginal = sdf.format(horarioExameDispOriginal.getCriadoEm());

        if (!criacaoHorarioExameAtual.equals(criacaoHorarioExameOriginal) ||
				CoreUtil.modificados(horarioExameDisp.getServidor(), horarioExameDispOriginal.getServidor())) {

			throw new ApplicationBusinessException(AelHorarioExameDispRNExceptionCode.AEL_00369);

		}

	}

	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
