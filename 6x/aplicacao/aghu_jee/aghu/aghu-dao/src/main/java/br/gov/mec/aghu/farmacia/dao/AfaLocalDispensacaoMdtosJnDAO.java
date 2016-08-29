package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;

/**
 * Acesso a dados para persistencia de Journal de AfaLocalDispensacaoMdtosJn
 * Herda os métodos de persistencia da GenericDAO
 * 
 * @author sedimar.bortolin
 *
 */
public class AfaLocalDispensacaoMdtosJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaLocalDispensacaoMdtosJn> {

	private static final long serialVersionUID = 5777494060281115455L;

	public List<AfaLocalDispensacaoMdtosJn> pesquisarLocalDispensacaoMdtosJn(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento medicamento) {
		DetachedCriteria criteria = obterCriteriaLocalDispensacaoMdtosJn(medicamento,false);
		
		List<AfaLocalDispensacaoMdtosJn> jj = this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
		return jj;
	}
	
	public Long pesquisarLocalDispensacaoMdtosJnCount(AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = obterCriteriaLocalDispensacaoMdtosJn(medicamento,true);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaLocalDispensacaoMdtosJn(AfaMedicamento medicamento, Boolean pesquisaCount){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaLocalDispensacaoMdtosJn.class);
		
		criteria.createAlias(AfaLocalDispensacaoMdtosJn.Fields.UNIDADE_FUNCIONAL.toString(), "unidade", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unidade."+AghUnidadesFuncionais.Fields.ALA.toString(), "ala", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AfaLocalDispensacaoMdtosJn.Fields.UNF_SEQ_DISP_DOSE_INT.toString(), "unidadeFuncionalDispDoseInt", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtosJn.Fields.UNF_SEQ_DISP_DOSE_FRAC.toString(), "unidadeFuncionalDispDoseFrac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtosJn.Fields.UNF_SEQ_DISP_ALTERNATIVA.toString(), "unidadeFuncionalDispAlternativa", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtosJn.Fields.UNF_SEQ_DISP_USO_DOMICILIAR.toString(), "unidadeFuncionalDispUsoDomiciliar", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AfaLocalDispensacaoMdtosJn.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoa", JoinType.LEFT_OUTER_JOIN);
		
		
		criteria.add(Restrictions.eq(AfaLocalDispensacaoMdtosJn.Fields.MED_MAT_CODIGO.toString(), 
				medicamento));
		
		if (!pesquisaCount) {
			criteria.addOrder(Order.asc(AfaLocalDispensacaoMdtosJn.Fields.JN_DATE_TIME.toString()));
		}
				
		return criteria;
		
	}
}
