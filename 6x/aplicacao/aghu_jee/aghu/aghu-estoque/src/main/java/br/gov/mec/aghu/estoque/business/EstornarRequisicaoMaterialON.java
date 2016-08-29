package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria #5630 - Estornar uma requisição de material
 * @author aghu
 *
 */
@Stateless
public class EstornarRequisicaoMaterialON extends BaseBusiness {

	private static final long serialVersionUID = 5959174924562415031L;
	private static final Log LOG = LogFactory.getLog(EstornarRequisicaoMaterialON.class);
	@EJB
	private SceReqMateriaisRN sceReqMateriaisRN;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public void alterarSituacaoGeradaRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		sceReqMateriais.setIndSituacao(DominioSituacaoRequisicaoMaterial.G);
		getSceReqMateriaisRN().atualizar(sceReqMateriais, nomeMicrocomputador);
	}
	
	public void cancelarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		sceReqMateriais.setIndSituacao(DominioSituacaoRequisicaoMaterial.A);
		sceReqMateriais.setServidorCancelado(servidorLogado);
		getSceReqMateriaisRN().atualizar(sceReqMateriais, nomeMicrocomputador);
	}
	
	public void estornarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException{
		sceReqMateriais.setEstorno(true);
		getSceReqMateriaisRN().atualizar(sceReqMateriais, nomeMicrocomputador);
	}
	
	/**
	 * get de RNs e DAOs
	 */
	protected SceReqMateriaisRN getSceReqMateriaisRN(){
		return sceReqMateriaisRN;
	}
	
}
