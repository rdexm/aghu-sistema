package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.model.cups.ImpImpressora;

/**
 * 
 * 
 */
public class AghImpressoraPadraoUnidsDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AghImpressoraPadraoUnids> {

	private static final long serialVersionUID = 7427639552917000583L;

	public AghImpressoraPadraoUnids obterImpressoraPadraoUnidPorAtendimento(
			AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghImpressoraPadraoUnids.class);
		criteria.add(Restrictions.eq(
				AghImpressoraPadraoUnids.Fields.CODIGO_UNIDADE.toString(),
				atendimento.getUnidadeFuncional().getSeq()));
		criteria.add(Restrictions.eq(AghImpressoraPadraoUnids.Fields.TIPO
				.toString(),
				TipoDocumentoImpressao.FARMACIA_DISP));
		return (AghImpressoraPadraoUnids) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Retorna a impressora cadastrada para a unidade e tipo de documento
	 * fornecidos.
	 * 
	 * @param unfSeq
	 *            id da unidade funcional
	 * @param tipo
	 *            tipo de documento
	 * @return null se não encontrada e primeira da lista se encontrada mais de
	 *         uma.
	 */
	public AghImpressoraPadraoUnids obterImpressora(Short unfSeq,
			TipoDocumentoImpressao tipo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghImpressoraPadraoUnids.class);

		criteria.createAlias(AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString(), "IMP");
		criteria.add(Restrictions.eq(
				AghImpressoraPadraoUnids.Fields.CODIGO_UNIDADE.toString(),
				unfSeq));

		criteria.add(Restrictions.eq(AghImpressoraPadraoUnids.Fields.TIPO
				.toString(), tipo));

		List<AghImpressoraPadraoUnids> lista = executeCriteria(criteria);
		if (lista.isEmpty()) {
			return null;
		}

		return lista.get(0);
	}

	/**
	 * Retorna a impressora cadastrada para a unidade, IP do computador do
	 * usuário e tipo de documento fornecidos.
	 * 
	 * @param unfSeq
	 *            id da unidade funcional
	 * @param ipComputador
	 *            IP do computador do usuário
	 * @param tipo
	 *            tipo de documento
	 * @return null se não encontrada e primeira da lista se encontrada mais de
	 *         uma.
	 */
	@SuppressWarnings("unchecked")
	public AghImpressoraPadraoUnids obterImpressora(Short unfSeq,
			String ipComputador, TipoDocumentoImpressao tipoDocumentoImpressao) {
		StringBuilder hql = new StringBuilder(200);
		
		hql.append(" select aipu ");
		hql.append(" from ").append(AghImpressoraPadraoUnids.class.getSimpleName()).append(" aipu ");
		hql.append(" 	, ").append(ImpImpressora.class.getSimpleName()).append(" ii ");
		hql.append(" 	, ").append(ImpComputadorImpressora.class.getSimpleName()).append(" ici ");
		hql.append(" 	, ").append(ImpComputador.class.getSimpleName()).append(" ic ");
		hql.append(" where ");
		hql.append(" 	aipu.").append(AghImpressoraPadraoUnids.Fields.CODIGO_UNIDADE.toString()).append(" = :unfSeq ");
		hql.append("	and aipu.").append(AghImpressoraPadraoUnids.Fields.TIPO.toString()).append(" = :tipoDocumentoImpressao ");
		hql.append("	and ic.").append(ImpComputador.Fields.IP.toString()).append(" = :ipComputador ");
		hql.append("	and aipu.").append(AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString()).append(" = ii ");
		hql.append("	and ici.").append(ImpComputadorImpressora.Fields.IMP_COMPUTADOR.toString()).append(" = ic ");
		hql.append("	and ici.").append(ImpComputadorImpressora.Fields.IMP_IMPRESSORA.toString()).append(" = ii ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("unfSeq", unfSeq);
		query.setParameter("ipComputador", ipComputador);
		query.setParameter("tipoDocumentoImpressao", tipoDocumentoImpressao);
		
		List<AghImpressoraPadraoUnids> lista = query.getResultList();
		
		if (lista.isEmpty()) {
			return null;
		}
		
		return lista.get(0);
	}

	/**
	 * Cria o Criteria para AghImpressoraPadraoUnids.
	 * 
	 * @param codUnidade
	 * @return DetachedCriteria
	 */
	private DetachedCriteria createPesquisaImpressorasCriteria(Short codUnidade) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghImpressoraPadraoUnids.class);
		
		criteria.createAlias(AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString(),"IMP", JoinType.LEFT_OUTER_JOIN);	
		//criteria.createAlias(AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString(), AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString(), JoinType.LEFT_OUTER_JOIN);

		if (codUnidade != null) {
			criteria.add(Restrictions.eq(
					AghImpressoraPadraoUnids.Fields.CODIGO_UNIDADE.toString(),
					codUnidade));
		}

		return criteria;
	}
	
	public Long pesquisaImpressorasCount(Short unfSeq) {
		return executeCriteriaCount(createPesquisaImpressorasCriteria(unfSeq));
	}
	
	public List<AghImpressoraPadraoUnids> obterAghImpressoraPadraoUnids(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghImpressoraPadraoUnids.class);

		criteria.createAlias(AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString(),"IMP", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.eq(AghImpressoraPadraoUnids.Fields.CODIGO_UNIDADE.toString(), seq));

		return executeCriteria(criteria);
	}
	
	public List<AghImpressoraPadraoUnids> listarAghImpressoraPadraoUnids(Short seq, TipoDocumentoImpressao tipoDocumentoImpressao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghImpressoraPadraoUnids.class);

		criteria.createAlias(AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString(),"IMP", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.eq(AghImpressoraPadraoUnids.Fields.CODIGO_UNIDADE.toString(), seq));

		if (tipoDocumentoImpressao != null) {
			criteria.add(Restrictions.eq(AghImpressoraPadraoUnids.Fields.TIPO.toString(), tipoDocumentoImpressao));
		}

		return executeCriteria(criteria);
	}

	public List<ImpImpressora> listarImpImpressorasPorUnfSeqETipoDocumentoImpressao(Short unfSeq, TipoDocumentoImpressao tipoImpressora) {
		DetachedCriteria criteria = createPesquisaImpressorasCriteria(unfSeq);

		criteria.add(Restrictions.eq(AghImpressoraPadraoUnids.Fields.TIPO.toString(), tipoImpressora));

		criteria.setProjection(Projections.property(AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString()));

		return executeCriteria(criteria);
	}
	
	public List<ImpImpressora> listarImpImpressorasPorTipoDocumentoImpressao(TipoDocumentoImpressao tipoImpressora) {
		DetachedCriteria criteria = createPesquisaImpressorasCriteria(null);

		criteria.add(Restrictions.eq(AghImpressoraPadraoUnids.Fields.TIPO.toString(), tipoImpressora));

		//criteria.setProjection(Projections.property(AghImpressoraPadraoUnids.Fields.IMP_IMPRESSORA.toString()));
		List<AghImpressoraPadraoUnids> listaAghImpressoraPadraoUnids =  executeCriteria(criteria);
		List<ImpImpressora> listImpImpressora = new ArrayList<ImpImpressora>();
		
		for(AghImpressoraPadraoUnids aghImpressoraPadraoUnids:listaAghImpressoraPadraoUnids){
			listImpImpressora.add(aghImpressoraPadraoUnids.getImpImpressora());
		}

		return listImpImpressora;
	}
	
	public List<AghImpressoraPadraoUnids> listarImpressorasPorTipoDocumentoImpressao(TipoDocumentoImpressao tipoImpressora) {
		DetachedCriteria criteria = createPesquisaImpressorasCriteria(null);

		criteria.add(Restrictions.eq(AghImpressoraPadraoUnids.Fields.TIPO.toString(), tipoImpressora));


		return executeCriteria(criteria);
	}

}