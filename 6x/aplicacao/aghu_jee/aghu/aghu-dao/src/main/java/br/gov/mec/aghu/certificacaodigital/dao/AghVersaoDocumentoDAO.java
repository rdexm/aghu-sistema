package br.gov.mec.aghu.certificacaodigital.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioControlePendencias;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapVinculos;


public class AghVersaoDocumentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghVersaoDocumento> {


	private static final long serialVersionUID = -7667123311260724458L;

	public Long count(RapServidores responsavel) {
		DetachedCriteria criteria = montaCriteria(responsavel);

		return this.executeCriteriaCount(criteria);
	}

	public List<Integer> pesquisarAghVersaoDocumentoPorCirurgia(final Integer crgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class,"DOV");

		criteria.createAlias(AghVersaoDocumento.Fields.DOCUMENTO.toString(), "DOC");
		criteria.add(Restrictions.eq("DOC."+AghDocumento.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("DOV."+AghVersaoDocumento.Fields.SITUACAO.toString(), DominioSituacaoVersaoDocumento.P));

		criteria.setProjection(Projections.property("DOC."+AghDocumento.Fields.SEQ.toString()));
		
		return this.executeCriteria(criteria, 0, 1, new HashMap<String, Boolean>());
	}

	public List<AghVersaoDocumento> obterAghVersaoDocumentoPorCirurgia(final Integer crgSeq, final DominioTipoDocumento tipo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class,"DOV");

		criteria.createAlias(AghVersaoDocumento.Fields.DOCUMENTO.toString(), "DOC");
		criteria.add(Restrictions.eq("DOC."+AghDocumento.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("DOC."+AghDocumento.Fields.TIPO.toString(), tipo));


		/*
		 SELECT d.seq doc_seq, v.seq vdoc_seq
  FROM AGH_DOCUMENTOS d,
  		 AGH_VERSOES_DOCUMENTOS v
 WHERE d.seq = v.dok_seq
   AND d.crg_seq = c_crg_seq
   AND d.TIPO = 'DC';
		 */
		return this.executeCriteria(criteria);
	}
	
	public List<AghVersaoDocumento> obterAghVersaoDocumentoPorAgenda(final Integer agdSeq, final DominioTipoDocumento tipo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class,"DOV");

		criteria.createAlias(AghVersaoDocumento.Fields.DOCUMENTO.toString(), "DOC");
		criteria.add(Restrictions.eq("DOC." + AghDocumento.Fields.AGD_SEQ.toString(), agdSeq));
		criteria.add(Restrictions.eq("DOC." + AghDocumento.Fields.TIPO.toString(), tipo));

		/*
		 SELECT d.seq doc_seq, v.seq vdoc_seq
  		 FROM AGH_DOCUMENTOS d,
  		 	AGH_VERSOES_DOCUMENTOS v
 		WHERE d.seq = v.dok_seq
   		AND d.agd_seq = c_agd_seq
   		AND d.TIPO = 'DC';
		 */
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retorna os documentos pendentes do responsável com o paciente fornecido.<br />
	 * Intersecção dos conjuntos: documentos pendentes do responsável e
	 * documentos do paciente.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param responsavel
	 * @param paciente
	 * @return
	 */
	public List<AghVersaoDocumento> pesquisarPendentesResponsavelPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final RapServidores responsavel,
			final AipPacientes paciente) {

		DetachedCriteria criteria = getCriteriaPendentes(responsavel);

		adicionarCriteriaPaciente(paciente, criteria);

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	/**
	 * Retorna os documentos pendentes do paciente fornecido e de responsável
	 * diferente do fornecido.<br />
	 * Conjunto documentos do paciente sem os do responsável.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param paciente
	 * @return
	 */
	public List<AghVersaoDocumento> pesquisarPendentesPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores responsavel, final AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);

		adicionarCriteriaPaciente(paciente, criteria);

		//criteria.add(Restrictions.ne(
		//		AghVersaoDocumento.Fields.RESPONSAVEL.toString(), responsavel));
		
		adicionarCriteriaResponsavel(responsavel, criteria);

		criteria.add(Restrictions.eq(
				AghVersaoDocumento.Fields.SITUACAO.toString(),
				DominioSituacaoVersaoDocumento.P));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	public AghVersaoDocumento obterDocumentoOriginal(
			Integer seqAghVersaoDocumentos) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);

		criteria.add(Restrictions.eq(AghVersaoDocumento.Fields.SEQ.toString(),
				seqAghVersaoDocumentos));

		// criteria.add(Restrictions.eq(
		// AghVersaoDocumento.Fields.SITUACAO.toString(),
		// DominioSituacaoVersaoDocumento.P));

		return (AghVersaoDocumento) this.executeCriteriaUniqueResult(criteria);
	}

	public AghVersaoDocumento obterDocumentoAssinado(
			Integer seqAghVersaoDocumentos) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);

		criteria.add(Restrictions.eq(AghVersaoDocumento.Fields.SEQ.toString(),
				seqAghVersaoDocumentos));

		criteria.add(Restrictions.eq(
				AghVersaoDocumento.Fields.SITUACAO.toString(),
				DominioSituacaoVersaoDocumento.A));

		return (AghVersaoDocumento) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Retorna os documentos pendentes do responsável e de paciente diferente do
	 * fornecido. Conjunto documentos do responsável sem os do paciente.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param responsavel
	 * @param paciente
	 *            não lista os documentos pendentes do paciente
	 * @return
	 */
	public List<AghVersaoDocumento> pesquisarPendentes(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			final RapServidores responsavel, final AipPacientes paciente) {
		DetachedCriteria criteria = getCriteriaPendentes(responsavel);

		DetachedCriteria criteriaDocumento = criteria
				.createCriteria(AghVersaoDocumento.Fields.DOCUMENTO.toString());

		removerCriteriaPaciente(paciente, criteriaDocumento);

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	/**
	 * Retorna todos os documentos do responsável fornecido.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param responsavel
	 * @return
	 */
	public List<AghVersaoDocumento> pesquisarPendentesResponsavel(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final RapServidores responsavel) {

		DetachedCriteria criteria = getCriteriaPendentes(responsavel);

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	public Long count(AipPacientes paciente) {

		DetachedCriteria criteria = montaCriteria(paciente);

		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Método que pesquisa e retorna uma estrutura contendo o código de cada paciente que possui AghVersaoDocumento e
	 * a quantidade existente destes registros
	 * @param sListaCodigosPacientes
	 * @return
	 */
	public List<Object[]> countVersaoPacientes(String sListaCodigosPacientes){
		
		StringBuilder hql = new StringBuilder(400);

		hql.append("select pac." + AipPacientes.Fields.CODIGO.toString()
				+ ", ")
		.append("count(*) ")

		.append("from " + AghVersaoDocumento.class.getSimpleName()+ " aghVers ")
		
		.append("left join aghVers." + AghVersaoDocumento.Fields.DOCUMENTO.toString() + " documento ")
		.append("left join documento." + AghDocumento.Fields.ATENDIMENTO.toString() + " atend ")
		.append("inner join atend." + AghAtendimentos.Fields.PACIENTE.toString() + " pac ")
		.append("left join documento." + AghDocumento.Fields.CIRURGIA.toString() + " cig ")
		.append("left join documento." + AghDocumento.Fields.FICHA_ANESTESICA.toString() + " fichaAnest ")
		.append("left join documento." + AghDocumento.Fields.NOTA_ADICIONAL_EVOLUCAO.toString() + " notaAdic ")
		
		.append("where aghVers." + AghVersaoDocumento.Fields.SITUACAO.toString()
				+ " = '" + DominioSituacaoVersaoDocumento.P.toString() + "' ")
		.append("and ( atend." + AghAtendimentos.Fields.PAC_CODIGO.toString()
				+ " IN (" + sListaCodigosPacientes + ") ")
		.append("or cig." + MbcCirurgias.Fields.PAC_CODIGO.toString()
				+ " IN (" + sListaCodigosPacientes + ") ")
		.append("or fichaAnest." + MbcFichaAnestesias.Fields.PAC_CODIGO.toString()
				+ " IN (" + sListaCodigosPacientes + ") ")
		.append("or notaAdic." + MamNotaAdicionalEvolucoes.Fields.PAC_CODIGO.toString()
				+ " IN (" + sListaCodigosPacientes + ") )")
		
		.append("and atend." + AghAtendimentos.Fields.PAC_CODIGO.toString()
				+ " is not null ")
		
		.append("group by pac." ).append( AipPacientes.Fields.CODIGO.toString());
		
		Query query = this.createQuery(hql.toString());
		
		List<Object[]> lista = query.getResultList();

		return lista;

	}

	public Long listarPendentesResponsavelCount(RapServidores responsavel) {

		DetachedCriteria criteria = getCriteriaPendentes(responsavel);

		return this.executeCriteriaCount(criteria);
	}

	public Long listarPendentesResponsavelPacienteCount(
			RapServidores responsavel, AipPacientes paciente) {

		DetachedCriteria criteria = getCriteriaPendentes(responsavel);
		adicionarCriteriaPaciente(paciente, criteria);

		return this.executeCriteriaCount(criteria);
	}

	public Long listarPendentesPacienteCount(RapServidores responsavel,
			AipPacientes paciente) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);

		adicionarCriteriaPaciente(paciente, criteria);

		//criteria.add(Restrictions.ne(
		//		AghVersaoDocumento.Fields.RESPONSAVEL.toString(), responsavel));

		adicionarCriteriaResponsavel(responsavel, criteria);

		criteria.add(Restrictions.eq(
				AghVersaoDocumento.Fields.SITUACAO.toString(),
				DominioSituacaoVersaoDocumento.P));

		return this.executeCriteriaCount(criteria);
	}

	private void adicionarCriteriaResponsavel(RapServidores responsavel,
			DetachedCriteria criteria) {
		criteria.add(Restrictions.or(Restrictions.ne(AghVersaoDocumento.Fields.RESPONSAVEL.toString()+"."+RapServidores.Fields.MATRICULA.toString(), responsavel.getId().getMatricula()), 
				Restrictions.ne(AghVersaoDocumento.Fields.RESPONSAVEL.toString()+"."+RapServidores.Fields.CODIGO_VINCULO, responsavel.getId().getVinCodigo())));
	}

	public Long listarPendentesCount(RapServidores responsavel,
			AipPacientes paciente) {

		DetachedCriteria criteria = getCriteriaPendentes(responsavel);

		DetachedCriteria criteriaDocumento = criteria
				.createCriteria(AghVersaoDocumento.Fields.DOCUMENTO.toString());

		removerCriteriaPaciente(paciente, criteriaDocumento);

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Retorna criteria para documentos pendentes do responsável fornecido.
	 * 
	 * @param responsavel
	 * @return
	 */
	private DetachedCriteria getCriteriaPendentes(
			final RapServidores responsavel) {
		if (responsavel == null) {
			throw new IllegalArgumentException(
					"Parâmetro responsável é obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);

		criteria.add(Restrictions.eq(
				AghVersaoDocumento.Fields.RESPONSAVEL.toString(), responsavel));

		criteria.add(Restrictions.eq(
				AghVersaoDocumento.Fields.SITUACAO.toString(),
				DominioSituacaoVersaoDocumento.P));

		return criteria;
	}

	/**
	 * @author gandriotti
	 * @param responsavel
	 * @return
	 */
	public AghVersaoDocumento pesquisarPendenciaMaisAntiga(
			final RapServidores responsavel) {

		AghVersaoDocumento result = null;
		List<AghVersaoDocumento> lista = null;
		DetachedCriteria criteria = null;

		criteria = this.getCriteriaPendentes(responsavel);
		lista = this.executeCriteria(criteria, 0, 1,
				AghVersaoDocumento.Fields.CRIADOEM.toString(), true);
		if ((lista != null) && !lista.isEmpty()) {
			result = lista.get(0);
		}

		return result;
	}

	/**
	 * Adiciona criterios para pesquisa por paciente.
	 * 
	 * @param paciente
	 * @param criteria
	 */
	private void adicionarCriteriaPaciente(final AipPacientes paciente,
			DetachedCriteria criteria) {
		// criterio paciente
		DetachedCriteria criteriaDocumento = criteria
				.createCriteria(AghVersaoDocumento.Fields.DOCUMENTO.toString());

		criteriaDocumento.createAlias(
				AghDocumento.Fields.ATENDIMENTO.toString(), "atd",
				Criteria.LEFT_JOIN);
		criteriaDocumento.createAlias(AghDocumento.Fields.CIRURGIA.toString(),
				"cig", Criteria.LEFT_JOIN);
		criteriaDocumento.createAlias(
				AghDocumento.Fields.FICHA_ANESTESICA.toString(), "fic",
				Criteria.LEFT_JOIN);
		criteriaDocumento.createAlias(
				AghDocumento.Fields.NOTA_ADICIONAL_EVOLUCAO.toString(), "not",
				Criteria.LEFT_JOIN);

		Disjunction disjunction = Restrictions.disjunction();

		disjunction.add(Restrictions.eq("atd."
				+ AghAtendimentos.Fields.PACIENTE.toString(), paciente));
		disjunction.add(Restrictions.eq(
				"cig." + MbcCirurgias.Fields.PACIENTE.toString(), paciente));
		disjunction.add(Restrictions.eq("fic."
				+ MbcFichaAnestesias.Fields.PACIENTE.toString(), paciente));
		disjunction.add(Restrictions.eq("not."
				+ MamNotaAdicionalEvolucoes.Fields.PACIENTE.toString(),
				paciente));

		criteriaDocumento.add(disjunction);

	}

	/**
	 * Remove criterios para pesquisa por paciente.
	 * 
	 * @param paciente
	 * @param criteriaDocumento
	 */
	private void removerCriteriaPaciente(final AipPacientes paciente,
			DetachedCriteria criteriaDocumento) {
		criteriaDocumento.createAlias(
				AghDocumento.Fields.ATENDIMENTO.toString(), "atd",
				Criteria.LEFT_JOIN);
		criteriaDocumento.createAlias(AghDocumento.Fields.CIRURGIA.toString(),
				"cig", Criteria.LEFT_JOIN);
		criteriaDocumento.createAlias(
				AghDocumento.Fields.FICHA_ANESTESICA.toString(), "fic",
				Criteria.LEFT_JOIN);
		criteriaDocumento.createAlias(
				AghDocumento.Fields.NOTA_ADICIONAL_EVOLUCAO.toString(), "not",
				Criteria.LEFT_JOIN);

		Disjunction disjunction = Restrictions.disjunction();

		disjunction.add(Restrictions.ne("atd."
				+ AghAtendimentos.Fields.PACIENTE.toString(), paciente));
		disjunction.add(Restrictions.ne(
				"cig." + MbcCirurgias.Fields.PACIENTE.toString(), paciente));
		disjunction.add(Restrictions.ne("fic."
				+ MbcFichaAnestesias.Fields.PACIENTE.toString(), paciente));
		disjunction.add(Restrictions.ne("not."
				+ MamNotaAdicionalEvolucoes.Fields.PACIENTE.toString(),
				paciente));

		criteriaDocumento.add(disjunction);
	}

	private DetachedCriteria montaCriteria(RapServidores responsavel) {
		DetachedCriteria criteria = getCriteriaPendentes(responsavel);

		return criteria;
	}

	private DetachedCriteria montaCriteria(AipPacientes paciente) {
		if (paciente == null) {
			throw new IllegalArgumentException(
					"Parâmetro paciente é obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);

		adicionarCriteriaPaciente(paciente, criteria);

		criteria.add(Restrictions.eq(
				AghVersaoDocumento.Fields.SITUACAO.toString(),
				DominioSituacaoVersaoDocumento.P));

		return criteria;
	}

	public List<AghVersaoDocumento> listarDocumentosDoPacienteOrdenadoPorCriadoEm(
			AipPacientes paciente) {
		if (paciente == null) {
			throw new IllegalArgumentException(
					"Parâmetro paciente é obrigatório");
		}

		DetachedCriteria criteria = this
				.montarCriteriaDocumentosAssinadosPaciente(paciente);
		criteria.addOrder(Order.asc(AghVersaoDocumento.Fields.CRIADOEM
				.toString()));
		return executeCriteria(criteria);
	}

	public boolean verificarExisteDocumentosPaciente(AipPacientes paciente){
		Long countAtd = verificaExisteDocumentosPorAtendimento(paciente);
		if (countAtd != null && countAtd > 0) {
			return true;
		}		
		Long countCig = verificaExisteDocumentosPorCirurgia(paciente);
		if (countCig != null && countCig > 0){
			return true;
		}		
		Long countFic = verificaExisteDocumentosPorFichaAnestesica(paciente);
		if (countFic != null && countFic > 0){
			return true;
		}		
		Long countNota = verificaExisteDocumentosPorNotaAdicionalEvolucao(paciente);
		if (countNota != null && countNota > 0){
			return true;
		}		
		
		return false;
	}

	private Long verificaExisteDocumentosPorAtendimento(AipPacientes paciente) {
		DetachedCriteria criteria = montaCriteriaSituacaoPendente();

		DetachedCriteria criteriaDocumentoAtd = criteria.createCriteria(AghVersaoDocumento.Fields.DOCUMENTO.toString());

		criteriaDocumentoAtd.createAlias(AghDocumento.Fields.ATENDIMENTO.toString(), "atd", Criteria.LEFT_JOIN);
		criteriaDocumentoAtd.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PACIENTE.toString(), paciente));

		return executeCriteriaCount(criteria);
	}

	private Long verificaExisteDocumentosPorCirurgia(AipPacientes paciente) {
		DetachedCriteria criteria = montaCriteriaSituacaoPendente();

		DetachedCriteria criteriaDocumentoCig = criteria.createCriteria(AghVersaoDocumento.Fields.DOCUMENTO.toString());
		criteriaDocumentoCig.createAlias(AghDocumento.Fields.CIRURGIA.toString(), "cig", Criteria.LEFT_JOIN);
		criteriaDocumentoCig.add(Restrictions.eq("cig." + MbcCirurgias.Fields.PACIENTE.toString(), paciente));

		return executeCriteriaCount(criteria);
	}

	private Long verificaExisteDocumentosPorFichaAnestesica(AipPacientes paciente) {
		DetachedCriteria criteria = montaCriteriaSituacaoPendente();

		DetachedCriteria criteriaDocumentoFic = criteria.createCriteria(AghVersaoDocumento.Fields.DOCUMENTO.toString());
		criteriaDocumentoFic.createAlias(AghDocumento.Fields.FICHA_ANESTESICA.toString(), "fic", Criteria.LEFT_JOIN);
		criteriaDocumentoFic.add(Restrictions.eq("fic." + MbcFichaAnestesias.Fields.PACIENTE.toString(), paciente));

		return executeCriteriaCount(criteria);
	}

	private Long verificaExisteDocumentosPorNotaAdicionalEvolucao(AipPacientes paciente) {
		DetachedCriteria criteria = montaCriteriaSituacaoPendente();

		DetachedCriteria criteriaDocumentoNot = criteria.createCriteria(AghVersaoDocumento.Fields.DOCUMENTO.toString());
		criteriaDocumentoNot.createAlias(AghDocumento.Fields.NOTA_ADICIONAL_EVOLUCAO.toString(), "not", Criteria.LEFT_JOIN);
		criteriaDocumentoNot.add(Restrictions.eq("not." + MamNotaAdicionalEvolucoes.Fields.PACIENTE.toString(), paciente));
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montaCriteriaSituacaoAssinado() {	
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);
		criteria.add(Restrictions.eq(
				AghVersaoDocumento.Fields.SITUACAO.toString(),
				DominioSituacaoVersaoDocumento.A));
		return criteria;
	}
	
	private DetachedCriteria montaCriteriaSituacaoPendente() {	
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);
		criteria.add(Restrictions.eq(
				AghVersaoDocumento.Fields.SITUACAO.toString(),
				DominioSituacaoVersaoDocumento.P));
		return criteria;
	}

	private DetachedCriteria montarCriteriaDocumentosAssinadosPaciente(
			AipPacientes paciente) {
		DetachedCriteria criteria = montaCriteriaSituacaoAssinado();		
		adicionarCriteriaPaciente(paciente, criteria);
		return criteria;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<Object[]> pesquisaPendenciaAssinaturaDigital(
			RapServidores rapServidores, FccCentroCustos fccCentroCustos,
			DominioOrdenacaoRelatorioControlePendencias ordenacao) {

		StringBuilder hql = new StringBuilder(400);

		/*
		 * select rappessoas2_.NOME as col_0_0_, rapservido1_.MATRICULA as
		 * col_1_0_, rapservido1_.VIN_CODIGO as col_1_0_, count(*) as col_4_0_,
		 * fcccentroc3_.descricao, fcccentroc4_.descricao from
		 * AGH.AGH_VERSOES_DOCUMENTOS aghversaod0_ inner join AGH.RAP_SERVIDORES
		 * rapservido1_ on
		 * aghversaod0_.SER_MATRICULA_RESP=rapservido1_.MATRICULA and
		 * aghversaod0_.SER_VIN_CODIGO_RESP=rapservido1_.VIN_CODIGO left outer
		 * join AGH.FCC_CENTRO_CUSTOS fcccentroc3_ on
		 * rapservido1_.CCT_CODIGO_ATUA=fcccentroc3_.CODIGO left outer join
		 * AGH.FCC_CENTRO_CUSTOS fcccentroc4_ on
		 * rapservido1_.CCT_CODIGO=fcccentroc4_.CODIGO , AGH.RAP_PESSOAS_FISICAS
		 * rappessoas2_ where rapservido1_.PES_CODIGO=rappessoas2_.CODIGO and
		 * (fcccentroc3_.descricao is not null or fcccentroc4_.descricao is not
		 * null) and aghversaod0_.SITUACAO='P' group by rapservido1_.MATRICULA ,
		 * rappessoas2_.NOME, fcccentroc3_.descricao, fcccentroc4_.descricao ,
		 * rapservido1_.VIN_CODIGO
		 */

		hql.append("select pf." ).append( RapPessoasFisicas.Fields.NOME.toString()
				).append( ", ")
		.append("serv." ).append( RapServidores.Fields.MATRICULA.toString() ).append( ", ")
		.append("vin." ).append( RapVinculos.Fields.CODIGO ).append( ", ")
		.append("cca." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append("ccl." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")

		.append("count(*) ")

		.append("from " ).append( AghVersaoDocumento.class.getSimpleName()
				).append( " vDoc ")
		.append("inner join vDoc."
				).append( AghVersaoDocumento.Fields.RESPONSAVEL.toString() ).append( " serv ")
		.append("inner join serv."
				).append( RapServidores.Fields.PESSOA_FISICA.toString() ).append( " pf ")
		.append("inner join serv." ).append( RapServidores.Fields.VINCULO.toString()
				).append( " vin ")
		.append("left outer join serv."
				).append( RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString()
				).append( " cca ")
		.append("left outer join serv."
				).append( RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString()
				).append( " ccl ")

		.append("where (cca." ).append( FccCentroCustos.Fields.DESCRICAO.toString()
				).append( " is not null  or ccl." ).append( FccCentroCustos.Fields.DESCRICAO
				).append( " is not null) ")
		.append("and vDoc." ).append( AghVersaoDocumento.Fields.SITUACAO.toString()
				).append( " = '" ).append( DominioSituacaoVersaoDocumento.P.toString() ).append( "' ");

		if (rapServidores != null) {
			hql.append("and serv = :servidor ");
		}

		if (fccCentroCustos != null) {
			hql.append("and coalesce(cca, ccl) = :centroCusto ");
		}

		hql.append("group by serv." ).append( RapServidores.Fields.MATRICULA.toString()
			).append( ", ")
		.append("pf." ).append( RapPessoasFisicas.Fields.NOME.toString() ).append( ", ")
		.append("cca." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append("ccl." ).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ")
		.append("vin." ).append( RapVinculos.Fields.CODIGO.toString() ).append( ' ');

		if (ordenacao != null) {
			if (ordenacao
					.equals(DominioOrdenacaoRelatorioControlePendencias.NP)) {
				hql.append("order by pf."
					).append( RapPessoasFisicas.Fields.NOME.toString());
			} else if (ordenacao
					.equals(DominioOrdenacaoRelatorioControlePendencias.M)) {
				hql.append("order by serv."
					).append( RapServidores.Fields.MATRICULA.toString());
			} else if (ordenacao
					.equals(DominioOrdenacaoRelatorioControlePendencias.V)) {
				hql.append("order by vin."
					).append( RapVinculos.Fields.CODIGO.toString());
			} else if (ordenacao
					.equals(DominioOrdenacaoRelatorioControlePendencias.S)) {
				hql.append("order by cca."
					).append( FccCentroCustos.Fields.DESCRICAO.toString() ).append( ", ");
				hql.append("ccl." ).append( FccCentroCustos.Fields.DESCRICAO.toString());
			} else if (ordenacao
					.equals(DominioOrdenacaoRelatorioControlePendencias.TDP)) {
				hql.append("order by 6");
			}
		} else {
			hql.append("order by pf."
				).append( RapPessoasFisicas.Fields.NOME.toString());
		}

		Query query = this.createQuery(hql.toString());
		if (rapServidores != null) {
			query.setParameter("servidor", rapServidores);
		}

		if (fccCentroCustos != null) {
			query.setParameter("centroCusto", fccCentroCustos);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.getResultList();

		return lista;
	}

	/**
	 * Retorna a data do documento pendente mais antigo
	 * 
	 * @param responsavel
	 * @return
	 */
	public Date verificarDocPendenteAntigo(RapServidores responsavel) {
		if (responsavel == null) {
			throw new IllegalArgumentException(
					"Parâmetro responsável é obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);

		criteria = getCriteriaPendentes(responsavel);

		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.min(AghVersaoDocumento.Fields.CRIADOEM
				.toString()));
		criteria.setProjection(projList);

		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public AghVersaoDocumento verificarSituacaoDocumentoPorPaciente(
			Integer atdSeq, DominioTipoDocumento tipo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghVersaoDocumento.class);

		criteria.createAlias(AghVersaoDocumento.Fields.DOCUMENTO.toString(),
				"dok");
		criteria.add(Restrictions.eq(
				"dok." + AghDocumento.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(
				"dok." + AghDocumento.Fields.TIPO.toString(), tipo));

		criteria.add(Restrictions.in(
				AghVersaoDocumento.Fields.SITUACAO.toString(),
				new DominioSituacaoVersaoDocumento[] {
						DominioSituacaoVersaoDocumento.A,
						DominioSituacaoVersaoDocumento.P }));
		criteria.addOrder(Order.desc(AghVersaoDocumento.Fields.CRIADOEM
				.toString()));

		List<AghVersaoDocumento> lista = executeCriteria(criteria);
		if (lista == null || lista.isEmpty()) {
			return null;
		} else {
			return lista.get(0);
		}

	}
	
	/**
	 * Utilizado pela estoria 15838 e 17321
	 *  
	 * @param atdSeq
	 * @param tipoDocumento
	 * @return
	 */
	public List<AghVersaoDocumento> pesquisarDocumentosAssinadosPorAtendimento(Integer atdSeq, DominioTipoDocumento tipoDocumento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class, "versaoDoc");
		criteria.createAlias("versaoDoc.".concat(AghVersaoDocumento.Fields.DOCUMENTO.toString()), "doc");
		
		criteria.add(Restrictions.eq("versaoDoc.".concat(AghVersaoDocumento.Fields.SITUACAO.toString()), DominioSituacaoVersaoDocumento.A));
		criteria.add(Restrictions.eq("doc.".concat(AghDocumento.Fields.TIPO.toString()), tipoDocumento));
		criteria.add(Restrictions.eq("doc.".concat(AghDocumento.Fields.ATD_SEQ.toString()), atdSeq));
		
		return executeCriteria(criteria);
	}

	/**
	 * Utilizado pela estoria 15838 e 17321 
	 * @param atdSeq
	 * @param tipoDocumento
	 * @return
	 */
	public AghVersaoDocumento obterPrimeiroDocumentoAssinadoPorAtendimento(Integer atdSeq, DominioTipoDocumento tipoDocumento) {
		List<AghVersaoDocumento> documentos = pesquisarDocumentosAssinadosPorAtendimento(atdSeq, tipoDocumento);
		AghVersaoDocumento doc = null;
		if (!documentos.isEmpty()) {
			doc = documentos.get(0);
		}
		return doc;
	}
	
	public List<AghVersaoDocumento> pesquisarVersoesDocumentosNotasAdicionais(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class, "versaoDoc");
		criteria.createAlias("versaoDoc." + AghVersaoDocumento.Fields.DOCUMENTO.toString(), "aghDocumento");
		criteria.add(Restrictions.eq("aghDocumento." + AghDocumento.Fields.NPO_SEQ.toString(), seq));
		criteria.add(Restrictions.eq("aghDocumento." + AghDocumento.Fields.TIPO.toString(), DominioTipoDocumento.NPO));
		return executeCriteria(criteria);
	}
	
	public List<AghVersaoDocumento> verificaImprime(Integer seqAgenda, DominioTipoDocumento tipoDoc, List<DominioSituacaoVersaoDocumento> situacoes) {
		List<AghVersaoDocumento> versaoDocumentos = new ArrayList<AghVersaoDocumento>();
		
		DetachedCriteria criteria = criaPesquisaVersaoDocumento(seqAgenda, tipoDoc, situacoes);		
		versaoDocumentos = executeCriteria(criteria);
		
		return versaoDocumentos;
	}

	public List<AghVersaoDocumento> buscarVersaoSeqDoc(Integer seqAgenda, DominioTipoDocumento tipoDoc, List<DominioSituacaoVersaoDocumento> situacoes) {
		List<AghVersaoDocumento> versaoDocumentos = new ArrayList<AghVersaoDocumento>();
		
		DetachedCriteria criteria = criaPesquisaVersaoDocumento(seqAgenda, tipoDoc, situacoes);
		criteria.addOrder(Order.desc("dov." + AghVersaoDocumento.Fields.SEQ.toString()));
		versaoDocumentos = executeCriteria(criteria);
		
		return versaoDocumentos;
	}
	
	public DetachedCriteria criaPesquisaVersaoDocumento(Integer seqAgenda, DominioTipoDocumento tipoDoc, List<DominioSituacaoVersaoDocumento> situacoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class, "dov");
		criteria.createAlias("dov." + AghVersaoDocumento.Fields.DOCUMENTO.toString(), "dok");
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.eq("dok." + AghDocumento.Fields.AGD_SEQ.toString(), seqAgenda)));
		criteria.add(Restrictions.in("dov." + AghVersaoDocumento.Fields.SITUACAO.toString(), situacoes));
		criteria.add(Restrictions.eq("dok." + AghDocumento.Fields.TIPO.toString(), tipoDoc));
		
		return criteria;
	}

	public AghVersaoDocumento obterPorChavePrimariaTranferirResponsavel(
			Integer seqAghVersaoDocumento) {
		AghVersaoDocumento retorno = this.obterPorChavePrimaria(seqAghVersaoDocumento, AghVersaoDocumento.Fields.DOCUMENTO);
		
		if (retorno.getAghDocumentos().getAghAtendimento() != null) {

			retorno.getAghDocumentos().getAghAtendimento().getPaciente().getNome();

		} else if (retorno.getAghDocumentos().getCirurgia() != null) {

			retorno.getAghDocumentos().getCirurgia().getPaciente().getNome();

		} else if (retorno.getAghDocumentos().getFichaAnestesia() != null) {

			retorno.getAghDocumentos().getFichaAnestesia().getPaciente().getNome();

		} else if (retorno.getAghDocumentos().getNotaAdicionalEvolucao() != null) {

			retorno.getAghDocumentos().getNotaAdicionalEvolucao().getPaciente().getNome();

		}
		
		retorno.getServidorResp().getPessoaFisica().getNome();
		
		
		return retorno;
	}
	
	public List<AghVersaoDocumento> pesquisarVersoesLaudoAih(Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class, "dov");
		criteria.createAlias("dov." + AghVersaoDocumento.Fields.DOCUMENTO.toString(), "dok");
		criteria.add(Restrictions.eq("dok." + AghDocumento.Fields.LAI_SEQ.toString(), seq));
		criteria.add(Restrictions.eq("dok." + AghDocumento.Fields.TIPO.toString(), DominioTipoDocumento.AIH));
		return executeCriteria(criteria);
	}
	
	/**
	 * #38995 - Verifica se existe pendência de assinatura digital
	 * @param seqAtendimento
	 * @return
	 */
	public Boolean existePendenciaAssinaturaDigital(Integer seqAtendimento) {
			DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class, "VD");
			criteria.createAlias("VD."+ AghVersaoDocumento.Fields.DOCUMENTO, "DOC");
			criteria.add(Restrictions.eq("DOC."+ AghDocumento.Fields.ATD_SEQ.toString(), seqAtendimento));
			criteria.add(Restrictions.eq("VD."+ AghVersaoDocumento.Fields.SITUACAO.toString(), DominioSituacaoVersaoDocumento.P));
			return executeCriteriaCount(criteria)>0;
		}
	
	
	/**
	 * #39017 - Lista de versões de documentos por tipo
	 * @param seq
	 * @param tipo
	 * @return
	 */
	public List<AghVersaoDocumento> obterAghVersaoDocumentoPorSeqTipoSumari0Alta(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class, "VD");
		criteria.createAlias("VD."+ AghVersaoDocumento.Fields.DOCUMENTO, "DOC");
		criteria.add(Restrictions.eq("DOC."+ AghDocumento.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("DOC."+ AghDocumento.Fields.TIPO.toString(), DominioTipoDocumento.SA));
		return executeCriteria(criteria);
	}

	/**
	 * Web Service #39100
	 * 
	 * Buscar o identificador de um documento e sua versão para determinado tipo de documento e atendimento
	 * 
	 * @param atdSeq
	 * @param codTipoDocumento
	 * @return
	 */
	public List<AghVersaoDocumento> obterAghVersaoDocumentoPorAtendimentoTipoDocumento(Integer atdSeq, DominioTipoDocumento tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class, "V");
		criteria.createAlias("V." + AghVersaoDocumento.Fields.DOCUMENTO.toString(), "D");
		criteria.createAlias("D." + AghDocumento.Fields.ATENDIMENTO.toString(), "A");
		criteria.add(Restrictions.eq("D." + AghDocumento.Fields.TIPO.toString(), tipo));
		criteria.add(Restrictions.eq("A." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		return executeCriteria(criteria);
	}
	
	public List<AghVersaoDocumento> pesquisarAghVersaoDocumentoPorAtendimento(
			Integer atdSeq, DominioTipoDocumento tipo, Integer seqDocCertificado,
			DominioSituacaoVersaoDocumento ... situacoes) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghVersaoDocumento.class,"DOV");

		criteria.createAlias(AghVersaoDocumento.Fields.DOCUMENTO.toString(), "DOC");
		criteria.createAlias("DOC."+AghDocumento.Fields.ATENDIMENTO.toString(), "DOC_ATD", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AghVersaoDocumento.Fields.RESPONSAVEL.toString(), "RESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RESP."+RapServidores.Fields.PESSOA_FISICA.toString(), "RESP_PF", JoinType.LEFT_OUTER_JOIN);
		
		if(atdSeq != null){
			criteria.add(Restrictions.eq("DOC." + AghDocumento.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(tipo != null){
			criteria.add(Restrictions.eq("DOC." + AghDocumento.Fields.TIPO.toString(), tipo));
		}
		if(seqDocCertificado != null){
			criteria.add(Restrictions.eq(AghVersaoDocumento.Fields.SEQ.toString(), seqDocCertificado));
		}
		
		if(situacoes != null && situacoes.length != 0){
			criteria.add(Restrictions.in(AghVersaoDocumento.Fields.SITUACAO.toString(), situacoes));
		}
		
		return executeCriteria(criteria);
	}
}
