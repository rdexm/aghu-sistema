package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.dao.AelLoteExameDAO;
import br.gov.mec.aghu.exames.dao.AelLoteExameUsualDAO;
import br.gov.mec.aghu.exames.pesquisa.business.AelExamesExceptionCode;
import br.gov.mec.aghu.model.AelLoteExame;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelLoteExameUsualRN extends BaseBusiness{
	
	private static final Log LOG = LogFactory.getLog(AelLoteExameUsualRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelLoteExameUsualDAO aelLoteExameUsualDAO;
	
	@Inject
	private AelLoteExameDAO aelLoteExameDAO;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4008199999416470718L;

	public enum AelLoteExameUsualRNExceptionCode implements BusinessExceptionCode {
		LEU_RN2, LEU_RN3, LEU_RN4, LEU_RN5, LEU_RN6, LEU_RN7, LEU_RN8, AEL_00235_CAP, AEL_LEU_CK3, AEL_00434; 
	}

	/***
	 * Inserir lote de exame
	 */
	public void inserirAelLoteExameUsual(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException {
		// TRIGGER AELT_LOE_BRI	
		this.beforeInsertUpdateAelLoteExameUsual(loteExameUsual);
		// Insere o lote de exame
		this.getAelLoteExameUsualDAO().persistir(loteExameUsual);
	}
	
	/***
	 * Inserir lote de exame
	 * @param loteExame
	 * @throws BaseException 
	 */
	public void removerAelLoteExameUsual(Short loteSeq) throws BaseException {

		AelLoteExameUsual loteExameUsual = getAelLoteExameUsualDAO().obterPorChavePrimaria(loteSeq);
		
		if (loteExameUsual == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}

		// TRIGGER AELT_LOE_BRI	
		this.beforeRemoverAelLoteExameUsual(loteExameUsual);

		List<AelLoteExame> lstItens = getAelLoteExamesDAO().pesquisaLotesExamesPorLoteExameUsual(loteExameUsual.getSeq());
		AelLoteExameDAO loteExameDao = getAelLoteExamesDAO();

		for (AelLoteExame aelLoteExame : lstItens) {
			loteExameDao.remover(aelLoteExame);
			loteExameDao.flush();
		}
		// Insere o lote de exame
		this.getAelLoteExameUsualDAO().remover(loteExameUsual);
	}
	
	/***
	 * Atualizar lote de exame
	 * @param loteExame
	 */
	public void atualizarAelLoteExameUsual(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException {
		// TRIGGER AELT_LOE_BRI	
		this.beforeInsertUpdateAelLoteExameUsual(loteExameUsual);
		// Insere o lote de exame
		this.getAelLoteExameUsualDAO().merge(loteExameUsual);
	}
	
	/**
	 * ORADB TRIGGER AELT_LOE_BRI
	 * @param loteExame
	 */
	public void beforeInsertUpdateAelLoteExameUsual(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//RN1: aelk_ael_rn.rn_aelp_atu_servidor
		if(loteExameUsual.getSeq()==null){
			loteExameUsual.setServidor(servidorLogado);
			loteExameUsual.setCriadoEm(new Date());
		}

		aelLeuCk1(loteExameUsual);
		aelLeuCk3(loteExameUsual);
		rnLeupVerEspecial(loteExameUsual);
		rnLeupVerExmeUsu(loteExameUsual);
		rnLeupVerUnidFun(loteExameUsual);

		rnLeupVerDefault(loteExameUsual);
		rnLeupVerEspOrig(loteExameUsual);
		rnLeupVerUnidOrig(loteExameUsual);
		rnLeupVerGruOrig(loteExameUsual);
		
	}
	
	protected void beforeRemoverAelLoteExameUsual(AelLoteExameUsual loteExameUsual) throws BaseException {
		getCadastrosApoioExamesFacade().verificaDataCriacao(loteExameUsual.getCriadoEm(), AghuParametrosEnum.P_DIAS_PERM_DEL_AEL, AelExamesExceptionCode.AEL_00343, AelExamesExceptionCode.AEL_00344);
	}

	/**
	 * ORADB RN2: aelk_leu_rn.rn_leup_ver_especial
	 * especialidade
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	
	protected void rnLeupVerEspecial(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		
		if(loteExameUsual.getEspSeq()!= null && !loteExameUsual.getEspSeq().isAtivo()){
			throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.AEL_00434);
		}
		
		if(loteExameUsual.getEspSeq()!=null && loteExameUsual.getEspSeq().getEspecialidadeAgrupaLoteExame()!=null){
			if(!(loteExameUsual.getEspSeq().getEspecialidadeAgrupaLoteExame().getSeq().equals(loteExameUsual.getEspSeq().getSeq()))){
				throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.LEU_RN2, loteExameUsual.getEspSeq().getNomeReduzido());
			}
		}
	}
	
	
	

	/**
	 * ORADB aelk_leu_rn.rn_leup_ver_exme_usu
	 * Verifica se grupo esta ativo ou não
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	protected void rnLeupVerExmeUsu(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		if(loteExameUsual.getGruSeq()!=null && !loteExameUsual.getGruSeq().getIndSituacao().equals(DominioSituacao.A)){
			throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.LEU_RN3);
		}
	}
	
	/**
	 * ORADB aelk_leu_rn.rn_leup_ver_unid_fun
	 * Verifica se unidade esta ativa ou não
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	protected void rnLeupVerUnidFun(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		if(loteExameUsual.getUnfSeq()!=null && !loteExameUsual.getUnfSeq().isAtivo()){
			throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.LEU_RN4);
		}
	}
	
	/**
	 * ORADB constraint ael_leu_ck1
	 * Verifica se somente uma das opções foi selecionada.
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	protected void aelLeuCk1(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		if(
				!((loteExameUsual.getUnfSeq()!=null
				&& loteExameUsual.getGruSeq()==null
				&& loteExameUsual.getEspSeq()==null)
				
				|| (loteExameUsual.getGruSeq()!=null
					&& loteExameUsual.getUnfSeq()==null
					&& loteExameUsual.getEspSeq()==null)
					
				|| (loteExameUsual.getEspSeq()!=null
					&& loteExameUsual.getGruSeq()==null
					&& loteExameUsual.getUnfSeq()==null))
				
		){
			throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.AEL_00235_CAP);
		}
	}
	
	/**
	 * ORADB constraint ael_leu_ck3
	 * Verifica se somente uma das opções válidas foi selecionada.
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	protected void aelLeuCk3(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		if(loteExameUsual!=null && loteExameUsual.getOrigem()!=null){
			DominioOrigemAtendimento dominio = loteExameUsual.getOrigem();
			
			if(!dominio.equals(DominioOrigemAtendimento.A)
				&& !dominio.equals(DominioOrigemAtendimento.I)
				&& !dominio.equals(DominioOrigemAtendimento.U)
				&& !dominio.equals(DominioOrigemAtendimento.X)
				&& !dominio.equals(DominioOrigemAtendimento.D)
				&& !dominio.equals(DominioOrigemAtendimento.H)){
				throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.AEL_LEU_CK3);
			}
		}
	}
	
	
	
	
	/**
	 * ORADB aelk_leu_rn.rn_leup_ver_default
	 * Verifica se outro lote é default
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	protected void rnLeupVerDefault(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		if(loteExameUsual.getIndLoteDefault().equals(DominioSimNao.S)){
			if(getAelLoteExameUsualDAO().obterLotesDefaultComIdDiferente(loteExameUsual).size()>0){
				throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.LEU_RN5);
			}
		}
	}

	/**
	 * ORADB aelk_leu_rn.rn_leup_ver_esp_orig
	 * Verifica se existe outra especialidade/origem
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	protected void rnLeupVerEspOrig(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		if(getAelLoteExameUsualDAO().obterLotesDefaultComIdDiferentePorEspOrigem(loteExameUsual).size()>0){
			throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.LEU_RN6);
		}
	}

	/**
	 * ORADB aelk_leu_rn.rn_leup_ver_unid_ori
	 * Verifica se existe outra unidade/origem
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	protected void rnLeupVerUnidOrig(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		if(getAelLoteExameUsualDAO().obterLotesDefaultComIdDiferentePorUnidadeOrigem(loteExameUsual).size()>0){
			throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.LEU_RN7);
		}
	}

	/**
	 * ORADB aelk_leu_rn.rn_leup_ver_gru_orig
	 * Verifica se existe outra grupo/origem
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 */
	protected void rnLeupVerGruOrig(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException{
		if(getAelLoteExameUsualDAO().obterLotesDefaultComIdDiferentePorGrupoOrigem(loteExameUsual).size()>0){
			throw new ApplicationBusinessException(AelLoteExameUsualRNExceptionCode.LEU_RN8);
		}
	}

	/**
	 * Dependencias
	 */

	protected AelLoteExameUsualDAO getAelLoteExameUsualDAO(){
		return aelLoteExameUsualDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade(){
		return cadastrosApoioExamesFacade;
	}

	protected AelLoteExameDAO getAelLoteExamesDAO() {
		return aelLoteExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
