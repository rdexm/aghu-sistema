package br.gov.mec.aghu.certificacaodigital.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghCertificadoDigital;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AghCertificadoDigitalDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCertificadoDigital> {

	private static final long serialVersionUID = 6672722916917666624L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghCertificadoDigital.class, "certificado");
		return criteria;
	}

	private DetachedCriteria criarCriteria(AghCertificadoDigital elemento) {
		DetachedCriteria criteria = obterCriteria();

		// Popula criteria com dados do elemento
		if (elemento != null) {

			// RapServidor(vínculo e matrícula)
			if (elemento.getServidorResp() != null) {
				criteria.add(Restrictions.eq(
						AghCertificadoDigital.Fields.SERVIDOR_RESPONSAVEL
								.toString(), elemento.getServidorResp()));
			}
		}
		return criteria;
	}

	public List<AghCertificadoDigital> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghCertificadoDigital elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);

		criteria.createAlias(
				AghCertificadoDigital.Fields.SERVIDOR_RESPONSAVEL.toString(),AghCertificadoDigital.Fields.SERVIDOR_RESPONSAVEL.toString(),
				JoinType.INNER_JOIN);
		criteria.createAlias(AghCertificadoDigital.Fields.SERVIDOR_RESPONSAVEL.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),RapServidores.Fields.PESSOA_FISICA.toString(),
				JoinType.INNER_JOIN);
		criteria.addOrder(Order.asc(orderProperty));
		 
		return executeCriteria(criteria, firstResult, maxResult, null, asc);
	}

	public Long pesquisarCount(AghCertificadoDigital elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}
	
	public Long pesquisarCentroCustoComCertificadoDigitalCount(
			Object objPesquisa) {
		return (long) criarCriteriaCentroCustoComCertificadoDigital(objPesquisa).size();
	}
	
	private List<FccCentroCustos> criarCriteriaCentroCustoComCertificadoDigital(Object objPesquisa){
		
		List<FccCentroCustos> listaCC = new ArrayList<FccCentroCustos>();
		List<AghCertificadoDigital> lista;
		String srtPesquisa = (String) objPesquisa;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghCertificadoDigital.class);

		criteria.createAlias(
				AghCertificadoDigital.Fields.SERVIDOR_RESPONSAVEL.toString(),
				"SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(
				"SER." + RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString(),
				"CCT_ATUA", JoinType.LEFT_OUTER_JOIN);

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.eq("CCT_ATUA."
						+ FccCentroCustos.Fields.CODIGO.toString(),
						Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("CCT_ATUA."
						+ FccCentroCustos.Fields.DESCRICAO.toString(),
						srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		lista = executeCriteria(criteria);

		for (AghCertificadoDigital certificado : lista) {
			if (certificado.getServidorResp().getCentroCustoAtuacao() != null) {
				certificado.getServidorResp().getCentroCustoAtuacao()
						.getDescricao();
				listaCC.add(certificado.getServidorResp()
						.getCentroCustoAtuacao());
			}
		}

		criteria = DetachedCriteria.forClass(AghCertificadoDigital.class);

		criteria.createAlias(
				AghCertificadoDigital.Fields.SERVIDOR_RESPONSAVEL.toString(),
				"SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(
				"SER." + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(),
				"CCT_LOTA", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.isNull("SER."
				+ RapServidores.Fields.CENTRO_CUSTO_ATUACAO.toString()));

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.eq("CCT_LOTA."
						+ FccCentroCustos.Fields.CODIGO.toString(),
						Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("CCT_LOTA."
						+ FccCentroCustos.Fields.DESCRICAO.toString(),
						srtPesquisa, MatchMode.ANYWHERE));
			}
		}
		
		lista = executeCriteria(criteria);

		for (AghCertificadoDigital certificado : lista) {
			if (certificado.getServidorResp().getCentroCustoLotacao() != null) {
				if (!listaCC.contains(certificado.getServidorResp()
						.getCentroCustoLotacao())) {
					certificado.getServidorResp().getCentroCustoLotacao()
							.getDescricao();
					listaCC.add(certificado.getServidorResp()
							.getCentroCustoLotacao());
				}
			}
		}

		return listaCC;
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoComCertificadoDigital(
			Object objPesquisa) {
		
		return criarCriteriaCentroCustoComCertificadoDigital(objPesquisa);
	}

	/**
	 * Pesquisar servidores que tenham certificação digital
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<RapServidores> pesquisarServidorComCertificacaoDigital(
			Object objPesquisa) {

		List<AghCertificadoDigital> list = executeCriteria(this
				.montaCriteria(objPesquisa));
		List<RapServidores> listServidores = new ArrayList<RapServidores>();

		for (AghCertificadoDigital certificadoDigital : list) {
			if (!listServidores.contains(certificadoDigital.getServidorResp())) {
				listServidores.add(certificadoDigital.getServidorResp());
			}
		}

		return listServidores;
	}

	public Long pesquisarServidorComCertificacaoDigitalCount(Object objPesquisa) {
		
		List<AghCertificadoDigital> list = executeCriteria(this
				.montaCriteria(objPesquisa));
		List<RapServidores> listServidores = new ArrayList<RapServidores>();

		for (AghCertificadoDigital certificadoDigital : list) {
			if (!listServidores.contains(certificadoDigital.getServidorResp())) {
				listServidores.add(certificadoDigital.getServidorResp());
			}
		}
		
		return Long.valueOf(listServidores.size());
	}

	private DetachedCriteria montaCriteria(Object objPesquisa) {

		String srtPesquisa = (String) objPesquisa;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghCertificadoDigital.class);

		criteria.createAlias(
				AghCertificadoDigital.Fields.SERVIDOR_RESPONSAVEL.toString(),
				"SER");
		criteria.createAlias(
				"SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(objPesquisa)) {
				criteria.add(Restrictions.eq("SER."
						+ RapServidores.Fields.MATRICULA.toString(),
						Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike("PES."
						+ RapPessoasFisicas.Fields.NOME.toString(),
						srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	public boolean verificaProfissionalHabilitado(RapServidores profissional) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghCertificadoDigital.class);

		criteria.add(Restrictions.eq(
				AghCertificadoDigital.Fields.SERVIDOR_RESPONSAVEL.toString(),
				profissional));
		criteria.add(Restrictions.eq(
				AghCertificadoDigital.Fields.IND_HABILITA_CERTIF.toString(),
				DominioSimNao.S));

		List<Object> result = executeCriteria(criteria);

		if (result != null && !result.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

}
