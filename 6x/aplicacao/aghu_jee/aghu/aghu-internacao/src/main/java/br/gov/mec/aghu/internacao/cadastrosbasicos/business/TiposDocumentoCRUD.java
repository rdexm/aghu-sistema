package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatTiposDocumento;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a
 * entidade de tipo de documento.
 */
@Stateless
public class TiposDocumentoCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TiposDocumentoCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade iFaturamentoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6874930533442198474L;

	/**
	 * Obtem Tipo de documento a partir de código.
	 * 
	 * @param seqTipoDoc
	 * @return FatTiposDocumento
	 */
	public FatTiposDocumento obterTipoDoc(Short seqTipoDoc) {
		return this.getFaturamentoFacade().obterTipoDoc(seqTipoDoc);
	}

	/**
	 * Método para listar os Tipo de Docs de acordo com parametro de pesquisa.
	 * 
	 * @param paramPesquisa
	 * @return List
	 */
	public List<FatTiposDocumento> obterTiposDocs(String seqDesc) {
		return this.getFaturamentoFacade().obterTiposDocs(seqDesc);
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.iFaturamentoFacade;
	}

}
