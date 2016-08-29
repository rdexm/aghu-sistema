package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpLimiteItemControleDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpLimiteItemControleJnDAO;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.model.EcpLimiteItemControle;
import br.gov.mec.aghu.model.EcpLimiteItemControleJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
/**
 * 
 * @modulo controlepaciente.cadastrosbasicos
 *
 */
@Stateless
public class ManterLimiteItemControleON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterLimiteItemControleON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EcpLimiteItemControleDAO ecpLimiteItemControleDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private EcpLimiteItemControleJnDAO ecpLimiteItemControleJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7372042016295679713L;

	public enum ManterLimiteItemControleONExceptionCode implements
			BusinessExceptionCode {
		PARAMETRO_OBRIGATORIO,
		MENSAGEM_IDADE_MINIMA_MAXIMA,
		MEDIDA_IDADE_MINIMA_DIAS, 
		MEDIDA_IDADE_MINIMA_MESES,
		MEDIDA_IDADE_MINIMA_ANOS,
		MEDIDA_IDADE_MAXIMA_DIAS,
		MEDIDA_IDADE_MAXIMA_MESES,
		MEDIDA_IDADE_MAXIMA_ANOS,
		LIMITE_INFERIOR_SUPERIOR_ERRO,
		LIMITE_INFERIOR_SUPERIOR_NORMAL,
		LIMITE_INFERIOR_ERRO_INFERIOR_NORMAL,
		LIMITE_INFERIOR_ERRO_SUPERIOR_NORMAL,
		LIMITE_INFERIOR_NORMAL_SUPERIOR_ERRO,
		LIMITE_SUPERIOR_NORMAL_SUPERIOR_ERRO,
		MENSAGEM_FALHA_EXISTE_SOBREPOSICAO_IDADES;
	}
	
	public void inserirLimiteItemControle(EcpLimiteItemControle limiteItemControle) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (limiteItemControle == null) {
			throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.PARAMETRO_OBRIGATORIO);
		}
		
		validaIdades(limiteItemControle);
		validaLimites(limiteItemControle);
		
		limiteItemControle.setCriadoEm(new Date());
		limiteItemControle.setServidor(servidorLogado);

		ecpLimiteItemControleDAO.persistir(limiteItemControle);
	}
	
	public void alterarLimiteItemControle(EcpLimiteItemControle limiteItemControle) throws ApplicationBusinessException {
		if (limiteItemControle == null) {
			throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.PARAMETRO_OBRIGATORIO);
		}
		
		EcpLimiteItemControle oldLimiteItemControle =  ecpLimiteItemControleDAO.obterPorChavePrimaria(limiteItemControle.getSeq());
		
		//Journal de Limite de Item de Controle
		journalUpdateLimiteItemControle(limiteItemControle, oldLimiteItemControle);
		
		validaIdades(limiteItemControle);
		validaLimites(limiteItemControle);
		
		ecpLimiteItemControleDAO.merge(limiteItemControle);
	}

	public void excluir(final Integer seq) throws ApplicationBusinessException {
		if (seq == null) {
			throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.PARAMETRO_OBRIGATORIO);
		}
		
		EcpLimiteItemControle limiteItemControle = ecpLimiteItemControleDAO.obterPorChavePrimaria(seq);
		
		//Journal de Limite de Item de Controle
		journalDeleteLimiteItemControle(limiteItemControle);
		ecpLimiteItemControleDAO.remover(limiteItemControle);
	}
	
	private void validaIdades(EcpLimiteItemControle limiteItemControle) throws ApplicationBusinessException {
		
		if(limiteItemControle.getIdadeMinima() > limiteItemControle.getIdadeMaxima()){
			throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MENSAGEM_IDADE_MINIMA_MAXIMA);
		}
		
		if(limiteItemControle.getMedidaIdade().equals(DominioUnidadeMedidaIdade.D)){
			if(limiteItemControle.getIdadeMinima() < 0 || limiteItemControle.getIdadeMinima() > 30){
				throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MEDIDA_IDADE_MINIMA_DIAS);
			}
			if(limiteItemControle.getIdadeMaxima() < 0 || limiteItemControle.getIdadeMaxima() > 30){
				throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MEDIDA_IDADE_MAXIMA_DIAS);
			}
			
		}else{
			if(limiteItemControle.getMedidaIdade().equals(DominioUnidadeMedidaIdade.M)){
				if(limiteItemControle.getIdadeMinima() < 0 || limiteItemControle.getIdadeMinima() > 12){
					throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MEDIDA_IDADE_MINIMA_MESES);
				}
				if(limiteItemControle.getIdadeMaxima() < 0 || limiteItemControle.getIdadeMaxima() > 12){
					throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MEDIDA_IDADE_MAXIMA_MESES);
				}
				
			}else{
				if(limiteItemControle.getMedidaIdade().equals(DominioUnidadeMedidaIdade.A)){
					if(limiteItemControle.getIdadeMinima() < 0 || limiteItemControle.getIdadeMinima() > 99){
						throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MEDIDA_IDADE_MINIMA_ANOS);
					}
					if(limiteItemControle.getIdadeMaxima() < 0 || limiteItemControle.getIdadeMaxima() > 150){
						throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MEDIDA_IDADE_MAXIMA_ANOS);
					}
				}
			}
		} 
		
		validaSobreposicaoIdades(limiteItemControle);								
	}
	
	private void validaSobreposicaoIdades(EcpLimiteItemControle limiteItemControle) throws ApplicationBusinessException {
		List<EcpLimiteItemControle> lista = this.getEcpLimiteItemControleDAO().pesquisarLimitesItemControle(limiteItemControle.getItemControle());
		
		for (EcpLimiteItemControle idades : lista) {		
            if (limiteItemControle.getMedidaIdade() == idades.getMedidaIdade() && !limiteItemControle.equals(idades)){
					if(limiteItemControle.getIdadeMaxima()>= idades.getIdadeMinima() && limiteItemControle.getIdadeMaxima() <= idades.getIdadeMaxima()){
						throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MENSAGEM_FALHA_EXISTE_SOBREPOSICAO_IDADES, idades.getIdadeMinima(), idades.getIdadeMaxima(), idades.getMedidaIdade().getDescricao()); 
					 }else{
						 if(limiteItemControle.getIdadeMinima()>= idades.getIdadeMinima() && limiteItemControle.getIdadeMinima() <= idades.getIdadeMaxima()){
							 throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MENSAGEM_FALHA_EXISTE_SOBREPOSICAO_IDADES, idades.getIdadeMinima(), idades.getIdadeMaxima(), idades.getMedidaIdade().getDescricao());
						 }else{
							 if(limiteItemControle.getIdadeMinima()<= idades.getIdadeMinima() && limiteItemControle.getIdadeMaxima() >= idades.getIdadeMaxima()){
								 throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.MENSAGEM_FALHA_EXISTE_SOBREPOSICAO_IDADES, idades.getIdadeMinima(), idades.getIdadeMaxima(), idades.getMedidaIdade().getDescricao());
							 }
						 }					 
					 }
			 }	
		}    
	}

	private void validaLimites(EcpLimiteItemControle limiteItemControle) throws ApplicationBusinessException {

		if(limiteItemControle.getLimiteInferiorErro().compareTo(limiteItemControle.getLimiteSuperiorErro()) > 0){
			throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.LIMITE_INFERIOR_SUPERIOR_ERRO);
		}
		
		//Se informados limites de normalidade
		if(limiteItemControle.getLimiteInferiorNormal()!= null &&
			limiteItemControle.getLimiteSuperiorNormal()!= null &&
			 limiteItemControle.getLimiteInferiorNormal().compareTo(limiteItemControle.getLimiteSuperiorNormal()) > 0){
			
			throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.LIMITE_INFERIOR_SUPERIOR_NORMAL);
		}
		
		//Se informados limites de normalidade
		if(limiteItemControle.getLimiteInferiorNormal()!= null && limiteItemControle.getLimiteSuperiorNormal()!= null){
			if(limiteItemControle.getLimiteInferiorErro().compareTo(limiteItemControle.getLimiteInferiorNormal()) > 0){
				throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.LIMITE_INFERIOR_ERRO_INFERIOR_NORMAL);
			}
			
			if(limiteItemControle.getLimiteInferiorErro().compareTo(limiteItemControle.getLimiteSuperiorNormal()) > 0){
				throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.LIMITE_INFERIOR_ERRO_SUPERIOR_NORMAL);
			}
			
			if(limiteItemControle.getLimiteInferiorNormal().compareTo(limiteItemControle.getLimiteSuperiorErro())> 0){
				throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.LIMITE_INFERIOR_NORMAL_SUPERIOR_ERRO);
			}
			
			if(limiteItemControle.getLimiteSuperiorNormal().compareTo(limiteItemControle.getLimiteSuperiorErro())> 0){
				throw new ApplicationBusinessException(ManterLimiteItemControleONExceptionCode.LIMITE_SUPERIOR_NORMAL_SUPERIOR_ERRO);
			}
		}
	}
	
	private void journalUpdateLimiteItemControle(EcpLimiteItemControle limiteNovo, EcpLimiteItemControle limiteOriginal) {

		// Verificar efetivade da alteração entre o novo limite e o clone do objeto original.
		if (dadosAlterados(limiteNovo, limiteOriginal)) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			// Criar Dados de BaseJournal através da Factory
			EcpLimiteItemControleJn limiteItemControleJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, EcpLimiteItemControleJn.class, servidorLogado.getUsuario());
			
			limiteItemControleJn.setCriadoEm(limiteOriginal.getCriadoEm());
			limiteItemControleJn.setIdadeMaxima(limiteOriginal.getIdadeMaxima());
			limiteItemControleJn.setIdadeMinima(limiteOriginal.getIdadeMinima());
			limiteItemControleJn.setItemControle(limiteOriginal.getItemControle());
			limiteItemControleJn.setLimiteInferiorErro(limiteOriginal.getLimiteInferiorErro());
			
			if(limiteOriginal.getLimiteInferiorNormal() != null){
				limiteItemControleJn.setLimiteInferiorNormal(limiteOriginal.getLimiteInferiorNormal());
			}
			
			limiteItemControleJn.setLimiteSuperiorErro(limiteOriginal.getLimiteSuperiorErro());
			
			if(limiteOriginal.getLimiteSuperiorNormal() != null){
				limiteItemControleJn.setLimiteSuperiorNormal(limiteOriginal.getLimiteSuperiorNormal());
			}
			
			limiteItemControleJn.setMedidaIdade(limiteOriginal.getMedidaIdade());
			limiteItemControleJn.setSeq(limiteOriginal.getSeq());
			limiteItemControleJn.setServidor(limiteOriginal.getServidor());
			
			// Inserir dados em Journal
			ecpLimiteItemControleJnDAO.persistir(limiteItemControleJn);
		}
	}
	
	private void journalDeleteLimiteItemControle(EcpLimiteItemControle limiteOriginal){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Criar Dados de BaseJournal através da Factory
		
		EcpLimiteItemControleJn limiteItemControleJn = BaseJournalFactory.getBaseJournal( DominioOperacoesJournal.DEL, 
																								EcpLimiteItemControleJn.class, 
																								servidorLogado.getUsuario());
		
		limiteItemControleJn.setCriadoEm(limiteOriginal.getCriadoEm());
		limiteItemControleJn.setIdadeMaxima(limiteOriginal.getIdadeMaxima());
		limiteItemControleJn.setIdadeMinima(limiteOriginal.getIdadeMinima());
		limiteItemControleJn.setItemControle(limiteOriginal.getItemControle());
		limiteItemControleJn.setLimiteInferiorErro(limiteOriginal.getLimiteInferiorErro());
		
		if(limiteOriginal.getLimiteInferiorNormal() != null){
			limiteItemControleJn.setLimiteInferiorNormal(limiteOriginal.getLimiteInferiorNormal());
		}
		
		limiteItemControleJn.setLimiteSuperiorErro(limiteOriginal.getLimiteSuperiorErro());
		
		if(limiteOriginal.getLimiteSuperiorNormal() != null){
			limiteItemControleJn.setLimiteSuperiorNormal(limiteOriginal.getLimiteSuperiorNormal());
		}
		
		limiteItemControleJn.setMedidaIdade(limiteOriginal.getMedidaIdade());
		limiteItemControleJn.setSeq(limiteOriginal.getSeq());
		limiteItemControleJn.setServidor(limiteOriginal.getServidor());
		
		//Inserir dados em Journal
		ecpLimiteItemControleJnDAO.persistir(limiteItemControleJn);
	}
	
	private boolean dadosAlterados(EcpLimiteItemControle limiteNovo, EcpLimiteItemControle limiteOriginal) {

		boolean alterouDados = false;
		
		if(!CoreUtil.igual(limiteOriginal.getIdadeMinima(), limiteNovo.getIdadeMinima()) ||
			!CoreUtil.igual(limiteOriginal.getIdadeMaxima(), limiteNovo.getIdadeMaxima()) ||
			!CoreUtil.igual(limiteOriginal.getLimiteInferiorErro(),limiteNovo.getLimiteInferiorErro()) ||
			!CoreUtil.igual(limiteOriginal.getLimiteInferiorNormal(),limiteNovo.getLimiteInferiorNormal()) ||
			!CoreUtil.igual(limiteOriginal.getLimiteSuperiorErro(),limiteNovo.getLimiteSuperiorErro()) ||
			!CoreUtil.igual(limiteOriginal.getLimiteSuperiorNormal(),limiteNovo.getLimiteSuperiorNormal()) ||
			!CoreUtil.igual(limiteOriginal.getMedidaIdade(), limiteNovo.getMedidaIdade())){
 			
			alterouDados = true;
		}
	
		return alterouDados;
	}
	
	// DAOs
	protected EcpLimiteItemControleDAO getEcpLimiteItemControleDAO(){
		return ecpLimiteItemControleDAO;
	}
	
	protected EcpLimiteItemControleJnDAO getEcpLimiteItemControleJnDAO(){
		return ecpLimiteItemControleJnDAO;
	}
	
	//Facades
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
