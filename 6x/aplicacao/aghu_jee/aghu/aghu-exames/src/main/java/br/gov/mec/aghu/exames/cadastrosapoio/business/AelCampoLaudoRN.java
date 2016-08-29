package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.model.AbsExameComponenteVisualPrescricao;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelCampoLaudoLws;
import br.gov.mec.aghu.model.AelCampoUsoFaturamento;
import br.gov.mec.aghu.model.AelExameEquipamento;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampo;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoPadraoCampo;
import br.gov.mec.aghu.model.AelServidorCampoLaudo;
import br.gov.mec.aghu.model.AelSinonimoCampoLaudo;
import br.gov.mec.aghu.model.AelValorNormalidCampo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class AelCampoLaudoRN extends BaseBusiness{

	@EJB
	private AnticoagulanteRN anticoagulanteRN;
	
	@EJB
	private AelSinonimoCampoLaudoRN aelSinonimoCampoLaudoRN;
	
	private static final Log LOG = LogFactory.getLog(AelCampoLaudoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 124280383325527243L;
	
	public enum AelCampoLaudoRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_CAMPO_LAUDO,ERRO_REMOVER_CAMPO_LAUDO,AEL_00667,AEL_00770,AEL_00885,AEL_01047,AEL_01252,AEL_00769,AEL_00692,AEL_01063,AEL_00886,AEL_00369,AEL_01154,MENSAGEM_ERRO_REMOVER_DEPENDENCIAS,CAMPO_LAUDO_DUPLICADO;
		
	}
	
	/**
	 * Persiste AelCampoLaudo
	 * @param campoLaudo
	 * @throws BaseException
	 */
	public AelCampoLaudo persistirCampoLaudo(AelCampoLaudo campoLaudo) throws BaseException{
		
		if (campoLaudo.getSeq() == null) {
			return inserir(campoLaudo);
		} else {
			return atualizar(campoLaudo);
		}
	}
	
	/**
	 * ORADB CONSTRAINTS (INSERT/UPDATE)
	 * @param campoLaudo
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void executarRestricoes(AelCampoLaudo campoLaudo) throws BaseException{
		
		final AelGrupoResultadoCaracteristica grupoResultadoCaracteristica = campoLaudo.getGrupoResultadoCaracteristica();	
		final AelGrupoResultadoCodificado grupoResultadoCodificado = campoLaudo.getGrupoResultadoCodificado();	

	    // AEL_CAL_CK4
		if(!((grupoResultadoCaracteristica != null  && grupoResultadoCodificado == null) 
				|| (grupoResultadoCaracteristica == null  && grupoResultadoCodificado != null) 
				|| (grupoResultadoCaracteristica == null  && grupoResultadoCodificado == null))){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00667);
		}
		
		final DominioTipoCampoCampoLaudo tipoCampo = campoLaudo.getTipoCampo();
		
		// AEL_CAL_CK5
		if(!((!DominioTipoCampoCampoLaudo.C.equals(tipoCampo) && grupoResultadoCaracteristica == null && grupoResultadoCodificado == null) 
				 || (DominioTipoCampoCampoLaudo.C.equals(tipoCampo) && grupoResultadoCaracteristica != null && grupoResultadoCodificado == null)
				 || (DominioTipoCampoCampoLaudo.C.equals(tipoCampo) && grupoResultadoCaracteristica == null && grupoResultadoCodificado != null))){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00770);
		}
		
		final Boolean fluxo = campoLaudo.getFluxo();
		final Short ordem = campoLaudo.getOrdem();
	     
		// AEL_CAL_CK6
		if(!(Boolean.FALSE.equals(fluxo) || (Boolean.TRUE.equals(fluxo) &&  ordem != null && ordem > 0))){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00885);
		}
	
		final Boolean cancelaItemDept = campoLaudo.getCancelaItemDept();
		
		// AEL_CAL_CK8
		if(!((Boolean.TRUE.equals(cancelaItemDept) && DominioTipoCampoCampoLaudo.C.equals(tipoCampo)) 
				|| (Boolean.FALSE.equals(cancelaItemDept) && tipoCampo != null))){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_01047);
		}
	
		final Boolean permiteDigitacao = campoLaudo.getPermiteDigitacao();
		
		// AEL_CAL_CK12
		List<DominioTipoCampoCampoLaudo> camposNaoPermiteDigitacao = Arrays.asList(
				DominioTipoCampoCampoLaudo.T,
				DominioTipoCampoCampoLaudo.M,
				DominioTipoCampoCampoLaudo.Q,
				DominioTipoCampoCampoLaudo.R,
				DominioTipoCampoCampoLaudo.V,
				DominioTipoCampoCampoLaudo.E,
				DominioTipoCampoCampoLaudo.H);
		
		List<DominioTipoCampoCampoLaudo> camposPermiteDigitacao = Arrays.asList(
				DominioTipoCampoCampoLaudo.N,
				DominioTipoCampoCampoLaudo.A,
				DominioTipoCampoCampoLaudo.E,
				DominioTipoCampoCampoLaudo.C);
		
		if(!((camposNaoPermiteDigitacao.contains(tipoCampo) && Boolean.FALSE.equals(permiteDigitacao)) 
				|| (camposPermiteDigitacao.contains(tipoCampo) && Boolean.TRUE.equals(permiteDigitacao)))){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_01252);
		}
		

		
	}
	
	/**
	 * ORADB TRIGGER AELT_CAL_BRI (INSERT)
	 * @param campoLaudo
	 * @throws BaseException
	 */
	private void preInserir(AelCampoLaudo campoLaudo) throws BaseException{
		
		//AEL_CAL_UK1
		if (getAelCampoLaudoDAO().existeCampoLaudoNome(campoLaudo.getNome())) {
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.CAMPO_LAUDO_DUPLICADO);
		}
		
		this.atualizarServidorLogadoDataCriacao(campoLaudo); // RN1 e RN2
		this.verificarGrupoResultadoCaracteristicaAtivo(campoLaudo); // RN3
		this.verificarGrupoResultadoCodificadoAtivo(campoLaudo); // RN4
		this.verificarCampoLaudoTextoFixoUnico(campoLaudo); // RN5
	}
	
	/**
	 * Inserir AelCampoLaudo
	 */
	public AelCampoLaudo inserir(AelCampoLaudo campoLaudo) throws BaseException{
		
		this.executarRestricoes(campoLaudo);
		
		this.preInserir(campoLaudo);		
		this.getAelCampoLaudoDAO().persistir(campoLaudo);
		this.posInserir(campoLaudo);
		
		return campoLaudo;
	}
	
	
	/**
	 * ORADB PROCEDURE AELP_ENFORCE_CAL_RULES (INSERT)
	 * @param campoLaudo
	 * @throws BaseException
	 */
	private void posInserir(AelCampoLaudo campoLaudo) throws BaseException{
		this.atualizarSinonimoCampoLaudo(campoLaudo); // RN1
		this.verificarCampoLaudoOrdem(campoLaudo); // RN2
	}
	
	
	/**
	 * ORADB TRIGGER AELT_CAL_BRU (UPDATE)
	 * @param campoLaudo
	 * @throws BaseException
	 */
	private void preAtualizar(AelCampoLaudo campoLaudo, AelCampoLaudo old) throws BaseException{

		// Verifica campos que não podem ser alterados: criado em, servidor, nome, tipo campo e grupo resultado característica
		if((old.getCriadoEm() != null && campoLaudo.getCriadoEm() != null && old.getCriadoEm().getTime() != campoLaudo.getCriadoEm().getTime())
				|| !old.getServidor().equals(campoLaudo.getServidor()) 
				|| !old.getNome().equals(campoLaudo.getNome()) 
				|| !old.getTipoCampo().equals(campoLaudo.getTipoCampo())
				|| (old.getGrupoResultadoCaracteristica() != null && !old.getGrupoResultadoCaracteristica().equals(campoLaudo.getGrupoResultadoCaracteristica())) 
				|| (campoLaudo.getGrupoResultadoCaracteristica() != null && !campoLaudo.getGrupoResultadoCaracteristica().equals(old.getGrupoResultadoCaracteristica()))
				|| (old.getGrupoResultadoCodificado() != null && !old.getGrupoResultadoCodificado().equals(campoLaudo.getGrupoResultadoCodificado()))
				|| (campoLaudo.getGrupoResultadoCodificado() != null && !campoLaudo.getGrupoResultadoCodificado().equals(old.getGrupoResultadoCodificado()))){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00369);
		}

		// Verficia se a situação foi alterada e a nova situação é inativa
		if(!old.getSituacao().equals(campoLaudo.getSituacao()) && DominioSituacao.I.equals(campoLaudo.getSituacao())){
			this.verificarSituacaoAtivaVersaoParametroCampoLaudo(campoLaudo);
		}
		
	}
	
	/**
	 * Atuliza AelCampoLaudo
	 */
	public AelCampoLaudo atualizar(AelCampoLaudo campoLaudo) throws BaseException{
		this.executarRestricoes(campoLaudo);
		final AelCampoLaudo old = this.getAelCampoLaudoDAO().obterOriginal(campoLaudo);
		this.preAtualizar(campoLaudo, old);		
		this.getAelCampoLaudoDAO().merge(campoLaudo);
		this.posAtualizar(campoLaudo, old);
		return campoLaudo;
	}
	
	
	/**
	 * ORADB PROCEDURE AELP_ENFORCE_CAL_RULES (UPDATE)
	 */
	private void posAtualizar(AelCampoLaudo campoLaudo, AelCampoLaudo old) throws BaseException{

		// Verficia se a situação e ordem foram alteradas
		if(!old.getSituacao().equals(campoLaudo.getSituacao()) 
				|| (old.getOrdem() != null && !old.getOrdem().equals(campoLaudo.getOrdem())) 
				|| (campoLaudo.getOrdem() != null && !campoLaudo.getOrdem().equals(old.getOrdem()))){
			this.verificarCampoLaudoOrdem(campoLaudo);
		}
	}
	
	/**
	 * ORADB TRIGGER AELT_CAL_BRD (DELETE)
	 */
	private void preRemover(AelCampoLaudo campoLaudo) throws BaseException{
		this.verificarDelecao(campoLaudo);
	}
	
	/**
	 * Atuliza AelCampoLaudo
	 */
	public void remover(Integer seq) throws BaseException{
		final AelCampoLaudo campoLaudo = getAelCampoLaudoDAO().obterPorChavePrimaria(seq);
		
		if (campoLaudo == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.verificarDependenciasRemover(campoLaudo);
		this.preRemover(campoLaudo);		
		this.getAelCampoLaudoDAO().remover(campoLaudo);
	}
	
	
	/*
	 * RNs Inserir
	 */
	
	/**
	 * ORADB PROCEDURE AELK_AEL_RN.RN_AELP_ATU_SERVIDOR
	 * Atualiza servidor com o usuário logado e data de criação com a data atual
	 * @param campoLaudo
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarServidorLogadoDataCriacao(AelCampoLaudo campoLaudo) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		campoLaudo.setCriadoEm(new Date());
		campoLaudo.setServidor(servidorLogado);
		
	}
	
	/**
	 * ORADB PROCEDURE AELK_CAL_RN.RN_CALP_VER_GR_CARAC
	 * Verifica se o Grupo Característica está ativo
	 * @param campoLaudo
	 */
	protected void verificarGrupoResultadoCaracteristicaAtivo(AelCampoLaudo campoLaudo) throws ApplicationBusinessException{
		
		final AelGrupoResultadoCaracteristica grupoResultadoCaracteristica = campoLaudo.getGrupoResultadoCaracteristica();	
		
		if (grupoResultadoCaracteristica != null && DominioSituacao.I.equals(grupoResultadoCaracteristica.getSituacao())){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00769);
		}
			
	}
	
	/**
	 * ORADB PROCEDURE AELK_CAL_RN.RN_CALP_VER_GR_CODIF
	 * Verifica se o Grupo Codificado está ativo
	 * @param campoLaudo
	 */
	protected void verificarGrupoResultadoCodificadoAtivo(AelCampoLaudo campoLaudo) throws ApplicationBusinessException{
		
		final AelGrupoResultadoCodificado grupoResultadoCodificado = campoLaudo.getGrupoResultadoCodificado();	
		
		if (grupoResultadoCodificado != null && DominioSituacao.I.equals(grupoResultadoCodificado.getSituacao())){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00692);
		}
	}
	
	
	/**
	 * ORADB PROCEDURE AELK_CAL_RN.RN_CALP_VER_TXT_FIXO
	 * Verifica se já existe um Campo Laudo do tipo Texto Fixo cadastrado
	 * @param campoLaudo
	 */
	protected void verificarCampoLaudoTextoFixoUnico(AelCampoLaudo campoLaudo) throws ApplicationBusinessException{

		if(DominioTipoCampoCampoLaudo.T.equals(campoLaudo.getTipoCampo()) && this.getAelCampoLaudoDAO().existeCampoLaudoAtivoTipoCampoTextoFixo()){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_01063);
		}
		
	}
	
	/**
	 * ORADB PROCEDURE AELK_CAL_RN.RN_CALP_ATU_SINONIMO
	 * Gera Sinônimo de Campo Laudo
	 * @param campoLaudo
	 */
	protected void atualizarSinonimoCampoLaudo(AelCampoLaudo campoLaudo) throws BaseException{

		final AelSinonimoCampoLaudo sinonimoCampoLaudo = new AelSinonimoCampoLaudo();
		
		sinonimoCampoLaudo.setCampoLaudo(campoLaudo);
		sinonimoCampoLaudo.setNome(campoLaudo.getNome());

		this.getAelSinonimoCampoLaudoRN().persistirSinonimoCampoLaudo(sinonimoCampoLaudo);
	
	}
	
	/**
	 * ORADB PROCEDURE AELK_CAL_RN.RN_CALP_VER_ORDEM
	 * Verifica se já existe um Campo Laudo do tipo Texto Fixo cadastrado
	 * @param campoLaudo
	 */
	protected void verificarCampoLaudoOrdem(AelCampoLaudo campoLaudo) throws ApplicationBusinessException{

		final List<AelCampoLaudo> listaCampoLaudoOrdemRepetida = this.getAelCampoLaudoDAO().pesquisarCampoLaudoAtivoOrdemRepetida(campoLaudo.getSeq(), campoLaudo.getOrdem());

		if(listaCampoLaudoOrdemRepetida != null && !listaCampoLaudoOrdemRepetida.isEmpty()){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_00886);
		}
		
	}
	
	
	/*
	 * RNs Atualizar
	 */
	
	/**
	 * ORADB PROCEDURE AELK_CAL_RN.RN_CALP_VER_SIT_VERS
	 * Verifica se a situação da versão do Parâmetro Campo Laudo está ativa
	 * @param campoLaudo
	 */
	protected void verificarSituacaoAtivaVersaoParametroCampoLaudo(AelCampoLaudo campoLaudo) throws ApplicationBusinessException{

		if(this.getAelParametroCamposLaudoDAO().existeVersaoParametroAtivaCampoLaudo(campoLaudo.getSeq())){
			throw new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.AEL_01154);
		}
		
	}
	

	/*
	 * RNs Remover
	 */
	
	/**
	 * Verifica dependências antes da remoção e acrescenta cada exceção em uma lista
	 * @param campoLaudo
	 * @throws BaseException
	 */
	private void verificarDependenciasRemover(AelCampoLaudo campoLaudo) throws BaseException {
		
		// Declara lista de exceções
		final BaseListException erros = new BaseListException();

		// Valida cada dependência
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo.getSeq(), AelValorNormalidCampo.class, AelValorNormalidCampo.Fields.CAL_SEQ, this.getResourceBundleValue("LABEL_AEL_VALORES_NORMALID_CAMPOS")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo.getSeq(), AelSinonimoCampoLaudo.class, AelSinonimoCampoLaudo.Fields.CAL_SEQ, this.getResourceBundleValue("LABEL_AEL_SINONIMOS_CAMPOS_LAUDO")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo.getSeq(), AelServidorCampoLaudo.class, AelServidorCampoLaudo.Fields.CAL_SEQ, this.getResourceBundleValue("LABEL_AEL_SERVIDOR_CAMPOS_LAUDO")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo, AelExameEquipamento.class, AelExameEquipamento.Fields.CAMPO_LAUDO,this.getResourceBundleValue("LABEL_AEL_EXAMES_EQUIPAMENTOS")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo.getSeq(), AelGrupoTecnicaCampo.class, AelGrupoTecnicaCampo.Fields.CAL_SEQ, this.getResourceBundleValue("LABEL_AEL_GRP_TECNICA_CAMPOS")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo, AbsExameComponenteVisualPrescricao.class, AbsExameComponenteVisualPrescricao.Fields.CAMPO_LAUDO, this.getResourceBundleValue("LABEL_ABS_EXAMES_COMP_VISUAL_PRCR")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo.getSeq(), AelCampoLaudoLws.class, AelCampoLaudoLws.Fields.CAL_SEQ, this.getResourceBundleValue("LABEL_AEL_CAMPO_LAUDO_LWS")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo, AelResultadoPadraoCampo.class, AelResultadoPadraoCampo.Fields.CAMPO_LAUDO, this.getResourceBundleValue("LABEL_AEL_RESUL_PADROES_CAMPOS")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo, AelParametroCamposLaudo.class, AelParametroCamposLaudo.Fields.CAMPO_LAUDO, this.getResourceBundleValue("LABEL_AEL_PARAMETRO_CAMPOS_LAUDO")));
		erros.add(this.obterNegocioExceptionDependencias(campoLaudo.getSeq(), AelCampoUsoFaturamento.class, AelCampoUsoFaturamento.Fields.CAL_SEQ, this.getResourceBundleValue("LABEL_AEL_CAMPOS_USO_FATURAMENTO")));
		
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

		CoreUtil.validaParametrosObrigatorios(elemento,classeDependente,fieldChaveEstrangeira, nomeDependencia);

		if (this.getAelCampoLaudoDAO().existeDependencia(elemento, classeDependente, fieldChaveEstrangeira)){
			return new ApplicationBusinessException(AelCampoLaudoRNExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS, nomeDependencia);
		}
		return null;
	}
	
	
	/**
	 * ORADB PROCEDURE AELK_CAL_RN.RN_CALP_VER_DELECAO
	 * 
	 * @param campoLaudo
	 */
	protected void verificarDelecao(AelCampoLaudo campoLaudo) throws ApplicationBusinessException{
		
		final AelCampoLaudo old = this.getAelCampoLaudoDAO().obterOriginal(campoLaudo);
		
		// Chamada para PROCEDURE AELK_CAL_RN.RN_CALP_VER_DELECAO
		this.getAnticoagulanteRN().verificarParametroDelecao(old.getCriadoEm());
	}

	/**
	 * Getters para RNs e DAOs
	 */
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}	

	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}
	
	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}
	
	protected AnticoagulanteRN getAnticoagulanteRN(){
		return anticoagulanteRN;
	}
	
	protected AelSinonimoCampoLaudoRN getAelSinonimoCampoLaudoRN(){
		return aelSinonimoCampoLaudoRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
