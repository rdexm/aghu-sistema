package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de FORMs e montagem de VOs da estoria #5621 - Efetivar uma requisição de material
 * @author aghu
 *
 */
@Stateless
public class EfetivarRequisicaoMaterialON extends BaseBusiness {

	@EJB
	private SceReqMateriaisRN sceReqMateriaisRN;
	
	private static final Log LOG = LogFactory.getLog(EfetivarRequisicaoMaterialON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = 8809121407754224776L;

	public void efetivarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException{

		// Chamada para RNs com regras de banco
		
		// Define que a situação da requisição e material será E (Efetivada)
		sceReqMateriais.setIndSituacao(DominioSituacaoRequisicaoMaterial.E);
		getSceReqMateriaisRN().atualizar(sceReqMateriais, nomeMicrocomputador);
	}

	@SuppressWarnings("ucd")
	public void gravarItensRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException{
		getSceReqMateriaisRN().atualizar(sceReqMateriais, nomeMicrocomputador);
	}
	
	/**
	 * get de RNs e DAOs
	 */
	private SceReqMateriaisRN getSceReqMateriaisRN(){
		return sceReqMateriaisRN;
	}
}
