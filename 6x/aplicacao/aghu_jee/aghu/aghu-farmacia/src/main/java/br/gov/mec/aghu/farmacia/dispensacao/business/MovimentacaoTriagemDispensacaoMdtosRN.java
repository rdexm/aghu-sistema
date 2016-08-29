package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.VMpmPrescrMdtos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class MovimentacaoTriagemDispensacaoMdtosRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(MovimentacaoTriagemDispensacaoMdtosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@EJB
private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;

@Inject
private AfaMedicamentoDAO afaMedicamentoDAO;

@EJB
private IFarmaciaFacade farmaciaFacade;

	private static final long serialVersionUID = -7102244288602831970L;
	
	public enum MovimentacaoTriagemDispensacaoMdtosRNExceptionCode implements BusinessExceptionCode {
		MESSAGE_MOV_DISPMDTOS_QTDE_DISP_OBRIGATORIO,MESSAGE_MOV_DISPMDTOS_FARMACIA_OBRIGATORIO, MESSAGE_MOV_DISPMDTOS_IND_SITUACAO_OBRIGATORIO
	}

	// ********TRIGGERS********

	// BEFORE INSERT

	/**
	 * Utilizacao de persistencia das triggers
	 * @ORADB Trigger "AGH".AFAT_DSM_BRI e
	 * @ORADB Trigger "AGH".AFAT_DSM_BRU
	 * 
	 * @param prescricaoMedica
	 * @param itemPrescrito
	 * @param medicamentosDispensadosModificados
	 * @throws ApplicationBusinessException
	 */
	public void persistirMovimentosDispensacaoMdtos(
			MpmPrescricaoMedica prescricaoMedica,
			MpmItemPrescricaoMdto itemPrescrito,
			List<AfaDispensacaoMdtos> medicamentosDispensadosModificados, 
			List<AfaDispensacaoMdtos> medicamentosDispensadosOriginal,
			String nomeMicrocomputador)
			throws BaseException {

	
		verificaCamposObrigatorios(medicamentosDispensadosModificados);
		medicamentosDispensadosModificados = removeDispensacaoesNaoPreenchidas(medicamentosDispensadosModificados);
		if (medicamentosDispensadosModificados != null
				&& medicamentosDispensadosModificados.size() > 0) {
			for (int i = 0; i < medicamentosDispensadosModificados.size(); i++) {
				AfaDispensacaoMdtos admNew = medicamentosDispensadosModificados
						.get(i);
				admNew.setPrescricaoMedica(prescricaoMedica);
				admNew.setItemPrescricaoMdto(itemPrescrito);
				if (admNew.getSeq() != null && medicamentosDispensadosOriginal != null && medicamentosDispensadosOriginal.size() > 0) {
					// Atualiza - Validacao
					AfaDispensacaoMdtos admOld = buscaDispensacaoOriginal(
							admNew, medicamentosDispensadosOriginal);
					
					//Altera a indSituação do registro, em caso de erro é necessário recuperar o setado
					DominioSituacaoDispensacaoMdto indSit = admNew.getIndSituacao();
					if(!indSit.equals(admNew.getIndSituacaoNova())){
						admNew.setIndSituacao(admNew.getIndSituacaoNova());
					}
					if (admOld != null) {
						if (verificaSeSofreuAlteracao(admOld, admNew)) {
							try {
								getFarmaciaFacade().atualizaAfaDispMdto(admNew,admOld, nomeMicrocomputador);
								getFarmaciaDispensacaoFacade().processaImagensSituacoesDispensacao(admNew);
							} catch (ApplicationBusinessException e) {
								admNew.setIndSituacao(indSit);
								throw e;
							}
						}	
					}
				} else {
					// Insere - Validacao
					getFarmaciaFacade().criaDispMdtoTriagemPrescricao(
							admNew, nomeMicrocomputador);
				}
			}
			// Flush com as operacoes de insert/update
			getAfaDispensacaoMdtosDAO().flush();
		}
	}
	
	private IFarmaciaFacade getFarmaciaFacade(){
		return this.farmaciaFacade;
	}
	
	protected IFarmaciaDispensacaoFacade getFarmaciaDispensacaoFacade() {
		return this.farmaciaDispensacaoFacade;
	}
	
	//********PROCEDURES E FUNCTIONS********
	/**
	 * @ORADB MPMP_POPULA_MEDICAMENTO
	 * @param dispMdto
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String obterDescricaoMedicamentoPrescrito(
			MpmPrescricaoMedica prescricaoMedica, MpmItemPrescricaoMdto itemPrescricao) {
		
		// Integer pAtdSeq = prescricaoMedica.getId().getAtdSeq();
		//Removido pq a prescricao não é utlizada
		Integer pSeq = 0;//prescricaoMedica.getId().getSeq();
		
		Integer vPmdAtdSeq = itemPrescricao.getId().getPmdAtdSeq();
		Long vPmdSeq = itemPrescricao.getId().getPmdSeq();
		Integer vMatCodigo = itemPrescricao.getId().getMedMatCodigo();
		Short vSeqItem = itemPrescricao.getId().getSeqp();
		
		VMpmPrescrMdtos medicamento = getPrescricaoMedicaFacade().obtemPrescMdto(
				vPmdAtdSeq, vPmdSeq, vMatCodigo, vSeqItem);
		
		if(!"S".equalsIgnoreCase(medicamento.getId().getIndSolucao())) {
			MpmItemPrescricaoMdto itemPrescMdto = getPrescricaoMedicaFacade().obterMpmItemPrescricaoMdtoPorChavePrimaria(new MpmItemPrescricaoMdtoId(vPmdAtdSeq, vPmdSeq, vMatCodigo, vSeqItem));
			MpmItemPrescricaoMdto itemPrescMdtoPai = getPrescricaoMedicaFacade().obterItemMedicamentoNaoDiluente(itemPrescMdto.getPrescricaoMedicamento().getItensPrescricaoMdtos());
			medicamento = getPrescricaoMedicaFacade().obtemPrescMdto(itemPrescMdtoPai.getId().getPmdAtdSeq(), itemPrescMdtoPai.getId().getPmdSeq(), itemPrescMdtoPai.getId().getMedMatCodigo(), itemPrescMdtoPai.getId().getSeqp());
		}
		
		String vDescTva = null;
		String vTipoItem = null;
		
		MpmTipoFrequenciaAprazamento frequencia = medicamento.getTipoFrequenciaAprazamento();
		String vDescTfq = frequencia.getDescricao();
		String vSinTfq = frequencia.getSintaxe();
		
		if(medicamento.getTipoVelocidadeAdministracao() !=null){
			vDescTva = medicamento.getTipoVelocidadeAdministracao().getDescricao();
		}
		
		if("S".equalsIgnoreCase(medicamento.getId().getIndSolucao())) {
			vTipoItem = "S:";
		} else {
			vTipoItem = "M:";
		}
		
		StringBuffer vSintaxeMdto = new StringBuffer(60);
		
		//TODO VERIFICAR ALTERNATIVA A DESCRICAO_EDIT DA VIEW, ESTA VINDO NULO PARA ALGUNS ITENS PRESCRITOS
		if (medicamento.getId().getMedDescricaoEdit() != null 
				&& medicamento.getId().getMedDescricaoEdit().trim().length() > 0) {
			vSintaxeMdto.append(medicamento.getId().getMedDescricaoEdit());
		} else {
			vSintaxeMdto.append("");
		}
		
		if(itemPrescricao.getObservacao() != null) {
			vSintaxeMdto.append(" : ").append(itemPrescricao.getObservacao()).append("; "); 
		} else {
			vSintaxeMdto.append("; ");
		}
		
		if(medicamento.getId().getUnidDoseEdit() !=null){
			vSintaxeMdto.append("DS=").append(medicamento.getId().getUnidDoseEdit());
		} else{
			vSintaxeMdto.append("DS=");
		}
		
		if("S".equalsIgnoreCase(medicamento.getId().getIndSolucao())) {
				mpmpFormataLinha(vSintaxeMdto, vTipoItem, String.valueOf(pSeq), null, "N");
		}
		
		if("S".equalsIgnoreCase(medicamento.getId().getIndSolucao())) {
			vSintaxeMdto.append(medicamento.getId().getViaAdministracao()).append("; ");
		} else {
			vSintaxeMdto.append(' ').append(medicamento.getId().getViaAdministracao()).append("; "); 
		}
		
		if(vSinTfq !=null) {
			vDescTfq = frequencia.getDescricaoSintaxeFormatada(medicamento.getId().getFrequencia());
		}
		
		vSintaxeMdto.append(vDescTfq).append("; ");
		
		if(medicamento.getId().getHrInicioAdm() !=null) {
			vSintaxeMdto.append("I=").append(DateUtil.obterDataFormatada(medicamento.getId().getHrInicioAdm(), "hh24:mm")).append(" h; "); 
		}
		
		StringBuffer vDiluente = null;
		String vDilMedConcentracao = null;
		String vDilUmmDescricao = null;
		
		if(medicamento.getId().getMedMatCodigoDiluente() !=null){
			AfaMedicamento diluente = getAfaMedicamentoDAO().obterPorChavePrimaria(medicamento.getId().getMedMatCodigoDiluente());
			if(diluente== null || diluente.getMpmUnidadeMedidaMedicas() == null){
				vDiluente = new StringBuffer();
			}else{
				vDiluente = new StringBuffer(diluente.getDescricao());
				vDilMedConcentracao = diluente.getConcentracaoFormatada();
				vDilUmmDescricao = diluente.getMpmUnidadeMedidaMedicas().getDescricao();
			}
			
			if(vDilMedConcentracao !=null) {
				vDiluente.append(' ').append(vDilMedConcentracao);
			}
			
			if(vDilUmmDescricao !=null) {
				vDiluente.append(' ').append(vDilUmmDescricao);
			}
		}
		
		if(vDiluente != null && medicamento.getId().getVolumeDiluenteMl() !=null) {
			vSintaxeMdto.append(" Diluir em ").append(medicamento.getId().getVolumeDiluenteMl()).append(" ml de ").append(vDiluente).append("; ");
		}
		
		if(vDiluente != null && medicamento.getId().getVolumeDiluenteMl() ==null) {
			vSintaxeMdto.append(" Diluir em ").append(vDiluente).append("; ");
		}
		
		if(vDiluente == null && medicamento.getId().getVolumeDiluenteMl() !=null) {
			vSintaxeMdto.append(" Diluir em ").append(medicamento.getId().getVolumeDiluenteMl()).append(" ml; ");
		}
		
		if(medicamento.getId().getQtdeCorrer() !=null){
			vSintaxeMdto.append(" Correr em ").append(medicamento.getId().getQtdeCorrer().toString()).append(" ml; ");
			if(medicamento.getId().getUnidHorasCorrer() == null || "H".equals(medicamento.getId().getUnidHorasCorrer())) {
				vSintaxeMdto.append(" horas; ");
			} else {
				vSintaxeMdto.append(" minutos; ");
			}
		}
		
		if(medicamento.getId().getGotejo() != null) {
			vSintaxeMdto.append("Gotejo ").append(medicamento.getId().getGotejo()).append(' ').append(vDescTva).append("; ");
		}
		
		if("S".equalsIgnoreCase(medicamento.getId().getIndBombaInfusao())) {
			vSintaxeMdto.append("BI; ");
		}
		
		if("S".equalsIgnoreCase(medicamento.getId().getIndSeNecessario())) {
			vSintaxeMdto.append("Se Necessário; ");
		}
		
		if(medicamento.getId().getObservacao() != null) {
			vSintaxeMdto.append(medicamento.getId().getObservacao()).append("; ");
		}
		
		if("S".equalsIgnoreCase(medicamento.getId().getIndSolucao())) {
			mpmpFormataLinha(vSintaxeMdto, vTipoItem, String.valueOf(pSeq), null, "S");
		} else {
			mpmpFormataLinha(vSintaxeMdto, vTipoItem, String.valueOf(pSeq), vTipoItem, "S");
		}

		return vSintaxeMdto.toString();
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	/**
	 * @ORADB MPMP_FORMATA_LINHA
	 * @param string2 
	 * @param object 
	 * @param string 
	 * @param vTipoItem 
	 * @param vSintaxeMdto 
	 * @return
	 */
	private String mpmpFormataLinha(StringBuffer vSintaxeMdto, String vTipoItem, String string, Object object, String string2){
		return null;
	}

	// ********UTIL********

	public AfaDispensacaoMdtos buscaDispensacaoOriginal(
			AfaDispensacaoMdtos admNew,
			List<AfaDispensacaoMdtos> medicamentosDispensadosOriginal) {

		AfaDispensacaoMdtos admOld = null;

		for (int i = 0; i < medicamentosDispensadosOriginal.size(); i++) {
			AfaDispensacaoMdtos itemOriginal = medicamentosDispensadosOriginal
					.get(i);
			if (admNew.equals(itemOriginal)) {
				admOld = itemOriginal;
				break;
			}
		}
		return admOld;
	}

	public List<AfaDispensacaoMdtos> removeDispensacaoesNaoPreenchidas(List<AfaDispensacaoMdtos> medicamentosDispensadosModificados) {
		
		List<AfaDispensacaoMdtos> mdtosDispensadosModificadosTemp = new ArrayList<AfaDispensacaoMdtos>();
		for (int i = 0; i < medicamentosDispensadosModificados.size(); i++) {
			AfaDispensacaoMdtos dispMdtoTemp = medicamentosDispensadosModificados.get(i);
			if (dispMdtoTemp.getMedicamento() != null){
				mdtosDispensadosModificadosTemp.add(dispMdtoTemp);
			}
		}
		
		return mdtosDispensadosModificadosTemp;
	}
	
	public void verificaCamposObrigatorios(List<AfaDispensacaoMdtos> medicamentosDispensadosModificados) throws ApplicationBusinessException {
		for (int i = 0; i < medicamentosDispensadosModificados.size(); i++) {
			AfaDispensacaoMdtos admNew = medicamentosDispensadosModificados.get(i);
			if (admNew.getMedicamento() != null) {
				admNew.setIndexItemSendoAtualizado(Boolean.TRUE);
				if (admNew.getQtdeDispensada() == null) {
					throw new ApplicationBusinessException(MovimentacaoTriagemDispensacaoMdtosRNExceptionCode.MESSAGE_MOV_DISPMDTOS_QTDE_DISP_OBRIGATORIO);
				}
				if (admNew.getUnidadeFuncional() == null || admNew.getUnidadeFuncional().getSeq() == null) {
					throw new ApplicationBusinessException(MovimentacaoTriagemDispensacaoMdtosRNExceptionCode.MESSAGE_MOV_DISPMDTOS_FARMACIA_OBRIGATORIO);
				}
				if (admNew.getIndSituacao() == null) {
					throw new ApplicationBusinessException(MovimentacaoTriagemDispensacaoMdtosRNExceptionCode.MESSAGE_MOV_DISPMDTOS_IND_SITUACAO_OBRIGATORIO);
				}
				admNew.setIndexItemSendoAtualizado(Boolean.FALSE);
			}	
		}
	}
	
	/**
	 * Verifica se a triagem (movimentacao) sofreu alteração na tela
	 * @param admOld
	 * @param admNew
	 * @return
	 */
	public boolean verificaSeSofreuAlteracao(
			AfaDispensacaoMdtos admOld, AfaDispensacaoMdtos admNew) {
		if (!CoreUtil.igual(admOld.getQtdeDispensada(), admNew.getQtdeDispensada())
				|| !CoreUtil.igual(admOld.getTipoOcorrenciaDispensacao(), admNew.getTipoOcorrenciaDispensacao())
				|| !CoreUtil.igual(admOld.getUnidadeFuncional(), admNew.getUnidadeFuncional())
				|| !CoreUtil.igual(admOld.getIndSituacao(), admNew.getIndSituacao())
				|| !CoreUtil.igual(admOld.getMedicamento(), admNew.getMedicamento())) {
			return true;
		} else {
			return false;
		}
	}

	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
		return afaDispensacaoMdtosDAO;
	}
	
	private AfaMedicamentoDAO getAfaMedicamentoDAO(){
		return afaMedicamentoDAO;
	}

}
