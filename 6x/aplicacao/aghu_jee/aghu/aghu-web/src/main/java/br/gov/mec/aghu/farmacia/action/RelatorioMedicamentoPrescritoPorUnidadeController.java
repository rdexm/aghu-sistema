package br.gov.mec.aghu.farmacia.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.farmacia.vo.MedicamentoPrescritoUnidadeVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;

public class RelatorioMedicamentoPrescritoPorUnidadeController extends ActionReport{

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -2074192268871485180L;
	
	private static final String PAGE_MEDICAMENTO_PRESCRITO_POR_UNIDADE= "relatorioMedicamentoPrescritoPorUnidade";
	private static final String PAGE_MEDICAMENTO_PRESCRITO_POR_UNIDADE_PDF= "relatorioMedicamentoPrescritoPorUnidadePdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dataDeReferenciaInicio;
	private Date dataDeReferenciaFim;
	private AfaMedicamento medicamento;
	private Boolean itemDispensado = Boolean.TRUE;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * Dados que serão impressos 
	 */
	private List<MedicamentoPrescritoUnidadeVO> colecao = new ArrayList<MedicamentoPrescritoUnidadeVO>(0);
	
	/**
	 * Pesquisa Unidades Funcionais conforme o parametro recebido
	 * 
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String parametro) {
		return this.returnSGWithCount(farmaciaFacade.listarUnidadesPmeInformatizadaByPesquisa(parametro),pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(parametro));
	}
	
	/**
	 * Count de Unidades Funcionais conforme o parametro recebido
	 * 
	 */
	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(String parametro) {
		return farmaciaFacade.listarUnidadesPmeInformatizadaByPesquisaCount(parametro);
	}
	
	/**
	 * Pesquisa Medicamentos conforme o parametro recebido
	 * 
	 */
	public List<AfaMedicamento> pesquisarMedicamentos(String strPesquisa){
		return this.returnSGWithCount(farmaciaApoioFacade.pesquisarTodosMedicamentos(strPesquisa),pesquisarMedicamentosCount(strPesquisa));
	}
	
	/**
	 * Count de Medicamentos conforme o parametro recebido
	 * 
	 */
	public Long pesquisarMedicamentosCount(String strPesquisa){
		return farmaciaApoioFacade.pesquisarTodosMedicamentosCount(strPesquisa);
	}
	
	public void limparPesquisa() {
		this.dataDeReferenciaInicio = null;
		this.dataDeReferenciaFim = null;
		this.unidadeFuncional = null;
		this.medicamento = null;
		this.itemDispensado = Boolean.TRUE;
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {
			
			if (validaDatas(dataDeReferenciaInicio, dataDeReferenciaFim)){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL");
				return;
			}
			
			colecao = this.farmaciaFacade.obterConteudoRelatorioMedicamentoPrescritoPorUnidade(dataDeReferenciaInicio, 
					dataDeReferenciaFim, unidadeFuncional, medicamento, itemDispensado);
			
			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
			super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<MedicamentoPrescritoUnidadeVO> recuperarColecao() {
		return colecao;
	}

	/**
	 * Apresenta PDF na tela do navegador.
	 * 
	 */
	public String print()throws JRException, IOException, DocumentException{
		try {
						
			if (validaDatas(dataDeReferenciaInicio, dataDeReferenciaFim)){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL");
			
		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
			}
			
			colecao = this.farmaciaFacade.obterConteudoRelatorioMedicamentoPrescritoPorUnidade(dataDeReferenciaInicio, 
					dataDeReferenciaFim, unidadeFuncional, medicamento, itemDispensado);
			
			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return null;
			}
			return PAGE_MEDICAMENTO_PRESCRITO_POR_UNIDADE_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltar(){
		return PAGE_MEDICAMENTO_PRESCRITO_POR_UNIDADE;
	}
	
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/farmacia/report/medicamentoPrescritoUnidade.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()));
		params.put("funcionalidade", "Medicamentos Prescritos");
		params.put("nomeRelatorio", "AFAR_MDTO_PRCR_UNID");	
		params.put("filtroEscolhido", getFiltroSelecionado());

		return params;			
	}

	private String getFiltroSelecionado() {
		StringBuffer b = new StringBuffer(20);
		
		if(unidadeFuncional != null){
			b.append("POR UNIDADE");
			if(medicamento != null) {
				b.append("     ");
			}
		}
		if(medicamento != null){
			b.append("POR MEDICAMENTO");
		}
		
		return b.toString();
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 */
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException{
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public boolean validaDatas(Date dataDeReferenciaInicio, Date dataDeReferenciaFim)  {

		if (dataDeReferenciaInicio.after(dataDeReferenciaFim)) { 
			return true;
		}
		return false;		
	}
	//Getters e Setters

	public List<MedicamentoPrescritoUnidadeVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<MedicamentoPrescritoUnidadeVO> colecao) {
		this.colecao = colecao;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Date getDataDeReferenciaInicio() {
		return dataDeReferenciaInicio;
	}

	public void setDataDeReferenciaInicio(Date dataDeReferenciaInicio) {
		this.dataDeReferenciaInicio = dataDeReferenciaInicio;
	}

	public Date getDataDeReferenciaFim() {
		return dataDeReferenciaFim;
	}

	public void setDataDeReferenciaFim(Date dataDeReferenciaFim) {
		this.dataDeReferenciaFim = dataDeReferenciaFim;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public Boolean getItemDispensado() {
		return itemDispensado;
	}

	public void setItemDispensado(Boolean itemDispensado) {
		this.itemDispensado = itemDispensado;
	}
}