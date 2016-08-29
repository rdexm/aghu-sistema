package br.gov.mec.aghu.blococirurgico.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.AgendamentoProcedimentoCirurgicoVO;
import br.gov.mec.aghu.blococirurgico.vo.AlertaModalVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MensagemParametro;
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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface IBlocoCirurgicoFacadeBean extends Serializable {
	

	public MbcAgendas merge(MbcAgendas agenda);
	
	public void excluirAgenda(MbcAgendaJustificativa agendaJustificativa) throws BaseException;
	
	public void excluirPacienteEmAgenda(MbcAgendaJustificativa agendaJustificativa, String cameFrom) throws BaseException;
	
	public void transferirAgendamento(MbcAgendaJustificativa agendaJustificativa, String comeFrom) throws BaseException;
	
	public MbcAgendas obterAgendaPorAgdSeq(Integer agdSeq);
	
	void ajustarHorariosAgendamentoEmEscala(Date dtAgenda, Short sciSeqp, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short espSeq, Short unfSeq, Boolean ajustarHorarioEscala) throws BaseException;
	
	void transferirAgendasEmEscalaParaPlanejamento(Date dataAgenda, Short sciUnfSeqCombo,
			Short sciSeqpCombo, Short unfSeqParam, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam, Integer agdSeq) throws BaseException;
	
	public Boolean transferirAgendamentosEscala(
			EscalaPortalPlanejamentoCirurgiasVO planejado,
			Date dtAgendaParam, Short sciUnfSeqCombo, Short sciSeqpCombo,
			Short unfSeqParam, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam,
			Date horaEscala)
			throws  BaseException;

	public MbcAgendas atualizaRegimeMinimoSus(Integer seq) throws BaseException;
	
	AlertaModalVO registrarCirurgiaRealizadaNotaConsumo(boolean emEdicao, final boolean confirmaDigitacaoNS, CirurgiaTelaVO vo, String nomeMicrocomputador
			) throws BaseException;
	
	void validarModalTempoUtilizacaoO2Ozot(CirurgiaTelaVO vo, final AlertaModalVO alertaVO,
			final boolean isPressionouBotaoSimModal) throws BaseException;
	
	void salvarEscala(List<EscalaPortalPlanejamentoCirurgiasVO> planejados,List<EscalaPortalPlanejamentoCirurgiasVO> escalas, Short unfSeq, Long dataEscala, Integer pucSerMatriculaReserva,
			Short pucSerVinCodigoReserva, Short pucUnfSeqReserva, DominioFuncaoProfissional pucFuncProfReserva, AghEspecialidades especialidadeReserva) throws BaseException;

	public void ordenarEscalaParaCima(Integer agdSeq, Date dtAgendaParam,
			Short sciSeqpCombo, Short unfSeqParam,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeqParam) throws BaseException;

	public void ordenarEscalaParaBaixo(Integer agdSeq, Date dtAgendaParam,
			Short sciSeqpCombo, Short unfSeqParam,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeqParam) throws BaseException;
	
	//#31975
	Boolean isConvenioPacienteSUS(MbcAgendas agenda);
	AgendamentoProcedimentoCirurgicoVO verificarRequisicaoOPMEs(MbcAgendas agenda) throws BaseException ;

	MbcAgendas atualizarConvenio(MbcAgendas agenda)
			throws ApplicationBusinessException;

	MensagemParametro gravarAgenda(MbcAgendas agenda, Boolean isInclusao,
			MbcAgendaDiagnostico diagRemocao,
			List<MbcAgendaAnestesia> anestesiasRemocao,
			List<MbcAgendaSolicEspecial> solicEspecRemocao,
			List<MbcAgendaHemoterapia> listMbcAgendaHemoterapiaRemover,
			List<MbcAgendaProcedimento> listMbcAgendaProcedimentoRemover,
			List<MbcAgendaOrtProtese> listMbcAgendaOrteseProteseRemover,
			String obterLoginUsuarioLogado,
			List<MbcItensRequisicaoOpmes> itensExcluidos,
			List<MbcOpmesVO> listaClone, List<MbcOpmesVO> listaPesquisada,
			Boolean zeraFluxo) throws BaseException;
	
}