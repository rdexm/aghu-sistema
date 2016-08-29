package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamRegistrosDAO extends BaseDao<MamRegistro> {

	private static final long serialVersionUID = 824892726430400298L;
	
	private static final String MAM_REGISTRO = "MamRegistro."; 
	
	/**
	 * Obter registro utilziando uma triagem
	 */
	public List<MamRegistro> obterRegistroPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(
				MamRegistro.class, "MamRegistro");
		criteria.add(Restrictions.eq(MAM_REGISTRO
				+ MamRegistro.Fields.MVM_TRG.toString(), trgSeq));
		criteria.addOrder(Order.desc(MamRegistro.Fields.SEQ.toString()));
		return this.executeCriteria(criteria);
	}
	
	/**
	 * #28986 - C15
	 * @param seq
	 * @return
	 */
	public DominioTipoFormularioAlta obterTipoFormularioAlta(Long seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamRegistro.class);
		criteria.add(Restrictions.eq(MamRegistro.Fields.SEQ.toString(), seq));
		criteria.setProjection(Projections.property(MamRegistro.Fields.TIPO_FORMULARIO_ALTA.toString()));
		return (DominioTipoFormularioAlta) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 *  #28986 - C4
	 *  obter registro por atendimento
	 */
	public List<MamRegistro> obterRegistroPorAtendimento(Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamRegistro.class);
		criteria.add(Restrictions.eq(MamRegistro.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(MamRegistro.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}
	
	public MamRegistro obterRegistroComSituacaoPendentePorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamRegistro.class, "MamRegistro");
		criteria.add(Restrictions.eq(MAM_REGISTRO + MamRegistro.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq(MAM_REGISTRO + MamRegistro.Fields.IND_SITUACAO.toString(), DominioSituacaoRegistro.PE.toString()));
		criteria.add(Restrictions.eq(MAM_REGISTRO + MamRegistro.Fields.MVM_TRG.toString(), trgSeq));
		final DetachedCriteria subQuery = DetachedCriteria.forClass(MamRegistro.class, "rg");
		subQuery.setProjection(Projections.max("rg."+ MamRegistro.Fields.SEQ.toString()));
		criteria.add(Restrictions.eqProperty("rg." + MamRegistro.Fields.TRG_SEQ.toString(), MAM_REGISTRO + MamRegistro.Fields.TRG_SEQ.toString()));
		criteria.add(Subqueries.in(MAM_REGISTRO + MamRegistro.Fields.SEQ.toString(), subQuery));
			
		return (MamRegistro) this.executeCriteriaUniqueResult(criteria);		
	}
}
