package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatPacienteTransplantes;

public class FatPacienteTransplantesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatPacienteTransplantes> {


	private static final long serialVersionUID = 7886094754403178549L;

	private DetachedCriteria obterDetachedCriteriaFatPacienteTransplantes() {
		return DetachedCriteria.forClass(FatPacienteTransplantes.class);
	}

	/**
	 * Consulta Pacientes transplantados filtra por PAC_CODIGO e TTR_CODIGO,
	 * ordena por ordem decrescente
	 * 
	 * @param pacCodigo
	 * @param ttrCodigo
	 * @return
	 */
	public FatPacienteTransplantes consultaPacTransp(Integer pacCodigo, String ttrCodigo) {
		DetachedCriteria criteria = obterDetachedCriteriaFatPacienteTransplantes();

		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.TTR_CODIGO.toString(), ttrCodigo));
		criteria.addOrder(Order.desc(FatPacienteTransplantes.Fields.DT_INSCRICAO_TRANSPLANTE.toString()));

		List<FatPacienteTransplantes> result = executeCriteria(criteria);
		
		if(result != null && result.size() > 0) {
			return result.get(0);
		}

		return null;
	}

	/**
	 * Consulta pacientes transplantados sem AacAtendimentoApac
	 * 
	 * @param pacCodigo
	 * @param ttrCodigo
	 * @param dtInscrTranpl
	 * @return
	 */
	public List<FatPacienteTransplantes> consultaPacientesTransplantadosSemAacAtendimentoApac(Integer pacCodigo, String ttrCodigo,
			Date dtInscrTranpl) {

		DetachedCriteria criteria = obterDetachedCriteriaFatPacienteTransplantes();

		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.TTR_CODIGO.toString(), ttrCodigo));
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.DT_INSCRICAO_TRANSPLANTE.toString(), dtInscrTranpl));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacAtendimentoApacs.class);
		subCriteria.setProjection(Projections.property(AacAtendimentoApacs.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_PAC_CODIGO.toString(), pacCodigo));
		subCriteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_TTR_CODIGO.toString(), ttrCodigo));
		subCriteria.add(Restrictions.eq(AacAtendimentoApacs.Fields.PTR_DT_INSCRICAO_TRANSPLANTE.toString(), dtInscrTranpl));

		criteria.add(Subqueries.notExists(subCriteria));

		return executeCriteria(criteria);
	}

	public List<Date> listarDatasTransplantes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPacienteTransplantes.class);

		criteria.setProjection(Projections.property(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));

		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<FatPacienteTransplantes> listarPacientesTransplantesPorPacCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPacienteTransplantes.class);
		
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public Date obterDataUltimoTransplante(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPacienteTransplantes.class);
		criteria.setProjection(Projections.max(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNotNull(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public List<String> pesquisarTiposUltimosTransplantes(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPacienteTransplantes.class);
		criteria.setProjection(Projections.property(FatPacienteTransplantes.Fields.TTR_CODIGO.toString()));
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNotNull(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
		criteria.addOrder(Order.desc(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * #42011
	 * Consulta pacientes com datas de transplante não nulas
	 * 
	 * @param pacCodigo
	 * @param consultaNumero 
	 * @return
	 */
	public List<FatPacienteTransplantes> consultaPacientes(Integer pacCodigo) {
		DetachedCriteria criteria = obterDetachedCriteriaFatPacienteTransplantes();
		
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNotNull(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
//		criteria.setProjection(Projections.distinct(Projections.property(FatPacienteTransplantes.Fields.PAC_CODIGO.toString())));
		
		criteria.addOrder(Order.desc(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
		
		return executeCriteria(criteria);
	}	
	
	/**
	 * 42801
	 * @param nroProntuario
	 * @return
	 */
	public String pesquisarCodigoFormulaPaciente(Integer nroProntuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPacienteTransplantes.class, "ptr");
		criteria.createAlias("ptr." + FatPacienteTransplantes.Fields.AIP_PACIENTE.toString(),"pac");
		
		criteria.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), nroProntuario));
		criteria.add(Restrictions.isNotNull(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
		criteria.setProjection((Projections.property(FatPacienteTransplantes.Fields.TTR_CODIGO.toString())));
		
		criteria.addOrder(Order.desc("ptr." + FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));

		List<String> codigos = executeCriteria(criteria);

		if(codigos!=null && !codigos.isEmpty()){
			return codigos.get(0);
		}else{
			return null;
		}
	}
	
	public Date obterDataUltimoTransplantePorTratamento(Integer pacCodigo, String tipoTratamento){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatPacienteTransplantes.class, "ptr");
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(FatPacienteTransplantes.Fields.TTR_CODIGO.toString(), tipoTratamento));
		criteria.addOrder(Order.desc(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
		criteria.setProjection(Projections.property(FatPacienteTransplantes.Fields.DT_TRANSPLANTE.toString()));
		
		return (Date) executeCriteria(criteria).get(0);
	}
}