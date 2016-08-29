package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesEntrevistarVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioPacientesEntrevistarController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -2455280241079228998L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private static final String RELATORIO = "relatorioPacientesEntrevistar";
	private static final String RELATORIO_PDF = "relatorioPacientesEntrevistarPdf";
	
	//filtros
	private AghUnidadesFuncionais unidadeCirurgica;	
	private Date dataCirurgia;

	private List<RelatorioPacientesEntrevistarVO> colecao;
	
	private ProfDescricaoCirurgicaVO profAnestesistaVO;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String print()throws JRException, IOException, DocumentException{
		try {
			carregarColecao();
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.FALSE)));			
		
		return RELATORIO_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}		
	}

	public void directPrint(){

		try {
			carregarColecao();
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}	

	public String voltar() {
		return RELATORIO;
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();			

		params.put("DATA_ATUAL", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		params.put("UNIDADE_CIRURGICA", unidadeCirurgica.getDescricao());

		String escala = this.blocoCirurgicoFacade.cfESCALAFormula(unidadeCirurgica.getSeq(), dataCirurgia);
		
		params.put("ESCALA", escala);
		params.put("ANESTESISTA", profAnestesistaVO.getNome());

		params.put("SUBREPORT_DIR","br/gov/mec/aghu/blococirurgico/report/");  

		try {			
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("HOSPITAL_LOCAL", parametroRazaoSocial.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return params;
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioPacientesEntrevistarVO> recuperarColecao() {		
		return colecao; 
	}

	public void carregarColecao() throws ApplicationBusinessException {		
		colecao = blocoCirurgicoFacade.pesquisarRelatorioPacientesEntrevistar(unidadeCirurgica.getSeq(), dataCirurgia, profAnestesistaVO);			
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));			
		
	}

	public void limparCampos(){
		unidadeCirurgica = null;		
		dataCirurgia = null;
		profAnestesistaVO = null;
	}


	/**
	 * PESQUISA DE ANESTESISTAS 
	 * @param strPesquisa
	 * @return
	 */
	public List<ProfDescricaoCirurgicaVO> pesquisarAnestesista(final String strPesquisa) {
		try {
			 return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarAnestesistas((String) strPesquisa, unidadeCirurgica),pesquisarAnestesistaCount(strPesquisa));
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public Long pesquisarAnestesistaCount(String strPesquisa) {
		try {
			if(unidadeCirurgica!=null){
				return blocoCirurgicoFacade.pesquisarAnestesistasCount((String) strPesquisa, unidadeCirurgica);
			}else{
				return 0L;
			}
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}
	
	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioPacientesEntrevistar.jasper";
	}

	//Getters and Setters
	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<RelatorioPacientesEntrevistarVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioPacientesEntrevistarVO> colecao) {
		this.colecao = colecao;
	}

	public ProfDescricaoCirurgicaVO getProfAnestesistaVO() {
		return profAnestesistaVO;
	}

	public void setProfAnestesistaVO(ProfDescricaoCirurgicaVO profAnestesistaVO) {
		this.profAnestesistaVO = profAnestesistaVO;
	}

}
