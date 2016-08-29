package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoReinternacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;

/**
 * Regras de negócio do form MPMF_CAD_MOTIVO_REIN
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class MotivoReinternacaoON extends BaseBusiness {

	@EJB
	private MotivoReinternacaoRN motivoReinternacaoRN;
	
	private static final Log LOG = LogFactory.getLog(MotivoReinternacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private static final long serialVersionUID = -2648188397121556473L;
	
	@Inject
	private MpmMotivoReinternacaoDAO mpmMotivoReinternacaoDAO;
	

	/**
	 * Retorna uma instância de MpmMotivoReinternacao pelo id
	 * 
	 * @param chavePrimaria
	 * @return
	 */

	public MpmMotivoReinternacao obterPorChavePrimaria(Integer chavePrimaria) {
		return getPrescricaoMedicaFacade().obterMotivoReinternacaoPeloId(chavePrimaria);
	}

	/**
	 * Caso seja um elemento já existente, atualiza na base, senão, inclui.
	 * 
	 * @param elemento
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public MpmMotivoReinternacao inserirAtualizar(MpmMotivoReinternacao elemento) throws ApplicationBusinessException {

		if (elemento.getSeq() != null) {
			// Alterar
			getPrescricaoMedicaFacade().desatachar(elemento);
			MpmMotivoReinternacao motivoReinternacaoOld = getPrescricaoMedicaFacade().obterMotivoReinternacaoPeloId(elemento.getSeq());
			getPrescricaoMedicaFacade().desatachar(motivoReinternacaoOld);
			getMotivoReinternacaoRN().preUpdateMpmMotivoReinternacao(motivoReinternacaoOld, elemento);
			return getPrescricaoMedicaFacade().atualizarMpmMotivoReinternacao(elemento);
		} else {
			// Inserir
			getMotivoReinternacaoRN().preInsertMpmMotivoReinternacao(elemento);
			return getPrescricaoMedicaFacade().inserirMpmMotivoReinternacao(elemento);
		}
	}

	/**
	 * Remove o elemento da base
	 */
	public void remover(final Integer seq) throws ApplicationBusinessException {
		MpmMotivoReinternacao elemento = mpmMotivoReinternacaoDAO.obterPorChavePrimaria(seq);
			
		try {
			getMotivoReinternacaoRN().preDeleteMpmMotivoReinternacao(elemento);
			getPrescricaoMedicaFacade().removerMpmMotivoReinternacao(elemento);
		} catch (BaseRuntimeException e) {
			if (ApplicationBusinessExceptionCode.OFG_00005.equals(e.getCode())) {
				throw new ApplicationBusinessException(e.getCode(), elemento.getDescricao(), elemento.getClass().getSimpleName());
			} else {
				throw e;
			}
		}
	}

	/**
	 * Busca na base uma lista de MpmMotivoReinternacao pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param motivoReinternacao
	 * @return
	 */
	public List<MpmMotivoReinternacao> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			MpmMotivoReinternacao elemento) {

		return getPrescricaoMedicaFacade().pesquisarMpmMotivoReinternacao(firstResult, maxResult, orderProperty, asc, elemento);
	}

	/**
	 * Busca na base o número de elementos da lista de MpmMotivoReinternacao
	 * pelo filtro
	 * 
	 * @param motivoReinternacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarCount(MpmMotivoReinternacao elemento) {
		return getPrescricaoMedicaFacade().pesquisarMpmMotivoReinternacaoCount(elemento);
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public MotivoReinternacaoRN getMotivoReinternacaoRN() {
		return motivoReinternacaoRN;
	}

	public MpmMotivoReinternacaoDAO getMpmMotivoReinternacaoDAO() {
		return mpmMotivoReinternacaoDAO;
	}
}
