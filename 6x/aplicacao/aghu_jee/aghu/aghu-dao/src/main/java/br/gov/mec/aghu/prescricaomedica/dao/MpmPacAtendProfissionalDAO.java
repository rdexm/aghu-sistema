package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 * @see MpmPacAtendProfissional
 * 
 * @author cvagheti
 * 
 */
public class MpmPacAtendProfissionalDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPacAtendProfissional> {

	private static final long serialVersionUID = 2931852317138157812L;

	/**
	 * Retorna criteria para pesquisa de atendimentos em acompanhamento
	 * configurados para o servidor fornecido.<br>
	 * Execução da criteria retorna lista de {@link MpmPacAtendProfissional}.
	 * 
	 * @param servidor
	 * @return
	 */
	protected DetachedCriteria criteriaConfiguradaPara(
			final RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPacAtendProfissional.class);

		criteria.add(Restrictions.eq(MpmPacAtendProfissional.Fields.SERVIDOR
				.toString(), servidor));

		return criteria;
	}

	public List<AghAtendimentos> pesquisarAtendimentosPorServidor(RapServidores servidor) {
		DetachedCriteria subCriteria = this.criteriaConfiguradaPara(servidor);
		subCriteria.setProjection(Projections.property(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString()));
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Subqueries.propertyIn(AghAtendimentos.Fields.SEQ.toString(), subCriteria));
		
		return executeCriteria(criteria);
	}
	
	public List<MpmPacAtendProfissional> pesquisarAssociacoesPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = criteriaConfiguradaPara(servidor);
		return executeCriteria(criteria);
	}
	
	/**
	 * Método para pesquisar lista de MpmPacAtendProfissional pelo atendimento do servidor com
	 * restrições nas datas de final do seu vinculo
	 * 
	 * @param seqAtendimento
	 * @param dataFim
	 * @return
	 */
	public List<MpmPacAtendProfissional> pesquisarPacienteAtendimentoProfissional(
			Integer seqAtendimento, Date dataFim) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPacAtendProfissional.class);

		criteria.createAlias(
				MpmPacAtendProfissional.Fields.SERVIDOR.toString(), "servidor");
		criteria.createAlias(
				MpmPacAtendProfissional.Fields.ATENDIMENTO.toString(),
				"atendimento");

		criteria.add(Restrictions.eq("atendimento."
				+ AghAtendimentos.Fields.SEQ.toString(), seqAtendimento));

		Date data = new Date();
		if (dataFim != null) {
			data = dataFim;
		}

		criteria.add(Restrictions.le(
				MpmPacAtendProfissional.Fields.CRIADO_EM.toString(), data));

		Criterion c1 = Restrictions.gt("servidor."
				+ RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());
		Criterion c2 = Restrictions.isNull("servidor."
				+ RapServidores.Fields.DATA_FIM_VINCULO.toString());

		criteria.add(Restrictions.or(c1, c2));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Método para pesquisar lista de MpmPacAtendProfissional pelo atendimento do servidor.
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmPacAtendProfissional> pesquisarPacienteAtendimentoProfissional(Integer atdSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPacAtendProfissional.class);

		criteria.add(Restrictions.eq(MpmPacAtendProfissional.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));

		return this.executeCriteria(criteria);
	}
}