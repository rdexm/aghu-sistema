package br.gov.mec.aghu.internacao.dao;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AipPacientes;

/**
 * 
 * @author lalegre
 * 
 */
@ApplicationScoped
public class AhdHospitaisDiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AhdHospitaisDia> {

	private static final long serialVersionUID = 4671236016544070659L;

	/**
	 * ORADB CURSOR cur_hod
	 * 
	 * @param hodSeq
	 * @return
	 */
	public List executarCursorHod(Integer hodSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AhdHospitaisDia.class);
		criteria.add(Restrictions.eq(AhdHospitaisDia.Fields.SEQ.toString(), hodSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AhdHospitaisDia.Fields.CSP_SEQ.toString()));
		p.add(Projections.property(AhdHospitaisDia.Fields.CSP_CNV_CODIGO.toString()));
		p.add(Projections.property(AhdHospitaisDia.Fields.DTHR_INICIO.toString()));
		p.add(Projections.property(AhdHospitaisDia.Fields.TAM_CODIGO.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}

	/**
	 * ORADB CURSOR c_hod
	 * 
	 * @param atuSeq
	 * @return
	 */
	public List<Date> executarCursorHod2(Integer atuSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AhdHospitaisDia.class);
		criteria.add(Restrictions.eq(AhdHospitaisDia.Fields.SEQ.toString(), atuSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AhdHospitaisDia.Fields.DTHR_ULTIMO_EVENTO.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}

	/**
	 * Método usado para verificar se um paciente está internado.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Boolean verificarPacienteHospitalDia(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AhdHospitaisDia.class);
		criteria.createAlias(AhdHospitaisDia.Fields.PACIENTE.toString(), AhdHospitaisDia.Fields.PACIENTE.toString());
		criteria.add(Restrictions.eq(AhdHospitaisDia.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));

		criteria.add(Restrictions.eq(AhdHospitaisDia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString(), true));

		return this.executeCriteriaExists(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion14(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status) {

		StringBuffer hql = new StringBuffer(590);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	mv.dthrInicio,  esp.sigla, tam.codigo, mv.seq,");
		hql.append(" 	ser.id.matricula, ser.id.vinCodigo, cnv.codigo, pac.dtNascimento, cnv.descricao, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AhdHospitaisDia mv");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" join mv.especialidade as esp ");
		hql.append(" join mv.paciente as pac ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.servidor as ser ");
		hql.append(" left join mv.tipoAltaMedica as tam ");
		hql.append(" left join mv.convenioSaude as cnv ");
		hql.append(" where 1=1 ");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Query query = createHibernateQuery(hql.toString());
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion15(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status) {

		StringBuffer hql = new StringBuffer(610);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	mv.dthrInicio,  esp.sigla, tam.codigo, mv.seq,");
		hql.append(" 	ser.id.matricula, ser.id.vinCodigo, mv.dthrTermino, cnv.codigo, pac.dtNascimento, cnv.descricao, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AhdHospitaisDia mv");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" join mv.especialidade as esp ");
		hql.append(" join mv.paciente as pac ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.servidor as ser ");
		hql.append(" left join mv.tipoAltaMedica as tam ");
		hql.append(" left join mv.convenioSaude as cnv ");
		hql.append(" where 1=1 ");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Query query = createHibernateQuery(hql.toString());
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion16(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status) {

		StringBuffer hql = new StringBuffer(610);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	mv.dthrInicio,  esp.sigla, tam.codigo, mv.seq,");
		hql.append(" 	ser.id.matricula, ser.id.vinCodigo, mv.dthrTermino, cnv.codigo, pac.dtNascimento, cnv.descricao, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AhdHospitaisDia mv");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" join mv.especialidade as esp ");
		hql.append(" join mv.paciente as pac ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.servidor as ser ");
		hql.append(" left join mv.tipoAltaMedica as tam ");
		hql.append(" left join mv.convenioSaude as cnv ");
		hql.append(" where 1=1 ");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Query query = createHibernateQuery(hql.toString());
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion20(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status) {

		StringBuffer hql = new StringBuffer(610);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	mv.dthrInicio,  esp.sigla, tam.codigo, mv.seq,");
		hql.append(" 	ser.id.matricula, ser.id.vinCodigo, cnv.codigo, pac.dtNascimento, mv.dthrTermino, cnv.descricao, tam.descricao, esp.nomeEspecialidade");
		hql.append(" from AhdHospitaisDia mv");
		hql.append(" join mv.unidadeFuncional as unf ");
		hql.append(" join mv.especialidade as esp ");
		hql.append(" join mv.paciente as pac ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join mv.servidor as ser ");
		hql.append(" left join mv.tipoAltaMedica as tam ");
		hql.append(" left join mv.convenioSaude as cnv ");
		hql.append(" where 1=1 ");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		Query query = createHibernateQuery(hql.toString());
		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}
	
	public List<AhdHospitaisDia> listarHospitaisDiaPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AhdHospitaisDia.class);
		criteria.add(Restrictions.eq(
				AhdHospitaisDia.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem os dados do AhdHospitaisDia do paciente através de seu código,
	 * ordenadado decrescentemente pela data de início do atendimento no
	 * hospital dia.
	 * 
	 * @param aipPacientesCodigo
	 * @return
	 */
	public AhdHospitaisDia obterUltimoAtendimentoHospitaisDia(Integer aipPacientesCodigo) {
		AhdHospitaisDia ahdHospitaisDia = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AhdHospitaisDia.class);
		criteria.add(Restrictions.eq(AhdHospitaisDia.Fields.COD_PACIENTE.toString(),
				aipPacientesCodigo));

		criteria.addOrder(Order.desc(AhdHospitaisDia.Fields.DTHR_INICIO.toString()));

		List<AhdHospitaisDia> listaAhdHospitaisDia = executeCriteria(criteria);

		if (listaAhdHospitaisDia.size() > 0) {
			ahdHospitaisDia = listaAhdHospitaisDia.get(0);
		}
		return ahdHospitaisDia;
	}
	
	public AhdHospitaisDia obterHospitalDiaConvenio(final Integer hodSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AhdHospitaisDia.class);
		criteria.createAlias(AhdHospitaisDia.Fields.CONVENIO_SAUDE.toString(), "conv");
		criteria.add(Restrictions.eq(AhdHospitaisDia.Fields.SEQ.toString(), hodSeq));
		return (AhdHospitaisDia) executeCriteriaUniqueResult(criteria);
	}
}
