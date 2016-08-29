package br.gov.mec.aghu.sicon.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoLogEnvioSicon;

/**
 * @modulo sicon
 * @author cvagheti
 * 
 */
public class ScoLogEnvioSiconDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoLogEnvioSicon> {

	private static final long serialVersionUID = -8207404773959631895L;

	/**
	 * Pesquisar retorno de integração para envio de contratos
	 * 
	 * @param contrato
	 * @return List<ScoLogEnvioSicon>
	 */
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoContratos(
			ScoContrato contrato) {

		if (contrato == null) {
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoLogEnvioSicon.class);

		// ler dados relacionados com o Contrato
		criteria.add(Restrictions.eq(
				ScoLogEnvioSicon.Fields.CONTRATO.toString(), contrato));

		// que não tenha rescisão
		criteria.add(Restrictions.isNull(ScoLogEnvioSicon.Fields.RESCISAO
				.toString()));

		criteria.addOrder(Order.desc(ScoLogEnvioSicon.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Pesquisar retorno de integração para envio dos aditivos
	 * 
	 * @param contrato
	 * @return List<ScoLogEnvioSicon>
	 */
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoAditivos(
			ScoContrato contrato) {

		if (contrato == null) {
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoLogEnvioSicon.class);

		// Ler dados do Aditivos
		criteria.add(Restrictions.isNotNull(ScoLogEnvioSicon.Fields.ADITIVO_SEQ
				.toString()));
		criteria.add(Restrictions.eq(
				ScoLogEnvioSicon.Fields.ADITIVO_CONT_SEQ.toString(),
				contrato.getSeq()));

		criteria.addOrder(Order.desc(ScoLogEnvioSicon.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Traz a listagem de tentativas de envio de um aditivo.
	 * 
	 * @param contSeq
	 * 		numero do contrato.
	 * @param aditivo
	 * 		aditivo a ser analisado.
	 * @return
	 * 		lista de log de envios.
	 */
	public List<ScoLogEnvioSicon> verificarSucessoEmEnvioAditivos(ScoAditContrato aditivo) {

		if (aditivo == null) {
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLogEnvioSicon.class);

		criteria.add(Restrictions.eq(ScoLogEnvioSicon.Fields.ADITIVO
				.toString(), aditivo));
		criteria.add(Restrictions.eq(
				ScoLogEnvioSicon.Fields.IND_SUCESSO.toString(),
				DominioSimNao.S));

		return executeCriteria(criteria);
	}

	/**
	 * Pesquisar retorno de integração para envio da rescisão
	 * 
	 * @param contrato
	 * @return List<ScoLogEnvioSicon>
	 */
	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoRescicao(
			ScoContrato contrato) {

		if (contrato == null) {
			return null;
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoLogEnvioSicon.class);

		// ler dados relacionados com o contrato
		criteria.add(Restrictions.eq(
				ScoLogEnvioSicon.Fields.CONTRATO.toString(), contrato));

		// que tenha rescisao
		criteria.add(Restrictions.isNotNull(ScoLogEnvioSicon.Fields.RESCISAO
				.toString()));

		criteria.addOrder(Order.desc(ScoLogEnvioSicon.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	public List<ScoLogEnvioSicon> pesquisarRetornoIntegracaoPorAditivo(
			ScoAditContrato aditContrato) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoLogEnvioSicon.class);

		criteria.add(Restrictions.eq(
				ScoLogEnvioSicon.Fields.ADITIVO.toString(), aditContrato));

		return executeCriteria(criteria);
	}
}
