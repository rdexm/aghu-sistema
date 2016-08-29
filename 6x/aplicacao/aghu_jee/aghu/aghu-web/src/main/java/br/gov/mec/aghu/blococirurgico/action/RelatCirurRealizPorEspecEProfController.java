package br.gov.mec.aghu.blococirurgico.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class RelatCirurRealizPorEspecEProfController extends ActionController {

	private static final long serialVersionUID = -1328156589485207418L;	
	private static final String CONTENT_TYPE="text/csv";
	

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private Boolean gerouArquivo;
	private String fileName;
	private AghUnidadesFuncionais unidadeFuncional;
	private AghEspecialidades especialidade;
	private Date dataInicial;
	private Date dataFinal;
		
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;
	private static final String EXTENSAO = ".csv";
	
	private static final String RELATORIO_CIRURGIAS_REAL_ESPC_PROF_PDF = "relatorioCirurRealizPorEspecEProfPdf";
	
	
	@PostConstruct
	protected void init() {
		begin(conversation, true);
		inicio();
	}
	
	public void inicio() {
		limpar();	
	}
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS,  false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}

  	
  	public List<AghEspecialidades> listarEspecialidades(final String strPesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidadePorNomeOuSigla((String) strPesquisa),listarEspecialidadesCount(strPesquisa));
	}
	
	public Long listarEspecialidadesCount(final String strPesquisa) {
		return aghuFacade.listarEspecialidadePorNomeOuSiglaCount((String) strPesquisa);
	}
	
	public String imprimirRelatorio(){
		if(validarIntervaloEntreDatas(dataInicial, dataFinal)){
			return RELATORIO_CIRURGIAS_REAL_ESPC_PROF_PDF;
		}else{
			return null;
		}
	}
	
	public String visualizarRelatorio(){
		if(validarIntervaloEntreDatas(dataInicial, dataFinal)){
			return RELATORIO_CIRURGIAS_REAL_ESPC_PROF_PDF;
		}else{
			return null;
		}
	}
	
	public void limpar(){
		dataInicial = null;
		dataFinal = null;
		gerouArquivo = false;
		fileName = null;
		unidadeFuncional = null;
		especialidade = null;
	}
	private Boolean validarIntervaloEntreDatas(Date dataInicial, Date dataFinal){
		try {
			blocoCirurgicoFacade.validarIntervaldoEntreDatas(dataInicial, dataFinal);
			return Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return Boolean.FALSE;	
		}
	}
	
	public void gerarCSV() {
		if (validarIntervaloEntreDatas(dataInicial, dataFinal)) {
			try {
				fileName = blocoCirurgicoFacade.geraCSVRelatCirurRealizPorEspecEProf(
								unidadeFuncional.getSeq(),
								dataInicial,
								dataFinal,
								getEspecialidade() != null ? especialidade.getSeq() : null);
				
				gerouArquivo = true;

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
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
        response.addHeader("Content-disposition", "attachment; filename=\"" + DominioNomeRelatorio.RELAT_CIRUR_REALIZ_POR_ESPEC_PROF.getDescricao().toString() + EXTENSAO + "\"");
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
    		apresentarExcecaoNegocio(new ApplicationBusinessException(
    				AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
    	}
        
        gerouArquivo = false;
	}
	
	/*public void dispararDownload() {
		if (fileName != null) {

			try {
				this.
				blocoCirurgicoFacade.downloadedCSV(
						fileName,
						DominioNomeRelatorio.RELAT_CIRUR_REALIZ_POR_ESPEC_PROF.getDescricao() 
						+ EXTENSAO_CSV, CONTENT_TYPE);
				
				fileName = null;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new ApplicationBusinessException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,
						e, e.getLocalizedMessage()));
			}
		}
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return null;
	}*/

	//Getters and Setters
	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
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

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
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
