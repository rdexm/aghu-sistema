package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.EpeItemPrescricaoSumario;

public class EpeItemPrescricaoSumarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeItemPrescricaoSumario>{

	private static final long serialVersionUID = -6980593485023532211L;

	/**
	 * Metodo para listar itens de Prescricoes Sumario, filtrando pelo atendimento paciente.
	 * 
	 * @param atdSeq
	 * @param pteSeq
	 * @param pmoSeq
	 * @return
	 */
	public List<EpeItemPrescricaoSumario> listarItensPrescricaoSumario(Integer apaAtdSeq, Integer apaSeq, String descricao, DominioTipoItemPrescricaoSumario tipo){
		List<EpeItemPrescricaoSumario> lista = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeItemPrescricaoSumario.class, "IPS");
		
		criteria.createAlias("IPS."+ EpeItemPrescricaoSumario.Fields.ATENDIMENTO_PACIENTE.toString(), "APA", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("APA."+AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq("APA."+AghAtendimentoPacientes.Fields.SEQ.toString(), apaSeq));
		
		criteria.add(Restrictions.ilike(EpeItemPrescricaoSumario.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));

		criteria.add(Restrictions.eq(EpeItemPrescricaoSumario.Fields.TIPO.toString(), tipo));
		
		lista = this.executeCriteria(criteria);
		
		return lista;

	}
}