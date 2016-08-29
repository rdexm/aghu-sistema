package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaTipoApresentacaoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Regras de negócio do form AFA_TIPO_APRES_MDTOS
 * 
 * @author lcmoura
 * 
 */
@Stateless
public class TipoApresentacaoMedicamentoON extends BaseBusiness {


@EJB
private TipoApresentacaoMedicamentoRN tipoApresentacaoMedicamentoRN;

private static final Log LOG = LogFactory.getLog(TipoApresentacaoMedicamentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaTipoApresentacaoMedicamentoDAO afaTipoApresentacaoMedicamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8193769726377938390L;

	/**
	 * Retorna uma instância de AfaTipoApresentacaoMedicamento pelo id
	 * 
	 * @param chavePrimaria
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AfaTipoApresentacaoMedicamento obterPorChavePrimaria(
			String chavePrimaria) {

		return getAfaTipoApresentacaoMedicamentoDAO().obterPorChavePrimaria(
				chavePrimaria);
	}

	/**
	 * Caso seja um elemento já existente, atualiza na base, senão, inclui.
	 * 
	 * @param elemento
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public AfaTipoApresentacaoMedicamento inserirAtualizar(
			AfaTipoApresentacaoMedicamento elemento, Boolean novoRegistro)
			throws ApplicationBusinessException {

		if (!novoRegistro) {
			// Alterar
			getAfaTipoApresentacaoMedicamentoDAO().desatachar(elemento);
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoOld = getAfaTipoApresentacaoMedicamentoDAO()
					.obterOriginal(elemento);
			getTipoApresentacaoMedicamentoRN()
					.preUpdateTipoApresentacaoMedicamento(
							tipoApresentacaoMedicamentoOld, elemento);
			AfaTipoApresentacaoMedicamento retorno = getAfaTipoApresentacaoMedicamentoDAO().atualizar(elemento);
			getAfaTipoApresentacaoMedicamentoDAO().flush();
			return retorno;
		} else {
			// Inserir
			getTipoApresentacaoMedicamentoRN()
					.preInsertTipoApresentacaoMedicamento(elemento);
			getAfaTipoApresentacaoMedicamentoDAO().persistir(elemento);
			getAfaTipoApresentacaoMedicamentoDAO().flush();
			return elemento;
		}
	}

	/**
	 * Remove o elemento da base
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	public void remover(String siglaAfaTipoApresentacaoMedicamento)
			throws BaseException {
		AfaTipoApresentacaoMedicamento elemento = getAfaTipoApresentacaoMedicamentoDAO()
				.obterPorChavePrimaria(siglaAfaTipoApresentacaoMedicamento);
		if(elemento != null){
			getTipoApresentacaoMedicamentoRN().verificarDependenciasDelecao(elemento);
			getTipoApresentacaoMedicamentoRN()
				.preDeleteTipoApresentacaoMedicamento(elemento);
		
			getAfaTipoApresentacaoMedicamentoDAO().remover(elemento);
			getAfaTipoApresentacaoMedicamentoDAO().flush();
		}
		else{
			throw new ApplicationBusinessException (FarmaciaExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
	}

	/**
	 * Busca na base uma lista de AfaTipoApresentacaoMedicamento pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param tipoApresentacaoMedicamento
	 * @return
	 */
	public List<AfaTipoApresentacaoMedicamento> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaTipoApresentacaoMedicamento elemento) {

		return getAfaTipoApresentacaoMedicamentoDAO().pesquisar(firstResult,
				maxResult, orderProperty, asc, elemento);
	}

	/**
	 * Busca na base o número de elementos da lista de
	 * AfaTipoApresentacaoMedicamento pelo filtro
	 * 
	 * @param tipoApresentacaoMedicamento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarCount(AfaTipoApresentacaoMedicamento elemento) {
		return getAfaTipoApresentacaoMedicamentoDAO().pesquisarCount(elemento);
	}
	
	/**
	 * Pesquisa as tipos de apresentação de medicamentos ativos
	 * 
	 * @param siglaOuDescricao
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<AfaTipoApresentacaoMedicamento> pesquisaTipoApresentacaoMedicamentosAtivos(Object siglaOuDescricao){
		return getAfaTipoApresentacaoMedicamentoDAO()
				.pesquisaTipoApresentacaoMedicamentosAtivos(siglaOuDescricao);
	}
	
	

	protected AfaTipoApresentacaoMedicamentoDAO getAfaTipoApresentacaoMedicamentoDAO() {
		return afaTipoApresentacaoMedicamentoDAO;
	}

	public TipoApresentacaoMedicamentoRN getTipoApresentacaoMedicamentoRN() {
		return tipoApresentacaoMedicamentoRN;
	}
}
