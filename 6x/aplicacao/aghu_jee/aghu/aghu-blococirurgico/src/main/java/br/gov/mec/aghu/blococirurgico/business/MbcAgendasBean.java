package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MensagemParametro;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaJustificativa;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class MbcAgendasBean extends BaseBusiness implements IMbcAgendasBean {

	private static final Log LOG = LogFactory.getLog(MbcAgendasBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;


	@EJB
	private MbcAgendasON mbcAgendasON;

	@EJB
	private MbcAgendasHorarioPrevisaoON mbcAgendasHorarioPrevisaoON;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8640337808965093989L;
	
	
	@Resource
	protected SessionContext ctx;

	public enum SolicitacaoExameBeanExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_PERSISTIR_MBC_AGENDA;
	}

	public MensagemParametro gravarAgenda(MbcAgendas agenda, Boolean isInclusao, MbcAgendaDiagnostico diagRemocao,
			List<MbcAgendaAnestesia> anestesiasRemocao, List<MbcAgendaSolicEspecial> solicEspecRemocao,
			List<MbcAgendaHemoterapia> listMbcAgendaHemoterapiaRemover, List<MbcAgendaProcedimento> listMbcAgendaProcedimentoRemover,
			List<MbcAgendaOrtProtese> listMbcAgendaOrteseProteseRemover, String obterLoginUsuarioLogado,
			List<MbcItensRequisicaoOpmes> itensExcluidos, List<MbcOpmesVO> listaClone, List<MbcOpmesVO> listaPesquisada,
			Boolean zeraFluxo) throws BaseException {
		try {
			return this.getMbcAgendasON().gravarAgenda(agenda, diagRemocao, anestesiasRemocao, solicEspecRemocao, listMbcAgendaHemoterapiaRemover, listMbcAgendaProcedimentoRemover,listMbcAgendaOrteseProteseRemover, obterLoginUsuarioLogado, itensExcluidos, listaClone, listaPesquisada,zeraFluxo);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			if(isInclusao) {
				desatacharObjetosInclusao(agenda);
			}
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			if(isInclusao) {
				desatacharObjetosInclusao(agenda);
			}
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_MBC_AGENDA);
		}
	}
	
	
	protected void desatacharObjetosInclusao(MbcAgendas agenda) {
		getMbcAgendasDAO().desatachar(agenda);
		agenda.setSeq(null);
		if(agenda.getAgendasAnestesias() != null) {
			for(MbcAgendaAnestesia anestesia : agenda.getAgendasAnestesias()) {
				anestesia.setId(null);
			}
		}
		if(agenda.getAgendasDiagnosticos() != null) {
			for(MbcAgendaDiagnostico diag : agenda.getAgendasDiagnosticos()) {
				diag.setId(null);
			}
		}
		if(agenda.getAgendasHemoterapias() != null) {
			for(MbcAgendaHemoterapia hemo : agenda.getAgendasHemoterapias()) {
				hemo.setId(null);
			}
		}
		if(agenda.getAgendasOrtProteses() != null) {
			for(MbcAgendaOrtProtese ortProt : agenda.getAgendasOrtProteses()) {
				ortProt.setId(null);
			}
		}
		if(agenda.getAgendasSolicEspeciais() != null) {
			for(MbcAgendaSolicEspecial solicEsp : agenda.getAgendasSolicEspeciais()) {
				solicEsp.setId(null);
			}
		}
		if(agenda.getAgendasprocedimentos() != null) {
			for(MbcAgendaProcedimento proced : agenda.getAgendasprocedimentos()) {
				proced.setId(null);
			}
		}
	}
	
	protected MbcAgendasON getMbcAgendasON() {
		return mbcAgendasON;
	}

	@Override
	public void excluirAgenda(MbcAgendaJustificativa agendaJustificativa)
			throws BaseException {
		try {
			this.getMbcAgendasON().excluirAgenda(agendaJustificativa);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_MBC_AGENDA);
		}
		
	}
	
	@Override
	public void excluirPacienteEmAgenda(MbcAgendaJustificativa agendaJustificativa, String cameFrom)
			throws BaseException {
		try {
			this.getMbcAgendasON().excluirPacienteEmAgenda(agendaJustificativa, cameFrom);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_MBC_AGENDA);
		}
		
	}
	
	
	
	@Override
	public void transferirAgendamento(MbcAgendaJustificativa agendaJustificativa, String comeFrom)
			throws BaseException {
		try {
			this.getMbcAgendasON().transferirAgendamento(agendaJustificativa, comeFrom);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_MBC_AGENDA);
		}
		
	}
	
	@Override
	public void ajustarHorariosAgendamentoEmEscala(Date dtAgenda,
			Short sciSeqp, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short espSeq, Short unfSeq,
			Boolean ajustarHorarioEscala) throws BaseException {
		try {
			this.getMbcAgendasHorarioPrevisaoON().ajustarHorariosAgendamentoEmEscala(dtAgenda, sciSeqp, atuaUnidCirgs, espSeq, unfSeq, ajustarHorarioEscala);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_MBC_AGENDA);
		}
	}
	
	private MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	
	private MbcAgendasHorarioPrevisaoON getMbcAgendasHorarioPrevisaoON() {
		return mbcAgendasHorarioPrevisaoON;
	}

	
}
