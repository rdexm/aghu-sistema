package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.questionario.vo.VFatProcedSusPhiVO;
import br.gov.mec.aghu.faturamento.dao.FatConvGrupoItensProcedDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosJnDAO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosJn;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author Filipe Hoffmeister
 *
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ManterFatProcedHospInternosRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ManterFatProcedHospInternosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private FatProcedHospInternosJnDAO fatProcedHospInternosJnDAO;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	private FatConvGrupoItensProcedDAO fatConvGrupoItensProcedDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2232317877338252454L;
	
	private static final Integer TAMANHO_MAXIMO_DESCRICAO_PROCEDIMENTO = 99;

	public enum ManterFatProcedHospInternosRNExceptionCode implements BusinessExceptionCode {

		FAT_00871, FAT_00872, FAT_00878, FAT_00879, FAT_00149, FAT_00877, FAT_00152, FAT_00153,
		MENSAGEM_ERRO_CHECK_CONSTRAINT_FAT_PHI_CK2;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	/**
	 * Insere objeto MpmAltaMotivo.
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 */
	public void inserirFatProcedHospInternos(FatProcedHospInternos fatProcedHospInternos)
			throws ApplicationBusinessException {

		this.preInserirFaturamentoProcedimentosInternos(fatProcedHospInternos);
		this.getFatProcedHospInternosDAO().persistir(fatProcedHospInternos);

	}

	/**
	 * Insere objeto MpmAltaMotivo.
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarFatProcedHospInternos(FatProcedHospInternos fatProcedHospInternos)
			throws ApplicationBusinessException {

		FatProcedHospInternos fatProcedHospInternosOriginal = getFatProcedHospInternosDAO().obterFatProcedHospInternos(fatProcedHospInternos);
		
		this.preAtualizarFaturamentoProcedimentosInternos(fatProcedHospInternos, fatProcedHospInternosOriginal);
		this.getFatProcedHospInternosDAO().merge(fatProcedHospInternos);
		this.getFatProcedHospInternosDAO().flush();
		this.posAtualizarFaturamentoProcedimentosInternos(fatProcedHospInternos, fatProcedHospInternosOriginal);

	}
	

	/**
	 * @ORADB Trigger FATT_PHI_BRI
	 * 
	 * @param {FatProcedHospInternos} fatProcedHospInternos
	 * @throws ApplicationBusinessException
	 */
	private void preInserirFaturamentoProcedimentosInternos(FatProcedHospInternos fatProcedHospInternos)
			throws ApplicationBusinessException {

		//RN17 – Atualiza a data de criação do registro para a data atual.
		fatProcedHospInternos.setCriadoEm(new Date());

		verificarPreenchimento(fatProcedHospInternos);
	}
	
	/**
	 * 	Verifica se apenas um dos campos a seguir está preenchido: 
	 *  tipo_oper_conversao e fator_conversao.
	 * @param fatProcedHospInternos
	 * @throws ApplicationBusinessException
	 */
	protected void verificarPreenchimento(FatProcedHospInternos fatProcedHospInternos) throws ApplicationBusinessException{
		if (StringUtils.isNotBlank(fatProcedHospInternos.getTipoOperConversao() == null ? null : fatProcedHospInternos.getTipoOperConversao().toString()) && fatProcedHospInternos.getFatorConversao() == null) {
			ManterFatProcedHospInternosRNExceptionCode.FAT_00878.throwException();
		} else if (fatProcedHospInternos.getFatorConversao() != null && StringUtils.isBlank(fatProcedHospInternos.getTipoOperConversao() == null ? null : fatProcedHospInternos.getTipoOperConversao().toString())) {
			ManterFatProcedHospInternosRNExceptionCode.FAT_00879.throwException();
		}
	}
	
	
	

	/**
	 * @ORADB Trigger FATT_PHI_ARU
	 * 
	 * @param {FatProcedHospInternos} fatProcedHospInternos
	 * @throws ApplicationBusinessException
	 */
	private void posAtualizarFaturamentoProcedimentosInternos(FatProcedHospInternos fatProcedHospInternos, FatProcedHospInternos fatProcedHospInternosOriginal)
			throws ApplicationBusinessException {

//		RN25 – Se algum registro foi alterado Insere um registro na tabela “fat_proced_hosp_internos_jn”.
		if (inserirJN(fatProcedHospInternos, fatProcedHospInternosOriginal)) {		
			FatProcedHospInternosJn fatProcedHospInternosJn =  criarFatProcedHospInternosJn(fatProcedHospInternosOriginal, DominioOperacoesJournal.UPD);
			getFatProcedHospInternosJnDAO().persistir(fatProcedHospInternosJn);
			getFatProcedHospInternosJnDAO().flush();
		}
	}
	
	/**
	 * Verifica se algum dado foi modificado para inserir na tabela JN
	 * @param fatProcedHospInternos
	 * @param fatProcedHospInternosOriginal
	 * @return
	 */
	protected boolean inserirJN(FatProcedHospInternos fatProcedHospInternos, FatProcedHospInternos fatProcedHospInternosOriginal) {
		boolean inserirJN = false;   
		if (CoreUtil.modificados(fatProcedHospInternosOriginal.getSeq(), fatProcedHospInternos.getSeq()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getDescricao(), fatProcedHospInternos.getDescricao()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getOrientacoesFaturamento(), fatProcedHospInternos.getOrientacoesFaturamento()) ||
				(CoreUtil.modificados(fatProcedHospInternosOriginal.getSituacao() == null ? null : fatProcedHospInternosOriginal.getSituacao().getDescricao(), fatProcedHospInternos.getSituacao() == null ? null : fatProcedHospInternos.getSituacao().getDescricao())) ||		
				(CoreUtil.modificados(fatProcedHospInternosOriginal.getMaterial() == null ? null : fatProcedHospInternosOriginal.getMaterial().getCodigo(), fatProcedHospInternos.getMaterial() == null ? null : fatProcedHospInternos.getMaterial().getCodigo())) ||
				(CoreUtil.modificados(fatProcedHospInternosOriginal.getProcedimentoEspecialDiverso() == null ? null : fatProcedHospInternosOriginal.getProcedimentoEspecialDiverso().getSeq(), fatProcedHospInternos.getProcedimentoEspecialDiverso() == null ? null : fatProcedHospInternos.getProcedimentoEspecialDiverso().getSeq())) ||
				(CoreUtil.modificados(fatProcedHospInternosOriginal.getProcedimentoCirurgico() == null ? null : fatProcedHospInternosOriginal.getProcedimentoCirurgico().getSeq(), fatProcedHospInternos.getProcedimentoCirurgico() == null ? null : fatProcedHospInternos.getProcedimentoCirurgico().getSeq())) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getCsaCodigo(), fatProcedHospInternos.getCsaCodigo()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getPheCodigo(), fatProcedHospInternos.getPheCodigo()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getServidor(), fatProcedHospInternos.getServidor()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getCriadoEm(), fatProcedHospInternos.getCriadoEm()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getEmaExaSigla(), fatProcedHospInternos.getEmaExaSigla()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getEmaManSeq(), fatProcedHospInternos.getEmaManSeq()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getComponenteIrradiado(), fatProcedHospInternos.getComponenteIrradiado()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getTipoNutrParenteral(), fatProcedHospInternos.getTipoNutrParenteral()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getIndOrigemPresc(), fatProcedHospInternos.getIndOrigemPresc()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getTipoOperConversao(), fatProcedHospInternos.getTipoOperConversao()) ||
				CoreUtil.modificados(fatProcedHospInternosOriginal.getFatorConversao(), fatProcedHospInternos.getFatorConversao()) ||
				(CoreUtil.modificados(fatProcedHospInternosOriginal.getProcedimentoHospitalarInterno() == null ? null : fatProcedHospInternosOriginal.getProcedimentoHospitalarInterno().getSeq(), fatProcedHospInternos.getProcedimentoHospitalarInterno() == null ? null : fatProcedHospInternos.getProcedimentoHospitalarInterno().getSeq()))) {
			   inserirJN = true;
		   }
		return inserirJN;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private FatProcedHospInternosJn criarFatProcedHospInternosJn(FatProcedHospInternos fatProcedHospInternos,
			DominioOperacoesJournal upd) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		FatProcedHospInternosJn jn = BaseJournalFactory.getBaseJournal(upd, FatProcedHospInternosJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);

		jn.doSetPropriedades(
				fatProcedHospInternos.getSeq(),
				fatProcedHospInternos.getDescricao(),
				fatProcedHospInternos.getOrientacoesFaturamento(),
				fatProcedHospInternos.getSituacao(),
				fatProcedHospInternos.getMaterial() == null ? Integer.valueOf("0") : fatProcedHospInternos.getMaterial().getCodigo(),
				fatProcedHospInternos.getProcedimentoCirurgico() == null ? Integer.valueOf("0") : fatProcedHospInternos.getProcedimentoCirurgico().getSeq(),
				fatProcedHospInternos.getProcedimentoEspecialDiverso() == null ? Short.valueOf("0") : fatProcedHospInternos.getProcedimentoEspecialDiverso().getSeq(),
				fatProcedHospInternos.getCsaCodigo(),
				fatProcedHospInternos.getPheCodigo(),
				fatProcedHospInternos.getServidor() != null ? fatProcedHospInternos.getServidor().getId().getMatricula() : null,
				fatProcedHospInternos.getServidor() != null ? fatProcedHospInternos.getServidor().getId().getVinCodigo() : null,
				fatProcedHospInternos.getCriadoEm(),
				fatProcedHospInternos.getEmaExaSigla().getSigla(),
				fatProcedHospInternos.getEmaManSeq(),
				fatProcedHospInternos.getComponenteIrradiado(),
				fatProcedHospInternos.getTipoNutrParenteral(),
				fatProcedHospInternos.getIndOrigemPresc(),
				fatProcedHospInternos.getTipoOperConversao() == null ? null : fatProcedHospInternos.getTipoOperConversao().toString(),
				fatProcedHospInternos.getFatorConversao(),
				fatProcedHospInternos.getProcedimentoHospitalarInterno() == null ? Short.valueOf("0") : fatProcedHospInternos.getProcedimentoHospitalarInterno().getSeq()
				);
		return jn;
	}
	
	/**
	 * @ORADB Trigger FATT_PHI_BRU
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void preAtualizarFaturamentoProcedimentosInternos(FatProcedHospInternos fatProcedHospInternos, FatProcedHospInternos fatProcedHospInternosOriginal)
			throws ApplicationBusinessException {
//		RN20 -  Não permitir que o usuário altere dados que não possam ser alterados.
		verificarModificados(fatProcedHospInternos, fatProcedHospInternosOriginal);
		

//		RN21 – Verifica se apenas um dos campos a seguir está preenchido: 
//		tipo_oper_conversao e fator_conversao.
		verificarPreenchimento(fatProcedHospInternos);
		
//		RN22 - O tipo de operação e fator de conversão só podem ser alterados 
//		se o código do material estiver preenchido.
		verificarPreenchimentoCodigoMaterial(fatProcedHospInternos, fatProcedHospInternosOriginal);
		
//		RN23 - Verifica e o usuário está logado na aplicação. Detalhamento em D02.
		getPrescricaoMedicaFacade().validaServidorLogado();
	}

	/**
	 * O tipo de operação e fator de conversão só podem ser alterados 
	 * se o código do material estiver preenchido.
	 * @param fatProcedHospInternos
	 * @param fatProcedHospInternosOriginal
	 * @throws ApplicationBusinessException
	 */
	protected void verificarPreenchimentoCodigoMaterial(FatProcedHospInternos fatProcedHospInternos, FatProcedHospInternos fatProcedHospInternosOriginal) throws ApplicationBusinessException {
		if (CoreUtil.modificados(fatProcedHospInternosOriginal.getTipoOperConversao(), fatProcedHospInternos.getTipoOperConversao()) && 
				CoreUtil.modificados(fatProcedHospInternosOriginal.getFatorConversao(), fatProcedHospInternos.getFatorConversao()) && 
				(fatProcedHospInternosOriginal.getMaterial() == null || fatProcedHospInternosOriginal.getMaterial().getCodigo() == null)){
			ManterFatProcedHospInternosRNExceptionCode.FAT_00877.throwException();
		}
	}

	/**
	 * Se todos os dados a seguir forem nulos:  pci_seq, ped_seq, csa_codigo, phe_codigo, ema_man_seq, ema_exa_sigla, 
	 * e o campo “seq” foi modificado 
	 * ou, se algum dos campos a seguir foi modificado: ser_matricula, ser_vin_codigo, criado_em mostra o erro.
	 * @param fatProcedHospInternos
	 * @throws ApplicationBusinessException
	 */
	protected void verificarModificados(FatProcedHospInternos fatProcedHospInternos,FatProcedHospInternos fatProcedHospInternosOriginal) throws ApplicationBusinessException {
		if ((fatProcedHospInternos.getMaterial() == null || fatProcedHospInternos.getMaterial().getCodigo() == null) &&
				(fatProcedHospInternos.getProcedimentoCirurgico() == null || fatProcedHospInternos.getProcedimentoCirurgico().getSeq() == null) && 
				(fatProcedHospInternos.getProcedimentoEspecialDiverso() == null || fatProcedHospInternos.getProcedimentoEspecialDiverso().getSeq() == null) && 
				fatProcedHospInternos.getCsaCodigo() == null && 
				fatProcedHospInternos.getPheCodigo() == null && 
				fatProcedHospInternos.getEmaManSeq() == null && 
				fatProcedHospInternos.getEmaExaSigla() == null &&
				(CoreUtil.modificados(fatProcedHospInternosOriginal.getSeq(), fatProcedHospInternos.getSeq()) || 
				CoreUtil.modificados(fatProcedHospInternosOriginal.getServidor(), fatProcedHospInternos.getServidor()) || 
					CoreUtil.modificados(fatProcedHospInternosOriginal.getCriadoEm(), fatProcedHospInternos.getCriadoEm()))) { 
			ManterFatProcedHospInternosRNExceptionCode.FAT_00149.throwException();
		}
	}
	
	/**
	 * FATK_PHI_RN.RN_PHIP_ATU_DELETE
	 * @param tipoDieta
	 */
	public void deleteFatProcedHospInternos(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq, Boolean flush) {

		if (tidSeq != null) {
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(tidSeq, FatProcedHospInternos.Fields.TDI_SEQ, flush);
		} else if(matCodigo !=null){
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(matCodigo.getCodigo(), FatProcedHospInternos.Fields.MAT_CODIGO, flush);
		} else if(pciSeq !=null){
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(pciSeq.getSeq(), FatProcedHospInternos.Fields.PCI_SEQ, flush);
		} else if(pedSeq !=null){
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(pedSeq.getSeq(), FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ, flush);
		} else if(csaCodigo !=null){
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(csaCodigo, FatProcedHospInternos.Fields.CSA_CODIGO, flush);
		} else if(pheCodigo !=null){
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(pheCodigo, FatProcedHospInternos.Fields.PHE_CODIGO, flush);
		} else if(euuSeq !=null){
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(euuSeq, FatProcedHospInternos.Fields.EUU_SEQ, flush);
		} else if(cduSeq !=null){
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(cduSeq, FatProcedHospInternos.Fields.CDU_SEQ, flush);
		} else if(cuiSeq !=null){
			getFatProcedHospInternosDAO().removerFatProcedHospInternos(cuiSeq, FatProcedHospInternos.Fields.CUI_SEQ, flush);
		}
		
	}
	
	public void deleteFatProcedHospInternos(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos pciSeq, MpmProcedEspecialDiversos pedSeq,
			String csaCodigo, String pheCodigo, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq){
		deleteFatProcedHospInternos(matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, euuSeq, cduSeq, cuiSeq, tidSeq, Boolean.TRUE);
	}

	/** Valida a check constraint FAT_PHI_CK2
	 * 
	 * @param procedHospInterno
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void validarConstraintAntesPersistir(FatProcedHospInternos procedHospInterno) throws ApplicationBusinessException {
		//verifica constraint FAT_PHI_CK2
		if (!((procedHospInterno.getMaterial() != null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() == null &&
	/*1*/	procedHospInterno.getEmaExaSigla() == null &&
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() != null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() == null &&
	/*2*/	procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() != null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() == null &&
	/*3*/	procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() != null &&
	/*4*/	procedHospInterno.getProcedHemoterapico() == null &&
			procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() != null &&
	/*5*/	procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() == null &&
	/*6*/	procedHospInterno.getEmaExaSigla() != null &&
			procedHospInterno.getEmaManSeq() != null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() == null &&
	/*7*/	procedHospInterno.getEmaExaSigla() == null &&
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() == null &&
	/*8*/	procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() != null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
	/*9*/	procedHospInterno.getProcedHemoterapico() == null &&
			procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() != null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() == null &&
	/*10*/	procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() != null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
			procedHospInterno.getProcedHemoterapico() == null &&
	/*11*/	procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() != null &&
			procedHospInterno.getItemExame() == null)
			||
			(procedHospInterno.getMaterial() == null &&
			procedHospInterno.getProcedimentoCirurgico() == null &&
			procedHospInterno.getProcedimentoEspecialDiverso() == null &&
			procedHospInterno.getComponenteSanguineo() == null &&
	/*12*/	procedHospInterno.getProcedHemoterapico() == null &&
			procedHospInterno.getEmaExaSigla() == null && 
			procedHospInterno.getEmaManSeq() == null &&
			procedHospInterno.getEquipamentoCirurgico() == null &&
			procedHospInterno.getCuidadoUsual() == null &&
			procedHospInterno.getCuidado() == null &&
			procedHospInterno.getItemMedicacao() == null &&
			procedHospInterno.getItemExame() != null))) {
			throw new ApplicationBusinessException(ManterFatProcedHospInternosRNExceptionCode.MENSAGEM_ERRO_CHECK_CONSTRAINT_FAT_PHI_CK2);		
		}
		
	}	
	
	/**
	 * @ORADB AELC_BUSCA_PHI_SUS
	 * Esta função retorna o phi e o código do SUS correspondente
	 * @param item
	 * @return
	 */
	public StringBuilder buscarPhiSus(AelItemSolicitacaoExames item) {		
		FatProcedHospInternos procedHospInt = getFatProcedHospInternosDAO().obterFatProcedHospInternosPorMaterial(item.getExameMatAnalise().getExameMaterialAnalise().getId());
		VFatProcedSusPhiVO phiVO = new VFatProcedSusPhiVO();
		phiVO.setPhiSeq(procedHospInt.getSeq());
		List<VFatProcedSusPhiVO> listPhiVO = getFatConvGrupoItensProcedDAO().pesquisarViewFatProcedSusPhiVO(phiVO, null, null, null);
		if(listPhiVO != null && !listPhiVO.isEmpty()){
			phiVO = listPhiVO.iterator().next();
		}
		StringBuilder sb1 = new StringBuilder();
		sb1.append(phiVO.getPhiSeq()).append(" - ").append(phiVO.getCodTabela());
		return sb1;
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: FATK_PHI_RN.RN_PHIP_ATU_INSERT
	 * 
	 * @param matCodigo
	 * @param pciSeq
	 * @param pedSeq
	 * @param csaCodigo
	 * @param pheCodigo
	 * @param descricao
	 * @param indSituacao
	 * @param euuSeq
	 * @param cduSeq
	 * @param cuiSeq
	 * @param tidSeq
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void inserirProcedimentoHospitalarInterno(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, String descricao,
			DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		FatProcedHospInternos fat = new FatProcedHospInternos();
		if (descricao.length() > TAMANHO_MAXIMO_DESCRICAO_PROCEDIMENTO) {
			fat.setDescricao(descricao.substring(0, TAMANHO_MAXIMO_DESCRICAO_PROCEDIMENTO));
		} else {
			fat.setDescricao(descricao);
		}
		fat.setOrientacoesFaturamento(null);
		fat.setSituacao(indSituacao);
		fat.setMaterial(matCodigo);
		fat.setProcedimentoCirurgico(procedimentoCirurgico);
		fat.setProcedimentoEspecialDiverso(procedEspecialDiverso);
		fat.setCsaCodigo(csaCodigo);
		fat.setPheCodigo(pheCodigo);
		fat.setServidor(null);
		fat.setCriadoEm(null);

		int seq = 0;
		if (euuSeq != null) {
			seq = euuSeq;
		} else if (cduSeq != null) {
			seq = cduSeq;
		} else if (cuiSeq != null) {
			seq = cuiSeq;
		} else if (tidSeq != null) {
			seq = tidSeq;
		}

		if (seq == 0) {
			fat.setIndOrigemPresc(Boolean.TRUE);
		} else {
			fat.setIndOrigemPresc(Boolean.FALSE);
		}

		fat.setEuuSeq(euuSeq);
		fat.setCduSeq(cduSeq);
		fat.setCuiSeq(cuiSeq);
		fat.setTidSeq(tidSeq);
		
		try {
			inserirFatProcedHospInternos(fat);	
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(ManterFatProcedHospInternosRNExceptionCode.FAT_00152, e);
		} catch (PersistenceException e) {
			throw new ApplicationBusinessException(ManterFatProcedHospInternosRNExceptionCode.FAT_00152, e);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: FATK_PHI_RN.RN_PHIP_ATU_SITUACAO
	 * 
	 * @param matCodigo
	 * @param pciSeq
	 * @param pedSeq
	 * @param csaCodigo
	 * @param pheCodigo
	 * @param indSituacao
	 * @param euuSeq
	 * @param cduSeq
	 * @param cuiSeq
	 * @param tidSeq
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void atualizarProcedimentoHospitalarInternoSituacao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		List<FatProcedHospInternos> listaFatProcedHospInterno = buscarFatProcedHospInternosGenerico(matCodigo, procedimentoCirurgico, 
				procedEspecialDiverso, csaCodigo, pheCodigo, euuSeq, cduSeq, cuiSeq, tidSeq);
		
		try {
			for (FatProcedHospInternos procedHospInterno : listaFatProcedHospInterno) {
				procedHospInterno.setSituacao(indSituacao);
				atualizarFatProcedHospInternos(procedHospInterno);	
			}			
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(ManterFatProcedHospInternosRNExceptionCode.FAT_00153, e);
		} catch (PersistenceException e) {
			throw new ApplicationBusinessException(ManterFatProcedHospInternosRNExceptionCode.FAT_00153, e);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: FATK_PHI_RN.RN_PHIP_ATU_DESCR
	 * 
	 * @param matCodigo
	 * @param procedimentoCirurgico
	 * @param procedEspecialDiverso
	 * @param csaCodigo
	 * @param pheCodigo
	 * @param descricao
	 * @param euuSeq
	 * @param cduSeq
	 * @param cuiSeq
	 * @param tidSeq
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void atualizarProcedimentoHospitalarInternoDescricao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException { 
		
		List<FatProcedHospInternos> listaFatProcedHospInterno = buscarFatProcedHospInternosGenerico(matCodigo, procedimentoCirurgico, 
				procedEspecialDiverso, csaCodigo, pheCodigo, euuSeq, cduSeq, cuiSeq, tidSeq);
		
		try {
			for (FatProcedHospInternos procedHospInterno : listaFatProcedHospInterno) {
				procedHospInterno.setDescricao(descricao);
				atualizarFatProcedHospInternos(procedHospInterno);	
			}
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(ManterFatProcedHospInternosRNExceptionCode.FAT_00153, e);
		} catch (PersistenceException e) {
			throw new ApplicationBusinessException(ManterFatProcedHospInternosRNExceptionCode.FAT_00153, e);
		}
	}
	
	private List<FatProcedHospInternos> buscarFatProcedHospInternosGenerico(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) {
		List<FatProcedHospInternos> listaFatProcedHospInterno = null;
		if (tidSeq != null) {
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(tidSeq, FatProcedHospInternos.Fields.TDI_SEQ);						
		} else if (matCodigo != null){
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(matCodigo.getCodigo(), FatProcedHospInternos.Fields.MAT_CODIGO);
		} else if (procedimentoCirurgico != null){
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(procedimentoCirurgico.getSeq(), FatProcedHospInternos.Fields.PCI_SEQ);
		} else if (procedEspecialDiverso != null){
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(procedEspecialDiverso.getSeq(), FatProcedHospInternos.Fields.PROCEDIMENTO_ESPECIAL_DIVERSO_SEQ);
		} else if (csaCodigo != null){
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(csaCodigo, FatProcedHospInternos.Fields.CSA_CODIGO);
		} else if (pheCodigo != null){
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(pheCodigo, FatProcedHospInternos.Fields.PHE_CODIGO);
		} else if (euuSeq != null){
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(euuSeq, FatProcedHospInternos.Fields.EUU_SEQ);
		} else if (cduSeq != null){
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(cduSeq, FatProcedHospInternos.Fields.CDU_SEQ);
		} else if (cuiSeq != null){
			listaFatProcedHospInterno = getFatProcedHospInternosDAO().pesquisarListaPorChaveGenericaFatProcedHospInternos(cuiSeq, FatProcedHospInternos.Fields.CUI_SEQ);
		}
		return listaFatProcedHospInterno;
	}	
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}
	
	protected FatProcedHospInternosJnDAO getFatProcedHospInternosJnDAO() {
		return fatProcedHospInternosJnDAO;
	}
	
	protected FatConvGrupoItensProcedDAO getFatConvGrupoItensProcedDAO(){
		return fatConvGrupoItensProcedDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
