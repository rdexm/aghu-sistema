package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipAlergiaPacientes;
import br.gov.mec.aghu.model.AipPacientes;

public class AipAlergiaPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipAlergiaPacientes> {

	private static final long serialVersionUID = 2570882585429996664L;

	private static final String AP = "AP.";
	
	public List<AipAlergiaPacientes> listarAlergiasPacientesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipAlergiaPacientes.class);

		criteria.add(Restrictions.eq(AipAlergiaPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<AipAlergiaPacientes> obterAipAlergiasPacientes(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipAlergiaPacientes.class, "AP");
		criteria.createAlias(AP+AipAlergiaPacientes.Fields.AUS_SEQ_JOIN, "AL", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AP+AipAlergiaPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.asc(AP+AipAlergiaPacientes.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc(AP+AipAlergiaPacientes.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<AipAlergiaPacientes> obterAipAlergiasPacientesHistorico(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipAlergiaPacientes.class, "AP");
		criteria.createAlias(AP+AipAlergiaPacientes.Fields.PACIENTE.toString(), "P");
		criteria.add(Restrictions.eq(AP+AipAlergiaPacientes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.asc(AP+AipAlergiaPacientes.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #44179 - C1
	 * @author marcelo.deus 
	 */
	public boolean verificaExistenciaAlergiaCadastradaPaciente(Integer atendimentoSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipAlergiaPacientes.class, "AAP");
		
		criteria.createAlias("AAP." + AipAlergiaPacientes.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("PAC." + AipPacientes.Fields.ATENDIMENTOS.toString(), "AIP");
		
		criteria.add(Restrictions.eq("AAP." + AipAlergiaPacientes.Fields.IND_SITUACAO.toString(), "V"))
				.add(Restrictions.eq("AIP." + AghAtendimentos.Fields.SEQ.toString(), atendimentoSeq));
		
		return executeCriteriaExists(criteria);
	}
	
}
