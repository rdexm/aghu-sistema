package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelCampoCodifRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoRelacionadoDAO;
import br.gov.mec.aghu.exames.dao.AelCampoVinculadoDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.model.AelCampoCodifRelacionado;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelCampoLaudoRelacionado;
import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelParametroCamposLaudoRN extends BaseBusiness {


@EJB
private AelCampoCodifRelacionadoRN aelCampoCodifRelacionadoRN;

@EJB
private AelCampoLaudoRelacionadoRN aelCampoLaudoRelacionadoRN;

@EJB
private AelCampoVinculadoRN aelCampoVinculadoRN;

private static final Log LOG = LogFactory.getLog(AelParametroCamposLaudoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;

@Inject
private AelCampoLaudoRelacionadoDAO aelCampoLaudoRelacionadoDAO;

@Inject
private AelCampoVinculadoDAO aelCampoVinculadoDAO;

@Inject
private AelCampoLaudoDAO aelCampoLaudoDAO;

@Inject
private AelCampoCodifRelacionadoDAO aelCampoCodifRelacionadoDAO;

@Inject
private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6861588587927494361L;


	public enum AelParametroCamposLaudoRNExceptionCode implements BusinessExceptionCode {
		AEL_01061,//Versão do laudo está  'INATIVA'. 
		AEL_00779,//indicador de sumário deve ser 'N' pois o exame não pertence ao sumário. 
		AEL_01037,//indicador de sumário deve ser 'S' pois o exame pertence ao sumário de exames completo(com máscara).
		AEL_00782,//Campo  laudo está  inativo.
		AEL_00781,//Informe casas decimais somente para campos laudo do tipo numérico ou expressão.
		AEL_01049,//Só pode ter um único campo laudo com indicador de cancela dependente automático para uma mesma máscara.
		AEL_00938,//Parâmetro campo laudo,  cuja versão está em construção, não pode ser excluído.
		AEL_00776,////Versão laudo não encontrada.
		MSG_CAL_NE;//

	}



	/**
	 * Remover AelVersaoLaudo
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	public void remover(AelParametroCamposLaudo campoLaudo) throws BaseException {
		final AelParametroCamposLaudo campoLaudoExcluir = this.getAelParametroCamposLaudoDAO().obterPorChavePrimaria(campoLaudo.getId());
		this.preRemover(campoLaudoExcluir);
		this.getAelParametroCamposLaudoDAO().remover(campoLaudoExcluir);
		this.getAelParametroCamposLaudoDAO().flush();
	}

	private void preRemover(AelParametroCamposLaudo parametroCamposLaudo) throws BaseException {
		this.verificarDelecao(parametroCamposLaudo);
	}

	/**
	 * ORADB TRIGGER AELT_PCL_BRD (DELETE)
	 * 
	 * @param parametroCamposLaudo
	 * @throws BaseException
	 */
	protected void verificarDelecao(AelParametroCamposLaudo parametroCamposLaudo) throws BaseException {

		// Se a situação for diferente de 'EM CONSTRUÇÃO'
		if (!DominioSituacaoVersaoLaudo.E.equals(parametroCamposLaudo.getAelVersaoLaudo().getSituacao())) {
			throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_00938);
		}
	}



	/**
	 * Inserir AelParametroCamposLaudo ( TFormVersaoLaudo.Salvamento )
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	public void inserir(AelParametroCamposLaudo parametroCamposLaudo) throws BaseException {
		this.preInserir(parametroCamposLaudo);
		this.getAelParametroCamposLaudoDAO().persistir(parametroCamposLaudo);
		this.getAelParametroCamposLaudoDAO().flush();
		this.posInserir(parametroCamposLaudo);
	}
	
	/**
	 * Atualiza no banco os campos laudos vinculados, excluindo todos e inserido novamente.
	 * @param parametroCamposLaudo
	 * @throws BaseException
	 */
	public void atualizarCamposVinculadosCodificados(AelParametroCamposLaudo parametroCamposLaudo) throws BaseException {

		// Atualiza campos vinculados
		if(parametroCamposLaudo.getCampoVinculados() != null){

			/*Exclui todos os campos laudos vinculados ao paramentro campo laudo*/
			List<AelCampoVinculado> camposVinculados = getAelCampoVinculadoDAO().pesquisarCampoVinculadoPorParametroCampoLaudo(parametroCamposLaudo);
			if(!camposVinculados.isEmpty()){

				for (AelCampoVinculado aelCampoVinculado : camposVinculados) {
					getAelCampoVinculadoRN().excluir(aelCampoVinculado);
				}
			}

			/*Insert dos campos vinculados*/
			for (AelCampoVinculado campoVinculado : parametroCamposLaudo.getCampoVinculados()) {
				getAelCampoVinculadoRN().inserir(campoVinculado);
				getAelCampoVinculadoDAO().flush();
			}
		}

		// Atualiza campos relacionados
		if(parametroCamposLaudo.getCamposLaudoRelacionados() != null){
			/*Exclui todos os campos laudos relacionados ao paramentro campo laudo*/
			List<AelCampoLaudoRelacionado> camposRelacionados = getAelCampoLaudoRelacionadoDAO().pesquisarCampoLaudoPorParametroCampoLaudo(parametroCamposLaudo);
			if(!camposRelacionados.isEmpty()){
				for (AelCampoLaudoRelacionado aelCampoRelacionado : camposRelacionados) {
					getAelCampoLaudoRelacionadoRN().excluir(aelCampoRelacionado);
				}
			}

			/*Insert dos campos vinculados*/
			for (AelCampoLaudoRelacionado campoLaudoRelacionado : parametroCamposLaudo.getCamposLaudoRelacionados()) {
				getAelCampoLaudoRelacionadoRN().inserir(campoLaudoRelacionado);
				getAelCampoLaudoRelacionadoDAO().flush();
			}
		}
		
		// Atualiza campos relacionados codificados
		if(parametroCamposLaudo.getCampoCodifRelacionado() != null){
			/*Exclui todos os campos laudos relacionados ao paramentro campo laudo*/
			List<AelCampoCodifRelacionado> camposRelacionados = getAelCampoCodifRelacionadoDAO().pesquisarCampoCodificadoPorParametroCampoLaudo(parametroCamposLaudo);
			if(!camposRelacionados.isEmpty()){
				for (AelCampoCodifRelacionado aelCampoCodifRelacionado : camposRelacionados) {
					getAelCampoCodifRelacionadoRN().excluir(aelCampoCodifRelacionado);
					getAelCampoCodifRelacionadoDAO().flush();
				}
			}

			/*Insert dos campos vinculados*/
			for (AelCampoCodifRelacionado campoLaudoCodifRelacionado : parametroCamposLaudo.getCampoCodifRelacionado()) {
				getAelCampoCodifRelacionadoRN().inserir(campoLaudoCodifRelacionado);
				getAelCampoCodifRelacionadoDAO().flush();
			}
		}
	}


	/**
	 * Atualizar AelParametroCamposLaudo
	 * 
	 * @param versaoLaudo
	 * @throws BaseException
	 */
	public void atualizar(AelParametroCamposLaudo parametroCamposLaudo) throws BaseException {
		this.preAtualizar(parametroCamposLaudo);
		this.getAelParametroCamposLaudoDAO().merge(parametroCamposLaudo);
		
		/*Atualiza os campos laudos vinculados e codificados*/
		atualizarCamposVinculadosCodificados(parametroCamposLaudo);
	}
	
	/**
	 * ORADB AELT_PCL_BRU (UPDATE) 
	 * 
	 * @param parametroCamposLaudo
	 * @throws BaseException
	 */
	protected void preAtualizar(AelParametroCamposLaudo parametroCamposLaudo) throws BaseException {

		AelParametroCamposLaudo parametroCamposLaudoOLD = this.getAelParametroCamposLaudoDAO().obterOriginal(parametroCamposLaudo);
		
		/* RN1*/
		if(parametroCamposLaudoOLD != null && parametroCamposLaudoOLD.getId() != null && !parametroCamposLaudo.getId().getVelEmaExaSigla().equals(parametroCamposLaudoOLD.getId().getVelEmaExaSigla())
		   || !parametroCamposLaudo.getId().getVelEmaManSeq().equals(parametroCamposLaudoOLD.getId().getVelEmaManSeq())
		   ||  !parametroCamposLaudo.getId().getVelSeqp().equals(parametroCamposLaudoOLD.getId().getVelSeqp())){
			
			/**
			 * aelk_pcl_rn.rn_pclp_ver_versao
			 */
			this.validaVersaLaudo(parametroCamposLaudo); //RN1
		}
		
		
		/* RN2*/
		if(parametroCamposLaudoOLD != null && parametroCamposLaudoOLD.getId() != null && !parametroCamposLaudo.getId().getVelEmaExaSigla().equals(parametroCamposLaudoOLD.getId().getVelEmaExaSigla())
		   || !parametroCamposLaudo.getId().getVelEmaManSeq().equals(parametroCamposLaudoOLD.getId().getVelEmaManSeq())
		   ||  (parametroCamposLaudo.getSumarioSemMascara() != null && parametroCamposLaudoOLD.getSumarioSemMascara() != null && !parametroCamposLaudo.getSumarioSemMascara().equals(parametroCamposLaudoOLD.getSumarioSemMascara()))){
			
			/**
			 * aelk_pcl_rn.rn_pclp_ver_sumario
			 */
			this.validaSumario(parametroCamposLaudo); //RN2
		}
		
		/* RN3*/
		if(parametroCamposLaudoOLD != null && parametroCamposLaudoOLD.getId() != null && !parametroCamposLaudo.getId().getCalSeq().equals(parametroCamposLaudoOLD.getId().getCalSeq())
		   || (parametroCamposLaudo.getQuantidadeCasasDecimais() != null && parametroCamposLaudoOLD.getQuantidadeCasasDecimais() != null && !parametroCamposLaudo.getQuantidadeCasasDecimais().equals(parametroCamposLaudoOLD.getQuantidadeCasasDecimais()))
		   ||  (parametroCamposLaudo.getTextoLivre() != null && parametroCamposLaudoOLD.getTextoLivre() != null && !parametroCamposLaudo.getTextoLivre().equals(parametroCamposLaudoOLD.getTextoLivre()))){
			
			/**
			 * aelk_pcl_rn.rn_pclp_ver_tp_campo
			 */
			this.validaCampoLaudo(parametroCamposLaudo); //RN3
		}
				
		
	}

	/**
	 * ORADB AELT_PCL_BRI (INSERT)
	 * 
	 * @param parametroCamposLaudo
	 * @throws BaseException
	 */
	protected void preInserir(AelParametroCamposLaudo parametroCamposLaudo) throws BaseException {
		this.validaVersaLaudo(parametroCamposLaudo); //RN1
		this.validaSumario(parametroCamposLaudo); //RN2
		this.validaCampoLaudo(parametroCamposLaudo); //RN3
	}

	/**
	 *  ORADB AELP_ENFORCE_PCL_RULES (INSERT)
	 * @param parametroCamposLaudo
	 * @throws BaseException
	 */
	protected void posInserir(AelParametroCamposLaudo parametroCamposLaudo) throws BaseException {

		/** 
		 * ORADB aelk_pcl_rn.rn_pclp_ver_item_dep
		 */
		
		if(parametroCamposLaudo.getCampoLaudo()!=null && parametroCamposLaudo.getCampoLaudo().getCancelaItemDept()){

			AelCampoLaudo campoLaudo = this.getAelCampoLaudoDAO().pesquisarCampoLaudoPorParametrocampoLaudo(parametroCamposLaudo);

			if(campoLaudo.getCancelaItemDept()){
				/*Só pode ter um único campo laudo com indicador de cancela dependente automático para uma mesma máscara.*/
				throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_01049);
			}
		}
		
		/*Atualiza os campos laudos vinculados e codificados*/
		atualizarCamposVinculadosCodificados(parametroCamposLaudo);
		
	}


	/**
	 * ORADB aelk_pcl_rn.rn_pclp_ver_tp_campo
	 * @param parametroCamposLaudo
	 * @throws ApplicationBusinessException
	 */
	public void validaCampoLaudo(AelParametroCamposLaudo parametroCamposLaudo) throws ApplicationBusinessException {
		if(parametroCamposLaudo.getCampoLaudo() == null){
			String msgAux = null;
			if(parametroCamposLaudo.getTextoLivre()==null){
				msgAux = parametroCamposLaudo.getObjetoVisual().getDescricao();
			}else{
				msgAux = parametroCamposLaudo.getTextoLivre(); 
			}
			throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.MSG_CAL_NE, msgAux);
		}
		
		AelCampoLaudo campoLaudo = parametroCamposLaudo.getCampoLaudo();

		if(campoLaudo.getSituacao().equals(DominioSituacao.I)){
			/*Campo laudo está inativo.*/
			throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_00782);
		}


		if(parametroCamposLaudo.getQuantidadeCasasDecimais() != null && !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.N)
				&& !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.E)){
			
			if(parametroCamposLaudo.getQuantidadeCasasDecimais().intValue() ==  0){
				parametroCamposLaudo.setQuantidadeCasasDecimais(null);
			}else{
				/*Informe casas decimais somente para campos do tipo numérico ou expressão.*/
				throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_00781);
			}
		}

		/*
		if(parametroCamposLaudo.getTextoLivre() != null && !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.T) && !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.E)
				&& !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.Q) && !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.M)
				&& !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.R) && !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.V)
				&& !campoLaudo.getTipoCampo().equals(DominioTipoCampoCampoLaudo.H)){

			//Informe texto somente para campos laudo do tipo texto fixo, expressão, equipamento, método, recebimento e valores de referência.
			throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_00783);

		}
		*/
	}

	/**
	 * ORADB aelk_pcl_rn.rn_pclp_ver_sumario
	 * @param parametroCamposLaudo
	 * @param versaoLaudo
	 * @throws ApplicationBusinessException
	 */
	private void validaSumario(AelParametroCamposLaudo parametroCamposLaudo) throws ApplicationBusinessException {
		AelExamesMaterialAnalise exameMaterialAnalise =	this.getAelExamesMaterialAnaliseDAO().buscarAelExamesMaterialAnalisePorId(parametroCamposLaudo.getId().getVelEmaExaSigla(), parametroCamposLaudo.getId().getVelEmaManSeq());

		if(parametroCamposLaudo.getSumarioSemMascara()){

			//1)Se o valor da coluna sumario_sem_mascara for igual a ‘S’ e o retorno da consulta for diferente de ‘S’, ‘B’, ‘E’, ‘G’, ‘H’ então exibe o erro AEL-00779: “Indicador de sumário deve ser 'N' pois o exame em questão não pertence ao relatório de sumário de exames.” 

			if(exameMaterialAnalise.getPertenceSumario().equals(DominioSumarioExame.N)){
				throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_00779);
			}

		}else{

			//2)Se o valor da coluna sumario_sem_mascara for igual a ‘N’ e o retorno da consulta for diferente de ‘N’, ‘B’, ‘E’, ‘G’, ‘H’ então exibe o erro AEL-01037: “Indicador de sumário deve ser 'S' pois o exame em questão pertence ao relatório de sumário de exames completo(utilizando máscara).”

			if(exameMaterialAnalise.getPertenceSumario().equals(DominioSumarioExame.S)){
				throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_01037);
			}

		}
	}

	/**
	 * ORADB aelk_pcl_rn.rn_pclp_ver_versao	
	 * @param parametroCamposLaudo
	 * @throws ApplicationBusinessException
	 */
	private void validaVersaLaudo(AelParametroCamposLaudo parametroCamposLaudo)	throws ApplicationBusinessException {

		//Não precisa o trecho de código abaixo, pois já tenho a versão do laudo setada emparametroCamposLaudo 
		//List<AelParametroCamposLaudo> listParamCampoLaudo = this.getAelParametroCamposLaudoDAO().pesquisarParametroCamposLaudoComVersaoLaudoAtiva(parametroCamposLaudo.getId().getVelEmaExaSigla(), parametroCamposLaudo.getId().getVelEmaManSeq(), parametroCamposLaudo.getId().getCalSeq());

		if(parametroCamposLaudo.getAelVersaoLaudo() == null){
			throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_00776);
		}

		if(parametroCamposLaudo.getAelVersaoLaudo() != null  
				&& DominioSituacaoVersaoLaudo.I == parametroCamposLaudo.getAelVersaoLaudo().getSituacao()){
			throw new ApplicationBusinessException(AelParametroCamposLaudoRNExceptionCode.AEL_01061);
		}
	}


	/**
	 * Getters para RNs e DAOs
	 */

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}

	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}

	public AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}
	
	public AelCampoVinculadoRN getAelCampoVinculadoRN() {
		return aelCampoVinculadoRN;
	}
	
	public AelCampoLaudoRelacionadoRN getAelCampoLaudoRelacionadoRN() {
		return aelCampoLaudoRelacionadoRN;
	}
	
	public AelCampoLaudoRelacionadoDAO getAelCampoLaudoRelacionadoDAO() {
		return aelCampoLaudoRelacionadoDAO;
	}
	
	public AelCampoCodifRelacionadoRN getAelCampoCodifRelacionadoRN() {
		return aelCampoCodifRelacionadoRN;
	}
	
	public AelCampoCodifRelacionadoDAO getAelCampoCodifRelacionadoDAO() {
		return aelCampoCodifRelacionadoDAO;
	}
	
	public AelCampoVinculadoDAO getAelCampoVinculadoDAO(){
		return aelCampoVinculadoDAO;
	}
}