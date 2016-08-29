package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IEscalaPortalPlanejamentoCirurgiaBean;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.AgendamentoProcedimentoCirurgicoVO;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MensagemParametro;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghEspecialidades;
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


@Stateless
public class BlocoCirurgicoFacadeBean extends BaseFacade implements
		IBlocoCirurgicoFacadeBean {

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@EJB
	private MbcAgendasON mbcAgendasON;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4323200524352823091L;

	@EJB
	private IMbcAgendasBean iMbcAgendasBean;

	@EJB
	private IEscalaPortalPlanejamentoCirurgiaBean iEscalaPortalPlanejamentoCirurgiaBean;

	@EJB
	private IMbcCirurgiasBean iMbcCirurgiasBean;

	@Override
	@Secure("#{s:hasPermission('manterAgendaCirgPac', 'alterar') or "
			+ "s:hasPermission('alterarCirurgiaAposEscala', 'alterar') or "
			+ "s:hasPermission('incluirPacienteListaEspera', 'persistir') or "
			+ "s:hasPermission('agendarCirurgiaNaoPrevista', 'agendar')}")
	public MensagemParametro gravarAgenda(MbcAgendas agenda, Boolean isInclusao,
			MbcAgendaDiagnostico diagRemocao,
			List<MbcAgendaAnestesia> anestesiasRemocao,
			List<MbcAgendaSolicEspecial> solicEspecRemocao,
			List<MbcAgendaHemoterapia> listMbcAgendaHemoterapiaRemover,
			List<MbcAgendaProcedimento> listMbcAgendaProcedimentoRemover,
			List<MbcAgendaOrtProtese> listMbcAgendaOrteseProteseRemover,
			String obterLoginUsuarioLogado,
			List<MbcItensRequisicaoOpmes> itensExcluidos, List<MbcOpmesVO> listaClone, 
			List<MbcOpmesVO> listaPesquisada,
			Boolean zeraFluxo)
			throws BaseException {
		return this.iMbcAgendasBean.gravarAgenda(agenda, isInclusao, diagRemocao,
				anestesiasRemocao, solicEspecRemocao,
				listMbcAgendaHemoterapiaRemover,
				listMbcAgendaProcedimentoRemover,
				listMbcAgendaOrteseProteseRemover, obterLoginUsuarioLogado,
				itensExcluidos, listaClone, listaPesquisada,zeraFluxo);

	}

	
	public MbcAgendas merge(MbcAgendas agenda) {
		return getMbcAgendasDAO().merge(agenda);
	}

	private MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	@Override
	public MbcAgendas atualizarConvenio(MbcAgendas agenda) throws ApplicationBusinessException{
		return  mbcAgendasON.atualizarConvenio(agenda);
	}

	@Override
	@Secure("#{s:hasPermission('excluirPacienteListaEspera', 'excluir') or s:hasPermission('excluirPacienteListaCirurgiasCanceladas', 'excluir') or s:hasPermission('excluirPesquisaPacienteCirurgias', 'excluir')}")
	public void excluirAgenda(MbcAgendaJustificativa agendaJustificativa) throws BaseException {
		this.iMbcAgendasBean.excluirAgenda(agendaJustificativa);

	}

	@Override
	@Secure("#{s:hasPermission('excluirAgendamentoPaciente', 'excluir')}")
	public void excluirPacienteEmAgenda(
			MbcAgendaJustificativa agendaJustificativa, String cameFrom) throws BaseException {
		this.iMbcAgendasBean.excluirPacienteEmAgenda(agendaJustificativa, cameFrom);
	}

	@Override
	@Secure("#{s:hasPermission('transferirAgendamentoPacienteListaEspera', 'transferir')}")
	public void transferirAgendamento(
			MbcAgendaJustificativa agendaJustificativa, String comeFrom) throws BaseException {
		this.iMbcAgendasBean.transferirAgendamento(agendaJustificativa, comeFrom);
	}

	@Override
	public MbcAgendas obterAgendaPorAgdSeq(Integer agdSeq) {
		return getMbcAgendasDAO().obterPorChavePrimaria(agdSeq, true, MbcAgendas.Fields.PACIENTE, MbcAgendas.Fields.PROCEDIMENTO);
	}

	@Override
	public void ajustarHorariosAgendamentoEmEscala(Date dtAgenda,
			Short sciSeqp, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short espSeq,
			Short unfSeq, Boolean ajustarHorarioEscala) throws BaseException {
		this.iMbcAgendasBean.ajustarHorariosAgendamentoEmEscala(dtAgenda,
				sciSeqp, atuaUnidCirgs, espSeq, unfSeq, ajustarHorarioEscala);
	}

	@Override
	public void transferirAgendasEmEscalaParaPlanejamento(Date dataAgenda,
			Short sciUnfSeqCombo, Short sciSeqpCombo, Short unfSeqParam,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeqParam, Integer agdSeq)
			throws BaseException {
		this.iEscalaPortalPlanejamentoCirurgiaBean
				.transferirAgendasEmEscalaParaPlanejamento(dataAgenda,
						sciUnfSeqCombo, sciSeqpCombo, unfSeqParam,
						pucSerMatriculaParam, pucSerVinCodigoParam,
						pucUnfSeqParam, pucFuncProfParam, espSeqParam,
						agdSeq);
	}

	@Override
	public Boolean transferirAgendamentosEscala(
			EscalaPortalPlanejamentoCirurgiasVO planejado, Date dtAgendaParam,
			Short sciUnfSeqCombo, Short sciSeqpCombo, Short unfSeqParam,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeqParam, Date horaEscala)
			throws BaseException {
		return iEscalaPortalPlanejamentoCirurgiaBean
				.transferirAgendamentosEscala(planejado, dtAgendaParam,
						sciUnfSeqCombo, sciSeqpCombo, unfSeqParam,
						pucSerMatriculaParam, pucSerVinCodigoParam,
						pucUnfSeqParam, pucFuncProfParam, espSeqParam,
						horaEscala);
	}

	@Override
	public void ordenarEscalaParaCima(Integer agdSeq, Date dtAgendaParam,
			Short sciSeqpCombo, Short unfSeqParam,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeqParam) throws BaseException {
		iEscalaPortalPlanejamentoCirurgiaBean.ordenarEscalaParaCima(agdSeq,
				dtAgendaParam, sciSeqpCombo, unfSeqParam, pucSerMatriculaParam,
				pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam,
				espSeqParam);
	}

	@Override
	public void ordenarEscalaParaBaixo(Integer agdSeq, Date dtAgendaParam,
			Short sciSeqpCombo, Short unfSeqParam,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeqParam) throws BaseException {
		iEscalaPortalPlanejamentoCirurgiaBean.ordenarEscalaParaBaixo(agdSeq,
				dtAgendaParam, sciSeqpCombo, unfSeqParam, pucSerMatriculaParam,
				pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam,
				espSeqParam);
	}

	@Override
	public MbcAgendas atualizaRegimeMinimoSus(Integer seq) throws BaseException {
		return this.iEscalaPortalPlanejamentoCirurgiaBean
				.atualizaRegimeMinimoSus(seq);

	}

	@Override
	@Secure("#{s:hasPermission('registroCirurgiaRealizada','executar')}")
	public AlertaModalVO registrarCirurgiaRealizadaNotaConsumo(
			final boolean emEdicao, final boolean confirmaDigitacaoNS,
			CirurgiaTelaVO vo, final String nomeMicrocomputador) throws BaseException {
		return this.iMbcCirurgiasBean.registrarCirurgiaRealizadaNotaConsumo(
				emEdicao, confirmaDigitacaoNS, vo, nomeMicrocomputador);
	}

	@Override
	public void validarModalTempoUtilizacaoO2Ozot(CirurgiaTelaVO vo,
			AlertaModalVO alertaVO, boolean isPressionouBotaoSimModal)
			throws BaseException {
		this.iMbcCirurgiasBean.validarModalTempoUtilizacaoO2Ozot(vo, alertaVO,
				isPressionouBotaoSimModal);
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
		this.iEscalaPortalPlanejamentoCirurgiaBean.salvarEscala(planejados,
				escalas, unfSeq, dataEscala, 
				pucSerMatriculaReserva, pucSerVinCodigoReserva,
				pucUnfSeqReserva, pucFuncProfReserva, especialidadeReserva);
	}

	@Override
	public Boolean isConvenioPacienteSUS(MbcAgendas agenda) {
		return iEscalaPortalPlanejamentoCirurgiaBean.isConvenioPacienteSUS(agenda);
	}

	@Override
	public AgendamentoProcedimentoCirurgicoVO verificarRequisicaoOPMEs(
			MbcAgendas agenda)
			throws  BaseException  {
		
		return iEscalaPortalPlanejamentoCirurgiaBean.verificarRequisicaoOPMEs(agenda);
	}

}