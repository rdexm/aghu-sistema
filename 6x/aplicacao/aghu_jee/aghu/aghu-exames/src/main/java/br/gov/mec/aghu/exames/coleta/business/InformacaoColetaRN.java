package br.gov.mec.aghu.exames.coleta.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCumpriuJejumColeta;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelInformacaoColetaDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoColetaJnDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoMdtoColetaDAO;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoColetaId;
import br.gov.mec.aghu.model.AelInformacaoColetaJn;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio migradas de triggers
 * e procedures relacionadas com a entidade AelInformacaoColeta.
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class InformacaoColetaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(InformacaoColetaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelInformacaoColetaDAO aelInformacaoColetaDAO;
	
	@Inject
	private AelInformacaoColetaJnDAO aelInformacaoColetaJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AelInformacaoMdtoColetaDAO aelInformacaoMdtoColetaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5033107500392202794L;
	
	public enum InformacaoColetaRNExceptionCode implements BusinessExceptionCode {
		AEL_02250,// 
		AEL_02267,//
		AEL_02268,//
		AEL_00353,//
		AEL_ICL_CK5;//
	}
	
	/**
	 * Trigger 
	 * 
	 * ORADB: AELT_ICL_BRI
	 * 
	 * @param newInformacaoColeta
	 * @throws ApplicationBusinessException  
	 */
	public void executarBeforeInsertInformacaoColeta(AelInformacaoColeta newInformacaoColeta) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// se menstruação foi informada verifica o sexo do paciente
		if (newInformacaoColeta.getDtUltMenstruacao() != null 
				|| Boolean.TRUE.equals(newInformacaoColeta.getInfMenstruacao())) {
			verificarSexoPaciente(newInformacaoColeta.getSolicitacaoExame());
		}
		
		newInformacaoColeta.setCriadoEm(new Date());
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(InformacaoColetaRNExceptionCode.AEL_00353);
		}
		
		newInformacaoColeta.setServidor(servidorLogado);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: AELP_ENFORCE_ICL_RULES
	 * 
	 * @param newInformacaoColeta
	 * @param operacaoBanco
	 * @throws ApplicationBusinessException
	 */
	public void executarEnforce(AelInformacaoColeta newInformacaoColeta, DominioOperacaoBanco operacaoBanco) 
			throws ApplicationBusinessException {
		if (DominioOperacaoBanco.INS.equals(operacaoBanco))  {
			verificarTotalRegistros(newInformacaoColeta);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: AELK_ICL_RN.RN_ICLP_VER_TOT_REG
	 * 
	 * Verifica se foi informado 1 registro por solicitação.
	 * 
	 * @param informacaoColeta
	 * @throws ApplicationBusinessException 
	 */
	public void verificarTotalRegistros(AelInformacaoColeta informacaoColeta) throws ApplicationBusinessException {
		/*   Só pode ter um registro de informações da coleta por Solicitação */
		if (getAelInformacaoColetaDAO().obterCountInformacaoColetaPorSoeSeq(informacaoColeta.getId().getSoeSeq()) > 1) {
			// -- Ja existem informações da coleta para esta solicitação. Um novo registro não pode ser criado.
			throw new ApplicationBusinessException(InformacaoColetaRNExceptionCode.AEL_02267);
		}
	}
	
	/**
	 * Insere objeto AelInformacaoColeta.
	 * 
	 * @param newInformacaoColeta
	 * @throws ApplicationBusinessException  
	 */
	public void inserirInformacaoColeta(AelInformacaoColeta newInformacaoColeta) throws ApplicationBusinessException {
		executarBeforeInsertInformacaoColeta(newInformacaoColeta);
		getAelInformacaoColetaDAO().persistir(newInformacaoColeta);
		executarEnforce(newInformacaoColeta, DominioOperacaoBanco.INS);
	}
	
	/**
	 * Trigger 
	 * 
	 * ORADB: AELT_ICL_BRU
	 * 
	 * @param oldInformacaoColeta
	 * @param newInformacaoColeta
	 */
	public void executarBeforeUpdateInformacaoColeta(AelInformacaoColeta oldInformacaoColeta, 
			AelInformacaoColeta newInformacaoColeta) throws ApplicationBusinessException {
		// se menstruação modificou e foi preenchida, verifica o sexo do paciente
		if ((CoreUtil.modificados(newInformacaoColeta.getDtUltMenstruacao(), oldInformacaoColeta.getDtUltMenstruacao()) 
				|| CoreUtil.modificados(newInformacaoColeta.getInfMenstruacao(), oldInformacaoColeta.getInfMenstruacao()))
				&& (newInformacaoColeta.getDtUltMenstruacao() != null || Boolean.TRUE.equals(newInformacaoColeta.getInfMenstruacao()))) {
			verificarSexoPaciente(newInformacaoColeta.getSolicitacaoExame());
		}
		
		AelInformacaoColetaId informacaoColetaId = newInformacaoColeta.getId();
		
		verificarMedicamento(informacaoColetaId.getSoeSeq(), informacaoColetaId.getSeqp(), 
				newInformacaoColeta.getInfMedicacao());
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: AELK_ICL_RN.RN_ICLP_VER_MDTOS
	 * 
	 * Verifica se ja existem mdtos associados, caso exista não pode marcar "não soube".
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param infMedicacao
	 */
	public void verificarMedicamento(Integer soeSeq, Short seqp, Boolean infMedicacao) throws ApplicationBusinessException {
		if (Boolean.TRUE.equals(infMedicacao) 
				&& getAelInformacaoMdtoColetaDAO().obterCountInformacaoMdtoColetaPorSoeSeqESeqp(soeSeq, seqp) > 0) {
			throw new ApplicationBusinessException(InformacaoColetaRNExceptionCode.AEL_02250);
		}
	}

	/**
	 * Procedure
	 * 
	 * ORADB: AELK_ICL_RN.RN_ICLP_VER_SEXO_PAC
	 * 
	 * Só incluir se a opção "Paciente não soube Informar" esta desmarcada.
	 * 
	 * @param solicitacaoExame
	 */
	public void verificarSexoPaciente(AelSolicitacaoExames solicitacaoExame) throws ApplicationBusinessException {
		DominioSexo sexo = DominioSexo.getInstance(getExamesFacade().obterLaudoSexoPaciente(solicitacaoExame));
		if (DominioSexo.M.equals(sexo)) {
			throw new ApplicationBusinessException(InformacaoColetaRNExceptionCode.AEL_02268);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_ICL_ARU
	 * 
	 * @param oldInformacaoColeta
	 * @param newInformacaoColeta
	 * @throws ApplicationBusinessException 
	 */
	public void executarAfterUpdateInformacaoColeta(AelInformacaoColeta oldInformacaoColeta, 
			AelInformacaoColeta newInformacaoColeta) throws ApplicationBusinessException {
		if (CoreUtil.modificados(newInformacaoColeta.getInfMenstruacao(), oldInformacaoColeta.getInfMenstruacao()) 
				|| CoreUtil.modificados(newInformacaoColeta.getDtUltMenstruacao(), oldInformacaoColeta.getDtUltMenstruacao()) 
				|| CoreUtil.modificados(newInformacaoColeta.getInformacoesAdicionais(), oldInformacaoColeta.getInformacoesAdicionais())
				|| CoreUtil.modificados(newInformacaoColeta.getCriadoEm(), oldInformacaoColeta.getCriadoEm())
				|| CoreUtil.modificados(newInformacaoColeta.getId(), oldInformacaoColeta.getId())
				|| CoreUtil.modificados(newInformacaoColeta.getServidor(), oldInformacaoColeta.getServidor()) 
				|| CoreUtil.modificados(newInformacaoColeta.getCumpriuJejum(), oldInformacaoColeta.getCumpriuJejum())
				|| CoreUtil.modificados(newInformacaoColeta.getJejumRealizado(), oldInformacaoColeta.getJejumRealizado())
				|| CoreUtil.modificados(newInformacaoColeta.getDocumento(), oldInformacaoColeta.getDocumento())
				|| CoreUtil.modificados(newInformacaoColeta.getInfMedicacao(), oldInformacaoColeta.getInfMedicacao())
				|| CoreUtil.modificados(newInformacaoColeta.getLocalColeta(), oldInformacaoColeta.getLocalColeta())
				|| CoreUtil.modificados(newInformacaoColeta.getTipoAcesso(), oldInformacaoColeta.getTipoAcesso())) {
			
			inserirJournal(oldInformacaoColeta, DominioOperacoesJournal.UPD);
		}
	}	
	
	/**
	 * Atualiza objeto AelInformacaoColeta.
	 * 
	 * @param newInformacaoColeta
	 * @throws ApplicationBusinessException
	 */
	public void atualizarInformacaoColeta(AelInformacaoColeta newInformacaoColeta) throws ApplicationBusinessException {
		AelInformacaoColetaDAO informacaoColetaDAO = getAelInformacaoColetaDAO();
		AelInformacaoColeta oldInformacaoColeta = informacaoColetaDAO.obterOriginal(newInformacaoColeta.getId());
		
		this.executarConstraints(newInformacaoColeta);
		
		executarBeforeUpdateInformacaoColeta(oldInformacaoColeta, newInformacaoColeta);
		informacaoColetaDAO.atualizar(newInformacaoColeta);
		executarAfterUpdateInformacaoColeta(oldInformacaoColeta, newInformacaoColeta);
	}
	
	
	protected void executarConstraints(AelInformacaoColeta elemento) throws ApplicationBusinessException {
		//ael_icl_ck5
		if(!(StringUtils.isBlank(elemento.getJejumRealizado())
				&& (DominioCumpriuJejumColeta.S == elemento.getCumpriuJejum() 
						|| DominioCumpriuJejumColeta.P == elemento.getCumpriuJejum()) 
				|| StringUtils.isNotBlank(elemento.getJejumRealizado()) 
						&&  DominioCumpriuJejumColeta.N == elemento.getCumpriuJejum())) {
			
			throw new ApplicationBusinessException(InformacaoColetaRNExceptionCode.AEL_ICL_CK5);
		}
	}
	
	
	/**
	 * Trigger
	 * 
	 * ORADB: AELT_ICL_ARD
	 * 
	 * @param oldInformacaoColeta
	 * @throws ApplicationBusinessException 
	 */
	public void executarAfterDeleteInformacaoColeta(AelInformacaoColeta oldInformacaoColeta) throws ApplicationBusinessException {
		inserirJournal(oldInformacaoColeta, DominioOperacoesJournal.DEL);
	}
	
	private void inserirJournal(final AelInformacaoColeta oldInformacaoColeta, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelInformacaoColetaJn informacaoColetaJn = new AelInformacaoColetaJn();
		informacaoColetaJn.setNomeUsuario(servidorLogado.getUsuario());
		informacaoColetaJn.setOperacao(operacao);
		informacaoColetaJn.setSoeSeq(oldInformacaoColeta.getId().getSoeSeq());
		informacaoColetaJn.setSeqp(oldInformacaoColeta.getId().getSeqp());
		informacaoColetaJn.setInfMenstruacao(oldInformacaoColeta.getInfMenstruacao());
		informacaoColetaJn.setDtUltMenstruacao(oldInformacaoColeta.getDtUltMenstruacao());
		informacaoColetaJn.setInformacoesAdicionais(oldInformacaoColeta.getInformacoesAdicionais());
		informacaoColetaJn.setCumpriuJejum(oldInformacaoColeta.getCumpriuJejum());
		informacaoColetaJn.setJejumRealizado(oldInformacaoColeta.getJejumRealizado());
		informacaoColetaJn.setDocumento(oldInformacaoColeta.getDocumento());
		informacaoColetaJn.setInfMedicacao(oldInformacaoColeta.getInfMedicacao());
		informacaoColetaJn.setLocalColeta(oldInformacaoColeta.getLocalColeta());
		informacaoColetaJn.setTipoAcesso(oldInformacaoColeta.getTipoAcesso());
		informacaoColetaJn.setCriadoEm(oldInformacaoColeta.getCriadoEm());
		informacaoColetaJn.setSerMatricula(oldInformacaoColeta.getServidor().getId().getMatricula());
		informacaoColetaJn.setSerVinCodigo(oldInformacaoColeta.getServidor().getId().getVinCodigo());
		getAelInformacaoColetaJnDAO().persistir(informacaoColetaJn);
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected AelInformacaoColetaDAO getAelInformacaoColetaDAO() {
		return aelInformacaoColetaDAO;
	}
	
	protected AelInformacaoMdtoColetaDAO getAelInformacaoMdtoColetaDAO() {
		return aelInformacaoMdtoColetaDAO;
	}
	
	protected AelInformacaoColetaJnDAO getAelInformacaoColetaJnDAO() {
		return aelInformacaoColetaJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
