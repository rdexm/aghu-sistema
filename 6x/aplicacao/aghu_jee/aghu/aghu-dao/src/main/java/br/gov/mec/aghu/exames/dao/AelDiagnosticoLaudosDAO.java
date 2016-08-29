package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelCidos;
import br.gov.mec.aghu.model.AelDiagnosticoLaudos;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;

public class AelDiagnosticoLaudosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDiagnosticoLaudos> {

    private static final long serialVersionUID = 8848336991483259808L;

    /**
     * Lista os Diagnósticos associados a um número de exame(laudo único/Ap),
     * mostrando código e descrição da Tabela Ael_Diagnostico_Laudoss.
     * 
     * @param seqExame
     * @return
     */
    public List<AelDiagnosticoLaudos> listarDiagnosticoLaudosPorSeqExame(Long seqExame) {
	DetachedCriteria criteria = montaCriteriaDiagnosticoLaudos(seqExame);
	return executeCriteria(criteria);
    }

    public DetachedCriteria montaCriteriaDiagnosticoLaudos(Long seqExame) {
	final DetachedCriteria criteria = DetachedCriteria.forClass(AelDiagnosticoLaudos.class, "ADL");
	criteria.add(Restrictions.eq("ADL." + AelDiagnosticoLaudos.Fields.SEQ_EXAME.toString(), seqExame));
	
	criteria.setFetchMode(AelDiagnosticoLaudos.Fields.CID.toString(), FetchMode.JOIN);
	criteria.setFetchMode(AelDiagnosticoLaudos.Fields.CIDOS.toString(), FetchMode.JOIN);
	return criteria;
    }

    /**
     * Verifica se já existe um Diagnostico(CID-10) cadastrada, nesse exame. Não
     * pode haver repetições.
     * 
     * @param codigo
     * @return
     */
    public Long obterDiagnosticoLaudosCid(Long seqExame, Integer seqCid) {
	DetachedCriteria criteria = montaCriteriaDiagnosticoLaudos(seqExame);
	if(seqCid!=null){
		criteria.add(Restrictions.eq("ADL." + AelDiagnosticoLaudos.Fields.SEQ_CID.toString(), seqCid));	
	}
	return executeCriteriaCount(criteria);
    }

    /**
     * Verifica se já existe um Diagnostico(CID-O) cadastrada, nesse exame. Não
     * pode haver repetições.
     * 
     * @param codigo
     * @return
     */
    public Long obterDiagnosticoLaudosCidO(Long seqExame, Long seqCidO) {
	DetachedCriteria criteria = montaCriteriaDiagnosticoLaudos(seqExame);
	if(seqCidO != null){
		criteria.add(Restrictions.eq("ADL." + AelDiagnosticoLaudos.Fields.SEQ_CIDO.toString(), seqCidO));	
	}
	return executeCriteriaCount(criteria);
    }
    
    /**
	 * Ael_Cidos. A pesquisa pode ser feita pelo código, ou por
	 * palavras que compôe a descrição.
	 * 
	 * @param pesquisa
	 * @return
	 */
	public List<AelCidos> listarAelCidos(Object pesquisa){
		List<AelCidos> retorno = null;
		String codDesc = (String) pesquisa;
		retorno = executeCriteria(criarCriteriaPesquisarCidosPorCodigo(codDesc), 0, 100, null, false);
		if (retorno == null || retorno.isEmpty()) {
			retorno = executeCriteria(criarCriteriaPesquisarCidosPorDescricao(codDesc), 0, 100, null, false);
		}
		return retorno;
	
	}
	
	public Long listarAelCidosCount(Object pesquisa){

		Long retorno = null;
		String codDesc = (String) pesquisa;
		retorno = executeCriteriaCount(criarCriteriaPesquisarCidosPorCodigo(codDesc));

		if(retorno == null || retorno.equals(Integer.valueOf(0))){
			retorno = executeCriteriaCount(criarCriteriaPesquisarCidosPorDescricao(codDesc));
		}

		return retorno;
	}
	
	private DetachedCriteria criarCriteriaPesquisarCidosPorCodigo(final String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCidos.class);
		criteria.add(Restrictions.eq(AelCidos.Fields.CODIGO.toString(), descricao.toUpperCase()));
		return criteria;
	}

	private DetachedCriteria criarCriteriaPesquisarCidosPorDescricao(final String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelCidos.class);
		criteria.add(Restrictions.ilike(AelCidos.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		return criteria;
	}

	public Long listarDisgnosticosLaudosCount(TelaLaudoUnicoVO telaLaudoVO) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDiagnosticoLaudos.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq(AelDiagnosticoLaudos.Fields.SEQ_EXAME.toString(), telaLaudoVO.getAelExameAp().getSeq()));
		return this.executeCriteriaCount(criteria);
	}

	public List<AelDiagnosticoLaudos>  obterAelDiagnosticoLaudosPorLuxSeq(Long luxSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelDiagnosticoLaudos.class,"ADL");
		criteria.createAlias(MbcAgendaDiagnostico.Fields.AGH_CID.toString(), "CID", Criteria.LEFT_JOIN);
		criteria.createAlias(AelDiagnosticoLaudos.Fields.CIDOS.toString(), "CIDOS", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq("ADL." + AelDiagnosticoLaudos.Fields.SEQ_EXAME.toString(), luxSeq));
		return executeCriteria(criteria);
	}
	
}