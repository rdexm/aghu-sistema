package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoUsoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;

/**
 * Regras de negócio do form AFAF_CAD_GRP_USO_MDT
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class GrupoUsoMedicamentoON extends BaseBusiness {


@EJB
private GrupoUsoMedicamentoRN grupoUsoMedicamentoRN;

private static final Log LOG = LogFactory.getLog(GrupoUsoMedicamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaGrupoUsoMedicamentoDAO afaGrupoUsoMedicamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2790458941117908302L;

	/**
	 * Retorna uma instância de AfaGrupoUsoMedicamento pelo id
	 * 
	 * @param chavePrimaria
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AfaGrupoUsoMedicamento obterPorChavePrimaria(Integer chavePrimaria) {

		return getAfaGrupoUsoMedicamentoDAO().obterPorChavePrimaria(
				chavePrimaria);
	}

	/**
	 * Caso seja um elemento jah existente, atualiza na base, senão, inclui.
	 * 
	 * @param elemento
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public AfaGrupoUsoMedicamento inserirAtualizar(
			AfaGrupoUsoMedicamento elemento) throws ApplicationBusinessException {

		if (elemento.getSeq() == null) {
			// Inserir
			getGrupoUsoMedicamentoRN().preInsertGrupoUsoMedicamento(elemento);
			getAfaGrupoUsoMedicamentoDAO().persistir(elemento);
			getAfaGrupoUsoMedicamentoDAO().flush();
			return elemento;
		} else {
			getAfaGrupoUsoMedicamentoDAO().desatachar(elemento);
			AfaGrupoUsoMedicamento grupoUsoMedicamentoOld = getAfaGrupoUsoMedicamentoDAO()
					.obterPorChavePrimaria(elemento.getSeq());
			getAfaGrupoUsoMedicamentoDAO().desatachar(grupoUsoMedicamentoOld);
			getGrupoUsoMedicamentoRN().preUpdateGrupoUsoMedicamento(
					grupoUsoMedicamentoOld, elemento);
			AfaGrupoUsoMedicamento retorno =  getAfaGrupoUsoMedicamentoDAO().atualizar(elemento);
			getAfaGrupoUsoMedicamentoDAO().flush();
			return retorno;
		}
	}

	/**
	 * Remove o elemento da base
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	public void remover(Integer seqAfaGrupoUsoMedicamento)
			throws BaseException {
		AfaGrupoUsoMedicamento elemento = getAfaGrupoUsoMedicamentoDAO().obterPorChavePrimaria(seqAfaGrupoUsoMedicamento);
		
		if(elemento != null){
		getGrupoUsoMedicamentoRN().preDeleteGrupoUsoMedicamento(elemento);
			try {
				getAfaGrupoUsoMedicamentoDAO().remover(elemento);
				getAfaGrupoUsoMedicamentoDAO().flush();
			} catch (BaseRuntimeException e) {
				if (ApplicationBusinessExceptionCode.OFG_00005.equals(e.getCode())) {
					throw new BaseException(e.getCode(), elemento
							.getDescricao(), elemento.getClass().getSimpleName());
				} else {
					throw e;
				}
			}
		}
		else{
			throw new ApplicationBusinessException (FarmaciaExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
	}

	/**
	 * Busca na base uma lista de AfaGrupoUsoMedicamento pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param grupoUsoMedicamento
	 * @return
	 */
	public List<AfaGrupoUsoMedicamento> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaGrupoUsoMedicamento elemento) {
		List<AfaGrupoUsoMedicamento> grupos = getAfaGrupoUsoMedicamentoDAO().pesquisar(firstResult, maxResult,
				orderProperty, asc, elemento); 
		return grupos; 
	}

	/**
	 * Busca na base o número de elementos da lista de AfaGrupoUsoMedicamento
	 * pelo filtro
	 * 
	 * @param grupoUsoMedicamento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarCount(AfaGrupoUsoMedicamento elemento) {
		return getAfaGrupoUsoMedicamentoDAO().pesquisarCount(elemento);
	}

	protected AfaGrupoUsoMedicamentoDAO getAfaGrupoUsoMedicamentoDAO() {
		return afaGrupoUsoMedicamentoDAO;
	}

	public GrupoUsoMedicamentoRN getGrupoUsoMedicamentoRN() {
		return grupoUsoMedicamentoRN;
	}
}
