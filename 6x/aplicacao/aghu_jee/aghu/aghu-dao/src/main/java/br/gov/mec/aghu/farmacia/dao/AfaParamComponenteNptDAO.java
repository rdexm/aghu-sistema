package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaParamComponenteNpt;


public class AfaParamComponenteNptDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaParamComponenteNpt> {

	private static final long serialVersionUID = 4070084250474804889L;

	
	// #3504 C5
	public List<AfaParamComponenteNpt> listarPorMatCodigo(Integer cod) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaParamComponenteNpt.class);
	
		criteria.add(Restrictions.eq(AfaParamComponenteNpt.Fields.CNP_MED_MAT_CODIGO.toString(), cod));
		criteria.addOrder(Order.asc(AfaParamComponenteNpt.Fields.UMM_SEQ.toString()));
		criteria.addOrder(Order.asc(AfaParamComponenteNpt.Fields.IND_SITUACAO.toString()));
		//criteria.setResultTransformer(Transformers.aliasToBean(ComponenteNPTVO.class));
		return this.executeCriteria(criteria);
	}

	public Short obterParamComponenteCount(Integer componenteSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaParamComponenteNpt.class);
		criteria.add(Restrictions.eq(AfaParamComponenteNpt.Fields.CNP_MED_MAT_CODIGO.toString(), componenteSeq));
		criteria.addOrder(Order.desc(AfaParamComponenteNpt.Fields.SEQP.toString()));
		List<AfaParamComponenteNpt> lista = this.executeCriteria(criteria);
		if(lista != null){
			if(lista.size() > 0){
				return lista.get(0).getId().getSeqp();
			}
		}
		return (short)0;
	}
	
	public List<AfaParamComponenteNpt> obterAtivosPorMatCodigo(Integer cod, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaParamComponenteNpt.class);
		
		criteria.add(Restrictions.eq(AfaParamComponenteNpt.Fields.CNP_MED_MAT_CODIGO.toString(), cod));
		criteria.add(Restrictions.eq(AfaParamComponenteNpt.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if(seqp != null){
			criteria.add(Restrictions.ne(AfaParamComponenteNpt.Fields.SEQP.toString(), seqp));
		}
		
		return this.executeCriteria(criteria);
	}
	
	public List<AfaParamComponenteNpt> listarAfaParamComponenteNtpsCalculoNptAtivos() {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaParamComponenteNpt.class, "PCN");
		criteria.createAlias("PCN." + AfaParamComponenteNpt.Fields.COMPONENTE_NPT.toString(), "CNP");
		criteria.add(Restrictions.eq("CNP." + AfaComponenteNpt.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PCN." + AfaParamComponenteNpt.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria);

	}
	
}
