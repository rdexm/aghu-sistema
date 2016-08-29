/**
 * 
 */
package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelTipoAmoExameConvDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmoExameConvJnDAO;
import br.gov.mec.aghu.model.AelTipoAmoExameConv;
import br.gov.mec.aghu.model.AelTipoAmoExameConvJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelTipoAmoExameConvRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelTipoAmoExameConvRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelTipoAmoExameConvJnDAO aelTipoAmoExameConvJnDAO;
	
	@Inject
	private AelTipoAmoExameConvDAO aelTipoAmoExameConvDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5654631084744938856L;

	public enum AelTipoAmoExameConvRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_POS_ATUALIZAR,//
		ERRO_POS_REMOVER, //
		AEL_TIPO_AMO_EXAME_CONV_EXISTENTE,//
		;
	}
	
	
	/**
	 * Insere um registro na tabela<br>
	 * AEL_TIPOS_AMO_EXAME_CONV
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void inserir(AelTipoAmoExameConv elemento) throws BaseException {
		this.verificarConstraints(elemento);
		this.preInserir(elemento);
		this.getAelTipoAmoExameConvDAO().persistir(elemento);
		this.getAelTipoAmoExameConvDAO().flush();
	}
	
	
	/**
	 * ORADB TRIGGER AELT_TEX_BRI
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void preInserir(AelTipoAmoExameConv elemento) throws BaseException {
		//RN1
		elemento.setCriadoEm(new Date());
		//RN2
		this.obterServidorLogado(elemento);
	}
	
	
	/**
	 * Atualiza um registro na tabela<br>
	 * AEL_TIPOS_AMO_EXAME_CONV
	 * 
	 * @param elemento
	 */
	public void atualizar(AelTipoAmoExameConv elemento) throws BaseException {
		AelTipoAmoExameConv oldElemento = this.getAelTipoAmoExameConvDAO()
			.obterOriginal(elemento);
		this.getAelTipoAmoExameConvDAO().merge(elemento);
		this.posAtualizar(elemento, oldElemento);
	}
	
	
	/**
	 * ORADB TRIGGER AELT_TEX_ARU
	 * 
	 * @param elemento
	 * @param oldElemento
	 * @throws BaseException
	 */
	protected void posAtualizar(AelTipoAmoExameConv elemento, 
			AelTipoAmoExameConv oldElemento) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(this.verificarModificacoes(elemento, oldElemento)){
			try {
				AelTipoAmoExameConvJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelTipoAmoExameConvJn.class, servidorLogado.getUsuario());
				this.setarJournal(journal, oldElemento);
				this.getAelTipoAmoExameConvJnDAO().persistir(journal);
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				throw new ApplicationBusinessException(AelTipoAmoExameConvRNExceptionCode.ERRO_POS_ATUALIZAR, 
						AelTipoAmoExameConv.class.getSimpleName());
			}
		}
		
	}
	
	
	/**
	 * Exclui um registro na tabela<br>
	 * AEL_TIPOS_AMO_EXAME_CONV
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	public void excluir(AelTipoAmoExameConv elemento) throws BaseException {
		elemento = getAelTipoAmoExameConvDAO().obterPorChavePrimaria(elemento.getId());
		this.getAelTipoAmoExameConvDAO().remover(elemento);
		this.posExcluir(elemento);
	}
	
	
	/**
	 * ORADB TRIGGER AELT_TEX_ARD
	 * 
	 * @param elemento
	 * @param oldElemento
	 * @throws BaseException
	 */
	protected void posExcluir(AelTipoAmoExameConv elemento) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {
			AelTipoAmoExameConvJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelTipoAmoExameConvJn.class, servidorLogado.getUsuario());
			this.setarJournal(journal, elemento);
			this.getAelTipoAmoExameConvJnDAO().persistir(journal);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(AelTipoAmoExameConvRNExceptionCode.ERRO_POS_REMOVER, 
					AelTipoAmoExameConv.class.getSimpleName());
		}
	}
	
	
	/**
	 * Verifica houve alteracoes<br>
	 * de registro na tabela<br>
	 * AEL_TIPOS_AMO_EXAME_CONV
	 * 
	 * @param elemento
	 * @param oldElemento
	 * @return
	 */
	protected Boolean verificarModificacoes(AelTipoAmoExameConv elemento, AelTipoAmoExameConv oldElemento) {
		if (elemento == null || oldElemento == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		
		return (CoreUtil.modificados(elemento.getId().getCspCnvCodigo(), oldElemento.getId().getCspCnvCodigo())
				|| CoreUtil.modificados(elemento.getId().getCspCnvCodigo(), oldElemento.getId().getCspCnvCodigo())
				|| CoreUtil.modificados(elemento.getId().getCspSeq(), oldElemento.getId().getCspSeq())	
				|| CoreUtil.modificados(elemento.getId().getTaeEmaExaSigla(), oldElemento.getId().getTaeEmaExaSigla())
				|| CoreUtil.modificados(elemento.getId().getTaeEmaManSeq(), oldElemento.getId().getTaeEmaManSeq())
				|| CoreUtil.modificados(elemento.getId().getTaeManSeq(), oldElemento.getId().getTaeManSeq())
				|| CoreUtil.modificados(elemento.getId().getTaeOrigemAtendimento(), oldElemento.getId().getTaeOrigemAtendimento())
				|| CoreUtil.modificados(elemento.getResponsavelColeta(), oldElemento.getResponsavelColeta())
				|| CoreUtil.modificados(elemento.getConvSaudePlanos().getId().getCnvCodigo(), oldElemento.getConvSaudePlanos()
						.getId().getCnvCodigo())
				|| CoreUtil.modificados(elemento.getConvSaudePlanos().getId().getSeq(), oldElemento.getConvSaudePlanos()
						.getId().getSeq())
				|| CoreUtil.modificados(elemento.getServidor().getId().getMatricula(), oldElemento.getServidor().getId().getMatricula())
				|| CoreUtil.modificados(elemento.getServidor().getId().getVinCodigo(), oldElemento.getServidor().getId().getVinCodigo())
				); 
	}
		
	
	/**
	 * Insere um registro<br>
	 * na journal AEL_TIPOS_AMO_EXAME_CONV_JN
	 * 
	 * @param journal
	 * @param oldElemento
	 */
	protected void setarJournal(AelTipoAmoExameConvJn journal, AelTipoAmoExameConv oldElemento) {
		journal.setTaeEmaExaSigla(oldElemento.getId().getTaeEmaExaSigla());
		journal.setTaeEmaManSeq(oldElemento.getId().getTaeEmaManSeq());
		journal.setTaeManSeq(oldElemento.getId().getTaeManSeq());
		journal.setTaeOrigemAtendimento(oldElemento.getId().getTaeOrigemAtendimento().toString());
		journal.setCspCnvCodigo(oldElemento.getId().getCspCnvCodigo());
		journal.setCspSeq(oldElemento.getId().getCspSeq());
		journal.setResponsavelColeta(oldElemento.getResponsavelColeta().toString());
		journal.setCriadoEm(oldElemento.getCriadoEm());
		journal.setSerMatricula(oldElemento.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(oldElemento.getServidor().getId().getVinCodigo());
	}
	
	
	
	/**
	 * Obtem o servidor logado
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException  
	 */
	protected void obterServidorLogado(AelTipoAmoExameConv elemento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		elemento.setServidor(servidorLogado);
	}
	
	
	/**
	 * ORADB CONSTRAINTS (INSERT)
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarConstraints(AelTipoAmoExameConv elemento) throws BaseException {
		
		if (elemento == null 
				|| (elemento.getTipoAmostraExame() == null 
						&& elemento.getConvSaudePlanos() == null)) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		if(elemento.getTipoAmostraExame() == null || this.getAelTipoAmoExameConvDAO()
				.verificarAelTipoAmoExameConvExistente(
						elemento.getTipoAmostraExame().getId().getEmaExaSigla(),
						elemento.getTipoAmostraExame().getId().getEmaManSeq(), 
						elemento.getTipoAmostraExame().getId().getManSeq(), 
						elemento.getTipoAmostraExame().getId().getOrigemAtendimento(), 
						elemento.getConvSaudePlanos().getId().getCnvCodigo(), 
						elemento.getConvSaudePlanos().getId().getSeq().shortValue())) {
			
			throw new ApplicationBusinessException(AelTipoAmoExameConvRNExceptionCode
					.AEL_TIPO_AMO_EXAME_CONV_EXISTENTE);
		}
	}
	
	
	/** GET **/
	protected AelTipoAmoExameConvDAO getAelTipoAmoExameConvDAO() {
		return aelTipoAmoExameConvDAO;
	}
	
	protected AelTipoAmoExameConvJnDAO getAelTipoAmoExameConvJnDAO() {
		return aelTipoAmoExameConvJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
