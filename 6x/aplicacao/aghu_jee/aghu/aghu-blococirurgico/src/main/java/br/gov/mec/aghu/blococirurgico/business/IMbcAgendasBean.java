package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

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
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface IMbcAgendasBean {
	
	
	void excluirAgenda(MbcAgendaJustificativa agendaJustificativa) throws BaseException;
	
	void excluirPacienteEmAgenda(MbcAgendaJustificativa agendaJustificativa, String cameFrom) throws BaseException;
	
	void transferirAgendamento(MbcAgendaJustificativa agendaJustificativa, String ComeFrom) throws BaseException;
	
	void ajustarHorariosAgendamentoEmEscala(Date dtAgenda, Short sciSeqp, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short espSeq, Short unfSeq, Boolean ajustarHorarioEscala) throws BaseException;

	MensagemParametro gravarAgenda(MbcAgendas agenda, Boolean isInclusao,
			MbcAgendaDiagnostico diagRemocao,
			List<MbcAgendaAnestesia> anestesiasRemocao,
			List<MbcAgendaSolicEspecial> solicEspecRemocao,
			List<MbcAgendaHemoterapia> listMbcAgendaHemoterapiaRemover,
			List<MbcAgendaProcedimento> listMbcAgendaProcedimentoRemover,
			List<MbcAgendaOrtProtese> listMbcAgendaOrteseProteseRemover,
			String obterLoginUsuarioLogado,
			List<MbcItensRequisicaoOpmes> itensExcluidos,
			List<MbcOpmesVO> listaClone, List<MbcOpmesVO> listaPesquisada,Boolean zeraFluxo)
			throws BaseException;
}
