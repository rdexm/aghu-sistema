package br.gov.mec.aghu.exames.patologia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaJnDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelPatologistaJn;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelPatologistaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelPatologistaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
		
	@Inject
	private AelPatologistaJnDAO aelPatologistaJnDAO;
	
	@Inject
	private AelPatologistaDAO aelPatologistaDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private ICascaFacade cascaFacade ;
	
	@EJB
	private IParametroFacade parametroFacade ;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	private static final long serialVersionUID = -798406697870064652L;

	private enum AelPatologistaExceptionCode implements BusinessExceptionCode {
		AEL_00353, AEL_02606, AEL_02700, AEL_02605, USUARIO_SEM_ACESSO_AO_SISTEMA, HA_REGISTROS_DEPENDENTES, ERRO_CLONE_PATOLOGISTA,
		MSG_EXISTE_PATOLOGISTA_ATIVO_SERVIDOR, USUARIO_SEM_MED03; 
	}

	public void persistir(AelPatologista patologista) throws BaseException {

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		adicionarRevogarPermissao(patologista, patologista.getSituacao(), servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		if(patologista.getSeq() != null) {
			this.atualizar(patologista);
		} else {
			this.inserir(patologista);
		}
		
		getAelPatologistaDAO().flush();
	}
	
	public void adicionarRevogarPermissao(AelPatologista patologista, DominioSituacao situacao, String usuarioLogado) throws BaseException{
		String userId;
		
		if(patologista.getServidor().getUsuario() != null){
			userId =  patologista.getServidor().getUsuario();
		}else{
			userId = this.aelPatologistaDAO.pesquisarUsuarioPorMatricula(patologista.getServidor().getId().getMatricula());
		}
		
		Boolean MED03 = false;
		
		// BUSCA PARAMETRO COM PERFIL MEDICO EXECUTOR EXAMES (MED03)
		List<Perfil> perfisParamerizados = getCascaFacade().pesquisarPerfis(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERFIL_PATOLOGISTA_LAUDO_UNICO).getVlrTexto());
		Perfil perfilMedicoExecutorExames = perfisParamerizados.get(0);
		// BUSCA PERFIL DIFERENTE PARA CADA FUNÇÃO
		List<Perfil> perfis = getCascaFacade().pesquisarPerfis(patologista.getFuncao().getPermissao());
		// BUSCA TODOS PERFIS DE PATOLOGISTA
		List<Perfil> perfisPatologista =  buscaPerfisPatologista(perfilMedicoExecutorExames);
		
		if(!perfis.isEmpty()) {
			List<Usuario> usuarios = getCascaFacade().pesquisarUsuarios(0, 1, null, true, userId);
			if(!usuarios.isEmpty()) {
				Usuario usuario = getCascaFacade().obterUsuario(usuarios.get(0).getId());
				Set<PerfisUsuarios> perfisUsuario = usuario.getPerfisUsuario();
				
				for (PerfisUsuarios perfisUsuarios : perfisUsuario) {
					if(perfisUsuarios.getPerfil().equals(perfilMedicoExecutorExames)){
						MED03 = true;
					}
				}
				if(!MED03 && !DominioFuncaoPatologista.T.name().equals(patologista.getFuncao().name())){
					throw new ApplicationBusinessException(AelPatologistaExceptionCode.USUARIO_SEM_MED03);
				}else{
					// CARREGA PERFIS PARA ASSOCIAR
					List<PerfisUsuarios> perfisUsuarioNovo = carregaPerfilPatologista(perfisUsuario,perfis,patologista,usuario,perfilMedicoExecutorExames);
					getCascaFacade().associarPerfilUsuarioPatologista(usuarios.get(0).getId(), new ArrayList<PerfisUsuarios>(perfisUsuarioNovo), perfisPatologista);							
				}
			}else {
				throw new ApplicationBusinessException(AelPatologistaExceptionCode.USUARIO_SEM_ACESSO_AO_SISTEMA);
			}
		}
	}
	
	private List<Perfil> buscaPerfisPatologista(Perfil perfilMedicoExecutorExames) throws BaseException {
		List<Perfil> perfisPatologista = new ArrayList<Perfil>(0);
		List<Perfil> perfis = null;
			perfis = getCascaFacade().pesquisarPerfis(DominioFuncaoPatologista.P.getPermissao());
			perfisPatologista.add(perfis.get(0));
			perfis = getCascaFacade().pesquisarPerfis(DominioFuncaoPatologista.C.getPermissao());
			perfisPatologista.add(perfis.get(0));
			perfis = getCascaFacade().pesquisarPerfis(DominioFuncaoPatologista.R.getPermissao());
			perfisPatologista.add(perfis.get(0));
			perfis = getCascaFacade().pesquisarPerfis(DominioFuncaoPatologista.T.getPermissao());
			perfisPatologista.add(perfis.get(0));
			//if(perfilMedicoExecutorExames != null){
				//perfisPatologista.add(perfilMedicoExecutorExames);
			//}
	
		return perfisPatologista;
	}
	
	private List<PerfisUsuarios> carregaPerfilPatologista(Set<PerfisUsuarios> perfisUsuario, List<Perfil> perfis,AelPatologista patologista, 
			Usuario usuario, Perfil perfilMedicoExecutorExames) throws BaseException {
	
		List<PerfisUsuarios> perfisUsuarioNovo =  new ArrayList<PerfisUsuarios>(0);
		PerfisUsuarios perfilUsuario = new PerfisUsuarios();
		perfilUsuario.setDataCriacao(new Date());
		perfilUsuario.setUsuario(usuario);
		perfilUsuario.setPerfil(perfis.get(0));
		perfisUsuarioNovo.add(perfilUsuario);
		
		return perfisUsuarioNovo;
	}
	
	public void inserir(AelPatologista patologista) throws BaseException {
		preInserir(patologista);
		getAelPatologistaDAO().persistir(patologista);
		posInserir(patologista);
	}

	public void atualizar(AelPatologista patologista) throws BaseException {
		AelPatologista patologistaOld = getAelPatologistaDAO().obterOriginal(patologista);
		preAtualizar(patologista, patologistaOld);
		getAelPatologistaDAO().merge(patologista);
		posAtualizar(patologista, patologistaOld);
	}
	
	public void excluir(Integer seq) throws BaseException {
		try {
			AelPatologista patologista = getAelPatologistaDAO().obterPorChavePrimaria(seq);
			getAelPatologistaDAO().remover(patologista);
			posDelete(patologista);
			getAelPatologistaDAO().flush();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(AelPatologistaExceptionCode.HA_REGISTROS_DEPENDENTES, cve.getConstraintName());
			}
		}
	}
	
	public AelPatologista clonarPatologista(AelPatologista patologista) throws BaseException {
		AelPatologista patologistaClone = null;
		try{
			patologistaClone = (AelPatologista) BeanUtils.cloneBean(patologista);
		} catch(Exception e){
			throw new ApplicationBusinessException(AelPatologistaExceptionCode.ERRO_CLONE_PATOLOGISTA);
		}
		
		if(patologista.getServidor() != null) {
			RapServidores servidor = new RapServidores(new RapServidoresId(patologista.getServidor().getId().getMatricula(), patologista.getServidor().getId().getVinCodigo()));
			patologistaClone.setServidor(servidor);
		}

		if(patologista.getServidorDigitacao() != null) {
			RapServidores servidor = new RapServidores(new RapServidoresId(patologista.getServidorDigitacao().getId().getMatricula(), patologista.getServidorDigitacao().getId().getVinCodigo()));
			patologistaClone.setServidorDigitacao(servidor);
		}

		return patologistaClone;
	}

	/**
	 *  @ORADB : AELT_LUI_BRU
	 */
	private void preAtualizar(AelPatologista patologista, AelPatologista patologistaOld) throws BaseException {
		
		//Esse codigo foi adicionado para evitar o cadastro de mais de um patologista
		//ativo por servidor - essa funcionalidade não tem no AGH
		//verifica se esta ativando um patologista e se nao tem já um ativo
		if (DominioSituacao.I.equals(patologistaOld.getSituacao()) &&	DominioSituacao.A.equals(patologista.getSituacao()) ) {
			validaExistenciaPatologistaAtivoParaServidor(patologista);
			//throw new AGHUNegocioExceptionSemRollback(AelPatologistaExceptionCode.MSG_EXISTE_PATOLOGISTA_ATIVO_SERVIDOR);
		}
		//fim da verificação
		
		if (CoreUtil.modificados(patologista.getServidor(), patologistaOld.getServidor())) {
			verNome();
		}
	}

	/**
	 *  @ORADB : AELT_LUI_ARU
	 *  @ORADB : AELT_LUI_ASU
	 */
	private void posAtualizar(AelPatologista patologista, AelPatologista patologistaOld) throws BaseException {
		if(CoreUtil.modificados(patologista.getSeq(), patologistaOld.getSeq())
				|| CoreUtil.modificados(patologista.getNome(), patologistaOld.getNome())
				|| CoreUtil.modificados(patologista.getFuncao(), patologistaOld.getFuncao())
				|| CoreUtil.modificados(patologista.getPermiteLibLaudo(), patologistaOld.getPermiteLibLaudo())
				|| CoreUtil.modificados(patologista.getSituacao(), patologistaOld.getSituacao())
				|| CoreUtil.modificados(patologista.getCriadoEm(), patologistaOld.getCriadoEm())
				|| CoreUtil.modificados(patologista.getServidor(), patologistaOld.getServidor())
				|| CoreUtil.modificados(patologista.getServidorDigitacao(), patologistaOld.getServidorDigitacao())
				|| CoreUtil.modificados(patologista.getNomeParaLaudo(), patologistaOld.getNomeParaLaudo())
		) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelPatologistaJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelPatologistaJn.class, servidorLogado.getUsuario());
			jn.setSeq(patologistaOld.getSeq());
			jn.setNome(patologistaOld.getNome());
			jn.setFuncao(patologistaOld.getFuncao());
			jn.setPermiteLibLaudo(patologistaOld.getPermiteLibLaudo());
			jn.setSituacao(patologistaOld.getSituacao());
			jn.setCriadoEm(patologistaOld.getCriadoEm());
			if(patologistaOld.getServidor() != null){
				jn.setServidor(getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(patologistaOld.getServidor().getId()));
			}
			if(patologistaOld.getServidorDigitacao() != null){
				jn.setServidorDigitacao(getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(patologistaOld.getServidorDigitacao().getId()));
			}
			jn.setNomeParaLaudo(patologistaOld.getNomeParaLaudo());
			getAelPatologistaJnDAO().persistir(jn);
		}
		
		enforceLuiRules(patologista, patologistaOld, DominioOperacaoBanco.UPD);
	}


	/**
	 *  @ORADB : AELT_LUI_ARD
	 */
	private void posDelete(AelPatologista patologista) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelPatologistaJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelPatologistaJn.class, servidorLogado.getUsuario());
		jn.setSeq(patologista.getSeq());
		jn.setNome(patologista.getNome());
		jn.setFuncao(patologista.getFuncao());
		jn.setPermiteLibLaudo(patologista.getPermiteLibLaudo());
		jn.setSituacao(patologista.getSituacao());
		jn.setCriadoEm(patologista.getCriadoEm());
		if(patologista.getServidor() != null){
			jn.setServidor(getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(patologista.getServidor().getId()));
		}
		if(patologista.getServidorDigitacao() != null){
			jn.setServidorDigitacao(getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(patologista.getServidorDigitacao().getId()));
		}
		jn.setNomeParaLaudo(patologista.getNomeParaLaudo());
		getAelPatologistaJnDAO().persistir(jn);
	}
	
	/**
	 *  @ORADB : AELT_LUI_BRI
	 */
	private void preInserir(AelPatologista patologista) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(AelPatologistaExceptionCode.AEL_00353);
		} else {
			patologista.setServidorDigitacao(servidorLogado);
		}
		
		validaExistenciaPatologistaAtivoParaServidor(patologista);
		//fim da verificação
		patologista.setCriadoEm(new Date());
		
	}

	private void validaExistenciaPatologistaAtivoParaServidor(AelPatologista patologista) throws ApplicationBusinessException {
		//Esse codigo foi adicionado para evitar o cadastro de mais de um patologista
		//ativo por servidor - essa funcionalidade não tem no AGH
		RapServidores servidor  = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(patologista.getServidor().getId());
		patologista.setServidor(servidor);
		if (getAelPatologistaDAO().isPatologistaAtivoPorServidor(servidor)) {
			throw new ApplicationBusinessException(AelPatologistaExceptionCode.MSG_EXISTE_PATOLOGISTA_ATIVO_SERVIDOR);
		}
	}
	
	/**
	 *  @ORADB : AELT_LUI_BSI
	 */
	private void posInserir(AelPatologista patologista) throws BaseException {
		enforceLuiRules(patologista, null, DominioOperacaoBanco.INS);
	}

	/**
	 *  @ORADB : AELP_ENFORCE_LUI_RULES
	 */
	private void enforceLuiRules(AelPatologista patologista, AelPatologista patologistaOld, DominioOperacaoBanco operacao) throws BaseException {
		if(DominioOperacaoBanco.UPD.equals(operacao)) {
			 /* Não pode haver mesmo servidor para a mesma função */
			if((CoreUtil.modificados(patologista.getFuncao(), patologistaOld.getFuncao()) || CoreUtil.modificados(patologista.getSituacao(), patologistaOld.getSituacao())) 
					&& DominioSituacao.A.equals(patologista.getSituacao())) {
				verFuncao(patologista.getSeq(), patologista.getServidor(), patologista.getFuncao());
			/* Não pode haver o mesmo servidor para funções diferentes */
				verFuncaoDiferente(patologista.getSeq(), patologista.getServidor(), patologista.getFuncao());
			}			
		} else if(DominioOperacaoBanco.INS.equals(operacao)) {
			if(DominioSituacao.A.equals(patologista.getSituacao())) {
			/* Não pode haver mesmo servidor para a mesma função */
				verFuncao(patologista.getSeq(), patologista.getServidor(), patologista.getFuncao());
			/* Não pode haver o mesmo servidor para funções diferentes */
				verFuncaoDiferente(patologista.getSeq(), patologista.getServidor(), patologista.getFuncao());
			}
		}
	}
	
	/**
	 *  @ORADB : aelk_lui_rn.rn_luip_ver_funcao
	 */
	public void verFuncao(Integer seq, RapServidores servidor, DominioFuncaoPatologista funcao) throws BaseException {
		Long quantidade = getAelPatologistaDAO().obterQuantidadeFuncoesPeloServidorEFuncao(seq, servidor, funcao);
		if(quantidade >= 1) {
			throw new ApplicationBusinessException(AelPatologistaExceptionCode.AEL_02606);
		}
	}

	/**
	 *  @ORADB : aelk_lui_rn.rn_luip_ver_funcao_dif
	 */
	public void verFuncaoDiferente(Integer seq, RapServidores servidor, DominioFuncaoPatologista funcao) throws BaseException {
		Long quantidade = getAelPatologistaDAO().obterQuantidadeFuncoesPeloServidorEFuncaoDiferente(seq, servidor, funcao);
		if(quantidade >= 1) {
			throw new ApplicationBusinessException(AelPatologistaExceptionCode.AEL_02700);
		}
	}
	
	/**
	 * @ORADB aelk_ael_rn.rn_luip_ver_nome
	 */
	public void verNome() throws BaseException {
		throw new ApplicationBusinessException(AelPatologistaExceptionCode.AEL_02605);
	}
	
	/**
	 * ORADB Function AELC_BUSCA_CONV_GRP
	 */
	public DominioSimNao aelcBuscaConvGrp(Integer atdSeq, Integer atvSeq) {
		Short vCspCnvCodigo = null;
		if (atdSeq != null) {
			//-- atendimento assistencial
			ConvenioExamesLaudosVO convenioExamesLaudosVO = getPacienteFacade().buscarConvenioExamesLaudos(atdSeq);
			vCspCnvCodigo = convenioExamesLaudosVO.getCodigoConvenioSaude();
			
		}
		else if (atvSeq != null) {
			ConvenioExamesLaudosVO convenioExamesLaudosVO = getExamesFacade().rnAelpBusConvAtv(atvSeq);
			vCspCnvCodigo = convenioExamesLaudosVO.getCodigoConvenioSaude();
			//-- atendimento diversos
		}
		if (vCspCnvCodigo != null) { 
			FatConvenioSaude convenioSaude = getFaturamentoFacade().obterFatConvenioSaudePorId(vCspCnvCodigo);
			if (convenioSaude != null && DominioGrupoConvenio.S.equals(convenioSaude.getGrupoConvenio())) {
				return DominioSimNao.S;				
			}
		}
		
		return DominioSimNao.N;
	}
	
	/**
	 * ORADB Function AELC_BUSCA_CONV_LAUD
	 */
	public ConvenioExamesLaudosVO aelcBuscaConvLaud(Integer atdSeq, Integer atvSeq) {
		ConvenioExamesLaudosVO convenioExamesLaudosVO = null;
		if (atdSeq != null) {
			//-- atendimento assistencial
			 convenioExamesLaudosVO = getPacienteFacade().buscarConvenioExamesLaudos(atdSeq);
			 
		} else if (atvSeq != null) {
			//-- atendimento diversos
			convenioExamesLaudosVO = getExamesFacade().rnAelpBusConvAtv(atvSeq);
		}
		
		return convenioExamesLaudosVO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelPatologistaDAO getAelPatologistaDAO() {
		return aelPatologistaDAO;
	}
	
	protected AelPatologistaJnDAO getAelPatologistaJnDAO() {
		return aelPatologistaJnDAO;
	}
		
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
			
}
