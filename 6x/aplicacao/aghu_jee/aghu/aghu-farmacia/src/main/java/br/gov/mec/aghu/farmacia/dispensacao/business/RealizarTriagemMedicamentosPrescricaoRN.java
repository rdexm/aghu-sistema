package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoOcorDispensacaoDAO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.VMpmPrescrMdtos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

@Stateless
public class RealizarTriagemMedicamentosPrescricaoRN extends BaseBusiness
		implements Serializable {

private static final Log LOG = LogFactory.getLog(RealizarTriagemMedicamentosPrescricaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AfaMedicamentoDAO afaMedicamentoDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private AfaTipoOcorDispensacaoDAO afaTipoOcorDispensacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 964413893925606489L;
	//private Short SEQ_TIPO_OCOR_MEDICAMENTO_EM_GELADEIRA = Short.valueOf("4");

	public enum RealizarTriagemMedicamentosPrescricaoRNExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_ERRO_PARAMETRO_TIPO_OCORRENCIA_GELADEIRA
	}
	
	/**
	 * @ORADB MPMP_POPULA_MEDICAMENTO
	 * @param dispMdto
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String obterDescricaoMedicamentoPrescrito(
			AfaDispensacaoMdtos dispMdto) {
		
//		Integer pAtdSeq = dispMdto.getPrescricaoMedica().getId().getAtdSeq();
		Integer pSeq = dispMdto.getPrescricaoMedica().getId().getSeq();
		
		Integer vPmdAtdSeq = dispMdto.getItemPrescricaoMdto().getId().getPmdAtdSeq();
		Long vPmdSeq = dispMdto.getItemPrescricaoMdto().getId().getPmdSeq();
		Integer vMatCodigo = dispMdto.getItemPrescricaoMdto().getId().getMedMatCodigo();
		Short vSeqItem = dispMdto.getItemPrescricaoMdto().getId().getSeqp();
		
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
			if("S".equalsIgnoreCase(medicamento.getId().getIndSolucao())) {
				vTipoItem = "S:";
			} else {
				vTipoItem = "M:";
			}
		}
		
		StringBuffer vSintaxeMdto = new StringBuffer(70);
		vSintaxeMdto.append(medicamento.getId().getMedDescricaoEdit());
		
		if(medicamento.getId().getDescrComplementar() != null) {
			vSintaxeMdto.append(" : " + medicamento.getId().getDescrComplementar() + "; "); 
		} else {
			vSintaxeMdto.append("; ");
		}
		
		vSintaxeMdto.append("DS=" + medicamento.getId().getUnidDoseEdit() + "; ");
		
		if("S".equalsIgnoreCase(medicamento.getId().getIndSolucao())) {
				mpmpFormataLinha(vSintaxeMdto, vTipoItem, String.valueOf(pSeq), null, "N");
		}
		
		if("S".equalsIgnoreCase(medicamento.getId().getIndSolucao())) {
			vSintaxeMdto.append(medicamento.getId().getViaAdministracao()).append("; ");
		} else {
			vSintaxeMdto.append(' ').append(medicamento.getId().getViaAdministracao()).append("; "); 
		}
		
		if(vSinTfq !=null) {
			vDescTfq = frequencia.getDescricaoSintaxeFormatadaNotCase(medicamento.getId().getFrequencia());
		}
		
		vSintaxeMdto.append(vDescTfq).append("; ");
		
		if(medicamento.getId().getHrInicioAdm() !=null) {
			vSintaxeMdto.append("In=" + DateUtil.obterDataFormatada(medicamento.getId().getHrInicioAdm(), "HH:mm") + " h; "); 
		}
		
		StringBuffer vDiluente = null;
		String vDilMedConcentracao = null;
		String vDilUmmDescricao = null;
		
		if(medicamento.getId().getMedMatCodigoDiluente() !=null){
			AfaMedicamento diluente = getAfaMedicamentoDAO().obterPorChavePrimaria(medicamento.getId().getMedMatCodigoDiluente());
			if(diluente== null){
				vDiluente = new StringBuffer();
			} else{
				vDiluente = new StringBuffer(diluente.getDescricao());
				vDilMedConcentracao = diluente.getConcentracaoFormatada();
				vDilUmmDescricao = diluente.getMpmUnidadeMedidaMedicas()!=null?diluente.getMpmUnidadeMedidaMedicas().getDescricao():"";
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
		
		if(medicamento.getId().getQtdeCorrer() !=null) {
			vSintaxeMdto.append(" Correr em ").append(medicamento.getId().getQtdeCorrer());
			if(medicamento.getId().getUnidHorasCorrer() == null || "H".equalsIgnoreCase(medicamento.getId().getUnidHorasCorrer())) {
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
	
	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	protected AfaMedicamentoDAO getAfaMedicamentoDAO(){
		return afaMedicamentoDAO;
	}
	
	/**
	 * @ORADB MPMP_FORMATA_LINHA
	 * método criado somente para deixar explicito a chamada
	 * no forms quebrava a linha,
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

	/**
	 * Função isolada da mpmp_popula_medicamento
	 * A função popula_medicamento possuia esta função no final da sua execução.
	 * @param dispMdto
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public Boolean processaTipoOcorrenciaSeGeladeira(AfaDispensacaoMdtos dispMdto) throws ApplicationBusinessException {
		if(DominioSituacaoDispensacaoMdto.S.equals(dispMdto.getIndSituacao())){
			Boolean vIndGeladeira = dispMdto.getMedicamento().getIndGeladeira();
			if(vIndGeladeira){
				AghParametros tipoOcorGeladeira = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_OCORRENCIA_GELADEIRA);
				dispMdto.setTipoOcorrenciaDispensacao(getAfaTipoOcorDispensacaoDAO().obterPorChavePrimaria(tipoOcorGeladeira.getVlrNumerico().shortValue()));
				if(dispMdto.getTipoOcorrenciaDispensacao() != null){
					dispMdto.setSeqAfaTipoOcorSelecionada(dispMdto.getTipoOcorrenciaDispensacao().getSeq().toString());
				}
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	protected AfaTipoOcorDispensacaoDAO getAfaTipoOcorDispensacaoDAO(){
		return afaTipoOcorDispensacaoDAO;
	}
	
	private IParametroFacade getParametroFacade(){
		return parametroFacade;
	}

}
