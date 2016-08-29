package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.AnuTipoItemDietasJn;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpaCadOrdItemNutricoes;
import br.gov.mec.aghu.model.MpaUsoOrdItemNutricoes;
import br.gov.mec.aghu.model.MpmItemDietaSumario;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.nutricao.dao.AnuTipoItemDietaDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AnuTipoItemDietaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class TiposDietaCRUD extends BaseBusiness {

	private static final String PARAMETRO_OBRIGATORIO = "Parâmetro obrigatório";

	private static final Log LOG = LogFactory.getLog(TiposDietaCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade iPrescricaoMedicaFacade;
	
	@EJB
	private IFaturamentoFacade iFaturamentoFacade;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@EJB
	private IParametroFacade iParametroFacade;
	
	@EJB
	private IAghuFacade iAghuFacade;
	
	@EJB
	private INutricaoFacade iNutricaoFacade;
	
	@Inject
	private AnuTipoItemDietaDAO anuTipoItemDietaDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5468660448863989916L;

	protected enum TiposDietaCRUDExceptionCode implements
			BusinessExceptionCode {
		ANU_00117,AFA_00172,AFA_00173,FREQUENCIA_COM_TIPO_OBRIGATORIA_INVALIDA, TIPO_PACIENTE_OBRIGATORIO,
		MPM_IPD_TID_FK1,MPA_CRN_TID_FK1,MPA_UIN_TID_FK1,MPM_IMD_TID_FK1,MPM_IDS_TID_FK1, ANU_TIU_TID_FK1, CALCULO_NUMERO_VEZES_24HRS_INVALIDO;
	}

	public void persistirTiposDieta(AnuTipoItemDieta tipoDieta, List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs)
			throws ApplicationBusinessException {
		
		preInserirUpdateTiposDieta(tipoDieta);

		if (tipoDieta.getSeq() == null) {
			preInserirTiposDieta(tipoDieta);
			getNutricaoFacade().inserirAnuTipoItemDieta(tipoDieta);
			
			posInserirItemDieta(tipoDieta);
						
			persistirTiposDietaUnfs(tipoDieta, listaAnuTipoItemDietaUnfs);

			
		} else {
			// anuk_tid_rn.RN_TIDP_VER_ALTERA
			preUpdateTiposDieta(tipoDieta);

			tipoDieta.setTipoDietaUnidadeFuncional(new HashSet<AnuTipoItemDietaUnfs>(listaAnuTipoItemDietaUnfs));

			persistirTiposDietaUnfs(tipoDieta, listaAnuTipoItemDietaUnfs);

			getNutricaoFacade().atualizarAnuTipoItemDietaDepreciado(tipoDieta);
			persistirAnuTipoItemDietaJn(tipoDieta,DominioOperacoesJournal.UPD);
						
		}
	}

	private void posInserirItemDieta(AnuTipoItemDieta tipoDieta) throws ApplicationBusinessException {
		// fatk_phi_rn.RN_PHIP_ATU_INSERT
		String descricao = null;
		if(tipoDieta.getDescricao() != null){
			descricao = tipoDieta.getDescricao().substring(0, tipoDieta.getDescricao().length() < 99 ? tipoDieta.getDescricao().length() : 99);
		}
		inserirFatProcedHospInternos(null,null,null,null,null,descricao,tipoDieta.getIndSituacao(),null,null,null,tipoDieta.getSeq());
		//insertFatProcedHospInternos(null,null,null,new String(),new String(),descricao,tipoDieta.getIndSituacao(),Short.valueOf("0"),Integer.valueOf("0"),Short.valueOf("0"),tipoDieta.getSeq());
	}

	private void persistirTiposDietaUnfs(AnuTipoItemDieta tipoDieta,
		List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs) throws ApplicationBusinessException {
		
		for(AnuTipoItemDietaUnfs unfs : listaAnuTipoItemDietaUnfs){
			preInserirTipoDietaUnfs(unfs);
		}
		
		/*Salvo a nova lista*/
		inserirUpdateListaUnidadesTipoDieta(tipoDieta, listaAnuTipoItemDietaUnfs);
		/*-----------------*/
		
	}
	
	private void persistirTiposDietaUnfs(AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas,
			List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc) throws ApplicationBusinessException {
			
			if(tipoDieta != null &&
			   tipoDieta.getSeq() == null){
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				tipoDieta.setCriadoEm(new Date());
				tipoDieta.setServidor(servidorLogado);
				
				anuTipoItemDietaDAO.persistir(tipoDieta);
			}else{
				anuTipoItemDietaDAO.merge(tipoDieta);
			}
		
			/*Salvo a nova lista*/
			inserirUpdateListaUnidadesTipoDieta(tipoDieta, listaUnidadeFuncAdicionadas, listaExcluiUnidadeFunc);
			/*-----------------*/
			
		}

	// ANUT_TIU_BRI
	private void preInserirTipoDietaUnfs(AnuTipoItemDietaUnfs unfs) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		unfs.setServidor(servidorLogado);
		unfs.setCriadoEm(new Date());
		
	}

	private void preInserirUpdateTiposDieta(AnuTipoItemDieta tipoDieta) throws ApplicationBusinessException {
		// CONSTRAINT ANU_TID_CK10
		if (!((tipoDieta.getTipoFrequenciaAprazamento() == null) || (tipoDieta.getTipoFrequenciaAprazamento() != null && tipoDieta.getIndDigitaAprazamento()
				.equals(DominioRestricao.O)))) {
			throw new ApplicationBusinessException(
					TiposDietaCRUDExceptionCode.FREQUENCIA_COM_TIPO_OBRIGATORIA_INVALIDA);
		}

		// CONSTRAINT ANU_TID_CK8
		if (!((tipoDieta.getIndAdulto() != null && tipoDieta.getIndAdulto().booleanValue())
				|| (tipoDieta.getIndPediatria() != null && tipoDieta.getIndPediatria()
						.booleanValue()) || (tipoDieta.getIndNeonatologia() != null && tipoDieta.getIndNeonatologia()
				.booleanValue()))) {
			throw new ApplicationBusinessException(
					TiposDietaCRUDExceptionCode.TIPO_PACIENTE_OBRIGATORIO);
		}
	}

	private void inserirUpdateListaUnidadesTipoDieta(AnuTipoItemDieta tipoDieta, List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs) {
		getNutricaoFacade().inserirAtualizarListaUnidadesTipoDieta(tipoDieta, listaAnuTipoItemDietaUnfs);
	}
	
	private void inserirUpdateListaUnidadesTipoDieta(AnuTipoItemDieta tipoDieta, List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas,
			List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc) {
		getNutricaoFacade().inserirAtualizarListaUnidadesTipoDieta(tipoDieta, listaUnidadeFuncAdicionadas, listaExcluiUnidadeFunc);
	}

	private void persistirAnuTipoItemDietaJn(AnuTipoItemDieta tipoDieta, DominioOperacoesJournal operacao) {
		AnuTipoItemDietasJn jn = criarTiposDietaJn(tipoDieta, operacao);
		
		getNutricaoFacade().inserirAnuTipoItemDietasJn(jn);
		
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private AnuTipoItemDietasJn criarTiposDietaJn(AnuTipoItemDieta tipoDieta,
			DominioOperacoesJournal upd) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AnuTipoItemDietasJn jn = BaseJournalFactory.getBaseJournal(upd, AnuTipoItemDietasJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		jn.doSetPropriedades(
				tipoDieta.getSeq(),
				tipoDieta.getDescricao(),
				tipoDieta.getSintaxeMedico(),
				tipoDieta.getSintaxeNutricao(),
				tipoDieta.getIndDigitaQuantidade() == null ? null : tipoDieta.getIndDigitaQuantidade().toString(),
				tipoDieta.getIndDigitaAprazamento() == null ? null : tipoDieta.getIndDigitaAprazamento().toString(),
				tipoDieta.getIndAdulto() == null ? null : DominioSimNao.getInstance(tipoDieta.getIndAdulto()).toString(),
				tipoDieta.getIndPediatria() == null ? null : DominioSimNao.getInstance(tipoDieta.getIndPediatria()).toString(),
				tipoDieta.getIndNeonatologia() == null ? null : DominioSimNao.getInstance(tipoDieta.getIndNeonatologia()).toString(),
				tipoDieta.getIndDietaPadronizada() == null ? null : DominioSimNao.getInstance(tipoDieta.getIndDietaPadronizada()).toString(),
				tipoDieta.getCriadoEm(), 
				tipoDieta.getIndSituacao().toString(),
				tipoDieta.getDescricaoCompletaMapaDieta(), 
				tipoDieta.getServidor().getId().getMatricula(), 
				tipoDieta.getServidor().getId().getVinCodigo(), 
				tipoDieta.getUnidadeMedidaMedica() == null ? null : tipoDieta.getUnidadeMedidaMedica().getSeq(), 
				DominioSimNao.getInstance(tipoDieta.getIndItemUnico()).toString(),
				tipoDieta.getFrequencia(), 
				tipoDieta.getTipoFrequenciaAprazamento() == null ? null: tipoDieta.getTipoFrequenciaAprazamento().getSeq());
		return jn;
	}

	/**
	 * @ORADB ANUT_TID_BRU, ANUT_TID_ARU
	 * @param tipoDieta
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preUpdateTiposDieta(AnuTipoItemDieta tipoDieta)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		getNutricaoFacade().desatacharAnuTipoItemDieta(tipoDieta);
		AnuTipoItemDieta tipoDietaOriginal = getNutricaoFacade()
				.obterAnuTipoItemDietaPorChavePrimaria(tipoDieta.getSeq());
		
		verificarNumeroVezesAprazamento24Hrs(tipoDieta);
		
		
		if (tipoDietaOriginal.getDescricao()!=null && 
			!tipoDietaOriginal.getDescricao().equalsIgnoreCase(tipoDieta.getDescricao())
			|| tipoDietaOriginal.getIndDietaPadronizada()!=null && 
			!tipoDietaOriginal.getIndDietaPadronizada().equals(tipoDieta.getIndDietaPadronizada())
			|| (tipoDietaOriginal.getUnidadeMedidaMedica()!=null && 
			tipoDietaOriginal.getUnidadeMedidaMedica().getSeq()!=null && 
			tipoDieta.getUnidadeMedidaMedica()!=null && tipoDieta.getUnidadeMedidaMedica().getSeq()!=null) && 
			!tipoDietaOriginal.getUnidadeMedidaMedica().getSeq().equals(tipoDieta.getUnidadeMedidaMedica().getSeq())) {
			
			throw new ApplicationBusinessException(TiposDietaCRUDExceptionCode.ANU_00117);
		}

		tipoDieta.setServidor(servidorLogado);

		// anuk_tid_rn.rn_tidp_ver_tp_freq
		 getPrescricaoMedicaFacade().validarAprazamento(
				tipoDieta.getTipoFrequenciaAprazamento(),
				tipoDieta.getFrequencia());

		// ANUT_TID_BRU
		if (tipoDietaOriginal.getSintaxeMedico()!=null && !tipoDietaOriginal.getSintaxeMedico().equals(
				tipoDieta.getSintaxeMedico())) {
					
			updateFatProcedHospInternosDescr(null,null,null,null,null,tipoDieta.getSintaxeMedico(),null,null,null,tipoDieta.getSeq());
			
		}
	
		if(tipoDietaOriginal.getIndSituacao()!=null && !tipoDietaOriginal.getIndSituacao().equals(
						tipoDieta.getIndSituacao())){
			updateFatProcedHospInternosSituacao(null,null,null,null,null,tipoDieta.getIndSituacao(),null,null,null,tipoDieta.getSeq());
		}
		//getAnuTipoItemDietaDAO().merge(tipoDieta);	

	}
	
		
	// fatk_phi_rn.RN_PHIP_ATU_DESCR 
	public void updateFatProcedHospInternosDescr(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		FatProcedHospInternos fatProcedHospInternos = buscaFatProcedHospInternosGenerico(matCodigo, pciSeq, pedSeq,
				csaCodigo, pheCodigo, euuSeq, cduSeq, cuiSeq, tidSeq);
		
		//#40982 - erro ao inativar dieta
		//Foi retirado por algumas diega não ter faturamento.
		if (fatProcedHospInternos != null) {
			getFaturamentoFacade().atualizarFatProcedHospInternos(fatProcedHospInternos);
		}
		
	}
	
	// fatk_phi_rn.RN_PHIP_ATU_SITUACAO
	public void updateFatProcedHospInternosSituacao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		

		FatProcedHospInternos fatProcedHospInternos = buscaFatProcedHospInternosGenerico(matCodigo, pciSeq, pedSeq,
				csaCodigo, pheCodigo, euuSeq, cduSeq, cuiSeq, tidSeq);
		//foi tira por causa da #40982 - erro ao inativar dieta
		//Foi retirado por algumas diega não ter faturamento.
		//
		if (fatProcedHospInternos != null) {
			getFaturamentoFacade().atualizarFatProcedHospInternos(fatProcedHospInternos);
		}
	}
	
	private FatProcedHospInternos buscaFatProcedHospInternosGenerico(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) {
		FatProcedHospInternos fatProcedHospInternos = null;
		if (tidSeq != null) {
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(tidSeq, FatProcedHospInternos.Fields.TDI_SEQ);						
		} else if(matCodigo !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(matCodigo.getCodigo(), FatProcedHospInternos.Fields.MAT_CODIGO);
		} else if(pciSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(pciSeq.getSeq(), FatProcedHospInternos.Fields.PCI_SEQ);
		} else if(pedSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(pedSeq.getSeq(), FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ);
		} else if(csaCodigo !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(csaCodigo, FatProcedHospInternos.Fields.CSA_CODIGO);
		} else if(pheCodigo !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(pheCodigo, FatProcedHospInternos.Fields.PHE_CODIGO);
		} else if(euuSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(euuSeq, FatProcedHospInternos.Fields.EUU_SEQ);
		} else if(cduSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(cduSeq, FatProcedHospInternos.Fields.CDU_SEQ);
		} else if(cuiSeq !=null){
			fatProcedHospInternos = getFaturamentoFacade().pesquisarPorChaveGenericaFatProcedHospInternos(cuiSeq, FatProcedHospInternos.Fields.CUI_SEQ);
		}
		return fatProcedHospInternos;
	}

	public void excluirTiposDieta(final Integer seqTipoDieta) throws BaseException {
		
		AnuTipoItemDieta tipoDieta =  getAnuTipoItemDietaDAO().obterPorChavePrimaria(seqTipoDieta);
		
		if (tipoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}
		
		// Pré-delete com  ANUT_TID_BRD, ANUT_TID_ARD
		this.preDeleteTiposDieta(tipoDieta);
		
		this.getNutricaoFacade().removerAnuTipoItemDieta(tipoDieta);
		
		// ANUT_TID_ARD
		persistirAnuTipoItemDietaJn(tipoDieta, DominioOperacoesJournal.DEL);
	}

	/**
	 * @ORADB ANUT_TID_BRI, ANUT_TID_ARI e CHK_ANU_TIPO_ITEM_DIETAS
	 * @param tipoDieta
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preInserirTiposDieta(AnuTipoItemDieta tipoDieta)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		verificarNumeroVezesAprazamento24Hrs(tipoDieta);
		
		tipoDieta.setServidor(servidorLogado);

		tipoDieta.setCriadoEm(new Date());

		// anuk_tid_rn.rn_tidp_ver_tp_freq
		 getPrescricaoMedicaFacade().validarAprazamento(
				tipoDieta.getTipoFrequenciaAprazamento(),
				tipoDieta.getFrequencia());


	}
	
	/**
	 * #13504 - Não deixar cadastrar frequência com numero de vezes ao dia = 0.
	 * @param tipoDieta
	 */
	private void verificarNumeroVezesAprazamento24Hrs(AnuTipoItemDieta tipoDieta) throws ApplicationBusinessException  {
		if (tipoDieta != null && tipoDieta.getFrequencia() != null && tipoDieta.getTipoFrequenciaAprazamento() != null && 
				getPrescricaoMedicaFacade().calculoNumeroVezesAprazamento24Horas(tipoDieta.getTipoFrequenciaAprazamento(), tipoDieta.getFrequencia()).intValue() < 1) {
			throw new ApplicationBusinessException(TiposDietaCRUDExceptionCode.CALCULO_NUMERO_VEZES_24HRS_INVALIDO); 
		}		
	}
	
	/**
	 * @ORADB ANUT_TID_BRD, ANUT_TID_ARD
	 * @param tipoDieta
	 * @throws ApplicationBusinessException
	 */
	public void preDeleteTiposDieta(AnuTipoItemDieta tipoDieta) throws BaseException {
		
		if (tipoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}
		
		// ANUK_TID_RN.RN_TIDP_VER_DELECAO
		this.verificaDataCriacao(tipoDieta.getCriadoEm());
		
		// CHK_ANU_TIPO_ITEM_DIETAS
		this.validaDelecao(tipoDieta);

		// FATK_PHI_RN.RN_PHIP_ATU_DELETE
		this.deleteFatProcedHospInternos(null,null,null,null,null,null,null,null,tipoDieta.getSeq());
		
	}
	
	/**
	 * @ORADB CHK_ANU_TIPO_ITEM_DIETAS
	 * @param tipoDieta
	 * @throws BaseException
	 */
	public void validaDelecao(AnuTipoItemDieta tipoDieta) throws BaseException {
		
		if (tipoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}
		
		BaseListException erros = new BaseListException();
		
		erros.add(this.existeItemDieta(tipoDieta, MpmItemPrescricaoDieta.class, MpmItemPrescricaoDieta.Fields.TIPO_ITEM_DIETA, TiposDietaCRUDExceptionCode.MPM_IPD_TID_FK1));
		erros.add(this.existeItemDieta(tipoDieta, MpaCadOrdItemNutricoes.class, MpaCadOrdItemNutricoes.Fields.TIPO_ITEM_DIETA, TiposDietaCRUDExceptionCode.MPA_CRN_TID_FK1));
		erros.add(this.existeItemDieta(tipoDieta, MpaUsoOrdItemNutricoes.class, MpaUsoOrdItemNutricoes.Fields.TIPO_ITEM_DIETA, TiposDietaCRUDExceptionCode.MPA_UIN_TID_FK1));
		erros.add(this.existeItemDieta(tipoDieta, MpmItemModeloBasicoDieta.class, MpmItemModeloBasicoDieta.Fields.TIPO_ITEM_DIETA, TiposDietaCRUDExceptionCode.MPM_IMD_TID_FK1));
		erros.add(this.existeItemDieta(tipoDieta, MpmItemDietaSumario.class, MpmItemDietaSumario.Fields.TIPO_ITEM_DIETA, TiposDietaCRUDExceptionCode.MPM_IDS_TID_FK1));
		erros.add(this.existeItemDieta(tipoDieta, AnuTipoItemDietaUnfs.class, AnuTipoItemDietaUnfs.Fields.TIPO_ITEM_DIETA, TiposDietaCRUDExceptionCode.ANU_TIU_TID_FK1));
	
		if (erros.hasException()) {
			throw erros;
		}
		
	}
	
	/**
	 * 
	 * Verificar a existência de registros de tipo item de dieta em outras entidades
	 * 
	 * @param pdtSeq
	 * @return
	 * @param tipoDieta
	 * @param class1
	 * @param field
	 * @param exceptionCode
	 * @return
	 */
	private ApplicationBusinessException existeItemDieta(AnuTipoItemDieta tipoDieta, Class class1, Enum field, TiposDietaCRUDExceptionCode exceptionCode) {

		if (tipoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		final boolean isExisteItemDieta = getNutricaoFacade().existeItemDieta(tipoDieta, class1, field);
		
		if(isExisteItemDieta){
			return new ApplicationBusinessException(exceptionCode);
		}
		
		return null;
	}
	
	/**
	 * @ORADB ANUK_TID_RN.RN_TIDP_VER_DELECAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDataCriacao(Date data) throws ApplicationBusinessException {
		AghParametros aghParametro = iParametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AFA);

		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar
					.getInstance().getTime(), data);
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(
						TiposDietaCRUDExceptionCode.AFA_00172);
			}
		} else {
			throw new ApplicationBusinessException(
					TiposDietaCRUDExceptionCode.AFA_00173);
		}
	}
	
	
	
			
	//RN_PHIP_ATU_INSERT
	public void inserirFatProcedHospInternos(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, String descricao,
			DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq)  throws ApplicationBusinessException {

		FatProcedHospInternos fat = new FatProcedHospInternos();
		fat.setDescricao(descricao);
		fat.setSituacao(indSituacao);
		fat.setMaterial(matCodigo);
		fat.setProcedimentoCirurgico(pciSeq);
		fat.setProcedimentoEspecialDiverso(pedSeq);
		fat.setCsaCodigo(csaCodigo);
		fat.setPheCodigo(pheCodigo);

		int seq = 0;
		if (euuSeq != null) {
			seq = euuSeq;
		} else {
			if (cduSeq != null) {
				seq = cduSeq;
			} else {
				if (cuiSeq != null) {
					seq = cuiSeq;
				} else {
					if (tidSeq != null) {
						seq = tidSeq;
					} else {
						seq = 0;
					}
				}
			}
		}

		if (seq == 0) {
			fat.setIndOrigemPresc(true);
		} else {
			fat.setIndOrigemPresc(false);
		}

		fat.setEuuSeq(euuSeq);
		fat.setCduSeq(cduSeq);
		fat.setCuiSeq(cuiSeq);
		fat.setTidSeq(tidSeq);
		
		getFaturamentoFacade().inserirFatProcedHospInternos(fat);
		
	}
	
	/**
	 * FATK_PHI_RN.RN_PHIP_ATU_DELETE
	 * @param tipoDieta
	 */
	public void deleteFatProcedHospInternos(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) {

		getFaturamentoFacade().deleteFatProcedHospInternos(matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, euuSeq, cduSeq, cuiSeq, tidSeq);
		
	}

	protected INutricaoFacade getNutricaoFacade() {
		return this.iNutricaoFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return iPrescricaoMedicaFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	public Long pesquisarTipoItemDietaCount(Integer codigo, String descricao, DominioSituacao situacao){
		return getNutricaoFacade().pesquisarTipoItemDietaCount(codigo, descricao, situacao, null);
	}

	public List<AnuTipoItemDietaVO> pesquisarTipoItemDieta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao) {
		
		
		MpmTipoFrequenciaAprazamentoDAO freqApraDAO = mpmTipoFrequenciaAprazamentoDAO;
		
		
		List<AnuTipoItemDietaVO> lstItens = getNutricaoFacade().pesquisarTipoItemDieta(firstResult,
				maxResult, orderProperty, asc, codigo, descricao, situacao, null);

		for (AnuTipoItemDietaVO item : lstItens) {
			if(item.getSeqAprazamento() != null){
				MpmTipoFrequenciaAprazamento apraza = freqApraDAO.obterTipoFrequenciaAprazamentoPeloId(item.getSeqAprazamento());
				item.setAprazamento(apraza.getDescricaoSintaxeFormatada(item.getFrequencia()));
			}
		}
		
		return lstItens;
	}

	public Integer pesquisarTipoDietaCount(
			AghUnidadesFuncionais unidadeFuncional) {
		return this.getINutricaoFacade().pesquisarTipoDietaCount(unidadeFuncional);
	}

	public List<AnuTipoItemDietaUnfs> pesquisarTipoDieta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncional) {
		return this.getINutricaoFacade().pesquisarTipoDieta(firstResult, maxResult,
				orderProperty, asc, unidadeFuncional);
	}

	public void excluir(AnuTipoItemDietaUnfs anuTipoItemDietaUnfs) {
		anuTipoItemDietaUnfs = this.getINutricaoFacade().obterAnuTipoItemDietaUnfsPorId(anuTipoItemDietaUnfs.getSeq());
		this.getINutricaoFacade().excluir(anuTipoItemDietaUnfs);
	}

	public void inserirTodosTiposDietasUnfs(RapServidores servidor)
			throws ApplicationBusinessException {
		this.getINutricaoFacade().inserirTodosTiposDietasUnf(servidor);
	}

	public void incluirTiposDietasUnfs(AghUnidadesFuncionais unidadeFuncional,
			RapServidores servidor) throws ApplicationBusinessException {
		this.getINutricaoFacade().inserirTiposDietasUnf(unidadeFuncional,
				servidor);
	}
	
	protected INutricaoFacade getINutricaoFacade() {
		return iNutricaoFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}
	
	public AnuTipoItemDietaDAO getAnuTipoItemDietaDAO() {
		return anuTipoItemDietaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public void persistirTiposDieta(AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas,
			List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc) throws ApplicationBusinessException {
		persistirTiposDietaUnfs(tipoDieta, listaUnidadeFuncAdicionadas,listaExcluiUnidadeFunc);
	}
	
}
