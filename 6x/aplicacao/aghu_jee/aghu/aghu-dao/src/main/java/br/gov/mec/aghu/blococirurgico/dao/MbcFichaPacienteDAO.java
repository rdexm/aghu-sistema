package br.gov.mec.aghu.blococirurgico.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaPaciente;

public class MbcFichaPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaPaciente> {

	private static final long serialVersionUID = -2971388765905302224L;

	public List<MbcFichaPaciente> pesquisarMbcFichasPacientes(Long seqMbcFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaPaciente.class);
		criteria.add(Restrictions.eq(MbcFichaPaciente.Fields.FICHA_ANESTESIA_SEQ.toString(), seqMbcFichaAnestesia));
		
		return executeCriteria(criteria);
	}

	public BigDecimal calcularSuperficieCorporalDoPaciente(Integer codPaciente) {
		StringBuilder hql = new StringBuilder(200);
		hql.append("select (power(p.peso,0.425) * power(p.altura*100,0.725) *0.007184) from MbcFichaPaciente p ");

			hql.append("where " +
					"p." + MbcFichaPaciente.Fields.FICHA_ANESTESIA.toString() + 
					 "." + MbcFichaAnestesias.Fields.PAC_CODIGO.toString() + 
					 " = " + codPaciente);

		hql.append(" order by p.mbcFichaAnestesias.paciente.prontuario");
		List result = createHibernateQuery(hql.toString()).list();
		if(result != null && !result.isEmpty()){
			return new BigDecimal((Double) result.get(0));
		}
		
		return null;
		
		
	}	



}
