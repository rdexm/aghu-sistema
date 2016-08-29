package br.gov.mec.aghu.comissoes;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business.IComissaoProntuariosPrescricaoComissoesFacade;
import br.gov.mec.aghu.dominio.DominioOcorrenciaPOL;
import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;


public class ConsultaArquivoLogPolPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7127097777622718289L;

	private enum ConsultarArquivoLogPolPaginatorControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS,
	}

	private static final long LENGTH_OCORRENCIA = 80;
	
	private static final long LENGTH_SERVIDOR = 42;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IComissaoProntuariosPrescricaoComissoesFacade comissaoProntuariosPrescricaoComissoesFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AipLogProntOnlines> dataModel;
	
	RapServidores servidor;
	private Integer prontuarioPesquisa;
	Date dtInicio;
	Date dtFim;
	DominioOcorrenciaPOL ocorrencia;

	private String fileName;
	private Boolean gerouArquivo;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {		
		prepararDatasFimInicio();
		return pacienteFacade.pesquisarLogPorServidorProntuarioDatasOcorrenciaCount(servidor, prontuarioPesquisa, dtInicio, dtFim, ocorrencia);
	}

	@Override
	public List<AipLogProntOnlines> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		prepararDatasFimInicio();
		return pacienteFacade.pesquisarLogPorServidorProntuarioDatasOcorrencia(servidor, prontuarioPesquisa, dtInicio, dtFim, ocorrencia, firstResult, maxResult, orderProperty, asc);
	}

	private void prepararDatasFimInicio() {
		//prepara as datas de inicio e fim, caso estejam preenchidas
		if(dtInicio != null && dtFim != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtInicio);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			dtInicio = cal.getTime();
			
			Calendar calfim = Calendar.getInstance();
			calfim.setTime(dtFim);
			calfim.set(Calendar.HOUR_OF_DAY, 23);
			calfim.set(Calendar.MINUTE, 59);
			calfim.set(Calendar.SECOND, 59);
			calfim.set(Calendar.MILLISECOND, 999);			
			dtFim = calfim.getTime();	
		}
	}

	public List<RapServidores> pesquisarServidores(String parameter){
		return this.returnSGWithCount(registroColaboradorFacade.pesquisarServidores(parameter),pesquisarServidoresCount(parameter));
	}
	
	public Long pesquisarServidoresCount(String parameter){
		return registroColaboradorFacade.pesquisarServidoresCount(parameter);
	}
	
	public void pesquisar(){
		try {
			prepararDatasFimInicio();
			comissaoProntuariosPrescricaoComissoesFacade.validarIntervaloData(dtInicio, dtFim);
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String formatarProntuario(Integer prontuario){
		return CoreUtil.formataProntuario(prontuario);
	}
	
	public void exportar(){
		try {
			
			prepararDatasFimInicio();
			comissaoProntuariosPrescricaoComissoesFacade.validarIntervaloData(dtInicio, dtFim);
			List<AipLogProntOnlines> logs = pacienteFacade.pesquisarLogPorServidorProntuarioDatasOcorrencia(servidor, prontuarioPesquisa, dtInicio, dtFim, ocorrencia, 0, 999999, null, true);
			fileName = comissaoProntuariosPrescricaoComissoesFacade.gerarCSVLogProntuarioOnline(logs);
			
			if(fileName == null){
				String mensagem = obterStringMessagem(ConsultarArquivoLogPolPaginatorControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem,new Object[0]);
			}else{
				setGerouArquivo(Boolean.TRUE);	
				this.dispararDownload();
			}			
			
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private String obterStringMessagem(String codigo) {
		return super.getBundle().getString(codigo);
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}
	
	public void dispararDownload(){
		if(fileName != null){
			try {
				super.download(fileName);
				setGerouArquivo(Boolean.FALSE);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage())); 
			}
		}
	}
	
	public String abreviarOcorrencia(String original) {
		return abreviarString(original, LENGTH_OCORRENCIA);
	}
	
	public String abreviarServidor(String original) {
		return abreviarString(original, LENGTH_SERVIDOR);
	}

	private String abreviarString(String original, Long lenght) {
		String retorno = original;
		if (original.length() > lenght) {
			retorno = StringUtil.trunc(original, Boolean.TRUE, lenght);
		}
		return retorno;
	}
	
	public void limparCampos(){
		dataModel.limparPesquisa();
		servidor = null;
		prontuarioPesquisa = null;
		dtInicio = null;
		dtFim = null;
		ocorrencia = null;
		fileName = null;
		gerouArquivo = false;
	} 

	public DynamicDataModel<AipLogProntOnlines> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipLogProntOnlines> dataModel) {
		this.dataModel = dataModel;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public Integer getProntuarioPesquisa() {
		return prontuarioPesquisa;
	}

	public void setProntuarioPesquisa(Integer prontuarioPesquisa) {
		this.prontuarioPesquisa = prontuarioPesquisa;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public DominioOcorrenciaPOL getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(DominioOcorrenciaPOL ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
}
