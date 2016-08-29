package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.RapServidores;

public class MpmAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAnamneses> {

	/**
	* 
	*/
	private static final long serialVersionUID = 3564901376957447982L;
	
	public MpmAnamneses obterAnamneseValidaParaAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAnamneses.class, "MAN");
		criteria.createAlias("MAN." + MpmAnamneses.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("MAN." + MpmAnamneses.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("MAN.".concat(MpmAnamneses.Fields.IND_PENDENTE.toString()),
				DominioIndPendenteAmbulatorio.V));
		return (MpmAnamneses) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean existeAnamneseValidaParaAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmAnamneses.class, "MAN");
		criteria.createAlias(
		                "MAN." + MpmAnamneses.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq(
		                "ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(
		                "MAN.".concat(MpmAnamneses.Fields.IND_PENDENTE.toString()),
		                DominioIndPendenteAmbulatorio.V));
		return executeCriteriaExists(criteria);
	}
	
	public MpmAnamneses obterAnamneseAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmAnamneses.class, "MAN");
		criteria.createAlias("MAN." + MpmAnamneses.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "CAR");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		return (MpmAnamneses) executeCriteriaUniqueResult(criteria);
	}
	
	public MpmAnamneses obterAnamneseValidadaPorAnamneses(Long atdAna) {
		DetachedCriteria criteria = DetachedCriteria
		                .forClass(MpmAnamneses.class);
		criteria.add(Restrictions.eq(MpmAnamneses.Fields.SEQ.toString(), atdAna));
		criteria.add(Restrictions.eq(
		                MpmAnamneses.Fields.IND_PENDENTE.toString(),
		                DominioIndPendenteAmbulatorio.V));
		return (MpmAnamneses) executeCriteriaUniqueResult(criteria);
	}
	
	public boolean verificarAnamneseValida(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmAnamneses.class, "ANA");
		criteria.add(Restrictions.eq(
		                "ANA." + MpmAnamneses.Fields.SEQ.toString(), anaSeq));
		criteria.add(Restrictions.eq(
		                "ANA." + MpmAnamneses.Fields.IND_PENDENTE.toString(),
		                DominioIndPendenteAmbulatorio.V));
		return executeCriteriaExists(criteria);
	}

	public MpmAnamneses obterAnamneseDetalhamento(MpmAnamneses anamnese) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAnamneses.class);		
		criteria.createAlias(MpmAnamneses.Fields.ATENDIMENTO.toString() , "ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.SERVIDOR.toString(), "RAP", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmAnamneses.Fields.SEQ.toString(), anamnese.getSeq()));
		return (MpmAnamneses) executeCriteriaUniqueResult(criteria);
	}
	
	public MpmAnamneses obterUnidadeFuncionalAtendimentoAnamnese(MpmAnamneses anamnese) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAnamneses.class);		
		criteria.createAlias(MpmAnamneses.Fields.ATENDIMENTO.toString() , "ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MpmAnamneses.Fields.SEQ.toString(), anamnese.getSeq()));
		return (MpmAnamneses) executeCriteriaUniqueResult(criteria);
	}
	
}
