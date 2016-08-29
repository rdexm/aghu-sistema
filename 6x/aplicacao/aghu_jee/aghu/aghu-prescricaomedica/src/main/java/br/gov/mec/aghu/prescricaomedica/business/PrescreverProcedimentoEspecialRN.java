package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.prescricaomedica.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.business.PrescreverProcedimentosEspeciaisON.PrescreverProcedEspeciaisONExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.ObjetosOracleException;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * @author rcorvalao
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength", "PMD.JUnit4TestShouldUseTestAnnotation"})
@Stateless
public class PrescreverProcedimentoEspecialRN extends BaseBusiness {

	@EJB
	private ManterModoUsoPrescProcedRN manterModoUsoPrescProcedRN;
	
	@EJB
	private ManterAltaSumarioRN manterAltaSumarioRN;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;
	
	private static final Log LOG = LogFactory.getLog(PrescreverProcedimentoEspecialRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;
	
	@Inject
	private MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1137714722707370724L;

	public enum PrescreverProcedEspeciaisRNExceptionCode implements
			BusinessExceptionCode {
		ERRO_REMOVER_PROCEDIMENTO, AHD_00090, AIN_00268, MPM_01083, MPM_01175, MPM_03603, MPM_03604, MPM_01120, MPM_02424, MPM_02425, MPM_02426

		// Prescrição procedimento está pendente
		 ,MPM_01096
		 
		// TIPO_MODO_PROCEDIMENTO_INVALIDO, MPM_01340, AGH_00183,
		// ATENDIMENTO_COM_ESTE_CODIGO_NAO_CADASTRADO,
		// AGH_00199, E_OBRIGATORIO_INFORMAR_QUANTIDADE,
		, MPM_02186;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}
	}

	/**
	 * ORADB Procedure MPMK_PPR_RN.RN_PPRP_VER_JUST.
	 * 
	 * Operação: UPD Descrição: Verificar se o convênio exige justificativa para
	 * o procedimento.
	 * 
	 * bsoliveira 26/10/2010
	 * 
	 * @param descricaoProcedimento
	 * 
	 * @param {Integer} matCodigo codigo de ScoMateriais.
	 * @param {Integer} novoPciSeq seq de MbcProcedimentoCirurgicos.
	 * @param {Short} novoPedSeq seq de MpmProcedEspecialDiversos.
	 * @param {String} descricaoProcedimento.
	 * @throws ApplicationBusinessException
	 */
	public void verificarConvenioExigeJustificativaParaProcedimento(
			Integer matCodigo, Integer novoPciSeq, Short novoPedSeq,
			String descricaoProcedimento) throws BaseException {

		if (matCodigo != null || novoPciSeq != null || novoPedSeq != null) {

			String descricao = StringUtils.isNotBlank(descricaoProcedimento) ? descricaoProcedimento
					.substring(0, descricaoProcedimento.length() - 1) : "";

			throw new ApplicationBusinessException(
					PrescreverProcedEspeciaisRNExceptionCode.MPM_01120,
					descricao);

		}

	}


    public void removerModoDeUso(
            List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao) throws BaseException {
        for (ModoUsoProcedimentoEspecialVO mupp : listaModoUsoParaExclusao) {
            this.getManterModoUsoPrescProcedRN()
                    .removerModoUsoPrescProced(mupp.getModel());
        }
    }

	/**
	 * ORADB Trigger MPMT_PPR_BRD, BEFORE DELETE ON MPM_PRESCRICAO_PROCEDIMENTOS
	 * 
	 * 
	 * @author mtocchetto
	 * @author rcorvalao
	 * @throws ApplicationBusinessException
	 */
	public void removerPrescricaoProcedimento(
			MpmPrescricaoProcedimento prescricaoProcedimento)
			throws ApplicationBusinessException {
		try {
			if (DominioIndPendenteItemPrescricao.N
					.equals(prescricaoProcedimento.getIndPendente())) {
				// ORADB Procedure MPMK_PPR_RN.RN_PPRP_VER_DELECAO
				PrescreverProcedEspeciaisRNExceptionCode.MPM_01083
						.throwException();
			}

			// Remover filhos
			this.excluirModoUsoPrescProced(prescricaoProcedimento);

			// Desativado a pedido do Vacaro.
			// MpmPrescricaoProcedimentoId id = procedimentoOriginal.getId();
			// deletarUsoOrdProcedimento(id.getAtdSeq(), id.getSeq());
			if(mpmModoUsoPrescProcedDAO.isOracle()){
				mpmkPprRnRnPprpDelUopProt(prescricaoProcedimento.getId().getAtdSeq(), prescricaoProcedimento.getId().getSeq());
			}

			prescricaoProcedimento.getModoUsoPrescricaoProcedimentos().clear();

			MpmPrescricaoProcedimentoDAO dao = getMpmPrescricaoProcedimentoDAO();
			dao.remover(prescricaoProcedimento);
			dao.flush();
		} catch (Exception e) {
			logError(e.getMessage(), e);
			PrescreverProcedEspeciaisRNExceptionCode.ERRO_REMOVER_PROCEDIMENTO
					.throwException(e);
		}
	}

	private void excluirModoUsoPrescProced(
			MpmPrescricaoProcedimento prescricaoProcedimento)
			throws ApplicationBusinessException {
		// Set<MpmModoUsoPrescProced> listModoUsoPrescProced =
		// prescricaoProcedimento.getModoUsoPrescricaoProcedimentos();
		MpmModoUsoPrescProcedDAO dao = getMpmModoUsoPrescProcedDAO();
		List<MpmModoUsoPrescProced> listModoUsoPrescProced = dao
				.pesquisarModoUsoPrescProcedimentosPorID(prescricaoProcedimento);
		// dao.buscaModoUsoPrescProcedimentos(prescricaoProcedimento.getId());

		for (MpmModoUsoPrescProced mupp : listModoUsoPrescProced) {
			this.getManterModoUsoPrescProcedRN()
					.removerModoUsoPrescProced(mupp);
		}
		dao.flush();
	}
	
	/**
	 * Por motivos de implantação junto ao HCPA, decidiu-se acionar (quando oracle) 
	 * a chamada a procedure diretamente no Oracle. 
	 * 
	 * ORADB Procedure MPMK_PPR_RN.RN_PPRP_DEL_UOP_PROT.
	 * @param seq 
	 * @param atdSeq 
	 */
	private void mpmkPprRnRnPprpDelUopProt(final Integer atdSeq, final Long seq) throws ApplicationBusinessException{
		try {
			getAghuFacade().mpmkPprRnRnPprpDelUopProt(atdSeq, seq);
		} catch (ObjetosOracleException e) {
			throw new ApplicationBusinessException(e.getCode(), e);
		}
	}

	/**
	 * 
	 * @param prescProced
	 */
	public void verificarAlteracaoDataAlteradoEm(
			MpmPrescricaoProcedimento prescProced) {
		PrescricaoProcedimentoVo vo = this.getMpmPrescricaoProcedimentoDAO()
				.buscaPrescricaoProcedimentoVo(prescProced);

		DominioIndPendenteItemPrescricao oldIndPendente = vo.getIndPendente();
		Date oldDthrFim = vo.getDthrFim();

		if (CoreUtil.modificados(oldDthrFim, prescProced.getDthrFim())
				&& prescProced.getDthrFim() != null) {
			if (DominioIndPendenteItemPrescricao.P != prescProced.getIndPendente()
					|| DominioIndPendenteItemPrescricao.P != oldIndPendente) {
				prescProced.setAlteradoEm(new Date());
			}
		}
	}

	/**
	 * Verifica se ocorreu modificacao em um dos seguintes campos:<br>
	 * pci_seq <code>MbcProcedimentoCirurgicos</code>,<br>
	 * ped_seq <code>MpmProcedEspecialDiversos</code>,<br>
	 * mat_codigo <code>ScoMateriais</code>.<br>
	 * 
	 * @ORADB package mpmk_ppr_rn.rn_pprp_ver_alt_arco
	 * 
	 * @param prescProc
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void verificarAlteracaoDoArco(MpmPrescricaoProcedimento prescProc)
			throws BaseException {
		PrescricaoProcedimentoVo vo = this.getMpmPrescricaoProcedimentoDAO()
				.buscaPrescricaoProcedimentoVo(prescProc);

		Integer oldPciSeq = (vo.getPciSeq() != null) ? vo.getPciSeq().getSeq()
				: null;
		Short oldPedSeq = (vo.getPedSeq() != null) ? vo.getPedSeq().getSeq()
				: null;
		Integer oldMatCodigo = (vo.getMatCodigo() != null) ? vo.getMatCodigo()
				.getCodigo() : null;

		Integer newPciSeq = (prescProc.getProcedimentoCirurgico() != null) ? prescProc
				.getProcedimentoCirurgico().getSeq() : null;
		Short newPedSeq = (prescProc.getProcedimentoEspecialDiverso() != null) ? prescProc
				.getProcedimentoEspecialDiverso().getSeq() : null;
		Integer newMatCodigo = (prescProc.getMatCodigo() != null) ? prescProc
				.getMatCodigo().getCodigo() : null;

		if (CoreUtil.modificados(oldPciSeq, newPciSeq)
				|| CoreUtil.modificados(oldPedSeq, newPedSeq)
				|| CoreUtil.modificados(oldMatCodigo, newMatCodigo)) {
			throw new ApplicationBusinessException(
					PrescreverProcedEspeciaisRNExceptionCode.MPM_02186);
		}
	}

	protected MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO() {
		return mpmPrescricaoProcedimentoDAO;
	}

	private MpmModoUsoPrescProcedDAO getMpmModoUsoPrescProcedDAO() {
		return mpmModoUsoPrescProcedDAO;
	}

	private ManterModoUsoPrescProcedRN getManterModoUsoPrescProcedRN() {
		return manterModoUsoPrescProcedRN;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	/**
	 * ORADB procedure MPMP_VER_DURACAO_TRAT
	 * 
	 * ORADB package mpmk_rn.rn_mpmp_ver_int ORADB package
	 * mpmk_rn.rn_mpmp_ver_atu ORADB package mpmk_rn.rn_mpmp_ver_hod
	 * 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void verificarDuracaoTratamentoSolicitado(
			MpmPrescricaoProcedimento prescProc, AghAtendimentos atendimento)
			throws BaseException {
		if (prescProc == null || atendimento == null) {
			throw new IllegalArgumentException("Parametros Invalidos!!!");
		}

		Byte cpgCphCspSeq = null;
		Short cpgCphCspCnvCodigo = null;
		Integer phiSeq = null;

		// ORADB package mpmk_rn.rn_mpmp_ver_int
		if (atendimento.getInternacao() != null) {
			if (atendimento.getInternacao().getConvenioSaudePlano() != null) {
				cpgCphCspSeq = atendimento.getInternacao()
						.getConvenioSaudePlano().getId().getSeq();
				cpgCphCspCnvCodigo = atendimento.getInternacao()
						.getConvenioSaudePlano().getId().getCnvCodigo();
			} else {
				throw new ApplicationBusinessException(
						PrescreverProcedEspeciaisRNExceptionCode.AIN_00268);
			}
		} else
		// ORADB package mpmk_rn.rn_mpmp_ver_atu
		if (atendimento.getAtendimentoUrgencia() != null) {
			if (atendimento.getAtendimentoUrgencia().getConvenioSaude() != null) {
				cpgCphCspSeq = atendimento.getAtendimentoUrgencia().getCspSeq();
				cpgCphCspCnvCodigo = atendimento.getAtendimentoUrgencia()
						.getConvenioSaude().getCodigo();
			} else {
				throw new ApplicationBusinessException(
						PrescreverProcedEspeciaisRNExceptionCode.MPM_01175);
			}
		} else
		// ORADB package mpmk_rn.rn_mpmp_ver_hod
		if (atendimento.getHospitalDia() != null) {
			if (atendimento.getHospitalDia().getSeq() != null
					&& atendimento.getHospitalDia().getConvenioSaudePlano() != null) {
				cpgCphCspSeq = atendimento.getHospitalDia().getSeq()
						.byteValue();
				cpgCphCspCnvCodigo = atendimento.getHospitalDia()
						.getConvenioSaudePlano().getId().getCnvCodigo();
			} else {
				throw new ApplicationBusinessException(
						PrescreverProcedEspeciaisRNExceptionCode.AHD_00090);
			}
		}

		
		FatProcedHospInternos procedHospitalarInterno = this
				.buscaProcedimentoHospitalarInterno(prescProc);
		if (procedHospitalarInterno != null) {
			phiSeq = procedHospitalarInterno.getSeq();
		}

		if (cpgCphCspSeq == null && cpgCphCspCnvCodigo == null
				&& phiSeq == null) {
			throw new IllegalStateException(
					"Alguma das associacoes necessarias nao foi encontrada!!!!");
		}

		FatConvGrupoItemProced umFatConvGrupoItensProced = this
				.getFaturamentoFacade()
				.obterFatConvGrupoItensProcedPeloId(cpgCphCspCnvCodigo,
						cpgCphCspSeq, phiSeq);
		if (umFatConvGrupoItensProced != null) {

			if(prescProc.getId() != null){
				this.getMpmPrescricaoProcedimentoDAO().merge(prescProc);
			}
			String descricao = StringUtils.isNotBlank(prescProc
					.getDescricaoFormatada()) ? prescProc
					.getDescricaoFormatada().substring(0,
							prescProc.getDescricaoFormatada().length() - 1)
					: "";

			if (prescProc.getDuracaoTratamentoSolicitado() == null) {
				if (umFatConvGrupoItensProced
						.getIndInformaTempoTrat()) {
					throw new ApplicationBusinessException(
							PrescreverProcedEspeciaisRNExceptionCode.MPM_03603,
							descricao);
				}
			} else {
				if (!umFatConvGrupoItensProced
						.getIndInformaTempoTrat()) {
					throw new ApplicationBusinessException(
							PrescreverProcedEspeciaisRNExceptionCode.MPM_03604);
				}
			}
		}
	}
	
	/**
	 * Metodo para verificar se ocorreu modificacao para o Objeto MpmPrescricaoProcedimento.<br>
	 * NAO executa load da Entidade MpmPrescricaoProcedimento.<br>
	 * Compara os campos que podem ser alterados pelo Usuario,
	 * na tela de Manter Prescricao Procedimento.<br>
	 * O parametro <b>prescricaoProcedimento</b> deve ser informado e deve existir no banco de dados.<br> 
	 * O parametro <b>hasModificacao</b> informa se existe algum indicativo externo de modificação.<br>
	 * 
	 * @param prescricaoProcedimento
	 * @param hasModificacao
	 * @return retorna <code>true</code> se o indicador externo de modificacao <b>hasModificacao</b> for <code>true</code>
	 * ou se algum campo foi alterado. E <code>false</code> para os outros casos.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean hasModificacao(MpmPrescricaoProcedimento prescProc, boolean hasModificacao, Boolean formChanged) {
		if (prescProc == null) {
			throw new IllegalArgumentException("Parametro Invalido: deve ser informado uma entidade.");
		}
		if (prescProc.getId() == null) {
			throw new IllegalStateException("A entidade nao possui Id, nao pode ser feito teste de modificacao.");
		}
		if (hasModificacao || formChanged) {
			return true;
		}
		
		PrescricaoProcedimentoVo vo = this.getMpmPrescricaoProcedimentoDAO().buscaPrescricaoProcedimentoVo(prescProc);
		
		if (vo.getPedSeq() != null 
				&& (prescProc.getProcedimentoEspecialDiverso() == null || !vo.getPedSeq().equals(prescProc.getProcedimentoEspecialDiverso()))
			) {
			return true;
		}
		if (vo.getPciSeq() != null 
				&& (prescProc.getProcedimentoCirurgico() == null || !vo.getPciSeq().equals(prescProc.getProcedimentoCirurgico()))
			) {
			return true;
		}
		if (vo.getMatCodigo() != null 
				&& (prescProc.getMatCodigo() == null || !vo.getMatCodigo().equals(prescProc.getMatCodigo()))
			) {
			return true;
		}
		
		if (StringUtil.modificado(vo.getJustificativa(), prescProc.getJustificativa())) {
			return true;
		}
		if (CoreUtil.modificados(vo.getDuracaoTratamentoSolicitado(), prescProc.getDuracaoTratamentoSolicitado())) {
			return true;
		}
		if (StringUtil.modificado(vo.getInformacaoComplementar(), prescProc.getInformacaoComplementar())) {
			return true;
		}
		if (CoreUtil.modificados(vo.getQuantidade(), prescProc.getQuantidade())) {
			return true;
		}
		
		List<MpmModoUsoPrescProced> modoUsoLista = prescProc.getModoUsoPrescricaoProcedimentos();
		MpmModoUsoPrescProcedDAO dao = getMpmModoUsoPrescProcedDAO();
		List<ModoUsoPrescProcedVO> listModoUsoDB = dao.buscaModoUsoPrescProcedimentoVos(prescProc);
		for (MpmModoUsoPrescProced modoUsoPrescProced : modoUsoLista) {
			if (modoUsoPrescProced.getId() == null) {
				// Item adicionado.
				return true;
			} else {
				// Item existente, pode ter sido modificado.
				for (ModoUsoPrescProcedVO modoUsoVO : listModoUsoDB) {
					if (modoUsoPrescProced.getId().equals(modoUsoVO.getId())) {
						if (CoreUtil.modificados(modoUsoVO.getTipoModUsoProcedimento(), modoUsoPrescProced.getTipoModUsoProcedimento())) {
							return true;
						}
						if (CoreUtil.modificados(modoUsoVO.getQuantidade(), modoUsoPrescProced.getQuantidade())) {
							return true;
						}
					}
				}// FOR dos modos de uso do bando de dados.
			}
		}//FOR do modos de usos da tela
		
		return false;
	}

	/**
	 * Busca um FatProcedHospInternos conforme o atributo informado em
	 * MpmPrescricaoProcedimento<br>
	 * ORADB package MPMK_RN.RN_MPMP_VER_PROC_HOS<br>
	 * 
	 * @return um <code>MpmPrescricaoProcedimento</code>
	 */
	public FatProcedHospInternos buscaProcedimentoHospitalarInterno(
			MpmPrescricaoProcedimento prescProc) {
		if (prescProc == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		if (prescProc.getMatCodigo() == null && prescProc.getProcedimentoCirurgico() == null
				&& prescProc.getProcedimentoEspecialDiverso() == null) {
			throw new IllegalArgumentException(
					"Parametro Invalido: um dos parametros nao deveria ser nulo!");
		}

		Integer matCodigo = null;
		Integer pciSeq = null;
		Short pedSeq = null;

		if (prescProc.getMatCodigo() != null) {
			matCodigo = prescProc.getMatCodigo().getCodigo();
		}
		if (prescProc.getProcedimentoCirurgico() != null) {
			pciSeq = prescProc.getProcedimentoCirurgico().getSeq();
		}
		if (prescProc.getProcedimentoEspecialDiverso() != null) {
			pedSeq = prescProc.getProcedimentoEspecialDiverso().getSeq();
		}

		FatProcedHospInternos returnValue = null;
		List<FatProcedHospInternos> list = this.getFaturamentoFacade()
				.buscaProcedimentoHospitalarInterno(matCodigo, pciSeq, pedSeq);
		if (list != null && !list.isEmpty()) {
			returnValue = list.get(0);
		}

		return returnValue;
	}

	public MpmPrescricaoProcedimento inserirPrescricaoProcedimento(
			MpmPrescricaoProcedimento prescProc, String nomeMicrocomputador) throws BaseException {
		return inserirPrescricaoProcedimento(prescProc, false, nomeMicrocomputador);
	}

	public MpmPrescricaoProcedimento inserirPrescricaoProcedimento(
			MpmPrescricaoProcedimento prescProc, Boolean isCopiado, String nomeMicrocomputador) throws BaseException {
		this.preInsertPrescricaoProcedimento(prescProc, isCopiado, nomeMicrocomputador);
		return this.getMpmPrescricaoProcedimentoDAO().inserir(prescProc);
	}

	/**
	 * ORADB MPMT_PPR_BRI
	 * 
	 * @param prescProc
	 * @throws ApplicationBusinessException
	 */
	private void preInsertPrescricaoProcedimento(
			MpmPrescricaoProcedimento prescProc, Boolean isCopiado, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (!isCopiado){
			this.verificaJustificativaPrescricaoProcedimentoInsercao(prescProc);			
		}

		this.verificaAtendimento(prescProc.getDthrInicio(),
				prescProc.getDthrFim(),
				// prescProc.getAtendimento().getSeq(),
				prescProc.getPrescricaoMedica().getId().getAtdSeq(), null,
				null, null);
		this.getPrescricaoMedicaRN().verificaPrescricaoMedica(
				prescProc.getPrescricaoMedica().getId().getAtdSeq(),
				prescProc.getDthrInicio(), prescProc.getDthrFim(),
				prescProc.getCriadoEm(), prescProc.getIndPendente(), "I", nomeMicrocomputador, 
				new Date());

		prescProc.setServidor(servidorLogado);
		prescProc.setCriadoEm(new Date());
	}

	/**
	 * Atualiza o registro de Prescricao Procedimento, executando as validacoes
	 * necessarias.
	 * 
	 * @param prescProc
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public MpmPrescricaoProcedimento atualizarPrescricaoProcedimento(
			MpmPrescricaoProcedimento prescProc, String nomeMicrocomputador) throws BaseException {
		prescProc = this.getMpmPrescricaoProcedimentoDAO().merge(prescProc);
		this.validarAlteracaoProcedimento(prescProc, nomeMicrocomputador, new Date());
		final MpmPrescricaoProcedimento original = this.getMpmPrescricaoProcedimentoDAO().obterOriginal(prescProc.getId());
		MpmPrescricaoProcedimento mpmpp = this.getMpmPrescricaoProcedimentoDAO().atualizar(prescProc);
		this.getMpmPrescricaoProcedimentoDAO().flush();
		mpmpEnforcePprRules(mpmpp, original);
		
		return mpmpp;
	}

	/**
	 * Faz toda a verificacao da MPMT_PPR_BRU.
	 * 
	 * @ORADB Trigger MPMT_PPR_BRU
	 * 
	 * @param prescricaoProcedimento
	 * @throws ApplicationBusinessException
	 */
	public void validarAlteracaoProcedimento(
			MpmPrescricaoProcedimento prescricaoProcedimento, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		if (prescricaoProcedimento == null
				|| prescricaoProcedimento.getId() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}

		prescricaoProcedimento = getMpmPrescricaoProcedimentoDAO().merge(prescricaoProcedimento);
		
		// PASSO 1: Não permitida alteração do arco
		this.verificarAlteracaoDoArco(prescricaoProcedimento);

		// PASSO 2: a rotina de data auditing foi alterada para o seguinte
		// código
		this.verificarAlteracaoDataAlteradoEm(prescricaoProcedimento);

		// PASSO 3: Verifica vigência do atendimento
		// Busca endereço do convênio e primeiro e último evento da internação,
		// hospital dia ou atend. urgência que originou o atendimento.
		// Verificar se o convênio exige justificativa para o procedimento
		this.verificaJustificativaPrescricaoProcedimentoAlteracao(prescricaoProcedimento);

		PrescricaoProcedimentoVo vo = this.getMpmPrescricaoProcedimentoDAO()
				.buscaPrescricaoProcedimentoVo(prescricaoProcedimento);

		// PASSO 4: Atualização dthr_mvto_pendente da prescrição médica
		verificarAtualizarDataMovimentoPendentePrescricaoMedica(
				vo.getDthrFim(), prescricaoProcedimento, nomeMicrocomputador, dataFimVinculoServidor);

		// PASSO 5: Ao ser informada dthr_fim, atualizar
		// ser_vin_codigo_movimentado e ser_matricula_movimentado
		verificarAtualizarServidorMovimentado(vo.getDthrFim(),
				prescricaoProcedimento);

		// PASSO 6: Dados do servidor que altera
		verificarDadosServidor(vo.getDthrFim(), prescricaoProcedimento);
	}

	

	/**
	 * ORADB: Procedure: MPMP_ENFORCE_PPR_RULES
	 *  
	 */
	private void mpmpEnforcePprRules(final  MpmPrescricaoProcedimento prescricao, final  MpmPrescricaoProcedimento prescricaoOld) throws ApplicationBusinessException{
		
		// Nao pode ser relacionada a uma PRESCRICAO_PROCEDIMENTO que estiver pendente.  (auto-relacionamento)
		if(prescricao.getPrescricaoProcedimento() != null
				&& prescricao.getPrescricaoProcedimento().getId() != null
				&& prescricao.getPrescricaoProcedimento().getId().getAtdSeq() != null
				&& CoreUtil.modificados(prescricao.getPrescricaoProcedimento().getId().getAtdSeq(), prescricaoOld.getPrescricaoProcedimento().getId().getAtdSeq())){
			rnPprpVerPrcrPro(prescricao);
		}
		
		/*
		   if (aghk_util.modificados (l_ppr_saved_row.ppr_atd_seq, l_ppr_row_new.ppr_atd_seq))
		      and   l_ppr_row_new.ppr_atd_seq is not null then
		             mpmk_ppr_rn.rn_pprp_ver_prcr_pro(l_ppr_row_new.ppr_atd_seq, l_ppr_row_new.ppr_seq);
		   end if;
		 */
	}
	
	/**
	 * ORADB: mpmk_ppr_rn.rn_pprp_ver_prcr_pro
	 *  
	 */
	private void rnPprpVerPrcrPro(final  MpmPrescricaoProcedimento prescricao) throws ApplicationBusinessException{
		
		// Nao pode ser relacionada a uma PRESCRICAO_PROCEDIMENTO que estiver pendente.  (auto-relacionamento) 
		if(DominioIndPendenteItemPrescricao.P.equals(prescricao.getIndPendente())){
			// Prescrição procedimento está pendente
			PrescreverProcedEspeciaisRNExceptionCode.MPM_01096.throwException();	
		} 
	}

	/*
	 * // @ORADB MPMT_PPR_BRU public void
	 * validarAlteracaoProcedimento(MpmPrescricaoProcedimento
	 * prescricaoProcedimento) throws ApplicationBusinessException { if
	 * (prescricaoProcedimento == null) { throw new
	 * IllegalArgumentException("Parametros Invalidos!!!"); }
	 * 
	 * MpmPrescricaoProcedimentoDAO dao = getMpmPrescricaoProcedimentoDAO();
	 * MpmPrescricaoProcedimento procedimentoOriginal =
	 * dao.obterProcedimentoOriginal(prescricaoProcedimento); AghAtendimentoDAO
	 * daoAtd = getAghAtendimentoDAO();
	 * 
	 * AghAtendimentos atendimento =
	 * daoAtd.obterAtendimentoPeloSeq(prescricaoProcedimento
	 * .getId().getAtdSeq()); if (atendimento == null) { throw new
	 * IllegalArgumentException("Parametros Invalidos!!!"); }
	 * 
	 * // Verificar se o convênio exige justificativa para o procedimento se a
	 * // coluna ind_pendente for diferente de 'B'// /* IF :new.ind_pendente <>
	 * 'B' THEN IF (aghk_util.modificados(:old.justificativa,
	 * :new.justificativa) AND :new.justificativa IS NULL) OR
	 * (aghk_util.modificados(:old.ind_pendente, :new.ind_pendente) AND
	 * :old.ind_pendente = 'B' AND :new.justificativa IS NULL) THEN /
	 * 
	 * if(!DominioIndPendenteItemPrescricao.B.equals(prescricaoProcedimento.
	 * getIndPendente())) { if (procedimentoOriginal.getJustificativa() !=
	 * prescricaoProcedimento.getJustificativa() &&
	 * StringUtils.isBlank(prescricaoProcedimento.getJustificativa()) ||
	 * procedimentoOriginal.getIndPendente() !=
	 * prescricaoProcedimento.getIndPendente() &&
	 * !DominioIndPendenteItemPrescricao
	 * .B.equals(procedimentoOriginal.getIndPendente())) { Byte cpgCphCspSeq =
	 * null; Short cpgCphCspCnvCodigo = null; Integer phiSeq = null; // ORADB
	 * package mpmk_rn.rn_mpmp_ver_int if (atendimento.getInternacao() != null)
	 * { if (atendimento.getInternacao().getConvenioSaudePlano() != null) {
	 * cpgCphCspSeq =
	 * atendimento.getInternacao().getConvenioSaudePlano().getId().getSeq();
	 * cpgCphCspCnvCodigo =
	 * atendimento.getInternacao().getConvenioSaudePlano().getId
	 * ().getCnvCodigo(); } else { throw new
	 * ApplicationBusinessException(PrescreverProcedEspeciaisRNExceptionCode.AIN_00268);
	 * } } else // ORADB package mpmk_rn.rn_mpmp_ver_atu if
	 * (atendimento.getAtendimentoUrgencia() != null) { if
	 * (atendimento.getAtendimentoUrgencia().getConvenioSaude() != null) {
	 * cpgCphCspSeq = atendimento.getAtendimentoUrgencia() .getCspSeq();
	 * cpgCphCspCnvCodigo = atendimento
	 * .getAtendimentoUrgencia().getConvenioSaude() .getCodigo(); } else { throw
	 * new ApplicationBusinessException(
	 * PrescreverProcedEspeciaisRNExceptionCode.MPM_01175); } } else // ORADB
	 * package mpmk_rn.rn_mpmp_ver_hod if (atendimento.getHospitalDia() != null)
	 * { if (atendimento.getHospitalDia().getSeq() != null &&
	 * atendimento.getHospitalDia().getConvenioSaudePlano() != null) {
	 * cpgCphCspSeq = atendimento.getHospitalDia().getSeq() .byteValue();
	 * cpgCphCspCnvCodigo = atendimento.getHospitalDia()
	 * .getConvenioSaudePlano().getId().getCnvCodigo(); } else { throw new
	 * ApplicationBusinessException(
	 * PrescreverProcedEspeciaisRNExceptionCode.AHD_00090); } }
	 * 
	 * /* Se foram modificados as colunas pci_seq, ped_seq ou mat_codigo exibe
	 * mensagem de erro “Procedimento cirúrgico,material ou procedimento
	 * especial não podem ser alterados. Encerre e inclua nova prescrição.”Não
	 * permitida alteração do arco / if (procedimentoOriginal.getPciSeq() ==
	 * null && prescricaoProcedimento.getPciSeq() != null ||
	 * procedimentoOriginal.getPciSeq() != null &&
	 * (prescricaoProcedimento.getPciSeq() != procedimentoOriginal.getPciSeq())
	 * && (prescricaoProcedimento.getPedSeq() !=
	 * procedimentoOriginal.getPedSeq()) &&
	 * (prescricaoProcedimento.getMatCodigo() !=
	 * procedimentoOriginal.getMatCodigo())) { throw new
	 * ApplicationBusinessException(PrescreverProcedEspeciaisRNExceptionCode
	 * .ERRO_NAO_PERMITIDA_ALTERACAO_DO_ARCO); }
	 * 
	 * // Se data início da MPM_PRESCRICAO_PROCEDIMENTOS for diferente // de
	 * nulo verifica a vigência da prescrição médica if
	 * (prescricaoProcedimento.getDthrFim() != null) {
	 * 
	 * this.verificaAtendimento( prescricaoProcedimento.getDthrInicio(),
	 * prescricaoProcedimento.getDthrFim(),
	 * prescricaoProcedimento.getPrescricaoMedica().getId().getAtdSeq(), null,
	 * null, null); }
	 * 
	 * if (StringUtils.isBlank(procedimentoOriginal.getJustificativa()) &&
	 * !procedimentoOriginal
	 * .getJustificativa().equalsIgnoreCase(prescricaoProcedimento
	 * .getJustificativa())) { // Busca indicadores do convênio
	 * FatProcedHospInternos procedHospitalarInterno = new
	 * FatProcedHospInternos(); procedHospitalarInterno =
	 * this.buscaProcedimentoHospitalarInterno(prescricaoProcedimento);
	 * FatConvGrupoItensProcedDAO daoConv = getFatConvGrupoItensProcedDAO();
	 * 
	 * FatConvGrupoItensProced convenio =
	 * daoConv.obterFatConvGrupoItensProcedPeloId(cpgCphCspCnvCodigo,
	 * cpgCphCspSeq, phiSeq);
	 * 
	 * if (convenio != null && convenio.getIndExigeJustificativa()) {
	 * this.verificarConvenioExigeJustificativaParaProcedimento
	 * (prescricaoProcedimento.getMatCodigo().getCodigo(),
	 * prescricaoProcedimento.getPciSeq().getSeq(),
	 * prescricaoProcedimento.getPedSeq().getSeq()); } } } }
	 * 
	 * if (prescricaoProcedimento.getDthrFim() != null &&
	 * !procedimentoOriginal.getDthrFim
	 * ().equals(prescricaoProcedimento.getDthrFim())) { // Atualização
	 * dthr_mvto_pendente da prescrição médica String operacao = "U";
	 * BuscaPrescricaoMedicaVO buscaPrescricaoMedicaVO = buscaPrescricaoMedica(
	 * prescricaoProcedimento.getId().getAtdSeq(),
	 * prescricaoProcedimento.getDthrInicio(),
	 * prescricaoProcedimento.getDthrFim(), operacao,
	 * prescricaoProcedimento.getIndPendente()); if
	 * (buscaPrescricaoMedicaVO.getDataHoraInicio() == null &&
	 * buscaPrescricaoMedicaVO.getDataHoraFim() == null &&
	 * buscaPrescricaoMedicaVO.getSituacao() == null) { throw new
	 * ApplicationBusinessException(PrescreverProcedEspeciaisRNExceptionCode.MPM_01104);
	 * } if (buscaPrescricaoMedicaVO.getDataHoraMovimentoPendente() == null &&
	 * DominioIndPendenteItemPrescricao
	 * .N.equals(prescricaoProcedimento.getIndPendente())) { MpmPrescricaoMedica
	 * prescMed =
	 * getMpmPrescricaoMedicaDAO().obterPorChavePrimaria(buscaPrescricaoMedicaVO
	 * .getSeqPrescricaoMedica()); if (prescMed != null) {
	 * prescMed.setDthrInicioMvtoPendente(buscaPrescricaoMedicaVO
	 * .getDataHoraMovimento() != null ? buscaPrescricaoMedicaVO
	 * .getDataHoraMovimento() : null); if (prescMed.getDthrInicioMvtoPendente()
	 * != null) { try { getMpmPrescricaoMedicaDAO().merge(prescMed);
	 * getMpmPrescricaoMedicaDAO().flush(); } catch (Exception e) { throw new
	 * ApplicationBusinessException(PrescreverProcedEspeciaisRNExceptionCode.MPM_01311);
	 * // Erro update MPM_PRESCRICAO_MEDICAS na // MPMK_RN.RN_MPM_VER_PR_MD_UP.
	 * Contate GSIS. } } } } }
	 * 
	 * // @ORADB MPMT_PPR_BRU.RN_PPR_ATU_SER_DTFN /* RN_PPR_ATU_SER_DTFN * / /*
	 * Ao ser informada dthr_fim, atualizar ser_vin_codigo e * / /*
	 * ser_matricula responsável pela alteração * / if
	 * ((procedimentoOriginal.getDthrFim() == null &&
	 * prescricaoProcedimento.getDthrInicio() != null) &&
	 * prescricaoProcedimento.getDthrFim() != null) { //
	 * prescricaoProcedimento.setServidorMovimentado(get) get servidor // logado
	 * prescricaoProcedimento.setServidor(getServidorLogado()); /* IF
	 * (:old.dthr_fim IS NULL AND :new.dthr_fim IS NOT NULL) AND :new.dthr_fim
	 * IS NOT NULL THEN
	 * mpmk_ppr_rn.rn_pprp_atu_ser_dtfn(:new.ser_matricula_movimentado,
	 * :new.ser_vin_codigo_movimentado); END IF; / }
	 * 
	 * MpmPrescricaoProcedimento procedimentoAutoRelacionado =
	 * dao.obterProcedimentoPeloId(
	 * prescricaoProcedimento.getAtendimento().getSeq() ,
	 * prescricaoProcedimento.getId().getSeq()); if (procedimentoAutoRelacionado
	 * != null &&
	 * DominioIndPendenteItemPrescricao.B.equals(procedimentoAutoRelacionado
	 * .getIndPendente())) { // 'MPM-01096' Prescrição procedimento está
	 * pendente throw new
	 * ApplicationBusinessException(PrescreverProcedEspeciaisRNExceptionCode
	 * .PRESCRICAO_PROCEDIMENTO_ESTA_PENDENTE); } /*
	 * 
	 * @ORADB RN_MUPP_VER_TP_US_PR tipo mod uso procedimento deve estar ativo se
	 * ind exige quantidade = S, quantidade é obrigatória ind_pendente = S Luiz
	 * Adriano 10/06/99 / // MpmTipoModoUsoProcedimentoDAO daoModoUso = //
	 * getMpmTipoModoUsoProcedimentoDAO(); // MpmTipoModoUsoProcedimento modoUso
	 * = //
	 * daoModoUso.obterTipoModoUsoProcedimentoPeloId(prescricaoProcedimento.
	 * getPedSeq(), // seqp); //
	 * if(!DominioSituacao.A.equals(modoUso.getIndSituacao())){ //
	 * //raise_application_error('-20000','MPM-00277'); // }else
	 * if(modoUso.getIndExigeQuantidade().equals('S')){ //
	 * if(prescricaoProcedimento.getQuantidade()==null || //
	 * prescricaoProcedimento.getQuantidade() == 0){ // //É obrigatório informar
	 * quantidade. // throw new ApplicationBusinessException( //
	 * PrescreverProcedEspeciaisRNExceptionCode
	 * .E_OBRIGATORIO_INFORMAR_QUANTIDADE); // } // }
	 * 
	 * }// FIM VALIDAR ALTERAÇÃO
	 */

	/**
	 * Verificacoes de obrigatoridade dos campos Justificativa e Duracao
	 * Solicitado do Procedimento Especial. Metodo utilizado para validacao na
	 * tela de procedimento Inclusao e Alteracao.
	 * 
	 * @param prescProc
	 */
	public void verificaDuracaoJustificativaPrescricaoProcedimento(
			MpmPrescricaoProcedimento prescProc,
			MpmPrescricaoMedica aPrescricaoMedica) throws BaseException {
		if (prescProc == null || aPrescricaoMedica == null) {
			throw new IllegalArgumentException("Paramtro Invalido!!!");
		}
		this.verificarExigenciaJustificativa(prescProc);
		this.verificarDuracaoTratamentoSolicitado(prescProc,
				aPrescricaoMedica.getAtendimento());
	}

	/**
	 * @ORADB Trigger MPMT_PPR_BRI
	 * 
	 * @param prescProc
	 * @throws BaseException
	 */
	private void verificaJustificativaPrescricaoProcedimentoInsercao(
			MpmPrescricaoProcedimento prescProc) throws BaseException {
		if (prescProc == null) {
			throw new IllegalArgumentException("Paramtro Invalido!!!");
		}
		// if :new.ind_pendente not in ( 'B','P')
		List<DominioIndPendenteItemPrescricao> listIndPendente = Arrays.asList(
				DominioIndPendenteItemPrescricao.B,
				DominioIndPendenteItemPrescricao.P);
		if (!listIndPendente.contains(prescProc.getIndPendente())) {
			this.verificarExigenciaJustificativa(prescProc);
		}
	}

	/**
	 * 
	 * @ORADB MPMT_PPR_BRU
	 * 
	 * @param prescProc
	 * @throws BaseException
	 */
	private void verificaJustificativaPrescricaoProcedimentoAlteracao(
			MpmPrescricaoProcedimento prescProc) throws BaseException {
		if (prescProc == null) {
			throw new IllegalArgumentException("Paramtro Invalido!!!");
		}

		// IF :new.ind_pendente <> 'B' THEN
		// IF (aghk_util.modificados(:old.justificativa, :new.justificativa)
		// AND :new.justificativa IS NULL)
		// OR (aghk_util.modificados(:old.ind_pendente, :new.ind_pendente)
		// AND :old.ind_pendente = 'B'
		// AND :new.justificativa IS NULL)
		if (DominioIndPendenteItemPrescricao.B != prescProc.getIndPendente()) {
			PrescricaoProcedimentoVo vo = this
					.getMpmPrescricaoProcedimentoDAO()
					.buscaPrescricaoProcedimentoVo(prescProc);
			// Devido a problema com esta logica o IF foi modifcado.
			if ((CoreUtil.modificados(vo.getJustificativa(),
					prescProc.getJustificativa()) && StringUtils
					.isBlank(prescProc.getJustificativa())) || (
			// CoreUtil.modificados(vo.getJustificativa(),
			// prescProc.getJustificativa()) &&
					vo.getIndPendente() == DominioIndPendenteItemPrescricao.B && StringUtils
							.isBlank(prescProc.getJustificativa()))) {
				this.verificarExigenciaJustificativa(prescProc);
			}
		}// if indPendente B
	}

	/**
	 * Procedure PROCEDURE MPMP_VER_EXIGE_JUSTIF, encontrada no arquivo
	 * MPMF_PRCR_PROCED_ESP.pll.
	 * 
	 * @author mtocchetto
	 * @throws ApplicationBusinessException
	 */
	private void verificarExigenciaJustificativa(
			MpmPrescricaoProcedimento prescricaoProcedimento)
			throws BaseException {
		Short codigoConvenioSaude = null; // convenioSaudePlano.id.cnvCodigo
		Byte seqConvenioSaudePlano = null; // convenioSaudePlano.id.seq

		ScoMaterial material = prescricaoProcedimento.getMatCodigo();
		MbcProcedimentoCirurgicos procedimentoCirurgico = prescricaoProcedimento
				.getProcedimentoCirurgico();
		MpmProcedEspecialDiversos procedEspecialDiverso = prescricaoProcedimento
				.getProcedimentoEspecialDiverso();

		PrescricaoMedicaRN prescricaoMedicaRN = getPrescricaoMedicaRN();

		AghAtendimentos atendimento = prescricaoProcedimento
				.getPrescricaoMedica().getAtendimento();

		if (atendimento == null) {
			throw new ApplicationBusinessException(
					PrescreverProcedEspeciaisONExceptionCode.MPM_02396);
		}

		/*
		 * Busca endereço do convênio e primeiro e último evento da internação,
		 * hospital dia ou atend. urgência que originou o atendimento.
		 */
		if (atendimento.getInternacao() != null) {
			AinInternacao internacao = atendimento.getInternacao();
			BuscaDadosInternacaoVO buscaDadosInternacaoVO = prescricaoMedicaRN
					.buscaDadosInternacao(internacao.getSeq());

			codigoConvenioSaude = buscaDadosInternacaoVO
					.getCodigoConvenioSaude();
			seqConvenioSaudePlano = buscaDadosInternacaoVO
					.getSeqConvenioSaudePlano();
		} else if (atendimento.getAtendimentoUrgencia() != null) {
			AinAtendimentosUrgencia atendimentoUrgencia = atendimento
					.getAtendimentoUrgencia();
			BuscaDadosAtendimentoUrgenciaVO buscaDadosAtendimentoUrgenciaVO = prescricaoMedicaRN
					.buscaDadosAtendimentoUrgencia(atendimentoUrgencia.getSeq());

			codigoConvenioSaude = buscaDadosAtendimentoUrgenciaVO
					.getCodigoConvenioSaude();
			seqConvenioSaudePlano = buscaDadosAtendimentoUrgenciaVO.getCspSeq();
		} else if (atendimento.getHospitalDia() != null) {
			AhdHospitaisDia hospitalDia = atendimento.getHospitalDia();
			BuscaDadosHospitalDiaVO buscaDadosHospitalDiaVO = prescricaoMedicaRN
					.buscaDadosHospitalDia(hospitalDia.getSeq());

			codigoConvenioSaude = buscaDadosHospitalDiaVO
					.getCodigoConvenioSaude();
			seqConvenioSaudePlano = buscaDadosHospitalDiaVO.getCspSeq();
		}

		// Verificar se o convênio exige justificativa para o procedimento
		if (StringUtils.isBlank(prescricaoProcedimento.getJustificativa())) {
			Integer seqProcedHospInterno = null; // FatProcedHospInternos.seq

			// Busca indicadores do convênio
			if (material != null) {
				seqProcedHospInterno = prescricaoMedicaRN
						.verificaProcedimentoHospitalar(material.getCodigo(),
								null, null);
			} else if (procedimentoCirurgico != null) {
				seqProcedHospInterno = prescricaoMedicaRN
						.verificaProcedimentoHospitalar(null,
								procedimentoCirurgico.getSeq(), null);
			} else if (procedEspecialDiverso != null) {
				seqProcedHospInterno = prescricaoMedicaRN
						.verificaProcedimentoHospitalar(null, null,
								procedEspecialDiverso.getSeq());
			}

			VerificaIndicadoresConvenioInternacaoVO verificaIndicadoresConvenioInternacaoVO = null;
			verificaIndicadoresConvenioInternacaoVO = prescricaoMedicaRN
					.verificaIndicadoresConvenioInternacao(codigoConvenioSaude,
							seqConvenioSaudePlano, seqProcedHospInterno);

			if (verificaIndicadoresConvenioInternacaoVO != null) {
				if (verificaIndicadoresConvenioInternacaoVO
						.getIndExigeJustificativa()) {

					if (material != null) {
						this.verificarConvenioExigeJustificativaParaProcedimento(
								material.getCodigo(), null, null,
								prescricaoProcedimento.getDescricaoFormatada());
					} else if (procedimentoCirurgico != null) {
						this.verificarConvenioExigeJustificativaParaProcedimento(
								null, procedimentoCirurgico.getSeq(), null,
								prescricaoProcedimento.getDescricaoFormatada());
					} else if (procedEspecialDiverso != null) {
						this.verificarConvenioExigeJustificativaParaProcedimento(
								null, null, procedEspecialDiverso.getSeq(),
								prescricaoProcedimento.getDescricaoFormatada());
					}
				}
			}
		}
	}

	protected PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_ATD_PRCR<br>
	 * 
	 * Regras: RNI5 E RNI6<br>
	 * Verifica se o atendimento é valido.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaAtendimento(Date dataHoraInicio, Date dataHoraFim,
			Integer seqAtendimento, Integer seqHospitalDia,
			Integer seqInternacao, Integer seqAtendimentoUrgencia)
			throws ApplicationBusinessException {

		this.getPrescricaoMedicaRN().verificaAtendimento(dataHoraInicio,
				dataHoraFim, seqAtendimento, seqHospitalDia, seqInternacao,
				seqAtendimentoUrgencia);

	}

	/**
	 * ORADB Procedure MPMK_RN.MPMP_TESTA_EM_USO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void testaEmUso(DominioIndPendenteItemPrescricao pendente,
			Date dataHoraFim, String operacao,
			DominioSituacaoPrescricao situacao, Date dataHoraMovimento,
			Date dataHoraFimPrescricaoMedica, int tSeq)
			throws ApplicationBusinessException {
		boolean testar = true;

		if ((pendente == null
				|| DominioIndPendenteItemPrescricao.D.equals(pendente) || DominioIndPendenteItemPrescricao.N
				.equals(pendente))
				|| (dataHoraFim != null && new Date().after(dataHoraFim))
				|| ("U".equals(operacao)
						&& DominioIndPendenteItemPrescricao.P.equals(pendente) && dataHoraFim != null)
				|| ("U".equals(operacao)
						&& (DominioIndPendenteItemPrescricao.E.equals(pendente) || DominioIndPendenteItemPrescricao.A
								.equals(pendente)) && dataHoraFim
						.equals(dataHoraFimPrescricaoMedica))) {
			testar = false;
		}

		if (testar) {
			if (!DominioSituacaoPrescricao.U.equals(situacao)
					|| dataHoraMovimento == null) {
				if (tSeq == 1) {
					throw new ApplicationBusinessException(
							PrescreverProcedEspeciaisRNExceptionCode.MPM_02424);
				} else if (tSeq == 2) {
					throw new ApplicationBusinessException(
							PrescreverProcedEspeciaisRNExceptionCode.MPM_02425);
				} else if (tSeq == 3) {
					throw new ApplicationBusinessException(
							PrescreverProcedEspeciaisRNExceptionCode.MPM_02426);
				}
			}
		}
	}

	/**
	 * @ORADB Implementa parte de trigger MPMT_PPR_BRU.
	 * 
	 *        Atualização dthr_mvto_pendente da prescrição médica.
	 * 
	 * @param atualDthrFim
	 * @param novoPrescricaoProcedimento
	 * @throws ApplicationBusinessException
	 */
	public void verificarAtualizarDataMovimentoPendentePrescricaoMedica(
			Date atualDthrFim,
			MpmPrescricaoProcedimento novoPrescricaoProcedimento, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		if (CoreUtil.modificados(novoPrescricaoProcedimento.getDthrFim(),
				atualDthrFim)
				&& novoPrescricaoProcedimento.getDthrFim() != null) {

			getPrescricaoMedicaRN().verificaPrescricaoMedicaUpdate(
					novoPrescricaoProcedimento.getId().getAtdSeq(),
					novoPrescricaoProcedimento.getDthrInicio(),
					novoPrescricaoProcedimento.getDthrFim(),
					novoPrescricaoProcedimento.getAlteradoEm(),
					novoPrescricaoProcedimento.getIndPendente(), "U",
					nomeMicrocomputador,
					dataFimVinculoServidor);

		}

	}

	/**
	 * @ORADB Implementa parte de trigger MPMT_PPR_BRU e
	 *        mpmk_ppr_rn.rn_pprp_atu_ser_dtfn.
	 * 
	 *        Ao ser informada dthr_fim, atualizar ser_vin_codigo_movimentado e
	 *        ser_matricula_movimentado.
	 * 
	 *        Seta no servidor por referencia.
	 * 
	 * @param atualDthrFim
	 * @param novoDthrFim
	 * @param novoPrescricaoProcedimento
	 * @throws ApplicationBusinessException
	 */
	public void verificarAtualizarServidorMovimentado(Date atualDthrFim,
			MpmPrescricaoProcedimento novoPrescricaoProcedimento)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (atualDthrFim == null
				&& novoPrescricaoProcedimento.getDthrFim() != null) {
			novoPrescricaoProcedimento.setServidorMovimentado(servidorLogado);
			getAltaSumarioRN().validaServidorLogado();

		}

	}

	/**
	 * @ORADB Implementa parte de trigger MPMT_PPR_BRU.
	 * 
	 *        Verifica dados do servidor. Se data fim antiga for diferente da
	 *        data de fim nova então Se matricula for nula sobe exceção.
	 * 
	 * @param atualDthrFim
	 * @param novoPrescricaoProcedimento
	 * @throws ApplicationBusinessException
	 */
	public void verificarDadosServidor(Date atualDthrFim,
			MpmPrescricaoProcedimento novoPrescricaoProcedimento)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (CoreUtil.modificados(novoPrescricaoProcedimento.getDthrFim(),
				atualDthrFim)) {

			novoPrescricaoProcedimento.setServidor(servidorLogado);
			getAltaSumarioRN().validaServidorLogado();

		}

	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
