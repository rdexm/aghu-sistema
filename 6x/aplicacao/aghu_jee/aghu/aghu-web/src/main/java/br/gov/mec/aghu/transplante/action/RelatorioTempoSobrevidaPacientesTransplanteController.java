package br.gov.mec.aghu.transplante.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaSobrevidaTransplante;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoTransplanteCombo;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.util.RelatorioSobrevidaPacienteTransplanteVOComparator;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.FiltroTempoPermanenciaListVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoSobrevidaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioSobrevidaPacienteTransplanteVO;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioTempoSobrevidaPacientesTransplanteController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8811297485606862956L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioTempoSobrevidaPacientesTransplanteController.class);
	private DominioTipoOrgao tipoOrgao;
	private DominioTipoTransplanteCombo tipoTransplante;
	private DominioSituacaoTmo tipoTMO;
	private DominioTipoAlogenico tipoAlogenico;
	private AipPacientes paciente;
	
	
	
	
	private static final String REDIRECT_RELATORIO_SOBREVIDA_TRANSPLANTE_PDF = "relatorioTempoSobrevidaPacientesTransplantesPdf";
	private static final String REDIRECT_RELATORIO_SOBREVIDA_TRANSPLANTE = "relatorioTempoSobrevidaPacientesTransplantes";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";

	
	private boolean tipoTransplanteTMO;
	private boolean tipoTransplanteOrgaos;
	private boolean tipoTmoAlogenico;
	private boolean gerouArquivo;
	private String fileName;

	
	private StreamedContent media;
	
	private List<RelatorioSobrevidaPacienteTransplanteVO> colecao = null;
	
	private FiltroTempoSobrevidaTransplanteVO filtro = new FiltroTempoSobrevidaTransplanteVO();
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@PostConstruct
	public void inicializar(){
		this.begin(conversation,true);
		setTipoTransplanteTMO(true);
		setTipoTransplanteOrgaos(true);
		setTipoTmoAlogenico(true);
		fileName = null;
		gerouArquivo = false;
		filtro.setOrdenacao(DominioOrdenacaoPesquisaSobrevidaTransplante.NOME);
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			this.paciente = this.pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}

	public void processarBuscaPacientePorCodigo(Integer codigoPaciente){
        if(codigoPaciente != null){
               paciente = this.pacienteFacade.buscaPaciente(codigoPaciente);
        }else{
               this.paciente = null;
        }
	}
	
	private void verificaFiltroPorProntuario() {
		if (this.paciente != null) {
			filtro.setProntuarioNome(this.paciente.getProntuario().toString());
		}
		else{
			filtro.setProntuarioNome(null);
		}
	}
	
	public void  directPrintGerado(){
		try {	
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
	
	public boolean gerarDadosRelatorio(){
		try {
			this.transplanteFacade.validarRegrasTelaRelatorioSobrevidaTransplante(this.filtro);
		
		verificaFiltroPorProntuario();

		this.colecao = this.transplanteFacade.gerarRelatorioSobrevidaTransplante(this.filtro);	

		if (this.colecao == null || this.colecao.isEmpty()) {
			apresentarMsgNegocio("NENHUM_REGISTRO_ENCONTRADO_RELATORIO_SOBREVIDA");
			return false;
		}
		
		if(this.filtro.getOrdenacao().equals(DominioOrdenacaoPesquisaSobrevidaTransplante.SOBREVIDA)){
			Collections.sort(this.colecao,  new RelatorioSobrevidaPacienteTransplanteVOComparator("sobrevida"));
		}
		
		this.regrasAnosMesesDias(this.colecao);  
		
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			return false;
		} 
		return true;
	}
	
	public String visualizarImpressao() throws BaseListException{
		if(this.gerarDadosRelatorio()){
			return REDIRECT_RELATORIO_SOBREVIDA_TRANSPLANTE_PDF;
		}else{
			return REDIRECT_RELATORIO_SOBREVIDA_TRANSPLANTE;
		}
	}

	
	
	@Override
	protected Collection<?> recuperarColecao()
			throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/transplante/report/sobrevidaPacienteTransplante.jasper";	
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		AghParametros hospital = null;
		try {
			hospital = this.parametroFacade
					.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		if (hospital != null) {
			params.put("nomeInstituicao", hospital.getValor());
		}
		params.put("qtRegistros", String.valueOf(this.colecao.size()));
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		return params;
	}
	
	
	public void verificaTipoTransplante() {
		//Reaproveitar tela de Tempo de Permanencia em Fila
		FiltroTempoPermanenciaListVO filtro = new FiltroTempoPermanenciaListVO();
		filtro.setTipoTransplante(this.filtro.getTipoTransplante());
		filtro.setTipoTMO(this.filtro.getTipoTMO());
		filtro.setTipoOrgao(this.filtro.getTipoOrgao());
		filtro.setTipoAlogenico(this.filtro.getTipoAlogenico());
		boolean tipoTrans = this.transplanteFacade.verificaTipoTransplanteRelatorioFila(filtro);

		if (filtro.getTipoTransplante() == null) {
			setTipoTransplanteTMO(true);
			setTipoTransplanteOrgaos(true);
			setTipoTmoAlogenico(true);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoTMO(null);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoOrgao(null);
		} else {
			setTipoTransplanteTMO(tipoTrans);
			setTipoTransplanteOrgaos(!tipoTrans);
			this.filtro.setTipoOrgao(null);
		}
		
		if(this.filtro.getTipoTMO() != null && this.filtro.getTipoTMO().equals(DominioSituacaoTmo.U) ) {
			setTipoTmoAlogenico(true);
			this.filtro.setTipoAlogenico(null);
		}
		if(this.filtro.getTipoTMO() == null) {
			setTipoTmoAlogenico(true);
			this.filtro.setTipoAlogenico(null);
			
		}
	}
	
	public void verificaTipoTMO() {
		//Reaproveitar tela de Tempo de Permanencia em Fila
		FiltroTempoPermanenciaListVO filtro = new FiltroTempoPermanenciaListVO();
		filtro.setTipoTransplante(this.filtro.getTipoTransplante());
		filtro.setTipoTMO(this.filtro.getTipoTMO());
		filtro.setTipoOrgao(this.filtro.getTipoOrgao());
		filtro.setTipoAlogenico(this.filtro.getTipoAlogenico());
		boolean tipoTmo = this.transplanteFacade.verificaTipoTMOFilaTransplante(filtro);
		if (this.filtro.getTipoTransplante() == null)	 {
			setTipoTmoAlogenico(true);
			setTipoTransplanteOrgaos(true);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoTMO(null);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoOrgao(null);
			
		} else if(this.filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.O)) {
			setTipoTmoAlogenico(true);
			setTipoTransplanteOrgaos(false);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoTMO(null);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoOrgao(null);
			
		} else {
			setTipoTmoAlogenico(tipoTmo);
			setTipoTransplanteOrgaos(true);
			this.filtro.setTipoOrgao(null);
		}
		if(this.filtro.getTipoTMO() != null && this.filtro.getTipoTMO().equals(DominioSituacaoTmo.U) ) {
			setTipoTmoAlogenico(true);
			this.filtro.setTipoAlogenico(null);
		}
		if(this.filtro.getTipoTMO() == null) {
			setTipoTmoAlogenico(true);
			this.filtro.setTipoAlogenico(null);
			
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		verificaTipoTransplante();
		verificaTipoTMO();
	}
	
	public String limpar() {
		this.filtro = new FiltroTempoSobrevidaTransplanteVO();
		filtro.setOrdenacao(DominioOrdenacaoPesquisaSobrevidaTransplante.NOME);
		this.gerouArquivo = false;
		this.fileName = null;
		
		//Desabilita combos
		this.tipoTransplanteTMO = true;
		this.tipoTransplanteOrgaos = true;
		this.tipoTmoAlogenico = true;
		
		this.paciente = null;
		return REDIRECT_RELATORIO_SOBREVIDA_TRANSPLANTE;
	}
	
	/*
	*//**
	 * Dispara o download para o arquivo CSV do relatório.
	 */

	public void dispararDownload() {
		if (fileName != null) {
			try {
				this.download(
						fileName,
						DominioNomeRelatorio.RELATORIO_CVS_TRANSPLANTES_SOBREVIDA
								.getDescricao() + DominioNomeRelatorio.EXTENSAO_CSV,
						DominioMimeType.CSV.getContentType());
				download(fileName);
				fileName = null;
				this.gerouArquivo = false;
			} catch (IOException e) {
				fileName = null;
				this.gerouArquivo = false;
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,
						e, e.getLocalizedMessage()));
			}
		} else {
			fileName = null;
			this.gerouArquivo = false;
			apresentarMsgNegocio(Severity.ERROR,
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV
							.toString());
		}

	}
	
	public String print() throws JRException, IOException, DocumentException,
			ApplicationBusinessException {
		try {

			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getPdfByteArray(Boolean.TRUE)));
			return REDIRECT_RELATORIO_SOBREVIDA_TRANSPLANTE_PDF;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	//Gerar pdf bloqueado para impressao
		public StreamedContent getRenderPdf() throws JRException, IOException, DocumentException{
			try {
				DocumentoJasper documento = gerarDocumento();
				media =  this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
				return media;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}
		}

	
	public void directPrint() {
		try {
			if(this.gerarDadosRelatorio()){
				DocumentoJasper documento = gerarDocumento();

				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
				super.getEnderecoIPv4HostRemoto());

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			}
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO"); 
		}
	}
	public void gerarCSV() throws BaseListException {
		this.gerouArquivo = false;
			
		if(this.gerarDadosRelatorio()){

			try {
				this.fileName = this.transplanteFacade
						.gerarRelatorioSobrevidaCSV(colecao); 
				this.gerouArquivo = true;
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,
						e, e.getLocalizedMessage()));
			}
		}
	}
	
	public String voltar() {
		fileName = null;
		gerouArquivo = false;
		return REDIRECT_RELATORIO_SOBREVIDA_TRANSPLANTE;
	}
	
	
	public void regrasAnosMesesDias(
			List<RelatorioSobrevidaPacienteTransplanteVO> colecao) {
		for (RelatorioSobrevidaPacienteTransplanteVO current : colecao) {
			if(current.getSobrevida() != null && !"".equals(current.getSobrevida())){
				int qt = Integer.valueOf(current.getSobrevida());
				if(qt >= 1825){
					SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy");
					Date d1;
					Date d2;
					
					try {
						d1 = d.parse(current.getDataTransplante());
						d2 = d.parse(current.getDataUltimoAtendimento());
						String periodoDescritivo = calcDiff(d1, d2);
						current.setSobrevida(periodoDescritivo);
					} catch (ParseException e) {
						LOG.error(e.getMessage());
					}
				}else{
					current.setSobrevida(qt + " dias");
				}
			}
		}
	}
	
	//RN para calcular ANOS, MESES, DIAS. Não há dependencia da JODA-TIME para os RNs, por isso a transferencia ao controller.
	private String calcDiff(final Date d1, final Date d2) {
		if (d1 != null ^ d2 != null) {
			return "Período inválido";
		} else if (d1 == null && d2 == null) {
			return "";
		} else {
			if (d1.compareTo(d2) > 0) {
				return "Período inválido";
			}
			final Period p = new Period(d1.getTime(), d2.getTime(), PeriodType.yearMonthDay());
			final StringBuffer s = new StringBuffer();
			if (p.getYears() > 0) {
				s.append(p.getYears());
				if (p.getYears() == 1) {
					s.append(" ano ");
				} else {
					s.append(" anos ");
				}
			}
			if (p.getMonths() > 0) {
				s.append(" e ")
					.append(p.getMonths());
				if (p.getMonths() == 1) {
					s.append(" mês ");
				} else {
					s.append(" meses ");
				}
			}
			if (p.getDays() > 0) {
				s.append(" e ")
					.append(p.getDays());
				if (p.getDays() == 1) {
					s.append(" dia ");
				} else {
					s.append(" dias ");
				}
			}
			return s.toString();
		}
	}

	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public DominioTipoTransplanteCombo getTipoTransplante() {
		return tipoTransplante;
	}

	public void setTipoTransplante(DominioTipoTransplanteCombo tipoTransplante) {
		this.tipoTransplante = tipoTransplante;
	}

	public DominioSituacaoTmo getTipoTMO() {
		return tipoTMO;
	}

	public void setTipoTMO(DominioSituacaoTmo tipoTMO) {
		this.tipoTMO = tipoTMO;
	}

	public DominioTipoAlogenico getTipoAlogenico() {
		return tipoAlogenico;
	}

	public void setTipoAlogenico(DominioTipoAlogenico tipoAlogenico) {
		this.tipoAlogenico = tipoAlogenico;
	}

	public boolean isTipoTransplanteTMO() {
		return tipoTransplanteTMO;
	}

	public void setTipoTransplanteTMO(boolean tipoTransplanteTMO) {
		this.tipoTransplanteTMO = tipoTransplanteTMO;
	}

	public boolean isTipoTransplanteOrgaos() {
		return tipoTransplanteOrgaos;
	}

	public void setTipoTransplanteOrgaos(boolean tipoTransplanteOrgaos) {
		this.tipoTransplanteOrgaos = tipoTransplanteOrgaos;
	}

	public boolean isTipoTmoAlogenico() {
		return tipoTmoAlogenico;
	}

	public void setTipoTmoAlogenico(boolean tipoTmoAlogenico) {
		this.tipoTmoAlogenico = tipoTmoAlogenico;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public boolean isGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public List<RelatorioSobrevidaPacienteTransplanteVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioSobrevidaPacienteTransplanteVO> colecao) {
		this.colecao = colecao;
	}

	public FiltroTempoSobrevidaTransplanteVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTempoSobrevidaTransplanteVO filtro) {
		this.filtro = filtro;
	}
	
	

}
