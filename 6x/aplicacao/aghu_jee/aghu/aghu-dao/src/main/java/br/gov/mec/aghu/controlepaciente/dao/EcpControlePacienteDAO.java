package br.gov.mec.aghu.controlepaciente.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.EcpItemControle;
/**
 * 
 * @modulo controlepaciente
 *
 */
public class EcpControlePacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EcpControlePaciente> {

	private static final long serialVersionUID = 1654657763685518236L;

	/**
	 * Retorna controles (medições) de horários
	 * 
	 * @param horario
	 * @return
	 */
	public List<EcpControlePaciente> listarControlePacientePorHorario(
			EcpHorarioControle horario) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpControlePaciente.class);
		criteria.createAlias(EcpControlePaciente.Fields.ITEM.toString(), "ITEM");

		criteria.add(Restrictions.eq(
				EcpControlePaciente.Fields.HORARIO.toString(), horario));

		criteria.addOrder(Order.asc("ITEM."
				+ EcpItemControle.Fields.ORDEM.toString()));

		return executeCriteria(criteria);

	}

	/**
	 * Retorna a lista de controles de determinado horario do paciente e grupo
	 * de itens
	 * 
	 * @param horario
	 * @param grupo
	 *            (opcional)
	 * @return
	 */
	public List<EcpControlePaciente> listaControlesHorario(
			EcpHorarioControle horario, EcpGrupoControle grupo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpControlePaciente.class);

		criteria.createAlias(EcpControlePaciente.Fields.ITEM.toString(), "ITEM");

		criteria.add(Restrictions.eq(
				EcpControlePaciente.Fields.HORARIO.toString(), horario));

		if (grupo != null) {
			criteria.add(Restrictions.eq("ITEM."
							+ EcpItemControle.Fields.GRUPO.toString(), grupo));
		}

		criteria.addOrder(Order.asc("ITEM."
				+ EcpItemControle.Fields.ORDEM.toString()));

		return executeCriteria(criteria);
	}

	public boolean existeControleItensAlteracaoIgual(EcpControlePaciente cont) {
		if (cont == null) {
			throw new IllegalArgumentException("Controle nulo");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpControlePaciente.class);
		criteria.add(Restrictions.eq(EcpControlePaciente.Fields.SEQ.toString(), cont.getSeq()));
		if (cont.getMedicao() != null) {
			criteria.add(Restrictions.eq(EcpControlePaciente.Fields.MEDICAO.toString(), cont.getMedicao()));
		}
		if (StringUtils.isNotBlank(cont.getTexto())) {
			criteria.add(Restrictions.eq(EcpControlePaciente.Fields.TEXTO.toString(), cont.getTexto()));
		}
		if (cont.getSimNao() != null) {
			criteria.add(Restrictions.eq(EcpControlePaciente.Fields.SIM_NAO.toString(), cont.getSimNao()));
		}
		if (cont.getInicioFim() != null) {
			criteria.add(Restrictions.eq(EcpControlePaciente.Fields.INICIO_FIM.toString(), cont.getInicioFim()));
		}
		long result = executeCriteriaCount(criteria);
		return result > 0;
	}
	
	/**
	 * 
	 * @param atdSeq
	 * @param pacCodigo
	 * @return
	 */
	public List<EcpControlePaciente> listarControlePacientePorAtendimentoEPaciente(
			Integer atdSeq, Integer pacCodigo) {
		return executeCriteria(this.montarCriteriaParaListarControlePacientePorAtendimentoEPaciente(atdSeq, pacCodigo));
	}
	
	public Boolean verificarExisteControlePacientePorAtendimentoEPaciente(
			Integer atdSeq, Integer pacCodigo) {
		return executeCriteriaCount(this.montarCriteriaParaListarControlePacientePorAtendimentoEPaciente(atdSeq, pacCodigo)) > 0;
	}	
	
	private DetachedCriteria montarCriteriaParaListarControlePacientePorAtendimentoEPaciente(
			Integer atdSeq, Integer pacCodigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpControlePaciente.class);
		criteria.createAlias(EcpControlePaciente.Fields.HORARIO.toString(), "horario");

		criteria.add(Restrictions.eq(
				"horario."+EcpHorarioControle.Fields.ATENDIMENTO.toString()+"."+AghAtendimentos.Fields.SEQ.toString(), atdSeq));

		criteria.add(Restrictions.eq(
				"horario."+EcpHorarioControle.Fields.PACIENTE.toString()+"."+AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		
		return criteria;

	}

	public Long obterNumeroRegistrosItemPorControle(EcpItemControle itemControle) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpControlePaciente.class);
		criteria.add(Restrictions.eq(
				EcpControlePaciente.Fields.ITEM.toString()+"."+EcpItemControle.Fields.SEQ.toString(),
				itemControle.getSeq()));
		return executeCriteriaCount(criteria);
	}
	
	public Boolean verificarExisteSinalVitalPorPaciente(Short iceSeq, Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpControlePaciente.class, "ECP");
		criteria.createAlias(EcpControlePaciente.Fields.ITEM.toString(), "ICE");
		criteria.createAlias(EcpControlePaciente.Fields.HORARIO.toString(), "HCT");
		
		criteria.add(Restrictions.eq("ICE." + EcpItemControle.Fields.SEQ.toString(), iceSeq));
		criteria.add(Restrictions.eq("ICE." + EcpItemControle.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("HCT." + EcpHorarioControle.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		
		Calendar dataMenosUmaHora = Calendar.getInstance();
		dataMenosUmaHora.add(Calendar.HOUR, -1);
		
		criteria.add(Restrictions.between("HCT." + EcpHorarioControle.Fields.DATA_HORA.toString(), dataMenosUmaHora.getTime(), new Date()));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}

	public List<EcpControlePaciente> listarUltimosDadosControlePacientePelaConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpControlePaciente.class, "ECP");
		criteria.createAlias("ECP." + EcpControlePaciente.Fields.ITEM.toString(), "EIC", JoinType.INNER_JOIN);
		criteria.createAlias("ECP." + EcpControlePaciente.Fields.HORARIO.toString(), "HCT", JoinType.INNER_JOIN);
		criteria.createAlias("EIC." + EcpItemControle.Fields.UNIDADE_MEDIDA_MEDICA.toString(), "UMC", JoinType.LEFT_OUTER_JOIN);
		
		DetachedCriteria subQueryPacCon = DetachedCriteria.forClass(AacConsultas.class, "CON");
		subQueryPacCon.setProjection(Projections.property("CON." + AacConsultas.Fields.PAC_CODIGO.toString()));
		subQueryPacCon.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		DetachedCriteria subQueryMaxHorario = DetachedCriteria.forClass(EcpHorarioControle.class, "HRC");
		subQueryMaxHorario.setProjection(Projections.max("HRC." + EcpHorarioControle.Fields.SEQ.toString()));
		subQueryMaxHorario.add(Subqueries.propertyEq("HRC." + EcpHorarioControle.Fields.PACIENTE_CODIGO.toString(), subQueryPacCon));
		
		criteria.add(Subqueries.propertyIn("HCT." + EcpHorarioControle.Fields.SEQ.toString(), subQueryMaxHorario));
		
		return executeCriteria(criteria);
	}

	public List<EcpControlePaciente> listarUltimosDadosControlePacientePeloCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpControlePaciente.class, "ECP");
		criteria.createAlias("ECP." + EcpControlePaciente.Fields.ITEM.toString(), "EIC", JoinType.INNER_JOIN);
		criteria.createAlias("ECP." + EcpControlePaciente.Fields.HORARIO.toString(), "HCT", JoinType.INNER_JOIN);
		criteria.createAlias("EIC." + EcpItemControle.Fields.UNIDADE_MEDIDA_MEDICA.toString(), "UMC", JoinType.LEFT_OUTER_JOIN);
		
		DetachedCriteria subQueryMaxHorario = DetachedCriteria.forClass(EcpHorarioControle.class, "HRC");
		subQueryMaxHorario.setProjection(Projections.max("HRC." + EcpHorarioControle.Fields.SEQ.toString()));
		subQueryMaxHorario.add(Restrictions.eq("HRC." + EcpHorarioControle.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		
		criteria.add(Subqueries.propertyIn("HCT." + EcpHorarioControle.Fields.SEQ.toString(), subQueryMaxHorario));
		
		return executeCriteria(criteria);
	}
	
	public Boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpControlePaciente.class, "ECP");	
		criteria.createAlias(EcpControlePaciente.Fields.HORARIO.toString(), "HCT");
	
	
		criteria.add(Restrictions.eq("HCT." + EcpHorarioControle.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		
		Calendar dataMenosUmaHora = Calendar.getInstance();
		dataMenosUmaHora.add(Calendar.HOUR, -1);
		
		criteria.add(Restrictions.between("HCT." + EcpHorarioControle.Fields.DATA_HORA.toString(), dataMenosUmaHora.getTime(), new Date()));
		
		return (this.executeCriteriaCount(criteria) > 0);
	}
}
