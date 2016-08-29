package br.gov.mec.aghu.paciente.prontuarioonline.business;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class IncluiNotasPOLON  extends BaseBusiness implements Serializable {

	@EJB
	private IncluiNotasPOLRN incluiNotasPOLRN;
	
	private static final Log LOG = LogFactory.getLog(IncluiNotasPOLON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
 	private static final long serialVersionUID = 8290899081749420427L;

 	public List<MamNotaAdicionalEvolucoes> pesquisarNotasPorCodigoPaciente(Integer codigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws ApplicationBusinessException {
	 List<MamNotaAdicionalEvolucoes> notas = getAmbulatorioFacade().pesquisarNotasAdicionaisEvolucoesPorCodigoPaciente(codigo, firstResult, maxResult, orderProperty, asc);
		for(MamNotaAdicionalEvolucoes nt : notas){
			nt.setNomeConsProf((String) getPrescricaoMedicaFacade().buscaConsProf(nt.getServidorValida())[1]);
		}
	return notas;
 	}


	public Long pesquisarNotasPorCodigoPacienteCount(Integer codigo) {
		return getAmbulatorioFacade().pesquisarNotasAdicionaisEvolucoesPorCodigoPacienteCount(codigo);
	}
	
	public MamNotaAdicionalEvolucoes incluirNotaPOL(String textoIncluiNota, AipPacientes paciente) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		MamNotaAdicionalEvolucoes registroInclusao = new MamNotaAdicionalEvolucoes();
		
		Short pSeqProcesso = getIncluiNotasPOLRN().mamcGetProcNaPrj();
		
		Boolean validaProcesso = getAmbulatorioFacade().validarProcesso(null, null, pSeqProcesso);
		
		if (validaProcesso != null && validaProcesso){
			registroInclusao.setDescricao("Notas Adicionais de Pesquisa \n" + textoIncluiNota);
		}else {
			registroInclusao.setDescricao(textoIncluiNota);
		}
		
		registroInclusao.setDescricao(textoIncluiNota);
		Date d = new Date();
		registroInclusao.setDthrCriacao(d);
		registroInclusao.setDthrValida(d);
		registroInclusao.setPendente(DominioIndPendenteAmbulatorio.V);
		registroInclusao.setPaciente(paciente);
		registroInclusao.setImpresso(Boolean.FALSE); 
		
		RapServidores servidor = servidorLogado;
		registroInclusao.setServidor(servidor);
		registroInclusao.setServidorValida(servidor);

		this.getAmbulatorioFacade().inserirNotaAdicionalEvolucoes(registroInclusao); 
		
		return registroInclusao;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}

	private IncluiNotasPOLRN getIncluiNotasPOLRN(){
		return incluiNotasPOLRN;
	}
	
	private IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}


	public Boolean validarBotaoAdicionarIncluirNotasPOL() throws ApplicationBusinessException {
		Short procEvo = getAmbulatorioFacade().retornarCodigoProcessoEvolucao();
		Short procNaPrj = getIncluiNotasPOLRN().mamcGetProcNaPrj();
		Boolean validaProcessoProcEvo = getAmbulatorioFacade().validarProcesso(null, null, procEvo);
		Boolean validaProcessoProcPrj = getAmbulatorioFacade().validarProcesso(null, null, procNaPrj);
		if (validaProcessoProcEvo || validaProcessoProcPrj)  {
			return Boolean.TRUE;
		}
		else{
			return Boolean.FALSE;
		}
	}

	/*public Boolean validarLinkDiagnosticosIncluirNotasPOL() throws ApplicationBusinessException {
		Short procDiag = getIncluiNotasPOLRN().mamcGetProcDiag();
		Boolean validaProcessoProcDiag = getAmbulatorioFacade().validarProcesso(null, null, procDiag);
		if (validaProcessoProcDiag)  {
			return Boolean.TRUE;
		}
		else{
			return Boolean.FALSE;
		}
	}*/

	public void gravarJustificarExclusaoIncluirNotasPol(
			MamNotaAdicionalEvolucoes notaAdicionalEvolucao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date dataAtual = new Date();
		notaAdicionalEvolucao.setDthrValidaMvto(dataAtual);
		notaAdicionalEvolucao.setDthrMvto(dataAtual);
		
		RapServidores servidor = servidorLogado;
		//notaAdicionalEvolucao.setServidor(servidor);
		//notaAdicionalEvolucao.setServidorValida(servidor);
		notaAdicionalEvolucao.setServidorMvto(servidor);
		notaAdicionalEvolucao.setServidorValidaMvto(servidor);
		
		getAmbulatorioFacade().atualizarMamNotaAdicionalEvolucao(notaAdicionalEvolucao);
		
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}