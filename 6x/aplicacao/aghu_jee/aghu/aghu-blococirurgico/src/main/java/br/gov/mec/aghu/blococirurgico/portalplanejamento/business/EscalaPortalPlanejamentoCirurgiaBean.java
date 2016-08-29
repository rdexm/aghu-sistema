package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.AgendamentoProcedimentoCirurgicoVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class EscalaPortalPlanejamentoCirurgiaBean extends BaseBusiness implements IEscalaPortalPlanejamentoCirurgiaBean {

	private static final Log LOG = LogFactory.getLog(EscalaPortalPlanejamentoCirurgiaBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private EscalaPortalPlanejamentoCirurgiaBotaoGravarON escalaPortalPlanejamentoCirurgiaBotaoGravarON;

	@EJB
	private MovimentacoesEscalaPortalPlanejamentoCirurgiaON movimentacoesEscalaPortalPlanejamentoCirurgiaON;

	@EJB
	private ConfirmarAgendamentoProcedimentoCirurgicoRN confirmarAgendamentoProcedimentoCirurgicoRN;

	@EJB
	private EscalaPortalPlanejamentoCirurgiaON escalaPortalPlanejamentoCirurgiaON;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7482477867536218342L;
	@Resource
	protected SessionContext ctx;
	
	public enum TranferirAgendaEscalaBeanExceptionCode implements BusinessExceptionCode {
		ERRO_TENTAR_TRANSFERIR_AGENDA_ESCALA_PARA_PLANEJAMENTO, ERRO_TENTAR_GRAVAR_ESCALA_CIRURGIA,
		ERRO_TENTAR_TRANSFERIR_AGENDA_PLANEJADO_PARA_ESCALA, ERRO_TENTAR_ORDENAR_ESCALA_PARA_CIMA,
		ERRO_AO_ATUALIZAR_REGIME_SUS, ERRO_TENTAR_ORDENAR_ESCALA_PARA_BAIXO;
	}
	
	@Override
	public void transferirAgendasEmEscalaParaPlanejamento(Date dataAgenda, Short sciUnfSeqCombo,
			Short sciSeqpCombo, Short unfSeqParam, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam, Integer agdSeq) throws BaseException {
		
		try {
			this.getEscalaPortalPlanejamentoCirurgiaON().transferirAgendasEmEscalaParaPlanejamento(dataAgenda, sciUnfSeqCombo,
					sciSeqpCombo, unfSeqParam, pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam,
					pucFuncProfParam, espSeqParam, agdSeq);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(TranferirAgendaEscalaBeanExceptionCode.ERRO_TENTAR_TRANSFERIR_AGENDA_ESCALA_PARA_PLANEJAMENTO);
		}
	}

	@Override
	public Boolean transferirAgendamentosEscala(
			EscalaPortalPlanejamentoCirurgiasVO planejado,
			Date dtAgendaParam, Short sciUnfSeqCombo, Short sciSeqpCombo,
			Short unfSeqParam, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam,
			Date horaEscala)
			throws  BaseException{
		try {
			return this.getEscalaPortalPlanejamentoCirurgiaON().transferirAgendamentosEscala(planejado, pucSerMatriculaParam, 
					pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, espSeqParam, pucUnfSeqParam, 
					dtAgendaParam, horaEscala, sciSeqpCombo);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(TranferirAgendaEscalaBeanExceptionCode.ERRO_TENTAR_TRANSFERIR_AGENDA_PLANEJADO_PARA_ESCALA);
		}
	}
	
	@Override
	public void ordenarEscalaParaCima(
			Integer agdSeq, Date dtAgendaParam, Short sciSeqpCombo,
			Short unfSeqParam, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam
			) throws  BaseException {
		try {
			this.getMovimentacoesEscalaPortalPlanejamentoCirurgiaON().deslocarHorariosBotaoAcima(agdSeq, dtAgendaParam,
					pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sciSeqpCombo, unfSeqParam,
					espSeqParam, servidorLogadoFacade.obterServidorLogado().getUsuario());
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(TranferirAgendaEscalaBeanExceptionCode.ERRO_TENTAR_ORDENAR_ESCALA_PARA_CIMA);
		}
	}
	
	@Override
	public void ordenarEscalaParaBaixo(
			Integer agdSeq, Date dtAgendaParam, Short sciSeqpCombo,
			Short unfSeqParam, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam
			) throws  BaseException {
		try {
			this.getMovimentacoesEscalaPortalPlanejamentoCirurgiaON().deslocarHorariosBotaoBaixo(agdSeq, dtAgendaParam,
					pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sciSeqpCombo, unfSeqParam,
					espSeqParam, servidorLogadoFacade.obterServidorLogado().getUsuario());
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(TranferirAgendaEscalaBeanExceptionCode.ERRO_TENTAR_ORDENAR_ESCALA_PARA_BAIXO);
		}
	}
	
	@Override
	public MbcAgendas atualizaRegimeMinimoSus(Integer seq
			) throws BaseException {
		
		MbcAgendas agenda = null;
		try {
			agenda = this.getEscalaPortalPlanejamentoCirurgiaON().atualizaRegimeMinimoSus(seq);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(TranferirAgendaEscalaBeanExceptionCode.ERRO_AO_ATUALIZAR_REGIME_SUS);
		}
		return agenda;
	}
	
	@Override
	public void salvarEscala(
			List<EscalaPortalPlanejamentoCirurgiasVO> planejados,
			List<EscalaPortalPlanejamentoCirurgiasVO> escalas, Short unfSeq,
			Long dataEscala, 
			Integer pucSerMatriculaReserva, Short pucSerVinCodigoReserva,
			Short pucUnfSeqReserva,
			DominioFuncaoProfissional pucFuncProfReserva,
			AghEspecialidades especialidadeReserva) throws BaseException {
		
		try {
			this.getEscalaPortalPlanejamentoCirurgiaBotaoGravarON().salvarEscala(planejados, escalas, unfSeq, dataEscala, 
					pucSerMatriculaReserva, pucSerVinCodigoReserva, pucUnfSeqReserva, pucFuncProfReserva, especialidadeReserva);
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(TranferirAgendaEscalaBeanExceptionCode.ERRO_TENTAR_GRAVAR_ESCALA_CIRURGIA);
		}
	}
	
	
	private EscalaPortalPlanejamentoCirurgiaON getEscalaPortalPlanejamentoCirurgiaON() {
		return escalaPortalPlanejamentoCirurgiaON;
	}
	
	private MovimentacoesEscalaPortalPlanejamentoCirurgiaON getMovimentacoesEscalaPortalPlanejamentoCirurgiaON() {
		return movimentacoesEscalaPortalPlanejamentoCirurgiaON;
	}

	private EscalaPortalPlanejamentoCirurgiaBotaoGravarON getEscalaPortalPlanejamentoCirurgiaBotaoGravarON() {
		return escalaPortalPlanejamentoCirurgiaBotaoGravarON;
	}

	@Override
	public Boolean isConvenioPacienteSUS(MbcAgendas agenda) {
		return confirmarAgendamentoProcedimentoCirurgicoRN.isConvenioPacienteSUS(agenda);
	}
	
	@Override
	public AgendamentoProcedimentoCirurgicoVO verificarRequisicaoOPMEs(MbcAgendas agenda) throws BaseException {
		return confirmarAgendamentoProcedimentoCirurgicoRN.verificarRequisicaoOPMEs(agenda);
	}
	
}
