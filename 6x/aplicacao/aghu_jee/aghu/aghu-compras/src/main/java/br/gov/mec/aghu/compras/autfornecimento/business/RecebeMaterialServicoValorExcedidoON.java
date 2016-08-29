package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.RecebeMaterialServicoON.RecebimentoItemAFONExceptionCode;
import br.gov.mec.aghu.compras.autfornecimento.vo.RecebimentoMaterialServicoVO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * ON responsável pela validação de valores excedidos.
 * 
 * @see RecebeMaterialServicoON
 * @author matheus
 */
@Stateless
public class RecebeMaterialServicoValorExcedidoON extends BaseBusiness {


@Inject
private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	private static final long serialVersionUID = -5334032903806477530L;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	private static final Log LOG = LogFactory.getLog(RecebeMaterialServicoValorExcedidoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Indica se valor (serviço) ou quantidade (compra) é maior que zero.
	 * 
	 * @param itemAf Item de AF
	 * @return Flag
	 */
	public boolean possuiEntrega(RecebimentoMaterialServicoVO itemAf) {
		return (itemAf.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.C)
				&& itemAf.getQtdEntregue() != null && itemAf.getQtdEntregue() > 0)
				|| (itemAf.getTipoSolicitacao().equals(DominioTipoFaseSolicitacao.S)
						&& itemAf.getValorEntregue() != null 
						&& itemAf.getValorEntregue().compareTo(BigDecimal.ZERO) > 0);
	}
 	
	/**
	 * Valida valor unitário excedente permitido conforme item de AF.
	 * 
	 * @param itemRecebimento
	 * @param servidorLogado
	 * @param force
	 * @throws ApplicationBusinessException
	 * @throws ItemRecebimentoValorExcedido
	 */
	public void validarValorUnitarioExcedentePermitido(
			RecebimentoMaterialServicoVO itemRecebimento,
			RapServidores servidorLogado, boolean force)
			throws ApplicationBusinessException, ItemRecebimentoValorExcedido {
		if (!DominioTipoFaseSolicitacao.C.equals(itemRecebimento
				.getTipoSolicitacao())) {
			return;
		}
		
		if (isValorPercentualExcedido(itemRecebimento)) {
			// Busca do percentual permitido para o item
			ScoItemAutorizacaoFornId idItemAF = new ScoItemAutorizacaoFornId();
			idItemAF.setAfnNumero(itemRecebimento.getNumeroAf());
			idItemAF.setNumero(itemRecebimento.getNumeroItemAf());

			ScoItemAutorizacaoForn itemAf = getScoItemAutorizacaoFornDAO()
					.obterPorChavePrimaria(idItemAF);

			// Caso o item seja um material e o usuário não possuir permissão
			// para informar valor total excedendo o percentual permitido
			// verifica-se se o valor informado excede esta margem
			if (!cascaFacade.usuarioTemPermissao(servidorLogado.getUsuario(),
					"excederPerVariaValor")) {
				throw new ApplicationBusinessException(
						RecebimentoItemAFONExceptionCode.MENSAGEM_VALOR_PERCENTUAL_EXCEDIDO,
						itemRecebimento.getNumeroItemAf(), itemRecebimento
								.getNomeMaterialServico(), itemRecebimento
								.getParcela(), itemAf.getPercVarPreco());
			// Se não lança itens com valores excedidos a fim de que a operação seja confirmada.
			} else if (!force) {
				String msg = getBundle().getString(
						RecebimentoItemAFONExceptionCode.MENSAGEM_VALOR_PERCENTUAL_EXCEDIDO.name());
				
				String formatted = MessageFormat.format(msg,
						itemRecebimento.getNumeroItemAf(), itemRecebimento.getNomeMaterialServico(),
						itemRecebimento.getParcela(), itemAf.getPercVarPreco());

				throw new ItemRecebimentoValorExcedido(formatted);
			}
		}
	}
 	
	/**
	 * Verifica se valor do item de recebimento excedeu de acordo com o %
	 * variável de preço do item de AF.
	 * 
	 * @return Flag
	 */
	private boolean isValorPercentualExcedido(RecebimentoMaterialServicoVO itemRecebimento) {
		// Calcula o valor absoluto da diferença entre o valor total informado e
		// o valor unitário multiplicado pela quantidade
		BigDecimal difValores = itemRecebimento.getValorTotal().subtract(
				itemRecebimento.getValorUnitario().multiply(
						new BigDecimal(itemRecebimento.getQtdEntregue())));
		
		difValores = difValores.abs();
		
		if (difValores.setScale(4, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) != 0) {
			// Busca do percentual permitido para o item
			ScoItemAutorizacaoFornId idItemAF = new ScoItemAutorizacaoFornId();
			idItemAF.setAfnNumero(itemRecebimento.getNumeroAf());
			idItemAF.setNumero(itemRecebimento.getNumeroItemAf());
			
			ScoItemAutorizacaoForn itemAF = getScoItemAutorizacaoFornDAO()
					.obterPorChavePrimaria(idItemAF);

			// Calcula o valor diferença máxima
			BigDecimal difMaxValor = itemRecebimento.getValorUnitario()
					.multiply(new BigDecimal(itemAF.getPercVarPreco())
							.divide(new BigDecimal(100)));
			
			difMaxValor = difMaxValor.multiply(
					new BigDecimal(itemRecebimento.getQtdEntregue()));

			// Caso a diferença entre o valor informado e o valor unitário x
			// quantidade seja maior que
			// o percentual permitido é lançado uma excessão bloqueando o
			// recebimento
			boolean excedeu = difValores.compareTo(difMaxValor) > 0;
			return excedeu;
		} else {
			return false;
		}
	}
	
	// Dependências
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO(){
		return scoItemAutorizacaoFornDAO;
	}
	
    protected ResourceBundle getBundle() {
    	return ResourceBundle.getBundle("br.gov.mec.aghu.bundle.MessagesResourceBundle");
    }
}