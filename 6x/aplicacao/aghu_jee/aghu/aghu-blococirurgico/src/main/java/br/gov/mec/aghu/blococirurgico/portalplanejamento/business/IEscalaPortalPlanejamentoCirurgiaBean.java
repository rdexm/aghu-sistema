package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.AgendamentoProcedimentoCirurgicoVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface IEscalaPortalPlanejamentoCirurgiaBean {
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
	
	MbcAgendas atualizaRegimeMinimoSus(Integer seq) throws BaseException;

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
	AgendamentoProcedimentoCirurgicoVO verificarRequisicaoOPMEs(MbcAgendas agenda) throws  BaseException ;

	void salvarEscala(List<EscalaPortalPlanejamentoCirurgiasVO> planejados,
			List<EscalaPortalPlanejamentoCirurgiasVO> escalas, Short unfSeq,
			Long dataEscala, Integer pucSerMatriculaReserva,
			Short pucSerVinCodigoReserva, Short pucUnfSeqReserva,
			DominioFuncaoProfissional pucFuncProfReserva,
			AghEspecialidades especialidadeReserva) throws BaseException;
}
