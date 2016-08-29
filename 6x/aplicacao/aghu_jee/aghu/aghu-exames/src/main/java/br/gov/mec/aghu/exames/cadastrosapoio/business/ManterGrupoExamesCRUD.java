package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTecnicaUnfExamesDAO;
import br.gov.mec.aghu.exames.dao.AelGrpTecnicaUnfExamesJnDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExameTecnicasDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExameTecnicasJnDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoTecnicaCampoDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.vo.AelGrpTecnicaUnfExamesVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExames;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExamesId;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExamesJn;
import br.gov.mec.aghu.model.AelGrupoExameTecnicas;
import br.gov.mec.aghu.model.AelGrupoExameTecnicasJn;
import br.gov.mec.aghu.model.AelGrupoTecnicaCampo;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterGrupoExamesCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterGrupoExamesCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrupoExameTecnicasJnDAO aelGrupoExameTecnicasJnDAO;
	
	@Inject
	private AelGrpTecnicaUnfExamesJnDAO aelGrpTecnicaUnfExamesJnDAO;
	
	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;
	
	@Inject
	private AelGrupoExameTecnicasDAO aelGrupoExameTecnicasDAO;
	
	@Inject
	private AelExamesDAO aelExamesDAO;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@Inject
	private AelGrupoTecnicaCampoDAO aelGrupoTecnicaCampoDAO;
	
	@Inject
	private AelGrpTecnicaUnfExamesDAO aelGrpTecnicaUnfExamesDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2608638542137822614L;

	public enum ManterGrupoExamesCRUDExceptionCode implements BusinessExceptionCode {

		GRUPO_TECNICA_UNF_EXAME_DUPLICADO,
		GRUPO_EXAMES_DESCRICAO_JA_UTILIZADA,
		ERRO_REMOVER_GRUPO_EXAMES_CONSTRAINT_GRUPO_TECNICA_UNF_EXAMES,
		ERRO_REMOVER_GRUPO_EXAMES_CONSTRAINT_GRUPO_TECNICA_CAMPO;

	}
	
	/**
	 * Persistir AelGrupoExameTecnicas
	 * @param grupoExameTecnicas
	 * @throws BaseException
	 */
	public void persistirAelGrupoExameTecnicas(AelGrupoExameTecnicas grupoExameTecnicas) throws BaseException {
		
		CoreUtil.validaParametrosObrigatorios(grupoExameTecnicas);
		
		// Verifica se a descricao de AelGrupoExameTecnicas ja foi utilizada
		if(this.getAelGrupoExameTecnicasDAO().existeGrupoPorCodigoDescricao(grupoExameTecnicas.getDescricao(), grupoExameTecnicas.getSeq())){
			throw new ApplicationBusinessException(ManterGrupoExamesCRUDExceptionCode.GRUPO_EXAMES_DESCRICAO_JA_UTILIZADA, grupoExameTecnicas.getDescricao());
		}

		if (grupoExameTecnicas.getSeq() == null) {
			// Pre-inserir
			this.preInserirAelGrupoExameTecnicas(grupoExameTecnicas);
			this.getAelGrupoExameTecnicasDAO().persistir(grupoExameTecnicas);
			
		}else{
			// Pre-atualizar
			this.preAtualizarAelGrupoExameTecnicas(grupoExameTecnicas);
			this.getAelGrupoExameTecnicasDAO().merge(grupoExameTecnicas);
		}
		
	}

	/**
	 * ORADB AELT_GRT_BRI (INSERT)
	 * @param grupoExameTecnicas
	 * @throws BaseException
	 */
	public void preInserirAelGrupoExameTecnicas(AelGrupoExameTecnicas grupoExameTecnicas) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Seta a data e hora do momento da criacao
		grupoExameTecnicas.setCriadoEm(new Date());
		// ORADB aelk_ael_rn.rn_aelp_atu_servidor 
		grupoExameTecnicas.setServidor(servidorLogado);
	}
	
	/**
	 * ORADB AELT_GRT_ARU (UPDATE)
	 * @param grupoExameTecnicas
	 * @throws BaseException
	 */
	public void preAtualizarAelGrupoExameTecnicas(AelGrupoExameTecnicas grupoExameTecnicas) throws BaseException {
	
		CoreUtil.validaParametrosObrigatorios(grupoExameTecnicas);
		
		// Obtem registro antigo/original
		AelGrupoExameTecnicas grupoExameTecnicasOld = getAelGrupoExameTecnicasDAO().obterOriginal(grupoExameTecnicas.getSeq());
		
		// Obtem atributos antigos para verificar a ocorrencia de mudancas
		final Integer seqOld = grupoExameTecnicasOld.getSeq();
		final String descricaoOld = grupoExameTecnicasOld.getDescricao();
		final DominioSituacao indSituacaoOld =  grupoExameTecnicasOld.getIndSituacao();
		final Date criadoEmOld = grupoExameTecnicasOld.getCriadoEm();
		final RapServidores servidorOld = grupoExameTecnicasOld.getServidor();
		
		// Verifica se ocorreu alguma modificacao
		if ((grupoExameTecnicas.getSeq() != null && !grupoExameTecnicas.getSeq().equals(seqOld)) ||
				(grupoExameTecnicas.getDescricao() != null && !grupoExameTecnicas.getDescricao().equals(descricaoOld)) ||
				(grupoExameTecnicas.getIndSituacao() != null && !grupoExameTecnicas.getIndSituacao().equals(indSituacaoOld)) ||
				(grupoExameTecnicas.getCriadoEm() != null && !grupoExameTecnicas.getCriadoEm().equals(criadoEmOld)) ||
				(grupoExameTecnicas.getServidor()!= null && !grupoExameTecnicas.getServidor().equals(servidorOld))
			) {
			
			// Insere na journal de AelGrupoExameTecnicas
			this.inserirAelGrupoExameTecnicasJn(grupoExameTecnicasOld, DominioOperacoesJournal.UPD);
			
		}
		
	}
	
	/**
	 * Inserir Journal de AelGrupoExameTecnicasJn
	 * @param grupoExameTecnicas
	 * @param grupoExameTecnicasOld
	 * @param dominioOperacoesJournal
	 * @throws BaseException
	 */
	public void inserirAelGrupoExameTecnicasJn(AelGrupoExameTecnicas grupoExameTecnicasOld, DominioOperacoesJournal dominioOperacoesJournal) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		CoreUtil.validaParametrosObrigatorios(dominioOperacoesJournal);

		//AelGrupoExameTecnicasJn grupoExameTecnicasJn = new AelGrupoExameTecnicasJn();
		AelGrupoExameTecnicasJn grupoExameTecnicasJn = BaseJournalFactory.getBaseJournal(dominioOperacoesJournal, AelGrupoExameTecnicasJn.class, servidorLogado.getUsuario());
		
		//grupoExameTecnicasJn.setJnUser(this.usuarioLogado);
		//grupoExameTecnicasJn.setJnDateTime(new Date());
		//grupoExameTecnicasJn.setJnOperation(dominioOperacoesJournal.toString());
		grupoExameTecnicasJn.setSeq(grupoExameTecnicasOld.getSeq());
		grupoExameTecnicasJn.setDescricao(grupoExameTecnicasOld.getDescricao());
		grupoExameTecnicasJn.setIndSituacao(grupoExameTecnicasOld.getIndSituacao());
		grupoExameTecnicasJn.setCriadoEm(grupoExameTecnicasOld.getCriadoEm());
		grupoExameTecnicasJn.setServidor(grupoExameTecnicasOld.getServidor());

		this.getAelGrupoExameTecnicasJnDAO().persistir(grupoExameTecnicasJn); 
		this.getAelGrupoExameTecnicasJnDAO().flush();
	}
	
	/**
	 * ORADB AEL_GRT_BRD (DELETE)
	 * @param grupoExameTecnicas
	 * @throws BaseException
	 */
	public void preRemoverAelGrupoExameTecnicas(AelGrupoExameTecnicas grupoExameTecnicas) throws BaseException {		
		
		// Insere na journal de AelGrupoExameTecnicas
		this.inserirAelGrupoExameTecnicasJn(grupoExameTecnicas, DominioOperacoesJournal.DEL);
	
	}

	/**
	 * Remover AelGrupoExameTecnicas
	 */
	public void removerAelGrupoExameTecnicas(Integer seq) throws BaseException{
		
		AelGrupoExameTecnicas grupoExameTecnicas = getAelGrupoExameTecnicasDAO().obterPorChavePrimaria(seq);
		if (grupoExameTecnicas == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		// Verifica a existencia de exames dependentes cadastrados no grupo de exames
		if (this.getAelGrpTecnicaUnfExamesDAO().existeAelGrpTecnicaUnfExamesPorAelGrupoExameTecnica(grupoExameTecnicas)){
			throw new ApplicationBusinessException(ManterGrupoExamesCRUDExceptionCode.ERRO_REMOVER_GRUPO_EXAMES_CONSTRAINT_GRUPO_TECNICA_UNF_EXAMES, grupoExameTecnicas.getDescricao());
		}

		// Pre-remover AelGrupoExameTecnicas
		this.preRemoverAelGrupoExameTecnicas(grupoExameTecnicas);
		
		// Remover AelGrupoExameTecnicas
		this.getAelGrupoExameTecnicasDAO().remover(grupoExameTecnicas);
	}
	
	/**
	 * Pesquisa GrpTecnicaUnfExamesVO
	 * @param grupoExameTecnicas
	 * @return
	 */
	public List<AelGrpTecnicaUnfExamesVO> buscarAelGrpTecnicaUnfExamesVOPorAelGrupoExameTecnica(AelGrupoExameTecnicas grupoExameTecnicas) {
		
		List<AelGrpTecnicaUnfExames> lista = getAelGrpTecnicaUnfExamesDAO().buscarAelGrpTecnicaUnfExamesPorAelGrupoExameTecnica(grupoExameTecnicas);
		List<AelGrpTecnicaUnfExamesVO> retorno = null; 
		
		if(lista != null && !lista.isEmpty()){
			
			retorno = new LinkedList<AelGrpTecnicaUnfExamesVO>(); 

			for (AelGrpTecnicaUnfExames item : lista) {
				
				AelGrpTecnicaUnfExamesVO vo = new AelGrpTecnicaUnfExamesVO();
				
				vo.setId(item.getId());

				final String ufeEmaExaSigla = item.getId().getUfeEmaExaSigla();
				final Integer ufeEmaManSeq = item.getId().getUfeEmaManSeq();
				final Short ufeUnfSeq = item.getId().getUfeUnfSeq();
				
				// Seta a descricao do exame
				AelExames exame = this.getAelExamesDAO().obterPeloId(ufeEmaExaSigla);
				if (exame != null){
					vo.setDescricaoExame(ufeEmaExaSigla + " - " + exame.getDescricao());
				}	
				
				// Seta o material de analise
				AelMateriaisAnalises materialAnalise =  this.getAelMaterialAnaliseDAO().obterPeloId(ufeEmaManSeq);
				if (materialAnalise != null){
					vo.setDescricaoMaterialAnalise(ufeEmaManSeq + " - " + materialAnalise.getDescricao());
				}
				
				// Seta a unidade executora do exame
				AelUnfExecutaExames unfExecutaExames = this.getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(ufeEmaExaSigla, ufeEmaManSeq, ufeUnfSeq);
				if(unfExecutaExames != null && unfExecutaExames.getUnidadeFuncional() != null){
					vo.setDescricaoUnidadeExecutora(ufeUnfSeq + " - " + unfExecutaExames.getUnidadeFuncional().getDescricao());	
				}
				
				retorno.add(vo);
			}
		
		}
		
		return  retorno;
	}
	
	/**
	 * Persistir AelGrpTecnicaUnfExames
	 * @param grpTecnicaUnfExames
	 * @throws BaseException
	 */
	public void persistirAelGrpTecnicaUnfExames(AelGrpTecnicaUnfExames grpTecnicaUnfExames) throws BaseException{
		
		CoreUtil.validaParametrosObrigatorios(grpTecnicaUnfExames);
		
		this.validarAelGrpTecnicaUnfExame(grpTecnicaUnfExames);

		this.getAelGrpTecnicaUnfExamesDAO().persistir(grpTecnicaUnfExames);
		this.getAelGrpTecnicaUnfExamesDAO().flush();
		
	}
	
	/**
	 * Inserir Journal de AelGrpTecnicaUnfExamesJn
	 * @param grAelGrpTecnicaUnfExamesOld
	 * @param dominioOperacoesJournal
	 * @throws BaseException
	 */
	public void inserirAelGrpTecnicaUnfExamesJn(AelGrpTecnicaUnfExames grAelGrpTecnicaUnfExamesOld, DominioOperacoesJournal dominioOperacoesJournal) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		CoreUtil.validaParametrosObrigatorios(dominioOperacoesJournal);

		//AelGrpTecnicaUnfExamesJn grAelGrpTecnicaUnfExamesJn = new AelGrpTecnicaUnfExamesJn();
		AelGrpTecnicaUnfExamesJn grAelGrpTecnicaUnfExamesJn = BaseJournalFactory.getBaseJournal(dominioOperacoesJournal, AelGrpTecnicaUnfExamesJn.class, servidorLogado.getUsuario());
		
		//grAelGrpTecnicaUnfExamesJn.setJnUser(this.usuarioLogado);
		//grAelGrpTecnicaUnfExamesJn.setJnDateTime(new Date());
		//grAelGrpTecnicaUnfExamesJn.setJnOperation(dominioOperacoesJournal.toString());
		grAelGrpTecnicaUnfExamesJn.setGrtSeq(grAelGrpTecnicaUnfExamesOld.getId().getGrtSeq());
		grAelGrpTecnicaUnfExamesJn.setUfeEmaExaSigla(grAelGrpTecnicaUnfExamesOld.getId().getUfeEmaExaSigla());
		grAelGrpTecnicaUnfExamesJn.setUfeEmaManSeq(grAelGrpTecnicaUnfExamesOld.getId().getUfeEmaManSeq());
		grAelGrpTecnicaUnfExamesJn.setUfeUnfSeq(grAelGrpTecnicaUnfExamesOld.getId().getUfeUnfSeq());

		this.getAelGrpTecnicaUnfExamesJnDAO().persistir(grAelGrpTecnicaUnfExamesJn); 
		this.getAelGrpTecnicaUnfExamesJnDAO().flush();
	}

	/**
	 * ORADB AEL_GRT_BRD (DELETE)
	 * @param grpTecnicaUnfExames
	 * @throws BaseException
	 */
	public void preRemoverAelGrpTecnicaUnfExames(AelGrpTecnicaUnfExames grpTecnicaUnfExames) throws BaseException {		
		// Insere na journal de AelGrpTecnicaUnfExames
		this.inserirAelGrpTecnicaUnfExamesJn(grpTecnicaUnfExames, DominioOperacoesJournal.DEL);
	}
	
	/**
	 * Validar AelGrpTecnicaUnfExames
	 * @param grpTecnicaUnfExames
	 * @throws BaseException
	 */
	protected void validarAelGrpTecnicaUnfExame(AelGrpTecnicaUnfExames grpTecnicaUnfExames) throws BaseException{
		
		// Valida existencia de registros e evita a violacao da chave primaria dos mesmos
		if(grpTecnicaUnfExames.getAelUnfExecutaExames() != null) {
			
			
			if (this.getAelGrpTecnicaUnfExamesDAO().buscarAelGrpTecnicaUnfExamesPorId(
					grpTecnicaUnfExames.getAelGrupoExameTecnicas().getSeq(), 
					grpTecnicaUnfExames.getAelUnfExecutaExames().getId().getEmaExaSigla(), 
					grpTecnicaUnfExames.getAelUnfExecutaExames().getId().getEmaManSeq(), 
					grpTecnicaUnfExames.getAelUnfExecutaExames().getId().getUnfSeq().getSeq()) != null) {
			
				AelGrpTecnicaUnfExamesVO grpTecnicaUnfExamesVO = 
					this.getAelGrpTecnicaUnfExamesDAO().obterAelGrpTecnicaUnfExamesVO(
							grpTecnicaUnfExames.getAelGrupoExameTecnicas().getSeq(), 
							grpTecnicaUnfExames.getAelUnfExecutaExames().getId().getEmaExaSigla(), 
							grpTecnicaUnfExames.getAelUnfExecutaExames().getId().getEmaManSeq(), 
							grpTecnicaUnfExames.getAelUnfExecutaExames().getId().getUnfSeq().getSeq());
				
				throw new ApplicationBusinessException(
						ManterGrupoExamesCRUDExceptionCode
						.GRUPO_TECNICA_UNF_EXAME_DUPLICADO,grpTecnicaUnfExamesVO.getDescricaoExame());
			}
		}
	}
	
	/**
	 * Remover AelGrpTecnicaUnfExames
	 */
	public void removerAelGrpTecnicaUnfExames(AelGrpTecnicaUnfExamesId id) throws BaseException{
		AelGrpTecnicaUnfExames grpTecnicaUnfExames = getAelGrpTecnicaUnfExamesDAO().obterPorChavePrimaria(id); 
		
		if (grpTecnicaUnfExames == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		CoreUtil.validaParametrosObrigatorios(grpTecnicaUnfExames);
		
		//verificar se possui ao menos um campo laudo pendencia associado. caso sim, nao deixar remover.
		List<AelGrupoTecnicaCampo> campoLaudoPendenciaList = this.getAelGrupoTecnicaCampoDAO()
			.listarAelGrupoTecnicaCampoPorExamesDoGrupo(grpTecnicaUnfExames.getId().getGrtSeq(), 
					grpTecnicaUnfExames.getId().getUfeEmaExaSigla(), grpTecnicaUnfExames.getId()
						.getUfeEmaManSeq(), grpTecnicaUnfExames.getId().getUfeUnfSeq());
		
		if(!campoLaudoPendenciaList.isEmpty()) {
			String descricao = grpTecnicaUnfExames.getAelGrupoExameTecnicas().getDescricao();
			if(grpTecnicaUnfExames.getExames() != null 
					&& StringUtils.isNotBlank(grpTecnicaUnfExames.getExames().getDescricao())) {
				descricao = grpTecnicaUnfExames.getExames().getDescricao();
			}
			throw new ApplicationBusinessException(ManterGrupoExamesCRUDExceptionCode.ERRO_REMOVER_GRUPO_EXAMES_CONSTRAINT_GRUPO_TECNICA_CAMPO, descricao);
		}
		
		// Pre-remover AelGrupoExameTecnicas
		this.preRemoverAelGrpTecnicaUnfExames(grpTecnicaUnfExames);
		
		// Remover AelGrpTecnicaUnfExames
		this.getAelGrpTecnicaUnfExamesDAO().remover(grpTecnicaUnfExames);
	}

	/**
	 * Dependencias
	 */
	
	protected AelGrupoExameTecnicasDAO getAelGrupoExameTecnicasDAO(){
		return aelGrupoExameTecnicasDAO;
	}
	
	protected AelGrupoExameTecnicasJnDAO getAelGrupoExameTecnicasJnDAO() {
		return aelGrupoExameTecnicasJnDAO;
	}
	
	protected AelGrpTecnicaUnfExamesJnDAO getAelGrpTecnicaUnfExamesJnDAO() {
		return aelGrpTecnicaUnfExamesJnDAO;
	}

	protected AelGrpTecnicaUnfExamesDAO getAelGrpTecnicaUnfExamesDAO(){
		return aelGrpTecnicaUnfExamesDAO;
	}
	
	protected AelExamesDAO getAelExamesDAO(){
		return aelExamesDAO;
	}
	
	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO(){
		return aelMaterialAnaliseDAO;
	}
	
	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO(){
		return aelUnfExecutaExamesDAO;
	}
	
	protected AelGrupoTecnicaCampoDAO getAelGrupoTecnicaCampoDAO() {
		return aelGrupoTecnicaCampoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
