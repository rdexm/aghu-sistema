package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoDAO;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.exception.BaseRuntimeExceptionCode;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;

/**
 * Regras de negócio do form AFAF_CAD_TP_USO_MDTO
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class TipoUsoMdtoON extends BaseBusiness {


@EJB
private TipoUsoMdtoRN tipoUsoMdtoRN;

private static final Log LOG = LogFactory.getLog(TipoUsoMdtoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaTipoUsoMdtoDAO afaTipoUsoMdtoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5831649341446563612L;

	/**
	 * Retorna uma instância de AfaTipoUsoMdto pelo id
	 * 
	 * @param chavePrimaria
	 * @return
	 */
	public AfaTipoUsoMdto obterPorChavePrimaria(String chavePrimaria) {
		return getAfaTipoUsoMdtoDAO().obterPorChavePrimaria(chavePrimaria);
	}

	/**
	 * Caso seja um elemento já existente, atualiza na base, senão, inclui.
	 * 
	 * @param elemento
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public AfaTipoUsoMdto inserirAtualizar(AfaTipoUsoMdto elemento, Boolean novoRegistro) throws ApplicationBusinessException {

		AfaTipoUsoMdto retorno = elemento;

		
		// CONSTRAINT AFA_TUM_CK6
		if (!((elemento.getIndAvaliacao() != null && !elemento.getIndAvaliacao()) || ((elemento.getIndAvaliacao() != null && elemento.getIndAvaliacao()) && (elemento.getIndExigeJustificativa() != null && elemento.getIndExigeJustificativa())))) {
			throw new ApplicationBusinessException(
					FarmaciaExceptionCode.AVALIACAO_EXIGE_JUSTIFICATIVA);
		}
		
		if (!novoRegistro) {
			// Alterar
			getAfaTipoUsoMdtoDAO().desatachar(elemento);
			AfaTipoUsoMdto tipoUsoMdtoOld = getAfaTipoUsoMdtoDAO()
					.obterPorChavePrimaria(elemento.getSigla());
			getAfaTipoUsoMdtoDAO().desatachar(tipoUsoMdtoOld);
			getTipoUsoMdtoRN().preUpdateTipoUsoMdto(tipoUsoMdtoOld, elemento);
			retorno = getAfaTipoUsoMdtoDAO().atualizar(elemento);
			getAfaTipoUsoMdtoDAO().flush();
			getTipoUsoMdtoRN().posUpdateTipoUsoMdto(tipoUsoMdtoOld, elemento);
		} else {
			// Inserir
			getTipoUsoMdtoRN().preInsertTipoUsoMdto(elemento);
			getAfaTipoUsoMdtoDAO().persistir(elemento);
			getAfaTipoUsoMdtoDAO().flush();
			retorno = elemento;
		}
		return retorno;
	}

	/**
	 * Remove o elemento da base
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void remover(String siglaAfaTipoUsoMdto) throws BaseException {
		AfaTipoUsoMdto elemento = getAfaTipoUsoMdtoDAO().obterPorChavePrimaria(siglaAfaTipoUsoMdto);
		
		if(elemento != null){
			getTipoUsoMdtoRN().preDeleteTipoUsoMdto(elemento);
			
			//@PreRemove
			if (elemento.getAfaMedicamentos() != null && elemento.getAfaMedicamentos().size() > 0) {
				throw new BaseRuntimeException(BaseRuntimeExceptionCode.OFG_00005);
			}
		
			try {
				getAfaTipoUsoMdtoDAO().remover(elemento);
				getAfaTipoUsoMdtoDAO().flush();
			} catch (BaseRuntimeException e) {
				if ( ApplicationBusinessExceptionCode.OFG_00005.toString().equals(e.getCode().toString()) ) {
					throw new BaseException(e.getCode(), elemento.getDescricao(), "Medicamentos");
				} else {
					throw e;
				}
			}
		
			getTipoUsoMdtoRN().posDeleteTipoUsoMdto(elemento);
			
		}
		else{
			throw new ApplicationBusinessException (FarmaciaExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
	}

	/**
	 * Busca na base uma lista de AfaTipoUsoMdto pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param tipoUsoMdto
	 * @return
	 */
	public List<AfaTipoUsoMdto> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaTipoUsoMdto elemento) {

		return getAfaTipoUsoMdtoDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento);
	}

	/**
	 * Busca na base o número de elementos da lista de AfaTipoUsoMdto pelo
	 * filtro
	 * 
	 * @param tipoUsoMdto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarCount(AfaTipoUsoMdto elemento) {
		return getAfaTipoUsoMdtoDAO().pesquisarCount(elemento);
	}

	/**
	 * Pesquisa as tipos de uso de medicamentos ativos
	 * 
	 * @param siglaOuDescricao
	 * @return
	 */
	public List<AfaTipoUsoMdto> pesquisaTipoUsoMdtoAtivos(Object siglaOuDescricao) {
		List<AfaTipoUsoMdto> lista = getAfaTipoUsoMdtoDAO().pesquisaTipoUsoMdtoAtivos(siglaOuDescricao);
		for(AfaTipoUsoMdto afaTipoMdto : lista){
			afaTipoMdto.getRapServidores().getPessoaFisica();
		}
		return lista;
	}

	protected AfaTipoUsoMdtoDAO getAfaTipoUsoMdtoDAO() {
		return afaTipoUsoMdtoDAO;
	}

	public TipoUsoMdtoRN getTipoUsoMdtoRN() {
		return tipoUsoMdtoRN;
	}
}