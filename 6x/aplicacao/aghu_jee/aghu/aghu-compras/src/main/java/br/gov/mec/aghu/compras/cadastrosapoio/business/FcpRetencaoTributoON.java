package br.gov.mec.aghu.compras.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.FcpRetencaoTributo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FcpRetencaoTributoON extends BaseBusiness {

	private static final long serialVersionUID = 2743726191279600477L;

	@EJB
	private FcpRetencaoTributoRN fcpRetencaoTributoRN;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List obterListaCodigoRecolhimento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpRetencaoTributo fcpRetencaoTributo) {
		List<FcpRetencaoAliquota> retorno = this.getFcpRetencaoTributoRN().obterListaCodigoRecolhimento(firstResult, maxResult, orderProperty, asc, fcpRetencaoTributo);
		return retorno;
	}

	public Long obterCountCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) {
		Long retorno = this.fcpRetencaoTributoRN.obterCountCodigoRecolhimento(fcpRetencaoTributo);
		return retorno;
	}

	public void inserirCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) {
		this.fcpRetencaoTributoRN.inserirCodigoRecolhimento(fcpRetencaoTributo);
	}

	public void atualizarCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) {
		this.fcpRetencaoTributoRN.atualizarCodigoRecolhimento(fcpRetencaoTributo);
	}

	public void excluirCodigoRecolhimento(FcpRetencaoTributo fcpRetencaoTributo) throws ApplicationBusinessException {
		Integer codigoRecolhimento = fcpRetencaoTributo.getCodigo();
		this.fcpRetencaoTributoRN.excluirCodigoRecolhimento(codigoRecolhimento);
	}
	
	public List<FcpRetencaoTributo> pesquisarRecolhimentoPorCodigoOuDescricao(Object parametro) {
		return this.fcpRetencaoTributoRN.pesquisarRecolhimentoPorCodigoOuDescricao(parametro);
	}
	
	public Long pesquisarRecolhimentoPorCodigoOuDescricaoCount(final String parametro) {
		  return this.fcpRetencaoTributoRN.pesquisarRecolhimentoPorCodigoOuDescricaoCount(parametro);
	}
	
	/**
	 * @return the fcpRetencaoTributoRN
	 */
	private FcpRetencaoTributoRN getFcpRetencaoTributoRN() {
		return fcpRetencaoTributoRN;
	}

	

}
