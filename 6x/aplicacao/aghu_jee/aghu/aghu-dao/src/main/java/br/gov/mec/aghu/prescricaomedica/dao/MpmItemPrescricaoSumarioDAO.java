package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.MpmItemPrescricaoSumario;

public class MpmItemPrescricaoSumarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmItemPrescricaoSumario> {

	private static final long serialVersionUID = -8920546405539273371L;

	/**
	 * Metodo para listar itens de Prescricoes Sumario, filtrando pelo atendimento paciente.
	 * 
	 * @param atdSeq
	 * @param pteSeq
	 * @param pmoSeq
	 * @return
	 */
	public List<MpmItemPrescricaoSumario> listarItensPrescricaoSumario(Integer apaAtdSeq, Integer apaSeq, String descricao, DominioTipoItemPrescricaoSumario tipo){
		List<MpmItemPrescricaoSumario> lista = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoSumario.class, "IPS");
		
		criteria.createAlias("IPS."+ MpmItemPrescricaoSumario.Fields.ATENDIMENTO_PACIENTE.toString(), "APA", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("APA."+AghAtendimentoPacientes.Fields.ATENDIMENTO_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq("APA."+AghAtendimentoPacientes.Fields.SEQ.toString(), apaSeq));
		
		criteria.add(Restrictions.ilike(MpmItemPrescricaoSumario.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));

		criteria.add(Restrictions.eq(MpmItemPrescricaoSumario.Fields.TIPO.toString(), tipo));
		
		lista = this.executeCriteria(criteria);
		
		return lista;
	}
	
}
