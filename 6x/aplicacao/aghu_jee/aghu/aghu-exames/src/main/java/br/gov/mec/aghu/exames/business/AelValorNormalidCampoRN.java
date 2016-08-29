package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelValorNormalidCampoDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelValorNormalidCampo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author amalmeida
 * 
 */
@Stateless
public class AelValorNormalidCampoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelValorNormalidCampoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelValorNormalidCampoDAO aelValorNormalidCampoDAO;
	
	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;
	
	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	private static final long serialVersionUID = 5960016652904441045L;

	public enum AelValorNormalidCampoRNExceptionCode implements BusinessExceptionCode {

		AEL_00780,AEL_00782,AEL_00876,AEL_00877,AEL_00878,AEL_00879,AEL_00889,AEL_00883,AEL_00890,AEL_00882,AEL_00881,AEL_00880,AEL_01834,AEL_01836,AEL_01837;

	}

	/**
	 * Restrições (Pré-Insert e Pré-Update):
	 * @param valorNormalidCampo
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void validaPreInsertPreUpdate(AelValorNormalidCampo valorNormalidCampo)	throws BaseException {

		/*AEL_VNC_CK3*/
		if(valorNormalidCampo.getDthrInicial()!=null && valorNormalidCampo.getDthrFinal()!=null && valorNormalidCampo.getDthrInicial().after(valorNormalidCampo.getDthrFinal())){
			//Data final deve ser maior que a data inicial.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00881);
		}

		/*AEL_VNC_CK4*/
		if(valorNormalidCampo.getIdadeMinima() != null && valorNormalidCampo.getIdadeMaxima()!=null && valorNormalidCampo.getIdadeMaxima() < valorNormalidCampo.getIdadeMinima()){
			//Idade máxima deve ser maior que a idade mínima.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00880);
		}

		/*AEL_VNC_CK5*/
		if(valorNormalidCampo.getUnidMedidaIdade() != null && valorNormalidCampo.getUnidMedidaIdadeMin()!=null && valorNormalidCampo.getUnidMedidaIdadeMin() != valorNormalidCampo.getUnidMedidaIdade()){
			//Unidade de tempo das idades devem ser a mesma.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_01834);
		}

		/*AEL_VNC_CK6*/
		if(valorNormalidCampo.getUnidMedidaIdadeMin() != null && valorNormalidCampo.getUnidMedidaIdadeMin().equals(DominioUnidadeMedidaIdade.D) && (valorNormalidCampo.getIdadeMinima() != null && valorNormalidCampo.getIdadeMinima()>30)){
			//A idade mínima em dias deve ser entre 0 e 30, em meses entre 0 e 12 e anos é livre.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_01836);
		}

		/*AEL_VNC_CK6*/
		if(valorNormalidCampo.getUnidMedidaIdadeMin() != null && valorNormalidCampo.getUnidMedidaIdadeMin().equals(DominioUnidadeMedidaIdade.M) && (valorNormalidCampo.getIdadeMinima() != null && valorNormalidCampo.getIdadeMinima()>12)){
			//A idade mínima em dias deve ser entre 0 e 30, em meses entre 0 e 12 e anos é livre.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_01836);
		}

		/*AEL_VNC_CK7*/
		if(valorNormalidCampo.getUnidMedidaIdade() != null && valorNormalidCampo.getUnidMedidaIdade().equals(DominioUnidadeMedidaIdade.D) && valorNormalidCampo.getIdadeMaxima() != null && valorNormalidCampo.getIdadeMaxima()>30){
			//A idade mínima em dias deve ser entre 0 e 30, em meses entre 0 e 12 e anos é livre.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_01837);
		}

		/*AEL_VNC_CK7*/
		if(valorNormalidCampo.getUnidMedidaIdade() != null && valorNormalidCampo.getUnidMedidaIdade().equals(DominioUnidadeMedidaIdade.M) && valorNormalidCampo.getIdadeMaxima() != null && valorNormalidCampo.getIdadeMaxima()>12){
			//A idade mínima em dias deve ser entre 0 e 30, em meses entre 0 e 12 e anos é livre.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_01837);
		}
	}

	/**
	 * ORADB TRIGGER AELT_VNC_BRI (INSERT) 
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	private void preInserir(AelValorNormalidCampo valorNormalidCampo) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Restrições BD (Pré-Insert e Pré-Update)
		this.validaPreInsertPreUpdate(valorNormalidCampo);

		/*TRIGGER AELT_VNC_BRI*/
		valorNormalidCampo.setDthrInicial(new Date());//RN1
		valorNormalidCampo.setServidor(servidorLogado);//RN2
		this.validaCampoLaudo(valorNormalidCampo);//RN3
		this.validaValorMaxMin(valorNormalidCampo);//RN4
		this.validaValorMaxMinAceitavel(valorNormalidCampo);//RN5
		this.validaValorMaxMinAbsurdo(valorNormalidCampo);//RN6


	}

	/**
	 * PROCEDURE RN_VNCP_VER_VLR_ABSU
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	public void validaValorMaxMinAbsurdo(AelValorNormalidCampo valorNormalidCampo)	throws BaseException {
		if(valorNormalidCampo.getValorMinimoAbsurdo() != null && valorNormalidCampo.getValorMaximoAbsurdo()!=null){
			//Valida se são numéricos
			if(this.validaVlrNumerico(valorNormalidCampo.getValorMinimoAbsurdo()) && this.validaVlrNumerico(valorNormalidCampo.getValorMaximoAbsurdo())){

				if(Integer.parseInt(valorNormalidCampo.getValorMaximoAbsurdo()) < Integer.parseInt( valorNormalidCampo.getValorMinimoAbsurdo())){
					//Valor máximo deve ser maior que o valor mínimo.
					throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00879);
				}

			}

		}
	}

	/**
	 * PROCEDURE RN_VNCP_VER_VLR
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	public void validaValorMaxMin(AelValorNormalidCampo valorNormalidCampo)
	throws BaseException {
		if(valorNormalidCampo.getValorMinimo() != null && valorNormalidCampo.getValorMaximo()!=null){

			//Valida se são numéricos
			if(this.validaVlrNumerico(valorNormalidCampo.getValorMinimo()) && this.validaVlrNumerico(valorNormalidCampo.getValorMaximo())){

				if(Integer.parseInt(valorNormalidCampo.getValorMaximo()) < Integer.parseInt( valorNormalidCampo.getValorMinimo())){
					//Valor máximo deve ser maior que o valor mínimo.
					throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00877);
				}

			}

		}
	}

	/**
	 * PROCEDURE RN_VNCP_VER_VLR_ACEI
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	public void validaValorMaxMinAceitavel(AelValorNormalidCampo valorNormalidCampo) throws BaseException {

		if(valorNormalidCampo.getValorMinimoAceitavel() != null && valorNormalidCampo.getValorMaximoAceitavel()!=null){

			//Valida se são numéricos
			if(this.validaVlrNumerico(valorNormalidCampo.getValorMinimoAceitavel()) && this.validaVlrNumerico(valorNormalidCampo.getValorMaximoAceitavel())){

				if(Integer.parseInt(valorNormalidCampo.getValorMaximoAceitavel()) < Integer.parseInt( valorNormalidCampo.getValorMinimoAceitavel())){
					//Valor máximo aceitável  deve ser maior que o valor mínimo aceitável.
					throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00878);
				}

			}

		}
	}

	private Boolean validaVlrNumerico(String valor) {
		Boolean retorno = true;
		try {  
			Integer.parseInt(valor);  
		} catch( NumberFormatException ex ) {  
			retorno =false;
		}

		return retorno;

	}

	/**
	 * ORADB PROCEDURE RN_VNCP_VER_TP_CAMPO
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	public void validaCampoLaudo(AelValorNormalidCampo valorNormalidCampo)
	throws BaseException {
		AelCampoLaudo campoLaudo = getAelCampoLaudoDAO().obterPorChavePrimaria(valorNormalidCampo.getId().getCalSeq());

		if(campoLaudo==null){
			//Campo Laudo não encontrado.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00780);
		}

		if(campoLaudo.getSituacao()!=null && campoLaudo.getSituacao().equals(DominioSituacao.I)){
			//Campo Laudo está Inativo
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00782);
		}

		if(campoLaudo.getTipoCampo()!=null && !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.N) && !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.E) 
				&& !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.A) ){
			//Apenas campo tipo numérico, expressão e alfanumérico permite valores de normalidade
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00876);
		}
	}

	/**
	 * Inserir AelvalorNormalidCampo
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	public void inserir(AelValorNormalidCampo valorNormalidCampo) throws BaseException{
		this.preInserir(valorNormalidCampo);		
		this.getAelValorNormalidCampoDAO().persistir(valorNormalidCampo);
		this.posInserir(valorNormalidCampo);
		this.getAelValorNormalidCampoDAO().flush();
	}


	/**
	 * ORADB PROCEDURE AELP_ENFORCE_VNC_RULES (INSERT)
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	private void posInserir(AelValorNormalidCampo valorNormalidCampo) throws BaseException{

		this.validaSobreposicaoIdadeSexo(valorNormalidCampo);

	}

	/**
	 *  PROCEDURE RN_VNCP_VER_SOBREP
	 *  Verifica sobreposição de idade e sexo para o mesmo período pela consulta
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	private void validaSobreposicaoIdadeSexo(AelValorNormalidCampo valorNormalidCampo) throws BaseException {

		List<AelValorNormalidCampo> listValores = getAelValorNormalidCampoDAO().pesquisarSobreposicaoNormalidadesCampoLaudo(valorNormalidCampo);
		if(listValores != null && listValores.size() > 0){
			throw new ApplicationBusinessException(AelValorNormalidCampoRNExceptionCode.AEL_00889);
		}		
	}

	/**
	 * ORADB AELT_VNC_BRU (UPDATE) 
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	private void preAtualizar(AelValorNormalidCampo novo, AelValorNormalidCampo antigo) throws BaseException{
		//Restrições BD (Pré-Insert e Pré-Update)
		this.validaPreInsertPreUpdate(novo);
		
		/*AELT_VNC_BRU*/
		this.validaAlteracaoSituacao(novo, antigo);//RN1 e RN3
		this.validaCampoLaudo(novo);//RN2
		this.validaSituacaoInativa(novo, antigo);//RN4 e RN5	
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private Boolean validaAlteracao(AelValorNormalidCampo novo, AelValorNormalidCampo antigo) {
		
		Boolean alterado = false;
		
		if(CoreUtil.modificados(novo.getDthrInicial(),antigo.getDthrInicial())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getSexo(),antigo.getSexo())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getIdadeMinima(),antigo.getIdadeMinima())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getIdadeMaxima(),antigo.getIdadeMaxima())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getQtdeCasasDecimais(),antigo.getQtdeCasasDecimais())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getValorMinimo(),antigo.getValorMinimo())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getValorMaximo(),antigo.getValorMaximo())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getValorMinimoAceitavel(),antigo.getValorMinimoAceitavel())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getValorMaximoAceitavel(),antigo.getValorMaximoAceitavel())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getValorMinimoAbsurdo(),antigo.getValorMinimoAbsurdo())){
			alterado = true;
		}
		if(CoreUtil.modificados(novo.getValorMaximoAbsurdo(),antigo.getValorMaximoAbsurdo())){
			alterado = true;
		}
		
		return alterado;
		
	}

	/**
	 * ORADB aelk_vnc_rn.rn_vncp_ver_update
	 * @param novo
	 * @param antigo
	 * @throws BaseException
	 */
	private void validaSituacaoInativa(AelValorNormalidCampo novo, AelValorNormalidCampo antigo) throws BaseException {
		
		List<AelParametroCamposLaudo> listParametrosCamposLaudo = this.getAelParametroCamposLaudoDAO().pesquisarAelParametroCamposLaudoValorNormalidadeInativo(novo.getId().getCalSeq());
		
		/**
		 * RN4
		 */
		if(novo.getSituacao().equals(DominioSituacao.I) &&  novo.getSituacao().equals(antigo.getSituacao())){
			
			
			if(listParametrosCamposLaudo !=null && listParametrosCamposLaudo.size()>0){
				//Valor de normalidade inativo. Não permite alteração.
				throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00890);
			}
			
		}
		
		/**
		 * RN 5
		 */
		Boolean valorAlterado = validaAlteracao(novo, antigo);
		
		if(valorAlterado){
			if(listParametrosCamposLaudo !=null && listParametrosCamposLaudo.size()>0){
				
				//É permitido alterar apenas a situação para inativação dos valores de normalidade. 
				throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00882);
			}
		}
		
		
	}

	public void validaAlteracaoSituacao(AelValorNormalidCampo novo, AelValorNormalidCampo old) throws BaseException {
		
		if(!old.getSituacao().equals(novo.getSituacao()) && novo.getSituacao().equals(DominioSituacao.I)){
			novo.setDthrFinal(new Date());
		}
		
		if(!old.getSituacao().equals(novo.getSituacao()) && old.getSituacao().equals(DominioSituacao.I)){
			//O valor não pode ser ativado.  Se necessário, gerar um outro.
			throw new BaseException(AelValorNormalidCampoRNExceptionCode.AEL_00883);
		}
		
		
	}

	/**
	 * Atuliza AelvalorNormalidCampo
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	public void atualizar(AelValorNormalidCampo valorNormalidCampo) throws BaseException{
		
		final AelValorNormalidCampo old = this.getAelValorNormalidCampoDAO().obterOriginal(valorNormalidCampo);

		this.preAtualizar(valorNormalidCampo,old);		
		this.getAelValorNormalidCampoDAO().merge(valorNormalidCampo);
		this.posAtualizar(valorNormalidCampo);
		this.getAelValorNormalidCampoDAO().flush();
	}


	/**
	 * ORADB PROCEDURE AELP_ENFORCE_CAL_RULES (UPDATE)
	 * @param valorNormalidCampo
	 * @throws BaseException
	 */
	private void posAtualizar(AelValorNormalidCampo valorNormalidCampo) throws BaseException{

		if(valorNormalidCampo.getSituacao().equals(DominioSituacao.A)){
			this.validaSobreposicaoIdadeSexo(valorNormalidCampo); //RN1
		}
		
	}

	/**
	 * Getters para RNs e DAOs
	 */

	protected AelValorNormalidCampoDAO getAelValorNormalidCampoDAO() {
		return aelValorNormalidCampoDAO;
	}

	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}
	

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
