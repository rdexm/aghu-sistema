package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

public class ScoMaterialJNDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMaterialJN> {

	private static final long serialVersionUID = 2011216945259754159L;

	
	private DetachedCriteria obterCriteriaBasica(Integer codigoMaterial, DominioOperacoesJournal operacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterialJN.class);
		criteria.createAlias(ScoMaterialJN.Fields.SERVIDOR_ALTERACAO.toString(), "SERV_ALT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_ALT."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES_FIS_SERV_ALT", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoMaterialJN.Fields.CODIGO.toString(),codigoMaterial));
		if(operacao != null){
			criteria.add(Restrictions.eq(ScoMaterialJN.Fields.OPERACAO.toString(), operacao));
		}
		return criteria;

	}
	
	public List<ScoMaterialJN> pesquisarScoMaterialJNPorCodigoMaterial(Integer codigoMaterial, DominioOperacoesJournal operacao) {
		DetachedCriteria criteria = obterCriteriaBasica(codigoMaterial,operacao);
		criteria.addOrder(Order.desc(ScoMaterialJN.Fields.SEQ_JN.toString()));
		return executeCriteria(criteria);

	}

	public ScoMaterialJN obterScoMaterialJNPorCodigoMaterialSeqJN(
			Integer codigoMaterial, Integer seqJn) {

		DetachedCriteria criteria = obterCriteriaBasica(codigoMaterial,null);
		criteria.add(Restrictions.lt(ScoMaterialJN.Fields.SEQ_JN.toString(), seqJn));
		criteria.addOrder(Order.desc(ScoMaterialJN.Fields.SEQ_JN.toString()));
		List<ScoMaterialJN> lista = executeCriteria(criteria);
		if (lista == null || lista.isEmpty()) {
			return null;
		}
		return lista.get(0);
	}
	
}
