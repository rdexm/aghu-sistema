package br.gov.mec.aghu.prescricaomedica.dao;



import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaImpDiagnostica;
import br.gov.mec.aghu.model.MpmAltaSumario;



/**
 * 
 * @author dansantos
 *
 */
public class MpmAltaImpDiagnosticaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaImpDiagnostica> {
    
	private static final long serialVersionUID = -2248357916158888931L;

	public List<MpmAltaImpDiagnostica> pesquisarAltaImpDiagnosticaPorAtendimento(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaImpDiagnostica.class);
		criteria.createAlias(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString(), MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString());
		criteria.add(Restrictions.eq(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString()+"."+MpmAltaSumario.Fields.APA_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString()+"."+MpmAltaSumario.Fields.EMERGENCIA.toString(), true));
		criteria.add(Restrictions.eq(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString()+"."+MpmAltaSumario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString()+"."+MpmAltaSumario.Fields.IND_CONCLUIDO.toString(), DominioIndConcluido.S));
		criteria.add(Restrictions.or(Restrictions.eq(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString()+"."+MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.ALT), Restrictions.eq(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString()+"."+MpmAltaSumario.Fields.IND_TIPO.toString(), DominioIndTipoAltaSumarios.OBT)));
		criteria.add(Restrictions.or(Restrictions.isNull(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString()+"."+MpmAltaSumario.Fields.ESTORNO.toString()), Restrictions.eq(MpmAltaImpDiagnostica.Fields.ALTA_SUMARIO.toString()+"."+MpmAltaSumario.Fields.ESTORNO.toString(), false)));
		criteria.addOrder(Order.asc(MpmAltaImpDiagnostica.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public List<MpmAltaImpDiagnostica> listarAltaImpDiagnosticaPorIdSemSeqp(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaImpDiagnostica.class);
		criteria.add(Restrictions.eq(MpmAltaImpDiagnostica.Fields.ASU_APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaImpDiagnostica.Fields.ASU_APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaImpDiagnostica.Fields.ASU_SEQP.toString(), asuSeqp));
		return executeCriteria(criteria);
	}
}
