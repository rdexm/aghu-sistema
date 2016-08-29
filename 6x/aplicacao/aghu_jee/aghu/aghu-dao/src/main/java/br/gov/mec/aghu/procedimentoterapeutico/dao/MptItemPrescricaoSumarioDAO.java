package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.model.MptItemPrescricaoSumario;


public class MptItemPrescricaoSumarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptItemPrescricaoSumario> {

	private static final long serialVersionUID = -514131677809520550L;

	public List<MptItemPrescricaoSumario> pesquisarItemPrescricaoSumario(Integer intAtdSeq, Integer apaSeq, String sintaxe, DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptItemPrescricaoSumario.class);
		
		criteria.add(Restrictions.eq(MptItemPrescricaoSumario.Fields.ATENDIMENTO_PACIENTES_ID_ATD_SEQ.toString(), intAtdSeq));
		criteria.add(Restrictions.eq(MptItemPrescricaoSumario.Fields.ATENDIMENTO_PACIENTES_ID_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MptItemPrescricaoSumario.Fields.TIPO.toString(), dominioTipoItemPrescSum.getCodigo()));	
		criteria.add(Restrictions.eq(MptItemPrescricaoSumario.Fields.DESCRICAO.toString(), sintaxe));
		
		return executeCriteria(criteria);
	}

}
