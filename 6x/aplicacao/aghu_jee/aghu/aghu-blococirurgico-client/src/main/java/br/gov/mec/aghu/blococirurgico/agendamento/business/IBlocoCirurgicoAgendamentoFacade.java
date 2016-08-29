package br.gov.mec.aghu.blococirurgico.agendamento.business;

import java.util.List;

import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoPadraoId;
import java.io.Serializable;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IBlocoCirurgicoAgendamentoFacade extends Serializable {
		
	List<MbcDescricaoPadrao> buscarDescricaoPadrao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Short especialidadeId, Integer procedimentoId, String titulo) ;
	
	Long contarDescricaoPadrao(Short especialidadeId,Integer procedimentoId, String titulo);
	
	MbcDescricaoPadrao obterDescricaoPadraoById(Integer seqp, Short espSeq);
	
	void persistirMbcDescricaopadrao(MbcDescricaoPadrao descricaoPadrao) throws BaseException;
	
	void excluirDescricaoPadrao(MbcDescricaoPadraoId id) throws BaseException;
	MbcDescricaoPadrao obterPorChavePrimaria(MbcDescricaoPadraoId id) throws BaseException;
		
}