package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigObjetoCustoAnalise;
import br.gov.mec.aghu.model.SigProcessamentoCusto;


public class SigObjetoCustoAnaliseDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoAnalise> {

	private static final long serialVersionUID = -7899323029489378118L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(158);
		sql.append(" DELETE ").append(SigObjetoCustoAnalise.class.getSimpleName().toString()).append(" caaa ");
		sql.append(" WHERE caaa.").append(SigObjetoCustoAnalise.Fields.CALCULO_ATIVIDADE_CONSUMO.toString()).append('.').append(SigCalculoAtdConsumo.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT caa.").append(SigCalculoAtdConsumo.Fields.SEQ.toString());
			sql.append(" FROM ").append(SigCalculoAtdConsumo.class.getSimpleName().toString()).append(" caa ");
			sql.append(" WHERE caa.").append(SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString()).append('.').append(SigCalculoAtdPermanencia.Fields.SEQ.toString());
			sql.append(" IN ( ");
				sql.append(" SELECT ca.").append(SigCalculoAtdPermanencia.Fields.SEQ.toString());
				sql.append(" FROM ").append(SigCalculoAtdPermanencia.class.getSimpleName().toString()).append(" ca ");
				sql.append(" WHERE ca.").append(SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString()).append('.').append(SigCalculoAtdPaciente.Fields.SEQ.toString());
				sql.append(" IN ( ");
					sql.append(" SELECT c.").append(SigCalculoAtdPaciente.Fields.SEQ.toString());
					sql.append(" FROM ").append( SigCalculoAtdPaciente.class.getSimpleName()).append(" c ");
					sql.append(" WHERE c.").append(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
				sql.append(" ) ");
			sql.append(" ) ");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
}
