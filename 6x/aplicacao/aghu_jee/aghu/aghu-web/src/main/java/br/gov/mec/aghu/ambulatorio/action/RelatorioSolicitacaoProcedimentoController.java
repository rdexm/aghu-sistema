package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioSolicitacaoProcedimentoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamSolicProcedimento;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

/**
 * Classe Controller para o relatorio de Solicitacao de Procedimento estória #43087
 */
public class RelatorioSolicitacaoProcedimentoController extends ActionReport {


	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	private static final long serialVersionUID = -2691118544724548015L;

	private static final Log LOG = LogFactory.getLog(RelatorioSolicitacaoProcedimentoController.class);

	private final String ESPACO_TRACO_ESPACO = " - ";

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private MamSolicProcedimento solicitacaoProcedimento;

	private String descricaoDocumento;

	private StreamedContent media;
	private String complemento = "";

	public MamSolicProcedimento getSolicitacaoProcedimento() {
		return solicitacaoProcedimento;
	}

	@Inject
	private SistemaImpressao sistemaImpressao;

	private String enderecoHospital = "";
	private String cepPadrao = "";
	private String ufPadrao = "";
	private String endCidade = "";
	private String orientacao = "";
	private String endFone = null;
	private String razaoSocial = "";
	private String orientacaoHorario="";
	@Override
	public void directPrint() {
		Short nroVias = solicitacaoProcedimento.getNroVias();
		if(nroVias != 0) {
			try {
				for(int i=0; i < nroVias; i++) {					
					DocumentoJasper documento = gerarDocumento();						
					this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());			
				}
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", descricaoDocumento);
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		} else {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INFORMACAO_NRO_VIAS");
		}
	}
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA1);
			enderecoHospital = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CEP_PADRAO);
			cepPadrao = aghParametros.getVlrNumerico().toString();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UF_PADRAO);
			ufPadrao = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
			endCidade = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_FONE);
			endFone = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ORIENTACAO_DOC_SOL_HEMO);
			orientacao = aghParametros.getVlrTexto();
			aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			razaoSocial = aghParametros.getVlrTexto();
			String[] orientacao2 = orientacao.split("\\.");
			orientacao = "";
			for(int i=0 ; i<orientacao2.length ;i++){
				if(i == 0){
					orientacaoHorario = orientacao2[i]+". ";
				}else{
					orientacao += orientacao2[i];
				}
			}
			this.formataComplementoEndereco();
			params.put("complemento", complemento);
			params.put("enderecoHospital", enderecoHospital);
			params.put("orientacaoHorario", orientacaoHorario);
			params.put("orientacao", orientacao);
			params.put("razaoSocial", razaoSocial);
			params.put("caminhoLogo", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images/logoClinicas2.jpg"));

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return params;
	}

	@Override
	protected List<RelatorioSolicitacaoProcedimentoVO> recuperarColecao() throws ApplicationBusinessException {
		List<RelatorioSolicitacaoProcedimentoVO> colecao = new ArrayList<RelatorioSolicitacaoProcedimentoVO>();
		colecao.add(obterDadosRelatorio());
		return colecao;
	}

	private RelatorioSolicitacaoProcedimentoVO obterDadosRelatorio() {
		// C2
		RelatorioSolicitacaoProcedimentoVO infoSolicProcedimento = ambulatorioFacade.obterInformacoesRelatorioSolicProcedimento(this.solicitacaoProcedimento.getConNumero());
		// C3
		RelatorioSolicitacaoProcedimentoVO dadosSolicProcedimento = ambulatorioFacade.obterDadosRelatorioSolicProcedimento(this.solicitacaoProcedimento.getSeq());
		// Adiciona especialidade a dadosSolicProcedimento
		dadosSolicProcedimento.setEspecialidade(ambulatorioFacade.obterEspecialidade(this.solicitacaoProcedimento.getConNumero()));
		// Adiciona nome do medico a dadosSolicProcedimento
		obterNomeMedico(dadosSolicProcedimento);
		// Atribui valores do retorno de infoSolicProcedimento a dadosSolicProcedimento
		atribuiInfoRelatorio(infoSolicProcedimento, dadosSolicProcedimento);
		return dadosSolicProcedimento;
	}

	private void obterNomeMedico(RelatorioSolicitacaoProcedimentoVO dadosSolicProcedimento) {
		String TRACO_SEPARADOR = "-";
		try {
			List<ConselhoProfissionalServidorVO> registroMedico = ambulatorioFacade.obterRegistroMedico(dadosSolicProcedimento.getSerMatriculaValida(), dadosSolicProcedimento.getSerVinCodigoValida());
			if (!registroMedico.isEmpty()) {
				String siglaConselho = registroMedico.get(0).getSiglaConselho();
				String numeroRegistroConselho = registroMedico.get(0).getNumeroRegistroConselho();
				String nomeMedico = registroMedico.get(0).getNome();
				String assinatura = "";
				dadosSolicProcedimento.setNomeMedico(nomeMedico);
				if (nomeMedico != null) {
					assinatura = nomeMedico;
				}
				if (siglaConselho != null) {
					assinatura = assinatura.concat(" " + TRACO_SEPARADOR + " " + siglaConselho);
				}
				if (numeroRegistroConselho != null) {
					assinatura = assinatura.concat(" " + numeroRegistroConselho);
				}
				dadosSolicProcedimento.setAssinatura(assinatura);
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void atribuiInfoRelatorio(RelatorioSolicitacaoProcedimentoVO infoSolicProcedimento, RelatorioSolicitacaoProcedimentoVO dadosSolicProcedimento) {
		if (infoSolicProcedimento.getProntuario() != null) {
			StringBuilder str = new StringBuilder(infoSolicProcedimento.getProntuario().toString());
			str.insert(str.length() - 1, "/");
			dadosSolicProcedimento.setProntuarioFormatado(str.toString());
		}
		dadosSolicProcedimento.setNome(infoSolicProcedimento.getNome());
		dadosSolicProcedimento.setGrdSeq(infoSolicProcedimento.getGrdSeq());
		dadosSolicProcedimento.setDtNascimento(infoSolicProcedimento.getDtNascimento());
		dadosSolicProcedimento.setDtConsulta(infoSolicProcedimento.getDtConsulta());
		dadosSolicProcedimento.setSigla(infoSolicProcedimento.getSigla());
		dadosSolicProcedimento.setConvenio(infoSolicProcedimento.getCspCnvCodigo().toString() + " " + infoSolicProcedimento.getDescricaoConvenio());
		dadosSolicProcedimento.setConNumero(infoSolicProcedimento.getConNumero());
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioSolicitacaoProcedimento.jasper";
	}

	private void formataComplementoEndereco() {
		complemento = "";
		if (endFone != null) {
			complemento = complemento.concat(" Fone: ".concat(endFone).concat(ESPACO_TRACO_ESPACO));
		}
		if (cepPadrao != null) {
			complemento = complemento.concat("CEP ").concat(cepPadrao).concat(ESPACO_TRACO_ESPACO);
		}
		if (endCidade != null) {
			complemento = complemento.concat(endCidade).concat(", ");
		}
		if (ufPadrao != null) {
			complemento = complemento.concat(ufPadrao);
		}
	}

	public void setSolicitacaoProcedimento(MamSolicProcedimento solicitacaoProcedimento) {
		this.solicitacaoProcedimento = solicitacaoProcedimento;
	}

	public String getDescricaoDocumento() {
		return descricaoDocumento;
	}

	public void setDescricaoDocumento(String descricaoDocumento) {
		this.descricaoDocumento = descricaoDocumento;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
}
