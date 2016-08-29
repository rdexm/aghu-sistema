package br.gov.mec.aghu.internacao.dao;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.ServidorConselhoVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class AinSolicitacoesInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinSolicitacoesInternacao> {

	private static final long serialVersionUID = 7885125215941729820L;

	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria createPesquisaCriteria(DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao,
			AghClinicas clinica, Date criadoEm, Date dtPrevistaInternacao, AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicitacoesInternacao.class);
		
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.PACIENTE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.PROCEDIMENTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.ESPECIALIDADE.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.SERVIDOR.toString(), FetchMode.JOIN);
		criteria.createAlias(AinSolicitacoesInternacao.Fields.SERVIDOR.toString(), "servidor");
		criteria.setFetchMode("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		
		
		if (indSolicitacaoInternacao != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.SOLICITACAO_ITERNACAO.toString(), indSolicitacaoInternacao));
		}

		if (criadoEm != null) {
			GregorianCalendar cDate = new GregorianCalendar();
			cDate.setTime(criadoEm);
			cDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
			cDate.set(GregorianCalendar.MINUTE, 59);
			cDate.set(GregorianCalendar.SECOND, 59);
			criteria.add(Restrictions.between(AinSolicitacoesInternacao.Fields.CRIADO_EM.toString(), criadoEm, cDate.getTime()));
		}

		if (dtPrevistaInternacao != null) {
			GregorianCalendar cDate = new GregorianCalendar();
			cDate.setTime(dtPrevistaInternacao);
			cDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
			cDate.set(GregorianCalendar.MINUTE, 59);
			cDate.set(GregorianCalendar.SECOND, 59);
			criteria.add(Restrictions.between(AinSolicitacoesInternacao.Fields.DATA_PREVISTA_INTERNACAO.toString(),
					dtPrevistaInternacao, cDate.getTime()));

		}

		if (paciente != null && paciente.getCodigo() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.COD_PACIENTE.toString(), paciente.getCodigo()));
		}

		if (crm != null && crm.getVinCodigo() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.CODIGO_VINCULO.toString(), crm.getVinCodigo()));
		}

		if (crm != null && crm.getMatricula() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.MATRICULA.toString(), crm.getMatricula()));
		}

		if (especialidade != null && especialidade.getSeq() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.ESPECIALIDADE_SEQ.toString(), especialidade.getSeq()));
		}

		if (convenio != null && convenio.getCnvCodigo() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.CODIGO_CONVENIO.toString(), convenio.getCnvCodigo()));
		}

		if (convenio != null && convenio.getPlano() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.CONVENIO_SAUDE_PLANO_SEQ.toString(), convenio.getPlano()));
		}

		if (clinica != null && clinica.getCodigo() != null) {
			DetachedCriteria subquery = DetachedCriteria.forClass(AghEspecialidades.class);
			subquery.add(Restrictions.eq(AghEspecialidades.Fields.CLINICA_CODIGO.toString(), clinica.getCodigo()));
			subquery.setProjection(Projections.property(AghEspecialidades.Fields.SEQ.toString()));
			criteria.add(Property.forName(AinSolicitacoesInternacao.Fields.ESPECIALIDADE_SEQ.toString()).in(subquery));
		}
		return criteria;
	}

	public List<AinSolicitacoesInternacao> pesquisarSolicitacaoInternacao(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao, AghClinicas clinica,
			Date criadoEm, Date dtPrevistaInternacao, AipPacientes paciente, ServidorConselhoVO crm, AghEspecialidades especialidade,
			ConvenioPlanoVO convenio) throws ApplicationBusinessException {

		DetachedCriteria criteria = this.createPesquisaCriteria(indSolicitacaoInternacao, clinica, criadoEm, dtPrevistaInternacao,
				paciente, crm, especialidade, convenio);

		criteria.addOrder(Order.asc(AinSolicitacoesInternacao.Fields.CRIADO_EM.toString()));

		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long pesquisarSolicitacaoInternacaoCount(DominioSituacaoSolicitacaoInternacao indSolicitacaoInternacao,
			AghClinicas clinica, Date criadoEm, Date dtPrevistaInternacao, AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio) throws ApplicationBusinessException {

		DetachedCriteria criteria = createPesquisaCriteria(indSolicitacaoInternacao, clinica, criadoEm, dtPrevistaInternacao,
				paciente, crm, especialidade, convenio);

		return executeCriteriaCount(criteria);
	}
	
	public AinSolicitacoesInternacao obterSolicitacaoInternacaoDetached(AinSolicitacoesInternacao solicitacaoInternacao) {
		this.desatachar(solicitacaoInternacao);
		return this.obterPorChavePrimaria(solicitacaoInternacao.getSeq());
	}

	public List<AinSolicitacoesInternacao> listarSolicitacoesInternacaoPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicitacoesInternacao.class);

		criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public AinSolicitacoesInternacao obterPrimeiraSolicitacoesAtendidasPorPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicitacoesInternacao.class);
		criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.SOLICITACAO_ITERNACAO.toString(),
				DominioSituacaoSolicitacaoInternacao.A));
		List<AinSolicitacoesInternacao> lista = executeCriteria(criteria, 0, 1, null, true);
		if (lista != null & !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	public AinSolicitacoesInternacao obterPrimeiraSolicitacaoPendentePorPaciente(Integer codigoPaciente) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicitacoesInternacao.class);
		criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.SOLICITACAO_ITERNACAO.toString(),DominioSituacaoSolicitacaoInternacao.P));
		List<AinSolicitacoesInternacao> lista = executeCriteria(criteria, 0, 1, null, true);

		if (lista != null & !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	
	public List<AinSolicitacoesInternacao> obterSolicitacoesAtendidasPorPacienteSituacao(Integer codigoPaciente,
			DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoAtendida) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicitacoesInternacao.class);
		criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.SOLICITACAO_ITERNACAO.toString(), situacaoSolicitacaoAtendida));
		return executeCriteria(criteria);
	}

	/**
	 * Método que monta a criteria considerando todos os parâmetros da tela de
	 * pesquisa de solicitações de internações
	 * 
	 * @param criadoEm
	 * @param indSitSolicInternacao
	 * @param clinica
	 * @param dtPrevistaInternacao
	 * @param paciente
	 * @param crm
	 * @param especialidade
	 * @param convenio
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria createPesquisaCriteria(Date criadoEm, DominioSituacaoSolicitacaoInternacao indSitSolicInternacao,
			AghClinicas clinica, Date dtPrevistaInternacao, AipPacientes paciente, ServidorConselhoVO crm,
			AghEspecialidades especialidade, ConvenioPlanoVO convenio) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicitacoesInternacao.class);

		if (criadoEm != null) {
			GregorianCalendar cDate = new GregorianCalendar();
			cDate.setTime(criadoEm);
			cDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
			cDate.set(GregorianCalendar.MINUTE, 59);
			cDate.set(GregorianCalendar.SECOND, 59);
			criteria.add(Restrictions.between(AinSolicitacoesInternacao.Fields.CRIADO_EM.toString(), criadoEm, cDate.getTime()));
		}

		if (indSitSolicInternacao != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.SOLICITACAO_ITERNACAO.toString(), indSitSolicInternacao));
		}

		if (dtPrevistaInternacao != null) {
			GregorianCalendar cDate = new GregorianCalendar();
			cDate.setTime(dtPrevistaInternacao);
			cDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
			cDate.set(GregorianCalendar.MINUTE, 59);
			cDate.set(GregorianCalendar.SECOND, 59);
			criteria.add(Restrictions.between(AinSolicitacoesInternacao.Fields.DATA_PREVISTA_INTERNACAO.toString(),
					dtPrevistaInternacao, cDate.getTime()));

		}

		if (paciente != null && paciente.getCodigo() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.COD_PACIENTE.toString(), paciente.getCodigo()));
		}

		if (crm != null && crm.getVinCodigo() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.CODIGO_VINCULO.toString(), crm.getVinCodigo()));
		}

		if (crm != null && crm.getMatricula() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.MATRICULA.toString(), crm.getMatricula()));
		}

		if (especialidade != null && especialidade.getSeq() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.ESPECIALIDADE_SEQ.toString(), especialidade.getSeq()));
		}

		if (convenio != null && convenio.getCnvCodigo() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.CODIGO_CONVENIO.toString(), convenio.getCnvCodigo()));
		}

		if (convenio != null && convenio.getPlano() != null) {
			criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.CONVENIO_SAUDE_PLANO_SEQ.toString(), convenio.getPlano()));
		}

		if (clinica != null && clinica.getCodigo() != null) {

			DetachedCriteria subquery = DetachedCriteria.forClass(AghEspecialidades.class);

			subquery.add(Restrictions.eq(AghEspecialidades.Fields.CLINICA_CODIGO.toString(), clinica.getCodigo()));

			subquery.setProjection(Projections.property(AghEspecialidades.Fields.SEQ.toString()));

			criteria.add(Property.forName(AinSolicitacoesInternacao.Fields.ESPECIALIDADE_SEQ.toString()).in(subquery));
		}

		return criteria;
	}

	public List<Object[]> listarInformacoesSolicitacoesInternacao(Date criadoEm,
			DominioSituacaoSolicitacaoInternacao indSitSolicInternacao, AghClinicas clinica, Date dtPrevistaInternacao,
			AipPacientes paciente, ServidorConselhoVO crm, AghEspecialidades especialidade, ConvenioPlanoVO convenio) {
		DetachedCriteria criteria = createPesquisaCriteria(criadoEm, indSitSolicInternacao, clinica, dtPrevistaInternacao, paciente,
				crm, especialidade, convenio);
		criteria.createAlias("paciente", "paciente");
		criteria.createAlias("especialidade", "especialidade");
		criteria.createAlias(AinSolicitacoesInternacao.Fields.PROCEDIMENTO.toString(), "procedimento", Criteria.LEFT_JOIN);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AinSolicitacoesInternacao.Fields.COD_PACIENTE.toString()))
				.add(Projections.property(AinSolicitacoesInternacao.Fields.NOME_PACIENTE.toString()))
				.add(Projections.property(AinSolicitacoesInternacao.Fields.PRONTUARIO.toString()))
				.add(Projections.property(AinSolicitacoesInternacao.Fields.DATA_PREVISTA_INTERNACAO.toString()))
				.add(Projections.property(AinSolicitacoesInternacao.Fields.ESPECIALIDADE_SIGLA.toString()))
				.add(Projections.property("procedimento." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()))
				.add(Projections.property("procedimento." + FatItensProcedHospitalar.Fields.DESCRICAO.toString())));

		criteria.addOrder(Order.asc(AinSolicitacoesInternacao.Fields.CRIADO_EM.toString()));

		return executeCriteria(criteria);
	}
	
	public AinSolicitacoesInternacao obterSolicitacoesInternacaoDetalhada(Integer seqSolicitaco){
		DetachedCriteria criteria = DetachedCriteria.forClass(AinSolicitacoesInternacao.class);
		
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.PACIENTE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.ACOMODACAO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.ESPECIALIDADE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.CONVENIO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.LEITO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.QUARTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.UNIDADE_FUNCIONAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.PROCEDIMENTO.toString(), FetchMode.JOIN);
		
		//solicitacaoInternacao->servidorDigitador
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.SERVIDOR_DIGITADOR.toString(), FetchMode.JOIN);
		criteria.createAlias(AinSolicitacoesInternacao.Fields.SERVIDOR_DIGITADOR.toString(), "servidorDigitador", JoinType.INNER_JOIN );
		
		//servidorDigitador->pessoaFisica
		criteria.setFetchMode("servidorDigitador."+RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		
		//solicitacaoInternacao->servidor
		criteria.setFetchMode(AinSolicitacoesInternacao.Fields.SERVIDOR.toString(), FetchMode.JOIN);
		criteria.createAlias(AinSolicitacoesInternacao.Fields.SERVIDOR.toString(), "servidor", JoinType.INNER_JOIN );
		
		//servidor->pessoaFisica
		criteria.setFetchMode("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), FetchMode.JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica", JoinType.LEFT_OUTER_JOIN );
		
		//pessoaFisica->qualificações
		criteria.setFetchMode("pessoaFisica."+RapPessoasFisicas.Fields.QUALIFICACOES.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(AinSolicitacoesInternacao.Fields.SEQ.toString(), seqSolicitaco));
		return (AinSolicitacoesInternacao) this.executeCriteriaUniqueResult(criteria);
	}
	
}
