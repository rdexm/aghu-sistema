package br.gov.mec.aghu.internacao.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.VIndIntPacienteDia;

public class VIndIntPacienteDiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VIndIntPacienteDia> {
	
	private static final long serialVersionUID = 2633768617275653718L;

	public Object[] obterPacienteDia(Integer matricula, Short vinculo, Short seqEspecialidade) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VIndIntPacienteDia.class);
		
		// Colunas selecionadas e somadores 
		// NÃO ALTERAR ORDEM DAS PROJECTIONS SEM AVALIAR MÉTODOS QUE UTILIZAM ESSA QUERY
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.groupProperty(VIndIntPacienteDia.Fields.SER_MATRICULA.toString()));
		pList.add(Projections.groupProperty(VIndIntPacienteDia.Fields.SER_VINCULO.toString()));
		pList.add(Projections.groupProperty(VIndIntPacienteDia.Fields.ESP_SEQ.toString()));
		pList.add(Projections.sum(VIndIntPacienteDia.Fields.PACIENTE_DIA.toString()));
		pList.add(Projections.sum(VIndIntPacienteDia.Fields.SAIDA_MOVIMENTACAO_ESPECIALIDADE.toString()));
		pList.add(Projections.sum(VIndIntPacienteDia.Fields.SAIDA_MOVIMENTACAO_CLINICA.toString()));
		pList.add(Projections.sum(VIndIntPacienteDia.Fields.SAIDA_MOVIMENTACAO_EQUIPE.toString()));
		pList.add(Projections.sum(VIndIntPacienteDia.Fields.SAIDA_MOVIMENTACAO_UNIDADE.toString()));
		pList.add(Projections.sum(VIndIntPacienteDia.Fields.SAIDA.toString()));
		criteria.setProjection(pList);

		// Restricoes
		criteria.add(Restrictions.eq(VIndIntPacienteDia.Fields.SER_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(VIndIntPacienteDia.Fields.SER_VINCULO.toString(), vinculo));
		criteria.add(Restrictions.eq(VIndIntPacienteDia.Fields.ESP_SEQ.toString(), seqEspecialidade));
		
		return (Object[]) this.executeCriteriaUniqueResult(criteria);
	}

}
