package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialDiversoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialRmDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ProcedEspecialDiversoRN extends BaseBusiness {


	@EJB
	private ProcedEspecialRmRN procedEspecialRmRN;
	
	@EJB
	private TipoModoUsoProcedimentoRN tipoModoUsoProcedimentoRN;
	
	private static final Log LOG = LogFactory.getLog(ProcedEspecialDiversoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MpmProcedEspecialRmDAO mpmProcedEspecialRmDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MpmTipoModoUsoProcedimentoDAO mpmTipoModoUsoProcedimentoDAO;
	
	@Inject
	private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;

	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
 
	private static final long serialVersionUID = 121324429376440867L;

	public enum ManterProcedEspecialDiversoRNExceptionCode implements BusinessExceptionCode {
		MPM_00774,
		MPM_00680,
		MPM_00681,
		FAT_00152,
		ERRO_ATUALIZAR_PROCEDIMENTO_ESPECIAL,
		ERRO_REMOVER_PROCEDIMENTO_ESPECIAL
		, MPM_PROCED_ESPECIAL_DIVERSOS_MPM_PED_CK4,
		ERRO_EXCLUSAO_PROCEDIMENTO_ESPECIAL_DEPENDENCIAS;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	public MpmProcedEspecialDiversos inserir(MpmProcedEspecialDiversos procedimento, 
			List<MpmTipoModoUsoProcedimento> modosUsos, List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRm) throws BaseException {
		
		final RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		procedimento.setServidor(servidorLogado);
		
		//Regras pré-insert procedimento
		preInsertProcedEspecialDiverso(procedimento);
		
		//Insert procedimento
		 getMpmProcedEspecialDiversoDAO().persistir(procedimento);		

		 Short seqP = Short.valueOf("0");
		//Atribui ao procedimento as lista de objetos filhos atualizados com o id do objeto inserido
		for(MpmTipoModoUsoProcedimento modoUso : modosUsos) {
			modoUso.getId().setSeqp(++seqP);
			modoUso.getId().setPedSeq(procedimento.getSeq());
			getTipoModoUsoProcedimentoRN().inserir(modoUso, servidorLogado);
		}
		
		for(MpmProcedEspecialRm material : materiaisMpmProcedEspecialRm) {
			material.getId().setPedSeq(procedimento.getSeq());
			getProcedEspecialRmRN().inserir(material, servidorLogado);
		}
		
		procedimento = getMpmProcedEspecialDiversoDAO().obterPorChavePrimaria(procedimento.getSeq());
		
		//Regras pós-insert procedimento
		posInsertProcedEspecialDiverso(procedimento);
		
		return procedimento;
	}

	public MpmProcedEspecialDiversos atualizar( MpmProcedEspecialDiversos procedimento, 
												List<MpmTipoModoUsoProcedimento> modosUsos, List<MpmTipoModoUsoProcedimento> modosUsosExcluidos, 
												List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRm,
												List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidos) throws BaseException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		procedimento.setServidor(servidorLogado);
		
		//Regras pré-update
		preUpdateProcedEspecialDiverso(procedimento);
		
		try {
			//Update
			procedimento = getMpmProcedEspecialDiversoDAO().merge(procedimento);
		} catch(PersistenceException e) {
			logError("Exceção capturada: ", e);
	    	throw new ApplicationBusinessException(ManterProcedEspecialDiversoRNExceptionCode.ERRO_ATUALIZAR_PROCEDIMENTO_ESPECIAL);
	    } catch(BaseRuntimeException e) {
	    	throw new ApplicationBusinessException(e.getCode());
	    }
		
		processaModosUso(procedimento, modosUsos, modosUsosExcluidos, servidorLogado);
		
		processaMateriais(procedimento, materiaisMpmProcedEspecialRm, materiaisMpmProcedEspecialRmExcluidos, servidorLogado);
		
		return procedimento;
	}

	private void processaModosUso(  MpmProcedEspecialDiversos procedimento, 
								    List<MpmTipoModoUsoProcedimento> modosUsos, List<MpmTipoModoUsoProcedimento> modosUsosExcluidos,
									RapServidores servidorLogado) throws BaseException, ApplicationBusinessException {
		
		//Modos de Uso
		Short seqP = getMpmTipoModoUsoProcedimentoDAO().obterMaxSeqP(procedimento.getSeq());
		for(MpmTipoModoUsoProcedimento modoUso : modosUsos) {
			if(modoUso.getCriadoEm() == null) {
				modoUso.getId().setSeqp(++seqP);
				getTipoModoUsoProcedimentoRN().inserir(modoUso, servidorLogado);
				
			} else {
				getTipoModoUsoProcedimentoRN().alterar(modoUso, servidorLogado);
			}
		}
		
		//Verifica os modos de uso do banco de dados que foram deletados
		for(MpmTipoModoUsoProcedimento modoUso : modosUsosExcluidos) {
			MpmTipoModoUsoProcedimento modoUsoBD = getMpmTipoModoUsoProcedimentoDAO().obterPorChavePrimaria(modoUso.getId());
			getTipoModoUsoProcedimentoRN().remover(modoUsoBD);
		}
	}

	private void processaMateriais(final MpmProcedEspecialDiversos procedimento, final List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRm,
			final List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidos, RapServidores servidorLogado) throws BaseException {
		
		for(MpmProcedEspecialRm material : materiaisMpmProcedEspecialRm) {
			if(material.getCriadoEm() == null) {
				getProcedEspecialRmRN().inserir(material, servidorLogado);
			} else {
				getProcedEspecialRmRN().atualizar(material, servidorLogado);
			}
		}

		for (MpmProcedEspecialRm mpmProcedEspecialRm : materiaisMpmProcedEspecialRmExcluidos) {
			MpmProcedEspecialRm mpmProcedEspecialRmBD = getMpmProcedEspecialRmDAO().obterPorChavePrimaria(mpmProcedEspecialRm.getId());
			getProcedEspecialRmRN().remover(mpmProcedEspecialRmBD);
		}
	}
	
	public void remover(Short codigo) throws BaseException {
		MpmProcedEspecialDiversos procedimento = getMpmProcedEspecialDiversoDAO().obterPeloId(codigo);
		
		if(procedimento != null) {
			List<FatProcedHospInternos> listaProcedHospInternos = getFaturamentoFacade().pesquisarPorProcedimentoEspecialDiverso(procedimento);
			if (!listaProcedHospInternos.isEmpty()){
				//Remove os procedimentos hospitalares internos
				try{
					for (FatProcedHospInternos proced: listaProcedHospInternos){
						this.getFaturamentoFacade().removerProcedimetoHospitalarInterno(proced);
					}					
				}
				catch (PersistenceException e) {
					throw new ApplicationBusinessException(
							ManterProcedEspecialDiversoRNExceptionCode.ERRO_EXCLUSAO_PROCEDIMENTO_ESPECIAL_DEPENDENCIAS,e);
				}
			}
			
			List<MamProcedimento> listaProcedimentos = getAmbulatorioFacade().pesquisarProcedimentosComProcedEspecialDiverso(procedimento.getSeq());
			if (listaProcedimentos.size() > 0){
				throw new ApplicationBusinessException(ManterProcedEspecialDiversoRNExceptionCode.ERRO_EXCLUSAO_PROCEDIMENTO_ESPECIAL_DEPENDENCIAS);
			}
			
			//Regras pré-delete
			preDeleteProcedEspecialDiverso(procedimento);
			
			try {

				for(MpmTipoModoUsoProcedimento modoUso : procedimento.getModosProcedimentosPrescricaoDeProcedimentos()) {
					getTipoModoUsoProcedimentoRN().remover(modoUso);
				}
				
				for(MpmProcedEspecialRm material : procedimento.getMateriaisDaRequisicaoPrescricaoDeProcedimentos()){
					getProcedEspecialRmRN().remover(material);
				}
				
				//Delete
				getMpmProcedEspecialDiversoDAO().remover(procedimento);
				
				// TODO Regras pós-delete NÃO forma implementadas, por isso comentei 
				//posDeleteProcedEspecialDiverso(procedimento);
			} catch(PersistenceException e) {
				logError("Exceção capturada: ", e);
		    	throw new ApplicationBusinessException(ManterProcedEspecialDiversoRNExceptionCode.ERRO_REMOVER_PROCEDIMENTO_ESPECIAL);
		    }
		}
	}
	
	
	/**
	 * @ORADB Trigger MPMT_PED_BRI
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException
	 */
	protected void preInsertProcedEspecialDiverso(MpmProcedEspecialDiversos procedimento) throws BaseException {
		//Atualiza data de criação do procedimento
		procedimento.setCriadoEm(new Date());
		
		// CONSTRAINT MPM_PED_CK4
		if ((Boolean.TRUE.equals(procedimento.getPrescricaoCaraterVascular()) && Boolean.FALSE
				.equals(procedimento.getPermitePrescricao()))) {
			throw new BaseRuntimeException(
					ManterProcedEspecialDiversoRNExceptionCode.MPM_PROCED_ESPECIAL_DIVERSOS_MPM_PED_CK4);
		}
	}
	
	/**
	 * @ORADB Trigger MPMT_PED_BRU
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException
	 */
	protected void preUpdateProcedEspecialDiverso(MpmProcedEspecialDiversos procedimento) throws BaseException {
		
		//Verifica se a descrição é diferente da já existente
		MpmProcedEspecialDiversos procedimentoOld = getMpmProcedEspecialDiversoDAO().obterPeloId(procedimento.getSeq());
		if(!procedimento.getDescricao().trim().equalsIgnoreCase(procedimentoOld.getDescricao().trim())) {
			ManterProcedEspecialDiversoRNExceptionCode.MPM_00774.throwException();
		}
		
		// CONSTRAINT MPM_PED_CK4
		if ((Boolean.TRUE.equals(procedimento.getPrescricaoCaraterVascular()) && Boolean.FALSE.equals(procedimento.getPermitePrescricao()))) {
			throw new ApplicationBusinessException(ManterProcedEspecialDiversoRNExceptionCode.MPM_PROCED_ESPECIAL_DIVERSOS_MPM_PED_CK4);
		}
		
	}
	
	/**
	 * @ORADB Trigger MPMT_PED_BRD
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException
	 */
	protected void preDeleteProcedEspecialDiverso(MpmProcedEspecialDiversos procedimento) throws ApplicationBusinessException {
		validarDiasPermitidosDelecao(procedimento.getCriadoEm());
	}
	
	/**
	 * @ORADB Procedure MPMK_RN.RN_MPMP_VER_DEL
	 * 
	 * @param dataInicial A data inicial do intervalo para verificação do prazo para deleção
	 * @throws ApplicationBusinessException
	 */
	public void validarDiasPermitidosDelecao(Date dataInicial) throws ApplicationBusinessException {
		//Verifica e obtém o parâmetro

		int limite = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);
		
		//Verifica se o período para deletar é válido
		int diasDesdeCriacao = DateUtil.diffInDaysInteger(new Date(), dataInicial);
		if(diasDesdeCriacao > limite) {
			ManterProcedEspecialDiversoRNExceptionCode.MPM_00681.throwException();
		}
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	/**
	 * @ORADB Trigger MPMT_PED_ASI
	 * 
	 * @param procedimento
	 * @throws ApplicationBusinessException
	 */
	protected void posInsertProcedEspecialDiverso(MpmProcedEspecialDiversos procedimento) throws ApplicationBusinessException {
		//Insere registro em FatProcedHospInternos 
		criarFatProcedHospInternos(procedimento);
	}

	
	/**
	 * @ORADB Procedure FATK_PHI_RN.RN_PHIP_ATU_INSERT
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void criarFatProcedHospInternos(MpmProcedEspecialDiversos procedimento) throws ApplicationBusinessException {
		FatProcedHospInternos fatProcedHospInternos = new FatProcedHospInternos();
		fatProcedHospInternos.setMaterial(null);
		fatProcedHospInternos.setProcedimentoCirurgico(null);
		fatProcedHospInternos.setProcedimentoEspecialDiverso(procedimento);
		fatProcedHospInternos.setCsaCodigo(null);
		fatProcedHospInternos.setPheCodigo(null);
		
		String descricaoSubstring = null;
		if(StringUtils.isNotBlank(procedimento.getDescricao())) {
			if(procedimento.getDescricao().length() > 100) {
				descricaoSubstring = procedimento.getDescricao().substring(0,100);
			} else {
				descricaoSubstring = procedimento.getDescricao();
			}
		}
		fatProcedHospInternos.setDescricao(descricaoSubstring);
		fatProcedHospInternos.setSituacao(procedimento.getIndSituacao());
		fatProcedHospInternos.setIndOrigemPresc(true);
		fatProcedHospInternos.setIndUtilizaKit(true);
		
		try {
			getFaturamentoFacade().inserirFatProcedHospInternos(fatProcedHospInternos);
		} catch(ApplicationBusinessException e) {
			ManterProcedEspecialDiversoRNExceptionCode.FAT_00152.throwException();
		}
	}
	
	/**
	 * @ORADB Procedure FATK_PHI_RN.RN_PHIP_ATU_INSERT
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void criarFatProcedHospInternos(Short euuSeq, String descricao, DominioSituacao situacao) throws ApplicationBusinessException {
		FatProcedHospInternos fatProcedHospInternos = new FatProcedHospInternos();

		fatProcedHospInternos.setEuuSeq(euuSeq);
		fatProcedHospInternos.setDescricao(descricao);
		fatProcedHospInternos.setSituacao(situacao);
		
		try {
			getFaturamentoFacade().inserirFatProcedHospInternos(fatProcedHospInternos);
		} catch(ApplicationBusinessException e) {
			ManterProcedEspecialDiversoRNExceptionCode.FAT_00152.throwException();
		}
	}
	
	protected MpmProcedEspecialDiversoDAO getMpmProcedEspecialDiversoDAO() {
		return mpmProcedEspecialDiversoDAO;
	}
	
	protected TipoModoUsoProcedimentoRN getTipoModoUsoProcedimentoRN() {
		return tipoModoUsoProcedimentoRN;
	}
	
	protected ProcedEspecialRmRN getProcedEspecialRmRN() {
		return procedEspecialRmRN;
	}
	
	protected MpmTipoModoUsoProcedimentoDAO getMpmTipoModoUsoProcedimentoDAO() {
		return mpmTipoModoUsoProcedimentoDAO;
	}
	
	protected MpmProcedEspecialRmDAO getMpmProcedEspecialRmDAO() {
		return mpmProcedEspecialRmDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
