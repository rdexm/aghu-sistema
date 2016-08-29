package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOcorrenciaIntercorrenciaGestacao;
import br.gov.mec.aghu.model.McoIntercorrenciaGestacoes;

public class McoIntercorrenciaGestacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoIntercorrenciaGestacoes> {

	private static final long serialVersionUID = 5103021566432197937L;

	public List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoes(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrenciaGestacoes.class);

		criteria.add(Restrictions.eq(McoIntercorrenciaGestacoes.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoIntercorrenciaGestacoes.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoesPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrenciaGestacoes.class);

		criteria.add(Restrictions.eq(McoIntercorrenciaGestacoes.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}

	/***
	 * @param short gsoSeq
	 * @param integer gsoPacCodigo
	 * 
	 * @return list<McoIntercorrenciaGestacoes>
	 * 
	 * @see Q_ING, Q_PASSADAS
	 */

	public List<McoIntercorrenciaGestacoes> listarIntercorrenciasGestacoesPorCodGestCodPaciente(Short gsoSeq, Integer gsoPacCodigo, 
														DominioOcorrenciaIntercorrenciaGestacao dominioOcorrenciaIntercorrenciaGestacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorrenciaGestacoes.class);
		
		criteria.add(Restrictions.eq(McoIntercorrenciaGestacoes.Fields.OCORRENCIA.toString(), dominioOcorrenciaIntercorrenciaGestacao.toString()));
		criteria.add(Restrictions.eq(McoIntercorrenciaGestacoes.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoIntercorrenciaGestacoes.Fields.SEQUENCE.toString(), gsoSeq));

		return executeCriteria(criteria);

	}
}
