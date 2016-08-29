package br.gov.mec.aghu.exameselaudos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelPacUnidFuncionaisDAO;
import br.gov.mec.aghu.exames.dao.AelProtocoloInternoUnidsDAO;
import br.gov.mec.aghu.exames.dao.AelProtocoloInternoUnidsJnDAO;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelProtocoloInternoUnids;
import br.gov.mec.aghu.model.AelProtocoloInternoUnidsJn;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;


@Stateless
public class ProtocoloInternoUnidsRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ProtocoloInternoUnidsRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelPacUnidFuncionaisDAO aelPacUnidFuncionaisDAO;
	
	@Inject
	private AelProtocoloInternoUnidsDAO aelProtocoloInternoUnidsDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelProtocoloInternoUnidsJnDAO aelProtocoloInternoUnidsJnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6035014179067312277L;

	private enum ProtocoloInternoUnidsRNExceptionCode implements BusinessExceptionCode {
		AEL_00403,//
		AEL_00478,//
		AEL_00767,//
		VIOLACAO_CONSTRAINT_AEL_PUF_PIU_FK1,//
		MENSAGEM_INFO_PACIENTE_PROTOCOLO_NAO_INFORMADO,//
		VIOLACAO_CONSTRAINT_AEL_PIU_UK1,//
		;
	}
	
	/* ORADB Package AELK_PIU_RN Procedure RN_PIUP_VER_UNID_FUN
     * RN_PIU002 - A unidade funcional informada deverá ter a
     * característica de unidade executora de exames,
     * característica de Protocola Paciente e deverá estar ativa */
	private void verificaUnidadeFuncional(AelProtocoloInternoUnids piu) throws ApplicationBusinessException {
		
		AghUnidadesFuncionais uf = this.getAghuFacade()
			.obterUnidadeFuncionalComCaracteristica(piu.getUnidadeFuncional().getSeq()); //piu.getUnidadeFuncional();
		
		if (!uf.isAtivo()) {
			throw new ApplicationBusinessException(ProtocoloInternoUnidsRNExceptionCode.AEL_00403);
		}
		 
		boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(uf.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES);
		if (!possuiCaracteristica) {
			throw new ApplicationBusinessException(ProtocoloInternoUnidsRNExceptionCode.AEL_00478);
		}
		
		possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(uf.getSeq(), ConstanteAghCaractUnidFuncionais.PROTOCOLA_PACIENTE);
		if (!possuiCaracteristica) {
			throw new ApplicationBusinessException(ProtocoloInternoUnidsRNExceptionCode.AEL_00767);
		}
	}
	
	/**
	 * @ORADB Trigger AELT_PIU_ARI (operacao INS)
	 * @ORADB Trigger AELT_PIU_ARD (operacao DEL)
	 * 
	 * @param piu
	 * @param operacao
	 * @throws ApplicationBusinessException 
	 */
	private void inserirJournal(
			AelProtocoloInternoUnids piu, 
			DominioOperacoesJournal operacao
	) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelProtocoloInternoUnidsJn jn  = BaseJournalFactory.getBaseJournal(operacao, AelProtocoloInternoUnidsJn.class, servidorLogado.getUsuario());
		
		jn.setPacCodigo(piu.getId().getPacCodigo());
		jn.setUnfSeq(piu.getId().getUnidadeFuncional().getSeq());
		jn.setNroProtocolo(piu.getNroProtocolo());
		jn.setCriadoEm(piu.getCriadoEm());
		jn.setServidor(piu.getServidor());
		
		this.getAelProtocoloInternoUnidsJnDAO().persistir(jn);
		this.getAelProtocoloInternoUnidsJnDAO().flush();
	}
	
	// ORADB Triggers AELT_PIU_BRI
	private void preInserir(AelProtocoloInternoUnids piu)
	throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.verificarPacienteInformado(piu);
		this.verificarConstraintAelPiuUK1(piu);
		piu.setServidor(servidorLogado); // aelk_ael_rn.rn_aelp_atu_servidor
		this.verificaUnidadeFuncional(piu); // aelk_piu_rn.rn_piup_ver_unid_fun
		piu.setCriadoEm(new Date()); // :new.CRIADO_EM := l_sysdate;
	}
	
	// Executa triggers de insert e faz a inserção 
	public void inserir(AelProtocoloInternoUnids piu) 
	throws ApplicationBusinessException {
		preInserir(piu); // Trigger AELT_PIU_BRI 
		
		getAelProtocoloInternoUnidsDAO().persistir(piu);
		getAelProtocoloInternoUnidsDAO().flush();
		inserirJournal(piu, DominioOperacoesJournal.INS); // Trigger AELT_PIU_ARI
	}
	
	
	// Executa triggers de delete e faz a exclusão 
	public void excluir(AelProtocoloInternoUnids piu) throws BaseException {
		piu = this.getAelProtocoloInternoUnidsDAO().obterPorChavePrimaria(piu.getId());
		this.verificarConstraints(piu);
		getAelProtocoloInternoUnidsDAO().remover(piu);
		getAelProtocoloInternoUnidsDAO().flush();
		inserirJournal(piu, DominioOperacoesJournal.DEL); // Trigger AELT_PIU_ARD
	}
	
	
	/**
	 * ORADB CONSTRAINTS (DELETE)
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	protected void verificarConstraints(AelProtocoloInternoUnids elemento) throws ApplicationBusinessException {
		List<AelPacUnidFuncionais> pacUnidFuncionaisList = 
			this.getAelPacUnidFuncionaisDAO().listarUnidadesFuncionaisPaciente(
					elemento.getId().getPacCodigo(), elemento.getId().getUnidadeFuncional().getSeq());
		
		//ael_puf_piu_fk1
		if(!pacUnidFuncionaisList.isEmpty()) {
			throw new ApplicationBusinessException(ProtocoloInternoUnidsRNExceptionCode.VIOLACAO_CONSTRAINT_AEL_PUF_PIU_FK1);
		}
	}
	
	/**
	 * Metodo que verifica se foi informado um paciente, pois eh<br>
	 * obrigatorio ter o paciente informado para o cadastro.
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	protected void verificarPacienteInformado(AelProtocoloInternoUnids elemento) throws ApplicationBusinessException {
		if(elemento.getPaciente() == null) {
			throw new ApplicationBusinessException(ProtocoloInternoUnidsRNExceptionCode.MENSAGEM_INFO_PACIENTE_PROTOCOLO_NAO_INFORMADO);
		}
	}
	
	
	/**
	 * ORADB CONSTRAINTS AEL_PIU_UK1 (INSERT)
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	protected void verificarConstraintAelPiuUK1(AelProtocoloInternoUnids elemento) throws ApplicationBusinessException {
		AelProtocoloInternoUnids protocoloInterno = 
			this.getAelProtocoloInternoUnidsDAO()
				.obterProtocoloInterno(
						elemento.getPaciente().getCodigo(), elemento.getUnidadeFuncional().getSeq());
		
		if(protocoloInterno != null) {
			throw new ApplicationBusinessException(ProtocoloInternoUnidsRNExceptionCode.VIOLACAO_CONSTRAINT_AEL_PIU_UK1);
		}
	}
	
	/** GET **/
	protected AelProtocoloInternoUnidsJnDAO getAelProtocoloInternoUnidsJnDAO(){
		return aelProtocoloInternoUnidsJnDAO;
	}
	
	protected AelProtocoloInternoUnidsDAO getAelProtocoloInternoUnidsDAO(){
		return aelProtocoloInternoUnidsDAO;
	}
	
	protected AelPacUnidFuncionaisDAO getAelPacUnidFuncionaisDAO() {
		return aelPacUnidFuncionaisDAO;
	}
	
	protected IAghuFacade getAghuFacade(){
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
			
}
