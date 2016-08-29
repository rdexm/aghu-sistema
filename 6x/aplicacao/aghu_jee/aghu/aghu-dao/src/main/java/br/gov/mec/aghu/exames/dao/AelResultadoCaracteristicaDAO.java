package br.gov.mec.aghu.exames.dao;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;

public class AelResultadoCaracteristicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelResultadoCaracteristica> {

	
	private static final long serialVersionUID = -8471354082470081063L;

	/**
	 * Retorna uma lista de resultado<br>
	 * caracteristica atraves do parametro<br>
	 * de pesquisa da suggestion.
	 * 
	 * @param param
	 * @param calSeq
	 * @return
	 */
	public List<AelResultadoCaracteristica> listarResultadoCaracteristica(String param, Integer calSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCaracteristica.class);
		
		if(CoreUtil.isNumeroInteger(param)){
			criteria.add(Restrictions.eq(AelResultadoCaracteristica.Fields.SEQ.toString(), 
					Integer.valueOf(param)));
		} else if(StringUtils.isNotBlank(param)){
			criteria.add(Restrictions.ilike(AelResultadoCaracteristica.Fields.DESCRICAO.toString(), 
					param, MatchMode.ANYWHERE));
		}
		
		if(calSeq != null) {
			criteria.add(Restrictions.in(AelResultadoCaracteristica.Fields.SEQ.toString(), 
					obterSubQueryListarResultadoCaracteristica(calSeq)));
		}
		
		criteria.addOrder(Order.asc(AelResultadoCaracteristica.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Subquery executada pela<br>
	 * consulta listarResultadoCaracteristica.
	 * 
	 * @param calSeq
	 * @return
	 */
	protected List<Integer> obterSubQueryListarResultadoCaracteristica(Integer calSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameGrupoCaracteristica.class);
		criteria.createAlias(AelExameGrupoCaracteristica.Fields.GRUPO_RESULTADO_CARACTERISTICA.toString(), "GCA");
		criteria.createAlias(AelExameGrupoCaracteristica.Fields.CAMPO_LAUDO.toString(), "CAL");
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property("GCA." + AelGrupoResultadoCaracteristica.Fields.SEQ.toString()));
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq("CAL." + AelCampoLaudo.Fields.SEQ.toString(), calSeq));
		
		List<Integer> resultado = executeCriteria(criteria);
		
		if(resultado.isEmpty()) {
			resultado = new LinkedList<Integer>();
			resultado.add(Integer.valueOf(0));
		}
		
		return resultado;
	}
	
	public AelResultadoCaracteristica obterResultadoCaracteristicaPorTipoCampo(Integer calSeq, String emaExaSigla, Integer emaManSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCaracteristica.class);
		
		criteria.add(Restrictions.in(AelResultadoCaracteristica.Fields.SEQ.toString(), 
				this.obterSubQueryListarResultadoCaracteristicaPorCodigo(calSeq, emaExaSigla, emaManSeq)));
		
		List<AelResultadoCaracteristica> result = executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		}else {
			return result.get(0);
		}
		
	}
	
	
	protected List<Integer> obterSubQueryListarResultadoCaracteristicaPorCodigo(Integer calSeq, String emaExaSigla, Integer emaManSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameGrupoCaracteristica.class);
		criteria.createAlias(AelExameGrupoCaracteristica.Fields.CAMPO_LAUDO.toString(), "CAL");
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property(AelExameGrupoCaracteristica.Fields.CAC_SEQ.toString()));
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq("CAL." + AelCampoLaudo.Fields.SEQ.toString(), calSeq));
		criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		
		List<Integer> result = this.executeCriteria(criteria); 
		
		if(result.isEmpty()) {
			result = new LinkedList<Integer>();
			result.add(Integer.valueOf(0));
		}

		return result;
	}
	
	public Long pesquisarCount(AelResultadoCaracteristica resultadoCaracteristica) {
		DetachedCriteria criteria = criarCriteria(resultadoCaracteristica);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria criarCriteria(AelResultadoCaracteristica elemento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCaracteristica.class);
	    
		if(elemento.getSeq() != null){
			criteria.add(Restrictions.eq(AelResultadoCaracteristica.Fields.SEQ.toString(), elemento.getSeq()));
		}
		
		if(StringUtils.isNotBlank(elemento.getDescricao())){
			criteria.add(Restrictions.ilike(AelResultadoCaracteristica.Fields.DESCRICAO.toString(), elemento.getDescricao().trim(), MatchMode.ANYWHERE));
		}
		
		if(elemento.getIndSituacao() != null){
			criteria.add(Restrictions.eq(AelResultadoCaracteristica.Fields.SITUACAO.toString(), elemento.getIndSituacao()));
		}
		
		return criteria;
	}
	
	public List<AelResultadoCaracteristica> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelResultadoCaracteristica elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteria(criteria, firstResult, maxResult, AelResultadoCaracteristica.Fields.DESCRICAO.toString(), true);
	}
	
	
	/**
	 * Verifica a existência de alguma dependência
	 * @param elemento
	 * @param classeDependente
	 * @param fieldChaveEstrangeira
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("rawtypes")
	public final boolean existeDependencia(Object elemento, Class classeDependente, Enum fieldChaveEstrangeira) throws BaseException{

		CoreUtil.validaParametrosObrigatorios(elemento,classeDependente, fieldChaveEstrangeira);

		DetachedCriteria criteria = DetachedCriteria.forClass(classeDependente);
		criteria.add(Restrictions.eq(fieldChaveEstrangeira.toString(), elemento));
		
		return executeCriteriaCount(criteria) > 0;
		
	}
	
	/**
	 * 
	 * @param descricao
	 * @return
	 */
	public boolean obterResultadoCaracteristicaPorDescricao(String descricao, Integer seq, Boolean isUpdate) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelResultadoCaracteristica.class);
		
		criteria.add(Restrictions.eq(AelResultadoCaracteristica.Fields.DESCRICAO.toString(), descricao.trim()));
		
		if(isUpdate) {
			criteria.add(Restrictions.ne(AelResultadoCaracteristica.Fields.SEQ.toString(), seq));
		}
		
		return executeCriteriaCount(criteria) > 0;
		
	}

	/**
	 * Consulta C31 de Web Service #39353
	 * 
	 * @param seq
	 * @return
	 */
	public String obterDescricao(final Integer seq) {
		AelResultadoCaracteristica result = super.obterPorChavePrimaria(seq);
		if (result != null) {
			return result.getDescricao();
		}
		return null;
	}

}
