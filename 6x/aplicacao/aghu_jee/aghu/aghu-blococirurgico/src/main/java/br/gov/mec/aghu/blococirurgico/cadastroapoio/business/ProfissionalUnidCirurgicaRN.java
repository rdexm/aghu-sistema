package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaImplantacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEscalaProfUnidCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.MbcAgendaImplantacaoId;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsJn;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável por prover os métodos de negócio genéricos para cadastro de 
 * profissionais por unidade cirúrgica
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class ProfissionalUnidCirurgicaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(ProfissionalUnidCirurgicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private MbcSubstEscalaSalaDAO mbcSubstEscalaSalaDAO;

	@Inject
	private MbcEscalaProfUnidCirgDAO mbcEscalaProfUnidCirgDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcProfAtuaUnidCirgsJnDAO mbcProfAtuaUnidCirgsJnDAO;

	@Inject
	private MbcAgendaImplantacaoDAO mbcAgendaImplantacaoDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	private static final long serialVersionUID = -9001235819703398140L;

	private enum ProfissionalUnidCirurgicaRNExceptionCode implements BusinessExceptionCode {
		MBC_00292, MBC_00296, MBC_00294, MBC_00492, MENSAGEM_ERRO_INCLUSAO_PROFISSIONAL_UNIDADE_JA_EXISTE,
		MENSAGEM_ERRO_EXCLUSAO_AGENDA_IMPLANTACAO_EXISTENTE, MENSAGEM_ERRO_EXCLUSAO_ESCALA_PROFI_UNIDADE_CIRURGICA_EXISTENTE,
		MENSAGEM_ERRO_EXCLUSAO_SUBS_ESCALA_SALA_EXISTENTE, MENSAGEM_ERRO_EXCLUSAO_PROF_CIRURGIA_EXISTENTE,
		MENSAGEM_ERRO_EXCLUSAO_CARACT_SALA_ESP_EXISTENTE;
	}

	public void persistirProfiUnidade(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException {
		if (profissional.getId() == null) {
			this.executarAntesInserir(profissional);
			this.getMbcProfAtuaUnidCirgsDAO().persistir(profissional);
			this.getMbcProfAtuaUnidCirgsDAO().flush();
			this.mbcpVerUser(profissional);
		} else {
			MbcProfAtuaUnidCirgs oldProfissional = getMbcProfAtuaUnidCirgsDAO().obterOriginal(profissional);
			this.getMbcProfAtuaUnidCirgsDAO().atualizar(profissional);
			this.getMbcProfAtuaUnidCirgsDAO().flush();
			executarDepoisAlterar(profissional, oldProfissional);
		}
	}
	
	public void excluirProfiUnidade(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException{
		MbcProfAtuaUnidCirgs profissionalExcluir = getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(profissional.getId()); 
		executarAntesExcluir(profissionalExcluir);
		this.getMbcProfAtuaUnidCirgsDAO().remover(profissionalExcluir);
		this.getMbcProfAtuaUnidCirgsDAO().flush();
		this.mbcpRevoga(profissionalExcluir);
		insereMbcProfAtuaUnidCirgsJn(profissionalExcluir, DominioOperacoesJournal.DEL);
	}

	/**
	 * ORADB TRIGGER "AGH".MBCT_PUC_BRD
	 * @param profissional
	 * @throws ApplicationBusinessException
	 */
	protected void executarAntesExcluir(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException {
		// ORADB MBCK_PUC_RN.RN_PUCP_VER_DEL
		Integer dias = null;
		AghParametros parametro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_MBC);
		if (parametro != null) {
			dias = parametro.getVlrNumerico().intValue();
		}
		
		Date dtReferencia = new Date();
		dtReferencia = DateUtil.adicionaDias(dtReferencia, -dias);
		if (DateUtil.validaDataMenorIgual(profissional.getCriadoEm(), dtReferencia)) {
			throw new ApplicationBusinessException(
					ProfissionalUnidCirurgicaRNExceptionCode.MBC_00292);
		}
		
		// ORADB CHK_MBC_PROF_ATUA_UNID_CI
		MbcAgendaImplantacaoId agendaImplantacaoId = getChaveCompostaAgendaImplantacao(profissional);
		if (getMbcAgendaImplantacaoDAO().obterAgendaImplantacaoPorId(agendaImplantacaoId) > 0) {
			throw new ApplicationBusinessException(
					ProfissionalUnidCirurgicaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_AGENDA_IMPLANTACAO_EXISTENTE);
		}
		
		Long escalaProfUnidCirgCount = getMbcEscalaProfUnidCirgDAO()
			.pesquisarEscalaProfissionalPorMatriculaCodigoUnidFuncaoCount(profissional.getRapServidores().getId().getMatricula()
					, profissional.getRapServidores().getId().getVinCodigo(), profissional.getUnidadeFuncional().getSeq()
					, profissional.getIndFuncaoProf());
		
		if (escalaProfUnidCirgCount > 0) {
			throw new ApplicationBusinessException(
					ProfissionalUnidCirurgicaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_ESCALA_PROFI_UNIDADE_CIRURGICA_EXISTENTE);
		}
		
		Long substEscalaSalaCount = getMbcSubstEscalaSalaDAO()
			.pesquisarSubsEscalaSalaPorMatriculaCodigoUnidFuncaoCount(profissional.getRapServidores().getId().getMatricula()
					, profissional.getRapServidores().getId().getVinCodigo(), profissional.getUnidadeFuncional().getSeq()
					, profissional.getIndFuncaoProf());
		if (substEscalaSalaCount > 0) {
			throw new ApplicationBusinessException(
					ProfissionalUnidCirurgicaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_SUBS_ESCALA_SALA_EXISTENTE);
		}
		
		Long profCirurgiasCount = getMbcProfCirurgiasDAO()
			.pesquisarMbcProfCirurgiasPorMatriculaCodigoUnidFuncaoCount(profissional.getRapServidores().getId().getMatricula()
					, profissional.getRapServidores().getId().getVinCodigo(), profissional.getUnidadeFuncional().getSeq()
					, profissional.getIndFuncaoProf());
		if (profCirurgiasCount > 0) {
			throw new ApplicationBusinessException(
					ProfissionalUnidCirurgicaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_PROF_CIRURGIA_EXISTENTE);
		}
		
		Long caractSalaEspCount = getMbcCaractSalaEspDAO()
			.pesquisarMbcCaractSalaEspPorMbcProfAtuaUnidCirgsCount(profissional);
		if(caractSalaEspCount > 0) {
			throw new ApplicationBusinessException(
					ProfissionalUnidCirurgicaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_CARACT_SALA_ESP_EXISTENTE);
		}
	}
	
	private MbcAgendaImplantacaoId getChaveCompostaAgendaImplantacao(MbcProfAtuaUnidCirgs profissional) {
		MbcAgendaImplantacaoId agendaImplantacaoId = new MbcAgendaImplantacaoId();
		agendaImplantacaoId.setPucIndFuncaoProf(profissional.getIndFuncaoProf());
		agendaImplantacaoId.setPucSerMatricula(profissional.getRapServidores().getId().getMatricula());
		agendaImplantacaoId.setPucSerVinCodigo(profissional.getRapServidores().getId().getVinCodigo());
		agendaImplantacaoId.setPucUnfSeq(profissional.getUnidadeFuncional().getSeq());
		
		return agendaImplantacaoId;
	}
	
	/**
	 * ORADB MBCT_PUC_BRI
	 * @param profissional
	 * @throws ApplicationBusinessException
	 */
	protected void executarAntesInserir(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException{
		profissional.setCriadoEm(new Date());
		verFuncaoProfissional(profissional);

		//defini o Id setando todos os atributos necessários
		definirId(profissional);			
	}
	
	/**
	 * ORADB MBCP_REVOGA
	 * @param profissional
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @throws ApplicationBusinessException 
	 */
	protected void mbcpRevoga(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException{
		
		AghParametros parametros;
		List<String> listaEspecialidade = Arrays.asList(
				DominioFuncaoProfissional.MPF.toString(),
				DominioFuncaoProfissional.MCO.toString(),
				DominioFuncaoProfissional.MAX.toString(),
				DominioFuncaoProfissional.ANP.toString(),
				DominioFuncaoProfissional.ANC.toString(),
				DominioFuncaoProfissional.ANR.toString(),
				DominioFuncaoProfissional.ENF.toString());

		if (listaEspecialidade.contains(profissional.getIndFuncaoProf().name())) {
		
			if (profissional.getIndFuncaoProf().name().equals(DominioFuncaoProfissional.ENF.toString())) {
				/*-- Enfermeiro*/
				parametros = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_PERFIL_ENF_MBC);
			} else {
				/*-- Médico*/
				parametros = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_PERFIL_MED_MBC);
			}
			
			if (isHCPA()) {	
				
				getObjetosOracleDAO().revogarPerfisServidor(
					profissional.getRapServidores().getId().getMatricula(), 
					profissional.getRapServidores().getId().getVinCodigo(), null,
					parametros.getVlrTexto(), 
					profissional.getServidorAlteradoPor());
				
			} else if (!getBlocoCirurgicoFacade().verificarProfissionalEstaAtivoEmOutraUnidade(profissional)) {
				Usuario usuario = getCascaFacade().obterUsuario(profissional.getRapServidores().getUsuario());
				if (usuario != null) {
					List<PerfisUsuarios> listaPerf = getCascaFacade().pequisarPerfisUsuarios(usuario);
					if (listaPerf != null && !listaPerf.isEmpty()) {
						for(PerfisUsuarios p : listaPerf) {
							if (p.getPerfil().getDescricao().equals(parametros.getVlrTexto())) {
								getCascaFacade().removerPerfisUsuario(p);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * ORADB MBCP_VER_USER
	 * @param profissional
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @throws ApplicationBusinessException 
	 */
	protected void mbcpVerUser(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException{
		
		AghParametros parametros;
		List<String> listaEspecialidade = Arrays.asList(
				DominioFuncaoProfissional.MPF.toString(),
				DominioFuncaoProfissional.MCO.toString(),
				DominioFuncaoProfissional.MAX.toString(),
				DominioFuncaoProfissional.ANP.toString(),
				DominioFuncaoProfissional.ANC.toString(),
				DominioFuncaoProfissional.ANR.toString(),
				DominioFuncaoProfissional.ENF.toString());

		if (listaEspecialidade.contains(profissional.getIndFuncaoProf().name())) {
		
			if (profissional.getIndFuncaoProf().name().equals(DominioFuncaoProfissional.ENF.toString())) {
				/*-- Enfermeiro*/
				parametros = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_PERFIL_ENF_MBC);
			} else {
				/*-- Médico*/
				parametros = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_PERFIL_MED_MBC);
			}
            
			if (isHCPA()) {	
				
				getObjetosOracleDAO().fornecerPerfisServidor(
					profissional.getRapServidores().getId().getMatricula(), 
					profissional.getRapServidores().getId().getVinCodigo(), null,
					parametros.getVlrTexto(), 
					profissional.getServidorCadastrado());
				
			} else {
		
				getCascaFacade().atribuirPerfilUsuario(
						profissional.getRapServidores().getUsuario(), 
						parametros.getVlrTexto(), 
						profissional.getServidorCadastrado().getUsuario());
					
					this.getMbcProfAtuaUnidCirgsDAO().flush();
				}
			}
		}
	
	/**
	 * ORADB MBCT_PUC_ARU
	 * @param profissional
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @throws ApplicationBusinessException 
	 */
	protected void executarDepoisAlterar(MbcProfAtuaUnidCirgs profissional, MbcProfAtuaUnidCirgs oldProfissional) throws ApplicationBusinessException, ApplicationBusinessException, ApplicationBusinessException{
		profissional.setCriadoEm(new Date());
		verFuncaoProfissional(profissional);
		
		if (CoreUtil.modificados(profissional.getSituacao(), oldProfissional.getSituacao())) {
			
			if (profissional.getSituacao().equals(DominioSituacao.I)) {
				this.mbcpRevoga(profissional);
			} else {
				this.mbcpVerUser(profissional);
			}
			
			insereMbcProfAtuaUnidCirgsJn(profissional, DominioOperacoesJournal.UPD);
		}
	}
	
	
	/**
	 * ORADB mbck_puc_rn.rn_pucp_ver_fnc_prof
	 * @param profissional
	 * @throws ApplicationBusinessException
	 */
	protected void verFuncaoProfissional(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException{
		if(DominioFuncaoProfissional.MPF.equals(profissional.getIndFuncaoProf()) || DominioFuncaoProfissional.MCO.equals(profissional.getIndFuncaoProf())){

			//verifica caracteristica unidade funcional é igual CCA
			boolean isCCA = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(profissional.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.CCA);
		
			//tem ind_atua_ambt = ‘S’
			//tem ind_cirurgiao_bloco = ‘S’
			boolean isAtuaAmbt = false;
			boolean isCirurgiaoBloco = false;
			boolean isAtuaInternacao = false;
			
			List<AghProfEspecialidades> profEsp = new ArrayList<AghProfEspecialidades>(getAghuFacade().listarEspecialidadesPorServidor(profissional.getRapServidores()));
			if(profEsp!=null && profEsp.size() > 0){
				for (AghProfEspecialidades aghProfEspecialidades : profEsp) {
					if(aghProfEspecialidades.getIndAtuaAmbt()){
						isAtuaAmbt = true;
					}
					if(aghProfEspecialidades.getIndCirurgiaoBloco().isSim()){
						isCirurgiaoBloco = true;
					}
					if(aghProfEspecialidades.getIndAtuaInternacao().isSim()){
						isAtuaInternacao = true;
					}
				}
			}

			if(isCCA && !isAtuaAmbt){
				throw new ApplicationBusinessException(ProfissionalUnidCirurgicaRNExceptionCode.MBC_00296);
			}else if(!isCCA && DominioFuncaoProfissional.MPF.equals(profissional.getIndFuncaoProf()) && !isCirurgiaoBloco){
				throw new ApplicationBusinessException(ProfissionalUnidCirurgicaRNExceptionCode.MBC_00294);
			}else if(!isCCA && DominioFuncaoProfissional.MCO.equals(profissional.getIndFuncaoProf()) && !isCirurgiaoBloco && !isAtuaInternacao){
				throw new ApplicationBusinessException(ProfissionalUnidCirurgicaRNExceptionCode.MBC_00492);
			}						
		}
	}
	
	protected void definirId(MbcProfAtuaUnidCirgs profissional) throws ApplicationBusinessException{
		if(profissional != null && profissional.getId() == null){
			MbcProfAtuaUnidCirgsId profissionalId = new MbcProfAtuaUnidCirgsId();
			profissionalId.setIndFuncaoProf(profissional.getIndFuncaoProf());
			profissionalId.setSerMatricula(profissional.getRapServidores().getId().getMatricula());
			profissionalId.setSerVinCodigo(profissional.getRapServidores().getId().getVinCodigo());
			profissionalId.setUnfSeq(profissional.getUnidadeFuncional().getSeq());
			
			
			MbcProfAtuaUnidCirgs objExistente = getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(profissionalId);
			if(objExistente != null){
				throw new ApplicationBusinessException(ProfissionalUnidCirurgicaRNExceptionCode.MENSAGEM_ERRO_INCLUSAO_PROFISSIONAL_UNIDADE_JA_EXISTE, profissional.getUnidadeFuncional().getSeq(), profissional.getUnidadeFuncional().getDescricao());

			}
			
			profissional.setId(profissionalId);
		}
	}
	
	
	protected void insereMbcProfAtuaUnidCirgsJn(MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs, DominioOperacoesJournal operacao) {
			MbcProfAtuaUnidCirgsJn jn = BaseJournalFactory.getBaseJournal(operacao, MbcProfAtuaUnidCirgsJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());

			jn.setSerMatricula(mbcProfAtuaUnidCirgs.getId().getSerMatricula());
			jn.setSerVinCodigo(mbcProfAtuaUnidCirgs.getId().getSerVinCodigo());
			jn.setUnfSeq(mbcProfAtuaUnidCirgs.getId().getUnfSeq());
			jn.setIndFuncaoProf(mbcProfAtuaUnidCirgs.getId().getIndFuncaoProf());
			jn.setMbcProfAtuaUnidCirgs(mbcProfAtuaUnidCirgs.getMbcProfAtuaUnidCirgs());
			jn.setServidorCadastrado(mbcProfAtuaUnidCirgs.getServidorCadastrado());
			jn.setServidorAlteradoPor(mbcProfAtuaUnidCirgs.getServidorAlteradoPor());
			jn.setSituacao(mbcProfAtuaUnidCirgs.getSituacao());
			jn.setCriadoEm(new Date());

			getMbcProfAtuaUnidCirgsJnDAO().persistir(jn);
			getMbcProfAtuaUnidCirgsJnDAO().flush();
	}
	
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO(){
		return mbcProfAtuaUnidCirgsDAO;
	}
	
	protected MbcProfAtuaUnidCirgsJnDAO getMbcProfAtuaUnidCirgsJnDAO(){
		return mbcProfAtuaUnidCirgsJnDAO;
	}
	
	protected MbcAgendaImplantacaoDAO getMbcAgendaImplantacaoDAO(){
		return mbcAgendaImplantacaoDAO;
	}
	
	protected MbcEscalaProfUnidCirgDAO getMbcEscalaProfUnidCirgDAO(){
		return mbcEscalaProfUnidCirgDAO;
	}
	
	protected MbcSubstEscalaSalaDAO getMbcSubstEscalaSalaDAO() {
		return mbcSubstEscalaSalaDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}	
	
	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}
	
	protected ICascaFacade getCascaFacade() {
		return iCascaFacade;
	}
	
	protected ICascaFacade getICascaFacade() {
		return iCascaFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return this.iRegistroColaboradorFacade;
	}
	
}