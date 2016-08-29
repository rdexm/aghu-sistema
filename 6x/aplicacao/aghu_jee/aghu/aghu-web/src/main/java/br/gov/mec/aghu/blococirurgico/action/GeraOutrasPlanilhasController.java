package br.gov.mec.aghu.blococirurgico.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;



public class GeraOutrasPlanilhasController extends ActionController {

	private static final long serialVersionUID = -3974605509655243489L;
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE_CSV = "text/csv";
	private static final String EXTENSAO_ZIP = ".zip";
	private static final String CONTENT_TYPE_ZIP = "application/zip";
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private RapPessoasFisicas rapPessoasFisicas;
	
	private Boolean gerouArquivo = Boolean.FALSE;
	private String nomeRelatorio;
	private String fileName;
	private String contentType;
	private String extensaoArquivo;
	private Date dataInicial;
	private Date dataFinal;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private void limpar() {
		dataInicial = null;
		dataFinal = null;
		gerouArquivo = Boolean.FALSE;
		fileName = null;
		nomeRelatorio = null;	
	}
	
	private List<String> splitParametros(AghParametros aghParametros) throws ApplicationBusinessException{
		List<String> listaParametros = null;
		if(aghParametros != null && StringUtils.isNotBlank(aghParametros.getVlrTexto())){
			listaParametros = Arrays.asList(aghParametros.getVlrTexto().split(","));
		}else{
			new ApplicationBusinessException(AghParemetrosONExceptionCode.PARAMETRO_INVALIDO, aghParametros.getNome());
		}
		return listaParametros;
	}

	private List<String> obterListaSiglaConselhoProfissional() {
		 List<String> listaSiglaConselhoProfissional = null;
		
		try {
			listaSiglaConselhoProfissional = splitParametros(parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_LISTA_CONSELHOS_PROF_MBC));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return listaSiglaConselhoProfissional;
	}
	
	public List<RapPessoasFisicas> listarRapPessoasFisicasPorConselhoProfissional(final String strPesquisa) {
		return this.returnSGWithCount(registroColaboradorFacade.listarRapPessoasFisicasPorConselhoProfissional((String) strPesquisa, obterListaSiglaConselhoProfissional()),listarRapPessoasFisicasPorConselhoProfissionalCount(strPesquisa));   
	}
	
	public Long listarRapPessoasFisicasPorConselhoProfissionalCount(final String strPesquisa) {
		return  registroColaboradorFacade.listarRapPessoasFisicasPorConselhoProfCount((String) strPesquisa, obterListaSiglaConselhoProfissional());
	}

	// Cirurgias e Procedimentos do Profissional
	public void listarCirurgiasProcedimentosProfissional() {
		Integer pesCodigo = null;
		if (rapPessoasFisicas != null) {
			pesCodigo = rapPessoasFisicas.getCodigo();
		}
		
		extensaoArquivo = EXTENSAO_CSV;
		contentType = CONTENT_TYPE_CSV;
		
		try {
			NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = blocoCirurgicoFacade.gerarRelatorioCirurgiaProcedProfissionalCSV(
					pesCodigo, dataInicial, dataFinal, extensaoArquivo);
			fileName = nomeArquivoRelatorioCrgVO.getFileName(); 
			nomeRelatorio = nomeArquivoRelatorioCrgVO.getNomeRelatorio();
			gerouArquivo = Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
			gerouArquivo = Boolean.FALSE;
		} catch (IOException e) {
			apresentarExcecaoNegocio(
					new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));			
			gerouArquivo = Boolean.FALSE;
		}
	}
	
	// Cirurgias com Exposição à Radiação Ionizante
	public void listarCirurgiasExposicaoRadiacaoIonizante() {
		extensaoArquivo = EXTENSAO_CSV;
		contentType = CONTENT_TYPE_CSV;
		
		try {
			blocoCirurgicoFacade.validarProfissional(rapPessoasFisicas);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return;
		}
	
		List<Short> unidadesFuncionais = new ArrayList<Short>();
		List<Short> equipamentos = new ArrayList<Short>();
		
		try {
			for(String i:(splitParametros(parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_LISTA_UNIDADES_RELATORIO_EQUIP_RX)))){
				unidadesFuncionais.add(Short.parseShort(i));
			}
		} catch (ApplicationBusinessException exceptionSemRollback) {
			apresentarExcecaoNegocio(exceptionSemRollback);			
			return;
		}
		
		try {
			for(String i:splitParametros(parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_LISTA_CODS_EQUIPAMENTOS_RX))){
				equipamentos.add(Short.parseShort(i));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return;
		}
		
		try {
			fileName = blocoCirurgicoFacade.geraRelCSVCirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal,unidadesFuncionais, equipamentos);
			nomeRelatorio = DominioNomeRelatorio.RELAT_CIRURGIAS_EXPOSICAO_RADIACAO_IONIZANTE.getDescricao() + DateUtil.obterDataFormatada(new Date(), "ddMMyyyyHHmmss");
			gerouArquivo = Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			gerouArquivo = Boolean.FALSE;			
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			gerouArquivo = Boolean.FALSE;
		}
	}

	// Transplantes Realizados (TMO e Outros)
	public void listarTransplantesRealizados() {
		extensaoArquivo = EXTENSAO_ZIP;
		contentType = CONTENT_TYPE_ZIP;
		
		try {
			NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = 
					blocoCirurgicoFacade.gerarRelatoriosTMOEOutrosTransplantesZip(dataInicial, dataFinal, rapPessoasFisicas, EXTENSAO_CSV, EXTENSAO_ZIP);
			fileName = nomeArquivoRelatorioCrgVO.getFileName();
			nomeRelatorio = nomeArquivoRelatorioCrgVO.getNomeRelatorio();
			gerouArquivo = Boolean.TRUE;			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			gerouArquivo = Boolean.FALSE;
		} catch (IOException e) {
			apresentarExcecaoNegocio(
					new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			
			gerouArquivo = Boolean.FALSE;
		}
	}

	// Diagnósticos Pré e Pós-operatórios
	public void listarDiagnosticosPrePosOperatorio() {
		extensaoArquivo = EXTENSAO_CSV;
		contentType = CONTENT_TYPE_CSV;
		
		try {
			blocoCirurgicoFacade.validarProfissional(rapPessoasFisicas);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			return;
		}
		
		try {
			fileName = blocoCirurgicoFacade.geraRelCSVDiagnosticosPrePosOperatorio(dataInicial, dataFinal);
			nomeRelatorio = DominioNomeRelatorio.RELATORIO_DIAGNOSTICOS_PRE_POS_OPERATORIO.getDescricao() + DateUtil.obterDataFormatada(new Date(), "ddMMyyyyHHmmss");
			gerouArquivo = Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			gerouArquivo = Boolean.FALSE;			
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			gerouArquivo = Boolean.FALSE;			
		}
	}
	
	public void executarDownload() {
		if (fileName != null) {
			try {
				dispararDownload(fileName, nomeRelatorio + extensaoArquivo, contentType);
				limpar();
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
	
	private void dispararDownload(
			final String caminhoArquivo,
			final String nomeArq, 
			final String mimeType) throws IOException {
		
        FacesContext facesContext = null;
        HttpServletResponse response = null;
        ServletOutputStream out = null;
        FileInputStream in = null;
        byte[] buffer = null;
        int len = 0;

        facesContext = FacesContext.getCurrentInstance();
        response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.reset();
        response.setContentType(mimeType);
        response.setContentLength((int) (new File(caminhoArquivo)).length());
        response.addHeader("Content-disposition", "attachment; filename=\"" + nomeArq + "\"");
        response.addHeader("Cache-Control", "no-cache");             
        buffer = new byte[BUFFER_SIZE_EQ_1M];
        // committing status and headers
        response.flushBuffer();
        out = response.getOutputStream();
        in = new FileInputStream(new File(caminhoArquivo));
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);            	
        }
		out.flush();
		out.close();
		in.close();
		facesContext.responseComplete();		
	}

	

	// Getters and Setters
	public RapPessoasFisicas getRapPessoasFisicas() {
		return rapPessoasFisicas;
	}

	public void setRapPessoasFisicas(RapPessoasFisicas rapPessoasFisicas) {
		this.rapPessoasFisicas = rapPessoasFisicas;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}