package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatCaractComplexidade;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;

public class FatCaractComplexidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCaractComplexidade> {
	
	private static final long serialVersionUID = 6124846320147461694L;
	
	/**
	 * Lista complexidades ativas.
	 * 
	 * @param pesquisa
	 * @return
	 */
	public List<FatCaractComplexidade> listarComplexidadesAtivasPorCodigoOuDescricao(
			Object pesquisa) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCaractComplexidade.class);

		String strParametro = (String) pesquisa;

		if (StringUtils.isNotBlank(strParametro)) {

			criteria.add(Restrictions.eq(
					FatCaractComplexidade.Fields.CODIGO.toString(),
					strParametro));
		}

		criteria.add(Restrictions.eq(
				FatCaractComplexidade.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));


		criteria.addOrder(Order.asc(FatCaractComplexidade.Fields.DESCRICAO
				.toString()));

		List<FatCaractComplexidade> result = this.executeCriteria(criteria);

		if (result == null || result.size() == 0) {
			criteria = DetachedCriteria.forClass(FatCaractComplexidade.class);

			criteria.add(Restrictions.ilike(
					FatCaractComplexidade.Fields.DESCRICAO.toString(),
					strParametro, MatchMode.ANYWHERE));

			criteria.add(Restrictions.eq(
					FatCaractComplexidade.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));

			criteria.addOrder(Order.asc(FatCaractComplexidade.Fields.DESCRICAO
					.toString()));

			result = this.executeCriteria(criteria);
		}
		return result;
	}
	
	public FatCaractComplexidade obterCaractComplexidadePorSeqEPhoSeqECodTabela(
			Short iphPhoSeq, Integer iphSeq, Long codTabela) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCaractComplexidade.class);
		criteria.createAlias(
				FatCaractComplexidade.Fields.FAT_ITENS_PROCEDIMENTOS_HOSPITALARES
						.toString(), "IPH");

		criteria.add(Restrictions.eq("IPH."
				+ FatItensProcedHospitalar.Fields.SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("IPH."
				+ FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("IPH."
				+ FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
				codTabela));

		return (FatCaractComplexidade) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Lista as Caracteristicas De Complexidade por código e/ou descricao para suggestionBox
	 *  
	 * @param filtro
	 * @return
	 */
	public List<FatCaractComplexidade> listaCaracteristicasDeComplexidade(
			final Object caractComplexidade) {
		
		DetachedCriteria criteria = getCriteriaCodigoOuCodigoPai(caractComplexidade, true);
		
		criteria.addOrder(Order.asc(FatCaractComplexidade.Fields.CODIGO.toString()));
		
		return executeCriteria(criteria);
	}

	public Long listaCaracteristicasDeComplexidadeCount(final Object caractComplexidade) {

		return executeCriteriaCount(getCriteriaCodigoOuCodigoPai(caractComplexidade, true));
	}

	/**
	 * Adicionar ordem a uma Detached Criteria.
	 * 
	 * @param criteria
	 * @param orderProperty
	 * @param asc
	 */
	private void addOrder(final DetachedCriteria criteria, String orderProperty, boolean asc) {

		if (orderProperty != null && StringUtils.isNotBlank(orderProperty)) {
			criteria.addOrder(asc ? Order.asc(orderProperty) : Order.desc(orderProperty));
		}
	}
	
	/**
	 * Remove o % da string
	 * 
	 * @param descricao
	 * @return
	 */
	private String replaceCaracterEspecial(String descricao) {
        
	       return descricao.replace("_", "\\_").replace("%", "\\%");
	}
	
	public FatCaractComplexidade obterPorCodigoSus(Integer seqSus) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractComplexidade.class);
		
		if (seqSus != null) {
			criteria.add(Restrictions.eq(FatCaractComplexidade.Fields.SEQ_SUS.toString(), seqSus));
		}
		List<FatCaractComplexidade> lista = this.executeCriteria(criteria,0,1,null,false);
		if(lista != null){
			if(lista.size() > 0){
				return lista.get(0);
			}
		}
		return null;
	}
	
	/**
	 * Criteria para listar os motivos de pendencias por seq e descricao
	 * 
	 * @param seq
	 * @param descricao
	 * @return
	 */
	private DetachedCriteria getCriteriaCodigoOuCodigoPai(Object caractComplexidade, boolean suggestionBox) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractComplexidade.class, "FCC");

		if(suggestionBox){
			String strParametro = (String) caractComplexidade;

			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.or(Restrictions.ilike(FatCaractComplexidade.Fields.CODIGO.toString(), this.replaceCaracterEspecial(strParametro), MatchMode.ANYWHERE), 
				Restrictions.ilike(FatCaractComplexidade.Fields.DESCRICAO.toString(), this.replaceCaracterEspecial(strParametro), MatchMode.ANYWHERE)));
			}
			
		} else{
			
			FatCaractComplexidade fatParametro = (FatCaractComplexidade) caractComplexidade;
			criteria.createAlias("FCC." + FatCaractComplexidade.Fields.FAT_CARACT_COMPLEXIDADE.toString(), "PAI", JoinType.LEFT_OUTER_JOIN);

			if (StringUtils.isNotBlank(fatParametro.getCodigo())) {
				criteria.add(Restrictions.eq("FCC." + FatCaractComplexidade.Fields.CODIGO.toString(), fatParametro.getCodigo()));
			}
			
			if (fatParametro.getFatCaractComplexidade() != null) {
				criteria.add(Restrictions.eq("PAI." + FatCaractComplexidade.Fields.SEQ.toString(), fatParametro.getFatCaractComplexidade().getSeq()));
			}
			
			if (fatParametro.getIndSituacao() != null) {
				criteria.add(Restrictions.eq("FCC." + FatCaractComplexidade.Fields.IND_SITUACAO.toString(), fatParametro.getIndSituacao()));
			}
		}

		return criteria;
	}
	
	/**
	 * Pesuisa Caracteristicas De Complexidade
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param motivoPendencia
	 * @return
	 */
	public List<FatCaractComplexidade> pesquisarCaracteristicasDeComplexidade(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FatCaractComplexidade caractComplexidade) {
		DetachedCriteria criteria = getCriteriaCodigoOuCodigoPai(caractComplexidade, false);

		addOrder(criteria, orderProperty, asc);
		
		return this.executeCriteria(criteria, firstResult, maxResult, null, asc);
	}
	
	public Long pesquisarCaracteristicasDeComplexidadeCount(FatCaractComplexidade caractComplexidade) {
		DetachedCriteria criteria = getCriteriaCodigoOuCodigoPai(caractComplexidade, false);
		return this.executeCriteriaCount(criteria);
	}

}
