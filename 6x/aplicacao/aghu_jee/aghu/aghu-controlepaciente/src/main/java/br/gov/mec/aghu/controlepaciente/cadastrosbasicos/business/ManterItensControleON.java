package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpControlePacienteDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemControleDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemCtrlCuidadoEnfDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemCtrlCuidadoMedicoDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpLimiteItemControleDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterItensControleON extends BaseBusiness {

	@Inject
	private EcpControlePacienteDAO ecpControlePacienteDAO;
	
	private static final Log LOG = LogFactory.getLog(ManterItensControleON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EcpItemControleDAO ecpItemControleDAO;
	
	@Inject
	private EcpItemCtrlCuidadoMedicoDAO ecpItemCtrlCuidadoMedicoDAO;
	
	@Inject
	private EcpLimiteItemControleDAO ecpLimiteItemControleDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private EcpItemCtrlCuidadoEnfDAO ecpItemCtrlCuidadoEnfDAO;

	private static final long serialVersionUID = 8040866045449960716L;

	public enum ManterItensControleONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ITEM_CONTROLE_INVALIDO, ERRO_REGISTRO_RELACIONADO, ERRO_PERSISTIR_ITEM_CONTROLE, ERRO_EXCLUIR_ITEM_CONTROLE, ERRO_ATUALIZAR_ITEM_CONTROLE, ERRO_SIGLA_CONTROLE_JA_EXISTENTE, ERRO_DESCRICAO_CONTROLE_JA_EXISTENTE, PARAMETRO_OBRIGATORIO;
	}

	public Long pesquisarItensCount(String sigla, String descricao,EcpGrupoControle grupo, DominioSituacao situacao) {
		return ecpItemControleDAO.pesquisarItensCount(sigla,descricao, grupo, situacao);
	}

	public List<EcpItemControle> pesquisarItens(Integer firstResult,Integer maxResult, String orderProperty, boolean asc, String sigla,
			String descricao, EcpGrupoControle grupo, DominioSituacao situacao) {
		return ecpItemControleDAO.pesquisarItens(firstResult,maxResult, orderProperty, asc, sigla, descricao, grupo,situacao);
	}
	
	public void inserir(EcpItemControle itemControle) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		verificarInconsistenciasItemControle(itemControle);

		itemControle.setCriadoEm(new Date());
		itemControle.setServidor(servidorLogado);
		itemControle.setSeq(null);
		
		ecpItemControleDAO.persistir(itemControle);
	}

	private void verificarInconsistenciasItemControle (EcpItemControle itemControle) throws ApplicationBusinessException {
		if (itemControle == null) {
			throw new ApplicationBusinessException(ManterItensControleONExceptionCode.MENSAGEM_ITEM_CONTROLE_INVALIDO);
		}
		verificarSiglaJaExistente(itemControle);
		verificarDescricaoJaExistente(itemControle);
	}

	private void verificarDescricaoJaExistente(EcpItemControle itemControle) throws ApplicationBusinessException {
		List<EcpItemControle> listEcpItemControles = getEcpItemControleDAO().pesquisarItensPorDescricao(itemControle.getDescricao());
		for (EcpItemControle ecpItemControle : listEcpItemControles) {
			if (! ecpItemControle.getSeq().equals(itemControle.getSeq())) {
				throw new ApplicationBusinessException(ManterItensControleONExceptionCode.ERRO_DESCRICAO_CONTROLE_JA_EXISTENTE);
			}
		}
	}

	private void verificarSiglaJaExistente(EcpItemControle itemControle) throws ApplicationBusinessException {
		List<EcpItemControle> listEcpItemControles = getEcpItemControleDAO().pesquisarItensPorSigla(itemControle.getSigla());
		for (EcpItemControle ecpItemControle : listEcpItemControles) {
			if (! ecpItemControle.getSeq().equals(itemControle.getSeq())) {
				throw new ApplicationBusinessException(ManterItensControleONExceptionCode.ERRO_SIGLA_CONTROLE_JA_EXISTENTE);
			}
		}
		
	}

	public void alterar(EcpItemControle itemControle) throws ApplicationBusinessException {
		verificarInconsistenciasItemControle(itemControle);
		ecpItemControleDAO.merge(itemControle);
	}

	public void excluir(Short seq) throws ApplicationBusinessException {
		EcpItemControle itemControle = ecpItemControleDAO.obterPorChavePrimaria(seq);
		verificarInconsistenciasExclusaoItemControle(itemControle);
		ecpItemControleDAO.remover(itemControle);
	}

	private void verificarInconsistenciasExclusaoItemControle(EcpItemControle itemControle) throws ApplicationBusinessException {
		if (itemControle == null) {
			throw new IllegalArgumentException(ManterItensControleONExceptionCode.PARAMETRO_OBRIGATORIO.toString());
		}
		
		Long nroRegLimite = 0l;
		Long nroRegCuidadosMed = 0l;
		Long nroRegCuidadosEnf = 0l;
		Long nroRegControles = 0l;
		
		
		//tabela agh.ecp_limite_item_controles
		nroRegLimite = getEcpLimiteItemControleDAO().obterNumeroRegistrosItemPorControle(itemControle);
		if (nroRegLimite > 0) {
			throw new ApplicationBusinessException(
					ManterItensControleONExceptionCode.ERRO_REGISTRO_RELACIONADO);
		}
//		List<EcpLimiteItemControle> listaLimites = getEcpLimiteItemControleDAO().pesquisarLimitesItemControle(itemControle);
//		if (listaLimites != null && listaLimites.size() > 0) {
//			throw new ApplicationBusinessException(
//					ManterItensControleONExceptionCode.ERRO_REGISTRO_RELACIONADO);
//		}
//		
//		//tabela agh.ecp_item_ctrl_cuidado_medicos
//		//ecp_cmc_ice_fk1		
		nroRegCuidadosMed = getEcpItemCtrlCuidadoMedicoDAO().obterNumeroRegistrosItemPorControle(itemControle);
		if (nroRegCuidadosMed > 0) {
			throw new ApplicationBusinessException(
					ManterItensControleONExceptionCode.ERRO_REGISTRO_RELACIONADO);
		}

//		List<EcpItemCtrlCuidadoMedico> listaCtrlMedicos = getEcpItemCtrlCuidadoMedicoDAO().obterCuidadoMedicoPorItemControle(itemControle);
//		if (listaCtrlMedicos != null && listaCtrlMedicos.size() > 0) {
//			throw new ApplicationBusinessException(
//					ManterItensControleONExceptionCode.ERRO_REGISTRO_RELACIONADO);
//		}
//		//tabela agh.ecp_item_ctrl_cuidado_enfs
//		//ecp_cen_ice_fk1		
		nroRegCuidadosEnf = getEcpItemCtrlCuidadoEnfDAO().obterNumeroRegistrosItemPorControle(itemControle);
		if (nroRegCuidadosEnf > 0) {
			throw new ApplicationBusinessException(
					ManterItensControleONExceptionCode.ERRO_REGISTRO_RELACIONADO);
		}

//		List<EcpItemCtrlCuidadoEnf> listaCtrlEnfs = getEcpItemCtrlCuidadoEnfDAO().obterEcpItemCtrlCuidadoEnfs(itemControle);
//		if (listaCtrlEnfs != null && listaCtrlEnfs.size() > 0) {
//			throw new ApplicationBusinessException(
//					ManterItensControleONExceptionCode.ERRO_REGISTRO_RELACIONADO);
//		}
	
		// tabela agh.ecp_controle_pacientes
		// ECP_RCP_ICE_FK1
		nroRegControles = getEcpControlePacienteDAO().obterNumeroRegistrosItemPorControle(itemControle);
		if (nroRegControles > 0) {
			throw new ApplicationBusinessException(ManterItensControleONExceptionCode.ERRO_REGISTRO_RELACIONADO);
		}
		
	}

	// DAOs
	private EcpItemCtrlCuidadoMedicoDAO getEcpItemCtrlCuidadoMedicoDAO() {
		return ecpItemCtrlCuidadoMedicoDAO;
	}
	
	private EcpItemCtrlCuidadoEnfDAO getEcpItemCtrlCuidadoEnfDAO() {
		return ecpItemCtrlCuidadoEnfDAO;
	}
	
	protected EcpItemControleDAO getEcpItemControleDAO() {
		return ecpItemControleDAO;
	}
	
	private EcpLimiteItemControleDAO getEcpLimiteItemControleDAO() {
		return ecpLimiteItemControleDAO;
	}
	
	private EcpControlePacienteDAO getEcpControlePacienteDAO() {
		return ecpControlePacienteDAO;
	}

	// Facades
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}