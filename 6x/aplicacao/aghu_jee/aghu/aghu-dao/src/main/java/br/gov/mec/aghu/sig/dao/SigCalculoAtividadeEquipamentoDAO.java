package br.gov.mec.aghu.sig.dao;

import javax.persistence.Query;

import br.gov.mec.aghu.model.SigCalculoAtividadeEquipamento;
import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigCalculoAtividadeEquipamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtividadeEquipamento> {

	private static final long serialVersionUID = -927434469412768268L;

	public void removerPorProcessamento(Integer idProcessamento) {

		StringBuilder sql = new StringBuilder(100);

		sql.append(" DELETE " ).append( SigCalculoAtividadeEquipamento.class.getSimpleName().toString() ).append( " ca ");
		sql.append(" WHERE ca." ).append( SigCalculoAtividadeEquipamento.Fields.CALCULO_COMPONENTE.toString() ).append( '.' ).append( SigCalculoComponente.Fields.SEQ.toString());
		sql.append(" IN ( ");
		sql.append(" SELECT c." ).append( SigCalculoComponente.Fields.SEQ.toString());
		sql.append(" FROM " ).append( SigCalculoComponente.class.getSimpleName() ).append( " c ");
		sql.append(" WHERE c." ).append( SigCalculoComponente.Fields.PROCESSAMENTO_CUSTO.toString() ).append( '.' ).append( SigProcessamentoCusto.Fields.SEQ.toString() ).append( " = :pSeq");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}

}
