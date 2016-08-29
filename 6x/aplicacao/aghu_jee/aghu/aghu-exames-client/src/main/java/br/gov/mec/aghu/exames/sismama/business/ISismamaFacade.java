package br.gov.mec.aghu.exames.sismama.business;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameSismamaVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaHistoRes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface ISismamaFacade extends Serializable {	
	
	Boolean verificarExamesSismamaPorNumeroAp(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException;
	
	void verificarPreenchimentoExamesSismama(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException;
	
	List<ItemSolicitacaoExameSismamaVO> pesquisarExameSismama(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException;
	
	void recuperarRespostasResultadoExameHistopatologico(Integer soeSeq, Short seqp,	Map<String, Object> respostas) throws ApplicationBusinessException;
	
	void gravarRespostasResultadoExameHistopatologico(Integer soeSeq, Short seqp, Map<String, Object> respostas) throws ApplicationBusinessException;
	
	void validarRespostasResultadoExameHistopatologico(Map<String, Object> respostas) throws ApplicationBusinessException;

	Map<String, Object> recuperarRespostasBiopsisa(Integer soeSeq, Short seqp, Boolean isHist);

	Boolean habilitarBotaoQuestaoSismamaBiopsia(
			Map<Integer, Vector<Short>> solicitacoes, Boolean isHist);

	Map<String, Object> recuperarQuestoesRespostasBiopsia();

	AelSismamaHistoRes criaResposta(
			AelItemSolicitacaoExames itemSolicitacaoExame,
			String codigo, Object resposta, RapServidores rap);

	void salvarAelSismamaHistoRes(AelSismamaHistoRes res);
	
	List<AelSismamaHistoRes> obterRespostaSismamaHistoPorNumeroApECodigoCampo(Long numeroAp,
			String codigoCampo, Integer lu2Seq);

}
