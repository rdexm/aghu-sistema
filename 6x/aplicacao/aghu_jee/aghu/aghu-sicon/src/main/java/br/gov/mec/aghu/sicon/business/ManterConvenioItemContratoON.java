package br.gov.mec.aghu.sicon.business;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoConvItensContrato;
import br.gov.mec.aghu.model.ScoConvItensContratoId;
import br.gov.mec.aghu.sicon.dao.ScoConvItensContratoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterConvenioItemContratoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterConvenioItemContratoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoConvItensContratoDAO scoConvItensContratoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4364351491607482905L;

	public enum ManterConvenioItemContratoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG, MENSAGEM_ERRO_HIBERNATE_VALIDATION, MENSAGEM_SOMA_VALOR_CONVENIOS_MAIOR_QUE_VALOR_ITEM, MENSAGEM_SOMA_VALOR_CONVENIOS_MENOR_QUE_VALOR_ITEM;
	}

	/**
	 * Grava os dados da grid de lista de convênios.
	 * 
	 * @param _listaConvenios
	 *            Lista a ser persistida
	 * @param _valorTotal
	 *            Valor total definido para o item
	 * @throws BaseException
	 */
	public void gravar(List<ScoConvItensContrato> _listaConvenios,
			List<ScoConvItensContratoId> _listaIdConveniosExcluidos,
			BigDecimal _valorTotal) throws BaseException {
		ScoConvItensContratoDAO scoConvItensContratoDAO = this.getScoConvItensContratoDAO();
		
		if ((_listaConvenios == null) || (_valorTotal == null)) {
			throw new BaseException(
					ManterConvenioItemContratoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		BigDecimal somatorio = BigDecimal.ZERO;

		for (ScoConvItensContrato convItem : _listaConvenios) {
			somatorio = somatorio.add(convItem.getValorRateio());
		}
		
		// RN01 - valor total dos itens de convênio não pode ser diferente do valor do item
		validarValorTotal(somatorio, _valorTotal);
		
		// Exclusão de Registros
		try {
			ScoConvItensContrato item;
			for (ScoConvItensContratoId itemId : _listaIdConveniosExcluidos) {
				item = scoConvItensContratoDAO.obterPorChavePrimaria(itemId);
				if (item != null){
					scoConvItensContratoDAO.remover(item);
				}
			}
			scoConvItensContratoDAO.flush();
			
		} catch (ConstraintViolationException ise) {
			String mensagem = "Valor inválido para o campo ";
			throw new ApplicationBusinessException(
					ManterConvenioItemContratoONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);
		}


		// Inclusão/Edição de Registros
		try {
			for (ScoConvItensContrato item : _listaConvenios) {
				
				if (item.getVersion() == null){
					// Inclusão de convênio
					scoConvItensContratoDAO.persistir(item);
				} else {
					// Atualização de convênio
					scoConvItensContratoDAO.atualizar(item);
				}
			}
			scoConvItensContratoDAO.flush();
		} catch (ConstraintViolationException ise) {
			String mensagem = "Valor inválido para o campo";
			throw new ApplicationBusinessException(
					ManterConvenioItemContratoONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);
		}	
	}

	/**
	 * Garante que o valor da soma dos convênios não é menor nem maior queu o
	 * valor total do item.
	 * 
	 * @param _somatorio
	 *            Somatório dos valores de rateio de cada convênio de item.
	 * @param _valorTotal
	 *            Valor total disponível para o item.
	 * @throws BaseException
	 */

	public void validarValorTotal(BigDecimal _somatorio, BigDecimal _valorTotal)
			throws BaseException {

		if (_somatorio.doubleValue() > _valorTotal.doubleValue()) {
			throw new BaseException(
					ManterConvenioItemContratoONExceptionCode.MENSAGEM_SOMA_VALOR_CONVENIOS_MAIOR_QUE_VALOR_ITEM);
		}
		if (_somatorio.doubleValue() < _valorTotal.doubleValue()) {
			throw new BaseException(
					ManterConvenioItemContratoONExceptionCode.MENSAGEM_SOMA_VALOR_CONVENIOS_MENOR_QUE_VALOR_ITEM);
		}
	}

	protected ScoConvItensContratoDAO getScoConvItensContratoDAO() {
		return scoConvItensContratoDAO;
	}
}
