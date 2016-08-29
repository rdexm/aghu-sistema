package br.gov.mec.aghu.farmacia.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoUsoDispensacao;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AfaTipoOcorDispensacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaTipoOcorDispensacao> {
	
	private static final long serialVersionUID = 6449367393537100402L;

	/**
	 * Realiza a pesquisa de ocorrências filtrando pelos parâmetros inseridos na
	 * pesquisa de Tipo de Ocorrência
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param seqOcorrencia
	 * @param descricaoOcorrencia
	 * @param tipoUsoOcorrencia
	 * @param situacaoPesq 
	 * @return List<AfaTipoOcorDispensacao>
	 */
	public List<AfaTipoOcorDispensacao> pesquisarOcorrencias(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Short seqOcorrencia, 
			String descricaoOcorrencia,
			DominioTipoUsoDispensacao tipoUsoOcorrencia, DominioSituacao situacaoPesq){
		
		DetachedCriteria criteria = obterCriteriaPesquisaOcorrencia(
				seqOcorrencia, descricaoOcorrencia, tipoUsoOcorrencia, situacaoPesq);
		
		criteria.createAlias(AfaTipoOcorDispensacao.Fields.SERVIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pf", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc(AfaTipoOcorDispensacao.Fields.DESCRICAO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	/**
	 * Obtém a criteria para a pesquisa de ocorrências
	 * @param seqOcorrencia
	 * @param descricaoOcorrencia
	 * @param tipoUsoOcorrencia
	 * @param situacaoPesq 
	 * @return
	 */
	private DetachedCriteria obterCriteriaPesquisaOcorrencia(Short seqOcorrencia, 
			String descricaoOcorrencia,
			DominioTipoUsoDispensacao tipoUsoOcorrencia, DominioSituacao situacaoPesq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoOcorDispensacao.class);
		
		if (seqOcorrencia != null){
			criteria.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.SEQ.toString(), seqOcorrencia));
		}
		
		if (StringUtils.isNotBlank(descricaoOcorrencia)){
			criteria.add(Restrictions.like(
					AfaTipoOcorDispensacao.Fields.DESCRICAO.toString(),
					descricaoOcorrencia, MatchMode.ANYWHERE));
		}
		if (tipoUsoOcorrencia != null){
			criteria.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.TIPO_USO.toString(), tipoUsoOcorrencia));
		}
		if (situacaoPesq != null){
			criteria.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.SITUACAO.toString(), situacaoPesq));
		}
		
		return criteria;
	}
	
	/**
	 * Obtém o número de registros retornados
	 * @param seqOcorrencia
	 * @param descricaoOcorrencia
	 * @param tipoUsoOcorrencia
	 * @return
	 */
	public Long pesquisarOcorrenciasCount(
			Short seqOcorrencia, 
			String descricaoOcorrencia,
			DominioTipoUsoDispensacao tipoUsoOcorrencia, DominioSituacao situacaoPesq){
		
		DetachedCriteria criteria = obterCriteriaPesquisaOcorrencia(
				seqOcorrencia, descricaoOcorrencia, tipoUsoOcorrencia, situacaoPesq);
		
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Obtem a descrição de um registro salvo no banco
	 * @param seq
	 * @return
	 */
	public String obterDescricaoAnterior(Short seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoOcorDispensacao.class);
		criteria.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.SEQ.toString(),seq));
		
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(AfaTipoOcorDispensacao.Fields.DESCRICAO.toString())));
		
		String descricaoAnterior = (String) this.executeCriteriaUniqueResult(criteria);

		return descricaoAnterior;
	}
	
	public AfaTipoOcorDispensacao reatacharOcorrencia(AfaTipoOcorDispensacao ocorrencia){
		AfaTipoOcorDispensacao objetoOcorrencia = ocorrencia;
		
		if (ocorrencia.getSeq() != null && !contains(objetoOcorrencia)){
			objetoOcorrencia = merge(ocorrencia);
		}
		
		return objetoOcorrencia;
		
	}

	
	/**
	 * Seleciona Motivo (Tipo de Ocorrencia de Dispensacao): Situacao: A-Ativo / Tipo de Uso: A-Ambos, E - Estornado  
	 * @param SEQP
	 * @param DESCRICAO
	 * @return
	 */

	public List<AfaTipoOcorDispensacao> pesquisarMotivoOcorrenciaDispensacao(Object strPesquisa) {
		
		DetachedCriteria criteria = criarCriteriaMotivoOcorrenciaDispensacao(strPesquisa);
		criteria.addOrder(Order.asc(AfaTipoOcorDispensacao.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * Count - Seleciona Motivo (Tipo de Ocorrencia de Dispensacao): Situacao: A-Ativo / Tipo de Uso: A-Ambos, E - Estornado  
	 * @param SEQP
	 * @param DESCRICAO
	 * @return
	 */

	public Long pesquisarMotivoOcorrenciaDispensacaoCount(Object strPesquisa) {
		DetachedCriteria criteria = criarCriteriaMotivoOcorrenciaDispensacao(strPesquisa);
		return executeCriteriaCount(criteria);
	}
	
	public List<AfaTipoOcorDispensacao> pesquisarTipoOcorrenciasAtivasENaoEstornada(){
		DetachedCriteria cri = DetachedCriteria.forClass(AfaTipoOcorDispensacao.class);
		cri.add(Restrictions.ne(AfaTipoOcorDispensacao.Fields.TIPO_USO.toString(), DominioTipoUsoDispensacao.E));
		cri.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		cri.addOrder(Order.asc(AfaTipoOcorDispensacao.Fields.SEQ.toString()));
		
		return executeCriteria(cri);
	}
	
	//5465
	public List<AfaTipoOcorDispensacao> pesquisarTipoOcorrenciasAtivasEstornadas(Short ...seqNotIn){
		DetachedCriteria cri = DetachedCriteria.forClass(AfaTipoOcorDispensacao.class);
		cri.add(Restrictions.ne(AfaTipoOcorDispensacao.Fields.TIPO_USO.toString(), DominioTipoUsoDispensacao.D));
		cri.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		cri.add(Restrictions.not(Restrictions.in(AfaTipoOcorDispensacao.Fields.SEQ.toString(), seqNotIn)));
		
		return executeCriteria(cri);
	}
	
	/**
	 * Apresenta todas as ocorrências
	 * @param SEQP
	 * @param DESCRICAO
	 * @return
	 */

	public List<AfaTipoOcorDispensacao> pesquisarTodosMotivosOcorrenciaDispensacao(Object strPesquisa) {
		
		DetachedCriteria criteria = criteriaTodosMotivosOcorrenciaDispensacao(strPesquisa);
		return executeCriteria(criteria, 0, 100, AfaTipoOcorDispensacao.Fields.SEQ.toString(), true);
	}
	
	public Long pesquisarTodosMotivosOcorrenciaDispensacaoCount(Object strPesquisa) {
		
		DetachedCriteria criteria = criteriaTodosMotivosOcorrenciaDispensacao(strPesquisa);
		
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria criteriaTodosMotivosOcorrenciaDispensacao(Object strPesquisa) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoOcorDispensacao.class);
		
		if (strPesquisa != null && !"".equals(strPesquisa)){
			if(CoreUtil.isNumeroInteger(strPesquisa)){
				Short numPesquisa = Short.valueOf(strPesquisa.toString());
				criteria.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.SEQ.toString(), numPesquisa));
			}else{
				criteria.add(Restrictions.ilike(AfaTipoOcorDispensacao.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	
	public DetachedCriteria criarCriteriaMotivoOcorrenciaDispensacao(Object strPesquisa){
		
		List<DominioTipoUsoDispensacao> tipoUso = new ArrayList<DominioTipoUsoDispensacao>();
		tipoUso.add(DominioTipoUsoDispensacao.A);
		tipoUso.add(DominioTipoUsoDispensacao.E);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoOcorDispensacao.class);
		criteria.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.in(AfaTipoOcorDispensacao.Fields.TIPO_USO.toString(), tipoUso));
		
		if (strPesquisa != null && !"".equals(strPesquisa)){
			if(CoreUtil.isNumeroInteger(strPesquisa)){
				Short numPesquisa = Short.valueOf(strPesquisa.toString());
				criteria.add(Restrictions.eq(AfaTipoOcorDispensacao.Fields.SEQ.toString(), numPesquisa));
			}else{
				criteria.add(Restrictions.ilike(AfaTipoOcorDispensacao.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}
