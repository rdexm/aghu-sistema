package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamAreaAtuacaoNumero;
import br.gov.mec.aghu.model.MpmPostoSaude;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MpmPostoSaudeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPostoSaude> {

	private static final long serialVersionUID = 3828117033785678808L;

	public List<MpmPostoSaude> listarPostosSaudePorNumeroLogradouroParesEImpares(Integer atuSeq, Integer nroLogradouro) {
		DetachedCriteria c_atu_nros = DetachedCriteria.forClass(MpmPostoSaude.class)
				.createAlias("areaAtuacaoNumeroPostos", "arp", Criteria.INNER_JOIN)
				.createAlias("areaAtuacaoNumeroPostos.mamAreaAtuNro", "arn", Criteria.INNER_JOIN)
				.add(Restrictions.eq("arn." + MamAreaAtuacaoNumero.Fields.ARA_SEQ, atuSeq));

		if (nroLogradouro % 2 == 0) { // caso c_atu_nros_pares
			c_atu_nros.add(Restrictions.le("arn." + MamAreaAtuacaoNumero.Fields.NRO_INICIAL_PAR, nroLogradouro)).add(
					Restrictions.ge("arn." + MamAreaAtuacaoNumero.Fields.NRO_FINAL_PAR, nroLogradouro));
		} else { // caso c_atu_nros_impares
			c_atu_nros.add(Restrictions.le("arn." + MamAreaAtuacaoNumero.Fields.NRO_INICIAL_IMPAR, nroLogradouro)).add(
					Restrictions.ge("arn." + MamAreaAtuacaoNumero.Fields.NRO_FINAL_IMPAR, nroLogradouro));
		}

		return executeCriteria(c_atu_nros);
	}

	public String obterNomePosto(Integer codigoAreaAtuacao, Integer numeroLogradouro, Integer opcao) {
		if (opcao == null || !(opcao == 0 || opcao == 1)) {
			return null;
		} else {
			StringBuilder sb = new StringBuilder(100);
			sb.append("select pss.descricao ");
			sb.append("from MpmPostoSaude pss, ");
			sb.append(" 	MamAreaAtuacaoNumeroPosto arp, ");
			sb.append("		MamAreaAtuacaoNumero arn ");
			sb.append("where pss.seq = arp.id.pssSeq ");
			sb.append("	and arp.id.arnSeqp = arn.id.seqp ");
			sb.append("	and arp.id.arnAraSeq = arn.id.araSeq ");

			if (opcao == 0) {
				// Busca por numeros pares
				sb.append("and arn.numeroInicialPar <= :numeroLogradouro ");
				sb.append("and arn.numeroFinalPar >= :numeroLogradouro ");
			} else {
				// Busca por numeros ímpares
				sb.append("and arn.numeroInicialImpar <= :numeroLogradouro ");
				sb.append("and arn.numeroFinalImpar >= :numeroLogradouro ");
			}
			sb.append("and arn.id.araSeq = :codigoAreaAtuacao ");

			Query q = this.createQuery(sb.toString());
			q.setParameter("numeroLogradouro", numeroLogradouro);
			q.setParameter("codigoAreaAtuacao", codigoAreaAtuacao);
			q.setMaxResults(1);

			@SuppressWarnings("unchecked")
			List<String> nomePostoList = (List<String>) q.getResultList();
			if (nomePostoList != null && !nomePostoList.isEmpty()) {
				return nomePostoList.get(0);
			}
			return null;
		}
	}
	
	public List<MpmPostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro) {
		final DetachedCriteria criteria = obterCriteriaListarMpmPostoSaudePorSeqDescricao(parametro);
		
		return executeCriteria(criteria, 0, 100, MpmPostoSaude.Fields.DESCRICAO.toString(), true);
	}
	
	public Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro) {
		final DetachedCriteria criteria = obterCriteriaListarMpmPostoSaudePorSeqDescricao(parametro);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaListarMpmPostoSaudePorSeqDescricao(final Object parametro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmPostoSaude.class);
		
		criteria.add(Restrictions.eq(MpmPostoSaude.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if (CoreUtil.isNumeroInteger(parametro)) {
			criteria.add(Restrictions.eq(MpmPostoSaude.Fields.SEQ.toString(), Integer.valueOf(parametro.toString())));
			
		} else if (parametro instanceof String) {
			final String strPesquisa = (String) parametro;
			if (StringUtils.isNotBlank(StringUtils.trim(strPesquisa))) {
				criteria.add(Restrictions.ilike(MpmPostoSaude.Fields.DESCRICAO.toString(), strPesquisa, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
}
