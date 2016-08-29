package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FatResumoApacs;

public class FatResumoApacsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatResumoApacs> {

	private static final long serialVersionUID = -5482817625443404692L;

	public List<FatResumoApacs> listarResumosApacsPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatResumoApacs.class);

		criteria.add(Restrictions.eq(FatResumoApacs.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<Date> buscarDataFinalResumosApacsAtivos(final Integer pacCodigo, final Date dtRealizado, final DominioSimNao indAparelho) {
		DetachedCriteria criteria = obterCriteriaBuscarDataFinalResumosApacsAtivos(pacCodigo, dtRealizado, indAparelho);
		// ORDER BY dt_final DESC, atm_numero DESC
		criteria.addOrder(Order.desc(FatResumoApacs.Fields.DT_FINAL.toString()));
		criteria.addOrder(Order.desc(FatResumoApacs.Fields.ATM_NUMERO.toString()));
		criteria.setProjection(Projections.property(FatResumoApacs.Fields.DT_FINAL.toString()));
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaBuscarDataFinalResumosApacsAtivos(final Integer pacCodigo, final Date dtRealizado,
			final DominioSimNao indAparelho) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatResumoApacs.class);

		criteria.add(Restrictions.eq(FatResumoApacs.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ge(FatResumoApacs.Fields.DT_FINAL.toString(), dtRealizado));
		criteria.add(Restrictions.eq(FatResumoApacs.Fields.IND_APARELHO.toString(), indAparelho));
		return criteria;
	}
	
	/*
	 * #42803
	 * ORADB: c_busca_apac_aparelho
	 */	
	public List<FatResumoApacs> buscarDatasResumosApacsAtivos(final Integer pacCodigo, final Date dtFinal){
		DetachedCriteria criteria = obterCriteriaBuscaAparelho(pacCodigo);
		
		criteria.add(Restrictions.le(FatResumoApacs.Fields.DT_FINAL.toString(), dtFinal));
		criteria.addOrder(Order.desc(FatResumoApacs.Fields.DT_INICIO.toString()));
//		criteria.setProjection(Projections.property(FatResumoApacs.Fields.DT_FINAL.toString()));

		return executeCriteria(criteria);		
	}

	private DetachedCriteria obterCriteriaBuscaAparelho(final Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatResumoApacs.class);
		criteria.add(Restrictions.eq(FatResumoApacs.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(FatResumoApacs.Fields.IND_APARELHO.toString(), DominioSimNao.S));
		return criteria;
	}
	
	/*
	 * #42803
	 * ORADB: c_busca_aparelho_rao
	 */
	
	public Integer buscarAparelho(final Integer pacCodigo){
		DetachedCriteria criteria = obterCriteriaBuscaAparelho(pacCodigo);
		
		if(isOracle()){ //ORADB
			criteria.setProjection(Projections.sqlProjection("nvl(sum(nvl('" + FatResumoApacs.Fields.QUANTIDADE_APARELHO.toString() + "',0)),0)", new String[]{}, new Type[]{}));
		}else{
			criteria.setProjection(Projections.sqlProjection("coalesce(sum(coalesce('" + FatResumoApacs.Fields.QUANTIDADE_APARELHO.toString() + "',0)),0)", new String[]{}, new Type[]{}));
		}
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	/* #42803
	 * Modificada 18/11/2014
	 * ORADB c_busca_resumo
	 */
	public FatResumoApacs buscaResumo(final Integer pacCodigo, Integer diagnostico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatResumoApacs.class);
		
		criteria.add(Restrictions.eq(FatResumoApacs.Fields.PAC_CODIGO.toString(), pacCodigo));
		if (diagnostico != null) {
			criteria.add(Restrictions.eq(FatResumoApacs.Fields.PHI_SEQ.toString(), diagnostico));
		}
		
		criteria.addOrder(Order.desc(FatResumoApacs.Fields.DT_INICIO.toString()));
		criteria.addOrder(Order.desc(FatResumoApacs.Fields.DT_FINAL.toString()));
		criteria.addOrder(Order.desc(FatResumoApacs.Fields.ATM_NUMERO.toString()));

		List<FatResumoApacs> retorno = executeCriteria(criteria);
		if (retorno.isEmpty()) {
			return null;
		}
		
		return retorno.get(0);
	}
	
	public List<FatResumoApacs> obterApacs(final Integer pacCodigo, Integer phi, Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatResumoApacs.class);
		
		criteria.add(Restrictions.eq(FatResumoApacs.Fields.PAC_CODIGO.toString(), pacCodigo));
		if(isOracle()){
			criteria.add(Restrictions.sqlRestriction(" trunc(months_between( TO_DATE('"+ data.toString().substring(0, data.toString().length()-2) + "', 'yyyy-mm-dd hh24:mi:ss'), this_.dt_final))<12 "));
		}else{
			criteria.add(Restrictions.sqlRestriction("(SELECT EXTRACT(year FROM age('"+ data + "', this_.dt_final)) > 0)"));
		}
		
		return executeCriteria(criteria);
	}
	
	/** #42803
	 * consulta da procedure ver_apac_aparelho/ cursor c_busca_qtde_aparelho
	 * @param pacCodigo
	 * @param dataFinal
	 * @return quant_apa
	 */
	
	public Long buscarQuantidadeAparelho(Integer pacCodigo, Date dataFinal){
		DetachedCriteria criteria = obterCriteriaBuscaAparelho(pacCodigo);
		
		criteria.add(Restrictions.le(FatResumoApacs.Fields.DT_FINAL.toString(), dataFinal));
		criteria.setProjection(Projections.sum(FatResumoApacs.Fields.QUANTIDADE_APARELHO.toString()));
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}
		
	/**
	 * ORADB busca_qtde_apac
	 * @param codigo
	 * @param listaDecode
	 * @return List<FatResumoApacs>
	 */
	public List<FatResumoApacs> obterQuantidadeApac(Integer codigo, List<Integer> listaDecode){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatResumoApacs.class);
		
		criteria.add(Restrictions.eq(FatResumoApacs.Fields.PAC_CODIGO.toString(), codigo));
		criteria.add(Restrictions.in(FatResumoApacs.Fields.PHI_SEQ.toString(), listaDecode));
		
		return executeCriteria(criteria);
	}
}
