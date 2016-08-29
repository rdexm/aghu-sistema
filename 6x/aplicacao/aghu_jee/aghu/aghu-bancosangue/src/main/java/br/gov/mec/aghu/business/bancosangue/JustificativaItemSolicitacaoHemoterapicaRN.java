package br.gov.mec.aghu.business.bancosangue;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsItemSolicitacaoHemoterapicaJustificativaDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Implementação da package ABSK_ISJ_RN.
 */

@Stateless
public class JustificativaItemSolicitacaoHemoterapicaRN extends BaseBusiness implements Serializable {
	
	@EJB
	private JustificativaComponenteSanguineoRN justificativaComponenteSanguineoRN;

	private static final Log LOG = LogFactory.getLog(JustificativaItemSolicitacaoHemoterapicaRN.class);

	@Inject
	private AbsJustificativaComponenteSanguineoDAO absJustificativaComponenteSanguineoDAO;

	@Inject
	private AbsItemSolicitacaoHemoterapicaJustificativaDAO absItemSolicitacaoHemoterapicaJustificativaDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8889748812630051304L;

	private enum JustificativaItemSolicitacaoHemoterapicaRNExceptionCode
			implements BusinessExceptionCode {
		JUSTIFICATIVA_NAO_ENCONTRADA, JUSTIFICATIVA_NAO_PERMITE_DESCRICAO_LIVRE;
	}
	
	public void excluirJustificativaItemSolicitacaoHemoterapica (
			AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa) throws ApplicationBusinessException  {
		
		getAbsItemSolicitacaoHemoterapicaJustificativaDAO().remover(itemSolicitacaoHemoterapicaJustificativa);
	}

	public AbsItemSolicitacaoHemoterapicaJustificativa inserirJustificativaItemSolicitacaoHemoterapica(
			AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa) throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preInserirJustificativaItemSolicitacaoHemoterapica(itemSolicitacaoHemoterapicaJustificativa);
		
		getAbsItemSolicitacaoHemoterapicaJustificativaDAO().persistir(itemSolicitacaoHemoterapicaJustificativa);
		
		return itemSolicitacaoHemoterapicaJustificativa;
	}

	public AbsItemSolicitacaoHemoterapicaJustificativa atualizarJustificativaItemSolicitacaoHemoterapica(
			AbsItemSolicitacaoHemoterapicaJustificativa itemSHEJustificativa) throws ApplicationBusinessException {

		AbsItemSolicitacaoHemoterapicaJustificativa oldItemSolicitacaoHemoterapicaJustificativa = new AbsItemSolicitacaoHemoterapicaJustificativa(); 
		Object o = getAbsItemSolicitacaoHemoterapicaJustificativaDAO().obterItemSolicitacaoHemoterapicaVO(
				itemSHEJustificativa.getId().getIshSheAtdSeq()
				, itemSHEJustificativa.getId().getIshSheSeq()
				, itemSHEJustificativa.getId().getIshSequencia()
				, itemSHEJustificativa.getId().getJcsSeq()
		);
		oldItemSolicitacaoHemoterapicaJustificativa.setDescricaoLivre((String)o);
		// Chamada de trigger "before each row"
		this.preAtualizarJustificativaItemSolicitacaoHemoterapica(itemSHEJustificativa, oldItemSolicitacaoHemoterapicaJustificativa);
		
		String descricao = itemSHEJustificativa.getDescricaoLivre();
		Boolean marcado = itemSHEJustificativa.getMarcado();
		AbsItemSolicitacaoHemoterapicaJustificativa itemSolicHemoJustif = getAbsItemSolicitacaoHemoterapicaJustificativaDAO().obterPorChavePrimaria(itemSHEJustificativa.getId()); 
		
		itemSolicHemoJustif.setDescricaoLivre(descricao);
		itemSolicHemoJustif.setMarcado(marcado);
		
		return getAbsItemSolicitacaoHemoterapicaJustificativaDAO().merge(itemSolicHemoJustif);
	}

	/**
	 * ORADB Procedure ABSK_ISJ_RN.RN_ISJP_VER_DESC_LIV
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarDescricaoLivre(
			Integer seqJustificativaComponenteSanguineo)
			throws ApplicationBusinessException {
		if (seqJustificativaComponenteSanguineo != null) {
			AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo = getAbsJustificativaComponenteSanguineoDAO()
					.obterPorChavePrimaria(seqJustificativaComponenteSanguineo);

			if (justificativaComponenteSanguineo == null
					|| justificativaComponenteSanguineo
							.getGrupoJustificativaComponenteSanguineo() == null) {
				throw new ApplicationBusinessException(
						JustificativaItemSolicitacaoHemoterapicaRNExceptionCode.JUSTIFICATIVA_NAO_ENCONTRADA);
			} else if (!justificativaComponenteSanguineo.getDescricaoLivre()) {
				throw new ApplicationBusinessException(
						JustificativaItemSolicitacaoHemoterapicaRNExceptionCode.JUSTIFICATIVA_NAO_PERMITE_DESCRICAO_LIVRE,
						justificativaComponenteSanguineo
								.getGrupoJustificativaComponenteSanguineo()
								.getTitulo());
			}
		}
	}

	/**
	 * ORADB ABST_ISJ_BRI
	 * 
	 * Implementação da trigger de before INSERT da tabela
	 * ABS_ITENS_SOL_HEMO_JUSTIF
	 * 
	 * @param itemSolicitacaoHemoterapicaJustificativa
	 * @throws ApplicationBusinessException
	 */
	public void preInserirJustificativaItemSolicitacaoHemoterapica(
			AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa)
			throws ApplicationBusinessException {
		if (itemSolicitacaoHemoterapicaJustificativa.getDescricaoLivre() != null) {
			this
					.verificarDescricaoLivre(itemSolicitacaoHemoterapicaJustificativa
							.getId() != null ? itemSolicitacaoHemoterapicaJustificativa
							.getId().getJcsSeq()
							: null);
		}
	}

	/**
	 * ORADB ABST_ISJ_BRU
	 * 
	 * Implementação da trigger de before UPDATE da tabela
	 * ABS_ITENS_SOL_HEMO_JUSTIF
	 * 
	 * @param newItemSolicitacaoHemoterapicaJustificativa
	 * @param oldItemSolicitacaoHemoterapicaJustificativa
	 * @throws ApplicationBusinessException
	 */
	public void preAtualizarJustificativaItemSolicitacaoHemoterapica(
			AbsItemSolicitacaoHemoterapicaJustificativa newItemSolicitacaoHemoterapicaJustificativa,
			AbsItemSolicitacaoHemoterapicaJustificativa oldItemSolicitacaoHemoterapicaJustificativa)
			throws ApplicationBusinessException {
		if (newItemSolicitacaoHemoterapicaJustificativa.getDescricaoLivre() != null
				&& !newItemSolicitacaoHemoterapicaJustificativa
						.getDescricaoLivre().equals(
								oldItemSolicitacaoHemoterapicaJustificativa
										.getDescricaoLivre())) {
			this
					.verificarDescricaoLivre(newItemSolicitacaoHemoterapicaJustificativa
							.getId() != null ? newItemSolicitacaoHemoterapicaJustificativa
							.getId().getJcsSeq()
							: null);
		}
	}
	
	protected AbsJustificativaComponenteSanguineoDAO getAbsJustificativaComponenteSanguineoDAO() {
		return absJustificativaComponenteSanguineoDAO;
	}

	protected AbsItemSolicitacaoHemoterapicaJustificativaDAO getAbsItemSolicitacaoHemoterapicaJustificativaDAO() {
		return absItemSolicitacaoHemoterapicaJustificativaDAO;
	}

	protected JustificativaComponenteSanguineoRN getJustificativaComponenteSanguineoRN() {
		return justificativaComponenteSanguineoRN;
	}

}
