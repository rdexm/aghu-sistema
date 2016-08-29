package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.ImprimirRelatorioMedicoVO;
import br.gov.mec.aghu.ambulatorio.vo.MamRelatorioVO;
import br.gov.mec.aghu.blococirurgico.action.RelatorioLaudoAIHController;
import br.gov.mec.aghu.blococirurgico.action.RelatorioLaudoAIHSolicController;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MptPrescricaoPacienteId;
import br.gov.mec.aghu.paciente.prontuarioonline.action.ImprimeAltaAmbulatorialPolController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Controller criado devido excesso de linhas em AtenderPacientesAgendadosController
 * @author pedro.santiago
 *
 */
public class AtenderPacientesAgendadosAuxiliarController extends ActionController{

	private static final long serialVersionUID = 9216783656232560659L;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	ImprimeRelatorioMedicoController imprimeRelatorioMedicoController;

	@Inject
	private ImprimeAltaAmbulatorialPolController imprimeAltaAmbulatorialPolController;

	@Inject
	private RelatorioLaudoAIHController relatorioLaudoAIHController;
		
	@Inject
	private RelatorioSolicitacaoProcedimentoController relatorioSolicitacaoProcedimentoController;

	@Inject
	private RelatorioTratamentoFisiatricoController relatorioTratamentoFisiatricoController;

	@Inject
	private RelatorioLaudoAIHSolicController relatorioLaudoAIHSolicController;

	@Inject
	private RelatorioGuiaAtendimentoUnimedController relatorioGuiaAtendimentoUnimedController;

	public void verificarImpressaoRelatorios(DocumentosPacienteVO documento) throws BaseException, JRException, SystemException, IOException {

		//Tratamento Fisiátrico
		verificarImpressaoTratamentoFisiatrico(documento);
		
		verificarRelatorioMedico(documento);
		
		//Alta Sumarios
		verificarAltaSumario(documento);
		//LaudoAih
		verificarLaudoAih(documento); 
		//Solicitacao Procedimento 
		verificaMamSolicProcedimento(documento);
		//Guia Atendimento Unimed
		verificaGuiaAtendimentoUnimed(documento);
	}

	/**
	 * Imprime relatório de tratamento fisiátrico.
	 * @param documento
	 */
	private void verificarImpressaoTratamentoFisiatrico(DocumentosPacienteVO documento) {		
		if (documento.getRelatorioTratamentoFisiatricoVO() != null && documento.getSelecionado()) {
			relatorioTratamentoFisiatricoController.setRelatorioTratamentoFisiatricoVO(documento.getRelatorioTratamentoFisiatricoVO());
			relatorioTratamentoFisiatricoController.setDescricaoDocumento(documento.getDescricao());
			relatorioTratamentoFisiatricoController.directPrint();
			if (!documento.getImprimiu()) {
				MptPrescricaoPacienteId mptPrescricaoPacienteId = documento.getRelatorioTratamentoFisiatricoVO().getPrescricaoPaciente().getId();
				this.ambulatorioFacade.atualizarIndPrcrImpressaoTratamentoFisiatrico(mptPrescricaoPacienteId.getAtdSeq(), mptPrescricaoPacienteId.getSeq());
			}
		}
	}

	/**
	 * Verifica se possuí Relatórios Médicos a serem impressos
	 * 
	 * @param documento
	 */
	private void verificarRelatorioMedico(DocumentosPacienteVO documento) {
		if (documento.getMamRelatorioVO() != null && documento.getSelecionado() != null && documento.getSelecionado()) {

			ImprimirRelatorioMedicoVO relatorioMedicoVO = new ImprimirRelatorioMedicoVO();

			relatorioMedicoVO.setEndereco(this.parametroFacade
					.obterAghParametroPorNome("P_HOSPITAL_END_COMPLETO_LINHA1")
					.getVlrTexto());
			relatorioMedicoVO.setCep(this.parametroFacade
					.obterAghParametroPorNome("P_CEP_PADRAO").getVlrNumerico()
					.intValue());
			relatorioMedicoVO.setCidade(this.parametroFacade
					.obterAghParametroPorNome("P_HOSPITAL_END_CIDADE")
					.getVlrTexto());
			relatorioMedicoVO.setUf(this.parametroFacade
					.obterAghParametroPorNome("P_UF_PADRAO").getVlrTexto());
			relatorioMedicoVO.setTelefone(this.parametroFacade
					.obterAghParametroPorNome("P_HOSPITAL_END_FONE")
					.getVlrTexto());

			relatorioMedicoVO.setNome(documento.getMamRelatorioVO()
					.getNomeMedico());
			relatorioMedicoVO.setUsuario(documento.getMamRelatorioVO()
					.getUsuario());
			relatorioMedicoVO.setIdentificacao(documento.getMamRelatorioVO()
					.getSeq().intValue());
			relatorioMedicoVO.formatarEnderecoParteDois();
			MamRelatorioVO mamRelatorioVO = this.ambulatorioFacade
					.obterMamRelatorioVOPorSeq(documento.getMamRelatorioVO()
							.getSeq());

			if (mamRelatorioVO != null) {
				if (mamRelatorioVO.getNomePac() != null
						&& !mamRelatorioVO.getNomePac().trim().isEmpty()) {
					relatorioMedicoVO.setNomePaciente(mamRelatorioVO
							.getNomePac());
				}

				if (mamRelatorioVO.getDescricao() != null
						&& !mamRelatorioVO.getDescricao().trim().isEmpty()) {
					relatorioMedicoVO.setDescricao(mamRelatorioVO
							.getDescricao());
				}

				if (documento.getNumeroConsulta() != null) {
					String especialidade = ambulatorioFacade.obterEspecialidadePorConsulta(documento.getNumeroConsulta());
					if (especialidade != null) {
						relatorioMedicoVO.setEspecialidade(especialidade);
				}
			}
			}

			imprimeRelatorioMedicoController
					.setImprimirRelatorioMedicoVO(relatorioMedicoVO);
			imprimeRelatorioMedicoController.directPrint();

			if (!documento.getImprimiu()) {
				this.ambulatorioFacade
						.atualizarIndImpressoRelatorioMedico(documento
								.getMamRelatorioVO().getSeq());
			}
		}

	}
	/**
	 * VERIFICA SE POSSUI DOCUMENTO SOLICITACAO DE PROCEDIMENTO A IMPRIMIR
	 * @param documento
	 */
	public void verificaMamSolicProcedimento(DocumentosPacienteVO documento) {
		if (documento.getMamSolicProcedimento()!= null && documento.getSelecionado() != null && documento.getSelecionado()) {
			relatorioSolicitacaoProcedimentoController.setSolicitacaoProcedimento(documento.getMamSolicProcedimento());
			relatorioSolicitacaoProcedimentoController.setDescricaoDocumento(documento.getDescricao());
			relatorioSolicitacaoProcedimentoController.directPrint();
			if (!documento.getImprimiu()) {
				this.ambulatorioFacade.atualizarIndImpressoSolicitacaoProcedimento(documento.getMamSolicProcedimento().getSeq());
			}
		}
	}
	
	/**
	 * VERIFICA SE POSSUI DOCUMENTO ALTA SUMARIO A IMPRIMIR
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws JRException 
	 * @throws BaseException 
	 */
	public void verificarAltaSumario(DocumentosPacienteVO documento) throws BaseException, JRException, SystemException, IOException{
		if(documento.getAltaSumario()!= null && documento.getSelecionado() != null && documento.getSelecionado()){
			imprimeAltaAmbulatorialPolController.impressaoDireta(documento.getAltaSumario().getSeq());	
			if(!documento.getImprimiu()){
				ambulatorioFacade.atualizarIndImpressaoAltaAmb(documento.getAltaSumario().getSeq()); 
			}
		}
		
	}
	
	/**
	 * VERIFICA SE POSSUI DOCUMENTO LAUDO AIH A IMPRIMIR
	 * @throws ApplicationBusinessException 
	 */
	public void verificarLaudoAih(DocumentosPacienteVO documento) throws ApplicationBusinessException{
		if(documento.getLaudoAih()!=null && documento.getSelecionado() != null && documento.getSelecionado()){
			relatorioLaudoAIHController.setSeq(documento.getLaudoAih().getSeq().longValue());
			relatorioLaudoAIHController.setCodigoPac(documento.getLaudoAih().getPaciente().getCodigo());
			if (documento.getLaudoAih().getServidorValida() != null) {
				relatorioLaudoAIHController.setMatricula(documento.getLaudoAih().getServidorValida().getId().getMatricula());
				relatorioLaudoAIHController.setVinCodigo(documento.getLaudoAih().getServidorValida().getId().getVinCodigo());
			} else {
				relatorioLaudoAIHController.setMatricula(documento.getLaudoAih().getServidorValida().getId().getMatricula());
				relatorioLaudoAIHController.setVinCodigo(documento.getLaudoAih().getServidorValida().getId().getVinCodigo());
			}
			relatorioLaudoAIHController.directPrint(true);
			if(documento.getLaudoAih().getMaterialSolicitado() != null){
				relatorioLaudoAIHSolicController.setCodigoPac(documento.getLaudoAih().getPacCodigo());
				relatorioLaudoAIHSolicController.setMatricula(documento.getLaudoAih().getServidor().getId().getMatricula());
				relatorioLaudoAIHSolicController.setVinCodigo(documento.getLaudoAih().getServidor().getId().getVinCodigo());
				relatorioLaudoAIHSolicController.setMaterialSolicitado(documento.getLaudoAih().getMaterialSolicitado());
				relatorioLaudoAIHSolicController.directPrint(true);	 	
			}
			if(!documento.getImprimiu()){
				ambulatorioFacade.atualizarIndImpressaoLaudoAIH(documento.getLaudoAih().getSeq());  
			}
		}
	}

	public void verificaGuiaAtendimentoUnimed(DocumentosPacienteVO documento) throws ApplicationBusinessException, JRException {
		if (documento.getSelecionado() != null && documento.getSelecionado() && documento.getGuiaAtendimentoUnimed() != null && documento.getGuiaAtendimentoUnimed()) {
			
			AacConsultas consulta = this.ambulatorioFacade.obterAacConsulta(documento.getNumeroConsulta());
			if (consulta != null) {
				relatorioGuiaAtendimentoUnimedController.setConNumero(documento.getNumeroConsulta());
			}
			
			relatorioGuiaAtendimentoUnimedController.directPrint();
		}
	}
	
	/**
	 * VERIFICA SE POSSUI DOCUMENTO LAUDO AIH A IMPRIMIR
	 */
	public void verificaLaudoSolic(DocumentosPacienteVO documento){
			if(documento.getLaudoAIHSolicVO()!=null && documento.getSelecionado()){
				if(documento.getMaterialSolicitado()!=null && documento.getSelecionado()){
				relatorioLaudoAIHSolicController.setCodigoPac(documento.getPacCodigo());
				relatorioLaudoAIHSolicController.setMatricula(documento.getLaudoAIHSolicVO().getServidor().getId().getMatricula());
				relatorioLaudoAIHSolicController.setVinCodigo(documento.getLaudoAIHSolicVO().getServidor().getId().getVinCodigo());
				relatorioLaudoAIHSolicController.setMaterialSolicitado(documento.getMaterialSolicitado());
				relatorioLaudoAIHSolicController.directPrint(true);	 		
			}
		}
	}	
}