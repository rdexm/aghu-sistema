package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.exames.cadastrosapoio.business.AelCampoLaudoRN.AelCampoLaudoRNExceptionCode;
import br.gov.mec.aghu.exames.dao.AelEquipamentoJnDAO;
import br.gov.mec.aghu.exames.dao.AelEquipamentosDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelCampoLaudoLws;
import br.gov.mec.aghu.model.AelEquipComandoLabwide;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelEquipamentosJn;
import br.gov.mec.aghu.model.AelExecExamesMatAnalise;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelResuCodifLws;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * @author amalmeida
 * 
 */
@Stateless
public class AelEquipamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelEquipamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private AelEquipamentosDAO aelEquipamentosDAO;
	
	@Inject
	private AelEquipamentoJnDAO aelEquipamentoJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5960016652904441045L;

	public enum AelEquipamentoRNExceptionCode implements BusinessExceptionCode {

		AEL_01201,AEL_00556,AEL_00369;

	}

	/**
	 * ORADB TRIGGER AELT_EQU_BRI
	 * @param equipamentos
	 * @throws BaseException
	 */
	private void preInserir(AelEquipamentos equipamentos) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		/* AEL_EQU_CK3 */
		this.validaInterfaceamento(equipamentos);

		equipamentos.setCriadoEm(new Date());//RN1
		equipamentos.setServidor(servidorLogado);//RN2

		this.validaUnidadeFuncional(equipamentos);//RN3

		this.validaCaracteristicasUnidadeFunc(equipamentos);//RN4


	}

	/**
	 * aelk_equ_rn.rn_equp_ver_carac_uf
	 * @param equipamentos
	 * @throws ApplicationBusinessException
	 */
	private void validaCaracteristicasUnidadeFunc(AelEquipamentos equipamentos)
	throws ApplicationBusinessException {

		if (!getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(equipamentos.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES)) {

			throw new ApplicationBusinessException(AelEquipamentoRNExceptionCode.AEL_00556);

		}
	}


	/**
	 * ORADB aelk_equ_rn.rn_equp_ver_unid_fun
	 */
	private void validaUnidadeFuncional(AelEquipamentos equipamentos) throws BaseException {

		if(equipamentos.getUnidadeFuncional()!=null){
			this.getCadastrosApoioExamesFacade().verUnfAtiv(equipamentos.getUnidadeFuncional());
		}
	}

	private void validaInterfaceamento(AelEquipamentos equipamentos) throws ApplicationBusinessException {
		
		
		if(StringUtils.isBlank(equipamentos.getDriverId())){
			equipamentos.setDriverId(null);
		}
		
		/**
		 * AEL_EQU_CK3
		 */
		if((Boolean.TRUE.equals(equipamentos.getPossuiInterface()) &&  (equipamentos.getDriverId()==null || equipamentos.getDriverId().isEmpty())) 
				|| (Boolean.FALSE.equals(equipamentos.getPossuiInterface()) && (equipamentos.getDriverId()!=null))) {

			//Se equipamento possui interfaceamento o Driver_id é obrigatório, caso contrário não deve ser informado.
			throw new ApplicationBusinessException(AelEquipamentoRNExceptionCode.AEL_01201);

		}
	}

	/**
	 * Inserir AelEquipamentos
	 * @param equipamentos
	 * @throws BaseException
	 */
	public void inserir(AelEquipamentos equipamentos) throws BaseException{
		this.preInserir(equipamentos);		
		this.getAelEquipamentosDAO().persistir(equipamentos);
		//this.posInserir(equipamentos);
		this.getAelEquipamentosDAO().flush();
		
	}

	/**
	 * ORADB TRIGGER AELT_EQU_BRU (UPDATE) 
	 * @param equipamentos
	 * @throws BaseException
	 */
	private void preAtualizar(AelEquipamentos equipamentos, AelEquipamentos equipamentoOld) throws BaseException{

		/* AEL_EQU_CK3 */
		this.validaInterfaceamento(equipamentos);
		

		if(!equipamentos.getUnidadeFuncional().getSeq().equals(equipamentoOld.getUnidadeFuncional().getSeq())){

			this.validaUnidadeFuncional(equipamentos);

			this.validaCaracteristicasUnidadeFunc(equipamentos);

		}


		this.validaAlteracaoCampos(equipamentos, equipamentoOld);


	}

	private void validaAlteracaoCampos(AelEquipamentos equipamentos,
			AelEquipamentos equipamentoOld)
	throws ApplicationBusinessException {

		if(!equipamentos.getCriadoEm().equals(equipamentoOld.getCriadoEm())  || !equipamentos.getServidor().equals(equipamentoOld.getServidor()) ){

			//Tentativa de alterar campos que não podem ser alterados.
			throw new ApplicationBusinessException(AelEquipamentoRNExceptionCode.AEL_00369);

		}
		
	}

	/**
	 * Atuliza Aelequipamentos
	 * @param equipamentos
	 * @throws BaseException
	 */
	public void atualizar(AelEquipamentos equipamentos) throws BaseException{
		AelEquipamentos equipamentoOld = this.getAelEquipamentosDAO().obterOriginal(equipamentos);

		this.preAtualizar(equipamentos, equipamentoOld);		
		this.getAelEquipamentosDAO().merge(equipamentos);
		this.posAtualizar(equipamentos, equipamentoOld);
	}


	/**
	 * ORADB AELT_EQU_ARU (UPDATE)
	 * @param equipamentos
	 * @throws BaseException
	 */
	private void posAtualizar(AelEquipamentos equipamentos, AelEquipamentos equipamentoOld) throws BaseException{


		/**
		 * RN1
		 */
		if(this.verificaModificados(equipamentos, equipamentoOld)){
		
			AelEquipamentosJn equipamentoJn =  criarAelEquipamentosJn(equipamentos, DominioOperacoesJournal.UPD);
		
			getAelEquipamentoJnDAO().persistir(equipamentoJn);
			
			
		}

	}

	private AelEquipamentosJn criarAelEquipamentosJn(AelEquipamentos equipamentos, DominioOperacoesJournal dominio) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelEquipamentosJn aelEquipametosJn = BaseJournalFactory.getBaseJournal(dominio, AelEquipamentosJn.class, servidorLogado.getUsuario());
		
		aelEquipametosJn.setSeq(equipamentos.getSeq());
		aelEquipametosJn.setCargaAutomatica(equipamentos.getCargaAutomatica());
		aelEquipametosJn.setCriadoEm(equipamentos.getCriadoEm());
		aelEquipametosJn.setDescricao(equipamentos.getDescricao());
		aelEquipametosJn.setDriverId(equipamentos.getDriverId());
		aelEquipametosJn.setPossuiInterface(equipamentos.getPossuiInterface());
		aelEquipametosJn.setServidor(equipamentos.getServidor());
		aelEquipametosJn.setSituacao(equipamentos.getSituacao());
		aelEquipametosJn.setUnidadeFuncional(equipamentos.getUnidadeFuncional());
		
		return aelEquipametosJn;
	}
	
	private Boolean verificaModificados(AelEquipamentos equipamentos,
			AelEquipamentos equipamentoOld) {
		Boolean alterado = false;
		if(CoreUtil.modificados(equipamentos.getCargaAutomatica(), equipamentoOld.getCargaAutomatica())){

			alterado = true;

		}
		if(CoreUtil.modificados(equipamentos.getDescricao(), equipamentoOld.getDescricao())){

			alterado = true;

		}
		if(CoreUtil.modificados(equipamentos.getDriverId(), equipamentoOld.getDriverId())){

			alterado = true;

		}
		if(CoreUtil.modificados(equipamentos.getPossuiInterface(), equipamentoOld.getPossuiInterface())){

			alterado = true;

		}
		if(CoreUtil.modificados(equipamentos.getSituacao(), equipamentoOld.getSituacao())){

			alterado = true;

		}
		if(CoreUtil.modificados(equipamentos.getUnidadeFuncional().getSeq(), equipamentoOld.getUnidadeFuncional().getSeq())){

			alterado = true;

		}

		return alterado;
	}

	/**
	 * Verifica dependências antes da remoção e acrescenta cada exceção em uma lista
	 * @param campoLaudo
	 * @throws BaseException
	 */
	private void verificarDependenciasRemover(AelEquipamentos equipamento) throws BaseException {
		
		// Declara lista de exceções
		final BaseListException erros = new BaseListException();

		
		// Valida cada dependência
		erros.add(this.obterNegocioExceptionDependencias(equipamento.getSeq(), AelExecExamesMatAnalise.class, AelExecExamesMatAnalise.Fields.EQUIPAMENTOS_SEQ, this.getResourceBundleValue("LABEL_AEL_EXEC_EXAMES_MAT_ANALISE")));
		erros.add(this.obterNegocioExceptionDependencias(equipamento.getSeq(), AelAmostraItemExames.class, AelAmostraItemExames.Fields.AEL_EQUIPAMENTOS_SEQ, this.getResourceBundleValue("LABEL_AEL_AMOSTRA_ITEM_EXAMES")));
		erros.add(this.obterNegocioExceptionDependencias(equipamento.getSeq(), AelPacUnidFuncionais.class, AelPacUnidFuncionais.Fields.EQUIPAMENTO_SEQ, this.getResourceBundleValue("LABEL_AEL_PACIENTE_UNIDADES_FUNCIONAIS")));
		erros.add(this.obterNegocioExceptionDependencias(equipamento.getSeq(), AelCampoLaudoLws.class, AelCampoLaudoLws.Fields.EQU_SEQ,this.getResourceBundleValue("LABEL_AEL_CAMPOS_LAUDOS_LWS")));
		erros.add(this.obterNegocioExceptionDependencias(equipamento.getSeq(), AelResuCodifLws.class, AelResuCodifLws.Fields.EQU_SEQ, this.getResourceBundleValue("LABEL_AEL_RESU_CODIF_LWS")));
		erros.add(this.obterNegocioExceptionDependencias(equipamento.getSeq(), AelEquipComandoLabwide.class, AelEquipComandoLabwide.Fields.EQU_SEQ,this.getResourceBundleValue("LABEL_AEL_EQUIPE_COMANDO_LABWIDE")));
		
		// Lança exceções quando existem
		if (erros.hasException()) {
			throw erros;
		}
		
	}
	
	/**
	 * Verifica dependências antes da remoção e obtém a exceção necessária
	 * @param elemento
	 * @param classeDependente
	 * @param fieldChaveEstrangeira
	 * @param nomeDependencia
	 * @return
	 * @throws BaseException
	 */
	public final ApplicationBusinessException obterNegocioExceptionDependencias(Object elemento, Class classeDependente, Enum fieldChaveEstrangeira, String nomeDependencia) throws BaseException{

//		CoreUtil.validaParametrosObrigatorios(elemento,classeDependente,fieldChaveEstrangeira, nomeDependencia);

		if (this.getAelEquipamentosDAO().existeDependencia(elemento, classeDependente, fieldChaveEstrangeira)){
			return new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, nomeDependencia);
		}
		return null;
	}
	
	/**
	 * CHK_AEL_EQUIPAMENTOS
	 * @param equipamentos
	 * @throws BaseException
	 */
	private void preRemover(AelEquipamentos equipamento) throws BaseException{

		this.verificarDependenciasRemover(equipamento);//RN1: CHK_AEL_EQUIPAMENTOS

	}

	/**
	 * Remove AelEquipamentos
	 * @param equipamentos
	 * @throws BaseException
	 */
	public void remover(AelEquipamentos equipamentos) throws BaseException{
		this.preRemover(equipamentos);		
		this.getAelEquipamentosDAO().removerPorId(equipamentos.getSeq());
		this.posRemover(equipamentos);
		this.getAelEquipamentosDAO().flush();
	}
	
	
	/**
	 * ORADB AELT_EQU_ARD (DELETE)
	 * @param equipamentos
	 * @throws BaseException
	 */
	private void posRemover(AelEquipamentos equipamentos) throws BaseException{
		
		AelEquipamentosJn equipamentoJn =  criarAelEquipamentosJn(equipamentos, DominioOperacoesJournal.DEL);
		getAelEquipamentoJnDAO().persistir(equipamentoJn);
	//	getAelEquipamentoJnDAO().flush();
		
		
		
	}


	

		/**
	 * Getters para RNs e DAOs
	 */

	protected AelEquipamentosDAO getAelEquipamentosDAO() {
		return aelEquipamentosDAO;
	}


	public ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
	protected AelEquipamentoJnDAO getAelEquipamentoJnDAO() {
		return aelEquipamentoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
