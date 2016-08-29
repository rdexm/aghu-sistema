package br.gov.mec.aghu.certificacaodigital.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghDocumentoCertificado;

public class AghDocumentoCertificadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghDocumentoCertificado> {

	private static final long serialVersionUID = 7726191317010762889L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghDocumentoCertificado.class);
		return criteria;
	}

	private DetachedCriteria criarCriteria(AghDocumentoCertificado elemento) {
		DetachedCriteria criteria = obterCriteria();

		// Popula criteria com dados do elemento
		if (elemento != null) {

			// Código
			if (elemento.getSeq() != null) {
				criteria.add(Restrictions.eq(
						AghDocumentoCertificado.Fields.SEQ.toString(),
						elemento.getSeq()));
			}

			// Nome
			if (elemento.getNome() != null
					&& !elemento.getNome().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(
						AghDocumentoCertificado.Fields.NOME.toString(),
						elemento.getNome(), MatchMode.ANYWHERE));
			}

			// Identificador
			if (elemento.getIdentificador() != null) {
				criteria.add(Restrictions.eq(
						AghDocumentoCertificado.Fields.IDENTIFICADOR.toString(),
						elemento.getIdentificador()));
			}

			// Tipo Documento
			if (elemento.getTipo() != null) {
				criteria.add(Restrictions.eq(
						AghDocumentoCertificado.Fields.TIPO.toString(),
						elemento.getTipo()));
			}

			// Situação
			if (elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(
						AghDocumentoCertificado.Fields.IND_SITUACAO.toString(),
						elemento.getIndSituacao()));
			}
		}

		return criteria;
	}

	public List<AghDocumentoCertificado> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghDocumentoCertificado elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);

		// Métodos copiados da GenericDAO pois é necessário usar 2 critérios de
		// ordenação
		return executeCriteriaOrdenada(criteria, firstResult, maxResult);
	}

	public AghDocumentoCertificado verificarExistenciaDocumentoCertificadoPorNome(
			String nome, DominioTipoDocumento tipoDocumento) {

		if (nome == null) {
			throw new IllegalArgumentException("Argumentos são obrigatórios");
		}

		DetachedCriteria criteria = obterCriteria();

		criteria.add(Restrictions.eq(
				AghDocumentoCertificado.Fields.NOME.toString(), nome));

		criteria.add(Restrictions.eq(
				AghDocumentoCertificado.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));

		if (tipoDocumento != null) {
			criteria.add(Restrictions.eq(
					AghDocumentoCertificado.Fields.TIPO.toString(),
					tipoDocumento));
		}

		// Se você está pegando uma NotUniqueResultException nesta chamada,
		// provavelmente deveria estar passando o tipo do documento!!!
		return (AghDocumentoCertificado) this
				.executeCriteriaUniqueResult(criteria);
	}

	@SuppressWarnings("unchecked")
	protected List<AghDocumentoCertificado> executeCriteriaOrdenada(
			DetachedCriteria criteria, int firstResult, int maxResults) {

		criteria.addOrder(Order.asc(AghDocumentoCertificado.Fields.IND_SITUACAO
				.toString()));
		criteria.addOrder(Order.asc(AghDocumentoCertificado.Fields.NOME
				.toString()));
		
		return executeCriteria(criteria, firstResult, maxResults, null);
	}

	public Long pesquisarCount(AghDocumentoCertificado elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}

	public AghDocumentoCertificado obterPeloId(Integer codigo) {
		AghDocumentoCertificado elemento = new AghDocumentoCertificado();
		elemento.setSeq(codigo);
		DetachedCriteria criteria = criarCriteria(elemento);
		return (AghDocumentoCertificado) executeCriteriaUniqueResult(criteria);
	}
	
	public List<DominioTipoDocumento[]> listarTipoDocumentosAtivos() {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(
				AghDocumentoCertificado.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(AghDocumentoCertificado.Fields.TIPO.toString()));		
		criteria.setProjection(projList);

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		return executeCriteria(criteria);
		
	}
	
	public List<DominioTipoDocumento> listarTipoDocumentosAtivosDistinct() {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(
				AghDocumentoCertificado.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		
		criteria.setProjection(Projections.distinct(Projections.property(AghDocumentoCertificado.Fields.TIPO.toString())));
		
		return executeCriteria(criteria);
		
	}

}
