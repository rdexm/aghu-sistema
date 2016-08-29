package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.model.MpmUnidadeTempo;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

public class AbsComponenteSanguineoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsComponenteSanguineo> {

	private static final long serialVersionUID = 1910370935521900552L;

	/**
	 * Obtem os Componentes Sanguineos que tem status Ativo
	 * 
	 * @return List<AbsComponenteSanguineo>
	 */
	public List<AbsComponenteSanguineo> obterComponetesSanguineosAtivos() {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsComponenteSanguineo.class);

		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_PERMITE_PRESCRICAO
				.toString(), true));

		return executeCriteria(criteria);
	}
	
	
	/**
	 * Obtem os Componentes Sanguineos pelo codigo informado
	 * 
	 * @return AbsComponenteSanguineo
	 */
	public AbsComponenteSanguineo obterComponeteSanguineoPorCodigo(String codigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsComponenteSanguineo.class);

		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.CODIGO
				.toString(), codigo));

		return (AbsComponenteSanguineo) executeCriteriaUniqueResult(criteria);
	}
	
	
	  /**
	 * Metodo que monta uma criteria para pesquisar AbsComponenteSanguineos filtrando
	 *  pelo codigo.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaAbsComponenteSanguineosPorCodigo(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteSanguineo.class);
		String strPesquisa = (String) objPesquisa;
		
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(AbsComponenteSanguineo.Fields.CODIGO.toString(), strPesquisa, MatchMode.EXACT));
		}
		
		return criteria;
	}

	/**
	 * Metodo que monta uma criteria para pesquisar AbsComponenteSanguineos filtrando
	 *  pela descricao.
	 * @param objPesquisa
	 * @return
	 */
	private DetachedCriteria montarCriteriaAbsComponenteSanguineosPorDescricao(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteSanguineo.class);
		String strPesquisa = (String) objPesquisa;
		
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.ilike(AbsComponenteSanguineo.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
		}
		
		return criteria;
	}
	
	public List<AbsComponenteSanguineo> listarComponentesSuggestion(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteSanguineo.class);
		String strPesquisa = (String) objPesquisa;	
		
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.or(Restrictions.eq(AbsComponenteSanguineo.Fields.CODIGO
					.toString(), strPesquisa), Restrictions.ilike(AbsComponenteSanguineo.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}
		
		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria, 0, 100, AbsComponenteSanguineo.Fields.DESCRICAO.toString(), true);
	}
	
	public Long listarComponentesSuggestionCount(Object objPesquisa){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteSanguineo.class);
		String strPesquisa = (String) objPesquisa;	
		
		if(StringUtils.isNotBlank(strPesquisa)){
			criteria.add(Restrictions.or(Restrictions.eq(AbsComponenteSanguineo.Fields.CODIGO
					.toString(), strPesquisa), Restrictions.ilike(AbsComponenteSanguineo.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}
		
		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteriaCount(criteria);
	}
	

	/**
	 * Metodo para utilizado em suggestionBox para pesquisar por AbsComponenteSanguineos,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return List<AbsComponenteSanguineo>
	 */
	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineos(Object objPesquisa){
		List<AbsComponenteSanguineo> lista = null;
		DetachedCriteria criteria = montarCriteriaAbsComponenteSanguineosPorCodigo(objPesquisa);
		criteria.addOrder(Order.asc(AbsComponenteSanguineo.Fields.CODIGO.toString()));
		
		lista = executeCriteria(criteria);
		
		if(lista == null || lista.isEmpty()){
			criteria = montarCriteriaAbsComponenteSanguineosPorDescricao(objPesquisa);
			
			criteria.addOrder(Order.asc(AbsComponenteSanguineo.Fields.CODIGO.toString()));
			
			lista = executeCriteria(criteria, 0, 100, null, true);
		}
		
		return lista;
	}

	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineosNaoExisteNaCirurgia(Object objPesquisa, Integer crgSeq){
		List<AbsComponenteSanguineo> lista = null;
		DetachedCriteria criteria = montarCriteriaAbsComponenteSanguineosPorCodigo(objPesquisa);
		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AbsComponenteSanguineo.Fields.CODIGO.toString()));
		
		criarSubcriteriaAbsComponenteSanguineosNaoExisteNaCirurgia(crgSeq, criteria);
		
		lista = executeCriteria(criteria);
		
		if(lista == null || lista.isEmpty()){
			criteria = montarCriteriaAbsComponenteSanguineosPorDescricao(objPesquisa);
			criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			criarSubcriteriaAbsComponenteSanguineosNaoExisteNaCirurgia(crgSeq, criteria);
			criteria.addOrder(Order.asc(AbsComponenteSanguineo.Fields.CODIGO.toString()));
			
			lista = executeCriteria(criteria, 0, 100, null, true);
		}
		
		return lista;
	}


	private void criarSubcriteriaAbsComponenteSanguineosNaoExisteNaCirurgia(
			Integer crgSeq, DetachedCriteria criteria) {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MbcSolicHemoCirgAgendada.class);

		subCriteria.setProjection(Projections.projectionList().add(Projections.property(MbcSolicHemoCirgAgendada.Fields.ID_CSA_CODIGO.toString())));
		
		subCriteria.add(Restrictions.eq(MbcSolicHemoCirgAgendada.Fields.ID_CRG_SEQ.toString(), crgSeq));
				
		criteria.add(Subqueries.propertyNotIn(AbsComponenteSanguineo.Fields.CODIGO.toString(), subCriteria));
	}
	
	
	public Long listarAbsComponenteSanguineosNaoExisteNaCirurgiaCount(Object objPesquisa, Integer crgSeq){
		Long count = 0L;
		DetachedCriteria criteria = montarCriteriaAbsComponenteSanguineosPorCodigo(objPesquisa);
		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criarSubcriteriaAbsComponenteSanguineosNaoExisteNaCirurgia(crgSeq, criteria);
		count = executeCriteriaCount(criteria);
		if(count == null || count == 0){
			criteria = montarCriteriaAbsComponenteSanguineosPorDescricao(objPesquisa);
			criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			criarSubcriteriaAbsComponenteSanguineosNaoExisteNaCirurgia(crgSeq, criteria);
			count = executeCriteriaCount(criteria);
		}
		
		return count;
	}
	
	/**
	 * Metodo para obter o count. Utilizado em suggestionBox para pesquisar por AbsComponenteSanguineos,
	 * filtrando pela descricao ou pelo codigo.
	 * @param objPesquisa
	 * @return count
	 */
	public Long listarAbsComponenteSanguineosCount(Object objPesquisa){
		Long count = 0L;
		DetachedCriteria criteria = montarCriteriaAbsComponenteSanguineosPorCodigo(objPesquisa);
		count = executeCriteriaCount(criteria);
		if(count == null || count == 0){
			criteria = montarCriteriaAbsComponenteSanguineosPorDescricao(objPesquisa);
			executeCriteriaCount(criteria);
		}
		
		return count;
	}
	
	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineosAtivos(Object objPesquisa){
		List<AbsComponenteSanguineo> lista = null;
		DetachedCriteria criteria = montarCriteriaAbsComponenteSanguineosPorCodigo(objPesquisa);
		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AbsComponenteSanguineo.Fields.CODIGO.toString()));
		
		lista = executeCriteria(criteria);
		
		if(lista == null || lista.isEmpty()){
			criteria = montarCriteriaAbsComponenteSanguineosPorDescricao(objPesquisa);
			criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			criteria.addOrder(Order.asc(AbsComponenteSanguineo.Fields.CODIGO.toString()));
			
			lista = executeCriteria(criteria, 0, 100, null, true);
		}
		
		return lista;
	}

	public Long listarAbsComponenteSanguineosAtivosCount(Object objPesquisa){
		Long count = null;
		DetachedCriteria criteria = montarCriteriaAbsComponenteSanguineosPorCodigo(objPesquisa);
		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		count = executeCriteriaCount(criteria);
		
		if(count == null || count == 0){
			criteria = montarCriteriaAbsComponenteSanguineosPorDescricao(objPesquisa);
			criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
			
			count = executeCriteriaCount(criteria);
		}
		
		return count;
	}


	//Lista recuperada para paginaçao. 
	public List<AbsComponenteSanguineo> pesquisarComponentesSanguineos(Integer firstResult, Integer maxResult, String orderProperty, 
																	boolean asc, AbsComponenteSanguineo componenteSanguineo){
		
		DetachedCriteria criteria = montaCriteriaAbsComponentesSanguineos(componenteSanguineo);
		if (orderProperty == null){
			criteria.addOrder(Order.asc(AbsComponenteSanguineo.Fields.CODIGO.toString()));
		}
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Boolean verificaComponenteSanguineoExistente(String cod){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteSanguineo.class);
		criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.CODIGO.toString(), cod));
		return executeCriteriaExists(criteria);
	}

	
	//count da lista de paginaçao.
	public Long pesquisarComponentesSanguineoCount(AbsComponenteSanguineo componenteSanguineo){

		DetachedCriteria criteria = montaCriteriaAbsComponentesSanguineos(componenteSanguineo);
		
		return this.executeCriteriaCount(criteria);
	}
	
	//Criteria para paginação. Segundo parametros.
	public DetachedCriteria montaCriteriaAbsComponentesSanguineos(AbsComponenteSanguineo componenteSanguineo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteSanguineo.class);
		
		if(StringUtils.isNotBlank(componenteSanguineo.getCodigo())){
			criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.CODIGO.toString(), componenteSanguineo.getCodigo()));
		}
		
		if(StringUtils.isNotBlank(componenteSanguineo.getDescricao())){
			criteria.add(Restrictions.ilike(AbsComponenteSanguineo.Fields.DESCRICAO.toString(), componenteSanguineo.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if(componenteSanguineo.getNroDiasValidade()!= null){
			criteria.add(Restrictions.eq(AbsComponenteSanguineo.Fields.NRO_DIAS_VALIDADE.toString(), componenteSanguineo.getNroDiasValidade()));
		}
		
		if(componenteSanguineo.getIndSituacao() != null){
			criteria.add(Restrictions.eq(MpmUnidadeTempo.Fields.INDSITUACAO.toString(), componenteSanguineo.getIndSituacao()));
		}
		
		return criteria;
	}

	public List<LinhaReportVO> pesquisarComponentesSanguineosAgendas(Integer agdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsComponenteSanguineo.class, "csa");
		criteria.createAlias("csa."+AbsComponenteSanguineo.Fields.AGENDA_HEMOTERAPIAS.toString(),"ahe",Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq("ahe."+MbcAgendaHemoterapia.Fields.AGD_SEQ.toString(),agdSeq));
		criteria.addOrder(Order.asc(AbsComponenteSanguineo.Fields.DESCRICAO.toString()));
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("csa."+AbsComponenteSanguineo.Fields.DESCRICAO.toString()),		LinhaReportVO.Fields.TEXTO1.toString())
			.add(Projections.property("ahe."+MbcAgendaHemoterapia.Fields.QTDE_UNIDADE.toString()),		LinhaReportVO.Fields.NUMERO4.toString())
			.add(Projections.property("ahe."+MbcAgendaHemoterapia.Fields.QTDE_UNIDADE_ADIC.toString()),	LinhaReportVO.Fields.NUMERO5.toString())
		);
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		return executeCriteria(criteria); 
	}
}
