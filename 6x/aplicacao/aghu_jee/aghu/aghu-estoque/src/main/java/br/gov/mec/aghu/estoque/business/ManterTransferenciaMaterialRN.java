package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceTransferenciaDAO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ManterTransferenciaMaterialRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterTransferenciaMaterialRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceTransferenciaDAO sceTransferenciaDAO;

@Inject
private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;

	private static final long serialVersionUID = 6312066064487869867L;

	/**
	 * ORADB: Procedure SCEK_SCE_RN.RN_SCEP_VER_ALM_ATIV
	 * 
	 * RN8: Verifica se o almoxarifado está ativo
	 * 
	 * @param nova
	 * @return
	 */
	public boolean isAlmoxarifadoValido(Short almoxSeq) {
		SceAlmoxarifado almoxarifado = getSceAlmoxarifadoDAO().obterAlmoxarifadoAtivoPorId(almoxSeq);
		if(almoxarifado != null && DominioSituacao.A.equals(almoxarifado.getIndSituacao())) {
			return Boolean.TRUE; 			
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 
	 * @return
	 */
	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}
	
	protected SceTransferenciaDAO getSceTransferenciaDAO(){
		return sceTransferenciaDAO;
	}


	/**
	 * Retorna a quantidade de transferências dependentes do pacote de materiais
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 */
	public Long obterQuantidadeTransferenciasPorCentrosCustosNumeroPacoteMaterial(
			ScePacoteMateriaisId idPacoteMateriais) {
		return getSceTransferenciaDAO().obterQuantidadeTransferenciasPorCentrosCustosNumeroPacoteMaterial(idPacoteMateriais.getCodigoCentroCustoProprietario(), idPacoteMateriais.getCodigoCentroCustoAplicacao(), idPacoteMateriais.getNumero());
	}
	

}
