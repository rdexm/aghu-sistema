package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghAtendimentosPacExtern;

/**
 * 
 * @see AghAtendimentosPacExtern
 * 
 */
public class AghAtendimentosPacExternDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghAtendimentosPacExtern> {

	

	private static final long serialVersionUID = 5774660240922174354L;

	public Long countPacientesExternosPorMedicoExterno(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentosPacExtern.class);
		criteria.add(Restrictions.eq(AghAtendimentosPacExtern.Fields.MEX_SEQ.toString(), seq));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<AghAtendimentosPacExtern> listarPorPaciente(Integer codigoPaciente) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentosPacExtern.class, "ATP");
		
		criteria.createAlias("ATP." + AghAtendimentosPacExtern.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("ATP." + AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString(), "ALE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATP." + AghAtendimentosPacExtern.Fields.MEDICOEXTERNO.toString(), "MED", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATP." + AghAtendimentosPacExtern.Fields.CONVENIOSAUDEPLANO.toString(), "CON", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATP." + AghAtendimentosPacExtern.Fields.RESPONSAVEL_CONTA.toString(), "RESP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AghAtendimentosPacExtern.Fields.PAC_CODIGO.toString(), codigoPaciente));
		
		return executeCriteria(criteria);
	}

	/*public AghAtendimentosPacExtern obterOriginal(Integer seq) {
		if (seq == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		
		StringBuilder hql = new StringBuilder();
		hql.append("select o.").append(AghAtendimentosPacExtern.Fields.SEQ.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.MEDICOEXTERNO.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.PACIENTE.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.SERVIDORDIGITADO.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.UNIDADEFUNCIONAL.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.CONVENIOSAUDEPLANO.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.DTCOLETA.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.CODIGODOADOR.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.CONTATO1.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.CONTATO2.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.CRIADOEM.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.DDDFONECONTATO1.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.DDDFONECONTATO2.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.FONECONTATO1.toString());
		hql.append(", o.").append(AghAtendimentosPacExtern.Fields.FONECONTATO2.toString());
		hql.append(" from ").append(AghAtendimentosPacExtern.class.getSimpleName()).append(" o ");
		hql.append(" left outer join o.").append(AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString());
		hql.append(" left outer join o.").append(AghAtendimentosPacExtern.Fields.MEDICOEXTERNO.toString());
		hql.append(" left outer join o.").append(AghAtendimentosPacExtern.Fields.PACIENTE.toString());
		hql.append(" left outer join o.").append(AghAtendimentosPacExtern.Fields.SERVIDOR.toString());
		hql.append(" left outer join o.").append(AghAtendimentosPacExtern.Fields.SERVIDORDIGITADO.toString());
		hql.append(" left outer join o.").append(AghAtendimentosPacExtern.Fields.UNIDADEFUNCIONAL.toString());
		hql.append(" left outer join o.").append(AghAtendimentosPacExtern.Fields.CONVENIOSAUDEPLANO.toString());
		hql.append(" where o.").append(AghAtendimentosPacExtern.Fields.SEQ.toString()).append(" = :pSeq ");
		
		super.logInfo(hql.toString());
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("pSeq", seq);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.getResultList();
		
		AghAtendimentosPacExtern returnValue = null;
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new AghAtendimentosPacExtern();
			for (Object[] listFileds : lista) {
				returnValue.setSeq( (Integer) listFileds[0]);
				returnValue.setLaboratorioExterno( (AelLaboratorioExternos) listFileds[1]);
				returnValue.setMedicoExterno( (AghMedicoExterno) listFileds[2]);
				returnValue.setPaciente( (AipPacientes) listFileds[3]);
				returnValue.setServidor( (RapServidores) listFileds[4]);
				returnValue.setServidorDigitado( (RapServidores) listFileds[5]);
				returnValue.setUnidadeFuncional( (AghUnidadesFuncionais) listFileds[6]);
				returnValue.setConvenioSaudePlano( (FatConvenioSaudePlano) listFileds[7]);
				returnValue.setDtColeta( (Date) listFileds[8]);
				returnValue.setCodigoDoador( (String) listFileds[9]);
				returnValue.setContato1( (String) listFileds[10]);
				returnValue.setContato2( (String) listFileds[11]);
				returnValue.setCriadoEm( (Date) listFileds[12]);
				returnValue.setDddFoneContato1( (Short) listFileds[13]);
				returnValue.setDddFoneContato2( (Short) listFileds[14]);
				returnValue.setFoneContato1( (Long) listFileds[15]);
				returnValue.setFoneContato2( (Long) listFileds[16]);
			}
		}
		
		return returnValue;
	}*/

	public List<AghAtendimentosPacExtern> listarAtendimentosPacExternPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentosPacExtern.class);

		criteria.add(Restrictions.eq(AghAtendimentosPacExtern.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<AghAtendimentosPacExtern> obterPorLaboratorioExterno(Integer AghAtendimentosPacExternSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentosPacExtern.class, "ATP");
		
		criteria.createAlias("ATP." + AghAtendimentosPacExtern.Fields.LABORATORIOEXTERNO.toString(), "ALE");
		
		criteria.add(Restrictions.eq(AghAtendimentosPacExtern.Fields.LABORATORIO_EXTERNO_SEQ.toString(), AghAtendimentosPacExternSeq));
		
		return executeCriteria(criteria);
	}
		
}