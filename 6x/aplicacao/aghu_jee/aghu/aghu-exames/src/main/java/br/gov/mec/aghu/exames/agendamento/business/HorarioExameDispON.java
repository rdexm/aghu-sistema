package br.gov.mec.aghu.exames.agendamento.business;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGradeAgendaExameId;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioExameDispId;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class HorarioExameDispON extends BaseBusiness {

	@EJB
	private GradeAgendaExameRN gradeAgendaExameRN;
	
	private static final Log LOG = LogFactory.getLog(HorarioExameDispON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;
	
	@Inject
	private AelHorarioExameDispDAO aelHorarioExameDispDAO;
	
	@EJB
	private IExamesFacade examesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7235596871398092093L;
	
	public enum HorarioExameDispONExceptionCode implements BusinessExceptionCode {
		ERRO_MANTER_DISPONIBILIDADE_HORARIO_NOVA_DATA_PASSADO, ERRO_MANTER_DISPONIBILIDADE_HORARIO_NOVO_EXISTENTE;
	}
	
	private AelHorarioExameDisp obterHorarioExameDataMaiorGrade(List<AelHorarioExameDisp> listaHorarioExameDisp) {
		AelGradeAgendaExame gradeAgendaExame;
		
		if (!listaHorarioExameDisp.isEmpty()) {
			Comparator<AelHorarioExameDisp> decrescente = Collections.reverseOrder(new AelHorarioExameDispComparator());
			Collections.sort(listaHorarioExameDisp, decrescente);

			gradeAgendaExame = listaHorarioExameDisp.get(0).getGradeAgendaExame();
			
			/*
			 * Retorna o primeiro horarioExameDisp cuja data/hora
			 * for maior que a data/hora da grade 
			 */
			for (AelHorarioExameDisp horarioExameDisp : listaHorarioExameDisp) {
				if (horarioExameDisp.getId().getDthrAgenda().getTime() > gradeAgendaExame.getDataUltimaGeracao().getTime()) {
					AelGradeAgendaExame grade = getAelGradeAgendaExameDAO().obterPorChavePrimaria(horarioExameDisp.getGradeAgendaExame().getId());
					horarioExameDisp.setGradeAgendaExame(grade);
					return horarioExameDisp;
				}
			}
		}
		 
		return null;
	}
	
	private class AelHorarioExameDispComparator implements Comparator<AelHorarioExameDisp> {
		@Override
		public int compare(AelHorarioExameDisp h1, AelHorarioExameDisp h2) {
			return h1.getId().getDthrAgenda().compareTo(h2.getId().getDthrAgenda());
		}
	}
	
	private void atualizarDataUltimaGeracaoGrade(AelGradeAgendaExame gradeAgendaExame) throws ApplicationBusinessException {
		Date data = getAelHorarioExameDispDAO().obterHorarioExameDataMaisRecentePorGrade(
				gradeAgendaExame.getId().getUnfSeq(), gradeAgendaExame.getId().getSeqp());
		
		gradeAgendaExame.setDataUltimaGeracao(DateUtil.truncaData(data));
		getGradeAgendaExameRN().atualizarGradeAgendaExame(gradeAgendaExame);
	}	
	
	/**
	 * Valida se um novo horário de exame é menor
	 * que a data atual, caso positivo retorna uma exceção de negócio.
	 * 
	 * @param novoHorarioExameDisp
	 * @throws ApplicationBusinessException
	 */
	private void validarDataNovoHorario(AelHorarioExameDisp novoHorarioExameDisp) throws ApplicationBusinessException {
		if (novoHorarioExameDisp.getId().getDthrAgenda().getTime() < new Date().getTime()) {
			throw new ApplicationBusinessException(
					HorarioExameDispONExceptionCode.ERRO_MANTER_DISPONIBILIDADE_HORARIO_NOVA_DATA_PASSADO);
		}
	}
	
	/**
	 * Verifica se o novo horário já existe.
	 * 
	 * @param novoHorarioExameDisp
	 * @throws ApplicationBusinessException
	 */
	private void verificarHorarioExistente(AelHorarioExameDisp novoHorarioExameDisp) throws ApplicationBusinessException {
		if (getAelHorarioExameDispDAO().obterPorChavePrimaria(novoHorarioExameDisp.getId()) != null) {
			// Lança exceção caso o novo horário ja exista
			throw new ApplicationBusinessException(
					HorarioExameDispONExceptionCode.ERRO_MANTER_DISPONIBILIDADE_HORARIO_NOVO_EXISTENTE);
		}
	}
	
	/**
	 * Verifica se situação do horário do exame não permite a
	 * alteração ou exclusão do mesmo. Caso situacaoHorario 
	 * seja M (marcado) ou E (executado) retorna true, senão retorna false. 
	 * 
	 * 
	 * @param horarioExameDisp
	 * @return Boolean
	 */
	public Boolean verificarSituacaoHorarioIndisponivel(AelHorarioExameDisp horarioExameDisp) {
		if (horarioExameDisp.getSituacaoHorario().equals(DominioSituacaoHorario.M)
				|| horarioExameDisp.getSituacaoHorario().equals(DominioSituacaoHorario.E)) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}	
	
	/**
	 * Remove lista de AelHorarioExameDisp.
	 * 
	 * @param listaHorarioExame
	 * @throws ApplicationBusinessException
	 */
	public Boolean removerListaHorarioExameDisp(List<AelHorarioExameDisp> listaHorarioExame) 
			throws ApplicationBusinessException {
		int totalHorariosExame = listaHorarioExame.size();
		int totalHorariosExameExcluidos = 0;
		AelHorarioExameDispDAO aelHorarioExameDispDAO = getAelHorarioExameDispDAO();

		for (AelHorarioExameDisp horarioExameDisp : listaHorarioExame) {
			AelHorarioExameDisp horarioExameDispExcluir = 
					aelHorarioExameDispDAO.obterPorChavePrimaria(horarioExameDisp.getId());
			getExamesFacade().removerHorarioExameDisp(horarioExameDispExcluir);
			totalHorariosExameExcluidos++;
		}

		AelHorarioExameDisp horarioComDataMaiorGrade = obterHorarioExameDataMaiorGrade(listaHorarioExame);
		if (horarioComDataMaiorGrade != null) {
			AelGradeAgendaExame gradeAgendaExame = horarioComDataMaiorGrade.getGradeAgendaExame();
			atualizarDataUltimaGeracaoGrade(gradeAgendaExame);
		}
		
		if (totalHorariosExame == totalHorariosExameExcluidos) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	/**
	 * Atualiza (persiste) lista de AelHorarioExameDisp. 
	 * 
	 * @param listaHorarioExameDisp
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	// @TransactionTimeout(1800)
	public void atualizarListaHorarioExameDisp(List<AelHorarioExameDisp> listaHorarioExameDisp, 
			DominioSituacaoHorario situacaoHorario, AelTipoMarcacaoExame tipoMarcacaoExame, Boolean extra, 
			Boolean exclusivo) throws BaseException {
		IExamesFacade examesFacade = getExamesFacade();
		for (AelHorarioExameDisp horarioExameDisp : listaHorarioExameDisp) {
			horarioExameDisp.setSituacaoHorario(situacaoHorario);
			if (tipoMarcacaoExame != null) {
				horarioExameDisp.setTipoMarcacaoExame(tipoMarcacaoExame);
			}
			horarioExameDisp.setIndHorarioExtra(extra);
			horarioExameDisp.setExclusivoExecutor(exclusivo);
			examesFacade.atualizarHorarioExameDisp(horarioExameDisp);
		}
	}
	
	public void inserirHorarioExameDisp(DominioSituacaoHorario situacaoHorario,
			AelTipoMarcacaoExame tipoMarcacaoExame, Boolean extra, Boolean exclusivo, Short gaeUnfSeq, Integer gaeSeqp,
			Date dthrAgenda) throws BaseException {
		
		AelGradeAgendaExameId gradeId = new AelGradeAgendaExameId(gaeUnfSeq, gaeSeqp); 
		AelGradeAgendaExame gradeAgendaExame = getAelGradeAgendaExameDAO().obterPorChavePrimaria(gradeId);
		
		AelHorarioExameDispId horarioExameId = new AelHorarioExameDispId(gaeUnfSeq, gaeSeqp, dthrAgenda);
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		horarioExameDisp.setId(horarioExameId);
		horarioExameDisp.setSituacaoHorario(situacaoHorario);
		horarioExameDisp.setTipoMarcacaoExame(tipoMarcacaoExame);
		horarioExameDisp.setIndHorarioExtra(extra);
		horarioExameDisp.setExclusivoExecutor(exclusivo);
		horarioExameDisp.setGradeAgendaExame(gradeAgendaExame);
		
		verificarHorarioExistente(horarioExameDisp);
		validarDataNovoHorario(horarioExameDisp);
		
		getExamesFacade().inserirHorarioExameDisp(horarioExameDisp);
	}
	
	
	public AelHorarioExameDisp refreshHorarioExameDisp(AelHorarioExameDisp horarioExameDisp) {
		try {
			this.refresh(horarioExameDisp);
		} catch (IllegalArgumentException ex) {
			horarioExameDisp = getAelHorarioExameDispDAO().obterPorChavePrimaria(horarioExameDisp.getId());
		}
		return horarioExameDisp;
	}	
	
	/**
	 * Retorna uma instância de AelHorarioExameDisp sem persistir.
	 * 
	 * @param dataHora
	 * @param unfExecutora
	 * @param seqGrade
	 * @param tipoMarcacaoExame
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public AelHorarioExameDisp montarHorarioExameDisp(Date dataHora, Short unfExecutora, Integer seqGrade, 
			AelTipoMarcacaoExame tipoMarcacaoExame) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelHorarioExameDisp horarioExameDisp = new AelHorarioExameDisp();
		AelHorarioExameDispId id = new AelHorarioExameDispId();
		id.setGaeUnfSeq(unfExecutora);
		id.setGaeSeqp(seqGrade);
		id.setDthrAgenda(dataHora);
		horarioExameDisp.setId(id);
		horarioExameDisp.setTipoMarcacaoExame(tipoMarcacaoExame);
		horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.L);
		horarioExameDisp.setIndHorarioExtra(true);
		horarioExameDisp.setCriadoEm(new Date());
		horarioExameDisp.setServidor(servidorLogado);
		horarioExameDisp.setExclusivoExecutor(false);
		horarioExameDisp.setIndConvenio(null);

		AelGradeAgendaExameId gradeAgendaExameId = new AelGradeAgendaExameId();
		gradeAgendaExameId.setSeqp(id.getGaeSeqp());
		gradeAgendaExameId.setUnfSeq(id.getGaeUnfSeq());
		AelGradeAgendaExame gradeAgendaExame = getAelGradeAgendaExameDAO().obterPorChavePrimaria(gradeAgendaExameId);
		horarioExameDisp.setGradeAgendaExame(gradeAgendaExame);
		
		return horarioExameDisp;
	}
	
	/**
	 * Retorna uma instância de AelHorarioExameDisp. 
	 * Obs.: Faz verificação se essa instância já está persistida retornando-a caso já esteja, 
	 * caso contrário será criada uma nova instância (não persistida).
	 * 
	 * @param gaeUnfSeq
	 * @param gaeSeqp
	 * @param dataDisponivel
	 * @param tipoMarcacaoExame
	 * @param horarioExameDispDAO
	 * @throws ApplicationBusinessException  
	 */
	public AelHorarioExameDisp obterHorarioExameDisp(Short gaeUnfSeq, Integer gaeSeqp, Date dataDisponivel, 
			AelTipoMarcacaoExame tipoMarcacaoExame,	AelHorarioExameDispDAO horarioExameDispDAO) throws ApplicationBusinessException {
		AelHorarioExameDispId horarioExameDispId = new AelHorarioExameDispId(gaeUnfSeq, gaeSeqp, dataDisponivel);
		AelHorarioExameDisp horarioExameDisp = horarioExameDispDAO.obterPorChavePrimaria(horarioExameDispId);
		if (horarioExameDisp == null) {
			horarioExameDisp = montarHorarioExameDisp(dataDisponivel, gaeUnfSeq, gaeSeqp, tipoMarcacaoExame);
		}
		return horarioExameDisp;
	}	
	
	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO() {
		return aelGradeAgendaExameDAO;
	}
	
	protected GradeAgendaExameRN getGradeAgendaExameRN() {
		return gradeAgendaExameRN;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
