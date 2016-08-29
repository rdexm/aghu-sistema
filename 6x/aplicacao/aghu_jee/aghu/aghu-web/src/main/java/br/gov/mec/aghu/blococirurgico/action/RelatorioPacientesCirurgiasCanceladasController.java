package br.gov.mec.aghu.blococirurgico.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioPacientesCirurgiasCanceladasController extends ActionReport {


	private static final long serialVersionUID = 3805116582799980576L;
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE = "text/csv";
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;

	private String nomeRelatorio;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private Boolean gerouArquivo;
	private String fileName;
	//Filtros
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dataInicial;
	private Date dataFinal;
	private Short unfSeq;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, false),pesquisarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long pesquisarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, false);
	}

	public void limpar() {
		dataInicial = null;
		dataFinal = null;
		gerouArquivo = false;
		fileName = null;
		unidadeFuncional = null;

	}

	private Boolean validarIntervaloEntreDatas(Date dataInicial, Date dataFinal) {
		try {
			blocoCirurgicoFacade.validarIntervaloEntreDatasCirurgiasCanceladas(dataInicial, dataFinal);
			return Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return Boolean.FALSE;
		}
	}

	public void gerarCSV() {
		if (!validarIntervaloEntreDatas(dataInicial, dataFinal)) {
			gerouArquivo = false;
			return;
		}

		try {
			if(unidadeFuncional!=null){
				unfSeq = unidadeFuncional.getSeq();
				NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = 
					blocoCirurgicoFacade.gerarRelatorioCirurgiasCanceladasCSV(unfSeq, dataInicial, dataFinal, EXTENSAO_CSV);
				fileName = nomeArquivoRelatorioCrgVO.getFileName();
				nomeRelatorio = nomeArquivoRelatorioCrgVO.getNomeRelatorio();
				gerouArquivo = true;
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GERAR_ARQUIVO");
			}else{
				gerouArquivo = false;
				return;
			}


		} catch (ApplicationBusinessException ex){
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, ex, ex.getLocalizedMessage()));
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		} 

	}

	public void dispararDownload() {
		FacesContext facesContext = null;
		HttpServletResponse response = null;
		ServletOutputStream out = null;
		FileInputStream in = null;
		byte[] buffer = null;
		int len = 0;

		facesContext = FacesContext.getCurrentInstance();
		response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.reset();
		response.setContentType(CONTENT_TYPE);
		response.setContentLength((int) (new File(fileName)).length());
		response.addHeader("Content-disposition", "attachment; filename=\"" + nomeRelatorio + "\"");
		response.addHeader("Cache-Control", "no-cache");             
		buffer = new byte[BUFFER_SIZE_EQ_1M];
		// committing status and headers
		try {
			response.flushBuffer();
			out = response.getOutputStream();
			in = new FileInputStream(new File(fileName));
			while ((len = in.read(buffer)) >= 0) {
				out.write(buffer, 0, len);            	
			}
			out.flush();
			out.close();
			in.close();
			facesContext.responseComplete();        	
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}

		gerouArquivo = false;
	}

	public void executarDownload() {
		if (fileName != null) {
			dispararDownload();
			limpar();
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return null;
	}


	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
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

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@Override
	protected Collection<?> recuperarColecao()
			throws ApplicationBusinessException {
		// TODO Auto-generated method stub
		return null;
	}



}
