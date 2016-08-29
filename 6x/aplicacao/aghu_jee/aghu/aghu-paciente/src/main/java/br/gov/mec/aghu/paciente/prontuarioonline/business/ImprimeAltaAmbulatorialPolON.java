package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolDiagnosticoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolEvolucaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolImpressaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltaAmbulatorialPolPrescricaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class ImprimeAltaAmbulatorialPolON extends BaseBusiness implements Serializable{


@EJB
private ImprimeAltaAmbulatorialPolRN imprimeAltaAmbulatorialPolRN;

private static final Log LOG = LogFactory.getLog(ImprimeAltaAmbulatorialPolON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAmbulatorioFacade ambulatorioFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 433923429495751285L;
	
	public enum ImprimeAltaAmbulatorialPolONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<AltaAmbulatorialPolImpressaoVO> recuperarAltaAmbuPolImpressaoVO(Long seqMamAltaSumario) throws ApplicationBusinessException {

		List<AltaAmbulatorialPolImpressaoVO> listaAltaAmbPolImprVO = getAmbulatorioFacade().recuperarAltaAmbuPolImpressaoVO(seqMamAltaSumario);

		for (AltaAmbulatorialPolImpressaoVO altaAmbPolImprVO: listaAltaAmbPolImprVO){	
			
			//Seta Data Nascimento 
			if (altaAmbPolImprVO.getDtNasc() != null){
				altaAmbPolImprVO.setDtNascimento(DateUtil.dataToString(altaAmbPolImprVO.getDtNasc(), "dd/MM/yyyy"));
			}

			//Seta Idade			
			altaAmbPolImprVO.setIdade(criaIdadeExtenso(altaAmbPolImprVO.getIdadeAnos(), altaAmbPolImprVO.getIdadeMeses().intValue(), altaAmbPolImprVO.getIdadeDias().intValue()));	

			//Seta Sexo
			if (altaAmbPolImprVO.getSexo() != null){
				altaAmbPolImprVO.setDescSexo(altaAmbPolImprVO.getSexo().getDescricao()) ;
			}

			//Seta Destino Alta
			if (altaAmbPolImprVO.getDestinoAlta() != null){
				altaAmbPolImprVO.setDescDestinoAlta(altaAmbPolImprVO.getDestinoAlta().getDescricao()) ;
			}

			//Seta Retorno Agenda
			if (altaAmbPolImprVO.getRetornoAgenda() != null){
				altaAmbPolImprVO.setDescRetornoAgenda(altaAmbPolImprVO.getRetornoAgenda().getDescricao()) ;
			}

			//Seta Descricao Especialidade Destino
			if (altaAmbPolImprVO.getDescEspDestino() != null){				
				StringBuffer descEspDestinoFormat = new StringBuffer("Especialidade: ")					
				.append(altaAmbPolImprVO.getDescEspDestino()); 		

				altaAmbPolImprVO.setDescEspDestino(descEspDestinoFormat.toString());				
			}

			//Seta Observacao Destino
			if (altaAmbPolImprVO.getObservacaoDestino() != null){				
				StringBuffer observacaoDestinoFormat = new StringBuffer("Observação: ")					
				.append(altaAmbPolImprVO.getObservacaoDestino()); 		

				altaAmbPolImprVO.setObservacaoDestino(observacaoDestinoFormat.toString());				
			}
			
			//Seta Numero da Consulta + Seq Alta Sumario
			
			StringBuffer conNumeroAlsSeq = new StringBuffer();
			
			if (altaAmbPolImprVO.getConNumero() != null) {
				conNumeroAlsSeq.append(altaAmbPolImprVO.getConNumero());
				conNumeroAlsSeq.append(',');
			}
			
			if (altaAmbPolImprVO.getAlsSeq() != null) {
				conNumeroAlsSeq.append(altaAmbPolImprVO.getAlsSeq());				
			}
			
			altaAmbPolImprVO.setConNumeroAlsSeq(conNumeroAlsSeq.toString()) ;
			
			//Seta Total Prescricoes

			altaAmbPolImprVO.setTotalPrescricoes(getAmbulatorioFacade().pesquisarAltaPrescricoesCount(seqMamAltaSumario));	
			

			//Seta Assinatura

			if (altaAmbPolImprVO.getMatricula() != null && altaAmbPolImprVO.getVinCodigo() != null)  {	

				altaAmbPolImprVO.setAssinatura(getImprimeAltaAmbulatorialPolRN().buscaAssinaturaMedicoCrm(altaAmbPolImprVO.getMatricula(),altaAmbPolImprVO.getVinCodigo()));
			}

			//Seta Lista de Diagnosticos
			altaAmbPolImprVO.setAltaAmbPolDiagnosticoList(processaDiagnostico(seqMamAltaSumario));

			//Seta Lista de Evolucoes
			altaAmbPolImprVO.setAltaAmbPolEvolucaoList(processaEvolucao(seqMamAltaSumario));

			//Seta Lista de Prescricoes
			altaAmbPolImprVO.setAltaAmbPolPrescricaoList(processaPrescricao(seqMamAltaSumario));

		}	
		
		if (listaAltaAmbPolImprVO.isEmpty()) {
			throw new ApplicationBusinessException(ImprimeAltaAmbulatorialPolONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		atualizaMamAltaSumario(seqMamAltaSumario);

		return listaAltaAmbPolImprVO;	

	}	

	private void atualizaMamAltaSumario(Long seqMamAltaSumario) {
		
		MamAltaSumario mamAltaSumario = getAmbulatorioFacade().obterMamAltaSumarioPorId(seqMamAltaSumario);	
		
		mamAltaSumario.setImpresso(Boolean.TRUE);
		
		getAmbulatorioFacade().atualizarAltaSumerio(mamAltaSumario);		
	}

	public String criaIdadeExtenso(Short anos, Integer meses, Integer dias){		
		StringBuffer idadeExt = new StringBuffer();
		if (anos!=null && anos > 1) {
			idadeExt.append(anos).append(" anos ");
		}else if (anos!=null && anos == 1) {
			idadeExt.append(anos).append(" ano ");
		}
		if (meses!=null && meses > 1) {
			idadeExt.append(meses).append(" meses ");
		}else if (meses!=null && meses == 1) {
			idadeExt.append(meses).append(" mês ");
		}
		if (dias!=null && dias > 1) {
			idadeExt.append(dias).append(" dias ");
		}else if (dias!=null && dias == 1) {
			idadeExt.append(dias).append(" dia ");
		}
		return idadeExt.toString();
	}
	
	private List<AltaAmbulatorialPolEvolucaoVO> processaEvolucao(Long seqMamAltaSumario) {
		List<AltaAmbulatorialPolEvolucaoVO> altaAmbPolEvolucaoList =  getAmbulatorioFacade().recuperarAltaAmbPolEvolucaoList(seqMamAltaSumario);
		return altaAmbPolEvolucaoList;
	}


	private List<AltaAmbulatorialPolDiagnosticoVO> processaDiagnostico(Long seqMamAltaSumario) {
		List<AltaAmbulatorialPolDiagnosticoVO> altaAmbPolDiagnosticoList =  getAmbulatorioFacade().recuperarAltaAmbPolDiagnosticoList(seqMamAltaSumario);
		for(AltaAmbulatorialPolDiagnosticoVO vo: altaAmbPolDiagnosticoList){
			vo.setDescricao(vo.getDescricao() + (vo.getComplemento() != null ? (" - " + vo.getComplemento()) : ""));
		}
		return altaAmbPolDiagnosticoList;
	}
	
	
	private List<AltaAmbulatorialPolPrescricaoVO> processaPrescricao(Long seqMamAltaSumario) {
		List<AltaAmbulatorialPolPrescricaoVO> altaAmbPolPrescricaoList =  getAmbulatorioFacade().recuperarAltaAmbPolPrescricaoList(seqMamAltaSumario);
		return altaAmbPolPrescricaoList;
	}


	private ImprimeAltaAmbulatorialPolRN getImprimeAltaAmbulatorialPolRN() {
		return imprimeAltaAmbulatorialPolRN;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	public void validarRelatorioAltasAmbulatorias(Long seqMamAltaSumario) throws ApplicationBusinessException {
		Long count = getAmbulatorioFacade().recuperarAltaAmbuPolImpressaoVOCount(seqMamAltaSumario);
		if(count == 0){
			throw new ApplicationBusinessException(ImprimeAltaAmbulatorialPolONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
	}	
	
}
