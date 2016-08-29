package br.gov.mec.aghu.internacao.dao;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioIndSitSolicLeito;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoTransferencia;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;

public class AinSolicTransfPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinSolicTransfPacientes> {

	private static final long serialVersionUID = 4724960527667534623L;

	/**
	 * Busca SolicTransfPacientes associadas a quartos e com IndSolicLeito igual
	 * a P
	 * 
	 * @param quartoList
	 * @return
	 */
	public List<AinSolicTransfPacientes> pesquisarTotSolTransPndQrt() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class);
		criteria.createAlias(AinSolicTransfPacientes.Fields.QUARTO.toString(), "q");
		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.IND_SITUACAO.toString(), DominioSituacaoSolicitacaoInternacao.P));
		return this.executeCriteria(criteria);
	}

	/**
	 * Retorna criteria
	 * 
	 * @dbtables AinSolicTransfPacientes select
	 * 
	 * @return
	 */
	private DetachedCriteria obterSolicitacoesTransferenciaPacientesCriteria() {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class);
		criteria.createAlias(AinSolicTransfPacientes.Fields.INTERNACAO.toString(),
				AinSolicTransfPacientes.Fields.INTERNACAO.toString());
		criteria.createAlias(AinSolicTransfPacientes.Fields.ACOMODACOES.toString(),
				AinSolicTransfPacientes.Fields.ACOMODACOES.toString());
		criteria.createAlias(AinSolicTransfPacientes.Fields.ESPECIALIDADE.toString(),
				AinSolicTransfPacientes.Fields.ESPECIALIDADE.toString());
		criteria.createAlias(AinSolicTransfPacientes.Fields.INTERNACAO.toString() + "." + AinInternacao.Fields.PACIENTE.toString(),
				AinInternacao.Fields.PACIENTE.toString());

		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString(), DominioIndSitSolicLeito.P));
		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.INTERNACAO.toString() + "."
				+ AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), false));

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.PRONTUARIO.toString()))
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString()))
				.add(Projections.property(AinSolicTransfPacientes.Fields.INTERNACAO.toString() + "."
						+ AinInternacao.Fields.LEITO.toString()))
				.add(Projections.property(AinSolicTransfPacientes.Fields.INTERNACAO.toString() + "."
						+ AinInternacao.Fields.QUARTO.toString()))
				.add(Projections.property(AinSolicTransfPacientes.Fields.IND_LEITO_ISOLAMENTO.toString()))
				.add(Projections.property(AinSolicTransfPacientes.Fields.CRIADO_EM.toString()))
				.add(Projections.property(AinSolicTransfPacientes.Fields.ACOMODACOES.toString() + "."
						+ AinAcomodacoes.Fields.DESCRICAO.toString()))
				.add(Projections.property(AinSolicTransfPacientes.Fields.ESPECIALIDADE.toString() + "."
						+ AghEspecialidades.Fields.NOME.toString()))
				.add(Projections.property(AinSolicTransfPacientes.Fields.OBSERVACAO.toString()))
				.add(Projections.property(AinSolicTransfPacientes.Fields.INTERNACAO.toString() + "."
						+ AinInternacao.Fields.UNIDADE_FUNCIONAL.toString())));

		return criteria;
	}

	public List<Object[]> pesquisarSolicitacoesTransferenciaPacientesOrderCriacao() {
		DetachedCriteria criteria = this.obterSolicitacoesTransferenciaPacientesCriteria();
		criteria.addOrder(Order.asc(AinSolicTransfPacientes.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarSolicitacoesTransferenciaPacientesCount() {
		DetachedCriteria criteria = this.obterSolicitacoesTransferenciaPacientesCriteria();
		return executeCriteriaCount(criteria);
	}
	
	public List<Object[]> mensagemSolicTransPaciente(Integer internacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class);
		criteria.createAlias(AinSolicTransfPacientes.Fields.INTERNACAO.toString(), "INT");
		criteria.add(Restrictions.eq("INT." + AinInternacao.Fields.SEQ.toString(), internacao));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString()))
				.add(Projections.count(AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString()))
				.add(Projections.groupProperty(AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString()))
				.add(Projections.groupProperty(AinSolicTransfPacientes.Fields.INTERNACAO.toString())));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria createPesquisaCriteriaSolicitacaoTransferenciaLeito(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class);

		criteria.createAlias(AinSolicTransfPacientes.Fields.INTERNACAO.toString(), "INT");
		criteria.createAlias("INT." + AinInternacao.Fields.PACIENTE, "PAC");

		criteria.add(Restrictions.eq("INT." + AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));

		if (prontuario != null) {
			criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}

		return criteria;
	}

	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaLeito(Integer prontuarioPesquisa, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {

		DetachedCriteria criteriaSolicitacao = this.createPesquisaCriteriaSolicitacaoTransferenciaLeito(prontuarioPesquisa);
		criteriaSolicitacao.addOrder(Order.desc(AinSolicTransfPacientes.Fields.SEQUENCE.toString()));
		return executeCriteria(criteriaSolicitacao, firstResult, maxResults, orderProperty, asc);
	}	
	
	public Long pesquisarSolicitacaoTransferenciaLeitoCount(Integer prontuarioPesquisa) {
		DetachedCriteria criteriaSolicitacao = this.createPesquisaCriteriaSolicitacaoTransferenciaLeito(prontuarioPesquisa);
		return executeCriteriaCount(criteriaSolicitacao);
	}
	
	private DetachedCriteria createPesquisaCriteria(DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao, Date criadoEm,
			Integer prontuario, AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm, AghEspecialidades especialidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class,"astp");
		criteria.createAlias("astp."+AinSolicTransfPacientes.Fields.INTERNACAO.toString(), "int", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("astp."+AinSolicTransfPacientes.Fields.DIGITADOR.toString(), "dig", JoinType.INNER_JOIN);
		criteria.createAlias("astp."+AinSolicTransfPacientes.Fields.SOLICITANTE.toString(), "sol", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("astp."+AinSolicTransfPacientes.Fields.CANCELADOR.toString(), "can", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("int."+AinInternacao.Fields.LEITO.toString(), "lei", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("int."+AinInternacao.Fields.QUARTO.toString(), "qua", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("int."+AinInternacao.Fields.CONVENIO.toString(), "cnv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("int."+AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "cnp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("int."+AinInternacao.Fields.PACIENTE.toString(), "pacInt", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("astp."+AinSolicTransfPacientes.Fields.ACOMODACOES.toString(), "aco", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("astp."+AinSolicTransfPacientes.Fields.ESPECIALIDADE.toString(), "esp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("astp."+AinSolicTransfPacientes.Fields.PROFESSOR.toString(), "prof", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("prof."+RapServidores.Fields.PESSOA_FISICA.toString(), "pf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("astp."+AinSolicTransfPacientes.Fields.UNF_SOLICITANTE.toString(), "unfSol", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unfSol."+AghUnidadesFuncionais.Fields.ALA.toString(), "ala", JoinType.LEFT_OUTER_JOIN);

		if (indSolicitacaoInternacao != null) {
			criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString(), indSolicitacaoInternacao));
		}

		if (criadoEm != null) {
			GregorianCalendar cDate = new GregorianCalendar();
			cDate.setTime(criadoEm);
			cDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
			cDate.set(GregorianCalendar.MINUTE, 59);
			cDate.set(GregorianCalendar.SECOND, 59);
			criteria.add(Restrictions.between(AinSolicTransfPacientes.Fields.CRIADO_EM.toString(), criadoEm, cDate.getTime()));
		}

		if (prontuario != null) {
			criteria.add(Restrictions.eq("pacInt.prontuario", prontuario));
		}

		if (crm != null && crm.getVinCodigo() != null) {
			criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.VIN_PROFESSOR.toString(), crm.getVinCodigo()));
		}

		if (crm != null && crm.getMatricula() != null) {
			criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.MATRICULA_PROFESSOR.toString(), crm.getMatricula()));
		}

		if (especialidade != null && especialidade.getSeq() != null) {
			criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.ESPECIALIDADE.toString(), especialidade));
		}

		if (unidadeFuncional != null && unidadeFuncional.getSeq() != null) {
			criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.UNF_SOLICITANTE.toString(), unidadeFuncional));
		}

		return criteria;
	}
	
	public Long pesquisarSolicitacaoTransferenciaPacienteCount(DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao,
			Date criadoEm, Integer prontuario, AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm,
			AghEspecialidades especialidade) {

		return executeCriteriaCount(createPesquisaCriteria(indSolicitacaoInternacao, criadoEm, prontuario, unidadeFuncional, crm,
				especialidade));
	}

	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaPaciente(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao, Date criadoEm,
			Integer prontuario, AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm, AghEspecialidades especialidade) {

		DetachedCriteria criteria = createPesquisaCriteria(indSolicitacaoInternacao, criadoEm, prontuario, unidadeFuncional, crm,
				especialidade);

		criteria.addOrder(Order.desc(AinSolicTransfPacientes.Fields.CRIADO_EM.toString()));
		criteria.addOrder(Order.asc(AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString()));

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);

	}
	
	@SuppressWarnings("unchecked")
	public List<AinSolicTransfPacientes> listarSolicTransPacientes(Integer seqInternacao, String leitoId, Short numeroQuarto,
			Short seqUnidadeFuncional, DominioSituacaoSolicitacaoInternacao indSolicitacaoAtendida) {
		StringBuffer hql = new StringBuffer(290);
		hql.append(" select asf ");
		hql.append(" from AinSolicTransfPacientes asf ");
		hql.append(" where asf.internacao.seq = :seqInternacao ");
		hql.append(" 	and asf.indSitSolicLeito = :indSolicitacaoAtendida");
		hql.append(" 	and ( ");
		hql.append(" 		asf.leito.leitoID = :leitoId ");
		hql.append(" 		or asf.ainQuartos.numero = :numeroQuarto ");
		hql.append(" 		or asf.unidadeFuncional.seq = :seqUnidadeFuncional ");
		hql.append(" 	) ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqInternacao", seqInternacao);
		query.setParameter("indSolicitacaoAtendida", indSolicitacaoAtendida);
		query.setParameter("leitoId", leitoId);
		query.setParameter("numeroQuarto", numeroQuarto);
		query.setParameter("seqUnidadeFuncional", seqUnidadeFuncional);

		return query.list();
	}
	
	public List<AinSolicTransfPacientes> carregaSolicitacoesTransfereciaPaciente(
			Integer seq, DominioSituacaoSolicitacaoInternacao indSituacaoSolicitacaoPendente,
			DominioSituacaoSolicitacaoInternacao indSituacaoSolicitacaoAtendida) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinSolicTransfPacientes.class);
		criteria.createAlias(AinSolicTransfPacientes.Fields.INTERNACAO
				.toString(), AinSolicTransfPacientes.Fields.INTERNACAO
				.toString());
		criteria.add(Restrictions.eq(
				AinSolicTransfPacientes.Fields.INTERNACAO_SEQ.toString(), seq));
		criteria.add(Restrictions.or(Restrictions.eq(
				AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString(),
				indSituacaoSolicitacaoPendente), Restrictions.eq(
				AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString(),
				indSituacaoSolicitacaoAtendida)));

		return executeCriteria(criteria);
	}

	public List<AinSolicTransfPacientes> listarSolicTransfPacientesComLeitoQuartoEUnidadeFuncionalNulos(Integer seqInternacao,
			Date dthrAltaMedica, DominioSituacaoSolicitacaoInternacao indSolicitacaoLeito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class);
		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.INTERNACAO_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString(), indSolicitacaoLeito));
		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.CRIADO_EM.toString(), dthrAltaMedica));
		criteria.add(Restrictions.isNull(AinSolicTransfPacientes.Fields.LEITO.toString()));
		criteria.add(Restrictions.isNull(AinSolicTransfPacientes.Fields.QUARTO.toString()));
		criteria.add(Restrictions.isNull(AinSolicTransfPacientes.Fields.UNIDADE_FUNCIONAL.toString()));

		return executeCriteria(criteria);
	}

	public List<AinSolicTransfPacientes> listarSolicTransfPacientesComLeitoQuartoOuUnidadeFuncionalNotNull(Integer seqInternacao,
			Date dthrAltaMedica, DominioSituacaoSolicitacaoInternacao indSolicitacaoLeito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class);
		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.INTERNACAO_SEQ.toString(), seqInternacao));
		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.SOLIC_LEITO.toString(), indSolicitacaoLeito));
		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.CRIADO_EM.toString(), dthrAltaMedica));

		criteria.add(Restrictions.or(
				Restrictions.isNotNull(AinSolicTransfPacientes.Fields.LEITO.toString()),
				Restrictions.or(Restrictions.isNotNull(AinSolicTransfPacientes.Fields.QUARTO.toString()),
						Restrictions.isNotNull(AinSolicTransfPacientes.Fields.UNIDADE_FUNCIONAL.toString()))));

		return executeCriteria(criteria);
	}

	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaPorProntuario(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicTransfPacientes.class);
		criteria.createAlias(AinSolicTransfPacientes.Fields.INTERNACAO.toString(), "INT");
		criteria.createAlias("INT." + AinInternacao.Fields.PACIENTE, "PAC");

		criteria.add(Restrictions.eq(AinSolicTransfPacientes.Fields.IND_SITUACAO.toString(), DominioSituacaoSolicitacaoInternacao.A));
		if (prontuario != null) {
			criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}

		return executeCriteria(criteria);
	}

}
