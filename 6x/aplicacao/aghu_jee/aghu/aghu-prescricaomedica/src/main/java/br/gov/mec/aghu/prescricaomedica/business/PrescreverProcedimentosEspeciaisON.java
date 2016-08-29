package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoEspecial;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmModoUsoPrescProcedId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialDiversoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ModoUsoProcedimentoEspecialVO;
import br.gov.mec.aghu.prescricaomedica.vo.ProcedimentoEspecialVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author rcorvalao
 *
 */
@Stateless
public class PrescreverProcedimentosEspeciaisON extends BaseBusiness {

	@EJB
	private ManterModoUsoPrescProcedRN manterModoUsoPrescProcedRN;
	
	@EJB
	private PrescreverProcedimentoEspecialRN prescreverProcedimentoEspecialRN;
	
	private static final Log LOG = LogFactory.getLog(PrescreverProcedimentosEspeciaisON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;
	
	@Inject
	private MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private MpmTipoModoUsoProcedimentoDAO mpmTipoModoUsoProcedimentoDAO;
	
	@Inject
	private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;

    @Inject
    private ScoMaterialDAO scoMaterialDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1912275393041654817L;

	public enum PrescreverProcedEspeciaisONExceptionCode implements
			BusinessExceptionCode {
		TIPO_MODO_PROCEDIMENTO_INVALIDO, GMT_CODIGO_INVALIDO,
		MPM_02396, 
		ERRO_CAMPO_ESPECIAL_DIVERSAS_OBRIGATORIO,
		ERRO_CAMPO_PROCEDIMENTO_NO_LEITO_OBRIGATORIO,
		ERRO_CAMPO_ORTESES_OBRIGATORIO
		, NENHUMA_PROPRIEDDE_FOI_ALTERADA;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}


	private MpmTipoModoUsoProcedimentoDAO getMpmTipoModoUsoProcedimentodDAO() {
		return mpmTipoModoUsoProcedimentoDAO;
	}

	private MpmProcedEspecialDiversoDAO getMpmProcedEspecialDiversosDAO() {
		return mpmProcedEspecialDiversoDAO;
	}
	
	private PrescreverProcedimentoEspecialRN getPrescreverProcedimentoEspecialRN() {
		return prescreverProcedimentoEspecialRN;
	}
	
	private MpmPrescricaoMedicaDAO getPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	public List<MpmProcedEspecialDiversos> getListaProcedEspecialDiversos(
			Object objPesquisa) {
		// Metódo pra SB
		// Verificar necessidade de lançar AghuNegocioException
		MpmProcedEspecialDiversoDAO dao = getMpmProcedEspecialDiversosDAO();
		List<MpmProcedEspecialDiversos> result = dao
				.buscaProcedEspecialDiversos(objPesquisa);
		if (result == null) {
			result = new ArrayList<MpmProcedEspecialDiversos>();
		}

		return result;
	}

	public List<MpmTipoModoUsoProcedimento> getListaTipoModoUsoProcedimento(
			Object objPesquisa, MpmProcedEspecialDiversos procedEspecial)
			throws ApplicationBusinessException {

		if (objPesquisa == null) {
			throw new ApplicationBusinessException(
					PrescreverProcedEspeciaisONExceptionCode.TIPO_MODO_PROCEDIMENTO_INVALIDO);
		}

		// Metódo pra Sb
		MpmTipoModoUsoProcedimentoDAO dao = getMpmTipoModoUsoProcedimentodDAO();
		List<MpmTipoModoUsoProcedimento> result = dao
				.buscaMpmTipoModoUsoProcedimento(objPesquisa, procedEspecial);
		if (result == null) {
			result = new ArrayList<MpmTipoModoUsoProcedimento>();
		}
		return result;
	}
	
	/**
	 * Busca <b>ScoMaterias</b> orteses e proteses conforme criterios necessarios para a tela de Procedimento Especiais.
	 * 
	 * @param paramVlNumerico
	 * @param objPesquisa
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ScoMaterial> getListaOrtesesProteses(
			BigDecimal paramVlNumerico, Object objPesquisa)
			throws ApplicationBusinessException {
		// Metódo pra SB
		if (paramVlNumerico == null) {
			throw new ApplicationBusinessException(
					PrescreverProcedEspeciaisONExceptionCode.GMT_CODIGO_INVALIDO);
		}

		List<ScoMaterial> result = this.getComprasFacade().obterMateriaisOrteseseProteses(
				paramVlNumerico, objPesquisa);
		if (result == null) {
			result = new ArrayList<ScoMaterial>();
		}
		return result;
	}

    public List<ScoMaterial> obterMateriaisOrteseseProtesesPrescricao(final BigDecimal paramVlNumerico,
                                                                      final String  objPesquisa) {

        return scoMaterialDAO.obterMateriaisOrteseseProtesesPrescricao(paramVlNumerico,objPesquisa);
    }

	public List<MbcProcedimentoCirurgicos> getListaProcedimentosRealizadosLeito(
			Object objPesquisa) {
		// Metódo pra SB
		IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();
		List<MbcProcedimentoCirurgicos> result = blocoCirurgicoFacade
				.buscaProcedimentoCirurgicosRealizadoLeito(objPesquisa);
		if (result == null) {
			result = new ArrayList<MbcProcedimentoCirurgicos>();
		}
		return result;
	}
	
	/**
	 * Busca um Prescricao Procedimento e os Modo Uso associados a ele.
	 * 
	 * @param idPrescProc
	 * @return
	 */
	public ProcedimentoEspecialVO buscaPrescricaoProcedimentoEspecialVOPorId(MpmPrescricaoProcedimentoId idPrescProc) {
		ProcedimentoEspecialVO procedimentoVO = new ProcedimentoEspecialVO();
		
		MpmPrescricaoProcedimento procedimento = this.getMpmPrescricaoProcedimentoDAO().obterPorChavePrimaria(idPrescProc);
		if(procedimento != null){
			procedimento.getDescricaoFormatada();
			procedimentoVO.setModel(procedimento);
			procedimentoVO.addAllModoUso(procedimento.getModoUsoPrescricaoProcedimentos());
		}
		
		return procedimentoVO;
	}
	
	/**
	 * Metodo para Inserir ou Alterar Prescricao Procedimento.
	 * 
	 * @param prescProc
	 * @param listaModoUsoParaExclusao
	 * @param tipo
	 * @throws ApplicationBusinessException
	 */
	public void gravarPrescricaoProcedimentoEspecial(MpmPrescricaoProcedimento prescProc, List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao, DominioTipoProcedimentoEspecial tipo, String nomeMicrocomputador, Boolean formChanged) throws BaseException {
		if (prescProc == null || prescProc.getPrescricaoMedica() == null || prescProc.getPrescricaoMedica().getId() == null) {
			throw new IllegalArgumentException("Parametros obrigatorios estao nulos!!!");
		}
		
		this.validaCamposObrigatorios(prescProc, tipo);
		
		MpmPrescricaoMedica aPrescricaoMedica = this.getPrescricaoMedicaDAO().obterPorChavePrimaria(prescProc.getPrescricaoMedica().getId());
		prescProc.setPrescricaoMedica(aPrescricaoMedica);
		prescProc.getPrescricaoMedica().setAtendimento(aPrescricaoMedica.getAtendimento());
		
		this.getPrescreverProcedimentoEspecialRN().verificaDuracaoJustificativaPrescricaoProcedimento(prescProc, aPrescricaoMedica);
		
		if (prescProc.getId() == null) {
			this.inserirPrescricaoProcedimentoEspecial(prescProc, aPrescricaoMedica, nomeMicrocomputador);
		} else {
			this.alterarPrescricaoProcedimentoEspecial(prescProc, aPrescricaoMedica, listaModoUsoParaExclusao, nomeMicrocomputador, formChanged);
		}
	}

	/**
	 * Valida campos obrigatórios da tela de procedimentos especiais.
	 * 
	 * @param {MpmPrescricaoProcedimento} prescProc
	 * @param {DominioTipoProcedimentoEspecial} tipo 
	 *  
	 */
	public void validaCamposObrigatorios(MpmPrescricaoProcedimento prescProc, DominioTipoProcedimentoEspecial tipo) throws ApplicationBusinessException {
		
		if (DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS.equals(tipo) && prescProc.getProcedimentoEspecialDiverso() == null) {
			
			throw new ApplicationBusinessException(PrescreverProcedEspeciaisONExceptionCode.ERRO_CAMPO_ESPECIAL_DIVERSAS_OBRIGATORIO);
			
		} else if (DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO.equals(tipo) && prescProc.getProcedimentoCirurgico() == null) {
			
			throw new ApplicationBusinessException(PrescreverProcedEspeciaisONExceptionCode.ERRO_CAMPO_PROCEDIMENTO_NO_LEITO_OBRIGATORIO);
			
		} else if (DominioTipoProcedimentoEspecial.ORTESES_PROTESES.equals(tipo) && prescProc.getMatCodigo() == null) {
			
			throw new ApplicationBusinessException(PrescreverProcedEspeciaisONExceptionCode.ERRO_CAMPO_ORTESES_OBRIGATORIO);
			
		}
		
	}
	
	/**
	 * Popula Dados padroes para a Inclusao de Procedimento Especial atraves do Prescricao Medica.
	 * 
	 * @param prescProc
	 * @param aPrescricaoMedica
	 */
	private void populaDadosInsercaoPrescricaoProcedimentoEspecial(MpmPrescricaoProcedimento prescProc, MpmPrescricaoMedica aPrescricaoMedica) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		boolean isVigente = aPrescricaoMedica.isPrescricaoMedicaVigente();
		if (isVigente) {
			prescProc.setDthrInicio(aPrescricaoMedica.getDthrMovimento());
		} else {
			prescProc.setDthrInicio(aPrescricaoMedica.getDthrInicio());			
		}
		prescProc.setDthrFim(aPrescricaoMedica.getDthrFim());
		prescProc.setCriadoEm(new Date());
		prescProc.setIndPendente(DominioIndPendenteItemPrescricao.P);
		prescProc.setAlteradoEm(null);
		prescProc.setServidor(servidorLogado);
		prescProc.setServidorMovimentado(null);		
	}
	
	private MpmPrescricaoProcedimento inserirPrescricaoProcedimentoEspecial(MpmPrescricaoProcedimento prescProc, MpmPrescricaoMedica aPrescricaoMedica, String nomeMicrocomputador) throws BaseException {
		this.populaDadosInsercaoPrescricaoProcedimentoEspecial(prescProc, aPrescricaoMedica);
		
		prescProc.setPrescricaoProcedimento(null);
		prescProc.setDigitacaoSolicitante(Boolean.TRUE);
		
		prescProc = this.getPrescreverProcedimentoEspecialRN().inserirPrescricaoProcedimento(prescProc, nomeMicrocomputador);
		
		for (MpmModoUsoPrescProced mpmModoUsoPrescProced : prescProc.getModoUsoPrescricaoProcedimentos()) {
			mpmModoUsoPrescProced.setPrescricaoProcedimento(prescProc);
			this.getManterModoUsoPrescProcedRN().inserirModoUsoPrescProced(mpmModoUsoPrescProced);
		}
		return prescProc;
	}
	
	public void inserirCargaPrescricaoProcedimentoEspecial(MpmPrescricaoProcedimento prescProc, 
			MpmPrescricaoMedica aPrescricaoMedica, String nomeMicrocomputador) throws BaseException {
		
		//this.verificaGravacaoPrescricaoProcedimento(prescProc, aPrescricaoMedica);
		
		List<MpmModoUsoPrescProced> modoUsos = prescProc.getModoUsoPrescricaoProcedimentos();
		prescProc.setModoUsoPrescricaoProcedimentos(null);
		
		prescProc.setIndPendente(DominioIndPendenteItemPrescricao.B);
		prescProc.setDigitacaoSolicitante(Boolean.TRUE);
		boolean isVigente = aPrescricaoMedica.isPrescricaoMedicaVigente();
		if (isVigente) {
			prescProc.setDthrInicio(aPrescricaoMedica.getDthrMovimento());
		} else {
			prescProc.setDthrInicio(aPrescricaoMedica.getDthrInicio());			
		}
		prescProc.setDthrFim(aPrescricaoMedica.getDthrFim());
		
		Date dMomento = GregorianCalendar.getInstance().getTime(); 
		prescProc.setCriadoEm(dMomento);
		prescProc.setAlteradoEm(null);
		//prescProc.setServidor(); ja foi setado na controller
		prescProc.setServidorMovimentado(null);
		prescProc.setPrescricaoProcedimento(null);
		prescProc.getPrescricaoMedica().setAtendimento(aPrescricaoMedica.getAtendimento());
		
		this.getPrescreverProcedimentoEspecialRN().inserirPrescricaoProcedimento(prescProc, nomeMicrocomputador);
		
		for (MpmModoUsoPrescProced mpmModoUsoPrescProced : modoUsos) {
			mpmModoUsoPrescProced.setPrescricaoProcedimento(prescProc);
			this.getManterModoUsoPrescProcedRN().inserirModoUsoPrescProced(mpmModoUsoPrescProced);
		}
		prescProc.setModoUsoPrescricaoProcedimentos(modoUsos);
		
	}
	
	private ManterModoUsoPrescProcedRN getManterModoUsoPrescProcedRN() {
		return manterModoUsoPrescProcedRN;
	}
    	
	private MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}
	
	private IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	/**
	 * Executa as logicas de Insercao, Remocao e Atualizacao de Modo Uso do Procedimento.
	 * 
	 * @param prescricaoProcedimento
	 * @param listaModoUsoParaExclusao
	 * @throws ApplicationBusinessException
	 */
	private void atualizaModoUsoPrescricao(
			MpmPrescricaoProcedimento prescricaoProcedimento,
			List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao) throws ApplicationBusinessException {
		
		MpmModoUsoPrescProcedDAO modoUsoDao = getMpmModoUsoPrescProcedDAO();

		// Remove itens
		for (ModoUsoProcedimentoEspecialVO modoUsoVO : listaModoUsoParaExclusao) {
			if (modoUsoVO.getItemId() != null) {
				MpmModoUsoPrescProced modoUso = modoUsoVO.getModel();
				prescricaoProcedimento.removerModoUso(modoUso);
				//this.getManterModoUsoPrescProcedRN().removerModoUsoPrescProced(modoUso);
			}
		}
		
		// Atualiza e Inserir itens
		for (MpmModoUsoPrescProced mpmModoUsoPrescProced : prescricaoProcedimento.getModoUsoPrescricaoProcedimentos()) {
			if (mpmModoUsoPrescProced.getId() == null) {
				mpmModoUsoPrescProced.setPrescricaoProcedimento(prescricaoProcedimento);
				this.getManterModoUsoPrescProcedRN().inserirModoUsoPrescProced(mpmModoUsoPrescProced);
			} else {
				modoUsoDao.merge(mpmModoUsoPrescProced);
				modoUsoDao.flush();
			}
			
		}
	}
	
	/**
	 * Executa as regras de atualizacao de Prescricao Procedimento.
	 * 
	 * @param prescricaoProcedimento
	 * @param aPrescricaoMedica
	 * @param listaModoUsoParaExclusao
	 * @throws ApplicationBusinessException
	 */
	private void alterarPrescricaoProcedimentoEspecial(MpmPrescricaoProcedimento prescricaoProcedimento
			, MpmPrescricaoMedica aPrescricaoMedica
			, List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao
			, String nomeMicrocomputador, Boolean formChanged) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MpmPrescricaoProcedimentoDAO procDao = getMpmPrescricaoProcedimentoDAO();
		
		List<DominioIndPendenteItemPrescricao> listaSetaServidor = Arrays.asList(DominioIndPendenteItemPrescricao.P
				, DominioIndPendenteItemPrescricao.B, DominioIndPendenteItemPrescricao.R);
				
		if (listaSetaServidor.contains(prescricaoProcedimento.getIndPendente())) {
			prescricaoProcedimento.setServidor(servidorLogado);
			atualizaModoUsoPrescricao(prescricaoProcedimento, listaModoUsoParaExclusao);
			//procDao.atualizar(prescricaoProcedimento);
			this.getPrescreverProcedimentoEspecialRN().atualizarPrescricaoProcedimento(prescricaoProcedimento, nomeMicrocomputador);
		} else if (DominioIndPendenteItemPrescricao.N.equals(prescricaoProcedimento.getIndPendente())) {
			boolean hasItensParaExclusao = (listaModoUsoParaExclusao != null && !listaModoUsoParaExclusao.isEmpty());
			boolean hasModificacao = this.getPrescreverProcedimentoEspecialRN().hasModificacao(prescricaoProcedimento, hasItensParaExclusao, formChanged);
			if (hasModificacao) {
				// PASSO 1 ####################
				// Registro novo.
				// A copia deve ser feita antes do Refresh.
				MpmPrescricaoProcedimento procNovo = this.copiarPrescricaoProcedimentoEspecial(prescricaoProcedimento, aPrescricaoMedica, listaModoUsoParaExclusao);
				
				// O refresh deve ser feito antes de qualquer Flush.
				MpmPrescricaoProcedimento procedimentoOriginal = procDao.obterProcedimentoPeloId(prescricaoProcedimento.getId().getAtdSeq(), prescricaoProcedimento.getId().getSeq());
				//MpmPrescricaoProcedimento procedimentoOriginal = prescricaoProcedimento;
				// Devido ao bug relatado abaixo no Jira do Hibernate
				//http://opensource.atlassian.com/projects/hibernate/browse/HHH-5133?page=com.atlassian.jira.plugin.system.issuetabpanels%3Aworklog-tabpanel
				// Vamos setar pra null a lista. O refresh recarrega a lista, apenas com os dados da base.
				procedimentoOriginal.setModoUsoPrescricaoProcedimentos(null);
				procDao.refresh(procedimentoOriginal);
				
				procNovo = this.inserirPrescricaoProcedimentoEspecial(procNovo, aPrescricaoMedica, nomeMicrocomputador);
				procNovo.setPrescricaoProcedimento(procedimentoOriginal);
				procDao.atualizar(procNovo);
				procDao.flush();
				
				/// PASSO 2 ####################
				//Atualiza o Registro.
				procedimentoOriginal.setIndPendente(DominioIndPendenteItemPrescricao.A);
				if (aPrescricaoMedica.isPrescricaoMedicaVigente()) {
					procedimentoOriginal.setDthrFim(aPrescricaoMedica.getDthrMovimento() != null ? aPrescricaoMedica.getDthrMovimento(): null);
				} else {
					procedimentoOriginal.setDthrFim(aPrescricaoMedica.getDthrInicio() != null ? aPrescricaoMedica.getDthrInicio(): null);
				}
				procedimentoOriginal.setAlteradoEm(new Date());
				procedimentoOriginal.setServidorMovimentado(servidorLogado);
				
				//procDao.atualizar(procedimentoOriginal);
				this.getPrescreverProcedimentoEspecialRN().atualizarPrescricaoProcedimento(procedimentoOriginal, nomeMicrocomputador);
			} else {
				throw new ApplicationBusinessException(PrescreverProcedEspeciaisONExceptionCode.NENHUMA_PROPRIEDDE_FOI_ALTERADA,Severity.WARN);
			}
		}
	}
	
	/**
	 * Copia os dados alterados na tela pelo usuario para um novo Procedimento Espeical.
	 * 
	 * @param prescricaoProcedimento
	 * @param aPrescricaoMedica
	 * @param listaModoUsoParaExclusao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmPrescricaoProcedimento copiarPrescricaoProcedimentoEspecial(
			MpmPrescricaoProcedimento prescricaoProcedimento,
			MpmPrescricaoMedica aPrescricaoMedica,
			List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao) throws ApplicationBusinessException {
		
		// Instancia o novo Procedimento com as associacoes basicas.
		MpmPrescricaoProcedimento novo = new MpmPrescricaoProcedimento();
		novo.setPrescricaoMedica(aPrescricaoMedica);
		novo.getPrescricaoMedica().setAtendimento(aPrescricaoMedica.getAtendimento());
		
		//Copia valores.
		novo.setDuracaoTratamentoSolicitado(prescricaoProcedimento.getDuracaoTratamentoSolicitado());
		novo.setJustificativa(prescricaoProcedimento.getJustificativa());
		novo.setInformacaoComplementar(prescricaoProcedimento.getInformacaoComplementar());
		novo.setQuantidade(prescricaoProcedimento.getQuantidade());
		novo.setProcedimentoEspecialDiverso(prescricaoProcedimento.getProcedimentoEspecialDiverso());
		novo.setMatCodigo(prescricaoProcedimento.getMatCodigo());
		novo.setProcedimentoCirurgico(prescricaoProcedimento.getProcedimentoCirurgico());
		
		// Prepara os Modo Uso para Insercao no Novo Procedimento.
		List<MpmModoUsoPrescProcedId> modoUsoIdExcluidos = new LinkedList<MpmModoUsoPrescProcedId>();
		for (ModoUsoProcedimentoEspecialVO modoUsoProcEspVO : listaModoUsoParaExclusao) {
			modoUsoIdExcluidos.add(modoUsoProcEspVO.getItemId());
		}
		for (MpmModoUsoPrescProced modoUsoPrescProced : prescricaoProcedimento.getModoUsoPrescricaoProcedimentos()) {
			if (modoUsoPrescProced.getId() == null || !modoUsoIdExcluidos.contains(modoUsoPrescProced.getId())) {
				MpmModoUsoPrescProced novoModoUso = new MpmModoUsoPrescProced();
				
				novoModoUso.setQuantidade(modoUsoPrescProced.getQuantidade());
				novoModoUso.setTipoModUsoProcedimento(modoUsoPrescProced.getTipoModUsoProcedimento());
				
				novo.addModoUso(novoModoUso);
			}
		}
		
		return novo;
	}
	
	private MpmModoUsoPrescProcedDAO getMpmModoUsoPrescProcedDAO() {
		return mpmModoUsoPrescProcedDAO;
	}
	
	/**
	 * Executa a regras de Exclusao de Prescricao Procedimento.
	 * 
	 * @param prescricaoProcedimento
	 * @throws ApplicationBusinessException
	 */
	public void excluirProcedimento(final MpmPrescricaoProcedimento prescricaoProcedimento)throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MpmPrescricaoProcedimentoDAO daoPrescProced = getMpmPrescricaoProcedimentoDAO();
		
		MpmPrescricaoProcedimento prescricaoProcedimentoOriginal = daoPrescProced.obterPorChavePrimaria(prescricaoProcedimento.getId());
		daoPrescProced.refresh(prescricaoProcedimentoOriginal);
		
		if (prescricaoProcedimentoOriginal.getDthrInicio()!= null) {
			MpmPrescricaoMedica  prescricaoMedica = prescricaoProcedimentoOriginal.getPrescricaoMedica();
			PrescreverProcedimentoEspecialRN prescreverProcedimentoEspecialRN = getPrescreverProcedimentoEspecialRN();
			Date dAlteracao = GregorianCalendar.getInstance().getTime();
			
			switch (prescricaoProcedimentoOriginal.getIndPendente()) {
				case B:
					prescreverProcedimentoEspecialRN.removerPrescricaoProcedimento(prescricaoProcedimentoOriginal);
				break;
				case P:
					MpmPrescricaoProcedimento prescProcedPai = prescricaoProcedimentoOriginal.getPrescricaoProcedimento();
					
					prescreverProcedimentoEspecialRN.removerPrescricaoProcedimento(prescricaoProcedimentoOriginal);
					
					if (prescProcedPai != null) {
						prescProcedPai.setIndPendente(DominioIndPendenteItemPrescricao.E);
						prescProcedPai.setServidorMovimentado(servidorLogado);					
						daoPrescProced.atualizar(prescProcedPai);
						daoPrescProced.flush();
						//this.getPrescreverProcedimentoEspecialRN().atualizarPrescricaoProcedimento(prescProcedPai);
					}
				break;
				case N:
					prescricaoProcedimentoOriginal.setIndPendente(DominioIndPendenteItemPrescricao.E);
					
					if (prescricaoMedica.isPrescricaoMedicaVigente()) {
						prescricaoProcedimentoOriginal.setDthrFim(prescricaoMedica.getDthrMovimento());		    	    
					} else {
						prescricaoProcedimentoOriginal.setDthrFim(prescricaoProcedimentoOriginal.getDthrInicio());
					}

					prescricaoProcedimentoOriginal.setAlteradoEm(dAlteracao);
					prescricaoProcedimentoOriginal.setServidorMovimentado(servidorLogado);
					daoPrescProced.atualizar(prescricaoProcedimentoOriginal);
					daoPrescProced.flush();
					//this.getPrescreverProcedimentoEspecialRN().atualizarPrescricaoProcedimento(prescricaoProcedimentoOriginal);
				break;
				case R:						   
					prescricaoProcedimentoOriginal.setIndPendente(DominioIndPendenteItemPrescricao.Y);
					//Recebe valor da dthr_inicio do item prescrito
					//prescricaoProcedimentoOriginal.setDthrFim();
					prescricaoProcedimentoOriginal.setAlteradoEm(dAlteracao);
					prescricaoProcedimentoOriginal.setServidorMovimentado(servidorLogado);
					
					daoPrescProced.atualizar(prescricaoProcedimentoOriginal);
					daoPrescProced.flush();
					//this.getPrescreverProcedimentoEspecialRN().atualizarPrescricaoProcedimento(prescricaoProcedimentoOriginal);
				break;
			}
			
		} //if dtHrInicio	
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}