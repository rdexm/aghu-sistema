package br.gov.mec.aghu.blococirurgico.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;



public class PacientesCirurgiaUnidadeController extends ActionController {

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private static final long serialVersionUID = -3974605509655243489L;
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE_CSV = "text/csv";
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;
	
	private Boolean gerouArquivo = Boolean.FALSE;
	private String nomeRelatorio;
	private String fileName;
	//private String contentType;
	//private String extensaoArquivo;

	private AghUnidadesFuncionais unidadeCirurgica;
	private RapServidores equipe;
	private Date crgDataInicio;
	private Date crgDataFim;
	
	@PostConstruct
	protected void init() {
		begin(conversation, true);	
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesCirurgicas(String param){
		return this.returnSGWithCount(aghuFacade.pesquisarUnidadesFuncionaisExecutoraCirurgias(param, Boolean.TRUE, AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()),pesquisarUnidadesCirurgicasCount(param));
	}
	
	public Long pesquisarUnidadesCirurgicasCount(String param){
		return aghuFacade.pesquisarUnidadesFuncionaisExecutoraCirurgiasCount(param);
	}
	
	public List<RapServidores> pesquisarEquipe(String filtro) {
		try {
			return this.returnSGWithCount(registroColaboradorFacade.
					pesquisarServidorPorSituacaoAtivoParaProcedimentos(
							filtro,
							unidadeCirurgica != null ? unidadeCirurgica.getSeq(): null),pesquisarEquipeCount(filtro));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}
	
	public Integer pesquisarEquipeCount(String param) throws ApplicationBusinessException {
		try {
			return this.registroColaboradorFacade
				.pesquisarServidorPorSituacaoAtivoParaProcedimentosCount(
						param, 
						unidadeCirurgica != null ? unidadeCirurgica.getSeq(): null);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return Integer.valueOf(0);
	}
	
	public void limpar() {
		gerouArquivo = Boolean.FALSE;
		fileName = null;
		nomeRelatorio = null;
		unidadeCirurgica = null; 
		crgDataInicio = null;
		crgDataFim = null;
		equipe = null;
	}

	
	public void exportarArquivoCsv() {
		try {
			Integer serMatricula = equipe != null? equipe.getId().getMatricula() : null;
			Short 	serVinCodigo = equipe != null? equipe.getId().getVinCodigo() : null;
			NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = 
						blocoCirurgicoFacade.
							gerarRelatorioPacientesCirurgiaUnidadeCSV(
									unidadeCirurgica.getSeq(),crgDataInicio,crgDataFim,
									serMatricula,serVinCodigo, EXTENSAO_CSV);
			fileName = nomeArquivoRelatorioCrgVO.getFileName(); 
			nomeRelatorio = nomeArquivoRelatorioCrgVO.getNomeRelatorio();
			gerouArquivo = Boolean.TRUE;
			baixarArquivo(fileName, nomeRelatorio + EXTENSAO_CSV, CONTENT_TYPE_CSV);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			gerouArquivo = Boolean.FALSE;
		} catch (IOException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));			
			gerouArquivo = Boolean.FALSE;
		}
		//return "process";
	}
	
	private void baixarArquivo(final String caminhoArquivo, final String nomeArq, final String mimeType) throws IOException {
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

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
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

	public Date getCrgDataInicio() {
		return crgDataInicio;
	}

	public void setCrgDataInicio(Date crgDataInicio) {
		this.crgDataInicio = crgDataInicio;
	}

	public Date getCrgDataFim() {
		return crgDataFim;
	}

	public void setCrgDataFim(Date crgDataFim) {
		this.crgDataFim = crgDataFim;
	}

	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public RapServidores getEquipe() {
		return equipe;
	}

	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public void setEquipe(RapServidores equipe) {
		this.equipe = equipe;
	}

}
