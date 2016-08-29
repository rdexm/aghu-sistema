package br.gov.mec.aghu.compras.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloPendenteVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FcpTituloPendentePagamentoRN extends BaseBusiness{
	
	private static final long serialVersionUID = 8649102437825650381L;

	@Inject
	private FcpTituloDAO fcpTitulo;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	/**
	 * Método responsável por fornecer a coleção de títulos pendentes
	 * @param dtInicialVencimentoTitulo data inicial de vencimento do título
	 * @param dtFinalVencimentoTitulo data final de vencimento do título
	 * @param dtInicialEmissaoDocumentoFiscal data inicial de emissão de documento fiscal
	 * @param dtFinalEmissaoDocumentoFiscal data final de emissão de documento fiscal
	 * @param fornecedor fornecedor
	 * @return coleção de títulos pendentes
	 * @throws ApplicationBusinessException Exceção caso alguma regra de negócio não seja atendida.
	 */
	public List<TituloPendenteVO> pesquisarTituloPendentePagamento(Date dtInicialVencimentoTitulo, 
			Date dtFinalVencimentoTitulo, Date dtInicialEmissaoDocumentoFiscal, 
			Date dtFinalEmissaoDocumentoFiscal, ScoFornecedor fornecedor) throws ApplicationBusinessException {
		return this.getFcpTitulo().pesquisaDividaSemNF(dtInicialVencimentoTitulo, dtFinalVencimentoTitulo, dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal, fornecedor);
	}	

	public FcpTituloDAO getFcpTitulo() {
		return this.fcpTitulo;
	}
	
}
