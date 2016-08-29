package br.gov.mec.aghu.internacao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosAtendUrgencia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmAltaSumario;

/**
 * 
 * @author lalegre
 * 
 */

public class AinAtendimentosUrgenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinAtendimentosUrgencia> {

	private static final long serialVersionUID = 2217121650284873696L;

	public Date obterDataAtendimento(Integer atu_seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.SEQ.toString(), atu_seq));
		criteria.setProjection(Projections.property(AinAtendimentosUrgencia.Fields.DT_ATENDIMENTO.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	
	public AinAtendimentosUrgencia obterDetalheAtendimentoUrgencia(Integer codigoAtendimentoUrgencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.PACIENTE.toString(), "pac",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.QUARTO.toString(), "qua",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.LEITO.toString(), "lei",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.UNIDADE_FUNCIONAL.toString(), "unf",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.CONVENIO_SAUDE.toString(), "con",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.ESPECIALIDADE.toString(), "esp",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.SERVIDOR.toString(), "servidor",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.PESSOA_FISICA.toString(), "pes",JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.SEQ.toString(), codigoAtendimentoUrgencia));
		return (AinAtendimentosUrgencia)executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * ORADB CURSOR cur_atu
	 * 
	 * @param atuSeq
	 * @return
	 */
	public List executarCursorAtu(Integer atuSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.SEQ.toString(), atuSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AinAtendimentosUrgencia.Fields.CSP_SEQ.toString()));
		p.add(Projections.property(AinAtendimentosUrgencia.Fields.CSP_CNV_CODIGO.toString()));
		p.add(Projections.property(AinAtendimentosUrgencia.Fields.DT_ATENDIMENTO.toString()));
		p.add(Projections.property(AinAtendimentosUrgencia.Fields.TIPO_ALTA_MEDICA_CODIGO.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}
	
	/**
	 * ORADB CURSOR c_atu
	 * 
	 * @param atuSeq
	 * @return
	 */
	public List<Date> executarCursorAtu2(Integer atuSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosAtendUrgencia.class);
		criteria.add(Restrictions.eq(AinMovimentosAtendUrgencia.Fields.ATENDIMENTO_URGENCIA_SEQ.toString(), atuSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AinMovimentosAtendUrgencia.Fields.DATA_HORA_LANCAMENTO.toString()));
		criteria.setProjection(p);

		criteria.addOrder(Order.desc(AinMovimentosAtendUrgencia.Fields.CRIADO_EM.toString()));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * ORADB CURSOR c_urgencia
	 * 
	 * @param atuSeq
	 * @return
	 */
	public List executarCursorUrgencia(Integer atuSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.SEQ.toString(), atuSeq));

		ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AinAtendimentosUrgencia.Fields.TIPO_ALTA_MEDICA_CODIGO.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * Busca Atendimento de Urgencia Por Sequence
	 * 
	 * @return Atendimento de Urgencia
	 */
	private DetachedCriteria createPesquisaAtendimentoUrgenciaCriteria(
			Integer sequence, Integer prontuario) {
		DetachedCriteria cri = DetachedCriteria.forClass(
				AinAtendimentosUrgencia.class, "aau");

		DetachedCriteria subsubquery = DetachedCriteria.forClass(
				MpmAltaSumario.class, "mas");
		subsubquery.add(Restrictions.or(Restrictions.eq("mas."
				+ MpmAltaSumario.Fields.IND_TIPO.toString(),
				DominioIndTipoAltaSumarios.ALT), Restrictions.eq("mas."
				+ MpmAltaSumario.Fields.IND_TIPO.toString(),
				DominioIndTipoAltaSumarios.OBT)));
		subsubquery.add(Restrictions.eq("mas."
				+ MpmAltaSumario.Fields.IND_CONCLUIDO.toString(),
				DominioIndConcluido.S));
		subsubquery.setProjection(Projections.property("mas."
				+ MpmAltaSumario.Fields.APA_ATD_SEQ.toString()));

		DetachedCriteria subquery = DetachedCriteria.forClass(
				AghAtendimentos.class, "aat");
		subquery.add(Restrictions.isNull("aat."
				+ AghAtendimentos.Fields.INT_SEQ));
		subquery.add(Property.forName(
				"aat." + AghAtendimentos.Fields.SEQ.toString()).in(
				subsubquery));
		subquery.setProjection(Projections.property("aat."
				+ AghAtendimentos.Fields.ATU_SEQ.toString()));
		
		cri.add(Property.forName(
				"aau." + AinAtendimentosUrgencia.Fields.SEQ.toString()).in(
				subquery));

		if (sequence != null) {
			cri.add(Restrictions.eq("aau."
					+ AinAtendimentosUrgencia.Fields.SEQ.toString(), sequence));
		}
		
		if (prontuario != null) {
			subquery.add(Restrictions.eq("aat."
					+ AghAtendimentos.Fields.PRONTUARIO, prontuario));
		}

		return cri;
	}

	public AinAtendimentosUrgencia pesquisaAtendimentoUrgencia(Integer sequence, Integer prontuario) {
		DetachedCriteria criteria = createPesquisaAtendimentoUrgenciaCriteria(sequence, prontuario);
		return (AinAtendimentosUrgencia) executeCriteriaUniqueResult(criteria);
	}
	
	public Long pesquisaAtendimentoUrgenciaCount(Integer sequence, Integer prontuario) {
		DetachedCriteria criteria = createPesquisaAtendimentoUrgenciaCriteria(
				null, prontuario);
		return executeCriteriaCount(criteria);
	}
	
	public boolean verificarExisteAtendimentoUrgencia(Integer prontuario){
		DetachedCriteria criteria = createPesquisaAtendimentoUrgenciaCriteria(null, prontuario);
		
		return executeCriteriaExists(criteria);
	}

	public List<AinAtendimentosUrgencia> listarAtendimentosUrgencia(Integer sequence, Integer prontuario) {
		DetachedCriteria criteria = createPesquisaAtendimentoUrgenciaCriteria(sequence, prontuario);
		return executeCriteria(criteria);
	}
	
	/**
	 * Método usado para verificar se um paciente está internado.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public AinAtendimentosUrgencia obterPacienteAtendimentoUrgencia(Integer codigoPaciente) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.PACIENTE.toString(), AinAtendimentosUrgencia.Fields.PACIENTE.toString());
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));

		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString(), true));

		AinAtendimentosUrgencia atendimento = (AinAtendimentosUrgencia) executeCriteriaUniqueResult(criteria);
		return atendimento;
	}
	
	public List<Object[]> pesquisarAtendimentoUrgenciaPorPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AinAtendimentosUrgencia.Fields.DT_ATENDIMENTO.toString()))
				.add(Projections.property(AinAtendimentosUrgencia.Fields.IND_LOCAL_PACIENTE.toString()))
				.add(Projections.property(AinAtendimentosUrgencia.Fields.LEITO_ID.toString()))
				.add(Projections.property(AinAtendimentosUrgencia.Fields.QUARTO_NUMERO.toString()))
				.add(Projections.property(AinAtendimentosUrgencia.Fields.UNIDADE_FUNCIONAL_SEQ.toString()))
				.add(Projections.property(AinAtendimentosUrgencia.Fields.DATA_ALTA_ATENDIMENTO.toString()))
				.add(Projections.property(AinAtendimentosUrgencia.Fields.SEQ.toString()))
				.add(Projections.property(AinAtendimentosUrgencia.Fields.TIPO_ALTA_MEDICA_CODIGO.toString())));
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.PACIENTE_CODIGO.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}

	/**
	 * Busca At. Urgencia associado ao leito
	 * 
	 * @param leitoId
	 * @return
	 */
	public Integer obterSeqAtUrgencia(String leitoId) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.LEITO.toString(), AinAtendimentosUrgencia.Fields.LEITO.toString());

		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.LEITO.toString() + "." + AinLeitos.Fields.LTO_ID.toString(), leitoId));
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString(), true));

		criteria.setProjection(Projections.projectionList().add(Projections.property(AinAtendimentosUrgencia.Fields.SEQ.toString())));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	
	public AinAtendimentosUrgencia obterDetalheAtendimentoUrgencia2(String leito) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.LEITO.toString(), AinAtendimentosUrgencia.Fields.LEITO.toString());

		if (leito != null) {
			criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.LEITO.toString() + "." + AinLeitos.Fields.LTO_ID.toString(), leito));
			criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString(), true));
		}

		AinAtendimentosUrgencia retorno = null;
		List<AinAtendimentosUrgencia> res = executeCriteria(criteria, 1, 0, null, true);
		if (res != null && !res.isEmpty()) {
			retorno = res.get(0);
		}
		return retorno;
	}
	
	/*
	public List<Object> pesquisarAtendimentoUrgenciaAvisoSamisUnidadeAtendimentoUrgencia() {
		String separador = ".";

		DetachedCriteria criteriaAvisoInternacaoSAMES = DetachedCriteria
				.forClass(AinAtendimentosUrgencia.class);

		// join com paciente
		criteriaAvisoInternacaoSAMES.createAlias(
				AinAtendimentosUrgencia.Fields.PACIENTE.toString(),
				AinAtendimentosUrgencia.Fields.PACIENTE.toString());

		// join com unidade Funcional
		criteriaAvisoInternacaoSAMES.createAlias(
				AinAtendimentosUrgencia.Fields.UNIDADE_FUNCIONAL.toString(),
				AinAtendimentosUrgencia.Fields.UNIDADE_FUNCIONAL.toString());

		// join com especialidade
		criteriaAvisoInternacaoSAMES.createAlias(
				AinAtendimentosUrgencia.Fields.ESPECIALIDADE.toString(),
				AinAtendimentosUrgencia.Fields.ESPECIALIDADE.toString(),
				Criteria.LEFT_JOIN);

		// join com clinicas
		criteriaAvisoInternacaoSAMES.createAlias(
				AinAtendimentosUrgencia.Fields.CLINICA.toString(),
				AinAtendimentosUrgencia.Fields.CLINICA.toString());

		criteriaAvisoInternacaoSAMES.add(Restrictions.eq(
				AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO
						.toString(), DominioSimNao.S));

		// null

		PropertyProjection projectionSeq = Projections
				.property(AinAtendimentosUrgencia.Fields.SEQ.toString());

		PropertyProjection projectionPaciente = Projections
				.property(AinAtendimentosUrgencia.Fields.PACIENTE.toString()
						+ separador + AipPacientes.Fields.CODIGO.toString());

		PropertyProjection projectionProntuarioPaciente = Projections
				.property(AinAtendimentosUrgencia.Fields.PACIENTE.toString()
						+ separador + AipPacientes.Fields.PRONTUARIO);

		PropertyProjection projectionDtAtendimento = Projections
				.property(AinAtendimentosUrgencia.Fields.DT_ATENDIMENTO
						.toString());

		PropertyProjection projectionLeitoID = Projections
				.property(AinAtendimentosUrgencia.Fields.LEITO.toString()
						+ separador + AinLeitos.Fields.ID.toString());

		PropertyProjection projectionNomePaciente = Projections
				.property(AinAtendimentosUrgencia.Fields.PACIENTE.toString()
						+ separador + AipPacientes.Fields.NOME);

		PropertyProjection projectionEspecialidadeSigla = Projections
				.property(AinAtendimentosUrgencia.Fields.ESPECIALIDADE
						.toString()
						+ separador + AghEspecialidades.Fields.SIGLA.toString());

		PropertyProjection projectionCodigoClinica = Projections
				.property(AinAtendimentosUrgencia.Fields.CLINICA.toString()
						+ separador + AghClinicas.Fields.CODIGO.toString());

		PropertyProjection projectionDescricaoClinica = Projections
				.property(AinAtendimentosUrgencia.Fields.CLINICA.toString()
						+ separador + AghClinicas.Fields.DESCRICAO.toString());

		// null

		PropertyProjection projectionAndar = Projections
				.property(AinAtendimentosUrgencia.Fields.UNIDADE_FUNCIONAL
						.toString()
						+ separador + AghUnidadesFuncionais.Fields.ANDAR);

		PropertyProjection projectionAla = Projections
				.property(AinAtendimentosUrgencia.Fields.UNIDADE_FUNCIONAL
						.toString()
						+ separador + AghUnidadesFuncionais.Fields.ALA);

		PropertyProjection projectionDescricao = Projections
				.property(AinAtendimentosUrgencia.Fields.UNIDADE_FUNCIONAL
						.toString()
						+ separador + AghUnidadesFuncionais.Fields.DESCRICAO);

		// null

		criteriaAvisoInternacaoSAMES.setProjection(Projections.projectionList()
				.add(projectionSeq).add(projectionPaciente).add(
						projectionProntuarioPaciente).add(
						projectionDtAtendimento).add(projectionLeitoID).add(
						projectionNomePaciente).add(
						projectionEspecialidadeSigla).add(
						projectionCodigoClinica)
				.add(projectionDescricaoClinica).add(projectionAndar).add(
						projectionAla).add(projectionDescricao));

		return this
				.executeCriteria(criteriaAvisoInternacaoSAMES);
	}
	*/

	public List<AinAtendimentosUrgencia> pesquisarAinAtendimentosUrgencia(
			Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AinAtendimentosUrgencia.class);
		criteria.add(Restrictions.eq(
				AinAtendimentosUrgencia.Fields.COD_PACIENTE.toString(),
				pacCodigo));

		return executeCriteria(criteria);
	}
	
	public AinAtendimentosUrgencia obterUltimoAtendimentoUrgenciaDoPaciente(AipPacientes paciente) {
		if (paciente != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
			criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.PACIENTE.toString(), paciente));
			criteria.addOrder(Order.desc(AinAtendimentosUrgencia.Fields.DT_ATENDIMENTO.toString()));
			List<AinAtendimentosUrgencia> list = executeCriteria(criteria, 0, 1, null, true);
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

	/**
	 * Método para buscar todos atendimentos de urgencia de um determinado
	 * paciente.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public List<AinAtendimentosUrgencia> pesquisarAtendimentoUrgencia(Integer codigoPaciente) {
		List<AinAtendimentosUrgencia> retorno = new ArrayList<AinAtendimentosUrgencia>();
		if (codigoPaciente != null) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
			criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.COD_PACIENTE.toString(), codigoPaciente));
			criteria.addOrder(Order.desc(AinAtendimentosUrgencia.Fields.DT_ATENDIMENTO.toString()));
			retorno = executeCriteria(criteria);
		}
		return retorno;
	}
	
	public List<AinAtendimentosUrgencia> pesquisarAtendimentoUrgencia(Integer seqAtendimentoUrgencia, Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.SEQ.toString(), seqAtendimentoUrgencia));
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.COD_PACIENTE.toString(), codigoPaciente));
		return executeCriteria(criteria);
	}

	/**
	 * Obtem os dados do atendimento de urgência do paciente através de seu
	 * código, ordenadado decrescentemente pela data de atendimento do paciente.
	 * 
	 * @param aipPacientesCodigo
	 * @return
	 */
	public AinAtendimentosUrgencia obterUltimoAtendimentosUrgencia(Integer aipPacientesCodigo) {

		AinAtendimentosUrgencia ainAtendimentosUrgencia = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.COD_PACIENTE.toString(),
				aipPacientesCodigo));

		criteria.addOrder(Order.desc(AinAtendimentosUrgencia.Fields.DT_ATENDIMENTO.toString()));

		List<AinAtendimentosUrgencia> listaAinAtendimentosUrgencia = executeCriteria(criteria);

		if (listaAinAtendimentosUrgencia.size() > 0) {
			ainAtendimentosUrgencia = listaAinAtendimentosUrgencia.get(0);
		}

		return ainAtendimentosUrgencia;
	}

	public AinAtendimentosUrgencia obterAtendimentoUrgencia(Integer seq, LockOptions lockMode) {
		return (AinAtendimentosUrgencia) this.getAndLock( seq, lockMode);
	}
	
	@SuppressWarnings("unchecked")
	public List<AinAtendimentosUrgencia> listarAtendimentosUrgenciaParaTestarSobreposicaoDatas(Integer seqAtendimentoUrgencia,
			AipPacientes paciente, Date dataHoraInternacao,
			Date dataHoraAltaMedica) {
		StringBuffer hql = new StringBuffer(480);
		
		hql.append(" select atu ");
		hql.append(" from AinAtendimentosUrgencia atu ");
		hql.append(" where ");
		hql.append(" 	atu.paciente = :paciente ");
		hql.append(" 	and atu.seq != :seqAtendimentoUrgencia ");
		hql.append(" 	and ( ");
		hql.append(" 		atu.dtAtendimento between :dataHoraInternacao1 and :dataHoraAltaMedica1 ");
		hql.append(" 		or atu.dtAltaAtendimento between :dataHoraInternacao2 and :dataHoraAltaMedica2 ");
		hql.append(" 		or ( ");
		hql.append(" 			atu.dtAtendimento < :dataHoraInternacao3 ");
		hql.append(" 			and case when atu.dtAltaAtendimento is null  then current_timestamp() else atu.dtAltaAtendimento end  > :dataHoraAltaMedica3 ");
		hql.append(" 		) ");
		hql.append(" 	) ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("paciente", paciente);
		query
				.setParameter("seqAtendimentoUrgencia",
						seqAtendimentoUrgencia);
		query.setParameter("dataHoraInternacao1", dataHoraInternacao);
		query.setParameter("dataHoraInternacao2", dataHoraInternacao);
		query.setParameter("dataHoraInternacao3", dataHoraInternacao);
		query.setParameter("dataHoraAltaMedica1", dataHoraAltaMedica);
		query.setParameter("dataHoraAltaMedica2", dataHoraAltaMedica);
		query.setParameter("dataHoraAltaMedica3", dataHoraAltaMedica);

		return query.list();
	}
	
	public AinAtendimentosUrgencia obterAtendimentoUrgenciaConvenio(final Integer atuSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.CONVENIO_SAUDE.toString(), "conv");
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.SEQ.toString(), atuSeq));
		return (AinAtendimentosUrgencia) executeCriteriaUniqueResult(criteria);
	}
	
	public AinAtendimentosUrgencia obterAtendimentoUrgenciaPorSequencialPaciente(final Integer seq, final Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		criteria.createAlias(AinAtendimentosUrgencia.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("PAC.".concat(AipPacientes.Fields.CODIGO.toString()), pacCodigo));
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString(), Boolean.TRUE));
		return (AinAtendimentosUrgencia) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obter código do atendimento de urgência pelo número da consulta
	 * 
	 * Web Service #38477
	 * 
	 * @param conNumero
	 * @return
	 */
	public Integer obterSeqAtendimentoUrgenciaPorConsulta(final Integer conNumero) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class, "AAU");
		criteria.createAlias("AAU." + AinAtendimentosUrgencia.Fields.CONSULTA.toString(), "CON");
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		criteria.setProjection(Projections.property("AAU." + AinAtendimentosUrgencia.Fields.SEQ.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean verificaPacienteIngressoSOPorConsulta(Integer numeroConsulta){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.CONSULTA_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.isNull(AinAtendimentosUrgencia.Fields.DATA_ALTA_ATENDIMENTO.toString()));
		
		return this.executeCriteriaExists(criteria);
	}


	public AinAtendimentosUrgencia obterPacienteEmAtendimentoUrgencia(Integer numeroConsulta, Integer codigoDoPaciente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinAtendimentosUrgencia.class);
		
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.CONSULTA_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq(AinAtendimentosUrgencia.Fields.COD_PACIENTE.toString(), codigoDoPaciente));
		
		return (AinAtendimentosUrgencia) executeCriteriaUniqueResult(criteria);
	}
}

