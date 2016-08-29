package br.gov.mec.aghu.exames.sismama.business;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoResDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameSismamaVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaHistoRes;
import br.gov.mec.aghu.model.RapServidores;


@Modulo(ModuloEnum.EXAMES_LAUDOS)
@Stateless
public class SismamaFacade extends BaseFacade implements ISismamaFacade {


	@EJB
	private VerificarQuestoesSismamaApBiopsiaON verificarQuestoesSismamaApBiopsiaON;
	
	@EJB
	private ResultadoExameHistopatologicoON resultadoExameHistopatologicoON;
	
	@Inject
	private AelSismamaHistoResDAO aelSismamaHistoResDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7871086287097248089L;

	@Override
	public Boolean verificarExamesSismamaPorNumeroAp(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException {
		return getResultadoExameHistopatologicoON().verificarExamesSismamaPorNumeroAp(numeroAp, lu2Seq);
	}
	
	@Override
	public void verificarPreenchimentoExamesSismama(Long numeroAp,Integer lu2Seq) throws ApplicationBusinessException {
		getResultadoExameHistopatologicoON().verificarPreenchimentoExamesSismama(numeroAp, lu2Seq);
	}
	
	@Override
	public List<ItemSolicitacaoExameSismamaVO> pesquisarExameSismama(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException {
		return getResultadoExameHistopatologicoON().pesquisarExameSismama(numeroAp, lu2Seq);
	}
	
	@Override
	public void recuperarRespostasResultadoExameHistopatologico(Integer soeSeq, Short seqp,	Map<String, Object> respostas) throws ApplicationBusinessException {
		getResultadoExameHistopatologicoON().recuperarRespostas(soeSeq, seqp, respostas);
	}
	
	@Override
	public void gravarRespostasResultadoExameHistopatologico(Integer soeSeq, Short seqp, Map<String, Object> respostas) throws ApplicationBusinessException {
		getResultadoExameHistopatologicoON().gravarRespostas(soeSeq, seqp, respostas);
	}
	
	@Override
	public void validarRespostasResultadoExameHistopatologico(Map<String, Object> respostas) throws ApplicationBusinessException {
		getResultadoExameHistopatologicoON().validarRespostas(respostas);
	}
	
	@Override
	public Map<String, Object> recuperarRespostasBiopsisa(Integer soeSeq, Short seqp, Boolean isHist) {
		return getVerificarQuestoesSismamaApBiopsiaON().recuperarRespostas(soeSeq, seqp, isHist);
	}
	
	@Override
	public Map<String, Object> recuperarQuestoesRespostasBiopsia() {
		return getVerificarQuestoesSismamaApBiopsiaON().recuperarQuestoesRespostas();
	}
	
	
	@Override
	@BypassInactiveModule
	public Boolean habilitarBotaoQuestaoSismamaBiopsia(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist) {
		return getVerificarQuestoesSismamaApBiopsiaON().habilitarBotaoQuestaoSismamaBiopsia(solicitacoes, isHist);
	}
	
	/*@Override
	public Boolean habilitarBotaoQuestaoSismamaBiopsiaHist(Map<Integer, Vector<Short>> solicitacoes) {
		return getVerificarQuestoesSismamaApBiopsiaON().habilitarBotaoQuestaoSismamaBiopsiaHist(solicitacoes);
	}*/
	
	protected ResultadoExameHistopatologicoON getResultadoExameHistopatologicoON() {
		return resultadoExameHistopatologicoON;
	}

	protected VerificarQuestoesSismamaApBiopsiaON getVerificarQuestoesSismamaApBiopsiaON() {
		return verificarQuestoesSismamaApBiopsiaON;
	}

	@Override	
	public AelSismamaHistoRes criaResposta(AelItemSolicitacaoExames itemSolicitacaoExame, String codigo, Object resposta, RapServidores rap){
		return getVerificarQuestoesSismamaApBiopsiaON().criarResposta(itemSolicitacaoExame, codigo, resposta, rap);
	}
	
	@Override		
	public void salvarAelSismamaHistoRes(AelSismamaHistoRes res){
		getAelSismamaHistoResDAO().persistir(res);
	}
	
	@Override
	public List<AelSismamaHistoRes> obterRespostaSismamaHistoPorNumeroApECodigoCampo(Long numeroAp,
			String codigoCampo, Integer lu2Seq) {
		return getAelSismamaHistoResDAO().obterRespostaSismamaHistoPorNumeroApECodigoCampo(numeroAp, codigoCampo, lu2Seq);
	}
	
	protected AelSismamaHistoResDAO getAelSismamaHistoResDAO() {
		return aelSismamaHistoResDAO;
	}
	
}