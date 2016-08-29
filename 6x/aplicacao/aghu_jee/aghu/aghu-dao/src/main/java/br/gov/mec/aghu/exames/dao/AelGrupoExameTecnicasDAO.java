package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelGrupoExameTecnicas;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelGrupoExameTecnicasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoExameTecnicas> {
	
	
	private static final long serialVersionUID = 9077180590704203082L;

	/**
	 * Obtem uma criteria padrao/default
	 * @return
	 */
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoExameTecnicas.class);
		return criteria;
    }
	
	/**
	 * Retorna AelAmostraItemExames original
	 * @param id
	 * @return
	 */
	/*public AelGrupoExameTecnicas obterOriginal(Integer id) {
		
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AelGrupoExameTecnicas.Fields.SEQ.toString());
		hql.append(", o.").append(AelGrupoExameTecnicas.Fields.DESCRICAO.toString());
		hql.append(", o.").append(AelGrupoExameTecnicas.Fields.IND_SITUACAO.toString());
		hql.append(", o.").append(AelGrupoExameTecnicas.Fields.CRIADO_EM.toString());
		hql.append(", o.").append(AelGrupoExameTecnicas.Fields.SERVIDOR.toString());
		hql.append(" from ").append(AelGrupoExameTecnicas.class.getSimpleName()).append(" o ");
		hql.append(" where o.").append(AelGrupoExameTecnicas.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		
		AelGrupoExameTecnicas retorno = null;
		Object[] campos = (Object[]) query.getSingleResult();
		 
		if(campos != null) {
			retorno = new AelGrupoExameTecnicas();

			retorno.setSeq( (Integer) campos[0]);
			retorno.setDescricao((String) campos[1]);
			retorno.setIndSituacao((DominioSituacao) campos[2]);
			retorno.setCriadoEm((Date) campos[3]);
			retorno.setServidor((RapServidores) campos[4]);
		}		
		
		return retorno;

	}*/
	
	/**
	 * Obtem uma instancia de AelGrupoExameTecnicas atraves do id
	 * @param seq
	 * @return
	 */
	public AelGrupoExameTecnicas obterPeloId(Integer seq){
		if(seq == null){
			return null;
		}
		return super.obterPorChavePrimaria(seq);
	}
	
	/**
	 * Cria os criterios para pesquisas paginadas
	 * @param elemento
	 * @return
	 */
	private DetachedCriteria criarCriteria(AelGrupoExameTecnicas elemento){
		
		DetachedCriteria criteria = obterCriteria();
		
		// Restricao de codigo/seq
		if(elemento.getSeq() != null){
			criteria.add(Restrictions.eq(AelGrupoExameTecnicas.Fields.SEQ.toString(), elemento.getSeq()));
		}

		// Restricao para descricao
		if(StringUtils.isNotBlank(elemento.getDescricao())){
			criteria.add(Restrictions.ilike(AelGrupoExameTecnicas.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
		}
		
		// Restricao para situacao
		if(elemento.getIndSituacao() != null){
			criteria.add(Restrictions.eq(AelGrupoExameTecnicas.Fields.IND_SITUACAO.toString(), elemento.getIndSituacao()));
		}
		
		return criteria;
	}

	/**
	 * Contabilizar resultados para pesquisas paginadas
	 * @param elemento
	 * @return
	 */
	public Long pesquisarCount(AelGrupoExameTecnicas elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa paginada
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return
	 */
	public List<AelGrupoExameTecnicas> pesquisarAelGrupoExameTecnicas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelGrupoExameTecnicas elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteria(criteria, firstResult, maxResult, AelGrupoExameTecnicas.Fields.DESCRICAO.toString(), true);
	}
	
	/**
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AelGrupoExameTecnicas> obterGrupoPorCodigoDescricao(String parametro) {
		DetachedCriteria criteria = obterCriteria();
		if (StringUtils.isNotBlank(parametro)) {
			if (CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(AelGrupoExameTecnicas.Fields.SEQ.toString(), Integer.parseInt(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AelGrupoExameTecnicas.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}		
		}
		criteria.addOrder(Order.asc(AelGrupoExameTecnicas.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}	
	
	/**
	 * Verifica a existencia de um registro AelGrupoExameTecnicas atraves da descricao
	 * @param descricao
	 * @return
	 */
	public Boolean existeGrupoPorCodigoDescricao(String descricao, Integer seq) {
		DetachedCriteria criteria = obterCriteria();
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AelGrupoExameTecnicas.Fields.DESCRICAO.toString(), descricao.trim(), MatchMode.ANYWHERE));
		}
		if(seq != null) {
			criteria.add(Restrictions.ne(AelGrupoExameTecnicas.Fields.SEQ.toString(), seq));
		}
		return executeCriteriaCount(criteria) > 0;
	}
	


}
